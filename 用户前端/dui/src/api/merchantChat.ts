import axios, { type AxiosRequestConfig } from 'axios'

// 商家客服接口使用8000端口（Python后端）
const merchantChatInstance = axios.create({
  baseURL: 'http://localhost:8000',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    'Accept': 'application/json;charset=UTF-8'
  },
  responseType: 'json',
  responseEncoding: 'utf8'
})

export interface MerchantChatRequest {
  merchant_id: number
  user_id: string
  message: string
  conversation_id?: string
  user_name?: string
}

export interface MerchantChatResponse {
  response: string
  conversation_id: string
  source: 'corpus' | 'llm'
  merchant_id: number
  user_id: string
}

export interface MessageHistory {
  role: 'user' | 'assistant'
  content: string
  source: string | null
  timestamp: string
}

export interface ConversationHistoryResponse {
  conversation_id: string
  merchant_id: number
  user_id: string
  created_at: string
  updated_at: string
  messages: MessageHistory[]
}

export interface EndConversationResponse {
  status: string
  conversation_id: string
  message: string
}

export async function merchantChat(requestData: MerchantChatRequest): Promise<MerchantChatResponse> {
  const params = new URLSearchParams()
  params.append('merchant_id', String(requestData.merchant_id))
  params.append('user_id', requestData.user_id)
  params.append('message', requestData.message)
  if (requestData.conversation_id) {
    params.append('conversation_id', requestData.conversation_id)
  }
  if (requestData.user_name) {
    params.append('user_name', requestData.user_name)
  }
  
  const response = await merchantChatInstance.post(`/MerchantChat?${params}`)
  return response.data
}

export async function getConversationHistory(conversationId: string): Promise<ConversationHistoryResponse> {
  const response = await merchantChatInstance.get(`/MerchantChat/history?conversation_id=${conversationId}`)
  return response.data
}

export async function endConversation(conversationId: string): Promise<EndConversationResponse> {
  const response = await merchantChatInstance.post(`/MerchantChat/end?conversation_id=${conversationId}`)
  return response.data
}