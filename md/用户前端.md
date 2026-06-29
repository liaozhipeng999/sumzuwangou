# Dui 商城 — 前端接口文档总览

> 前端基于 Vue 3 + TypeScript + Vite + Vant + Pinia + Vue Router。
> 所有 HTTP 请求通过 `src/utils/request.ts`（axios 实例）或原生 fetch 发出。
> `baseURL` 由 `.env` 中 `VITE_API_BASE_URL` 控制，生产构建后前端通过 Nginx / Vite 代理转发到真实后端。
> 接口前缀存在 `/api`、`/product`、`/recommend`、`/MerchantChat` 等多套，对应不同后端（Java :8080 与 Python :8000）。
> 响应数据统一结构基本为：
>
> ```json
> { "code": 0, "message": "ok", "data": {} }
> ```
>
> 前端 axios 响应拦截器会自动做：
>
> - 字段下划线 → 驼峰转换（例如 `main_image` → `mainImage`）
> - 图片路径兜底（`F:/temp/image/...` 转 `/images/...`）
> - 401 自动跳转登录页
> - Token 通过 `sessionStorage.token` 放在 `Authorization: Bearer xxx`

---

## 0. 公共约定

| 项 | 说明 |
|---|---|
| 协议 | HTTP / JSON |
| Content-Type | `application/json;charset=UTF-8` |
| 鉴权 | `Authorization: Bearer <token>`（从 sessionStorage 读取） |
| 超时 | 10s（商家客服 Python 服务为 30s） |
| 字段转换 | 后端 snake_case → 前端 camelCase（由响应拦截器完成） |
| 端口路由 | Java 后端 :8080；Python 商家客服 :8000；前端默认走同域代理 |

---

## 1. 用户模块 — user.ts

前缀：`/api/user`（Java 后端）

### 1.1 获取图形验证码

| 项 | 值 |
|---|---|
| URL | `GET /api/user/captcha` |
| 前端方法 | `getCaptcha()` |
| 请求体 | 无 |
| 响应 | 二进制图片流 + 响应头 `Captcha-Key` |

前端使用 `fetch` 直接取 Blob，不是 axios。

### 1.2 获取验证码（调试用）

| 项 | 值 |
|---|---|
| URL | `GET /api/user/captcha/debug` |
| 前端方法 | `getCaptchaDebug()` |
| 请求体 | 无 |
| 响应 | `{ key: string, code: string }` |

### 1.3 用户注册

| 项 | 值 |
|---|---|
| URL | `POST /api/user/register` |
| 前端方法 | `register(data, captchaKey)` |
| 请求头 | `Captcha-Key: <captchaKey>` |
| 请求体 |

```json
{
  "username": "string",
  "password": "string",
  "confirmPassword": "string",
  "phone": "string",
  "email": "string?",
  "nickname": "string?",
  "captcha": "string"
}
```

| 项 | 值 |
|---|---|
| 响应 | `{ code, message }` |

### 1.4 用户登录

| 项 | 值 |
|---|---|
| URL | `POST /api/user/login` |
| 前端方法 | `login(username, password)` |
| 请求体 |

```json
{ "username": "string", "password": "string" }
```

| 项 | 值 |
|---|---|
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "phone": "13800000000",
    "email": "a@b.com"
  }
}
```

### 1.5 获取用户信息

| 项 | 值 |
|---|---|
| URL | `GET /api/user/{id}` |
| 前端方法 | `getUser(id)` |
| 响应 | `{ code, message, data: UserInfo }` |

---

## 2. 收货地址模块 — address.ts

前缀：`/api/address`（Java 后端）

### 2.1 地址列表

| 项 | 值 |
|---|---|
| URL | `GET /api/address/list?userId=<userId>` |
| 前端方法 | `getAddressList(userId)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "receiver_name": "张三",
      "receiver_phone": "13800000000",
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "detail_address": "科技园1号",
      "is_default": 1,
      "sort": 1,
      "created_at": "2026-01-01 12:00:00",
      "updated_at": "2026-01-01 12:00:00"
    }
  ]
}
```

### 2.2 地址详情

| 项 | 值 |
|---|---|
| URL | `GET /api/address/{id}` |
| 前端方法 | `getAddressDetail(id)` |

### 2.3 新增地址

| 项 | 值 |
|---|---|
| URL | `POST /api/address/create` |
| 前端方法 | `createAddress(data)` |
| 请求体 |

```json
{
  "userId": 1,
  "receiver_name": "张三",
  "receiver_phone": "13800000000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detail_address": "科技园1号",
  "is_default": 0
}
```

### 2.4 修改地址

| 项 | 值 |
|---|---|
| URL | `POST /api/address/update` |
| 前端方法 | `updateAddress(data)` |

请求体与创建一致，但需包含 `id`。

### 2.5 删除地址

| 项 | 值 |
|---|---|
| URL | `POST /api/address/delete/{id}` |
| 前端方法 | `deleteAddress(id)` |

### 2.6 设为默认地址

| 项 | 值 |
|---|---|
| URL | `POST /api/address/set_default/{id}?userId=<userId>` |
| 前端方法 | `setDefaultAddress(id, userId)` |

### 2.7 省市区数据

| 项 | 值 |
|---|---|
| URL | `GET /api/region/list` |
| 前端方法 | `getRegionData()` |

---

## 3. 购物车模块 — cart.ts

前缀：`/api/cart`（Java 后端）

### 3.1 购物车列表

| 项 | 值 |
|---|---|
| URL | `GET /api/cart/list?userId=<userId>` |
| 前端方法 | `getCartList(userId)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "productId": 100,
      "skuId": 200,
      "productName": "iPhone 15",
      "productImage": "/images/phone/001.jpg",
      "price": 5999,
      "quantity": 2,
      "selected": 1,
      "createdAt": "2026-01-01 12:00:00",
      "updatedAt": "2026-01-01 12:00:00"
    }
  ]
}
```

### 3.2 添加到购物车

| 项 | 值 |
|---|---|
| URL | `POST /api/cart/add` |
| 前端方法 | `addToCart(params)` |
| 请求体 |

```json
{
  "userId": 1,
  "productId": 100,
  "productName": "iPhone 15",
  "productImage": "/images/phone/001.jpg",
  "price": 5999,
  "quantity": 1
}
```

### 3.3 更新数量

| 项 | 值 |
|---|---|
| URL | `POST /api/cart/update` |
| 前端方法 | `updateCartQuantity({ userId, cartId, quantity })` |

### 3.4 切换选中

| 项 | 值 |
|---|---|
| URL | `POST /api/cart/select` |
| 前端方法 | `updateCartSelected({ userId, cartId, selected })` |
| 说明 | `selected`：0 未选中，1 选中 |

### 3.5 全选 / 取消全选

| 项 | 值 |
|---|---|
| URL | `POST /api/cart/selectAll` |
| 前端方法 | `updateAllSelected({ userId, selected })` |

### 3.6 删除商品

| 项 | 值 |
|---|---|
| URL | `POST /api/cart/remove` |
| 前端方法 | `removeFromCart({ userId, cartId })` |

### 3.7 清空购物车

| 项 | 值 |
|---|---|
| URL | `POST /api/cart/clear` |
| 前端方法 | `clearCart(userId)` |

### 3.8 购物车数量

| 项 | 值 |
|---|---|
| URL | `GET /api/cart/count?userId=<userId>` |
| 前端方法 | `getCartCount(userId)` |

---

## 4. 分类 & 商品模块 — category.ts

前缀：`/api/category`（Java 后端）

### 4.1 一级分类

| 项 | 值 |
|---|---|
| URL | `GET /api/category/list` |
| 前端方法 | `getLevel1Categories()` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    { "id": 1, "categoryName": "手机通讯", "icon": "/icons/phone.png", "sort": 1 }
  ]
}
```

### 4.2 一级 + 二级分类（各 10 个）

| 项 | 值 |
|---|---|
| URL | `GET /api/category/level1-level2` |
| 前端方法 | `getLevel1Level2Categories()` |

### 4.3 二级分类

| 项 | 值 |
|---|---|
| URL | `GET /api/category/{level1Id}/children` |
| 前端方法 | `getLevel2Categories(level1Id)` |

### 4.4 三级分类

| 项 | 值 |
|---|---|
| URL | `GET /api/category/level3/{level2Id}` |
| 前端方法 | `getLevel3Categories(level2Id)` |

### 4.5 一级分类商品

| 项 | 值 |
|---|---|
| URL | `GET /api/category/{level1Id}/products?limit=8` |
| 前端方法 | `getLevel1Products(level1Id, limit)` |

### 4.6 二级分类商品

| 项 | 值 |
|---|---|
| URL | `GET /api/category/level2/{level2Id}/products?limit=8` |
| 前端方法 | `getLevel2Products(level2Id, limit)` |

### 4.7 三级分类商品

| 项 | 值 |
|---|---|
| URL | `GET /api/category/level3/{level3Id}/products?limit=8` |
| 前端方法 | `getLevel3Products(level3Id, limit)` |

---

## 5. 商品推荐 / 详情 — recommend.ts

前缀：`/recommend`、`/product`（Java 后端）

### 5.1 综合推荐

| 项 | 值 |
|---|---|
| URL | `GET /recommend/products?limit=10&userId=?` |
| 前端方法 | `getRecommendProducts(limit, userId?)` |

### 5.2 热销

| 项 | 值 |
|---|---|
| URL | `GET /recommend/hot?limit=8` |
| 前端方法 | `getHotProducts(limit)` |

### 5.3 新品

| 项 | 值 |
|---|---|
| URL | `GET /recommend/new?limit=10` |
| 前端方法 | `getNewProducts(limit)` |

### 5.4 好评榜

| 项 | 值 |
|---|---|
| URL | `GET /recommend/top-rated?limit=10` |
| 前端方法 | `getTopRatedProducts(limit)` |

### 5.5 分类推荐

| 项 | 值 |
|---|---|
| URL | `GET /recommend/category/{categoryId}?limit=10` |
| 前端方法 | `getCategoryProducts(categoryId, limit)` |

### 5.6 随机推荐

| 项 | 值 |
|---|---|
| URL | `GET /recommend/random?limit=10` |
| 前端方法 | `getRandomProducts(limit)` |

### 5.7 记录浏览

| 项 | 值 |
|---|---|
| URL | `POST /recommend/record-view` |
| 前端方法 | `recordView(productId, userId?)` |
| 请求体 | 前端传了 `params` 对象（实际后端若需 JSON，需按后端契约校验） |

### 5.8 商品详情

| 项 | 值 |
|---|---|
| URL | `GET /product/detail?productId=<id>` |
| 前端方法 | `getProductDetail(id)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "product": { "...": "..." },
    "carouselImages": [ { "id": 1, "imageUrl": "/images/...", "sort": 1, "imageType": 1 } ],
    "merchant": { "id": 1, "merchantName": "xxx", "merchantLogo": "...", "merchantBrief": "..." },
    "brandInfo": { "brandName": "...", "brandLogo": "...", "brandSlogan": "...", "introduction": "..." },
    "groupInfo": { "groupId": 1, "groupSize": 2, "currentCount": 1, "groupPrice": 50.0, "expireSeconds": 120, "status": 0, "users": [] },
    "reviews": [],
    "detailImages": [],
    "details": [ { "paramKey": "产地", "paramValue": "中国" } ],
    "tags": [ { "tagName": "自营", "tagColor": "#ff6034" } ],
    "isFavorited": false
  }
}
```

---

## 6. 商品属性 / SKU / 折扣 — productAttributes.ts

前缀：`/api/product/attributes`（Java 后端）

### 6.1 属性选项

| 项 | 值 |
|---|---|
| URL | `GET /api/product/attributes/options?productId=<id>` |
| 前端方法 | `getAttributeOptions(productId)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "productId": 100,
    "productName": "iPhone 15",
    "productType": "phone",
    "dimensions": [ { "key": "color", "name": "颜色", "sortOrder": 1 } ],
    "attributeOptions": { "颜色": { "黑色": [101, 102], "蓝色": [103] } },
    "skus": {
      "sku_black_128": { "skuId": 101, "skuCode": "xxx", "price": 5999, "originalPrice": 6999, "stock": 100, "imageUrl": "...", "attributes": { "颜色": "黑色", "存储": "128GB" } }
    }
  }
}
```

### 6.2 属性维度

| 项 | 值 |
|---|---|
| URL | `GET /api/product/attributes/dimensions?productType=?` |
| 前端方法 | `getDimensions(productType?)` |

### 6.3 计算折扣

| 项 | 值 |
|---|---|
| URL | `GET /api/product/attributes/discount?productId=&skuId=&userId=` |
| 前端方法 | `calculateDiscount(productId, skuId, userId?)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "productId": 100,
    "skuId": 101,
    "skuCode": "xxx",
    "originalPrice": 6999,
    "currentPrice": 5999,
    "discountAmount": 1000,
    "discountRate": 86,
    "discountLabel": "8.6折",
    "availableCoupons": [],
    "bestCouponDiscount": 200,
    "finalPrice": 5799
  }
}
```

### 6.4 所有商品类型

| 项 | 值 |
|---|---|
| URL | `GET /api/product/attributes/types` |
| 前端方法 | `getProductTypes()` |
| 响应 | `{ code, message, data: ["bike","phone",...] }` |

---

## 7. 搜索模块 — search.ts

前缀：`/api/search`（Java 后端）

### 7.1 搜索商品

| 项 | 值 |
|---|---|
| URL | `GET /api/search/products?keyword=&categoryId=&page=&pageSize=&sortBy=&sortOrder=` |
| 前端方法 | `searchProducts(keyword, categoryId?, page, pageSize, sort, filters)` |
| 说明 | sort 值：`sales_asc/sales_desc/price_asc/price_desc/created_at/brand` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "products": [ "...SearchProduct..." ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

### 7.2 搜索建议

| 项 | 值 |
|---|---|
| URL | `GET /api/search/suggest?keyword=&limit=5` |
| 前端方法 | `getSearchSuggestions(keyword, limit)` |

### 7.3 搜索历史

| 项 | 值 |
|---|---|
| URL | `GET /api/search/history?userId=default` |
| 前端方法 | `getSearchHistory(userId)` |

### 7.4 保存搜索历史

| 项 | 值 |
|---|---|
| URL | `POST /api/search/history?userId=&keyword=` |
| 前端方法 | `saveSearchHistory(keyword, userId)` |

### 7.5 清空搜索历史

| 项 | 值 |
|---|---|
| URL | `DELETE /api/search/history?userId=` |
| 前端方法 | `clearSearchHistory(userId)` |

### 7.6 删除单条历史

| 项 | 值 |
|---|---|
| URL | `DELETE /api/search/history/delete?userId=&keyword=` |
| 前端方法 | `deleteSearchHistory(keyword, userId)` |

### 7.7 热门搜索

| 项 | 值 |
|---|---|
| URL | `GET /api/search/hot?limit=10` |
| 前端方法 | `getHotSearch(limit)` |

---

## 8. 收藏模块 — favorite.ts

前缀：`/product/favorite`（Java 后端）

### 8.1 切换收藏状态

| 项 | 值 |
|---|---|
| URL | `POST /product/favorite/toggle?userId=&productId=` |
| 前端方法 | `toggleFavorite(userId, productId)` |
| 响应 | `{ code, message, isFavorited: true/false }` |

### 8.2 收藏列表

| 项 | 值 |
|---|---|
| URL | `GET /product/favorite/list?userId=&page=&pageSize=` |
| 前端方法 | `getFavoriteList(userId, page, pageSize)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "total": 100,
    "totalPages": 5,
    "pageSize": 20,
    "page": 1,
    "products": [
      {
        "id": 1,
        "productName": "iPhone 15",
        "price": 5999,
        "originalPrice": 6999,
        "mainImage": "...",
        "sales": 10000,
        "brief": "...",
        "favoritedAt": "2026-01-01 12:00:00"
      }
    ]
  }
}
```

### 8.3 取消收藏（复用 toggle）

| 项 | 值 |
|---|---|
| URL | 同 `toggle` |
| 前端方法 | `removeFavorite(userId, productId)` |

### 8.4 清空收藏

| 项 | 值 |
|---|---|
| URL | （后端未实现） |
| 前端方法 | `clearFavorites(userId)` |
| 说明 | 前端直接返回 `{ code: 500, message: '清空收藏列表功能暂未实现' }` |

---

## 9. 订单模块 — order.ts

前缀：`/api/order`（Java 后端）

### 9.1 下单前 — 获取地址列表

| 项 | 值 |
|---|---|
| URL | `GET /api/order/addresses?userId=<userId>` |
| 前端方法 | `getAddresses(userId)` |

### 9.2 下单前 — 商品 SKU

| 项 | 值 |
|---|---|
| URL | `GET /api/order/product/skus?productId=<id>` |
| 前端方法 | `getProductSkus(productId)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "skus": [
      { "id": 1, "productId": 100, "skuCode": "xxx", "attributes": "颜色:黑色;存储:128GB", "price": 5999, "originalPrice": 6999, "stock": 100, "sales": 1000, "imageUrl": "...", "status": 1 }
    ],
    "attributeGroups": [
      { "attr_name": "颜色", "attr_values": "黑色,蓝色" }
    ]
  }
}
```

### 9.3 下单前 — 可用优惠券

| 项 | 值 |
|---|---|
| URL | `GET /api/order/coupons?userId=&amount=` |
| 前端方法 | `getCoupons(userId, amount?)` |

### 9.4 下单前 — 价格计算

| 项 | 值 |
|---|---|
| URL | `POST /api/order/calculate` |
| 前端方法 | `calculatePrice(data)` |
| 请求体 |

```json
{
  "items": [ { "skuId": 101, "quantity": 2 } ],
  "couponId": 999
}
```

| 项 | 值 |
|---|---|
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "totalPrice": 11998,
    "discountAmount": 1000,
    "couponAmount": 200,
    "payAmount": 10798
  }
}
```

### 9.5 创建订单

| 项 | 值 |
|---|---|
| URL | `POST /api/order/create` |
| 前端方法 | `createOrder(data)` |
| 请求体 |

```json
{
  "userId": 1,
  "addressId": 10,
  "items": [ { "skuId": 101, "quantity": 2 } ],
  "couponId": 999
}
```

| 项 | 值 |
|---|---|
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "orderId": 9001,
    "orderNo": "O20260629123000",
    "totalAmount": 11998,
    "discountAmount": 1000,
    "couponAmount": 200,
    "payAmount": 10798
  }
}
```

### 9.6 订单列表

| 项 | 值 |
|---|---|
| URL | `GET /api/order/list?userId=<userId>` |
| 前端方法 | `getOrderList(userId)` |

### 9.7 订单详情

| 项 | 值 |
|---|---|
| URL | `GET /api/order/detail?orderId=<id>` |
| 前端方法 | `getOrderDetail(orderId)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "order": { "id": 9001, "orderNo": "...", "status": 1, "totalAmount": 11998, ... },
    "address": { "id": 10, "receiverName": "张三", "receiverPhone": "...", "province": "...", ... },
    "items": [
      { "id": 1, "productId": 100, "skuId": 101, "productName": "iPhone 15", "skuAttributes": "黑色 128GB", "price": 5999, "originalPrice": 6999, "quantity": 2, "imageUrl": "...", "merchant": { "id": 1, "name": "xxx", "logo": "..." } }
    ]
  }
}
```

### 9.8 更新订单状态

| 项 | 值 |
|---|---|
| URL | `PUT /api/order/status?orderId=&status=` |
| 前端方法 | `updateOrderStatus(orderId, status)` |
| 状态 | 0待支付 1已支付 2已发货 3已收货 4已取消（由前端 deleteOrder 映射） |

### 9.9 发货

| 项 | 值 |
|---|---|
| URL | `POST /api/order/ship` |
| 前端方法 | `shipOrder(data)` |
| 请求体 |

```json
{
  "orderId": 9001,
  "shipCompany": "顺丰速运",
  "shipNo": "SF1234567890",
  "shipRemark": "易碎品，请轻拿轻放"
}
```

---

## 10. 支付模块 — payment.ts

前缀：`/api/payment`（Java 后端）

### 10.1 创建支付

| 项 | 值 |
|---|---|
| URL | `POST /api/payment/create` |
| 前端方法 | `createPayment(data)` |
| 请求体 |

```json
{ "orderId": 9001, "userId": 1, "payType": 1 }
```

| 项 | 值 |
|---|---|
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "paymentId": 1,
    "orderId": 9001,
    "orderNo": "O...",
    "amount": 10798,
    "payType": 1,
    "payStatus": 0,
    "transactionId": "TP123456",
    "payTime": "..."
  }
}
```

### 10.2 查询支付状态

| 项 | 值 |
|---|---|
| URL | `GET /api/payment/check?orderId=<id>` |
| 前端方法 | `checkPayment(orderId)` |

PayType 映射（前端常量）：

| payType | 含义 |
|---|---|
| 1 | 微信支付 |
| 2 | 支付宝 |
| 3 | 银行卡 |

PayStatus 映射：

| payStatus | 含义 |
|---|---|
| 0 | 待支付 |
| 1 | 支付成功 |
| 2 | 支付失败 |

---

## 11. 消息模块 — message.ts

前缀：`/api/messages`（Java 后端）

消息类型：`logistics` 物流、`transaction` 交易、`system` 系统、`promotion` 活动优惠、`merchant` 商家

### 11.1 消息分类概览

| 项 | 值 |
|---|---|
| URL | `GET /api/messages/summary?userId=1` |
| 前端方法 | `getMessageSummary(userId)` |
| 响应 |

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "type": "promotion",
      "label": "活动优惠",
      "icon": "gift-o",
      "unreadCount": 3,
      "latestMessage": { "...Message..." }
    }
  ]
}
```

### 11.2 总未读数

| 项 | 值 |
|---|---|
| URL | `GET /api/messages/unread-count?userId=` |
| 前端方法 | `getUnreadCount(userId)` |

### 11.3 分类消息列表

| 项 | 值 |
|---|---|
| URL | `GET /api/messages/{type}?page=&pageSize=&userId=` |
| 前端方法 | `getMessagesByType(type, page, pageSize, userId)` |

### 11.4 标记单条已读

| 项 | 值 |
|---|---|
| URL | `PUT /api/messages/{id}/read?userId=` |
| 前端方法 | `markMessageRead(id, userId)` |

### 11.5 分类全部已读

| 项 | 值 |
|---|---|
| URL | `PUT /api/messages/{type}/read-all?userId=` |
| 前端方法 | `markAllRead(type, userId)` |

### 11.6 全部已读

| 项 | 值 |
|---|---|
| URL | `PUT /api/messages/read-all?userId=` |
| 前端方法 | `markAllMessagesRead(userId)` |

### 11.7 删除单条

| 项 | 值 |
|---|---|
| URL | `DELETE /api/messages/{id}?userId=` |
| 前端方法 | `deleteMessage(id, userId)` |

### 11.8 批量删除某分类

| 项 | 值 |
|---|---|
| URL | `DELETE /api/messages/type/{type}?userId=` |
| 前端方法 | `deleteMessagesByType(type, userId)` |

---

## 12. 用户客服模块 — customerChat.ts

前缀：`/api/customer-service`（Java 后端）

### 12.1 用户会话列表

| 项 | 值 |
|---|---|
| URL | `GET /api/customer-service/conversations?userId=<id>` |
| 前端方法 | `getConversations(userId)` |

### 12.2 用户未读总数

| 项 | 值 |
|---|---|
| URL | `GET /api/customer-service/unread-count?userId=<id>` |
| 前端方法 | `getUnreadCount(userId)` |

### 12.3 标记某店铺已读

| 项 | 值 |
|---|---|
| URL | `POST /api/customer-service/mark-read` |
| 前端方法 | `markAsRead(userId, shopId)` |
| 请求体 | `{ userId, shopId }` |

### 12.4 聊天记录

| 项 | 值 |
|---|---|
| URL | `GET /api/customer-service/messages?shopId=&userId=&page=1&pageSize=20` |
| 前端方法 | `getMessages(shopId, userId, page, pageSize)` |

### 12.5 发送消息

| 项 | 值 |
|---|---|
| URL | `POST /api/customer-service/messages` |
| 前端方法 | `sendMessage(data)` |
| 请求体 |

```json
{
  "userId": 1,
  "shopId": 10,
  "content": "请问这个商品包邮吗？",
  "messageType": "text",
  "productId": 100
}
```

### 12.6 删除单条

| 项 | 值 |
|---|---|
| URL | `DELETE /customer-service/messages/{messageId}?userId=` |
| 前端方法 | `deleteMessage(messageId, userId)` |

### 12.7 清空店铺聊天

| 项 | 值 |
|---|---|
| URL | `DELETE /api/customer-service/messages/clear?userId=&shopId=` |
| 前端方法 | `clearMessages(userId, shopId)` |

### 12.8 店铺信息

| 项 | 值 |
|---|---|
| URL | `GET /customer-service/shop/{shopId}` |
| 前端方法 | `getShopInfo(shopId)` |

### 12.9 商品信息（客服入口）

| 项 | 值 |
|---|---|
| URL | `GET /customer-service/product/{productId}` |
| 前端方法 | `getProductInfo(productId)` |

### 12.10 快捷回复

| 项 | 值 |
|---|---|
| URL | `GET /customer-service/quick-replies/{shopId}` |
| 前端方法 | `getQuickReplies(shopId)` |

---

## 13. 商家 AI 客服模块 — merchantChat.ts

前缀：`http://localhost:8000/MerchantChat`（Python 后端，axios 单独实例）

### 13.1 发送消息（AI 智能客服）

| 项 | 值 |
|---|---|
| URL | `POST http://localhost:8000/MerchantChat` |
| 前端方法 | `merchantChat(requestData)` |
| 参数（URLSearchParams） | `merchant_id`, `user_id`, `message`, `conversation_id?`, `user_name?` |
| 响应 |

```json
{
  "response": "亲亲这款商品是包邮的哦~",
  "conversation_id": "abc123",
  "source": "llm",
  "merchant_id": 1,
  "user_id": "u_001"
}
```

### 13.2 会话历史

| 项 | 值 |
|---|---|
| URL | `GET http://localhost:8000/MerchantChat/history?conversation_id=` |
| 前端方法 | `getConversationHistory(conversationId)` |
| 响应 |

```json
{
  "conversation_id": "abc123",
  "merchant_id": 1,
  "user_id": "u_001",
  "created_at": "...",
  "updated_at": "...",
  "messages": [
    { "role": "user", "content": "...", "source": null, "timestamp": "..." },
    { "role": "assistant", "content": "...", "source": "llm", "timestamp": "..." }
  ]
}
```

### 13.3 结束会话

| 项 | 值 |
|---|---|
| URL | `POST http://localhost:8000/MerchantChat/end?conversation_id=` |
| 前端方法 | `endConversation(conversationId)` |
| 响应 | `{ status, conversation_id, message }` |

---

## 14. 接口清单统计

| 模块 | 文件 | 接口数 | 后端 |
|---|---|---|---|
| 用户 | user.ts | 5 | Java :8080 |
| 地址 | address.ts | 7 | Java |
| 购物车 | cart.ts | 8 | Java |
| 分类/商品列表 | category.ts | 7 | Java |
| 商品推荐/详情 | recommend.ts | 8 | Java |
| 商品属性/SKU/折扣 | productAttributes.ts | 4 | Java |
| 搜索 | search.ts | 7 | Java |
| 收藏 | favorite.ts | 4 | Java |
| 订单 | order.ts | 9 | Java |
| 支付 | payment.ts | 2 | Java |
| 消息 | message.ts | 8 | Java |
| 用户客服 | customerChat.ts | 10 | Java |
| 商家 AI 客服 | merchantChat.ts | 3 | Python :8000 |
| **合计** | | **82** | |
