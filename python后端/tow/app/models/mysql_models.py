"""
MySQL 数据库模型定义

注意：
- term_merchants 表已存在，使用反射机制
- 其他表（term_conversation, term_message, term_corpus）如果不存在则创建
"""

from sqlalchemy import (
    create_engine, Column, String, Text, Integer, DateTime, Boolean,
    ForeignKey, inspect, BigInteger, DECIMAL
)
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, relationship
from datetime import datetime
import uuid
from app.config import get_mysql_connection_string, settings

# 创建数据库引擎
engine = create_engine(get_mysql_connection_string(), echo=False)

# 创建会话工厂
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 基础模型类
Base = declarative_base()


def get_db():
    """获取数据库会话"""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# ========== 已存在的表（反射） ==========
class TermMerchants(Base):
    """商家信息表（已存在）"""
    __tablename__ = 'term_merchants'
    
    # 使用反射获取已存在的表结构
    __table_args__ = {'extend_existing': True}
    
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    merchant_name = Column(String(128), nullable=False)
    merchant_logo = Column(String(512))
    merchant_brief = Column(String(512))
    username = Column(String(32), nullable=False)
    password = Column(String(255), nullable=False)
    contact_name = Column(String(32), nullable=False)
    contact_phone = Column(String(16), nullable=False)
    email = Column(String(128))
    main_category_id = Column(BigInteger)
    merchant_level = Column(Integer, default=1)
    status = Column(Integer, default=1)
    sort = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.now)
    updated_at = Column(DateTime, onupdate=datetime.now)
    deleted_at = Column(DateTime)


# ========== 需要创建的表（如果不存在） ==========
class TermConversation(Base):
    """会话记录表"""
    __tablename__ = 'term_conversation'
    
    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    merchant_id = Column(BigInteger, nullable=False, index=True)
    user_id = Column(String(100), nullable=False)
    user_name = Column(String(100))
    status = Column(Integer, default=1)  # 0-结束，1-活跃
    created_at = Column(DateTime, default=datetime.now)
    updated_at = Column(DateTime, onupdate=datetime.now)


class TermMessage(Base):
    """消息记录表"""
    __tablename__ = 'term_message'
    
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    conversation_id = Column(String(36), ForeignKey('term_conversation.id'), nullable=False, index=True)
    role = Column(String(20), nullable=False)  # user/assistant
    content = Column(Text, nullable=False)
    source = Column(String(50))  # corpus/llm/template
    timestamp = Column(DateTime, default=datetime.now)


class TermCorpus(Base):
    """商家语料表"""
    __tablename__ = 'term_corpus'
    
    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    merchant_id = Column(BigInteger, nullable=False, index=True)
    question = Column(Text, nullable=False)
    answer = Column(Text, nullable=False)
    keywords = Column(String(500))  # 逗号分隔
    priority = Column(Integer, default=0)
    enabled = Column(Integer, default=1)
    created_at = Column(DateTime, default=datetime.now)
    updated_at = Column(DateTime, onupdate=datetime.now)


def init_tables():
    """初始化数据库表（只创建不存在的表）"""
    inspector = inspect(engine)
    
    # 检查并创建表
    tables_to_create = [
        ('term_conversation', TermConversation),
        ('term_message', TermMessage),
        ('term_corpus', TermCorpus),
    ]
    
    created_tables = []
    existing_tables = []
    
    for table_name, model in tables_to_create:
        if table_name not in inspector.get_table_names():
            model.__table__.create(bind=engine)
            created_tables.append(table_name)
        else:
            existing_tables.append(table_name)
    
    # 检查商家表是否存在
    if 'term_merchants' not in inspector.get_table_names():
        TermMerchants.__table__.create(bind=engine)
        created_tables.append('term_merchants')
        print(f"⚠️  警告：term_merchants 表不存在，已自动创建")
    else:
        existing_tables.append('term_merchants')
    
    print(f"\n数据库表初始化完成:")
    print(f"已创建: {', '.join(created_tables) if created_tables else '无'}")
    print(f"已存在: {', '.join(existing_tables)}")
    
    return created_tables, existing_tables
