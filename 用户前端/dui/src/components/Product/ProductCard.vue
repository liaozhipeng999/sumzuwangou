<template>
  <div class="product-card" @click="$emit('click', product)">
    <van-image :src="product.mainImage" class="product-image" />
    <div class="product-info">
      <div class="product-name">{{ product.productName }}</div>
      <div class="price-row">
        <span v-if="product.discountedPrice" class="discounted-price">
          ¥{{ product.discountedPrice }}
        </span>
        <span v-else class="current-price">
          ¥{{ product.price }}
        </span>
        <span v-if="product.originalPrice" class="original-price">
          ¥{{ product.originalPrice }}
        </span>
      </div>
      <div v-if="product.applicableCoupon" class="coupon-tag">
        <span class="coupon-badge">{{ product.applicableCoupon.couponName }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Product } from '@/api/recommend'

defineProps<{
  product: Product
}>()

defineEmits<{
  (e: 'click', product: Product): void
}>()
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.product-info {
  padding: 10px;
}

.product-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 8px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.discounted-price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: bold;
}

.current-price {
  color: #1f1f1f;
  font-size: 16px;
  font-weight: bold;
}

.original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
}

.coupon-tag {
  margin-top: 6px;
}

.coupon-badge {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
}
</style>
