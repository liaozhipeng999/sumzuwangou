<template>
  <div class="message">
    <van-nav-bar title="消息" />

    <!-- 消息列表 -->
    <van-cell-group inset>
      <van-empty v-if="messages.length === 0" description="暂无消息" />
      <van-cell
        v-for="item in messages"
        :key="item.id"
        :title="item.title"
        :label="item.content"
        :value="item.time"
        is-link
      />
    </van-cell-group>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Message {
  id: number
  title: string
  content: string
  time: string
}

const messages = ref<Message[]>([])
</script>

<style scoped>
.message {
  padding-bottom: 50px;
  background: #f7f8fa;
  min-height: 100vh;
}

.van-cell-group {
  margin-top: 10px;
}
</style>