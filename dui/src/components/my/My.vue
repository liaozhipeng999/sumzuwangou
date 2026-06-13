<template>
  <div class="my-page">
    <!-- 用户信息区 -->
    <div class="user-section">
      <div class="user-info">
        <van-image
          round
          width="80"
          height="80"
          src="https://neeko-copilot.bytedance.net/api/text_to_image?prompt=portrait%20avatar%20icon%20cartoon%20style&image_size=square"
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
        <div class="action-btn settings">
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
        <div class="header-right">
          <span>全部</span>
          <van-icon name="arrow" size="12" />
        </div>
      </div>
      <div class="order-grid">
        <div class="order-item" v-for="item in orderItems" :key="item.name">
          <div class="order-icon-wrap" :class="{ active: item.count > 0 }">
            <van-icon :name="item.icon" size="24" />
            <van-badge v-if="item.count > 0" :content="item.count" class="order-badge" />
          </div>
          <span class="order-name">{{ item.name }}</span>
        </div>
      </div>
    </div>

    <!-- 功能区 -->
    <div class="function-section">
      <div class="function-grid">
        <div class="function-item" v-for="item in functionItems" :key="item.name">
          <div class="function-icon" :style="{ background: item.bgColor }">
            <van-icon :name="item.icon" size="20" />
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
            <van-icon :name="item.icon" size="20" />
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
        <div class="goods-item" v-for="item in recommendGoods" :key="item.id">
          <van-image :src="item.image" class="goods-image" />
          <div class="goods-info">
            <span class="goods-title">{{ item.title }}</span>
            <div class="goods-tags">
              <span class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</span>
            </div>
            <div class="goods-price">
              <span class="price">券后¥{{ item.price }}</span>
              <span class="sales">{{ item.sales }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 订单状态
const orderItems = ref([
  { name: '待付款', icon: 'Wallet', count: 0 },
  { name: '拼团中', icon: 'Share', count: 0 },
  { name: '打包中', icon: 'Package', count: 1 },
  { name: '待收货', icon: 'Truck', count: 8 },
  { name: '评价', icon: 'Comment', count: 2 }
])

// 功能区
const functionItems = ref([
  { name: '商品收藏', icon: 'Heart', bgColor: '#ffebef' },
  { name: '多多钱包', icon: 'CreditCard', bgColor: '#fff4e8' },
  { name: '优惠券', icon: 'Ticket', bgColor: '#ffe0e0' },
  { name: '历史浏览', icon: 'Clock', bgColor: '#f0f9ff' },
  { name: '退款售后', icon: 'Refresh', bgColor: '#fff4e8' }
])

// 更多服务
const serviceItems = ref([
  { name: '专属客服', icon: 'Headphones', badge: '' },
  { name: '现金大转盘', icon: 'Refresh', badge: '100' },
  { name: '火车票', icon: 'Train', badge: '' },
  { name: '首件抢五折', icon: 'Percent', badge: '6' }
])

// 推荐商品
const recommendGoods = ref([
  {
    id: 1,
    image: 'https://picsum.photos/seed/nasa/200/200',
    title: 'NASA冰丝冰丝速干短袖T恤男夏季薄款透气宽松潮流半袖',
    tags: ['旗舰店', 'NASA冰丝速干'],
    price: '10.35',
    sales: '全店总售900万+件'
  },
  {
    id: 2,
    image: 'https://picsum.photos/seed/food1/200/200',
    title: '焗火火盐焗鸡胗鸡肫即食熟食梅州客家特产休闲零食小吃',
    tags: ['旗舰店', '焗火火盐焗鸡'],
    price: '5.99',
    sales: '已抢528件'
  },
  {
    id: 3,
    image: 'https://picsum.photos/seed/food2/200/200',
    title: '火山石烤肠地道肠脆皮肉肠台湾风味热狗肠早餐烧烤肠',
    tags: ['旗舰店', '限时7.5折'],
    price: '12.9',
    sales: '30天热销10万+'
  },
  {
    id: 4,
    image: 'https://picsum.photos/seed/bike/200/200',
    title: '3D硅胶坐垫套加厚软座自行车坐垫套山地车座套',
    tags: ['告别蛋疼'],
    price: '8.9',
    sales: '已抢3.2万件'
  }
])
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

/* 省钱月卡 */
.card-section {
  background: #fff9f4;
  margin: 0 12px;
  margin-top: -10px;
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
