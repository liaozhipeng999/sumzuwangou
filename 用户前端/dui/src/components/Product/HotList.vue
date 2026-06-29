<template>
  <div class="hot-list">
    <!-- 顶部导航 -->
    <div class="nav-bar">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="nav-title">热销榜单</span>
      <div class="placeholder"></div>
    </div>

    <!-- 榜单头部 -->
    <div class="header-banner">
      <div class="banner-content">
        <div class="banner-title">🔥 热销榜单</div>
        <div class="banner-subtitle">为你精选最受欢迎的商品</div>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <van-tabs v-model="activeFilter" active-color="#ee0a24" color="#666">
        <van-tab title="综合">
          <div class="product-list">
            <van-cell-group inset>
              <van-cell 
                v-for="(item, index) in hotProducts" 
                :key="item.id" 
                class="product-cell"
                @click="goProduct(item.id)"
              >
                <div class="rank-badge" :class="getRankClass(index)">{{ index + 1 }}</div>
                <van-image :src="item.mainImage" class="product-image" />
                <div class="product-info">
                  <div class="product-name">{{ item.productName }}</div>
                  <div class="product-brief">{{ item.brief }}</div>
                  <div class="product-bottom">
                    <span class="product-price">¥{{ item.price }}</span>
                    <span class="product-sales">已售{{ item.sales }}件</span>
                  </div>
                </div>
              </van-cell>
            </van-cell-group>
          </div>
        </van-tab>
        <van-tab title="销量">
          <div class="product-list">
            <van-cell-group inset>
              <van-cell 
                v-for="(item, index) in sortedBySales" 
                :key="item.id" 
                class="product-cell"
                @click="goProduct(item.id)"
              >
                <div class="rank-badge" :class="getRankClass(index)">{{ index + 1 }}</div>
                <van-image :src="item.mainImage" class="product-image" />
                <div class="product-info">
                  <div class="product-name">{{ item.productName }}</div>
                  <div class="product-brief">{{ item.brief }}</div>
                  <div class="product-bottom">
                    <span class="product-price">¥{{ item.price }}</span>
                    <span class="product-sales">已售{{ item.sales }}件</span>
                  </div>
                </div>
              </van-cell>
            </van-cell-group>
          </div>
        </van-tab>
        <van-tab title="价格">
          <div class="product-list">
            <van-cell-group inset>
              <van-cell 
                v-for="(item, index) in sortedByPrice" 
                :key="item.id" 
                class="product-cell"
                @click="goProduct(item.id)"
              >
                <div class="rank-badge" :class="getRankClass(index)">{{ index + 1 }}</div>
                <van-image :src="item.mainImage" class="product-image" />
                <div class="product-info">
                  <div class="product-name">{{ item.productName }}</div>
                  <div class="product-brief">{{ item.brief }}</div>
                  <div class="product-bottom">
                    <span class="product-price">¥{{ item.price }}</span>
                    <span class="product-sales">已售{{ item.sales }}件</span>
                  </div>
                </div>
              </van-cell>
            </van-cell-group>
          </div>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotProducts, type Product } from '@/api/recommend'

const router = useRouter()

const activeFilter = ref(0)
const hotProducts = ref<Product[]>([])

const sortedBySales = computed(() => {
  return [...hotProducts.value].sort((a, b) => b.sales - a.sales)
})

const sortedByPrice = computed(() => {
  return [...hotProducts.value].sort((a, b) => a.price - b.price)
})

const goBack = () => {
  router.back()
}

const goProduct = (id: number) => {
  router.push(`/product/${id}`)
}

const getRankClass = (index: number) => {
  if (index === 0) return 'rank-first'
  if (index === 1) return 'rank-second'
  if (index === 2) return 'rank-third'
  return ''
}

onMounted(async () => {
  try {
    const result = await getHotProducts(20)
    if (result.code === 200) {
      hotProducts.value = result.data
    }
  } catch (error) {
    console.error('加载热销商品失败:', error)
  }
})
</script>

<style scoped>
.hot-list {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #333;
}

.nav-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.placeholder {
  width: 40px;
}

.header-banner {
  background: linear-gradient(135deg, #ff6b6b, #ee0a24);
  padding: 24px 16px;
  text-align: center;
}

.banner-title {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8px;
}

.banner-subtitle {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
}

.filter-tabs {
  background-color: #fff;
}

.product-list {
  padding: 12px;
}

.product-cell {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 12px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.rank-badge {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: #999;
  font-size: 14px;
  font-weight: bold;
  border-radius: 50%;
  margin-right: 12px;
}

.rank-first {
  background-color: #ffd700;
  color: #fff;
}

.rank-second {
  background-color: #c0c0c0;
  color: #fff;
}

.rank-third {
  background-color: #cd7f32;
  color: #fff;
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  margin-right: 12px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-brief {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #ee0a24;
}

.product-sales {
  font-size: 12px;
  color: #999;
}
</style>