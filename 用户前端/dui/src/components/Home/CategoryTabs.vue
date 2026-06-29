<template>
  <!-- 风格1: 图标+文字分类卡片 -->
  <div class="category-tabs-card" v-if="type === 'card'">
    <div
      v-for="item in tabs"
      :key="item.id"
      :class="['tab-item', { active: activeId === item.id }]"
      @click="selectTab(item.id)"
    >
      <van-icon :name="item.icon" size="24" />
      <span class="tab-text">{{ item.name }}</span>
    </div>
  </div>

  <!-- 风格2: 纯文字可滑动标签 -->
  <div class="category-tabs-text" v-else-if="type === 'text'">
    <div 
      class="tabs-wrapper" 
      ref="tabsWrapperRef"
      @scroll="onScroll"
    >
      <div
        v-for="(item, index) in tabs"
        :key="item.id"
        :ref="el => setTabRef(el, index)"
        :class="['tab-item', { active: activeId === item.id }]"
        @click="selectTab(item.id)"
      >
        <span
          class="tab-text"
          :style="item.color && activeId !== item.id ? { color: item.color } : {}"
        >{{ item.name }}</span>
        <div class="tab-indicator" v-if="activeId === item.id"></div>
      </div>
    </div>
    <!-- 左右滑动提示 -->
    <div 
      v-if="showLeftHint" 
      class="scroll-hint left"
      @click="scrollToLeft"
    >
      <van-icon name="chevron-left" size="16" />
    </div>
    <div 
      v-if="showRightHint" 
      class="scroll-hint right"
      @click="scrollToRight"
    >
      <van-icon name="chevron-right" size="16" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from 'vue'

export interface TabItem {
  id: number | string
  name: string
  icon?: string
  color?: string
}

interface Props {
  type: 'card' | 'text'
  tabs: TabItem[]
  modelValue: number | string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update:modelValue', value: number | string): void
  (e: 'change', value: number | string, item: TabItem): void
}>()

const activeId = ref(props.modelValue)
const tabsWrapperRef = ref<Element | null>(null)
const tabRefs = ref<Element[]>([])
const showLeftHint = ref(false)
const showRightHint = ref(false)

// 设置tab元素引用
const setTabRef = (el: any, index: number) => {
  if (el && el.$el) {
    tabRefs.value[index] = el.$el
  } else if (el) {
    tabRefs.value[index] = el as Element
  }
}

// 监听外部modelValue变化
watch(() => props.modelValue, (val) => {
  activeId.value = val
  // 切换标签后自动滚动到居中位置
  nextTick(() => {
    scrollToActiveTab()
  })
})

// 监听tabs变化，更新提示状态
watch(() => props.tabs, () => {
  nextTick(() => {
    updateScrollHints()
  })
}, { deep: true })

// 选择标签
const selectTab = (id: number | string) => {
  activeId.value = id
  emit('update:modelValue', id)
  const item = props.tabs.find(t => t.id === id)
  if (item) {
    emit('change', id, item)
  }
  // 滚动到激活的标签
  nextTick(() => {
    scrollToActiveTab()
  })
}

// 滚动到激活的标签并居中
const scrollToActiveTab = () => {
  if (!tabsWrapperRef.value) return
  
  const activeIndex = props.tabs.findIndex(t => t.id === activeId.value)
  if (activeIndex === -1) return
  
  const activeTab = tabRefs.value[activeIndex]
  if (!activeTab) return
  
  const wrapper = tabsWrapperRef.value as HTMLElement
  const activeTabEl = activeTab as HTMLElement
  const wrapperWidth = wrapper.offsetWidth
  const tabLeft = activeTabEl.offsetLeft
  const tabWidth = activeTabEl.offsetWidth
  
  // 计算目标滚动位置（让激活的标签居中）
  const targetScrollLeft = tabLeft - (wrapperWidth - tabWidth) / 2
  
  // 边界处理
  const maxScroll = wrapper.scrollWidth - wrapperWidth
  const scrollLeft = Math.max(0, Math.min(targetScrollLeft, maxScroll))
  
  // 平滑滚动
  wrapper.scrollTo({
    left: scrollLeft,
    behavior: 'smooth'
  })
  
  // 更新提示状态
  updateScrollHints()
}

// 更新滚动提示
const updateScrollHints = () => {
  if (!tabsWrapperRef.value) return
  
  const wrapper = tabsWrapperRef.value as HTMLElement
  const scrollLeft = wrapper.scrollLeft
  const maxScroll = wrapper.scrollWidth - wrapper.offsetWidth
  
  showLeftHint.value = scrollLeft > 10
  showRightHint.value = maxScroll - scrollLeft > 10
}

// 滚动事件处理
const onScroll = () => {
  updateScrollHints()
}

// 向左滚动
const scrollToLeft = () => {
  if (!tabsWrapperRef.value) return
  const wrapper = tabsWrapperRef.value as HTMLElement
  wrapper.scrollBy({
    left: -200,
    behavior: 'smooth'
  })
}

// 向右滚动
const scrollToRight = () => {
  if (!tabsWrapperRef.value) return
  const wrapper = tabsWrapperRef.value as HTMLElement
  wrapper.scrollBy({
    left: 200,
    behavior: 'smooth'
  })
}

// 组件挂载时初始化
onMounted(() => {
  nextTick(() => {
    updateScrollHints()
  })
})
</script>

<style scoped>
/* 风格1: 图标+文字卡片 */
.category-tabs-card {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1px;
  background-color: #f5f5f5;
  margin: 10px 0;
}

.category-tabs-card .tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 8px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-tabs-card .tab-item:active {
  background-color: #f0f0f0;
}

.category-tabs-card .tab-item.active {
  color: #ee0a24;
}

.category-tabs-card .tab-item.active .tab-text {
  color: #ee0a24;
  font-weight: 600;
}

.category-tabs-card .van-icon {
  color: #666;
  margin-bottom: 8px;
}

.category-tabs-card .tab-item.active .van-icon {
  color: #ee0a24;
}

.category-tabs-card .tab-text {
  font-size: 13px;
  color: #333;
}

/* 风格2: 纯文字滑动标签 */
.category-tabs-text {
  background-color: #fff;
  padding: 0;
  border-bottom: 1px solid #f5f5f5;
  position: relative;
}

.category-tabs-text .tabs-wrapper {
  display: flex;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  gap: 0;
  padding: 0 12px;
}

.category-tabs-text .tabs-wrapper::-webkit-scrollbar {
  display: none;
}

.category-tabs-text .tab-item {
  position: relative;
  flex-shrink: 0;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-tabs-text .tab-text {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
  transition: all 0.2s ease;
}

.category-tabs-text .tab-item.active .tab-text {
  color: #ee0a24;
  font-weight: 600;
  font-size: 15px;
}

.category-tabs-text .tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background-color: #ee0a24;
  border-radius: 2px;
}

/* 滚动提示按钮 */
.scroll-hint {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 28px;
  height: 28px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s ease;
}

.scroll-hint.left {
  left: 0;
}

.scroll-hint.right {
  right: 0;
}

.scroll-hint:active {
  transform: translateY(-50%) scale(0.95);
  background-color: #fff;
}

.scroll-hint .van-icon {
  color: #666;
}
</style>