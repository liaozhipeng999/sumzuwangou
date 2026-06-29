import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import type { Product } from '@/api/recommend'

// 历史记录项接口
export interface HistoryItem {
  productId: number
  productName: string
  price: number
  originalPrice?: number
  mainImage: string
  browseTime: number
  browseCount: number
}

// 缓存过期时间（30分钟）
const SYNC_INTERVAL = 30 * 60 * 1000

export const useHistoryStore = defineStore('history', () => {
  // 历史浏览记录
  const historyList = ref<HistoryItem[]>([])
  
  // 是否正在同步
  const isSyncing = ref(false)
  
  // 上次同步时间
  const lastSyncTime = ref(0)
  
  // 定时器
  let syncTimer: ReturnType<typeof setInterval> | null = null

  // 获取历史记录列表（按浏览时间倒序）
  const sortedHistoryList = computed(() => {
    return [...historyList.value].sort((a, b) => b.browseTime - a.browseTime)
  })

  // 获取今日浏览记录
  const todayHistory = computed(() => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const todayTimestamp = today.getTime()
    return sortedHistoryList.value.filter(item => item.browseTime >= todayTimestamp)
  })

  // 获取精选推荐（最近浏览的商品）
  const featuredHistory = computed(() => {
    return sortedHistoryList.value.slice(0, 4)
  })

  // 从 localStorage 加载历史记录
  function loadFromCache() {
    try {
      const cached = localStorage.getItem('history_cache')
      if (cached) {
        historyList.value = JSON.parse(cached)
        console.log('历史记录从缓存加载成功')
      }
    } catch (e) {
      console.error('Failed to load history cache:', e)
    }
  }

  // 保存到 localStorage
  function saveToCache() {
    try {
      localStorage.setItem('history_cache', JSON.stringify(historyList.value))
    } catch (e) {
      console.error('Failed to save history cache:', e)
    }
  }

  // 添加浏览记录
  function addHistory(product: Product) {
    const existingIndex = historyList.value.findIndex(item => item.productId === product.id)
    
    if (existingIndex >= 0) {
      // 如果已存在，更新浏览时间和次数
      historyList.value[existingIndex].browseTime = Date.now()
      historyList.value[existingIndex].browseCount++
    } else {
      // 如果不存在，添加新记录
      historyList.value.push({
        productId: product.id,
        productName: product.productName,
        price: product.price,
        originalPrice: product.originalPrice,
        mainImage: product.mainImage,
        browseTime: Date.now(),
        browseCount: 1
      })
    }
    
    // 保存到缓存
    saveToCache()
    
    // 最多保存100条记录
    if (historyList.value.length > 100) {
      historyList.value = historyList.value.slice(-100)
    }
  }

  // 移除单条记录
  function removeHistory(productId: number) {
    const index = historyList.value.findIndex(item => item.productId === productId)
    if (index >= 0) {
      historyList.value.splice(index, 1)
      saveToCache()
    }
  }

  // 清空所有历史记录
  function clearHistory() {
    historyList.value = []
    saveToCache()
  }

  // 同步历史记录到后端
  async function syncHistory() {
    // 如果正在同步，跳过
    if (isSyncing.value) return
    
    // 检查是否需要同步（30分钟间隔）
    if (Date.now() - lastSyncTime.value < SYNC_INTERVAL) {
      console.log('未到同步时间，跳过')
      return
    }
    
    // 如果没有记录，不需要同步
    if (historyList.value.length === 0) return

    isSyncing.value = true
    
    try {
      // 调用后端接口同步历史记录
      const response = await fetch('/api/history/sync', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          records: historyList.value
        })
      })
      
      const result = await response.json()
      if (result.code === 200) {
        lastSyncTime.value = Date.now()
        console.log('历史记录同步成功')
      }
    } catch (error) {
      console.error('同步历史记录失败:', error)
    } finally {
      isSyncing.value = false
    }
  }

  // 启动定时同步（每30分钟）
  function startSyncTimer() {
    if (syncTimer) return
    
    syncTimer = setInterval(() => {
      syncHistory()
    }, SYNC_INTERVAL)
  }

  // 停止定时同步
  function stopSyncTimer() {
    if (syncTimer) {
      clearInterval(syncTimer)
      syncTimer = null
    }
  }

  // 页面离开时同步
  function onPageLeave() {
    syncHistory()
  }

  // 初始化
  function init() {
    loadFromCache()
    // 启动定时同步
    startSyncTimer()
    
    // 监听页面离开事件
    if (typeof window !== 'undefined') {
      window.addEventListener('beforeunload', onPageLeave)
      window.addEventListener('visibilitychange', () => {
        if (document.hidden) {
          onPageLeave()
        }
      })
    }
  }

  return {
    historyList,
    sortedHistoryList,
    todayHistory,
    featuredHistory,
    isSyncing,
    lastSyncTime,
    addHistory,
    removeHistory,
    clearHistory,
    syncHistory,
    init,
    stopSyncTimer
  }
})
