# 商品收藏接口 API 文档

---

## 一、功能概述

本接口提供商品收藏相关功能，包括：

- **切换式收藏**：点击收藏按钮自动切换状态（已收藏→取消，未收藏→收藏）
- **收藏列表**：获取用户收藏的商品列表（支持分页）
- **收藏数量**：获取用户收藏商品的总数
- **状态同步**：实时返回当前收藏状态

---

## 二、API 接口列表

| API 路径 | HTTP 方法 | 功能描述 |
| :--- | :--- | :--- |
| `POST /product/favorite/toggle` | POST | 收藏/取消收藏（切换接口） |
| `GET /product/favorite/list` | GET | 获取用户收藏列表（分页） |
| `GET /product/favorite/count` | GET | 获取用户收藏数量 |
| `GET /product/detail/{id}` | GET | 获取商品详情（含收藏状态） |
| `GET /api/product/bottom-bar` | GET | 获取底部价格栏（含收藏状态） |

---

## 三、接口详细说明

### 3.1 收藏/取消收藏（切换接口）

**接口地址**：`POST /product/favorite/toggle`

**功能描述**：点击收藏按钮时，如果已收藏则取消，如果未收藏则添加

**请求参数**（Query Parameters）：

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| userId | Long | 是 | 用户ID |
| productId | Long | 是 | 商品ID |

**请求示例**：

```javascript
// 收藏商品
fetch('/product/favorite/toggle?userId=1&productId=179', {
  method: 'POST'
})

// 取消收藏（再次调用同一接口）
fetch('/product/favorite/toggle?userId=1&productId=179', {
  method: 'POST'
})
```

**成功响应**（收藏成功）：

```json
{
  "code": 200,
  "message": "收藏成功",
  "isFavorited": true
}
```

**成功响应**（取消收藏成功）：

```json
{
  "code": 200,
  "message": "取消收藏成功",
  "isFavorited": false
}
```

**失败响应**（参数错误）：

```json
{
  "code": 400,
  "message": "参数错误",
  "isFavorited": false
}
```

---

### 3.2 获取用户收藏列表

**接口地址**：`GET /product/favorite/list`

**功能描述**：获取用户收藏的商品列表，支持分页

**请求参数**（Query Parameters）：

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| userId | Long | 是 | - | 用户ID |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**请求示例**：

```javascript
// 获取收藏列表（第一页，每页10条）
fetch('/product/favorite/list?userId=1&page=1&pageSize=10')

// 获取收藏列表（第二页）
fetch('/product/favorite/list?userId=1&page=2&pageSize=10')
```

**成功响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 25,
    "totalPages": 3,
    "page": 1,
    "pageSize": 10,
    "products": [
      {
        "id": 179,
        "productName": "维达纸巾",
        "productCode": "SKU7968",
        "categoryId": 72,
        "merchantId": 128,
        "price": 29.90,
        "originalPrice": 38.87,
        "stock": 224,
        "sales": 23400,
        "mainImage": "https://xxx.com/image.jpg",
        "brief": "优质维达纸巾",
        "favoritedAt": "2026-06-15 19:30:00"
      }
    ]
  }
}
```

**响应字段说明**：

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| total | Integer | 收藏商品总数 |
| totalPages | Integer | 总页数 |
| page | Integer | 当前页码 |
| pageSize | Integer | 每页数量 |
| products | Array | 商品列表 |
| products[].favoritedAt | String | 收藏时间 |

---

### 3.3 获取用户收藏数量

**接口地址**：`GET /product/favorite/count`

**功能描述**：获取用户收藏商品的总数

**请求参数**（Query Parameters）：

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| userId | Long | 是 | 用户ID |

**请求示例**：

```javascript
fetch('/product/favorite/count?userId=1')
```

**成功响应**：

```json
{
  "code": 200,
  "message": "success",
  "data": 25
}
```

---

### 3.4 获取商品详情（含收藏状态）

**接口地址**：`GET /product/detail/{id}`

**功能描述**：获取商品完整详情，包含收藏状态

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| id | Long | 是 | 商品ID（路径参数） |
| userId | Long | 否 | 用户ID（不传则不返回收藏状态） |

**请求示例**：

```javascript
// 获取商品详情（含收藏状态）
fetch('/product/detail/179?userId=1')

// 获取商品详情（不含收藏状态）
fetch('/product/detail/179')
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "product": {
      "id": 179,
      "productName": "维达纸巾",
      "price": 29.90,
      "originalPrice": 38.87,
      "mainImage": "https://xxx.com/image.jpg"
    },
    "carouselImages": [...],
    "merchant": {...},
    "brandInfo": {...},
    "groupInfo": {...},
    "reviews": [...],
    "detailImages": [...],
    "details": [...],
    "tags": [...],
    "isFavorited": true  // 当前用户是否已收藏
  }
}
```

---

### 3.5 获取底部价格栏（含收藏状态）

**接口地址**：`GET /api/product/bottom-bar`

**功能描述**：获取商品底部价格栏信息，包含收藏状态

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| productId | Long | 是 | 商品ID |
| userId | Long | 否 | 用户ID（不传则不返回收藏状态） |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productId": 179,
    "price": 29.90,
    "originalPrice": 38.87,
    "groupPrice": 27.90,
    "isFavorited": true,
    "stock": 224,
    "sales": 23400
  }
}
```

---

## 四、前端使用示例

### 4.1 Vue.js 收藏按钮示例

```vue
<template>
  <div class="product-detail">
    <!-- 收藏按钮 -->
    <button 
      class="favorite-btn" 
      :class="{ active: isFavorited }"
      @click="handleFavorite"
    >
      <span class="icon">{{ isFavorited ? '❤️' : '🤍' }}</span>
      <span class="text">{{ isFavorited ? '已收藏' : '收藏' }}</span>
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const isFavorited = ref(false);
const userId = ref(1); // 用户ID，从登录状态获取
const productId = ref(179); // 商品ID

// 点击收藏按钮
const handleFavorite = async () => {
  try {
    const response = await fetch(
      `/product/favorite/toggle?userId=${userId.value}&productId=${productId.value}`,
      { method: 'POST' }
    );
    const result = await response.json();
    
    if (result.code === 200) {
      isFavorited.value = result.isFavorited;
      alert(result.message);
    }
  } catch (error) {
    console.error('收藏操作失败:', error);
  }
};

// 初始化时获取收藏状态
const fetchFavoriteStatus = async () => {
  try {
    const response = await fetch(`/product/detail/${productId.value}?userId=${userId.value}`);
    const result = await response.json();
    
    if (result.code === 200) {
      isFavorited.value = result.data.isFavorited || false;
    }
  } catch (error) {
    console.error('获取收藏状态失败:', error);
  }
};

fetchFavoriteStatus();
</script>

<style scoped>
.favorite-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.favorite-btn:hover {
  border-color: #ff4d4f;
}

.favorite-btn.active {
  background: #fff1f0;
  border-color: #ff4d4f;
  color: #ff4d4f;
}
</style>
```

### 4.2 Vue.js 收藏列表页面示例

```vue
<template>
  <div class="favorite-page">
    <h2>我的收藏</h2>
    
    <!-- 收藏数量 -->
    <div class="count-info">共 {{ total }} 件商品</div>
    
    <!-- 收藏列表 -->
    <div v-if="products.length > 0" class="product-list">
      <div v-for="product in products" :key="product.id" class="product-item">
        <img :src="product.mainImage" :alt="product.productName" />
        <div class="product-info">
          <h3>{{ product.productName }}</h3>
          <p class="price">¥{{ product.price }}</p>
          <p class="favorited-time">收藏于 {{ product.favoritedAt }}</p>
        </div>
        <button class="remove-btn" @click="removeFavorite(product.id)">
          删除
        </button>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-state">
      <p>暂无收藏商品</p>
    </div>
    
    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination">
      <button @click="prevPage" :disabled="page <= 1">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="page >= totalPages">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const userId = ref(1);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const totalPages = ref(1);
const products = ref([]);

// 获取收藏列表
const fetchFavorites = async () => {
  try {
    const response = await fetch(
      `/product/favorite/list?userId=${userId.value}&page=${page.value}&pageSize=${pageSize.value}`
    );
    const result = await response.json();
    
    if (result.code === 200) {
      products.value = result.data.products;
      total.value = result.data.total;
      totalPages.value = result.data.totalPages;
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error);
  }
};

// 删除收藏
const removeFavorite = async (productId) => {
  try {
    const response = await fetch(
      `/product/favorite/toggle?userId=${userId.value}&productId=${productId}`,
      { method: 'POST' }
    );
    const result = await response.json();
    
    if (result.code === 200) {
      fetchFavorites(); // 刷新列表
      alert('已取消收藏');
    }
  } catch (error) {
    console.error('取消收藏失败:', error);
  }
};

// 上一页
const prevPage = () => {
  if (page.value > 1) {
    page.value--;
    fetchFavorites();
  }
};

// 下一页
const nextPage = () => {
  if (page.value < totalPages.value) {
    page.value++;
    fetchFavorites();
  }
};

onMounted(() => {
  fetchFavorites();
});
</script>

<style scoped>
.favorite-page {
  padding: 20px;
}

.count-info {
  margin-bottom: 16px;
  color: #666;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: white;
  border-radius: 8px;
}

.product-item img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.price {
  color: #ff4d4f;
  font-weight: bold;
  margin: 0 0 4px 0;
}

.favorited-time {
  color: #999;
  font-size: 12px;
  margin: 0;
}

.remove-btn {
  padding: 8px 16px;
  background: #f5f5f5;
  border: none;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
```

---

## 五、状态管理建议

### 5.1 用户登录状态处理

| 场景 | userId | 行为 |
| :--- | :--- | :--- |
| 用户已登录 | 传入用户ID | 正常获取/修改收藏状态 |
| 用户未登录 | 不传或传null | 不返回收藏状态，收藏按钮置灰或提示登录 |

### 5.2 未登录用户提示

```javascript
const handleFavorite = () => {
  if (!userId) {
    alert('请先登录');
    window.location.href = '/login';
    return;
  }
  // 执行收藏操作...
};
```

---

## 六、注意事项

1. **参数校验**：`userId` 和 `productId` 必须为有效数字，否则返回错误
2. **登录状态**：未登录用户无法进行收藏操作，建议前端提前判断
3. **状态同步**：收藏状态变更后，建议刷新收藏列表或更新本地状态
4. **接口幂等性**：切换接口支持重复调用，不会产生副作用
5. **分页参数**：`page` 默认值为1，`pageSize` 默认值为10

---

## 七、数据库表结构

**表名**：`user_favorites`

| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID |
| product_id | BIGINT | 商品ID |
| created_at | DATETIME | 创建时间（收藏时间） |

---

## 八、联系信息

如有疑问或需要协助，请联系后端开发人员。