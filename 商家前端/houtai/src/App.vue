<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMerchantStore } from '@/stores/merchant'

const router = useRouter()
const merchantStore = useMerchantStore()

onMounted(() => {
  // 检查是否当天已登录
  if (merchantStore.checkTodayLogin()) {
    console.log('✅ 当天已登录，自动跳转到后台')
    router.push('/merchant/dashboard')
  }
})
</script>

<template>
  <router-view />
</template>
