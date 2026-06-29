<template>
  <div class="order-detail-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="订单详情" left-arrow @click-left="goBack" />

    <!-- 加载状态 -->
    <van-loading v-if="isLoading" type="spinner" size="24px" style="padding: 100px 0;" />

    <!-- 订单内容 -->
    <div v-else-if="orderData" class="order-content">
      <!-- 物流状态 -->
      <div class="logistics-status" :class="getStatusClass(orderData.order.status)">
        <van-icon name="truck-o" size="32" />
        <span class="status-text">{{ labelStatus(orderData.order.status) }}</span>
      </div>

      <!-- 物流信息 -->
      <div v-if="normStatus(orderData.order.status) >= ORDER_STATUS.SHIPPING" class="logistics-info">
        <div class="info-row">
          <van-icon name="location-o" color="#ee0a24" />
          <div class="info-content">
            <div class="info-title">包裹已签收！(凭取件码) 如有问题请电...</div>
            <div class="info-time">{{ orderData.order.shipTime || '' }}</div>
          </div>
        </div>
      </div>

      <!-- 收货地址 -->
      <div class="address-section">
        <div class="address-header">
          <van-icon name="contact" color="#ee0a24" />
          <span class="receiver-name">{{ orderData.address.receiverName }}</span>
          <span class="receiver-phone">{{ maskPhone(orderData.address.receiverPhone) }}</span>
          <van-tag v-if="isProtected" type="success">号码保护中</van-tag>
        </div>
        <div class="address-detail">
          所在地区: {{ orderData.address.province }}{{ orderData.address.city }}{{ orderData.address.district }}
          <span class="expand-btn" @click="showFullAddress = !showFullAddress">
            {{ showFullAddress ? '收起' : '展开' }}
          </span>
        </div>
        <div v-if="showFullAddress" class="address-full">
          {{ orderData.address.detailAddress }}
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="product-section">
        <div class="shop-header">
          <img :src="shopMerchant?.merchantLogo || shopMerchant?.logo || defaultShopLogo" class="shop-logo" />
          <span class="shop-name">{{ shopMerchant?.merchantName || shopMerchant?.name || '未知商家' }}</span>
          <van-tag type="warning" size="medium">回头客好店</van-tag>
          <van-icon name="arrow" />
        </div>

        <div v-for="item in orderData.items" :key="item.id" class="product-item">
          <img :src="item.imageUrl || defaultImage" class="product-image" />
          <div class="product-info">
            <h3 class="product-name">{{ item.productName }}</h3>
            <p class="product-spec">{{ parseSkuAttributes(item.skuAttributes) }}</p>
            <div class="product-tags">
              <van-tag type="success">退货包运费保障中</van-tag>
              <van-tag type="success">7天无理由退货</van-tag>
            </div>
            <div class="product-actions">
              <van-button size="small" plain @click.stop="shareProduct(item.productId)">分享商品</van-button>
              <van-button size="small" plain icon="chat-o" @click.stop="contactMerchant(item.productId)">联系商家</van-button>
              <van-button size="small" type="danger" plain @click.stop="applyRefund(item.id)">申请退款</van-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 价格明细 -->
      <div class="price-section">
        <div class="price-row">
          <span class="label">共优惠</span>
          <span class="value discount">¥{{ (orderData.order.discountAmount + orderData.order.couponAmount).toFixed(2) }}</span>
        </div>
        <div class="price-row total">
          <span class="original-price">¥{{ orderData.order.totalAmount.toFixed(2) }}</span>
          <span class="pay-label">实付</span>
          <span class="pay-price">¥{{ orderData.order.payAmount.toFixed(2) }}</span>
          <span class="free-shipping">(免运费)</span>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="order-info-section">
        <div class="info-row">
          <span class="label">订单号:</span>
          <span class="value">{{ orderData.order.orderNo }}</span>
          <van-button size="mini" plain @click="copyOrderNo">复制</van-button>
        </div>
        <div class="info-row">
          <span class="label">商品快照:</span>
          <span class="value">发生交易争议时，可作为判断依据</span>
          <van-icon name="arrow-down" />
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div class="bottom-actions">
        <van-button size="large" plain @click="showMore">更多</van-button>
        <van-button v-if="normStatus(orderData.order.status) === ORDER_STATUS.UNPAID" size="large" type="danger" @click="goPay">去支付</van-button>
        <van-button v-if="normStatus(orderData.order.status) === ORDER_STATUS.COMPLETED" size="large" type="primary" @click="buyAgain">再次拼单</van-button>
        <van-button v-if="normStatus(orderData.order.status) >= ORDER_STATUS.SHIPPING && normStatus(orderData.order.status) <= ORDER_STATUS.RECEIVING" size="large" plain @click="checkLogistics">查看物流</van-button>
        <van-button v-if="normStatus(orderData.order.status) === ORDER_STATUS.RECEIVING" size="large" type="danger" @click="confirmReceipt">确认收货</van-button>
      </div>
    </div>

    <!-- 热门商品推荐 -->
    <div class="hot-products-section">
      <div class="products-grid">
        <div 
          v-for="product in hotProducts" 
          :key="product.id" 
          class="product-card"
          @click="goProductDetail(product.id)"
        >
          <img :src="product.mainImage || defaultImage" class="product-image" />
          <div class="product-info">
            <h4 class="product-name">{{ product.productName }}</h4>
            <div class="price-row">
              <span class="current-price">¥{{ product.price.toFixed(2) }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice.toFixed(2) }}</span>
            </div>
            <div class="sales-text">销量: {{ product.sales }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getOrderDetail, updateOrderStatus, type OrderDetail as OrderDetailType } from '@/api/order'
import { getProductDetail } from '@/api/recommend'
import { normalizeStatus, getStatusLabel, ORDER_STATUS } from '@/utils/orderStatus'
import { getHotProducts, type Product } from '@/api/recommend'

const router = useRouter()
const route = useRoute()
const defaultImage = 'https://via.placeholder.com/200x200?text=No+Image'

// 数据
const isLoading = ref(false)
const orderData = ref<OrderDetailType | null>(null)
const hotProducts = ref<Product[]>([])
const showFullAddress = ref(false)
const isProtected = ref(true)
const defaultShopLogo = 'https://via.placeholder.com/40x40?text=Shop'

const shopMerchant = ref<{ id: number; merchantName?: string; name?: string; merchantLogo?: string; logo?: string } | null>(null)

const normStatus = (s: number) => normalizeStatus(s)
const labelStatus = (s: number) => getStatusLabel(s)

const goBack = () => {
  router.back()
}

const productImageCacheDetail = new Map<number, string>()
const productMerchantCache = new Map<number, { id: number; merchantName?: string; name?: string; merchantLogo?: string; logo?: string }>()

async function fillItemImagesDetail(items: any[]): Promise<void> {
  for (const it of items) {
    if (it.imageUrl || !it.productId) continue
    if (productImageCacheDetail.has(it.productId)) {
      it.imageUrl = productImageCacheDetail.get(it.productId)!
      continue
    }
    try {
      const r = await getProductDetail(it.productId)
      const img = r?.data?.product?.mainImage || r?.data?.product?.image || r?.data?.product?.cover
      if (img) {
        productImageCacheDetail.set(it.productId, img)
        it.imageUrl = img
      }
    } catch {}
  }
}

async function fetchMerchantByProduct(productId: number) {
  if (productMerchantCache.has(productId)) {
    return productMerchantCache.get(productId)!
  }
  try {
    const r = await getProductDetail(productId)
    const m = r?.data?.merchant
    if (m && m.id) {
      const norm = {
        id: m.id,
        merchantName: (m as any).merchantName || (m as any).name,
        name: (m as any).merchantName || (m as any).name,
        merchantLogo: (m as any).merchantLogo || (m as any).logo,
        logo: (m as any).merchantLogo || (m as any).logo
      }
      productMerchantCache.set(productId, norm)
      return norm
    }
  } catch {}
  return null
}

async function resolveShopMerchant(order: any, items: any[]): Promise<void> {
  if (order && order.merchant && (order.merchant.name || order.merchant.merchantName)) {
    shopMerchant.value = {
      id: order.merchant.id,
      merchantName: order.merchant.name || order.merchant.merchantName,
      name: order.merchant.name || order.merchant.merchantName,
      merchantLogo: order.merchant.logo || order.merchant.merchantLogo,
      logo: order.merchant.logo || order.merchant.merchantLogo
    }
    return
  }
  const targetId = order?.merchantId
  for (const it of items) {
    if (!it.productId) continue
    const m = await fetchMerchantByProduct(it.productId)
    if (m && (!targetId || m.id === targetId)) {
      shopMerchant.value = m
      return
    }
    if (m && !shopMerchant.value) shopMerchant.value = m
  }
}

const loadOrderDetail = async () => {
  const orderId = Number(route.params.id)
  if (!orderId) {
    showToast('订单ID无效')
    return
  }

  isLoading.value = true
  shopMerchant.value = null
  try {
    const result = await getOrderDetail(orderId)
    if (result.code === 200 && result.data) {
      orderData.value = result.data
      await Promise.all([
        fillItemImagesDetail(orderData.value?.items || []),
        resolveShopMerchant(orderData.value.order, orderData.value.items || [])
      ])
    } else {
      showToast(result.message || '获取订单详情失败')
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    showToast('加载失败')
  } finally {
    isLoading.value = false
  }
}

// 加载热门商品
const loadHotProducts = async () => {
  try {
    const result = await getHotProducts(6)
    if (result.code === 200 && result.data) {
      hotProducts.value = result.data
    }
  } catch (error) {
    console.error('加载热门商品失败:', error)
  }
}

// 获取状态文本
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    [ORDER_STATUS.UNPAID]: '待付款',
    [ORDER_STATUS.SHIPPING]: '待发货',
    [ORDER_STATUS.RECEIVING]: '待收货',
    [ORDER_STATUS.COMPLETED]: '已完成',
    [ORDER_STATUS.CANCELLED]: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态样式类
const getStatusClass = (status: number): string => {
  const classMap: Record<number, string> = {
    [ORDER_STATUS.UNPAID]: 'status-unpaid',
    [ORDER_STATUS.SHIPPING]: 'status-paid',
    [ORDER_STATUS.RECEIVING]: 'status-shipped',
    [ORDER_STATUS.COMPLETED]: 'status-completed',
    [ORDER_STATUS.CANCELLED]: 'status-cancelled'
  }
  return classMap[status] || ''
}

// 手机号脱敏
const maskPhone = (phone: string): string => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 解析SKU属性
const parseSkuAttributes = (attributes: string): string => {
  try {
    const attrs = JSON.parse(attributes)
    return Object.values(attrs).join(', ')
  } catch {
    return attributes
  }
}



// 复制订单号
const copyOrderNo = () => {
  if (orderData.value) {
    navigator.clipboard.writeText(orderData.value.order.orderNo)
    showToast('订单号已复制')
  }
}

// 操作按钮
const showMore = () => {
  showToast('更多操作')
}

const goPay = () => {
  showToast('去支付')
}

const buyAgain = () => {
  showToast('再次购买')
}

const checkLogistics = () => {
  showToast('查看物流')
}

const confirmReceipt = async () => {
  if (!orderData.value) return
  
  try {
    await showConfirmDialog({
      title: '确认收货',
      message: '确认收到商品了吗？'
    })
    
    const result = await updateOrderStatus(orderData.value.order.id, 3)
    if (result.code === 200) {
      showToast('确认收货成功')
      loadOrderDetail() // 重新加载
    } else {
      showToast(result.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('操作失败')
    }
  }
}

const shareProduct = (productId: number) => {
  showToast(`分享商品 ${productId}`)
}

const contactMerchant = (productId: number) => {
  router.push(`/customer-service/0/${productId}`)
}

const applyRefund = (itemId: number) => {
  showToast(`申请退款 ${itemId}`)
}

const goProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

onMounted(() => {
  loadOrderDetail()
  loadHotProducts()
})
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}

/* 物流状态 */
.logistics-status {
  background: white;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.logistics-status.status-completed {
  color: #07c160;
}

.logistics-status.status-shipped {
  color: #ff976a;
}

.logistics-status.status-paid {
  color: #1989fa;
}

.logistics-status.status-unpaid {
  color: #ee0a24;
}

.status-text {
  font-size: 18px;
  font-weight: bold;
}

/* 物流信息 */
.logistics-info {
  background: white;
  padding: 15px;
  margin-bottom: 10px;
}

.info-row {
  display: flex;
  gap: 12px;
}

.info-content {
  flex: 1;
}

.info-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.info-time {
  font-size: 12px;
  color: #999;
}

/* 收货地址 */
.address-section {
  background: white;
  padding: 15px;
  margin-bottom: 10px;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.receiver-name {
  font-weight: bold;
  font-size: 15px;
}

.receiver-phone {
  color: #666;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.expand-btn {
  color: #999;
  cursor: pointer;
  margin-left: 8px;
}

.address-full {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
  line-height: 1.5;
}

/* 商品信息 */
.product-section {
  background: white;
  margin-bottom: 10px;
}

.shop-header {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #f5f5f5;
}

.shop-logo {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  margin-right: 10px;
}

.shop-name {
  font-weight: bold;
  font-size: 15px;
  margin-right: 8px;
}

.product-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #f5f5f5;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
  margin-right: 12px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin: 0 0 6px 0;
  line-height: 1.4;
}

.product-spec {
  font-size: 13px;
  color: #999;
  margin: 0 0 8px 0;
}

.product-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 10px;
}

.product-actions {
  display: flex;
  gap: 8px;
}

/* 价格明细 */
.price-section {
  background: white;
  padding: 15px;
  margin-bottom: 10px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
}

.price-row.total {
  border-top: 1px solid #f5f5f5;
  padding-top: 12px;
  margin-top: 6px;
}

.label {
  color: #666;
  font-size: 14px;
}

.value {
  color: #333;
  font-size: 14px;
}

.value.discount {
  color: #ee0a24;
}

.original-price {
  color: #999;
  font-size: 14px;
  text-decoration: line-through;
}

.pay-label {
  color: #666;
  font-size: 14px;
  margin-left: auto;
  margin-right: 8px;
}

.pay-price {
  color: #ee0a24;
  font-size: 18px;
  font-weight: bold;
}

.free-shipping {
  color: #999;
  font-size: 12px;
  margin-left: 8px;
}

/* 订单信息 */
.order-info-section {
  background: white;
  padding: 15px;
  margin-bottom: 10px;
}

.order-info-section .info-row {
  display: flex;
  align-items: center;
  padding: 8px 0;
  font-size: 13px;
}

.order-info-section .label {
  color: #999;
  min-width: 80px;
}

.order-info-section .value {
  color: #666;
  flex: 1;
}

/* 底部操作栏 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 10px 15px;
  display: flex;
  gap: 10px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.bottom-actions .van-button {
  flex: 1;
}

/* 热门商品区域 */
.hot-products-section {
  margin-top: 20px;
  padding: 10px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
}

.product-card .product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  margin: 0;
}

.product-card .product-info {
  padding: 10px;
}

.product-card .product-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 8px;
}

.product-card .price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.product-card .current-price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: bold;
}

.product-card .original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
}

.product-card .sales-text {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
}
</style>
