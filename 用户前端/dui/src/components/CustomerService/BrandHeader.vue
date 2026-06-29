<template>
  <div class="brand-header">
    <div class="brand-info">
      <van-image :src="brandLogo" class="brand-logo" round />
      <div class="brand-detail">
        <div class="brand-name">{{ brandName }}</div>
        <div class="brand-status">
          <van-icon name="online" color="#07c160" size="12" />
          <span>{{ onlineStatus }}</span>
        </div>
      </div>
    </div>
    <div class="brand-tags">
      <span 
        v-for="tag in brandTags" 
        :key="tag" 
        class="brand-tag"
      >
        {{ tag }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  brandName: string
  brandLogo: string
  onlineStatus: string
  brandTags: string[]
}>()
</script>

<style lang="scss" scoped>
.brand-header {
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;

  .brand-info {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;

    .brand-logo {
      width: 50px;
      height: 50px;
    }

    .brand-detail {
      flex: 1;

      .brand-name {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }

      .brand-status {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #07c160;
      }
    }
  }

  .brand-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .brand-tag {
      font-size: 12px;
      color: #ee0a24;
      background: rgba(238, 10, 36, 0.08);
      padding: 4px 10px;
      border-radius: 12px;
    }
  }
}
</style>
