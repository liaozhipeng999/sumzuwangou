<template>
  <div class="setting-page">
    <!-- 顶部导航 -->
    <div class="setting-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">设置</span>
      <div class="header-placeholder"></div>
    </div>

    <!-- 设置列表 -->
    <div class="setting-list">
      <!-- 账户设置 -->
      <div class="section">
        <div class="section-title">账户设置</div>
        
        <div class="setting-item" @click="handleAction('bindWechat')">
          <div class="item-icon-wrap">
            <span class="item-icon">💬</span>
          </div>
          <div class="item-content">
            <span class="item-title">绑定微信</span>
          </div>
          <div class="item-right">
            <span class="item-value">czc</span>
            <van-icon name="arrow" size="14" />
          </div>
        </div>

        <div class="setting-item" @click="handleAction('accountSecurity')">
          <div class="item-icon-wrap">
            <span class="item-icon">🔐</span>
          </div>
          <div class="item-content">
            <span class="item-title">账号与安全</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('payment')">
          <div class="item-icon-wrap">
            <span class="item-icon">💳</span>
          </div>
          <div class="item-content">
            <span class="item-title">免密支付设置</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('credit')">
          <div class="item-icon-wrap">
            <span class="item-icon">💸</span>
          </div>
          <div class="item-content">
            <span class="item-title">先用后付设置</span>
          </div>
          <div class="item-right">
            <span class="item-badge">领取0元下单资格</span>
            <van-icon name="arrow" size="14" />
          </div>
        </div>

        <div class="setting-item" @click="handleAction('groupBuy')">
          <div class="item-icon-wrap">
            <span class="item-icon">🤝</span>
          </div>
          <div class="item-content">
            <span class="item-title">免拼设置</span>
          </div>
          <div class="item-right">
            <span class="item-value">还剩136次免拼机会</span>
            <van-icon name="arrow" size="14" />
          </div>
        </div>
      </div>

      <!-- 通用设置 -->
      <div class="section">
        <div class="section-title">通用设置</div>

        <div class="setting-item" @click="handleAction('language')">
          <div class="item-icon-wrap">
            <span class="item-icon">🌍</span>
          </div>
          <div class="item-content">
            <span class="item-title">国家与地区/语言</span>
          </div>
          <div class="item-right">
            <span class="item-value">中国大陆/中文</span>
            <van-icon name="arrow" size="14" />
          </div>
        </div>

        <div class="setting-item" @click="handleAction('social')">
          <div class="item-icon-wrap">
            <span class="item-icon">👥</span>
          </div>
          <div class="item-content">
            <span class="item-title">拼小圈设置</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('notification')">
          <div class="item-icon-wrap">
            <span class="item-icon">🔔</span>
          </div>
          <div class="item-content">
            <span class="item-title">消息接收设置</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('media')">
          <div class="item-icon-wrap">
            <span class="item-icon">📹</span>
          </div>
          <div class="item-content">
            <span class="item-title">直播、照片、视频和通话</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('privacyNumber')">
          <div class="item-icon-wrap">
            <span class="item-icon">🔒</span>
          </div>
          <div class="item-content">
            <span class="item-title">隐私号码保护设置</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('fontSize')">
          <div class="item-icon-wrap">
            <span class="item-icon">📝</span>
          </div>
          <div class="item-content">
            <span class="item-title">字体大小设置</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('darkMode')">
          <div class="item-icon-wrap">
            <span class="item-icon">🌙</span>
          </div>
          <div class="item-content">
            <span class="item-title">深色模式</span>
          </div>
          <div class="item-right">
            <span class="item-value">已关闭</span>
            <van-icon name="arrow" size="14" />
          </div>
        </div>
      </div>

      <!-- 帮助与反馈 -->
      <div class="section">
        <div class="section-title">帮助与反馈</div>

        <div class="setting-item" @click="handleAction('faq')">
          <div class="item-icon-wrap">
            <span class="item-icon">❓</span>
          </div>
          <div class="item-content">
            <span class="item-title">常见问题</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('feedback')">
          <div class="item-icon-wrap">
            <span class="item-icon">💬</span>
          </div>
          <div class="item-content">
            <span class="item-title">意见反馈</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('cooperation')">
          <div class="item-icon-wrap">
            <span class="item-icon">🤝</span>
          </div>
          <div class="item-content">
            <span class="item-title">合作/免费入驻拼多多</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <!-- 特殊功能 -->
      <div class="section">
        <div class="setting-item" @click="handleAction('elderMode')">
          <div class="item-icon-wrap">
            <span class="item-icon">👵</span>
          </div>
          <div class="item-content">
            <span class="item-title">长辈版</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('minorMode')">
          <div class="item-icon-wrap">
            <span class="item-icon">👶</span>
          </div>
          <div class="item-content">
            <span class="item-title">未成年人模式</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <!-- 数据管理 -->
      <div class="section">
        <div class="section-title">数据管理</div>

        <div class="setting-item" @click="handleAction('clearCache')">
          <div class="item-icon-wrap">
            <span class="item-icon">🗑️</span>
          </div>
          <div class="item-content">
            <span class="item-title">清除缓存</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('shareList')">
          <div class="item-icon-wrap">
            <span class="item-icon">📊</span>
          </div>
          <div class="item-content">
            <span class="item-title">个人信息共享清单</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('collectList')">
          <div class="item-icon-wrap">
            <span class="item-icon">📋</span>
          </div>
          <div class="item-content">
            <span class="item-title">个人信息收集清单</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('privacy')">
          <div class="item-icon-wrap">
            <span class="item-icon">🔐</span>
          </div>
          <div class="item-content">
            <span class="item-title">隐私政策摘要</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <!-- 关于 -->
      <div class="section">
        <div class="setting-item" @click="handleAction('about')">
          <div class="item-icon-wrap">
            <span class="item-icon">ℹ️</span>
          </div>
          <div class="item-content">
            <span class="item-title">关于拼多多</span>
          </div>
          <div class="item-right">
            <span class="update-badge">有新版本可升级</span>
            <van-icon name="arrow" size="14" />
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作 -->
    <div class="bottom-section">
      <div class="section">
        <div class="setting-item action-item" @click="handleAction('switchAccount')">
          <span class="action-title">切换账号</span>
        </div>
        <div class="setting-item action-item" @click="handleAction('logout')">
          <span class="action-title logout">退出登录</span>
        </div>
        <div class="setting-item action-item" @click="handleAction('network')">
          <span class="action-title diagnose">网络诊断</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { showToast, showLoadingToast, showDialog } from 'vant'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const goBack = () => {
  router.back()
}

const handleAction = (action: string) => {
  const actionMap: Record<string, { name: string; path?: string }> = {
    bindWechat: { name: '绑定微信', path: '/setting/account' },
    accountSecurity: { name: '账号与安全', path: '/setting/account' },
    payment: { name: '免密支付设置', path: '/setting/payment' },
    credit: { name: '先用后付设置', path: '/setting/credit' },
    groupBuy: { name: '免拼设置', path: '/setting/groupbuy' },
    language: { name: '国家与地区/语言', path: '/setting/language' },
    social: { name: '拼小圈设置', path: '/setting/social' },
    notification: { name: '消息接收设置', path: '/setting/notification' },
    media: { name: '直播、照片、视频和通话', path: '/setting/media' },
    privacyNumber: { name: '隐私号码保护设置', path: '/setting/privacy-number' },
    fontSize: { name: '字体大小设置', path: '/setting/fontsize' },
    darkMode: { name: '深色模式', path: '/setting/darkmode' },
    faq: { name: '常见问题', path: '/setting/faq' },
    feedback: { name: '意见反馈', path: '/setting/feedback' },
    cooperation: { name: '合作/免费入驻拼多多', path: '/setting/cooperation' },
    elderMode: { name: '长辈版', path: '/setting/elder' },
    minorMode: { name: '未成年人模式', path: '/setting/minor' },
    clearCache: { name: '清除缓存' },
    shareList: { name: '个人信息共享清单', path: '/setting/share-list' },
    collectList: { name: '个人信息收集清单', path: '/setting/collect-list' },
    privacy: { name: '隐私政策摘要', path: '/setting/privacy' },
    about: { name: '关于拼多多', path: '/setting/about' },
    switchAccount: { name: '切换账号' },
    logout: { name: '退出登录' },
    network: { name: '网络诊断' }
  }
  
  const item = actionMap[action]
  
  if (action === 'logout') {
    sessionStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('loginInfo')
    localStorage.removeItem('cartCache')
    userStore.clearUserInfo()
    cartStore.cartItems = []
    cartStore.totalCount = 0
    router.push('/login')
    showToast('已退出登录')
  } else if (action === 'clearCache') {
    showLoadingToast({ message: '清除中...', duration: 1500 })
    setTimeout(() => {
      localStorage.removeItem('cartCache')
      showToast('缓存已清除')
    }, 1500)
  } else if (action === 'switchAccount') {
    showDialog({
      title: '切换账号',
      message: '确定要切换账号吗？',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }).then(() => {
      sessionStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('loginInfo')
      localStorage.removeItem('cartCache')
      userStore.clearUserInfo()
      cartStore.cartItems = []
      cartStore.totalCount = 0
      router.push('/login')
    }).catch(() => {})
  } else if (action === 'network') {
    showLoadingToast({ message: '诊断中...', duration: 2000 })
    setTimeout(() => {
      showDialog({
        title: '网络诊断结果',
        message: '网络连接正常，可以正常使用应用。',
        confirmButtonText: '确定'
      })
    }, 2000)
  } else if (item?.path) {
    router.push(item.path)
  } else {
    showToast(item?.name || action)
  }
}
</script>

<style scoped>
.setting-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 120px;
}

/* 顶部导航 */
.setting-header {
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

/* 设置列表 */
.setting-list {
  padding: 12px;
}

.section {
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
}

.section-title {
  padding: 14px 16px 8px;
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

.setting-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background-color 0.2s;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-item:active {
  background-color: #f8f9fa;
}

.item-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.item-icon {
  font-size: 18px;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 15px;
  color: #333;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-value {
  font-size: 14px;
  color: #999;
}

.item-badge {
  font-size: 12px;
  color: white;
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.update-badge {
  font-size: 12px;
  color: white;
  background: #ff4757;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

/* 底部操作 */
.bottom-section {
  padding-bottom: 24px;
}

.action-item {
  justify-content: center;
  border-bottom: none;
}

.action-title {
  font-size: 15px;
  color: #333;
}

.action-title.logout {
  color: #999;
}

.action-title.diagnose {
  color: #667eea;
  font-size: 14px;
}
</style>
