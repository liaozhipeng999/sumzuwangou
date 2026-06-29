<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">免拼设置</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="section-title">免拼权益</div>
        
        <div class="benefit-card">
          <div class="benefit-header">
            <span class="benefit-title">免拼卡</span>
            <span class="benefit-count">还剩136次免拼机会</span>
          </div>
          <div class="benefit-desc">
            自动确认收货，无需等待，订单更快完成
          </div>
        </div>
      </div>

      <div class="section">
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">自动免拼</span>
            <span class="item-desc">订单发货后自动使用免拼权益</span>
          </div>
          <van-switch v-model="autoGroupBuy" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="setting-item" @click="handleAction('buy')">
          <div class="item-icon-wrap">
            <span class="item-icon">🛒</span>
          </div>
          <div class="item-content">
            <span class="item-title">购买免拼卡</span>
            <span class="item-desc">更多免拼机会</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('record')">
          <div class="item-icon-wrap">
            <span class="item-icon">📋</span>
          </div>
          <div class="item-content">
            <span class="item-title">免拼记录</span>
            <span class="item-desc">查看使用历史</span>
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

const autoGroupBuy = ref(true)

const goBack = () => {
  router.back()
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    buy: '购买免拼卡',
    record: '免拼记录'
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

.benefit-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0 16px 16px;
  border-radius: 12px;
  padding: 16px;
  color: white;
}

.benefit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.benefit-title {
  font-size: 16px;
  font-weight: bold;
}

.benefit-count {
  font-size: 13px;
  opacity: 0.9;
}

.benefit-desc {
  margin-top: 8px;
  font-size: 13px;
  opacity: 0.85;
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
</style>
