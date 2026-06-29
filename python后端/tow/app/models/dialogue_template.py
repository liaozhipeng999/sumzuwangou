"""
商家话术模板数据模型
用于存储商家的预设问答话术
"""

from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime
from enum import Enum


class TemplateCategory(str, Enum):
    """话术分类"""
    GREETING = "greeting"           # 问候语
    PRODUCT = "product"             # 产品介绍
    PRICE = "price"                 # 价格咨询
    PROMOTION = "promotion"         # 促销活动
    SERVICE = "service"             # 售后服务
    COMPLAINT = "complaint"         # 投诉处理
    SHIPPING = "shipping"           # 物流配送
    PAYMENT = "payment"             # 支付问题
    RETURN = "return"               # 退换货
    OTHER = "other"                 # 其他


class DialogueTemplate(BaseModel):
    """话术模板"""
    id: Optional[str] = Field(None, description="模板ID")
    merchant_id: str = Field(..., description="商家ID")
    category: TemplateCategory = Field(..., description="话术分类")
    
    # 触发条件
    keywords: List[str] = Field(default_factory=list, description="触发关键词列表")
    patterns: List[str] = Field(default_factory=list, description="匹配模式（正则表达式）")
    intent: Optional[str] = Field(None, description="意图标签")
    
    # 问答内容
    question_template: str = Field(..., description="问题模板/示例问题")
    answer_template: str = Field(..., description="回复模板")
    
    # 变量支持（如 {product_name}, {price} 等）
    variables: Optional[dict] = Field(default_factory=dict, description="变量定义")
    
    # 元数据
    priority: int = Field(default=0, description="优先级，数字越大优先级越高")
    enabled: bool = Field(default=True, description="是否启用")
    tags: List[str] = Field(default_factory=list, description="标签")
    
    # 统计
    use_count: int = Field(default=0, description="使用次数")
    last_used_at: Optional[datetime] = Field(None, description="最后使用时间")
    
    # 时间戳
    created_at: datetime = Field(default_factory=datetime.now, description="创建时间")
    updated_at: datetime = Field(default_factory=datetime.now, description="更新时间")

    class Config:
        json_schema_extra = {
            "example": {
                "merchant_id": "merchant_001",
                "category": "greeting",
                "keywords": ["你好", "您好", "在吗", "有人吗"],
                "patterns": ["^(你好|您好|在吗|有人吗)[？?]?"],
                "question_template": "你好，请问有人吗？",
                "answer_template": "您好！欢迎光临本店，请问有什么可以帮您的？",
                "priority": 10,
                "enabled": True,
                "tags": ["欢迎", "客服"]
            }
        }


class TemplateCreateRequest(BaseModel):
    """创建话术模板请求"""
    merchant_id: str = Field(..., description="商家ID")
    category: TemplateCategory = Field(..., description="话术分类")
    keywords: List[str] = Field(default_factory=list, description="触发关键词")
    patterns: List[str] = Field(default_factory=list, description="匹配模式")
    question_template: str = Field(..., description="问题模板")
    answer_template: str = Field(..., description="回复模板")
    variables: Optional[dict] = Field(default_factory=dict, description="变量定义")
    priority: int = Field(default=0, description="优先级")
    tags: List[str] = Field(default_factory=list, description="标签")


class TemplateUpdateRequest(BaseModel):
    """更新话术模板请求"""
    category: Optional[TemplateCategory] = Field(None, description="话术分类")
    keywords: Optional[List[str]] = Field(None, description="触发关键词")
    patterns: Optional[List[str]] = Field(None, description="匹配模式")
    question_template: Optional[str] = Field(None, description="问题模板")
    answer_template: Optional[str] = Field(None, description="回复模板")
    variables: Optional[dict] = Field(None, description="变量定义")
    priority: Optional[int] = Field(None, description="优先级")
    enabled: Optional[bool] = Field(None, description="是否启用")
    tags: Optional[List[str]] = Field(None, description="标签")


class TemplateMatchResult(BaseModel):
    """话术匹配结果"""
    matched: bool = Field(..., description="是否匹配成功")
    template: Optional[DialogueTemplate] = Field(None, description="匹配到的模板")
    confidence: float = Field(default=0.0, description="匹配置信度 0-1")
    answer: Optional[str] = Field(None, description="生成的回复（已填充变量）")


class TemplateListResponse(BaseModel):
    """话术模板列表响应"""
    templates: List[DialogueTemplate] = Field(default_factory=list, description="模板列表")
    total: int = Field(..., description="总数")
    page: int = Field(default=1, description="当前页")
    page_size: int = Field(default=20, description="每页数量")
