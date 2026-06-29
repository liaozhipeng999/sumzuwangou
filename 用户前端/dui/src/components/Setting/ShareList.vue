<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">个人信息共享清单</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="list-content">
      <div class="section">
        <div class="section-title">共享的个人信息</div>
        <div class="info-list">
          <div class="info-item" v-for="item in shareInfo" :key="item.title">
            <div class="info-header">
              <span class="info-icon">{{ item.icon }}</span>
              <span class="info-title">{{ item.title }}</span>
            </div>
            <p class="info-desc">{{ item.desc }}</p>
            <div class="info-partners">
              <span class="partners-label">共享给：</span>
              <span class="partners-value">{{ item.partners }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">共享目的</div>
        <div class="purpose-list">
          <div class="purpose-item">
            <van-icon name="check" size="14" color="#07c160" />
            <span>为您提供更好的商品推荐和服务体验</span>
          </div>
          <div class="purpose-item">
            <van-icon name="check" size="14" color="#07c160" />
            <span>优化产品功能和用户体验</span>
          </div>
          <div class="purpose-item">
            <van-icon name="check" size="14" color="#07c160" />
            <span>保障交易安全和用户权益</span>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">管理共享设置</div>
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">个性化推荐</span>
            <span class="item-desc">基于您的行为提供个性化推荐</span>
          </div>
          <van-switch v-model="personalized" size="24" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const personalized = ref(true)

const shareInfo = [
  {
    icon: '👤',
    title: '基本信息',
    desc: '包括您的昵称、头像、性别等基础个人信息',
    partners: '认证服务商、第三方SDK'
  },
  {
    icon: '📱',
    title: '设备信息',
    desc: '包括设备型号、操作系统、设备标识符等',
    partners: '数据分析服务商、安全服务商'
  },
  {
    icon: '📍',
    title: '位置信息',
    desc: '包括您的地理位置信息，用于提供本地化服务',
    partners: '地图服务商、本地生活服务商'
  },
  {
    icon: '🛒',
    title: '消费行为',
    desc: '包括您的浏览记录、购买记录、收藏记录等',
    partners: '推荐算法服务商、广告服务商'
  }
]

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.setting-sub-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.page-header {
  background: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.back-btn {
  font-size: 20px;
  color: #333;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.header-placeholder {
  width: 24px;
}

.list-content {
  padding: 12px;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  padding: 12px;
  background: #f7f8fa;
  border-radius: 10px;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.info-icon {
  font-size: 20px;
}

.info-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.info-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 8px;
}

.info-partners {
  display: flex;
  align-items: center;
}

.partners-label {
  font-size: 12px;
  color: #999;
}

.partners-value {
  font-size: 12px;
  color: #667eea;
  margin-left: 4px;
}

.purpose-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.purpose-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.setting-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.setting-item:last-child {
  border-bottom: none;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 15px;
  color: #333;
  display: block;
}

.item-desc {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
</style>
