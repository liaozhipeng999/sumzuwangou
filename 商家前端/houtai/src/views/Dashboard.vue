<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(stat, index) in statCards" :key="index">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-title">{{ stat.title }}</p>
              <p class="stat-value" v-html="stat.value"></p>
              <p class="stat-desc" v-if="stat.growth !== null">
                较上月
                <span :class="stat.growth >= 0 ? 'up' : 'down'">
                  {{ stat.growth >= 0 ? '↑' : '↓' }}
                  {{ Math.abs(stat.growth) }}%
                </span>
              </p>
              <p class="stat-desc" v-else>&nbsp;</p>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="40" :color="'#fff'">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="timeRange" size="small" @change="reload">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">本年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>商品分类占比</span>
          </template>
          <div ref="categoryChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>商品销售排行</span>
          </template>
          <div ref="rankingChart" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新订单</span>
          </template>
          <el-table :data="recentOrders" style="width: 100%" empty-text="暂无订单">
            <el-table-column prop="orderNo" label="订单号" width="180" show-overflow-tooltip />
            <el-table-column prop="customerName" label="客户" width="90" />
            <el-table-column prop="totalAmount" label="金额" width="90" align="right">
              <template #default="{ row }">
                <span style="color: #f56c6c; font-weight: bold;">¥{{ row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ row.statusText }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ShoppingCart,
  Money,
  User,
  DocumentChecked
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardStats, type DashboardData } from '@/api/dashboard'

const timeRange = ref<'week' | 'month' | 'year'>('week')
const loading = ref(false)
const shopId = ref<number | null>(null)

const trendChart = ref<HTMLElement | null>(null)
const categoryChart = ref<HTMLElement | null>(null)
const rankingChart = ref<HTMLElement | null>(null)
let trendChartInstance: any = null
let categoryChartInstance: any = null
let rankingChartInstance: any = null

const statCards = reactive<any[]>([
  { title: '今日销售额', value: '¥0', growth: 0, color: '#409eff', icon: Money },
  { title: '今日订单', value: '0笔', growth: 0, color: '#67c23a', icon: ShoppingCart },
  { title: '待发货订单', value: '0笔', growth: null, color: '#e6a23c', icon: User },
  { title: '累计销售额', value: '¥0', growth: null, color: '#f56c6c', icon: DocumentChecked }
])

const recentOrders = ref<any[]>([])
const topProducts = ref<any[]>([])
const categories = ref<any[]>([])
const trendData = ref<{ labels: string[]; sales: number[]; orders: number[] }>({ labels: [], sales: [], orders: [] })

const getStatusType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'warning',
    paid: 'warning',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

const fmtMoney = (v: number) => {
  const n = Number(v) || 0
  if (n >= 10000) return '¥' + (n / 10000).toFixed(2) + 'w'
  return '¥' + n.toFixed(2)
}

const loadDashboard = async () => {
  if (!shopId.value) return
  loading.value = true
  try {
    const data: DashboardData = await getDashboardStats(shopId.value, timeRange.value)
    const cards = data.statCards

    statCards[0].value = fmtMoney(cards.todaySales)
    statCards[0].growth = cards.salesGrowth
    statCards[1].value = `${cards.todayOrders}笔`
    statCards[1].growth = cards.ordersGrowth
    statCards[2].value = `${cards.pendingShip}笔`
    statCards[3].value = fmtMoney(cards.totalSales)

    trendData.value = data.trend[0] || { labels: [], sales: [], orders: [] }
    topProducts.value = data.topProducts || []
    categories.value = data.categories || []
    recentOrders.value = (data.recentOrders || []).map((o: any) => ({
      ...o,
      totalAmount: Number(o.totalAmount) || 0
    }))

    await nextTick()
    renderTrend()
    renderCategory()
    renderRanking()
  } catch (e: any) {
    ElMessage.error(e?.message || '加载看板数据失败')
  } finally {
    loading.value = false
  }
}

const reload = () => loadDashboard()

const renderTrend = () => {
  if (!trendChart.value) return
  if (trendChartInstance) trendChartInstance.dispose()
  trendChartInstance = echarts.init(trendChart.value)

  const d = trendData.value
  const hasData = d.labels.length > 0

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: { color: '#303133' }
    },
    legend: { data: ['销售额', '订单数'], top: 0, textStyle: { color: '#606266' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: hasData ? d.labels : ['暂无数据'],
      axisLine: { lineStyle: { color: '#ebeef5' } },
      axisLabel: { color: '#909399' }
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额(元)',
        nameTextStyle: { color: '#909399' },
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: {
          color: '#909399',
          formatter: (v: number) => v >= 10000 ? (v / 10000).toFixed(1) + 'w' : v
        },
        splitLine: { lineStyle: { color: '#ebeef5' } }
      },
      {
        type: 'value',
        name: '订单数',
        nameTextStyle: { color: '#909399' },
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#909399' },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: hasData ? d.sales : [0],
        lineStyle: { width: 3, color: '#409eff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        itemStyle: { color: '#409eff' }
      },
      {
        name: '订单数',
        type: 'bar',
        yAxisIndex: 1,
        data: hasData ? d.orders : [0],
        barWidth: '50%',
        itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] }
      }
    ]
  }
  trendChartInstance.setOption(option)
}

const renderCategory = () => {
  if (!categoryChart.value) return
  if (categoryChartInstance) categoryChartInstance.dispose()
  categoryChartInstance = echarts.init(categoryChart.value)

  const data = categories.value
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#b37feb', '#36cfc9', '#ff85c0', '#909399']
  const pieData = data.length > 0 ? data.map((c, i) => ({
    name: c.name,
    value: c.value,
    itemStyle: { color: colors[i % colors.length] }
  })) : [{ name: '暂无数据', value: 0, itemStyle: { color: '#dcdfe6' } }]

  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: { color: '#303133' },
      formatter: (p: any) => `${p.name}<br/>销量: ${p.value}`
    },
    legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { color: '#606266' } },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#303133' } },
      labelLine: { show: false },
      data: pieData
    }]
  }
  categoryChartInstance.setOption(option)
}

const renderRanking = () => {
  if (!rankingChart.value) return
  if (rankingChartInstance) rankingChartInstance.dispose()
  rankingChartInstance = echarts.init(rankingChart.value)

  const data = topProducts.value
  const hasData = data.length > 0
  const names = hasData ? data.map((p: any) => p.name.length > 12 ? p.name.substring(0, 12) + '…' : p.name) : ['暂无商品']
  const sales = hasData ? data.map((p: any) => p.sales) : [0]
  const colors = ['#f56c6c', '#e6a23c', '#409eff', '#67c23a', '#909399']
  const barData = hasData
    ? data.map((p: any, i: number) => ({ value: p.sales, itemStyle: { color: colors[i % colors.length], borderRadius: [0, 4, 4, 0] } }))
    : [{ value: 0, itemStyle: { color: '#dcdfe6', borderRadius: [0, 4, 4, 0] } }]

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: { color: '#303133' }
    },
    grid: { left: '3%', right: '10%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#909399' },
      splitLine: { lineStyle: { color: '#ebeef5' } }
    },
    yAxis: {
      type: 'category',
      data: names.reverse(),
      inverse: false,
      axisLine: { lineStyle: { color: '#ebeef5' } },
      axisLabel: { color: '#303133' }
    },
    series: [{
      type: 'bar',
      data: barData.reverse(),
      barWidth: '60%',
      label: { show: true, position: 'right', color: '#606266', formatter: '{c}' }
    }]
  }
  rankingChartInstance.setOption(option)
}

const handleResize = () => {
  trendChartInstance?.resize()
  categoryChartInstance?.resize()
  rankingChartInstance?.resize()
}

onMounted(() => {
  const merchantInfoRaw = localStorage.getItem('merchant_info')
  const mid = merchantInfoRaw ? (() => { try { return JSON.parse(merchantInfoRaw).id } catch { return null } })() : null
  if (!mid) {
    ElMessage.warning('请先登录')
    return
  }
  shopId.value = mid
  loadDashboard()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  categoryChartInstance?.dispose()
  rankingChartInstance?.dispose()
})

watch(timeRange, () => loadDashboard())
</script>

<style scoped>
.dashboard { padding: 0; }
.stat-card { margin-bottom: 20px; }
.stat-content { display: flex; justify-content: space-between; align-items: center; }
.stat-info { flex: 1; }
.stat-title { font-size: 14px; color: #909399; margin: 0 0 10px 0; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; margin: 0 0 10px 0; }
.stat-desc { font-size: 12px; color: #909399; margin: 0; }
.stat-desc .up { color: #67c23a; font-weight: bold; }
.stat-desc .down { color: #f56c6c; font-weight: bold; }
.stat-icon { width: 80px; height: 80px; border-radius: 50%; display: flex; align-items: center; justify-content: center; opacity: 0.8; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.chart-container { width: 100%; height: 300px; }
</style>
