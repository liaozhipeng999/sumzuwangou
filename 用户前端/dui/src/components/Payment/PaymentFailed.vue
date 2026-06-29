<template>
  <div class="payment-failed-page">
    <div class="failed-icon">
      <van-icon name="cross-circle" color="#ee0a24" size="96" />
    </div>

    <div class="failed-title">支付失败</div>
    <div class="failed-desc">{{ errorMessage }}</div>

    <div class="order-card">
      <div class="order-info-row">
        <span class="info-label">订单编号</span>
        <span class="info-value order-no">{{ orderNo }}</span>
      </div>
      <div class="order-info-row">
        <span class="info-label">订单金额</span>
        <span class="info-value price">¥{{ amount.toFixed(2) }}</span>
      </div>
    </div>

    <div class="action-buttons">
      <van-button type="primary" class="btn-primary" @click="retryPayment">重新支付</van-button>
      <van-button type="default" class="btn-default" @click="goOrderDetail">查看订单</van-button>
    </div>

    <div class="contact-service" @click="goService">
      <van-icon name="service" color="#07c160" size="16" />
      <span>遇到问题？联系客服</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Icon as VanIcon, Button as VanButton } from 'vant'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const orderNo = ref('')
const amount = ref(0)
const errorMessage = ref('支付失败，请稍后重试')

const retryPayment = () => {
  router.push('/order')
}

const goOrderDetail = () => {
  router.push('/order')
}

const goService = () => {
  router.push('/message/chat/1')
}

onMounted(() => {
  orderNo.value = route.query.orderNo as string || `ORD${Date.now()}`
  amount.value = Number(route.query.amount) || 0
  errorMessage.value = route.query.message as string || '支付失败，请稍后重试'
})
</script>

<style scoped>
.payment-failed-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 60px 20px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.failed-icon {
  margin-bottom: 24px;
}

.failed-title {
  font-size: 22px;
  font-weight: 600;
  color: #ee0a24;
  margin-bottom: 8px;
}

.failed-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 32px;
}

.order-card {
  width: 100%;
  background: #fff;
  border-radius: 12px;
  padding: 18px 20px;
  margin-bottom: 28px;
}

.order-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.order-info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: #999;
}

.info-value {
  font-size: 14px;
  color: #333;
}

.order-no {
  font-family: monospace;
  font-size: 12px;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #ee0a24;
}

.action-buttons {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 24px;
}

.btn-primary,
.btn-default {
  width: 100%;
  height: 44px;
  border-radius: 22px;
  font-size: 15px;
  font-weight: 500;
}

.contact-service {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #07c160;
  margin-top: 8px;
}
</style>
