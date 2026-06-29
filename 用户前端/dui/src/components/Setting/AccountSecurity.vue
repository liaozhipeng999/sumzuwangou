<template>
  <div class="security-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">账号与安全</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="setting-item" @click="handleAction('phone')">
          <div class="item-icon-wrap">
            <span class="item-icon">📱</span>
          </div>
          <div class="item-content">
            <span class="item-title">绑定手机号</span>
            <span class="item-desc">当前绑定：138****8888</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('wechat')">
          <div class="item-icon-wrap">
            <span class="item-icon">💬</span>
          </div>
          <div class="item-content">
            <span class="item-title">绑定微信</span>
            <span class="item-desc">czc</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('qq')">
          <div class="item-icon-wrap">
            <span class="item-icon">🐧</span>
          </div>
          <div class="item-content">
            <span class="item-title">绑定QQ</span>
            <span class="item-desc">未绑定</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">密码安全</div>
        
        <div class="setting-item" @click="handleAction('modifyPwd')">
          <div class="item-icon-wrap">
            <span class="item-icon">🔑</span>
          </div>
          <div class="item-content">
            <span class="item-title">修改登录密码</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('payPwd')">
          <div class="item-icon-wrap">
            <span class="item-icon">💳</span>
          </div>
          <div class="item-content">
            <span class="item-title">支付密码</span>
            <span class="item-desc">已设置</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">安全设置</div>
        
        <div class="setting-item">
          <div class="item-icon-wrap">
            <span class="item-icon">🔔</span>
          </div>
          <div class="item-content">
            <span class="item-title">登录提醒</span>
            <span class="item-desc">登录新设备时发送提醒</span>
          </div>
          <van-switch v-model="loginRemind" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-icon-wrap">
            <span class="item-icon">🔓</span>
          </div>
          <div class="item-content">
            <span class="item-title">免密登录</span>
            <span class="item-desc">在当前设备上可免密登录</span>
          </div>
          <van-switch v-model="autoLogin" size="24" />
        </div>

        <div class="setting-item" @click="handleAction('devices')">
          <div class="item-icon-wrap">
            <span class="item-icon">📱</span>
          </div>
          <div class="item-content">
            <span class="item-title">登录设备管理</span>
            <span class="item-desc">当前2台设备在线</span>
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

const loginRemind = ref(true)
const autoLogin = ref(false)

const goBack = () => {
  router.back()
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    phone: '绑定手机号',
    wechat: '绑定微信',
    qq: '绑定QQ',
    modifyPwd: '修改登录密码',
    payPwd: '支付密码',
    devices: '登录设备管理'
  }
  
  showToast(actionMap[action] || action)
}
</script>

<style scoped>
.security-page {
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
</style>
