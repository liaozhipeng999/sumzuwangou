import api from './merchant'

// ========== 类型定义 ==========

export interface Conversation {
  shopId?: number
  shopName?: string
  shopLogo?: string
  userId?: number
  userName?: string
  lastMessage: string
  lastMessageType: string
  lastSendTime: string
  lastSenderType: string
  unreadCount: number
}

export interface MessageVO {
  messageId: number
  senderId: number
  senderType: string
  content: string
  sendTime: string
  messageType: string
}

export interface UnreadCountVO {
  total: number
  users?: { userId: number; unreadCount: number }[]
  shops?: { shopId: number; unreadCount: number }[]
}

export interface QuickReply {
  id: number
  content: string
  sort?: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface MessagesResponse {
  total: number
  page: number
  pageSize: number
  list: MessageVO[]
}

// ========== 商家端 API ==========

/** 商家获取会话列表 */
export function getMerchantConversations(shopId: number): Promise<ApiResponse<Conversation[]>> {
  return api.get('/customer-service/merchant/conversations', { params: { shopId } })
}

/** 商家获取未读消息数 */
export function getMerchantUnreadCount(shopId: number): Promise<ApiResponse<UnreadCountVO>> {
  return api.get('/customer-service/merchant/unread-count', { params: { shopId } })
}

/** 商家标记某用户消息已读 */
export function merchantMarkAsRead(shopId: number, userId: number): Promise<ApiResponse<void>> {
  return api.post('/customer-service/merchant/mark-read', { shopId, userId })
}

/** 商家发送消息 */
export function merchantSendMessage(data: {
  shopId: number
  userId: number
  content: string
  messageType?: string
  productId?: number
}): Promise<ApiResponse<{ messageId: number; sendTime: string }>> {
  return api.post('/customer-service/merchant/messages', data)
}

/** 获取聊天记录（分页） */
export function getMessages(
  shopId: number,
  userId: number,
  page: number = 1,
  pageSize: number = 20
): Promise<ApiResponse<MessagesResponse>> {
  return api.get('/customer-service/messages', {
    params: { shopId, userId, page, pageSize }
  })
}

/** 获取快捷回复列表 */
export function getMerchantQuickReplies(shopId: number): Promise<ApiResponse<QuickReply[]>> {
  return api.get('/customer-service/merchant/quick-replies', { params: { shopId } })
}

/** 新增快捷回复 */
export function addQuickReply(data: {
  shopId: number
  content: string
  sort?: number
}): Promise<ApiResponse<QuickReply>> {
  return api.post('/customer-service/merchant/quick-replies', data)
}

/** 删除快捷回复 */
export function deleteQuickReply(id: number, shopId: number): Promise<ApiResponse<void>> {
  return api.delete(`/customer-service/merchant/quick-replies/${id}`, { params: { shopId } })
}

/** 上传消息图片 */
export function uploadImage(file: File): Promise<ApiResponse<{ url: string; fileName: string }>> {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/customer-service/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 标记单条消息已读 */
export function markMessageAsRead(messageId: number): Promise<ApiResponse<void>> {
  return api.put(`/customer-service/messages/${messageId}/read`)
}

/** 获取商品列表（用于发送商品卡片） */
export function getProductList(merchantId: number): Promise<ApiResponse<any[]>> {
  return api.get(`/product/list/${merchantId}`)
}
