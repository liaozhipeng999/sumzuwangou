<template>
  <van-tabbar v-model="active" route fixed>
    <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
    <van-tabbar-item to="/cart" icon="shopping-cart-o" :badge="cartCount">购物车</van-tabbar-item>
    <van-tabbar-item to="/message" icon="chat-o" :badge="messageBadge">消息</van-tabbar-item>
    <van-tabbar-item to="/my" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useMessageStore } from '@/stores/message'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const active = ref(0)
const messageStore = useMessageStore()
const cartStore = useCartStore()

// 购物车角标：显示商品总数量
const cartCount = computed(() => {
  const count = cartStore.totalCount
  return count > 0 ? count : ''
})

// 消息角标：未读数 > 0 时显示
const messageBadge = computed(() => {
  const count = messageStore.unreadCount
  if (count <= 0) return ''
  return count > 99 ? '99+' : String(count)
})

// 加载购物车数量
const loadCartCount = async () => {
  await cartStore.loadCart()
}

// 监听路由变化，每次进入购物车页面时刷新数量
watch(() => route.path, (newPath) => {
  if (newPath === '/cart') {
    loadCartCount()
  }
})

onMounted(() => {
  messageStore.fetchUnreadCount()
  messageStore.startPolling(30000)
  loadCartCount() // 初始加载购物车数量
})

onUnmounted(() => {
  messageStore.stopPolling()
})
</script>
