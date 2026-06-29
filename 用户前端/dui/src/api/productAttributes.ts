import request from '@/utils/request'

export interface Dimension {
  key: string
  name: string
  sortOrder: number
}

export interface AttributeOption {
  [option: string]: number[]
}

export interface Sku {
  skuId: number
  skuCode: string
  price: number
  originalPrice: number
  stock: number
  imageUrl: string | null
  attributes: Record<string, string>
}

export interface AttributeOptionsResponse {
  code: number
  message: string
  data?: {
    productId: number
    productName: string
    productType: string
    dimensions: Dimension[]
    attributeOptions: Record<string, AttributeOption | string>
    skus: Record<string, Sku>
  }
}

export interface DiscountCoupon {
  userCouponId: number
  couponId: number
  couponName: string
  couponType: number
  discountValue: number
  minAmount: number
  startTime: string
  endTime: string
  applicableDiscount: number
}

export interface DiscountResponse {
  code: number
  message: string
  data?: {
    productId: number
    skuId: number
    skuCode: string
    originalPrice: number
    currentPrice: number
    discountAmount: number
    discountRate: number
    discountLabel: string
    availableCoupons: DiscountCoupon[]
    bestCouponDiscount: number
    finalPrice: number
  }
}

export interface ProductTypeResponse {
  code: number
  message: string
  data?: string[]
}

// 获取商品属性选项
export async function getAttributeOptions(productId: number): Promise<AttributeOptionsResponse> {
  const response = await request.get<AttributeOptionsResponse>('/api/product/attributes/options', { params: { productId } })
  return response
}

// 获取属性维度
export async function getDimensions(productType?: string): Promise<{ code: number; message: string; data?: Dimension[] }> {
  const params: Record<string, string> = {}
  if (productType) params.productType = productType
  const response = await request.get<{ code: number; message: string; data?: Dimension[] }>('/api/product/attributes/dimensions', { params })
  return response
}

// 计算折扣
export async function calculateDiscount(productId: number, skuId: number, userId?: number): Promise<DiscountResponse> {
  const params: Record<string, number> = { productId, skuId }
  if (userId) params.userId = userId
  const response = await request.get<DiscountResponse>('/api/product/attributes/discount', { params })
  return response
}

// 获取所有商品类型
export async function getProductTypes(): Promise<ProductTypeResponse> {
  const response = await request.get<ProductTypeResponse>('/api/product/attributes/types')
  return response
}

// 获取折扣标签
export function getDiscountLabel(discountRate: number): string {
  if (discountRate >= 90) return '一折'
  if (discountRate >= 80) return '两折'
  if (discountRate >= 70) return '三折'
  if (discountRate >= 60) return '四折'
  if (discountRate >= 50) return '五折'
  if (discountRate >= 40) return '六折'
  if (discountRate >= 30) return '七折'
  if (discountRate >= 20) return '八折'
  if (discountRate >= 10) return '九折'
  if (discountRate > 0) return '优惠'
  return ''
}

// 商品类型名称映射
export const ProductTypeNames: Record<string, string> = {
  bike: '自行车',
  phone: '手机',
  laptop: '笔记本电脑',
  tablet: '平板',
  earphone: '耳机',
  clothing: '服装',
  food: '食品零食',
  fruit: '水果',
  shoes: '鞋子',
  watch: '手表',
  computer: '电脑配件',
  accessory: '数码配件',
  toy: '玩具',
  fresh: '生鲜',
  appliance: '家电',
  home: '家居',
  baby: '母婴',
  sports: '运动',
  car: '车品',
  default: '其他'
}