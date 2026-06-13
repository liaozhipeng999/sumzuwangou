# 商品推荐接口文档

## 服务地址
```
http://localhost:8080
```

---

## 一、推荐接口列表

| 接口 | 方法 | 说明 |
|------|------|------|
| `/recommend/products` | GET | 综合推荐（智能算法） |
| `/recommend/hot` | GET | 热销商品 |
| `/recommend/new` | GET | 新品推荐 |
| `/recommend/category/{categoryId}` | GET | 分类推荐 |
| `/recommend/random` | GET | 随机推荐 |
| `/recommend/record-view` | POST | 记录浏览 |

---

## 二、接口详情

### 2.1 综合推荐（核心接口）

**推荐算法**：智能混合推荐，包含以下权重：
- 热门标记商品 25%
- 新品商品 25%
- 用户个性化推荐 ~33%
- 热销商品（按销量）
- 随机商品（兜底）

**接口地址**：
```
GET /recommend/products?limit=10&userId=1
```

**参数**：
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| limit | Integer | 否 | 10 | 返回数量 |
| userId | Long | 否 | - | 用户ID（用于个性化推荐） |

**响应示例**：
```json
{
    "code": 200,
    "message": "success",
    "count": 5,
    "data": [
        {
            "id": 4,
            "productName": "华为Mate60 Pro",
            "price": 6999.00,
            "stock": 100,
            "sales": 50,
            "mainImage": "https://example.com/huawei.jpg",
            "brief": "华为旗舰手机",
            "isHot": 1,
            "isNew": 0
        }
    ]
}
```

---

### 2.2 热销商品

```
GET /recommend/hot?limit=10
```

**说明**：按销量排序的商品列表

---

### 2.3 新品推荐

```
GET /recommend/new?limit=10
```

**说明**：按上架时间排序的最新商品

---

### 2.4 分类推荐

```
GET /recommend/category/{categoryId}?limit=10
```

**路径参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | Long | 是 | 分类ID |

**示例**：`GET /recommend/category/1?limit=10`

---

### 2.5 随机推荐

```
GET /recommend/random?limit=10
```

**说明**：随机获取商品，用于推荐多样性

---

### 2.6 记录浏览

```
POST /recommend/record-view?productId=1&userId=1
```

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productId | Long | 是 | 商品ID |
| userId | Long | 否 | 用户ID |

**说明**：记录用户浏览行为，用于优化个性化推荐

---

## 三、商品数据结构

```javascript
{
    id: 4,                       // 商品ID
    productName: "华为Mate60 Pro", // 商品名称
    productCode: "SKU001",        // 商品编码
    categoryId: 1,                // 分类ID
    price: 6999.00,              // 售价
    originalPrice: 7999.00,       // 原价
    stock: 100,                   // 库存
    sales: 50,                    // 销量
    mainImage: "https://...",    // 主图URL
    brief: "华为旗舰手机",        // 简介
    status: 1,                    // 状态(1上架,0下架)
    isHot: 1,                    // 是否热门(1是,0否)
    isNew: 0,                    // 是否新品(1是,0否)
    createdAt: "2026-06-12T10:00:00"  // 创建时间
}
```

---

## 四、Redis缓存策略

| 缓存Key | 说明 | 过期时间 |
|---------|------|----------|
| `recommend:hot` | 热销商品列表 | 1小时 |
| `recommend:new` | 新品列表 | 1小时 |
| `recommend:category:{id}` | 分类商品 | 1小时 |
| `recommend:user:{id}` | 用户个性化推荐 | 1小时 |
| `user:view:{id}` | 用户浏览记录 | 7天 |

---

## 五、前端调用示例

### 获取综合推荐
```javascript
const res = await fetch('http://localhost:8080/recommend/products?limit=10');
const result = await res.json();
const products = result.data;
```

### 获取热销商品
```javascript
const res = await fetch('http://localhost:8080/recommend/hot?limit=10');
const result = await res.json();
```

### 记录用户浏览
```javascript
await fetch('http://localhost:8080/recommend/record-view?productId=1&userId=1', {
    method: 'POST'
});
```

---

## 六、状态码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 500 | 服务器错误 |