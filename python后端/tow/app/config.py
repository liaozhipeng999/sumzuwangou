from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    """应用配置类"""
    
    # LLM Configuration
    LLM_API_BASE: str = "http://localhost:8000/v1"
    LLM_API_KEY: str = "sk-default"
    LLM_MODEL_NAME: str = "default-model"
    
    # Chroma Database
    CHROMA_PERSIST_DIR: str = "./data/chroma_db"
    
    # Redis (Optional)
    REDIS_URL: str = "redis://localhost:6379/0"
    
    # Context Settings
    MAX_CONTEXT_LENGTH: int = 4096
    MAX_HISTORY_ROUNDS: int = 10
    
    # Java API Base (for tool calling)
    JAVA_API_BASE: str = "http://localhost:8000/api"
    
    # File Upload Settings
    MAX_FILE_SIZE: int = 10485760  # 10MB in bytes
    ALLOWED_FILE_TYPES: str = ".txt,.md,.pdf,.doc,.docx"
    
    # MySQL Database Configuration
    MYSQL_HOST: str = "localhost"
    MYSQL_PORT: int = 3306
    MYSQL_USER: str = "root"
    MYSQL_PASSWORD: str = "password"
    MYSQL_DATABASE: str = "termshop"
    
    class Config:
        env_file = ".env"
        case_sensitive = True


# 创建全局配置实例
settings = Settings()


# MySQL 连接字符串
def get_mysql_connection_string() -> str:
    """获取 MySQL 连接字符串"""
    return f"mysql+pymysql://{settings.MYSQL_USER}:{settings.MYSQL_PASSWORD}@{settings.MYSQL_HOST}:{settings.MYSQL_PORT}/{settings.MYSQL_DATABASE}?charset=utf8mb4"
