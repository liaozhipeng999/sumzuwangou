"""
商家客服数据库服务

负责会话、消息、语料的数据库操作
"""

from sqlalchemy.orm import Session
from sqlalchemy import desc, func
from app.models.mysql_models import (
    TermConversation, TermMessage, TermCorpus, TermMerchants, get_db
)
from datetime import datetime
import uuid
from typing import List, Optional, Dict


class MerchantDbService:
    """商家数据库服务"""
    
    def __init__(self):
        pass
    
    # ========== 会话管理 ==========
    def get_or_create_conversation(
        self, 
        db: Session, 
        merchant_id: int, 
        user_id: str, 
        user_name: Optional[str] = None
    ) -> TermConversation:
        """
        获取或创建会话
        
        Args:
            db: 数据库会话
            merchant_id: 商家ID
            user_id: 用户ID
            user_name: 用户名称
        
        Returns:
            会话对象
        """
        # 查找用户与该商家的活跃会话
        conversation = db.query(TermConversation).filter(
            TermConversation.merchant_id == merchant_id,
            TermConversation.user_id == user_id,
            TermConversation.status == 1
        ).first()
        
        if conversation:
            # 更新会话时间
            conversation.updated_at = datetime.now()
            db.commit()
            return conversation
        
        # 创建新会话
        new_conversation = TermConversation(
            id=str(uuid.uuid4()),
            merchant_id=merchant_id,
            user_id=user_id,
            user_name=user_name,
            status=1,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        db.add(new_conversation)
        db.commit()
        db.refresh(new_conversation)
        
        return new_conversation
    
    def get_conversation(self, db: Session, conversation_id: str) -> Optional[TermConversation]:
        """获取会话详情"""
        return db.query(TermConversation).filter(
            TermConversation.id == conversation_id
        ).first()
    
    def end_conversation(self, db: Session, conversation_id: str) -> bool:
        """结束会话"""
        conversation = self.get_conversation(db, conversation_id)
        if conversation:
            conversation.status = 0
            conversation.updated_at = datetime.now()
            db.commit()
            return True
        return False
    
    def get_user_conversations(self, db: Session, merchant_id: int, user_id: str) -> List[TermConversation]:
        """获取用户与商家的所有会话"""
        return db.query(TermConversation).filter(
            TermConversation.merchant_id == merchant_id,
            TermConversation.user_id == user_id
        ).order_by(desc(TermConversation.created_at)).all()
    
    # ========== 消息管理 ==========
    def save_message(
        self, 
        db: Session, 
        conversation_id: str, 
        role: str, 
        content: str, 
        source: Optional[str] = None
    ) -> TermMessage:
        """
        保存消息
        
        Args:
            db: 数据库会话
            conversation_id: 会话ID
            role: 角色（user/assistant）
            content: 消息内容
            source: 消息来源（corpus/llm/template）
        
        Returns:
            消息对象
        """
        message = TermMessage(
            conversation_id=conversation_id,
            role=role,
            content=content,
            source=source,
            timestamp=datetime.now()
        )
        
        db.add(message)
        db.commit()
        db.refresh(message)
        
        return message
    
    def get_conversation_messages(self, db: Session, conversation_id: str, limit: int = 50) -> List[TermMessage]:
        """获取会话的消息列表"""
        return db.query(TermMessage).filter(
            TermMessage.conversation_id == conversation_id
        ).order_by(TermMessage.timestamp).limit(limit).all()
    
    def format_history(self, messages: List[TermMessage]) -> str:
        """格式化历史对话为字符串"""
        if not messages:
            return ""
        
        formatted_parts = []
        for msg in messages:
            role_label = "用户" if msg.role == "user" else "助手"
            formatted_parts.append(f"{role_label}: {msg.content}")
        
        return "\n".join(formatted_parts)
    
    # ========== 语料管理 ==========
    def get_merchant_corpus(self, db: Session, merchant_id: int) -> List[TermCorpus]:
        """获取商家的所有语料"""
        return db.query(TermCorpus).filter(
            TermCorpus.merchant_id == merchant_id,
            TermCorpus.enabled == 1
        ).order_by(desc(TermCorpus.priority)).all()
    
    def search_corpus(self, db: Session, merchant_id: int, query: str, top_k: int = 5) -> List[TermCorpus]:
        """
        搜索匹配的语料
        
        使用关键词匹配和相似度匹配
        """
        corpus_list = self.get_merchant_corpus(db, merchant_id)
        
        # 简单的关键词匹配
        matched = []
        query_lower = query.lower()
        
        for corpus in corpus_list:
            score = 0
            
            # 问题匹配
            if query_lower in corpus.question.lower():
                score += 5
            
            # 关键词匹配
            if corpus.keywords:
                keywords = [k.strip().lower() for k in corpus.keywords.split(',')]
                for keyword in keywords:
                    if keyword in query_lower:
                        score += 2
            
            if score > 0:
                matched.append((corpus, score + corpus.priority))
        
        # 按分数排序
        matched.sort(key=lambda x: x[1], reverse=True)
        
        # 返回 top_k
        return [item[0] for item in matched[:top_k]]
    
    def create_corpus(self, db: Session, merchant_id: int, question: str, answer: str, 
                     keywords: Optional[str] = None, priority: int = 0) -> TermCorpus:
        """创建语料"""
        corpus = TermCorpus(
            id=str(uuid.uuid4()),
            merchant_id=merchant_id,
            question=question,
            answer=answer,
            keywords=keywords,
            priority=priority,
            enabled=1,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        db.add(corpus)
        db.commit()
        db.refresh(corpus)
        
        return corpus
    
    def update_corpus(self, db: Session, corpus_id: str, **kwargs) -> Optional[TermCorpus]:
        """更新语料"""
        corpus = db.query(TermCorpus).filter(TermCorpus.id == corpus_id).first()
        if not corpus:
            return None
        
        for key, value in kwargs.items():
            if hasattr(corpus, key):
                setattr(corpus, key, value)
        
        corpus.updated_at = datetime.now()
        db.commit()
        db.refresh(corpus)
        
        return corpus
    
    def delete_corpus(self, db: Session, corpus_id: str) -> bool:
        """删除语料"""
        corpus = db.query(TermCorpus).filter(TermCorpus.id == corpus_id).first()
        if corpus:
            db.delete(corpus)
            db.commit()
            return True
        return False
    
    # ========== 商家信息 ==========
    def get_merchant(self, db: Session, merchant_id: int) -> Optional[TermMerchants]:
        """获取商家信息"""
        return db.query(TermMerchants).filter(
            TermMerchants.id == merchant_id,
            TermMerchants.status == 1,
            TermMerchants.deleted_at.is_(None)
        ).first()
    
    def get_merchant_by_username(self, db: Session, username: str) -> Optional[TermMerchants]:
        """根据用户名获取商家"""
        return db.query(TermMerchants).filter(
            TermMerchants.username == username,
            TermMerchants.status == 1,
            TermMerchants.deleted_at.is_(None)
        ).first()


# 创建全局实例
merchant_db_service = MerchantDbService()
