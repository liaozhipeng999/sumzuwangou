import request from '@/utils/request'

export interface CartItem {
  id: number
  userId: number
  productId: number
  skuId?: number  // SKU ID（可选，用于结算）
  productName: string
  productImage: string
  price: number
  quantity: number
  selected: number
  createdAt: string
  updatedAt: string
}

export interface CartResponse {
  code: number
  message: string
  data: CartItem[] | CartItem | number
}

// 获取购物车列表
export async function getCartList(userId: number): Promise<CartResponse> {
  return request.get('/api/cart/list', { params: { userId } })
}

// 添加商品到购物车
export async function addToCart(params: {
  userId: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity?: number
}): Promise<CartResponse> {
  return request.post('/api/cart/add', params)
}

// 更新数量
export async function updateCartQuantity(params: {
  userId: number
  cartId: number
  quantity: number
}): Promise<CartResponse> {
  return request.post('/api/cart/update', params)
}

// 更新选中状态
export async function updateCartSelected(params: {
  userId: number
  cartId: number
  selected: number
}): Promise<CartResponse> {
  return request.post('/api/cart/select', params)
}

// 全选/取消全选
export async function updateAllSelected(params: {
  userId: number
  selected: number
}): Promise<CartResponse> {
  return request.post('/api/cart/selectAll', params)
}

// 删除商品
export async function removeFromCart(params: {
  userId: number
  cartId: number
}): Promise<CartResponse> {
  return request.post('/api/cart/remove', params)
}

// 清空购物车
export async function clearCart(userId: number): Promise<CartResponse> {
  return request.post('/api/cart/clear', { userId })
}

// 获取购物车数量
export async function getCartCount(userId: number): Promise<CartResponse> {
  return request.get('/api/cart/count', { params: { userId } })
}
