<template>
  <view class="page">
    <u-navbar title="我的订单" :border-bottom="false" :placeholder="true"></u-navbar>

    <!-- 卖出的车/买到的车 入口 -->
    <view class="entry-row">
      <view class="entry-item" :class="{ active: currentRole === 'SELLER' }" @click="switchRole('SELLER')">
        <text class="entry-num">{{ orderStats.soldCount }}</text>
        <text class="entry-label">卖出的车</text>
      </view>
      <view class="entry-item" :class="{ active: currentRole === 'BUYER' }" @click="switchRole('BUYER')">
        <text class="entry-num">{{ orderStats.boughtCount }}</text>
        <text class="entry-label">买到的车</text>
      </view>
    </view>

    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input">
        <u-icon name="search" size="32" color="#999"></u-icon>
        <input class="search-field" :placeholder="currentRole === 'SELLER' ? '搜索卖出的车' : '搜索买到的车'" placeholder-style="color:#ccc" v-model="searchKeyword" />
      </view>
    </view>

    <!-- 状态筛选 Tab -->
    <view class="status-tabs">
      <u-tabs :list="statusTabs" :current="currentStatus" @change="switchStatus" :is-scroll="true" :duration="0.3" active-color="#3c9cff" :bold="false" bg-color="#fff" gutter="20" bar-width="40"></u-tabs>
    </view>

    <!-- 订单列表 -->
    <view class="order-list">
      <view class="order-card" v-for="item in filteredOrders" :key="item.id">
        <view class="order-header">
          <text class="counterparty">{{ currentRole === 'SELLER' ? '买家：' : '卖家：' }}{{ item.buyerName || item.sellerName }}</text>
          <text class="status-text">{{ item.statusText }}</text>
        </view>
        <view class="order-body" @click="toDetail(item.id)">
          <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="order-image"></image>
          <view class="order-info">
            <view class="order-title u-line-1">{{ item.carTitle || item.title }}</view>
            <view class="order-meta">
              <text class="order-car-id">车源ID {{ item.carId }}</text>
              <text class="order-price">成交价 {{ formatPrice(item.price) }}</text>
            </view>
          </view>
        </view>
        <view class="order-actions">
          <u-button size="mini" :plain="true" @click="toDetail(item.id)">详情</u-button>
          <u-button size="mini" type="primary" @click="contactUser(item)">{{ currentRole === 'SELLER' ? '联系买家' : '联系卖家' }}</u-button>
        </view>
      </view>
    </view>

    <u-empty v-if="filteredOrders.length === 0" mode="order" text="暂无订单"></u-empty>
  </view>
</template>

<script>
import { getOrderList, getOrderStats } from '@/api/order'
import { formatPrice } from '@/utils/format'

const STATUS_MAP = {
  PENDING: '待确认',
  IN_TRANSACTION: '交易中',
  DISPUTE: '纠纷中',
  COMPLETED: '已完成',
  CANCELLED: '已终止'
}

export default {
  data() {
    return {
      orderStats: {
        soldCount: 0,
        boughtCount: 0
      },
      currentRole: 'SELLER',
      searchKeyword: '',
      currentStatus: 0,
      statusTabs: [
        { name: '全部' },
        { name: '待确认' },
        { name: '交易中' },
        { name: '纠纷中' },
        { name: '已完成' },
        { name: '已终止' }
      ],
      statusMap: ['ALL', 'PENDING', 'IN_TRANSACTION', 'DISPUTE', 'COMPLETED', 'CANCELLED'],
      orderList: [],
      loading: false
    }
  },
  computed: {
    filteredOrders() {
      let list = this.orderList
      if (this.searchKeyword) {
        const kw = this.searchKeyword.toLowerCase()
        list = list.filter(o => {
          const title = (o.carTitle || '').toLowerCase()
          const id = (o.carId || '').toLowerCase()
          const counterparty = (o.buyerName || o.sellerName || '').toLowerCase()
          return title.includes(kw) || id.includes(kw) || counterparty.includes(kw)
        })
      }
      return list
    }
  },
  onShow() {
    this.loadData()
  },
  methods: {
    formatPrice,
    formatStatus(status) {
      return STATUS_MAP[status] || status
    },
    switchRole(role) {
      this.currentRole = role
      this.currentStatus = 0
      this.searchKeyword = ''
      this.loadData()
    },
    switchStatus(e) {
      this.currentStatus = e.index
      this.fetchOrders()
    },
    async loadData() {
      this.loading = true
      try {
        const statsRes = await getOrderStats()
        if (statsRes && statsRes.data) {
          this.orderStats = {
            soldCount: statsRes.data.soldCount || 0,
            boughtCount: statsRes.data.boughtCount || 0
          }
        }
        await this.fetchOrders()
      } catch (e) {
        console.error('load orders failed:', e)
      } finally {
        this.loading = false
      }
    },
    async fetchOrders() {
      try {
        const status = this.statusMap[this.currentStatus]
        const params = {
          role: this.currentRole,
          page: 1,
          size: 50
        }
        if (status && status !== 'ALL') {
          params.status = status
        }
        const res = await getOrderList(params)
        const data = res.data
        let list = data.list || data.records || data || []
        list = list.map(o => ({
          ...o,
          statusText: STATUS_MAP[o.status] || o.status,
          counterpartyName: this.currentRole === 'SELLER' ? o.buyerName : o.sellerName
        }))
        this.orderList = list
      } catch (e) {
        console.error('fetch orders failed:', e)
      }
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/order-detail/index?id=' + id })
    },
    contactUser(item) {
      uni.navigateTo({ url: '/pages/customer-service/index?orderId=' + item.id + '&name=' + (item.buyerName || item.sellerName) })
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 入口行 */
.entry-row {
  display: flex;
  background: #fff;
  padding: 20rpx 30rpx;
  gap: 20rpx;
}
.entry-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  border-radius: 12rpx;
  background: #f5f5f5;
  transition: all 0.2s;
}
.entry-item.active {
  background: #e8f4ff;
}
.entry-num {
  font-size: 40rpx;
  font-weight: 700;
  color: #3c9cff;
  display: block;
}
.entry-label {
  font-size: 24rpx;
  color: #666;
  display: block;
  margin-top: 4rpx;
}

/* 搜索栏 */
.search-bar {
  padding: 16rpx 30rpx;
  background: #fff;
}
.search-input {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 40rpx;
  padding: 12rpx 24rpx;
}
.search-field {
  flex: 1;
  font-size: 26rpx;
  margin-left: 12rpx;
}

/* 状态 Tab */
.status-tabs {
  background: #fff;
  padding: 0 0 10rpx;
}

/* 订单列表 */
.order-list {
  padding: 20rpx 30rpx;
}
.order-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}
.counterparty {
  font-size: 26rpx;
  color: #333;
  font-weight: 600;
}
.status-text {
  font-size: 24rpx;
  color: #999;
}
.order-body {
  display: flex;
  cursor: pointer;
}
.order-image {
  width: 160rpx;
  height: 120rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}
.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.order-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
}
.order-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-car-id {
  font-size: 22rpx;
  color: #999;
}
.order-price {
  font-size: 28rpx;
  font-weight: 700;
  color: #f56c6c;
}
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f5f5f5;
}
</style>