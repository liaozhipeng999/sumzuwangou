<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TabBar from './components/TabBar.vue'
import AIFloat from './components/AIFloat/AIFloat.vue'

const route = useRoute()
const showTabBar = computed(() => !route.meta.hideTabBar)
</script>

<template>
  <div class="app">
    <router-view />
    <TabBar v-if="showTabBar" />
    <AIFloat />
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
}
</style>
