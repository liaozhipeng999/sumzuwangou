<template>
  <div class="product-header">
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
      <van-icon name="arrow-left" class="back-btn" @click="$emit('back')" />
      <div class="nav-actions">
        <van-icon name="heart-o" class="action-btn" @click="toggleCollect" />
        <van-icon name="share-o" class="action-btn" />
      </div>
    </div>

    <!-- 轮播图区域 -->
    <van-swipe 
      :autoplay="3000" 
      :loop="true"
      :show-indicators="true"
      indicator-color="rgba(255,255,255,0.5)"
      indicator-active-color="#fff"
      class="image-swipe"
    >
      <van-swipe-item v-for="(img, index) in carouselImages" :key="index">
        <img :src="img" class="swipe-image" mode="aspectFill" />
      </van-swipe-item>
    </van-swipe>

    <!-- 价格区域 -->
    <div class="price-section">
      <div class="price-left">
        <span class="price-symbol">¥</span>
        <span class="price-value">{{ formatPrice(product.price) }}</span>
        <span v-if="product.originalPrice" class="original-price">¥{{ formatPrice(product.originalPrice) }}</span>
      </div>
      <div class="discount-tag" v-if="discount">
        {{ discount }}折
      </div>
    </div>

    <!-- 服务保障 -->
    <div class="service-section">
      <div class="service-item" v-for="service in services" :key="service.text">
        <van-icon :name="service.icon" class="service-icon" />
        <span class="service-text">{{ service.text }}</span>
      </div>
    </div>

    <!-- 商品标题 -->
    <div class="title-section">
      <h1 class="product-title">{{ product.name }}</h1>
      <p class="product-subtitle">{{ product.subtitle }}</p>
    </div>

    <!-- 品牌信息 -->
    <div class="brand-section" v-if="product.brand">
      <div class="brand-logo">
        <img :src="brandIcon" alt="品牌logo" />
      </div>
      <span class="brand-name">{{ product.brand }}</span>
      <span class="brand-divider">|</span>
      <span class="brand-promise">该商品100%正品，假一赔十</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { buildBrandIconSvg } from '@/utils/request'

interface Product {
  id: number
  name: string
  subtitle: string
  price: number
  originalPrice?: number
  sales: number
  brand?: string
  merchantLogo?: string
}

interface Service {
  icon: string
  text: string
}

const props = withDefaults(defineProps<{
  product: Product
  carouselImages: string[]
}>(), {
  carouselImages: () => []
})

const brandIcon = computed(() => {
  if (props.product.merchantLogo && props.product.merchantLogo.trim() !== '') {
    return props.product.merchantLogo
  }
  return buildBrandIconSvg('brand-' + (props.product.brand || 'brand'), props.product.brand)
})

defineEmits<{
  (e: 'back'): void
  (e: 'collect'): void
}>()

const isCollected = ref(false)

// 计算折扣
const discount = computed(() => {
  if (props.product.originalPrice && props.product.price) {
    const discount = Math.round((props.product.price / props.product.originalPrice) * 10)
    return discount < 10 ? discount : 0
  }
  return 0
})

// 服务保障数据
const services = ref<Service[]>([
  { icon: 'shield', text: '正品保证' },
  { icon: 'refresh', text: '7天无理由' },
  { icon: 'truck', text: '急速发货' },
  { icon: 'credit', text: '先用后付' }
])

// 格式化价格
const formatPrice = (price: number | undefined): string => {
  if (!price) return '0.00'
  return price.toFixed(2)
}

// 收藏切换
const toggleCollect = () => {
  isCollected.value = !isCollected.value
}
</script>

<style scoped>
.product-header {
  background-color: #fff;
}

/* 导航栏 */
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
}

.back-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.nav-actions {
  display: flex;
  gap: 20px;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
}

/* 轮播图 */
.image-swipe {
  height: 400px;
}

.swipe-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 价格区域 */
.price-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 16px;
  background: linear-gradient(135deg, #fff5f5, #fff);
}

.price-left {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-symbol {
  font-size: 18px;
  font-weight: bold;
  color: #ee0a24;
}

.price-value {
  font-size: 32px;
  font-weight: bold;
  color: #ee0a24;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.discount-tag {
  background: linear-gradient(135deg, #ee0a24, #ff6b6b);
  color: #fff;
  font-size: 14px;
  font-weight: bold;
  padding: 6px 12px;
  border-radius: 4px;
}

/* 服务保障 */
.service-section {
  display: flex;
  justify-content: space-around;
  padding: 12px 16px;
  border-top: 1px solid #f5f5f5;
  border-bottom: 1px solid #f5f5f5;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.service-icon {
  font-size: 16px;
  color: #07c160;
}

.service-text {
  font-size: 12px;
  color: #666;
}

/* 标题区域 */
.title-section {
  padding: 16px;
}

.product-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  line-height: 1.5;
  margin: 0 0 8px 0;
}

.product-subtitle {
  font-size: 14px;
  color: #999;
  margin: 0;
  line-height: 1.4;
}

/* 品牌区域 */
.brand-section {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  margin: 0 12px 16px;
  background: linear-gradient(135deg, #c70a1e, #ee0a24);
  border-radius: 8px;
}

.brand-logo {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  overflow: hidden;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.brand-name {
  font-size: 15px;
  font-weight: bold;
  color: #fff;
}

.brand-divider {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.brand-promise {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  flex: 1;
}
</style>