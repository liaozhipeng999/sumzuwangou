import axios, { type AxiosRequestConfig } from 'axios'
import router from '@/router'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    'Accept': 'application/json;charset=UTF-8'
  },
  responseType: 'json',
  responseEncoding: 'utf8'
})

// 下划线转驼峰
function underscoreToCamel(str: string): string {
  return str.replace(/_([a-z])/g, (_match, letter) => letter.toUpperCase())
}

// 递归转换对象字段名
function transformKeys(obj: any): any {
  if (obj === null || typeof obj !== 'object') {
    return obj
  }
  if (Array.isArray(obj)) {
    return obj.map(item => transformKeys(item))
  }
  const result: Record<string, any> = {}
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      const newKey = underscoreToCamel(key)
      result[newKey] = transformKeys(obj[key])
    }
  }
  return result
}

// 图片路径转换：将本地路径转换为 URL，并对空图片做兜底
function transformImagePath(obj: any): any {
  if (obj === null || typeof obj === 'object') {
    return obj
  }
  if (Array.isArray(obj)) {
    return obj.map(item => transformImagePath(item))
  }
  const imageFields = new Set([
    'mainImage', 'main_image', 'productImage', 'product_image',
    'imageUrl', 'image_url', 'image',
    'merchantLogo', 'merchant_logo',
    'brandLogo', 'brand_logo',
    'avatar', 'userAvatar', 'user_avatar'
  ])
  const result: Record<string, any> = {}
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      let value = obj[key]
      if (imageFields.has(key) && typeof value === 'string') {
        if (value.trim() === '') {
          value = ''
        } else if (value.startsWith('F:/temp/image/')) {
          value = value.replace(/^F:\/temp\/image\//, '/images/')
        } else if (value.startsWith('F:\\temp\\image\\')) {
          value = value.replace(/^F:\\temp\\image\\/, '/images/')
        } else if (value.startsWith('F:/') || value.startsWith('F:\\')) {
          const parts = value.replace(/^F:[/\\]/, '')
          value = '/' + parts.replace(/\\/g, '/')
          if (!value.startsWith('/brand/') && !value.startsWith('/images/') && !value.startsWith('/user/')) {
            value = '/images/' + value
          }
        } else if (value.startsWith('http://') || value.startsWith('https://') || value.startsWith('data:')) {
          // 已经是完整 URL，不动
        } else if (value.startsWith('/brand/') || value.startsWith('/images/') || value.startsWith('/user/') || value.startsWith('/recommend/') || value.startsWith('/catalog/')) {
          // 后端返回的相对路径，保持原样
        } else if (value.startsWith('/')) {
          // 其他相对根路径
        } else {
          // 后端返回的像 brand/xxx.png 这种没带 / 前缀的
          value = '/' + value
        }
      }
      result[key] = transformImagePath(value)
    }
  }
  return result
}

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
    console.log('=== 响应拦截器被调用 ===')
    console.log('原始响应数据:', JSON.stringify(response.data).substring(0, 200) + '...')
    
    let data = response.data
    
    // 处理可能的编码问题：修复中文乱码
    if (data && typeof data === 'object') {
      const jsonString = JSON.stringify(data)
      // 检测是否存在乱码模式（ASCII范围内的乱码字符）
      if (/[\x80-\xFF]/.test(jsonString)) {
        // 尝试修复编码问题
        try {
          const fixedString = decodeURIComponent(escape(jsonString))
          data = JSON.parse(fixedString)
        } catch (e) {
          console.warn('编码修复失败，使用原始数据')
        }
      }
    }
    
    // 字段名转换：下划线转驼峰
    data = transformKeys(data)
    
    // 图片路径转换：将本地路径转换为 URL
    data = transformImagePath(data)
    
    console.log('转换后的数据:', JSON.stringify(data).substring(0, 200) + '...')
    
    // 返回修改后的响应对象，保持 axios 响应结构
    return { ...response, data }
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

// 类型化的请求方法（响应拦截器已处理数据，返回响应的data部分）
function hashSeed(seed: string): number {
  let h = 2166136261
  for (let i = 0; i < seed.length; i++) {
    h ^= seed.charCodeAt(i)
    h = Math.imul(h, 16777619)
  }
  return h >>> 0
}

function hueFromSeed(seed: string): number {
  return hashSeed(seed) % 360
}

function lighten(h: number, pct: number): string {
  return `hsl(${h}, 70%, ${pct}%)`
}

export function buildAvatarSvg(seed: string): string {
  const h = hueFromSeed(seed)
  const bg = lighten(h, 85)
  const face = lighten(h, 55)
  const hair = lighten((h + 60) % 360, 35)
  const eyeY = 78 + ((hashSeed(seed + 'ey') % 6) - 3)
  const smilePath = 'M70,110 Q100,140 130,110'
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">
  <rect width="200" height="200" fill="${bg}"/>
  <circle cx="100" cy="100" r="62" fill="${face}"/>
  <path d="M40,90 Q100,30 160,90 L160,60 Q100,10 40,60 Z" fill="${hair}"/>
  <circle cx="75" cy="${eyeY}" r="7" fill="#222"/>
  <circle cx="125" cy="${eyeY}" r="7" fill="#222"/>
  <path d="${smilePath}" stroke="#222" stroke-width="6" fill="none" stroke-linecap="round"/>
</svg>`
  return 'data:image/svg+xml;utf8,' + encodeURIComponent(svg)
}

export function buildBrandIconSvg(seed: string, label?: string): string {
  const h = hueFromSeed(seed)
  const bg = lighten(h, 70)
  const fg = lighten((h + 180) % 360, 45)
  const letter = (label && label.trim().length > 0)
    ? label.trim()[0].toUpperCase()
    : String.fromCharCode(65 + (hashSeed(seed + 'L') % 26))
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">
  <rect width="200" height="200" rx="28" fill="${bg}"/>
  <text x="100" y="132" font-family="Arial, Helvetica, sans-serif" font-weight="700" font-size="112" text-anchor="middle" fill="${fg}">${letter}</text>
</svg>`
  return 'data:image/svg+xml;utf8,' + encodeURIComponent(svg)
}

export function buildShapesSvg(seed: string): string {
  const h = hueFromSeed(seed)
  const bg = lighten(h, 88)
  const c1 = lighten(h, 62)
  const c2 = lighten((h + 120) % 360, 60)
  const c3 = lighten((h + 240) % 360, 55)
  const r1 = 26 + (hashSeed(seed + 'r1') % 18)
  const r2 = 22 + (hashSeed(seed + 'r2') % 16)
  const r3 = 20 + (hashSeed(seed + 'r3') % 14)
  const x1 = 40 + (hashSeed(seed + 'x1') % 80)
  const y1 = 40 + (hashSeed(seed + 'y1') % 80)
  const x2 = 40 + (hashSeed(seed + 'x2') % 80)
  const y2 = 40 + (hashSeed(seed + 'y2') % 80)
  const x3 = 40 + (hashSeed(seed + 'x3') % 80)
  const y3 = 40 + (hashSeed(seed + 'y3') % 80)
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">
  <rect width="200" height="200" fill="${bg}"/>
  <circle cx="${x1}" cy="${y1}" r="${r1}" fill="${c1}" opacity="0.9"/>
  <polygon points="${x2 - r2},${y2 + r2} ${x2 + r2},${y2 + r2} ${x2},${y2 - r2}" fill="${c2}" opacity="0.85"/>
  <rect x="${x3 - r3}" y="${y3 - r3}" width="${r3 * 2}" height="${r3 * 2}" rx="4" fill="${c3}" opacity="0.8"/>
  <circle cx="160" cy="160" r="22" fill="${c1}" opacity="0.5"/>
</svg>`
  return 'data:image/svg+xml;utf8,' + encodeURIComponent(svg)
}

const request = {
  get: async <T>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    const response = await instance.get<T>(url, config)
    return response.data as T
  },
  post: async <T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    const response = await instance.post<T>(url, data, config)
    return response.data as T
  },
  put: async <T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    const response = await instance.put<T>(url, data, config)
    return response.data as T
  },
  delete: async <T>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    const response = await instance.delete<T>(url, config)
    return response.data as T
  }
}

export default request