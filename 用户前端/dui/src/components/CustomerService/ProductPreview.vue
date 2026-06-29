<template>
  <div class="product-preview" @click="$emit('click')">
    <van-image :src="productImage" class="product-image" />
    <div class="product-info">
      <div class="product-name">{{ productName }}</div>
      <div class="product-price">
        <span class="price-symbol">¥</span>
        <span class="price-value">{{ price }}</span>
      </div>
      <div class="product-action">
        <van-button size="small" type="primary" plain>
          马上购买
        </van-button>
      </div>
    </div>
    <van-icon name="arrow" class="arrow-icon" />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  productImage: string
  productName: string
  price: string | number
}>()

defineEmits<{
  click: []
}>()
</script>

<style lang="scss" scoped>
.product-preview {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #fff;
  border-bottom: 1px solid #f5f5f5;

  .product-image {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    flex-shrink: 0;
  }

  .product-info {
    flex: 1;
    margin-left: 12px;

    .product-name {
      font-size: 14px;
      color: #333;
      line-height: 1.4;
      margin-bottom: 8px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .product-price {
      display: flex;
      align-items: baseline;
      margin-bottom: 10px;

      .price-symbol {
        font-size: 14px;
        color: #ee0a24;
        font-weight: 600;
      }

      .price-value {
        font-size: 20px;
        color: #ee0a24;
        font-weight: 700;
      }
    }

    .product-action {
      .van-button {
        width: 80px;
        height: 28px;
        line-height: 28px;
        padding: 0;
        border-radius: 14px;
        font-size: 12px;
      }
    }
  }

  .arrow-icon {
    color: #999;
    margin-left: 10px;
  }
}
</style>
