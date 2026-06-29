<template>
  <div class="address-page">
    <!-- 头部 -->
    <van-nav-bar 
      title="收货地址" 
      left-arrow 
      @click-left="handleBack"
    />
    
    <!-- 地址列表 -->
    <div class="address-list" v-if="addressList.length > 0">
      <AddressItem
        v-for="address in addressList"
        :key="address.id"
        :address="address"
        @use="handleUse"
        @edit="handleEdit"
        @delete="handleDelete"
        @set-default="handleSetDefault"
        @copy="handleCopy"
        @top="handleTop"
      />
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-state">
      <van-icon name="location-o" size="64" color="#ccc" />
      <p class="empty-text">暂无收货地址</p>
    </div>
    
    <!-- 添加地址按钮 -->
    <div class="add-address-btn-wrapper">
      <van-button 
        class="add-address-btn" 
        type="primary" 
        block
        round
        @click="showAddForm = true"
      >
        <van-icon name="plus" size="18" />
        <span>添加收货地址</span>
      </van-button>
    </div>
    
    <!-- 添加/编辑地址表单 -->
    <AddressForm
      v-model:visible="showAddForm"
      :address="editingAddress"
      @save="handleSaveAddress"
    />
    
    <!-- 确认删除弹窗 -->
    <van-dialog
      v-model:show="showDeleteConfirm"
      title="提示"
      message="确定要删除该地址吗？"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  NavBar as VanNavBar, 
  Icon as VanIcon, 
  Button as VanButton,
  Dialog as VanDialog,
  showToast
} from 'vant'
import { useUserStore } from '@/stores/user'
import AddressItem from './AddressItem.vue'
import AddressForm from './AddressForm.vue'
import type { AddressItem as AddressItemType, CreateAddressRequest, UpdateAddressRequest } from '@/api/address'
import { getAddressList, createAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const userStore = useUserStore()
const getUserId = () => userStore.userInfo?.id || 1

const addressList = ref<AddressItemType[]>([])
const showAddForm = ref(false)
const editingAddress = ref<AddressItemType | null>(null)
const showDeleteConfirm = ref(false)
const deleteId = ref<number>(0)

// 加载地址列表
const loadAddressList = async () => {
  try {
    const response = await getAddressList(getUserId())
    if (response.code === 200) {
      addressList.value = response.data as AddressItemType[]
    } else {
      addressList.value = []
    }
  } catch (error) {
    console.error('加载地址列表失败:', error)
    addressList.value = []
  }
}

// 返回
const handleBack = () => {
  window.history.back()
}

// 使用地址
const handleUse = (address: AddressItemType) => {
  showToast(`已选择地址: ${address.receiver_name}`)
}

// 编辑地址
const handleEdit = (address: AddressItemType) => {
  editingAddress.value = address
  showAddForm.value = true
}

// 删除地址（显示确认弹窗）
const handleDelete = (id: number) => {
  deleteId.value = id
  showDeleteConfirm.value = true
}

// 确认删除
const confirmDelete = async () => {
  try {
    const response = await deleteAddress(deleteId.value)
    if (response.code === 200) {
      addressList.value = addressList.value.filter(a => a.id !== deleteId.value)
      showToast('删除成功')
    } else {
      showToast('删除失败')
    }
  } catch (error) {
    console.error('删除地址失败:', error)
    showToast('删除失败')
  }
  showDeleteConfirm.value = false
}

// 设置默认地址
const handleSetDefault = async (id: number) => {
  try {
    const response = await setDefaultAddress(id, getUserId())
    if (response.code === 200) {
      addressList.value.forEach(a => {
        a.is_default = a.id === id ? 1 : 0
      })
      showToast('设置成功')
    } else {
      showToast('设置失败')
    }
  } catch (error) {
    console.error('设置默认地址失败:', error)
    showToast('设置失败')
  }
}

// 复制地址
const handleCopy = (address: AddressItemType) => {
  const fullAddress = `${address.province}${address.city}${address.district}${address.detail_address}`
  if (navigator.clipboard) {
    navigator.clipboard.writeText(fullAddress).then(() => {
      showToast('复制成功')
    }).catch(() => {
      showToast('复制失败')
    })
  } else {
    showToast('您的浏览器不支持复制')
  }
}

// 置顶地址
const handleTop = (address: AddressItemType) => {
  const index = addressList.value.findIndex(a => a.id === address.id)
  if (index > 0) {
    const [removed] = addressList.value.splice(index, 1)
    addressList.value.unshift(removed)
    showToast('已置顶')
  }
}

// 保存地址
const handleSaveAddress = async (data: CreateAddressRequest | UpdateAddressRequest) => {
  try {
    // 加上 userId
    const payload = { ...data, userId: getUserId() }
    if ('id' in data) {
      // 更新地址
      const response = await updateAddress(payload as UpdateAddressRequest)
      if (response.code === 200) {
        const index = addressList.value.findIndex(a => a.id === data.id)
        if (index !== -1) {
          addressList.value[index] = {
            ...addressList.value[index],
            receiver_name: data.receiver_name,
            receiver_phone: data.receiver_phone,
            province: data.province,
            city: data.city,
            district: data.district,
            detail_address: data.detail_address,
            is_default: data.is_default || 0,
            updated_at: new Date().toISOString()
          }
        }
        showToast('更新成功')
      } else {
        showToast('更新失败')
      }
    } else {
      // 创建地址
      const response = await createAddress(payload as CreateAddressRequest)
      if (response.code === 200 && response.data) {
        addressList.value.unshift(response.data as AddressItemType)
        showToast('添加成功')
      } else {
        showToast('添加失败')
      }
    }
  } catch (error) {
    console.error('保存地址失败:', error)
    showToast('保存失败')
  }
  editingAddress.value = null
}

// 组件挂载时加载地址列表
onMounted(() => {
  loadAddressList()
})
</script>

<style scoped>
.address-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.address-list {
  padding: 10px;
  padding-bottom: 100px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-text {
  margin-top: 16px;
  font-size: 14px;
  color: #999;
}

.add-address-btn-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: #fff;
  border-top: 1px solid #f5f5f5;
}

.add-address-btn {
  height: 48px;
  font-size: 16px;
}
</style>