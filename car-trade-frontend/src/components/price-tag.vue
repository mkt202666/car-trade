<template>
  <view class="price-tag">
    <text class="price-symbol">¥</text>
    <text class="price-value">{{ formattedPrice }}</text>
    <text class="price-unit">万</text>
  </view>
</template>

<script>
export default {
  props: {
    price: { type: [Number, String], required: true }
  },
  computed: {
    formattedPrice() {
      const num = Number(this.price)
      if (isNaN(num)) return '0.00'
      const inWan = num >= 10000 ? (num / 10000) : num
      return inWan.toFixed(2)
    }
  }
}
</script>

<style scoped>
.price-tag {
  display: inline-flex;
  align-items: baseline;
}
.price-symbol {
  font-size: 22rpx;
  color: #ff3b00;
  font-weight: 600;
}
.price-value {
  font-size: 36rpx;
  color: #ff3b00;
  font-weight: 700;
}
.price-unit {
  font-size: 22rpx;
  color: #ff3b00;
  font-weight: 500;
}
</style>
