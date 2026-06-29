import request from '@/utils/request'

export interface Category {
  id: number
  categoryName: string
  icon: string | null
  sort: number
}

export interface Product {
  id: number
  productName: string
  productNameEn: string | null
  brandId: number | null
  brandName: string | null
  categoryId: number
  merchantId: number | null
  price: string | number
  originalPrice: string | number
  discountedPrice?: string | number
  costPrice: string | null
  stock: number
  sales: number
  imageUrl: string | null
  mainImage: string | null
  description: string | null
  brief: string | null
  isHot: number
  isNew: number
  sort: number
  status: number
  createdAt: string
  updatedAt: string
  deletedAt: string | null
  applicableCoupon?: {
    couponName: string
    remainingSeconds: number
  }
}

export interface CategoryResponse {
  code: number
  message: string
  data?: Category[]
}

export interface ProductResponse {
  code: number
  message: string
  data?: Product[]
}

// 获取一级分类
export async function getLevel1Categories(): Promise<CategoryResponse> {
  const response = await request.get<CategoryResponse>('/api/category/list')
  return response
}

export interface CategoryWithLevel2 extends Category {
  level2?: Category[]
}

export interface CategoryLevel1Level2Response {
  code: number
  message: string
  data?: CategoryWithLevel2[]
}

// 获取一级和二级分类（各10个）
export async function getLevel1Level2Categories(): Promise<CategoryLevel1Level2Response> {
  const response = await request.get<CategoryLevel1Level2Response>('/api/category/level1-level2')
  return response
}

// 获取二级分类（根据一级分类ID）
export async function getLevel2Categories(level1Id: number): Promise<CategoryResponse> {
  const response = await request.get<CategoryResponse>(`/api/category/${level1Id}/children`)
  return response
}

// 获取三级分类（根据二级分类ID）
export async function getLevel3Categories(level2Id: number): Promise<CategoryResponse> {
  const response = await request.get<CategoryResponse>(`/api/category/level3/${level2Id}`)
  return response
}

// 获取一级分类商品
export async function getLevel1Products(level1Id: number, limit: number = 8): Promise<ProductResponse> {
  const response = await request.get<ProductResponse>(`/api/category/${level1Id}/products`, { params: { limit } })
  return response
}

// 获取二级分类商品
export async function getLevel2Products(level2Id: number, limit: number = 8): Promise<ProductResponse> {
  const response = await request.get<ProductResponse>(`/api/category/level2/${level2Id}/products`, { params: { limit } })
  return response
}

// 获取三级分类商品
export async function getLevel3Products(level3Id: number, limit: number = 8): Promise<ProductResponse> {
  const response = await request.get<ProductResponse>(`/api/category/level3/${level3Id}/products`, { params: { limit } })
  return response
}