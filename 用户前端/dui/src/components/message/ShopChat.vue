<template>
  <div class="shop-chat-page">
    <!-- 顶部导航 -->
    <van-nav-bar :title="shopName" left-arrow @click-left="goBack">
      <template #right>
        <van-icon name="more-o" size="20" />
      </template>
    </van-nav-bar>

    <!-- 对话消息列表 -->
    <div class="chat-body" ref="chatBodyRef">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
        <div class="load-more" v-if="hasMore && !loadingMessages" @click="loadMore">
          <span v-if="loadingMore">加载中...</span>
          <span v-else>点击加载更多历史消息</span>
        </div>

        <template v-if="loadingMessages">
          <div class="chat-item">
            <div class="avatar-skeleton"></div>
            <div class="chat-content">
              <div class="bubble-skeleton bubble-skeleton-l"></div>
            </div>
          </div>
          <div class="chat-item is-self">
            <div class="chat-content">
              <div class="bubble-skeleton bubble-skeleton-r"></div>
            </div>
            <div class="avatar-skeleton"></div>
          </div>
          <div class="chat-item">
            <div class="avatar-skeleton"></div>
            <div class="chat-content">
              <div class="bubble-skeleton bubble-skeleton-l bubble-skeleton-long"></div>
            </div>
          </div>
        </template>

        <template v-else>
          <div
            v-for="msg in messages"
            :key="msg.messageId"
            :class="['chat-item', { 'is-self': msg.senderType === 'USER' }]"
          >
            <!-- 商家头像（左侧） -->
            <van-image
              v-if="msg.senderType === 'SHOP'"
              :src="shopLogo"
              class="avatar"
              round
              width="36"
              height="36"
            />
            <div class="chat-content">
              <div :class="['bubble', { self: msg.senderType === 'USER' }]">
                {{ msg.content }}
              </div>
              <div class="chat-time">{{ formatTime(msg.sendTime) }}</div>
            </div>
            <!-- 用户头像（右侧） -->
            <van-image
              v-if="msg.senderType === 'USER'"
              src=""
              class="avatar"
              round
              width="36"
              height="36"
            >
              <template #error>
                <div class="avatar-placeholder">我</div>
              </template>
            </van-image>
          </div>

          <van-empty
            v-if="messages.length === 0"
            description="暂无消息，发送一条消息开始对话吧"
          />
        </template>
      </van-pull-refresh>
    </div>

    <!-- 新消息提示条 -->
    <div
      v-if="isUserReadingHistory && messages.length > 0"
      class="new-message-hint"
      @click="scrollToBottom(true); isUserReadingHistory = false"
    >
      有新消息，点击查看
    </div>

    <!-- 底部输入区域 -->
    <div class="chat-input-area">
      <van-field
        v-model="inputMessage"
        placeholder="输入消息..."
        :border="false"
        @keyup.enter="handleSend"
      />
      <van-button
        type="primary"
        size="small"
        :disabled="!inputMessage.trim()"
        :loading="sending"
        @click="handleSend"
      >
        发送
      </van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getMessages, sendMessage, markAsRead, getShopInfo } from '@/api/customerChat'
import { useUserStore } from '@/stores/user'

interface ChatMessage {
  messageId: number
  senderId: number
  senderType: 'USER' | 'SHOP'
  content: string
  sendTime: string
  messageType: string
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const shopId = computed(() => Number(route.params.shopId))
const userId = computed(() => userStore.userInfo?.id || 0)

const shopName = ref('客服对话')
const shopLogo = ref('')
const shopInfoLoaded = ref(false)
const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const sending = ref(false)
const refreshing = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const loadingMessages = ref(true)
const currentPage = ref(1)
const pageSize = 20
const chatBodyRef = ref<HTMLElement>()
const isUserReadingHistory = ref(false)

let pollTimer: ReturnType<typeof setInterval> | null = null
const POLL_INTERVAL = 5000

function formatTime(time: string): string {
  if (!time) return ''
  const d = new Date(time.replace(/-/g, '/'))
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  if (isToday) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return `${d.getMonth() + 1}/${d.getDate()} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

function scrollToBottom(smooth = false) {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTo({
        top: chatBodyRef.value.scrollHeight,
        behavior: smooth ? 'smooth' : 'auto'
      })
    }
  })
}

function scrollToBottomAnimated() {
  nextTick(() => {
    nextTick(() => {
      scrollToBottom(true)
    })
  })
}

const knownIds = new Set<number>()

function mergeNewMessages(list: ChatMessage[]): ChatMessage[] {
  const merged: ChatMessage[] = []
  for (const m of list) {
    if (!knownIds.has(m.messageId)) {
      knownIds.add(m.messageId)
      merged.push(m)
    }
  }
  return merged
}

async function loadMessages(reset = false, appendBottom = false) {
  if (reset) {
    currentPage.value = 1
    hasMore.value = true
    knownIds.clear()
  }
  try {
    const res: any = await getMessages(shopId.value, userId.value, currentPage.value, pageSize)
    const data = res?.data || res
    const rawList: ChatMessage[] = data?.list || []

    if (reset) {
      const reversed = rawList.slice().reverse()
      for (const m of reversed) knownIds.add(m.messageId)
      messages.value = reversed
      if (rawList.length < pageSize) hasMore.value = false
    } else if (appendBottom) {
      const newOnes = mergeNewMessages(rawList)
      if (newOnes.length > 0) {
        const newOnesSorted = newOnes.sort((a, b) => a.sendTime.localeCompare(b.sendTime))
        messages.value = [...messages.value, ...newOnesSorted]
      }
    } else {
      const merged = mergeNewMessages(rawList.slice().reverse())
      if (merged.length > 0) {
        messages.value = [...merged, ...messages.value]
      }
      if (rawList.length < pageSize) hasMore.value = false
    }

    if (reset) {
      loadingMessages.value = false
      scrollToBottom()
    } else if (appendBottom && !isUserReadingHistory.value) {
      scrollToBottomAnimated()
    }
  } catch (e) {
    console.error('加载消息失败', e)
    if (reset) loadingMessages.value = false
  }
}

async function loadMore() {
  if (loadingMore.value || !hasMore.value) return
  loadingMore.value = true
  currentPage.value++
  await loadMessages(false, false)
  loadingMore.value = false
}

async function pollNewMessages() {
  try {
    const res: any = await getMessages(shopId.value, userId.value, 1, pageSize)
    const data = res?.data || res
    const rawList: ChatMessage[] = data?.list || []
    const newOnes = mergeNewMessages(rawList)
    if (newOnes.length > 0) {
      const sortedByTime = newOnes.sort((a, b) => a.sendTime.localeCompare(b.sendTime))
      messages.value = [...messages.value, ...sortedByTime]
      await markRead()
      if (!isUserReadingHistory.value) {
        scrollToBottomAnimated()
      }
    }
  } catch (e) {
    // ignore polling errors
  }
}

function startPolling() {
  stopPolling()
  pollTimer = setInterval(pollNewMessages, POLL_INTERVAL)
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

async function onRefresh() {
  await loadMessages(true)
  refreshing.value = false
}

async function handleSend() {
  const text = inputMessage.value.trim()
  if (!text || sending.value) return

  sending.value = true
  try {
    const optimisticMsg: ChatMessage = {
      messageId: Date.now(),
      senderId: userId.value,
      senderType: 'USER',
      content: text,
      sendTime: new Date().toISOString(),
      messageType: 'TEXT'
    }
    knownIds.add(optimisticMsg.messageId)
    messages.value.push(optimisticMsg)
    inputMessage.value = ''
    scrollToBottomAnimated()

    const res: any = await sendMessage({
      userId: userId.value,
      shopId: shopId.value,
      content: text,
      messageType: 'TEXT'
    })
    const data = res?.data || res

    const optimisticIdx = messages.value.findIndex(m => m.messageId === optimisticMsg.messageId)
    if (optimisticIdx !== -1 && data?.messageId) {
      knownIds.delete(optimisticMsg.messageId)
      knownIds.add(data.messageId)
      messages.value[optimisticIdx] = {
        ...messages.value[optimisticIdx],
        messageId: data.messageId,
        sendTime: data.sendTime || messages.value[optimisticIdx].sendTime
      }
    }

    if (data?.autoReply) {
      const replyMsg: ChatMessage = {
        messageId: data.autoReply.messageId || Date.now() + 1,
        senderId: shopId.value,
        senderType: 'SHOP',
        content: data.autoReply.content,
        sendTime: data.autoReply.sendTime || new Date().toISOString(),
        messageType: 'TEXT'
      }
      if (!knownIds.has(replyMsg.messageId)) {
        knownIds.add(replyMsg.messageId)
        messages.value.push(replyMsg)
        scrollToBottomAnimated()
      }
    }
  } catch (e) {
    showToast('发送失败，请重试')
    messages.value = messages.value.filter(m => m.messageId !== Date.now())
    knownIds.delete(Date.now())
    console.error('发送消息失败', e)
  } finally {
    sending.value = false
  }
}

async function loadShopInfo() {
  try {
    const res: any = await getShopInfo(shopId.value)
    const data = res?.data || res
    if (data && data.shopName) {
      shopName.value = data.shopName
      shopLogo.value = data.shopLogo || ''
    }
  } catch (e) {
    console.error('加载店铺信息失败', e)
  } finally {
    shopInfoLoaded.value = true
  }
}

async function markRead() {
  try {
    await markAsRead(userId.value, shopId.value)
  } catch (e) {
    console.error('标记已读失败', e)
  }
}

function goBack() {
  stopPolling()
  router.back()
}

function onChatScroll() {
  if (!chatBodyRef.value) return
  const el = chatBodyRef.value
  const nearBottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 80
  isUserReadingHistory.value = !nearBottom
}

onMounted(() => {
  loadShopInfo()
  loadMessages(true).then(() => {
    markRead()
    startPolling()
  })
  if (chatBodyRef.value) {
    chatBodyRef.value.addEventListener('scroll', onChatScroll)
  }
})

onUnmounted(() => {
  stopPolling()
  if (chatBodyRef.value) {
    chatBodyRef.value.removeEventListener('scroll', onChatScroll)
  }
})
</script>

<style scoped>
.shop-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  -webkit-overflow-scrolling: touch;
}

.load-more {
  text-align: center;
  padding: 12px;
  color: #999;
  font-size: 12px;
}

.chat-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.chat-item.is-self {
  flex-direction: row-reverse;
}

.avatar {
  flex-shrink: 0;
}

.avatar-placeholder {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #1989fa;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.chat-content {
  max-width: 70%;
  margin: 0 8px;
}

.bubble {
  background: #fff;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
}

.bubble.self {
  background: #1989fa;
  color: #fff;
}

.chat-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.chat-item.is-self .chat-time {
  text-align: right;
}

.chat-input-area {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: #fff;
  border-top: 1px solid #eee;
  gap: 8px;
}

.chat-input-area .van-field {
  flex: 1;
  background: #f5f5f5;
  border-radius: 20px;
  padding: 6px 12px;
}

.avatar-skeleton {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(90deg, #eee 25%, #f5f5f5 37%, #eee 63%);
  background-size: 400% 100%;
  animation: skeleton-pulse 1.4s ease infinite;
  flex-shrink: 0;
}

.bubble-skeleton {
  height: 32px;
  border-radius: 12px;
  background: linear-gradient(90deg, #eee 25%, #f5f5f5 37%, #eee 63%);
  background-size: 400% 100%;
  animation: skeleton-pulse 1.4s ease infinite;
}

.bubble-skeleton-l {
  width: 120px;
}

.bubble-skeleton-r {
  width: 90px;
  margin-left: auto;
}

.bubble-skeleton-long {
  width: 180px;
}

@keyframes skeleton-pulse {
  0% { background-position: 100% 50%; }
  100% { background-position: 0 50%; }
}

.new-message-hint {
  position: fixed;
  bottom: 130px;
  left: 50%;
  transform: translateX(-50%);
  background: #1989fa;
  color: #fff;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  box-shadow: 0 2px 8px rgba(25, 137, 250, 0.4);
  z-index: 100;
  cursor: pointer;
}
</style>
