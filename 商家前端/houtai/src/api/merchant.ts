import axios from 'axios'
import type { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse, Merchant, ApiResponse } from '@/types/merchant'

// 创建 axios 实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加 token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('merchant_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('merchant_token')
      localStorage.removeItem('merchant_info')
      window.location.href = '/merchant/login'
    }
    return Promise.reject(error.response?.data || error.message)
  }
)

// 商家登录
export function merchantLogin(data: LoginRequest): Promise<LoginResponse> {
  return api.post('/merchant/login', data)
}

// 商家注册
export function merchantRegister(data: RegisterRequest): Promise<RegisterResponse> {
  return api.post('/merchant/register', data)
}

// 获取商家信息
export function getMerchantInfo(username: string): Promise<ApiResponse<Merchant>> {
  return api.get(`/merchant/info/${username}`)
}

// 退出登录
export function merchantLogout(): Promise<{ message: string }> {
  return api.post('/merchant/logout')
}

export default api