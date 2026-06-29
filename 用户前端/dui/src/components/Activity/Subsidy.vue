<template>
  <div class="subsidy-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="百亿补贴" left-arrow @click-left="goBack" />

    <!-- 活动横幅 -->
    <div class="activity-banner">
      <div class="banner-content">
        <h2> 百亿补贴</h2>
        <p>官方补贴，正品低价！</p>
        <div class="countdown" v-if="remainingTime > 0">
          <span class="time-label">距结束还有：</span>
          <span class="time-value">{{ formatCountdown(remainingTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="products-section">
      <h3 class="section-title">今日补贴商品</h3>
      
      <div v-if="loading" class="loading-state">
        <van-loading size="24px" vertical>加载中...</van-loading>
      </div>
      
      <div v-else-if="products.length === 0" class="empty-state">
        <van-empty description="暂无补贴商品" image="search" />
      </div>
      
      <div v-else class="products-list">
        <ProductCard 
          v-for="product in products" 
          :key="product.id" 
          :product="product"
          @click="goProduct(product.id)"
        />
      </div>
    </div>

    <!-- 活动说明 -->
    <div class="rules-section">
      <h3 class="section-title">活动规则</h3>
      <ul class="rules-list">
        <li>所有商品均为官方正品，假一赔十</li>
        <li>活动时间：每日 10:00 - 22:00</li>
        <li>补贴价格仅限活动期间有效</li>
        <li>支持7天无理由退换货</li>
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
import ProductCard from '@/components/Product/ProductCard.vue'

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
    const result = await getHotProducts(20)
    if (result.code === 200 && result.data) {
      products.value = result.data
    }
  } catch (error) {
    console.error('加载补贴商品失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 跳转商品详情
const goProduct = (id: number) => {
  router.push(`/product/${id}`)
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
.subsidy-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 20px;
}

/* 活动横幅 */
.activity-banner {
  background: linear-gradient(135deg, #ee0a24 0%, #ff6b6b 100%);
  margin: 0 15px 15px;
  border-radius: 12px;
  padding: 25px 20px;
  color: #fff;
  box-shadow: 0 4px 15px rgba(238, 10, 36, 0.3);
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
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
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
