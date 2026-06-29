<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">未成年人模式</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="minor-content">
      <div class="feature-card">
        <div class="feature-header">
          <span class="feature-icon">👶</span>
          <div class="feature-info">
            <span class="feature-title">未成年人模式</span>
            <span class="feature-desc">为未成年人提供更安全的使用环境</span>
          </div>
        </div>
        
        <div class="feature-switch">
          <span>开启未成年人模式</span>
          <van-switch v-model="minorMode" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">未成年人模式特点</div>
        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-icon-small">🔒</span>
            <span class="feature-text">过滤不适宜内容</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon-small">⏱️</span>
            <span class="feature-text">限制使用时长</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon-small">💰</span>
            <span class="feature-text">消费限额保护</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon-small">📝</span>
            <span class="feature-text">家长监督功能</span>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">使用时长设置</div>
        <div class="time-options">
          <div 
            class="time-option" 
            v-for="time in timeOptions" 
            :key="time.value"
            :class="{ active: selectedTime === time.value }"
            @click="selectTime(time.value)"
          >
            {{ time.label }}
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">消费限额设置</div>
        <div class="limit-options">
          <div 
            class="limit-option" 
            v-for="limit in limitOptions" 
            :key="limit.value"
            :class="{ active: selectedLimit === limit.value }"
            @click="selectLimit(limit.value)"
          >
            {{ limit.label }}
          </div>
        </div>
      </div>

      <div class="section">
        <div class="setting-item" @click="handleAction('password')">
          <div class="item-content">
            <span class="item-title">设置监护人密码</span>
            <span class="item-desc">防止未成年人自行关闭模式</span>
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

const minorMode = ref(false)
const selectedTime = ref('1h')
const selectedLimit = ref('100')

const timeOptions = [
  { label: '30分钟', value: '30m' },
  { label: '1小时', value: '1h' },
  { label: '2小时', value: '2h' },
  { label: '3小时', value: '3h' }
]

const limitOptions = [
  { label: '50元', value: '50' },
  { label: '100元', value: '100' },
  { label: '200元', value: '200' },
  { label: '500元', value: '500' }
]

const goBack = () => {
  router.back()
}

const selectTime = (value: string) => {
  selectedTime.value = value
  showToast('使用时长已设置')
}

const selectLimit = (value: string) => {
  selectedLimit.value = value
  showToast('消费限额已设置')
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    password: '设置监护人密码'
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

.minor-content {
  padding: 12px;
}

.feature-card {
  background: linear-gradient(135deg, #ffd93d 0%, #ff9500 100%);
  border-radius: 12px;
  padding: 20px;
  color: white;
  margin-bottom: 12px;
}

.feature-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.feature-icon {
  font-size: 40px;
  margin-right: 12px;
}

.feature-info {
  flex: 1;
}

.feature-title {
  font-size: 18px;
  font-weight: bold;
  display: block;
}

.feature-desc {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 4px;
}

.feature-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  padding: 12px 16px;
  font-size: 15px;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.feature-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.feature-item {
  width: calc(50% - 6px);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 10px;
}

.feature-icon-small {
  font-size: 18px;
}

.feature-text {
  font-size: 14px;
  color: #333;
}

.time-options, .limit-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.time-option, .limit-option {
  padding: 10px 20px;
  background: #f7f8fa;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.time-option.active, .limit-option.active {
  background: linear-gradient(135deg, #ffd93d 0%, #ff9500 100%);
  color: white;
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
</style>
