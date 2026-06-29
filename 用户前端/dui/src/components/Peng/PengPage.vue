<template>
  <div class="peng-page">
    <!-- 顶部导航栏 -->
    <div class="peng-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <h1 class="page-title">商品列表</h1>
      <van-icon name="search" class="search-btn" @click="showToast('搜索功能开发中')" />
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs">
      <van-tabs v-model="activeTab" color="#ee0a24" title-active-color="#ee0a24">
        <van-tab title="综合">综合</van-tab>
        <van-tab title="销量">销量</van-tab>
        <van-tab title="价格">价格</van-tab>
        <van-tab title="新品">新品</van-tab>
      </van-tabs>
    </div>

    <!-- 商品网格 -->
    <div class="products-grid">
      <div 
        class="product-item" 
        v-for="product in products" 
        :key="product.id"
        @click="showProductDetail(product)"
      >
        <div class="product-image-wrapper">
          <img :src="product.image" :alt="product.name" class="product-image" />
          <div class="product-tags" v-if="product.tags && product.tags.length > 0">
            <span 
              v-for="(tag, index) in product.tags.slice(0, 2)" 
              :key="index" 
              class="tag"
              :class="getTagClass(tag)"
            >
              {{ tag }}
            </span>
          </div>
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-subtitle">{{ product.subtitle }}</p>
          <div class="product-bottom">
            <div class="price-area">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price.toFixed(2) }}</span>
              <span class="original-price" v-if="product.originalPrice">
                ¥{{ product.originalPrice.toFixed(2) }}
              </span>
            </div>
            <span class="sales-count">已售{{ formatSales(product.sales) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div class="load-more">
      <van-loading v-if="loading" />
      <div v-else-if="hasMore" @click="loadMoreProducts" class="load-btn">
        <span>点击加载更多</span>
      </div>
      <div v-else class="no-more">— 没有更多商品了 —</div>
    </div>

    <!-- 商品详情弹窗 -->
    <van-popup v-model="showDetail" position="bottom" :style="{ height: '70%' }">
      <div class="detail-popup" v-if="selectedProduct">
        <div class="popup-header">
          <h3>{{ selectedProduct.name }}</h3>
          <van-icon name="close" @click="showDetail = false" />
        </div>
        <div class="popup-content">
          <img :src="selectedProduct.image" :alt="selectedProduct.name" class="detail-image" />
          <div class="detail-info">
            <p class="detail-subtitle">{{ selectedProduct.subtitle }}</p>
            <div class="detail-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ selectedProduct.price.toFixed(2) }}</span>
              <span v-if="selectedProduct.originalPrice" class="original-price">
                ¥{{ selectedProduct.originalPrice.toFixed(2) }}
              </span>
            </div>
            <div class="detail-sales">已售{{ selectedProduct.sales }}件</div>
          </div>
        </div>
        <div class="popup-footer">
          <van-button type="primary" @click="handleBuy">立即购买</van-button>
          <van-button type="warning" @click="handleAddCart">加入购物车</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

interface Product {
  id: number
  name: string
  subtitle: string
  price: number
  originalPrice?: number
  sales: number
  image: string
  tags?: string[]
}

// 商品数据（从后端获取）
const products = ref<Product[]>([])

// 状态
const activeTab = ref(0)
const loading = ref(false)
const hasMore = ref(true)
const showDetail = ref(false)
const selectedProduct = ref<Product | null>(null)

// 格式化销量
const formatSales = (sales: number): string => {
  if (sales >= 10000) {
    return (sales / 10000).toFixed(1) + '万'
  }
  return sales.toString()
}

// 获取标签样式
const getTagClass = (tag: string): string => {
  switch (tag) {
    case '爆款': return 'tag-hot'
    case '新品': return 'tag-new'
    case '折扣': return 'tag-discount'
    default: return ''
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 显示商品详情弹窗
const showProductDetail = (product: Product) => {
  selectedProduct.value = product
  showDetail.value = true
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    // TODO: 调用后端接口获取商品列表
    // const response = await getProducts()
    // if (response.code === 200) {
    //   products.value = response.data
    // }
    console.log('加载商品列表')
  } catch (error) {
    console.error('加载商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载更多
const loadMoreProducts = async () => {
  if (loading.value || !hasMore.value) return
  
  loading.value = true
  try {
    // TODO: 调用后端接口加载更多商品
    // const response = await getMoreProducts()
    // if (response.code === 200 && response.data.length > 0) {
    //   products.value.push(...response.data)
    // } else {
    //   hasMore.value = false
    // }
    console.log('加载更多商品')
    hasMore.value = false
  } catch (error) {
    console.error('加载更多商品失败:', error)
  } finally {
    loading.value = false
  }
}

// 立即购买
const handleBuy = () => {
  showToast('正在跳转结算页...')
  showDetail.value = false
}

// 加入购物车
const handleAddCart = () => {
  showToast('已加入购物车')
  showDetail.value = false
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.peng-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: env(safe-area-inset-bottom);
}

.peng-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn,
.search-btn {
  font-size: 20px;
  color: #333;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
}

.category-tabs {
  background: #fff;
  margin-bottom: 12px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 0 12px;
}

.product-item {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.product-image-wrapper {
  position: relative;
  padding-top: 100%;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-tags {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 4px;
}

.tag {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
}

.tag-hot {
  background: #ee0a24;
  color: #fff;
}

.tag-new {
  background: #07c160;
  color: #fff;
}

.tag-discount {
  background: #ff9500;
  color: #fff;
}

.product-info {
  padding: 8px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-subtitle {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-area {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 12px;
  color: #ee0a24;
}

.price-value {
  font-size: 16px;
  font-weight: 700;
  color: #ee0a24;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
  margin-left: 4px;
}

.sales-count {
  font-size: 12px;
  color: #999;
}

.load-more {
  padding: 16px;
  text-align: center;
}

.load-btn {
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  color: #666;
  font-size: 14px;
}

.no-more {
  color: #ccc;
  font-size: 14px;
}

.detail-popup {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.popup-header h3 {
  font-size: 16px;
  font-weight: 600;
}

.popup-header .van-icon {
  font-size: 20px;
}

.popup-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.detail-image {
  width: 100%;
  border-radius: 8px;
  margin-bottom: 12px;
}

.detail-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.detail-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.detail-price .price-symbol {
  font-size: 16px;
}

.detail-price .price-value {
  font-size: 24px;
}

.detail-sales {
  font-size: 12px;
  color: #999;
}

.popup-footer {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}

.popup-footer .van-button {
  flex: 1;
}
</style>