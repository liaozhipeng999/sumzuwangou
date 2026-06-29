import request from '@/utils/request'

export interface Address {
  id: number
  userId: number
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: number
  status: number
  createdAt: string
  updatedAt: string
}

export interface Sku {
  id: number
  productId: number
  skuCode: string
  attributes: string
  price: number
  originalPrice: number
  stock: number
  sales: number
  imageUrl: string | null
  status: number
}

export interface AttributeGroup {
  attr_name: string
  attr_values: string
}

export interface SkuResponse {
  code: number
  message: string
  data?: {
    skus: Sku[]
    attributeGroups: AttributeGroup[]
  }
}

export interface Coupon {
  userCouponId: number
  couponId: number
  name: string
  type: number
  discountValue: number
  minAmount: number
  maxDiscount: number | null
  startTime: string
  endTime: string
}

export interface CouponResponse {
  code: number
  message: string
  data?: Coupon[]
}

export interface CalculateRequest {
  items: { skuId: number; quantity: number }[]
  couponId?: number
}

export interface PriceDetail {
  totalPrice: number
  discountAmount: number
  couponAmount: number
  payAmount: number
}

export interface CalculateResponse {
  code: number
  message: string
  data?: PriceDetail
}

export interface CreateOrderRequest {
  userId: number
  addressId: number
  items: { skuId: number; quantity: number }[]
  couponId?: number
}

export interface OrderResult {
  orderId: number
  orderNo: string
  totalAmount: number
  discountAmount: number
  couponAmount: number
  payAmount: number
}

export interface CreateOrderResponse {
  code: number
  message: string
  data?: OrderResult
}

// 获取用户地址列表
export async function getAddresses(userId: number): Promise<{ code: number; message: string; data?: Address[] }> {
  const response = await request.get<{ code: number; message: string; data?: Address[] }>('/api/order/addresses', { params: { userId } })
  return response
}

// 获取商品规格和属性
export async function getProductSkus(productId: number): Promise<SkuResponse> {
  const response = await request.get<SkuResponse>('/api/order/product/skus', { params: { productId } })
  return response
}

// 获取可用优惠券
export async function getCoupons(userId: number, amount?: number): Promise<CouponResponse> {
  const params: Record<string, number> = { userId }
  if (amount) params.amount = amount
  const response = await request.get<CouponResponse>('/api/order/coupons', { params })
  return response
}

// 计算订单价格
export async function calculatePrice(data: CalculateRequest): Promise<CalculateResponse> {
  const response = await request.post<CalculateResponse>('/api/order/calculate', data)
  return response
}

// 创建订单
export async function createOrder(data: CreateOrderRequest): Promise<CreateOrderResponse> {
  const response = await request.post<CreateOrderResponse>('/api/order/create', data)
  return response
}

// 获取用户订单列表
// 商家信息
export interface Merchant {
  id: number
  name: string
  logo: string
  brief?: string // 仅在订单项级别存在
}

export interface OrderItem {
  id: number
  productId: number
  skuId: number
  productName: string
  skuAttributes: string
  price: number
  originalPrice: number
  quantity: number
  imageUrl: string | null
  merchant?: Merchant // 订单项级别的商家信息
}

export interface OrderAddress {
  id: number
  userId: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: number
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  addressId: number
  totalAmount: number
  discountAmount: number
  couponAmount: number
  payAmount: number
  status: number
  payTime: string | null
  shipTime: string | null
  receiveTime: string | null
  cancelTime: string | null
  createdAt: string
  updatedAt: string
  merchant?: Merchant // 订单级别的商家信息（第一个商品的商家）
}

export interface OrderDetail {
  order: Order
  address: OrderAddress
  items: OrderItem[]
}

export interface OrderListResponse {
  code: number
  message: string
  data?: Order[]
}

export interface OrderDetailResponse {
  code: number
  message: string
  data?: OrderDetail
}

// 获取用户订单列表
export async function getOrderList(userId: number): Promise<OrderListResponse> {
  const response = await request.get<OrderListResponse>('/api/order/list', { params: { userId } })
  return response
}

// 获取订单详情
export async function getOrderDetail(orderId: number): Promise<OrderDetailResponse> {
  const response = await request.get<OrderDetailResponse>('/api/order/detail', { params: { orderId } })
  return response
}

// 更新订单状态
export async function updateOrderStatus(orderId: number, status: number): Promise<{ code: number; message: string }> {
  const response = await request.put<{ code: number; message: string }>('/api/order/status', null, { params: { orderId, status } })
  return response
}

// 删除订单（实际上是取消订单，将状态改为已取消）
export async function deleteOrder(orderId: number): Promise<{ code: number; message: string }> {
  // 订单状态5表示已取消
  const response = await request.put<{ code: number; message: string }>('/api/order/status', null, { params: { orderId, status: 4 } })
  return response
}

interface ShipRequest {
  orderId: number
  shipCompany: string
  shipNo: string
  shipRemark?: string
}

export async function shipOrder(data: ShipRequest): Promise<{ code: number; message: string }> {
  const response = await request.post<{ code: number; message: string }>('/api/order/ship', data)
  return response
}