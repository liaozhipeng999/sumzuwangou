import request from '@/utils/request'

// 消息类型
export type MessageType = 'logistics' | 'transaction' | 'system' | 'promotion' | 'merchant'

// 消息分类配置
export const MESSAGE_TYPE_CONFIG: Record<MessageType, { label: string; icon: string; color: string }> = {
  promotion: { label: '活动优惠', icon: 'gift-o', color: '#ff6034' },
  logistics: { label: '物流通知', icon: 'logistics', color: '#1989fa' },
  transaction: { label: '交易消息', icon: 'bill-o', color: '#07c160' },
  system: { label: '系统通知', icon: 'bell', color: '#909399' },
  merchant: { label: '商家消息', icon: 'chat-o', color: '#7232dd' }
}

// 消息接口
export interface Message {
  id: number
  type: MessageType
  title: string
  content: string
  image: string
  orderId: string
  merchantId?: number
  userId?: number
  isRead: boolean
  createdAt: string
}

// 消息分类概览
export interface MessageSummary {
  type: MessageType
  label: string
  icon: string
  unreadCount: number
  latestMessage: Message | null
}

// 消息列表响应
export interface MessageListResponse {
  list: Message[]
  total: number
  page: number
  pageSize: number
}

// 获取消息分类概览
export function getMessageSummary(userId: number = 1): Promise<{ code: number; message: string; data: MessageSummary[] }> {
  return request.get('/api/messages/summary', { params: { userId } })
}

// 获取总未读数
export function getUnreadCount(userId: number = 1): Promise<{ code: number; message: string; data: { total: number } }> {
  return request.get('/api/messages/unread-count', { params: { userId } })
}

// 获取某分类的消息列表
export function getMessagesByType(type: MessageType, page = 1, pageSize = 20, userId: number = 1): Promise<{ code: number; message: string; data: MessageListResponse }> {
  return request.get(`/api/messages/${type}`, { params: { page, pageSize, userId } })
}

// 标记单条消息已读
export function markMessageRead(id: number, userId: number = 1): Promise<{ code: number; message: string }> {
  return request.put(`/api/messages/${id}/read`, null, { params: { userId } })
}

// 标记某分类全部已读
export function markAllRead(type: MessageType, userId: number = 1): Promise<{ code: number; message: string }> {
  return request.put(`/api/messages/${type}/read-all`, null, { params: { userId } })
}

// 标记所有消息已读
export function markAllMessagesRead(userId: number = 1): Promise<{ code: number; message: string }> {
  return request.put('/api/messages/read-all', null, { params: { userId } })
}

// 删除单条消息
export function deleteMessage(id: number, userId: number = 1): Promise<{ code: number; message: string }> {
  return request.delete(`/api/messages/${id}`, { params: { userId } })
}

// 批量删除某类型消息
export function deleteMessagesByType(type: MessageType, userId: number = 1): Promise<{ code: number; message: string; data: { count: number } }> {
  return request.delete(`/api/messages/type/${type}`, { params: { userId } })
}
