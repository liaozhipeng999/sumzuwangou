// 商家信息类型定义
export interface Merchant {
  id: number
  merchantName: string
  merchantLogo: string | null
  merchantBrief: string | null
  username: string
  password?: string
  contactName: string
  contactPhone: string
  email?: string
  mainCategoryId: number | null
  merchantLevel: number
  status: number
  sort: number
  createdAt: string
  updatedAt: string
  deletedAt?: string | null
}

// 登录请求参数
export interface LoginRequest {
  loginId: string
  password: string
}

// 登录响应
export interface LoginResponse {
  code: number
  message: string
  data: {
    token: string
    merchant: Merchant
  }
}

// 注册请求参数
export interface RegisterRequest {
  merchantName: string
  username: string
  password: string
  contactName: string
  contactPhone: string
  email?: string
  merchantLogo?: string
  merchantBrief?: string
  mainCategoryId?: number
}

// 通用响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 注册响应
export interface RegisterResponse {
  code: number
  message: string
  data: Merchant
}