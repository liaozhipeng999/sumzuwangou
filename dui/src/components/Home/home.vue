<template>
  <div class="home">
    <!-- 搜索栏 -->
    <van-search
      v-model="searchValue"
      placeholder="搜索商品"
      shape="round"
      @search="onSearch"
    />

    <!-- 轮播图 -->
    <van-swipe class="banner" :autoplay="3000" indicator-color="white">
      <van-swipe-item v-for="item in banners" :key="item.id">
        <img :src="item.image" :alt="item.title" />
      </van-swipe-item>
    </van-swipe>

    <!-- 分类导航 -->
    <van-grid :column-num="4" class="category">
      <van-grid-item
        v-for="item in categories"
        :key="item.id"
        :icon="item.icon"
        :text="item.name"
        @click="goCategory(item.id)"
      />
    </van-grid>

    <!-- 热销商品 -->
    <div class="section">
      <div class="section-header">
        <h3>热销榜单</h3>
        <span class="more" @click="goHotList">更多</span>
      </div>
      <van-grid :column-num="2" :gutter="10">
        <van-grid-item v-for="item in hotProducts" :key="item.id" @click="goProduct(item.id)">
          <van-image :src="item.mainImage" class="product-image" />
          <div class="product-info">
            <div class="product-name">{{ item.productName }}</div>
            <div class="product-price">¥{{ item.price }}</div>
            <div class="product-sales">销量: {{ item.sales }}</div>
          </div>
        </van-grid-item>
      </van-grid>
    </div>

    <!-- 新品推荐 -->
    <div class="section">
      <div class="section-header">
        <h3>新品上市</h3>
        <span class="more" @click="goNewList">更多</span>
      </div>
      <van-grid :column-num="2" :gutter="10">
        <van-grid-item v-for="item in newProducts" :key="item.id" @click="goProduct(item.id)">
          <van-image :src="item.mainImage" class="product-image" />
          <div class="product-info">
            <div class="product-name">{{ item.productName }}</div>
            <div class="product-price">¥{{ item.price }}</div>
            <div class="tag new-tag">新品</div>
          </div>
        </van-grid-item>
      </van-grid>
    </div>

    <!-- 综合推荐 -->
    <div class="section">
      <div class="section-header">
        <h3>为你推荐</h3>
        <van-button size="small" type="primary" plain @click="refreshRecommend">换一批</van-button>
      </div>
      <van-grid :column-num="2" :gutter="10">
        <van-grid-item v-for="item in recommendProducts" :key="item.id" @click="goProduct(item.id)">
          <van-image :src="item.mainImage" class="product-image" />
          <div class="product-info">
            <div class="product-name">{{ item.productName }}</div>
            <div class="product-price">¥{{ item.price }}</div>
            <div class="product-brief">{{ item.brief }}</div>
          </div>
        </van-grid-item>
      </van-grid>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import {
  getRecommendProducts,
  getHotProducts,
  getNewProducts,
  recordView,
  type Product
} from '@/api/recommend'

// 搜索
const searchValue = ref('')
const onSearch = () => {
  showToast(`搜索: ${searchValue.value}`)
}

// 轮播图数据
const banners = ref([
  { id: 1, title: '夏季特惠', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=summer%20sale%20banner%20promotion%20colorful%20shopping&image_size=landscape_16_9' },
  { id: 2, title: '新品上市', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=new%20arrival%20banner%20tech%20products%20modern&image_size=landscape_16_9' },
  { id: 3, title: '限时折扣', image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=limited%20time%20discount%20banner%20sale%20red%20promotional&image_size=landscape_16_9' }
])

// 分类数据
const categories = ref([
  { id: 1, name: '手机', icon: 'phone-o' },
  { id: 2, name: '电脑', icon: 'laptop' },
  { id: 3, name: '平板', icon: 'tablet' },
  { id: 4, name: '配件', icon: 'headphones' }
])

// 商品数据
const recommendProducts = ref<Product[]>([])
const hotProducts = ref<Product[]>([])
const newProducts = ref<Product[]>([])

// 加载推荐商品
const loadRecommendProducts = async () => {
  try {
    const result = await getRecommendProducts(6)
    if (result.code === 200) {
      recommendProducts.value = result.data
    }
  } catch (error) {
    console.error('加载推荐商品失败:', error)
  }
}

// 加载热销商品
const loadHotProducts = async () => {
  try {
    const result = await getHotProducts(4)
    if (result.code === 200) {
      hotProducts.value = result.data
    }
  } catch (error) {
    console.error('加载热销商品失败:', error)
  }
}

// 加载新品
const loadNewProducts = async () => {
  try {
    const result = await getNewProducts(4)
    if (result.code === 200) {
      newProducts.value = result.data
    }
  } catch (error) {
    console.error('加载新品失败:', error)
  }
}

// 刷新推荐
const refreshRecommend = async () => {
  showToast('正在刷新...')
  await loadRecommendProducts()
  showToast('刷新成功')
}

// 跳转分类
const goCategory = (id: number) => {
  showToast(`分类: ${id}`)
}

// 跳转商品详情
const goProduct = (id: number) => {
  // 记录浏览
  recordView(id)
  showToast(`商品详情: ${id}`)
}

// 跳转热销列表
const goHotList = () => {
  showToast('查看更多热销商品')
}

// 跳转新品列表
const goNewList = () => {
  showToast('查看更多新品')
}

// 页面挂载时加载数据
onMounted(async () => {
  await Promise.all([
    loadRecommendProducts(),
    loadHotProducts(),
    loadNewProducts()
  ])
})
</script>

<style scoped>
.home {
  padding-bottom: 50px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.banner img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.category {
  margin: 10px 0;
  background-color: #fff;
}

.section {
  background-color: #fff;
  margin: 10px 0;
  padding: 10px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.more {
  font-size: 14px;
  color: #999;
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.product-info {
  padding: 8px;
}

.product-name {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
}

.product-price {
  color: #ee0a24;
  font-size: 16px;
  font-weight: bold;
  margin-top: 5px;
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
</style>