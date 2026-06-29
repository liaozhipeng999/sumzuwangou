import json
import time
import urllib.request
import urllib.error
import os
import shutil
from typing import Dict, Any, Optional


class ComfyUIApi:
    """
    ComfyUI API 客户端，用于调用生图功能
    """
    
    def __init__(self, base_url: str = "http://127.0.0.1:8188", output_dir: str = r"F:\temp\image"):
        """
        初始化 API 客户端
        
        :param base_url: ComfyUI 服务器地址
        :param output_dir: 图片输出目录
        """
        self.base_url = base_url
        self.output_dir = output_dir
        self.comfyui_output = r"F:\ComfuUI\ComfyUI-aki-v3\ComfyUI\output"
        
        # 确保输出目录存在
        os.makedirs(self.output_dir, exist_ok=True)
    
    def is_server_running(self, timeout: int = 5) -> bool:
        """
        检查服务器是否运行
        
        :param timeout: 超时时间（秒）
        :return: 服务器是否运行
        """
        try:
            response = urllib.request.urlopen(f"{self.base_url}/api/system_stats", timeout=timeout)
            return response.status == 200
        except Exception:
            return False
    
    def wait_for_server(self, max_wait: int = 120) -> bool:
        """
        等待服务器启动
        
        :param max_wait: 最大等待时间（秒）
        :return: 服务器是否启动成功
        """
        print("正在等待 ComfyUI 服务器启动...")
        start_time = time.time()
        while time.time() - start_time < max_wait:
            if self.is_server_running():
                print("服务器已启动！")
                return True
            print(f"等待中... ({int(time.time() - start_time)}s)")
            time.sleep(3)
        print("服务器启动超时！")
        return False
    
    def load_workflow(self, workflow_path: str) -> Dict[str, Any]:
        """
        加载工作流文件
        
        :param workflow_path: 工作流 JSON 文件路径
        :return: 工作流数据
        """
        with open(workflow_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    
    def update_prompt(self, workflow: Dict[str, Any], prompt_text: str) -> Dict[str, Any]:
        """
        更新工作流中的提示词
        
        :param workflow: 工作流数据
        :param prompt_text: 新的提示词
        :return: 更新后的工作流
        """
        for node_id, node_data in workflow.items():
            if node_data.get('class_type') == 'CLIPTextEncode':
                if 'text' in node_data.get('inputs', {}):
                    node_data['inputs']['text'] = prompt_text
                    print(f"已更新提示词到节点 {node_id}")
        return workflow
    
    def submit_prompt(self, workflow: Dict[str, Any], client_id: str = "comfyui_api_client") -> Optional[str]:
        """
        提交生图任务
        
        :param workflow: 工作流数据
        :param client_id: 客户端标识
        :return: Prompt ID（失败返回 None）
        """
        prompt_data = {
            "prompt": workflow,
            "client_id": client_id
        }
        
        try:
            json_data = json.dumps(prompt_data).encode('utf-8')
            req = urllib.request.Request(
                f"{self.base_url}/api/prompt",
                data=json_data,
                headers={'Content-Type': 'application/json'}
            )
            response = urllib.request.urlopen(req, timeout=60)
            result = json.loads(response.read().decode('utf-8'))
            
            if 'prompt_id' in result:
                prompt_id = result['prompt_id']
                print(f"任务提交成功！Prompt ID: {prompt_id}")
                return prompt_id
            else:
                print(f"任务提交失败: {result}")
                return None
                
        except urllib.error.HTTPError as e:
            error_body = e.read().decode('utf-8') if hasattr(e, 'read') else str(e)
            print(f"HTTP错误 {e.code}: {e.reason}")
            print(f"错误详情: {error_body}")
            return None
        except Exception as e:
            print(f"请求失败: {type(e).__name__}: {e}")
            return None
    
    def get_queue_status(self) -> Dict[str, Any]:
        """
        获取队列状态
        
        :return: 队列状态信息
        """
        try:
            response = urllib.request.urlopen(f"{self.base_url}/api/queue", timeout=10)
            return json.loads(response.read().decode('utf-8'))
        except Exception as e:
            print(f"获取队列状态失败: {e}")
            return {}
    
    def is_task_running(self, prompt_id: str) -> bool:
        """
        检查任务是否正在运行
        
        :param prompt_id: 任务 ID
        :return: 是否正在运行
        """
        status = self.get_queue_status()
        running = status.get('queue_running', [])
        pending = status.get('queue_pending', [])
        
        is_running = any(item[1] == prompt_id for item in running)
        is_pending = any(item[1] == prompt_id for item in pending)
        
        return is_running or is_pending
    
    def wait_for_completion(self, prompt_id: str, max_wait: int = 300) -> bool:
        """
        等待任务完成
        
        :param prompt_id: 任务 ID
        :param max_wait: 最大等待时间（秒）
        :return: 是否成功完成
        """
        start_time = time.time()
        while time.time() - start_time < max_wait:
            if not self.is_task_running(prompt_id):
                print("任务完成！")
                return True
            elapsed = int(time.time() - start_time)
            print(f"生成中... ({elapsed}s)")
            time.sleep(2)
        print("任务超时！")
        return False
    
    def generate_image(self, workflow_path: str, prompt_text: str, 
                      client_id: str = "comfyui_api_client", 
                      max_wait: int = 300) -> Optional[str]:
        """
        完整的生图流程
        
        :param workflow_path: 工作流文件路径
        :param prompt_text: 提示词
        :param client_id: 客户端标识
        :param max_wait: 最大等待时间（秒）
        :return: 移动后的图片路径（失败返回 None）
        """
        # 加载工作流
        workflow = self.load_workflow(workflow_path)
        
        # 更新提示词
        workflow = self.update_prompt(workflow, prompt_text)
        
        # 提交任务
        prompt_id = self.submit_prompt(workflow, client_id)
        if not prompt_id:
            return None
        
        # 等待完成
        if not self.wait_for_completion(prompt_id, max_wait):
            return None
        
        # 等待一下确保文件完全写入（增加等待时间）
        print("等待文件写入...")
        time.sleep(3)
        
        # 移动图片到输出目录
        return self._move_latest_image()
    
    def _move_latest_image(self) -> Optional[str]:
        """
        移动最新生成的图片到输出目录
        
        :return: 移动后的图片路径（失败返回 None）
        """
        try:
            # 可能的输出目录列表
            possible_outputs = [
                r"F:\ComfuUI\ComfyUI-aki-v3\ComfyUI\output",
                r"F:\ComfuUI\ComfyUI-aki-v3\output",
                r"C:\Users\27772\Documents\ComfyUI\output",
                os.path.join(os.path.expanduser("~"), "Documents", "ComfyUI", "output")
            ]
            
            source_path = None
            comfyui_output = None
            
            # 遍历所有可能的输出目录
            for output_dir in possible_outputs:
                if os.path.exists(output_dir):
                    files = [f for f in os.listdir(output_dir) if f.endswith(('.png', '.jpg', '.jpeg'))]
                    if files:
                        latest_file = max(files, key=lambda x: os.path.getmtime(os.path.join(output_dir, x)))
                        source_path = os.path.join(output_dir, latest_file)
                        comfyui_output = output_dir
                        print(f"找到图片: {source_path}")
                        break
            
            if not source_path:
                print("未找到生成的图片")
                print(f"检查的目录: {possible_outputs}")
                return None
            
            # 生成目标文件名（添加时间戳避免重名）
            import datetime
            timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
            file_ext = os.path.splitext(os.path.basename(source_path))[1]
            dest_filename = f"z-image_{timestamp}{file_ext}"
            dest_path = os.path.join(self.output_dir, dest_filename)
            
            # 移动文件
            shutil.move(source_path, dest_path)
            
            print(f"图片已移动到: {dest_path}")
            return dest_path
            
        except Exception as e:
            print(f"移动图片失败: {e}")
            import traceback
            traceback.print_exc()
            return None


def main():
    """
    示例用法
    """
    import os
    
    # 创建 API 客户端
    api = ComfyUIApi("http://127.0.0.1:8188")
    
    # 检查服务器状态
    if not api.is_server_running():
        print("ComfyUI 服务器未运行，请先启动服务器！")
        return
    
    # 定义参数
    workflow_path = r"f:\temp\tow\app\comfyui\z-image_turbo.json"
    prompt_text = "一只可爱的小猫，坐在窗台上，看着窗外的风景"
    output_dir = r"F:\temp\image"
    
    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)
    
    # 生成图片
    image_path = api.generate_image(workflow_path, prompt_text)
    
    if image_path:
        print(f"图片生成成功！")
        print(f"最终图片位置: {image_path}")
        print(f"文件大小: {os.path.getsize(image_path)/1024:.2f} KB")
    else:
        print("图片生成失败")


if __name__ == "__main__":
    main()