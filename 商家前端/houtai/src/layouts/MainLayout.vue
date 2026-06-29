<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'">
      <div class="logo">
        <img src="@/assets/vue.svg" alt="logo" v-if="!isCollapse" />
        <span v-if="!isCollapse">电商后台</span>
        <el-icon v-else><Shop /></el-icon>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        
        <el-sub-menu index="product">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/product/list">商品列表</el-menu-item>
          <el-menu-item index="/product/add">添加商品</el-menu-item>
          <el-menu-item index="/product/category">商品分类</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="order">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order/list">订单列表</el-menu-item>
          <el-menu-item index="/order/return">退货管理</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/user/list">用户列表</el-menu-item>
          <el-menu-item index="/user/address">收货地址</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="customer">
          <template #title>
            <el-icon><ChatDotRound /></el-icon>
            <span>客服中心</span>
          </template>
          <el-menu-item index="/merchant/customer/conversations">
            <el-badge :value="customerUnread" :hidden="customerUnread === 0" :max="99" class="menu-badge">
              会话列表
            </el-badge>
          </el-menu-item>
          <el-menu-item index="/merchant/customer/quick-reply">快捷回复</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </template>
          <el-menu-item index="/system/config">基础配置</el-menu-item>
          <el-menu-item index="/system/permission">权限管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">{{ merchantStore.merchantName || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 主内容 -->
      <el-main>
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Shop,
  DataBoard,
  Goods,
  Document,
  User,
  Setting,
  Fold,
  Expand,
  ArrowDown,
  ChatDotRound
} from '@element-plus/icons-vue'
import { useMerchantStore } from '@/stores/merchant'
import { getMerchantUnreadCount } from '@/api/customer'
import { useWebSocket } from '@/composables/useWebSocket'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const merchantStore = useMerchantStore()
const { connect, subscribeShopUnread } = useWebSocket()
const customerUnread = ref(0)
let unreadTimer: ReturnType<typeof setInterval> | null = null
let unsubUnread: (() => void) | null = null

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta && item.meta.title)
})

async function loadUnreadCount() {
  const shopId = merchantStore.merchantInfo?.id
  if (!shopId) return
  try {
    const res = await getMerchantUnreadCount(shopId)
    if (res.code === 200 && res.data) {
      customerUnread.value = res.data.total || 0
    }
  } catch (e) {
    // silent
  }
}

onMounted(() => {
  loadUnreadCount()
  connect()
  const shopId = merchantStore.merchantInfo?.id
  if (shopId) {
    unsubUnread = subscribeShopUnread(shopId, (msg) => {
      if (msg.unreadCount !== undefined) {
        customerUnread.value = msg.unreadCount
      }
    })
  }
  unreadTimer = setInterval(loadUnreadCount, 30000)
})

onUnmounted(() => {
  if (unreadTimer) clearInterval(unreadTimer)
  if (unsubUnread) unsubUnread()
})

const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/system/config')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '退出',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch {
        return
      }

      await merchantStore.logout()
      sessionStorage.clear()

      const currentPath = route.path
      if (currentPath.startsWith('/merchant')) {
        router.replace('/merchant/login')
      } else {
        router.replace('/login')
      }
      break
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background-color: #2b3a4a;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.logo img {
  width: 32px;
  height: 32px;
}

.sidebar-menu {
  border-right: none;
  background-color: #304156;
}

:deep(.el-menu) {
  background-color: #304156;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: #bfcbd9;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #263445;
  color: #409eff;
}

:deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #fff;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 50px !important;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 0 10px;
}

.username {
  font-size: 14px;
  color: #333;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
