import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/components/Login/login.vue'),
    meta: { title: '登录', hideTabBar: true }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/components/Home/home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/index',
    name: 'IndexHome',
    component: () => import('@/components/Home/IndexHome.vue'),
    meta: { title: '商城首页' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/components/Cart/cart.vue'),
    meta: { title: '购物车' }
  },
  {
    path: '/message',
    name: 'Message',
    component: () => import('@/components/message/message.vue'),
    meta: { title: '消息' }
  },
  {
    path: '/my',
    name: 'My',
    component: () => import('@/components/my/My.vue'),
    meta: { title: '我的' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由白名单：不需要登录就能访问的页面
const whiteList = ['/login']

// 全局前置守卫
router.beforeEach((to, _from, next) => {
  // 获取登录凭证
  const token = sessionStorage.getItem('token')

  // 情况1：当前路由在白名单（登录页），直接放行
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 情况2：不在白名单，判断是否登录
  if (token) {
    // 已登录：正常跳转
    next()
  } else {
    // 未登录：静默跳转到登录页（不显示 alert）
    next({ path: '/login', query: { redirect: to.fullPath } })
  }
})

export default router