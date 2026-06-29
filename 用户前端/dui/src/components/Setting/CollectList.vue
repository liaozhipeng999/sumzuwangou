<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">个人信息收集清单</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="list-content">
      <div class="section">
        <div class="section-title">收集的个人信息</div>
        <div class="info-list">
          <div class="info-item" v-for="item in collectInfo" :key="item.title">
            <div class="info-header">
              <span class="info-icon">{{ item.icon }}</span>
              <span class="info-title">{{ item.title }}</span>
            </div>
            <p class="info-desc">{{ item.desc }}</p>
            <div class="info-status">
              <span class="status-label">收集状态：</span>
              <span class="status-value" :class="item.status">{{ item.statusText }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">信息使用说明</div>
        <div class="usage-content">
          <p>我们收集您的个人信息是为了向您提供更好的服务和体验。我们会严格遵守相关法律法规，保护您的隐私安全。</p>
          <p>您可以在设置中管理部分信息的收集权限。</p>
        </div>
      </div>

      <div class="section">
        <div class="section-title">管理收集权限</div>
        <div class="permission-list">
          <div class="permission-item">
            <div class="permission-info">
              <span class="permission-icon">📱</span>
              <div class="permission-content">
                <span class="permission-title">设备信息</span>
                <span class="permission-desc">用于识别设备和保障安全</span>
              </div>
            </div>
            <van-switch v-model="deviceInfo" size="24" />
          </div>
          <div class="permission-item">
            <div class="permission-info">
              <span class="permission-icon">📍</span>
              <div class="permission-content">
                <span class="permission-title">位置信息</span>
                <span class="permission-desc">用于提供本地化服务</span>
              </div>
            </div>
            <van-switch v-model="locationInfo" size="24" />
          </div>
          <div class="permission-item">
            <div class="permission-info">
              <span class="permission-icon">🎤</span>
              <div class="permission-content">
                <span class="permission-title">麦克风</span>
                <span class="permission-desc">用于语音搜索和通话</span>
              </div>
            </div>
            <van-switch v-model="microphone" size="24" />
          </div>
          <div class="permission-item">
            <div class="permission-info">
              <span class="permission-icon">📷</span>
              <div class="permission-content">
                <span class="permission-title">相机</span>
                <span class="permission-desc">用于扫码和拍照</span>
              </div>
            </div>
            <van-switch v-model="camera" size="24" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const deviceInfo = ref(true)
const locationInfo = ref(true)
const microphone = ref(false)
const camera = ref(true)

const collectInfo = [
  {
    icon: '👤',
    title: '账户信息',
    desc: '包括您的用户名、密码、手机号、邮箱等用于账户注册和登录的信息',
    status: 'active',
    statusText: '持续收集'
  },
  {
    icon: '📱',
    title: '设备信息',
    desc: '包括设备型号、操作系统、设备标识符、IP地址等',
    status: 'active',
    statusText: '持续收集'
  },
  {
    icon: '📍',
    title: '位置信息',
    desc: '包括您的地理位置信息，用于提供本地化服务和商品推荐',
    status: 'active',
    statusText: '持续收集'
  },
  {
    icon: '🛒',
    title: '行为信息',
    desc: '包括您的浏览记录、搜索记录、购买记录、收藏记录等',
    status: 'active',
    statusText: '持续收集'
  },
  {
    icon: '📧',
    title: '联系方式',
    desc: '包括您的收货地址、联系电话等用于订单配送的信息',
    status: 'on-demand',
    statusText: '按需收集'
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

.info-status {
  display: flex;
  align-items: center;
}

.status-label {
  font-size: 12px;
  color: #999;
}

.status-value {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-left: 4px;
}

.status-value.active {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-value.on-demand {
  background: #fff3e0;
  color: #e65100;
}

.usage-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.permission-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.permission-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 10px;
}

.permission-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.permission-icon {
  font-size: 20px;
}

.permission-content {
  display: flex;
  flex-direction: column;
}

.permission-title {
  font-size: 14px;
  color: #333;
}

.permission-desc {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
</style>
