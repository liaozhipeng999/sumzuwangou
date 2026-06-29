<template>
  <van-popup
    :show="visible"
    position="bottom"
    :style="{ height: '90%' }"
    round
  >
    <div class="order-popup">
      <!-- 顶部标题栏 -->
      <div class="popup-header">
        <span class="popup-title">确认订单</span>
        <van-icon name="cross" class="close-btn" @click="handleClose" />
      </div>

      <div class="popup-content">
        <!-- 地址选择区域 -->
        <div class="address-section" @click="goAddressPage">
          <div class="address-icon">
            <van-icon name="map-marker" color="#ee0a24" size="20" />
          </div>
          <div class="address-info" v-if="selectedAddress">
            <div class="address-header">
              <span class="receiver-name">{{ selectedAddress.name }}</span>
              <span class="receiver-phone">{{ selectedAddress.phone }}</span>
            </div>
            <div class="address-detail">
              {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detail }}
            </div>
          </div>
          <div class="address-info" v-else>
            <div class="no-address">
              <van-icon name="plus" color="#ee0a24" size="18" />
              <span>新增收货地址</span>
            </div>
          </div>
          <van-icon name="arrow" color="#999" />
        </div>

        <!-- 发货信息 -->
        <div class="shipping-section">
          <span class="shipping-icon">
            <van-icon name="logistics" color="#07c160" size="16" />
          </span>
          <span class="shipping-text">预计8小时内发货</span>
          <span class="shipping-user">{{ selectedAddress?.name || '未设置' }}, {{ selectedAddress?.phone || '未设置' }}</span>
        </div>

        <!-- 优惠信息 -->
        <div class="coupon-banner">
          <span class="coupon-icon">
            <van-icon name="tag" color="#ee0a24" size="16" />
          </span>
          <span class="coupon-text">优惠马上失效 | 请尽快下单</span>
        </div>

        <!-- 商品信息 -->
        <div class="product-section">
          <div class="product-header">
            <span class="product-tag">百亿补贴</span>
            <span class="product-sales">{{ salesCount }}人逛过该店铺</span>
          </div>
          <div class="product-item">
            <div class="product-info">
              <img :src="productImage" class="product-image" />
              <div class="product-detail">
                <div class="product-name">{{ productName }}</div>
                <div class="product-spec">已选: {{ selectedSpecText }}</div>
              </div>
            </div>
            <div class="product-price-section">
              <div class="product-price">¥{{ currentSku?.price || productPrice }}</div>
              <div class="product-quantity">x{{ quantity }}</div>
            </div>
          </div>
        </div>

        <!-- 动态属性选择区域 -->
        <div class="spec-section" v-if="dimensions.length > 0">
          <div class="spec-group" v-for="dimension in sortedDimensions" :key="dimension.key">
            <span class="spec-name">{{ dimension.name }}：</span>
            <div class="spec-values">
              <button
                v-for="option in Object.keys(getOptionsForDimension(dimension.key))"
                :key="option"
                :class="['spec-btn', { selected: selectedAttributes[dimension.key] === option }]"
                @click="selectOption(dimension.key, option)"
              >
                {{ option }}
              </button>
            </div>
          </div>
          <!-- 数量选择 -->
          <div class="quantity-section">
            <span class="quantity-label">数量</span>
            <div class="quantity-control">
              <button class="qty-btn" @click="decreaseQty" :disabled="quantity <= 1">-</button>
              <span class="qty-value">{{ quantity }}</span>
              <button class="qty-btn" @click="increaseQty" :disabled="quantity >= (currentSku?.stock || 999)">+</button>
            </div>
            <span class="stock-info" v-if="currentSku && currentSku.stock < 10">即将售罄</span>
          </div>
        </div>

        <!-- 折扣信息 -->
        <div class="discount-section" v-if="discountInfo">
          <div class="discount-header">
            <span class="discount-tag" v-if="discountInfo.discountLabel">{{ discountInfo.discountLabel }}</span>
            <span class="discount-text">折扣: 立减¥{{ discountInfo.discountAmount.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 优惠券选择 -->
        <div class="coupon-section" @click="toggleCouponPanel">
          <span class="coupon-label">优惠券</span>
          <div class="coupon-value">
            <span v-if="selectedCoupon">-¥{{ selectedCoupon?.discountValue }}</span>
            <span v-else-if="availableCoupons.length > 0">{{ availableCoupons.length }}张可用</span>
            <span v-else class="no-coupon">暂无可用</span>
          </div>
          <van-icon :name="showCouponPanel ? 'arrow-up' : 'arrow-down'" color="#999" />
        </div>

        <!-- 优惠券列表 -->
        <div class="coupon-list" v-if="showCouponPanel">
          <div
            v-for="coupon in availableCoupons"
            :key="coupon.userCouponId"
            :class="['coupon-item', { selected: selectedCoupon?.userCouponId === coupon.userCouponId }]"
            @click="selectCoupon(coupon)"
          >
            <div class="coupon-left">
              <span class="coupon-amount">¥{{ coupon.discountValue }}</span>
              <span class="coupon-condition">满{{ coupon.minAmount }}可用</span>
            </div>
            <div class="coupon-right">
              <span class="coupon-name">{{ coupon.couponName }}</span>
              <span class="coupon-time">{{ formatDate(coupon.endTime) }}到期</span>
            </div>
            <van-icon v-if="selectedCoupon?.userCouponId === coupon.userCouponId" name="check" color="#ee0a24" />
          </div>
          <div v-if="availableCoupons.length === 0" class="empty-coupon">
            <van-icon name="coupon-o" color="#ccc" size="48" />
            <p>暂无可用优惠券</p>
          </div>
        </div>

        <!-- 优惠明细 -->
        <div class="discount-detail">
          <div class="discount-item">
            <span>商品金额</span>
            <span class="discount-value">¥{{ (currentSku?.price || productPrice * quantity).toFixed(2) }}</span>
          </div>
          <div class="discount-item" v-if="discountInfo && discountInfo.discountAmount > 0">
            <span>商品折扣</span>
            <span class="discount-value">-¥{{ discountInfo.discountAmount.toFixed(2) }}</span>
          </div>
          <div class="discount-item" v-if="selectedCoupon">
            <span>{{ selectedCoupon.couponName }}</span>
            <span class="discount-value">-¥{{ selectedCoupon.discountValue.toFixed(2) }}</span>
          </div>
          <div class="discount-item total">
            <span>实付金额</span>
            <span class="total-price">¥{{ finalPrice.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="payment-section">
          <span class="payment-label">支付方式</span>
          <div class="payment-options">
            <div 
              :class="['payment-item', { active: selectedPayment === 'wechat' }]"
              @click="selectedPayment = 'wechat'"
            >
              <svg class="payment-icon wechat-icon" viewBox="0 0 24 24" fill="none">
                <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 01.213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 00.167-.054l1.903-1.114a.864.864 0 01.717-.098 10.16 10.16 0 002.837.403c4.801 0 8.692-3.287 8.692-7.342 0-4.053-3.891-7.339-8.692-7.339zm-3.21 5.324c-.486 0-.88.398-.88.886 0 .487.395.886.88.886.486 0 .88-.399.88-.886 0-.488-.394-.886-.88-.886zm6.419 0c-.486 0-.88.398-.88.886 0 .487.395.886.88.886.486 0 .88-.399.88-.886 0-.488-.394-.886-.88-.886zm3.21 0c-.486 0-.88.398-.88.886 0 .487.394.886.88.886.487 0 .88-.399.88-.886 0-.488-.393-.886-.88-.886z" fill="#07C160"/>
              </svg>
              <span>微信支付</span>
            </div>
            <div 
              :class="['payment-item', { 'alipay-active': selectedPayment === 'alipay' }]"
              @click="selectedPayment = 'alipay'"
            >
              <svg class="payment-icon alipay-icon" viewBox="0 0 24 24" fill="none">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" fill="#1677FF"/>
              </svg>
              <span>支付宝</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部结算栏 -->
      <div class="popup-footer">
        <div class="footer-left">
          <div class="total-label">618狂降价</div>
          <div class="total-price">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ finalPrice.toFixed(2) }}</span>
            <span class="original-price">¥{{ originalTotal.toFixed(2) }}</span>
          </div>
        </div>
        <van-button 
          class="submit-btn" 
          type="danger" 
          @click="submitOrder"
          :disabled="!canSubmit"
        >
          发起拼单
        </van-button>
      </div>
    </div>
  </van-popup>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { Popup as VanPopup, Icon as VanIcon, Button as VanButton, showToast, showLoadingToast, closeToast } from 'vant'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { Address } from '@/api/order'
import { getAddresses, createOrder } from '@/api/order'
import { createPayment, type CreatePaymentResponse } from '@/api/payment'
import { 
  getAttributeOptions, 
  calculateDiscount, 
  type Dimension, 
  type Sku, 
  type DiscountCoupon,
  type DiscountResponse
} from '@/api/productAttributes'

interface Props {
  visible: boolean
  productId: number
  productName: string
  productPrice: number
  productImage: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'orderSuccess', orderNo: string): void
}>()

const router = useRouter()
const userStore = useUserStore()

// 地址相关
const addresses = ref<Address[]>([])
const selectedAddress = ref<Address | null>(null)

// 属性相关
const dimensions = ref<Dimension[]>([])
const attributeOptions = ref<Record<string, Record<string, number[]> | string>>({})
const skus = ref<Record<string, Sku>>({})
const selectedAttributes = reactive<Record<string, string>>({})
const quantity = ref(1)
const salesCount = ref(59)

// 折扣相关
const discountInfo = ref<DiscountResponse['data'] | null>(null)
const availableCoupons = ref<DiscountCoupon[]>([])
const selectedCoupon = ref<DiscountCoupon | null>(null)
const showCouponPanel = ref(false)

// 支付方式
const selectedPayment = ref<'wechat' | 'alipay'>('wechat')

// 当前选中的SKU（如果没有SKU则返回null）
const currentSku = computed(() => {
  const selectedKeys = Object.keys(selectedAttributes)
  
  if (selectedKeys.length === 0) {
    const skuIds = Object.keys(skus.value)
    return skuIds.length > 0 ? skus.value[skuIds[0]] : null
  }

  let candidateSkuIds: Set<number> | null = null
  for (const key of selectedKeys) {
    const options = attributeOptions.value[key] as Record<string, number[]>
    const selectedValue = selectedAttributes[key]
    
    if (options && options[selectedValue]) {
      const skuIds = options[selectedValue]
      if (candidateSkuIds === null) {
        candidateSkuIds = new Set(skuIds)
      } else {
        candidateSkuIds = new Set(skuIds.filter(id => candidateSkuIds!.has(id)))
      }
    }
  }

  if (candidateSkuIds && candidateSkuIds.size > 0) {
    const firstSkuId = Array.from(candidateSkuIds)[0]
    return skus.value[firstSkuId.toString()] || null
  }

  return null
})

// 是否可以下单（有地址且有SKU或没有SKU时）
const canSubmit = computed(() => {
  return selectedAddress.value && (currentSku.value || Object.keys(skus.value).length === 0)
})

// 排序后的维度
const sortedDimensions = computed(() => {
  return [...dimensions.value].sort((a, b) => a.sortOrder - b.sortOrder)
})

// 获取指定维度的选项
const getOptionsForDimension = (dimensionKey: string): Record<string, number[]> => {
  return (attributeOptions.value[dimensionKey] as Record<string, number[]>) || {}
}

// 已选规格文本
const selectedSpecText = computed(() => {
  const specs: string[] = []
  for (const dimension of sortedDimensions.value) {
    const value = selectedAttributes[dimension.key]
    if (value) {
      specs.push(`${dimension.name}: ${value}`)
    }
  }
  return specs.join(' ') || '请选择规格'
})

// 原价总和
const originalTotal = computed(() => {
  const originalPrice = currentSku.value?.originalPrice || props.productPrice * 1.25
  return originalPrice * quantity.value
})

// 最终价格
const finalPrice = computed(() => {
  const price = currentSku.value?.price || props.productPrice
  let final = price * quantity.value
  
  // 减去商品折扣
  if (discountInfo.value) {
    final -= discountInfo.value.discountAmount
  }
  
  // 减去优惠券折扣
  if (selectedCoupon.value) {
    final -= selectedCoupon.value.discountValue
  }
  
  return Math.max(0, final)
})

// 获取用户ID
const getUserId = (): number => {
  return userStore.userInfo?.id || 1
}

// 加载地址列表
const loadAddresses = async () => {
  try {
    const response = await getAddresses(getUserId())
    if (response.code === 200 && response.data) {
      addresses.value = response.data
      selectedAddress.value = response.data.find(a => a.isDefault === 1) || response.data[0] || null
    }
  } catch (error) {
    console.error('加载地址失败:', error)
    addresses.value = []
    selectedAddress.value = null
  }
}

// 加载商品属性选项
const loadAttributes = async () => {
  try {
    const response = await getAttributeOptions(props.productId)
    if (response.code === 200 && response.data) {
      dimensions.value = response.data.dimensions
      attributeOptions.value = response.data.attributeOptions
      skus.value = response.data.skus
      salesCount.value = Math.floor(Math.random() * 500) + 50
      
      // 默认选中第一个维度的第一个选项
      if (dimensions.value.length > 0) {
        const firstDimension = dimensions.value[0]
        const options = getOptionsForDimension(firstDimension.key)
        if (options && Object.keys(options).length > 0) {
          selectedAttributes[firstDimension.key] = Object.keys(options)[0]
        }
      }
    }
  } catch (error) {
    console.error('加载商品属性失败:', error)
    dimensions.value = []
    attributeOptions.value = {}
    skus.value = {}
  }
}

// 加载折扣信息
const loadDiscount = async (skuId: number) => {
  if (!skuId) return
  
  try {
    const response = await calculateDiscount(props.productId, skuId, getUserId())
    if (response.code === 200 && response.data) {
      discountInfo.value = response.data
      availableCoupons.value = response.data.availableCoupons || []
    }
  } catch (error) {
    console.error('加载折扣信息失败:', error)
    discountInfo.value = null
    availableCoupons.value = []
  }
}

// 选择属性选项
const selectOption = (dimensionKey: string, option: string) => {
  selectedAttributes[dimensionKey] = option
}

// 选择优惠券
const selectCoupon = (coupon: DiscountCoupon) => {
  if (selectedCoupon.value?.userCouponId === coupon.userCouponId) {
    selectedCoupon.value = null
  } else {
    selectedCoupon.value = coupon
  }
}

// 切换优惠券面板
const toggleCouponPanel = () => {
  showCouponPanel.value = !showCouponPanel.value
}

// 数量操作
const decreaseQty = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const increaseQty = () => {
  const maxStock = currentSku.value?.stock || 999
  if (quantity.value < maxStock) {
    quantity.value++
  }
}

// 跳转到地址页面
const goAddressPage = () => {
  handleClose()
  router.push('/my/address')
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 当前创建的订单ID
const currentOrderId = ref(0)

// 提交订单
const submitOrder = async () => {
  if (!selectedAddress.value) {
    showToast('请先添加收货地址')
    return
  }
  
  // 获取当前选中的SKU ID
  const skuId = currentSku.value?.skuId
  if (!skuId) {
    showToast('请选择商品规格')
    return
  }
  
  try {
    showLoadingToast({ message: '正在创建订单...', duration: 0, forbidClick: true })
    
    // 1. 先创建订单
    const orderData = {
      userId: getUserId(),
      addressId: selectedAddress.value.id,
      items: [{
        skuId: skuId,
        quantity: quantity.value
      }]
    }

    console.log('提交订单数据:', JSON.stringify(orderData, null, 2))

    const orderRes = await createOrder(orderData)
    
    if (orderRes.code !== 200 || !orderRes.data) {
      closeToast()
      showToast(orderRes.message || '创建订单失败')
      return
    }

    const orderId = orderRes.data.orderId
    
    // 2. 创建支付
    const payType = selectedPayment.value === 'wechat' ? 1 : 2
    const paymentResponse: CreatePaymentResponse = await createPayment({
      orderId,
      userId: getUserId(),
      payType
    })
    
    closeToast()
    
    if (paymentResponse.code === 200 && paymentResponse.data) {
      handleClose()
      router.replace({ 
        path: '/payment/success', 
        query: { 
          orderId: orderId,
          orderNo: paymentResponse.data.orderNo,
          amount: paymentResponse.data.amount
        }
      })
    } else {
      handleClose()
      router.replace({ 
        path: '/payment/failed', 
        query: { 
          orderId: orderId,
          orderNo: `ORD${Date.now()}`,
          amount: finalPrice.value,
          message: paymentResponse.message || '支付失败'
        }
      })
    }
  } catch (error) {
    console.error('下单支付失败:', error)
    closeToast()
    showToast('支付失败，请稍后重试')
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 监听SKU变化，重新加载折扣
watch(currentSku, (newSku) => {
  if (newSku) {
    loadDiscount(newSku.skuId)
  }
})

// 监听visible变化，加载数据
watch(() => props.visible, (val) => {
  if (val) {
    loadAddresses()
    loadAttributes()
  }
})

onMounted(() => {
  // 初始化折扣
  if (currentSku.value) {
    loadDiscount(currentSku.value.skuId)
  }
})
</script>

<style scoped>
.order-popup {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f5f5;
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.popup-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  position: absolute;
  right: 16px;
  color: #999;
}

.popup-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

/* 地址区域 */
.address-section {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.address-icon {
  margin-right: 12px;
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-right: 12px;
}

.receiver-phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.no-address {
  display: flex;
  align-items: center;
  color: #ee0a24;
  font-size: 14px;
}

.no-address .van-icon {
  margin-right: 4px;
}

/* 发货信息 */
.shipping-section {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.shipping-icon {
  margin-right: 8px;
}

.shipping-text {
  font-size: 13px;
  color: #07c160;
  margin-right: 12px;
}

.shipping-user {
  font-size: 13px;
  color: #666;
}

/* 优惠横幅 */
.coupon-banner {
  display: flex;
  align-items: center;
  background: linear-gradient(90deg, #fff7f0, #fff);
  padding: 10px 16px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.coupon-icon {
  margin-right: 8px;
}

.coupon-text {
  font-size: 13px;
  color: #ee0a24;
}

/* 商品信息 */
.product-section {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  overflow: hidden;
}

.product-header {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  background: linear-gradient(90deg, #ff6b6b, #ee0a24);
}

.product-tag {
  background: #fff;
  color: #ee0a24;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  margin-right: 12px;
}

.product-sales {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.product-item {
  padding: 16px;
}

.product-info {
  display: flex;
  margin-bottom: 12px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  margin-right: 12px;
  object-fit: cover;
}

.product-detail {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  margin-bottom: 4px;
}

.product-spec {
  font-size: 13px;
  color: #999;
}

.product-price-section {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}

.product-price {
  font-size: 18px;
  font-weight: 600;
  color: #ee0a24;
}

.product-quantity {
  font-size: 14px;
  color: #666;
}

/* 规格选择 */
.spec-section {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.spec-group {
  margin-bottom: 16px;
}

.spec-name {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.spec-values {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.spec-btn {
  padding: 8px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  font-size: 14px;
  color: #333;
  background: #fff;
  cursor: pointer;
}

.spec-btn.selected {
  border-color: #ee0a24;
  background: #fff5f5;
  color: #ee0a24;
}

.quantity-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.quantity-label {
  font-size: 14px;
  color: #333;
}

.quantity-control {
  display: flex;
  align-items: center;
}

.qty-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  font-size: 18px;
  background: #fff;
  cursor: pointer;
}

.qty-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.qty-value {
  width: 48px;
  text-align: center;
  font-size: 14px;
  color: #333;
}

.stock-info {
  font-size: 12px;
  color: #ee0a24;
}

/* 折扣信息 */
.discount-section {
  background: #fff0f0;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.discount-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.discount-tag {
  background: #ee0a24;
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.discount-text {
  font-size: 14px;
  color: #ee0a24;
}

/* 优惠券区域 */
.coupon-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.coupon-label {
  font-size: 14px;
  color: #333;
}

.coupon-value {
  font-size: 14px;
  color: #ee0a24;
}

.no-coupon {
  color: #999;
}

/* 优惠券列表 */
.coupon-list {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  overflow: hidden;
}

.coupon-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
  position: relative;
}

.coupon-item:last-child {
  border-bottom: none;
}

.coupon-item.selected {
  background: #fff5f5;
}

.coupon-left {
  text-align: center;
  padding-right: 16px;
  border-right: 1px dashed #e8e8e8;
}

.coupon-amount {
  display: block;
  font-size: 24px;
  font-weight: 600;
  color: #ee0a24;
}

.coupon-condition {
  font-size: 12px;
  color: #999;
}

.coupon-right {
  flex: 1;
  padding-left: 16px;
}

.coupon-name {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.coupon-time {
  font-size: 12px;
  color: #999;
}

.empty-coupon {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #999;
}

.empty-coupon p {
  margin-top: 12px;
  font-size: 14px;
}

/* 优惠明细 */
.discount-detail {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.discount-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 8px;
}

.discount-item:last-child {
  margin-bottom: 0;
}

.discount-item span:first-child {
  color: #666;
}

.discount-value {
  color: #333;
}

.discount-item.total {
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  margin-top: 8px;
}

.total-price {
  font-size: 18px;
  font-weight: 600;
  color: #ee0a24;
}

/* 支付方式 */
.payment-section {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
}

.payment-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
  display: block;
}

.payment-options {
  display: flex;
  gap: 12px;
}

.payment-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-item.active {
  border-color: #07c160;
  background: #f0fff4;
}

.payment-item.alipay-active {
  border-color: #1677FF;
  background: #f0f5ff;
}

.payment-icon {
  width: 20px;
  height: 20px;
}

.payment-item span {
  margin-left: 8px;
  font-size: 14px;
  color: #333;
}

/* 底部结算栏 */
.popup-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
}

.footer-left {
  display: flex;
  flex-direction: column;
}

.total-label {
  font-size: 12px;
  color: #ee0a24;
  margin-bottom: 4px;
}

.total-price {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 14px;
  color: #ee0a24;
}

.price-value {
  font-size: 24px;
  font-weight: 600;
  color: #ee0a24;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}

.submit-btn {
  padding: 12px 32px;
  font-size: 16px;
  border-radius: 24px;
}

.submit-btn:disabled {
  background: #ccc;
}
</style>