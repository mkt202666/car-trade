<template>
  <u-card @click="$emit('click')" :border="true" margin="10rpx 20rpx" padding="0">
    <view class="car-card">
      <image class="car-image" :src="car.coverImage || car.images?.[0] || '/static/default-car.png'" mode="aspectFill"></image>
      <view v-if="car.auctionStatus === 'AUCTIONING'" class="auction-badge">拍卖中 <u-count-down :timestamp="car.auctionEndTime" separator="zh" :show-hours="true" :show-minutes="true" :show-seconds="true" color="#fff" bg-color="transparent" separator-color="#fff"></u-count-down></view>
      <view class="car-info">
        <view class="car-title u-line-1">{{ car.title || car.carName }}</view>
        <view class="car-meta">
          <text class="meta-item">{{ car.registerYear || '-' }}年</text>
          <text class="meta-divider">|</text>
          <text class="meta-item">{{ car.mileage ? car.mileage + '万公里' : '-' }}</text>
        </view>
        <view class="car-tags">
          <u-tag v-if="car.hasDeposit" text="保证金" type="warning" size="mini" :plain="true"></u-tag>
          <u-tag v-if="car.energyType === 'EV'" text="纯电" type="success" size="mini" :plain="true"></u-tag>
          <u-tag v-if="car.energyType === 'HYBRID'" text="混动" type="primary" size="mini" :plain="true"></u-tag>
          <u-tag v-if="car.forExport" text="出口" type="error" size="mini" :plain="true"></u-tag>
          <export-flags v-if="car.exportCountries" :countries="car.exportCountries"></export-flags>
        </view>
        <view class="car-bottom">
          <price-tag :price="car.price"></price-tag>
          <text class="car-location">{{ car.city || car.location }}</text>
          <text class="car-time">{{ car.publishTime }}</text>
        </view>
      </view>
    </view>
  </u-card>
</template>

<script>
import PriceTag from './price-tag.vue'
import ExportFlags from './export-flags.vue'
export default {
  components: { PriceTag, ExportFlags },
  props: {
    car: { type: Object, required: true }
  },
  emits: ['click']
}
</script>

<style scoped>
.car-card {
  display: flex;
  padding: 20rpx;
}
.car-image {
  width: 240rpx;
  height: 180rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
  background-color: #f5f5f5;
}
.auction-badge {
  position: absolute;
  top: 0;
  left: 20rpx;
  background: linear-gradient(135deg, #ff6b35, #ff3b00);
  color: #fff;
  font-size: 22rpx;
  padding: 4rpx 16rpx;
  border-radius: 0 0 12rpx 12rpx;
  display: flex;
  align-items: center;
}
.car-info {
  flex: 1;
  margin-left: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.car-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}
.car-meta {
  font-size: 24rpx;
  color: #999;
  margin: 6rpx 0;
}
.meta-divider {
  margin: 0 8rpx;
}
.car-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin: 6rpx 0;
}
.car-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
}
.car-location {
  font-size: 22rpx;
  color: #999;
}
.car-time {
  font-size: 20rpx;
  color: #ccc;
  width: 100%;
  margin-top: 4rpx;
}
</style>
