from typing import List, Dict, Any, Optional
import httpx
from app.config import settings


class ToolService:
    """工具调用服务 (Function Calling)"""
    
    def __init__(self):
        self.java_api_base = settings.JAVA_API_BASE.rstrip('/')
        self.tools = {}
        self.tools_schema = []
        
        # 注册默认工具
        self._register_default_tools()
    
    def _register_default_tools(self):
        """注册默认工具"""
        
        # 工具1: 查询订单物流
        self.register_tool(
            name="query_order_logistics",
            description="查询订单的物流信息",
            parameters={
                "type": "object",
                "properties": {
                    "order_id": {
                        "type": "string",
                        "description": "订单ID"
                    }
                },
                "required": ["order_id"]
            },
            func=self._query_order_logistics
        )
        
        # 工具2: 查询订单状态
        self.register_tool(
            name="query_order_status",
            description="查询订单的当前状态",
            parameters={
                "type": "object",
                "properties": {
                    "order_id": {
                        "type": "string",
                        "description": "订单ID"
                    }
                },
                "required": ["order_id"]
            },
            func=self._query_order_status
        )
    
    def register_tool(self, name: str, description: str, parameters: Dict, func: callable):
        """
        注册工具
        
        Args:
            name: 工具名称
            description: 工具描述
            parameters: 参数schema (JSON Schema格式)
            func: 工具执行函数
        """
        self.tools[name] = {
            "description": description,
            "parameters": parameters,
            "func": func
        }
        
        # 构建 OpenAI 格式的 tool schema
        tool_schema = {
            "type": "function",
            "function": {
                "name": name,
                "description": description,
                "parameters": parameters
            }
        }
        self.tools_schema.append(tool_schema)
    
    async def execute_tool(self, tool_name: str, arguments: Dict[str, Any]) -> str:
        """
        执行指定工具
        
        Args:
            tool_name: 工具名称
            arguments: 调用参数
            
        Returns:
            执行结果字符串
        """
        if tool_name not in self.tools:
            return f"错误: 未找到工具 '{tool_name}'"
        
        try:
            tool = self.tools[tool_name]
            result = await tool["func"](**arguments)
            return str(result)
        except Exception as e:
            return f"工具执行失败: {str(e)}"
    
    def get_tools_schema(self) -> List[Dict]:
        """获取所有工具的 schema (用于 Function Calling)"""
        return self.tools_schema
    
    async def _query_order_logistics(self, order_id: str) -> str:
        """
        查询订单物流信息 (调用 Java API)
        
        Args:
            order_id: 订单ID
            
        Returns:
            物流信息
        """
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(
                    f"{self.java_api_base}/logistics/{order_id}",
                    timeout=10.0
                )
                response.raise_for_status()
                data = response.json()
                
                # 假设 Java API 返回格式: {"code": 200, "data": {...}}
                if data.get("code") == 200:
                    logistics_info = data.get("data", {})
                    return f"订单 {order_id} 的物流信息:\n" + \
                           f"当前状态: {logistics_info.get('status', '未知')}\n" + \
                           f"当前位置: {logistics_info.get('location', '未知')}\n" + \
                           f"预计送达: {logistics_info.get('estimated_delivery', '未知')}"
                else:
                    return f"查询失败: {data.get('message', '未知错误')}"
        except httpx.HTTPError as e:
            return f"HTTP 请求失败: {str(e)}"
        except Exception as e:
            return f"查询物流信息失败: {str(e)}"
    
    async def _query_order_status(self, order_id: str) -> str:
        """
        查询订单状态 (调用 Java API)
        
        Args:
            order_id: 订单ID
            
        Returns:
            订单状态信息
        """
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(
                    f"{self.java_api_base}/orders/{order_id}/status",
                    timeout=10.0
                )
                response.raise_for_status()
                data = response.json()
                
                if data.get("code") == 200:
                    order_info = data.get("data", {})
                    return f"订单 {order_id} 的状态:\n" + \
                           f"状态: {order_info.get('status', '未知')}\n" + \
                           f"下单时间: {order_info.get('created_at', '未知')}\n" + \
                           f"总金额: {order_info.get('total_amount', '未知')}元"
                else:
                    return f"查询失败: {data.get('message', '未知错误')}"
        except httpx.HTTPError as e:
            return f"HTTP 请求失败: {str(e)}"
        except Exception as e:
            return f"查询订单状态失败: {str(e)}"


# 创建全局实例
tool_service = ToolService()
