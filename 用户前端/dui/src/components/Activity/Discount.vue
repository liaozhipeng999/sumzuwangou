<template>
  <div class="discount-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="首件抢五折" left-arrow @click-left="goBack" />

    <!-- 活动横幅 -->
    <div class="activity-banner">
      <div class="banner-content">
        <h2>🔥 首件抢购</h2>
        <p>限时5折优惠，先到先得！</p>
        <div class="countdown" v-if="remainingTime > 0">
          <span class="time-label">距结束还有：</span>
          <span class="time-value">{{ formatCountdown(remainingTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="products-section">
      <h3 class="section-title">今日特惠商品</h3>
      
      <div v-if="loading" class="loading-state">
        <van-loading size="24px" vertical>加载中...</van-loading>
      </div>
      
      <div v-else-if="products.length === 0" class="empty-state">
        <van-empty description="暂无活动商品" image="search" />
      </div>
      
      <div v-else class="products-list">
        <div v-for="product in products" :key="product.id" class="product-card" @click="goProduct(product.id)">
          <img :src="product.mainImage || 'https://via.placeholder.com/150x150?text=No+Image'" class="product-image" />
          <div class="product-info">
            <h4 class="product-name">{{ product.productName }}</h4>
            <div class="price-row">
              <span class="original-price">¥{{ product.originalPrice }}</span>
              <span class="discount-price">¥{{ (product.originalPrice * 0.5).toFixed(2) }}</span>
              <van-tag type="danger" size="medium">5折</van-tag>
            </div>
            <div class="stock-info">
              <span class="stock-text">剩余库存：{{ product.stock }}件</span>
              <van-progress 
                :percentage="(product.sold / (product.sold + product.stock)) * 100" 
                :show-pivot="false"
                color="#ff4757"
                stroke-width="6"
              />
            </div>
            <div class="sales-info">
              <span>已售 {{ product.sold }} 件</span>
              <van-button type="danger" size="small" round @click.stop="handleBuy(product)">
                立即抢购
              </van-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 活动说明 -->
    <div class="rules-section">
      <h3 class="section-title">活动说明</h3>
      <ul class="rules-list">
        <li>每个用户限购1件，每人每天最多参与3次</li>
        <li>活动时间：每日 10:00 - 22:00</li>
        <li>优惠仅限首件商品，第二件恢复原价</li>
        <li>库存有限，售完即止</li>
        <li>最终解释权归平台所有</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getHotProducts, type Product } from '@/api/recommend'

const router = useRouter()

// 加载状态
const loading = ref(false)

// 倒计时（秒）
const remainingTime = ref(3600) // 默认1小时

// 商品列表
const products = ref<Product[]>([])

// 加载商品
const loadProducts = async () => {
  loading.value = true
  try {
    const result = await getHotProducts(10)
    if (result.code === 200 && result.data) {
      // 模拟添加库存和销量信息
      products.value = result.data.map((item, index) => ({
        ...item,
        stock: Math.floor(Math.random() * 50) + 10,
        sold: Math.floor(Math.random() * 100) + 20
      }))
    }
  } catch (error) {
    console.error('加载活动商品失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 跳转商品详情
const goProduct = (id: number) => {
  router.push(`/product/${id}`)
}

// 购买
const handleBuy = (product: Product) => {
  showToast(`已加入购物车：${product.productName}`)
  // 这里可以调用加入购物车的API
}

// 返回
const goBack = () => {
  router.back()
}

// 格式化倒计时
const formatCountdown = (seconds: number): string => {
  if (seconds <= 0) return '已结束'
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

// 启动倒计时
onMounted(() => {
  loadProducts()
  
  // 每秒更新倒计时
  const timer = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      clearInterval(timer)
    }
  }, 1000)
})
</script>

<style scoped>
.discount-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 20px;
}

/* 活动横幅 */
.activity-banner {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  margin: 0 15px 15px;
  border-radius: 12px;
  padding: 25px 20px;
  color: #fff;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.banner-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: bold;
}

.banner-content p {
  margin: 0 0 12px 0;
  font-size: 14px;
  opacity: 0.9;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 12px;
  border-radius: 8px;
  width: fit-content;
}

.time-label {
  font-size: 13px;
}

.time-value {
  font-size: 16px;
  font-weight: bold;
  font-family: 'Courier New', monospace;
}

/* 商品区域 */
.products-section {
  background: #fff;
  margin: 0 15px 15px;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
}

.loading-state,
.empty-state {
  padding: 40px 0;
  text-align: center;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-card {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.product-name {
  font-size: 15px;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.original-price {
  font-size: 13px;
  color: #999;
  text-decoration: line-through;
}

.discount-price {
  font-size: 20px;
  font-weight: bold;
  color: #ff4757;
}

.stock-info {
  margin-bottom: 8px;
}

.stock-text {
  font-size: 12px;
  color: #666;
  display: block;
  margin-bottom: 4px;
}

.sales-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sales-info span {
  font-size: 12px;
  color: #999;
}

/* 规则区域 */
.rules-section {
  background: #fff;
  margin: 0 15px;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.rules-list {
  margin: 0;
  padding-left: 20px;
  color: #666;
  font-size: 14px;
  line-height: 1.8;
}

.rules-list li {
  margin-bottom: 8px;
}
</style>
