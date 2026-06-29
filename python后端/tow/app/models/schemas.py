from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any
from datetime import datetime


class MessageHistory(BaseModel):
    """消息历史项"""
    role: str = Field(..., description="角色: user 或 assistant")
    content: str = Field(..., description="消息内容")
    timestamp: datetime = Field(default_factory=datetime.now, description="时间戳")


class ChatRequest(BaseModel):
    """聊天请求"""
    user_id: str = Field(..., description="用户ID")
    message: str = Field(..., description="用户消息")
    conversation_id: Optional[str] = Field(None, description="会话ID,不传则自动生成")
    merchant_id: Optional[str] = Field(None, description="商家ID，用于话术模板匹配")
    stream: bool = Field(False, description="是否使用流式输出")
    speech: bool = Field(False, description="是否返回语音数据")
    speaker_id: Optional[int] = Field(0, description="说话人ID，默认0")
    rate: Optional[float] = Field(1.0, description="语速，范围0.5-2.0")


class ChatResponse(BaseModel):
    """聊天响应"""
    response: str = Field(..., description="AI回复内容")
    conversation_id: str = Field(..., description="会话ID")
    is_streaming: bool = Field(False, description="是否为流式响应")
    sources: Optional[List[str]] = Field(None, description="参考来源文档ID列表")


class KnowledgeUploadRequest(BaseModel):
    """知识库上传请求(用于API文档)"""
    category: Optional[str] = Field("general", description="文档分类")
    metadata: Optional[Dict[str, Any]] = Field(None, description="额外元数据")


class KnowledgeUpdateResponse(BaseModel):
    """知识库更新响应"""
    success: bool = Field(..., description="是否成功")
    document_ids: List[str] = Field(default_factory=list, description="文档ID列表")
    message: str = Field(..., description="响应消息")


class KnowledgeListItem(BaseModel):
    """知识库列表项"""
    doc_id: str = Field(..., description="文档ID")
    category: str = Field(..., description="文档分类")
    filename: str = Field(..., description="文件名")
    created_at: datetime = Field(..., description="创建时间")
    metadata: Optional[Dict[str, Any]] = Field(None, description="元数据")


class ToolCallResult(BaseModel):
    """工具调用结果"""
    tool_name: str = Field(..., description="工具名称")
    arguments: Dict[str, Any] = Field(..., description="调用参数")
    result: str = Field(..., description="执行结果")
    success: bool = Field(True, description="是否成功")


class HealthResponse(BaseModel):
    """健康检查响应"""
    status: str = Field("healthy", description="服务状态")
    version: str = Field("1.0.0", description="版本号")
    timestamp: datetime = Field(default_factory=datetime.now, description="时间戳")


class SpeechToTextResponse(BaseModel):
    """语音转文字响应"""
    success: bool = Field(..., description="是否成功")
    text: str = Field(..., description="识别出的文字内容")
    duration: Optional[float] = Field(None, description="音频时长(秒)")
    message: str = Field("", description="提示消息")
