<template>
  <div class="search-bar-wrapper" @click="goSearch">
    <div class="search-input">
      <van-icon name="search" size="18" color="#999" />
      <span class="placeholder">{{ placeholder }}</span>
    </div>
    <van-icon name="photograph" size="22" color="#999" class="camera-icon" />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

interface Props {
  placeholder?: string
}

withDefaults(defineProps<Props>(), {
  placeholder: '搜索商品'
})

const router = useRouter()

const goSearch = () => {
  router.push('/search')
}
</script>

<style scoped>
.search-bar-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background-color: #fff;
}

.search-input {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 12px;
  background-color: #f5f5f5;
  border-radius: 18px;
}

.placeholder {
  font-size: 14px;
  color: #999;
}

.camera-icon {
  flex-shrink: 0;
}
</style>
