import httpx
import json
from typing import AsyncGenerator, Optional, List, Dict
from app.config import settings


class LLMService:
    """LLM 调用服务 - 支持 Ollama 和 OpenAI 兼容接口"""
    
    def __init__(self):
        self.api_base = settings.LLM_API_BASE.rstrip('/')
        self.api_key = settings.LLM_API_KEY
        self.model_name = settings.LLM_MODEL_NAME
        
        # 检测是否使用 Ollama (通过端口判断)
        self.is_ollama = '11434' in self.api_base or 'ollama' in self.api_key.lower()
        
        # 创建 HTTP 客户端
        headers = {"Content-Type": "application/json"}
        if self.api_key and not self.is_ollama:
            headers["Authorization"] = f"Bearer {self.api_key}"
        
        self.client = httpx.AsyncClient(
            timeout=120.0,
            headers=headers
        )
    
    def generate_response(self, messages: List[Dict[str, str]], 
                          stream: bool = False):
        """
        生成响应
        
        Args:
            messages: 消息列表,格式为 [{"role": "user", "content": "..."}]
            stream: 是否使用流式输出
            
        Returns:
            非流式: 协程(需await获取完整响应字符串)
            流式: 异步生成器,逐字输出
        """
        if self.is_ollama:
            # 使用 Ollama API
            url = f"{self.api_base}/chat"
            
            payload = {
                "model": self.model_name,
                "messages": messages,
                "stream": stream,
                "options": {
                    "temperature": 0.7,
                    "num_ctx": 4096
                }
            }
            
            try:
                if stream:
                    return self._ollama_stream_response(url, payload)
                else:
                    return self._ollama_non_stream_response(url, payload)
            except Exception as e:
                print(f"Ollama 调用失败: {e}")
                raise
        else:
            # 使用 OpenAI 兼容 API
            url = f"{self.api_base}/chat/completions"
            
            payload = {
                "model": self.model_name,
                "messages": messages,
                "stream": stream,
                "temperature": 0.7,
                "max_tokens": 2048
            }
            
            try:
                if stream:
                    return self._openai_stream_response(url, payload)
                else:
                    return self._openai_non_stream_response(url, payload)
            except Exception as e:
                print(f"LLM 调用失败: {e}")
                raise
    
    async def _ollama_generate(self, messages: List[Dict[str, str]], stream: bool = False):
        """使用 Ollama API 生成响应"""
        url = f"{self.api_base}/chat"
        
        # 构建 Ollama 格式的请求
        payload = {
            "model": self.model_name,
            "messages": messages,
            "stream": stream,
            "options": {
                "temperature": 0.7,
                "num_ctx": 4096
            }
        }
        
        try:
            if stream:
                return self._ollama_stream_response(url, payload)
            else:
                return await self._ollama_non_stream_response(url, payload)
        except Exception as e:
            print(f"Ollama 调用失败: {e}")
            raise
    
    async def _ollama_non_stream_response(self, url: str, payload: dict) -> str:
        """Ollama 非流式响应"""
        response = await self.client.post(url, json=payload)
        response.raise_for_status()
        
        data = response.json()
        if 'message' in data and 'content' in data['message']:
            return data['message']['content']
        else:
            raise ValueError("无效的 Ollama 响应格式")
    
    async def _ollama_stream_response(self, url: str, payload: dict) -> AsyncGenerator[str, None]:
        """Ollama 流式响应"""
        async with self.client.stream('POST', url, json=payload) as response:
            response.raise_for_status()
            
            async for line in response.aiter_lines():
                if not line:
                    continue
                
                try:
                    data = json.loads(line)
                    
                    # 提取内容
                    if 'message' in data and 'content' in data['message']:
                        yield data['message']['content']
                    
                    # 检查是否结束
                    if data.get('done', False):
                        break
                except json.JSONDecodeError:
                    continue
    
    async def _openai_non_stream_response(self, url: str, payload: dict) -> str:
        """OpenAI 格式非流式响应"""
        response = await self.client.post(url, json=payload)
        response.raise_for_status()
        
        data = response.json()
        
        # 兼容 OpenAI 格式的响应
        if 'choices' in data and len(data['choices']) > 0:
            return data['choices'][0]['message']['content']
        else:
            raise ValueError("无效的 LLM 响应格式")
    
    async def _openai_stream_response(self, url: str, payload: dict) -> AsyncGenerator[str, None]:
        """OpenAI 格式流式响应"""
        async with self.client.stream('POST', url, json=payload) as response:
            response.raise_for_status()
            
            async for line in response.aiter_lines():
                if not line:
                    continue
                
                # 跳过 SSE 注释行
                if line.startswith(':'):
                    continue
                
                # 解析 SSE 数据
                if line.startswith('data: '):
                    data_str = line[6:]  # 去掉 "data: " 前缀
                    
                    # 检查是否是结束标记
                    if data_str.strip() == '[DONE]':
                        break
                    
                    try:
                        data = json.loads(data_str)
                        
                        # 提取 token
                        if 'choices' in data and len(data['choices']) > 0:
                            delta = data['choices'][0].get('delta', {})
                            content = delta.get('content', '')
                            
                            if content:
                                yield content
                    except json.JSONDecodeError:
                        continue
    
    def generate_with_context(self, context: str, question: str, 
                             history: str = "", stream: bool = False,
                             tools: Optional[List[Dict]] = None):
        """
        基于上下文和问题生成回复
        
        Args:
            context: RAG 检索的上下文
            question: 用户问题
            history: 历史对话
            stream: 是否使用流式输出
            tools: 工具定义列表 (用于 Function Calling)
            
        Returns:
            非流式: 协程(需await获取完整响应字符串)
            流式: 异步生成器,逐字输出
        """
        # 构建系统提示词
        system_prompt = """你是一个智能客服助手,请基于以下参考资料回答用户问题。

要求:
1. 如果参考资料中有相关信息,请基于资料准确回答
2. 如果资料中没有相关信息,请如实告知用户"抱歉,我暂时没有相关信息"
3. 保持回答简洁、友好、专业
4. 不要编造信息"""
        
        # 构建用户消息
        user_message = ""
        
        if context:
            user_message += f"【参考资料】\n{context}\n\n"
        
        if history:
            user_message += f"【历史对话】\n{history}\n\n"
        
        user_message += f"【用户问题】\n{question}\n\n请用简洁、友好的语气回答:"
        
        # 构建消息列表
        messages = [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_message}
        ]
        
        # 如果有工具定义,添加到请求中
        if tools:
            pass
        
        # 调用 LLM
        return self.generate_response(messages, stream=stream)
    
    async def close(self):
        """关闭 HTTP 客户端"""
        await self.client.aclose()


# 创建全局实例
llm_service = LLMService()