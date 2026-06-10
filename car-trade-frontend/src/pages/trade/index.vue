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
        <u-icon name="search" size="32" color="#64748B"></u-icon>
        <input
          class="search-field"
          :placeholder="currentRole === 'SELLER' ? '搜索卖出的车' : '搜索买到的车'"
          placeholder-class="placeholder"
          v-model="searchKeyword"
        />
      </view>
    </view>

    <!-- 状态筛选 Tab -->
    <view class="status-tabs">
      <u-tabs
        :list="statusTabs"
        :current="currentStatus"
        @change="switchStatus"
        :is-scroll="true"
        :duration="0.3"
        active-color="#0369A1"
        inactive-color="#64748B"
        :bold="false"
        bg-color="#ffffff"
        gutter="20"
        bar-width="40"
      ></u-tabs>
    </view>

    <!-- 订单列表 -->
    <view class="order-list">
      <view class="order-card" v-for="item in filteredOrders" :key="item.id">
        <view class="order-header">
          <text class="counterparty">{{ currentRole === 'SELLER' ? '买家：' : '卖家：' }}{{ item.buyerName || item.sellerName }}</text>
          <view class="status-badge" :class="'status-' + item.status">{{ item.statusText }}</view>
        </view>
        <view class="order-body" @click="toDetail(item.id)">
          <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="order-image"></image>
          <view class="order-info">
            <view class="order-title u-line-1">{{ item.carTitle || item.title }}</view>
            <view class="order-meta">
              <text class="order-car-id">车源ID {{ item.carId }}</text>
              <text class="order-price">¥{{ formatPrice(item.price) }}</text>
            </view>
          </view>
        </view>
        <view class="order-actions">
          <view class="action-btn secondary" @click="toDetail(item.id)">详情</view>
          <view class="action-btn primary" @click="contactUser(item)">{{ currentRole === 'SELLER' ? '联系买家' : '联系卖家' }}</view>
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
/* =========================================
   5D Car Trade 企业级设计系统 - 交易页
   ========================================= */

.page {
  min-height: 100vh;
  background: #F8FAFC;
}

/* ============= 入口行 ============= */
.entry-row {
  display: flex;
  background: #ffffff;
  padding: 24rpx 32rpx;
  gap: 24rpx;
  border-bottom: 1px solid #E2E8F0;
}

.entry-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  border-radius: 16rpx;
  background: #F8FAFC;
  border: 2rpx solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;

  &.active {
    background: #F0F9FF;
    border-color: #0369A1;
  }

  &:active {
    transform: scale(0.98);
  }
}

.entry-num {
  font-size: 40rpx;
  font-weight: 800;
  color: #0369A1;
  display: block;
}

.entry-label {
  font-size: 24rpx;
  color: #64748B;
  display: block;
  margin-top: 8rpx;
}

/* ============= 搜索栏 ============= */
.search-bar {
  padding: 20rpx 32rpx;
  background: #ffffff;
}

.search-input {
  display: flex;
  align-items: center;
  background: #F8FAFC;
  border-radius: 40rpx;
  padding: 16rpx 24rpx;
  border: 1px solid #E2E8F0;
  transition: all 0.2s ease;
}

.search-input:focus-within {
  border-color: #0369A1;
  background: #ffffff;
}

.search-field {
  flex: 1;
  font-size: 26rpx;
  color: #0F172A;
  margin-left: 16rpx;
}

.placeholder {
  color: #94A3B8;
}

/* ============= 状态 Tab ============= */
.status-tabs {
  background: #ffffff;
  padding: 0 0 16rpx;
  border-bottom: 1px solid #E2E8F0;
}

/* ============= 订单列表 ============= */
.order-list {
  padding: 24rpx 32rpx;
}

.order-card {
  background: #ffffff;
  border-radius: 20rpx;
  margin-bottom: 24rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.06);
  border: 1px solid #E2E8F0;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.99);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.counterparty {
  font-size: 26rpx;
  color: #0F172A;
  font-weight: 600;
}

.status-badge {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-weight: 500;

  &.status-PENDING {
    background: #FEF3C7;
    color: #D97706;
  }

  &.status-IN_TRANSACTION {
    background: #DBEAFE;
    color: #0369A1;
  }

  &.status-DISPUTE {
    background: #FEE2E2;
    color: #DC2626;
  }

  &.status-COMPLETED {
    background: #D1FAE5;
    color: #059669;
  }

  &.status-CANCELLED {
    background: #F3F4F6;
    color: #6B7280;
  }
}

.order-body {
  display: flex;
  cursor: pointer;
}

.order-image {
  width: 160rpx;
  height: 120rpx;
  border-radius: 12rpx;
  margin-right: 24rpx;
  flex-shrink: 0;
  background: #E2E8F0;
}

.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.order-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #0F172A;
}

.order-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-car-id {
  font-size: 22rpx;
  color: #64748B;
}

.order-price {
  font-size: 28rpx;
  font-weight: 700;
  color: #EF4444;
}

/* ============= 操作按钮 ============= */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1px solid #F1F5F9;
}

.action-btn {
  padding: 12rpx 32rpx;
  border-radius: 32rpx;
  font-size: 24rpx;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;

  &.secondary {
    background: #F8FAFC;
    color: #64748B;
    border: 1px solid #E2E8F0;

    &:hover {
      background: #F1F5F9;
    }
  }

  &.primary {
    background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
    color: #ffffff;

    &:hover {
      box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.3);
    }
  }

  &:active {
    transform: scale(0.96);
  }
}

/* ============= 可访问性 ============= */
*:focus-visible {
  outline: 2px solid #0369A1;
  outline-offset: 2px;
}
</style>