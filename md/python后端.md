
# 智能客服系统 API 接口文档

> 基于 FastAPI + LangChain + Chroma + MySQL 的智能客服系统
> 
> 服务地址：`http://localhost:8000`
> Swagger 文档：`http://localhost:8000/docs`

---

## 目录

- [1. 系统概览](#1-系统概览)
- [2. 通用客服接口](#2-通用客服接口)
- [3. 聊天接口](#3-聊天接口)
- [4. 知识库管理接口](#4-知识库管理接口)
- [5. 语音接口](#5-语音接口)
- [6. 会话管理接口](#6-会话管理接口)
- [7. 常见问题接口](#7-常见问题接口)
- [8. 商家客服接口](#8-商家客服接口)
- [9. 话术模板管理接口](#9-话术模板管理接口)
- [10. 商家语料管理接口](#10-商家语料管理接口)
- [11. 自动生成语料接口](#11-自动生成语料接口)
- [12. 附录](#12-附录)

---

## 1. 系统概览

| 项目 | 说明 |
|------|------|
| 框架 | FastAPI |
| 数据库 | MySQL + ChromaDB (向量库) |
| LLM | 支持兼容 OpenAI 格式的模型 |
| 语音 | Vosk (STT) + VoxCPM2 (TTS) |
| 通信 | HTTP + SSE (Server-Sent Events) |

**路由前缀汇总：**

| 前缀 | 文件 | 说明 |
|------|------|------|
| /api | routes.py | 通用客服接口 |
| /merchant | merchant_routes.py | 话术模板管理 |
| 无前缀 | merchant_chat_routes.py | 商家专用客服 |
| /api/templates | template_routes.py | 话术模板管理(另一个前缀) |

---

## 2. 通用客服接口

### 2.1 健康检查
- URL: `GET /api/health`
- 说明: 检查服务状态

响应:
```json
{"status": "healthy", "version": "1.0.0"}
```

### 2.2 系统统计
- URL: `GET /api/stats`
- 说明: 获取系统运行统计

响应:
```json
{"active_conversations": 15, "knowledge_documents": 120}
```

---

## 3. 聊天接口

### 3.1 普通聊天（非流式）
- URL: `POST /api/chat`
- Content-Type: application/json

请求体:
```json
{
  "user_id": "user_001",
  "message": "你们的退换货政策是什么？",
  "conversation_id": "conv_abc123",
  "merchant_id": "merchant_001"
}
```

响应:
```json
{
  "response": "我们支持7天无理由退换货...",
  "conversation_id": "conv_abc123",
  "is_streaming": false,
  "sources": ["doc_001", "doc_002"]
}
```

### 3.2 流式聊天（SSE）
- URL: `POST /api/chat/stream`
- Accept: text/event-stream
- 说明: 逐字返回 AI 回复，混合模式（先匹配话术模板，未匹配走 RAG+LLM）

请求体: 同上
可选字段: speech=true 返回语音, speaker_id=0, rate=1.0

SSE 事件类型:

| 事件 | 说明 |
|------|------|
| sources | 参考来源 |
| confidence | 匹配置信度 |
| token | 文本片段(逐字) |
| speech | 语音数据(hex编码,wav) |
| speech_error | 语音错误 |
| done | 完成标记 |
| error | 系统错误 |

SSE 示例:
```
data: {"type": "sources", "data": ["doc_001"]}
data: {"type": "token", "data": "你"}
data: {"type": "token", "data": "好"}
data: {"type": "done", "data": {"conversation_id": "conv_001"}}
```

---

## 4. 知识库管理接口

### 4.1 上传知识库文档
- URL: `POST /api/knowledge/upload`
- Content-Type: multipart/form-data

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 文件(最大10MB) |
| category | string | 否 | 分类,默认general |
| metadata | string | 否 | JSON元数据 |

支持格式: .txt .md .pdf .doc .docx

### 4.2 删除知识库文档
- URL: `DELETE /api/knowledge/{doc_id}`

### 4.3 列出知识库文档
- URL: `GET /api/knowledge/list?limit=100&offset=0`

---

## 5. 语音接口

### 5.1 语音转文字 (STT)
- URL: `POST /api/speech/to-text`
- Content-Type: multipart/form-data

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 音频(最大50MB,WAV/MP3/OGG/FLAC) |

响应:
```json
{"success": true, "text": "你好，我想咨询退款", "message": "语音识别成功"}
```

### 5.2 文字转语音一次性 (TTS)
- URL: `POST /api/speech/tts`
- 返回: audio/wav 音频流

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|------|------|------|
| text | string | 是 | - | 要转换的文本 |
| speaker_id | int | 否 | 0 | 说话人ID |
| rate | float | 否 | 1.0 | 语速(0.5-2.0) |
| pitch | float | 否 | 0.0 | 音调偏移 |
| use_clone | bool | 否 | true | 是否语音克隆 |

### 5.3 文字转语音流式 (TTS Stream)
- URL: `POST /api/speech/tts/stream`
- 返回: 分块 WAV 流,采样率16kHz,低延迟实时

### 5.4 文字转语音并保存
- URL: `POST /api/speech/tts/save`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| text | string | 是 | 文本 |
| user_id | string | 是 | 用户ID |
| conversation_id | string | 是 | 会话ID |
| use_clone | bool | 否 | 默认true |

---

## 6. 会话管理接口

### 6.1 结束会话
- URL: `POST /api/session/end`
- Content-Type: multipart/form-data

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | string | 是 | 用户ID |
| conversation_id | string | 否 | 不传则清理用户所有会话 |

### 6.2 获取会话信息
- URL: `GET /api/session/info?user_id=xxx`

---

## 7. 常见问题接口

### 7.1 常见问题列表
- URL: `GET /api/questions/common`

### 7.2 预测问题列表
- URL: `GET /api/questions/predicted`

### 7.3 预生成问题语音
- URL: `POST /api/questions/pregenerate`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | string | 是 | 用户ID |
| conversation_id | string | 是 | 会话ID |
| questions | string | 否 | JSON数组,不传用预设 |


---

## 8. 商家客服接口

### 8.1 商家健康检查
- URL: `GET /merchant/health`

响应:
```json
{"status": "healthy", "service": "merchant_chat", "template_count": 25}
```

### 8.2 商家客服对话(非流式)
- URL: `POST /MerchantChat`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| merchant_id | int | 是 | 商家ID |
| user_id | string | 是 | 用户ID |
| message | string | 是 | 用户消息 |
| conversation_id | string | 否 | 会话ID |
| user_name | string | 否 | 用户名称 |

响应:
```json
{
  "response": "亲，支持7天无理由退换货哦~",
  "conversation_id": "conv_001",
  "source": "corpus",
  "merchant_id": 1,
  "user_id": "user_001"
}
```

### 8.3 商家客服流式对话(SSE)
- URL: `POST /MerchantChat/stream`
- Accept: text/event-stream
- 参数: 同上

SSE事件:

| 事件 | 说明 |
|------|------|
| sources | 来源(corpus/llm) |
| token | 文本片段 |
| done | 完成标记 |
| error | 错误 |

### 8.4 兼容路径对话
- URL: `POST /customer-service/{merchant_id}/{user_id}`
- 说明: 兼容前端 RESTful 路径格式
- 路径参数: merchant_id(int), user_id(string)
- 查询参数: message(必填), conversation_id, user_name

### 8.5 获取会话历史
- URL: `GET /MerchantChat/history?conversation_id=xxx`

响应:
```json
{
  "conversation_id": "conv_001",
  "merchant_id": 1,
  "user_id": "user_001",
  "created_at": "2026-06-29T10:00:00",
  "messages": [
    {"role": "user", "content": "有优惠吗？"},
    {"role": "assistant", "content": "本月全场8折~"}
  ]
}
```

### 8.6 结束商家会话
- URL: `POST /MerchantChat/end`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| conversation_id | string | 是 | 会话ID |

---

## 9. 话术模板管理接口

> 同时存在两个路由: `/merchant/templates/*` 和 `/api/templates/*`

### 9.1 创建话术模板
- URL: `POST /merchant/templates` 或 `POST /api/templates`

请求体:
```json
{
  "merchant_id": "merchant_001",
  "category": "price",
  "keywords": ["优惠", "打折"],
  "patterns": ["多少钱"],
  "question_template": "{{keywords}}",
  "answer_template": "全场8折~",
  "variables": {"promotion": "全场8折"},
  "priority": 10,
  "tags": ["热门"]
}
```

### 9.2 列出话术模板
- URL: `GET /merchant/templates`

| 参数 | 类型 | 默认 | 说明 |
|------|------|------|------|
| merchant_id | string | - | 按商家过滤 |
| category | string | - | 按分类过滤 |
| enabled | bool | - | 按启用过滤 |
| page | int | 1 | 页码 |
| page_size | int | 20 | 每页数量 |

### 9.3 获取话术模板
- URL: `GET /merchant/templates/{template_id}`

### 9.4 更新话术模板
- URL: `PUT /merchant/templates/{template_id}`
- 请求体: 同创建(可选字段)

### 9.5 删除话术模板
- URL: `DELETE /merchant/templates/{template_id}`

### 9.6 测试话术匹配
- URL: `POST /merchant/templates/match`

| 参数 | 类型 | 默认 | 说明 |
|------|------|------|------|
| message | string | - | 用户消息(必填) |
| merchant_id | string | default | 商家ID |
| threshold | float | 0.6 | 置信度阈值(0-1) |

响应:
```json
{
  "matched": true,
  "confidence": 0.88,
  "answer": "亲，我们支持7天无理由退换货哦~",
  "template": {
    "id": "tpl_001",
    "category": {"value": "service", "label": "售后服务"}
  }
}
```

### 9.7 获取话术分类
- URL: `GET /merchant/templates/categories` 或 `GET /api/templates/categories/list`

响应:
```json
{
  "categories": [
    {"value": "greeting", "label": "问候语", "description": "欢迎、问候类话术"},
    {"value": "product", "label": "产品介绍", "description": "产品咨询、介绍类话术"},
    {"value": "price", "label": "价格咨询", "description": "价格、优惠类话术"},
    {"value": "promotion", "label": "促销活动", "description": "活动、促销类话术"},
    {"value": "service", "label": "售后服务", "description": "售后、服务类话术"},
    {"value": "complaint", "label": "投诉处理", "description": "投诉、抱怨类话术"},
    {"value": "shipping", "label": "物流配送", "description": "发货、物流类话术"},
    {"value": "payment", "label": "支付问题", "description": "支付、付款类话术"},
    {"value": "return", "label": "退换货", "description": "退货、换货类话术"},
    {"value": "other", "label": "其他", "description": "其他类型话术"}
  ]
}
```

### 9.8 批量导入话术模板
- URL: `POST /merchant/templates/batch`

请求体:
```json
{
  "merchant_id": "merchant_001",
  "templates": [
    {"category": "price", "keywords": ["多少钱"], "answer_template": "99元~"},
    {"category": "shipping", "keywords": ["多久到"], "answer_template": "3-5天~"}
  ]
}
```

### 9.9 话术模板统计
- URL: `GET /merchant/stats?merchant_id=xxx`

响应:
```json
{
  "total_templates": 125,
  "total_usage": 2480,
  "by_category": {
    "greeting": {"count": 15, "usage": 500},
    "price": {"count": 30, "usage": 800}
  }
}
```


---

## 10. 商家语料管理接口

> 商家专用 FAQ 语料库，存储在 MySQL term_corpus 表，优先级高于 LLM

### 10.1 创建商家语料
- URL: `POST /MerchantChat/corpus`
- Content-Type: multipart/form-data

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| merchant_id | int | 是 | 商家ID |
| question | string | 是 | 问题 |
| answer | string | 是 | 回答 |
| keywords | string | 否 | 关键词(逗号分隔) |
| priority | int | 否 | 优先级,默认0,越大越优先 |

### 10.2 获取语料列表
- URL: `GET /MerchantChat/corpus`

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|------|------|------|
| merchant_id | int | 是 | - | 商家ID |
| page | int | 否 | 1 | 页码 |
| page_size | int | 否 | 20 | 每页数量 |

### 10.3 获取单个语料
- URL: `GET /MerchantChat/corpus/{corpus_id}`

### 10.4 更新语料
- URL: `PUT /MerchantChat/corpus/{corpus_id}`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| question | string | 否 | 问题 |
| answer | string | 否 | 回答 |
| keywords | string | 否 | 关键词 |
| priority | int | 否 | 优先级 |
| enabled | int | 否 | 是否启用(0/1) |

### 10.5 删除语料
- URL: `DELETE /MerchantChat/corpus/{corpus_id}`

### 10.6 批量创建语料
- URL: `POST /MerchantChat/corpus/batch`

请求体:
```json
{
  "merchant_id": 1,
  "items": [
    {"question": "包邮吗？", "answer": "满99包邮~", "keywords": "包邮,运费", "priority": 5},
    {"question": "可以开发票吗？", "answer": "可以的~", "keywords": "发票"}
  ]
}
```

### 10.7 清空商家语料
- URL: `POST /MerchantChat/corpus/clear`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| merchant_id | int | 是 | 商家ID |

响应:
```json
{
  "success": true,
  "message": "已删除商家 1 的 45 条语料",
  "deleted_count": 45
}
```

### 10.8 语料统计
- URL: `GET /MerchantChat/corpus/stats?merchant_id=1`

响应:
```json
{
  "merchant_id": 1,
  "corpus_count": 45,
  "product_count": 25,
  "coverage": 1.8,
  "message": "每个商品平均生成 1.8 条语料"
}
```

---

## 11. 自动生成语料接口

### 11.1 根据商品自动生成语料
- URL: `POST /MerchantChat/corpus/generate`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| merchant_id | int | 是 | 商家ID |
| clear_existing | bool | 否 | 是否先清空旧语料,默认false |

自动生成的语料类型：

| 类型 | 示例问题 |
|------|----------|
| 商品咨询 | 商品怎么样？好用吗？ |
| 价格咨询 | 多少钱？价格多少？ |
| 库存咨询 | 有货吗？库存多少？ |
| 销量咨询 | 卖了多少？销量如何？ |
| 规格咨询 | 有什么规格？ |
| 热销推荐 | 有什么热销的？ |
| 新品推荐 | 有什么新品？ |
| 通用咨询 | 你们店有什么？ |

响应:
```json
{
  "success": true,
  "message": "成功为商家 1 生成 200 条语料",
  "generated_count": 200,
  "product_count": 25,
  "errors": []
}
```

---

## 12. 附录

### A. 话术分类说明

| 值 | 标签 | 说明 |
|----|------|------|
| greeting | 问候语 | 欢迎、问候类话术 |
| product | 产品介绍 | 产品咨询、介绍类话术 |
| price | 价格咨询 | 价格、优惠类话术 |
| promotion | 促销活动 | 活动、促销类话术 |
| service | 售后服务 | 售后、服务类话术 |
| complaint | 投诉处理 | 投诉、抱怨类话术 |
| shipping | 物流配送 | 发货、物流类话术 |
| payment | 支付问题 | 支付、付款类话术 |
| return | 退换货 | 退货、换货类话术 |
| other | 其他 | 其他类型话术 |

### B. 技术架构

```
┌─────────────────────────────────────────────────────────┐
│                  智能客服系统架构                        │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Vue 前端 (Vant/Pinia)                                   │
│       │                                                 │
│       ▼ HTTP / SSE                                      │
│  FastAPI 后端 (app/main.py)                              │
│   ├── /api/*       通用客服 + RAG + LLM                  │
│   ├── /merchant/*  话术模板管理                          │
│   └── 无前缀       商家专用客服 (MySQL语料库)             │
│       │                                                 │
│  ┌────┼────────────────┐                                 │
│  ▼    ▼                ▼                                │
│ MySQL ChromaDB(向量库)  LLM(OpenAI兼容)                  │
│          │                                               │
│          ▼                                               │
│    LangChain + LangGraph                                │
│    Vosk(STT) + VoxCPM2(TTS)                             │
└─────────────────────────────────────────────────────────┘
```

### C. 核心对话流程

```
用户消息
  │
  ▼
[话术模板匹配] ─匹配─▶ 直接返回预设话术(0.6阈值)
  │
  ▼ 未匹配
[商家语料检索] ─匹配─▶ 返回商家自定义FAQ
  │
  ▼ 未匹配
[RAG 检索] ──▶ ChromaDB 向量检索 Top-K=5
  │
  ▼
[LLM 生成] ──▶ Context + 历史 ──▶ SSE流式返回
  │
  ▼ 可选
[TTS 语音] ──▶ VoxCPM2 语音克隆合成
```

### D. 文件结构

```
app/
├── main.py                 # FastAPI 入口
├── config.py               # 配置(.env)
├── api/
│   ├── routes.py           # 通用客服 /api/* (健康检查/知识库/聊天/语音)
│   ├── merchant_routes.py  # 商家客服 /merchant/* (话术模板)
│   ├── merchant_chat_routes.py  # 商家专用客服(无前缀)
│   ├── template_routes.py  # 话术模板管理 /api/templates/*
│   └── dependencies.py     # 依赖注入
├── models/
│   ├── schemas.py          # Pydantic 数据模型
│   ├── mysql_models.py     # MySQL ORM 模型
│   └── dialogue_template.py # 话术模板模型
├── services/
│   ├── chat_service.py          # 聊天服务
│   ├── rag_service.py           # RAG 检索服务
│   ├── llm_service.py           # LLM 调用服务
│   ├── speech_service.py        # STT 服务
│   ├── tts_service.py           # TTS 服务
│   ├── merchant_chat_service.py # 商家客服对话
│   ├── merchant_db_service.py   # 商家数据库服务
│   ├── corpus_generator_service.py # 自动语料生成
│   ├── dialogue_template_service.py # 话术模板服务
│   └── tool_service.py          # 工具调用
├── database/
│   └── chroma_db.py        # ChromaDB 初始化
└── utils/
    └── text_splitter.py    # 文本分块工具
```

---

> 文档版本: v1.0 | 更新日期: 2026-06-29
> 项目名称: TOW 智能客服系统 (FastAPI + Vue)
