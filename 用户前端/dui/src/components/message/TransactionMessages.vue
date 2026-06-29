<template>
  <div class="message-list-page">
    <!-- 顶部导航 -->
    <div class="list-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">交易消息</span>
      <div class="header-right">
        <van-icon name="search" class="header-icon" @click="showSearch = !showSearch" />
        <span class="header-action" @click="handleReadAll" v-if="unreadTotal > 0">全部已读</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <van-search
      v-model="searchKeyword"
      v-show="showSearch"
      placeholder="搜索交易消息..."
      shape="round"
      background="#f7f8fa"
      @clear="searchKeyword = ''"
    />

    <!-- 消息列表 -->
    <div class="message-list">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
        <van-list
          v-model:loading="listLoading"
          :finished="finished"
          :immediate-check="false"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <van-swipe-cell
            v-for="msg in filteredMessages"
            :key="msg.id"
          >
            <div
              :class="['message-card', { unread: !msg.isRead }]"
              @click="handleClick(msg)"
            >
              <div class="msg-icon-wrap transaction">
                <van-icon name="bill-o" size="20" color="#fff" />
              </div>
              <div class="msg-body">
                <div class="msg-title-row">
                  <span class="msg-title">{{ msg.title }}</span>
                  <span class="msg-dot" v-if="!msg.isRead"></span>
                </div>
                <span class="msg-content">{{ msg.content }}</span>
                <div class="msg-footer">
                  <span class="msg-time">{{ formatTime(msg.createdAt) }}</span>
                  <span class="msg-link" v-if="msg.orderId" @click.stop="goOrder(msg.orderId)">查看订单</span>
                </div>
              </div>
            </div>
            <template #right>
              <van-button square type="danger" text="删除" class="delete-btn" @click="handleDelete(msg)" />
            </template>
          </van-swipe-cell>

          <van-empty
            v-if="!listLoading && filteredMessages.length === 0"
            image="search"
            :description="searchKeyword ? '没有找到匹配的消息' : '暂无交易消息'"
          />
        </van-list>
      </van-pull-refresh>
    </div>

    <!-- 消息详情弹窗 -->
    <van-popup
      v-model:show="showDetail"
      position="bottom"
      round
      :style="{ maxHeight: '75vh' }"
    >
      <div class="detail-popup" v-if="detailMessage">
        <div class="detail-header">
          <span class="detail-title">{{ detailMessage.title }}</span>
          <van-icon name="cross" size="20" @click="showDetail = false" />
        </div>
        <div class="detail-meta">
          <van-icon name="bill-o" size="16" color="#07c160" />
          <span class="detail-type">交易消息</span>
          <span class="detail-time">{{ formatTime(detailMessage.createdAt) }}</span>
        </div>
        <div class="detail-content">{{ detailMessage.content }}</div>
        <div class="detail-image" v-if="detailMessage.image">
          <van-image :src="detailMessage.image" fit="contain" width="100%" radius="8" />
        </div>
        <div class="detail-actions">
          <van-button
            v-if="detailMessage.orderId"
            type="primary"
            size="small"
            round
            @click="goOrder(detailMessage.orderId)"
          >
            查看订单
          </van-button>
          <van-button
            size="small"
            round
            plain
            type="danger"
            @click="handleDeleteFromDetail"
          >
            删除
          </van-button>
          <van-button size="small" round plain @click="showDetail = false">关闭</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { useMessageStore } from '@/stores/message'
import type { MessageType, Message } from '@/api/message'

const router = useRouter()
const messageStore = useMessageStore()
const msgType: MessageType = 'transaction'

const refreshing = ref(false)
const listLoading = ref(true)
const showSearch = ref(false)
const searchKeyword = ref('')
const showDetail = ref(false)
const detailMessage = ref<Message | null>(null)

const finished = computed(() => !messageStore.pagination[msgType]?.hasMore)
const messages = computed(() => messageStore.messageLists[msgType] || [])

const filteredMessages = computed(() => {
  if (!searchKeyword.value.trim()) return messages.value
  const kw = searchKeyword.value.trim().toLowerCase()
  return messages.value.filter(msg =>
    msg.title.toLowerCase().includes(kw) || msg.content.toLowerCase().includes(kw)
  )
})

const unreadTotal = computed(() => {
  const item = messageStore.summary.find(s => s.type === msgType)
  return item?.unreadCount || 0
})

async function onLoad() {
  await messageStore.fetchMessages(msgType)
  listLoading.value = false
}

async function onRefresh() {
  await messageStore.fetchMessages(msgType, true)
  refreshing.value = false
}

function handleClick(msg: Message) {
  if (!msg.isRead) messageStore.markRead(msg.id, msg.type)
  detailMessage.value = msg
  showDetail.value = true
}

async function handleReadAll() {
  try {
    await showDialog({ title: '提示', message: '确定将所有交易消息标记为已读吗？', showCancelButton: true })
  } catch { return }
  await messageStore.markTypeAllRead(msgType)
  showToast('已全部标记为已读')
}

async function handleDelete(msg: Message) {
  try {
    await showDialog({ title: '删除消息', message: '确定删除该消息吗？', showCancelButton: true })
  } catch { return }
  await messageStore.deleteMessage(msg.id, msgType)
  showToast('已删除')
}

function goOrder(orderId: string) {
  showDetail.value = false
  router.push(`/order?orderId=${orderId}`)
}

async function handleDeleteFromDetail() {
  if (!detailMessage.value) return
  await handleDelete(detailMessage.value)
  showDetail.value = false
}

function goBack() { router.back() }

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

onMounted(async () => {
  await messageStore.fetchMessages(msgType, true)
  listLoading.value = false
})
</script>

<style scoped>
.message-list-page { background: #f7f8fa; min-height: 100vh; }
.list-header { display: flex; align-items: center; padding: 12px 16px; background: #fff; position: sticky; top: 0; z-index: 10; gap: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.back-btn { font-size: 20px; color: #323233; cursor: pointer; }
.header-title { flex: 1; font-size: 17px; font-weight: 600; color: #323233; }
.header-right { display: flex; align-items: center; gap: 12px; }
.header-icon { font-size: 18px; color: #646566; cursor: pointer; }
.header-action { font-size: 13px; color: #1989fa; cursor: pointer; }
.message-list { padding: 8px 12px; }
.message-card { display: flex; gap: 12px; background: #fff; border-radius: 10px; padding: 14px; margin-bottom: 8px; cursor: pointer; transition: background 0.2s; }
.message-card:active { background: #f7f8fa; }
.message-card.unread { background: #f0f7ff; }
.msg-icon-wrap { width: 40px; height: 40px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.msg-icon-wrap.transaction { background: linear-gradient(135deg, #07c160, #2ecc71); }
.msg-body { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.msg-title-row { display: flex; align-items: center; gap: 6px; }
.msg-title { font-size: 14px; font-weight: 500; color: #323233; }
.msg-dot { width: 6px; height: 6px; border-radius: 50%; background: #ee0a24; flex-shrink: 0; }
.msg-content { font-size: 12px; color: #969799; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.msg-footer { display: flex; align-items: center; justify-content: space-between; margin-top: 4px; }
.msg-time { font-size: 11px; color: #c8c9cc; }
.msg-link { font-size: 12px; color: #07c160; cursor: pointer; }
.msg-link:active { opacity: 0.7; }
.delete-btn { height: 100%; }
.detail-popup { padding: 20px 16px; }
.detail-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.detail-title { font-size: 17px; font-weight: 600; color: #323233; flex: 1; }
.detail-meta { display: flex; align-items: center; gap: 6px; margin-bottom: 16px; }
.detail-type { font-size: 12px; color: #646566; }
.detail-time { font-size: 12px; color: #c8c9cc; margin-left: auto; }
.detail-content { font-size: 14px; line-height: 1.8; color: #323233; margin-bottom: 16px; white-space: pre-wrap; }
.detail-image { margin-bottom: 16px; }
.detail-actions { display: flex; gap: 10px; justify-content: center; padding-top: 12px; border-top: 1px solid #f5f6f7; }
</style>
