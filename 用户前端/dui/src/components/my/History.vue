<template>
  <div class="history-page">
    <!-- 顶部导航 -->
    <div class="history-header">
      <van-nav-bar title="历史浏览" left-arrow @click-left="goBack">
        <template #right>
          <van-button text="true" @click="showManagement = !showManagement">
            {{ showManagement ? '完成' : '管理' }}
          </van-button>
        </template>
      </van-nav-bar>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs">
      <van-tabs v-model="activeTab" @change="onTabChange">
        <van-tab title="商品">
          <div class="tab-content">
            <!-- 精选推荐 -->
            <div v-if="featuredHistory.length > 0" class="featured-section">
              <h3 class="section-title">为你精选看过的商品</h3>
              <van-grid :column-num="4" :gutter="10" style="padding: 0 15px;">
                <van-grid-item
                  v-for="item in featuredHistory"
                  :key="item.productId"
                  @click="goProduct(item.productId)"
                >
                  <van-image :src="item.mainImage" class="featured-image" />
                </van-grid-item>
              </van-grid>
            </div>

            <!-- 今日浏览 -->
            <div v-if="todayHistory.length > 0" class="today-section">
              <h3 class="section-title">今天</h3>
              <van-cell-group>
                <van-cell
                  v-for="item in todayHistory"
                  :key="item.productId"
                  clickable
                  @click="goProduct(item.productId)"
                  :border="false"
                >
                  <template #icon>
                    <van-image :src="item.mainImage" class="history-image" />
                  </template>
                  <template #title>
                    <div class="product-title">{{ item.productName }}</div>
                    <div class="product-meta">
                      <span v-if="item.browseCount > 1" class="browse-count">
                        最近看过 {{ item.browseCount }} 次
                      </span>
                    </div>
                  </template>
                  <template #right-icon>
                    <div class="product-price">
                      ¥{{ item.price }}
                    </div>
                  </template>
                  <!-- 删除按钮（管理模式） -->
                  <template #extra v-if="showManagement">
                    <van-checkbox
                      :checked="selectedItems.includes(item.productId)"
                      @change="toggleSelect(item.productId)"
                    />
                  </template>
                </van-cell>
              </van-cell-group>
            </div>

            <!-- 更多历史（非今日） -->
            <div v-if="otherHistory.length > 0" class="other-section">
              <h3 class="section-title">更多历史</h3>
              <van-cell-group>
                <van-cell
                  v-for="item in otherHistory"
                  :key="item.productId"
                  clickable
                  @click="goProduct(item.productId)"
                  :border="false"
                >
                  <template #icon>
                    <van-image :src="item.mainImage" class="history-image" />
                  </template>
                  <template #title>
                    <div class="product-title">{{ item.productName }}</div>
                    <div class="product-meta">
                      <span class="browse-time">{{ formatTime(item.browseTime) }}</span>
                      <span v-if="item.browseCount > 1" class="browse-count">
                        看过 {{ item.browseCount }} 次
                      </span>
                    </div>
                  </template>
                  <template #right-icon>
                    <div class="product-price">
                      ¥{{ item.price }}
                    </div>
                  </template>
                  <!-- 删除按钮（管理模式） -->
                  <template #extra v-if="showManagement">
                    <van-checkbox
                      :checked="selectedItems.includes(item.productId)"
                      @change="toggleSelect(item.productId)"
                    />
                  </template>
                </van-cell>
              </van-cell-group>
            </div>

            <!-- 空状态 -->
            <div v-if="historyStore.sortedHistoryList.length === 0" class="empty-state">
              <van-empty description="暂无浏览记录" />
            </div>
          </div>
        </van-tab>
        <van-tab title="商品视频">
          <div class="empty-state">
            <van-empty description="暂无视频浏览记录" />
          </div>
        </van-tab>
        <van-tab title="店铺">
          <div class="empty-state">
            <van-empty description="暂无店铺浏览记录" />
          </div>
        </van-tab>
        <van-tab title="直播">
          <div class="empty-state">
            <van-empty description="暂无直播浏览记录" />
          </div>
        </van-tab>
      </van-tabs>
    </div>

    <!-- 底部操作栏（管理模式） -->
    <div v-if="showManagement" class="bottom-bar">
      <van-button
        type="default"
        @click="selectAll"
      >
        {{ isAllSelected ? '取消全选' : '全选' }}
      </van-button>
      <van-button
        type="danger"
        @click="deleteSelected"
        :disabled="selectedItems.length === 0"
      >
        删除 ({{ selectedItems.length }})
      </van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useHistoryStore } from '@/stores/history'

const router = useRouter()
const historyStore = useHistoryStore()

// 当前选中的标签
const activeTab = ref(0)

// 是否显示管理模式
const showManagement = ref(false)

// 选中的商品ID列表
const selectedItems = ref<number[]>([])

// 获取精选推荐
const featuredHistory = computed(() => historyStore.featuredHistory)

// 获取今日浏览
const todayHistory = computed(() => historyStore.todayHistory)

// 获取其他历史记录（非今日）
const otherHistory = computed(() => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const todayTimestamp = today.getTime()
  return historyStore.sortedHistoryList.filter(item => item.browseTime < todayTimestamp)
})

// 是否全选
const isAllSelected = computed(() => {
  const allIds = historyStore.sortedHistoryList.map(item => item.productId)
  return allIds.length > 0 && selectedItems.value.length === allIds.length
})

// 格式化时间
function formatTime(timestamp: number): string {
  const now = Date.now()
  const diff = now - timestamp
  
  const minutes = Math.floor(diff / (60 * 1000))
  const hours = Math.floor(diff / (60 * 60 * 1000))
  const days = Math.floor(diff / (24 * 60 * 60 * 1000))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  const date = new Date(timestamp)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

// 返回
function goBack() {
  router.back()
}

// 跳转到商品详情
function goProduct(productId: number) {
  if (!showManagement.value) {
    router.push(`/product/${productId}`)
  }
}

// 标签切换
function onTabChange() {
  selectedItems.value = []
}

// 切换选择
function toggleSelect(productId: number) {
  const index = selectedItems.value.indexOf(productId)
  if (index >= 0) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(productId)
  }
}

// 全选/取消全选
function selectAll() {
  if (isAllSelected.value) {
    selectedItems.value = []
  } else {
    selectedItems.value = historyStore.sortedHistoryList.map(item => item.productId)
  }
}

// 删除选中的记录
function deleteSelected() {
  selectedItems.value.forEach(productId => {
    historyStore.removeHistory(productId)
  })
  selectedItems.value = []
  showManagement.value = false
  showToast('删除成功')
}

// 页面挂载
onMounted(() => {
  // 如果还未初始化，进行初始化
  if (historyStore.historyList.length === 0) {
    historyStore.init()
  }
})
</script>

<style scoped>
.history-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.history-header {
  position: sticky;
  top: 0;
  z-index: 100;
}

.category-tabs {
  min-height: calc(100vh - 50px);
}

.tab-content {
  padding-bottom: 60px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  padding: 15px;
  margin: 0;
  background-color: #fff;
}

.featured-section {
  background-color: #fff;
  margin-bottom: 10px;
}

.featured-image {
  width: 100%;
  height: 80px;
  border-radius: 8px;
}

.today-section,
.other-section {
  background-color: #fff;
  margin-bottom: 10px;
}

.history-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.product-title {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-meta {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.browse-count {
  margin-right: 10px;
}

.browse-time {
  margin-right: 10px;
}

.product-price {
  font-size: 16px;
  font-weight: 600;
  color: #ee0a24;
}

.empty-state {
  padding: 60px 0;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 10px;
  padding: 10px 15px;
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.bottom-bar van-button {
  flex: 1;
}
</style>
