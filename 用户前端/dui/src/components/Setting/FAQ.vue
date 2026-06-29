<template>
  <div class="faq-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">常见问题</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="faq-content">
      <div class="search-box">
        <van-icon name="search" size="16" />
        <input type="text" placeholder="搜索问题" class="search-input" v-model="searchText" />
      </div>

      <div class="category-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeCategory === item.key }"
          v-for="item in categories" 
          :key="item.key"
          @click="activeCategory = item.key"
        >
          {{ item.name }}
        </div>
      </div>

      <div class="faq-list">
        <van-collapse v-model="activeNames">
          <van-collapse-item 
            v-for="(item, index) in filteredFAQs" 
            :key="index" 
            :title="item.title"
            :name="index"
          >
            <div class="faq-content-text">{{ item.content }}</div>
          </van-collapse-item>
        </van-collapse>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchText = ref('')
const activeCategory = ref('all')
const activeNames = ref<string[]>([])

const categories = [
  { key: 'all', name: '全部' },
  { key: 'order', name: '订单问题' },
  { key: 'payment', name: '支付问题' },
  { key: 'logistics', name: '物流问题' },
  { key: 'account', name: '账号问题' }
]

const faqs = ref([
  { 
    title: '如何修改收货地址？', 
    content: '您可以在"我的"页面点击"收货地址"，进入地址管理页面进行修改。也可以在下单时直接编辑收货地址。',
    category: 'order'
  },
  { 
    title: '订单提交后可以修改吗？', 
    content: '订单提交后，在商家未发货前，您可以尝试修改订单信息。如果无法修改，请联系客服协助处理。',
    category: 'order'
  },
  { 
    title: '如何申请退款？', 
    content: '在"我的订单"页面找到对应订单，点击"申请退款/售后"，按照提示选择退款原因并提交申请。',
    category: 'order'
  },
  { 
    title: '支持哪些支付方式？', 
    content: '我们支持微信支付、支付宝、银行卡等多种支付方式。部分商品还支持分期付款和先用后付。',
    category: 'payment'
  },
  { 
    title: '支付失败怎么办？', 
    content: '请检查您的支付账户余额是否充足，网络是否正常。如果问题持续存在，请联系支付平台客服或我们的在线客服。',
    category: 'payment'
  },
  { 
    title: '如何查询物流信息？', 
    content: '在"我的订单"页面点击对应订单，即可查看物流跟踪信息。也可以在物流详情页查看完整的运输轨迹。',
    category: 'logistics'
  },
  { 
    title: '商品迟迟不到怎么办？', 
    content: '请先查看物流信息确认包裹状态。如果超过预计送达时间仍未收到，可以联系商家或我们的客服协助催件。',
    category: 'logistics'
  },
  { 
    title: '如何找回密码？', 
    content: '在登录页面点击"忘记密码"，通过绑定的手机号或邮箱找回密码。如果无法找回，请联系客服提供身份验证。',
    category: 'account'
  },
  { 
    title: '如何绑定手机号？', 
    content: '在"设置"页面点击"账号与安全"，选择"绑定手机号"，按照提示输入手机号和验证码完成绑定。',
    category: 'account'
  },
  { 
    title: '账号登录异常怎么办？', 
    content: '如果遇到登录异常，请检查网络连接，尝试清除缓存后重新登录。如问题持续，请联系客服核实账号安全。',
    category: 'account'
  }
])

const filteredFAQs = computed(() => {
  let result = faqs.value
  
  if (activeCategory.value !== 'all') {
    result = result.filter(item => item.category === activeCategory.value)
  }
  
  if (searchText.value) {
    const keyword = searchText.value.toLowerCase()
    result = result.filter(item => 
      item.title.toLowerCase().includes(keyword) || 
      item.content.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.faq-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.page-header {
  background: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.back-btn {
  font-size: 20px;
  color: #333;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.header-placeholder {
  width: 24px;
}

.faq-content {
  padding: 12px;
}

.search-box {
  background: white;
  border-radius: 20px;
  padding: 10px 16px;
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  margin-left: 8px;
}

.category-tabs {
  display: flex;
  background: white;
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 12px;
  overflow-x: auto;
}

.tab-item {
  padding: 8px 16px;
  font-size: 14px;
  color: #666;
  border-radius: 8px;
  white-space: nowrap;
  transition: all 0.3s;
}

.tab-item.active {
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  color: white;
}

.faq-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.faq-content-text {
  padding: 12px 16px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}
</style>
