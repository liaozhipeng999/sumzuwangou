import axios from 'axios'
import router from '@/router'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 添加 token
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    // 后端返回 401 = 未登录 / Token失效
    if (error.response?.status === 401) {
      // 清空失效凭证
      sessionStorage.removeItem('token')
      alert('登录已过期，请重新登录！')
      // 跳登录页
      router.push('/login')
      return Promise.reject(error)
    }
    
    // 统一错误处理
    const message = error.response?.data?.message || error.message || '请求失败'
    console.error('请求错误:', message)
    return Promise.reject(error)
  }
)

export default instance