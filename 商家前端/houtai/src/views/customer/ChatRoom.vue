<template>
  <div class="chat-room">
    <!-- 顶部标题栏 -->
    <div class="chat-header">
      <el-page-header @back="goBack">
        <template #content>
          <div class="header-title">
            <el-avatar :size="32" style="margin-right: 10px">
              <el-icon :size="18"><User /></el-icon>
            </el-avatar>
            <span>用户 #{{ userId }}</span>
          </div>
        </template>
      </el-page-header>
    </div>

    <!-- 消息区域 -->
    <div class="chat-messages" ref="messageContainer">
      <div v-if="loadingHistory" class="loading-history">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <div v-if="hasMore && !loadingHistory" class="load-more" @click="loadMore">
        <el-link type="primary">加载更多消息</el-link>
      </div>

      <div
        v-for="(msg, idx) in messages"
        :key="msg.messageId"
        class="message-row"
        :class="{ 'message-self': msg.senderType === 'SHOP', 'message-fade-in': renderedUpTo >= idx }"
      >
        <div class="message-avatar">
          <el-avatar :size="36">
            <el-icon :size="18">
              <User v-if="msg.senderType === 'USER'" />
              <Shop v-else />
            </el-icon>
          </el-avatar>
        </div>
        <div class="message-bubble">
          <!-- 文本消息 -->
          <div v-if="msg.messageType === 'TEXT'" class="message-text">{{ msg.content }}</div>
          <!-- 图片消息 -->
          <div v-else-if="msg.messageType === 'IMAGE'" class="message-image">
            <el-image :src="msg.content" :preview-src-list="[msg.content]" style="max-width: 200px; border-radius: 6px" />
          </div>
          <!-- 商品卡片 -->
          <div v-else-if="msg.messageType === 'PRODUCT'" class="message-product">
            <div class="product-card">
              <div class="product-card-content">
                <span class="product-name">{{ parseProduct(msg.content).productName }}</span>
                <span class="product-price">¥{{ parseProduct(msg.content).price }}</span>
              </div>
              <el-image
                v-if="parseProduct(msg.content).productImage"
                :src="parseProduct(msg.content).productImage"
                style="width: 60px; height: 60px; border-radius: 4px; flex-shrink: 0"
                fit="cover"
              />
            </div>
          </div>
          <div class="message-time">{{ formatTime(msg.sendTime) }}</div>
        </div>
      </div>
    </div>

    <!-- 快捷回复 -->
    <div v-if="showQuickReplies" class="quick-reply-panel">
      <div class="quick-reply-header">
        <span>快捷回复</span>
        <el-icon @click="showQuickReplies = false" style="cursor: pointer"><Close /></el-icon>
      </div>
      <div class="quick-reply-list">
        <div
          v-for="qr in quickReplies"
          :key="qr.id"
          class="quick-reply-item"
          @click="useQuickReply(qr.content)"
        >
          {{ qr.content }}
        </div>
        <div v-if="quickReplies.length === 0" class="empty-qr">暂无快捷回复</div>
      </div>
    </div>

    <!-- 商品选择弹窗 -->
    <el-dialog v-model="showProductPicker" title="选择商品" width="600px" top="8vh">
      <div v-if="productList.length === 0" style="text-align: center; padding: 40px">
        <el-empty description="暂无商品" />
      </div>
      <div v-else class="product-picker-list">
        <div
          v-for="p in productList"
          :key="p.id"
          class="product-picker-item"
          @click="sendProductCard(p)"
        >
          <el-image :src="p.mainImage" style="width: 50px; height: 50px; border-radius: 4px; flex-shrink: 0" fit="cover" />
          <div class="product-picker-info">
            <span class="product-picker-name">{{ p.productName }}</span>
            <span class="product-picker-price">¥{{ p.price }}</span>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 输入区域 -->
    <div class="chat-input">
      <div class="input-toolbar">
        <el-tooltip content="快捷回复">
          <el-button :icon="ChatDotRound" text @click="toggleQuickReplies" />
        </el-tooltip>
        <el-tooltip content="发送图片">
          <el-button :icon="Picture" text @click="triggerImageUpload" />
        </el-tooltip>
        <el-tooltip content="发送商品卡片">
          <el-button :icon="ShoppingCart" text @click="openProductPicker" />
        </el-tooltip>
        <input
          ref="imageInput"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleImageUpload"
        />
      </div>
      <div class="input-row">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="2"
          placeholder="输入消息..."
          resize="none"
          @keydown.enter.exact.prevent="sendTextMessage"
        />
        <el-button type="primary" :disabled="!inputText.trim()" @click="sendTextMessage">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Shop, Loading, Close, ChatDotRound, Picture, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getMessages,
  merchantSendMessage,
  merchantMarkAsRead,
  getMerchantQuickReplies,
  uploadImage,
  getProductList
} from '@/api/customer'
import type { MessageVO, QuickReply } from '@/api/customer'
import { useMerchantStore } from '@/stores/merchant'
import { useWebSocket } from '@/composables/useWebSocket'

const route = useRoute()
const router = useRouter()
const merchantStore = useMerchantStore()
const { connect, subscribeShopMessages } = useWebSocket()

const shopId = computed(() => merchantStore.merchantInfo?.id || 0)
const userId = computed(() => Number(route.params.userId))

const messages = ref<MessageVO[]>([])
const renderedUpTo = ref(-1)
const inputText = ref('')
const loadingHistory = ref(false)
const page = ref(1)
const pageSize = 20
const hasMore = ref(true)
const messageContainer = ref<HTMLElement | null>(null)
const showQuickReplies = ref(false)
const quickReplies = ref<QuickReply[]>([])
const showProductPicker = ref(false)
const productList = ref<any[]>([])
const imageInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const isRendering = ref(false)
let pollTimer: ReturnType<typeof setInterval> | null = null
let renderTimer: ReturnType<typeof setTimeout> | null = null

async function renderMessagesSequentially(list: MessageVO[], mode: 'replace' | 'prepend') {
  if (renderTimer) {
    clearTimeout(renderTimer)
    renderTimer = null
  }

  if (mode === 'replace') {
    messages.value = []
    renderedUpTo.value = -1
  }

  const batch = [...list]

  if (mode === 'prepend' && messages.value.length > 0) {
    messages.value = [...batch.reverse(), ...messages.value]
    renderedUpTo.value = messages.value.length - 1
    await nextTick()
    scrollToTopAnchored()
    return
  }

  isRendering.value = true

  for (let i = 0; i < batch.length; i++) {
    messages.value.push(batch[i])
    const targetIdx = messages.value.length - 1
    await nextTick()
    renderedUpTo.value = targetIdx
    if (i === 0) {
      await nextTick()
      scrollToBottom()
    }
    if (i < batch.length - 1) {
      await new Promise<void>((r) => { renderTimer = setTimeout(() => r(), 35) })
    }
  }

  isRendering.value = false
  renderTimer = null
}

function scrollToTopAnchored() {
  if (!messageContainer.value) return
  const c = messageContainer.value
  const anchor = c.scrollHeight
  requestAnimationFrame(() => {
    c.scrollTop = c.scrollHeight - anchor
  })
}

async function loadMessages(append = false) {
  if (!shopId.value || !userId.value) return
  loadingHistory.value = true
  try {
    const res = await getMessages(shopId.value, userId.value, page.value, pageSize)
    if (res.code === 200 && res.data) {
      const list: MessageVO[] = res.data.list || []
      hasMore.value = (messages.value.length + list.length) < res.data.total
      if (append) {
        await renderMessagesSequentially(list, 'prepend')
      } else {
        await renderMessagesSequentially(list, 'replace')
      }
    }
  } catch (e) {
    console.error('加载消息失败', e)
  } finally {
    loadingHistory.value = false
    if (!append) {
      await nextTick()
      scrollToBottom()
    }
  }
}

function loadMore() {
  page.value++
  loadMessages(true)
}

// 发送文本消息
async function sendTextMessage() {
  const text = inputText.value.trim()
  if (!text || !shopId.value || !userId.value) return

  try {
    const res = await merchantSendMessage({
      shopId: shopId.value,
      userId: userId.value,
      content: text,
      messageType: 'TEXT'
    })
    if (res.code === 200) {
      messages.value.push({
        messageId: res.data?.messageId || Date.now(),
        senderId: shopId.value,
        senderType: 'SHOP',
        content: text,
        sendTime: res.data?.sendTime || new Date().toISOString(),
        messageType: 'TEXT'
      })
      renderedUpTo.value = messages.value.length - 1
      inputText.value = ''
      await nextTick()
      scrollToBottom()
    }
  } catch (e) {
    console.error('发送失败', e)
  }
}

// 触发图片选择
function triggerImageUpload() {
  imageInput.value?.click()
}

// 处理图片上传
async function handleImageUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 10MB')
    return
  }

  uploading.value = true
  try {
    const uploadRes = await uploadImage(file)
    if (uploadRes.code === 200 && uploadRes.data) {
      const imageUrl = uploadRes.data.url
      const sendRes = await merchantSendMessage({
        shopId: shopId.value,
        userId: userId.value,
        content: imageUrl,
        messageType: 'IMAGE'
      })
      if (sendRes.code === 200) {
        messages.value.push({
          messageId: sendRes.data?.messageId || Date.now(),
          senderId: shopId.value,
          senderType: 'SHOP',
          content: imageUrl,
          sendTime: sendRes.data?.sendTime || new Date().toISOString(),
          messageType: 'IMAGE'
        })
        renderedUpTo.value = messages.value.length - 1
        await nextTick()
        scrollToBottom()
      }
    }
  } catch (e) {
    ElMessage.error('图片上传失败')
    console.error(e)
  } finally {
    uploading.value = false
    target.value = ''
  }
}

// 打开商品选择弹窗
async function openProductPicker() {
  showProductPicker.value = true
  if (productList.value.length === 0) {
    try {
      const res = await getProductList(shopId.value)
      if (res.code === 200) {
        productList.value = res.data || []
      }
    } catch (e) {
      console.error('加载商品列表失败', e)
    }
  }
}

// 发送商品卡片
async function sendProductCard(product: any) {
  const content = JSON.stringify({
    productId: product.id,
    productName: product.productName,
    productImage: product.mainImage,
    price: product.price
  })
  try {
    const res = await merchantSendMessage({
      shopId: shopId.value,
      userId: userId.value,
      content,
      messageType: 'PRODUCT',
      productId: product.id
    })
    if (res.code === 200) {
      messages.value.push({
        messageId: res.data?.messageId || Date.now(),
        senderId: shopId.value,
        senderType: 'SHOP',
        content,
        sendTime: res.data?.sendTime || new Date().toISOString(),
        messageType: 'PRODUCT'
      })
      renderedUpTo.value = messages.value.length - 1
      showProductPicker.value = false
      await nextTick()
      scrollToBottom()
    }
  } catch (e) {
    ElMessage.error('发送失败')
    console.error(e)
  }
}

function scrollToBottom() {
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

function goBack() {
  router.push('/merchant/customer/conversations')
}

function formatTime(time: string): string {
  if (!time) return ''
  const date = new Date(time)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function parseProduct(content: string) {
  try {
    return JSON.parse(content)
  } catch {
    return { productName: '商品信息', price: '0.00' }
  }
}

async function markRead() {
  if (!shopId.value || !userId.value) return
  try {
    await merchantMarkAsRead(shopId.value, userId.value)
  } catch (e) {
    console.error('标记已读失败', e)
  }
}

async function loadQuickReplies() {
  if (!shopId.value) return
  try {
    const res = await getMerchantQuickReplies(shopId.value)
    if (res.code === 200) {
      quickReplies.value = res.data || []
    }
  } catch (e) {
    console.error('加载快捷回复失败', e)
  }
}

function toggleQuickReplies() {
  showQuickReplies.value = !showQuickReplies.value
  if (showQuickReplies.value && quickReplies.value.length === 0) {
    loadQuickReplies()
  }
}

function useQuickReply(content: string) {
  inputText.value = content
  showQuickReplies.value = false
}

let unsubscribeWs: (() => void) | null = null

onMounted(() => {
  loadMessages()
  markRead()
  connect()
  if (shopId.value) {
    unsubscribeWs = subscribeShopMessages(shopId.value, (msg) => {
      if (msg.userId === userId.value && msg.type === 'new_message') {
        messages.value.push({
          messageId: msg.messageId,
          senderId: msg.senderType === 'SHOP' ? shopId.value : userId.value,
          senderType: msg.senderType,
          content: msg.content,
          sendTime: msg.sendTime,
          messageType: msg.messageType
        })
        renderedUpTo.value = messages.value.length - 1
        nextTick(() => {
          scrollToBottom()
          markRead()
        })
      }
    })
  }
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
  if (renderTimer) clearTimeout(renderTimer)
  if (unsubscribeWs) unsubscribeWs()
})
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.chat-header {
  padding: 12px 20px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.header-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  background: #f5f5f5;
}

.loading-history,
.load-more {
  text-align: center;
  padding: 10px;
  color: #909399;
  font-size: 13px;
}

.message-row {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  align-items: flex-start;
  opacity: 0;
  transform: translateY(8px);
  transition: opacity 260ms ease, transform 260ms ease;
}

.message-row.message-fade-in {
  opacity: 1;
  transform: translateY(0);
}

.message-row.message-self {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-bubble {
  max-width: 60%;
}

.message-text {
  background: #fff;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  color: #303133;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
  word-break: break-word;
}

.message-self .message-text {
  background: #409eff;
  color: #fff;
}

.message-image {
  border-radius: 8px;
  overflow: hidden;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
}

.message-self .message-time {
  text-align: right;
}

.product-card {
  background: #fff;
  padding: 10px 14px;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  display: flex;
  gap: 10px;
  align-items: center;
}

.product-card-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 600;
}

/* 商品选择弹窗 */
.product-picker-list {
  max-height: 400px;
  overflow-y: auto;
}

.product-picker-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 6px;
}

.product-picker-item:hover {
  background: #ecf5ff;
}

.product-picker-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-picker-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-picker-price {
  font-size: 13px;
  color: #f56c6c;
  font-weight: 600;
}

/* 快捷回复面板 */
.quick-reply-panel {
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
  max-height: 200px;
  overflow-y: auto;
  flex-shrink: 0;
}

.quick-reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  font-size: 13px;
  color: #606266;
  font-weight: 500;
  border-bottom: 1px solid #f0f0f0;
}

.quick-reply-list {
  padding: 8px;
}

.quick-reply-item {
  padding: 8px 12px;
  margin-bottom: 4px;
  border-radius: 6px;
  font-size: 13px;
  color: #303133;
  cursor: pointer;
  transition: background 0.2s;
}

.quick-reply-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.empty-qr {
  text-align: center;
  padding: 16px;
  color: #c0c4cc;
  font-size: 13px;
}

/* 输入区域 */
.chat-input {
  border-top: 1px solid #f0f0f0;
  padding: 10px 16px;
  flex-shrink: 0;
  background: #fff;
}

.input-toolbar {
  margin-bottom: 6px;
}

.input-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.input-row .el-input {
  flex: 1;
}

.input-row .el-button {
  height: 54px;
}
</style>
