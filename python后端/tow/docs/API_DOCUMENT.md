# 智能客服系统 API 文档

> **更新时间**: 2026-06-18
> **服务端口**: 8000（固定不变）

---

## 一、服务概览

本系统提供两个独立的 AI 客服服务：

| 服务名称 | 接口前缀 | 技术原理 | 数据来源 | 适用场景 |
|----------|----------|----------|----------|----------|
| **通用客服** | `/api` | RAG + LLM | 知识库文档 | 通用问答、智能助手 |
| **商家客服** | `/merchant` | 话术模板 | 预设话术 | 电商客服、标准化服务 |

---

## 二、通用客服 API

**基础地址**: `http://localhost:8000`

### 2.1 流式对话

```
POST /api/chat/stream
```

**请求参数** (JSON):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | string | ✅ | 用户唯一标识 |
| message | string | ✅ | 用户消息 |
| conversation_id | string | ❌ | 会话ID，不传自动生成 |
| speech | boolean | ❌ | 是否返回语音，默认 false |

**请求示例**:
```json
{
  "user_id": "user_001",
  "message": "如何查询订单物流？",
  "conversation_id": null,
  "speech": false
}
```

**响应格式** (SSE 流):

```
data: {"type": "sources", "data": ["doc_id_xxx"]}
data: {"type": "token", "data": "您"}
data: {"type": "token", "data": "好"}
...
data: {"type": "done", "data": {"conversation_id": "xxx"}}
```

**事件类型**:

| 事件 | 说明 | data |
|------|------|------|
| sources | 知识库来源 | 文档ID数组 |
| token | 文本片段 | 单个字符 |
| done | 完成 | `{conversation_id}` |

---

### 2.2 普通对话

```
POST /api/chat
```

**请求参数**: 同流式对话

**响应**:
```json
{
  "response": "AI回复内容",
  "conversation_id": "xxx",
  "is_streaming": false,
  "sources": ["doc_id"]
}
```

---

### 2.3 健康检查

```
GET /api/health
```

**响应**:
```json
{
  "status": "healthy",
  "version": "1.0.0"
}
```

---

## 三、商家客服 API

**基础地址**: `http://localhost:8000`

### 3.1 流式对话

```
POST /merchant/chat/stream
```

**请求参数** (Query):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | string | ✅ | 用户唯一标识 |
| message | string | ✅ | 用户消息 |
| merchant_id | string | ❌ | 商家ID，默认 "default" |
| conversation_id | string | ❌ | 会话ID |

**请求示例**:
```
POST /merchant/chat/stream?user_id=user_001&message=你好&merchant_id=shop_001
```

**响应格式** (SSE 流):

```
data: {"type": "sources", "data": ["template:greeting"]}
data: {"type": "confidence", "data": 0.75}
data: {"type": "token", "data": "您"}
data: {"type": "token", "data": "好"}
...
data: {"type": "done", "data": {"conversation_id": "xxx", "matched": true}}
```

**事件类型**:

| 事件 | 说明 | data |
|------|------|------|
| sources | 来源 | `template:分类名` 或 `no_match` |
| confidence | 置信度 | 0.0 - 1.0 |
| token | 文本片段 | 单个字符 |
| done | 完成 | `{conversation_id, matched}` |

---

### 3.2 普通对话

```
POST /merchant/chat
```

**请求参数**: 同流式对话

**响应**:
```json
{
  "response": "AI回复内容",
  "conversation_id": "xxx",
  "sources": ["template:greeting"],
  "confidence": 0.75,
  "matched": true
}
```

---

### 3.3 健康检查

```
GET /merchant/health
```

**响应**:
```json
{
  "status": "healthy",
  "service": "merchant_chat",
  "template_count": 9
}
```

---

## 四、话术模板管理 API

### 4.1 创建话术

```
POST /merchant/templates
```

**请求体**:
```json
{
  "merchant_id": "shop_001",
  "category": "greeting",
  "keywords": ["你好", "您好"],
  "patterns": ["^(你好|您好)[？?]?"],
  "question_template": "你好",
  "answer_template": "您好！欢迎光临本店！",
  "priority": 10,
  "tags": ["问候", "欢迎"]
}
```

**分类选项**:

| 值 | 说明 |
|-----|------|
| greeting | 问候语 |
| product | 产品介绍 |
| price | 价格咨询 |
| promotion | 促销活动 |
| shipping | 物流配送 |
| payment | 支付问题 |
| return | 退换货 |
| complaint | 投诉处理 |
| service | 售后服务 |
| other | 其他 |

---

### 4.2 列出话术

```
GET /merchant/templates
```

**查询参数**:

| 参数 | 说明 |
|------|------|
| merchant_id | 商家ID过滤 |
| category | 分类过滤 |
| enabled | 启用状态过滤 |
| page | 页码，默认1 |
| page_size | 每页数量，默认20 |

---

### 4.3 获取单个话术

```
GET /merchant/templates/{template_id}
```

---

### 4.4 更新话术

```
PUT /merchant/templates/{template_id}
```

---

### 4.5 删除话术

```
DELETE /merchant/templates/{template_id}
```

---

### 4.6 测试匹配

```
POST /merchant/templates/match
```

**参数**:

| 参数 | 说明 |
|------|------|
| message | 测试消息 |
| merchant_id | 商家ID |
| threshold | 匹配阈值，默认0.6 |

---

### 4.7 获取分类列表

```
GET /merchant/templates/categories
```

---

### 4.8 统计信息

```
GET /merchant/stats?merchant_id=shop_001
```

---

## 五、前端调用示例

### 5.1 通用客服调用

```javascript
// 通用客服 - 流式对话
async function chatGeneral(userId, message) {
  const response = await fetch('http://localhost:8000/api/chat/stream', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ user_id: userId, message: message })
  });

  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let fullResponse = '';

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    const chunk = decoder.decode(value);
    const lines = chunk.split('\n');

    for (const line of lines) {
      if (line.startsWith('data: ')) {
        const event = JSON.parse(line.slice(6));

        if (event.type === 'token') {
          fullResponse += event.data;
          console.log('收到字符:', event.data);
        }

        if (event.type === 'done') {
          console.log('对话完成:', event.data.conversation_id);
        }
      }
    }
  }

  return fullResponse;
}

// 调用
chatGeneral('user_001', '你好').then(response => {
  console.log('完整回复:', response);
});
```

---

### 5.2 商家客服调用

```javascript
// 商家客服 - 流式对话
async function chatMerchant(userId, message, merchantId = 'default') {
  const response = await fetch(
    `http://localhost:8000/merchant/chat/stream?user_id=${userId}&message=${encodeURIComponent(message)}&merchant_id=${merchantId}`
  );

  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let fullResponse = '';
  let confidence = 0;

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    const chunk = decoder.decode(value);
    const lines = chunk.split('\n');

    for (const line of lines) {
      if (line.startsWith('data: ')) {
        const event = JSON.parse(line.slice(6));

        if (event.type === 'token') {
          fullResponse += event.data;
        }

        if (event.type === 'confidence') {
          confidence = event.data;
          console.log('匹配置信度:', confidence);
        }

        if (event.type === 'sources') {
          console.log('回复来源:', event.data);
        }

        if (event.type === 'done') {
          console.log('是否匹配话术:', event.data.matched);
        }
      }
    }
  }

  return fullResponse;
}

// 调用
chatMerchant('user_001', '你好', 'shop_001').then(response => {
  console.log('完整回复:', response);
});
```

---

### 5.3 Vue 3 完整示例

```vue
<template>
  <div class="chat-container">
    <div class="messages">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        {{ msg.content }}
      </div>
    </div>

    <div class="input-area">
      <select v-model="chatType">
        <option value="general">通用客服</option>
        <option value="merchant">商家客服</option>
      </select>
      <input
        v-model="inputMessage"
        @keyup.enter="sendMessage"
        placeholder="输入消息..."
      />
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const messages = ref([]);
const inputMessage = ref('');
const chatType = ref('merchant'); // 默认商家客服
const merchantId = ref('default');
const conversationId = ref(null);

async function sendMessage() {
  if (!inputMessage.value.trim()) return;

  const userMessage = inputMessage.value;
  messages.value.push({ role: 'user', content: userMessage });
  inputMessage.value = '';

  try {
    const url = chatType.value === 'general'
      ? 'http://localhost:8000/api/chat/stream'
      : `http://localhost:8000/merchant/chat/stream?user_id=user_001&merchant_id=${merchantId.value}`;

    const body = chatType.value === 'general'
      ? JSON.stringify({ user_id: 'user_001', message: userMessage, conversation_id: conversationId.value })
      : undefined;

    const response = await fetch(url, {
      method: 'POST',
      headers: body ? { 'Content-Type': 'application/json' } : {},
      body: body
    });

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let aiResponse = '';

    messages.value.push({ role: 'assistant', content: '' });

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value);
      const lines = chunk.split('\n');

      for (const line of lines) {
        if (line.startsWith('data: ')) {
          const event = JSON.parse(line.slice(6));

          if (event.type === 'token') {
            aiResponse += event.data;
            messages.value[messages.value.length - 1].content = aiResponse;
          }

          if (event.type === 'done') {
            conversationId.value = event.data.conversation_id;
          }
        }
      }
    }
  } catch (error) {
    console.error('请求失败:', error);
    messages.value.push({ role: 'assistant', content: '抱歉，发生了错误。' });
  }
}
</script>
```

---

## 六、端口说明

| 服务 | 端口 | 状态 |
|------|------|------|
| 智能客服 API | **8000** | ✅ 运行中 |
| 图像生成 API | 8088 | ✅ 运行中 |

**端口固定不变**，所有服务都在端口 8000 上运行，通过不同的路径前缀区分。

---

## 七、Swagger 文档

- **通用客服**: http://localhost:8000/docs
- **商家客服**: http://localhost:8000/docs#/商家客服
- **话术管理**: http://localhost:8000/docs#/话术模板管理

---

## 八、注意事项

1. **跨域**: 已配置允许所有来源，前端可直接调用
2. **会话保持**: 传入相同 `conversation_id` 保持上下文
3. **流式响应**: 使用 SSE 协议，逐字返回
4. **错误处理**: 建议添加 try-catch 和超时处理
