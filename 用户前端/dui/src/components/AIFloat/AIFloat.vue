<template>
  <div class="ai-float" :class="{ 'is-expanded': isExpanded }">
    <div class="float-btn" @click="toggleExpand">
      <van-image 
        :src="avatarUrl" 
        class="avatar" 
        round 
      />
      <div v-if="hasNewMessage" class="badge">1</div>
    </div>

    <div v-if="isExpanded" class="chat-panel">
      <div class="chat-header">
        <div class="header-left">
          <van-image :src="avatarUrl" class="header-avatar" round />
          <div class="header-info">
            <span class="name">八千代</span>
            <span class="status">在线</span>
          </div>
        </div>
        <van-icon name="close" size="20" @click="closePanel" />
      </div>

      <div class="chat-messages" ref="messagesRef">
        <div 
          v-for="(msg, index) in messages" 
          :key="index"
          :class="['message', { 'self': msg.isSelf }]"
        >
          <template v-if="!msg.isSelf">
            <van-image 
              :src="avatarUrl" 
              class="msg-avatar" 
              round 
            />
            <div :class="['msg-bubble', { 'self': msg.isSelf }]">
              {{ msg.content }}
            </div>
          </template>
          <template v-else>
            <div :class="['msg-bubble', { 'self': msg.isSelf }]">
              {{ msg.content }}
            </div>
            <van-image 
              :src="userAvatar" 
              class="msg-avatar" 
              round 
            />
          </template>
        </div>
        <div v-if="isLoading" class="loading">
          <van-loading type="spinner" size="16" />
        </div>
      </div>

      <div class="chat-input">
        <!-- 语音按钮 -->
        <div class="voice-btn" @click="toggleVoice">
          <svg v-if="isRecording" viewBox="0 0 24 24" width="20" height="20" fill="#ee0a24">
            <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3zm0 10a1 1 0 0 1 1 1v3a1 1 0 0 1-2 0v-3a1 1 0 0 1 1-1zm6-8a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h13a1 1 0 0 0 1-1V4z"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" width="20" height="20" fill="#666">
            <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3zm0 10a1 1 0 0 1 1 1v3a1 1 0 0 1-2 0v-3a1 1 0 0 1 1-1zm6-8a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h13a1 1 0 0 0 1-1V4zM9 5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1H10a1 1 0 0 1-1-1V5z"/>
          </svg>
        </div>
        
        <van-field 
          v-model="inputMessage" 
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
        />
        <van-button type="primary" @click="sendMessage">发送</van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'

const avatarUrl = '/image/aihead/140407044_bobopic.jpg'
const userAvatar = 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=user%20avatar%20cartoon%20friendly&image_size=square'

const isExpanded = ref(false)
const hasNewMessage = ref(false)
const inputMessage = ref('')
const isLoading = ref(false)
const isRecording = ref(false)
const messagesRef = ref<HTMLElement | null>(null)
const conversationId = ref<string | null>(null)

const messages = ref([
  {
    content: '您好！我是八千代，很高兴为您服务！',
    isSelf: false
  }
])

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
  hasNewMessage.value = false
}

const closePanel = () => {
  isExpanded.value = false
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const toggleVoice = () => {
  isRecording.value = !isRecording.value
}

const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message) return

  messages.value.push({
    content: message,
    isSelf: true
  })
  inputMessage.value = ''
  scrollToBottom()

  isLoading.value = true

  try {
    const response = await fetch('http://localhost:8000/api/chat/stream', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        user_id: 'default_user',
        message: message,
        conversation_id: conversationId.value,
        speech: false
      })
    })

    if (!response.ok) {
      throw new Error('请求失败')
    }

    const reader = response.body?.getReader()
    if (!reader) {
      throw new Error('无法获取响应流')
    }

    const decoder = new TextDecoder()
    let fullResponse = ''
    let aiMessageId = messages.value.length

    messages.value.push({
      content: '',
      isSelf: false
    })

    while (true) {
      const { done, value } = await reader.read()
      
      if (done) break

      const text = decoder.decode(value)
      const lines = text.split('\n').filter(line => line.startsWith('data: '))

      for (const line of lines) {
        try {
          const data = JSON.parse(line.slice(6))
          
          if (data.type === 'token') {
            fullResponse += data.data
            messages.value[aiMessageId].content = fullResponse
            scrollToBottom()
          } else if (data.type === 'done') {
            conversationId.value = data.data.conversation_id
          } else if (data.type === 'error') {
            console.error('后端错误:', data.data)
          }
        } catch (e) {
          console.error('解析数据失败:', e)
        }
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    const lastIndex = messages.value.length - 1
    if (messages.value[lastIndex] && !messages.value[lastIndex].isSelf) {
      messages.value[lastIndex].content = '抱歉，网络有点问题，请稍后再试。'
    } else {
      messages.value.push({
        content: '抱歉，网络有点问题，请稍后再试。',
        isSelf: false
      })
    }
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}
</script>

<style lang="scss" scoped>
.ai-float {
  position: fixed;
  right: 20px;
  bottom: 70px;
  z-index: 999;

  .float-btn {
    position: relative;
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
    transition: all 0.3s ease;

    &:active {
      transform: scale(0.95);
    }

    .avatar {
      width: 48px;
      height: 48px;
      border: 2px solid #fff;
    }

    .badge {
      position: absolute;
      top: -4px;
      right: -4px;
      width: 20px;
      height: 20px;
      background: #ff4d4f;
      border-radius: 50%;
      color: #fff;
      font-size: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 2px solid #fff;
    }
  }

  .chat-panel {
    position: absolute;
    right: 0;
    bottom: 75px;
    width: 280px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    overflow: hidden;
    animation: slideUp 0.3s ease;

    .chat-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 15px;
      background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
      color: #fff;

      .header-left {
        display: flex;
        align-items: center;
        gap: 8px;

        .header-avatar {
          width: 36px;
          height: 36px;
          border: 2px solid rgba(255, 255, 255, 0.5);
        }

        .header-info {
          display: flex;
          flex-direction: column;

          .name {
            font-size: 14px;
            font-weight: 600;
          }

          .status {
            font-size: 11px;
            opacity: 0.8;
          }
        }
      }

      .van-icon {
        padding: 6px;
      }
    }

    .chat-messages {
      height: 240px;
      overflow-y: auto;
      padding: 12px;
      background: #f5f7fa;

      .message {
        display: flex;
        margin-bottom: 12px;

        &.self {
          justify-content: flex-end;

          .msg-bubble {
            background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
            color: #fff;
            border-radius: 10px 4px 10px 10px;
          }
        }

        .msg-avatar {
          width: 32px;
          height: 32px;
          flex-shrink: 0;
          margin: 0 6px;
        }

        .msg-bubble {
          max-width: 75%;
          padding: 8px 12px;
          background: #fff;
          border-radius: 4px 10px 10px 10px;
          font-size: 13px;
          line-height: 1.5;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
        }
      }

      .loading {
        display: flex;
        justify-content: center;
        padding: 8px;
      }
    }

    .chat-input {
      display: flex;
      gap: 8px;
      padding: 10px 12px;
      border-top: 1px solid #e8e8e8;

      .voice-btn {
        width: 36px;
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
        transition: all 0.3s ease;

        &:active {
          transform: scale(0.95);
        }
      }

      :deep(.van-field) {
        flex: 1;
        background: #f5f7fa;
        border-radius: 16px;
        height: 36px;
      }

      .van-button {
        border-radius: 16px;
        padding: 0 16px;
        background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
        border: none;
      }
    }
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>