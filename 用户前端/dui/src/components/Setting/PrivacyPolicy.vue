<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">隐私政策摘要</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="policy-content">
      <div class="policy-section">
        <div class="section-header" @click="toggleSection('info')">
          <span class="section-icon">📋</span>
          <span class="section-title">信息收集</span>
          <van-icon :name="expandedSections.includes('info') ? 'arrow-up' : 'arrow-down'" size="14" />
        </div>
        <div v-if="expandedSections.includes('info')" class="section-content">
          <p>我们收集您的个人信息是为了向您提供更好的服务。主要包括：</p>
          <ul>
            <li>账户信息：用户名、密码、手机号、邮箱</li>
            <li>设备信息：设备型号、操作系统、IP地址</li>
            <li>行为信息：浏览记录、购买记录、收藏记录</li>
            <li>位置信息：用于提供本地化服务</li>
          </ul>
        </div>
      </div>

      <div class="policy-section">
        <div class="section-header" @click="toggleSection('use')">
          <span class="section-icon">🔑</span>
          <span class="section-title">信息使用</span>
          <van-icon :name="expandedSections.includes('use') ? 'arrow-up' : 'arrow-down'" size="14" />
        </div>
        <div v-if="expandedSections.includes('use')" class="section-content">
          <p>我们将您的信息用于以下目的：</p>
          <ul>
            <li>提供和维护服务</li>
            <li>个性化推荐</li>
            <li>保障交易安全</li>
            <li>改进产品功能</li>
          </ul>
        </div>
      </div>

      <div class="policy-section">
        <div class="section-header" @click="toggleSection('share')">
          <span class="section-icon">🤝</span>
          <span class="section-title">信息共享</span>
          <van-icon :name="expandedSections.includes('share') ? 'arrow-up' : 'arrow-down'" size="14" />
        </div>
        <div v-if="expandedSections.includes('share')" class="section-content">
          <p>我们不会向第三方出售您的个人信息。仅在以下情况下共享：</p>
          <ul>
            <li>获得您的明确同意</li>
            <li>遵守法律法规</li>
            <li>保障用户权益和安全</li>
            <li>与授权合作伙伴共享</li>
          </ul>
        </div>
      </div>

      <div class="policy-section">
        <div class="section-header" @click="toggleSection('rights')">
          <span class="section-icon">✅</span>
          <span class="section-title">您的权利</span>
          <van-icon :name="expandedSections.includes('rights') ? 'arrow-up' : 'arrow-down'" size="14" />
        </div>
        <div v-if="expandedSections.includes('rights')" class="section-content">
          <p>您享有以下权利：</p>
          <ul>
            <li>访问和查看您的个人信息</li>
            <li>更正和更新您的信息</li>
            <li>删除您的个人信息</li>
            <li>撤回同意和注销账户</li>
          </ul>
        </div>
      </div>

      <div class="policy-section">
        <div class="section-header" @click="toggleSection('security')">
          <span class="section-icon">🛡️</span>
          <span class="section-title">安全保护</span>
          <van-icon :name="expandedSections.includes('security') ? 'arrow-up' : 'arrow-down'" size="14" />
        </div>
        <div v-if="expandedSections.includes('security')" class="section-content">
          <p>我们采取多种安全措施保护您的信息：</p>
          <ul>
            <li>数据加密存储</li>
            <li>访问权限控制</li>
            <li>安全审计和监控</li>
            <li>定期安全更新</li>
          </ul>
        </div>
      </div>

      <div class="full-policy">
        <button class="policy-btn" @click="handleAction('full')">查看完整隐私政策</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'

const router = useRouter()

const expandedSections = ref(['info', 'use', 'share', 'rights', 'security'])

const goBack = () => {
  router.back()
}

const toggleSection = (section: string) => {
  const index = expandedSections.value.indexOf(section)
  if (index > -1) {
    expandedSections.value.splice(index, 1)
  } else {
    expandedSections.value.push(section)
  }
}

const handleAction = (_action: string) => {
  showToast('查看完整隐私政策')
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

.policy-content {
  padding: 12px;
}

.policy-section {
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
}

.section-header {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  cursor: pointer;
}

.section-icon {
  font-size: 18px;
  margin-right: 10px;
}

.section-title {
  flex: 1;
  font-size: 15px;
  color: #333;
}

.section-content {
  padding: 0 16px 16px;
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.section-content ul {
  margin-top: 8px;
  padding-left: 20px;
}

.section-content li {
  margin-bottom: 8px;
}

.full-policy {
  padding: 16px;
}

.policy-btn {
  width: 100%;
  padding: 12px;
  background: #f7f8fa;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: #667eea;
}
</style>
