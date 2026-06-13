<template>
  <van-tabbar v-model="active" route fixed>
    <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
    <van-tabbar-item to="/cart" icon="shopping-cart-o" :badge="cartCount">购物车</van-tabbar-item>
    <van-tabbar-item to="/message" icon="chat-o" :badge="messageCount">消息</van-tabbar-item>
    <van-tabbar-item to="/my" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const active = ref(0)
const cartCount = ref(0)
const messageCount = ref(0)
</script>