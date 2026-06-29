<template>
  <div class="my-page">
    <!-- 用户信息区 -->
    <div class="user-section">
      <div class="user-info">
        <van-image
          round
          width="80"
          height="80"
          src="/image/aihead/140407044_bobopic.jpg"
          class="avatar"
        />
        <div class="user-detail">
          <div class="username-row">
            <span class="username">CZC</span>
            <van-icon name="edit" size="16" class="edit-icon" />
          </div>
          <div class="address-row">
            <van-icon name="map-o" size="14" />
            <span class="address">收货地址</span>
            <van-icon name="arrow" size="12" />
          </div>
        </div>
      </div>
      <div class="user-actions">
        <div class="action-btn">
          <van-icon name="gift" size="18" />
          <span>下单返现金</span>
        </div>
        <div class="action-btn settings" @click="goSetting">
          <van-icon name="setting-o" size="18" />
          <span>设置</span>
        </div>
      </div>
    </div>

    <!-- 省钱月卡 -->
    <div class="card-section">
      <van-icon name="ticket" size="18" />
      <span class="card-title">省钱月卡</span>
      <van-icon name="arrow" size="14" />
    </div>

    <!-- 我的订单 -->
    <div class="order-section">
      <div class="section-header">
        <div class="header-left">
          <span class="section-title">我的订单</span>
          <van-icon name="shield" size="14" class="shield-icon" />
          <span class="service-text">专属客服·闪电退换·售后无忧</span>
        </div>
        <div class="header-right" @click="goOrderList('all')">
          <span>全部</span>
          <van-icon name="arrow" size="12" />
        </div>
      </div>
      <div class="order-grid">
          <div class="order-item" v-for="item in orderItems" :key="item.name" @click="goOrderList(item.status)">
            <div class="order-icon-wrap" :class="{ active: item.count > 0 }">
              <span class="emoji-icon">{{ item.icon }}</span>
              <van-badge v-if="item.count > 0" :content="item.count" class="order-badge" />
            </div>
            <span class="order-name">{{ item.name }}</span>
          </div>
        </div>
    </div>

    <!-- 功能区 -->
    <div class="function-section">
      <div class="function-grid">
        <div class="function-item" v-for="item in functionItems" :key="item.name" @click="handleFunctionClick(item.name)">
          <div class="function-icon" :style="{ background: item.bgColor }">
            <span class="emoji-icon">{{ item.icon }}</span>
          </div>
          <span class="function-name">{{ item.name }}</span>
        </div>
      </div>
    </div>

    <!-- 更多服务 -->
    <div class="service-section">
      <div class="service-grid">
        <div class="service-item" v-for="item in serviceItems" :key="item.name">
          <div class="service-icon-wrap">
            <span class="emoji-icon">{{ item.icon }}</span>
            <van-badge v-if="item.badge" :content="item.badge" class="service-badge" />
          </div>
          <span class="service-name">{{ item.name }}</span>
        </div>
      </div>
    </div>

    <!-- 商品推荐 -->
    <div class="recommend-section">
      <div class="section-header">
        <span class="section-title">为你推荐</span>
      </div>
      <div class="goods-list">
        <div class="goods-item" v-for="item in recommendGoods" :key="item.id" @click="goProductDetail(item.id)">
          <van-image :src="item.mainImage || 'https://via.placeholder.com/150x150?text=No+Image'" class="goods-image" />
          <div class="goods-info">
            <span class="goods-title">{{ item.productName }}</span>
            <div class="goods-tags">
              <span class="tag" v-for="(tag, idx) in getTags(item)" :key="idx">{{ tag }}</span>
            </div>
            <div class="goods-price">
              <span class="price">¥{{ item.price }}</span>
              <span class="sales">已售{{ item.sales }}件</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

import { useRouter } from 'vue-router'

import { showToast } from 'vant'

import { getHotProducts, type Product } from '@/api/recommend'
import { getOrderList, type Order } from '@/api/order'
import { normalizeStatus, ORDER_STATUS } from '@/utils/orderStatus'
import { useUserStore } from '@/stores/user'

// 订单状态
const orderItems = ref([
  { name: '待付款', icon: '💳', count: 0, status: 'unpaid' },
  { name: '拼团中', icon: '👥', count: 0, status: 'group' },
  { name: '打包中', icon: '📦', count: 1, status: 'packing' },
  { name: '待收货', icon: '🚚', count: 8, status: 'shipping' },
  { name: '评价', icon: '⭐', count: 2, status: 'review' }
])

// 功能区
const functionItems = ref([
  { name: '商品收藏', icon: '❤️', bgColor: '#ffebef' },
  { name: '我的钱包', icon: '👛', bgColor: '#fff4e8' },
  { name: '优惠券', icon: '🎫', bgColor: '#ffe0e0' },
  { name: '历史浏览', icon: '📖', bgColor: '#f0f9ff' },
  { name: '退款售后', icon: '🔄', bgColor: '#fff4e8' }
])

// 更多服务
const serviceItems = ref([
  { name: '专属客服', icon: '🎧', badge: '' },
  { name: '现金大转盘', icon: '🎡', badge: '100' },
  { name: '火车票', icon: '🚂', badge: '' },
  { name: '首件抢五折', icon: '🏷️', badge: '6' }
])

// 创建路由实例
const router = useRouter()
const userStore = useUserStore()

// 推荐商品
const recommendGoods = ref<Product[]>([])

// 加载订单数量统计
const loadOrderCounts = async () => {
  const userId = userStore.userInfo?.id
  if (!userId) return

  try {
    const result = await getOrderList(userId)
    if (result.code === 200 && result.data) {
      const orders = result.data
      
      // 统计各状态订单数量
      orderItems.value.forEach(item => {
        switch (item.status) {
          case 'unpaid':
            item.count = orders.filter(o => normalizeStatus(o.status) === ORDER_STATUS.UNPAID).length
            break
          case 'packing':
            item.count = orders.filter(o => normalizeStatus(o.status) === ORDER_STATUS.SHIPPING).length
            break
          case 'shipping':
            item.count = orders.filter(o => normalizeStatus(o.status) === ORDER_STATUS.RECEIVING).length
            break
          case 'review':
            item.count = orders.filter(o => normalizeStatus(o.status) === ORDER_STATUS.COMPLETED).length
            break
        }
      })
    }
  } catch (error) {
    console.error('加载订单数量失败:', error)
  }
}

// 跳转到订单列表
const goOrderList = (status: string) => {
  router.push({ path: '/order', query: { status } })
}

// 跳转到设置页面
const goSetting = () => {
  router.push('/setting')
}

// 跳转到商品详情页
const goProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 处理功能区点击
const handleFunctionClick = (name: string) => {
  switch (name) {
    case '商品收藏':
      router.push('/my/favorites')
      break
    case '我的钱包':
      router.push('/my/wallet')
      break
    case '优惠券':
      showToast('优惠券功能开发中')
      break
    case '历史浏览':
      router.push('/my/history')
      break
    case '退款售后':
      showToast('退款售后功能开发中')
      break
    default:
      break
  }
}

// 获取商品标签
const getTags = (item: Product): string[] => {
  const tags: string[] = []
  if (item.isHot === 1) tags.push('热销')
  if (item.isNew === 1) tags.push('新品')
  if (item.originalPrice > item.price) {
    const discount = Math.round((1 - item.price / item.originalPrice) * 100)
    tags.push(`${discount}%OFF`)
  }
  return tags.length > 0 ? tags : ['精选']
}

// 加载推荐商品
const loadRecommendGoods = async () => {
  try {
    const result = await getHotProducts(8)
    if (result.code === 200 && result.data) {
      recommendGoods.value = result.data
    }
  } catch (error) {
    console.error('加载推荐商品失败:', error)
    recommendGoods.value = []
  }
}

// 页面加载时获取推荐商品和订单数量
onMounted(() => {
  loadRecommendGoods()
  loadOrderCounts()
})
</script>

<style scoped>
.my-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 60px;
}

/* 用户信息区 */
.user-section {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  padding: 30px 20px 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  border: 3px solid rgba(255, 255, 255, 0.5);
}

.user-detail {
  color: white;
}

.username-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  font-size: 20px;
  font-weight: bold;
}

.edit-icon {
  color: rgba(255, 255, 255, 0.8);
}

.address-row {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 13px;
  opacity: 0.9;
}

.user-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 8px 14px;
  color: white;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-btn.settings {
  background: rgba(255, 255, 255, 0.15);
}

/* 省钱月卡 - 往下移一点，不与头部重叠 */
.card-section {
  background: #fff9f4;
  margin: 12px;
  border-radius: 8px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #ff6b35;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
}

.card-saved {
  font-size: 13px;
  color: #ff4757;
  margin-left: auto;
}

/* 我的订单 */
.order-section {
  background: white;
  margin: 12px;
  border-radius: 8px;
  padding: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.shield-icon {
  color: #2ed573;
}

.service-text {
  font-size: 12px;
  color: #999;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.order-grid {
  display: flex;
  justify-content: space-around;
}

.order-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.order-icon-wrap {
  width: 44px;
  height: 44px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.emoji-icon {
  font-size: 24px;
}

.order-icon-wrap.active {
  background: #fff0f0;
}

.order-icon-wrap.active :deep(.van-icon) {
  color: #ff4757;
}

.order-badge {
  position: absolute;
  top: -4px;
  right: -4px;
}

.order-name {
  font-size: 12px;
  color: #666;
}

/* 功能区 */
.function-section {
  background: white;
  margin: 12px;
  border-radius: 8px;
  padding: 16px 0;
}

.function-grid {
  display: flex;
  justify-content: space-around;
}

.function-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.function-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
}

.function-name {
  font-size: 12px;
  color: #666;
}

/* 更多服务 */
.service-section {
  background: white;
  margin: 12px;
  border-radius: 8px;
  padding: 16px 0;
}

.service-grid {
  display: flex;
  justify-content: space-around;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.service-icon-wrap {
  width: 44px;
  height: 44px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.service-badge {
  position: absolute;
  top: -4px;
  right: -4px;
}

.service-name {
  font-size: 12px;
  color: #666;
}

/* 商品推荐 */
.recommend-section {
  background: white;
  margin: 12px;
  border-radius: 8px;
  padding: 16px;
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.goods-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.goods-image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 8px;
}

.goods-info {
  padding: 0 4px;
}

.goods-title {
  font-size: 13px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 4px;
}

.tag {
  font-size: 10px;
  color: #ff6b35;
  background: #fff5f0;
  padding: 2px 6px;
  border-radius: 4px;
}

.goods-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}

.price {
  font-size: 15px;
  font-weight: bold;
  color: #ff4757;
}

.sales {
  font-size: 11px;
  color: #999;
}
</style>
