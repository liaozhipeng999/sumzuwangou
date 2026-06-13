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
    return !!token.value || !!sessionStorage.getItem('token')
  }

  return {
    userInfo,
    token,
    setUserInfo,
    loadUserInfo,
    setToken,
    getToken,
    clearUserInfo,
    isLoggedIn
  }
})
