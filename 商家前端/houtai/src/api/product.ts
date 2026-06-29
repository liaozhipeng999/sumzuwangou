import api from './merchant'

export interface ProductTag {
  tagName: string
  tagColor: string
}

export interface AddProductRequest {
  merchantId: number
  productName: string
  categoryId: number
  price: number
  stock: number
  mainImage: string
  productCode?: string
  originalPrice?: number
  brief?: string
  status?: number
  tags?: ProductTag[]
}

export interface Product {
  id: number
  merchantId: number
  productName: string
  productCode: string
  categoryId: number
  price: number
  originalPrice: number | null
  stock: number
  sales: number
  mainImage: string
  brief: string | null
  status: number
  sort: number
  isHot: number
  isNew: number
  createdAt: string
  updatedAt: string
  deletedAt: string | null
  tags?: ProductTag[]
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export function addProduct(data: AddProductRequest): Promise<ApiResponse<Product>> {
  return api.post('/product/add', data)
}

export function getProductList(merchantId: number): Promise<ApiResponse<Product[]>> {
  return api.get(`/product/list/${merchantId}`)
}

export interface UpdateProductRequest {
  id: number
  productName: string
  categoryId: number
  price: number
  stock: number
  mainImage: string
  productCode?: string
  originalPrice?: number
  brief?: string
  status?: number
  tags?: ProductTag[]
}

export function updateProduct(data: UpdateProductRequest): Promise<ApiResponse<Product>> {
  return api.put('/product/update', data)
}

// 商品下架
export function downProduct(id: number): Promise<ApiResponse<Product>> {
  return api.put(`/product/down/${id}`)
}

// 商品上架
export function upProduct(id: number): Promise<ApiResponse<Product>> {
  return api.put(`/product/up/${id}`)
}

// 商品删除
export function deleteProduct(id: number): Promise<ApiResponse<null>> {
  return api.delete(`/product/delete/${id}`)
}
