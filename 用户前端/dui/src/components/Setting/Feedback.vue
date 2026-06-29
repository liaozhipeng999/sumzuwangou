<template>
  <div class="setting-sub-page">
    <div class="page-header">
      <van-icon name="arrow-left" class="back-btn" @click="goBack" />
      <span class="header-title">意见反馈</span>
      <div class="header-placeholder"></div>
    </div>

    <div class="feedback-content">
      <div class="feedback-form">
        <div class="form-item">
          <label class="form-label">反馈类型</label>
          <van-picker 
            :columns="feedbackTypes" 
            v-model="selectedType"
            @confirm="onTypeConfirm"
          />
          <div class="picker-value" @click="showTypePicker = true">
            {{ feedbackTypes.find(t => t.value === selectedType[0])?.text || '请选择反馈类型' }}
            <van-icon name="arrow" size="14" />
          </div>
        </div>

        <div class="form-item">
          <label class="form-label">问题描述</label>
          <textarea 
            v-model="feedbackContent"
            class="feedback-textarea"
            placeholder="请详细描述您遇到的问题或建议..."
            maxlength="500"
          ></textarea>
          <div class="text-count">{{ feedbackContent.length }}/500</div>
        </div>

        <div class="form-item">
          <label class="form-label">联系方式</label>
          <input 
            v-model="contactInfo"
            class="contact-input"
            placeholder="选填，方便我们联系您"
          />
        </div>

        <button class="submit-btn" @click="submitFeedback">
          提交反馈
        </button>
      </div>

      <div class="contact-info">
        <div class="contact-title">联系客服</div>
        <div class="contact-options">
          <div class="contact-item" @click="handleAction('online')">
            <span class="contact-icon">💬</span>
            <span class="contact-name">在线客服</span>
            <van-icon name="arrow" size="14" />
          </div>
          <div class="contact-item" @click="handleAction('phone')">
            <span class="contact-icon">📞</span>
            <span class="contact-name">客服热线</span>
            <van-icon name="arrow" size="14" />
          </div>
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

const feedbackTypes = [
  { text: '功能建议', value: 0 },
  { text: 'Bug反馈', value: 1 },
  { text: '账号问题', value: 2 },
  { text: '订单问题', value: 3 },
  { text: '支付问题', value: 4 },
  { text: '其他', value: 5 }
]
const selectedType = ref([0])
const feedbackContent = ref('')
const contactInfo = ref('')
const showTypePicker = ref(false)

const goBack = () => {
  router.back()
}

const onTypeConfirm = (value: number[]) => {
  selectedType.value = value
  showTypePicker.value = false
}

const submitFeedback = () => {
  if (!feedbackContent.value.trim()) {
    showToast('请输入问题描述')
    return
  }
  
  showToast('反馈已提交，感谢您的意见！')
  feedbackContent.value = ''
  contactInfo.value = ''
}

const handleAction = (action: string) => {
  const actionMap: Record<string, string> = {
    online: '在线客服',
    phone: '客服热线'
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

.feedback-content {
  padding: 12px;
}

.feedback-form {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
}

.form-item {
  margin-bottom: 16px;
}

.form-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  display: block;
}

.picker-value {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
}

.feedback-textarea {
  width: 100%;
  height: 120px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  border: none;
  outline: none;
  box-sizing: border-box;
}

.text-count {
  text-align: right;
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.contact-input {
  width: 100%;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  font-size: 14px;
  border: none;
  outline: none;
  box-sizing: border-box;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: bold;
  margin-top: 8px;
}

.contact-info {
  background: white;
  border-radius: 12px;
  padding: 16px;
}

.contact-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
}

.contact-options {
  display: flex;
  gap: 12px;
}

.contact-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  cursor: pointer;
}

.contact-icon {
  font-size: 18px;
}

.contact-name {
  font-size: 14px;
  color: #333;
}
</style>
