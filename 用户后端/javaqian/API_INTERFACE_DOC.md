# TermShop 电商后端 API 接口文档

> 后端服务地址：`http://localhost:8080`
> 数据库：MySQL（termshop）
> 缓存：Redis（localhost:6379）
> 框架：Spring Boot 2.x + MyBatis-Plus

---

## 目录

- [1. 用户模块 `/user`](#1-用户模块-user)
- [2. 商品模块 `/product`](#2-商品模块-product)
- [3. 商品详情模块 `/api/product`](#3-商品详情模块-apiproduct)
- [4. 分类模块 `/category`](#4-分类模块-category)
- [5. 购物车模块 `/cart`](#5-购物车模块-cart)
- [6. 订单模块 `/api/order`](#6-订单模块-apiorder)
- [7. 支付模块 `/api/payment`](#7-支付模块-apipayment)
- [8. 地址模块 `/api/address`](#8-地址模块-apiaddress)
- [9. 优惠券模块 `/coupon`](#9-优惠券模块-coupon)
- [10. 消息模块 `/api/messages`](#10-消息模块-apimessages)
- [11. 搜索模块 `/search`](#11-搜索模块-search)
- [12. 推荐模块 `/recommend`](#12-推荐模块-recommend)
- [13. 商家模块 `/merchant`](#13-商家模块-merchant)
- [14. 店铺品牌模块 `/api`](#14-店铺品牌模块-api)
- [15. 客服模块 `/customer-service`](#15-客服模块-customer-service)
- [16. 历史浏览模块 `/api/history`](#16-历史浏览模块-apihistory)
- [17. 商品属性模块 `/api/product/attributes`](#17-商品属性模块-apiproductattributes)
- [18. AI 图片生成模块 `/image`](#18-ai-图片生成模块-image)

---

## 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 说明 |
|------|------|
| code | 200=成功，400=参数错误，404=未找到，500=服务器错误，503=服务不可用 |
| message | 提示信息 |
| data | 返回数据（结构依接口而定） |

---

## 1. 用户模块 `/user`

### 1.1 用户登录

- **POST** `/user/login`

请求体：
```json
{
  "username": "string",
  "password": "string"
}
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "zhang3",
    "nickname": "小张",
    "phone": "13800138000",
    "email": "zhang3@example.com"
  }
}
```

### 1.2 获取图形验证码

- **GET** `/user/captcha`

响应：`image/png` 图片流，响应头含 `Captcha-Key`

### 1.3 调试：获取验证码内容

- **GET** `/user/captcha/debug`

响应：
```json
{ "key": "uuid", "code": "abcd" }
```

### 1.4 用户注册

- **POST** `/user/register`

请求头：`Captcha-Key: <key>`

请求体：
```json
{
  "username": "string",
  "password": "string",
  "phone": "string",
  "email": "string",
  "nickname": "string",
  "captcha": "string"
}
```

响应：`{ "code": 200, "message": "注册成功" }`

### 1.5 获取用户收藏列表（分页）

- **GET** `/user/favorites?userId=1&page=1&pageSize=20`

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "page": 1,
    "pageSize": 20,
    "list": [ { "productId": 101, "productName": "...", "price": 99.00, "mainImage": "..." } ]
  }
}
```

### 1.6 添加商品到收藏

- **POST** `/user/favorites`

请求体：`{ "userId": 1, "productId": 101 }`

### 1.7 取消收藏

- **DELETE** `/user/favorites?userId=1&productId=101`

---

## 2. 商品模块 `/product`

### 2.1 获取商品详情

- **GET** `/product/detail/{id}?userId=1`

路径参数：`id` = 商品 ID
查询参数：`userId`（可选，用于判断是否收藏）

响应（含轮播图、商家、评论、拼单等完整信息）：
```json
{
  "code": 200,
  "data": {
    "product": { "id": 101, "productName": "...", "price": 99.00, "stock": 100, "brief": "...", "mainImage": "..." },
    "images": [ { "id": 1, "imageUrl": "...", "sort": 1 } ],
    "tags": [ { "tagName": "热销", "tagColor": "#FF4D4F" } ],
    "merchant": { "id": 5, "name": "XX旗舰店", "logo": "...", "brief": "..." },
    "reviews": [ { "id": 1, "rating": 5, "content": "...", "username": "..." } ],
    "isFavorited": true,
    "bottomBar": { "price": 99.00, "originalPrice": 199.00, "discount": "...", "skuCount": 3 }
  }
}
```

### 2.2 获取商品评价列表

- **GET** `/product/{id}/reviews?userId=1&page=1&pageSize=10`

### 2.3 收藏/取消收藏（切换）

- **POST** `/product/favorite/toggle?userId=1&productId=101`

响应：`{ "code": 200, "favorited": true }`（切换后状态）

### 2.4 获取用户收藏列表

- **GET** `/product/favorite/list?userId=1&page=1&pageSize=10`

### 2.5 获取用户收藏数量

- **GET** `/product/favorite/count?userId=1`

响应：`{ "code": 200, "count": 15 }`

---

## 3. 商品详情模块 `/api/product`

### 3.1 获取商品详情（完整版）

- **GET** `/api/product/detail?productId=179&userId=1`

### 3.2 收藏/取消收藏

- **POST** `/api/product/favorite?userId=1&productId=179`

### 3.3 获取底部价格栏信息

- **GET** `/api/product/bottom-bar?productId=179&userId=1`

响应：
```json
{
  "code": 200,
  "data": {
    "price": 99.00,
    "originalPrice": 199.00,
    "coupon": { "name": "满199减30", "discountValue": 30 },
    "isFavorited": true,
    "skuCount": 3
  }
}
```

---

## 4. 分类模块 `/category`

### 4.1 获取一级分类列表

- **GET** `/category/list`

响应：
```json
{
  "code": 200,
  "data": [ { "id": 1, "name": "手机通讯", "icon": "...", "sort": 1 } ]
}
```

### 4.2 获取子分类

- **GET** `/category/{id}/children`

路径参数：`id` = 父分类 ID

### 4.3 获取一级分类下商品

- **GET** `/category/{id}/products?limit=20`

### 4.4 获取二级分类下商品

- **GET** `/category/sub/{id}/products?limit=8`

### 4.5 获取二级分类下的三级分类

- **GET** `/category/level3/{id}`

### 4.6 根据二级分类获取商品

- **GET** `/category/level2/{id}/products?limit=8`

### 4.7 根据三级分类获取商品

- **GET** `/category/level3/{id}/products?limit=8`

### 4.8 获取一级+二级分类组合

- **GET** `/category/level1-level2`

每个一级分类只返回一个代表性的二级分类。

---

## 5. 购物车模块 `/cart`

### 5.1 获取购物车列表

- **GET** `/cart/list?userId=1`

响应：
```json
{
  "code": 200,
  "data": [
    { "id": 1, "userId": 1, "productId": 101, "productName": "...", "productImage": "...", "price": 99.00, "quantity": 2, "selected": 1 }
  ]
}
```

### 5.2 添加到购物车

- **POST** `/cart/add`

请求体：
```json
{
  "userId": 1,
  "productId": 101,
  "productName": "string",
  "productImage": "string",
  "price": 99.00,
  "quantity": 1
}
```

### 5.3 更新商品数量

- **POST** `/cart/update`

请求体：`{ "userId": 1, "cartId": 1, "quantity": 3 }`

### 5.4 更新选中状态

- **POST** `/cart/select`

请求体：`{ "userId": 1, "cartId": 1, "selected": 1 }` （1=选中，0=未选中）

### 5.5 全选/取消全选

- **POST** `/cart/selectAll`

请求体：`{ "userId": 1, "selected": 1 }`

### 5.6 删除购物车项

- **POST** `/cart/remove`

请求体：`{ "userId": 1, "cartId": 1 }`

### 5.7 清空购物车

- **POST** `/cart/clear`

请求体：`{ "userId": 1 }`

### 5.8 获取购物车数量

- **GET** `/cart/count?userId=1`

响应：`{ "code": 200, "data": 5 }`

---

## 6. 订单模块 `/api/order`

### 6.1 获取用户地址列表

- **GET** `/api/order/addresses?userId=1`

### 6.2 获取商品规格 SKU 列表

- **GET** `/api/order/product/skus?productId=179`

响应：
```json
{
  "code": 200,
  "data": {
    "skus": [ { "id": 1, "skuCode": "...", "price": 99.00, "originalPrice": 199.00, "stock": 50, "attributes": "{\"颜色\":\"黑色\",\"尺寸\":\"L\"}" } ],
    "attributeGroups": [ { "attrName": "颜色", "values": ["黑色", "白色"] } ]
  }
}
```

### 6.3 获取可用优惠券

- **GET** `/api/order/coupons?userId=1&amount=199.00`

响应：
```json
{
  "code": 200,
  "data": [
    {
      "userCouponId": 3, "couponId": 1, "name": "满199减30", "type": 1,
      "discountValue": 30.00, "minAmount": 199.00,
      "startTime": "2026-01-01 00:00:00", "endTime": "2026-12-31 23:59:59"
    }
  ]
}
```

### 6.4 计算订单价格

- **POST** `/api/order/calculate`

请求体：
```json
{
  "items": [
    { "skuId": 1, "quantity": 2 }
  ],
  "couponId": 3
}
```

响应：
```json
{
  "code": 200,
  "data": {
    "totalPrice": 198.00,
    "discountAmount": 100.00,
    "couponAmount": 30.00,
    "payAmount": 68.00
  }
}
```

### 6.5 创建订单

- **POST** `/api/order/create`

请求体：
```json
{
  "userId": 1,
  "addressId": 2,
  "items": [ { "skuId": 1, "quantity": 2 } ],
  "couponId": 3
}
```

响应：
```json
{
  "code": 200,
  "message": "下单成功",
  "data": {
    "orderId": 1001,
    "orderNo": "TS202606290001",
    "totalAmount": 198.00,
    "discountAmount": 100.00,
    "couponAmount": 30.00,
    "payAmount": 68.00
  }
}
```

### 6.6 获取订单详情

- **GET** `/api/order/detail?orderId=1001`

响应（含地址、订单项、商家信息）：
```json
{
  "code": 200,
  "data": {
    "order": { "id": 1001, "orderNo": "...", "status": 1, ... },
    "address": { "id": 2, "receiverName": "张三", "receiverPhone": "...", "province": "...", "city": "...", "detailAddress": "..." },
    "items": [
      { "id": 1, "productId": 101, "skuId": 1, "productName": "...", "skuAttributes": "...", "price": 99.00, "quantity": 2, "imageUrl": "..." }
    ]
  }
}
```

### 6.7 获取用户订单列表

- **GET** `/api/order/list?userId=1`

响应：每个订单包含 items 和 merchant（商家信息）。

### 6.8 更新订单状态

- **PUT** `/api/order/status?orderId=1001&status=2`

状态值：`0=待付款` `1=已付款` `2=已发货` `3=已收货` `4=已取消` `5=已退款`

### 6.9 订单发货

- **POST** `/api/order/ship`

请求体：
```json
{
  "orderId": 1001,
  "shipCompany": "顺丰速运",
  "shipNo": "SF1234567890",
  "shipRemark": "string"
}
```

---

## 7. 支付模块 `/api/payment`

### 7.1 创建支付

- **POST** `/api/payment/create`

请求体：
```json
{ "orderId": 1001, "userId": 1, "payType": 1 }
```

payType：`1=支付宝` `2=微信支付`

### 7.2 查询支付状态

- **GET** `/api/payment/check?orderId=1001`

---

## 8. 地址模块 `/api/address`

### 8.1 获取地址列表

- **GET** `/api/address/list?userId=1`

### 8.2 获取地址详情

- **GET** `/api/address/{id}`

### 8.3 新增地址

- **POST** `/api/address/create`

请求体：
```json
{
  "userId": 1,
  "receiver_name": "张三",
  "receiver_phone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detail_address": "建国门外大街1号",
  "is_default": 1
}
```

### 8.4 修改地址

- **POST** `/api/address/update`

请求体（字段同创建，加 `id` 字段，只传需要修改的字段即可）。

### 8.5 删除地址（软删除）

- **POST** `/api/address/delete/{id}`

### 8.6 设为默认地址

- **POST** `/api/address/set_default/{id}?userId=1`

---

## 9. 优惠券模块 `/coupon`

### 9.1 获取可领取优惠券列表

- **GET** `/coupon/available`

### 9.2 获取用户已领优惠券

- **GET** `/coupon/user/{userId}`

### 9.3 领取优惠券

- **POST** `/coupon/receive?userId=1&couponId=1`

---

## 10. 消息模块 `/api/messages`

### 10.1 获取消息分类概览

- **GET** `/api/messages/summary?userId=1`

响应：
```json
{
  "code": 200,
  "data": [
    { "type": "ORDER", "label": "订单通知", "unreadCount": 2 },
    { "type": "PROMOTION", "label": "促销活动", "unreadCount": 0 }
  ]
}
```

type 可选值：`ORDER` `PROMOTION` `SYSTEM` `COUPON`

### 10.2 获取总未读数

- **GET** `/api/messages/unread-count?userId=1`

响应：`{ "code": 200, "data": { "total": 5 } }`

### 10.3 获取某分类消息列表

- **GET** `/api/messages/{type}?userId=1&page=1&pageSize=20`

### 10.4 标记单条消息已读

- **PUT** `/api/messages/{id}/read?userId=1`

### 10.5 标记某分类全部已读

- **PUT** `/api/messages/{type}/read-all?userId=1`

### 10.6 标记所有消息已读

- **PUT** `/api/messages/read-all?userId=1`

### 10.7 删除单条消息

- **DELETE** `/api/messages/{id}?userId=1`

### 10.8 批量删除某类型消息

- **DELETE** `/api/messages/type/{type}?userId=1`

响应：`{ "code": 200, "data": { "count": 5 } }`

---

## 11. 搜索模块 `/search`

### 11.1 搜索商品

- **GET** `/search/products?keyword=手机&categoryId=&page=1&pageSize=10&sortBy=sales&sortOrder=desc&minPrice=&maxPrice=`

| 参数 | 说明 |
|------|------|
| keyword | 搜索关键词（必填） |
| categoryId | 分类 ID（可选） |
| page | 页码，默认 1 |
| pageSize | 每页数量，默认 10 |
| sortBy | 排序：sales / price / time |
| sortOrder | desc 降序 / asc 升序 |
| minPrice / maxPrice | 价格范围筛选（可选） |

响应：
```json
{
  "code": 200,
  "data": {
    "total": 150, "page": 1, "pageSize": 10,
    "list": [ { "id": 101, "productName": "...", "price": 99.00, "mainImage": "...", "sales": 1000 } ]
  }
}
```

### 11.2 搜索建议

- **GET** `/search/suggest?keyword=手机&limit=5`

### 11.3 获取搜索历史

- **GET** `/search/history?userId=1`

### 11.4 保存搜索历史

- **POST** `/search/history?userId=1&keyword=手机`

### 11.5 清空搜索历史

- **DELETE** `/search/history?userId=1`

### 11.6 获取热搜关键词

- **GET** `/search/hot?limit=10`

响应：`{ "code": 200, "data": ["手机", "耳机", "口红"] }`

---

## 12. 推荐模块 `/recommend`

### 12.1 获取个性化推荐

- **GET** `/recommend/products?userId=1&limit=10`

### 12.2 获取热销商品（含详情）

- **GET** `/recommend/hot?limit=8`

响应（比普通列表多 carouselImages、detailImages、details、tags）。

### 12.3 热销商品（带优惠券+分页）

- **GET** `/recommend/hot-with-coupon?userId=1&page=1`

响应：
```json
{
  "code": 200, "page": 1, "pageSize": 10, "hasMore": true, "preloadedPages": [1, 2]
}
```

### 12.4 获取新品上架

- **GET** `/recommend/new?limit=10`

### 12.5 获取高评分商品

- **GET** `/recommend/top-rated?limit=10`

### 12.6 按分类推荐

- **GET** `/recommend/category/{categoryId}?limit=10`

### 12.7 随机推荐

- **GET** `/recommend/random?limit=10`

### 12.8 记录用户浏览（推荐算法用）

- **POST** `/recommend/record-view?userId=1&productId=101`

响应：`{ "code": 200, "message": "记录成功" }`

---

## 13. 商家模块 `/merchant`

### 13.1 获取商家详情

- **GET** `/merchant/detail/{id}`

响应：
```json
{
  "code": 200,
  "data": {
    "id": 5, "merchantName": "XX旗舰店", "merchantLogo": "...",
    "merchantBrief": "...", "rating": 4.9, "sales": 10000
  }
}
```

### 13.2 获取商家商品列表

- **GET** `/merchant/{id}/products?limit=20`

### 13.3 获取商家列表

- **GET** `/merchant/list?limit=10`

---

## 14. 店铺品牌模块 `/api`

### 14.1 获取店铺信息

- **GET** `/api/shop/{shopId}`

响应：
```json
{
  "code": 200,
  "data": { "id": 5, "shopName": "XX旗舰店", "shopLogo": "...", "shopBrief": "...", "rating": 4.9, "sales": 10000, "categoryCount": 5, "productCount": 120 }
}
```

### 14.2 获取品牌信息（含富文本介绍）

- **GET** `/api/brand/{brandId}`

响应：
```json
{
  "code": 200,
  "data": {
    "id": 1, "brandName": "XX品牌", "brandLogo": "...", "brandStory": "<p>富文本 HTML</p>",
    "featureItems": [ { "icon": "...", "title": "核心科技", "description": "...", "color": "#FF4D4F" } ],
    "relatedProducts": [ { "id": 101, "productName": "...", "price": 99.00 } ]
  }
}
```

---

## 15. 客服模块 `/customer-service`

### 15.1 获取店铺信息

- **GET** `/customer-service/shop/{shopId}`

### 15.2 获取商品信息（客服用）

- **GET** `/customer-service/product/{productId}`

### 15.3 获取聊天消息（用户→店铺）

- **GET** `/customer-service/messages?shopId=5&userId=1&page=1&pageSize=20`

### 15.4 发送消息（用户）

- **POST** `/customer-service/messages`

请求体：
```json
{
  "userId": 1, "shopId": 5, "productId": 101,
  "content": "这个商品有货吗？", "messageType": "TEXT"
}
```

messageType：`TEXT` `IMAGE` `PRODUCT`

### 15.5 获取店铺快捷回复

- **GET** `/customer-service/quick-replies/{shopId}`

### 15.6 获取用户会话列表

- **GET** `/customer-service/conversations?userId=1`

### 15.7 获取用户未读消息数

- **GET** `/customer-service/unread-count?userId=1`

响应：
```json
{
  "code": 200,
  "data": {
    "total": 5,
    "shops": [ { "shopId": 5, "shopName": "...", "unreadCount": 3 } ]
  }
}
```

### 15.8 标记某店铺消息已读

- **POST** `/customer-service/mark-read`

请求体：`{ "userId": 1, "shopId": 5 }`

### 15.9 删除单条消息

- **DELETE** `/customer-service/messages/{messageId}?userId=1`

### 15.10 清空与某店铺聊天记录

- **DELETE** `/customer-service/messages/clear?userId=1&shopId=5`

### 15.11 商家端 - 发送消息

- **POST** `/customer-service/merchant/messages`

请求体：`{ "shopId": 5, "userId": 1, "content": "...", "messageType": "TEXT" }`

### 15.12 商家端 - 会话列表

- **GET** `/customer-service/merchant/conversations?shopId=5`

### 15.13 商家端 - 未读消息数

- **GET** `/customer-service/merchant/unread-count?shopId=5`

响应：
```json
{ "code": 200, "data": { "total": 8, "users": [ { "userId": 1, "username": "...", "unreadCount": 3 } ] } }
```

### 15.14 商家端 - 标记已读

- **POST** `/customer-service/merchant/mark-read`

请求体：`{ "shopId": 5, "userId": 1 }`

### 15.15 商家端 - 获取快捷回复

- **GET** `/customer-service/merchant/quick-replies?shopId=5`

### 15.16 商家端 - 新增快捷回复

- **POST** `/customer-service/merchant/quick-replies`

请求体：`{ "shopId": 5, "content": "亲，有什么可以帮您？", "sort": 1 }`

### 15.17 商家端 - 删除快捷回复

- **DELETE** `/customer-service/merchant/quick-replies/{id}?shopId=5`

---

## 16. 历史浏览模块 `/api/history`

### 16.1 同步浏览历史（前端批量上报）

- **POST** `/api/history/sync`

请求体：
```json
{
  "records": [
    { "productId": 101, "productName": "...", "price": 99.00, "originalPrice": 199.00, "mainImage": "...", "browseTime": 1719999999, "browseCount": 3 }
  ]
}
```

响应：`{ "code": 200, "data": { "syncedCount": 3, "totalCount": 15 } }`

### 16.2 获取浏览历史列表

- **GET** `/api/history/list?page=1&size=20`

响应：
```json
{
  "code": 200,
  "data": {
    "total": 15, "page": 1, "size": 20,
    "list": [ { "id": 1, "productId": 101, "productName": "...", "price": 99.00, "mainImage": "...", "browseCount": 3, "browseTime": "...", "product": { "id": 101, "productName": "...", "price": 99.00, "sales": 1000, "mainImage": "..." } } ]
  }
}
```

### 16.3 删除单条历史

- **DELETE** `/api/history/{id}`

### 16.4 清空全部历史

- **DELETE** `/api/history/clear`

响应：`{ "code": 200, "data": { "deletedCount": 15 } }`

---

## 17. 商品属性模块 `/api/product/attributes`

### 17.1 获取商品属性选项（SKU 维度）

- **GET** `/api/product/attributes/options?productId=179`

### 17.2 按类型获取属性维度

- **GET** `/api/product/attributes/dimensions?productType=default`

### 17.3 计算折扣信息

- **GET** `/api/product/attributes/discount?productId=179&skuId=1&userId=1`

### 17.4 获取所有商品类型

- **GET** `/api/product/attributes/types`

响应：`{ "code": 200, "data": ["default", "phone", "beauty"] }`

---

## 18. AI 图片生成模块 `/image`

### 18.1 文生图（普通）

- **POST** `/image/generimage`

请求体：
```json
{
  "prompt": "一只可爱的熊猫，高清",
  "negativePrompt": "low quality, blurry",
  "steps": 20,
  "width": 512,
  "height": 512,
  "cfgScale": 7.0,
  "samplerName": "Euler",
  "seed": -1
}
```

响应：
```json
{
  "code": 200,
  "message": "生成成功",
  "data": { "image": "data:image/png;base64,iVBOR...", "prompt": "..." }
}
```

### 18.2 文生图（SSE 流式）

- **POST** `/image/generimage/stream`
- **Content-Type**: `text/event-stream`

前端使用 EventSource 订阅，事件类型：`start` → `progress`（多次） → `complete` / `error`

进度事件示例：
```json
{ "type": "progress", "percent": 45.2, "info": "{...}" }
```

完成事件示例：
```json
{ "type": "complete", "image": "data:image/png;base64,...", "prompt": "..." }
```

### 18.3 检查 SD WebUI 状态

- **GET** `/image/status`

响应：`{ "code": 200, "data": { "available": true } }`

---

## 附录：订单状态码

| 状态 | 含义 |
|------|------|
| 0 | 待付款 |
| 1 | 已付款 |
| 2 | 已发货 |
| 3 | 已收货 |
| 4 | 已取消 |
| 5 | 已退款 |

## 附录：支付类型

| 值 | 含义 |
|----|------|
| 1 | 支付宝 |
| 2 | 微信支付 |

## 附录：优惠券类型

| 值 | 含义 |
|----|------|
| 1 | 满减券 |
| 2 | 折扣券 |

## 附录：消息分类 type

| 值 | 含义 |
|----|------|
| ORDER | 订单通知 |
| PROMOTION | 促销活动 |
| SYSTEM | 系统消息 |
| COUPON | 优惠券消息 |
