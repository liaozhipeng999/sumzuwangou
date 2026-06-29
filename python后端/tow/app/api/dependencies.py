from fastapi import Depends
from app.services.rag_service import rag_service, RAGService
from app.services.chat_service import chat_service, ChatService
from app.services.llm_service import llm_service, LLMService
from app.services.tool_service import tool_service, ToolService
from app.services.speech_service import speech_service, SpeechService
from app.database.chroma_db import chroma_db, ChromaDBManager


def get_rag_service() -> RAGService:
    """获取 RAG 服务实例"""
    return rag_service


def get_chat_service() -> ChatService:
    """获取对话服务实例"""
    return chat_service


def get_llm_service() -> LLMService:
    """获取 LLM 服务实例"""
    return llm_service


def get_tool_service() -> ToolService:
    """获取工具服务实例"""
    return tool_service


def get_speech_service() -> SpeechService:
    """获取语音转文字服务实例"""
    return speech_service


def get_chroma_db() -> ChromaDBManager:
    """获取 Chroma 数据库实例"""
    return chroma_db
