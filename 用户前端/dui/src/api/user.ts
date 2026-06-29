// 用户相关接口

export interface UserInfo {
  id: number
  username: string
  nickname: string
  phone: string
  email: string
}

export interface LoginResponse {
  code: number
  message: string
  data: UserInfo
}

export interface RegisterResponse {
  code: number
  message: string
}

// 使用相对路径，通过 Vite 代理转发到 Java 后端 (8080)
const BASE_URL = '/api'

// 获取图形验证码
export async function getCaptcha(): Promise<{ blob: Blob; key: string }> {
  try {
    const response = await fetch(`${BASE_URL}/user/captcha`)
    const blob = await response.blob()
    const key = response.headers.get('Captcha-Key') || ''
    return { blob, key }
  } catch (error) {
    console.error('获取验证码失败:', error)
    throw error
  }
}

// 获取验证码（调试用）
export async function getCaptchaDebug(): Promise<{ key: string; code: string }> {
  try {
    const response = await fetch(`${BASE_URL}/user/captcha/debug`)
    const data = await response.json()
    return data.data
  } catch (error) {
    console.error('获取验证码失败:', error)
    throw error
  }
}

// 用户注册
export async function register(
  data: {
    username: string
    password: string
    confirmPassword: string
    phone: string
    email?: string
    nickname?: string
    captcha: string
  },
  captchaKey: string
): Promise<RegisterResponse> {
  try {
    const response = await fetch(`${BASE_URL}/user/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Captcha-Key': captchaKey
      },
      body: JSON.stringify(data)
    })
    return await response.json()
  } catch (error) {
    console.error('注册失败:', error)
    throw error
  }
}

// 用户登录
export async function login(username: string, password: string): Promise<LoginResponse> {
  try {
    const response = await fetch(`${BASE_URL}/user/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    })
    return await response.json()
  } catch (error) {
    console.error('登录失败:', error)
    throw error
  }
}

// 获取用户信息
export async function getUser(id: number): Promise<UserInfo> {
  try {
    const response = await fetch(`${BASE_URL}/user/${id}`)
    const data = await response.json()
    return data.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
    throw error
  }
}