<template>
  <div class="favorite-item" @click="handleClick">
    <!-- 商品图片区域 -->
    <div class="item-image-wrapper">
      <img 
        v-if="getImageUrl"
        :src="getImageUrl" 
        class="item-image" 
        @error="handleImageError"
      />
      <div v-else class="default-image">
        <van-icon name="photo-o" size="40" color="#d9d9d9" />
      </div>
      <div class="item-tags">
        <span v-if="isBillionSubsidy" class="tag billion">百亿补贴</span>
        <span v-if="isBrand" class="tag brand">品牌</span>
        <span v-if="isFlashSale" class="tag flash">限时特惠</span>
      </div>
    </div>
    
    <!-- 商品信息区域 -->
    <div class="item-info">
      <!-- 商品名称 -->
      <div class="item-title">
        <span v-if="item.brandName" class="brand-name">{{ item.brandName }}</span>
        {{ item.productName }}
      </div>
      
      <!-- 商品规格/简介 -->
      <div class="item-spec">
        {{ item.brief }}
      </div>
      
      <!-- 销量信息 -->
      <div class="item-sales">
        <van-icon name="shopping-cart" size="12" />
        <span>已售 {{ formatSales(item.sales) }}</span>
      </div>
      
      <!-- 价格区域 -->
      <div class="item-footer">
        <div class="price-section">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ item.price }}</span>
          <span v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice }}</span>
          <span v-if="discount" class="discount-tag">{{ discount }}折</span>
        </div>
        <van-button type="danger" size="small" class="buy-btn" @click.stop="handleBuy">
          去购买
        </van-button>
      </div>
    </div>
    
    <!-- 收藏按钮 -->
    <div class="favorite-btn" @click.stop="handleFavorite">
      <van-icon 
        name="heart" 
        :color="isFavorite ? '#ee0a24' : '#d9d9d9'" 
        :size="24" 
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import type { FavoriteProduct } from '@/api/favorite'

const props = defineProps<{
  item: FavoriteProduct
  isFavorite?: boolean
}>()

const emit = defineEmits<{
  (e: 'remove', productId: number): void
}>()

const router = useRouter()

// 判断是否有百亿补贴
const isBillionSubsidy = computed(() => {
  return props.item.tags?.includes('百亿补贴') || props.item.brief?.includes('补贴')
})

// 判断是否是品牌商品
const isBrand = computed(() => {
  return !!props.item.brandName
})

// 判断是否是限时特惠
const isFlashSale = computed(() => {
  return props.item.tags?.includes('限时') || props.item.tags?.includes('特惠')
})

// 获取商品图片URL（支持多个字段）
const getImageUrl = computed(() => {
  const imageFields = [props.item.mainImage, props.item.imageUrl, props.item.image]
  
  for (const field of imageFields) {
    if (field && field.trim()) {
      // 检查是否是Windows本地绝对路径（如 F:/... 或 C:/...）
      const isLocalPath = /^[A-Za-z]:\\|^[A-Za-z]:\//.test(field)
      if (!isLocalPath) {
        // 如果是相对路径，确保以 / 开头
        if (field.startsWith('/')) {
          return field
        } else if (field.startsWith('http')) {
          // 如果是完整URL，直接返回
          return field
        }
      }
    }
  }
  
  // 返回空字符串，使用默认占位图
  return ''
})

// 计算折扣
const discount = computed(() => {
  if (props.item.originalPrice && props.item.price) {
    return Math.round((props.item.price / props.item.originalPrice) * 10)
  }
  return null
})

// 格式化销量
const formatSales = (sales: number): string => {
  if (sales >= 10000) {
    return (sales / 10000).toFixed(1) + '万'
  }
  return sales.toString()
}

// 图片加载失败处理
const handleImageError = () => {
  // 可以设置默认图片
}

// 点击商品
const handleClick = () => {
  router.push(`/product/${props.item.id}`)
}

// 点击去购买
const handleBuy = () => {
  router.push(`/product/${props.item.id}`)
  showToast('正在跳转商品详情')
}

// 点击收藏
const handleFavorite = () => {
  emit('remove', props.item.id)
}
</script>

<style lang="scss" scoped>
.favorite-item {
  display: flex;
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}

/* 图片区域 */
.item-image-wrapper {
  position: relative;
  width: 140px;
  height: 140px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  
  .item-image {
    width: 100%;
    height: 100%;
    background: #f8f8f8;
  }
  
  .default-image {
    width: 100%;
    height: 100%;
    background: #f8f8f8;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .item-tags {
    position: absolute;
    top: 8px;
    left: 8px;
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .tag {
      font-size: 11px;
      padding: 3px 8px;
      border-radius: 6px;
      font-weight: 600;
      backdrop-filter: blur(4px);
      
      &.billion {
        background: linear-gradient(135deg, #ff4757 0%, #ff6b81 100%);
        color: #fff;
      }
      
      &.brand {
        background: linear-gradient(135deg, #ffa502 0%, #ff7f50 100%);
        color: #fff;
      }
      
      &.flash {
        background: linear-gradient(135deg, #ee0a24 0%, #ff4757 100%);
        color: #fff;
      }
    }
  }
}

/* 信息区域 */
.item-info {
  flex: 1;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  min-width: 0;
  
  .item-title {
    font-size: 15px;
    font-weight: 600;
    color: #1a1a1a;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    margin-bottom: 8px;
    
    .brand-name {
      color: #ee0a24;
      font-weight: 700;
      margin-right: 6px;
    }
  }
  
  .item-spec {
    font-size: 13px;
    color: #999;
    margin-bottom: 6px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .item-sales {
    font-size: 12px;
    color: #bbb;
    display: flex;
    align-items: center;
    gap: 4px;
    margin-bottom: 10px;
  }
  
  .item-footer {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-top: auto;
    
    .price-section {
      display: flex;
      align-items: baseline;
      gap: 4px;
      
      .price-symbol {
        font-size: 14px;
        font-weight: 700;
        color: #ee0a24;
      }
      
      .price-value {
        font-size: 22px;
        font-weight: 700;
        color: #ee0a24;
      }
      
      .original-price {
        font-size: 13px;
        color: #bbb;
        text-decoration: line-through;
      }
      
      .discount-tag {
        font-size: 10px;
        color: #fff;
        background: #ee0a24;
        padding: 1px 5px;
        border-radius: 4px;
        margin-left: 4px;
      }
    }
    
    .buy-btn {
      border-radius: 20px;
      padding: 8px 20px;
      font-size: 14px;
      font-weight: 600;
      background: linear-gradient(135deg, #ee0a24 0%, #ff4757 100%);
      border: none;
      
      &::after {
        border: none;
      }
    }
  }
}

/* 收藏按钮 */
.favorite-btn {
  padding: 8px;
  display: flex;
  align-items: flex-start;
  
  .van-icon {
    padding: 4px;
    transition: transform 0.2s ease;
    
    &:active {
      transform: scale(1.2);
    }
  }
}
</style>