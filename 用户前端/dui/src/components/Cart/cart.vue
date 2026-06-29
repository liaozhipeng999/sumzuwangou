<template>
  <div class="cart">
    <van-nav-bar title="购物车" />

    <van-pull-refresh v-model="refreshing" @refresh="loadCart">
      <!-- 购物车列表 -->
      <div class="cart-list">
        <van-empty v-if="!loading && cartItems.length === 0" description="购物车空空如也" />

        <div v-for="item in cartItems" :key="item.id" class="cart-item-wrap">
          <div class="cart-item">
            <van-checkbox
              v-model="item.selected"
              :true-value="1"
              :false-value="0"
              @change="(val: number) => onItemSelectChange(item.id, val)"
            />
            <van-card
              :price="item.price"
              :title="item.productName"
              :thumb="item.productImage || defaultImage"
              @click-thumb="goProductDetail(item.productId)"
            >
              <template #footer>
                <van-stepper
                  v-model="item.quantity"
                  :min="1"
                  :max="99"
                  @change="(value: number) => onQuantityChange(item.id, value)"
                />
              </template>
            </van-card>
            <van-icon 
              name="delete-o" 
              size="20" 
              color="#ee0a24" 
              class="delete-icon"
              @click="removeItem(item.id)"
            />
          </div>
        </div>
      </div>
    </van-pull-refresh>

    <!-- 底部结算栏 -->
    <van-submit-bar
      v-if="cartItems.length > 0"
      :price="totalPrice"
      button-text="结算"
      @submit="onSubmit"
      class="submit-bar"
    >
      <van-checkbox v-model="allChecked" @change="toggleAll">全选</van-checkbox>
    </van-submit-bar>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import {
  getCartList,
  updateCartQuantity,
  updateCartSelected,
  updateAllSelected,
  removeFromCart,
  type CartItem
} from '@/api/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const defaultImage = 'https://via.placeholder.com/200x200?text=No+Image'

const cartItems = ref<CartItem[]>([])
const loading = ref(false)
const refreshing = ref(false)

// 全选状态
const allChecked = computed({
  get() {
    if (cartItems.value.length === 0) return false
    return cartItems.value.every(item => item.selected === 1)
  },
  set(val: boolean) {
    const v = val ? 1 : 0
    cartItems.value.forEach(item => { item.selected = v })
  }
})

// 计算总价（单位：分）
const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected === 1)
    .reduce((sum, item) => sum + item.price * item.quantity * 100, 0)
})

// 加载购物车列表
const loadCart = async () => {
  await cartStore.loadCart()
  cartItems.value = cartStore.cartItems
}

// 更新数量
const onQuantityChange = async (cartId: number, quantity: number) => {
  if (!userStore.userInfo?.id) return
  try {
    const res: any = await updateCartQuantity({
      userId: userStore.userInfo.id,
      cartId,
      quantity
    })
    if (res.code !== 200) {
      showToast(res.message || '更新失败')
      loadCart()
    }
  } catch (error) {
    showToast('更新失败')
    loadCart()
  }
}

// 单个选中状态变化
const onItemSelectChange = async (cartId: number, val: number) => {
  if (!userStore.userInfo?.id) return
  try {
    await updateCartSelected({
      userId: userStore.userInfo.id,
      cartId,
      selected: val
    })
  } catch (error) {
    showToast('操作失败')
    loadCart()
  }
}

// 全选/取消全选
const toggleAll = async (checked: boolean) => {
  if (!userStore.userInfo?.id) return
  try {
    await updateAllSelected({
      userId: userStore.userInfo.id,
      selected: checked ? 1 : 0
    })
    const v = checked ? 1 : 0
    cartItems.value.forEach(item => { item.selected = v })
  } catch (error) {
    showToast('操作失败')
    loadCart()
  }
}

// 删除商品
const removeItem = async (id: number) => {
  if (!userStore.userInfo?.id) return
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除该商品吗？'
    })
    const res: any = await removeFromCart({
      userId: userStore.userInfo.id,
      cartId: id
    })
    if (res.code === 200) {
      showToast('已删除')
      // 刷新购物车（会自动更新底部导航栏的角标）
      await cartStore.refreshCart()
      cartItems.value = cartStore.cartItems
    } else {
      showToast(res.message || '删除失败')
    }
  } catch {
    // 用户取消
  }
}

// 去商品详情
const goProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 结算
const onSubmit = () => {
  const checkedItems = cartItems.value.filter(item => item.selected === 1)
  if (checkedItems.length === 0) {
    showToast('请选择商品')
    return
  }
  // 通过 URL query 传递数据（序列化后传递）
  router.push({
    path: '/checkout',
    query: {
      items: JSON.stringify(checkedItems)
    }
  })
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped lang="scss">
.cart {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 100px;
}

.cart-list {
  padding: 10px;
}

.cart-item-wrap {
  margin-bottom: 10px;
}

.cart-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 10px;
  width: 100%;
  box-sizing: border-box;
  position: relative;

  :deep(.van-checkbox) {
    margin-right: 10px;
    flex-shrink: 0;
  }

  :deep(.van-card) {
    flex: 1;
    background: transparent;
    padding: 0;
  }

  :deep(.van-card__thumb) {
    width: 80px;
    height: 80px;
  }

  :deep(.van-card__content) {
    padding-left: 10px;
  }
}

.delete-icon {
  flex-shrink: 0;
  margin-left: 10px;
  cursor: pointer;
  padding: 8px;
}

.submit-bar {
  bottom: 50px;
}
</style>
