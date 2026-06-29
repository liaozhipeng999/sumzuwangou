// 商品推荐接口
import request from '@/utils/request'

export interface ApplicableCoupon {
  id: number
  couponName: string
  couponType: number
  couponTypeName: string
  discountValue: number
  minAmount: number
  remainingSeconds: number
}

export interface Product {
  id: number
  productName: string
  productCode?: string
  categoryId: number
  merchantId?: number
  price: number
  originalPrice?: number
  discountedPrice?: number // 优惠后价格（红色显示）
  stock: number
  sales: number
  mainImage: string
  brief: string
  status: number
  isHot: number
  isNew: number
  createdAt?: string
  applicableCoupon?: ApplicableCoupon // 适用优惠券
  couponDiscount?: number
  couponEndTime?: string
}

export interface RecommendResponse {
  code: number
  message: string
  count?: number
  data: Product[]
}

// 综合推荐
export async function getRecommendProducts(limit: number = 10, userId?: number): Promise<RecommendResponse> {
  const response = await request.get<RecommendResponse>('/recommend/products', { 
    params: { limit, ...(userId && { userId }) } 
  })
  return response
}

// 热销商品
export async function getHotProducts(limit: number = 8): Promise<RecommendResponse> {
  const response = await request.get<RecommendResponse>('/recommend/hot', { params: { limit } })
  return response
}

// 新品推荐
export async function getNewProducts(limit: number = 10): Promise<RecommendResponse> {
  const response = await request.get<RecommendResponse>('/recommend/new', { params: { limit } })
  return response
}

// 好评商品
export async function getTopRatedProducts(limit: number = 10): Promise<RecommendResponse> {
  const response = await request.get<RecommendResponse>('/recommend/top-rated', { params: { limit } })
  return response
}

// 分类推荐
export async function getCategoryProducts(categoryId: number, limit: number = 10): Promise<RecommendResponse> {
  const response = await request.get<RecommendResponse>(`/recommend/category/${categoryId}`, { params: { limit } })
  return response
}

// 随机推荐
export async function getRandomProducts(limit: number = 10): Promise<RecommendResponse> {
  const response = await request.get<RecommendResponse>('/recommend/random', { params: { limit } })
  return response
}

// 记录浏览
export async function recordView(productId: number, userId?: number): Promise<void> {
  await request.post('/recommend/record-view', { 
    params: { productId, ...(userId && { userId }) } 
  })
}

// ==================== 商品详情接口类型定义 ====================

export interface CarouselImage {
  id: number
  imageUrl: string
  sort: number
  imageType: number
}

export interface DetailImage {
  id: number
  imageUrl: string
  sort: number
  imageType: number
}

export interface Merchant {
  id: number
  merchantName: string
  merchantLogo: string
  merchantBrief: string
}

export interface BrandFeature {
  label: string
  value: string
}

export interface BrandInfo {
  brandName: string
  brandLogo: string
  brandSlogan: string
  introduction: string // 富文本HTML
  features: BrandFeature[]
  establishedYear: number
  totalSales: string
  goodReviewCount: string
  recentReviewCount: string
  recentGroupCount: string
  reviewerCount: string
  guaranteeTags: string[]
  shopTags: string[]
}

export interface GroupUser {
  userName: string
  userAvatar: string
}

export interface GroupInfo {
  groupId: number | null
  groupSize: number
  currentCount: number
  groupPrice: number | null
  expireSeconds: number
  status: number
  users: GroupUser[]
}

export interface Review {
  id: number
  userId: number
  userName: string
  avatar: string
  rating: number
  content: string
  images: string | null // JSON数组或null
  helpfulCount: number
  createdAt: string
}

export interface ProductParam {
  paramKey: string
  paramValue: string
}

export interface ProductTag {
  tagName: string
  tagColor: string
}

export interface ProductDetail {
  product: Product
  carouselImages: CarouselImage[]
  merchant: Merchant
  brandInfo: BrandInfo
  groupInfo: GroupInfo
  reviews: Review[]
  detailImages: DetailImage[]
  details: ProductParam[]
  tags: ProductTag[]
  isFavorited: boolean
}

export interface ProductDetailResponse {
  code: number
  message: string
  data: ProductDetail
}

// 获取商品详情
export async function getProductDetail(id: number): Promise<ProductDetailResponse> {
  const response = await request.get<ProductDetailResponse>('/product/detail', { params: { productId: id } })
  return response
}

