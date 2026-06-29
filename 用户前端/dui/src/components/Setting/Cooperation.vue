<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">合作/免费入驻拼多多</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="cooperation-content">
      <div class="banner-card">
        <div class="banner-icon">🏪</div>
        <div class="banner-text">
          <div class="banner-title">免费入驻拼多多</div>
          <div class="banner-desc">0元开店，轻松创业</div>
        </div>
        <button class="banner-btn">立即入驻</button>
      </div>

      <div class="info-cards">
        <div class="info-card">
          <span class="info-icon">💰</span>
          <span class="info-title">0元入驻</span>
          <span class="info-desc">无需缴纳入驻费用</span>
        </div>
        <div class="info-card">
          <span class="info-icon">🚀</span>
          <span class="info-title">快速开店</span>
          <span class="info-desc">3分钟完成入驻</span>
        </div>
        <div class="info-card">
          <span class="info-icon">📈</span>
          <span class="info-title">海量流量</span>
          <span class="info-desc">亿级用户覆盖</span>
        </div>
      </div>

      <div class="section">
        <div class="section-title">入驻流程</div>
        <div class="process-list">
          <div class="process-item">
            <div class="process-step">1</div>
            <div class="process-info">
              <span class="process-title">提交资料</span>
              <span class="process-desc">填写店铺信息</span>
            </div>
          </div>
          <div class="process-item">
            <div class="process-step">2</div>
            <div class="process-info">
              <span class="process-title">审核通过</span>
              <span class="process-desc">平台审核资质</span>
            </div>
          </div>
          <div class="process-item">
            <div class="process-step">3</div>
            <div class="process-info">
              <span class="process-title">上架商品</span>
              <span class="process-desc">开始经营店铺</span>
            </div>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-title">入驻要求</div>
        <div class="requirement-list">
          <div class="requirement-item">
            <van-icon name="check" size="14" color="#07c160" />
            <span>年满18周岁，持有有效身份证件</span>
          </div>
          <div class="requirement-item">
            <van-icon name="check" size="14" color="#07c160" />
            <span>拥有合法经营资质</span>
          </div>
          <div class="requirement-item">
            <van-icon name="check" size="14" color="#07c160" />
            <span>遵守平台规则和协议</span>
          </div>
        </div>
      </div>

      <div class="contact-section">
        <div class="contact-title">联系招商</div>
        <div class="contact-methods">
          <div class="contact-item" @click="handleAction('phone')">
            <span class="contact-icon">📞</span>
            <span class="contact-info">招商热线</span>
            <span class="contact-value">400-886-0888</span>
          </div>
          <div class="contact-item" @click="handleAction('email')">
            <span class="contact-icon">📧</span>
            <span class="contact-info">招商邮箱</span>
            <span class="contact-value">business@pinduoduo.com</span>
          </div>
        </div>
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
    phone: '招商热线：400-886-0888',
    email: '招商邮箱：business@pinduoduo.com'
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

.cooperation-content {
  padding: 12px;
}

.banner-card {
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  color: white;
  margin-bottom: 12px;
}

.banner-icon {
  font-size: 36px;
  margin-right: 12px;
}

.banner-text {
  flex: 1;
}

.banner-title {
  font-size: 16px;
  font-weight: bold;
}

.banner-desc {
  font-size: 13px;
  opacity: 0.9;
  margin-top: 4px;
}

.banner-btn {
  background: white;
  color: #ff4757;
  border: none;
  border-radius: 20px;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: bold;
}

.info-cards {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.info-card {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.info-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.info-title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.info-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
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

.process-list {
  display: flex;
  justify-content: space-between;
}

.process-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.process-step {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  border-radius: 50%;
  color: white;
  font-size: 14px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.process-info {
  text-align: center;
}

.process-title {
  font-size: 13px;
  color: #333;
  display: block;
}

.process-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.requirement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.requirement-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
}

.contact-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
}

.contact-title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.contact-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.contact-icon {
  font-size: 20px;
}

.contact-info {
  font-size: 14px;
  color: #999;
}

.contact-value {
  flex: 1;
  text-align: right;
  font-size: 14px;
  color: #333;
}
</style>
