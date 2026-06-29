<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">长辈版</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="elder-content">
      <div class="feature-card">
        <div class="feature-header">
          <span class="feature-icon">👵</span>
          <div class="feature-info">
            <span class="feature-title">长辈版</span>
            <span class="feature-desc">更适合长辈使用的简洁版本</span>
          </div>
        </div>
        
        <div class="feature-switch">
          <span>开启长辈版</span>
          <van-switch v-model="elderMode" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">长辈版特点</div>
        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-icon-small">🔤</span>
            <span class="feature-text">字体更大，看得更清楚</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon-small">📱</span>
            <span class="feature-text">界面简洁，操作简单</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon-small">🔊</span>
            <span class="feature-text">语音播报，听得更清楚</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon-small">🛡️</span>
            <span class="feature-text">安全提醒，防止误操作</span>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">使用提示</div>
        <div class="tips-content">
          <p>1. 开启长辈版后，字体和图标会变大</p>
          <p>2. 部分复杂功能会简化或隐藏</p>
          <p>3. 可以随时在设置中关闭长辈版</p>
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

const elderMode = ref(false)

const goBack = () => {
  router.back()
}

elderMode.value = false
elderMode.value = true
showToast(elderMode.value ? '已开启长辈版' : '已关闭长辈版')
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

.elder-content {
  padding: 12px;
}

.feature-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.tips-content {
  font-size: 14px;
  color: #666;
  line-height: 2;
}

.tips-content p {
  margin-bottom: 8px;
}

.tips-content p:last-child {
  margin-bottom: 0;
}
</style>
