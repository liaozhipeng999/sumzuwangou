"""
图像生成 API 服务
独立运行，不需要集成到项目中

启动方式:
    python image_api_server.py

访问文档:
    http://localhost:8088/docs
"""

import os
import sys
from typing import Optional
from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import uvicorn

# 添加当前目录到路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from comfyui_api import ComfyUIApi


# ==================== 请求模型 ====================

class ImageGenerateRequest(BaseModel):
    """图像生成请求"""
    prompt: str = Field(..., description="正提示词（想要生成的内容）", example="精美电商产品展示图，高端护肤品套装，白色背景")
    negative_prompt: str = Field(default="", description="负提示词（不想要的内容）", example="模糊，低质量，水印")
    width: int = Field(default=1024, ge=64, le=4096, description="图片宽度（64-4096）")
    height: int = Field(default=1024, ge=64, le=4096, description="图片高度（64-4096）")
    output_dir: Optional[str] = Field(default=None, description="输出目录（不存在会自动创建）", example="F:\\temp\\image\\banner")
    
    class Config:
        json_schema_extra = {
            "example": {
                "prompt": "精美电商产品展示图，高端护肤品套装，白色背景，专业摄影风格",
                "negative_prompt": "模糊，低质量，水印，文字",
                "width": 1024,
                "height": 1024,
                "output_dir": "F:\\temp\\image\\banner"
            }
        }


class ImageGenerateResponse(BaseModel):
    """图像生成响应"""
    success: bool = Field(..., description="是否成功")
    message: str = Field(..., description="消息")
    image_path: Optional[str] = Field(default=None, description="图片绝对路径")
    image_url: Optional[str] = Field(default=None, description="图片访问URL")
    file_size: Optional[float] = Field(default=None, description="文件大小（KB）")


class HealthResponse(BaseModel):
    """健康检查响应"""
    status: str
    comfyui_connected: bool
    default_output_dir: str


# ==================== 全局配置 ====================

# 默认工作流文件路径
DEFAULT_WORKFLOW_PATH = os.path.join(os.path.dirname(__file__), "z-image_turbo.json")

# 默认输出目录
DEFAULT_OUTPUT_DIR = r"F:\temp\image"

# ComfyUI 服务器地址
COMFYUI_BASE_URL = "http://127.0.0.1:8188"

# API 服务端口
API_PORT = 8088


# ==================== 初始化 ====================

# 创建 ComfyUI 客户端
comfyui_client = ComfyUIApi(base_url=COMFYUI_BASE_URL, output_dir=DEFAULT_OUTPUT_DIR)

# 创建 FastAPI 应用
app = FastAPI(
    title="图像生成 API",
    description="基于 ComfyUI 的 AI 图像生成服务，支持动态尺寸、自定义输出路径、正负提示词",
    version="2.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# 配置 CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ==================== API 路由 ====================

@app.get("/", response_model=HealthResponse, tags=["系统"])
async def root():
    """根路径 - 健康检查"""
    is_connected = comfyui_client.is_server_running()
    return HealthResponse(
        status="healthy" if is_connected else "degraded",
        comfyui_connected=is_connected,
        default_output_dir=DEFAULT_OUTPUT_DIR
    )


@app.get("/health", response_model=HealthResponse, tags=["系统"])
async def health_check():
    """健康检查接口"""
    is_connected = comfyui_client.is_server_running()
    return HealthResponse(
        status="healthy" if is_connected else "degraded",
        comfyui_connected=is_connected,
        default_output_dir=DEFAULT_OUTPUT_DIR
    )


@app.post("/api/generate", response_model=ImageGenerateResponse, tags=["图像生成"])
async def generate_image(request: ImageGenerateRequest):
    """
    生成图像
    
    - **prompt**: 正提示词，描述你想要生成的图像内容
    - **negative_prompt**: 负提示词，描述你不想要的内容（可选）
    - **width**: 图片宽度，默认 1024，范围 64-4096
    - **height**: 图片高度，默认 1024，范围 64-4096
    - **output_dir**: 输出目录，不存在会自动创建（可选）
    
    返回:
    - **image_path**: 图片的绝对路径
    - **image_url**: 图片的访问 URL（可通过本服务访问）
    - **file_size**: 文件大小（KB）
    """
    # 检查 ComfyUI 服务
    if not comfyui_client.is_server_running():
        raise HTTPException(status_code=503, detail="ComfyUI 服务不可用，请确保 ComfyUI 服务器正在运行")
    
    # 检查工作流文件
    if not os.path.exists(DEFAULT_WORKFLOW_PATH):
        raise HTTPException(status_code=500, detail=f"工作流文件不存在: {DEFAULT_WORKFLOW_PATH}")
    
    try:
        # 生成图片
        image_path = comfyui_client.generate_image(
            workflow_path=DEFAULT_WORKFLOW_PATH,
            prompt_text=request.prompt,
            negative_prompt=request.negative_prompt,
            width=request.width,
            height=request.height,
            output_dir=request.output_dir
        )
        
        if image_path and os.path.exists(image_path):
            # 获取文件大小
            file_size = os.path.getsize(image_path) / 1024  # KB
            
            # 生成访问 URL
            # 提取相对于输出目录的路径
            output_base = request.output_dir if request.output_dir else DEFAULT_OUTPUT_DIR
            relative_path = os.path.relpath(image_path, output_base)
            image_url = f"/images/{relative_path.replace(os.sep, '/')}"
            
            return ImageGenerateResponse(
                success=True,
                message="图像生成成功",
                image_path=image_path,
                image_url=image_url,
                file_size=round(file_size, 2)
            )
        else:
            return ImageGenerateResponse(
                success=False,
                message="图像生成失败，请检查 ComfyUI 服务日志",
                image_path=None,
                image_url=None,
                file_size=None
            )
            
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"图像生成异常: {str(e)}")


@app.get("/images/{path:path}", tags=["图像访问"])
async def get_image(path: str):
    """
    访问生成的图片
    
    使用 /api/generate 返回的 image_url 访问图片
    """
    # 构建完整路径
    full_path = os.path.join(DEFAULT_OUTPUT_DIR, path)
    
    if not os.path.exists(full_path):
        raise HTTPException(status_code=404, detail="图片不存在")
    
    return FileResponse(
        full_path,
        media_type="image/png",
        filename=os.path.basename(full_path)
    )


@app.get("/api/queue", tags=["系统"])
async def get_queue_status():
    """获取 ComfyUI 队列状态"""
    if not comfyui_client.is_server_running():
        raise HTTPException(status_code=503, detail="ComfyUI 服务不可用")
    
    status = comfyui_client.get_queue_status()
    return {
        "queue_running": status.get("queue_running", []),
        "queue_pending": status.get("queue_pending", [])
    }


# ==================== 启动服务 ====================

def print_banner():
    """打印启动横幅"""
    print("\n" + "=" * 60)
    print("       图像生成 API 服务 v2.0")
    print("=" * 60)
    print(f"  API 文档:     http://localhost:{API_PORT}/docs")
    print(f"  ComfyUI:      {COMFYUI_BASE_URL}")
    print(f"  默认输出目录: {DEFAULT_OUTPUT_DIR}")
    print(f"  工作流文件:   {DEFAULT_WORKFLOW_PATH}")
    print("=" * 60)
    
    # 检查 ComfyUI 连接
    if comfyui_client.is_server_running():
        print("  [✓] ComfyUI 服务已连接")
    else:
        print("  [✗] ComfyUI 服务未连接，请先启动 ComfyUI")
    
    print("=" * 60 + "\n")


if __name__ == "__main__":
    print_banner()
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=API_PORT,
        log_level="info"
    )
