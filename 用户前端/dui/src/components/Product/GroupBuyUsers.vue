<template>
  <div class="group-buy-section">
    <!-- 顶部统计信息 -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-number">{{ currentStats.buyingCount }}</span>
        <span class="stat-label">人在拼</span>
      </div>
      <span class="stat-divider">，</span>
      <div class="stat-item">
        <span class="stat-label">同类畅销</span>
        <span class="stat-rank">第{{ currentStats.rank }}名</span>
      </div>
    </div>

    <!-- 用户列表区域 -->
    <div class="users-container">
      <div class="user-card" v-for="(user, index) in displayedUsers" :key="index">
        <!-- 用户头像组 -->
        <div class="avatar-group">
          <div 
            class="avatar-item"
            v-for="(avatar, idx) in user.avatars" 
            :key="idx"
            :style="{ zIndex: user.avatars.length - idx }"
          >
            <img :src="avatar" :alt="`用户${idx + 1}`" />
          </div>
        </div>

        <!-- 用户信息 -->
        <div class="user-info">
          <div class="user-name">{{ user.names }}</div>
        </div>

        <!-- 倒计时区域 -->
        <div class="countdown-section">
          <div class="countdown-label">拼单即将结束</div>
          <div class="countdown-time">
            <span class="time-unit">{{ formatTime(user.countdown.hours) }}</span>
            <span class="time-separator">:</span>
            <span class="time-unit">{{ formatTime(user.countdown.minutes) }}</span>
            <span class="time-separator">:</span>
            <span class="time-unit">{{ formatTime(user.countdown.seconds) }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-btn">
          <span class="btn-badge">无需等待</span>
          <span class="btn-text">直接拼成</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { buildAvatarSvg } from '@/utils/request'


interface UserCountdown {
  hours: number
  minutes: number
  seconds: number
}

interface GroupBuyUser {
  id: number
  avatars: string[]
  names: string
  tags: string[]
  countdown: UserCountdown
}

interface Stats {
  buyingCount: number
  rank: number
}

function av(seed: string): string {
  return buildAvatarSvg(seed)
}

// 用户数据（从后端获取）
const groupBuyUsers = ref<GroupBuyUser[]>([])

// 当前显示的用户索引
const currentUserIndex = ref(0)

// 当前统计数据
const currentStats = ref<Stats>({
  buyingCount: 0,
  rank: 0
})

// 定时器
let userTimer: ReturnType<typeof setInterval> | null = null
let countdownTimer: ReturnType<typeof setInterval> | null = null

// 显示的用户（显示当前和下一个用户）
const displayedUsers = computed(() => {
  if (groupBuyUsers.value.length === 0) {
    return []
  }
  const result = []
  // 显示当前用户
  result.push(groupBuyUsers.value[currentUserIndex.value])
  // 显示下一个用户
  const nextIndex = (currentUserIndex.value + 1) % groupBuyUsers.value.length
  if (nextIndex !== currentUserIndex.value) {
    result.push(groupBuyUsers.value[nextIndex])
  }
  return result
})

// 格式化时间
const formatTime = (num: number): string => {
  return num.toString().padStart(2, '0')
}

// 切换用户
const switchUser = () => {
  if (groupBuyUsers.value.length > 0) {
    currentUserIndex.value = (currentUserIndex.value + 1) % groupBuyUsers.value.length
  }
}

// 更新倒计时
const updateCountdown = () => {
  groupBuyUsers.value.forEach(user => {
    if (user.countdown.seconds > 0) {
      user.countdown.seconds--
    } else if (user.countdown.minutes > 0) {
      user.countdown.minutes--
      user.countdown.seconds = 59
    } else if (user.countdown.hours > 0) {
      user.countdown.hours--
      user.countdown.minutes = 59
      user.countdown.seconds = 59
    }
  })
}

// 加载拼团用户数据
const loadGroupBuyUsers = async () => {
  // 模拟数据 - 拼团统计
  currentStats.value = {
    buyingCount: 128,
    rank: 3
  }
  
  // 模拟数据 - 拼团用户列表
  groupBuyUsers.value = [
    {
      id: 1,
      avatars: [av('u1a'), av('u1b')],
      names: '甜甜圈、小美',
      tags: ['新人专享', '已拼2件'],
      countdown: { hours: 0, minutes: 15, seconds: 30 }
    },
    {
      id: 2,
      avatars: [av('u2a'), av('u2b'), av('u2c')],
      names: '吃货达人、阳光、小雨',
      tags: ['回头客', '已拼5件'],
      countdown: { hours: 0, minutes: 8, seconds: 45 }
    },
    {
      id: 3,
      avatars: [av('u3a')],
      names: '购物狂',
      tags: ['新用户', '首单立减'],
      countdown: { hours: 0, minutes: 23, seconds: 12 }
    },
    {
      id: 4,
      avatars: [av('u4a'), av('u4b')],
      names: '省钱小能手、剁手党',
      tags: ['拼单达人', '已拼12件'],
      countdown: { hours: 1, minutes: 5, seconds: 0 }
    }
  ]
  
  console.log('加载拼团用户数据完成')
}

onMounted(() => {
  loadGroupBuyUsers()
  
  // 3秒切换一次用户
  userTimer = setInterval(switchUser, 3000)
  
  // 每秒更新倒计时
  countdownTimer = setInterval(updateCountdown, 1000)
})

onUnmounted(() => {
  if (userTimer) clearInterval(userTimer)
  if (countdownTimer) clearInterval(countdownTimer)
})
</script>

<style scoped>
.group-buy-section {
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
}

.stats-bar {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.stat-item {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #ee0a24;
}

.stat-label {
  font-size: 11px;
  color: #666;
}

.stat-rank {
  font-size: 11px;
  color: #ee0a24;
  font-weight: 600;
}

.stat-divider {
  margin: 0 4px;
  color: #ccc;
}

.users-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-group {
  display: flex;
  flex-shrink: 0;
}

.avatar-item {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #fff;
  margin-left: -5px;
  position: relative;
  flex-shrink: 0;
}

.avatar-item:first-child {
  margin-left: 0;
}

.avatar-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 12px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.countdown-section {
  text-align: center;
  flex-shrink: 0;
}

.countdown-label {
  font-size: 9px;
  color: #999;
  margin-bottom: 2px;
}

.countdown-time {
  display: flex;
  align-items: center;
  font-size: 11px;
  font-weight: 600;
  color: #ee0a24;
}

.time-unit {
  background: #ee0a24;
  color: #fff;
  padding: 1px 3px;
  border-radius: 2px;
  font-size: 10px;
  min-width: 16px;
  text-align: center;
}

.time-separator {
  margin: 0 1px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 5px 12px;
  background: linear-gradient(135deg, #ff6b6b, #ee0a24);
  border-radius: 14px;
  color: #fff;
  flex-shrink: 0;
}

.btn-badge {
  font-size: 9px;
  opacity: 0.9;
}

.btn-text {
  font-size: 11px;
  font-weight: 600;
}
</style>