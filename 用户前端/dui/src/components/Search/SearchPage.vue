<template>
  <div class="search-page">
    <!-- 顶部搜索栏 -->
    <van-nav-bar fixed placeholder bg-color="#fff">
      <template #left>
        <van-icon name="arrow-left" size="22" class="back-btn" @click="goBack" />
      </template>
      <template #title>
        <div class="search-bar" @click="focusSearchBox">
          <van-icon name="search" size="18" color="#999" />
          <input
            ref="searchInputRef"
            v-model="searchKeyword"
            class="search-input"
            placeholder="搜索商品"
            @keyup.enter="handleSearch"
          />
          <van-icon
            v-if="searchKeyword"
            name="clear"
            size="14"
            color="#999"
            class="clear-icon"
            @click.stop="clearKeyword"
          />
        </div>
      </template>
    </van-nav-bar>

    <van-pull-refresh
      v-model="refreshing"
      @refresh="onRefresh"
      :head-height="60"
      success-text="刷新成功"
      :success-duration="600"
    >
      <!-- 未搜索：历史 + 发现 -->
      <div v-if="!showResults" class="home">
        <div v-if="searchHistory.length" class="section">
          <div class="section-row">
            <span class="section-title">最近搜索</span>
            <span class="section-action" @click="handleClearHistory">
              <van-icon name="delete-o" size="14" /> 清空
            </span>
          </div>
          <div class="tag-wrap">
            <div
              v-for="(t, i) in searchHistory"
              :key="i"
              class="tag-chip"
              @click="handleSearchKeyword(t)"
            >{{ t }}</div>
          </div>
        </div>

        <div class="section">
          <div class="section-row">
            <span class="section-title">大家都在搜</span>
            <span class="section-action">
              <van-icon name="chart-trends-o" size="14" color="#ee0a24" />
            </span>
          </div>
          <div class="discover-wrap">
            <div
              v-for="(t, i) in hotKeywords"
              :key="i"
              class="discover-item"
              @click="handleSearchKeyword(t)"
            >
              <span :class="['rank', 'rank-' + (i + 1)]">{{ i + 1 }}</span>
              <span class="discover-text">{{ t }}</span>
            </div>
          </div>
        </div>

        <div class="section" v-if="recommendProducts.length">
          <div class="section-row">
            <span class="section-title">为你推荐</span>
          </div>
          <div class="rec-grid">
            <div
              v-for="item in recommendProducts"
              :key="item.id"
              class="rec-card"
              @click="goProduct(item.id)"
            >
              <van-image :src="item.mainImage" fit="cover" class="rec-img" />
              <div class="rec-info">
                <div class="rec-name">{{ item.productName }}</div>
                <div class="rec-price">¥{{ item.price }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 搜索结果 -->
      <div v-else class="result">
        <div class="result-meta" v-if="totalCount">
          找到 <span class="red">{{ totalCount }}</span> 件商品
        </div>

        <!-- 排序栏 -->
        <div class="sort-bar">
          <div
            :class="['sort-item', { active: currentSort === 'default' }]"
            @click="changeSort('default')"
          >
            <span>综合</span>
          </div>
          <div
            :class="['sort-item', { active: currentSort === 'sales_asc' || currentSort === 'sales_desc' }]"
            @click="changeSalesSort"
          >
            <span>销量</span>
            <span class="arrow-pair">
              <van-icon name="arrow-up" size="10" :class="['arrow', { on: currentSort === 'sales_asc' }]" />
              <van-icon name="arrow-down" size="10" :class="['arrow', { on: currentSort === 'sales_desc' }]" />
            </span>
          </div>
          <div
            :class="['sort-item', { active: currentSort === 'price_asc' || currentSort === 'price_desc' }]"
            @click="changePriceSort"
          >
            <span>价格</span>
            <span class="arrow-pair">
              <van-icon name="arrow-up" size="10" :class="['arrow', { on: currentSort === 'price_asc' }]" />
              <van-icon name="arrow-down" size="10" :class="['arrow', { on: currentSort === 'price_desc' }]" />
            </span>
          </div>
          <div
            :class="['sort-item', { active: currentSort === 'brand' }]"
            @click="changeSort('brand')"
          >
            <span>品牌</span>
          </div>
          <div
            :class="['sort-item filter', { active: showFilter }]"
            @click="toggleFilter"
          >
            <van-icon name="filter-o" size="14" :color="showFilter ? '#ee0a24' : '#333'" />
            <span>筛选</span>
          </div>
        </div>

        <van-list
          v-model:loading="listLoading"
          :finished="finished"
          finished-text="没有更多了"
          :immediate-check="true"
          @load="onLoadMore"
        >
          <div class="product-grid">
            <div
              v-for="item in searchProductsList"
              :key="item.id"
              class="product-card"
              @click="goProduct(item.id)"
            >
              <van-image :src="item.mainImage" fit="cover" class="product-img" />
              <div class="product-body">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-brief">{{ item.brief }}</div>
                <div class="product-foot">
                  <span class="product-price">¥{{ item.price }}</span>
                  <span class="product-sales">{{ item.sales }}人付款</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="searchProductsList.length === 0 && !listLoading && !initialLoading" class="empty-state">
            <van-empty description="没有找到相关商品" />
          </div>
        </van-list>
      </div>
    </van-pull-refresh>

    <!-- 筛选弹窗 -->
    <van-popup v-model:show="showFilter" position="bottom" round>
      <div class="filter-popup">
        <div class="filter-header">
          <span class="filter-title">筛选</span>
          <van-icon name="cross" size="20" @click="showFilter = false" />
        </div>

        <div class="filter-body">
          <div class="filter-section">
            <div class="filter-section-title">价格区间</div>
            <div class="price-chips">
              <div
                v-for="r in priceChips"
                :key="r.value"
                :class="['chip', { active: priceRange === r.value }]"
                @click="priceRange = r.value"
              >{{ r.label }}</div>
            </div>
            <div class="price-inputs">
              <van-field
                v-model="minPrice"
                placeholder="最低"
                type="number"
                class="price-field"
                @focus="priceRange = ''"
              />
              <span class="sep">-</span>
              <van-field
                v-model="maxPrice"
                placeholder="最高"
                type="number"
                class="price-field"
                @focus="priceRange = ''"
              />
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-section-title">服务</div>
            <div class="filter-row">
              <div
                v-for="s in services"
                :key="s"
                :class="['chip small', { active: selectedServices.includes(s) }]"
                @click="toggleArr(selectedServices, s)"
              >{{ s }}</div>
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-section-title">发货地</div>
            <div class="filter-row">
              <div
                v-for="l in locations"
                :key="l"
                :class="['chip small', { active: selectedLocations.includes(l) }]"
                @click="toggleArr(selectedLocations, l)"
              >{{ l }}</div>
            </div>
          </div>
        </div>

        <div class="filter-footer">
          <button class="btn-reset" @click="resetFilter">重置</button>
          <button class="btn-ok" @click="applyFilter">确定</button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import {
  searchProducts,
  getSearchHistory,
  saveSearchHistory,
  clearSearchHistory,
  deleteSearchHistory,
  getHotSearch,
  type SearchProduct
} from '@/api/search'
import { getHotProducts } from '@/api/recommend'

const router = useRouter()
const route = useRoute()

const searchInputRef = ref<HTMLInputElement | null>(null)

const searchKeyword = ref('')
const showResults = ref(false)
const initialLoading = ref(false)

const searchHistory = ref<string[]>([])
const hotKeywords = ref<string[]>([])
const recommendProducts = ref<SearchProduct[]>([])

const searchProductsList = ref<SearchProduct[]>([])
const listLoading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const currentPage = ref(1)
const totalCount = ref(0)
const pageSize = 10

const currentSort = ref('default')
const priceAsc = ref(true)

const showFilter = ref(false)
const priceRange = ref('')
const minPrice = ref('')
const maxPrice = ref('')
const selectedServices = ref<string[]>([])
const selectedLocations = ref<string[]>([])

const priceChips = [
  { label: '0-88',    value: '0-88' },
  { label: '88-228',  value: '88-228' },
  { label: '228-340', value: '228-340' },
  { label: '340以上',  value: '340+' },
]
const services = ['退货包运费', '顺丰包邮', '正品发票', '只换不修', '假一赔十']
const locations = ['广东', '安徽', '上海', '浙江', '四川', '广西']

const focusSearchBox = () => {
  nextTick(() => searchInputRef.value?.focus())
}

const clearKeyword = () => {
  searchKeyword.value = ''
  searchInputRef.value?.focus()
}

const toggleArr = (arr: string[], val: string) => {
  const i = arr.indexOf(val)
  if (i > -1) arr.splice(i, 1)
  else arr.push(val)
}

const changeSort = (sort: string) => {
  if (sort === 'sales') {
    changeSalesSort()
    return
  }
  if (sort === currentSort.value) return
  currentSort.value = sort
  refreshSearch()
}

const changeSalesSort = () => {
  if (currentSort.value === 'sales_desc') {
    currentSort.value = 'sales_asc'
  } else {
    currentSort.value = 'sales_desc'
  }
  refreshSearch()
}

const changePriceSort = () => {
  if (currentSort.value === 'price_asc') {
    currentSort.value = 'price_desc'
  } else {
    currentSort.value = 'price_asc'
  }
  refreshSearch()
}

const toggleFilter = () => { showFilter.value = !showFilter.value }

const resetFilter = () => {
  priceRange.value = ''
  minPrice.value = ''
  maxPrice.value = ''
  selectedServices.value = []
  selectedLocations.value = []
}

const applyFilter = () => {
  showFilter.value = false
  refreshSearch()
}

const refreshSearch = () => {
  if (showResults.value && searchKeyword.value) {
    currentPage.value = 1
    finished.value = false
    searchProductsList.value = []
    initialLoading.value = true
    listLoading.value = true
    fetchPage()
  }
}

const buildFilters = () => {
  const f: Record<string, any> = {}
  if (priceRange.value) {
    const [a, b] = priceRange.value.split('-')
    if (b) { f.minPrice = Number(a); f.maxPrice = Number(b) }
    else if (priceRange.value.endsWith('+')) { f.minPrice = Number(priceRange.value.replace('+', '')) }
  } else {
    if (minPrice.value) f.minPrice = Number(minPrice.value)
    if (maxPrice.value) f.maxPrice = Number(maxPrice.value)
  }
  if (selectedServices.value.length)  f.services  = selectedServices.value.join(',')
  if (selectedLocations.value.length) f.locations = selectedLocations.value.join(',')
  return f
}

const fetchPage = async () => {
  try {
    const sortParam = currentSort.value === 'price_asc'  ? 'price_asc'
                    : currentSort.value === 'price_desc' ? 'price_desc'
                    : currentSort.value
    const res = await searchProducts(
      searchKeyword.value,
      undefined,
      currentPage.value,
      pageSize,
      sortParam,
      buildFilters()
    )
    if (res.code === 200 && res.data) {
      const prods = res.data.products || []
      totalCount.value = res.data.total ?? 0
      if (currentPage.value === 1) {
        searchProductsList.value = prods
      } else {
        searchProductsList.value.push(...prods)
      }
      if (searchProductsList.value.length >= totalCount.value || prods.length < pageSize) {
        finished.value = true
      }
    } else {
      finished.value = true
    }
  } catch (e) {
    console.error('[SearchPage] fetchPage error', e)
    finished.value = true
  } finally {
    listLoading.value = false
    initialLoading.value = false
  }
}

const onLoadMore = () => {
  if (finished.value) return
  currentPage.value++
  fetchPage()
}

const onRefresh = async () => {
  if (showResults.value && searchKeyword.value) {
    currentPage.value = 1
    finished.value = false
    searchProductsList.value = []
    await fetchPage()
  } else {
    await Promise.all([loadHistory(), loadHotKeywords(), loadRecommendProducts()])
  }
  refreshing.value = false
}

const handleSearch = () => {
  const kw = searchKeyword.value.trim()
  if (!kw) { showToast('请输入关键词'); return }
  performSearch(kw)
}

const handleSearchKeyword = (kw: string) => {
  searchKeyword.value = kw
  performSearch(kw)
}

const performSearch = async (kw: string) => {
  showResults.value = true
  currentPage.value = 1
  finished.value = false
  searchProductsList.value = []
  initialLoading.value = true
  listLoading.value = true
  await saveSearchHistory(kw)
  await fetchPage()
}

const removeHistory = async (keyword: string) => {
  try {
    const r = await deleteSearchHistory(keyword)
    if (r.code === 200) {
      searchHistory.value = searchHistory.value.filter(x => x !== keyword)
    }
  } catch (e) { console.error(e) }
}

const handleClearHistory = async () => {
  const r = await clearSearchHistory()
  if (r.code === 200) {
    searchHistory.value = []
    showToast('已清除')
  }
}

const goProduct = (id: number) => router.push(`/product/${id}`)
const goBack = () => router.back()

const loadHistory = async () => {
  try {
    const res = await getSearchHistory()
    if (res.code === 200) searchHistory.value = res.data || []
  } catch (e) { console.error(e) }
}
const loadHotKeywords = async () => {
  try {
    const res = await getHotSearch(15)
    if (res.code === 200) hotKeywords.value = res.data || []
  } catch (e) { console.error(e) }
}
const loadRecommendProducts = async () => {
  try {
    const res = await getHotProducts(6)
    if (res.code === 200) recommendProducts.value = res.data || []
  } catch (e) { console.error(e) }
}

onMounted(async () => {
  const keyword = (route.query.keyword as string)?.trim()
  if (keyword) {
    searchKeyword.value = keyword
    await performSearch(keyword)
  } else {
    await Promise.all([loadHistory(), loadHotKeywords(), loadRecommendProducts()])
  }
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #f4f4f6;
  padding-bottom: 80px;
}

.back-btn {
  padding: 8px 6px;
  color: #333;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f5f6f8;
  border-radius: 999px;
  padding: 6px 14px;
  width: 100%;
  max-width: 360px;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  font-size: 14px;
  color: #222;
}

.clear-icon {
  flex-shrink: 0;
}

.section {
  background: #fff;
  margin: 10px 10px 0;
  padding: 14px 14px 16px;
  border-radius: 10px;
}

.section-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
}

.section-action {
  font-size: 12px;
  color: #999;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.tag-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-chip {
  padding: 5px 12px;
  border-radius: 6px;
  background: #f5f6f8;
  color: #444;
  font-size: 13px;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.discover-wrap {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.discover-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 2px 0;
}

.rank {
  width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  background: #bfbfbf;
  flex-shrink: 0;
}
.rank-1 { background: #ff4d4f; }
.rank-2 { background: #ff7a45; }
.rank-3 { background: #ffc53d; }

.discover-text {
  font-size: 14px;
  color: #333;
}

.rec-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.rec-card {
  background: #fafafa;
  border-radius: 10px;
  overflow: hidden;
}

.rec-img {
  width: 100%;
  height: 130px;
}

.rec-info {
  padding: 8px 10px 10px;
}

.rec-name {
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rec-price {
  font-size: 15px;
  color: #ee0a24;
  font-weight: 700;
  margin-top: 4px;
}

/* 结果页 */
.result {
  padding-bottom: 30px;
}

.result-meta {
  padding: 8px 12px 4px;
  font-size: 12px;
  color: #888;
}

.result-meta .red {
  color: #ee0a24;
  font-weight: 600;
}

.sort-bar {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 8px 6px;
  margin: 0;
  position: sticky;
  top: 46px;
  z-index: 20;
  border-bottom: 1px solid #f0f0f2;
}

.sort-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
  font-size: 13px;
  color: #555;
  padding: 8px 0;
  position: relative;
}

.sort-item.active {
  color: #ee0a24;
  font-weight: 600;
}

.sort-item.filter {
  color: #333;
}

.arrow-pair {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  line-height: 1;
  margin-left: 2px;
  gap: 0;
}

.arrow-pair .arrow {
  color: #bbb;
  font-size: 10px;
  line-height: 1;
  margin: -2px 0;
  transition: color .15s, transform .15s;
}

.arrow-pair .arrow.on {
  color: #ee0a24;
  transform: scale(1.1);
}

.sort-item.active .arrow-pair .arrow:not(.on) {
  color: #e8a8a8;
}

/* 商品列表 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  padding: 10px;
}

.product-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.product-img {
  width: 100%;
  height: 170px;
  background: #f7f8fa;
}

.product-body {
  padding: 10px 12px 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 500;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 36px;
}

.product-brief {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-foot {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.product-price {
  color: #ee0a24;
  font-size: 17px;
  font-weight: 700;
}

.product-sales {
  font-size: 12px;
  color: #aaa;
}

.empty-state {
  padding: 40px 0 20px;
}

/* 筛选弹窗 */
.filter-popup {
  background: #fff;
  border-radius: 16px 16px 0 0;
  max-height: 75vh;
  display: flex;
  flex-direction: column;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f2f2f4;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
}

.filter-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #222;
  margin-bottom: 10px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.price-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.chip {
  padding: 6px 14px;
  border-radius: 6px;
  background: #f4f5f7;
  color: #333;
  font-size: 13px;
  border: 1px solid transparent;
  transition: all .15s;
}

.chip.active {
  background: #fff0f0;
  color: #ee0a24;
  border-color: #ffcccc;
}

.chip.small {
  padding: 5px 12px;
  font-size: 12px;
}

.price-inputs {
  display: flex;
  align-items: center;
  gap: 6px;
}

.price-field {
  flex: 1;
}

.sep {
  color: #999;
}

.filter-footer {
  display: flex;
  gap: 10px;
  padding: 14px 16px calc(14px + env(safe-area-inset-bottom));
  border-top: 1px solid #f2f2f4;
}

.filter-footer button {
  flex: 1;
  height: 42px;
  border-radius: 21px;
  font-size: 15px;
  border: none;
  cursor: pointer;
}

.btn-reset {
  background: #f4f5f7;
  color: #444;
}

.btn-ok {
  background: linear-gradient(135deg, #ff5252, #ee0a24);
  color: #fff;
  font-weight: 600;
}
</style>
