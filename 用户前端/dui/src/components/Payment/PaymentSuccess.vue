<template>
  <div class="payment-success-page">
    <!-- 顶部成功图标 -->
    <div class="success-icon">
      <van-icon name="check-circle" color="#07c160" size="120" />
    </div>
    
    <!-- 成功提示 -->
    <div class="success-title">支付成功</div>
    <div class="success-desc">您的订单已支付成功</div>
    
    <!-- 订单信息 -->
    <div class="order-card">
      <div class="order-info-row">
        <span class="info-label">订单编号</span>
        <span class="info-value order-no">{{ orderInfo.orderNo }}</span>
      </div>
      <div class="order-info-row">
        <span class="info-label">支付金额</span>
        <span class="info-value price">¥{{ orderInfo.amount.toFixed(2) }}</span>
      </div>
      <div class="order-info-row">
        <span class="info-label">支付方式</span>
        <span class="info-value">{{ payTypeName }}</span>
      </div>
      <div class="order-info-row">
        <span class="info-label">支付时间</span>
        <span class="info-value">{{ formatPayTime(orderInfo.payTime) }}</span>
      </div>
      <div class="order-info-row">
        <span class="info-label">交易单号</span>
        <span class="info-value">{{ orderInfo.transactionId }}</span>
      </div>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <van-button type="primary" class="btn-primary" @click="goOrderDetail">查看订单详情</van-button>
      <van-button type="default" class="btn-default" @click="goHome">继续购物</van-button>
    </div>
    
    <!-- 温馨提示 -->
    <div class="tips">
      <div class="tip-item">
        <van-icon name="shield-o" color="#ff9500" size="16" />
        <span>支付成功后，请留意订单发货进度</span>
      </div>
      <div class="tip-item">
        <van-icon name="service" color="#ff9500" size="16" />
        <span>如有疑问，请联系客服</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Icon as VanIcon, Button as VanButton, showToast } from 'vant'
import { useRouter, useRoute } from 'vue-router'
import { checkPayment, type PaymentResult, PayTypeMap } from '@/api/payment'

const router = useRouter()
const route = useRoute()

// 订单信息
const orderInfo = ref<PaymentResult>({
  paymentId: 0,
  orderId: 0,
  orderNo: '',
  amount: 0,
  payType: 1,
  payStatus: 1,
  transactionId: '',
  payTime: ''
})

// 支付方式名称
const payTypeName = computed(() => PayTypeMap[orderInfo.value.payType] || '微信支付')

// 格式化支付时间
const formatPayTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 跳转到订单详情
const goOrderDetail = () => {
  router.push('/order')
}

// 跳转到首页
const goHome = () => {
  router.push('/home')
}

// 加载支付信息
const loadPaymentInfo = async () => {
  const orderId = Number(route.query.orderId)
  
  if (!orderId) {
    console.error('缺少订单ID参数')
    showToast('订单信息不完整')
    return
  }
  
  try {
    const response = await checkPayment(orderId)
    if (response.code === 200 && response.data) {
      orderInfo.value = response.data
    } else {
      console.error('查询支付状态失败:', response.message)
      showToast(response.message || '查询支付信息失败')
    }
  } catch (error) {
    console.error('加载支付信息失败:', error)
    showToast('加载支付信息失败，请检查网络连接')
  }
}

onMounted(() => {
  loadPaymentInfo()
})
</script>

<style scoped>
.payment-success-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 成功图标 */
.success-icon {
  margin-bottom: 20px;
}

/* 标题 */
.success-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.success-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 30px;
}

/* 订单信息卡片 */
.order-card {
  width: 100%;
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
}

.order-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.order-info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #999;
}

.info-value {
  font-size: 14px;
  color: #333;
}

.order-no {
  font-family: monospace;
  font-size: 13px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #ee0a24;
}

/* 操作按钮 */
.action-buttons {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 30px;
}

.btn-primary {
  width: 100%;
  height: 44px;
  border-radius: 22px;
  font-size: 16px;
}

.btn-default {
  width: 100%;
  height: 44px;
  border-radius: 22px;
  font-size: 16px;
}

/* 温馨提示 */
.tips {
  width: 100%;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}
</style>