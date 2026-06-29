<template>
  <div class="product-detail">
    <!-- 商品头部 -->
    <ProductHeader 
      :product="currentProduct" 
      :carousel-images="currentImages"
      @back="goBack"
    />

    <!-- 拼单用户区域 -->
    <GroupBuyUsers />

    <!-- 发货信息区域 -->
    <div class="shipping-info" @click="showShippingPopup = true">
      <div class="shipping-left">
        <van-icon name="logistics" color="#07c160" />
        <span class="shipping-text">预计11小时内发货</span>
        <span class="shipping-divider">|</span>
        <span class="shipping-promise">承诺48小时内发货，超时必赔</span>
      </div>
      <van-icon name="arrow" color="#999" />
    </div>

    <!-- 专属售后保障区域 -->
    <div class="after-sales-info" @click="showAfterSalesPopup = true">
      <div class="after-sales-left">
        <van-icon name="shield-o" color="#07c160" />
        <span class="after-sales-title">专属售后保障</span>
        <div class="after-sales-tags">
          <span class="after-sales-tag">专属客服</span>
          <span class="after-sales-tag">闪电退换</span>
          <span class="after-sales-tag">售后无忧</span>
        </div>
      </div>
      <van-icon name="arrow" color="#999" />
    </div>

    <!-- 商品评价（只显示2-3条） -->
    <div class="review-section">
      <div class="section-header">
        <h3>商品评价({{ currentReviews.total }})</h3>
        <span class="review-score">{{ currentReviews.avgRating }}分</span>
        <span class="view-all" @click="goViewAllReviews">查看全部</span>
      </div>

      <!-- 评价列表（只显示前2-3条） -->
      <div class="review-list">
        <div class="review-item" v-for="review in currentReviews.list.slice(0, 3)" :key="review.id">
          <van-image :src="review.avatar" class="review-avatar" round />
          <div class="review-content">
            <div class="review-header">
              <span class="review-name">{{ review.name }}</span>
              <van-rate v-model="review.rating" readonly size="14" />
            </div>
            <div class="review-text">{{ review.content }}</div>
            <div class="review-footer">
              <span class="review-time">{{ review.time }}</span>
              <span class="review-helpful" @click="helpfulReview()">
                <van-icon name="thumb-up" />
                {{ review.helpful }}人觉得有用
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 店铺评论 -->
    <div class="shop-review-section">
      <div class="section-header">
        <h3>该商品所属店铺评价({{ shopInfo.totalReviewCount }})</h3>
        <span class="view-all" @click="goViewAllShopReviews">查看全部</span>
      </div>

      <!-- 店铺评价标签 -->
      <div class="shop-review-tags">
        <span 
          v-for="tag in shopReviewTags" 
          :key="tag.name" 
          class="shop-review-tag"
        >
          {{ tag.name }}({{ tag.count }})
        </span>
      </div>

      <!-- 店铺信息 -->
      <div class="shop-info-card">
        <div class="shop-brand-info">
          <van-image :src="currentMerchant.merchantLogo" class="shop-brand-logo" />
          <div class="shop-brand-detail">
            <div class="shop-brand-name">{{ currentMerchant.merchantName }}</div>
            <div class="shop-brand-sales">全店总售{{ currentBrandInfo.totalSales }}</div>
          </div>
        </div>
        <div class="shop-stats">
          <span class="stat-item">近30天全店好评{{ currentBrandInfo.goodReviewCount }}</span>
          <span class="stat-item">近30天已拼{{ currentBrandInfo.recentGroupCount }}</span>
          <span class="stat-item">全店{{ currentBrandInfo.reviewerCount }}人评价</span>
        </div>
        <div class="shop-guarantee">
          <span class="guarantee-label">店铺保障</span>
          <span class="guarantee-value">{{ currentBrandInfo.guaranteeTags.join(' · ') }}</span>
        </div>
        <div class="shop-operator">
          <span>由「{{ currentMerchant.merchantName }}」运营</span>
          <span class="enter-shop-btn" @click="goEnterShop">进店</span>
        </div>
      </div>
    </div>

    <!-- 品牌介绍（富文本） -->
    <div class="brand-section">
      <div class="brand-header">
        <van-image :src="currentBrandInfo.brandLogo" class="brand-logo" />
        <div class="brand-info">
          <div class="brand-name">{{ currentBrandInfo.brandName }}</div>
          <div class="brand-since">始于{{ currentBrandInfo.establishedYear }}年</div>
        </div>
      </div>
      <div class="brand-intro">
        <span class="brand-intro-label">品牌介绍</span>
        <van-rich-text :nodes="currentBrandInfo.introduction" class="brand-intro-text" />
      </div>
      <div class="brand-features">
        <div class="feature-item" v-for="feature in currentBrandInfo.features" :key="feature.label">
          <span class="feature-label">{{ feature.label }}</span>
          <span class="feature-value">{{ feature.value }}</span>
        </div>
      </div>
    </div>

    <!-- 商品参数 -->
    <div class="params-section">
      <div class="section-header">
        <h3>商品详情</h3>
        <span class="view-all" @click="goViewAllParams">查看全部</span>
      </div>
      <div class="params-list">
        <div class="param-item" v-for="param in currentParams" :key="param.key">
          <span class="param-label">{{ param.key }}</span>
          <span class="param-value">{{ param.value }}</span>
        </div>
      </div>
    </div>

    <!-- 商品详情图 -->
    <div class="detail-images-section">
      <div class="detail-images-badge">
        <span class="badge-text">百亿补贴 · 正品保障</span>
      </div>
      <div class="detail-images-icons">
        <div class="icon-item">
          <van-icon name="certificate" color="#ee0a24" />
          <span>官方补贴</span>
        </div>
        <div class="icon-item">
          <van-icon name="tag-o" color="#ee0a24" />
          <span>惊喜特价</span>
        </div>
        <div class="icon-item">
          <van-icon name="shield-o" color="#ee0a24" />
          <span>品质保障</span>
        </div>
        <div class="icon-item">
          <van-icon name="service" color="#ee0a24" />
          <span>售后无忧</span>
        </div>
      </div>
      <div class="detail-images">
        <img 
          v-for="(img, index) in currentDetailImages" 
          :key="index" 
          :src="img" 
          class="detail-image" 
          lazy-load
        />
      </div>
    </div>

    <!-- 平台免责说明 -->
    <div class="disclaimer-section">
      <div class="section-header">
        <h3>平台免责说明</h3>
      </div>
      <div class="disclaimer-content">
        <div class="disclaimer-item">
          <h4>价格说明</h4>
          <p>单独购买价：是您单独购买商品的价格</p>
          <p>发起拼单价：是您拼单购买商品的价格</p>
          <p>划线价：是指商品展示的参考价，是商品的专柜价、吊牌价、零售价、厂商指导价、曾经展示过的销售价，或其他真实有依据的价格（非原价）。由于地区、时间的差异性和市场行情波动，专柜价、零售价等可能会与您购物时展示的不一致，该价格仅供您参考。</p>
          <p>官方补贴价：商品展示的官方补贴价为该商品在专柜价、吊牌价、零售价、厂商指导价、销售价，或其他真实有依据的价格（非原价）基础上进行补贴后的价格。由于地区、时间的差异性和市场行情波动，补贴前的价格仅供参考。</p>
          <p>特别提示：实际的成交价格可能因您使用优惠券等发生变化，最终以商家结算页的价格为准。若商家单独对价格进行说明的，以商家的表述为准。如您发现活动商品售价或促销信息存在异常，建议购买前先联系商家进行咨询。</p>
        </div>
        <div class="disclaimer-item">
          <h4>消费说明</h4>
          <p>为保障消费者购买公平性，避免非生活消费目的购买、囤积抬价转售等违法违规行为，拼多多或商家将对部分商品进行限购，并限制异常订单、异常用户。发现异常时，系统可能会取消同一用户、异常用户的订单、取消活动资格、撤销/收回补贴、奖励等。</p>
        </div>
      </div>
      <div class="disclaimer-footer">
        <p>本商品由商家提供，商品详情页信息由商家自行编辑提供，平台不对其真实性、准确性负责。用户购买商品时请仔细阅读商家提供的商品描述、参数、售后保障等信息，如有疑问可联系商家咨询。</p>
      </div>
    </div>

    <!-- 返回顶部按钮 -->
    <div 
      class="back-top-btn" 
      v-show="showBackTop" 
      @click="scrollToTop"
    >
      <van-icon name="arrow-up" />
    </div>

    <!-- 发货弹窗 -->
    <van-popup v-model:show="showShippingPopup" position="bottom" :style="{ height: '60%' }">
      <div class="shipping-popup-header">
        <van-icon name="map-o" color="#07c160" size="24" />
        <span class="shipping-popup-title">发货与物流</span>
        <van-icon name="cross" @click="showShippingPopup = false" />
      </div>
      <div class="shipping-popup-content">
        <div class="shipping-info-item">
          <div class="shipping-info-label">发货时间</div>
          <div class="shipping-info-value">预计11小时内发货</div>
        </div>
        <div class="shipping-info-item">
          <div class="shipping-info-label">承诺发货时间</div>
          <div class="shipping-info-value">48小时内发货，超时必赔</div>
        </div>
        <div class="shipping-info-item">
          <div class="shipping-info-label">配送方式</div>
          <div class="shipping-info-value">快递配送</div>
        </div>
        <div class="shipping-info-item">
          <div class="shipping-info-label">配送区域</div>
          <div class="shipping-info-value">全国配送（港澳台及海外除外）</div>
        </div>
        <div class="shipping-info-item">
          <div class="shipping-info-label">运费</div>
          <div class="shipping-info-value">免运费</div>
        </div>
        <div class="shipping-info-item">
          <div class="shipping-info-label">预计送达时间</div>
          <div class="shipping-info-value">发货后1-3天送达</div>
        </div>
      </div>
    </van-popup>

    <!-- 售后保障弹窗 -->
    <van-popup v-model:show="showAfterSalesPopup" position="bottom" :style="{ height: '60%' }">
      <div class="after-sales-popup-header">
        <van-icon name="passed" color="#07c160" size="24" />
        <span class="after-sales-popup-title">专属售后保障</span>
        <van-icon name="cross" @click="showAfterSalesPopup = false" />
      </div>
      <div class="after-sales-popup-content">
        <div class="after-sales-service-item">
          <div class="service-item-header">
            <van-icon name="chat-o" color="#07c160" />
            <span class="service-item-title">专属客服</span>
          </div>
          <div class="service-item-desc">
            客服快速应答，高峰期少排队。如遇售后纠纷，将由专员跟进处理，24小时内会有跟进记录。
          </div>
        </div>
        <div class="after-sales-service-item">
          <div class="service-item-header">
            <van-icon name="refund-o" color="#07c160" />
            <span class="service-item-title">闪电退换</span>
          </div>
          <div class="service-item-desc">
            用户申请退货，如满足相应条件，平台可快速同意并发送退货地址。退货寄出后，如满足相应条件，平台自动极速退款。
          </div>
        </div>
        <div class="after-sales-service-item">
          <div class="service-item-header">
            <van-icon name="smile-o" color="#07c160" />
            <span class="service-item-title">售后无忧</span>
          </div>
          <div class="service-item-desc">
            买到有质量问题的商品，如符合条件，平台会尽快支持破损腐烂部分的退款诉求，或报销退货运费。部分情况会有专属额外体验补偿。
          </div>
        </div>
      </div>
    </van-popup>

    <!-- 下单弹窗 -->
    <OrderPopup
      v-model:visible="showOrderPopup"
      :product-id="currentProduct.id"
      :product-name="currentProduct.name"
      :product-price="currentProduct.price"
      :product-image="currentImages[0] || ''"
      @order-success="handleOrderSuccess"
    />

    <!-- 底部操作栏 -->
    <div class="bottom-bar">
      <div class="bar-left">
        <div class="bar-item" @click="goShop">
          <van-icon name="shop" />
          <span>店铺</span>
        </div>
        <div class="bar-item" @click="toggleFavorite">
          <van-icon :name="isFavorite ? 'like' : 'like-o'" :color="isFavorite ? '#ee0a24' : '#999'" />
          <span>{{ isFavorite ? '已收藏' : '收藏' }}</span>
        </div>
        <div class="bar-item" @click="goMessage">
          <van-icon name="service" />
          <span>客服</span>
        </div>
      </div>
      <div class="bar-right">
        <van-button type="warning" class="cart-btn" @click="addToCartNow">加入购物车</van-button>
        <van-button type="danger" class="buy-btn" @click="buyNow">发起拼单</van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import ProductHeader from './ProductHeader.vue'
import GroupBuyUsers from './GroupBuyUsers.vue'
import OrderPopup from '../Order/OrderPopup.vue'
import { buildAvatarSvg, buildBrandIconSvg, buildShapesSvg } from '@/utils/request'
import {
  getProductDetail, 
  type ProductDetailResponse,
  type BrandInfo,
  type Merchant,
  type Review
} from '@/api/recommend'
import { toggleFavorite as apiToggleFavorite, getFavoriteList, type FavoriteResponse } from '@/api/favorite'
import { addToCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import { useHistoryStore } from '@/stores/history'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const historyStore = useHistoryStore()
const userStore = useUserStore()
const cartStore = useCartStore()

// 弹窗显示控制
const showShippingPopup = ref(false)
const showAfterSalesPopup = ref(false)
const showOrderPopup = ref(false)

// 收藏状态（从后端获取）
const isFavorite = ref(false)

// 返回顶部按钮显示状态（滚动超过500px显示）
const showBackTop = ref(false)

// 后端返回的商品详情数据
const productDetail = ref<ProductDetailResponse | null>(null)
const isLoading = ref(true)

// 当前商品ID
const currentProductId = ref(1)

// 当前商品（使用后端数据）
const currentProduct = computed(() => {
  if (productDetail.value && productDetail.value.code === 200) {
    const product = productDetail.value.data.product
    return {
      id: product.id,
      name: product.productName,
      subtitle: product.brief,
      price: product.price,
      originalPrice: product.originalPrice || 0,
      sales: product.sales,
      tags: currentTags.value,
      brand: currentBrandInfo.value.brandName,
      merchantLogo: currentMerchant.value.merchantLogo
    }
  }
  return {
    id: 0,
    name: '加载中...',
    subtitle: '',
    price: 0,
    originalPrice: 0,
    sales: 0,
    tags: [],
    brand: '',
    merchantLogo: ''
  }
})

// 当前商品的轮播图（使用后端数据）
const currentImages = computed(() => {
  if (productDetail.value && productDetail.value.code === 200 && productDetail.value.data.carouselImages) {
    return productDetail.value.data.carouselImages.map(img => img.imageUrl)
  }
  return []
})

// 当前商品的参数（使用后端数据）
const currentParams = computed(() => {
  if (productDetail.value && productDetail.value.code === 200 && productDetail.value.data.details) {
    return productDetail.value.data.details.map(d => ({
      key: d.paramKey,
      value: d.paramValue
    }))
  }
  return []
})

// 当前商品的详情图（使用后端数据）
const currentDetailImages = computed(() => {
  if (productDetail.value && productDetail.value.code === 200 && productDetail.value.data.detailImages) {
    return productDetail.value.data.detailImages.map(img => img.imageUrl)
  }
  return []
})

// 当前商品标签（使用后端数据）
const currentTags = computed(() => {
  if (productDetail.value && productDetail.value.code === 200 && productDetail.value.data.tags) {
    return productDetail.value.data.tags.map(t => ({
      text: t.tagName,
      type: 'hot'
    }))
  }
  return []
})

// 当前评论数据（使用后端数据）
const currentReviews = computed(() => {
  if (productDetail.value && productDetail.value.code === 200 && productDetail.value.data.reviews) {
    const reviews = productDetail.value.data.reviews
    return {
      total: reviews.length,
      avgRating: reviews.length > 0 ? 4.8 : 0,
      list: reviews.map((r: any) => ({
        id: r.id,
        name: r.userName,
        avatar: normalizeAvatar(r.avatar, 'user-' + r.userId + '-' + r.id),
        rating: r.rating,
        content: r.content,
        time: formatDate(r.createdAt),
        helpful: r.helpfulCount
      }))
    }
  }
  return {
    total: 0,
    avgRating: 0,
    list: []
  }
})

// 当前商家信息（使用后端数据）
const currentMerchant = computed<Merchant>(() => {
  if (productDetail.value && productDetail.value.code === 200 && productDetail.value.data.merchant) {
    const m = productDetail.value.data.merchant
    return {
      id: m.id,
      merchantName: m.merchantName || '加载中...',
      merchantLogo: normalizeImage(m.merchantLogo, 'merchant-' + m.id),
      merchantBrief: m.merchantBrief || ''
    }
  }
  return {
    id: 0,
    merchantName: '加载中...',
    merchantLogo: '',
    merchantBrief: ''
  }
})

// 当前品牌信息（使用后端数据）
const currentBrandInfo = computed<BrandInfo>(() => {
  if (productDetail.value && productDetail.value.code === 200 && productDetail.value.data.brandInfo) {
    const b = productDetail.value.data.brandInfo
    return {
      brandName: b.brandName || '',
      brandLogo: normalizeImage(b.brandLogo, 'brand-' + (b.brandName || 'brand')),
      brandSlogan: b.brandSlogan || '',
      introduction: b.introduction || '',
      features: b.features || [],
      establishedYear: b.establishedYear || 0,
      totalSales: String(b.totalSales || ''),
      goodReviewCount: String(b.goodReviewCount || ''),
      recentReviewCount: String(b.recentReviewCount || ''),
      recentGroupCount: String(b.recentGroupCount || ''),
      reviewerCount: String(b.reviewerCount || ''),
      guaranteeTags: b.guaranteeTags || [],
      shopTags: b.shopTags || []
    }
  }
  return {
    brandName: '',
    brandLogo: '',
    brandSlogan: '',
    introduction: '',
    features: [],
    establishedYear: 0,
    totalSales: '',
    goodReviewCount: '',
    recentReviewCount: '',
    recentGroupCount: '',
    reviewerCount: '',
    guaranteeTags: [],
    shopTags: []
  }
})

function normalizeImage(src: string | undefined | null, seed: string, label?: string): string {
  if (!src || src.trim() === '') {
    return buildBrandIconSvg(seed, label)
  }
  return src
}

function normalizeAvatar(src: string | undefined | null, seed: string): string {
  if (!src || src.trim() === '') {
    return buildAvatarSvg(seed)
  }
  if (src.startsWith('http://') || src.startsWith('https://') || src.startsWith('data:')) {
    if (src.includes('dicebear')) {
      return buildAvatarSvg(seed)
    }
    return src
  }
  return src
}

// 店铺信息（组合商家和品牌数据）
const shopInfo = computed(() => {
  return {
    totalReviewCount: currentBrandInfo.value.reviewerCount
  }
})

// 店铺评价标签（模拟数据，后续可从后端获取）
const shopReviewTags = ref([
  { name: '很合适', count: 802 },
  { name: '价格便宜', count: 567 },
  { name: '实用', count: 163 },
  { name: '客服好', count: 148 },
  { name: '质量很好', count: 68 }
])

// 格式化日期
function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取商品数据
const fetchProductData = async (id: number) => {
  isLoading.value = true
  try {
    // 调用后端接口获取商品详情
    const detailResult = await getProductDetail(id)
    if (detailResult.code === 200) {
      productDetail.value = detailResult
      
      // 通过获取用户收藏列表来判断当前商品是否已收藏
      await checkFavoriteStatus(id)
      
      console.log('后端商品详情数据:', detailResult.data)
      
      // 将商品添加到历史浏览记录
      const product = detailResult.data.product
      historyStore.addHistory({
        id: product.id,
        productName: product.productName,
        price: product.price,
        originalPrice: product.originalPrice,
        mainImage: product.mainImage || '',
        sales: product.sales,
        brief: product.brief || '',
        categoryId: product.categoryId || 0,
        stock: product.stock || 0,
        status: product.status || 1,
        isHot: product.isHot || 0,
        isNew: product.isNew || 0
      })
    }
  } catch (error) {
    console.error('获取商品数据失败:', error)
    showToast('获取商品数据失败')
  } finally {
    isLoading.value = false
  }
}

// 更新商品ID并获取数据
const updateProductId = (id: number) => {
  currentProductId.value = id
  fetchProductData(id)
}

// 监听路由参数变化
watch(() => route.params.id, (newId) => {
  const id = Number(newId)
  if (!isNaN(id)) {
    updateProductId(id)
  }
})

// 页面加载时获取路由参数和数据
onMounted(() => {
  const id = Number(route.params.id)
  if (!isNaN(id)) {
    updateProductId(id)
  } else {
    // 默认加载商品ID为1的数据
    updateProductId(1)
  }
  
  // 添加滚动监听
  window.addEventListener('scroll', handleScroll)
})

// 页面卸载时移除监听
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 滚动处理函数
const handleScroll = () => {
  showBackTop.value = window.scrollY > 500
}

// 返回
const goBack = () => {
  router.back()
}

// 滚动到顶部
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 获取当前用户ID（从sessionStorage获取或使用默认值）
const getUserId = (): number => {
  const userId = sessionStorage.getItem('user_id')
  if (userId) {
    return parseInt(userId, 10)
  }
  // 默认用户ID，实际应用中应该从登录状态获取
  return 1
}

// 检查商品收藏状态
const checkFavoriteStatus = async (productId: number) => {
  try {
    const userId = getUserId()
    const response = await getFavoriteList(userId, 1, 100) // 获取最多100个收藏
    if (response.code === 200 && response.data) {
      const favoriteIds = response.data.products.map((p: any) => p.id)
      isFavorite.value = favoriteIds.includes(productId)
      console.log('商品收藏状态检查:', productId, isFavorite.value ? '已收藏' : '未收藏')
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    // 如果获取失败，默认设为未收藏
    isFavorite.value = false
  }
}

// 切换收藏
const toggleFavorite = async () => {
  try {
    const userId = getUserId()
    const productId = currentProduct.value.id
    
    const response: FavoriteResponse = await apiToggleFavorite(userId, productId)
    
    if (response.code === 200) {
      isFavorite.value = response.isFavorited
      showToast(response.message)
    } else {
      showToast(response.message || '操作失败')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    // 如果接口调用失败，使用本地状态切换作为降级方案
    isFavorite.value = !isFavorite.value
    showToast(isFavorite.value ? '已添加收藏' : '已取消收藏')
  }
}

// 进入店铺
const goEnterShop = () => {
  showToast('进入店铺')
}

// 查看全部评论
const goViewAllReviews = () => {
  showToast('查看全部评论')
}

// 查看全部店铺评论
const goViewAllShopReviews = () => {
  showToast('查看全部店铺评论')
}

// 查看全部参数
const goViewAllParams = () => {
  showToast('查看全部参数')
}

// 去店铺
const goShop = () => {
  showToast('进入店铺')
}

// 去客服
const goMessage = () => {
  const merchantId = currentMerchant.value.id
  const productId = currentProduct.value.id
  router.push(`/customer-service/${merchantId}/${productId}`)
}

// 发起拼单
const buyNow = () => {
  showOrderPopup.value = true
}

// 加入购物车
const addToCartNow = async () => {
  if (!userStore.userInfo?.id) {
    // 先尝试自动登录
    const autoLoginSuccess = await userStore.autoLogin()
    if (!autoLoginSuccess) {
      showToast('请先登录')
      router.push('/login')
      return
    }
  }
  const product = productDetail.value?.data?.product
  if (!product) {
    showToast('商品信息加载中')
    return
  }
  try {
    const res: any = await addToCart({
      userId: userStore.userInfo!.id,
      productId: product.id,
      productName: product.productName,
      productImage: currentImages.value[0] || '',
      price: product.price,
      quantity: 1
    })
    if (res.code === 200) {
      showToast('已加入购物车')
      // 刷新购物车数据（会自动保存到本地缓存）
      await cartStore.refreshCart()
    } else {
      showToast(res.message || '添加失败')
    }
  } catch (error) {
    showToast('添加失败')
  }
}

// 订单成功处理
const handleOrderSuccess = (orderNo: string) => {
  console.log('订单创建成功:', orderNo)
}

// 点赞评论
const helpfulReview = () => {
  showToast('感谢您的反馈')
}
</script>

<style lang="scss" scoped>
.product-detail {
  padding-bottom: 100px;
  background: #f5f5f5;
}

/* 发货信息 */
.shipping-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #fff;
  margin-bottom: 10px;

  .shipping-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .shipping-text {
    font-size: 14px;
    color: #333;
  }

  .shipping-divider {
    color: #ddd;
  }

  .shipping-promise {
    font-size: 12px;
    color: #07c160;
  }
}

/* 售后保障 */
.after-sales-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #fff;
  margin-bottom: 10px;

  .after-sales-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .after-sales-title {
    font-size: 14px;
    color: #333;
  }

  .after-sales-tags {
    display: flex;
    gap: 5px;
  }

  .after-sales-tag {
    font-size: 12px;
    color: #07c160;
    background: rgba(7, 193, 96, 0.1);
    padding: 2px 6px;
    border-radius: 3px;
  }
}

/* 评价区域 */
.review-section {
  background: #fff;
  margin-bottom: 10px;
  padding: 15px;

  .section-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 15px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .review-score {
      font-size: 14px;
      color: #ff6034;
      font-weight: 600;
    }

    .view-all {
      margin-left: auto;
      font-size: 14px;
      color: #999;
    }
  }

  .review-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .review-item {
    display: flex;
    gap: 12px;

    .review-avatar {
      width: 40px;
      height: 40px;
      flex-shrink: 0;
    }

    .review-content {
      flex: 1;

      .review-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .review-name {
          font-size: 14px;
          color: #333;
        }
      }

      .review-text {
        font-size: 14px;
        color: #666;
        line-height: 1.6;
        margin-bottom: 8px;
      }

      .review-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .review-time {
          font-size: 12px;
          color: #999;
        }

        .review-helpful {
          font-size: 12px;
          color: #999;
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

/* 店铺评论区域 */
.shop-review-section {
  background: #fff;
  margin-bottom: 10px;
  padding: 15px;

  .section-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 15px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .view-all {
      margin-left: auto;
      font-size: 14px;
      color: #999;
    }
  }

  .shop-review-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 15px;
  }

  .shop-review-tag {
    font-size: 12px;
    color: #666;
    background: #f5f5f5;
    padding: 4px 10px;
    border-radius: 12px;
  }

  .shop-info-card {
    background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
    border-radius: 8px;
    padding: 15px;

    .shop-brand-info {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;

      .shop-brand-logo {
        width: 50px;
        height: 50px;
        border-radius: 8px;
      }

      .shop-brand-detail {
        .shop-brand-name {
          font-size: 16px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }

        .shop-brand-sales {
          font-size: 12px;
          color: #999;
        }
      }
    }

    .shop-stats {
      display: flex;
      gap: 15px;
      margin-bottom: 12px;

      .stat-item {
        font-size: 12px;
        color: #666;
      }
    }

    .shop-guarantee {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
      padding: 10px;
      background: rgba(238, 10, 36, 0.05);
      border-radius: 6px;

      .guarantee-label {
        font-size: 12px;
        color: #ee0a24;
        font-weight: 600;
      }

      .guarantee-value {
        font-size: 12px;
        color: #ee0a24;
      }
    }

    .shop-operator {
      display: flex;
      justify-content: space-between;
      align-items: center;

      span {
        font-size: 12px;
        color: #999;
      }

      .enter-shop-btn {
        color: #ee0a24;
        padding: 4px 12px;
        border: 1px solid #ee0a24;
        border-radius: 12px;
      }
    }
  }
}

/* 品牌介绍区域 */
.brand-section {
  background: #fff;
  margin-bottom: 10px;
  padding: 15px;

  .brand-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 15px;

    .brand-logo {
      width: 60px;
      height: 60px;
      border-radius: 8px;
    }

    .brand-info {
      .brand-name {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }

      .brand-since {
        font-size: 14px;
        color: #999;
      }
    }
  }

  .brand-intro {
    margin-bottom: 15px;

    .brand-intro-label {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 10px;
      display: block;
    }

    .brand-intro-text {
      font-size: 14px;
      color: #666;
      line-height: 1.8;
      white-space: pre-wrap;
    }
  }

  .brand-features {
    display: flex;
    flex-direction: column;
    gap: 10px;

    .feature-item {
      display: flex;
      gap: 12px;

      .feature-label {
        font-size: 14px;
        color: #ee0a24;
        font-weight: 600;
        width: 60px;
        flex-shrink: 0;
      }

      .feature-value {
        font-size: 14px;
        color: #666;
        flex: 1;
      }
    }
  }
}

/* 商品参数 */
.params-section {
  background: #fff;
  margin-bottom: 10px;
  padding: 15px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .view-all {
      font-size: 14px;
      color: #999;
    }
  }

  .params-list {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;

    .param-item {
      width: calc(50% - 7.5px);
      display: flex;
      gap: 8px;

      .param-label {
        font-size: 14px;
        color: #999;
        width: 70px;
        flex-shrink: 0;
      }

      .param-value {
        font-size: 14px;
        color: #333;
        flex: 1;
      }
    }
  }
}

/* 商品详情图 */
.detail-images-section {
  background: #fff;
  margin-bottom: 10px;

  .detail-images-badge {
    padding: 10px 15px;
    background: linear-gradient(90deg, #ee0a24 0%, #ff6034 100%);

    .badge-text {
      font-size: 14px;
      color: #fff;
      font-weight: 600;
    }
  }

  .detail-images-icons {
    display: flex;
    justify-content: space-around;
    padding: 15px;
    border-bottom: 1px solid #f0f0f0;

    .icon-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      color: #666;

      .van-icon {
        font-size: 24px;
      }
    }
  }

  .detail-images {
    padding: 10px;

    .detail-image {
      width: 100%;
      margin-bottom: 10px;
      border-radius: 8px;
    }
  }
}

/* 平台免责说明 */
.disclaimer-section {
  background: #fff;
  padding: 15px;

  .section-header {
    margin-bottom: 15px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }

  .disclaimer-content {
    .disclaimer-item {
      margin-bottom: 20px;

      h4 {
        font-size: 14px;
        font-weight: 600;
        color: #333;
        margin-bottom: 10px;
      }

      p {
        font-size: 12px;
        color: #999;
        line-height: 1.8;
        margin-bottom: 8px;
      }
    }
  }

  .disclaimer-footer {
    padding-top: 15px;
    border-top: 1px solid #f0f0f0;

    p {
      font-size: 12px;
      color: #999;
      line-height: 1.8;
    }
  }
}

/* 返回顶部按钮 */
.back-top-btn {
  position: fixed;
  right: 20px;
  bottom: 120px;
  width: 45px;
  height: 45px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;

  .van-icon {
    color: #fff;
    font-size: 24px;
  }
}

/* 弹窗样式 */
.shipping-popup-header,
.after-sales-popup-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  position: relative;

  .van-icon:first-child {
    margin-right: 8px;
  }

  .shipping-popup-title,
  .after-sales-popup-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .van-icon:last-child {
    position: absolute;
    right: 15px;
    color: #999;
  }
}

.shipping-popup-content {
  padding: 15px;

  .shipping-info-item {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid #f5f5f5;

    .shipping-info-label {
      font-size: 14px;
      color: #999;
    }

    .shipping-info-value {
      font-size: 14px;
      color: #333;
    }
  }
}

.after-sales-popup-content {
  padding: 15px;

  .after-sales-service-item {
    margin-bottom: 20px;

    .service-item-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;

      .service-item-title {
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }
    }

    .service-item-desc {
      font-size: 13px;
      color: #666;
      line-height: 1.6;
      padding-left: 32px;
    }
  }
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 8px 12px;
  padding-bottom: calc(8px + env(safe-area-inset-bottom));
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;

  .bar-left {
    display: flex;
    flex: 1;
    justify-content: space-around;
    max-width: 200px;

    .bar-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;
      font-size: 11px;
      color: #666;
      min-width: 44px;

      .van-icon {
        font-size: 22px;
      }
    }
  }

  .bar-right {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-shrink: 0;

    .price-section {
      .price-row {
        display: flex;
        align-items: baseline;
        gap: 4px;

        .price-label {
          font-size: 11px;
          color: #ee0a24;
        }

        .price-value {
          font-size: 18px;
          font-weight: 700;
          color: #ee0a24;
        }
      }
    }

    .cart-btn {
      width: 80px;
      height: 36px;
      border-radius: 18px;
      font-size: 13px;
      font-weight: 600;
    }

    .buy-btn {
      width: 90px;
      height: 36px;
      border-radius: 18px;
      font-size: 13px;
      font-weight: 600;
    }
  }
}
</style>
