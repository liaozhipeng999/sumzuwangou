# Dui 商城 — 项目介绍（PPT 提纲版）

> 面向 PPT 演讲使用：内容按"一句话标题 + 要点"组织，可直接拆成幻灯片。

---

## Slide 1 · 封面

**Dui 商城**
> 类拼多多的综合性移动端购物平台
> 技术栈：Vue 3 + TypeScript + Vite + Vant + Pinia + Vue Router

---

## Slide 2 · 一句话项目简介

**一个面向 C 端用户的移动端购物前端，覆盖"逛 → 搜 → 买 → 聊 → 付 → 管"的完整购物闭环。**

---

## Slide 3 · 技术栈

| 分类 | 选型 |
|---|---|
| 前端框架 | Vue 3（Composition API）+ TypeScript |
| 构建 | Vite 8 |
| UI 组件库 | Vant 4（移动端）+ @vant/icons |
| 状态管理 | Pinia 3 |
| 路由 | Vue Router 5 |
| HTTP | Axios（封装统一拦截器） |
| 样式 | Sass（sass-embedded） |
| 类型检查 | vue-tsc |
| 后端 A | Java（端口 8080，电商业务主接口） |
| 后端 B | Python FastAPI（端口 8000，商家 AI 客服） |

---

## Slide 4 · 系统架构总览

```
               ┌─────────────────────────────┐
               │        浏览器 / H5           │
               │  Vue3 SPA + Vant 移动端 UI   │
               └──────────────┬──────────────┘
                               │ Axios + fetch
                               │ （Bearer Token）
                 ┌─────────────┴───────────────┐
                 │                             │
      ┌──────────▼──────────┐      ┌───────────▼──────────┐
      │  Java 后端  :8080    │      │ Python FastAPI  :8000│
      │  业务主接口 电商核心   │      │ 商家 AI 客服 / 智能问答│
      └──────────┬──────────┘      └──────────────────────┘
                 │
          ┌──────▼──────┐
          │  MySQL / Redis│
          │ 商品/订单/用户 │
          └──────────────┘
```

---

## Slide 5 · 模块划分（共 6 大模块 / 13 个子模块 / 82 个接口）

| 大模块 | 子模块 |
|---|---|
| 用户中心 | 注册 / 登录 / 验证码 / 个人信息 |
| 商品系统 | 分类（三级）/ 推荐 / 搜索 / 详情 / SKU 属性 / 折扣 |
| 交易系统 | 购物车 / 下单 / 优惠券 / 价格计算 / 订单管理 / 支付 |
| 收藏 & 评价 | 商品收藏 / 评价展示（商品详情内） |
| 消息系统 | 5 类消息（活动/物流/交易/系统/商家） |
| 客服系统 | 店铺客服（Java）+ 商家 AI 智能客服（Python） |

---

## Slide 6 · 目录结构

```
dui/
├─ src/
│  ├─ api/              13 个接口封装文件（共 82 个方法）
│  ├─ assets/           静态资源
│  ├─ components/       业务组件（地址、购物车、订单、搜索等 20+）
│  ├─ data/             静态数据（省市区等）
│  ├─ router/           路由
│  ├─ stores/           Pinia（cart, category, user, message, history, counter）
│  ├─ utils/            请求封装 + 订单状态映射
│  ├─ views/            页面级视图
│  ├─ App.vue
│  └─ main.ts
├─ public/              公共静态资源
├─ .env                 VITE_API_BASE_URL
├─ vite.config.ts
├─ tsconfig.json
└─ package.json
```

---

## Slide 7 · 核心亮点 — 请求层

**统一请求封装（src/utils/request.ts）**

- Axios 实例 + `VITE_API_BASE_URL` 环境变量
- 请求拦截：自动注入 `Authorization: Bearer <token>`
- 响应拦截：
  - 自动修复中文乱码
  - 字段 `snake_case → camelCase`（`main_image → mainImage`）
  - 图片路径自动转换（`F:/temp/image/... → /images/...`）
  - 401 自动清空 Token 并跳登录页
- 附带 3 个 SVG 头像生成工具（`buildAvatarSvg / buildBrandIconSvg / buildShapesSvg`），后端无图片时兜底

---

## Slide 8 · 核心亮点 — 商品 SKU / 折扣

商品属性系统由 **商品类型 → 属性维度 → SKU 矩阵 → 折扣/优惠券** 四层组成：

- `getAttributeOptions`：按 `productType` 动态返回维度（颜色 / 存储 / 规格等）
- `calculateDiscount`：实时折扣计算，返回 `discountRate / discountLabel / availableCoupons / bestCouponDiscount / finalPrice`
- 折扣标签（前端工具函数）：按折扣率映射"一折~九折"

---

## Slide 9 · 核心亮点 — AI 客服

**双后端架构**：Java 负责常规店铺人工客服，Python 负责商家 AI 智能客服。

Python 服务（:8000）提供 3 个接口：

| 接口 | 作用 |
|---|---|
| `POST /MerchantChat` | 发起 AI 智能问答 |
| `GET  /MerchantChat/history` | 会话历史 |
| `POST /MerchantChat/end` | 结束会话 |

返回结构带 `source: "corpus" | "llm"`，用于前端区分语料命中 vs 大模型生成。

---

## Slide 10 · 核心亮点 — 消息中心（5 类消息）

| 类型 | 图标主色 | 示例 |
|---|---|---|
| 活动优惠 promotion | 橙 `#ff6034` | 优惠券到账、限时秒杀 |
| 物流通知 logistics | 蓝 `#1989fa` | 已发货、正在派送 |
| 交易消息 transaction | 绿 `#07c160` | 下单成功、退款到账 |
| 系统通知 system | 灰 `#909399` | 公告、规则变更 |
| 商家消息 merchant | 紫 `#7232dd` | 客服主动消息 |

每个分类支持：列表、单条已读、分类全已读、删除、批量删除。

---

## Slide 11 · 接口分布

```
  用户 ┃███   5
  地址 ┃███████  7
  购物车 ┃████████  8
  分类/商品 ┃███████  7
  推荐/详情 ┃████████  8
  商品属性 ┃███  4
  搜索 ┃███████  7
  收藏 ┃███  4
  订单 ┃█████████  9
  支付 ┃██  2
  消息 ┃████████  8
  用户客服 ┃██████████ 10
  AI客服 ┃██  3
        └──────────
            合计  82
```

---

## Slide 12 · 运行 / 构建

```bash
npm install
npm run dev      # Vite 8 开发模式
npm run build    # vue-tsc + vite 构建
npm run preview  # 预览 dist
```

`.env` 中 `VITE_API_BASE_URL=` 为空，意味着所有请求走同域，由 Vite dev server 代理到 Java 后端。

---

## Slide 13 · 总结

| 维度 | 结论 |
|---|---|
| 完成度 | 从用户注册到 AI 客服的购物闭环，**82 个接口** 全链路打通 |
| UI | Vant 移动端组件库，天然适配手机 |
| 架构 | 统一请求层 + 双后端（Java 主业务 / Python AI）清晰分离 |
| 可扩展 | SKU/折扣/消息均已抽象，新增类型只动配置 |
| 风险点 | `favorite.clearFavorites` 后端未实现，前端返回占位 |

---

## Slide 14 · 未来可扩展方向（可选）

- SSE / WebSocket 实时消息（订单状态、客服消息）
- 商品 SKU 可视化选择器 UI 组件化
- 支付真实对接（微信/支付宝沙箱 → 正式）
- 埋点 / 曝光统计（推荐效果评估）
- PWA 离线化 + App 壳（Capacitor/Tauri）
