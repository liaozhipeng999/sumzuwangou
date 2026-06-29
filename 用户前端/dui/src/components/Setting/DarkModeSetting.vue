<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">深色模式</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="mode-card">
          <div class="mode-header">
            <span class="mode-icon">🌙</span>
            <div class="mode-info">
              <span class="mode-title">深色模式</span>
              <span class="mode-desc">当前已关闭</span>
            </div>
            <van-switch v-model="darkMode" size="24" />
          </div>
          <div class="mode-preview">
            <div class="preview-phone">
              <div class="phone-screen light">
                <div class="screen-header"></div>
                <div class="screen-content">
                  <div class="content-item"></div>
                  <div class="content-item"></div>
                  <div class="content-item"></div>
                </div>
              </div>
              <span class="preview-arrow">→</span>
              <div class="phone-screen dark">
                <div class="screen-header"></div>
                <div class="screen-content">
                  <div class="content-item"></div>
                  <div class="content-item"></div>
                  <div class="content-item"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="setting-item" @click="selectMode('auto')">
          <div class="item-content">
            <span class="item-title">自动切换</span>
            <span class="item-desc">跟随系统设置自动切换</span>
          </div>
          <van-icon v-if="darkModeMode === 'auto'" name="success" size="16" color="#ff4757" />
          <van-icon v-else name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="selectMode('manual')">
          <div class="item-content">
            <span class="item-title">手动切换</span>
            <span class="item-desc">手动控制深色模式开关</span>
          </div>
          <van-icon v-if="darkModeMode === 'manual'" name="success" size="16" color="#ff4757" />
          <van-icon v-else name="arrow" size="14" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">定时切换</div>
        
        <div class="setting-item" @click="handleAction('schedule')">
          <div class="item-content">
            <span class="item-title">定时开启/关闭</span>
            <span class="item-desc">设置自动切换时间</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'

const router = useRouter()

const darkMode = ref(false)
const darkModeMode = ref('manual')

const goBack = () => {
  router.back()
}

const selectMode = (mode: string) => {
  darkModeMode.value = mode
  showToast('已切换到' + (mode === 'auto' ? '自动模式' : '手动模式'))
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    schedule: '定时设置'
  }
  showToast(actionMap[action] || action)
}
</script>

<style scoped>
.setting-sub-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.page-header {
  background: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.back-btn {
  font-size: 20px;
  color: #333;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.header-placeholder {
  width: 24px;
}

.setting-list {
  padding: 12px;
}

.section {
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
}

.section-title {
  padding: 14px 16px 8px;
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

.setting-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.setting-item:last-child {
  border-bottom: none;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 15px;
  color: #333;
  display: block;
}

.item-desc {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.mode-card {
  margin: 16px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 12px;
  padding: 16px;
  color: white;
}

.mode-header {
  display: flex;
  align-items: center;
}

.mode-icon {
  font-size: 24px;
  margin-right: 12px;
}

.mode-info {
  flex: 1;
}

.mode-title {
  font-size: 16px;
  font-weight: bold;
  display: block;
}

.mode-desc {
  font-size: 13px;
  opacity: 0.8;
  margin-top: 4px;
}

.mode-preview {
  margin-top: 16px;
}

.preview-phone {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.phone-screen {
  width: 80px;
  height: 140px;
  border-radius: 16px;
  padding: 8px;
}

.phone-screen.light {
  background: white;
}

.phone-screen.dark {
  background: #2d2d2d;
}

.screen-header {
  height: 20px;
  background: #f0f0f0;
  border-radius: 8px;
  margin-bottom: 8px;
}

.phone-screen.dark .screen-header {
  background: #1a1a1a;
}

.screen-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.content-item {
  height: 16px;
  background: #f0f0f0;
  border-radius: 4px;
}

.phone-screen.dark .content-item {
  background: #3d3d3d;
}

.preview-arrow {
  font-size: 18px;
  opacity: 0.6;
}
</style>
