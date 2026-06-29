<template>
  <div class="category-products-page">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <div class="header-title">{{ currentLevel2Name || '分类商品' }}</div>
      <van-icon name="search" class="search-btn" @click="goSearch" />
    </div>

    <!-- 三级分类标签 -->
    <div class="level3-tabs">
      <div class="tabs-wrapper">
        <div
          :class="['tab-item', { active: selectedLevel3Id === null }]"
          @click="selectLevel3(null)"
        >
          全部
        </div>
        <div
          v-for="cat in level3Categories"
          :key="cat.id"
          :class="['tab-item', { active: selectedLevel3Id === cat.id }]"
          @click="selectLevel3(cat.id)"
        >
          {{ cat.categoryName }}
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <div
        v-for="product in products"
        :key="product.id"
        class="product-card"
        @click="goProductDetail(product.id)"
      >
        <div class="product-image-wrapper">
          <img
            :src="product.imageUrl || 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=product%20photo%20of%20a%20shopping%20item&image_size=square'"
            :alt="product.productName"
            class="product-image"
          />
          <span v-if="product.isHot === 1" class="hot-tag">热卖</span>
          <span v-if="product.isNew === 1" class="new-tag">新品</span>
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.productName }}</h3>
          <div class="product-price-row">
            <span class="current-price">¥{{ product.price }}</span>
            <span v-if="parseFloat(String(product.originalPrice)) > parseFloat(String(product.price))" class="original-price">
              ¥{{ product.originalPrice }}
            </span>
          </div>
          <div class="product-sales">已售 {{ product.sales }} 件</div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="products.length === 0" class="empty-state">
      <van-icon name="shopping-cart-o" size="64" color="#ccc" />
      <p>暂无商品</p>
    </div>

    <!-- 加载提示 -->
    <div v-if="loading" class="loading-state">
      <van-loading type="spinner" color="#ee0a24" />
      <span>加载中...</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Icon as VanIcon, Loading as VanLoading } from 'vant'
import { useRouter, useRoute } from 'vue-router'
import {
  getLevel3Categories,
  getLevel2Products,
  getLevel3Products,
  type Category,
  type Product
} from '@/api/category'

const router = useRouter()
const route = useRoute()

// 路由参数
const level2Id = ref<number>(0)
const level2Name = ref<string>('')

// 分类数据
const level3Categories = ref<Category[]>([])
const selectedLevel3Id = ref<number | null>(null)
const products = ref<Product[]>([])
const loading = ref(false)

// 当前二级分类名称
const currentLevel2Name = computed(() => {
  return level2Name.value || '分类商品'
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 跳转到搜索页
const goSearch = () => {
  router.push('/search')
}

// 跳转到商品详情页
const goProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 选择三级分类
const selectLevel3 = async (level3Id: number | null) => {
  selectedLevel3Id.value = level3Id
  await loadProducts()
}

// 加载三级分类
const loadLevel3Categories = async () => {
  try {
    const response = await getLevel3Categories(level2Id.value)
    if (response.code === 200 && response.data) {
      level3Categories.value = response.data
    }
  } catch (error) {
    console.error('加载三级分类失败:', error)
    level3Categories.value = []
  }
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    let response
    if (selectedLevel3Id.value !== null) {
      // 获取三级分类商品
      response = await getLevel3Products(selectedLevel3Id.value)
    } else {
      // 获取二级分类商品
      response = await getLevel2Products(level2Id.value)
    }
    
    if (response.code === 200 && response.data) {
      products.value = response.data
    } else {
      products.value = []
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    products.value = []
  } finally {
    loading.value = false
  }
}

// 初始化数据
const initData = async () => {
  // 从路由参数获取二级分类ID和名称
  const id = route.params.id
  const name = route.params.name
  
  if (id) {
    level2Id.value = parseInt(id as string, 10)
  }
  if (name) {
    level2Name.value = decodeURIComponent(name as string)
  }
  
  // 加载三级分类
  await loadLevel3Categories()
  
  // 默认加载全部商品（二级分类下的商品）
  selectedLevel3Id.value = null
  await loadProducts()
}

// 监听路由变化
watch(() => route.params, () => {
  initData()
})

onMounted(() => {
  initData()
})
</script>

<style scoped>
.category-products-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: env(safe-area-inset-bottom);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn, .search-btn {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #333;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.level3-tabs {
  background: #fff;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.tabs-wrapper {
  display: flex;
  gap: 8px;
  padding: 0 16px;
  overflow-x: auto;
  white-space: nowrap;
}

.tab-item {
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s;
}

.tab-item.active {
  background: #ee0a24;
  color: #fff;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 16px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hot-tag, .new-tag {
  position: absolute;
  top: 8px;
  padding: 2px 8px;
  font-size: 12px;
  color: #fff;
  border-radius: 4px;
}

.hot-tag {
  left: 8px;
  background: linear-gradient(135deg, #ff6b6b, #ee0a24);
}

.new-tag {
  left: 8px;
  background: linear-gradient(135deg, #4ecdc4, #44a08d);
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.current-price {
  font-size: 18px;
  font-weight: 600;
  color: #ee0a24;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.product-sales {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
}

.loading-state span {
  margin-top: 12px;
  font-size: 14px;
}
</style>