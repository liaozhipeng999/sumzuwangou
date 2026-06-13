// 商品推荐接口

export interface Product {
  id: number
  productName: string
  productCode?: string
  categoryId: number
  price: number
  originalPrice?: number
  stock: number
  sales: number
  mainImage: string
  brief: string
  status: number
  isHot: number
  isNew: number
  createdAt?: string
}

export interface RecommendResponse {
  code: number
  message: string
  count: number
  data: Product[]
}

// 模拟商品数据
const mockProducts: Product[] = [
  {
    id: 1,
    productName: '华为Mate60 Pro',
    productCode: 'SKU001',
    categoryId: 1,
    price: 6999.00,
    originalPrice: 7999.00,
    stock: 100,
    sales: 50,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Huawei%20Mate60%20Pro%20smartphone%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '华为旗舰手机，搭载麒麟芯片',
    status: 1,
    isHot: 1,
    isNew: 0
  },
  {
    id: 2,
    productName: 'iPhone 15 Pro',
    productCode: 'SKU002',
    categoryId: 1,
    price: 8999.00,
    originalPrice: 9999.00,
    stock: 80,
    sales: 45,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=iPhone%2015%20Pro%20smartphone%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '苹果旗舰手机，钛金属设计',
    status: 1,
    isHot: 1,
    isNew: 1
  },
  {
    id: 3,
    productName: 'MacBook Pro 14寸',
    productCode: 'SKU003',
    categoryId: 2,
    price: 14999.00,
    originalPrice: 16999.00,
    stock: 50,
    sales: 30,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=MacBook%20Pro%2014%20inch%20laptop%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '苹果笔记本，M3 Pro芯片',
    status: 1,
    isHot: 1,
    isNew: 0
  },
  {
    id: 4,
    productName: '小米14 Ultra',
    productCode: 'SKU004',
    categoryId: 1,
    price: 5999.00,
    originalPrice: 6499.00,
    stock: 120,
    sales: 60,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Xiaomi%2014%20Ultra%20smartphone%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '小米旗舰，徕卡影像',
    status: 1,
    isHot: 0,
    isNew: 1
  },
  {
    id: 5,
    productName: 'iPad Pro 12.9',
    productCode: 'SKU005',
    categoryId: 3,
    price: 9299.00,
    originalPrice: 9999.00,
    stock: 60,
    sales: 25,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=iPad%20Pro%2012.9%20inch%20tablet%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '苹果平板，M2芯片',
    status: 1,
    isHot: 0,
    isNew: 0
  },
  {
    id: 6,
    productName: 'AirPods Pro 2',
    productCode: 'SKU006',
    categoryId: 4,
    price: 1899.00,
    originalPrice: 1999.00,
    stock: 200,
    sales: 100,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=AirPods%20Pro%202%20earbuds%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '苹果无线耳机，主动降噪',
    status: 1,
    isHot: 1,
    isNew: 0
  },
  {
    id: 7,
    productName: '华为Watch GT5',
    productCode: 'SKU007',
    categoryId: 5,
    price: 2499.00,
    originalPrice: 2699.00,
    stock: 80,
    sales: 35,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Huawei%20Watch%20GT5%20smartwatch%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '华为智能手表，健康监测',
    status: 1,
    isHot: 0,
    isNew: 1
  },
  {
    id: 8,
    productName: '索尼WH-1000XM5',
    productCode: 'SKU008',
    categoryId: 4,
    price: 2999.00,
    originalPrice: 3299.00,
    stock: 40,
    sales: 20,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Sony%20WH-1000XM5%20headphones%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '索尼旗舰耳机，顶级降噪',
    status: 1,
    isHot: 0,
    isNew: 0
  },
  {
    id: 9,
    productName: '联想ThinkPad X1',
    productCode: 'SKU009',
    categoryId: 2,
    price: 12999.00,
    originalPrice: 14999.00,
    stock: 30,
    sales: 15,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Lenovo%20ThinkPad%20X1%20carbon%20laptop%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '商务旗舰笔记本',
    status: 1,
    isHot: 0,
    isNew: 0
  },
  {
    id: 10,
    productName: '三星Galaxy S24',
    productCode: 'SKU010',
    categoryId: 1,
    price: 6499.00,
    originalPrice: 6999.00,
    stock: 90,
    sales: 40,
    mainImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=Samsung%20Galaxy%20S24%20smartphone%20product%20photo%20white%20background&image_size=landscape_4_3',
    brief: '三星旗舰，AI功能',
    status: 1,
    isHot: 0,
    isNew: 1
  }
]

// 综合推荐
export async function getRecommendProducts(limit: number = 10, userId?: number): Promise<RecommendResponse> {
  try {
    const url = new URL('/recommend/products', 'http://localhost:8080')
    url.searchParams.set('limit', limit.toString())
    if (userId) {
      url.searchParams.set('userId', userId.toString())
    }
    
    const response = await fetch(url.toString())
    return await response.json()
  } catch (error) {
    console.error('获取推荐商品失败，使用模拟数据:', error)
    // 返回模拟数据
    const shuffled = [...mockProducts].sort(() => Math.random() - 0.5)
    return {
      code: 200,
      message: 'success',
      count: Math.min(limit, shuffled.length),
      data: shuffled.slice(0, limit)
    }
  }
}

// 热销商品
export async function getHotProducts(limit: number = 10): Promise<RecommendResponse> {
  try {
    const url = new URL('/recommend/hot', 'http://localhost:8080')
    url.searchParams.set('limit', limit.toString())
    
    const response = await fetch(url.toString())
    return await response.json()
  } catch (error) {
    console.error('获取热销商品失败，使用模拟数据:', error)
    // 返回模拟数据（按销量排序）
    const sorted = [...mockProducts].sort((a, b) => b.sales - a.sales)
    return {
      code: 200,
      message: 'success',
      count: Math.min(limit, sorted.length),
      data: sorted.slice(0, limit)
    }
  }
}

// 新品推荐
export async function getNewProducts(limit: number = 10): Promise<RecommendResponse> {
  try {
    const url = new URL('/recommend/new', 'http://localhost:8080')
    url.searchParams.set('limit', limit.toString())
    
    const response = await fetch(url.toString())
    return await response.json()
  } catch (error) {
    console.error('获取新品推荐失败，使用模拟数据:', error)
    // 返回模拟数据（新品标记）
    const filtered = mockProducts.filter(p => p.isNew === 1)
    return {
      code: 200,
      message: 'success',
      count: Math.min(limit, filtered.length),
      data: filtered.slice(0, limit)
    }
  }
}

// 分类推荐
export async function getCategoryProducts(categoryId: number, limit: number = 10): Promise<RecommendResponse> {
  try {
    const url = new URL(`/recommend/category/${categoryId}`, 'http://localhost:8080')
    url.searchParams.set('limit', limit.toString())
    
    const response = await fetch(url.toString())
    return await response.json()
  } catch (error) {
    console.error('获取分类商品失败，使用模拟数据:', error)
    // 返回模拟数据
    const filtered = mockProducts.filter(p => p.categoryId === categoryId)
    return {
      code: 200,
      message: 'success',
      count: Math.min(limit, filtered.length),
      data: filtered.slice(0, limit)
    }
  }
}

// 随机推荐
export async function getRandomProducts(limit: number = 10): Promise<RecommendResponse> {
  try {
    const url = new URL('/recommend/random', 'http://localhost:8080')
    url.searchParams.set('limit', limit.toString())
    
    const response = await fetch(url.toString())
    return await response.json()
  } catch (error) {
    console.error('获取随机商品失败，使用模拟数据:', error)
    // 返回模拟数据
    const shuffled = [...mockProducts].sort(() => Math.random() - 0.5)
    return {
      code: 200,
      message: 'success',
      count: Math.min(limit, shuffled.length),
      data: shuffled.slice(0, limit)
    }
  }
}

// 记录浏览
export async function recordView(productId: number, userId?: number): Promise<void> {
  try {
    const url = new URL('/recommend/record-view', 'http://localhost:8080')
    url.searchParams.set('productId', productId.toString())
    if (userId) {
      url.searchParams.set('userId', userId.toString())
    }
    
    await fetch(url.toString(), { method: 'POST' })
  } catch (error) {
    console.error('记录浏览失败:', error)
    // 忽略错误，不影响用户体验
  }
}