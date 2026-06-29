import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/components/Login/login.vue'),
    meta: { title: '登录', hideTabBar: true, requiresAuth: false }
  },
  
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/components/Home/home.vue'),
    meta: { title: '首页', requiresAuth: true }
  },
  {
    path: '/index',
    name: 'IndexHome',
    component: () => import('@/components/Home/IndexHome.vue'),
    meta: { title: '商城首页', requiresAuth: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/components/Cart/cart.vue'),
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/message',
    name: 'Message',
    component: () => import('@/components/message/message.vue'),
    meta: { title: '消息', requiresAuth: true }
  },
  {
    path: '/message/merchant',
    name: 'MerchantMessages',
    component: () => import('@/components/message/MerchantMessages.vue'),
    meta: { title: '商家消息', requiresAuth: true, hideTabBar: true }
  },
  {
    path: '/message/chat/:shopId',
    name: 'ShopChat',
    component: () => import('@/components/message/ShopChat.vue'),
    meta: { title: '客服对话', requiresAuth: true, hideTabBar: true }
  },
  {
    path: '/message/transaction',
    name: 'TransactionMessages',
    component: () => import('@/components/message/TransactionMessages.vue'),
    meta: { title: '交易消息', requiresAuth: true, hideTabBar: true }
  },
  {
    path: '/message/system',
    name: 'SystemMessages',
    component: () => import('@/components/message/SystemMessages.vue'),
    meta: { title: '系统通知', requiresAuth: true, hideTabBar: true }
  },
  {
    path: '/message/promotion',
    name: 'PromotionMessages',
    component: () => import('@/components/message/PromotionMessages.vue'),
    meta: { title: '活动优惠', requiresAuth: true, hideTabBar: true }
  },
  {
    path: '/message/:type',
    name: 'MessageList',
    component: () => import('@/components/message/MessageList.vue'),
    meta: { title: '消息详情', requiresAuth: true, hideTabBar: true }
  },
  {
    path: '/my',
    name: 'My',
    component: () => import('@/components/my/My.vue'),
    meta: { title: '我的', requiresAuth: true }
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@/components/Order/OrderList.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/setting',
    name: 'Setting',
    component: () => import('@/components/Setting/Setting.vue'),
    meta: { title: '设置', requiresAuth: true }
  },
  {
    path: '/setting/account',
    name: 'AccountSecurity',
    component: () => import('@/components/Setting/AccountSecurity.vue'),
    meta: { title: '账号与安全', requiresAuth: true }
  },
  {
    path: '/setting/notification',
    name: 'Notification',
    component: () => import('@/components/Setting/Notification.vue'),
    meta: { title: '消息接收设置', requiresAuth: true }
  },
  {
    path: '/setting/faq',
    name: 'FAQ',
    component: () => import('@/components/Setting/FAQ.vue'),
    meta: { title: '常见问题', requiresAuth: false }
  },
  {
    path: '/setting/payment',
    name: 'Payment',
    component: () => import('@/components/Setting/PaymentSetting.vue'),
    meta: { title: '免密支付设置', requiresAuth: true }
  },
  {
    path: '/setting/credit',
    name: 'Credit',
    component: () => import('@/components/Setting/CreditSetting.vue'),
    meta: { title: '先用后付设置', requiresAuth: true }
  },
  {
    path: '/setting/groupbuy',
    name: 'GroupBuy',
    component: () => import('@/components/Setting/GroupBuySetting.vue'),
    meta: { title: '免拼设置', requiresAuth: true }
  },
  {
    path: '/setting/language',
    name: 'Language',
    component: () => import('@/components/Setting/LanguageSetting.vue'),
    meta: { title: '国家与地区/语言', requiresAuth: false }
  },
  {
    path: '/setting/social',
    name: 'Social',
    component: () => import('@/components/Setting/SocialSetting.vue'),
    meta: { title: '拼小圈设置', requiresAuth: true }
  },
  {
    path: '/setting/media',
    name: 'Media',
    component: () => import('@/components/Setting/MediaSetting.vue'),
    meta: { title: '直播、照片、视频和通话', requiresAuth: true }
  },
  {
    path: '/setting/privacy-number',
    name: 'PrivacyNumber',
    component: () => import('@/components/Setting/PrivacyNumberSetting.vue'),
    meta: { title: '隐私号码保护设置', requiresAuth: true }
  },
  {
    path: '/setting/fontsize',
    name: 'FontSize',
    component: () => import('@/components/Setting/FontSizeSetting.vue'),
    meta: { title: '字体大小设置', requiresAuth: false }
  },
  {
    path: '/setting/darkmode',
    name: 'DarkMode',
    component: () => import('@/components/Setting/DarkModeSetting.vue'),
    meta: { title: '深色模式', requiresAuth: false }
  },
  {
    path: '/setting/feedback',
    name: 'Feedback',
    component: () => import('@/components/Setting/Feedback.vue'),
    meta: { title: '意见反馈', requiresAuth: false }
  },
  {
    path: '/setting/cooperation',
    name: 'Cooperation',
    component: () => import('@/components/Setting/Cooperation.vue'),
    meta: { title: '合作/免费入驻拼多多', requiresAuth: false }
  },
  {
    path: '/setting/elder',
    name: 'Elder',
    component: () => import('@/components/Setting/ElderMode.vue'),
    meta: { title: '长辈版', requiresAuth: false }
  },
  {
    path: '/setting/minor',
    name: 'Minor',
    component: () => import('@/components/Setting/MinorMode.vue'),
    meta: { title: '未成年人模式', requiresAuth: false }
  },
  {
    path: '/setting/share-list',
    name: 'ShareList',
    component: () => import('@/components/Setting/ShareList.vue'),
    meta: { title: '个人信息共享清单', requiresAuth: false }
  },
  {
    path: '/setting/collect-list',
    name: 'CollectList',
    component: () => import('@/components/Setting/CollectList.vue'),
    meta: { title: '个人信息收集清单', requiresAuth: false }
  },
  {
    path: '/setting/privacy',
    name: 'Privacy',
    component: () => import('@/components/Setting/PrivacyPolicy.vue'),
    meta: { title: '隐私政策摘要', requiresAuth: false }
  },
  {
    path: '/setting/about',
    name: 'About',
    component: () => import('@/components/Setting/About.vue'),
    meta: { title: '关于拼多多', requiresAuth: false }
  },
  {
    path: '/my/favorites',
    name: 'FavoriteList',
    component: () => import('@/components/my/FavoriteList.vue'),
    meta: { title: '商品收藏', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/my/wallet',
    name: 'Wallet',
    component: () => import('@/components/my/Wallet.vue'),
    meta: { title: '我的钱包', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/my/history',
    name: 'History',
    component: () => import('@/components/my/History.vue'),
    meta: { title: '历史浏览', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/components/Product/ProductDetail.vue'),
    meta: { title: '商品详情', hideTabBar: true, requiresAuth: false }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/components/Product/ProductList.vue'),
    meta: { title: '商品列表', hideTabBar: true, requiresAuth: false }
  },
  {
    path: '/peng',
    name: 'PengPage',
    component: () => import('@/components/Peng/PengPage.vue'),
    meta: { title: 'Peng商品', hideTabBar: true, requiresAuth: false }
  },
  {
    path: '/hot-list',
    name: 'HotList',
    component: () => import('@/components/Product/HotList.vue'),
    meta: { title: '热销榜单', hideTabBar: true, requiresAuth: false }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/components/Search/SearchPage.vue'),
    meta: { title: '搜索', hideTabBar: true, requiresAuth: false }
  },
  {
    path: '/customer-service/:merchantId?/:productId?',
    name: 'CustomerService',
    component: () => import('@/components/CustomerService/CustomerService.vue'),
    meta: { title: '客服', hideTabBar: true, requiresAuth: false }
  },
  {
    path: '/my/address',
    name: 'Address',
    component: () => import('@/components/Address/AddressList.vue'),
    meta: { title: '收货地址', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/components/Order/Checkout.vue'),
    meta: { title: '确认订单', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('@/components/Order/OrderDetail.vue'),
    meta: { title: '订单详情', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/payment/success',
    name: 'PaymentSuccess',
    component: () => import('@/components/Payment/PaymentSuccess.vue'),
    meta: { title: '支付成功', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/payment/failed',
    name: 'PaymentFailed',
    component: () => import('@/components/Payment/PaymentFailed.vue'),
    meta: { title: '支付失败', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/category/:id/products/:name?',
    name: 'CategoryProducts',
    component: () => import('@/views/CategoryProducts.vue'),
    meta: { title: '分类商品', hideTabBar: true, requiresAuth: false }
  },
  {
    path: '/activity/lottery',
    name: 'Lottery',
    component: () => import('@/components/Activity/Lottery.vue'),
    meta: { title: '现金大转盘', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/activity/discount',
    name: 'Discount',
    component: () => import('@/components/Activity/Discount.vue'),
    meta: { title: '首件抢五折', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/activity/train-ticket',
    name: 'TrainTicket',
    component: () => import('@/components/Activity/TrainTicket.vue'),
    meta: { title: '火车票', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/activity/flash-sale',
    name: 'FlashSale',
    component: () => import('@/components/Activity/FlashSale.vue'),
    meta: { title: '限时秒杀', hideTabBar: true, requiresAuth: true }
  },
  {
    path: '/activity/subsidy',
    name: 'Subsidy',
    component: () => import('@/components/Activity/Subsidy.vue'),
    meta: { title: '百亿补贴', hideTabBar: true, requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 默认测试账号
const DEFAULT_USERNAME = 'user0001'
const DEFAULT_PASSWORD = '123456'

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = String(to.meta.title)
  }
  
  // 检查是否需要登录
  const requiresAuth = to.meta.requiresAuth !== undefined ? to.meta.requiresAuth : true
  
  if (requiresAuth && !userStore.isLoggedIn()) {
    console.log(' 路由守卫拦截：未登录用户尝试访问需要认证的页面:', to.path)
    // 需要登录但未登录，跳转到登录页
    next({ 
      path: '/login', 
      query: { redirect: to.fullPath } 
    })
  } else {
    if (userStore.isLoggedIn()) {
      console.log('✅ 路由守卫放行：已登录用户访问页面:', to.path)
    }
    next()
  }
})

export { DEFAULT_USERNAME, DEFAULT_PASSWORD }

export default router
