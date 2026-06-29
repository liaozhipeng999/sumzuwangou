<template>
  <div class="favorite-page">
    <!-- 顶部导航 -->
    <van-nav-bar 
      title="商品收藏" 
      left-arrow 
      @click-left="goBack"
      class="nav-bar"
    >
      <template #right>
        <van-button 
          text="true" 
          size="small" 
          @click="showClearConfirm = true" 
          v-if="favoriteList.length > 0"
          class="clear-btn"
        >
          清空
        </van-button>
      </template>
    </van-nav-bar>

    <!-- 主内容区 -->
    <div class="page-content">
      <!-- 空状态 -->
      <div v-if="favoriteList.length === 0 && !isLoading" class="empty-state">
        <div class="empty-icon">
          <van-icon name="heart-o" size="100" color="#e8e8e8" />
        </div>
        <p class="empty-title">暂无收藏商品</p>
        <p class="empty-desc">去挑选心仪的商品吧</p>
        <van-button type="primary" round class="go-shopping-btn" @click="goShopping">
          去逛逛
        </van-button>
      </div>

      <!-- 加载中状态 -->
      <div v-else-if="isLoading && favoriteList.length === 0" class="loading-state">
        <van-loading size="32" color="#ee0a24" />
        <p class="loading-text">加载中...</p>
      </div>

      <!-- 收藏列表 -->
      <div v-else class="favorite-list">
        <div v-if="total > 0" class="list-header">
          <span class="list-count">共 {{ total }} 件商品</span>
        </div>
        
        <FavoriteItem
          v-for="item in favoriteList"
          :key="item.id"
          :item="item"
          :is-favorite="true"
          @remove="removeItem"
        />
        
        <!-- 加载更多 -->
        <div v-if="hasMore && !isLoading" class="load-more">
          <van-loading size="20" color="#ee0a24" />
          <span>加载更多...</span>
        </div>
        <div v-if="!hasMore && favoriteList.length > 0" class="no-more">
          - 已加载全部 -
        </div>
      </div>
    </div>

    <!-- 清空确认弹窗 -->
    <van-dialog 
      v-model:show="showClearConfirm" 
      title="清空收藏" 
      message="确定要清空所有收藏商品吗？"
      confirm-button-color="#ee0a24"
      @confirm="handleClear"
    />

    <!-- 删除确认弹窗 -->
    <van-dialog 
      v-model:show="showDeleteConfirm" 
      title="取消收藏" 
      message="确定要取消收藏这件商品吗？"
      confirm-button-color="#ee0a24"
      @confirm="handleDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import FavoriteItem from '@/components/Favorite/FavoriteItem.vue'
import { 
  getFavoriteList, 
  removeFavorite, 
  clearFavorites,
  type FavoriteProduct 
} from '@/api/favorite'

const router = useRouter()

const isLoading = ref(false)
const showClearConfirm = ref(false)
const showDeleteConfirm = ref(false)
const favoriteList = ref<FavoriteProduct[]>([])
const hasMore = ref(true)
const page = ref(1)
const pageSize = 20
const total = ref(0)
const deletingId = ref<number | null>(null)

// 获取当前用户ID
const getUserId = (): number => {
  const userId = sessionStorage.getItem('user_id')
  if (userId) {
    return parseInt(userId, 10)
  }
  return 1
}

// 加载收藏列表
const loadFavorites = async (isRefresh: boolean = false) => {
  if (isLoading.value) return
  
  isLoading.value = true
  
  try {
    const userId = getUserId()
    const currentPage = isRefresh ? 1 : page.value
    
    const response = await getFavoriteList(userId, currentPage, pageSize)
    
    // 调试日志：打印后端返回的原始数据
    console.log('=== 收藏列表API响应数据 ===')
    console.log('用户ID:', userId)
    console.log('完整响应(JSON):', JSON.stringify(response, null, 2))
    console.log('数据总数:', response.data?.total)
    console.log('商品数量:', response.data?.products?.length)
    if (response.data?.products?.length > 0) {
      console.log('第一个商品详情:', JSON.stringify(response.data.products[0], null, 2))
    }
    console.log('============================')
    
    if (response.code === 200 && response.data) {
      if (isRefresh) {
        favoriteList.value = response.data.products
        total.value = response.data.total
      } else {
        favoriteList.value = [...favoriteList.value, ...response.data.products]
      }
      
      hasMore.value = response.data.products.length === pageSize
      if (!isRefresh) {
        page.value++
      }
    } else {
      showToast(response.message || '加载失败')
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    showToast('加载失败')
  } finally {
    isLoading.value = false
  }
}

// 移除单个收藏
const removeItem = (productId: number) => {
  deletingId.value = productId
  showDeleteConfirm.value = true
}

// 处理删除
const handleDelete = async () => {
  if (deletingId.value === null) return
  
  try {
    const userId = getUserId()
    const response = await removeFavorite(userId, deletingId.value)
    
    if (response.code === 200) {
      favoriteList.value = favoriteList.value.filter(item => item.id !== deletingId.value)
      total.value--
      showToast('已取消收藏')
    } else {
      showToast(response.message || '取消收藏失败')
    }
  } catch (error) {
    console.error('取消收藏失败:', error)
    showToast('取消收藏失败')
  } finally {
    deletingId.value = null
    showDeleteConfirm.value = false
  }
}

// 处理清空
const handleClear = async () => {
  try {
    const userId = getUserId()
    const response = await clearFavorites(userId)
    
    if (response.code === 200) {
      favoriteList.value = []
      total.value = 0
      hasMore.value = false
      showToast('已清空收藏')
    } else {
      showToast(response.message || '清空失败')
    }
  } catch (error) {
    console.error('清空收藏失败:', error)
    showToast('清空失败')
  } finally {
    showClearConfirm.value = false
  }
}

// 去购物
const goShopping = () => {
  router.push('/home')
}

// 返回
const goBack = () => {
  router.back()
}

// 页面加载
onMounted(() => {
  loadFavorites(true)
  
  // 滚动加载更多
  window.addEventListener('scroll', handleScroll)
})

// 页面卸载
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 滚动处理
const handleScroll = () => {
  const scrollTop = window.scrollY
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight
  
  if (scrollTop + windowHeight >= documentHeight - 100 && hasMore.value && !isLoading.value) {
    loadFavorites(false)
  }
}
</script>

<style lang="scss" scoped>
.favorite-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 导航栏 */
.nav-bar {
  background: linear-gradient(135deg, #ee0a24 0%, #ff4757 100%);
  
  :deep(.van-nav-bar__title) {
    color: #fff;
    font-weight: 600;
  }
  
  :deep(.van-nav-bar__text) {
    color: rgba(255, 255, 255, 0.9);
  }
  
  :deep(.van-icon) {
    color: #fff;
  }
}

.clear-btn {
  color: #fff;
  font-size: 14px;
}

/* 主内容区 */
.page-content {
  padding: 16px;
  padding-bottom: 60px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 20px 80px;
  
  .empty-icon {
    margin-bottom: 28px;
    opacity: 0.5;
  }
  
  .empty-title {
    font-size: 17px;
    color: #666;
    margin-bottom: 10px;
    font-weight: 500;
  }
  
  .empty-desc {
    font-size: 14px;
    color: #999;
    margin-bottom: 40px;
  }
  
  .go-shopping-btn {
    width: 180px;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #ee0a24 0%, #ff4757 100%);
    border: none;
    
    &::after {
      border: none;
    }
  }
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 150px 20px;
  
  .loading-text {
    margin-top: 20px;
    font-size: 14px;
    color: #999;
  }
}

/* 收藏列表 */
.favorite-list {
  display: flex;
  flex-direction: column;
  
  .list-header {
    padding: 8px 0 12px;
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 12px;
    
    .list-count {
      font-size: 14px;
      color: #666;
    }
  }
}

/* 加载更多 */
.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 24px;
  font-size: 14px;
  color: #999;
}

.no-more {
  text-align: center;
  padding: 20px;
  font-size: 13px;
  color: #ccc;
}
</style>