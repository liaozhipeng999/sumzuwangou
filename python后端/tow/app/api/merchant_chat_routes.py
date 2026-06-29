"""
商家专用客服接口

店铺专用客服，支持多轮对话和历史记录
不同用户与同一商家对话有独立的历史记录
"""

from fastapi import APIRouter, HTTPException, Depends
from fastapi.responses import StreamingResponse
from sqlalchemy.orm import Session
from typing import Optional, List
import json

from app.models.mysql_models import get_db, TermCorpus
from app.services.merchant_chat_service import merchant_chat_service
from app.services.merchant_db_service import merchant_db_service
from app.services.corpus_generator_service import corpus_generator

router = APIRouter()


# 兼容前端使用 /customer-service/{merchant_id}/{user_id} 的路径格式
@router.post("/customer-service/{merchant_id}/{user_id}", tags=["商家客服", "兼容路由"])
async def customer_service_chat(
    merchant_id: int,
    user_id: str,
    message: str,
    conversation_id: Optional[str] = None,
    user_name: Optional[str] = None,
    db: Session = Depends(get_db)
):
    """
    商家客服对话接口（兼容前端路径格式）
    
    **参数：**
    - merchant_id: 商家ID（从路径获取）
    - user_id: 用户ID（从路径获取）
    - message: 用户消息（必填）
    - conversation_id: 会话ID（可选）
    - user_name: 用户名称（可选）
    """
    # 验证商家ID是否有效
    if merchant_id <= 0:
        raise HTTPException(status_code=404, detail="商家不存在或未开通客服服务")
    
    # 验证商家是否存在
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        raise HTTPException(status_code=404, detail="商家不存在或未开通客服服务")
    
    try:
        result = await merchant_chat_service.chat(
            merchant_id=merchant_id,
            user_id=user_id,
            message=message,
            conversation_id=conversation_id,
            user_name=user_name
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"对话处理失败: {str(e)}")


@router.post("/MerchantChat", tags=["商家客服"])
async def merchant_chat(
    merchant_id: int,
    user_id: str,
    message: str,
    conversation_id: Optional[str] = None,
    user_name: Optional[str] = None,
    db: Session = Depends(get_db)
):
    """
    商家专用客服对话接口（非流式）
    
    **特点：**
    - 店铺专用客服，区别于通用客服
    - 支持多轮对话和历史记录
    - 不同用户与同一商家对话有独立的历史记录
    - 商家可自定义语料（问答对）存储在数据库
    - 优先匹配商家语料，未匹配则调用LLM
    
    **参数：**
    - merchant_id: 商家ID（必填）
    - user_id: 用户ID（必填）
    - message: 用户消息（必填）
    - conversation_id: 会话ID（可选，不传则自动创建）
    - user_name: 用户名称（可选）
    
    **响应：**
    - response: AI回复内容
    - conversation_id: 会话ID
    - source: 回复来源（corpus/llm）
    - merchant_id: 商家ID
    - user_id: 用户ID
    """
    # 验证商家ID是否有效
    if merchant_id <= 0:
        raise HTTPException(status_code=404, detail="商家不存在或未开通客服服务")
    
    # 验证商家是否存在
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        raise HTTPException(status_code=404, detail="商家不存在或未开通客服服务")
    
    try:
        result = await merchant_chat_service.chat(
            merchant_id=merchant_id,
            user_id=user_id,
            message=message,
            conversation_id=conversation_id,
            user_name=user_name
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"对话处理失败: {str(e)}")


@router.post("/MerchantChat/stream", tags=["商家客服"])
async def merchant_chat_stream(
    merchant_id: int,
    user_id: str,
    message: str,
    conversation_id: Optional[str] = None,
    user_name: Optional[str] = None,
    db: Session = Depends(get_db)
):
    """
    商家专用客服流式对话接口（SSE）
    
    **特点：**
    - 流式输出，逐字返回回复
    - 支持多轮对话和历史记录
    - 优先匹配商家语料
    
    **参数：**
    - merchant_id: 商家ID（必填）
    - user_id: 用户ID（必填）
    - message: 用户消息（必填）
    - conversation_id: 会话ID（可选）
    - user_name: 用户名称（可选）
    
    **响应事件：**
    - sources: 来源信息（corpus/llm）
    - token: 文本片段
    - done: 完成标记，包含会话ID
    - error: 错误信息
    """
    # 验证商家ID是否有效
    if merchant_id <= 0:
        async def error_generator():
            yield f"data: {json.dumps({'type': 'error', 'data': '商家不存在或未开通客服服务'})}\n\n"
        return StreamingResponse(error_generator(), media_type="text/event-stream")
    
    # 验证商家是否存在
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        async def error_generator():
            yield f"data: {json.dumps({'type': 'error', 'data': '商家不存在或未开通客服服务'})}\n\n"
        return StreamingResponse(error_generator(), media_type="text/event-stream")
    
    async def event_generator():
        try:
            async for item in merchant_chat_service.chat_stream(
                merchant_id=merchant_id,
                user_id=user_id,
                message=message,
                conversation_id=conversation_id,
                user_name=user_name
            ):
                yield f"data: {json.dumps(item)}\n\n"
        except Exception as e:
            yield f"data: {json.dumps({'type': 'error', 'data': str(e)})}\n\n"
    
    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no"
        }
    )


@router.get("/MerchantChat/history", tags=["商家客服"])
async def get_conversation_history(
    conversation_id: str,
    db: Session = Depends(get_db)
):
    """
    获取会话历史记录
    
    **参数：**
    - conversation_id: 会话ID（必填）
    
    **响应：**
    - conversation_id: 会话ID
    - messages: 消息列表
    """
    conversation = merchant_db_service.get_conversation(db, conversation_id)
    if not conversation:
        raise HTTPException(status_code=404, detail="会话不存在")
    
    history = merchant_chat_service.get_conversation_history(conversation_id)
    
    return {
        "conversation_id": conversation_id,
        "merchant_id": conversation.merchant_id,
        "user_id": conversation.user_id,
        "created_at": conversation.created_at.isoformat(),
        "updated_at": conversation.updated_at.isoformat(),
        "messages": history
    }


@router.post("/MerchantChat/end", tags=["商家客服"])
async def end_conversation(
    conversation_id: str,
    db: Session = Depends(get_db)
):
    """
    结束会话
    
    **参数：**
    - conversation_id: 会话ID（必填）
    """
    success = merchant_db_service.end_conversation(db, conversation_id)
    if not success:
        raise HTTPException(status_code=404, detail="会话不存在")
    
    return {
        "status": "success",
        "conversation_id": conversation_id,
        "message": "会话已结束"
    }


# ========== 商家语料管理接口 ==========

@router.post("/MerchantChat/corpus", tags=["商家客服", "语料管理"])
async def create_corpus(
    merchant_id: int,
    question: str,
    answer: str,
    keywords: Optional[str] = None,
    priority: int = 0,
    db: Session = Depends(get_db)
):
    """
    创建商家语料
    
    **参数：**
    - merchant_id: 商家ID（必填）
    - question: 问题（必填）
    - answer: 回答（必填）
    - keywords: 关键词，逗号分隔（可选）
    - priority: 优先级，数值越大优先级越高（可选，默认0）
    """
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        raise HTTPException(status_code=404, detail="商家不存在")
    
    corpus = merchant_db_service.create_corpus(
        db, merchant_id, question, answer, keywords, priority
    )
    
    return {
        "id": corpus.id,
        "merchant_id": corpus.merchant_id,
        "question": corpus.question,
        "answer": corpus.answer,
        "keywords": corpus.keywords,
        "priority": corpus.priority,
        "created_at": corpus.created_at.isoformat()
    }


@router.get("/MerchantChat/corpus", tags=["商家客服", "语料管理"])
async def list_corpus(
    merchant_id: int,
    page: int = 1,
    page_size: int = 20,
    db: Session = Depends(get_db)
):
    """
    获取商家语料列表
    
    **参数：**
    - merchant_id: 商家ID（必填）
    - page: 页码（默认1）
    - page_size: 每页数量（默认20）
    """
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        raise HTTPException(status_code=404, detail="商家不存在")
    
    corpus_list = merchant_db_service.get_merchant_corpus(db, merchant_id)
    
    # 分页
    start = (page - 1) * page_size
    end = start + page_size
    paginated_list = corpus_list[start:end]
    
    return {
        "total": len(corpus_list),
        "page": page,
        "page_size": page_size,
        "data": [
            {
                "id": corpus.id,
                "question": corpus.question,
                "answer": corpus.answer,
                "keywords": corpus.keywords,
                "priority": corpus.priority,
                "enabled": corpus.enabled,
                "created_at": corpus.created_at.isoformat()
            }
            for corpus in paginated_list
        ]
    }


@router.get("/MerchantChat/corpus/{corpus_id}", tags=["商家客服", "语料管理"])
async def get_corpus(
    corpus_id: str,
    db: Session = Depends(get_db)
):
    """
    获取单个语料详情
    
    **参数：**
    - corpus_id: 语料ID（必填）
    """
    corpus = db.query(TermCorpus).filter(TermCorpus.id == corpus_id).first()
    if not corpus:
        raise HTTPException(status_code=404, detail="语料不存在")
    
    return {
        "id": corpus.id,
        "merchant_id": corpus.merchant_id,
        "question": corpus.question,
        "answer": corpus.answer,
        "keywords": corpus.keywords,
        "priority": corpus.priority,
        "enabled": corpus.enabled,
        "created_at": corpus.created_at.isoformat(),
        "updated_at": corpus.updated_at.isoformat()
    }


@router.put("/MerchantChat/corpus/{corpus_id}", tags=["商家客服", "语料管理"])
async def update_corpus(
    corpus_id: str,
    question: Optional[str] = None,
    answer: Optional[str] = None,
    keywords: Optional[str] = None,
    priority: Optional[int] = None,
    enabled: Optional[int] = None,
    db: Session = Depends(get_db)
):
    """
    更新语料
    
    **参数：**
    - corpus_id: 语料ID（必填）
    - question: 问题（可选）
    - answer: 回答（可选）
    - keywords: 关键词（可选）
    - priority: 优先级（可选）
    - enabled: 是否启用（可选）
    """
    updates = {}
    if question is not None:
        updates["question"] = question
    if answer is not None:
        updates["answer"] = answer
    if keywords is not None:
        updates["keywords"] = keywords
    if priority is not None:
        updates["priority"] = priority
    if enabled is not None:
        updates["enabled"] = enabled
    
    if not updates:
        raise HTTPException(status_code=400, detail="至少需要提供一个更新字段")
    
    corpus = merchant_db_service.update_corpus(db, corpus_id, **updates)
    if not corpus:
        raise HTTPException(status_code=404, detail="语料不存在")
    
    return {
        "id": corpus.id,
        "question": corpus.question,
        "answer": corpus.answer,
        "keywords": corpus.keywords,
        "priority": corpus.priority,
        "enabled": corpus.enabled,
        "updated_at": corpus.updated_at.isoformat()
    }


@router.delete("/MerchantChat/corpus/{corpus_id}", tags=["商家客服", "语料管理"])
async def delete_corpus(
    corpus_id: str,
    db: Session = Depends(get_db)
):
    """
    删除语料
    
    **参数：**
    - corpus_id: 语料ID（必填）
    """
    success = merchant_db_service.delete_corpus(db, corpus_id)
    if not success:
        raise HTTPException(status_code=404, detail="语料不存在")
    
    return {"status": "success", "message": "删除成功"}


@router.post("/MerchantChat/corpus/batch", tags=["商家客服", "语料管理"])
async def batch_create_corpus(
    merchant_id: int,
    items: List[dict],
    db: Session = Depends(get_db)
):
    """
    批量创建语料
    
    **参数：**
    - merchant_id: 商家ID（必填）
    - items: 语料列表（必填）
    
    **items格式：**
    ```json
    [
        {"question": "问题1", "answer": "回答1", "keywords": "关键词1,关键词2", "priority": 0},
        {"question": "问题2", "answer": "回答2"}
    ]
    ```
    """
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        raise HTTPException(status_code=404, detail="商家不存在")
    
    created = []
    errors = []
    
    for i, item in enumerate(items):
        try:
            corpus = merchant_db_service.create_corpus(
                db,
                merchant_id,
                item.get("question", ""),
                item.get("answer", ""),
                item.get("keywords"),
                item.get("priority", 0)
            )
            created.append({"id": corpus.id, "question": corpus.question})
        except Exception as e:
            errors.append({"index": i, "error": str(e)})
    
    return {
        "success": True,
        "created_count": len(created),
        "created": created,
        "errors": errors
    }


# ========== 自动生成语料接口 ==========

@router.post("/MerchantChat/corpus/generate", tags=["商家客服", "语料生成"])
async def generate_corpus(
    merchant_id: int,
    clear_existing: bool = False,
    db: Session = Depends(get_db)
):
    """
    根据商家商品自动生成语料
    
    **功能说明：**
    根据商家的商品信息（名称、价格、库存、销量等）自动生成FAQ语料。
    每个商品会生成以下类型的语料：
    - 商品咨询：商品怎么样？好用吗？
    - 价格咨询：多少钱？价格多少？
    - 库存咨询：有货吗？库存多少？
    - 销量咨询：卖了多少？销量如何？
    - 规格咨询：有什么规格？
    - 热销推荐：有什么热销的？
    - 新品推荐：有什么新品？
    - 通用咨询：你们店有什么？
    
    **参数：**
    - merchant_id: 商家ID（必填）
    - clear_existing: 是否先清空现有语料（默认False，True表示替换）
    
    **返回：**
    - success: 是否成功
    - message: 结果消息
    - generated_count: 生成的语料数量
    - product_count: 商品数量
    - errors: 错误列表
    """
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        raise HTTPException(status_code=404, detail="商家不存在")
    
    # 清空现有语料（如果需要）
    if clear_existing:
        deleted = corpus_generator.clear_merchant_corpus(merchant_id, db)
        print(f"已清空商家 {merchant_id} 的 {deleted} 条旧语料")
    
    # 生成新语料
    result = corpus_generator.generate_corpus_from_products(merchant_id, db)
    
    if not result["success"]:
        raise HTTPException(status_code=400, detail=result["message"])
    
    return {
        "success": True,
        "message": f"成功为商家 {merchant_id} 生成 {result['generated_count']} 条语料",
        "generated_count": result["generated_count"],
        "product_count": result["product_count"],
        "errors": result.get("errors", [])
    }


@router.post("/MerchantChat/corpus/clear", tags=["商家客服", "语料管理"])
async def clear_corpus(
    merchant_id: int,
    db: Session = Depends(get_db)
):
    """
    清空商家所有语料
    
    **参数：**
    - merchant_id: 商家ID（必填）
    
    **返回：**
    - success: 是否成功
    - deleted_count: 删除的语料数量
    """
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        raise HTTPException(status_code=404, detail="商家不存在")
    
    deleted_count = corpus_generator.clear_merchant_corpus(merchant_id, db)
    
    return {
        "success": True,
        "message": f"已删除商家 {merchant_id} 的 {deleted_count} 条语料",
        "deleted_count": deleted_count
    }


@router.get("/MerchantChat/corpus/stats", tags=["商家客服", "语料统计"])
async def get_corpus_stats(
    merchant_id: int,
    db: Session = Depends(get_db)
):
    """
    获取商家语料统计信息
    
    **参数：**
    - merchant_id: 商家ID（必填）
    
    **返回：**
    - corpus_count: 语料总数
    - product_count: 商品总数
    - coverage: 覆盖率（语料数/商品数）
    """
    from sqlalchemy import func
    
    merchant = merchant_db_service.get_merchant(db, merchant_id)
    if not merchant:
        raise HTTPException(status_code=404, detail="商家不存在")
    
    # 统计语料数量
    corpus_count = db.query(func.count(TermCorpus.id)).filter(
        TermCorpus.merchant_id == merchant_id
    ).scalar()
    
    # 统计商品数量
    from sqlalchemy import text
    product_count = db.execute(
        text("SELECT COUNT(*) FROM term_products WHERE merchant_id = :merchant_id AND status = 1 AND deleted_at IS NULL"),
        {"merchant_id": merchant_id}
    ).scalar()
    
    coverage = corpus_count / product_count if product_count > 0 else 0
    
    return {
        "merchant_id": merchant_id,
        "corpus_count": corpus_count or 0,
        "product_count": product_count or 0,
        "coverage": round(coverage, 2),
        "message": f"每个商品平均生成 {round(coverage, 1)} 条语料"
    }
