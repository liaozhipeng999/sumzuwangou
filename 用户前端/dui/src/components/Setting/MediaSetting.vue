<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">直播、照片、视频和通话</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="section-title">直播设置</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">自动播放直播</span>
            <span class="item-desc">进入直播间自动播放</span>
          </div>
          <van-switch v-model="autoPlayLive" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">移动网络下自动播放</span>
            <span class="item-desc">使用流量自动播放直播</span>
          </div>
          <van-switch v-model="autoPlayMobile" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">视频设置</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">自动播放视频</span>
            <span class="item-desc">列表页视频自动播放</span>
          </div>
          <van-switch v-model="autoPlayVideo" size="24" />
        </div>

        <div class="setting-item" @click="handleAction('quality')">
          <div class="item-content">
            <span class="item-title">视频画质</span>
            <span class="item-desc">自动</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">照片设置</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">保存原图</span>
            <span class="item-desc">保存图片时保留原图质量</span>
          </div>
          <van-switch v-model="saveOriginal" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">通话设置</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">允许语音通话</span>
            <span class="item-desc">与商家语音通话</span>
          </div>
          <van-switch v-model="allowVoiceCall" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">允许视频通话</span>
            <span class="item-desc">与商家视频通话</span>
          </div>
          <van-switch v-model="allowVideoCall" size="24" />
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

const autoPlayLive = ref(true)
const autoPlayMobile = ref(false)
const autoPlayVideo = ref(true)
const saveOriginal = ref(true)
const allowVoiceCall = ref(true)
const allowVideoCall = ref(false)

const goBack = () => {
  router.back()
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    quality: '视频画质设置'
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
</style>
