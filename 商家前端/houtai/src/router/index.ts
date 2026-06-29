import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'product/list',
        name: 'ProductList',
        component: () => import('@/views/ProductList.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: 'product/add',
        name: 'ProductAdd',
        component: () => import('@/views/ProductAdd.vue'),
        meta: { title: '添加商品' }
      },
      {
        path: 'product/category',
        name: 'ProductCategory',
        component: () => import('@/views/ProductCategory.vue'),
        meta: { title: '商品分类' }
      },
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/OrderList.vue'),
        meta: { title: '订单列表' }
      },
      {
        path: 'order/return',
        name: 'OrderReturn',
        component: () => import('@/views/OrderReturn.vue'),
        meta: { title: '退货管理' }
      },
      {
        path: 'user/list',
        name: 'UserList',
        component: () => import('@/views/UserList.vue'),
        meta: { title: '用户列表' }
      },
      {
        path: 'user/address',
        name: 'UserAddress',
        component: () => import('@/views/UserAddress.vue'),
        meta: { title: '收货地址' }
      },
      {
        path: 'system/config',
        name: 'SystemConfig',
        component: () => import('@/views/SystemConfig.vue'),
        meta: { title: '基础配置' }
      },
      {
        path: 'system/permission',
        name: 'SystemPermission',
        component: () => import('@/views/SystemPermission.vue'),
        meta: { title: '权限管理' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  // 商家路由
  {
    path: '/merchant/login',
    name: 'MerchantLogin',
    component: () => import('@/views/merchant/MerchantLogin.vue'),
    meta: { title: '商家登录' }
  },
  {
    path: '/merchant/register',
    name: 'MerchantRegister',
    component: () => import('@/views/merchant/MerchantRegister.vue'),
    meta: { title: '商家入驻' }
  },
  {
    path: '/merchant',
    name: 'Merchant',
    component: MainLayout,
    redirect: '/merchant/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'MerchantDashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '商家后台' }
      },
      {
        path: 'product/list',
        name: 'MerchantProductList',
        component: () => import('@/views/ProductList.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'product/add',
        name: 'MerchantProductAdd',
        component: () => import('@/views/ProductAdd.vue'),
        meta: { title: '添加商品' }
      },
      {
        path: 'order/list',
        name: 'MerchantOrderList',
        component: () => import('@/views/OrderList.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'customer/conversations',
        name: 'ConversationList',
        component: () => import('@/views/customer/ConversationList.vue'),
        meta: { title: '客服会话' }
      },
      {
        path: 'customer/chat/:userId',
        name: 'ChatRoom',
        component: () => import('@/views/customer/ChatRoom.vue'),
        meta: { title: '聊天' }
      },
      {
        path: 'customer/quick-reply',
        name: 'QuickReplyManage',
        component: () => import('@/views/customer/QuickReplyManage.vue'),
        meta: { title: '快捷回复' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from) => {
  // 公共页面：登录页面、注册页面
  const publicPages = ['/login', '/merchant/login', '/merchant/register']
  const isPublicPage = publicPages.includes(to.path)
  
  // 从 localStorage 获取 token
  const token = localStorage.getItem('merchant_token')
  const hasToken = !!token
  
  // 如果已登录（有token）
  if (hasToken) {
    // 如果访问的是登录页面，跳转到商家后台
    if (isPublicPage) {
      return '/merchant/dashboard'
    }
    // 否则正常访问
    return true
  } else {
    // 未登录状态
    // 如果访问的是受保护的后台页面，跳转到商家登录页面
    if (!isPublicPage) {
      return '/merchant/login'
    }
    // 否则正常访问公共页面
    return true
  }
})

export default router
