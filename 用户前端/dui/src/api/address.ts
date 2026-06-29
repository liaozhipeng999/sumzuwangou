import request from '@/utils/request'

export interface AddressItem {
  id: number
  receiver_name: string
  receiver_phone: string
  province: string
  city: string
  district: string
  detail_address: string
  is_default: number
  sort: number
  created_at: string
  updated_at: string
}

export interface CreateAddressRequest {
  userId?: number
  receiver_name: string
  receiver_phone: string
  province: string
  city: string
  district: string
  detail_address: string
  is_default?: number
}

export interface UpdateAddressRequest extends CreateAddressRequest {
  id: number
}

export interface AddressResponse {
  code: number
  message: string
  data?: AddressItem | AddressItem[]
}

// 获取收货地址列表
export async function getAddressList(userId: number): Promise<AddressResponse> {
  const response = await request.get<AddressResponse>('/api/address/list', { params: { userId } })
  return response
}

// 获取单个收货地址
export async function getAddressDetail(id: number): Promise<AddressResponse> {
  const response = await request.get<AddressResponse>(`/api/address/${id}`)
  return response
}

// 创建收货地址
export async function createAddress(data: CreateAddressRequest): Promise<AddressResponse> {
  const response = await request.post<AddressResponse>('/api/address/create', data)
  return response
}

// 更新收货地址
export async function updateAddress(data: UpdateAddressRequest): Promise<AddressResponse> {
  const response = await request.post<AddressResponse>('/api/address/update', data)
  return response
}

// 删除收货地址
export async function deleteAddress(id: number): Promise<AddressResponse> {
  const response = await request.post<AddressResponse>(`/api/address/delete/${id}`)
  return response
}

// 设置默认地址
export async function setDefaultAddress(id: number, userId: number): Promise<AddressResponse> {
  const response = await request.post<AddressResponse>(`/api/address/set_default/${id}`, null, { params: { userId } })
  return response
}

// 获取省市区数据
export async function getRegionData(): Promise<any> {
  const response = await request.get<any>('/api/region/list')
  return response
}
