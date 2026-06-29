<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">关于拼多多</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="about-content">
      <div class="logo-section">
        <div class="logo-icon">🛒</div>
        <div class="logo-info">
          <span class="app-name">拼多多</span>
          <span class="app-version">版本 6.7.0</span>
        </div>
        <button class="update-btn">
          <span class="update-badge">有新版本可升级</span>
          <van-icon name="arrow" size="14" />
        </button>
      </div>

      <div class="section">
        <div class="setting-item" @click="handleAction('intro')">
          <div class="item-icon-wrap">
            <span class="item-icon">ℹ️</span>
          </div>
          <div class="item-content">
            <span class="item-title">关于我们</span>
            <span class="item-desc">了解拼多多的发展历程和企业文化</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('team')">
          <div class="item-icon-wrap">
            <span class="item-icon">👥</span>
          </div>
          <div class="item-content">
            <span class="item-title">团队介绍</span>
            <span class="item-desc">认识拼多多核心团队</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('join')">
          <div class="item-icon-wrap">
            <span class="item-icon">💼</span>
          </div>
          <div class="item-content">
            <span class="item-title">加入我们</span>
            <span class="item-desc">查看招聘信息</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('contact')">
          <div class="item-icon-wrap">
            <span class="item-icon">📞</span>
          </div>
          <div class="item-content">
            <span class="item-title">联系我们</span>
            <span class="item-desc">获取联系方式</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <div class="section">
        <div class="setting-item" @click="handleAction('privacy')">
          <div class="item-icon-wrap">
            <span class="item-icon">🔐</span>
          </div>
          <div class="item-content">
            <span class="item-title">隐私政策</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('terms')">
          <div class="item-icon-wrap">
            <span class="item-icon">📄</span>
          </div>
          <div class="item-content">
            <span class="item-title">用户协议</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('copyright')">
          <div class="item-icon-wrap">
            <span class="item-icon">©️</span>
          </div>
          <div class="item-content">
            <span class="item-title">知识产权声明</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <div class="cert-section">
        <div class="cert-item">
          <span class="cert-icon">✅</span>
          <span class="cert-text">ICP备案号：沪ICP备12345678号</span>
        </div>
        <div class="cert-item">
          <span class="cert-icon">✅</span>
          <span class="cert-text">增值电信业务经营许可证</span>
        </div>
        <div class="cert-item">
          <span class="cert-icon">✅</span>
          <span class="cert-text">网络安全等级保护三级认证</span>
        </div>
      </div>

      <div class="copyright-text">
        © 2024 拼多多. All rights reserved.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { showToast } from 'vant'
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    intro: '关于我们',
    team: '团队介绍',
    join: '加入我们',
    contact: '联系我们',
    privacy: '隐私政策',
    terms: '用户协议',
    copyright: '知识产权声明'
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

.about-content {
  padding: 12px;
}

.logo-section {
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  color: white;
  margin-bottom: 12px;
}

.logo-icon {
  font-size: 48px;
  margin-right: 16px;
}

.logo-info {
  flex: 1;
}

.app-name {
  font-size: 20px;
  font-weight: bold;
  display: block;
}

.app-version {
  font-size: 13px;
  opacity: 0.9;
  margin-top: 4px;
}

.update-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  color: white;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.update-badge {
  background: white;
  color: #ff4757;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
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
  cursor: pointer;
}

.setting-item:last-child {
  border-bottom: none;
}

.item-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.item-icon {
  font-size: 18px;
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

.cert-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
}

.cert-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.cert-item:last-child {
  margin-bottom: 0;
}

.cert-icon {
  font-size: 16px;
}

.copyright-text {
  text-align: center;
  font-size: 12px;
  color: #999;
  padding: 24px;
}
</style>
