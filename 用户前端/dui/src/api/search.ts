import request from '@/utils/request'

export interface SearchProduct {
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
}

export interface SearchResponse {
  code: number
  message: string
  data: {
    products: SearchProduct[]
    total: number
    page: number
    pageSize: number
    totalPages: number
  }
}

export interface SuggestResponse {
  code: number
  message: string
  data: SearchProduct[]
}

export interface HistoryResponse {
  code: number
  message: string
  data: string[]
}

export interface HotSearchResponse {
  code: number
  message: string
  data: string[]
}

export async function searchProducts(
  keyword: string,
  categoryId?: number,
  page: number = 1,
  pageSize: number = 10,
  sort: string = 'default',
  filters?: Record<string, any>
): Promise<SearchResponse> {
  try {
    const params: Record<string, any> = {
      keyword,
      page,
      pageSize
    }
    
    if (categoryId) {
      params.categoryId = categoryId
    }
    
    let sortBy = 'sales'
    let sortOrder = 'desc'

    if (sort === 'sales_asc') {
      sortBy = 'sales'
      sortOrder = 'asc'
    } else if (sort === 'sales_desc') {
      sortBy = 'sales'
      sortOrder = 'desc'
    } else if (sort === 'price_asc') {
      sortBy = 'price'
      sortOrder = 'asc'
    } else if (sort === 'price_desc') {
      sortBy = 'price'
      sortOrder = 'desc'
    } else if (sort === 'created_at') {
      sortBy = 'created_at'
      sortOrder = 'desc'
    } else if (sort === 'brand') {
      sortBy = 'sort'
      sortOrder = 'desc'
    }
    
    params.sortBy = sortBy
    params.sortOrder = sortOrder
    
    if (filters) {
      Object.keys(filters).forEach(key => {
        if (filters[key] !== undefined && filters[key] !== null && filters[key] !== '') {
          params[key] = filters[key]
        }
      })
    }
    
    return await request.get<SearchResponse>('/api/search/products', { params })
  } catch (error) {
    console.error('搜索商品失败:', error)
    throw error
  }
}

export async function getSearchSuggestions(keyword: string, limit: number = 5): Promise<SuggestResponse> {
  try {
    return await request.get<SuggestResponse>('/api/search/suggest', {
      params: { keyword, limit }
    })
  } catch (error) {
    console.error('获取搜索建议失败:', error)
    throw error
  }
}

export async function getSearchHistory(userId: string = 'default'): Promise<HistoryResponse> {
  try {
    return await request.get<HistoryResponse>('/api/search/history', {
      params: { userId }
    })
  } catch (error) {
    console.error('获取搜索历史失败:', error)
    throw error
  }
}

export async function saveSearchHistory(keyword: string, userId: string = 'default'): Promise<{ code: number; message: string }> {
  try {
    return await request.post<{ code: number; message: string }>('/api/search/history', null, {
      params: { userId, keyword }
    })
  } catch (error) {
    console.error('保存搜索历史失败:', error)
    throw error
  }
}

export async function clearSearchHistory(userId: string = 'default'): Promise<{ code: number; message: string }> {
  try {
    return await request.delete<{ code: number; message: string }>('/api/search/history', {
      params: { userId }
    })
  } catch (error) {
    console.error('清除搜索历史失败:', error)
    throw error
  }
}

export async function deleteSearchHistory(keyword: string, userId: string = 'default'): Promise<{ code: number; message: string }> {
  try {
    return await request.delete<{ code: number; message: string }>('/api/search/history/delete', {
      params: { userId, keyword }
    })
  } catch (error) {
    console.error('删除搜索历史失败:', error)
    throw error
  }
}

export async function getHotSearch(limit: number = 10): Promise<HotSearchResponse> {
  try {
    return await request.get<HotSearchResponse>('/api/search/hot', {
      params: { limit }
    })
  } catch (error) {
    console.error('获取热门搜索失败:', error)
    throw error
  }
}