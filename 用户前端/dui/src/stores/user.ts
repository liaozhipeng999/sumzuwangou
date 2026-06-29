import { ref } from 'vue'
import { defineStore } from 'pinia'

export interface UserInfo {
  id: number
  username: string
  phone: string
  email: string
  nickname: string
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref('')

  // 初始化：从 localStorage/sessionStorage 恢复登录状态
  function init() {
    // 从 sessionStorage 恢复 token
    const savedToken = sessionStorage.getItem('token')
    if (savedToken) {
      token.value = savedToken
    }
    
    // 从 localStorage 恢复用户信息
    loadUserInfo()
  }

  // 设置用户信息
  function setUserInfo(info: UserInfo) {
    userInfo.value = info
    // 同时保存到localStorage，以便页面刷新后恢复
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 从localStorage恢复用户信息
  function loadUserInfo() {
    const savedInfo = localStorage.getItem('userInfo')
    if (savedInfo) {
      try {
        userInfo.value = JSON.parse(savedInfo)
      } catch (e) {
        console.error('Failed to parse userInfo from localStorage:', e)
      }
    }
  }

  // 设置token
  function setToken(newToken: string) {
    token.value = newToken
    sessionStorage.setItem('token', newToken)
  }

  // 获取token
  function getToken() {
    if (!token.value) {
      token.value = sessionStorage.getItem('token') || ''
    }
    return token.value
  }

  // 清除用户信息（退出登录）
  function clearUserInfo() {
    userInfo.value = null
    token.value = ''
    sessionStorage.removeItem('token')
    localStorage.removeItem('loginInfo')
    localStorage.removeItem('userInfo')
  }

  // 判断是否已登录
  function isLoggedIn() {
    // 先尝试从 sessionStorage 获取最新值
    if (!token.value) {
      token.value = sessionStorage.getItem('token') || ''
    }
    return !!token.value
  }

  // 自动登录：检查 localStorage 中是否有有效的登录信息，有则静默登录
  async function autoLogin(): Promise<boolean> {
    if (isLoggedIn()) return true

    try {
      const loginInfoStr = localStorage.getItem('loginInfo')
      if (!loginInfoStr) return false

      const loginInfo = JSON.parse(loginInfoStr)
      const now = Date.now()

      // 检查是否过期（7天）
      if (!loginInfo.expireTime || loginInfo.expireTime <= now) {
        localStorage.removeItem('loginInfo')
        return false
      }

      // 静默调用登录接口
      const response = await fetch('/user/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: loginInfo.username,
          password: loginInfo.password
        })
      })

      const result = await response.json()

      if (result.code === 200) {
        // 正确设置 store 状态
        if (result.data) {
          setUserInfo(result.data)
        }
        setToken(result.token || 'user-token')
        return true
      } else {
        // 登录失败，清除过期信息
        localStorage.removeItem('loginInfo')
      }
    } catch (error) {
      console.error('自动登录失败:', error)
    }
    return false
  }

  return {
    userInfo,
    token,
    init,
    setUserInfo,
    loadUserInfo,
    setToken,
    getToken,
    clearUserInfo,
    isLoggedIn,
    autoLogin
  }
})
