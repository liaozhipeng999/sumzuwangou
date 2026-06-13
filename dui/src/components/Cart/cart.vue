<template>
  <div class="cart">
    <van-nav-bar title="购物车" />

    <!-- 购物车列表 -->
    <div class="cart-list">
      <van-empty v-if="cartItems.length === 0" description="购物车空空如也" />
      <van-swipe-cell v-for="item in cartItems" :key="item.id">
        <van-card
          :price="item.price"
          :title="item.title"
          :thumb="item.image"
        >
          <template #footer>
            <van-stepper v-model="item.quantity" @change="updateQuantity(item)" />
          </template>
        </van-card>
        <template #right>
          <van-button square type="danger" text="删除" @click="removeItem(item.id)" />
        </template>
      </van-swipe-cell>
    </div>

    <!-- 底部结算栏 -->
    <van-submit-bar
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
import { ref, computed } from 'vue'
import { showToast } from 'vant'

interface CartItem {
  id: number
  title: string
  price: number
  image: string
  quantity: number
  checked: boolean
}

// 购物车数据
const cartItems = ref<CartItem[]>([])
const allChecked = ref(false)

// 计算总价
const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.checked)
    .reduce((sum, item) => sum + item.price * item.quantity * 100, 0)
})

// 更新数量
const updateQuantity = (item: CartItem) => {
  console.log('更新数量:', item.id, item.quantity)
}

// 删除商品
const removeItem = (id: number) => {
  cartItems.value = cartItems.value.filter(item => item.id !== id)
  showToast('已删除')
}

// 全选/取消全选
const toggleAll = (checked: boolean) => {
  cartItems.value.forEach(item => {
    item.checked = checked
  })
}

// 提交订单
const onSubmit = () => {
  const checkedItems = cartItems.value.filter(item => item.checked)
  if (checkedItems.length === 0) {
    showToast('请选择商品')
    return
  }
  showToast('结算成功')
}
</script>

<style scoped>
.cart {
  padding-bottom: 100px;
}

.cart-list {
  padding: 10px;
}

.submit-bar {
  bottom: 50px;
}
</style>