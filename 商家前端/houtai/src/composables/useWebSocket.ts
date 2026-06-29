import { ref, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs.min.js'

interface WebSocketMessage {
  type: string
  [key: string]: any
}

type MessageCallback = (message: WebSocketMessage) => void

let stompClient: Client | null = null
const connected = ref(false)
const messageCallbacks = new Map<string, Set<MessageCallback>>()

function getWebSocketUrl(): string {
  const protocol = window.location.protocol === 'https:' ? 'https' : 'http'
  const host = import.meta.env.DEV ? 'localhost:9090' : window.location.host
  return `${protocol}://${host}/ws`
}

export function useWebSocket() {
  function connect() {
    if (stompClient && stompClient.connected) return

    stompClient = new Client({
      webSocketFactory: () => new SockJS(getWebSocketUrl()),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        connected.value = true
        console.log('[WebSocket] Connected')
        // 重新订阅所有已注册的主题
        messageCallbacks.forEach((callbacks, destination) => {
          subscribeToTopic(destination, callbacks)
        })
      },
      onDisconnect: () => {
        connected.value = false
        console.log('[WebSocket] Disconnected')
      },
      onStompError: (frame) => {
        console.error('[WebSocket] STOMP error:', frame.headers['message'])
        connected.value = false
      }
    })

    stompClient.activate()
  }

  function disconnect() {
    if (stompClient) {
      stompClient.deactivate()
      stompClient = null
      connected.value = false
    }
  }

  function subscribeToTopic(destination: string, callbacks: Set<MessageCallback>) {
    if (!stompClient || !stompClient.connected) return

    stompClient.subscribe(destination, (message) => {
      try {
        const parsed = JSON.parse(message.body) as WebSocketMessage
        callbacks.forEach((cb) => cb(parsed))
      } catch (e) {
        console.error('[WebSocket] Parse error:', e)
      }
    })
  }

  /**
   * 订阅商家新消息通知
   */
  function subscribeShopMessages(shopId: number, callback: MessageCallback): () => void {
    const destination = `/topic/shop/${shopId}/messages`
    if (!messageCallbacks.has(destination)) {
      messageCallbacks.set(destination, new Set())
    }
    messageCallbacks.get(destination)!.add(callback)
    subscribeToTopic(destination, messageCallbacks.get(destination)!)

    return () => {
      messageCallbacks.get(destination)?.delete(callback)
    }
  }

  /**
   * 订阅商家未读数通知
   */
  function subscribeShopUnread(shopId: number, callback: MessageCallback): () => void {
    const destination = `/topic/shop/${shopId}/unread`
    if (!messageCallbacks.has(destination)) {
      messageCallbacks.set(destination, new Set())
    }
    messageCallbacks.get(destination)!.add(callback)
    subscribeToTopic(destination, messageCallbacks.get(destination)!)

    return () => {
      messageCallbacks.get(destination)?.delete(callback)
    }
  }

  /**
   * 订阅用户新消息通知
   */
  function subscribeUserMessages(userId: number, callback: MessageCallback): () => void {
    const destination = `/topic/user/${userId}/messages`
    if (!messageCallbacks.has(destination)) {
      messageCallbacks.set(destination, new Set())
    }
    messageCallbacks.get(destination)!.add(callback)
    subscribeToTopic(destination, messageCallbacks.get(destination)!)

    return () => {
      messageCallbacks.get(destination)?.delete(callback)
    }
  }

  /**
   * 订阅用户未读数通知
   */
  function subscribeUserUnread(userId: number, callback: MessageCallback): () => void {
    const destination = `/topic/user/${userId}/unread`
    if (!messageCallbacks.has(destination)) {
      messageCallbacks.set(destination, new Set())
    }
    messageCallbacks.get(destination)!.add(callback)
    subscribeToTopic(destination, messageCallbacks.get(destination)!)

    return () => {
      messageCallbacks.get(destination)?.delete(callback)
    }
  }

  return {
    connected,
    connect,
    disconnect,
    subscribeShopMessages,
    subscribeShopUnread,
    subscribeUserMessages,
    subscribeUserUnread
  }
}
