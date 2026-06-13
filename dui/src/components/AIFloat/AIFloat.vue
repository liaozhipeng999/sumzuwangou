<template>
  <div class="ai-float">
    <!-- 悬浮按钮 -->
    <div class="float-btn" @click="toggleChat" v-if="!isOpen">
      <van-icon name="chat-o" size="24" />
      <span class="btn-text">AI助手</span>
    </div>

    <!-- 聊天窗口 -->
    <transition name="slide-up">
      <div class="chat-window" v-if="isOpen">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-left">
            <van-icon name="service-o" size="20" />
            <span class="title">AI 智能助手</span>
          </div>
          <van-icon name="cross" size="20" class="close-btn" @click="toggleChat" />
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messagesRef">
          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            :class="['message', msg.role]"
          >
            <div class="avatar">
              <van-icon v-if="msg.role === 'user'" name="user-o" size="16" />
              <van-icon v-else name="service-o" size="16" />
            </div>
            <div class="content">
              <div class="text">{{ msg.content }}</div>
            </div>
          </div>
          
          <!-- 加载中提示 -->
          <div class="message assistant" v-if="isLoading">
            <div class="avatar">
              <van-icon name="service-o" size="16" />
            </div>
            <div class="content">
              <div class="loading">
                <van-loading size="16" />
                <span>思考中...</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input">
          <van-field
            v-model="inputText"
            placeholder="请输入您的问题..."
            :autosize="{ maxHeight: 80 }"
            type="textarea"
            rows="1"
            class="input-field"
            @keyup.enter="sendMessage"
          />
          <van-button 
            type="primary" 
            size="small" 
            class="send-btn"
            :disabled="!inputText.trim() || isLoading"
            @click="sendMessage"
          >
            发送
          </van-button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { showToast } from 'vant'

interface Message {
  role: 'user' | 'assistant'
  content: string
}

const isOpen = ref(false)
const inputText = ref('')
const isLoading = ref(false)
const messagesRef = ref<HTMLElement | null>(null)

const messages = ref<Message[]>([
  {
    role: 'assistant',
    content: '您好！我是AI智能助手，有什么可以帮助您的吗？'
  }
])

// 切换聊天窗口
const toggleChat = () => {
  isOpen.value = !isOpen.value
}

// 发送消息
const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || isLoading.value) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: text
  })
  
  inputText.value = ''
  scrollToBottom()

  // 模拟AI回复
  isLoading.value = true
  
  try {
    // 这里可以接入真实的AI接口
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟AI回复
    const reply = getAIReply(text)
    
    messages.value.push({
      role: 'assistant',
      content: reply
    })
    
    scrollToBottom()
  } catch (error) {
    showToast('发送失败，请重试')
  } finally {
    isLoading.value = false
  }
}

// 模拟AI回复（可替换为真实API）
const getAIReply = (question: string): string => {
  const replies = [
    '这是一个很好的问题！让我为您解答...',
    '我理解您的需求，建议您可以这样做...',
    '感谢您的提问，根据我的分析...',
    '这个问题很有意思，我来为您详细说明...',
    '好的，我来帮您处理这个问题。'
  ]
  
  // 简单的关键词匹配
  if (question.includes('你好') || question.includes('hello')) {
    return '您好！很高兴为您服务，请问有什么可以帮助您的吗？'
  }
  if (question.includes('购物车')) {
    return '关于购物车的问题，您可以在底部导航栏点击购物车图标查看和管理您的商品。'
  }
  if (question.includes('订单')) {
    return '您可以在"我的"页面中查看所有订单状态，包括待付款、待发货、待收货等。'
  }
  if (question.includes('登录') || question.includes('注册')) {
    return '登录和注册功能已经准备好了，您可以使用用户名和密码进行登录，或者注册新账号。'
  }
  
  return replies[Math.floor(Math.random() * replies.length)]
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.ai-float {
  position: fixed;
  z-index: 9999;
}

/* 悬浮按钮 */
.float-btn {
  position: fixed;
  right: 20px;
  bottom: 80px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  cursor: pointer;
  transition: all 0.3s ease;
}

.float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.float-btn:active {
  transform: scale(0.95);
}

.btn-text {
  font-size: 10px;
  margin-top: 2px;
}

/* 聊天窗口 */
.chat-window {
  position: fixed;
  right: 20px;
  bottom: 80px;
  width: 320px;
  height: 480px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部 */
.chat-header {
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title {
  font-size: 16px;
  font-weight: 500;
}

.close-btn {
  cursor: pointer;
  padding: 4px;
}

/* 消息列表 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f7f8fa;
}

.message {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message.user .avatar {
  background: #1989fa;
  color: white;
}

.message.assistant .avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.content {
  max-width: 70%;
}

.text {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message.user .text {
  background: #1989fa;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .text {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.loading {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: white;
  border-radius: 12px;
  font-size: 14px;
  color: #999;
}

/* 输入区域 */
.chat-input {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 12px;
  background: white;
  border-top: 1px solid #eee;
  flex-shrink: 0;
}

.input-field {
  flex: 1;
  background: #f7f8fa;
  border-radius: 20px;
  padding: 0 12px;
}

.input-field :deep(.van-field__control) {
  background: transparent;
}

.send-btn {
  border-radius: 16px;
  padding: 0 16px;
  height: 32px;
}

/* 动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}
</style>
