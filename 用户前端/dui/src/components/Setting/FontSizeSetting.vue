<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">字体大小设置</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="preview-section">
          <div class="preview-title">预览文字大小</div>
          <div class="preview-text">这是一段预览文字，用于展示当前字体大小效果。</div>
        </div>

        <div class="font-size-options">
          <div 
            class="font-option" 
            v-for="size in fontSizes" 
            :key="size.key"
            :class="{ active: selectedSize === size.key }"
            @click="selectFontSize(size.key)"
          >
            <span class="option-label">{{ size.label }}</span>
            <span class="option-preview" :class="size.key">{{ size.preview }}</span>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">跟随系统字体</span>
            <span class="item-desc">使用系统设置的字体大小</span>
          </div>
          <van-switch v-model="followSystem" size="24" />
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

const selectedSize = ref('medium')
const followSystem = ref(false)

const fontSizes = [
  { key: 'small', label: '小', preview: '小' },
  { key: 'medium', label: '标准', preview: '标' },
  { key: 'large', label: '大', preview: '大' },
  { key: 'xlarge', label: '特大', preview: '特' }
]

const goBack = () => {
  router.back()
}

const selectFontSize = (key: string) => {
  selectedSize.value = key
  showToast('字体大小已更新')
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

.setting-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
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

.preview-section {
  padding: 16px;
  text-align: center;
}

.preview-title {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

.preview-text {
  font-size: 15px;
  color: #333;
  line-height: 1.6;
}

.font-size-options {
  display: flex;
  justify-content: space-around;
  padding: 16px;
}

.font-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 20px;
  border-radius: 10px;
  background: #f7f8fa;
  transition: all 0.3s;
}

.font-option.active {
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  color: white;
}

.option-label {
  font-size: 14px;
  margin-bottom: 8px;
}

.option-preview {
  font-weight: bold;
}

.option-preview.small {
  font-size: 16px;
}

.option-preview.medium {
  font-size: 20px;
}

.option-preview.large {
  font-size: 24px;
}

.option-preview.xlarge {
  font-size: 28px;
}

.font-option.active .option-preview {
  color: white;
}
</style>
