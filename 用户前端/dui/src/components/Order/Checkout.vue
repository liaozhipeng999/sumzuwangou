<template>
  <div class="checkout">
    <van-nav-bar title="确认订单" left-arrow @click-left="goBack" />

    <div class="checkout-content">
      <!-- 收货地址 -->
      <div class="section address-section" @click="showAddressPicker = true">
        <div class="address-icon">
          <van-icon name="location-o" color="#ee0a24" size="24" />
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
            <span>添加收货地址</span>
          </div>
        </div>
        <van-icon name="arrow" color="#999" />
      </div>

      <!-- 商品清单 -->
      <div class="section goods-section">
        <div class="section-title">商品清单</div>
        <div class="goods-list">
          <div class="goods-item" v-for="item in checkoutItems" :key="item.id">
            <img :src="item.productImage || defaultImage" class="goods-image" />
            <div class="goods-info">
              <div class="goods-name">{{ item.productName }}</div>
              <div class="goods-price">¥{{ item.price.toFixed(2) }}</div>
            </div>
            <div class="goods-quantity">x{{ item.quantity }}</div>
          </div>
        </div>
      </div>

      <!-- 优惠券 -->
      <div class="section coupon-section" @click="showCouponPicker = true">
        <div class="section-label">优惠券</div>
        <div class="section-value">
          <span v-if="selectedCoupon" class="coupon-active">-¥{{ selectedCoupon.discountValue.toFixed(2) }}</span>
          <span v-else-if="availableCoupons.length > 0" class="coupon-available">{{ availableCoupons.length }}张可用</span>
          <span v-else class="coupon-none">暂无可用</span>
          <van-icon name="arrow" color="#999" />
        </div>
      </div>

      <!-- 支付方式 -->
      <div class="section payment-section">
        <div class="section-title">支付方式</div>
        <div class="payment-list">
          <div 
            :class="['payment-item', { active: selectedPayment === 'wechat' }]"
            @click="selectedPayment = 'wechat'"
          >
            <svg class="payment-icon wechat-icon" viewBox="0 0 24 24" fill="none">
              <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 01.213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 00.167-.054l1.903-1.114a.864.864 0 01.717-.098 10.16 10.16 0 002.837.403c4.801 0 8.692-3.287 8.692-7.342 0-4.053-3.891-7.339-8.692-7.339zm-3.21 5.324c-.486 0-.88.398-.88.886 0 .487.395.886.88.886.486 0 .88-.399.88-.886 0-.488-.394-.886-.88-.886zm6.419 0c-.486 0-.88.398-.88.886 0 .487.395.886.88.886.486 0 .88-.399.88-.886 0-.488-.394-.886-.88-.886zm3.21 0c-.486 0-.88.398-.88.886 0 .487.394.886.88.886.487 0 .88-.399.88-.886 0-.488-.393-.886-.88-.886z" fill="#07C160"/>
            </svg>
            <span>微信支付</span>
            <van-icon v-if="selectedPayment === 'wechat'" name="success" color="#ee0a24" />
          </div>
          <div 
            :class="['payment-item', { active: selectedPayment === 'alipay' }]"
            @click="selectedPayment = 'alipay'"
          >
            <svg class="payment-icon alipay-icon" viewBox="0 0 24 24" fill="none">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z" fill="#1677FF"/>
            </svg>
            <span>支付宝</span>
            <van-icon v-if="selectedPayment === 'alipay'" name="success" color="#ee0a24" />
          </div>
        </div>
      </div>

      <!-- 价格明细 -->
      <div class="section price-section">
        <div class="price-row">
          <span class="price-label">商品总价</span>
          <span class="price-value">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <div class="price-row" v-if="couponDiscount > 0">
          <span class="price-label">优惠券</span>
          <span class="price-value discount">-¥{{ couponDiscount.toFixed(2) }}</span>
        </div>
        <div class="price-row">
          <span class="price-label">运费</span>
          <span class="price-value">¥0.00</span>
        </div>
      </div>

      <!-- 备注 -->
      <div class="section remark-section">
        <div class="section-label">订单备注</div>
        <van-field
          v-model="remark"
          placeholder="选填，可备注特殊需求"
          autosize
          type="textarea"
          maxlength="100"
          show-word-limit
        />
      </div>
    </div>

    <!-- 底部提交栏 -->
    <div class="submit-bar">
      <div class="submit-price">
        <span class="price-label">合计：</span>
        <span class="price-value">¥{{ finalPrice.toFixed(2) }}</span>
      </div>
      <van-button 
        type="danger" 
        class="submit-btn" 
        :disabled="!canSubmit"
        @click="submitOrder"
        :loading="submitting"
      >
        提交订单
      </van-button>
    </div>

    <!-- 地址选择弹窗 -->
    <van-popup v-model:show="showAddressPicker" position="bottom" round>
      <div class="address-picker">
        <div class="picker-header">
          <span>选择收货地址</span>
          <van-icon name="cross" @click="showAddressPicker = false" />
        </div>
        <div class="address-list">
          <div 
            v-for="addr in addressList" 
            :key="addr.id"
            :class="['address-item', { selected: selectedAddress?.id === addr.id }]"
            @click="selectAddress(addr)"
          >
            <div class="address-content">
              <div class="address-header">
                <span class="receiver-name">{{ addr.name }}</span>
                <span class="receiver-phone">{{ addr.phone }}</span>
                <van-tag v-if="addr.isDefault === 1" type="danger">默认</van-tag>
              </div>
              <div class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</div>
            </div>
            <van-icon v-if="selectedAddress?.id === addr.id" name="success" color="#ee0a24" />
          </div>
          <div class="add-address" @click="goAddAddress">
            <van-icon name="plus" color="#ee0a24" />
            <span>添加新地址</span>
          </div>
        </div>
      </div>
    </van-popup>

    <!-- 优惠券选择弹窗 -->
    <van-popup v-model:show="showCouponPicker" position="bottom" round>
      <div class="coupon-picker">
        <div class="picker-header">
          <span>选择优惠券</span>
          <van-icon name="cross" @click="showCouponPicker = false" />
        </div>
        <div class="coupon-list">
          <div 
            class="coupon-item"
            :class="{ selected: selectedCoupon === null, disabled: false }"
            @click="selectCoupon(null)"
          >
            <div class="coupon-left">
              <span class="coupon-amount">不使用</span>
            </div>
            <van-icon v-if="selectedCoupon === null" name="success" color="#ee0a24" />
          </div>
          <div 
            v-for="coupon in availableCoupons" 
            :key="coupon.userCouponId"
            :class="['coupon-item', { selected: selectedCoupon?.userCouponId === coupon.userCouponId, disabled: false }]"
            @click="selectCoupon(coupon)"
          >
            <div class="coupon-left">
              <span class="coupon-amount">¥{{ coupon.discountValue }}</span>
            </div>
            <div class="coupon-right">
              <span class="coupon-name">{{ coupon.name }}</span>
              <span class="coupon-condition">满{{ coupon.minAmount }}可用</span>
            </div>
            <van-icon v-if="selectedCoupon?.userCouponId === coupon.userCouponId" name="success" color="#ee0a24" />
          </div>
          <van-empty v-if="availableCoupons.length === 0" description="暂无可用优惠券" />
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { getAddresses, type Address, type Coupon, createOrder, getProductSkus } from '@/api/order'
import { createPayment, type CreatePaymentResponse } from '@/api/payment'
import { removeFromCart } from '@/api/cart'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()
const defaultImage = 'https://via.placeholder.com/200x200?text=No+Image'

// 结算商品列表
interface CheckoutItem {
  id: number  // 购物车项ID
  productId: number
  skuId?: number  // SKU ID（可选）
  productName: string
  productImage: string
  price: number
  quantity: number
}

const checkoutItems = ref<CheckoutItem[]>([])
const addressList = ref<Address[]>([])
const selectedAddress = ref<Address | null>(null)
const availableCoupons = ref<Coupon[]>([])
const selectedCoupon = ref<Coupon | null>(null)
const selectedPayment = ref<'wechat' | 'alipay'>('wechat')
const remark = ref('')
const submitting = ref(false)

const showAddressPicker = ref(false)
const showCouponPicker = ref(false)

// 计算价格
const totalPrice = computed(() => {
  return checkoutItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return 0
  return selectedCoupon.value.discountValue
})

const finalPrice = computed(() => {
  return Math.max(0, totalPrice.value - couponDiscount.value)
})

const canSubmit = computed(() => {
  return selectedAddress.value && checkoutItems.value.length > 0
})

// 获取用户ID
const getUserId = (): number => {
  return userStore.userInfo?.id || 1
}

// 加载地址列表
const loadAddresses = async () => {
  try {
    const res = await getAddresses(getUserId())
    if (res.code === 200 && res.data) {
      addressList.value = res.data
      selectedAddress.value = res.data.find(a => a.isDefault === 1) || res.data[0] || null
    }
  } catch (error) {
    console.error('加载地址失败:', error)
  }
}

// 选择地址
const selectAddress = (addr: Address) => {
  selectedAddress.value = addr
  showAddressPicker.value = false
}

// 跳转添加地址
const goAddAddress = () => {
  showAddressPicker.value = false
  router.push('/my/address')
}

// 选择优惠券
const selectCoupon = (coupon: Coupon | null) => {
  selectedCoupon.value = coupon
  showCouponPicker.value = false
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 提交订单
const submitOrder = async () => {
  if (!selectedAddress.value) {
    showToast('请选择收货地址')
    return
  }
  
  if (checkoutItems.value.length === 0) {
    showToast('没有可结算的商品')
    return
  }

  submitting.value = true
  showLoadingToast({ message: '正在提交...', forbidClick: true, duration: 0 })

  try {
    // 1. 为每个商品获取SKU列表，并选择第一个可用的SKU
    const itemsWithSkuId = await Promise.all(
      checkoutItems.value.map(async (item) => {
        // 如果已经有skuId，直接使用
        if (item.skuId) {
          return { skuId: item.skuId, quantity: item.quantity }
        }
        
        // 否则获取该商品的SKU列表
        try {
          const skuRes = await getProductSkus(item.productId)
          if (skuRes.code === 200 && skuRes.data?.skus && skuRes.data.skus.length > 0) {
            // 选择第一个有库存的SKU
            const availableSku = skuRes.data.skus.find(sku => sku.stock > 0) || skuRes.data.skus[0]
            return { skuId: availableSku.id, quantity: item.quantity }
          } else {
            throw new Error(`商品 ${item.productName} 无可用规格`)
          }
        } catch (error) {
          console.error(`获取商品 ${item.productId} SKU失败:`, error)
          throw new Error(`获取商品规格失败：${item.productName}`)
        }
      })
    )

    // 2. 创建订单
    const orderData = {
      userId: getUserId(),
      addressId: selectedAddress.value.id,
      items: itemsWithSkuId
    }

    console.log('提交订单数据:', JSON.stringify(orderData, null, 2))

    const orderRes = await createOrder(orderData)
    
    if (orderRes.code !== 200 || !orderRes.data) {
      closeToast()
      showToast(orderRes.message || '创建订单失败')
      return
    }

    const orderId = orderRes.data.orderId
    const orderNo = orderRes.data.orderNo

    // 2. 创建支付
    const payType = selectedPayment.value === 'wechat' ? 1 : 2
    const paymentRes: CreatePaymentResponse = await createPayment({
      orderId,
      userId: getUserId(),
      payType
    })

    closeToast()
    submitting.value = false
    await nextTick()

    if (paymentRes.code === 200 && paymentRes.data) {
      await deleteCheckedItems()
      router.replace({
        path: '/payment/success',
        query: {
          orderId: orderId.toString(),
          orderNo: orderNo,
          amount: paymentRes.data.amount.toString()
        }
      })
    } else {
      await deleteCheckedItems()
      router.replace({
        path: '/payment/failed',
        query: {
          orderId: orderId.toString(),
          orderNo: orderNo,
          amount: finalPrice.value.toString(),
          message: paymentRes.message || '支付失败'
        }
      })
    }
  } catch (error) {
    console.error('提交订单失败:', error)
    closeToast()
    showToast('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 删除购物车中已结算的商品
const deleteCheckedItems = async () => {
  const userId = getUserId()
  try {
    // 并发删除所有已结算的商品
    const deletePromises = checkoutItems.value.map(item => 
      removeFromCart({ userId, cartId: item.id })
    )
    
    await Promise.all(deletePromises)
    console.log('已删除购物车中的商品:', checkoutItems.value.length, '个')
    
    // 刷新购物车数据
    await cartStore.refreshCart()
  } catch (error) {
    console.error('删除购物车商品失败:', error)
    // 即使删除失败也不影响流程，只是记录错误
  }
}

onMounted(() => {
  // 从 URL query 获取结算商品
  const itemsStr = route.query.items as string
  if (itemsStr) {
    try {
      checkoutItems.value = JSON.parse(itemsStr)
      console.log('结算商品列表:', checkoutItems.value)
    } catch (e) {
      console.error('解析商品数据失败:', e)
      showToast('商品信息加载失败')
    }
  } else {
    console.warn('未找到结算商品数据')
    showToast('没有可结算的商品')
  }

  loadAddresses()
})
</script>

<style scoped lang="scss">
.checkout {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;
}

.checkout-content {
  padding: 10px;
}

.section {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  padding: 15px;
}

// 地址区域
.address-section {
  display: flex;
  align-items: center;
  cursor: pointer;
  
  .address-icon {
    margin-right: 12px;
  }
  
  .address-info {
    flex: 1;
    
    .address-header {
      margin-bottom: 4px;
      
      .receiver-name {
        font-weight: 600;
        margin-right: 10px;
      }
      
      .receiver-phone {
        color: #666;
      }
    }
    
    .address-detail {
      color: #666;
      font-size: 13px;
    }
    
    .no-address {
      display: flex;
      align-items: center;
      color: #ee0a24;
      
      span {
        margin-left: 6px;
      }
    }
  }
}

// 商品清单
.goods-section {
  .section-title {
    font-weight: 600;
    margin-bottom: 12px;
  }
  
  .goods-list {
    .goods-item {
      display: flex;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px solid #f5f5f5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .goods-image {
        width: 60px;
        height: 60px;
        border-radius: 4px;
        object-fit: cover;
      }
      
      .goods-info {
        flex: 1;
        margin-left: 12px;
        
        .goods-name {
          font-size: 14px;
          color: #333;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          margin-bottom: 4px;
        }
        
        .goods-price {
          color: #ee0a24;
          font-weight: 600;
        }
      }
      
      .goods-quantity {
        color: #999;
        font-size: 13px;
      }
    }
  }
}

// 优惠券
.coupon-section {
  display: flex;
  align-items: center;
  cursor: pointer;
  
  .section-label {
    font-weight: 600;
  }
  
  .section-value {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    
    .coupon-active {
      color: #ee0a24;
      font-weight: 600;
    }
    
    .coupon-available {
      color: #07c160;
    }
    
    .coupon-none {
      color: #999;
    }
    
    :deep(.van-icon) {
      margin-left: 6px;
    }
  }
}

// 支付方式
.payment-section {
  .section-title {
    font-weight: 600;
    margin-bottom: 12px;
  }
  
  .payment-list {
    .payment-item {
      display: flex;
      align-items: center;
      padding: 12px;
      border: 1px solid #eee;
      border-radius: 8px;
      margin-bottom: 10px;
      cursor: pointer;
      
      &.active {
        border-color: #ee0a24;
        background: #fff5f5;
      }
      
      .payment-icon {
        width: 24px;
        height: 24px;
        margin-right: 10px;
      }
      
      span {
        flex: 1;
      }
    }
  }
}

// 价格明细
.price-section {
  .price-row {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    
    .price-label {
      color: #666;
    }
    
    .price-value {
      color: #333;
      
      &.discount {
        color: #ee0a24;
      }
    }
  }
}

// 备注
.remark-section {
  .section-label {
    font-weight: 600;
    margin-bottom: 10px;
  }
  
  :deep(.van-field) {
    padding: 8px 12px;
    background: #f9f9f9;
  }
}

// 底部提交栏
.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 15px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  
  .submit-price {
    flex: 1;
    
    .price-label {
      color: #666;
    }
    
    .price-value {
      color: #ee0a24;
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  .submit-btn {
    width: 120px;
    height: 40px;
    border-radius: 20px;
  }
}

// 地址选择弹窗
.address-picker {
  max-height: 70vh;
  
  .picker-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    border-bottom: 1px solid #eee;
    font-weight: 600;
  }
  
  .address-list {
    padding: 10px;
    max-height: 60vh;
    overflow-y: auto;
    
    .address-item {
      display: flex;
      align-items: center;
      padding: 12px;
      border: 1px solid #eee;
      border-radius: 8px;
      margin-bottom: 10px;
      cursor: pointer;
      
      &.selected {
        border-color: #ee0a24;
        background: #fff5f5;
      }
      
      .address-content {
        flex: 1;
        
        .address-header {
          margin-bottom: 4px;
          
          .receiver-name {
            font-weight: 600;
            margin-right: 10px;
          }
          
          .receiver-phone {
            color: #666;
            margin-right: 8px;
          }
        }
        
        .address-detail {
          color: #666;
          font-size: 13px;
        }
      }
    }
    
    .add-address {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 15px;
      border: 1px dashed #ee0a24;
      border-radius: 8px;
      color: #ee0a24;
      cursor: pointer;
      
      span {
        margin-left: 8px;
      }
    }
  }
}

// 优惠券选择弹窗
.coupon-picker {
  max-height: 70vh;
  
  .picker-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    border-bottom: 1px solid #eee;
    font-weight: 600;
  }
  
  .coupon-list {
    padding: 10px;
    max-height: 60vh;
    overflow-y: auto;
    
    .coupon-item {
      display: flex;
      align-items: center;
      padding: 15px;
      background: #fff5f5;
      border: 1px solid #f5c6cb;
      border-radius: 8px;
      margin-bottom: 10px;
      cursor: pointer;
      
      &.selected {
        border-color: #ee0a24;
      }
      
      &.disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
      
      .coupon-left {
        .coupon-amount {
          font-size: 20px;
          font-weight: 600;
          color: #ee0a24;
        }
      }
      
      .coupon-right {
        flex: 1;
        margin-left: 15px;
        
        .coupon-name {
          display: block;
          font-weight: 600;
          margin-bottom: 4px;
        }
        
        .coupon-condition {
          color: #666;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
