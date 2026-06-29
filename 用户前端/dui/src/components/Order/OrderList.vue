<template>
  <div class="order-list-page">
    <!-- 顶部导航 -->
    <div class="order-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">我的订单</span>
      <van-icon name="search" class="search-btn" />
      <van-icon name="qr" class="qr-btn" />
      <van-icon name="share-o" class="share-btn" />
    </div>

    <!-- 订单状态标签 -->
    <div class="order-tabs">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-item', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        <span>{{ tab.name }}</span>
        <van-badge v-if="tab.count > 0" :content="tab.count" class="tab-badge" />
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <!-- 加载状态 -->
      <van-loading v-if="isLoading" type="spinner" size="24px" style="padding: 40px 0;" />

      <!-- 空状态 -->
      <van-empty v-else-if="filteredOrders.length === 0" description="暂无订单" />

      <!-- 订单卡片 -->
      <div v-else class="order-card" v-for="order in filteredOrders" :key="order.id">
        <van-swipe-cell>
          <div class="order-content" @click="goToOrderDetail(order.id)">
            <!-- 店铺信息 -->
            <div class="shop-info">
              <img :src="resolveShopLogo(order.id)" class="shop-logo" />
              <span class="shop-name">{{ resolveShopName(order.id) }}</span>
              <van-tag type="warning" size="medium">回头客好店</van-tag>
              <van-icon name="arrow" />
            </div>

            <!-- 商品列表 -->
            <div class="order-items">
              <template v-if="orderItemsMap[order.id] && orderItemsMap[order.id].length > 0">
                <div v-for="item in orderItemsMap[order.id]" :key="item.id" class="order-item">
                  <img :src="item.imageUrl || defaultImage" class="item-image" />
                  <div class="item-info">
                    <h4 class="item-title">{{ item.productName }}</h4>
                    <p class="item-spec">{{ parseSkuAttributes(item.skuAttributes) }}</p>
                    <div class="item-bottom">
                      <span class="item-price">¥{{ item.price.toFixed(2) }}</span>
                      <span class="item-total">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
              </template>
              <div v-else class="empty-items">
                <van-loading size="20px" />
              </div>
            </div>

            <!-- 订单底部 -->
            <div class="order-bottom">
              <div class="order-total">
                <span class="total-label">实付:</span>
                <span class="total-price">¥{{ order.payAmount }}</span>
              </div>
              <div class="order-actions">
                <van-button size="small" type="default" plain @click="showMore(order.id)">更多</van-button>
                <van-button v-if="order.status === 1" size="small" type="danger" @click="applyRefund(order.id)">去支付</van-button>
                <van-button v-if="order.status === 3" size="small" type="danger" @click="confirmReceipt(order.id)">确认收货</van-button>
                <van-button v-if="normStatus(order.status) >= ORDER_STATUS.SHIPPING && normStatus(order.status) <= ORDER_STATUS.RECEIVING" size="small" type="default" plain @click="checkLogistics(order.id)">查看物流</van-button>
              </div>
            </div>
          </div>
          <template #right>
            <van-button
              square
              type="danger"
              text="删除"
              class="delete-btn"
              @click="deleteOrderItem(order.id)"
            />
          </template>
        </van-swipe-cell>
      </div>
    </div>

    <!-- 热门商品 -->
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

    <!-- 底部提示 -->
    <div class="bottom-tip">
      <span>精品百货一站购物</span>
      <van-icon name="arrow" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import { useRouter, useRoute } from 'vue-router'
import { getOrderList, getOrderDetail, updateOrderStatus, deleteOrder, type Order, type OrderItem } from '@/api/order'
import { getProductDetail } from '@/api/recommend'
import { normalizeStatus, ORDER_STATUS } from '@/utils/orderStatus'
import { getHotProducts, type Product } from '@/api/recommend'
import { useUserStore } from '@/stores/user'

// 创建路由实例
const router = useRouter()
const route = useRoute()
const normStatus = (s: number) => normalizeStatus(s)
const userStore = useUserStore()
const defaultImage = 'https://via.placeholder.com/200x200?text=No+Image'

// 返回
const goBack = () => {
  router.back()
}

// 监听路由参数
onMounted(() => {
  const status = route.query.status as string
  if (status && status !== 'all') {
    activeTab.value = status
  }
  // 加载订单数据
  loadOrders()
  // 加载热门商品
  loadHotProducts()
})

watch(() => route.query.status, (newStatus) => {
  if (newStatus && newStatus !== 'all') {
    activeTab.value = newStatus as string
  }
})

// 标签页
const tabs = ref([
  { key: 'all', name: '全部', count: 0 },
  { key: 'unpaid', name: '待付款', count: 0 },
  { key: 'group', name: '拼团中', count: 0 },
  { key: 'packing', name: '打包中', count: 0 },
  { key: 'shipping', name: '待收货', count: 0 },
  { key: 'review', name: '评价', count: 0 }
])

const activeTab = ref('all')

// 订单数据
const orders = ref<Order[]>([])
const isLoading = ref(false)
const orderItemsMap = ref<Record<number, OrderItem[]>>({})
const orderMerchantMap = ref<Record<number, { id: number; merchantName?: string; name?: string; merchantLogo?: string; logo?: string }>>({})
const defaultShopLogo = 'https://via.placeholder.com/40x40?text=Shop'

const getShopMerchant = (orderId: number) => orderMerchantMap.value[orderId]
const resolveShopName = (orderId: number) => getShopMerchant(orderId)?.merchantName || getShopMerchant(orderId)?.name || '未知商家'
const resolveShopLogo = (orderId: number) => getShopMerchant(orderId)?.merchantLogo || getShopMerchant(orderId)?.logo || defaultShopLogo

// 热门商品数据
const hotProducts = ref<Product[]>([])

// 加载热门商品
const loadHotProducts = async () => {
  try {
    const result = await getHotProducts(6) // 获取6个热门商品
    if (result.code === 200 && result.data) {
      hotProducts.value = result.data
    }
  } catch (error) {
    console.error('加载热门商品失败:', error)
  }
}

// 跳转到热销榜单
const goToHotList = () => {
  router.push('/hot-list')
}

// 跳转商品详情
const goProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 跳转订单详情
const goToOrderDetail = (orderId: number) => {
  router.push(`/order/detail/${orderId}`)
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


const loadOrders = async () => {
  const userId = userStore.userInfo?.id
  console.log('=== 开始加载订单 ===')
  console.log('当前用户信息:', userStore.userInfo)
  console.log('用户ID:', userId)
  
  if (!userId) {
    showToast('请先登录')
    return
  }

  isLoading.value = true
  try {
    console.log('调用API获取订单列表, userId:', userId)
    const result = await getOrderList(userId)
    console.log('API响应结果:', result)
    console.log('响应码:', result.code)
    console.log('订单数据:', result.data)
    console.log('订单数量:', result.data ? result.data.length : 0)
    
    if (result.code === 200 && result.data) {
      orders.value = result.data
      console.log('成功加载', orders.value.length, '条订单')
      // 更新各状态的数量
      updateTabCounts(result.data)
      // 加载每个订单的商品信息
      await loadOrderItems(result.data)
    } else {
      console.error('获取订单失败，响应码:', result.code)
      showToast(result.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    showToast('加载订单失败')
  } finally {
    isLoading.value = false
  }
}

// 加载订单商品信息
const productImageCache = new Map<number, string>()
const productMerchantCache = new Map<number, { id: number; merchantName?: string; name?: string; merchantLogo?: string; logo?: string }>()

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

async function resolveOrderMerchant(order: Order, items: OrderItem[]): Promise<void> {
  if (order && order.merchant && (order.merchant.name || (order.merchant as any).merchantName)) {
    orderMerchantMap.value[order.id] = {
      id: order.merchant.id,
      merchantName: order.merchant.name || (order.merchant as any).merchantName,
      name: order.merchant.name || (order.merchant as any).merchantName,
      merchantLogo: order.merchant.logo || (order.merchant as any).merchantLogo,
      logo: order.merchant.logo || (order.merchant as any).merchantLogo
    }
    return
  }
  const targetId = order?.merchantId
  for (const it of items) {
    if (!it.productId) continue
    const m = await fetchMerchantByProduct(it.productId)
    if (m && (!targetId || m.id === targetId)) {
      orderMerchantMap.value[order.id] = m
      return
    }
    if (m && !orderMerchantMap.value[order.id]) orderMerchantMap.value[order.id] = m
  }
}

async function fillItemImages(items: OrderItem[]): Promise<void> {
  const needFetch: number[] = []
  for (const it of items) {
    if (it.imageUrl || !it.productId) continue
    if (productImageCache.has(it.productId)) {
      it.imageUrl = productImageCache.get(it.productId)!
    } else {
      needFetch.push(it.productId)
    }
  }
  if (needFetch.length === 0) return
  const unique = [...new Set(needFetch)]
  await Promise.all(unique.map(async (pid) => {
    try {
      const r = await getProductDetail(pid)
      const img = r?.data?.product?.mainImage || r?.data?.product?.image || r?.data?.product?.cover
      if (img) {
        productImageCache.set(pid, img)
        for (const it of items) {
          if (it.productId === pid && !it.imageUrl) it.imageUrl = img
        }
      }
    } catch {}
  }))
}

const loadOrderItems = async (orderList: Order[]) => {
  const promises = orderList.map(async (order) => {
    try {
      const result = await getOrderDetail(order.id)
      if (result.code === 200 && result.data) {
        const items = result.data.items
        await Promise.all([
          fillItemImages(items),
          resolveOrderMerchant(order, items)
        ])
        orderItemsMap.value[order.id] = items
      }
    } catch (error) {
      console.error(`???? ${order.id} ??????:`, error)
    }
  })
  await Promise.all(promises)
}


// 更新标签数量
const updateTabCounts = (orderList: Order[]) => {
  const counts = {
    unpaid: 0,
    packing: 0,
    shipping: 0,
    review: 0
  }

  orderList.forEach(order => {
    switch (order.status) {
      case 1: // 待付款
        counts.unpaid++
        break
      case 2: // 待发货
        counts.packing++
        break
      case 3: // 待收货
        counts.shipping++
        break
      case 4: // 已完成
        counts.review++
        break
    }
  })

  tabs.value.find(t => t.key === 'unpaid')!.count = counts.unpaid
  tabs.value.find(t => t.key === 'packing')!.count = counts.packing
  tabs.value.find(t => t.key === 'shipping')!.count = counts.shipping
  tabs.value.find(t => t.key === 'review')!.count = counts.review
  tabs.value.find(t => t.key === 'all')!.count = orderList.length
}

// 根据当前选中的标签过滤订单
const filteredOrders = computed(() => {
  if (activeTab.value === 'all') {
    return orders.value
  }

  // 将前端标签映射到后端状态
  const statusMap: Record<string, number[]> = {
    unpaid: [ORDER_STATUS.UNPAID],
    group: [],
    packing: [ORDER_STATUS.SHIPPING],
    shipping: [ORDER_STATUS.RECEIVING],
    review: [ORDER_STATUS.COMPLETED]
  }

  const targetStatuses = statusMap[activeTab.value] || []
  return orders.value.filter(order => targetStatuses.includes(order.status))
})



// 订单操作
const showMore = (id: number) => {
  showToast(`订单 ${id} 更多操作`)
}

const applyRefund = async (id: number) => {
  showToast(`订单 ${id} 申请退款`)
}

const checkLogistics = async (id: number) => {
  showToast(`订单 ${id} 查看物流`)
}

const confirmReceipt = async (id: number) => {
  try {
    const result = await updateOrderStatus(id, 3) // 状态改为已完成
    if (result.code === 200) {
      showToast('确认收货成功')
      loadOrders() // 重新加载订单列表
    } else {
      showToast(result.message || '确认收货失败')
    }
  } catch (error) {
    console.error('确认收货失败:', error)
    showToast('操作失败')
  }
}

// 删除订单（取消订单）
const deleteOrderItem = async (id: number) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除该订单吗？删除后将无法恢复。'
    })
    
    const result = await deleteOrder(id)
    if (result.code === 200) {
      showToast('订单已删除')
      loadOrders() // 重新加载订单列表
    } else {
      showToast(result.message || '删除失败')
    }
  } catch (error) {
    // 用户取消或操作失败
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      showToast('删除失败')
    }
  }
}
</script>

<style scoped>
.order-list-page {
  min-height: 100vh;
  background: #f7f8fa;
}

/* 顶部导航 */
.order-header {
  background: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  font-size: 20px;
  color: #333;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.search-btn, .qr-btn, .share-btn {
  font-size: 18px;
  color: #666;
}

/* 订单标签 */
.order-tabs {
  background: white;
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.tab-item {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #666;
  position: relative;
  padding-bottom: 8px;
}

.tab-item.active {
  color: #ff4757;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background: #ff4757;
}

.tab-badge {
  margin-left: 4px;
}

/* 优惠券横幅 */
.coupon-banner {
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  margin: 12px;
  border-radius: 8px;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.banner-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.discount-tag {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.banner-text {
  font-size: 14px;
}

/* 折扣优惠券 */
.discount-card {
  background: white;
  margin: 0 12px 12px;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.discount-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.discount-percent {
  font-size: 24px;
  font-weight: bold;
  color: #ff4757;
}

.discount-label {
  font-size: 14px;
  color: #333;
}

.discount-time {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}

.use-btn {
  flex-shrink: 0;
}

/* 订单列表 */
.order-list {
  padding: 0 12px;
}

.order-card {
  background: white;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.order-content {
  padding: 0;
}

.delete-btn {
  height: 100%;
}

/* 店铺信息 */
.shop-info {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.shop-logo {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  margin-right: 10px;
}

.shop-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  flex: 1;
}

.official-tag {
  background: #ff4757;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 8px;
}

/* 商品列表 */
.order-items {
  padding: 12px 16px;
}

.order-item {
  display: flex;
  gap: 12px;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-title {
  font-size: 13px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-spec {
  font-size: 12px;
  color: #999;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.item-price {
  font-size: 15px;
  font-weight: bold;
  color: #ff4757;
}

.item-total {
  font-size: 12px;
  color: #999;
}

/* 服务标签 */
.service-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 0 16px 12px;
}

.service-tag {
  font-size: 11px;
  color: #2ed573;
  background: #e8f5e9;
  padding: 2px 8px;
  border-radius: 4px;
}

/* 订单底部 */
.order-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid #f5f5f5;
}

.order-total {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.total-label {
  font-size: 13px;
  color: #666;
}

.total-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff4757;
}

.free-tag {
  font-size: 11px;
  color: #2ed573;
  background: #e8f5e9;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 8px;
}

.order-actions {
  display: flex;
  gap: 8px;
}

/* 物流信息 */
.logistics-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fffbeb;
  border-top: 1px solid #fef3c7;
}

.logistics-icon {
  color: #f59e0b;
  font-size: 14px;
}

.logistics-text {
  font-size: 12px;
  color: #92400e;
}

/* 底部提示 */
.bottom-tip {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  color: #999;
  font-size: 13px;
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

.product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.product-info {
  padding: 10px;
}

.product-name {
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

.price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.current-price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: bold;
}

.original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
}

.sales-text {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
}
</style>
