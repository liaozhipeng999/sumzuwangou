<template>
  <div class="message-page">
    <!-- 顶部导航 -->
    <div class="message-header">
      <span class="header-title">消息</span>
      <div class="header-actions">
        <van-icon name="search" class="header-icon" @click="showSearch = !showSearch" />
        <van-icon name="ellipsis" class="header-icon" @click="showActionSheet = true" />
      </div>
    </div>

    <!-- 搜索栏 -->
    <van-search
      v-model="searchKeyword"
      v-show="showSearch"
      placeholder="搜索消息"
      shape="round"
      background="#fff"
      @search="onSearch"
    />

    <!-- 总未读统计 -->
    <div class="unread-banner" v-if="messageStore.unreadCount > 0">
      <van-icon name="bell" color="#fff" size="16" />
      <span>你有 <b>{{ messageStore.unreadCount }}</b> 条未读消息</span>
      <span class="read-all-btn" @click="handleReadAll">全部已读</span>
    </div>

    <!-- 下拉刷新 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
      <!-- 骨架屏加载 -->
      <template v-if="initialLoading">
        <div class="skeleton-wrapper">
          <div class="skeleton-actions">
            <div class="skeleton-action" v-for="i in 3" :key="i">
              <div class="skeleton-circle"></div>
              <div class="skeleton-text-short"></div>
            </div>
          </div>
          <div class="skeleton-card" v-for="i in 4" :key="i">
            <div class="skeleton-avatar"></div>
            <div class="skeleton-content">
              <div class="skeleton-text-long"></div>
              <div class="skeleton-text-medium"></div>
            </div>
          </div>
        </div>
      </template>

      <!-- 实际内容 -->
      <template v-else>
        <!-- 功能入口 -->
        <div class="quick-actions">
          <div class="action-item" @click="goToType('merchant')">
            <div class="action-icon merchant-bg">
              <van-icon name="chat-o" size="22" color="#fff" />
            </div>
            <span>商家消息</span>
            <van-badge v-if="getUnread('merchant') > 0" :content="getUnread('merchant')" max="99" />
          </div>
          <div class="action-item" @click="goToType('logistics')">
            <div class="action-icon logistics-bg">
              <van-icon name="logistics" size="22" color="#fff" />
            </div>
            <span>物流消息</span>
            <van-badge v-if="getUnread('logistics') > 0" :content="getUnread('logistics')" max="99" />
          </div>
          <div class="action-item" @click="goToType('transaction')">
            <div class="action-icon transaction-bg">
              <van-icon name="bill-o" size="22" color="#fff" />
            </div>
            <span>交易消息</span>
            <van-badge v-if="getUnread('transaction') > 0" :content="getUnread('transaction')" max="99" />
          </div>
          <div class="action-item" @click="goToType('system')">
            <div class="action-icon system-bg">
              <van-icon name="bell" size="22" color="#fff" />
            </div>
            <span>系统通知</span>
            <van-badge v-if="getUnread('system') > 0" :content="getUnread('system')" max="99" />
          </div>
        </div>

        <!-- 消息分类列表 -->
        <div class="message-categories">
          <div
            v-for="item in filteredCategoryList"
            :key="item.type"
            class="category-card"
            @click="goToType(item.type)"
          >
            <div class="card-left">
              <div class="card-icon" :style="{ backgroundColor: item.bgColor }">
                <van-icon :name="item.icon" size="20" color="#fff" />
              </div>
              <div class="card-info">
                <div class="card-title-row">
                  <span class="card-title">{{ item.label }}</span>
                  <van-badge v-if="item.unreadCount > 0" :content="item.unreadCount" max="99" />
                </div>
                <span class="card-desc" v-if="item.latestMessage">{{ item.latestMessage.content }}</span>
                <span class="card-desc empty" v-else>暂无消息</span>
              </div>
            </div>
            <div class="card-right">
              <span class="card-time" v-if="item.latestMessage">{{ formatTime(item.latestMessage.createdAt) }}</span>
              <van-icon name="arrow" color="#c8c9cc" />
            </div>
          </div>
        </div>

        <!-- 推荐活动 -->
        <div class="promo-section">
          <div class="promo-header">
            <span class="promo-title">你可能感兴趣</span>
          </div>
          <van-swipe :autoplay="4000" indicator-color="#ee0a24" class="promo-swipe">
            <van-swipe-item>
              <div class="promo-card" @click="goToType('promotion')">
                <div class="promo-icon-wrap">
                  <van-icon name="gift-o" size="28" color="#ff6034" />
                </div>
                <div class="promo-info">
                  <span class="promo-name">618 年中大促</span>
                  <span class="promo-desc">全场低至5折，领券再减30元</span>
                </div>
                <van-tag type="danger" size="medium" round>去看看</van-tag>
              </div>
            </van-swipe-item>
            <van-swipe-item>
              <div class="promo-card" @click="goToType('promotion')">
                <div class="promo-icon-wrap">
                  <van-icon name="fire-o" size="28" color="#ff6034" />
                </div>
                <div class="promo-info">
                  <span class="promo-name">限时秒杀</span>
                  <span class="promo-desc">每天10点准时开抢，低至1元起</span>
                </div>
                <van-tag type="danger" size="medium" round>去抢购</van-tag>
              </div>
            </van-swipe-item>
          </van-swipe>
        </div>

        <!-- 空状态 -->
        <van-empty
          v-if="categoryList.length === 0 || categoryList.every(c => !c.latestMessage)"
          image="search"
          description="暂无消息记录"
        />
      </template>
    </van-pull-refresh>

    <!-- 右上角更多操作 -->
    <van-action-sheet
      v-model:show="showActionSheet"
      :actions="headerActions"
      cancel-text="取消"
      @select="onHeaderAction"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { useMessageStore } from '@/stores/message'
import { MESSAGE_TYPE_CONFIG, type MessageType } from '@/api/message'

const router = useRouter()
const messageStore = useMessageStore()

const refreshing = ref(false)
const initialLoading = ref(true)
const showSearch = ref(false)
const searchKeyword = ref('')
const showActionSheet = ref(false)

// 顶部更多操作
const headerActions = [
  { name: '全部已读', value: 'readAll' },
  { name: '消息设置', value: 'settings' },
]

// 分类列表（固定顺序）
const categoryOrder: MessageType[] = ['merchant', 'logistics', 'transaction', 'system', 'promotion']

const categoryList = computed(() => {
  return categoryOrder.map(type => {
    const config = MESSAGE_TYPE_CONFIG[type]
    const summaryItem = messageStore.summary.find(s => s.type === type)
    return {
      type,
      label: config.label,
      icon: config.icon,
      bgColor: config.color,
      unreadCount: summaryItem?.unreadCount || 0,
      latestMessage: summaryItem?.latestMessage || null
    }
  })
})

// 搜索过滤
const filteredCategoryList = computed(() => {
  if (!searchKeyword.value.trim()) return categoryList.value
  const kw = searchKeyword.value.trim().toLowerCase()
  return categoryList.value.filter(item => {
    const matchLabel = item.label.toLowerCase().includes(kw)
    const matchContent = item.latestMessage?.content?.toLowerCase().includes(kw)
    return matchLabel || matchContent
  })
})

// 获取某分类未读数
function getUnread(type: MessageType): number {
  const item = messageStore.summary.find(s => s.type === type)
  return item?.unreadCount || 0
}

// 格式化时间
function formatTime(dateStr: string): string {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 跳转到分类消息详情
function goToType(type: MessageType) {
  router.push(`/message/${type}`)
}

// 下拉刷新
async function onRefresh() {
  await Promise.all([
    messageStore.fetchSummary(),
    messageStore.fetchUnreadCount()
  ])
  refreshing.value = false
}

// 搜索
function onSearch() {
  // 搜索逻辑已在 computed 中处理
}

// 全部已读
async function handleReadAll() {
  try {
    await showDialog({
      title: '提示',
      message: '确定将所有消息标记为已读吗？',
      showCancelButton: true,
    })
  } catch {
    return // 用户取消
  }
  try {
    await messageStore.markAllReadMessages()
    showToast('全部已读')
  } catch {
    // ignore
  }
}

// 顶部菜单操作
function onHeaderAction(action: { value: string }) {
  showActionSheet.value = false
  if (action.value === 'readAll') {
    handleReadAll()
  } else if (action.value === 'settings') {
    router.push('/setting/notification')
  }
}

onMounted(async () => {
  try {
    await Promise.all([
      messageStore.fetchSummary(),
      messageStore.fetchUnreadCount()
    ])
  } finally {
    initialLoading.value = false
  }
  // 启动未读数轮询（30秒）
  messageStore.startPolling(30000)
})

onUnmounted(() => {
  messageStore.stopPolling()
})
</script>

<style scoped>
.message-page {
  background: #f7f8fa;
  min-height: 100vh;
  padding-bottom: 70px;
}

/* 顶部导航 */
.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #323233;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  font-size: 20px;
  color: #646566;
  cursor: pointer;
}

/* 未读横幅 */
.unread-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #ee0a24 0%, #ff6034 100%);
  color: #fff;
  padding: 10px 16px;
  font-size: 13px;
}

.unread-banner b {
  font-size: 16px;
  margin: 0 2px;
}

.read-all-btn {
  margin-left: auto;
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  cursor: pointer;
}

.read-all-btn:active {
  background: rgba(255, 255, 255, 0.35);
}

/* 骨架屏 */
.skeleton-wrapper {
  padding: 16px;
}

.skeleton-actions {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.skeleton-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.skeleton-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #eee;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-text-short {
  width: 40px;
  height: 12px;
  background: #eee;
  border-radius: 4px;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-card {
  display: flex;
  gap: 12px;
  padding: 14px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 8px;
}

.skeleton-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #eee;
  flex-shrink: 0;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-text-long {
  width: 60%;
  height: 14px;
  background: #eee;
  border-radius: 4px;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-text-medium {
  width: 40%;
  height: 12px;
  background: #eee;
  border-radius: 4px;
  animation: skeleton-pulse 1.5s ease-in-out infinite 0.2s;
}

@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

/* 快捷入口 */
.quick-actions {
  display: flex;
  justify-content: space-around;
  background: #fff;
  padding: 16px 8px;
  margin-bottom: 8px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
  cursor: pointer;
  flex: 1;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.action-icon:active {
  transform: scale(0.92);
}

.merchant-bg { background: linear-gradient(135deg, #7232dd 0%, #9b59b6 100%); }
.logistics-bg { background: linear-gradient(135deg, #1989fa 0%, #36d1dc 100%); }
.transaction-bg { background: linear-gradient(135deg, #07c160 0%, #2ecc71 100%); }
.system-bg { background: linear-gradient(135deg, #909399 0%, #bdc3c7 100%); }

.action-item span {
  font-size: 12px;
  color: #646566;
}

.action-item .van-badge {
  position: absolute;
  top: -2px;
  right: calc(50% - 30px);
}

/* 消息分类卡片 */
.message-categories {
  background: #fff;
  border-radius: 8px;
  margin: 0 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.category-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f6f7;
  cursor: pointer;
  transition: background 0.2s;
}

.category-card:last-child {
  border-bottom: none;
}

.category-card:active {
  background: #f7f8fa;
}

.card-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.card-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: #323233;
}

.card-desc {
  font-size: 12px;
  color: #969799;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.card-desc.empty {
  color: #c8c9cc;
}

.card-right {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  margin-left: 8px;
}

.card-time {
  font-size: 11px;
  color: #c8c9cc;
}

/* 推荐活动 */
.promo-section {
  margin: 12px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.promo-header {
  padding: 12px 16px 8px;
}

.promo-title {
  font-size: 14px;
  font-weight: 500;
  color: #323233;
}

.promo-swipe {
  height: 80px;
}

.promo-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  cursor: pointer;
  height: 80px;
  box-sizing: border-box;
}

.promo-card:active {
  background: #f7f8fa;
}

.promo-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #fff5f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.promo-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.promo-name {
  font-size: 14px;
  font-weight: 500;
  color: #323233;
}

.promo-desc {
  font-size: 12px;
  color: #969799;
}
</style>
