import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Merchant } from '@/types/merchant'
import { merchantLogin, merchantRegister, merchantLogout, getMerchantInfo } from '@/api/merchant'

export const useMerchantStore = defineStore('merchant', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('merchant_token'))
  const merchantInfo = ref<Merchant | null>(
    localStorage.getItem('merchant_info') 
      ? JSON.parse(localStorage.getItem('merchant_info')!) 
      : null
  )

  // 计算属性
  const isLoggedIn = computed(() => !!token.value && !!merchantInfo.value)
  const merchantName = computed(() => merchantInfo.value?.merchantName || '')
  const merchantLevel = computed(() => merchantInfo.value?.merchantLevel || 1)
  
  // 检查当天是否已登录
  function checkTodayLogin(): boolean {
    const loginDate = localStorage.getItem('merchant_login_date')
    const token = localStorage.getItem('merchant_token')
    
    if (!loginDate || !token) {
      return false
    }
    
    const now = new Date()
    const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    
    return loginDate === today
  }

  // 登录
  async function login(username: string, password: string) {
    try {
      // 去除空格
      const cleanUsername = username.trim()
      const cleanPassword = password.trim()
      
      console.log('🔑 登录请求开始:', { 
        originalUsername: username, 
        cleanUsername: cleanUsername,
        passwordLength: password.length, 
        usernameLength: username.length,
        cleanUsernameLength: cleanUsername.length
      })
      
      // 检查参数
      if (!cleanUsername || !cleanPassword) {
        console.error('🔑 参数错误：用户名或密码为空')
        return { success: false, message: '用户名或密码不能为空' }
      }
      
      // 发送正式登录请求
      console.log('🔑 发送登录请求到 /merchant/login...')
      const res = await merchantLogin({ loginId: cleanUsername, password: cleanPassword })
      console.log('🔑 登录响应:', res)
      
      // 检查响应格式
      if (!res) {
        console.error('🔑 响应为空')
        return { success: false, message: '服务器未返回数据' }
      }
      
      // 根据API文档，code=200表示成功，code=500表示失败
      if (res.code === 200 && res.data) {
        token.value = res.data.token
        merchantInfo.value = res.data.merchant
        
        // 存储到 localStorage
        localStorage.setItem('merchant_token', res.data.token)
        localStorage.setItem('merchant_info', JSON.stringify(res.data.merchant))
        
        // 存储登录时间（当天有效）
        const now = new Date()
        const loginDate = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
        localStorage.setItem('merchant_login_date', loginDate)
        
        return { success: true, message: res.message || '登录成功' }
      } else if (res.code === 500) {
        // 根据API文档，500表示失败，具体错误信息在message中
        console.error('🔑 登录失败:', res.message)
        return { success: false, message: res.message || '登录失败' }
      } else {
        console.error('🔑 未知响应:', res)
        return { success: false, message: res.message || '登录失败，请稍后重试' }
      }
    } catch (error: any) {
      console.error('🔑 登录异常:', error)
      
      // 分类处理错误
      if (error.message?.includes('Network Error') || error.message?.includes('ETIMEDOUT')) {
        return { success: false, message: '网络连接失败，请检查后端服务是否运行' }
      }
      if (error.message?.includes('404')) {
        return { success: false, message: '接口地址错误，请检查后端配置' }
      }
      if (error.message?.includes('CORS')) {
        return { success: false, message: '跨域请求被拒绝，请检查后端CORS配置' }
      }
      if (error.code === 500) {
        return { success: false, message: error.message || '登录失败' }
      }
      
      return { success: false, message: error.message || '登录失败，请稍后重试' }
    }
  }

  // 注册
  async function register(data: {
    merchantName: string
    username: string
    password: string
    contactName: string
    contactPhone: string
    email?: string
    merchantLogo?: string
    merchantBrief?: string
    mainCategoryId?: number
  }) {
    try {
      console.log('📝 发送注册请求到 /merchant/register...')
      const res = await merchantRegister(data)
      console.log('📝 注册响应:', res)
      
      if (res.code === 200) {
        return { success: true, message: res.message || '注册成功', data: res.data }
      } else if (res.code === 500) {
        return { success: false, message: res.message || '注册失败' }
      } else {
        return { success: false, message: '注册失败，请稍后重试' }
      }
    } catch (error: any) {
      console.error('📝 注册异常:', error)
      if (error.code === 500) {
        return { success: false, message: error.message || '注册失败' }
      }
      return { success: false, message: error.message || '注册失败，请稍后重试' }
    }
  }

  // 获取商家信息
  async function fetchMerchantInfo(username: string) {
    try {
      console.log('📋 发送获取商家信息请求到 /merchant/info/' + username)
      const res = await getMerchantInfo(username)
      console.log('📋 获取商家信息响应:', res)
      
      if (res.code === 200 && res.data) {
        merchantInfo.value = res.data
        localStorage.setItem('merchant_info', JSON.stringify(res.data))
        return { success: true }
      } else {
        return { success: false, message: res.message || '获取信息失败' }
      }
    } catch (error: any) {
      console.error('📋 获取商家信息异常:', error)
      return { success: false, message: error.message || '获取信息失败' }
    }
  }

  // 退出登录
  async function logout() {
    try {
      await merchantLogout()
    } catch (_error) {
      // 忽略退出登录的错误
    }
    
    // 清除本地状态
    token.value = null
    merchantInfo.value = null
    localStorage.removeItem('merchant_token')
    localStorage.removeItem('merchant_info')
    localStorage.removeItem('merchant_login_date')
  }

  return {
    token,
    merchantInfo,
    isLoggedIn,
    merchantName,
    merchantLevel,
    checkTodayLogin,
    login,
    register,
    fetchMerchantInfo,
    logout
  }
})