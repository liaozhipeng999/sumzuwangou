from typing import List, Optional, Dict
from app.models.schemas import MessageHistory
from app.config import settings
import json
import uuid
from datetime import datetime


class ChatService:
    """对话管理服务"""
    
    def __init__(self):
        # 使用内存存储对话历史 (生产环境建议使用 Redis)
        self.conversations: Dict[str, List[MessageHistory]] = {}
        self.max_history_rounds = settings.MAX_HISTORY_ROUNDS
        self.max_context_length = settings.MAX_CONTEXT_LENGTH
    
    def get_or_create_conversation_id(self, conversation_id: Optional[str] = None) -> str:
        """
        获取或创建会话ID
        
        Args:
            conversation_id: 现有的会话ID
            
        Returns:
            会话ID
        """
        if not conversation_id:
            conversation_id = str(uuid.uuid4())
        
        if conversation_id not in self.conversations:
            self.conversations[conversation_id] = []
        
        return conversation_id
    
    def get_history(self, conversation_id: str, max_rounds: Optional[int] = None) -> List[MessageHistory]:
        """
        获取最近 N 轮对话历史
        
        Args:
            conversation_id: 会话ID
            max_rounds: 最大回合数,默认使用配置值
            
        Returns:
            消息历史列表
        """
        if max_rounds is None:
            max_rounds = self.max_history_rounds
        
        history = self.conversations.get(conversation_id, [])
        
        # 返回最近的 N 轮对话 (每轮包含 user 和 assistant 两条消息)
        max_messages = max_rounds * 2
        return history[-max_messages:] if len(history) > max_messages else history
    
    def save_message(self, conversation_id: str, role: str, content: str) -> None:
        """
        保存单条消息
        
        Args:
            conversation_id: 会话ID
            role: 角色 (user 或 assistant)
            content: 消息内容
        """
        if conversation_id not in self.conversations:
            self.conversations[conversation_id] = []
        
        message = MessageHistory(
            role=role,
            content=content,
            timestamp=datetime.now()
        )
        
        self.conversations[conversation_id].append(message)
    
    def format_history(self, history: List[MessageHistory]) -> str:
        """
        格式化历史对话为字符串
        
        Args:
            history: 消息历史列表
            
        Returns:
            格式化后的字符串
        """
        if not history:
            return ""
        
        formatted_parts = []
        for msg in history:
            role_label = "用户" if msg.role == "user" else "助手"
            formatted_parts.append(f"{role_label}: {msg.content}")
        
        return "\n".join(formatted_parts)
    
    def trim_context(self, context: str, history: str, question: str) -> tuple[str, str]:
        """
        裁剪上下文和历史对话,避免超出 Token 限制
        
        Args:
            context: RAG 检索的上下文
            history: 历史对话
            question: 当前问题
            
        Returns:
            (裁剪后的上下文, 裁剪后的历史对话)
        """
        # 简单估算: 1个中文字符 ≈ 1.5 tokens, 1个英文单词 ≈ 1.3 tokens
        # 这里使用字符数作为近似值
        max_total_length = self.max_context_length
        
        # 优先保留问题和最近的对话
        question_length = len(question)
        remaining_length = max_total_length - question_length - 500  # 预留系统提示词空间
        
        if remaining_length <= 0:
            return "", ""
        
        # 先裁剪历史对话
        if len(history) > remaining_length // 2:
            # 从开头裁剪,保留最近的对话
            lines = history.split('\n')
            trimmed_lines = []
            current_length = 0
            
            # 从后往前添加,直到达到长度限制
            for line in reversed(lines):
                line_length = len(line) + 1  # +1 for newline
                if current_length + line_length <= remaining_length // 2:
                    trimmed_lines.insert(0, line)
                    current_length += line_length
                else:
                    break
            
            history = '\n'.join(trimmed_lines)
        
        # 再裁剪上下文
        remaining_for_context = remaining_length - len(history)
        if len(context) > remaining_for_context and remaining_for_context > 0:
            # 截断上下文
            context = context[:remaining_for_context] + "..."
        
        return context, history
    
    def clear_conversation(self, conversation_id: str) -> bool:
        """
        清空指定会话的历史
        
        Args:
            conversation_id: 会话ID
            
        Returns:
            是否成功
        """
        if conversation_id in self.conversations:
            del self.conversations[conversation_id]
            return True
        return False
    
    def get_conversation_count(self) -> int:
        """获取活跃会话数量"""
        return len(self.conversations)


# 创建全局实例
chat_service = ChatService()
