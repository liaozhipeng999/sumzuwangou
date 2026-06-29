<template>
  <div class="customer-service">
    <!-- 顶部导航栏 -->
    <van-nav-bar 
      :title="brandName" 
      left-arrow 
      @click-left="goBack"
    >
      <template #right>
        <van-icon name="more-o" size="20" />
      </template>
    </van-nav-bar>

    <!-- 品牌介绍组件 -->
    <BrandHeader 
      :brand-name="brandName"
      :brand-logo="brandLogo"
      :online-status="onlineStatus"
      :brand-tags="brandTags"
    />

    <!-- 打算购买的物品组件 -->
    <div class="product-section">
      <div class="section-title">
        <van-icon name="shopping-cart-o" color="#ee0a24" />
        <span>打算购买的物品</span>
      </div>
      <ProductPreview 
        v-if="currentProduct"
        :product-image="currentProduct.image"
        :product-name="currentProduct.name"
        :price="currentProduct.price"
        @click="goProductDetail"
      />
      <div v-else class="no-product">
        <van-icon name="gift-o" size="40" color="#ccc" />
        <span>暂无商品信息</span>
      </div>
    </div>

    <!-- 对话区域 -->
    <div class="chat-area" ref="chatAreaRef">
      <div 
        v-for="(message, index) in messages" 
        :key="index"
        :class="['message-item', { 'is-self': message.isSelf }]"
      >
        <!-- 头像 -->
        <van-image 
          :src="message.isSelf ? userAvatar : brandLogo" 
          class="avatar" 
          round 
        />
        
        <!-- 消息内容 -->
        <div :class="['message-content', { 'self': message.isSelf }]">
          <div :class="['message-bubble', { 'self': message.isSelf }]">
            {{ message.content }}
          </div>
          <div class="message-time">{{ message.time }}</div>
        </div>
      </div>

      <!-- 加载中提示 -->
      <div v-if="isLoading" class="loading-indicator">
        <van-loading type="spinner" size="20" />
        <span>正在输入...</span>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-bar">
        <!-- 语音识别按钮 -->
        <div class="voice-btn" @click="toggleVoice">
          <svg v-if="isRecording" viewBox="0 0 24 24" width="24" height="24" fill="#ee0a24">
            <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3zm0 10a1 1 0 0 1 1 1v3a1 1 0 0 1-2 0v-3a1 1 0 0 1 1-1zm6-8a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h13a1 1 0 0 0 1-1V4z"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" width="24" height="24" fill="#666">
            <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3zm0 10a1 1 0 0 1 1 1v3a1 1 0 0 1-2 0v-3a1 1 0 0 1 1-1zm6-8a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h13a1 1 0 0 0 1-1V4zM9 5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1H10a1 1 0 0 1-1-1V5z"/>
          </svg>
        </div>

        <!-- 输入框 -->
        <van-field 
          v-model="inputMessage" 
          placeholder="输入消息..."
          :adjust-position="false"
          @keyup.enter="sendMessage"
        />

        <!-- 表情按钮 -->
        <div class="emoji-btn" @click="toggleEmoji">
          <van-icon name="smile-o" color="#666" size="24" />
        </div>

        <!-- 加号按钮 -->
        <div class="plus-btn" @click="togglePlusMenu">
          <van-icon name="plus" color="#666" size="24" />
        </div>
      </div>

      <!-- 语音录制提示 -->
      <div v-if="isRecording" class="voice-recording">
        <van-icon name="mic" color="#ee0a24" size="28" />
        <span>正在录音，点击结束</span>
        <div class="voice-wave">
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>

      <!-- 加号菜单 -->
      <van-action-sheet 
        v-model:show="showPlusMenu"
        :actions="plusActions"
        cancel-text="取消"
        @select="handlePlusAction"
      />

      <!-- 表情选择器 -->
      <van-action-sheet 
        v-model:show="showEmoji"
        :actions="emojiList"
        cancel-text="取消"
        @select="selectEmoji"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import BrandHeader from './BrandHeader.vue'
import ProductPreview from './ProductPreview.vue'
import { 
  merchantChat, 
  type MerchantChatRequest, 
  type MerchantChatResponse
} from '@/api/merchantChat'
import { getProductDetail, type ProductDetailResponse } from '@/api/recommend'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 页面引用
const chatAreaRef = ref<HTMLElement | null>(null)

// 品牌信息
const brandName = ref('凤凰PHOENIX琚壹专卖店')
const brandLogo = ref('https://neeko-copilot.bytedance.net/api/text_to_image?prompt=phoenix%20logo%20simple%20elegant%20brand&image_size=square')
const onlineStatus = ref('在线')
const brandTags = ref(['品牌官方正品', '全店总售900万+件', '正品险'])

// 当前商品信息（从路由参数获取）
const currentProduct = ref<{
  id: number
  name: string
  image: string
  price: string | number
} | null>(null)

// 用户头像
const userAvatar = ref('https://neeko-copilot.bytedance.net/api/text_to_image?prompt=user%20avatar%20cartoon%20friendly&image_size=square')

// 商家ID（从路由参数获取）
const merchantId = ref(0)

// 会话ID（用于多轮对话）
const conversationId = ref<string | null>(null)

// 用户ID（从userStore获取，默认使用测试用户）
const userId = computed(() => {
  if (userStore.userInfo?.id) {
    return String(userStore.userInfo.id)
  }
  return sessionStorage.getItem('user_id') || 'test_user_001'
})

// 消息列表
const messages = ref<Array<{
  id: number
  content: string
  isSelf: boolean
  time: string
}>>([])

// 输入消息
const inputMessage = ref('')

// 状态
const isLoading = ref(false)
const isRecording = ref(false)
const showPlusMenu = ref(false)
const showEmoji = ref(false)

// 加号菜单选项
const plusActions = ref([
  { name: '发送商品', icon: 'shopping-cart-o' },
  { name: '发送图片', icon: 'photo-o' },
  { name: '发送视频', icon: 'video-o' },
  { name: '发送位置', icon: 'map-marker-o' }
])

// 表情列表
const emojiList = ref([
  { name: '😊' },
  { name: '😂' },
  { name: '🤔' },
  { name: '👍' },
  { name: '❤️' },
  { name: '🎉' },
  { name: '🙏' },
  { name: '😢' }
])

// 获取当前时间
const getCurrentTime = () => {
  const now = new Date()
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (chatAreaRef.value) {
    chatAreaRef.value.scrollTop = chatAreaRef.value.scrollHeight
  }
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message) return

  // 添加自己的消息
  messages.value.push({
    id: Date.now(),
    content: message,
    isSelf: true,
    time: getCurrentTime()
  })
  inputMessage.value = ''

  // 滚动到底部
  scrollToBottom()

  // 模拟加载状态
  isLoading.value = true

  try {
    // 调用商家客服API发送消息
    const requestData: MerchantChatRequest = {
      merchant_id: merchantId.value,
      user_id: userId.value,
      message: message
    }
    
    // 如果有会话ID，携带会话ID继续对话
    if (conversationId.value) {
      requestData.conversation_id = conversationId.value
    }
    
    const response: MerchantChatResponse = await merchantChat(requestData)
    
    // 更新会话ID（首次对话会创建新会话）
    conversationId.value = response.conversation_id
    
    // 添加客服回复
    messages.value.push({
      id: Date.now() + 1,
      content: response.response,
      isSelf: false,
      time: getCurrentTime()
    })
    
    console.log('回复来源:', response.source)
  } catch (error: any) {
    console.error('发送消息失败:', error)
    
    // 根据错误类型显示不同提示
    let errorMsg = '抱歉，网络有点问题，请稍后再试。'
    
    if (error.response) {
      // 后端返回错误
      const status = error.response.status
      if (status === 404) {
        errorMsg = '抱歉，该商家不存在或未开通客服服务。'
      } else if (status === 500) {
        errorMsg = '服务器内部错误，请稍后再试。'
      } else {
        errorMsg = error.response.data?.detail || error.response.data?.message || errorMsg
      }
    } else if (error.code === 'ECONNREFUSED') {
      errorMsg = '抱歉，客服服务暂不可用，请稍后再试。'
    }
    
    // 添加错误提示消息
    messages.value.push({
      id: Date.now() + 1,
      content: errorMsg,
      isSelf: false,
      time: getCurrentTime()
    })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

// 显示欢迎消息
const showWelcomeMessage = () => {
  messages.value = [{
    id: 1,
    content: '您好！请问有什么可以帮您的？',
    isSelf: false,
    time: getCurrentTime()
  }]
}

// 切换语音录制
const toggleVoice = () => {
  isRecording.value = !isRecording.value
  if (!isRecording.value) {
    showToast('语音录制已结束')
  }
}

// 切换表情选择器
const toggleEmoji = () => {
  showEmoji.value = !showEmoji.value
  showPlusMenu.value = false
}

// 切换加号菜单
const togglePlusMenu = () => {
  showPlusMenu.value = !showPlusMenu.value
  showEmoji.value = false
}

// 处理加号菜单操作
const handlePlusAction = (action: { name: string }) => {
  showToast(action.name)
  showPlusMenu.value = false
}

// 选择表情
const selectEmoji = (action: { name: string }) => {
  inputMessage.value += action.name
  showEmoji.value = false
}

// 返回
const goBack = () => {
  router.back()
}

// 跳转到商品详情
const goProductDetail = () => {
  if (currentProduct.value) {
    router.push(`/product/${currentProduct.value.id}`)
  }
}

// 获取商品数据
const fetchProductData = async () => {
  const productId = route.params.productId
  if (productId) {
    try {
      const response = await getProductDetail(Number(productId))
      if (response.code === 200 && response.data) {
        const { product, merchant, brandInfo } = response.data
        
        // 更新商品信息
        currentProduct.value = {
          id: product.id,
          name: product.productName,
          image: product.imageUrl || product.mainImage || '',
          price: product.discountedPrice || product.price
        }
        
        // 更新商家/品牌信息
        if (brandInfo) {
          brandName.value = brandInfo.brandName || merchant?.merchantName || '客服'
          brandLogo.value = brandInfo.brandLogo || ''
          // 使用店铺标签或保障标签
          const tags = brandInfo.shopTags && brandInfo.shopTags.length > 0 
            ? brandInfo.shopTags 
            : (brandInfo.guaranteeTags || [])
          if (tags.length > 0) {
            brandTags.value = tags.slice(0, 3)
          }
        } else if (merchant) {
          brandName.value = merchant.merchantName || '客服'
          brandLogo.value = merchant.logo || ''
        }
        
        // 更新在线状态（统一显示在线）
        onlineStatus.value = '在线'
      }
    } catch (error) {
      console.error('获取商品信息失败:', error)
      // 使用默认数据
      currentProduct.value = {
        id: Number(productId),
        name: '商品信息加载中...',
        image: '',
        price: ''
      }
    }
  }
}

// 初始化页面
const initPage = () => {
  // 从路由参数获取商家ID
  const merchantIdParam = route.params.merchantId
  if (merchantIdParam) {
    merchantId.value = Number(merchantIdParam)
  }
  
  // 显示欢迎消息
  showWelcomeMessage()
  
  // 获取商品数据
  fetchProductData()
}

onMounted(() => {
  initPage()
})
</script>

<style lang="scss" scoped>
.customer-service {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

/* 商品区域 */
.product-section {
  background: #fff;
  margin-top: 10px;

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 15px;
    font-size: 14px;
    color: #333;
    font-weight: 600;
    border-bottom: 1px solid #f5f5f5;
  }

  .no-product {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30px;
    color: #999;
    font-size: 14px;

    .van-icon {
      margin-bottom: 10px;
    }
  }
}

/* 对话区域 */
.chat-area {
  flex: 1;
  padding: 15px;
  overflow-y: auto;

  .message-item {
    display: flex;
    margin-bottom: 20px;
    animation: fadeIn 0.3s ease;

    &.is-self {
      justify-content: flex-end;

      .avatar {
        order: 2;
        margin-left: 10px;
        margin-right: 0;
      }

      .message-content {
        align-items: flex-end;
        order: 1;

        .message-bubble {
          background: linear-gradient(135deg, #ee0a24 0%, #ff4757 100%);
          color: #fff;
          border-radius: 20px 6px 20px 20px;
          box-shadow: 0 2px 8px rgba(238, 10, 36, 0.2);
        }

        .message-time {
          text-align: right;
        }
      }
    }

    &:not(.is-self) {
      justify-content: flex-start;

      .avatar {
        order: 1;
        margin-right: 10px;
        margin-left: 0;
      }

      .message-content {
        align-items: flex-start;
        order: 2;

        .message-bubble {
          background: #fff;
          border-radius: 6px 20px 20px 20px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        }

        .message-time {
          text-align: left;
        }
      }
    }

    .avatar {
      width: 44px;
      height: 44px;
      flex-shrink: 0;
      border-radius: 50%;
      overflow: hidden;
      border: 2px solid #f0f0f0;
      background: #f8f8f8;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .message-content {
      display: flex;
      flex-direction: column;
      max-width: 70%;

      .message-bubble {
        padding: 14px 18px;
        font-size: 15px;
        line-height: 1.6;
        position: relative;

        &::before {
          content: '';
          position: absolute;
          width: 0;
          height: 0;
          top: 10px;
          border-style: solid;
        }

        &.self::before {
          right: -8px;
          border-width: 8px 0 8px 8px;
          border-color: transparent transparent transparent #ee0a24;
        }

        &:not(.self)::before {
          left: -8px;
          border-width: 8px 8px 8px 0;
          border-color: transparent #fff transparent transparent;
        }
      }

      .message-time {
        font-size: 12px;
        color: #999;
        margin-top: 8px;
        padding: 0 6px;
      }
    }
  }

  .loading-indicator {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 20px;
    color: #999;
    font-size: 14px;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 输入区域 */
.input-area {
  background: #fff;
  padding: 12px 15px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  border-top: 1px solid #f0f0f0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);

  .input-bar {
    display: flex;
    align-items: center;
    gap: 12px;

    .voice-btn,
    .emoji-btn,
    .plus-btn {
      width: 44px;
      height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
      transition: all 0.3s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      &:active {
        transform: scale(0.95);
        background: linear-gradient(135deg, #e8eaed 0%, #d4d8dc 100%);
      }

      .van-icon {
        transition: all 0.3s ease;
      }
    }

    .voice-btn {
      &:hover, &:active {
        background: linear-gradient(135deg, #fff0f1 0%, #ffd6da 100%);
      }

      .van-icon {
        font-size: 26px;
      }
    }

    :deep(.van-field) {
      flex: 1;
      background: linear-gradient(135deg, #f5f7fa 0%, #e8eaed 100%);
      border-radius: 22px;
      height: 44px;
      line-height: 44px;
      padding: 0 20px;
      box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.04);
      border: 1px solid transparent;
      transition: all 0.3s ease;

      &:focus {
        background: #fff;
        border-color: #ee0a24;
        box-shadow: 0 0 0 3px rgba(238, 10, 36, 0.1);
      }

      .van-field__control {
        font-size: 15px;
        color: #333;
        placeholder {
          color: #999;
        }
      }
    }
  }

  .voice-recording {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 18px;
    background: linear-gradient(135deg, #fff5f5 0%, #ffeaea 100%);
    margin-top: 12px;
    border-radius: 12px;
    color: #ee0a24;
    font-size: 15px;
    box-shadow: 0 2px 8px rgba(238, 10, 36, 0.1);

    .van-icon {
      animation: pulse 1s ease-in-out infinite;
    }

    .voice-wave {
      display: flex;
      align-items: center;
      gap: 5px;
      height: 24px;

      span {
        width: 5px;
        background: #ee0a24;
        border-radius: 3px;
        animation: wave 0.5s ease-in-out infinite alternate;

        &:nth-child(1) { animation-delay: 0s; height: 10px; }
        &:nth-child(2) { animation-delay: 0.1s; height: 16px; }
        &:nth-child(3) { animation-delay: 0.2s; height: 22px; }
        &:nth-child(4) { animation-delay: 0.3s; height: 16px; }
        &:nth-child(5) { animation-delay: 0.4s; height: 10px; }
      }
    }
  }
}

@keyframes wave {
  from { transform: scaleY(0.5); }
  to { transform: scaleY(1); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
}
</style>
