<template>
  <div class="wallet-page">
    <!-- 顶部导航栏 -->
    <van-nav-bar 
      title="多多钱包" 
      left-arrow 
      @click-left="goBack"
    >
      <template #right>
        <van-icon name="setting-o" size="20" />
      </template>
    </van-nav-bar>

    <!-- 副标题 -->
    <div class="subtitle">拼多多旗下支付服务</div>

    <!-- 账户余额 -->
    <div class="balance-section">
      <div class="balance-item" @click="goBalanceDetail">
        <div class="balance-left">
          <span class="balance-label">账户余额</span>
        </div>
        <div class="balance-right">
          <span class="balance-amount">¥{{ accountBalance.toFixed(2) }}</span>
          <van-icon name="arrow" size="16" color="#999" />
        </div>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="function-list">
      <div class="function-item" @click="goTransactionRecord">
        <span class="function-text">交易记录</span>
        <van-icon name="arrow" size="16" color="#999" />
      </div>
      <div class="function-item" @click="goBankCard">
        <span class="function-text">银行卡</span>
        <van-icon name="arrow" size="16" color="#999" />
      </div>
      <div class="function-item" @click="goSecurity">
        <div class="security-left">
          <span class="function-text">安全保障</span>
        </div>
        <div class="security-right">
          <van-icon name="check-circle" color="#07c160" size="18" />
          <span class="security-text">中国人寿财险保障中</span>
          <van-icon name="arrow" size="16" color="#999" />
        </div>
      </div>
    </div>

    <!-- 银行限时优惠 -->
    <div class="bank-section">
      <div class="section-header">
        <span class="section-title">银行限时优惠</span>
        <div class="more-btn">
          <van-icon name="credit-card" color="#999" size="14" />
          <van-icon name="credit-card" color="#999" size="14" />
          <van-icon name="credit-card" color="#999" size="14" />
          <span>查看更多</span>
          <van-icon name="arrow" size="12" />
        </div>
      </div>
      <div class="bank-list">
        <div class="bank-item" v-for="bank in bankOffers" :key="bank.name">
          <div class="bank-amount">
            <span class="amount-symbol">{{ bank.amount }}</span>
            <span class="amount-unit">元</span>
          </div>
          <div class="bank-name">{{ bank.name }}</div>
          <van-button size="small" type="primary" class="bank-btn">领取</van-button>
        </div>
      </div>
    </div>

    <!-- 热门商品推荐 -->
    <div class="goods-section">
      <div class="section-header">
        <span class="section-title">热门商品</span>
      </div>
      <div class="goods-list">
        <div 
          class="goods-item" 
          v-for="item in hotProducts" 
          :key="item.id"
          @click="goProductDetail(item.id)"
        >
          <van-image :src="item.image" class="goods-image" lazy-load />
          <div class="goods-info">
            <span class="goods-title">{{ item.name }}</span>
            <div class="goods-tags">
              <span class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</span>
            </div>
            <div class="goods-price">
              <span class="price">¥{{ item.price }}</span>
              <span class="sales">{{ item.sales }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getHotProducts } from '@/api/recommend'

const router = useRouter()

// 账户余额
const accountBalance = ref(0.00)

// 银行优惠列表
const bankOffers = ref([
  { name: '工商银行信用卡', amount: '8' },
  { name: '招商银行信用卡', amount: '5' },
  { name: '农业银行信用卡', amount: '5' },
  { name: '邮储银行信用卡', amount: '5' }
])

// 热门商品列表
interface ProductItem {
  id: number
  name: string
  image: string
  price: string | number
  sales: string
  tags: string[]
}
const hotProducts = ref<ProductItem[]>([])

// 获取热门商品
const fetchHotProducts = async () => {
  try {
    const response = await getHotProducts(4)
    if (response.code === 200) {
      hotProducts.value = response.data.map((item: any) => ({
        id: item.id,
        name: item.productName,
        image: item.carouselImages?.[0]?.imageUrl || item.image,
        price: item.price,
        sales: `已售${item.sales || 0}件`,
        tags: item.tags?.map((t: any) => t.tagName) || []
      }))
    }
  } catch (error) {
    console.error('获取热门商品失败:', error)
    // 使用模拟数据
    hotProducts.value = [
      {
        id: 1,
        name: '3D硅胶坐垫套加厚软座自行车坐垫套山地车座套',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=bicycle%20seat%20cover%20silicone%20product&image_size=square',
        price: '8.9',
        sales: '已售3.2万件',
        tags: ['百亿补贴', '品牌']
      },
      {
        id: 2,
        name: '15.6桌面显示器支架臂双屏升降旋转底座',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=monitor%20arm%20stand%20desk%20mount&image_size=square',
        price: '15.6',
        sales: '已售1.8万件',
        tags: ['后天达', '专卖店']
      },
      {
        id: 3,
        name: '成人儿童自行车内胎通用型',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=bicycle%20inner%20tube%20product&image_size=square',
        price: '9.2',
        sales: '已售12万件',
        tags: ['百亿补贴', '品牌', '永久']
      },
      {
        id: 4,
        name: 'LED护眼台灯学生学习阅读灯',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=LED%20desk%20lamp%20eye%20protection&image_size=square',
        price: '19.9',
        sales: '已售5万件',
        tags: ['限时特惠']
      }
    ]
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 账户余额详情
const goBalanceDetail = () => {
  showToast('账户余额详情')
}

// 交易记录
const goTransactionRecord = () => {
  showToast('交易记录')
}

// 银行卡
const goBankCard = () => {
  showToast('银行卡管理')
}

// 安全保障
const goSecurity = () => {
  showToast('安全保障')
}

// 商品详情
const goProductDetail = (id: number) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  fetchHotProducts()
})
</script>

<style lang="scss" scoped>
.wallet-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 副标题 */
.subtitle {
  text-align: center;
  font-size: 12px;
  color: #999;
  padding: 8px 0 15px;
  background: #fff;
}

/* 账户余额 */
.balance-section {
  background: linear-gradient(135deg, #ee0a24 0%, #ff6034 100%);
  margin: 12px;
  border-radius: 12px;
  padding: 20px;

  .balance-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .balance-label {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.9);
  }

  .balance-amount {
    font-size: 32px;
    font-weight: 700;
    color: #fff;
    margin-right: 10px;
  }
}

/* 功能列表 */
.function-list {
  background: #fff;
  margin: 0 12px 12px;
  border-radius: 12px;
  overflow: hidden;

  .function-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    .function-text {
      font-size: 16px;
      color: #333;
    }
  }

  .security-left {
    flex: 1;
  }

  .security-right {
    display: flex;
    align-items: center;
    gap: 8px;

    .security-text {
      font-size: 14px;
      color: #07c160;
    }
  }
}

/* 银行限时优惠 */
.bank-section {
  background: #fff;
  margin: 0 12px 12px;
  border-radius: 12px;
  padding: 16px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }

    .more-btn {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: #999;
    }
  }

  .bank-list {
    display: flex;
    justify-content: space-between;
  }

  .bank-item {
    width: calc(25% - 8px);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 12px 0;
    background: #fff5f5;
    border-radius: 8px;

    .bank-amount {
      display: flex;
      align-items: baseline;

      .amount-symbol {
        font-size: 24px;
        font-weight: 700;
        color: #ee0a24;
      }

      .amount-unit {
        font-size: 14px;
        color: #ee0a24;
      }
    }

    .bank-name {
      font-size: 12px;
      color: #666;
      text-align: center;
      line-height: 1.4;
    }

    .bank-btn {
      width: 60px;
      height: 24px;
      line-height: 24px;
      padding: 0;
      border-radius: 12px;
      font-size: 12px;
      margin-top: 4px;
    }
  }
}

/* 热门商品 */
.goods-section {
  background: #fff;
  margin: 0 12px 12px;
  border-radius: 12px;
  padding: 16px;

  .section-header {
    margin-bottom: 12px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }

  .goods-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .goods-item {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .goods-image {
      width: 100%;
      aspect-ratio: 1;
      border-radius: 8px;
    }

    .goods-info {
      padding: 0 4px;

      .goods-title {
        font-size: 13px;
        color: #333;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .goods-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
        margin-top: 4px;

        .tag {
          font-size: 10px;
          color: #ee0a24;
          background: rgba(238, 10, 36, 0.08);
          padding: 2px 6px;
          border-radius: 4px;
        }
      }

      .goods-price {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 4px;

        .price {
          font-size: 15px;
          font-weight: 700;
          color: #ee0a24;
        }

        .sales {
          font-size: 11px;
          color: #999;
        }
      }
    }
  }
}
</style>
