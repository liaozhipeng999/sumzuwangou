<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">拼小圈设置</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">开启拼小圈</span>
            <span class="item-desc">分享购物动态，发现好友好物</span>
          </div>
          <van-switch v-model="enableSocial" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">隐私设置</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">允许好友查看我的动态</span>
          </div>
          <van-switch v-model="allowFriendView" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">允许陌生人查看我的动态</span>
          </div>
          <van-switch v-model="allowStrangerView" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">自动分享商品</span>
            <span class="item-desc">购买商品后自动分享到拼小圈</span>
          </div>
          <van-switch v-model="autoShare" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="setting-item" @click="handleAction('friends')">
          <div class="item-icon-wrap">
            <span class="item-icon">👥</span>
          </div>
          <div class="item-content">
            <span class="item-title">好友管理</span>
            <span class="item-desc">管理拼小圈好友</span>
          </div>
          <van-icon name="arrow" size="14" />
        </div>

        <div class="setting-item" @click="handleAction('block')">
          <div class="item-icon-wrap">
            <span class="item-icon">🚫</span>
          </div>
          <div class="item-content">
            <span class="item-title">黑名单</span>
            <span class="item-desc">管理屏蔽的用户</span>
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

const enableSocial = ref(true)
const allowFriendView = ref(true)
const allowStrangerView = ref(false)
const autoShare = ref(false)

const goBack = () => {
  router.back()
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    friends: '好友管理',
    block: '黑名单'
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
