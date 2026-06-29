"""启动服务脚本"""
import uvicorn
from app.main import app

if __name__ == "__main__":
    print("启动智能客服API服务...")
    uvicorn.run(app, host="0.0.0.0", port=8000, log_level="info")