import request from '@/utils/request'

// ============ 用户端接口 ============

/** 获取用户会话列表 */
export function getConversations(userId: number) {
  return request.get('/api/customer-service/conversations', { params: { userId } })
}

/** 获取用户未读消息总数 */
export function getUnreadCount(userId: number) {
  return request.get('/api/customer-service/unread-count', { params: { userId } })
}

/** 标记某店铺消息已读 */
export function markAsRead(userId: number, shopId: number) {
  return request.post('/api/customer-service/mark-read', { userId, shopId })
}

/** 获取聊天记录 */
export function getMessages(shopId: number, userId: number, page = 1, pageSize = 20) {
  return request.get('/api/customer-service/messages', { params: { shopId, userId, page, pageSize } })
}

/** 发送消息 */
export function sendMessage(data: {
  userId: number
  shopId: number
  content: string
  messageType?: string
  productId?: number
}) {
  return request.post('/api/customer-service/messages', data)
}

/** 删除单条消息 */
export function deleteMessage(messageId: number, userId: number) {
  return request.delete(`/customer-service/messages/${messageId}`, { params: { userId } })
}

/** 清空与某店铺的聊天记录 */
export function clearMessages(userId: number, shopId: number) {
  return request.delete('/api/customer-service/messages/clear', { params: { userId, shopId } })
}

/** 获取店铺信息 */
export function getShopInfo(shopId: number) {
  return request.get(`/customer-service/shop/${shopId}`)
}

/** 获取商品信息 */
export function getProductInfo(productId: number) {
  return request.get(`/customer-service/product/${productId}`)
}

/** 获取快捷回复 */
export function getQuickReplies(shopId: number) {
  return request.get(`/customer-service/quick-replies/${shopId}`)
}
