<template>
  <div class="train-ticket-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="火车票" left-arrow @click-left="goBack" />

    <!-- 搜索表单 -->
    <div class="search-form">
      <div class="form-card">
        <div class="route-section">
          <div class="city-selector">
            <div class="city-item" @click="selectFromCity">
              <span class="city-label">出发地</span>
              <span class="city-name">{{ fromCity }}</span>
              <van-icon name="arrow-down" />
            </div>
            <van-icon name="exchange" class="exchange-btn" @click="swapCities" />
            <div class="city-item" @click="selectToCity">
              <span class="city-label">目的地</span>
              <span class="city-name">{{ toCity }}</span>
              <van-icon name="arrow-down" />
            </div>
          </div>
        </div>

        <div class="date-section">
          <div class="date-item" @click="selectDate">
            <span class="date-label">出发日期</span>
            <span class="date-value">{{ travelDate }}</span>
            <van-icon name="calendar-o" />
          </div>
        </div>

        <van-button type="primary" block size="large" @click="searchTickets">
          查询车票
        </van-button>
      </div>
    </div>

    <!-- 结果列表 -->
    <div v-if="tickets.length > 0" class="tickets-section">
      <h3 class="section-title">查询结果</h3>
      
      <div class="ticket-list">
        <div v-for="ticket in tickets" :key="ticket.id" class="ticket-card">
          <div class="ticket-header">
            <span class="train-number">{{ ticket.trainNumber }}</span>
            <van-tag :type="getSeatTypeColor(ticket.seatType)" size="medium">
              {{ ticket.seatType }}
            </van-tag>
          </div>
          
          <div class="ticket-route">
            <div class="time-info">
              <span class="time">{{ ticket.departureTime }}</span>
              <span class="station">{{ ticket.fromStation }}</span>
            </div>
            <div class="duration">
              <span>{{ ticket.duration }}</span>
              <van-icon name="arrow" />
            </div>
            <div class="time-info right">
              <span class="time">{{ ticket.arrivalTime }}</span>
              <span class="station">{{ ticket.toStation }}</span>
            </div>
          </div>
          
          <div class="ticket-footer">
            <div class="price-info">
              <span class="price-label">票价：</span>
              <span class="price-value">¥{{ ticket.price }}</span>
            </div>
            <div class="stock-info">
              <span>余票：{{ ticket.stock }}张</span>
            </div>
            <van-button 
              type="danger" 
              size="small" 
              round 
              :disabled="ticket.stock === 0"
              @click="buyTicket(ticket)"
            >
              {{ ticket.stock > 0 ? '立即预订' : '已售罄' }}
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <van-empty v-else-if="hasSearched" description="未找到符合条件的车票" image="search" />

    <!-- 城市选择弹窗 -->
    <van-popup v-model:show="showCityPicker" position="bottom">
      <van-picker
        :columns="cityList"
        @confirm="onCityConfirm"
        @cancel="showCityPicker = false"
      />
    </van-popup>

    <!-- 日期选择弹窗 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="currentDate"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

// 城市选择
const fromCity = ref('北京')
const toCity = ref('上海')
const showCityPicker = ref(false)
const selectingCity = ref<'from' | 'to'>('from')

// 日期选择
const travelDate = ref('2026-06-26')
const showDatePicker = ref(false)
const currentDate = ref(['2026', '06', '26'])
const minDate = new Date(2026, 5, 26)
const maxDate = new Date(2026, 11, 31)

// 搜索结果
const hasSearched = ref(false)
const tickets = ref<any[]>([])

// 城市列表（示例数据）
const cityList = [
  { text: '北京' },
  { text: '上海' },
  { text: '广州' },
  { text: '深圳' },
  { text: '杭州' },
  { text: '南京' },
  { text: '成都' },
  { text: '武汉' },
  { text: '西安' },
  { text: '重庆' },
  { text: '天津' },
  { text: '苏州' }
]

// 选择出发城市
const selectFromCity = () => {
  selectingCity.value = 'from'
  showCityPicker.value = true
}

// 选择到达城市
const selectToCity = () => {
  selectingCity.value = 'to'
  showCityPicker.value = true
}

// 交换城市
const swapCities = () => {
  const temp = fromCity.value
  fromCity.value = toCity.value
  toCity.value = temp
}

// 确认城市选择
const onCityConfirm = ({ selectedOptions }: any) => {
  if (selectingCity.value === 'from') {
    fromCity.value = selectedOptions[0].text
  } else {
    toCity.value = selectedOptions[0].text
  }
  showCityPicker.value = false
}

// 选择日期
const selectDate = () => {
  showDatePicker.value = true
}

// 确认日期选择
const onDateConfirm = ({ selectedValues }: any) => {
  travelDate.value = `${selectedValues[0]}-${String(selectedValues[1]).padStart(2, '0')}-${String(selectedValues[2]).padStart(2, '0')}`
  showDatePicker.value = false
}

// 查询车票
const searchTickets = () => {
  hasSearched.value = true
  
  // 模拟数据
  tickets.value = [
    {
      id: 1,
      trainNumber: 'G1234',
      seatType: '二等座',
      fromStation: fromCity.value,
      toStation: toCity.value,
      departureTime: '08:00',
      arrivalTime: '12:30',
      duration: '4小时30分',
      price: 553.5,
      stock: 120
    },
    {
      id: 2,
      trainNumber: 'G5678',
      seatType: '一等座',
      fromStation: fromCity.value,
      toStation: toCity.value,
      departureTime: '10:15',
      arrivalTime: '14:45',
      duration: '4小时30分',
      price: 933.5,
      stock: 45
    },
    {
      id: 3,
      trainNumber: 'D9012',
      seatType: '二等座',
      fromStation: fromCity.value,
      toStation: toCity.value,
      departureTime: '14:30',
      arrivalTime: '19:20',
      duration: '4小时50分',
      price: 453.5,
      stock: 80
    },
    {
      id: 4,
      trainNumber: 'G3456',
      seatType: '商务座',
      fromStation: fromCity.value,
      toStation: toCity.value,
      departureTime: '16:00',
      arrivalTime: '20:15',
      duration: '4小时15分',
      price: 1748.5,
      stock: 12
    }
  ]
  
  showToast(`已查询到 ${tickets.value.length} 个车次`)
}

// 购买车票
const buyTicket = (ticket: any) => {
  showToast(`正在预订 ${ticket.trainNumber} 次列车`)
  // 这里可以跳转到订单页面
}

// 获取座位类型颜色
const getSeatTypeColor = (seatType: string) => {
  switch (seatType) {
    case '商务座':
      return 'warning'
    case '一等座':
      return 'success'
    case '二等座':
      return 'primary'
    default:
      return 'default'
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  // 初始化时可以加载热门线路等
})
</script>

<style scoped>
.train-ticket-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 20px;
}

/* 搜索表单 */
.search-form {
  padding: 15px;
}

.form-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.route-section {
  margin-bottom: 20px;
}

.city-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.city-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  background: #f7f8fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.city-item:hover {
  background: #e8eaed;
}

.city-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.city-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.exchange-btn {
  font-size: 24px;
  color: #666;
  cursor: pointer;
  padding: 10px;
}

.date-section {
  margin-bottom: 20px;
}

.date-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: #f7f8fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.date-item:hover {
  background: #e8eaed;
}

.date-label {
  font-size: 14px;
  color: #666;
}

.date-value {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

/* 结果区域 */
.tickets-section {
  padding: 0 15px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
  padding: 0 5px;
}

.ticket-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ticket-card {
  background: #fff;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.train-number {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.ticket-route {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.time-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.time-info.right {
  text-align: right;
}

.time {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.station {
  font-size: 14px;
  color: #666;
}

.duration {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 12px;
}

.ticket-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.price-label {
  font-size: 12px;
  color: #999;
}

.price-value {
  font-size: 20px;
  font-weight: bold;
  color: #ff4757;
}

.stock-info span {
  font-size: 13px;
  color: #666;
}
</style>
