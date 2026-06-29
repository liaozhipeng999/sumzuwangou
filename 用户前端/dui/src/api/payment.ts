import request from '@/utils/request'

export interface PaymentResult {
  paymentId: number
  orderId: number
  orderNo: string
  amount: number
  payType: number
  payStatus: number
  transactionId: string
  payTime: string
}

export interface CreatePaymentRequest {
  orderId: number
  userId: number
  payType?: number
}

export interface CreatePaymentResponse {
  code: number
  message: string
  data?: PaymentResult
}

export interface CheckPaymentResponse {
  code: number
  message: string
  data?: PaymentResult
}

// 创建支付
export async function createPayment(data: CreatePaymentRequest): Promise<CreatePaymentResponse> {
  const response = await request.post<CreatePaymentResponse>('/api/payment/create', data)
  return response
}

// 查询支付状态
export async function checkPayment(orderId: number): Promise<CheckPaymentResponse> {
  const response = await request.get<CheckPaymentResponse>('/api/payment/check', { params: { orderId } })
  return response
}

// 支付状态说明
export const PayStatusMap: Record<number, string> = {
  0: '待支付',
  1: '支付成功',
  2: '支付失败'
}

export const PayTypeMap: Record<number, string> = {
  1: '微信支付',
  2: '支付宝',
  3: '银行卡'
}