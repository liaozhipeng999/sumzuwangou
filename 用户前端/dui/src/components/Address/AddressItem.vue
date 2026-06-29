<template>
  <div class="address-item">
    <!-- 头部：姓名和电话 -->
    <div class="address-header">
      <span class="receiver-name">{{ address.receiver_name }}</span>
      <span class="receiver-phone">{{ address.receiver_phone }}</span>
      <van-icon 
        name="cross" 
        class="delete-btn" 
        size="20" 
        color="#999"
        @click="handleDelete"
      />
    </div>
    
    <!-- 地址内容 -->
    <div class="address-content">
      <span class="address-text">
        {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail_address }}
      </span>
      <van-button 
        class="use-btn" 
        size="small" 
        type="primary" 
        plain
        @click="handleUse"
      >使用</van-button>
    </div>
    
    <!-- 底部操作栏 -->
    <div class="address-footer">
      <label class="default-checkbox">
        <van-checkbox 
          v-model="isDefaultChecked" 
          shape="round"
          @change="handleSetDefault"
        />
        <span>默认</span>
      </label>
      <div class="actions">
        <span class="action-btn" @click="handleTop">置顶</span>
        <span class="action-divider">|</span>
        <span class="action-btn" @click="handleCopy">复制</span>
        <span class="action-divider">|</span>
        <span class="action-btn" @click="handleEdit">修改</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Checkbox as VanCheckbox, Button as VanButton, Icon as VanIcon } from 'vant'
import type { AddressItem as AddressItemType } from '@/api/address'

interface Props {
  address: AddressItemType
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'use', address: AddressItemType): void
  (e: 'edit', address: AddressItemType): void
  (e: 'delete', id: number): void
  (e: 'setDefault', id: number): void
  (e: 'copy', address: AddressItemType): void
  (e: 'top', address: AddressItemType): void
}>()

const isDefaultChecked = ref(props.address.is_default === 1)

// 监听地址变化，更新默认状态
watch(() => props.address, (newAddress) => {
  isDefaultChecked.value = newAddress.is_default === 1
}, { deep: true })

// 使用地址
const handleUse = () => {
  emit('use', props.address)
}

// 修改地址
const handleEdit = () => {
  emit('edit', props.address)
}

// 删除地址
const handleDelete = () => {
  emit('delete', props.address.id)
}

// 设置默认地址
const handleSetDefault = () => {
  if (isDefaultChecked.value) {
    emit('setDefault', props.address.id)
  }
}

// 复制地址
const handleCopy = () => {
  const fullAddress = `${props.address.province}${props.address.city}${props.address.district}${props.address.detail_address}`
  if (navigator.clipboard) {
    navigator.clipboard.writeText(fullAddress).then(() => {
      // 可以添加提示
    })
  }
  emit('copy', props.address)
}

// 置顶
const handleTop = () => {
  emit('top', props.address)
}
</script>

<style scoped>
.address-item {
  background: #fff;
  padding: 16px;
  margin-bottom: 10px;
  border-radius: 8px;
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-right: 12px;
}

.receiver-phone {
  font-size: 14px;
  color: #666;
  flex: 1;
}

.delete-btn {
  cursor: pointer;
  padding: 4px;
}

.address-content {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.address-text {
  flex: 1;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.use-btn {
  flex-shrink: 0;
  margin-left: 12px;
  padding: 4px 12px;
}

.address-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.default-checkbox {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.default-checkbox span {
  margin-left: 6px;
}

.actions {
  display: flex;
  align-items: center;
}

.action-btn {
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.action-btn:active {
  color: #ee0a24;
}

.action-divider {
  margin: 0 8px;
  color: #e0e0e0;
}
</style>