<template>
  <div class="notification-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">消息接收设置</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="setting-list">
      <div class="section">
        <div class="setting-item">
          <div class="item-icon-wrap">
            <span class="item-icon">🔔</span>
          </div>
          <div class="item-content">
            <span class="item-title">接收消息通知</span>
            <span class="item-desc">关闭后将不再接收任何消息</span>
          </div>
          <van-switch v-model="enableNotification" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">交易物流</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">订单消息</span>
            <span class="item-desc">订单状态变更、发货、退款等通知</span>
          </div>
          <van-switch v-model="orderNotification" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">物流消息</span>
            <span class="item-desc">包裹揽收、运输、派送、签收等通知</span>
          </div>
          <van-switch v-model="logisticsNotification" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">互动消息</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">好友消息</span>
            <span class="item-desc">拼小圈好友互动、评论、点赞等通知</span>
          </div>
          <van-switch v-model="socialNotification" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">客服消息</span>
            <span class="item-desc">官方客服、专属客服消息通知</span>
          </div>
          <van-switch v-model="serviceNotification" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">活动消息</span>
            <span class="item-desc">促销活动、优惠券到期等通知</span>
          </div>
          <van-switch v-model="activityNotification" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">系统消息</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">系统通知</span>
            <span class="item-desc">账号安全、系统公告等重要通知</span>
          </div>
          <van-switch v-model="systemNotification" size="24" />
        </div>
      </div>

      <div class="section">
        <div class="section-title">提醒方式</div>
        
        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">声音提醒</span>
            <span class="item-desc">收到消息时播放提示音</span>
          </div>
          <van-switch v-model="soundRemind" size="24" />
        </div>

        <div class="setting-item">
          <div class="item-content">
            <span class="item-title">震动提醒</span>
            <span class="item-desc">收到消息时震动提醒</span>
          </div>
          <van-switch v-model="vibrateRemind" size="24" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const enableNotification = ref(true)
const orderNotification = ref(true)
const logisticsNotification = ref(true)
const socialNotification = ref(true)
const serviceNotification = ref(true)
const activityNotification = ref(false)
const systemNotification = ref(true)
const soundRemind = ref(true)
const vibrateRemind = ref(false)

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.notification-page {
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
