import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getMessageSummary,
  getUnreadCount,
  getMessagesByType,
  markMessageRead,
  markAllRead,
  markAllMessagesRead,
  deleteMessage as apiDeleteMessage,
  deleteMessagesByType as apiDeleteMessagesByType,
  type MessageSummary,
  type Message,
  type MessageType
} from '@/api/message'

// Mock 数据，后端不可用时兜底显示
const MOCK_MESSAGES: Record<MessageType, Message[]> = {
  merchant: [
    { id: 101, type: 'merchant', title: '凤凰PHOENIX琚壹专卖店', content: '亲，您咨询的商品已补货，欢迎下单~', image: '', orderId: '', merchantId: 1, isRead: false, createdAt: new Date(Date.now() - 1800000).toISOString() },
    { id: 102, type: 'merchant', title: '优品生活馆', content: '您的售后申请已通过审核，退款将在1-3个工作日内到账。', image: '', orderId: 'ORD20260615001', merchantId: 2, isRead: false, createdAt: new Date(Date.now() - 7200000).toISOString() },
    { id: 103, type: 'merchant', title: '数码潮玩旗舰店', content: '618大促期间全场包邮，爆款直降50元！', image: '', orderId: '', merchantId: 3, isRead: true, createdAt: new Date(Date.now() - 86400000).toISOString() },
  ],
  logistics: [
    { id: 201, type: 'logistics', title: '包裹已发出', content: '您的订单 ORD20260618002 已由【顺丰速运】揽收，预计明天送达。', image: '', orderId: 'ORD20260618002', isRead: false, createdAt: new Date(Date.now() - 3600000).toISOString() },
    { id: 202, type: 'logistics', title: '派送中', content: '快递员正在为您派送，请保持电话畅通。', image: '', orderId: 'ORD20260617003', isRead: false, createdAt: new Date(Date.now() - 14400000).toISOString() },
    { id: 203, type: 'logistics', title: '已签收', content: '您的包裹已签收，如有问题请及时联系商家。', image: '', orderId: 'ORD20260612004', isRead: true, createdAt: new Date(Date.now() - 259200000).toISOString() },
  ],
  transaction: [
    { id: 301, type: 'transaction', title: '支付成功', content: '订单 ORD20260618002 已支付成功，金额 ¥89.00', image: '', orderId: 'ORD20260618002', isRead: false, createdAt: new Date(Date.now() - 5400000).toISOString() },
    { id: 302, type: 'transaction', title: '退款到账', content: '订单 ORD20260615005 退款 ¥35.00 已到账。', image: '', orderId: 'ORD20260615005', isRead: true, createdAt: new Date(Date.now() - 172800000).toISOString() },
  ],
  system: [
    { id: 401, type: 'system', title: '系统通知', content: '您的账号于 2026-06-18 10:30 在新设备登录，如非本人操作请及时修改密码。', image: '', orderId: '', isRead: false, createdAt: new Date(Date.now() - 43200000).toISOString() },
    { id: 402, type: 'system', title: '实名认证成功', content: '恭喜您，实名认证已通过，可享受更多权益。', image: '', orderId: '', isRead: true, createdAt: new Date(Date.now() - 604800000).toISOString() },
  ],
  promotion: [
    { id: 501, type: 'promotion', title: '618年中大促', content: '全场低至5折，领券再减30元！活动时间：6.18-6.20', image: '', orderId: '', isRead: false, createdAt: new Date(Date.now() - 900000).toISOString() },
    { id: 502, type: 'promotion', title: '限时秒杀', content: '每天10点准时开抢，爆款低至1元起，手慢无！', image: '', orderId: '', isRead: false, createdAt: new Date(Date.now() - 10800000).toISOString() },
    { id: 503, type: 'promotion', title: '新人专享', content: '新用户专享大礼包，满20减10，满50减25！', image: '', orderId: '', isRead: true, createdAt: new Date(Date.now() - 432000000).toISOString() },
  ]
}

function buildMockSummary(): MessageSummary[] {
  return (Object.keys(MOCK_MESSAGES) as MessageType[]).map(type => {
    const messages = MOCK_MESSAGES[type]
    const latestMessage = messages[0] || null
    return {
      type,
      label: '',
      icon: '',
      unreadCount: messages.filter(m => !m.isRead).length,
      latestMessage
    }
  })
}

export const useMessageStore = defineStore('message', () => {
  // 分类概览
  const summary = ref<MessageSummary[]>([])
  // 总未读数
  const unreadCount = ref(0)
  // 是否使用 mock 数据
  const useMock = ref(false)
  // 各分类消息列表缓存
  const messageLists = ref<Record<MessageType, Message[]>>({
    logistics: [],
    transaction: [],
    system: [],
    promotion: [],
    merchant: []
  })
  // 各分类分页信息
  const pagination = ref<Record<MessageType, { page: number; total: number; hasMore: boolean }>>({
    logistics: { page: 1, total: 0, hasMore: true },
    transaction: { page: 1, total: 0, hasMore: true },
    system: { page: 1, total: 0, hasMore: true },
    promotion: { page: 1, total: 0, hasMore: true },
    merchant: { page: 1, total: 0, hasMore: true }
  })
  // 加载状态（按类型独立，防止切换时互相阻塞）
  const loading = ref<Record<MessageType, boolean>>({
    logistics: false,
    transaction: false,
    system: false,
    promotion: false,
    merchant: false
  })

  // 计算属性：获取某分类未读数
  const getUnreadByType = computed(() => {
    return (type: MessageType) => {
      const item = summary.value.find(s => s.type === type)
      return item?.unreadCount || 0
    }
  })

  // 获取消息分类概览
  async function fetchSummary() {
    try {
      const res = await getMessageSummary()
      if (res.code === 200) {
        summary.value = res.data
        unreadCount.value = res.data.reduce((sum, item) => sum + item.unreadCount, 0)
        useMock.value = false
        return
      }
    } catch (e) {
      console.warn('获取消息概览失败，使用本地数据:', e)
    }
    // API 失败，使用 mock 数据
    useMock.value = true
    summary.value = buildMockSummary()
    unreadCount.value = summary.value.reduce((sum, item) => sum + item.unreadCount, 0)
  }

  // 获取总未读数
  async function fetchUnreadCount() {
    try {
      const res = await getUnreadCount()
      if (res.code === 200) {
        unreadCount.value = res.data.total
        return
      }
    } catch (e) {
      console.warn('获取未读数失败，使用本地计算:', e)
    }
    // mock 模式下已在 fetchSummary 中计算
    if (useMock.value) {
      unreadCount.value = summary.value.reduce((sum, item) => sum + item.unreadCount, 0)
    }
  }

  // 获取某分类消息列表
  async function fetchMessages(type: MessageType, refresh = false) {
    if (loading.value[type]) return
    loading.value[type] = true
    try {
      if (refresh) {
        pagination.value[type] = { page: 1, total: 0, hasMore: true }
        messageLists.value[type] = []
      }

      // 如果已知使用 mock 数据，直接使用
      if (useMock.value) {
        messageLists.value[type] = [...MOCK_MESSAGES[type]]
        pagination.value[type].total = MOCK_MESSAGES[type].length
        pagination.value[type].hasMore = false
        return
      }

      const currentPage = pagination.value[type].page
      const res = await getMessagesByType(type, currentPage)
      if (res.code === 200) {
        const data = res.data
        if (refresh) {
          messageLists.value[type] = data.list
        } else {
          messageLists.value[type].push(...data.list)
        }
        pagination.value[type].total = data.total
        pagination.value[type].hasMore = messageLists.value[type].length < data.total
        if (pagination.value[type].hasMore) {
          pagination.value[type].page = currentPage + 1
        }
      }
    } catch (e) {
      console.warn('获取消息列表失败，使用本地数据:', e)
      // API 失败，使用 mock 数据
      useMock.value = true
      messageLists.value[type] = [...MOCK_MESSAGES[type]]
      pagination.value[type].total = MOCK_MESSAGES[type].length
      pagination.value[type].hasMore = false
    } finally {
      loading.value[type] = false
    }
  }

  // 标记单条消息已读
  async function markRead(id: number, type: MessageType) {
    // 更新本地状态
    const msg = messageLists.value[type].find(m => m.id === id)
    if (msg && !msg.isRead) {
      msg.isRead = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
      const summaryItem = summary.value.find(s => s.type === type)
      if (summaryItem) {
        summaryItem.unreadCount = Math.max(0, summaryItem.unreadCount - 1)
      }
    }
    // mock 模式下同步更新 mock 数据
    if (useMock.value) {
      const mockMsg = MOCK_MESSAGES[type].find(m => m.id === id)
      if (mockMsg) mockMsg.isRead = true
    }
    try {
      await markMessageRead(id)
    } catch (e) {
      // mock 模式下忽略
    }
  }

  // 标记某分类全部已读
  async function markTypeAllRead(type: MessageType) {
    // 更新本地状态
    messageLists.value[type].forEach(m => { m.isRead = true })
    const summaryItem = summary.value.find(s => s.type === type)
    if (summaryItem) {
      unreadCount.value -= summaryItem.unreadCount
      summaryItem.unreadCount = 0
    }
    // mock 模式下同步更新
    if (useMock.value) {
      MOCK_MESSAGES[type].forEach(m => { m.isRead = true })
    }
    try {
      await markAllRead(type)
    } catch (e) {
      // mock 模式下忽略
    }
  }

  // 标记所有消息已读
  async function markAllReadMessages() {
    // 更新本地状态
    Object.values(messageLists.value).forEach(list => {
      list.forEach(m => { m.isRead = true })
    })
    summary.value.forEach(s => { s.unreadCount = 0 })
    unreadCount.value = 0
    if (useMock.value) {
      Object.values(MOCK_MESSAGES).forEach(list => {
        list.forEach(m => { m.isRead = true })
      })
    }
    try {
      await markAllMessagesRead()
    } catch (e) {
      // mock 模式下忽略
    }
  }

  // 删除单条消息
  async function deleteMessage(id: number, type: MessageType) {
    // 从列表中移除
    const list = messageLists.value[type]
    const idx = list.findIndex(m => m.id === id)
    if (idx !== -1) {
      const msg = list[idx]
      if (!msg.isRead) {
        unreadCount.value = Math.max(0, unreadCount.value - 1)
        const summaryItem = summary.value.find(s => s.type === type)
        if (summaryItem) {
          summaryItem.unreadCount = Math.max(0, summaryItem.unreadCount - 1)
        }
      }
      list.splice(idx, 1)
    }
    // mock 模式下同步更新
    if (useMock.value) {
      const mockIdx = MOCK_MESSAGES[type].findIndex(m => m.id === id)
      if (mockIdx !== -1) MOCK_MESSAGES[type].splice(mockIdx, 1)
    }
    try {
      await apiDeleteMessage(id)
    } catch (e) {
      // mock 模式下忽略
    }
  }

  // 批量删除某类型消息
  async function deleteTypeMessages(type: MessageType) {
    const list = messageLists.value[type]
    const unreadInList = list.filter(m => !m.isRead).length
    unreadCount.value = Math.max(0, unreadCount.value - unreadInList)
    const summaryItem = summary.value.find(s => s.type === type)
    if (summaryItem) {
      summaryItem.unreadCount = 0
      summaryItem.latestMessage = null
    }
    messageLists.value[type] = []
    pagination.value[type] = { page: 1, total: 0, hasMore: false }
    if (useMock.value) {
      MOCK_MESSAGES[type] = []
    }
    try {
      await apiDeleteMessagesByType(type)
    } catch (e) {
      // mock 模式下忽略
    }
  }

  // 启动未读数轮询
  let pollTimer: ReturnType<typeof setInterval> | null = null
  function startPolling(intervalMs = 30000) {
    stopPolling()
    pollTimer = setInterval(() => {
      fetchUnreadCount()
    }, intervalMs)
  }

  function stopPolling() {
    if (pollTimer) {
      clearInterval(pollTimer)
      pollTimer = null
    }
  }

  return {
    summary,
    unreadCount,
    useMock,
    messageLists,
    pagination,
    loading,
    getUnreadByType,
    fetchSummary,
    fetchUnreadCount,
    fetchMessages,
    markRead,
    markTypeAllRead,
    markAllReadMessages,
    deleteMessage,
    deleteTypeMessages,
    startPolling,
    stopPolling
  }
})
