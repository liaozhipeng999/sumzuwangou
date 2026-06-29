<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">免密支付设置</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">微信免密支付</span>
            <span class="item-desc">开启后，支付时无需输入密码</span>
          </div>
          <van-switch v-model="wechatPay" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">支付宝免密支付</span>
            <span class="item-desc">开启后，支付时无需输入密码</span>
          </div>
          <van-switch v-model="alipay" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">小额免密支付</div>
        
        <div class="setting-item" @click="showToast('设置免密额度')">
          <div class="item-content">
            <span class="item-title">免密支付额度</span>
            <span class="item-desc">当前：500元</span>
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

const wechatPay = ref(true)
const alipay = ref(false)

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
