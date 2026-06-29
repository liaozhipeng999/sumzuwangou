<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">国家与地区/语言</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="section-title">语言</div>
        
        <div 
          class="language-item" 
          v-for="lang in languages" 
          :key="lang.key"
          @click="selectLanguage(lang.key)"
        >
          <div class="language-content">
            <span class="language-name">{{ lang.name }}</span>
            <span class="language-native">{{ lang.native }}</span>
          </div>
          <van-icon v-if="selectedLanguage === lang.key" name="success" size="16" color="#ff4757" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">国家/地区</div>
        
        <div 
          class="language-item" 
          v-for="region in regions" 
          :key="region.key"
          @click="selectRegion(region.key)"
        >
          <div class="language-content">
            <span class="language-name">{{ region.name }}</span>
            <span class="language-native">{{ region.code }}</span>
          </div>
          <van-icon v-if="selectedRegion === region.key" name="success" size="16" color="#ff4757" />
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

const selectedLanguage = ref('zh-CN')
const selectedRegion = ref('CN')

const languages = [
  { key: 'zh-CN', name: '简体中文', native: '简体中文' },
  { key: 'zh-TW', name: '繁体中文', native: '繁體中文' },
  { key: 'en', name: 'English', native: 'English' },
  { key: 'ja', name: '日本語', native: '日本語' },
  { key: 'ko', name: '한국어', native: '한국어' }
]

const regions = [
  { key: 'CN', name: '中国大陆', code: 'CN' },
  { key: 'HK', name: '中国香港', code: 'HK' },
  { key: 'TW', name: '中国台湾', code: 'TW' },
  { key: 'US', name: '美国', code: 'US' },
  { key: 'JP', name: '日本', code: 'JP' }
]

const goBack = () => {
  router.back()
}

const selectLanguage = (key: string) => {
  selectedLanguage.value = key
  showToast('语言已切换')
}

const selectRegion = (key: string) => {
  selectedRegion.value = key
  showToast('地区已切换')
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

.language-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.language-item:last-child {
  border-bottom: none;
}

.language-content {
  flex: 1;
}

.language-name {
  font-size: 15px;
  color: #333;
  display: block;
}

.language-native {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
</style>
