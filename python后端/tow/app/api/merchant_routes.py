"""
商家客服 API
基于话术模板的智能客服服务
"""

from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse
from typing import Optional
import json

from app.models.schemas import ChatRequest
from app.services.dialogue_template_service import dialogue_template_service
from app.services.chat_service import ChatService

router = APIRouter()


@router.get("/health", tags=["商家客服"])
async def merchant_health():
    """商家客服服务健康检查"""
    return {
        "status": "healthy",
        "service": "merchant_chat",
        "template_count": len(dialogue_template_service.templates)
    }


@router.post("/chat/stream", tags=["商家客服"])
async def merchant_chat_stream(
    user_id: str,
    message: str,
    merchant_id: Optional[str] = "default",
    conversation_id: Optional[str] = None
):
    """
    商家客服流式对话接口
    
    基于话术模板的智能客服，逐字返回回复。
    适合电商客服、商家客服等场景。
    
    **特点：**
    - 基于商家预设话术模板
    - 混合模式：先匹配话术，匹配不到返回"未找到匹配话术"
    - 流式输出，打字机效果
    
    **参数：**
    - user_id: 用户ID
    - message: 用户消息
    - merchant_id: 商家ID（用于区分不同商家的话术）
    - conversation_id: 会话ID（可选）
    
    **响应事件：**
    - sources: 来源信息（话术模板分类）
    - confidence: 匹配置信度
    - token: 文本片段
    - done: 完成标记
    """
    chat_service = ChatService()
    
    async def event_generator():
        try:
            # 获取或创建会话ID
            conv_id = chat_service.get_or_create_conversation_id(conversation_id)
            
            # 保存用户消息
            chat_service.save_message(conv_id, "user", message)
            
            # 获取对话历史（用于上下文）
            history = chat_service.get_history(conv_id)
            
            # 匹配话术模板
            template_result = dialogue_template_service.match_template(
                message=message,
                merchant_id=merchant_id,
                threshold=0.6
            )
            
            if template_result.matched:
                # 匹配到话术模板
                full_response = template_result.answer
                
                # 记录使用
                if template_result.template:
                    dialogue_template_service.record_usage(template_result.template.id)
                
                # 发送来源
                yield f"data: {json.dumps({'type': 'sources', 'data': ['template:' + template_result.template.category.value]})}\n\n"
                
                # 发送置信度
                yield f"data: {json.dumps({'type': 'confidence', 'data': template_result.confidence})}\n\n"
                
                # 逐字发送
                for char in full_response:
                    yield f"data: {json.dumps({'type': 'token', 'data': char})}\n\n"
            else:
                # 未匹配到话术
                no_match_response = "抱歉，暂时没有找到匹配的话术模板。请联系人工客服获取帮助。"
                
                yield f"data: {json.dumps({'type': 'sources', 'data': ['no_match']})}\n\n"
                yield f"data: {json.dumps({'type': 'confidence', 'data': 0.0})}\n\n"
                
                for char in no_match_response:
                    yield f"data: {json.dumps({'type': 'token', 'data': char})}\n\n"
                
                full_response = no_match_response
            
            # 保存回复
            chat_service.save_message(conv_id, "assistant", full_response)
            
            # 发送完成
            yield f"data: {json.dumps({'type': 'done', 'data': {'conversation_id': conv_id, 'matched': template_result.matched}})}\n\n"
        
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


@router.post("/chat", tags=["商家客服"])
async def merchant_chat(
    user_id: str,
    message: str,
    merchant_id: Optional[str] = "default",
    conversation_id: Optional[str] = None
):
    """
    商家客服对话接口（非流式）
    
    返回完整的回复内容。
    """
    chat_service = ChatService()
    
    conv_id = chat_service.get_or_create_conversation_id(conversation_id)
    chat_service.save_message(conv_id, "user", message)
    
    # 匹配话术
    template_result = dialogue_template_service.match_template(
        message=message,
        merchant_id=merchant_id,
        threshold=0.6
    )
    
    if template_result.matched:
        full_response = template_result.answer
        if template_result.template:
            dialogue_template_service.record_usage(template_result.template.id)
        sources = ['template:' + template_result.template.category.value]
        confidence = template_result.confidence
    else:
        full_response = "抱歉，暂时没有找到匹配的话术模板。请联系人工客服获取帮助。"
        sources = ['no_match']
        confidence = 0.0
    
    chat_service.save_message(conv_id, "assistant", full_response)
    
    return {
        "response": full_response,
        "conversation_id": conv_id,
        "sources": sources,
        "confidence": confidence,
        "matched": template_result.matched
    }


# ========== 话术模板管理接口 ==========

from app.models.dialogue_template import (
    DialogueTemplate,
    TemplateCreateRequest,
    TemplateUpdateRequest,
    TemplateListResponse,
    TemplateCategory
)


@router.post("/templates", response_model=DialogueTemplate, summary="创建话术模板")
async def create_template(request: TemplateCreateRequest):
    """为商家创建话术模板"""
    try:
        template = dialogue_template_service.create_template(request)
        return template
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"创建失败: {str(e)}")


@router.get("/templates", summary="列出话术模板")
async def list_templates(
    merchant_id: Optional[str] = None,
    category: Optional[TemplateCategory] = None,
    enabled: Optional[bool] = None,
    page: int = 1,
    page_size: int = 20
):
    """列出话术模板（支持商家过滤）"""
    templates, total = dialogue_template_service.list_templates(
        merchant_id=merchant_id,
        category=category,
        enabled=enabled,
        page=page,
        page_size=page_size
    )
    
    return TemplateListResponse(
        templates=templates,
        total=total,
        page=page,
        page_size=page_size
    )


@router.get("/templates/{template_id}", response_model=DialogueTemplate, summary="获取话术模板")
async def get_template(template_id: str):
    """获取单个话术模板详情"""
    template = dialogue_template_service.get_template(template_id)
    if not template:
        raise HTTPException(status_code=404, detail="模板不存在")
    return template


@router.put("/templates/{template_id}", response_model=DialogueTemplate, summary="更新话术模板")
async def update_template(template_id: str, request: TemplateUpdateRequest):
    """更新话术模板"""
    template = dialogue_template_service.update_template(template_id, request)
    if not template:
        raise HTTPException(status_code=404, detail="模板不存在")
    return template


@router.delete("/templates/{template_id}", summary="删除话术模板")
async def delete_template(template_id: str):
    """删除话术模板"""
    success = dialogue_template_service.delete_template(template_id)
    if not success:
        raise HTTPException(status_code=404, detail="模板不存在")
    return {"success": True, "message": "删除成功"}


@router.post("/templates/match", summary="测试话术匹配")
async def test_match(
    message: str,
    merchant_id: str = "default",
    threshold: float = 0.6
):
    """测试话术匹配"""
    result = dialogue_template_service.match_template(
        message=message,
        merchant_id=merchant_id,
        threshold=threshold
    )
    return result


@router.get("/templates/categories", summary="获取话术分类")
async def get_categories():
    """获取所有可用的话术分类"""
    return {
        "categories": [
            {"value": "greeting", "label": "问候语", "description": "欢迎、问候类话术"},
            {"value": "product", "label": "产品介绍", "description": "产品咨询、介绍类话术"},
            {"value": "price", "label": "价格咨询", "description": "价格、优惠类话术"},
            {"value": "promotion", "label": "促销活动", "description": "活动、促销类话术"},
            {"value": "service", "label": "售后服务", "description": "售后、服务类话术"},
            {"value": "complaint", "label": "投诉处理", "description": "投诉、抱怨类话术"},
            {"value": "shipping", "label": "物流配送", "description": "发货、物流类话术"},
            {"value": "payment", "label": "支付问题", "description": "支付、付款类话术"},
            {"value": "return", "label": "退换货", "description": "退货、换货类话术"},
            {"value": "other", "label": "其他", "description": "其他类型话术"}
        ]
    }


@router.post("/templates/batch", summary="批量导入话术模板")
async def batch_import_templates(
    merchant_id: str,
    templates: list
):
    """批量导入话术模板"""
    imported = []
    errors = []
    
    for i, template_data in enumerate(templates):
        try:
            request = TemplateCreateRequest(
                merchant_id=merchant_id,
                category=TemplateCategory(template_data.get('category', 'other')),
                keywords=template_data.get('keywords', []),
                patterns=template_data.get('patterns', []),
                question_template=template_data.get('question_template', ''),
                answer_template=template_data.get('answer_template', ''),
                variables=template_data.get('variables', {}),
                priority=template_data.get('priority', 0),
                tags=template_data.get('tags', [])
            )
            template = dialogue_template_service.create_template(request)
            imported.append(template.id)
        except Exception as e:
            errors.append({"index": i, "error": str(e)})
    
    return {
        "success": True,
        "imported_count": len(imported),
        "imported_ids": imported,
        "errors": errors
    }


@router.get("/stats", summary="获取统计信息")
async def get_stats(merchant_id: Optional[str] = None):
    """获取话术模板统计信息"""
    templates, total = dialogue_template_service.list_templates(
        merchant_id=merchant_id,
        page_size=1000
    )
    
    # 按分类统计
    category_stats = {}
    total_usage = 0
    
    for template in templates:
        cat = template.category.value
        if cat not in category_stats:
            category_stats[cat] = {"count": 0, "usage": 0}
        category_stats[cat]["count"] += 1
        category_stats[cat]["usage"] += template.use_count
        total_usage += template.use_count
    
    return {
        "total_templates": total,
        "total_usage": total_usage,
        "by_category": category_stats,
        "merchant_id": merchant_id
    }
