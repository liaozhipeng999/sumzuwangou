<template>
  <div class="product-list-page">
    <!-- 顶部搜索栏 -->
    <div class="search-header">
      <van-search 
        v-model="searchKeyword" 
        placeholder="搜索商品" 
        show-action
        @search="handleSearch"
      >
        <template #action>
          <van-button size="small" type="primary" @click="handleSearch">搜索</van-button>
        </template>
      </van-search>
    </div>

    <!-- 分类筛选 -->
    <div class="filter-bar">
      <van-tabs v-model="activeFilter" color="#ee0a24">
        <van-tab title="综合">综合</van-tab>
        <van-tab title="销量">销量</van-tab>
        <van-tab title="价格">价格</van-tab>
        <van-tab title="新品">新品</van-tab>
      </van-tabs>
    </div>

    <!-- 商品瀑布流 -->
    <div class="waterfall-container">
      <div class="waterfall">
        <div class="waterfall-column">
          <ProductCard
            v-for="item in leftProducts"
            :key="item.id"
            :product="item"
            @click="goProduct"
          />
        </div>
        <div class="waterfall-column">
          <ProductCard
            v-for="item in rightProducts"
            :key="item.id"
            :product="item"
            @click="goProduct"
          />
        </div>
      </div>

      <!-- 加载更多 -->
      <div class="load-more-container">
        <van-loading v-if="loading" />
        <div v-else-if="hasMore" class="load-more-trigger">
          <span class="load-more-text">上拉加载更多</span>
        </div>
        <div v-else class="no-more">— 没有更多商品了 —</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from './ProductCard.vue'

const router = useRouter()

// 搜索关键词
const searchKeyword = ref('')

// 筛选标签
const activeFilter = ref(0)

// 加载状态
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)

// 当前显示的商品
const displayProducts = ref<any[]>([])

// 左侧列商品
const leftProducts = computed(() => {
  return displayProducts.value.filter((_, index) => index % 2 === 0)
})

// 右侧列商品
const rightProducts = computed(() => {
  return displayProducts.value.filter((_, index) => index % 2 === 1)
})

// 搜索
const handleSearch = () => {
  hasMore.value = true
  page.value = 1
}

// 跳转到商品详情
const goProduct = (product: any) => {
  router.push(`/product/${product.id}`)
}
</script>

<style scoped>
.product-list-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: env(safe-area-inset-bottom);
}

.search-header {
  padding: 12px 16px;
  background: #fff;
}

.filter-bar {
  background: #fff;
}

.waterfall-container {
  padding: 16px;
}

.waterfall {
  display: flex;
  gap: 12px;
}

.waterfall-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.load-more-container {
  padding: 20px;
  text-align: center;
}

.load-more-trigger {
  padding: 16px;
  background: #fff;
  border-radius: 8px;
}

.load-more-text {
  color: #999;
  font-size: 14px;
}

.no-more {
  color: #ccc;
  font-size: 14px;
}
</style>