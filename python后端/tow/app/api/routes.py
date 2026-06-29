from fastapi import APIRouter, UploadFile, File, Form, HTTPException, Depends
from fastapi.responses import StreamingResponse
from typing import Optional
import json
import os
import tempfile
import aiofiles
from datetime import datetime

from app.models.schemas import (
    ChatRequest, ChatResponse, KnowledgeUpdateResponse, 
    KnowledgeListItem, HealthResponse, SpeechToTextResponse
)
from app.services.rag_service import RAGService
from app.services.chat_service import ChatService
from app.services.llm_service import LLMService
from app.services.tool_service import ToolService
from app.services.speech_service import SpeechService
from app.services.dialogue_template_service import dialogue_template_service
from app.api.dependencies import (
    get_rag_service, get_chat_service, get_llm_service, get_tool_service,
    get_speech_service
)
from app.config import settings

router = APIRouter()


@router.get("/health", response_model=HealthResponse, tags=["系统"])
async def health_check():
    """健康检查接口"""
    return HealthResponse(status="healthy", version="1.0.0")


@router.post("/knowledge/upload", response_model=KnowledgeUpdateResponse, tags=["知识库管理"])
async def upload_knowledge(
    file: UploadFile = File(...),
    category: str = Form("general"),
    metadata: Optional[str] = Form(None),
    rag_service: RAGService = Depends(get_rag_service)
):
    """
    上传知识库文档
    
    - **file**: 要上传的文件 (支持 .txt, .md, .pdf 等)
    - **category**: 文档分类
    - **metadata**: 额外的元数据 (JSON格式字符串)
    """
    # 验证文件类型
    filename = file.filename or ""
    ext = os.path.splitext(filename)[1].lower()
    allowed_types = settings.ALLOWED_FILE_TYPES.split(',')
    
    if ext not in allowed_types:
        raise HTTPException(
            status_code=400,
            detail=f"不支持的文件类型: {ext}。支持的类型: {allowed_types}"
        )
    
    # 验证文件大小
    contents = await file.read()
    if len(contents) > settings.MAX_FILE_SIZE:
        raise HTTPException(
            status_code=400,
            detail=f"文件大小超过限制 ({settings.MAX_FILE_SIZE / 1024 / 1024}MB)"
        )
    
    # 保存临时文件
    with tempfile.NamedTemporaryFile(delete=False, suffix=ext) as tmp_file:
        tmp_file.write(contents)
        tmp_path = tmp_file.name
    
    try:
        # 解析元数据
        extra_metadata = {}
        if metadata:
            try:
                extra_metadata = json.loads(metadata)
            except json.JSONDecodeError:
                raise HTTPException(status_code=400, detail="元数据格式错误,应为有效的JSON")
        
        # 上传到知识库
        doc_ids = await rag_service.upload_knowledge(
            file_path=tmp_path,
            category=category,
            metadata=extra_metadata
        )
        
        return KnowledgeUpdateResponse(
            success=True,
            document_ids=doc_ids,
            message=f"成功上传 {len(doc_ids)} 个文档片段"
        )
    
    finally:
        # 清理临时文件
        if os.path.exists(tmp_path):
            os.remove(tmp_path)


@router.delete("/knowledge/{doc_id}", response_model=KnowledgeUpdateResponse, tags=["知识库管理"])
async def delete_knowledge(
    doc_id: str,
    rag_service: RAGService = Depends(get_rag_service)
):
    """删除指定知识库文档"""
    success = rag_service.delete_knowledge([doc_id])
    
    if success:
        return KnowledgeUpdateResponse(
            success=True,
            document_ids=[doc_id],
            message="删除成功"
        )
    else:
        raise HTTPException(status_code=500, detail="删除失败")


@router.get("/knowledge/list", response_model=list[KnowledgeListItem], tags=["知识库管理"])
async def list_knowledge(
    limit: int = 100,
    offset: int = 0,
    rag_service: RAGService = Depends(get_rag_service)
):
    """列出知识库文档"""
    documents = rag_service.list_knowledge(limit=limit, offset=offset)
    
    items = []
    for doc in documents:
        metadata = doc.get('metadata', {})
        items.append(KnowledgeListItem(
            doc_id=doc['doc_id'],
            category=metadata.get('category', 'unknown'),
            filename=metadata.get('filename', 'unknown'),
            created_at=datetime.fromisoformat(metadata.get('created_at', datetime.now().isoformat())),
            metadata=metadata
        ))
    
    return items


@router.post("/chat", response_model=ChatResponse, tags=["聊天"])
async def chat(
    request: ChatRequest,
    chat_service: ChatService = Depends(get_chat_service),
    rag_service: RAGService = Depends(get_rag_service),
    llm_service: LLMService = Depends(get_llm_service),
    tool_service: ToolService = Depends(get_tool_service)
):
    """
    普通聊天接口 (非流式)
    
    - **user_id**: 用户ID
    - **message**: 用户消息
    - **conversation_id**: 会话ID (可选,不传则自动生成)
    """
    try:
        # 获取或创建会话ID
        conversation_id = chat_service.get_or_create_conversation_id(request.conversation_id)
        
        # 保存用户消息
        chat_service.save_message(conversation_id, "user", request.message)
        
        # 获取对话历史
        history = chat_service.get_history(conversation_id)
        history_str = chat_service.format_history(history[:-1])  # 排除当前消息
        
        # RAG 检索相关上下文
        context, source_ids = rag_service.retrieve_context(request.message, top_k=5)
        
        # 裁剪上下文和历史 (避免超出 Token 限制)
        context, history_str = chat_service.trim_context(context, history_str, request.message)
        
        # 调用 LLM 生成回复
        response_text = await llm_service.generate_with_context(
            context=context,
            question=request.message,
            history=history_str,
            stream=False
        )
        
        # 保存助手回复
        chat_service.save_message(conversation_id, "assistant", response_text)
        
        return ChatResponse(
            response=response_text,
            conversation_id=conversation_id,
            is_streaming=False,
            sources=source_ids if source_ids else None
        )
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"聊天处理失败: {str(e)}")


@router.post("/chat/stream", tags=["聊天"])
async def chat_stream(
    request: ChatRequest,
    chat_service: ChatService = Depends(get_chat_service),
    rag_service: RAGService = Depends(get_rag_service),
    llm_service: LLMService = Depends(get_llm_service)
):
    """
    流式聊天接口 (SSE)
    
    返回 Server-Sent Events 流,逐字输出 AI 回复
    
    **新增语音功能:**
    - 设置 speech=true 可同时返回语音数据
    - 支持自定义说话人和语速
    - 适合实时对话外放场景，特别适合老人使用
    
    **音频格式:**
    - 采样率: 24kHz
    - 位深度: 16-bit
    - 声道: 单声道
    - 格式: PCM WAV (base64编码)
    """
    
    async def event_generator():
        try:
            # 获取或创建会话ID
            conversation_id = chat_service.get_or_create_conversation_id(request.conversation_id)
            
            # 保存用户消息
            chat_service.save_message(conversation_id, "user", request.message)
            
            # 获取对话历史
            history = chat_service.get_history(conversation_id)
            history_str = chat_service.format_history(history[:-1])  # 排除当前消息
            
            # ========== 混合模式：先尝试匹配话术模板 ==========
            template_result = dialogue_template_service.match_template(
                message=request.message,
                merchant_id=request.merchant_id if hasattr(request, 'merchant_id') and request.merchant_id else "default",
                threshold=0.6
            )
            
            if template_result.matched:
                # 匹配到话术模板，直接返回预设回复
                full_response = template_result.answer
                
                # 记录模板使用
                if template_result.template:
                    dialogue_template_service.record_usage(template_result.template.id)
                
                # 发送来源信息（标记为话术模板）
                yield f"data: {json.dumps({'type': 'sources', 'data': ['template:' + template_result.template.category.value if template_result.template else 'template']})}\n\n"
                
                # 发送匹配置信度
                yield f"data: {json.dumps({'type': 'confidence', 'data': template_result.confidence})}\n\n"
                
                # 逐字发送（模拟打字效果）
                for char in full_response:
                    yield f"data: {json.dumps({'type': 'token', 'data': char})}\n\n"
            else:
                # 未匹配到话术，走 RAG + LLM 流程
                
                # RAG 检索相关上下文
                context, source_ids = rag_service.retrieve_context(request.message, top_k=5)
                
                # 裁剪上下文和历史
                context, history_str = chat_service.trim_context(context, history_str, request.message)
                
                # 发送来源信息
                if source_ids:
                    yield f"data: {json.dumps({'type': 'sources', 'data': source_ids})}\n\n"
                
                # 流式调用 LLM
                full_response = ""
                response_generator = llm_service.generate_with_context(
                    context=context,
                    question=request.message,
                    history=history_str,
                    stream=True
                )
                async for token in response_generator:
                    full_response += token
                    yield f"data: {json.dumps({'type': 'token', 'data': token})}\n\n"
            
            # 保存完整回复
            chat_service.save_message(conversation_id, "assistant", full_response)
            
            # 如果需要语音输出
            if request.speech:
                try:
                    from app.services.tts_service import TtsService
                    
                    tts_service = TtsService()
                    # 异步初始化（如果还没初始化）
                    if not await tts_service.initialize():
                        yield f"data: {json.dumps({'type': 'speech_error', 'data': 'TTS服务初始化失败'})}\n\n"
                    else:
                        # 生成语音
                        print(f"开始异步生成语音: {full_response}", flush=True)
                        audio_chunks = []
                        async for chunk in tts_service.synthesize_stream(full_response, request.speaker_id):
                            audio_chunks.append(chunk)
                        
                        # 合并音频
                        audio_data = b''.join(audio_chunks)
                        
                        # 检查大小限制 (10MB)
                        max_size = 10 * 1024 * 1024  # 10MB
                        if len(audio_data) > max_size:
                            yield f"data: {json.dumps({'type': 'speech_error', 'data': f'语音数据过大({len(audio_data)//1024//1024}MB)，超过10MB限制'})}\n\n"
                        else:
                            # 编码为hex发送
                            audio_base64 = audio_data.hex()
                            yield f"data: {json.dumps({'type': 'speech', 'data': audio_base64, 'format': 'wav', 'sample_rate': 16000, 'size': len(audio_data)})}\n\n"
                except Exception as tts_error:
                    yield f"data: {json.dumps({'type': 'speech_error', 'data': f'语音生成失败: {str(tts_error)}'})}\n\n"
            
            # 发送结束标记
            yield f"data: {json.dumps({'type': 'done', 'data': {'conversation_id': conversation_id}})}\n\n"
        
        except Exception as e:
            error_msg = f"聊天处理失败: {str(e)}"
            yield f"data: {json.dumps({'type': 'error', 'data': error_msg})}\n\n"
    
    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no"  # 禁用 Nginx 缓冲
        }
    )


@router.get("/stats", tags=["系统"])
async def get_stats(
    chat_service: ChatService = Depends(get_chat_service),
    rag_service: RAGService = Depends(get_rag_service)
):
    """获取系统统计信息"""
    return {
        "active_conversations": chat_service.get_conversation_count(),
        "knowledge_documents": rag_service.get_document_count()
    }


# 语音转文字接口
@router.post("/speech/to-text", tags=["语音"])
async def speech_to_text(
    file: UploadFile = File(..., description="音频文件"),
    speech_service: SpeechService = Depends(get_speech_service)
):
    """
    语音转文字接口
    
    支持的音频格式: WAV, MP3, OGG, FLAC
    最大支持时长: 60秒
    文件大小限制: 10MB
    
    Args:
        file: 音频文件
    
    Returns:
        识别出的文字内容
    """
    # 检查文件大小
    file_size = 0
    chunk_size = 4096
    file_data = b""
    
    while True:
        chunk = await file.read(chunk_size)
        if not chunk:
            break
        file_data += chunk
        file_size += len(chunk)
        if file_size > 50 * 1024 * 1024:  # 50MB 限制
            raise HTTPException(status_code=400, detail="文件大小超过限制(最大50MB)")
    
    # 获取文件扩展名
    filename = file.filename
    if not filename:
        raise HTTPException(status_code=400, detail="文件名不能为空")
    
    file_ext = filename.split(".")[-1].lower()
    
    # 支持的格式
    supported_formats = ["wav", "mp3", "ogg", "flac"]
    if file_ext not in supported_formats:
        raise HTTPException(status_code=400, detail=f"不支持的音频格式: {file_ext}，支持格式: {', '.join(supported_formats)}")
    
    try:
        # 调用语音转文字服务
        text = speech_service.convert_speech_to_text(file_data, file_ext)
        
        return {
            "success": True,
            "text": text,
            "message": "语音识别成功"
        }
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"语音识别失败: {str(e)}")


# TTS 文字转语音接口
@router.post("/speech/tts", tags=["语音"])
async def text_to_speech(
    text: str = Form(..., description="要转换的文本"),
    speaker_id: Optional[int] = Form(0, description="说话人ID，默认0"),
    rate: Optional[float] = Form(1.0, description="语速，范围0.5-2.0"),
    pitch: Optional[float] = Form(0.0, description="音调偏移，范围-100-100"),
    use_clone: Optional[bool] = Form(True, description="是否使用语音克隆")
):
    """
    文字转语音（一次性生成）
    
    将文本转换为语音音频，返回完整的WAV音频数据。
    适用于短文本、非实时场景。
    
    参数说明：
    - text: 要转换的文本
    - speaker_id: 说话人ID
    - use_clone: 是否使用语音克隆（使用参考音色爻光.MP3）
    """
    from app.services.tts_service import TtsService
    
    tts_service = TtsService()
    
    if not await tts_service.initialize():
        raise HTTPException(status_code=503, detail="TTS服务未就绪，请检查VoxCPM2模型是否正确配置")
    
    try:
        audio_data = await tts_service.synthesize(text, speaker_id, rate, use_clone)
        
        return StreamingResponse(
            iter([audio_data]),
            media_type="audio/wav",
            headers={
                "Content-Disposition": "attachment; filename=speech.wav"
            }
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"TTS转换失败: {str(e)}")


@router.post("/speech/tts/stream", tags=["语音"])
async def text_to_speech_stream(
    text: str = Form(..., description="要转换的文本"),
    speaker_id: Optional[int] = Form(0, description="说话人ID，默认0"),
    rate: Optional[float] = Form(1.0, description="语速，范围0.5-2.0"),
    pitch: Optional[float] = Form(0.0, description="音调偏移，范围-100-100")
):
    """
    文字转语音（流式输出）- 首推用于实时外放
    
    **特点：**
    - 流式输出，低延迟，适合实时对话场景
    - 消费级GPU可实现5倍实时速度
    - 端到端延迟低于150ms
    - 非常适合老人使用的外放功能
    
    **使用场景：**
    - AI对话实时外放
    - 智能客服语音回复
    - 语音助手实时响应
    
    **音频格式：**
    - 采样率: 16kHz
    - 位深度: 16-bit
    - 声道: 单声道
    - 格式: PCM WAV
    """
    from app.services.tts_service import TtsService
    
    tts_service = TtsService()
    
    if not await tts_service.initialize():
        raise HTTPException(status_code=503, detail="TTS服务未就绪，请检查VoxCPM2模型是否正确配置")
    
    async def generate_audio():
        async for chunk in tts_service.synthesize_stream(text, speaker_id):
            yield chunk
    
    return StreamingResponse(
        generate_audio(),
        media_type="audio/wav",
        headers={
            "Transfer-Encoding": "chunked",
            "Content-Type": "audio/wav"
        }
    )


# 会话管理接口
@router.post("/session/end", tags=["会话管理"])
async def end_session(
    user_id: str = Form(..., description="用户ID"),
    conversation_id: Optional[str] = Form(None, description="会话ID，不提供则清理用户所有会话")
):
    """
    结束会话并清理语音文件
    
    当用户结束会话或超过5小时未活动时，清理该会话生成的所有语音文件。
    """
    from app.services.tts_service import TtsService
    
    tts_service = TtsService()
    await tts_service.cleanup_session(user_id, conversation_id)
    
    return {
        "status": "success",
        "message": f"已清理用户 {user_id} 的会话语音文件",
        "conversation_id": conversation_id
    }


@router.get("/session/info", tags=["会话管理"])
async def get_session_info(user_id: str):
    """
    获取用户会话信息
    
    返回用户当前活跃会话及其语音文件列表。
    """
    from app.services.tts_service import TtsService
    
    tts_service = TtsService()
    session_info = tts_service.get_session_info(user_id)
    
    return {
        "user_id": user_id,
        "sessions": session_info,
        "total_files": sum(len(s["files"]) for s in session_info.values())
    }


# 常见问题接口
@router.get("/questions/common", tags=["常见问题"])
async def get_common_questions():
    """
    获取常见问题列表
    
    返回预设的常见客服问题和回复。
    """
    from app.services.tts_service import COMMON_QUESTIONS
    
    return {
        "questions": COMMON_QUESTIONS,
        "count": len(COMMON_QUESTIONS)
    }


@router.get("/questions/predicted", tags=["常见问题"])
async def get_predicted_questions():
    """
    获取预测问题列表
    
    返回预测用户可能问的问题。
    """
    from app.services.tts_service import PREDICTED_QUESTIONS
    
    return {
        "questions": PREDICTED_QUESTIONS,
        "count": len(PREDICTED_QUESTIONS)
    }


@router.post("/questions/pregenerate", tags=["常见问题"])
async def pregenerate_question_speech(
    user_id: str = Form(..., description="用户ID"),
    conversation_id: str = Form(..., description="会话ID"),
    questions: Optional[str] = Form(None, description="自定义问题列表(JSON格式)，不提供则使用预设问题")
):
    """
    预生成常见问题的语音
    
    为指定会话预生成语音文件，使用语音克隆功能。
    生成的语音文件会在会话结束或超时后自动清理。
    """
    from app.services.tts_service import TtsService, COMMON_QUESTIONS, PREDICTED_QUESTIONS
    import json
    
    tts_service = TtsService()
    
    if not await tts_service.initialize():
        raise HTTPException(status_code=503, detail="TTS服务未就绪")
    
    # 解析自定义问题或使用预设
    if questions:
        try:
            question_list = json.loads(questions)
        except json.JSONDecodeError:
            raise HTTPException(status_code=400, detail="questions参数JSON格式错误")
    else:
        question_list = COMMON_QUESTIONS + PREDICTED_QUESTIONS
    
    generated_files = []
    
    for i, text in enumerate(question_list, 1):
        try:
            file_path = await tts_service.synthesize_and_save(
                text=text,
                user_id=user_id,
                conversation_id=conversation_id,
                filename=f"pregen_{user_id}_{conversation_id}_{i}.wav"
            )
            generated_files.append({
                "text": text,
                "file_path": file_path,
                "index": i
            })
        except Exception as e:
            print(f"预生成失败: {text}, {e}", flush=True)
    
    return {
        "status": "success",
        "user_id": user_id,
        "conversation_id": conversation_id,
        "generated_count": len(generated_files),
        "files": generated_files
    }


@router.post("/speech/tts/save", tags=["语音"])
async def text_to_speech_save(
    text: str = Form(..., description="要转换的文本"),
    user_id: str = Form(..., description="用户ID"),
    conversation_id: str = Form(..., description="会话ID"),
    use_clone: bool = Form(True, description="是否使用语音克隆")
):
    """
    文字转语音并保存
    
    将文本转换为语音并保存到指定目录，同时记录到会话缓存。
    会话结束或超时后自动清理。
    """
    from app.services.tts_service import TtsService
    
    tts_service = TtsService()
    
    if not await tts_service.initialize():
        raise HTTPException(status_code=503, detail="TTS服务未就绪")
    
    try:
        file_path = await tts_service.synthesize_and_save(
            text=text,
            user_id=user_id,
            conversation_id=conversation_id
        )
        
        return {
            "status": "success",
            "file_path": file_path,
            "text": text,
            "user_id": user_id,
            "conversation_id": conversation_id
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"TTS转换失败: {str(e)}")
