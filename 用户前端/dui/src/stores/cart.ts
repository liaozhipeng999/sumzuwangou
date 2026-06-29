import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getCartList, type CartItem } from '@/api/cart'
import { useUserStore } from './user'

export interface CheckoutItem {
  id: number
  productId: number
  skuId?: number
  productName: string
  productImage: string
  price: number
  quantity: number
}

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref<CartItem[]>([])
  const totalCount = ref(0)
  const checkoutItems = ref<CheckoutItem[]>([])

  // 初始化：从 localStorage 恢复购物车数据（临时缓存）
  function init() {
    try {
      const savedCart = localStorage.getItem('cartCache')
      if (savedCart) {
        const { items, count } = JSON.parse(savedCart)
        cartItems.value = items || []
        totalCount.value = count || 0
      }
    } catch (e) {
      console.error('恢复购物车缓存失败:', e)
    }
  }

  // 保存购物车到 localStorage（临时缓存）
  function saveToCache() {
    try {
      localStorage.setItem('cartCache', JSON.stringify({
        items: cartItems.value,
        count: totalCount.value
      }))
    } catch (e) {
      console.error('保存购物车缓存失败:', e)
    }
  }

  // 加载购物车列表
  async function loadCart() {
    const userStore = useUserStore()
    const userId = userStore.userInfo?.id
    
    if (!userId) {
      cartItems.value = []
      totalCount.value = 0
      saveToCache()
      return
    }

    try {
      const res: any = await getCartList(userId)
      if (res.code === 200 && res.data) {
        cartItems.value = res.data
        // 计算总数量
        totalCount.value = res.data.reduce((sum: number, item: any) => sum + item.quantity, 0)
        // 保存到本地缓存
        saveToCache()
      } else {
        cartItems.value = []
        totalCount.value = 0
        saveToCache()
      }
    } catch (error) {
      console.error('加载购物车失败:', error)
      // 如果后端请求失败，尝试使用本地缓存
      const savedCart = localStorage.getItem('cartCache')
      if (savedCart) {
        try {
          const { items, count } = JSON.parse(savedCart)
          cartItems.value = items || []
          totalCount.value = count || 0
        } catch (e) {
          cartItems.value = []
          totalCount.value = 0
        }
      } else {
        cartItems.value = []
        totalCount.value = 0
      }
    }
  }

  // 刷新购物车（用于添加/删除商品后）
  async function refreshCart() {
    await loadCart()
  }

  // 获取购物车数量
  function getCount(): number {
    return totalCount.value
  }

  return {
    cartItems,
    totalCount,
    init,
    loadCart,
    refreshCart,
    getCount,
    saveToCache
  }
})
