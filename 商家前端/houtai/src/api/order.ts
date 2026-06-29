import api from './merchant'

export interface OrderItem {
  name: string
  spec: string
  price: number
  quantity: number
  subtotal: number
}

export interface OrderVO {
  orderNo: string
  createTime: string
  customerName: string
  customerPhone: string
  productInfo: string
  totalAmount: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
}

export interface OrderDetailVO extends OrderVO {
  address: string
  payMethod: string
  express?: string
  trackingNo?: string
  shipRemark?: string
  items: OrderItem[]
}

export interface OrderPageResult {
  total: number
  page: number
  pageSize: number
  list: OrderVO[]
}

export interface ShipRequest {
  orderNo: string
  express: string
  trackingNo: string
  remark?: string
  shopId: number
  operator?: string
}

export interface CancelRequest {
  orderNo: string
  reason?: string
  shopId: number
  operator?: string
}

export function getOrderPage(params: {
  shopId: number
  page?: number
  pageSize?: number
  status?: string
  keyword?: string
  startDate?: string
  endDate?: string
}) {
  return api.get<any, any>('/merchant/order/page', { params })
    .then((r: any) => {
      if (r?.code === 200) return r.data as OrderPageResult
      throw new Error(r?.message || '获取订单失败')
    })
}

export function getOrderDetail(orderNo: string, shopId?: number) {
  return api.get<any, any>('/merchant/order/detail', { params: { orderNo, shopId } })
    .then((r: any) => {
      if (r?.code === 200) return r.data as OrderDetailVO
      throw new Error(r?.message || '获取订单详情失败')
    })
}

export function shipOrder(data: ShipRequest) {
  return api.post<any, any>('/merchant/order/ship', data)
    .then((r: any) => {
      if (r?.code === 200) return r
      throw new Error(r?.message || '发货失败')
    })
}

export function cancelOrder(data: CancelRequest) {
  return api.post<any, any>('/merchant/order/cancel', data)
    .then((r: any) => {
      if (r?.code === 200) return r
      throw new Error(r?.message || '取消失败')
    })
}
