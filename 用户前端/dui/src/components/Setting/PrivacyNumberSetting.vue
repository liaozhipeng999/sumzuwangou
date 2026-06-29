<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">隐私号码保护设置</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">开启隐私号码保护</span>
            <span class="item-desc">隐藏真实手机号，保护隐私安全</span>
          </div>
          <van-switch v-model="enablePrivacyNumber" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">使用说明</div>
        
        <div class="info-card">
          <div class="info-item">
            <span class="info-icon">🔒</span>
            <span class="info-text">下单后，商家和快递员将看到您的虚拟号码</span>
          </div>
          <div class="info-item">
            <span class="info-icon">📞</span>
            <span class="info-text">虚拟号码可正常接打电话和收发短信</span>
          </div>
          <div class="info-item">
            <span class="info-icon">⏱️</span>
            <span class="info-text">虚拟号码在订单完成后7天自动失效</span>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="setting-item" @click="handleAction('history')">
          <div class="item-icon-wrap">
            <span class="item-icon">📋</span>
          </div>
          <div class="item-content">
            <span class="item-title">隐私号码历史</span>
            <span class="item-desc">查看历史虚拟号码</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'

const router = useRouter()

const enablePrivacyNumber = ref(true)

const goBack = () => {
  router.back()
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    history: '隐私号码历史'
  }
  showToast(actionMap[action] || action)
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

.setting-list {
  padding: 12px;
}

.section {
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
}

.section-title {
  padding: 14px 16px 8px;
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

.setting-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.setting-item:last-child {
  border-bottom: none;
}

.item-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.item-icon {
  font-size: 18px;
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

.info-card {
  padding: 16px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-icon {
  font-size: 18px;
  margin-right: 10px;
}

.info-text {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}
</style>
