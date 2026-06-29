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
    增强版：支持动态尺寸、自定义输出路径、正负提示词
    """
    
    def __init__(self, base_url: str = "http://127.0.0.1:8188", output_dir: str = r"F:\temp\image"):
        """
        初始化 API 客户端
        
        :param base_url: ComfyUI 服务器地址
        :param output_dir: 默认图片输出目录
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
        更新工作流中的正提示词
        
        :param workflow: 工作流数据
        :param prompt_text: 正提示词
        :return: 更新后的工作流
        """
        for node_id, node_data in workflow.items():
            if node_data.get('class_type') == 'CLIPTextEncode':
                if 'text' in node_data.get('inputs', {}):
                    # 只更新正提示词节点（没有连接到 ConditioningZeroOut 的）
                    node_data['inputs']['text'] = prompt_text
                    print(f"已更新正提示词到节点 {node_id}")
        return workflow
    
    def update_negative_prompt(self, workflow: Dict[str, Any], negative_text: str) -> Dict[str, Any]:
        """
        更新工作流中的负提示词
        
        :param workflow: 工作流数据
        :param negative_text: 负提示词（不想要的内容）
        :return: 更新后的工作流
        """
        for node_id, node_data in workflow.items():
            if node_data.get('class_type') == 'CLIPTextEncode':
                if 'text' in node_data.get('inputs', {}):
                    # 检查这个节点是否连接到 ConditioningZeroOut（即负提示词）
                    is_negative = False
                    for target_node_id, target_node_data in workflow.items():
                        if target_node_data.get('class_type') == 'ConditioningZeroOut':
                            conditioning_input = target_node_data.get('inputs', {}).get('conditioning', [])
                            if isinstance(conditioning_input, list) and len(conditioning_input) > 0:
                                if conditioning_input[0] == node_id:
                                    is_negative = True
                                    break
                    if is_negative:
                        node_data['inputs']['text'] = negative_text
                        print(f"已更新负提示词到节点 {node_id}")
        return workflow
    
    def update_image_size(self, workflow: Dict[str, Any], width: int, height: int) -> Dict[str, Any]:
        """
        更新生成图片的尺寸
        
        :param workflow: 工作流数据
        :param width: 宽度（像素）
        :param height: 高度（像素）
        :return: 更新后的工作流
        """
        for node_id, node_data in workflow.items():
            if node_data.get('class_type') == 'EmptySD3LatentImage':
                node_data['inputs']['width'] = width
                node_data['inputs']['height'] = height
                print(f"已更新图片尺寸到 {width}x{height}")
        return workflow
    
    def update_output_dir(self, workflow: Dict[str, Any], output_dir: str) -> Dict[str, Any]:
        """
        更新图片输出目录
        
        :param workflow: 工作流数据
        :param output_dir: 输出目录路径
        :return: 更新后的工作流
        """
        for node_id, node_data in workflow.items():
            if node_data.get('class_type') == 'SaveImage':
                node_data['inputs']['output_dir'] = output_dir
                print(f"已更新输出目录到 {output_dir}")
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
    
    def _ensure_output_dir(self, output_dir: str) -> str:
        """
        确保输出目录存在，如果不存在则自动创建
        
        :param output_dir: 输出目录路径
        :return: 规范化后的输出目录路径
        """
        # 规范化路径
        normalized_dir = os.path.normpath(output_dir)
        
        # 如果目录不存在，自动创建
        if not os.path.exists(normalized_dir):
            try:
                os.makedirs(normalized_dir, exist_ok=True)
                print(f"已自动创建输出目录: {normalized_dir}")
            except Exception as e:
                print(f"创建目录失败: {e}")
                # 如果创建失败，使用默认目录
                normalized_dir = self.output_dir
                os.makedirs(normalized_dir, exist_ok=True)
                print(f"使用默认输出目录: {normalized_dir}")
        
        return normalized_dir
    
    def generate_image(self, workflow_path: str, prompt_text: str, 
                      client_id: str = "comfyui_api_client", 
                      max_wait: int = 300,
                      width: int = 1024,
                      height: int = 1024,
                      output_dir: Optional[str] = None,
                      negative_prompt: str = "") -> Optional[str]:
        """
        完整的生图流程（增强版）
        
        :param workflow_path: 工作流文件路径
        :param prompt_text: 正提示词（想要的内容）
        :param client_id: 客户端标识
        :param max_wait: 最大等待时间（秒）
        :param width: 图片宽度（默认 1024）
        :param height: 图片高度（默认 1024）
        :param output_dir: 自定义输出目录（None 使用默认目录，不存在会自动创建）
        :param negative_prompt: 负提示词（不想要的内容，如模糊、低质量等）
        :return: 生成的图片路径（失败返回 None）
        """
        # 确定并创建输出目录
        final_output_dir = output_dir if output_dir else self.output_dir
        final_output_dir = self._ensure_output_dir(final_output_dir)
        
        # 加载工作流
        workflow = self.load_workflow(workflow_path)
        
        # 更新正提示词
        workflow = self.update_prompt(workflow, prompt_text)
        
        # 更新负提示词（如果提供）
        if negative_prompt:
            workflow = self.update_negative_prompt(workflow, negative_prompt)
        
        # 更新图片尺寸
        workflow = self.update_image_size(workflow, width, height)
        
        # 更新输出目录
        workflow = self.update_output_dir(workflow, final_output_dir)
        
        # 提交任务
        prompt_id = self.submit_prompt(workflow, client_id)
        if not prompt_id:
            return None
        
        # 等待完成
        if not self.wait_for_completion(prompt_id, max_wait):
            return None
        
        # 等待一下确保文件完全写入
        print("等待文件写入...")
        time.sleep(3)
        
        # 移动图片到输出目录
        return self._move_latest_image(final_output_dir)
    
    def _move_latest_image(self, output_dir: str) -> Optional[str]:
        """
        移动最新生成的图片到指定输出目录
        
        :param output_dir: 目标输出目录
        :return: 移动后的图片路径（失败返回 None）
        """
        try:
            # 可能的输出目录列表
            possible_outputs = [
                r"F:\ComfuUI\ComfyUI-aki-v3\ComfyUI\output",
                r"F:\ComfuUI\ComfyUI-aki-v3\output",
                r"C:\Users\27772\Documents\ComfyUI\output",
                os.path.join(os.path.expanduser("~"), "Documents", "ComfyUI", "output"),
                output_dir  # 也检查目标目录
            ]
            
            source_path = None
            comfyui_output = None
            
            # 遍历所有可能的输出目录
            for search_dir in possible_outputs:
                if os.path.exists(search_dir):
                    files = [f for f in os.listdir(search_dir) if f.endswith(('.png', '.jpg', '.jpeg'))]
                    if files:
                        latest_file = max(files, key=lambda x: os.path.getmtime(os.path.join(search_dir, x)))
                        source_path = os.path.join(search_dir, latest_file)
                        comfyui_output = search_dir
                        print(f"找到图片: {source_path}")
                        break
            
            if not source_path:
                print("未找到生成的图片")
                print(f"检查的目录: {possible_outputs}")
                return None
            
            # 确保源文件存在
            if not os.path.exists(source_path):
                print(f"源文件不存在: {source_path}")
                return None
            
            # 如果已经在目标目录，直接返回
            if source_path.startswith(output_dir):
                print(f"图片已在目标目录: {source_path}")
                return source_path
            
            # 生成目标文件名（添加时间戳避免重名）
            timestamp = time.strftime("%Y%m%d_%H%M%S")
            file_ext = os.path.splitext(os.path.basename(source_path))[1]
            dest_filename = f"z-image_{timestamp}{file_ext}"
            dest_path = os.path.join(output_dir, dest_filename)
            
            # 确保目标目录存在
            os.makedirs(output_dir, exist_ok=True)
            
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
    示例用法（增强版）
    """
    import os
    
    # 创建 API 客户端
    api = ComfyUIApi("http://127.0.0.1:8188")
    
    # 检查服务器状态
    if not api.is_server_running():
        print("ComfyUI 服务器未运行，请先启动服务器！")
        return
    
    # ==================== 示例 1: 移动端轮播图 (1024x1024) ====================
    print("\n" + "="*60)
    print("示例 1: 生成移动端轮播图 (1024x1024)")
    print("="*60)
    
    workflow_path = r"F:\temp\tow\app\comfyui\comtest\z-image_turbo.json"
    prompt_text = "精美电商产品展示图，高端护肤品套装，白色背景，专业摄影风格，光线明亮，产品摆放整齐"
    negative_prompt = "模糊，低质量，水印，文字，杂乱背景，不清晰"
    
    image_path = api.generate_image(
        workflow_path=workflow_path,
        prompt_text=prompt_text,
        negative_prompt=negative_prompt,
        width=1024,
        height=1024,
        output_dir=r"F:\temp\image\banner"
    )
    
    if image_path:
        print(f"轮播图生成成功！路径: {image_path}")
    else:
        print("轮播图生成失败")
    
    # ==================== 示例 2: 商品介绍图片 (800x800) ====================
    print("\n" + "="*60)
    print("示例 2: 生成商品介绍图片 (800x800)")
    print("="*60)
    
    prompt_text = "时尚运动鞋，黑色配色，放在白色背景上，360度展示，细节清晰，专业产品摄影"
    negative_prompt = "模糊，划痕，污渍，背景杂乱，光线不足"
    
    image_path = api.generate_image(
        workflow_path=workflow_path,
        prompt_text=prompt_text,
        negative_prompt=negative_prompt,
        width=800,
        height=800,
        output_dir=r"F:\temp\image\product"
    )
    
    if image_path:
        print(f"商品图生成成功！路径: {image_path}")
    else:
        print("商品图生成失败")
    
    # ==================== 示例 3: 横版宣传图 (1920x1080) ====================
    print("\n" + "="*60)
    print("示例 3: 生成横版宣传图 (1920x1080)")
    print("="*60)
    
    prompt_text = "电商促销活动横幅，红色主题，优惠券元素，喜庆氛围，高清画质"
    negative_prompt = "低分辨率，模糊，颜色暗淡，不专业"
    
    image_path = api.generate_image(
        workflow_path=workflow_path,
        prompt_text=prompt_text,
        negative_prompt=negative_prompt,
        width=1920,
        height=1080,
        output_dir=r"F:\temp\image\banner"
    )
    
    if image_path:
        print(f"横幅图生成成功！路径: {image_path}")
    else:
        print("横幅图生成失败")


if __name__ == "__main__":
    main()
