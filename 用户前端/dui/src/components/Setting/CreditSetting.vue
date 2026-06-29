<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">先用后付设置</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="credit-card">
      <div class="card-header">
        <span class="card-title">先用后付</span>
        <span class="credit-limit">可用额度 ¥500.00</span>
      </div>
      <div class="card-btn" @click="handleAction('getCredit')">
        领取0元下单资格
      </div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="setting-item" @click="handleAction('bill')">
          <div class="item-icon-wrap">
            <span class="item-icon">📅</span>
          </div>
          <div class="item-content">
            <span class="item-title">账单明细</span>
            <span class="item-desc">查看账单和还款记录</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('repay')">
          <div class="item-icon-wrap">
            <span class="item-icon">💰</span>
          </div>
          <div class="item-content">
            <span class="item-title">立即还款</span>
            <span class="item-desc">当前待还 ¥0.00</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('limit')">
          <div class="item-icon-wrap">
            <span class="item-icon">📊</span>
          </div>
          <div class="item-content">
            <span class="item-title">额度管理</span>
            <span class="item-desc">调整可用额度</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <div class="section">
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">自动还款</span>
            <span class="item-desc">到期自动从绑定账户扣款</span>
          </div>
          <van-switch v-model="autoRepay" size="24" />
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

const autoRepay = ref(true)

const goBack = () => {
  router.back()
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    getCredit: '领取0元下单资格',
    bill: '账单明细',
    repay: '立即还款',
    limit: '额度管理'
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

.credit-card {
  margin: 12px;
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  border-radius: 12px;
  padding: 20px;
  color: white;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}

.credit-limit {
  font-size: 14px;
  opacity: 0.9;
}

.card-btn {
  margin-top: 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 10px;
  text-align: center;
  font-size: 14px;
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
