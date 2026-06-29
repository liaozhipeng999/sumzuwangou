<template>
  <div class="conversation-container">
    <div class="page-header">
      <h2>客服会话</h2>
      <el-badge :value="totalUnread" :hidden="totalUnread === 0" class="unread-badge">
        <el-button :icon="Refresh" circle @click="loadConversations" />
      </el-badge>
    </div>

    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="conversations.length === 0" class="empty-wrap">
      <el-empty description="暂无会话消息" />
    </div>

    <div v-else class="conversation-list">
      <div
        v-for="item in conversations"
        :key="item.userId"
        class="conversation-item"
        @click="openChat(item)"
      >
        <el-badge :value="item.unreadCount" :hidden="item.unreadCount === 0" :max="99">
          <el-avatar :size="48">
            <el-icon :size="24"><User /></el-icon>
          </el-avatar>
        </el-badge>
        <div class="conversation-info">
          <div class="info-top">
            <span class="user-name">用户 #{{ item.userId }}</span>
            <span class="send-time">{{ formatTime(item.lastSendTime) }}</span>
          </div>
          <div class="info-bottom">
            <span class="last-message">
              <template v-if="item.lastSenderType === 'SHOP'">[我]</template>
              {{ truncateMessage(item.lastMessage) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh, User } from '@element-plus/icons-vue'
import { getMerchantConversations, getMerchantUnreadCount } from '@/api/customer'
import type { Conversation } from '@/api/customer'
import { useMerchantStore } from '@/stores/merchant'
import { useWebSocket } from '@/composables/useWebSocket'

const router = useRouter()
const merchantStore = useMerchantStore()
const { connect, subscribeShopMessages, subscribeShopUnread } = useWebSocket()
const loading = ref(false)
const conversations = ref<Conversation[]>([])
const totalUnread = ref(0)
let pollTimer: ReturnType<typeof setInterval> | null = null
let unsubMsg: (() => void) | null = null
let unsubUnread: (() => void) | null = null

const shopId = computed(() => merchantStore.merchantInfo?.id || 0)

async function loadConversations() {
  if (!shopId.value) return
  loading.value = true
  try {
    const res = await getMerchantConversations(shopId.value)
    if (res.code === 200) {
      conversations.value = res.data || []
    }
  } catch (e) {
    console.error('加载会话列表失败', e)
  } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  if (!shopId.value) return
  try {
    const res = await getMerchantUnreadCount(shopId.value)
    if (res.code === 200) {
      totalUnread.value = res.data?.total || 0
    }
  } catch (e) {
    console.error('获取未读数失败', e)
  }
}

function openChat(item: Conversation) {
  router.push({
    name: 'ChatRoom',
    params: { userId: item.userId }
  })
}

function formatTime(time: string | null): string {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const isToday =
    date.getFullYear() === now.getFullYear() &&
    date.getMonth() === now.getMonth() &&
    date.getDate() === now.getDate()

  if (isToday) {
    return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  }
  return `${date.getMonth() + 1}/${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function truncateMessage(msg: string, len = 40): string {
  if (!msg) return ''
  return msg.length > len ? msg.substring(0, len) + '...' : msg
}

onMounted(() => {
  loadConversations()
  loadUnreadCount()
  // 启动 WebSocket 连接
  connect()
  // 订阅新消息通知（更新会话列表）
  if (shopId.value) {
    unsubMsg = subscribeShopMessages(shopId.value, () => {
      loadConversations()
      loadUnreadCount()
    })
    // 订阅未读数通知
    unsubUnread = subscribeShopUnread(shopId.value, (msg) => {
      if (msg.unreadCount !== undefined) {
        totalUnread.value = msg.unreadCount
      }
    })
  }
  // 降级：WebSocket 未连接时用轮询
  pollTimer = setInterval(() => {
    loadConversations()
    loadUnreadCount()
  }, 30000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
  if (unsubMsg) unsubMsg()
  if (unsubUnread) unsubUnread()
})
</script>

<style scoped>
.conversation-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  min-height: calc(100vh - 140px);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.loading-wrap {
  padding: 20px;
}

.empty-wrap {
  padding: 80px 0;
}

.conversation-list {
  display: flex;
  flex-direction: column;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 12px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background-color 0.2s;
  border-radius: 6px;
}

.conversation-item:hover {
  background-color: #f5f7fa;
}

.conversation-info {
  flex: 1;
  min-width: 0;
}

.info-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.user-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.send-time {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
  margin-left: 10px;
}

.info-bottom {
  display: flex;
  align-items: center;
}

.last-message {
  font-size: 13px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
