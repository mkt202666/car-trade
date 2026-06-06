<template>
  <view>
    <u-navbar title="卖出的车" :border-bottom="false" :placeholder="true"></u-navbar>
    <view class="page-content">
      <view class="order-list">
        <view class="order-card" v-for="item in orderList" :key="item.id" @click="toDetail(item.id)">
          <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="order-image"></image>
          <view class="order-info">
            <view class="order-title u-line-1">{{ item.carTitle || item.title }}</view>
            <view class="order-meta">
              <text class="order-price">{{ formatPrice(item.price) }}</text>
              <u-tag :text="statusText(item.status)" :type="statusType(item.status)" size="mini"></u-tag>
            </view>
            <text class="order-time">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
      </view>
      <u-empty v-if="orderList.length === 0 && !loading" mode="order" text="暂无卖出的车"></u-empty>
      <u-loadmore v-if="orderList.length > 0" :status="loadStatus" @loadmore="loadMore"></u-loadmore>
    </view>
  </view>
</template>

<script>
import { getOrderList } from '@/api/order'
import { formatPrice, formatTime } from '@/utils/format'

export default {
  data() {
    return {
      orderList: [],
      page: 1,
      pageSize: 10,
      loading: false,
      loadStatus: 'loadmore',
      hasMore: true
    }
  },
  onLoad() {
    this.loadOrders()
  },
  methods: {
    formatPrice,
    formatTime,
    async loadOrders() {
      this.loading = true
      this.loadStatus = 'loading'
      try {
        const res = await getOrderList({ page: this.page, pageSize: this.pageSize, role: 'SELLER' })
        const list = res.data.records || res.data.list || res.data || []
        this.orderList = this.page === 1 ? list : this.orderList.concat(list)
        this.hasMore = list.length >= this.pageSize
        this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
      } catch (e) { this.loadStatus = 'loadmore' }
      this.loading = false
    },
    loadMore() {
      if (!this.hasMore) return
      this.page++
      this.loadOrders()
    },
    statusText(status) {
      const map = { 'PENDING': '待确认', 'DEPOSIT_PAID': '已付定金', 'INSPECTING': '验车中', 'COMPLETED': '已完成', 'CANCELLED': '已取消' }
      return map[status] || status
    },
    statusType(status) {
      const map = { 'PENDING': 'warning', 'DEPOSIT_PAID': 'primary', 'INSPECTING': 'info', 'COMPLETED': 'success', 'CANCELLED': 'error' }
      return map[status] || 'default'
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/order-detail/index?id=' + id })
    }
  },
  onReachBottom() {
    this.loadMore()
  }
}
</script>

<style lang="scss" scoped>
.page-content { min-height: 100vh; background: #f5f5f5; padding: 20rpx 30rpx; }
.order-card {
  background: #fff; border-radius: 16rpx; margin-bottom: 16rpx;
  display: flex; padding: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.order-image { width: 200rpx; height: 150rpx; border-radius: 12rpx; margin-right: 20rpx; }
.order-info { flex: 1; }
.order-title { font-size: 28rpx; font-weight: 600; color: #333; }
.order-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 12rpx; }
.order-price { font-size: 32rpx; font-weight: 700; color: #f56c6c; }
.order-time { font-size: 22rpx; color: #999; display: block; margin-top: 8rpx; }
</style>
