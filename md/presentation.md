# 电商平台全栈项目 · 技术架构演讲

> 2026 春季期末项目答辩

---

# 01 · 项目一句话简介

> **AI智能驱动的电商全栈平台**
>
> 涵盖「用户端 H5 + 商家管理后台 + AI 智能客服」三大核心场景，
> 采用 Vue3 + Spring Boot + FastAPI + ChromaDB 现代化技术栈。

---

# 02 · 我们做了什么

| 模块 | 面向用户 | 核心功能 |
|:--|:--|:--|
| 🛒 **用户端 H5** | 亿万消费者 | 首页 / 商品 / 购物车 / 订单 / 活动 / 钱包 / 设置 |
| 📊 **商家后台** | 中小商家 | 商品管理 / 订单处理 / 数据分析 / 客服工作台 |
| 🤖 **AI 客服** | 双方共赢 | RAG 智能问答 / 语音交互 / 话术模板 / 知识库管理 |

---

# 03 · 全景架构

```
          ┌─────────────────────────────────────────┐
          │            客户端层（2 套前端）          │
          │  📱 用户端 H5 (Vue3+Vant4+TS)           │
          │     Vite5173 · 代理8080/8000             │
          │  💻 商家后台 (Vue3+ElementPlus+ECharts)  │
          │     Vite5174 · 代理9090                  │
          └────────────────┬────────────────────────┘
                           │ HTTP / WebSocket / SSE
          ┌────────────────┴────────────────────────┐
          │          服务层（3 套后端）               │
          │  ☕ Java × 2 —— 用户端API(8080)          │
          │     SpringBoot2.7 · MyBatisPlus          │
          │     mysql-connector-java8.0.33           │
          │     BCrypt · Hutool · LettuceRedis       │
          │  ☕ Java × 2 —— 商家端API(9090)          │
          │     SpringBoot2.7 · MyBatisPlus          │
          │     mysql-connector-j8.3.0               │
          │     JJWT0.11.5 · WebSocket · LettuceRedis│
          │  🐍 Python × 1 —— AI 客服 API(8000)     │
          │     FastAPI · Uvicorn · LangChain        │
          │     ChromaDB · Ollama(qwen2.5:7b 11434)  │
          │     Vosk 中文ASR + TTS + ComfyUI         │
          └────────────────┬────────────────────────┘
                           │ SQL / Redis / Vector
          ┌────────────────┴────────────────────────┐
          │     数据层（MySQL termshop + Redis 6379）│
          │     ChromaDB 向量知识库 (sqlite3)        │
          │     本地文件：图片 / Vosk模型 / 语料MD   │
          └─────────────────────────────────────────┘
```

---

# 04 · 技术选型亮点

| 层 | 选型 | 为什么选它？ |
|:--|:--|:--|
| 前端框架 | **Vue 3 + Composition API** | 学习曲线平缓、生态成熟、企业级首选 |
| 状态管理 | **Pinia 3** | Vue3 官方推荐、TypeScript 友好 |
| UI 组件 | **Vant 4 · Element Plus 2** | 移动端 / PC端 各司其职、开箱即用 |
| 图表 | **ECharts 6** | 大屏级可视化、官方维护 |
| HTTP | **Axios 统一封装** | 拦截器 + Token + 全局错误处理 |
| 实时通信 | **STOMP over WebSocket** | 商家客服 IM 即时收发 |
| 后端框架 | **Spring Boot 2.7 LTS** | Java 社区事实标准 |
| ORM | **MyBatis Plus 3.5** | 通用 CRUD + 分页 + 乐观锁 |
| 微服务 | **FastAPI + Uvicorn** | 异步高性能、自带 Swagger |
| AI | **LangChain + Ollama/OpenAI** | RAG 快速落地、本地/云端兼容 |
| 向量库 | **ChromaDB** | 零配置嵌入、持久化、轻量级 |
| 缓存 | **Redis** | 验证码 / 推荐缓存 / 会话 |
| 语音 | **Vosk 中文模型** | 离线 ASR、无需付费 |

---

# 05 · 五大模块技术拆解

## 📱 模块一 · 用户端 H5（dui）

**技术栈：** Vue 3.5 + TypeScript 6 + Vite 8 + Vant 4.9 + Pinia 3 + Axios（dev 端口 5173）

```
用户前端（35+ 页面）
├── 首页         Peng首页 / 商城首页 / 搜索
├── 商品         列表 / 详情 / 分类 / 热销榜
├── 交易         购物车 / 下单 / 支付 / 订单
├── 活动         限时秒杀 · 彩票转盘 · 火车票 · 百亿补贴
├── 消息         系统通知 · 商家消息 · 客服对话
└── 设置         30+ 项（深色/长辈/未成年模式等）
```

**亮点：**
- 路由守卫统一鉴权 + 默认测试账号（user0001/123456）
- Pinia 持久化购物车（localStorage）
- Axios 统一拦截器 + Vant Toast 全局报错
- 状态机驱动订单流转
- Vite 代理多后端：8080（Java 用户端）+ 8000（AI）

---

## 💻 模块二 · 商家后台（houtai）

**技术栈：** Vue 3.5 + Element Plus 2.14 + ECharts 6.1 + STOMP.js 7.3 + Pinia 3（dev 端口 5174）

```
商家后台（PC 管理台）
├── Dashboard     实时 GMV 图表（ECharts）
├── 商品管理       列表 / 新增 / 标签关联
├── 订单管理       列表 / 退换货处理 / 物流日志
├── 客服工作台     会话列表 / 实时聊天 / 快捷回复
├── 用户管理       用户列表 / 地址
└── 系统配置       权限 / 配置项
```

**亮点：**
- WebSocket STOMP 协议实现 IM 级实时客服
- 敏感词过滤器拦截不良内容
- Vite 代理指向 9090（商家后端）
- JJWT + Redis 白名单
- Element Plus 完整管理后台组件

---

## ☕ 模块三 · 用户后端（javaqian）

**技术栈：** Java 11 · Spring Boot 2.7.18 · MyBatis Plus 3.5.3.1 · Redis · MySQL 8.0.33（端口 8080）

```
Controller 覆盖 15+ 场景：
用户 · 商品 · 分类 · 搜索 · 购物车 · 地址 · 订单 · 支付
优惠券 · 收藏 · 历史 · 消息 · 推荐 · 品牌 · 验证码
```

**亮点：**
- MyBatis Plus 通用 Service + Page 分页 + 逻辑删除
- Hutool 图形验证码（CaptchaUtil）
- Spring Security BCrypt 密码加密
- Lettuce Redis 连接池（max-active 8）
- 静态资源映射到本地 F:/temp/image/ + F:/temp/catalog/

---

## ☕ 模块四 · 商家后端（shajiahoutai）

**技术栈：** Java 8 · Spring Boot 2.7.18 · MyBatis Plus · JJWT 0.11.5 · WebSocket（端口 9090）

```
核心 Controller：
Merchant 注册登录(JWT) · Product · ProductTag
MerchantOrder · Dashboard · Customer(STOMP) · RedisTest
```

**亮点：**
- spring-boot-starter-websocket + STOMP Broker `/ws/chat` 支持一对一 IM
- 自定义 SensitiveWordFilter 安全过滤
- JJWT 三方库（jjwt-api / impl / jackson）签发 Token
- Lettuce Redis 缓存热键 / 推荐
- mysql-connector-j 8.3.0 + HikariCP 连接池

---

## 🤖 模块五 · AI 智能客服（tow）

**技术栈：** Python 3.11 · FastAPI · LangChain · ChromaDB · Ollama (qwen2.5:7b) · Vosk（端口 8000）

```
用户问题
   │
   ▼  text_splitter（Markdown 分块）
   ▼  ChromaDB 向量化 & 持久化（默认本地向量）
   ▼  ChromaDB.search_similar(top-k 检索）
   ▼  参考资料 + 历史对话 + 用户问题 拼接 Prompt
   ▼  Ollama (11434) 或 OpenAI（SSE 流式输出）
   │
   ▼  FastAPI StreamingResponse(text/event-stream)
   │
   ▼  前端逐字打字机效果
```

**亮点：**
- ✅ RAG 增强生成（检索 5 条最相关 FAQ/商品）
- ✅ 本地 LLM Ollama（qwen2.5:7b, 端口 11434）+ 云端 OpenAI 双模式
- ✅ SSE 流式逐字输出体验
- ✅ Vosk 中文语音输入（离线 ASR）+ TTS
- ✅ 话术模板管理（可商用）
- ✅ Swagger UI 自动 API 文档
- ✅ 预置 5 份 FAQ 语料（积分/商品/物流/售后/详情）
- ✅ MySQL 库 termshop 回写用户数据

---

# 06 · 实时通信架构

### 场景 A · 用户端 → AI 客服（SSE 单向流）
```
用户 ──POST /api/chat──> FastAPI ──RAG检索──> ChromaDB
                           │
                           ├── SSE 流式逐字返回给前端
                           └── 前端打字机效果
```

### 场景 B · 商家 ↔ 用户 客服（WebSocket 双向 IM）
```
用户后端 ──HTTP客服消息──> 商家后端 ──STOMP──> 商家后台
                            │
                            ├── /user/queue/messages (一对一)
                            └── /topic/conversations (会话广播)
```

---

# 07 · 认证策略

| 端 | 方式 |
|:--|:--|
| 用户端 | 路由守卫 + localStorage JWT（默认测试账号 user0001 / 123456） |
| 商家端 | BCrypt 加密 + 自实现 JwtUtil + Redis 白名单 |
| AI 后端 | 内部信任 + 可选 API Key（OpenAI/Ollama） |

---

# 08 · 数据层全景

| 存储 | 角色 | 关键数据 |
|:--|:--|:--|
| **MySQL 8.0** | 业务主库 | 用户/商品/订单/购物车/收藏/优惠券/支付 |
| **Redis** | 缓存加速 | 验证码 / 推荐热榜 / JWT 白名单 |
| **ChromaDB** | 向量知识库 | FAQ 语料 / 商品知识 / 向量化持久化 |
| **本地文件** | 资源 & 模型 | 图片 / Vosk ASR 模型 / Markdown 语料 |

---

# 09 · 关键依赖一览

| 类别 | 依赖 |
|:--|:--|
| **用户前端** | Vue 3.5 · Vant 4.9 · Pinia 3 · Vite 8 · TypeScript 6 · Axios 1.17 |
| **商家前端** | Vue 3.5 · Element Plus 2.14 · ECharts 6 · STOMP.js 7 · Pinia 3 |
| **用户后端** | Java 11 · Spring Boot 2.7.18 · MyBatis Plus 3.5.3.1 · mysql-connector-java 8.0.33 |
| **用户后端** | Redis (Lettuce) · Hutool 5.8 · BCrypt · Jackson JSR310 |
| **商家后端** | Java 8 · Spring Boot 2.7.18 · MyBatis Plus · mysql-connector-j 8.3.0 |
| **商家后端** | JJWT 0.11.5 · Redis (Lettuce) · WebSocket STOMP |
| **Python 后端** | Python 3.11 · FastAPI 0.104 · Uvicorn 0.24 · LangChain 0.1 · ChromaDB 0.4+ |
| **AI 模型** | Ollama（qwen2.5:7b 本地） / OpenAI 兼容接口 / Vosk 中文 ASR |
| **数据库** | MySQL 8.0（termshop） · Redis 6379 · ChromaDB 文件持久化 |

---

# 10 · 项目亮点 & 创新点

🏆 **完整全栈（无模板）** — 用户端 + 商家端 + AI 客服 三端齐全

🤖 **AI 原生增强** — RAG 智能客服嵌入购物全流程，不是"伪 AI"

💬 **双实时通道** — SSE 流式 AI 回复 + WebSocket IM 级人工客服

🔄 **端到端订单流** — 下单 → 支付 → 发货 → 退换货 全链路闭环

📱 **移动端优先** — Vant 移动端 UI / 长辈模式 / 深色模式 / 无障碍设置

🧠 **本地化知识库** — ChromaDB 向量检索，FAQ/商品/售后 知识库可扩展

🛡️ **多安全层** — BCrypt + JWT + 图形验证码 + 敏感词过滤

🧩 **可插拔 AI 后端** — FastAPI 独立部署，Ollama/OpenAI 一键切换

---

# 11 · 端口与启动（已验证）

| 服务 | 端口 | 启动命令 |
|:--|:--|:--|
| 用户前端 dev | **5173** | `pnpm run dev` （代理 → 8080 + 8000） |
| 商家前端 dev | **5174** | `pnpm run dev` （代理 → 9090） |
| 用户端 API | **8080** | `mvn spring-boot:run` （库 termshop） |
| 商家端 API + WS | **9090** | `mvn spring-boot:run` （WebSocket /ws/chat） |
| AI 客服 API | **8000** | `python start_server.py` （Ollama 11434） |
| MySQL | 3306 | root / 123456 / **termshop** |
| Redis | 6379 | Lettuce 连接池 |
| Ollama | 11434 | qwen2.5:7b（本地 LLM） |

---

# 12 · 后续演进方向（可选）

- [ ] 引入 **Kafka/RabbitMQ** 做订单异步解耦
- [ ] Spring Boot 3.x + Java 17 升级（Jakarta 生态）
- [ ] 商家端 Nacos 注册中心 + Sentinel 限流
- [ ] AI 后端 + ComfyUI 接入商品图生成（已占位）
- [ ] 推荐算法升级为协同过滤 + Redis 协同过滤热缓存
- [ ] Kubernetes 容器化部署

---

# Q & A

> 欢迎提问
>
> *项目根目录：`f:\temp\前后端`*
> 详细技术文档见 `docs/architecture.md`

