<template>
  <div class="home">
    <!-- 顶部固定区域 -->
    <div class="header-sticky">
      <!-- 搜索栏 -->
      <SearchBar placeholder="搜索商品" />

      <!-- 分类标签 - 文字滑动风格 -->
      <CategoryTabs
        type="text"
        v-model="activeCategory"
        :tabs="categories"
        @change="onCategoryChange"
      />
    </div>

    <!-- ====== 推荐页面内容 ====== -->
    <template v-if="isRecommendPage">
      <!-- 快捷入口 -->
      <div class="quick-entries">
        <div
          v-for="item in quickEntries"
          :key="item.id"
          class="entry-item"
          @click="handleQuickEntryClick(item)"
        >
          <div class="entry-icon" :style="{ backgroundColor: item.color }">
            <van-icon :name="item.icon" size="24" color="#fff" />
          </div>
          <span class="entry-name">{{ item.name }}</span>
        </div>
      </div>

      <!-- 限时活动区域 -->
      <div class="activity-section">
        <div class="activity-card primary" @click="showToast('限时秒杀')">
          <div class="activity-tag">1元抢</div>
          <div class="activity-title">限时秒杀</div>
        </div>
        <div class="activity-card secondary" @click="showToast('百亿补贴')">
          <div class="activity-tag red">疯狂618</div>
          <div class="activity-title">百亿补贴</div>
          <div class="activity-subtitle">官方补贴</div>
        </div>
      </div>

      <!-- 商品列表（直接从后端获取，支持下拉刷新） -->
      <div class="product-scroll-container">
        <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
          <van-grid :column-num="2" :gutter="10" style="padding: 0 10px;">
            <van-grid-item v-for="item in hotProducts" :key="item.id" @click="goProduct(item.id)">
              <van-image :src="item.mainImage || 'https://via.placeholder.com/150x150?text=No+Image'" class="product-image" />
              <div class="product-info">
                <div class="product-name">{{ item.productName }}</div>
                <div class="price-row">
                  <!-- 优惠后价格（红色高亮） -->
                  <span v-if="item.discountedPrice" class="product-price discounted">
                    ¥{{ item.discountedPrice }}
                  </span>
                  <!-- 当前售价 -->
                  <span v-else class="product-price">
                    ¥{{ item.price }}
                  </span>
                  <!-- 划线原价 -->
                  <span v-if="item.originalPrice" class="product-original-price">
                    ¥{{ item.originalPrice }}
                  </span>
                </div>
                <!-- 优惠券标签 -->
                <div v-if="item.applicableCoupon" class="coupon-tag">
                  <span class="coupon-badge">{{ item.applicableCoupon.couponName }}</span>
                  <span class="coupon-countdown">
                    {{ formatCouponCountdown(item.applicableCoupon.remainingSeconds) }}
                  </span>
                </div>
                <div class="product-sales">销量: {{ item.sales }}</div>
              </div>
            </van-grid-item>
          </van-grid>
        </van-pull-refresh>
      </div>
    </template>

    <!-- ====== 分类页面内容 (食品/百货/车品) ====== -->
    <template v-else>
      <!-- 子分类图标网格 -->
      <div class="sub-category-grid">
        <div
          v-for="item in currentSubCategories"
          :key="item.id"
          class="sub-category-item"
          @click="goCategoryProducts(item.id, item.name)"
        >
          <div class="sub-icon" :style="{ backgroundColor: item.color + '20' }">
            <img v-if="item.icon && (item.icon.startsWith('http') || item.icon.startsWith('/'))" :src="item.icon" :alt="item.name" class="sub-icon-img" />
            <span v-else class="icon-emoji">{{ item.icon || '📦' }}</span>
          </div>
          <span class="sub-name">{{ item.name }}</span>
        </div>
      </div>

      <!-- 分类特色横幅 -->
      <div class="category-banner">
        <div class="banner-content">
          <h3>{{ currentCategoryName }}</h3>
          <p>精选好物，品质保证</p>
        </div>
      </div>

      <!-- 分类商品列表 -->
      <div class="section">
        <div class="section-header">
          <h3>热门{{ currentCategoryName }}</h3>
          <span class="more">更多</span>
        </div>
        <van-grid :column-num="2" :gutter="10">
          <van-grid-item v-for="item in categoryProducts" :key="item.id" @click="goProduct(item.id)">
            <van-image :src="item.mainImage || undefined" class="product-image" />
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="price-row">
                <!-- 优惠后价格（红色高亮） -->
                <span v-if="item.discountedPrice" class="product-price discounted">
                  ¥{{ item.discountedPrice }}
                </span>
                <!-- 当前售价 -->
                <span v-else class="product-price">
                  ¥{{ item.price }}
                </span>
                <!-- 划线原价 -->
                <span v-if="item.originalPrice" class="product-original-price">
                  ¥{{ item.originalPrice }}
                </span>
              </div>
              <!-- 优惠券标签 -->
              <div v-if="item.applicableCoupon" class="coupon-tag">
                <span class="coupon-badge">{{ item.applicableCoupon.couponName }}</span>
                <span class="coupon-countdown">
                  {{ formatCouponCountdown(item.applicableCoupon.remainingSeconds) }}
                </span>
              </div>
              <div class="product-sales">已售 {{ item.sales }} 件</div>
            </div>
          </van-grid-item>
        </van-grid>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getHotProducts, type Product as RecommendProduct } from '@/api/recommend'
import { getLevel1Categories, getLevel2Categories, getLevel1Products, type Product } from '@/api/category'
import CategoryTabs from './CategoryTabs.vue'
import type { TabItem } from './CategoryTabs.vue'
import SearchBar from './SearchBar.vue'

const router = useRouter()

// 分类数据（从 Pinia store 获取）
const categories = ref<TabItem[]>([
  { id: 0, name: '推荐', icon: 'home-o' }
])

// 当前选中的分类
const activeCategory = ref(0)

// 是否是推荐页面
const isRecommendPage = computed(() => activeCategory.value === 0)

// 快捷入口数据
const quickEntries = ref([
  { id: 1, name: '限时秒杀', icon: 'clock-o', color: '#ff6b6b', route: '/activity/flash-sale' },
  { id: 2, name: '百亿补贴', icon: 'gift-o', color: '#ee0a24', route: '/activity/subsidy' },
  { id: 3, name: '品牌馆', icon: 'medal-o', color: '#07c160', route: '' },
  { id: 4, name: '生活买菜', icon: 'shopping-cart-o', color: '#576b95', route: '' },
  { id: 5, name: '充值中心', icon: 'balance-pay', color: '#9c88ff', route: '' },
  { id: 6, name: '火车票', icon: 'logistics', color: '#4ecdc4', route: '/activity/train-ticket' },
  { id: 7, name: '机票', icon: 'guide-o', color: '#ff9ff3', route: '' },
  { id: 8, name: '酒店', icon: 'hotel-o', color: '#54a0ff', route: '' }
])

// 热门商品数据（每次展示4个）
const hotProducts = ref<RecommendProduct[]>([])

// 下拉刷新状态
const isRefreshing = ref(false)

// 二级分类数据
const level2Categories = ref<{ id: number; name: string; icon?: string }[]>([])

// 是否正在加载二级分类
const isLoadingLevel2 = ref(false)

// 当前子分类列表
const currentSubCategories = computed(() => {
  if (activeCategory.value === 0) return []
  
  const colors = ['#ff6b6b', '#ee5a5a', '#6c5ce7', '#fd79a8', '#00b894', '#74b9ff', '#fdcb6e', '#00cec9', '#a29bfe', '#ff9f43']
  return level2Categories.value.slice(0, 10).map((item: { id: number; name: string; icon?: string }, index: number) => ({
    id: item.id,
    name: item.name,
    icon: item.icon || '📦',
    color: colors[index % colors.length]
  }))
})

// 加载二级分类
const loadLevel2Categories = async (level1Id: number) => {
  if (level1Id === 0) return
  
  isLoadingLevel2.value = true
  try {
    const result = await getLevel2Categories(level1Id)
    if (result.code === 200 && result.data) {
      level2Categories.value = result.data.map((cat: { id: number; categoryName: string; icon: string | null }) => ({
        id: cat.id,
        name: cat.categoryName,
        icon: cat.icon || undefined
      }))
      console.log('二级分类数据加载成功:', level2Categories.value)
    } else {
      level2Categories.value = []
    }
  } catch (error) {
    console.error('加载二级分类失败:', error)
    level2Categories.value = []
  } finally {
    isLoadingLevel2.value = false
  }
}

// 当前分类名称
const currentCategoryName = computed(() => {
  return categories.value.find(c => c.id === activeCategory.value)?.name || ''
})

// 分类商品数据（从后端获取）
const categoryProducts = ref<Product[]>([])

// 加载热门商品（每次8个）
const loadHotProducts = async () => {
  try {
    const result = await getHotProducts(8)
    if (result.code === 200) {
      hotProducts.value = result.data
    }
  } catch (error) {
    console.error('加载热门商品失败:', error)
    hotProducts.value = []
  }
}

// 下拉刷新
const onRefresh = async () => {
  isRefreshing.value = true
  await loadHotProducts()
  isRefreshing.value = false
  showToast('刷新成功')
}

// 加载一级分类商品
const loadLevel1Products = async (level1Id: number) => {
  try {
    const result = await getLevel1Products(level1Id, 8)
    if (result.code === 200 && result.data) {
      categoryProducts.value = result.data
    } else {
      categoryProducts.value = []
    }
  } catch (error) {
    console.error('加载一级分类商品失败:', error)
    categoryProducts.value = []
  }
}

// 分类切换
const onCategoryChange = async (value: number | string) => {
  const categoryId = Number(value)
  activeCategory.value = categoryId
  
  // 切换到非推荐页面时，加载二级分类和一级分类商品
  if (categoryId !== 0) {
    await loadLevel2Categories(categoryId)
    await loadLevel1Products(categoryId)
  }
}

// 跳转商品详情
const goProduct = (id: number) => {
  router.push(`/product/${id}`)
}

// 跳转到分类商品页面
const goCategoryProducts = (level2Id: number, name: string) => {
  router.push(`/category/${level2Id}/products/${encodeURIComponent(name)}`)
}

// 处理快捷入口点击
const handleQuickEntryClick = (item: any) => {
  if (item.route) {
    router.push(item.route)
  } else {
    showToast(item.name)
  }
}

// 格式化优惠券倒计时
const formatCouponCountdown = (seconds: number): string => {
  if (seconds <= 0) return '已过期'
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (hours > 0) {
    return `${hours}时${minutes}分${secs}秒后过期`
  }
  return `${minutes}分${secs}秒后过期`
}

// 加载分类数据（直接调用一级分类接口）
const loadCategories = async () => {
  try {
    // 直接调用一级分类接口 GET /category/list
    const result = await getLevel1Categories()
    
    // 将分类数据转换为标签格式
    const categoryTabs: TabItem[] = [{ id: 0, name: '推荐', icon: 'home-o' }]
    
    if (result.code === 200 && result.data) {
      result.data.forEach((cat: { id: number; categoryName: string }) => {
        // 过滤无效的分类名称：空字符串、null、undefined、纯问号等
        const categoryName = cat.categoryName?.trim() || ''
        if (!categoryName || /^\?+$/.test(categoryName)) {
          console.warn(`跳过无效分类: id=${cat.id}, name="${cat.categoryName}"`)
          return
        }
        
        categoryTabs.push({
          id: cat.id,
          name: categoryName,
          icon: 'apps'
        })
      })
    }
    
    categories.value = categoryTabs
    console.log('一级分类数据加载成功:', result.data)
    console.log('最终分类列表:', categoryTabs.map(c => c.name))
  } catch (error) {
    console.error('加载分类数据失败:', error)
  }
}

// 页面挂载时加载数据
onMounted(async () => {
  // 先加载分类数据，不需要登录（使用 Pinia store 缓存）
  await loadCategories()
  // 再加载热门商品
  await loadHotProducts()
})
</script>

<style scoped>
.home {
  padding-bottom: 50px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 商品滚动容器 - 用于下拉刷新 */
.product-scroll-container {
  overflow-y: auto;
  max-height: calc(100vh - 380px);
}

.product-scroll-container :deep(.van-pull-refresh) {
  min-height: 100%;
}

/* 顶部固定区域 */
.header-sticky {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 快捷入口 */
.quick-entries {
  display: flex;
  justify-content: space-around;
  padding: 15px 10px;
  background-color: #fff;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.entry-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.entry-name {
  font-size: 12px;
  color: #333;
}

/* 活动区域 */
.activity-section {
  display: flex;
  gap: 10px;
  padding: 0 10px 10px;
}

.activity-card {
  flex: 1;
  border-radius: 12px;
  padding: 15px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.activity-card.primary {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
}

.activity-card.secondary {
  background: linear-gradient(135deg, #ffd93d 0%, #ff9f43 100%);
}

.activity-tag {
  display: inline-block;
  background-color: rgba(255, 255, 255, 0.3);
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 8px;
}

.activity-tag.red {
  background-color: #ee0a24;
}

.activity-title {
  font-size: 18px;
  font-weight: bold;
}

.activity-subtitle {
  font-size: 12px;
  opacity: 0.8;
  margin-top: 4px;
}

/* 模块区域 */
.section {
  background-color: #fff;
  margin: 10px;
  border-radius: 12px;
  padding: 15px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #1f1f1f;
}

.more {
  font-size: 13px;
  color: #999;
}

/* 商品图片 */
.product-image {
  width: 100%;
  height: 160px;
  border-radius: 8px;
  object-fit: cover;
}

/* 商品信息 */
.product-info {
  padding: 10px 0 0;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-top: 5px;
}

.product-price {
  color: #ee0a24;
  font-size: 16px;
  font-weight: bold;
}

.product-price.discounted {
  font-size: 18px;
}

.product-original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
}

.product-sales {
  font-size: 12px;
  color: #999;
  margin-top: 3px;
}

.product-brief {
  font-size: 12px;
  color: #999;
  margin-top: 3px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag {
  display: inline-block;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  margin-top: 5px;
}

.new-tag {
  background-color: #ee0a24;
  color: #fff;
}

/* 优惠券标签 */
.coupon-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}

.coupon-badge {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.coupon-countdown {
  color: #ff4d4f;
  font-size: 11px;
}

/* 子分类图标网格 */
.sub-category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  padding: 15px 10px;
  background-color: #fff;
}

.sub-category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.sub-category-item:active {
  opacity: 0.7;
}

.sub-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
}

.sub-category-item:active .sub-icon {
  transform: scale(0.95);
}

.icon-emoji {
  font-size: 28px;
}

.sub-icon-img {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.sub-name {
  font-size: 12px;
  color: #333;
  text-align: center;
}

/* 分类横幅 */
.category-banner {
  margin: 10px;
  height: 100px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.banner-content {
  text-align: center;
}

.banner-content h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: bold;
}

.banner-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}
</style>
