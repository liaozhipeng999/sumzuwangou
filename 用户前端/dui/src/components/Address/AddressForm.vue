<template>
  <van-popup
    :show="visible"
    position="bottom"
    round
    :style="{ height: '85%' }"
    @close="handleClose"
  >
    <div class="address-form">
      <!-- 标题 -->
      <div class="form-header">
        <span class="form-title">{{ isEdit ? '修改收货地址' : '添加收货地址' }}</span>
        <van-icon 
          name="cross" 
          class="close-btn" 
          size="20" 
          @click="handleClose"
        />
      </div>
      
      <div class="form-content">
        <!-- 收货人 -->
        <div class="form-item">
          <label class="form-label">收货人</label>
          <div class="input-wrapper">
            <input 
              v-model="form.receiver_name" 
              type="text" 
              class="form-input"
              placeholder="收货人姓名"
            />
          </div>
          <!-- 历史记录下拉 -->
          <div v-if="showNameSuggestions" class="suggestions">
            <div 
              v-for="item in historyNames" 
              :key="item"
              class="suggestion-item"
              @click="selectHistoryName(item)"
            >{{ item }}</div>
          </div>
        </div>
        
        <!-- 手机号 -->
        <div class="form-item">
          <label class="form-label">手机号</label>
          <input 
            v-model="form.receiver_phone" 
            type="tel" 
            class="form-input"
            placeholder="请输入手机号"
          />
        </div>
        
        <!-- 地区选择 -->
        <div class="form-item">
          <label class="form-label">地区</label>
          <div 
            class="region-select" 
            @click="showRegionPicker = true"
          >
            <span class="region-value">
              {{ form.province || '请选择省市区' }}
            </span>
            <van-icon name="arrow-right" size="16" color="#999" />
          </div>
        </div>
        
        <!-- 详细地址 -->
        <div class="form-item">
          <label class="form-label">详细地址</label>
          <div class="input-wrapper">
            <input 
              v-model="form.detail_address" 
              type="text" 
              class="form-input detail-input"
              placeholder="如街道、门牌号、小区、乡镇、村等"
            />
            <van-icon 
              name="map-pin" 
              class="location-btn" 
              size="20" 
              color="#ee0a24"
              @click="handleLocation"
            />
          </div>
        </div>
        
        <!-- 设置默认地址 -->
        <div class="form-item">
          <label class="form-label">默认地址</label>
          <van-switch 
            v-model="form.is_default" 
            size="24"
            active-color="#ee0a24"
          />
        </div>
      </div>
      
      <!-- 保存按钮 -->
      <div class="form-footer">
        <van-button 
          class="save-btn" 
          type="primary" 
          block
          @click="handleSave"
          :loading="isSaving"
        >保存</van-button>
      </div>
    </div>
    
    <!-- 地区选择器 -->
    <van-popup
      v-model:show="showRegionPicker"
      position="bottom"
      round
      :style="{ height: '50%' }"
    >
      <van-area
        :area-list="areaList"
        @confirm="handleRegionConfirm"
        @cancel="showRegionPicker = false"
      />
    </van-popup>
  </van-popup>
  
  <!-- 加载提示 -->
  <van-loading 
    v-if="isLocating" 
    class="location-loading"
  >定位中...</van-loading>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { 
  Popup as VanPopup, 
  Icon as VanIcon, 
  Switch as VanSwitch, 
  Button as VanButton,
  Area as VanArea,
  Loading as VanLoading,
  showToast
} from 'vant'
import type { AddressItem, CreateAddressRequest, UpdateAddressRequest } from '@/api/address'
import { areaList } from '@/data/area-list'

interface Props {
  visible: boolean
  address?: AddressItem | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'save', data: CreateAddressRequest | UpdateAddressRequest): void
}>()

const isEdit = computed(() => !!props.address)

const form = reactive({
  receiver_name: '',
  receiver_phone: '',
  province: '',
  city: '',
  district: '',
  detail_address: '',
  is_default: false
})

const regionValue = ref('')
const showRegionPicker = ref(false)
const showNameSuggestions = ref(false)
const isSaving = ref(false)
const isLocating = ref(false)

// 历史姓名记录（模拟）
const historyNames = computed(() => {
  return [
    'czc, 13680835826',
    '廖智鹏, 13680835826',
    '廖健勇, 13005719950'
  ]
})

// 监听visible变化，初始化表单
watch(() => props.visible, (val) => {
  if (val) {
    if (props.address) {
      // 编辑模式，填充数据
      form.receiver_name = props.address.receiver_name
      form.receiver_phone = props.address.receiver_phone
      form.province = props.address.province
      form.city = props.address.city
      form.district = props.address.district
      form.detail_address = props.address.detail_address
      form.is_default = props.address.is_default === 1
      regionValue.value = `${props.address.province}/${props.address.city}/${props.address.district}`
    } else {
      // 新增模式，重置表单
      form.receiver_name = ''
      form.receiver_phone = ''
      form.province = ''
      form.city = ''
      form.district = ''
      form.detail_address = ''
      form.is_default = false
      regionValue.value = ''
    }
  }
})

// 监听收货人输入，显示建议
watch(() => form.receiver_name, (val) => {
  showNameSuggestions.value = val.length > 0 && historyNames.value.some(h => h.includes(val))
})

// 选择历史姓名
const selectHistoryName = (item: string) => {
  const [name, phone] = item.split(', ')
  form.receiver_name = name
  form.receiver_phone = phone
  showNameSuggestions.value = false
}

// 地区选择确认
const handleRegionConfirm = (result: { selectedOptions: Array<{ text: string; value: string }> }) => {
  const options = result.selectedOptions
  if (options && options.length >= 3) {
    form.province = options[0].text
    form.city = options[1].text
    form.district = options[2].text
  }
  showRegionPicker.value = false
}

// 获取定位
const handleLocation = () => {
  isLocating.value = true
  
  if ('geolocation' in navigator) {
    navigator.geolocation.getCurrentPosition(
      (_position) => {
        // 这里可以调用逆地理编码API获取地址
        // 模拟获取到地址
        form.province = '广东省'
        form.city = '广州市'
        form.district = '增城区'
        form.detail_address = '根据定位自动填充地址'
        regionValue.value = '广东省/广州市/增城区'
        isLocating.value = false
        showToast('定位成功')
      },
      (error) => {
        console.error('定位失败:', error)
        isLocating.value = false
        showToast('定位失败，请手动输入地址')
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0
      }
    )
  } else {
    isLocating.value = false
    showToast('您的浏览器不支持定位')
  }
}

// 保存地址
const handleSave = () => {
  // 表单验证
  if (!form.receiver_name.trim()) {
    showToast('请输入收货人姓名')
    return
  }
  if (!form.receiver_phone.trim()) {
    showToast('请输入手机号')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(form.receiver_phone)) {
    showToast('请输入正确的手机号')
    return
  }
  if (!form.province) {
    showToast('请选择省市区')
    return
  }
  if (!form.detail_address.trim()) {
    showToast('请输入详细地址')
    return
  }
  
  isSaving.value = true
  
  const data: CreateAddressRequest | UpdateAddressRequest = {
    receiver_name: form.receiver_name.trim(),
    receiver_phone: form.receiver_phone.trim(),
    province: form.province,
    city: form.city,
    district: form.district,
    detail_address: form.detail_address.trim(),
    is_default: form.is_default ? 1 : 0
  }
  
  if (props.address) {
    ;(data as UpdateAddressRequest).id = props.address.id
  }
  
  setTimeout(() => {
    isSaving.value = false
    emit('save', data)
    handleClose()
  }, 500)
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.address-form {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
  position: relative;
}

.form-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  position: absolute;
  right: 16px;
  color: #999;
  cursor: pointer;
}

.form-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.form-item {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  height: 44px;
  padding: 0 12px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #ee0a24;
}

.detail-input {
  padding-right: 48px;
}

.input-wrapper {
  position: relative;
}

.location-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
}

.region-select {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  padding: 0 12px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  cursor: pointer;
}

.region-value {
  font-size: 14px;
  color: #333;
}

.suggestions {
  margin-top: 8px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  background: #fff;
}

.suggestion-item {
  padding: 10px 12px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-item:active {
  background: #f5f5f5;
}

.form-footer {
  padding: 16px;
  border-top: 1px solid #f5f5f5;
}

.save-btn {
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
}

.location-loading {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
}

/* 改善桌面端 picker 滚动体验 */
:deep(.van-picker-column) {
  overflow-y: auto;
}

:deep(.van-picker-column__item) {
  cursor: pointer;
}
</style>