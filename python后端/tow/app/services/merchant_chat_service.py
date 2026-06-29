"""
商家客服服务

核心业务逻辑：
1. 获取/创建会话
2. 保存消息
3. 检索商家语料（RAG）
4. 调用LLM生成回复
"""

from sqlalchemy.orm import Session
from app.models.mysql_models import get_db, TermMessage
from app.services.merchant_db_service import merchant_db_service
from app.services.llm_service import llm_service
from app.config import settings
from typing import List, Optional, Generator
import json


class MerchantChatService:
    """商家客服服务"""
    
    def __init__(self):
        self.max_history_rounds = settings.MAX_HISTORY_ROUNDS
        self.max_context_length = settings.MAX_CONTEXT_LENGTH
    
    async def chat(
        self,
        merchant_id: int,
        user_id: str,
        message: str,
        conversation_id: Optional[str] = None,
        user_name: Optional[str] = None
    ) -> dict:
        """
        非流式聊天接口
        
        Args:
            merchant_id: 商家ID
            user_id: 用户ID
            message: 用户消息
            conversation_id: 会话ID（可选）
            user_name: 用户名称（可选）
        
        Returns:
            回复结果
        """
        # 获取数据库会话
        db_gen = get_db()
        db = next(db_gen)
        
        try:
            # 获取或创建会话
            if conversation_id:
                conversation = merchant_db_service.get_conversation(db, conversation_id)
                if not conversation:
                    conversation = merchant_db_service.get_or_create_conversation(
                        db, merchant_id, user_id, user_name
                    )
            else:
                conversation = merchant_db_service.get_or_create_conversation(
                    db, merchant_id, user_id, user_name
                )
            
            # 保存用户消息
            merchant_db_service.save_message(db, conversation.id, "user", message)
            
            # 获取对话历史
            messages = merchant_db_service.get_conversation_messages(
                db, conversation.id, self.max_history_rounds * 2
            )
            history_str = merchant_db_service.format_history(messages[:-1])  # 排除当前消息
            
            # 检索商家语料
            matched_corpus = merchant_db_service.search_corpus(db, merchant_id, message, top_k=3)
            
            response_text = ""
            source = "llm"
            
            if matched_corpus:
                # 优先使用匹配的语料
                response_text = matched_corpus[0].answer
                source = "corpus"
            else:
                # 使用LLM生成回复
                context = self._build_context_from_corpus(
                    merchant_db_service.get_merchant_corpus(db, merchant_id)
                )
                context, history_str = self._trim_context(context, history_str, message)
                
                response_text = await llm_service.generate_with_context(
                    context=context,
                    question=message,
                    history=history_str,
                    stream=False
                )
            
            # 保存助手回复
            merchant_db_service.save_message(db, conversation.id, "assistant", response_text, source)
            
            return {
                "response": response_text,
                "conversation_id": conversation.id,
                "source": source,
                "merchant_id": merchant_id,
                "user_id": user_id
            }
        
        finally:
            db.close()
    
    async def chat_stream(
        self,
        merchant_id: int,
        user_id: str,
        message: str,
        conversation_id: Optional[str] = None,
        user_name: Optional[str] = None
    ) -> Generator[dict, None, None]:
        """
        流式聊天接口
        
        Args:
            merchant_id: 商家ID
            user_id: 用户ID
            message: 用户消息
            conversation_id: 会话ID（可选）
            user_name: 用户名称（可选）
        
        Returns:
            生成器，逐字返回回复
        """
        # 获取数据库会话
        db_gen = get_db()
        db = next(db_gen)
        
        try:
            # 获取或创建会话
            if conversation_id:
                conversation = merchant_db_service.get_conversation(db, conversation_id)
                if not conversation:
                    conversation = merchant_db_service.get_or_create_conversation(
                        db, merchant_id, user_id, user_name
                    )
            else:
                conversation = merchant_db_service.get_or_create_conversation(
                    db, merchant_id, user_id, user_name
                )
            
            # 保存用户消息
            merchant_db_service.save_message(db, conversation.id, "user", message)
            
            # 获取对话历史
            messages = merchant_db_service.get_conversation_messages(
                db, conversation.id, self.max_history_rounds * 2
            )
            history_str = merchant_db_service.format_history(messages[:-1])  # 排除当前消息
            
            # 检索商家语料
            matched_corpus = merchant_db_service.search_corpus(db, merchant_id, message, top_k=3)
            
            full_response = ""
            source = "llm"
            
            if matched_corpus:
                # 匹配到语料，直接返回预设回复
                source = "corpus"
                full_response = matched_corpus[0].answer
                
                # 发送来源信息
                yield {"type": "sources", "data": ["corpus"]}
                
                # 逐字发送
                for char in full_response:
                    yield {"type": "token", "data": char}
            else:
                # 未匹配到语料，调用LLM
                context = self._build_context_from_corpus(
                    merchant_db_service.get_merchant_corpus(db, merchant_id)
                )
                context, history_str = self._trim_context(context, history_str, message)
                
                # 发送来源信息
                yield {"type": "sources", "data": ["llm"]}
                
                # 流式调用LLM
                response_generator = llm_service.generate_with_context(
                    context=context,
                    question=message,
                    history=history_str,
                    stream=True
                )
                
                async for token in response_generator:
                    full_response += token
                    yield {"type": "token", "data": token}
            
            # 保存助手回复
            merchant_db_service.save_message(db, conversation.id, "assistant", full_response, source)
            
            # 发送完成标记
            yield {"type": "done", "data": {
                "conversation_id": conversation.id,
                "source": source,
                "merchant_id": merchant_id
            }}
        
        except Exception as e:
            yield {"type": "error", "data": str(e)}
        finally:
            db.close()
    
    def _build_context_from_corpus(self, corpus_list: List) -> str:
        """从语料列表构建上下文"""
        if not corpus_list:
            return ""
        
        context_parts = []
        for corpus in corpus_list[:10]:  # 最多取10条语料
            context_parts.append(f"问题: {corpus.question}\n回答: {corpus.answer}")
        
        return "\n\n".join(context_parts)
    
    def _trim_context(self, context: str, history: str, question: str) -> tuple:
        """裁剪上下文和历史对话，避免超出Token限制"""
        max_total_length = self.max_context_length
        
        question_length = len(question)
        remaining_length = max_total_length - question_length - 500
        
        if remaining_length <= 0:
            return "", ""
        
        # 先裁剪历史对话
        if len(history) > remaining_length // 2:
            lines = history.split('\n')
            trimmed_lines = []
            current_length = 0
            
            for line in reversed(lines):
                line_length = len(line) + 1
                if current_length + line_length <= remaining_length // 2:
                    trimmed_lines.insert(0, line)
                    current_length += line_length
                else:
                    break
            
            history = '\n'.join(trimmed_lines)
        
        # 再裁剪上下文
        remaining_for_context = remaining_length - len(history)
        if len(context) > remaining_for_context and remaining_for_context > 0:
            context = context[:remaining_for_context] + "..."
        
        return context, history
    
    def get_conversation_history(self, conversation_id: str) -> List[dict]:
        """获取会话历史记录"""
        db_gen = get_db()
        db = next(db_gen)
        
        try:
            messages = merchant_db_service.get_conversation_messages(db, conversation_id)
            return [
                {
                    "role": msg.role,
                    "content": msg.content,
                    "source": msg.source,
                    "timestamp": msg.timestamp.isoformat()
                }
                for msg in messages
            ]
        finally:
            db.close()


# 创建全局实例
merchant_chat_service = MerchantChatService()
