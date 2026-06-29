# 智能客服核心功能实现

基于 FastAPI + LangChain + Chroma 的智能客服系统,支持知识库问答(RAG)、多轮对话记忆、业务数据查询(工具调用)和流式输出。

## 项目结构

```
f:\temp\tow/
├── app/
│   ├── main.py                 # FastAPI 应用入口
│   ├── config.py               # 配置文件
│   ├── models/
│   │   └── schemas.py          # Pydantic 数据模型
│   ├── services/
│   │   ├── rag_service.py      # RAG 知识库服务
│   │   ├── chat_service.py     # 对话管理服务
│   │   ├── tool_service.py     # 工具调用服务
│   │   └── llm_service.py      # LLM 调用服务
│   ├── database/
│   │   └── chroma_db.py        # Chroma 向量数据库封装
│   ├── utils/
│   │   └── text_splitter.py    # 文本切片工具
│   └── api/
│       ├── routes.py           # API 路由
│       └── dependencies.py     # 依赖注入
├── data/
│   └── knowledge_base/         # 知识库文档存储目录
├── requirements.txt
└── .env                        # 环境变量配置
```

## 快速开始

### 1. 安装依赖

```bash
pip install -r requirements.txt
```

如果需要处理 PDF 文件,还需要安装:
```bash
pip install PyPDF2
```

### 2. 配置环境变量

编辑 `.env` 文件,配置你的 LLM API 端点:

```env
LLM_API_BASE=http://your-llm-endpoint/v1
LLM_API_KEY=your-api-key
LLM_MODEL_NAME=your-model-name
```

**注意**: 本项目使用自定义 LLM 端点,不是 Ollama。你可以使用:
- OpenAI API: `https://api.openai.com/v1`
- Azure OpenAI: `https://your-resource.openai.azure.com/openai/deployments/your-deployment`
- 其他兼容 OpenAI 格式的 API

### 3. 启动服务

```bash
cd f:\temp\tow
python -m app.main
```

或者:

```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

服务将在 `http://localhost:8000` 启动。

### 4. 访问 API 文档

打开浏览器访问:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## API 接口说明

### 1. 健康检查

```
GET /api/health
```

### 2. 知识库管理

#### 上传知识库文档

```
POST /api/knowledge/upload
Content-Type: multipart/form-data

参数:
- file: 文件 (.txt, .md, .pdf 等)
- category: 文档分类 (可选,默认 "general")
- metadata: 元数据 JSON 字符串 (可选)
```

示例 (使用 curl):
```bash
curl -X POST "http://localhost:8000/api/knowledge/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@product_manual.txt" \
  -F "category=product" \
  -F 'metadata={"version":"1.0"}'
```

#### 删除知识库文档

```
DELETE /api/knowledge/{doc_id}
```

#### 列出知识库文档

```
GET /api/knowledge/list?limit=100&offset=0
```

### 3. 聊天接口

#### 普通聊天 (非流式)

```
POST /api/chat
Content-Type: application/json

{
  "user_id": "user123",
  "message": "如何重置密码?",
  "conversation_id": null,
  "stream": false
}
```

响应:
```json
{
  "response": "您可以通过以下步骤重置密码...",
  "conversation_id": "abc-123-def",
  "is_streaming": false,
  "sources": ["doc-id-1", "doc-id-2"]
}
```

#### 流式聊天 (SSE)

```
POST /api/chat/stream
Content-Type: application/json

{
  "user_id": "user123",
  "message": "如何重置密码?",
  "conversation_id": null
}
```

响应为 Server-Sent Events 流:
```
data: {"type": "sources", "data": ["doc-id-1"]}

data: {"type": "token", "data": "您"}

data: {"type": "token", "data": "可以"}

data: {"type": "done", "data": {"conversation_id": "abc-123-def"}}
```

前端使用 EventSource 接收:
```javascript
const eventSource = new EventSource('/api/chat/stream');

eventSource.onmessage = (event) => {
  const data = JSON.parse(event.data);
  
  if (data.type === 'token') {
    // 逐字渲染
    appendToChat(data.data);
  } else if (data.type === 'done') {
    // 对话完成
    console.log('Conversation ID:', data.data.conversation_id);
    eventSource.close();
  }
};
```

### 4. 系统统计

```
GET /api/stats
```

返回:
```json
{
  "active_conversations": 5,
  "knowledge_documents": 120
}
```

## 核心功能说明

### 1. 知识库问答 (RAG)

**工作流程**:
1. 用户上传文档 → 文本切片 → 向量化 → 存入 Chroma
2. 用户提问 → 检索最相关的 top_k 条文档
3. 将检索结果 + 用户问题 → 构建 Prompt → 调用 LLM
4. LLM 基于参考资料生成回答

**优势**:
- 避免 AI 编造信息
- 回答基于真实的产品文档、帮助手册
- 可追溯回答来源

### 2. 多轮对话记忆

**实现方式**:
- 使用内存存储对话历史 (生产环境建议用 Redis)
- 自动保留最近 10 轮对话
- 超出 Token 限制时自动裁剪上下文

**会话管理**:
- 首次对话不传 `conversation_id`,系统自动生成
- 后续对话携带相同的 `conversation_id` 保持上下文

### 3. 业务数据查询 (工具调用)

**已实现的工具**:
- `query_order_logistics`: 查询订单物流信息
- `query_order_status`: 查询订单状态

**扩展方法**:
在 `tool_service.py` 中注册新工具:
```python
self.register_tool(
    name="your_tool_name",
    description="工具描述",
    parameters={...},  # JSON Schema
    func=your_function
)
```

工具会自动调用 Java 提供的 REST API 获取业务数据。

### 4. 流式输出 (打字机效果)

**技术实现**:
- FastAPI: `StreamingResponse` + SSE 协议
- 前端: `EventSource` 接收流式数据
- 逐字渲染,提升用户体验

**优势**:
- 降低首字延迟 (TTFT)
- 实时反馈,用户无需等待完整回复
- 适合长文本场景

## 配置说明

### 环境变量 (.env)

| 变量 | 说明 | 默认值 |
|------|------|--------|
| LLM_API_BASE | LLM API 基础 URL | http://localhost:8000/v1 |
| LLM_API_KEY | API 密钥 | sk-default |
| LLM_MODEL_NAME | 模型名称 | default-model |
| CHROMA_PERSIST_DIR | Chroma 数据存储路径 | ./data/chroma_db |
| REDIS_URL | Redis 连接字符串 | redis://localhost:6379/0 |
| MAX_CONTEXT_LENGTH | 最大上下文长度 (字符数) | 4096 |
| MAX_HISTORY_ROUNDS | 最大历史对话轮数 | 10 |
| JAVA_API_BASE | Java 业务 API 地址 | http://localhost:8080/api |
| MAX_FILE_SIZE | 最大文件大小 (字节) | 10485760 (10MB) |
| ALLOWED_FILE_TYPES | 允许的文件类型 | .txt,.md,.pdf,.doc,.docx |

## 性能优化建议

1. **向量数据库**: 
   - 批量插入文档
   - 定期清理无用文档
   - 使用 SSD 存储 Chroma 数据

2. **对话历史**:
   - 生产环境使用 Redis 替代内存存储
   - 设置合理的 TTL (如 24 小时)
   - 限制单个用户的会话数量

3. **LLM 调用**:
   - 启用响应缓存 (相同问题直接返回缓存)
   - 使用流式输出降低感知延迟
   - 合理设置 temperature 和 max_tokens

4. **文本切片**:
   - 根据文档类型调整 chunk_size
   - 适当增加 chunk_overlap 保持语义连贯
   - 对长文档进行预处理和索引

## 安全考虑

1. **文件上传**:
   - 验证文件类型白名单
   - 限制文件大小
   - 扫描恶意内容

2. **API 认证**:
   - 添加 API Key 或 JWT 认证
   - 限制请求频率
   - 记录访问日志

3. **输入过滤**:
   - 防止 Prompt 注入攻击
   - 过滤敏感词
   - 限制输入长度

4. **数据隐私**:
   - 对话数据加密存储
   - 定期清理过期数据
   - 符合 GDPR 等法规要求

## 故障排查

### 1. Chroma 初始化失败

**问题**: HuggingFace embeddings 下载失败

**解决**:
- 检查网络连接
- 手动下载模型: `huggingface-cli download sentence-transformers/all-MiniLM-L6-v2`
- 或使用备用方案 (代码中已包含简单哈希备用)

### 2. LLM 调用超时

**问题**: 请求 LLM API 超时

**解决**:
- 检查 `LLM_API_BASE` 配置是否正确
- 增加 timeout 参数 (在 `llm_service.py` 中)
- 检查 API Key 是否有效

### 3. 向量检索结果为空

**问题**: RAG 检索不到相关文档

**解决**:
- 确认知识库已上传文档
- 检查 embedding 模型是否正常工作
- 调整 `top_k` 参数
- 优化文档切片策略

## 部署建议

### Docker 部署

创建 `Dockerfile`:
```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

构建并运行:
```bash
docker build -t smart-customer-service .
docker run -p 8000:8000 -v ./data:/app/data smart-customer-service
```

### 生产环境配置

1. **反向代理**: 使用 Nginx 作为反向代理
2. **HTTPS**: 配置 SSL 证书
3. **进程管理**: 使用 Gunicorn + Uvicorn workers
4. **监控**: 集成 Prometheus + Grafana
5. **日志**: 集中日志管理 (ELK Stack)

## 后续扩展方向

1. 支持更多文档格式 (Word, Excel, PPT)
2. 添加对话质量评估机制
3. 实现人工客服转接功能
4. 添加敏感词过滤和内容审核
5. 支持多语言客服
6. 添加对话分析和统计报表
7. 集成语音识别和合成
8. 实现意图识别和槽位填充

## 许可证

MIT License

## 联系方式

如有问题,请提交 Issue 或联系开发者。
