// 商品收藏接口
// 使用相对路径，通过 Vite 代理转发到 Java 后端
// 后端实际接口路径：/product/favorite/*

export interface FavoriteResponse {
  code: number
  message: string
  isFavorited: boolean
}

export interface FavoriteProduct {
  id: number
  productName: string
  price: number
  originalPrice?: number
  mainImage: string
  // 后端可能返回的其他图片字段
  imageUrl?: string
  image?: string
  sales: number
  brief: string
  favoritedAt: string
  // 扩展字段
  brandName?: string
  tags?: string[]
  browseCount?: number
  canUseLater?: boolean
  count?: number
}

export interface FavoriteListResponse {
  code: number
  message: string
  data: {
    total: number
    totalPages: number
    pageSize: number
    page: number
    products: FavoriteProduct[]
  }
}

// 切换收藏状态（收藏/取消收藏）
// 使用查询参数传递 userId 和 productId
export async function toggleFavorite(userId: number, productId: number): Promise<FavoriteResponse> {
  try {
    const params = new URLSearchParams({
      userId: userId.toString(),
      productId: productId.toString()
    })
    const response = await fetch(`/product/favorite/toggle?${params.toString()}`, {
      method: 'POST'
    })
    return await response.json()
  } catch (error) {
    console.error('收藏操作失败:', error)
    throw error
  }
}

// 获取用户收藏列表
// 使用查询参数传递 userId、page、pageSize
export async function getFavoriteList(userId: number, page: number = 1, pageSize: number = 20): Promise<FavoriteListResponse> {
  try {
    const params = new URLSearchParams({
      userId: userId.toString(),
      page: page.toString(),
      pageSize: pageSize.toString()
    })
    const response = await fetch(`/product/favorite/list?${params.toString()}`)
    return await response.json()
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    throw error
  }
}

// 取消收藏（单个）- 使用 toggle 接口实现
export async function removeFavorite(userId: number, productId: number): Promise<FavoriteResponse> {
  try {
    const params = new URLSearchParams({
      userId: userId.toString(),
      productId: productId.toString()
    })
    const response = await fetch(`/product/favorite/toggle?${params.toString()}`, {
      method: 'POST'
    })
    return await response.json()
  } catch (error) {
    console.error('取消收藏失败:', error)
    throw error
  }
}

// 清空收藏列表 - 后端暂未实现此接口，返回提示
export async function clearFavorites(_userId: number): Promise<{ code: number; message: string }> {
  console.warn('清空收藏列表功能暂未实现')
  return { code: 500, message: '清空收藏列表功能暂未实现' }
}