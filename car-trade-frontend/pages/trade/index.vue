<template>
  <view>
    <u-navbar title="交易管理" :border-bottom="false" :placeholder="true"></u-navbar>
    <view class="page-content">
      <view class="stats-row">
        <view class="stat-card" @click="toPage('trade-all')">
          <text class="stat-num">{{ allCount }}</text>
          <text class="stat-label">全部订单</text>
        </view>
        <view class="stat-card" @click="toPage('trade-bought')">
          <text class="stat-num">{{ boughtCount }}</text>
          <text class="stat-label">买到的车</text>
        </view>
        <view class="stat-card" @click="toPage('trade-sold')">
          <text class="stat-num">{{ soldCount }}</text>
          <text class="stat-label">卖出的车</text>
        </view>
        <view class="stat-card" @click="toPage('publish')">
          <u-icon name="plus-circle" size="48" color="#3c9cff"></u-icon>
          <text class="stat-label">发布车源</text>
        </view>
      </view>
      <view class="section-header">
        <text class="section-title">近期交易</text>
        <text class="section-more" @click="toPage('trade-all')">查看全部 <u-icon name="arrow-right" size="20" color="#999"></u-icon></text>
      </view>
      <view class="order-list">
        <view class="order-card" v-for="item in recentOrders" :key="item.id" @click="toOrderDetail(item.id)">
          <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="order-image"></image>
          <view class="order-info">
            <view class="order-title u-line-1">{{ item.title }}</view>
            <view class="order-status">
              <u-tag :text="orderStatusText(item.status)" :type="orderStatusType(item.status)" size="mini"></u-tag>
            </view>
            <view class="order-price">{{ formatPrice(item.price) }}</view>
            <view class="order-time">{{ formatTime(item.createTime) }}</view>
          </view>
        </view>
      </view>
      <u-empty v-if="recentOrders.length === 0" mode="order" text="暂无交易记录"></u-empty>
    </view>
  </view>
</template>

<script>
import { getOrderList } from '@/api/order'
import { formatPrice, formatTime } from '@/utils/format'
import { getUserStats } from '@/api/user'

export default {
  data() {
    return {
      recentOrders: [],
      allCount: 0,
      boughtCount: 0,
      soldCount: 0
    }
  },
  onShow() {
    this.loadStats()
    this.loadOrders()
  },
  methods: {
    formatPrice,
    formatTime,
    async loadStats() {
      try {
        const res = await getUserStats()
        const stats = res.data
        this.allCount = stats.orderCount || 0
        this.boughtCount = stats.boughtCount || 0
        this.soldCount = stats.soldCount || 0
      } catch (e) {}
    },
    async loadOrders() {
      try {
        const res = await getOrderList({ page: 1, pageSize: 5 })
        this.recentOrders = res.data.records || res.data.list || res.data || []
      } catch (e) {}
    },
    orderStatusText(status) {
      const map = { 'PENDING': '待确认', 'DEPOSIT_PAID': '已付定金', 'INSPECTING': '验车中', 'COMPLETED': '已完成', 'CANCELLED': '已取消' }
      return map[status] || status
    },
    orderStatusType(status) {
      const map = { 'PENDING': 'warning', 'DEPOSIT_PAID': 'primary', 'INSPECTING': 'info', 'COMPLETED': 'success', 'CANCELLED': 'error' }
      return map[status] || 'default'
    },
    toPage(name) {
      uni.navigateTo({ url: '/pages/' + name + '/index' })
    },
    toOrderDetail(id) {
      uni.navigateTo({ url: '/pages/order-detail/index?id=' + id })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-content { min-height: 100vh; background: #f5f5f5; padding: 20rpx 30rpx; }
.stats-row { display: flex; gap: 16rpx; margin-bottom: 30rpx; }
.stat-card {
  flex: 1; background: #fff; border-radius: 16rpx; padding: 24rpx 0;
  display: flex; flex-direction: column; align-items: center;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.stat-num { font-size: 40rpx; font-weight: 700; color: #3c9cff; }
.stat-label { font-size: 22rpx; color: #666; margin-top: 8rpx; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; }
.section-more { font-size: 24rpx; color: #999; }
.order-list { }
.order-card {
  background: #fff; border-radius: 16rpx; margin-bottom: 16rpx; overflow: hidden;
  display: flex; padding: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.order-image { width: 180rpx; height: 140rpx; border-radius: 12rpx; margin-right: 20rpx; }
.order-info { flex: 1; position: relative; }
.order-title { font-size: 26rpx; font-weight: 600; color: #333; padding-right: 120rpx; }
.order-status { position: absolute; top: 0; right: 0; }
.order-price { font-size: 28rpx; color: #f56c6c; font-weight: 600; margin-top: 10rpx; }
.order-time { font-size: 22rpx; color: #999; margin-top: 8rpx; }
</style>
