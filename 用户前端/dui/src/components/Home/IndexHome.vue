<template>
  <div class="index-home">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="logo">TermShop</div>
      <div class="search-box" @click="goSearch">
        <van-icon name="search" />
        <span>搜索商品</span>
      </div>
    </div>

    <!-- 分类标签 -->
    <van-tabs v-model:active="activeCategory" sticky @change="onCategoryChange">
      <van-tab title="全部" name="all"></van-tab>
      <van-tab v-for="cat in categories" :key="cat.id" :title="cat.categoryName" :name="cat.id"></van-tab>
    </van-tabs>

    <!-- 商品列表 -->
    <div class="product-list">
      <div class="product-card" v-for="product in productList" :key="product.id" @click="goProductDetail(product.id)">
        <div class="product-image">
          <img :src="product.mainImage || defaultImage" :alt="product.productName" />
          <span v-if="product.isNew === 1" class="tag new">新品</span>
          <span v-if="product.isHot === 1" class="tag hot">热门</span>
        </div>
        <div class="product-info">
          <div class="product-name">{{ product.productName }}</div>
          <div class="product-price">
            <span class="price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
          </div>
          <div class="product-meta">
            <span class="sales">销量 {{ product.sales }}</span>
            <span class="stock">库存 {{ product.stock }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部提示 -->
    <div v-if="productList.length === 0 && !loading" class="empty">
      <van-empty description="暂无商品" />
    </div>

    <van-loading v-if="loading" class="loading" type="spinner" />

    <!-- 底部导航 -->
    <div class="bottom-nav">
      <div class="nav-item" @click="goHome">
        <van-icon name="wap-home" />
        <span>首页</span>
      </div>
      <div class="nav-item" @click="goCategory">
        <van-icon name="apps-o" />
        <span>分类</span>
      </div>
      <div class="nav-item" @click="goCart">
        <van-icon name="shopping-cart-o" />
        <span>购物车</span>
      </div>
      <div class="nav-item" @click="goMy">
        <van-icon name="user-o" />
        <span>我的</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const activeCategory = ref('all')
const productList = ref<any[]>([])
const categories = ref<any[]>([])
const defaultImage = 'https://via.placeholder.com/200x200?text=No+Image'



// 加载商品列表 - 使用热门商品接口
const loadProducts = async () => {
  loading.value = true
  try {
    console.log('=== 开始请求热门商品 ===')
    console.log('请求路径:', '/recommend/hot')
    
    const res: any = await request.get('/recommend/hot')
    
    console.log('响应结果:', res)
    console.log('响应码:', res?.code)
    console.log('响应数据:', res?.data)
    console.log('数据长度:', res?.data?.length)
    
    if (res.code === 200) {
      productList.value = res.data || []
      console.log('商品列表已更新:', productList.value.length, '件商品')
    } else {
      console.warn('响应码不是200:', res.code, res.message)
    }
  } catch (error: any) {
    console.error('加载热门商品失败:', error)
    console.error('错误详情:', error.message, error.response)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 加载分类
const loadCategories = async () => {
  try {
    await request.get('/recommend/category/1', { params: { limit: 100 } })
    // 假设分类接口返回的是商品，按分类展示，这里用固定分类
    categories.value = [
      { id: 1, categoryName: '手机' },
      { id: 2, categoryName: '电脑' },
      { id: 3, categoryName: '电器' },
      { id: 4, categoryName: '运动' }
    ]
  } catch (error) {
    // 使用默认分类
    categories.value = [
      { id: 1, categoryName: '手机' },
      { id: 2, categoryName: '电脑' },
      { id: 3, categoryName: '电器' },
      { id: 4, categoryName: '运动' }
    ]
  }
}

// 分类切换
const onCategoryChange = (name: string) => {
  activeCategory.value = name
  loadProducts()
}

// 跳转搜索页
const goSearch = () => {
  showToast('搜索功能开发中')
}

// 跳转商品详情
const goProductDetail = (id: number) => {
  showToast(`商品ID: ${id}`)
  // 后续连接商品详情页: router.push(`/product/${id}`)
}

// 底部导航
const goHome = () => {
  router.push('/home')
}

const goCategory = () => {
  showToast('分类页面开发中')
}

const goCart = () => {
  router.push('/cart')
}

const goMy = () => {
  router.push('/my')
}

onMounted(() => {
  loadProducts()
  loadCategories()
})
</script>

<style scoped>
.index-home {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;
}

.header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #1989fa;
  margin-right: 12px;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 20px;
  padding: 8px 16px;
  color: #999;
  font-size: 14px;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  padding: 10px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.product-image {
  position: relative;
  width: 100%;
  padding-top: 100%;
  background: #f9f9f9;
}

.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image .tag {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  color: #fff;
}

.tag.new {
  background: #07c160;
}

.tag.hot {
  background: #ff6034;
}

.product-info {
  padding: 10px;
}

.product-name {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.price {
  color: #ff6034;
  font-size: 16px;
  font-weight: bold;
}

.original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #999;
}

.empty {
  padding: 60px 0;
}

.loading {
  text-align: center;
  padding: 20px;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  padding: 8px 0 20px;
  background: #fff;
  border-top: 1px solid #eee;
  z-index: 1000;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}
</style>
