from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
import uvicorn

from app.api.routes import router as api_router
from app.api.template_routes import router as template_router
from app.api.merchant_routes import router as merchant_router
from app.api.merchant_chat_routes import router as merchant_chat_router
from app.config import settings
from app.models.mysql_models import init_tables


# 创建 FastAPI 应用实例
app = FastAPI(
    title="智能客服API",
    description="基于 FastAPI + LangChain + Chroma 的智能客服系统",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# 配置 CORS 中间件 (允许 Vue 前端跨域访问)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 生产环境应设置具体的域名
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 注册 API 路由
app.include_router(api_router, prefix="/api")
app.include_router(template_router)
app.include_router(merchant_router, prefix="/merchant")
app.include_router(merchant_chat_router)  # 商家专用客服接口，无前缀


# 全局异常处理
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request, exc):
    """请求验证错误处理"""
    return JSONResponse(
        status_code=422,
        content={
            "detail": "请求参数验证失败",
            "errors": exc.errors()
        }
    )


@app.exception_handler(Exception)
async def general_exception_handler(request, exc):
    """通用异常处理"""
    return JSONResponse(
        status_code=500,
        content={
            "detail": f"服务器内部错误: {str(exc)}"
        }
    )


# 启动事件
@app.on_event("startup")
async def startup_event():
    """应用启动时执行"""
    print("=" * 60)
    print("智能客服API 启动中...")
    print("=" * 60)
    
    # 初始化数据库表
    print("\n[数据库初始化]")
    try:
        created_tables, existing_tables = init_tables()
        print(f"  已创建表: {', '.join(created_tables) if created_tables else '无'}")
        print(f"  已存在表: {', '.join(existing_tables)}")
    except Exception as e:
        print(f"  ⚠️  数据库初始化警告: {str(e)}")
    
    print("\n[服务配置]")
    print(f"  LLM API Base: {settings.LLM_API_BASE}")
    print(f"  LLM Model: {settings.LLM_MODEL_NAME}")
    print(f"  Chroma DB Path: {settings.CHROMA_PERSIST_DIR}")
    print(f"  MySQL DB: {settings.MYSQL_DATABASE}")
    
    print("\n" + "=" * 60)
    print("智能客服API 启动成功!")
    print(f"文档地址: http://localhost:8000/docs")
    print("=" * 60)


# 关闭事件
@app.on_event("shutdown")
async def shutdown_event():
    """应用关闭时执行"""
    print("智能客服API 已关闭")


if __name__ == "__main__":
    # 启动 Uvicorn 服务器
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8000,
        reload=False,  # 关闭自动重载
        log_level="info"
    )
