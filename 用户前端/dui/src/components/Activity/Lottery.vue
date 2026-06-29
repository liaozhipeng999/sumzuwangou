<template>
  <div class="lottery-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="现金大转盘" left-arrow @click-left="goBack" />

    <!-- 转盘区域 -->
    <div class="wheel-container">
      <div class="wheel-wrapper" :class="{ 'spinning': isSpinning }">
        <div class="wheel" :style="{ transform: `rotate(${rotation}deg)` }">
          <div v-for="(prize, index) in prizes" :key="index" 
               class="wheel-segment" 
               :style="{ transform: `rotate(${index * 45}deg)` }">
            <span class="prize-text">{{ prize.text }}</span>
            <span class="prize-amount">{{ prize.amount }}</span>
          </div>
        </div>
        <div class="wheel-center">
          <button class="spin-btn" @click="startSpin" :disabled="isSpinning">
            {{ isSpinning ? '抽奖中' : '开始抽奖' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 我的奖品 -->
    <div class="prizes-section">
      <h3 class="section-title">我的奖品</h3>
      <div v-if="myPrizes.length > 0" class="prizes-list">
        <div v-for="prize in myPrizes" :key="prize.id" class="prize-item">
          <div class="prize-info">
            <span class="prize-name">{{ prize.name }}</span>
            <span class="prize-time">{{ formatTime(prize.time) }}</span>
          </div>
          <van-tag :type="prize.type === 'cash' ? 'danger' : 'success'" size="medium">
            {{ prize.type === 'cash' ? '现金' : '优惠券' }}
          </van-tag>
        </div>
      </div>
      <van-empty v-else description="暂无中奖记录" image="gift" />
    </div>

    <!-- 活动规则 -->
    <div class="rules-section">
      <h3 class="section-title">活动规则</h3>
      <ul class="rules-list">
        <li>每人每天可免费抽奖3次</li>
        <li>中奖后奖金将自动发放至账户余额</li>
        <li>优惠券有效期为7天</li>
        <li>最终解释权归平台所有</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

// 转盘状态
const isSpinning = ref(false)
const rotation = ref(0)

// 奖品配置
const prizes = [
  { text: '一等奖', amount: '¥88' },
  { text: '二等奖', amount: '¥50' },
  { text: '三等奖', amount: '¥20' },
  { text: '四等奖', amount: '¥10' },
  { text: '谢谢参与', amount: '' },
  { text: '优惠券', amount: '满100减30' },
  { text: '幸运奖', amount: '¥5' },
  { text: '再来一次', amount: '' }
]

// 我的奖品
const myPrizes = ref<any[]>([])

// 开始抽奖
const startSpin = () => {
  if (isSpinning.value) return
  
  isSpinning.value = true
  
  // 随机旋转角度（至少转5圈）
  const randomAngle = Math.floor(Math.random() * 360) + 1800
  rotation.value += randomAngle
  
  // 模拟抽奖结果
  setTimeout(() => {
    isSpinning.value = false
    
    // 随机生成奖品
    const prizeIndex = Math.floor(Math.random() * prizes.length)
    const prize = prizes[prizeIndex]
    
    if (prize.text !== '谢谢参与') {
      showToast(`恭喜！获得${prize.text}${prize.amount}`)
      
      // 添加到我的奖品
      myPrizes.value.unshift({
        id: Date.now(),
        name: `${prize.text} ${prize.amount}`,
        time: new Date().toISOString(),
        type: prize.amount.includes('¥') ? 'cash' : 'coupon'
      })
    } else {
      showToast('很遗憾，没有中奖')
    }
  }, 3000)
}

// 返回
const goBack = () => {
  router.back()
}

// 格式化时间
const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}-${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  // 可以从后端加载历史中奖记录
})
</script>

<style scoped>
.lottery-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #ff6b6b 0%, #ffd93d 100%);
  padding-bottom: 20px;
}

/* 转盘容器 */
.wheel-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
}

.wheel-wrapper {
  position: relative;
  width: 300px;
  height: 300px;
}

.wheel-wrapper.spinning .wheel {
  transition: transform 3s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.wheel {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(
    from 0deg,
    #ff6b6b 0deg 45deg,
    #ffd93d 45deg 90deg,
    #ff6b6b 90deg 135deg,
    #ffd93d 135deg 180deg,
    #ff6b6b 180deg 225deg,
    #ffd93d 225deg 270deg,
    #ff6b6b 270deg 315deg,
    #ffd93d 315deg 360deg
  );
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
  transition: transform 0s;
}

.wheel-segment {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 50%;
  height: 2px;
  transform-origin: 0 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
}

.prize-text {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
  margin-bottom: 4px;
}

.prize-amount {
  font-size: 12px;
  color: #fff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.wheel-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, #ff4757 0%, #ee0a24 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(238, 10, 36, 0.4);
  z-index: 10;
}

.spin-btn {
  background: transparent;
  border: none;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  padding: 10px 15px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.spin-btn:hover:not(:disabled) {
  transform: scale(1.1);
}

.spin-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 奖品区域 */
.prizes-section {
  background: #fff;
  margin: 0 15px 15px;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
}

.prizes-list {
  max-height: 300px;
  overflow-y: auto;
}

.prize-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.prize-item:last-child {
  border-bottom: none;
}

.prize-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.prize-name {
  font-size: 15px;
  color: #333;
}

.prize-time {
  font-size: 12px;
  color: #999;
}

/* 规则区域 */
.rules-section {
  background: #fff;
  margin: 0 15px;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.rules-list {
  margin: 0;
  padding-left: 20px;
  color: #666;
  font-size: 14px;
  line-height: 1.8;
}

.rules-list li {
  margin-bottom: 8px;
}
</style>
