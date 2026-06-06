<template>
  <view>
    <u-navbar title="订单详情" :border-bottom="false" :placeholder="true"></u-navbar>
    <view class="page-content">
      <view class="status-bar" :class="'status-' + order.status">
        <u-icon :name="statusIcon" size="64" color="#fff"></u-icon>
        <text class="status-text">{{ statusText }}</text>
        <text class="status-desc">{{ statusDesc }}</text>
      </view>
      <view class="section">
        <view class="section-title">车辆信息</view>
        <view class="car-row" @click="toCarDetail">
          <image :src="order.coverImage || '/static/default-car.png'" mode="aspectFill" class="car-image"></image>
          <view class="car-info">
            <text class="car-title">{{ order.carTitle || order.title }}</text>
            <text class="car-price">{{ formatPrice(order.price) }}</text>
          </view>
          <u-icon name="arrow-right" size="28" color="#999"></u-icon>
        </view>
      </view>
      <view class="section">
        <view class="section-title">交易信息</view>
        <u-cell-group :border="false">
          <u-cell-item title="订单编号" :value="order.orderNo" :border-bottom="true"></u-cell-item>
          <u-cell-item title="下单时间" :value="formatTime(order.createTime)" :border-bottom="true"></u-cell-item>
          <u-cell-item title="交易金额" :value="formatPrice(order.price)" :border-bottom="true"></u-cell-item>
          <u-cell-item title="定金金额" :value="formatPrice(order.depositAmount)" :border-bottom="true"></u-cell-item>
          <u-cell-item title="卖家" :value="order.sellerName" :border-bottom="true" @click="toSeller"></u-cell-item>
          <u-cell-item title="买家" :value="order.buyerName" :border-bottom="true"></u-cell-item>
        </u-cell-group>
      </view>
      <view class="section" v-if="order.contractId">
        <view class="section-title">电子合同</view>
        <u-cell-item title="查看合同" value="点击查看" @click="toContract"></u-cell-item>
      </view>
      <view class="action-bar" v-if="showActions">
        <u-button v-if="order.status === 'PENDING'" type="warning" @click="handleCancel" :plain="true">取消订单</u-button>
        <u-button v-if="order.status === 'PENDING'" type="primary" @click="handleConfirm">确认订单</u-button>
        <u-button v-if="order.status === 'DEPOSIT_PAID'" type="warning" @click="handleDispute">发起纠纷</u-button>
        <u-button v-if="order.status === 'INSPECTING'" type="success" @click="handleComplete">完成交易</u-button>
        <u-button v-if="order.status === 'COMPLETED'" type="primary" @click="toContract" :plain="true">查看合同</u-button>
      </view>
    </view>
  </view>
</template>

<script>
import { getOrderDetail, confirmOrder, cancelOrder, completeOrder, payDeposit } from '@/api/order'
import { formatPrice, formatTime } from '@/utils/format'

export default {
  data() {
    return {
      order: {},
      id: ''
    }
  },
  computed: {
    statusText() {
      const map = { 'PENDING': '待确认', 'DEPOSIT_PAID': '已付定金', 'INSPECTING': '验车中', 'COMPLETED': '交易完成', 'CANCELLED': '已取消' }
      return map[this.order.status] || this.order.status
    },
    statusDesc() {
      const map = { 'PENDING': '买家已下单，等待卖家确认', 'DEPOSIT_PAID': '定金已支付，等待验车', 'INSPECTING': '验车进行中', 'COMPLETED': '交易已完成，感谢使用5D好车', 'CANCELLED': '订单已取消' }
      return map[this.order.status] || ''
    },
    statusIcon() {
      const map = { 'PENDING': 'clock', 'DEPOSIT_PAID': 'rmb-circle', 'INSPECTING': 'search', 'COMPLETED': 'checkmark-circle', 'CANCELLED': 'close-circle' }
      return map[this.order.status] || 'info-circle'
    },
    showActions() {
      return this.order.status && this.order.status !== 'CANCELLED'
    }
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id
      this.loadDetail()
    }
  },
  methods: {
    formatPrice,
    formatTime,
    async loadDetail() {
      try {
        const res = await getOrderDetail(this.id)
        this.order = res.data
      } catch (e) {}
    },
    toCarDetail() {
      if (this.order.carId) {
        uni.navigateTo({ url: '/pages/car-detail/index?id=' + this.order.carId })
      }
    },
    toSeller() {
      if (this.order.sellerId) {
        uni.navigateTo({ url: '/pages/seller-home/index?id=' + this.order.sellerId })
      }
    },
    toContract() {
      uni.navigateTo({ url: '/pages/contract-detail/index?id=' + this.order.contractId })
    },
    async handleConfirm() {
      try {
        await confirmOrder(this.id)
        uni.$u.toast('已确认订单')
        this.loadDetail()
      } catch (e) {}
    },
    async handleCancel() {
      try {
        await cancelOrder(this.id, '用户主动取消')
        uni.$u.toast('已取消')
        this.loadDetail()
      } catch (e) {}
    },
    async handleComplete() {
      try {
        await completeOrder(this.id)
        uni.$u.toast('交易已完成')
        this.loadDetail()
      } catch (e) {}
    },
    handleDispute() {
      uni.navigateTo({ url: '/pages/customer-service/index?orderId=' + this.id })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-content { min-height: 100vh; background: #f5f5f5; padding-bottom: 120rpx; }
.status-bar {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60rpx 0; color: #fff;
}
.status-PENDING { background: linear-gradient(135deg, #f9ae3d, #f7b731); }
.status-DEPOSIT_PAID { background: linear-gradient(135deg, #3c9cff, #2979ff); }
.status-INSPECTING { background: linear-gradient(135deg, #5ac725, #4ca81b); }
.status-COMPLETED { background: linear-gradient(135deg, #5ac725, #4ca81b); }
.status-CANCELLED { background: linear-gradient(135deg, #999, #777); }
.status-text { font-size: 36rpx; font-weight: 700; margin-top: 12rpx; }
.status-desc { font-size: 24rpx; margin-top: 8rpx; opacity: 0.9; }
.section { background: #fff; margin: 16rpx 0; padding: 30rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }
.car-row { display: flex; align-items: center; }
.car-image { width: 160rpx; height: 120rpx; border-radius: 12rpx; margin-right: 20rpx; }
.car-info { flex: 1; }
.car-title { font-size: 28rpx; font-weight: 600; color: #333; display: block; }
.car-price { font-size: 32rpx; font-weight: 700; color: #f56c6c; display: block; margin-top: 8rpx; }
.action-bar {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  display: flex; gap: 20rpx; padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #eee;
}
.action-bar u-button { flex: 1; }
</style>
