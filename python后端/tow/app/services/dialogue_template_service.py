"""
商家话术模板匹配服务
支持关键词匹配、正则匹配、语义相似度匹配
"""

import re
import json
import os
from typing import List, Optional, Dict, Any
from datetime import datetime
import uuid

from app.models.dialogue_template import (
    DialogueTemplate, 
    TemplateMatchResult, 
    TemplateCategory,
    TemplateCreateRequest,
    TemplateUpdateRequest
)


class DialogueTemplateService:
    """话术模板服务"""
    
    def __init__(self, data_dir: str = "./data/templates"):
        """
        初始化服务
        
        Args:
            data_dir: 数据存储目录
        """
        self.data_dir = data_dir
        self.templates: Dict[str, DialogueTemplate] = {}  # id -> template
        self.merchant_templates: Dict[str, List[str]] = {}  # merchant_id -> [template_ids]
        
        # 确保数据目录存在
        os.makedirs(data_dir, exist_ok=True)
        
        # 加载已有数据
        self._load_templates()
        
        # 初始化默认话术
        self._init_default_templates()
    
    def _load_templates(self):
        """从文件加载话术模板"""
        template_file = os.path.join(self.data_dir, "templates.json")
        if os.path.exists(template_file):
            try:
                with open(template_file, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    for item in data:
                        template = DialogueTemplate(**item)
                        self.templates[template.id] = template
                        
                        if template.merchant_id not in self.merchant_templates:
                            self.merchant_templates[template.merchant_id] = []
                        self.merchant_templates[template.merchant_id].append(template.id)
                
                print(f"已加载 {len(self.templates)} 个话术模板")
            except Exception as e:
                print(f"加载话术模板失败: {e}")
    
    def _save_templates(self):
        """保存话术模板到文件"""
        template_file = os.path.join(self.data_dir, "templates.json")
        try:
            data = [t.model_dump() for t in self.templates.values()]
            with open(template_file, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2, default=str)
        except Exception as e:
            print(f"保存话术模板失败: {e}")
    
    def _init_default_templates(self):
        """初始化默认话术模板"""
        if len(self.templates) > 0:
            return
        
        default_templates = [
            # 问候语
            {
                "merchant_id": "default",
                "category": TemplateCategory.GREETING,
                "keywords": ["你好", "您好", "在吗", "有人吗", "客服"],
                "patterns": ["^(你好|您好|在吗|有人吗|客服)[？?？]?$"],
                "question_template": "你好，请问有人吗？",
                "answer_template": "您好！欢迎光临本店，我是您的专属客服，请问有什么可以帮您的？😊",
                "priority": 10,
                "tags": ["欢迎", "问候"]
            },
            # 产品咨询
            {
                "merchant_id": "default",
                "category": TemplateCategory.PRODUCT,
                "keywords": ["产品", "商品", "介绍", "怎么样", "好不好"],
                "patterns": ["(这个|这件|这款).*(产品|商品).*(怎么样|好不好|介绍)", ".*介绍.*产品"],
                "question_template": "这个产品怎么样？",
                "answer_template": "这款产品是我们店的热销商品，质量非常好，用户评价也很高。具体特点包括：\n1. 品质优良，材质上乘\n2. 设计时尚，款式新颖\n3. 性价比高，价格实惠\n您还有其他想了解的吗？",
                "priority": 8,
                "tags": ["产品", "咨询"]
            },
            # 价格咨询
            {
                "merchant_id": "default",
                "category": TemplateCategory.PRICE,
                "keywords": ["价格", "多少钱", "便宜", "优惠", "折扣"],
                "patterns": ["(价格|多少钱|便宜点|优惠|折扣)", ".*多少钱.*"],
                "question_template": "这个多少钱？",
                "answer_template": "您好，这款商品目前的价格非常优惠！现在下单还可以享受会员折扣哦~ 具体价格请以商品页面为准。如果您购买多件，还可以联系我申请更多优惠！",
                "priority": 9,
                "tags": ["价格", "优惠"]
            },
            # 物流配送
            {
                "merchant_id": "default",
                "category": TemplateCategory.SHIPPING,
                "keywords": ["发货", "物流", "快递", "什么时候到", "几天到"],
                "patterns": ["(发货|物流|快递|配送)", ".*什么时候.*到", ".*几天.*到"],
                "question_template": "什么时候发货？",
                "answer_template": "您好，我们承诺下单后24小时内发货！\n📦 发货时效：工作日当天16:00前下单当天发出\n🚚 配送时效：\n- 同城：1-2天\n- 省内：2-3天\n- 跨省：3-5天\n您可以在订单详情中查看物流信息哦~",
                "priority": 8,
                "tags": ["物流", "发货"]
            },
            # 退换货
            {
                "merchant_id": "default",
                "category": TemplateCategory.RETURN,
                "keywords": ["退货", "换货", "退款", "不想要", "质量问题"],
                "patterns": ["(退货|换货|退款|退换)", ".*不想要.*", ".*质量问题.*"],
                "question_template": "我想退货怎么办？",
                "answer_template": "您好，我们支持7天无理由退换货！\n📝 退换货流程：\n1. 进入【我的订单】找到对应订单\n2. 点击【申请售后】\n3. 选择退换货原因并提交\n4. 等待审核通过后寄回商品\n\n⚠️ 注意：商品需保持原包装完好，配件齐全。\n如有其他问题，随时联系我哦~",
                "priority": 9,
                "tags": ["售后", "退货"]
            },
            # 促销活动
            {
                "merchant_id": "default",
                "category": TemplateCategory.PROMOTION,
                "keywords": ["活动", "促销", "满减", "优惠券", "福利"],
                "patterns": ["(活动|促销|满减|优惠券|福利)"],
                "question_template": "有什么活动吗？",
                "answer_template": "您好！我们店正在进行超值优惠活动：\n🎁 新人专享：首单立减20元\n💰 满减活动：满199减30，满399减80\n🎫 会员福利：积分可兑换优惠券\n\n点击店铺首页【优惠券】专区领取哦~",
                "priority": 8,
                "tags": ["活动", "优惠"]
            },
            # 支付问题
            {
                "merchant_id": "default",
                "category": TemplateCategory.PAYMENT,
                "keywords": ["支付", "付款", "怎么付", "支付失败"],
                "patterns": ["(支付|付款|怎么付|支付失败)"],
                "question_template": "怎么支付？",
                "answer_template": "您好，我们支持多种支付方式：\n💳 支付宝/微信支付\n🏦 银行卡支付\n💰 货到付款（部分地区）\n\n如遇支付问题，请检查：\n1. 网络是否正常\n2. 账户余额是否充足\n3. 是否超出支付限额\n\n还有其他问题吗？",
                "priority": 7,
                "tags": ["支付"]
            },
            # 投诉处理
            {
                "merchant_id": "default",
                "category": TemplateCategory.COMPLAINT,
                "keywords": ["投诉", "不满意", "差评", "投诉你们"],
                "patterns": ["(投诉|不满意|差评)"],
                "question_template": "我要投诉！",
                "answer_template": "非常抱歉给您带来了不好的体验！🙏\n\n我们会认真对待您的反馈，请告诉我具体的问题，我会尽快帮您解决。如果问题严重，我会帮您升级到主管处理。\n\n感谢您的理解与支持！",
                "priority": 10,
                "tags": ["投诉", "售后"]
            },
            # 结束语
            {
                "merchant_id": "default",
                "category": TemplateCategory.OTHER,
                "keywords": ["好的", "谢谢", "感谢", "没问题", "再见", "拜拜"],
                "patterns": ["^(好的|谢谢|感谢|没问题|再见|拜拜)[！!。.？?]*$"],
                "question_template": "谢谢！",
                "answer_template": "不客气，很高兴能帮到您！😊\n\n如果还有其他问题，随时联系我哦~ 祝您购物愉快！",
                "priority": 5,
                "tags": ["感谢", "结束"]
            }
        ]
        
        for template_data in default_templates:
            template_id = str(uuid.uuid4())
            template_data["id"] = template_id
            template = DialogueTemplate(**template_data)
            self.templates[template_id] = template
            
            if template.merchant_id not in self.merchant_templates:
                self.merchant_templates[template.merchant_id] = []
            self.merchant_templates[template.merchant_id].append(template_id)
        
        self._save_templates()
        print(f"已初始化 {len(default_templates)} 个默认话术模板")
    
    def match_template(
        self, 
        message: str, 
        merchant_id: str = "default",
        threshold: float = 0.6
    ) -> TemplateMatchResult:
        """
        匹配话术模板
        
        Args:
            message: 用户消息
            merchant_id: 商家ID
            threshold: 匹配阈值（0-1）
            
        Returns:
            匹配结果
        """
        # 获取该商家的话术模板（包括默认模板）
        template_ids = set()
        if merchant_id in self.merchant_templates:
            template_ids.update(self.merchant_templates[merchant_id])
        if "default" in self.merchant_templates:
            template_ids.update(self.merchant_templates["default"])
        
        candidates = []
        
        for tid in template_ids:
            template = self.templates.get(tid)
            if not template or not template.enabled:
                continue
            
            # 计算匹配分数
            score = self._calculate_match_score(message, template)
            
            if score > 0:
                candidates.append((template, score))
        
        # 按分数和优先级排序
        candidates.sort(key=lambda x: (x[1], x[0].priority), reverse=True)
        
        if candidates:
            best_template, best_score = candidates[0]
            
            # 归一化分数到 0-1
            confidence = min(best_score / 10.0, 1.0)
            
            if confidence >= threshold:
                # 填充变量生成回复
                answer = self._fill_variables(best_template.answer_template, {})
                
                return TemplateMatchResult(
                    matched=True,
                    template=best_template,
                    confidence=confidence,
                    answer=answer
                )
        
        return TemplateMatchResult(matched=False)
    
    def _calculate_match_score(self, message: str, template: DialogueTemplate) -> float:
        """
        计算匹配分数
        
        Args:
            message: 用户消息
            template: 话术模板
            
        Returns:
            匹配分数（越高越匹配）
        """
        score = 0.0
        message_lower = message.lower().strip()
        
        # 1. 正则表达式匹配（权重最高）
        for pattern in template.patterns:
            try:
                if re.search(pattern, message_lower, re.IGNORECASE):
                    score += 5.0
                    break
            except re.error:
                continue
        
        # 2. 关键词匹配
        keyword_matches = 0
        for keyword in template.keywords:
            if keyword.lower() in message_lower:
                keyword_matches += 1
        
        if keyword_matches > 0:
            # 匹配的关键词越多，分数越高
            score += min(keyword_matches * 1.5, 4.0)
        
        # 3. 完全匹配问题模板
        if message_lower == template.question_template.lower().strip():
            score += 3.0
        
        # 4. 加上优先级权重
        score += template.priority * 0.1
        
        return score
    
    def _fill_variables(self, template: str, variables: dict) -> str:
        """
        填充模板变量
        
        Args:
            template: 模板字符串
            variables: 变量字典
            
        Returns:
            填充后的字符串
        """
        result = template
        for key, value in variables.items():
            result = result.replace(f"{{{key}}}", str(value))
        return result
    
    def create_template(self, request: TemplateCreateRequest) -> DialogueTemplate:
        """创建话术模板"""
        template_id = str(uuid.uuid4())
        
        template = DialogueTemplate(
            id=template_id,
            merchant_id=request.merchant_id,
            category=request.category,
            keywords=request.keywords,
            patterns=request.patterns,
            question_template=request.question_template,
            answer_template=request.answer_template,
            variables=request.variables or {},
            priority=request.priority,
            tags=request.tags,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        self.templates[template_id] = template
        
        if template.merchant_id not in self.merchant_templates:
            self.merchant_templates[template.merchant_id] = []
        self.merchant_templates[template.merchant_id].append(template_id)
        
        self._save_templates()
        
        return template
    
    def update_template(self, template_id: str, request: TemplateUpdateRequest) -> Optional[DialogueTemplate]:
        """更新话术模板"""
        template = self.templates.get(template_id)
        if not template:
            return None
        
        update_data = request.model_dump(exclude_unset=True)
        
        for key, value in update_data.items():
            if value is not None:
                setattr(template, key, value)
        
        template.updated_at = datetime.now()
        
        self._save_templates()
        
        return template
    
    def delete_template(self, template_id: str) -> bool:
        """删除话术模板"""
        template = self.templates.get(template_id)
        if not template:
            return False
        
        # 从商家索引中移除
        if template.merchant_id in self.merchant_templates:
            if template_id in self.merchant_templates[template.merchant_id]:
                self.merchant_templates[template.merchant_id].remove(template_id)
        
        # 删除模板
        del self.templates[template_id]
        
        self._save_templates()
        
        return True
    
    def get_template(self, template_id: str) -> Optional[DialogueTemplate]:
        """获取单个话术模板"""
        return self.templates.get(template_id)
    
    def list_templates(
        self, 
        merchant_id: Optional[str] = None,
        category: Optional[TemplateCategory] = None,
        enabled: Optional[bool] = None,
        page: int = 1,
        page_size: int = 20
    ) -> tuple[List[DialogueTemplate], int]:
        """
        列出话术模板
        
        Args:
            merchant_id: 商家ID过滤
            category: 分类过滤
            enabled: 启用状态过滤
            page: 页码
            page_size: 每页数量
            
        Returns:
            (模板列表, 总数)
        """
        templates = list(self.templates.values())
        
        # 过滤
        if merchant_id:
            templates = [t for t in templates if t.merchant_id == merchant_id]
        if category:
            templates = [t for t in templates if t.category == category]
        if enabled is not None:
            templates = [t for t in templates if t.enabled == enabled]
        
        # 排序（按优先级降序，创建时间降序）
        templates.sort(key=lambda x: (x.priority, x.created_at), reverse=True)
        
        total = len(templates)
        
        # 分页
        start = (page - 1) * page_size
        end = start + page_size
        
        return templates[start:end], total
    
    def record_usage(self, template_id: str):
        """记录模板使用"""
        template = self.templates.get(template_id)
        if template:
            template.use_count += 1
            template.last_used_at = datetime.now()
            self._save_templates()


# 创建全局实例
dialogue_template_service = DialogueTemplateService()
