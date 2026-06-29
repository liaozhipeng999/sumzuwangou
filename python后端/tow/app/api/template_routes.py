"""
商家话术模板管理 API
"""

from fastapi import APIRouter, HTTPException, Query
from typing import Optional

from app.models.dialogue_template import (
    DialogueTemplate,
    TemplateCreateRequest,
    TemplateUpdateRequest,
    TemplateMatchResult,
    TemplateListResponse,
    TemplateCategory
)
from app.services.dialogue_template_service import dialogue_template_service

router = APIRouter(prefix="/api/templates", tags=["话术模板管理"])


@router.post("", response_model=DialogueTemplate, summary="创建话术模板")
async def create_template(request: TemplateCreateRequest):
    """
    创建新的话术模板
    
    - **merchant_id**: 商家ID
    - **category**: 话术分类
    - **keywords**: 触发关键词列表
    - **patterns**: 正则匹配模式列表
    - **question_template**: 问题模板
    - **answer_template**: 回复模板
    """
    try:
        template = dialogue_template_service.create_template(request)
        return template
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"创建失败: {str(e)}")


@router.get("/{template_id}", response_model=DialogueTemplate, summary="获取话术模板")
async def get_template(template_id: str):
    """根据ID获取话术模板详情"""
    template = dialogue_template_service.get_template(template_id)
    if not template:
        raise HTTPException(status_code=404, detail="模板不存在")
    return template


@router.put("/{template_id}", response_model=DialogueTemplate, summary="更新话术模板")
async def update_template(template_id: str, request: TemplateUpdateRequest):
    """更新话术模板"""
    template = dialogue_template_service.update_template(template_id, request)
    if not template:
        raise HTTPException(status_code=404, detail="模板不存在")
    return template


@router.delete("/{template_id}", summary="删除话术模板")
async def delete_template(template_id: str):
    """删除话术模板"""
    success = dialogue_template_service.delete_template(template_id)
    if not success:
        raise HTTPException(status_code=404, detail="模板不存在")
    return {"success": True, "message": "删除成功"}


@router.get("", response_model=TemplateListResponse, summary="列出话术模板")
async def list_templates(
    merchant_id: Optional[str] = Query(None, description="商家ID过滤"),
    category: Optional[TemplateCategory] = Query(None, description="分类过滤"),
    enabled: Optional[bool] = Query(None, description="启用状态过滤"),
    page: int = Query(1, ge=1, description="页码"),
    page_size: int = Query(20, ge=1, le=100, description="每页数量")
):
    """
    列出话术模板（支持分页和过滤）
    
    - 不传 merchant_id 则返回所有商家的模板
    - 不传 category 则返回所有分类
    """
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


@router.post("/match", response_model=TemplateMatchResult, summary="测试话术匹配")
async def test_match(
    message: str = Query(..., description="用户消息"),
    merchant_id: str = Query("default", description="商家ID"),
    threshold: float = Query(0.6, ge=0, le=1, description="匹配阈值")
):
    """
    测试话术匹配功能
    
    输入用户消息，返回匹配结果
    """
    result = dialogue_template_service.match_template(
        message=message,
        merchant_id=merchant_id,
        threshold=threshold
    )
    return result


@router.get("/categories/list", summary="获取话术分类列表")
async def list_categories():
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
