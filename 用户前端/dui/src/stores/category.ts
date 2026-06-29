import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { getLevel1Level2Categories, type CategoryWithLevel2 } from '@/api/category'

// 缓存过期时间（24小时）
const CACHE_EXPIRE_TIME = 24 * 60 * 60 * 1000

export interface CategoryCache {
  data: CategoryWithLevel2[]
  timestamp: number
}

export const useCategoryStore = defineStore('category', () => {
  // 分类数据
  const categories = ref<CategoryWithLevel2[]>([])
  
  // 是否正在加载
  const isLoading = ref(false)
  
  // 是否已经加载过
  const isLoaded = ref(false)

  // 获取分类数据（优先使用缓存）
  const getCategoryList = computed(() => categories.value)

  // 从 localStorage 加载缓存
  function loadFromCache(): CategoryCache | null {
    try {
      const cached = localStorage.getItem('category_cache')
      if (cached) {
        const cache: CategoryCache = JSON.parse(cached)
        // 检查缓存是否过期
        if (Date.now() - cache.timestamp < CACHE_EXPIRE_TIME) {
          return cache
        }
      }
    } catch (e) {
      console.error('Failed to load category cache:', e)
    }
    return null
  }

  // 保存到 localStorage
  function saveToCache(data: CategoryWithLevel2[]) {
    try {
      const cache: CategoryCache = {
        data,
        timestamp: Date.now()
      }
      localStorage.setItem('category_cache', JSON.stringify(cache))
    } catch (e) {
      console.error('Failed to save category cache:', e)
    }
  }

  // 加载分类数据
  async function loadCategories(forceRefresh = false): Promise<void> {
    // 如果正在加载，直接返回
    if (isLoading.value && !forceRefresh) {
      return
    }

    // 如果已经加载过且不需要强制刷新，直接返回
    if (isLoaded.value && !forceRefresh) {
      return
    }

    isLoading.value = true

    try {
      // 优先从缓存加载
      if (!forceRefresh) {
        const cache = loadFromCache()
        if (cache) {
          categories.value = cache.data
          isLoaded.value = true
          console.log('分类数据从缓存加载成功')
          return
        }
      }

      // 从后端获取数据（包含一级和二级分类）
      const result = await getLevel1Level2Categories()
      if (result.code === 200 && result.data) {
        categories.value = result.data
        isLoaded.value = true
        // 保存到缓存
        saveToCache(result.data)
        console.log('分类数据（含二级分类）从后端加载成功')
      }
    } catch (error) {
      console.error('加载分类数据失败:', error)
      // 不使用降级方案，保持空数据
      categories.value = []
      isLoaded.value = true
    } finally {
      isLoading.value = false
    }
  }

  // 本地默认图标映射表
  const defaultIcons: Record<string, string> = {
    '休闲零食': '🍪',
    '生鲜水果': '🍎',
    '酒水饮料': '🍷',
    '粮油调味': '🥣',
    '进口食品': '🌍',
    '牛奶乳品': '🥛',
    '方便速食': '🍜',
    '茶叶': '🍵',
    '咖啡': '☕',
    '保健品': '💊',
    '美妆护肤': '💄',
    '个人护理': '🧴',
    '家居用品': '🏠',
    '厨房用品': '🍳',
    '母婴用品': '🍼',
    '数码配件': '📱',
    '服饰鞋包': '👜',
    '运动户外': '⚽',
    '宠物用品': '🐾',
    '办公用品': '📝',
    '汽车内饰': '🚗',
    '汽车外饰': '🎨',
    '汽车电子': '🔌',
    '车载用品': '☕',
    '维修保养': '🔧',
    '轮胎轮毂': '🛞',
    '机油配件': '⚙️',
    '行车安全': '🛡️',
    '应急工具': '🔦',
    '汽车清洗': '🧼',
    '手机通讯': '📱',
    '电脑办公': '💻',
    '智能穿戴': '⌚',
    '影音设备': '🎬',
    '网络设备': '📶',
    '安防监控': '📹',
    '软件服务': '💽',
    '电子元器件': '🔬',
    '办公设备': '🖨️',
    '存储设备': '💾',
    '男装': '👔',
    '女装': '👗',
    '童装': '👧',
    '鞋靴': '👟',
    '箱包': '👜',
    '配饰': '🎩',
    '内衣': '👙',
    '运动服': '🏃',
    '羽绒服': '🧥',
    '针织衫': '🧶',
    // 新增分类图标
    '大家电': '📺',
    '小家电': '🧊',
    '厨房电器': '🍳',
    '生活电器': '💨',
    '影音家电': '🎧',
    '智能家电': '🤖',
    '空调': '❄️',
    '冰箱': '🧊',
    '洗衣机': '🧺',
    '家具': '🪑',
    '家纺': '🛏️',
    '家装建材': '🧱',
    '厨卫': '🚿',
    '收纳整理': '📦',
    '窗帘地毯': '🪟',
    '灯具': '💡',
    '壁纸': '🎭',
    '装饰品': '🖼️',
    '五金工具': '🔩',
    '面部护肤': '🧴',
    '彩妆': '💄',
    '香水': '💍',
    '身体护理': '🧴',
    '头发护理': '💇',
    '男士护理': '🧔',
    '护肤套装': '🎁',
    '面膜': '🧖',
    '精华液': '🧪',
    '防晒霜': '🧴',
    '运动服饰': '🏃',
    '运动鞋': '👟',
    '户外装备': '⛺',
    '健身器材': '🏋️',
    '球类': '⚽',
    '游泳用品': '🏊',
    '骑行装备': '🚴',
    '登山装备': '🧗',
    '运动配件': '🎾',
    '瑜伽用品': '🧘',
    '奶粉': '🍼',
    '纸尿裤': '🧻',
    '宝宝服饰': '👶',
    '玩具': '🧸',
    '辅食': '🍚',
    '洗护用品': '🧴',
    '喂养用品': '🍼',
    '安全座椅': '🚗',
    '推车': '🚼',
    '早教益智': '🧩'
  }

  // 获取默认图标
  function getDefaultIcon(name: string): string {
    return defaultIcons[name] || '📦'
  }

  // 获取二级分类图标（优先使用后端返回的URL，否则使用本地默认图标）
  function getLevel2Icon(level2Item: { category_name: string; icon?: string }): string {
    if (level2Item.icon && level2Item.icon.trim()) {
      // 如果是URL格式，返回URL；否则返回图标字符串
      if (level2Item.icon.startsWith('http://') || level2Item.icon.startsWith('https://')) {
        return level2Item.icon
      }
      return level2Item.icon
    }
    return getDefaultIcon(level2Item.category_name)
  }

  // 刷新分类数据（强制从后端获取）
  async function refreshCategories(): Promise<void> {
    await loadCategories(true)
  }

  // 清除缓存
  function clearCache() {
    localStorage.removeItem('category_cache')
    isLoaded.value = false
    categories.value = []
  }

  return {
    categories,
    isLoading,
    isLoaded,
    getCategoryList,
    loadCategories,
    refreshCategories,
    clearCache,
    getLevel2Icon
  }
})
