<template>
  <view>
    <u-navbar title="全部订单" :border-bottom="false" :placeholder="true"></u-navbar>
    <view class="page-content">
      <u-tabs :list="statusTabs" :current="currentTab" @change="switchTab" active-color="#3c9cff" :bold="false"></u-tabs>
      <view v-if="loading" class="loading-wrap">
        <u-loading mode="flower" size="50"></u-loading>
      </view>
      <block v-else>
        <view class="order-list">
          <view class="order-card" v-for="item in orderList" :key="item.id" @click="toDetail(item.id)">
            <view class="order-header">
              <text class="order-number">订单号: {{ item.orderNo }}</text>
              <u-tag :text="statusText(item.status)" :type="statusType(item.status)" size="mini"></u-tag>
            </view>
            <view class="order-body">
              <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="order-image"></image>
              <view class="order-info">
                <view class="order-title u-line-2">{{ item.carTitle || item.title }}</view>
                <view class="order-price">{{ formatPrice(item.price) }}</view>
              </view>
            </view>
            <view class="order-footer">
              <text class="order-time">{{ formatTime(item.createTime) }}</text>
              <view class="order-actions" v-if="item.status === 'PENDING'">
                <u-button size="mini" type="error" plain @click.stop="cancelOrder(item.id)">取消</u-button>
                <u-button size="mini" type="primary" @click.stop="toDetail(item.id)">确认</u-button>
              </view>
            </view>
          </view>
        </view>
        <u-empty v-if="orderList.length === 0 && !loading" mode="order" text="暂无订单"></u-empty>
        <u-loadmore v-if="orderList.length > 0" :status="loadStatus" @loadmore="loadMore"></u-loadmore>
      </block>
    </view>
  </view>
</template>

<script>
import { getOrderList, cancelOrder } from '@/api/order'
import { formatPrice, formatTime } from '@/utils/format'

export default {
  data() {
    return {
      statusTabs: [{ name: '全部' }, { name: '待确认' }, { name: '已付定金' }, { name: '验车中' }, { name: '已完成' }, { name: '已取消' }],
      currentTab: 0,
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
  onPullDownRefresh() {
    this.page = 1
    this.hasMore = true
    this.orderList = []
    this.loadOrders().then(() => { uni.stopPullDownRefresh() })
  },
  methods: {
    formatPrice,
    formatTime,
    async loadOrders() {
      this.loading = true
      this.loadStatus = 'loading'
      try {
        const params = { page: this.page, pageSize: this.pageSize }
        if (this.currentTab > 0) params.status = ['PENDING', 'DEPOSIT_PAID', 'INSPECTING', 'COMPLETED', 'CANCELLED'][this.currentTab - 1]
        const res = await getOrderList(params)
        const list = res.data.records || res.data.list || res.data || []
        this.orderList = this.page === 1 ? list : this.orderList.concat(list)
        this.hasMore = list.length >= this.pageSize
        this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
      } catch (e) {
        this.loadStatus = 'loadmore'
      }
      this.loading = false
    },
    switchTab(index) {
      this.currentTab = index
      this.page = 1
      this.hasMore = true
      this.orderList = []
      this.loadOrders()
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
    },
    async cancelOrder(id) {
      try {
        await cancelOrder(id, '用户主动取消')
        uni.$u.toast('已取消')
        this.page = 1
        this.orderList = []
        this.loadOrders()
      } catch (e) {}
    }
  }
}
</script>

<style lang="scss" scoped>
.page-content { min-height: 100vh; background: #f5f5f5; }
.loading-wrap { display: flex; justify-content: center; padding: 100rpx 0; }
.order-list { padding: 20rpx 30rpx; }
.order-card { background: #fff; border-radius: 16rpx; margin-bottom: 20rpx; overflow: hidden; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06); }
.order-header { display: flex; justify-content: space-between; align-items: center; padding: 20rpx; border-bottom: 1rpx solid #f5f5f5; }
.order-number { font-size: 22rpx; color: #999; }
.order-body { display: flex; padding: 20rpx; }
.order-image { width: 160rpx; height: 120rpx; border-radius: 12rpx; margin-right: 20rpx; }
.order-info { flex: 1; }
.order-title { font-size: 26rpx; color: #333; }
.order-price { font-size: 30rpx; font-weight: 700; color: #f56c6c; margin-top: 10rpx; }
.order-footer { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 20rpx; border-top: 1rpx solid #f5f5f5; }
.order-time { font-size: 22rpx; color: #999; }
.order-actions { display: flex; gap: 16rpx; }
</style>
