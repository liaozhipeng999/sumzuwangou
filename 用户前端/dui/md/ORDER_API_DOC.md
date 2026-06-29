# 订单系统 API 文档

## 概述
本文档描述了订单相关的所有API接口，包括地址管理、商品规格查询、优惠券获取、价格计算、订单创建等功能。

**基础路径**: `/api/order`  
**响应格式**: JSON  
**通用响应结构**:
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**响应码说明**:
- `200`: 成功
- `400`: 请求参数错误（如库存不足、SKU不存在等）
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 1. 获取用户地址列表

### 接口信息
- **URL**: `GET /api/order/addresses`
- **描述**: 获取指定用户的收货地址列表
- **认证**: 需要userId参数

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

### 请求示例
```
GET /api/order/addresses?userId=1
```

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "receiverName": "廖智鹏",
      "receiverPhone": "13680835826",
      "province": "北京市",
      "city": "北京市",
      "district": "东城区",
      "detailAddress": "白花镇XX路XX号",
      "isDefault": 1,
      "createdAt": "2026-06-20T10:00:00",
      "updatedAt": "2026-06-20T10:00:00"
    }
  ]
}
```

### 字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 地址ID |
| userId | Long | 用户ID |
| receiverName | String | 收货人姓名 |
| receiverPhone | String | 收货人电话 |
| province | String | 省份 |
| city | String | 城市 |
| district | String | 区县 |
| detailAddress | String | 详细地址 |
| isDefault | Integer | 是否默认地址：0-否，1-是 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

## 2. 获取商品规格列表 ⭐重要

### 接口信息
- **URL**: `GET /api/order/product/skus`
- **描述**: 获取指定商品的SKU列表和属性分组信息
- **认证**: 需要productId参数

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品ID |

### 请求示例
```
GET /api/order/product/skus?productId=10913
```

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "skus": [
      {
        "id": 4808,
        "productId": 10913,
        "skuCode": "SKU10913_4808",
        "attributes": "{\"颜色\":\"黑色\",\"尺寸\":\"大号\"}",
        "price": 3603.24,
        "originalPrice": 4200.00,
        "stock": 229,
        "sales": 150,
        "status": 1,
        "createdAt": "2026-06-20T10:00:00",
        "updatedAt": "2026-06-25T10:00:00"
      },
      {
        "id": 4809,
        "productId": 10913,
        "skuCode": "SKU10913_4809",
        "attributes": "{\"颜色\":\"白色\",\"尺寸\":\"中号\"}",
        "price": 2565.30,
        "originalPrice": 3000.00,
        "stock": 85,
        "sales": 80,
        "status": 1,
        "createdAt": "2026-06-20T10:00:00",
        "updatedAt": "2026-06-25T10:00:00"
      }
    ],
    "attributeGroups": [
      {
        "attributeName": "颜色",
        "values": ["黑色", "白色", "红色"]
      },
      {
        "attributeName": "尺寸",
        "values": ["小号", "中号", "大号"]
      }
    ]
  }
}
```

### 字段说明

#### skus 数组
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | **SKU ID（下单时使用此ID）** |
| productId | Long | 商品ID |
| skuCode | String | SKU编码 |
| attributes | String | SKU属性JSON字符串 |
| price | BigDecimal | 当前售价 |
| originalPrice | BigDecimal | 原价 |
| stock | Integer | 库存数量 |
| sales | Integer | 销量 |
| status | Integer | 状态：1-启用，0-禁用 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

#### attributeGroups 数组
| 字段 | 类型 | 说明 |
|------|------|------|
| attributeName | String | 属性名称（如"颜色"） |
| values | String[] | 属性值数组（如["黑色", "白色"]） |

### ⚠️ 重要提示
**这是前端最容易出错的地方！**

- ❌ **错误做法**: 使用 `productId` 作为下单时的 `skuId`
- ✅ **正确做法**: 
  1. 先调用此接口获取该商品的SKU列表
  2. 从返回的 `skus` 数组中获取具体的 `id` 字段
  3. 使用这个 `id` 作为下单时的 `skuId`

**示例**:
```javascript
// 商品ID是 10913
const productId = 10913;

// 获取SKU列表
const response = await fetch(`/api/order/product/skus?productId=${productId}`);
const data = await response.json();

// 选择第一个SKU（或根据用户选择的属性匹配）
const selectedSku = data.data.skus[0];
const skuId = selectedSku.id; // 例如: 4808

// 下单时使用 skuId，而不是 productId
await createOrder({
  skuId: skuId,  // ✅ 正确：4808
  quantity: 1
});
```

---

## 3. 获取可用优惠券

### 接口信息
- **URL**: `GET /api/order/coupons`
- **描述**: 获取用户可用的优惠券列表
- **认证**: 需要userId参数

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| amount | Number | 否 | 订单金额（用于过滤满足最低消费金额的优惠券） |

### 请求示例
```
GET /api/order/coupons?userId=1&amount=100
```

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "userCouponId": 1,
      "couponId": 10,
      "name": "满100减20优惠券",
      "type": 1,
      "discountValue": 20.00,
      "minAmount": 100.00,
      "maxDiscount": 20.00,
      "startTime": "2026-06-01T00:00:00",
      "endTime": "2026-06-30T23:59:59"
    },
    {
      "userCouponId": 2,
      "couponId": 11,
      "name": "95折优惠券",
      "type": 2,
      "discountValue": 0.95,
      "minAmount": 50.00,
      "maxDiscount": 50.00,
      "startTime": "2026-06-01T00:00:00",
      "endTime": "2026-06-30T23:59:59"
    }
  ]
}
```

### 字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| userCouponId | Long | 用户优惠券ID（下单时传入此ID） |
| couponId | Long | 优惠券模板ID |
| name | String | 优惠券名称 |
| type | Integer | 优惠券类型：1-满减，2-折扣 |
| discountValue | BigDecimal | 优惠值（满减时为金额，折扣时为折扣率如0.95） |
| minAmount | BigDecimal | 最低消费金额 |
| maxDiscount | BigDecimal | 最大优惠金额 |
| startTime | LocalDateTime | 生效开始时间 |
| endTime | LocalDateTime | 生效结束时间 |

---

## 4. 计算订单价格

### 接口信息
- **URL**: `POST /api/order/calculate`
- **描述**: 在提交订单前计算总价、优惠金额和实付金额
- **Content-Type**: application/json

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| items | Array | 是 | 商品列表 |
| items[].skuId | Long | 是 | SKU ID |
| items[].quantity | Integer | 是 | 购买数量 |
| couponId | Long | 否 | 使用的优惠券ID（userCouponId） |

### 请求示例
```json
{
  "items": [
    {
      "skuId": 4808,
      "quantity": 2
    },
    {
      "skuId": 4809,
      "quantity": 1
    }
  ],
  "couponId": 1
}
```

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalPrice": 9771.78,
    "discountAmount": 1399.80,
    "couponAmount": 20.00,
    "payAmount": 8351.98
  }
}
```

### 字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| totalPrice | BigDecimal | 商品总价（原价总和） |
| discountAmount | BigDecimal | 商品折扣金额（原价-现价的差额） |
| couponAmount | BigDecimal | 优惠券减免金额 |
| payAmount | BigDecimal | 实付金额（totalPrice - discountAmount - couponAmount） |

### 计算公式
```
实付金额 = 商品总价 - 商品折扣金额 - 优惠券金额
```

---

## 5. 创建订单 ⭐核心接口

### 接口信息
- **URL**: `POST /api/order/create`
- **描述**: 创建新订单，会扣减库存、生成订单号和订单项
- **Content-Type**: application/json
- **事务**: 此接口使用数据库事务，失败会自动回滚

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| addressId | Long | 是 | 收货地址ID |
| items | Array | 是 | 商品列表 |
| items[].skuId | Long | 是 | **SKU ID（不是商品ID！）** |
| items[].quantity | Integer | 是 | 购买数量 |
| couponId | Long | 否 | 使用的优惠券ID（userCouponId） |

### 请求示例
```json
{
  "userId": 1,
  "addressId": 1,
  "items": [
    {
      "skuId": 4808,
      "quantity": 2
    },
    {
      "skuId": 4809,
      "quantity": 1
    }
  ],
  "couponId": 1
}
```

### 响应示例

#### 成功响应
```json
{
  "code": 200,
  "message": "下单成功",
  "data": {
    "orderId": 4,
    "orderNo": "ORD20260625120000ABC123",
    "totalAmount": 9771.78,
    "discountAmount": 1399.80,
    "couponAmount": 20.00,
    "payAmount": 8351.98
  }
}
```

#### 失败响应 - SKU不存在
```json
{
  "code": 400,
  "message": "错误的SKU ID：10913 是商品ID而非SKU ID。该商品的可用SKU ID包括：4808, 4809, 4810, 4811, 4812 等"
}
```

#### 失败响应 - 库存不足
```json
{
  "code": 400,
  "message": "库存不足，当前库存：5，需要数量：10"
}
```

### 字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| orderId | Long | 订单ID |
| orderNo | String | 订单编号（唯一标识） |
| totalAmount | BigDecimal | 商品总价 |
| discountAmount | BigDecimal | 商品折扣金额 |
| couponAmount | BigDecimal | 优惠券减免金额 |
| payAmount | BigDecimal | 实付金额 |

### ⚠️ 常见错误及解决方案

#### 错误1: "错误的SKU ID：XXX 是商品ID而非SKU ID"
**原因**: 前端传递的是 `productId` 而不是 `skuId`

**解决方案**:
```javascript
// ❌ 错误
const orderData = {
  userId: 1,
  addressId: 1,
  items: [
    { skuId: 10913, quantity: 1 }  // 10913是商品ID
  ]
};

// ✅ 正确
// 1. 先获取SKU列表
const skuResponse = await fetch('/api/order/product/skus?productId=10913');
const skuData = await skuResponse.json();

// 2. 选择具体的SKU
const selectedSku = skuData.data.skus[0]; // 或根据用户选择匹配

// 3. 使用SKU的id字段
const orderData = {
  userId: 1,
  addressId: 1,
  items: [
    { skuId: selectedSku.id, quantity: 1 }  // 例如: 4808
  ]
};
```

#### 错误2: "库存不足，当前库存：X，需要数量：Y"
**原因**: 商品库存不足

**解决方案**:
- 提示用户减少购买数量
- 或选择其他有库存的SKU

#### 错误3: "商品规格不存在（SKU ID: XXX）"
**原因**: SKU ID在数据库中完全不存在

**解决方案**:
- 检查SKU ID是否正确
- 重新获取商品SKU列表

---

## 6. 获取订单详情

### 接口信息
- **URL**: `GET /api/order/detail`
- **描述**: 获取指定订单的详细信息，包括订单信息、收货地址、商品列表
- **认证**: 需要orderId参数

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |

### 请求示例
```
GET /api/order/detail?orderId=4
```

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "order": {
      "id": 4,
      "orderNo": "ORD20260625120000ABC123",
      "userId": 1,
      "addressId": 1,
      "totalAmount": 9771.78,
      "discountAmount": 1399.80,
      "couponAmount": 20.00,
      "payAmount": 8351.98,
      "status": 0,
      "payTime": null,
      "shipTime": null,
      "receiveTime": null,
      "cancelTime": null,
      "createdAt": "2026-06-25T12:00:00",
      "updatedAt": "2026-06-25T12:00:00"
    },
    "address": {
      "id": 1,
      "userId": 1,
      "receiverName": "廖智鹏",
      "receiverPhone": "13680835826",
      "province": "北京市",
      "city": "北京市",
      "district": "东城区",
      "detailAddress": "白花镇XX路XX号",
      "isDefault": 1
    },
    "items": [
      {
        "id": 1,
        "productId": 10913,
        "skuId": 4808,
        "productName": "【品牌直营】手机支架 车载款 金属机身 超清三摄 品质保证 7天无理由",
        "skuAttributes": "{\"颜色\":\"黑色\",\"尺寸\":\"大号\"}",
        "price": 3603.24,
        "originalPrice": 4200.00,
        "quantity": 2,
        "imageUrl": "https://example.com/image1.jpg"
      },
      {
        "id": 2,
        "productId": 10913,
        "skuId": 4809,
        "productName": "【品牌直营】手机支架 车载款 金属机身 超清三摄 品质保证 7天无理由",
        "skuAttributes": "{\"颜色\":\"白色\",\"尺寸\":\"中号\"}",
        "price": 2565.30,
        "originalPrice": 3000.00,
        "quantity": 1,
        "imageUrl": "https://example.com/image2.jpg"
      }
    ]
  }
}
```

### 字段说明

#### order 对象
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单ID |
| orderNo | String | 订单编号 |
| userId | Long | 用户ID |
| addressId | Long | 收货地址ID |
| totalAmount | BigDecimal | 商品总价 |
| discountAmount | BigDecimal | 商品折扣金额 |
| couponAmount | BigDecimal | 优惠券减免金额 |
| payAmount | BigDecimal | 实付金额 |
| status | Integer | 订单状态（见下方状态说明） |
| payTime | LocalDateTime | 支付时间 |
| shipTime | LocalDateTime | 发货时间 |
| receiveTime | LocalDateTime | 收货时间 |
| cancelTime | LocalDateTime | 取消时间 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

#### address 对象
同"获取用户地址列表"接口中的地址对象

#### items 数组
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单项ID |
| productId | Long | 商品ID |
| skuId | Long | SKU ID |
| productName | String | 商品名称 |
| skuAttributes | String | SKU属性JSON字符串 |
| price | BigDecimal | 成交价 |
| originalPrice | BigDecimal | 原价 |
| quantity | Integer | 购买数量 |
| imageUrl | String | 商品图片URL |

### 订单状态说明
| 状态值 | 说明 |
|--------|------|
| 0 | 待付款 |
| 1 | 已支付/待发货 |
| 2 | 已发货/待收货 |
| 3 | 已完成 |
| 4 | 已取消 |

---

## 7. 获取用户订单列表

### 接口信息
- **URL**: `GET /api/order/list`
- **描述**: 获取指定用户的所有订单列表
- **认证**: 需要userId参数

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

### 请求示例
```
GET /api/order/list?userId=1
```

### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 4,
      "orderNo": "ORD20260625120000ABC123",
      "userId": 1,
      "addressId": 1,
      "totalAmount": 9771.78,
      "discountAmount": 1399.80,
      "couponAmount": 20.00,
      "payAmount": 8351.98,
      "status": 0,
      "payTime": null,
      "shipTime": null,
      "receiveTime": null,
      "cancelTime": null,
      "createdAt": "2026-06-25T12:00:00",
      "updatedAt": "2026-06-25T12:00:00"
    },
    {
      "id": 3,
      "orderNo": "ORD20260616234357C863AF43",
      "userId": 1,
      "addressId": 1,
      "totalAmount": 6999.00,
      "discountAmount": 1399.80,
      "couponAmount": 0.00,
      "payAmount": 5599.20,
      "status": 1,
      "payTime": "2026-06-16T23:43:57",
      "shipTime": null,
      "receiveTime": null,
      "cancelTime": null,
      "createdAt": "2026-06-16T23:43:57",
      "updatedAt": "2026-06-16T23:43:57"
    }
  ]
}
```

### 字段说明
同"获取订单详情"接口中的order对象

### ⚠️ 注意事项
- 订单列表按创建时间倒序排列（最新的在前）
- 如果返回空数组 `[]`，表示该用户没有订单
- 确保传入的 `userId` 正确，否则会返回空列表

---

## 8. 更新订单状态

### 接口信息
- **URL**: `PUT /api/order/status`
- **描述**: 更新订单状态（通常由后台管理系统调用）
- **认证**: 需要orderId和status参数

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |
| status | Integer | 是 | 新的订单状态 |

### 请求示例
```
PUT /api/order/status?orderId=4&status=1
```

或使用表单方式：
```
Content-Type: application/x-www-form-urlencoded

orderId=4&status=1
```

### 响应示例
```json
{
  "code": 200,
  "message": "状态更新成功"
}
```

或失败时：
```json
{
  "code": 400,
  "message": "订单不存在"
}
```

### 订单状态流转
```
0(待付款) → 1(已支付) → 2(已发货) → 3(已完成)
                ↓
              4(已取消)
```

---

## 完整下单流程示例

以下是前端完整的下单流程代码示例：

```javascript
// 步骤1: 用户选择商品后，进入确认订单页面
async function initOrderPage(productId, selectedAttributes) {
  // 1. 获取商品SKU列表
  const skuResponse = await fetch(`/api/order/product/skus?productId=${productId}`);
  const skuData = await skuResponse.json();
  
  // 2. 根据用户选择的属性匹配对应的SKU
  const matchedSku = matchSkuByAttributes(skuData.data.skus, selectedAttributes);
  const skuId = matchedSku.id;
  
  // 3. 获取用户地址列表
  const addressResponse = await fetch(`/api/order/addresses?userId=${currentUserId}`);
  const addressData = await addressResponse.json();
  const addresses = addressData.data;
  
  // 4. 获取可用优惠券
  const couponResponse = await fetch(`/api/order/coupons?userId=${currentUserId}&amount=${matchedSku.price}`);
  const couponData = await couponResponse.json();
  const coupons = couponData.data;
  
  // 渲染页面...
}

// 步骤2: 用户选择地址和优惠券后，计算价格
async function calculateOrderPrice(skuId, quantity, couponId) {
  const response = await fetch('/api/order/calculate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      items: [
        { skuId: skuId, quantity: quantity }
      ],
      couponId: couponId
    })
  });
  
  const data = await response.json();
  if (data.code === 200) {
    // 显示价格信息
    displayPrice(data.data);
  }
}

// 步骤3: 用户点击"提交订单"按钮
async function submitOrder(addressId, skuId, quantity, couponId) {
  try {
    const response = await fetch('/api/order/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: currentUserId,
        addressId: addressId,
        items: [
          { skuId: skuId, quantity: quantity }
        ],
        couponId: couponId
      })
    });
    
    const data = await response.json();
    
    if (data.code === 200) {
      // 下单成功，跳转到支付页面
      redirectToPayment(data.data.orderId);
    } else {
      // 显示错误信息
      showError(data.message);
      
      // 如果是SKU错误，给出友好提示
      if (data.message.includes('商品ID而非SKU ID')) {
        showHelp('请选择正确的商品规格后重试');
      } else if (data.message.includes('库存不足')) {
        showHelp('商品库存不足，请减少购买数量或选择其他规格');
      }
    }
  } catch (error) {
    console.error('下单失败:', error);
    showError('网络错误，请稍后重试');
  }
}

// 辅助函数：根据属性匹配SKU
function matchSkuByAttributes(skus, attributes) {
  // 这里需要根据实际的属性匹配逻辑实现
  // 简化示例：返回第一个SKU
  return skus[0];
}

// 辅助函数：显示价格
function displayPrice(priceInfo) {
  document.getElementById('totalPrice').textContent = `¥${priceInfo.totalPrice.toFixed(2)}`;
  document.getElementById('discountAmount').textContent = `-¥${priceInfo.discountAmount.toFixed(2)}`;
  document.getElementById('couponAmount').textContent = `-¥${priceInfo.couponAmount.toFixed(2)}`;
  document.getElementById('payAmount').textContent = `¥${priceInfo.payAmount.toFixed(2)}`;
}

// 辅助函数：跳转到支付页面
function redirectToPayment(orderId) {
  window.location.href = `/payment?orderId=${orderId}`;
}

// 辅助函数：显示错误信息
function showError(message) {
  alert(message);
}

// 辅助函数：显示帮助信息
function showHelp(message) {
  console.log('帮助:', message);
}
```

---

## 注意事项

### 1. SKU ID vs Product ID ⭐⭐⭐
这是最重要的概念，请务必理解：

| 概念 | 说明 | 使用场景 |
|------|------|----------|
| **Product ID** | 商品ID，代表一个商品整体 | 购物车、商品列表、商品详情 |
| **SKU ID** | SKU ID，代表商品的具体规格 | 订单创建、库存管理 |

**示例**:
- 商品："iPhone 15" (productId: 100)
  - SKU 1: "iPhone 15 - 黑色 - 128GB" (skuId: 1001)
  - SKU 2: "iPhone 15 - 白色 - 256GB" (skuId: 1002)
  - SKU 3: "iPhone 15 - 蓝色 - 512GB" (skuId: 1003)

### 2. 价格精度
- 所有价格字段使用 `BigDecimal` 类型
- 前端展示时建议保留两位小数
- 计算时注意浮点数精度问题

### 3. 库存扣减
- 创建订单时会立即扣减库存
- 如果订单取消，需要手动恢复库存（目前未实现自动恢复）
- 建议在订单超时未支付时恢复库存

### 4. 优惠券使用
- 优惠券ID使用的是 `userCouponId`（用户领取的优惠券ID），不是 `couponId`（优惠券模板ID）
- 一张优惠券只能使用一次
- 使用后优惠券状态会变为"已使用"

### 5. 订单状态管理
- 订单创建后默认状态为 0（待付款）
- 支付成功后应调用更新状态接口将状态改为 1（已支付）
- 建议在支付回调中自动更新订单状态

### 6. 错误处理
- 所有接口都会返回统一的响应格式
- 当 `code !== 200` 时，请查看 `message` 字段
- 特别注意区分 `400`（业务错误）和 `500`（系统错误）

### 7. 性能优化建议
- 获取订单列表时考虑分页（目前未实现）
- 商品SKU列表可以缓存，减少数据库查询
- 价格计算可以在前端做初步计算，后端做最终校验

---

## 常见问题 FAQ

### Q1: 为什么下单时提示"SKU ID不存在"？
A: 最常见的原因是传入了 `productId` 而不是 `skuId`。请先调用 `/api/order/product/skus` 接口获取SKU列表，然后使用返回的 `id` 字段。

### Q2: 如何判断用户是否有可用优惠券？
A: 调用 `/api/order/coupons` 接口，传入 `userId` 和可选的 `amount` 参数。返回空数组表示没有可用优惠券。

### Q3: 订单创建后能否修改？
A: 不能。订单一旦创建就不能修改，如果需要修改，应该取消原订单并重新创建。

### Q4: 如何处理订单超时未支付的情况？
A: 建议在后端设置定时任务，定期检查超过一定时间（如30分钟）仍未支付的订单，将其状态改为"已取消"并恢复库存。

### Q5: 订单列表为空怎么办？
A: 检查以下几点：
1. 确认传入的 `userId` 是否正确
2. 确认数据库中该用户是否有订单记录
3. 检查后端日志是否有错误信息

### Q6: 如何实现订单筛选（如只看待付款订单）？
A: 目前接口返回所有订单，前端可以根据 `status` 字段进行过滤。后续可以考虑在后端添加状态筛选参数。

### Q7: 商品价格发生变化怎么办？
A: 订单创建时会记录当时的成交价格（`price` 和 `originalPrice`），即使后续商品调价，已创建的订单价格不会改变。

---

## 更新日志

- **2026-06-25**: 初始版本发布，包含8个订单相关接口
- **2026-06-25**: 优化SKU ID错误提示，提供更详细的调试信息
- **2026-06-25**: 添加完整的下单流程示例代码

---

## 技术支持

如有问题，请联系后端开发团队或查看以下资源：
- 购物车API文档: [CART_API_DOC.md](./CART_API_DOC.md)
- 后端服务地址: http://localhost:8080
- 数据库: MySQL (termshop)
