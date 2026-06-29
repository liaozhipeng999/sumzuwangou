# 电商平台全栈架构文档

## 一、项目概览

本项目是一个**电商平台**，包含完整的用户端、商家端、智能客服系统三大模块，采用前后端分离的微服务架构。

### 核心定位

| 模块 | 角色 | 技术栈 |
|------|------|--------|
| 用户前端（dui） | 移动端H5商城用户端 | Vue 3 + Vant + Pinia + TypeScript |
| 用户后端（javaqian） | 用户端业务API | Spring Boot 2.7 + MyBatis Plus + MySQL + Redis |
| 商家前端（houtai） | PC端商家管理后台 | Vue 3 + Element Plus + Pinia + ECharts + WebSocket |
| 商家后端（shajiahoutai） | 商家端业务API + WebSocket | Spring Boot 2.7 + MyBatis Plus + JJWT + MySQL 8.3 + Redis + WebSocket |
| AI后端（tow） | 智能客服API | FastAPI + LangChain + ChromaDB + Ollama/OpenAI + Vosk |

---

## 二、架构总览

```
┌─────────────────────────────────────────────────────────────────────┐
│                        客户端层 (Client Layer)                        │
├─────────────────────────────┬───────────────────────────────────────┤
│   用户前端（dui）           │           商家前端（houtai）            │
│   Vue3 + Vant4 + TS        │     Vue3 + Element Plus + ECharts      │
│   Vite 8 / Pinia 3         │     Pinia 3 / WebSocket (STOMP)       │
│   移动端H5                  │     PC端管理后台                      │
└──────────────┬──────────────┴──────────────────────────┬─────────────┘
               │ Axios + RESTful                         │ Axios + STOMP over WebSocket
               ▼                                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        服务网关层                                     │
│         CORS跨域支持 / JWT认证 / Redis验证码缓存                      │
├──────────────────────┬──────────────────────┬────────────────────────┤
│  用户后端 (Java)     │  商家后端 (Java)    │    AI后端 (Python)      │
│  Spring Boot 2.7.18 │  Spring Boot 2.7.18 │   FastAPI 0.104+       │
│  Java 11            │  Java 8            │   Python 3.11         │
│  MyBatis Plus 3.5    │  MyBatis Plus 3.5  │   Uvicorn + LangChain   │
│  MySQL 8.0.33       │  mysql-connector-j │   ChromaDB (向量库)    │
│  Spring Security BCrypt │ 8.3.0          │   Ollama (qwen2.5:7b)  │
│  Redis Data Redis    │  JJWT 0.11.5       │   / OpenAI 兼容        │
│  Hutool 验证码       │  spring-boot-starter-websocket │  Vosk (中文ASR) │
│  MySQL 库 termshop  │  MySQL 库 termshop │   + TTS + ComfyUI     │
└──────────────┬───────┴──────────┬──────────┴──────────┬─────────────┘
               │                  │                      │
               ▼                  ▼                      ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        数据层 (Data Layer)                            │
│         MySQL 8.0 (业务数据)  |  Redis (缓存/验证码/会话)              │
│         ChromaDB (向量知识库)   |  本地文件系统 (语料/图片/ASR模型)    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 三、各模块详解

### 3.1 用户前端 `用户前端/dui/`

| 项目 | 说明 |
|------|------|
| 框架 | Vue 3.5 + TypeScript 6 |
| 构建 | Vite 8 |
| UI组件库 | Vant 4（移动端H5首选组件库）+ @vant/icons 3 |
| 状态管理 | Pinia 3 |
| 路由 | Vue Router 5（History模式 + 路由守卫） |
| HTTP客户端 | Axios 1.17 |
| 样式 | Sass (sass-embedded) |
| 认证 | 路由守卫 + localStorage持久化用户/JWT |
| 代理 | Express 5 (开发期CORS代理) |

**目录结构：**
```
dui/src/
├── api/                 # 11个API模块 (user, order, cart, favorite, search, recommend, payment, category, productAttributes, merchantChat, customerChat, message, address)
├── components/          # 业务组件
│   ├── AIFloat/         # AI悬浮球（智能客服入口）
│   ├── Activity/        # 活动模块（限时秒杀/彩票转盘/火车票/百亿补贴/折扣）
│   ├── Address/         # 收货地址
│   ├── Cart/            # 购物车
│   ├── Favorite/        # 收藏夹
│   ├── Home/            # 首页（CategoryTabs/IndexHome/SearchBar）
│   ├── Login/           # 登录页
│   ├── my/              # 我的（收藏/钱包/历史）
│   ├── Order/           # 订单（确认/详情/列表）
│   ├── Payment/         # 支付（成功/失败）
│   ├── Peng/            # Peng首页
│   ├── Product/         # 商品（卡片/详情/列表/热销/团购）
│   ├── Search/          # 搜索页
│   ├── Setting/         # 设置模块（30+个子页面）
│   ├── message/         # 消息中心（系统/商家/会话）
│   └── Address/         # 收货地址
├── stores/              # Pinia状态 (user/cart/history/message/category/counter)
├── router/index.ts      # 35+路由 + 路由守卫（默认测试账号 user0001/123456）
├── utils/
│   ├── request.ts       # Axios统一封装
│   └── orderStatus.ts   # 订单状态机
├── data/area-list.ts   # 省市区数据
└── views/               # 视图层
```

**路由概览：**
- 首页/商城首页/Peng首页
- 商品详情/商品列表/分类商品/搜索页/热销榜
- 购物车/下单确认/支付成功失败
- 订单列表/订单详情/退换货
- 消息中心（系统通知、商家消息、客服对话）
- 设置（账号安全/支付/深色模式/长辈版/未成年模式/语言/隐私等30+项）
- 活动（限时秒杀、彩票转盘、火车票、百亿补贴、折扣）
- 收藏/历史浏览/钱包/收货地址

---

### 3.2 用户后端 `用户后端/javaqian/`

| 项目 | 说明 |
|------|------|
| 语言 | Java 11 |
| 框架 | Spring Boot 2.7.18 |
| ORM | MyBatis Plus 3.5.3.1（含逻辑删除字段 isDeleted） |
| 数据库 | MySQL 8.0.33（库名 **termshop**，开发 root/123456） |
| 缓存 | Spring Data Redis（Lettuce 连接池：max-active 8, min-idle 2） |
| 安全 | Spring Security Crypto（BCrypt 密码加密） |
| 工具库 | Hutool 5.8（图形验证码 CaptchaUtil） |
| 验证 | Hibernate Validator |
| JSON | Jackson + jackson-datatype-jsr310（Java8 日期） |
| 静态资源 | classpath:/static/ + F:/temp/image/ + F:/temp/catalog/ |
| 其他 | Lombok、CORS、SD WebUI 文生图（localhost:7860） |

**Controller层：**
```
com.mall.controller/
├── UserController              # 用户注册/登录/验证码
├── ProductController           # 商品列表/详情/图片/标签/评价
├── ProductDetailController     # 商品详情富文本
├── ProductAttributeController  # 商品属性维度
├── CategoryController          # 分类树
├── SearchController            # 商品搜索
├── CartController              # 购物车CRUD
├── AddressController           # 收货地址
├── OrderController             # 下单/列表/详情
├── PaymentController           # 支付接口
├── CouponController            # 优惠券系统
├── FavoriteController          # 收藏夹
├── HistoryController           # 浏览历史
├── MessageController           # 消息（系统通知等）
├── CustomerServiceController   # 客服接口（对接AI后端）
├── RecommendController         # 推荐算法（含增强推荐）
├── ShopBrandController         # 品牌/店铺信息
├── MerchantController          # 商家信息
├── CaptchaService              # 图形验证码
└── ImageGenerateController     # AI图片生成（对接ComfyUI）
```

**Entity（核心数据模型）：**
- MallUser / Merchant / UserAddress / Order / OrderItem
- TermProducts / ProductDetail / ProductSku / ProductTag / ProductReview / ProductImage
- ProductAttribute / ProductAttributeDimension / Category
- Cart / UserFavorite / HistoryRecord / Coupon / UserCoupon / Payment
- MerchantBrandInfo / GroupOrder / GroupOrderUser / ShopInfo

---

### 3.3 商家前端 `商家前端/houtai/`

| 项目 | 说明 |
|------|------|
| 框架 | Vue 3.5 + TypeScript 6 |
| 构建 | Vite 8 |
| UI组件库 | Element Plus 2.14 + @element-plus/icons 2.3 |
| 状态管理 | Pinia 3 |
| 路由 | Vue Router 5 |
| 图表 | ECharts 6 |
| HTTP | Axios 1.17 |
| WebSocket | @stomp/stompjs 7 + sockjs-client 1.6 |
| 样式 | 原生CSS |

**页面模块：**
```
views/
├── Dashboard.vue            # 数据大盘（ECharts图表）
├── Login.vue                # 管理员登录
├── MerchantLogin.vue        # 商家登录
├── MerchantRegister.vue     # 商家注册
├── MerchantPrivacy.vue      # 隐私协议
├── ProductList.vue          # 商品列表
├── ProductAdd.vue           # 新增商品
├── ProductCategory.vue      # 分类管理
├── OrderList.vue            # 订单列表
├── OrderReturn.vue          # 退换货管理
├── UserList.vue             # 用户列表
├── UserAddress.vue          # 用户地址
├── SystemConfig.vue         # 系统配置
├── SystemPermission.vue     # 权限管理
├── customer/
│   ├── ChatRoom.vue         # 客服聊天（WebSocket实时）
│   ├── ConversationList.vue  # 会话列表
│   └── QuickReplyManage.vue # 快捷回复管理
└── components/HelloWorld.vue
```

---

### 3.4 商家后端 `商家后端/shajiahoutai/`

| 项目 | 说明 |
|------|------|
| 语言 | Java 8 |
| 框架 | Spring Boot 2.7.18 |
| ORM | MyBatis Plus 3.5.3.1 |
| 安全 | Spring Security + JJWT 0.11.5（jjwt-api/impl/jackson）+ 自实现 JwtUtil |
| 实时通信 | spring-boot-starter-websocket + STOMP over WebSocket（客服聊天） |
| 数据库 | mysql-connector-j 8.3.0，库名 **termshop** |
| 缓存 | Spring Data Redis（Lettuce 连接池） |
| 工具 | Jackson JSR310 / Lombok / CORS / 自定义 SensitiveWordFilter 敏感词过滤 |
| 其他 | 文件上传 10MB / 统一 Result\<T\> 响应包装 |

**Controller层：**
```
org.example.controller/
├── MerchantController         # 商家注册/登录(JWT)/隐私协议
├── ProductController          # 商品管理（含标签关联）
├── ProductTagService          # 商品标签CRUD
├── MerchantOrderController    # 商家视角订单/物流日志
├── CustomerController         # 客服消息/会话（WebSocket STOMP）
├── DashboardController        # 统计大盘数据
├── RedisTestController        # Redis测试端点
└── WebSocketController        # STOMP消息路由端点
```

**WebSocket架构（STOMP协议）：**
```
商家前端                       商家后端
   |                              |
   |-- STOMP CONNECT --->        |
   |                              |-- WebSocketConfig (/ws/chat)
   |                              |-- SensitiveWordFilter (内容安全)
   |-- STOMP SUBSCRIBE --->      |-- /user/queue/messages (一对一)
   |                              |-- /topic/conversations (会话广播)
   |-- STOMP SEND --->           |-- SimpleBrokerMessageHandler
   |                              |-- CustomerServiceImpl (消息持久化)
   |<-- WebSocketMessage --      |-- QuickReplyServiceImpl (快捷回复)
```

**DTO层：**
- RegisterDTO / LoginDTO / ProductAddDTO / ProductListDTO / ProductUpdateDTO
- MessageVO / ConversationVO / SendMessageDTO / MarkReadDTO / QuickReplyDTO / UnreadCountVO

---

### 3.5 AI后端 `python后端/tow/`

| 项目 | 说明 |
|------|------|
| 语言 | Python 3.11 |
| 框架 | FastAPI 0.104+ |
| ASGI服务器 | Uvicorn 0.24+ |
| ORM | Pydantic 2 + Pydantic Settings |
| LLM | 默认 **Ollama** 本地（qwen2.5:7b），同时兼容 **OpenAI** SSE 流式接口 |
| RAG框架 | LangChain 0.1 + LangChain Community |
| 向量数据库 | ChromaDB 0.4+（持久化到 sqlite3） |
| 语音识别 | Vosk 中文小型模型 `vosk-model-small-cn-0.22`（离线 ASR） |
| 语音合成 | TTS 服务 |
| 图像生成 | ComfyUI API 对接（占位） |
| HTTP客户端 | httpx 0.25（异步） |
| 其他 | python-dotenv / Redis / python-multipart / aiofiles / tiktoken |

**`.env` 关键配置：**
```
LLM_API_BASE=http://localhost:11434/api
LLM_API_KEY=ollama
LLM_MODEL_NAME=qwen2.5:7b
CHROMA_PERSIST_DIR=./data/chroma_db
REDIS_URL=redis://localhost:6379/0
JAVA_API_BASE=http://localhost:8000/api   # 回写到 Java 后端
MYSQL_DATABASE=termshop
```

**核心能力：**

```
                          ┌─────────────────────────┐
                          │      FastAPI App         │
                          │   title: 智能客服API      │
                          └────────────┬────────────┘
                                       │
         ┌────────────────────────────┼────────────────────────────┐
         │                            │                            │
         ▼                            ▼                            ▼
   /api/chat (SSE流式)         /merchant/chat (商家)         /template (模板管理)
         │                            │                            │
         ▼                            ▼                            │
┌─────────────────┐          ┌─────────────────┐                 │
│  ChatService    │          │ MerchantChat    │                 │
│  RAG + LLM     │          │ Service         │                 │
│  + 语音输入    │          │ + 商家知识库    │                 │
└────┬────┬──┬───┘          └────┬─────┬─────┘                 │
     │    │  │                    │     │                       │
     ▼    ▼  ▼                    ▼     ▼                       │
   ┌─────┐ ┌──────────┐       ┌──────────┐ ┌───────────────────┐│
   │ LLM │ │  RAG     │       │ TTS      │ │  MySQLModels      ││
   │ Svc │ │ Service  │       │ Service  │ │  init_tables      ││
   │     │ │          │       │          │ │                   ││
   │ Oll ││ ChromaDB │       │ Vosk     │ │  DialogueTemplate ││
   │ ama ││ 向量检索  │       │ 语音合成  │ │ 服务管理          ││
   │/AI  ││ 文本分割  │       │          │ │                   ││
   └─────┘ └──────────┘       └──────────┘ └───────────────────┘│
     │                                               │
     ▼                                               ▼
   ChromaDB (持久化)                          MySQL (用户端复用表)
```

**Service层：**
```
app/services/
├── chat_service.py           # 流式对话核心
├── merchant_chat_service.py  # 商家客服对话
├── llm_service.py            # LLM统一适配(Ollama/OpenAI SSE流式)
├── rag_service.py            # RAG(检索增强生成)
├── speech_service.py         # 语音识别(Vosk ASR)
├── tts_service.py            # 语音合成(TTS)
├── tool_service.py           # 工具调用
├── merchant_db_service.py    # 商家MySQL操作
├── dialogue_template_service.py # 话术模板管理
└── comfyui/comfyui_api.py    # ComfyUI图像生成API
```

**API路由：**
- `POST /api/chat` - 用户智能客服对话（支持流式SSE）
- `POST /api/chat/speech` - 语音输入对话
- `POST /merchant/chat` - 商家视角对话
- `POST /merchant/chat/speech` - 商家语音对话
- `/template/*` - 话术模板CRUD
- `/knowledge/*` - 知识库文档管理

**RAG工作流：**
```
用户问题
   │
   ▼
text_splitter (Markdown/文本分块)
   │
   ▼
ChromaDB.embeddings (向量化存储)
   │
   ▼
ChromaDB.search_similar (余弦相似度检索 top-k)
   │
   ▼
拼接参考资料 + 历史对话 + 用户问题
   │
   ▼
LLM.generate_with_context (Ollama/OpenAI SSE流式)
   │
   ▼
FastAPI StreamingResponse (text/event-stream)
   │
   ▼
前端逐字输出显示
```

**语料知识库（预置内容）：**
- `会员积分FAQ.md`
- `商品相关FAQ.md`
- `商品详情示例.md`
- `订单物流FAQ.md`
- `退换货售后FAQ.md`

---

## 四、数据层设计

### MySQL（共享同一个业务库 termshop）

| 端 | 数据库 | 用户名 |
|------|--------|--------|
| 用户端 + 商家端 | `termshop` | root / 123456（开发环境） |

| 模块 | 主要表 |
|------|--------|
| 用户端 | mall_user / user_address / order / order_item / term_products / product_sku / product_detail / product_review / cart / user_favorite / coupon / user_coupon / payment / message / history_record |
| 商家端 | term_merchant / term_product / term_product_tag / customer_message / quick_reply / merchant_order_log |

### Redis（独立缓存实例）

| 用途 | Key示例 |
|------|---------|
| 图形验证码 | captcha:{uuid} → code |
| 用户登录状态 | session:{userId} → token |
| 商家JWT白名单 | jwt:merchant:{merchantId} |
| 推荐榜单热缓存 | hot:products:{date} |

### ChromaDB（向量知识库）

| Collection | 用途 | Embedding |
|------------|------|-----------|
| default | FAQ与商品知识 | 默认sentence-transformer |

持久化路径：`app/data/chroma_db/chroma.sqlite3`

---

## 五、实时通信架构

### 5.1 用户端 — AI智能客服

```
用户前端 (Vue3)          AI后端 (FastAPI)             向量库 (ChromaDB)
    │                        │                           │
    │-- POST /api/chat ----->│                           │
    │  (SSE Stream)           │                           │
    │                        │-- RAG Service.retrieve_context ->│
    │                        │<-- top-k 文档片段 ----------│
    │                        │                           │
    │                        │-- LLM.generate_with_context(Ollama/OpenAI)
    │                        │  StreamingResponse
    │<- data: {"delta":"..."}│
    │<- data: {"delta":"逐字"}│
    │<- data: [DONE]         │
```

### 5.2 商家端 — WebSocket客服

```
商家前端 (Vue3 + STOMP)      商家后端 (Spring Boot + STOMP Broker)
    │                              │
    │-- STOMP /ws/chat ----------->│
    │                              │-- SensitiveWordFilter.check
    │                              │-- CustomerServiceImpl.save
    │-- SUBSCRIBE /user/queue/messages
    │                              │
    │-- STOMP SEND /app/chat ----->│
    │                              │-- SimpleBrokerMessageHandler
    │<-- WebSocketMessage VO ----- │
```

---

## 六、认证与安全

| 端 | 方式 | 细节 |
|----|------|------|
| 用户端 | Session + Cookie（开发期） / 路由守卫 + localStorage JWT | 默认测试账号 user0001/123456 |
| 商家端 | BCrypt密码加密 + 自实现JWT (JwtUtil) + Redis白名单 | /merchant/register → /merchant/login (POST) |
| AI后端 | API Key 可选（OpenAI/Ollama） + 前端无需鉴权 | 内部调用，服务间信任 |
| 全端 | CORS全局放行（开发期） + Hibernate Validator参数校验 | 敏感词过滤（商家聊天） |

---

## 七、构建与部署命令速查

### 用户前端（dui）
```bash
pnpm install
pnpm run dev         # http://localhost:5173
pnpm run build       # dist/产物
```

### 商家前端（houtai）
```bash
pnpm install
pnpm run dev         # http://localhost:5174
pnpm run build
```

### 用户后端（javaqian）
```bash
mvn spring-boot:run     # http://localhost:8080
mvn clean package       # mall-user.jar
```

### 商家后端（shajiahoutai）
```bash
mvn spring-boot:run     # http://localhost:9090  (WebSocket /ws/chat)
mvn clean package
```

### AI后端（tow）
```bash
pip install -r requirements.txt
python start_server.py  # http://localhost:8000  /docs Swagger UI
```

---

## 八、端口映射总表（已验证）

| 服务 | 端口 | 备注 |
|------|------|------|
| 用户前端 dev（dui） | Vite 默认 5173 | Vite HMR |
| 商家前端 dev（houtai） | **5174** | vite.config.ts 显式配置 |
| 用户后端（javaqian） | **8080** | application.yml |
| 商家后端（shajiahoutai） | **9090** | application.yml（代理目标） |
| AI 后端（tow） | **8000** | FastAPI /docs Swagger |
| MySQL | 3306 | 数据库 termshop |
| Redis | 6379 | Lettuce 连接池 |
| ChromaDB | 文件持久化 sqlite3 | `./data/chroma_db/chroma.sqlite3` |
| Ollama（本地 LLM） | 11434 | qwen2.5:7b |
| ComfyUI | 8188 | 图像生成（可选） |

**Vite 代理规则：**
- 商家前端（5174）代理 `/api` → `http://localhost:9090`（商家后端）
- 用户前端代理 `/api/*`、`/user`、`/cart`、`/favorite` → `http://127.0.0.1:8080`（用户后端）
- 用户前端代理 `/MerchantChat` → `http://localhost:8000`（AI 后端）

---

## 九、关键依赖版本汇总

| 分类 | 库 | 版本 |
|------|----|------|
| 用户前端 | Vue | 3.5 |
| 用户前端 | Vant | 4.9 |
| 用户前端 | Pinia | 3.0 |
| 用户前端 | Vite | 8 |
| 用户前端 | TypeScript | 6 |
| 商家前端 | Element Plus | 2.14 |
| 商家前端 | ECharts | 6.1 |
| 商家前端 | STOMP.js | 7.3 |
| 用户后端 | Java | 11 |
| 商家后端 | Java | 8 |
| 用户后端/商家后端 | Spring Boot | 2.7.18 |
| 用户后端/商家后端 | MyBatis Plus | 3.5.3.1 |
| 用户后端 | MySQL Connector | mysql-connector-java 8.0.33 |
| 商家后端 | MySQL Connector | mysql-connector-j 8.3.0 |
| 商家后端 | JJWT | 0.11.5（jjwt-api / impl / jackson） |
| 商家后端 | WebSocket | spring-boot-starter-websocket |
| 用户后端 | Hutool | 5.8.23 |
| AI后端 | FastAPI | 0.104+ |
| AI后端 | LangChain | 0.1 + LangChain Community |
| AI后端 | ChromaDB | 0.4+ |
| AI后端 | 本地 LLM | Ollama qwen2.5:7b（兼容 OpenAI 协议） |
| AI后端 | Python | 3.11 |
| 数据库 | MySQL | 8.0 库名 termshop |
| 数据库 | Redis | Lettuce + Spring Data Redis |

---

> 生成日期：2026-06-29
