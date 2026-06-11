<template>
  <view class="page">
    <u-navbar title="我的订单" :border-bottom="false" :placeholder="true"></u-navbar>

    <!-- 卖出/买入统计入口 -->
    <view class="entry-row">
      <view class="entry-item" :class="{ active: currentRole === 'SELLER' }" @click="switchRole('SELLER')">
        <view class="entry-icon-wrap seller">
          <text class="entry-icon">↑</text>
        </view>
        <view class="entry-text">
          <text class="entry-num">{{ orderStats.soldCount }}</text>
          <text class="entry-label">卖出的车</text>
        </view>
        <view class="entry-active-bar" v-if="currentRole === 'SELLER'"></view>
      </view>
      <view class="entry-divider"></view>
      <view class="entry-item" :class="{ active: currentRole === 'BUYER' }" @click="switchRole('BUYER')">
        <view class="entry-icon-wrap buyer">
          <text class="entry-icon">↓</text>
        </view>
        <view class="entry-text">
          <text class="entry-num">{{ orderStats.boughtCount }}</text>
          <text class="entry-label">买到的车</text>
        </view>
        <view class="entry-active-bar" v-if="currentRole === 'BUYER'"></view>
      </view>
    </view>

    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input">
        <view class="search-icon-wrap">
          <text class="search-icon">⌕</text>
        </view>
        <input
          class="search-field"
          :placeholder="currentRole === 'SELLER' ? '搜索卖出的车' : '搜索买到的车'"
          placeholder-class="placeholder"
          v-model="searchKeyword"
        />
        <view class="search-clear" v-if="searchKeyword" @click="searchKeyword = ''">
          <text>✕</text>
        </view>
      </view>
    </view>

    <!-- 状态筛选 Tab -->
    <view class="status-tabs">
      <scroll-view scroll-x class="status-scroll" :show-scrollbar="false">
        <view
          class="status-tab"
          v-for="(tab, index) in statusTabs"
          :key="index"
          :class="{ active: currentStatus === index }"
          @click="switchStatus(index)"
        >
          <text class="status-tab-text">{{ tab.name }}</text>
          <view class="status-tab-indicator" v-if="currentStatus === index"></view>
        </view>
      </scroll-view>
    </view>

    <!-- 订单列表 -->
    <view class="order-list">
      <view class="order-card" v-for="(item, idx) in filteredOrders" :key="item.id" :style="{ animationDelay: (idx * 40) + 'ms' }">
        <view class="order-header">
          <view class="counterparty-wrap">
            <text class="counterparty-label">{{ currentRole === 'SELLER' ? '买家' : '卖家' }}：</text>
            <text class="counterparty-name">{{ item.buyerName || item.sellerName || '未知' }}</text>
          </view>
          <view class="status-badge" :class="'status-' + item.status">
            <view class="status-dot"></view>
            <text>{{ item.statusText }}</text>
          </view>
        </view>
        <view class="order-body" @click="toDetail(item.id)">
          <view class="order-image-wrap">
            <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="order-image"></image>
            <view class="order-image-shine"></view>
          </view>
          <view class="order-info">
            <view class="order-title">{{ item.carTitle || item.title || '车辆信息' }}</view>
            <view class="order-meta">
              <text class="order-car-id">车源ID {{ item.carId || '--' }}</text>
            </view>
            <view class="order-price-row">
              <text class="order-price-symbol">¥</text>
              <text class="order-price">{{ formatPrice(item.price) }}</text>
            </view>
          </view>
          <view class="order-arrow">
            <text>›</text>
          </view>
        </view>
        <view class="order-actions">
          <view class="action-btn secondary" @click="toDetail(item.id)">
            <text>查看详情</text>
          </view>
          <view class="action-btn primary" @click="contactUser(item)">
            <text>{{ currentRole === 'SELLER' ? '联系买家' : '联系卖家' }}</text>
            <view class="btn-glow"></view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="filteredOrders.length === 0 && !loading">
      <view class="empty-icon-wrap">
        <text class="empty-icon">📋</text>
      </view>
      <text class="empty-title">暂无订单</text>
      <text class="empty-desc">{{ currentRole === 'SELLER' ? '您还没有卖出任何车辆' : '您还没有购买任何车辆' }}</text>
    </view>
  </view>
</template>

<script>
import { getOrderList, getOrderStats } from '@/api/order'
import { formatPrice } from '@/utils/format'
import { requireAuth } from '@/utils/auth'

const STATUS_MAP = {
  PENDING: '待确认',
  IN_TRANSACTION: '交易中',
  DISPUTE: '纠纷中',
  COMPLETED: '已完成',
  CANCELLED: '已终止'
}

export default {
  name: 'TradeIndex',
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
    // 未登录不请求订单接口
    if (!requireAuth()) return
    this.loadData()
  },
  methods: {
    formatPrice,
    formatStatus(status) {
      return STATUS_MAP[status] || status
    },
    switchRole(role) {
      if (!requireAuth()) return
      if (this.currentRole === role) return
      this.currentRole = role
      this.currentStatus = 0
      this.searchKeyword = ''
      this.loadData()
    },
    switchStatus(index) {
      if (!requireAuth()) return
      if (this.currentStatus === index) return
      this.currentStatus = index
      this.fetchOrders()
    },
    async loadData() {
      if (!requireAuth()) return
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
        // 被前端拦截（未登录）或后端返回 401 —— 不再打印 "error"，避免控制台红错
        if (e && e.statusCode !== 401) {
          console.error('load orders failed:', e)
        }
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
/* =========================================================
   5D好车 - 订单交易中心 - 高级UI设计
   颜色系统: 深蓝主色 #0369A1 | 琥珀金 #F59E0B | 灰色层次
   动效: fadeInUp | hover scale | smooth transitions
   ========================================================= */

/* ============ 主容器 ============ */
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8FAFC 0%, #EEF2F7 100%);
  padding-bottom: 220rpx;
}

/* ============ 入口统计行 ============ */
.entry-row {
  margin: 24rpx;
  padding: 32rpx;
  background: #ffffff;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.08), 0 2rpx 8rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 100ms both;
}

.entry-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 16rpx 12rpx;
  border-radius: 20rpx;
  cursor: pointer;
  position: relative;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);

  &:active {
    transform: scale(0.96);
  }
}

.entry-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);

  &.seller {
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.12) 0%, rgba(5, 150, 105, 0.08) 100%);

    .entry-icon {
      color: #10B981;
    }
  }

  &.buyer {
    background: linear-gradient(135deg, rgba(3, 105, 161, 0.12) 0%, rgba(2, 132, 199, 0.08) 100%);

    .entry-icon {
      color: #0369A1;
    }
  }
}

.entry-icon {
  font-size: 40rpx;
  font-weight: 700;
}

.entry-text {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.entry-num {
  font-size: 44rpx;
  font-weight: 800;
  color: #0F172A;
  line-height: 1.2;
  letter-spacing: -1rpx;
}

.entry-label {
  font-size: 24rpx;
  color: #64748B;
  margin-top: 4rpx;
  font-weight: 500;
}

.entry-active-bar {
  position: absolute;
  left: 50%;
  bottom: -4rpx;
  transform: translateX(-50%);
  width: 40rpx;
  height: 6rpx;
  background: linear-gradient(90deg, #0369A1 0%, #0284C7 100%);
  border-radius: 4rpx;
}

.entry-item.active {
  .entry-num {
    color: #0369A1;
  }

  .entry-icon-wrap.seller {
    background: linear-gradient(135deg, #10B981 0%, #059669 100%);
    box-shadow: 0 8rpx 24rpx rgba(16, 185, 129, 0.3);
  }
  .entry-icon-wrap.seller .entry-icon { color: #ffffff; }

  .entry-icon-wrap.buyer {
    background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
    box-shadow: 0 8rpx 24rpx rgba(3, 105, 161, 0.3);
  }
  .entry-icon-wrap.buyer .entry-icon { color: #ffffff; }
}

.entry-divider {
  width: 1rpx;
  height: 72rpx;
  background: linear-gradient(180deg, transparent 0%, #E2E8F0 30%, #E2E8F0 70%, transparent 100%);
  margin: 0 12rpx;
}

/* ============ 搜索栏 ============ */
.search-bar {
  padding: 0 24rpx 24rpx;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 150ms both;
}

.search-input {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 48rpx;
  padding: 0 24rpx;
  height: 88rpx;
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);

  &:focus-within {
    border-color: #0369A1;
    box-shadow: 0 8rpx 24rpx rgba(3, 105, 161, 0.12), 0 0 0 6rpx rgba(3, 105, 161, 0.08);
  }
}

.search-icon-wrap {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-icon {
  font-size: 40rpx;
  color: #94A3B8;
  font-weight: 300;
}

.search-field {
  flex: 1;
  font-size: 28rpx;
  color: #0F172A;
  height: 88rpx;
  margin-left: 8rpx;
}

.placeholder {
  color: #94A3B8;
}

.search-clear {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F1F5F9;
  border-radius: 50%;
  cursor: pointer;
  transition: all 200ms ease;

  text {
    font-size: 24rpx;
    color: #64748B;
  }

  &:active {
    transform: scale(0.9);
    background: #E2E8F0;
  }
}

/* ============ 状态筛选 Tab ============ */
.status-tabs {
  background: #ffffff;
  padding: 0;
  border-bottom: 1rpx solid rgba(226, 232, 240, 0.6);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 200ms both;
}

.status-scroll {
  white-space: nowrap;
  padding: 0 16rpx;
}

.status-tab {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  padding: 28rpx 24rpx 20rpx;
  position: relative;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);
}

.status-tab-text {
  font-size: 28rpx;
  color: #64748B;
  font-weight: 500;
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);
}

.status-tab.active {
  .status-tab-text {
    color: #0369A1;
    font-weight: 700;
    font-size: 30rpx;
  }
}

.status-tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 48rpx;
  height: 6rpx;
  background: linear-gradient(90deg, #0369A1 0%, #0284C7 100%);
  border-radius: 4rpx;
  box-shadow: 0 2rpx 8rpx rgba(3, 105, 161, 0.3);
  animation: indicatorSlide 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes indicatorSlide {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 48rpx;
    opacity: 1;
  }
}

/* ============ 订单列表 ============ */
.order-list {
  padding: 24rpx;
}

.order-card {
  background: #ffffff;
  border-radius: 28rpx;
  margin-bottom: 24rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(226, 232, 240, 0.6);
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) both;
  position: relative;
  overflow: hidden;

  &:active {
    transform: scale(0.99);
    box-shadow: 0 2rpx 12rpx rgba(15, 23, 42, 0.08);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx dashed #E2E8F0;
}

.counterparty-wrap {
  display: flex;
  align-items: center;
}

.counterparty-label {
  font-size: 24rpx;
  color: #64748B;
}

.counterparty-name {
  font-size: 28rpx;
  color: #0F172A;
  font-weight: 600;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 10rpx 20rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  font-weight: 600;
  transition: all 250ms ease;

  .status-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
  }

  &.status-PENDING {
    background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
    color: #B45309;
    .status-dot { background: #F59E0B; box-shadow: 0 0 8rpx #F59E0B; }
  }

  &.status-IN_TRANSACTION {
    background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%);
    color: #1E40AF;
    .status-dot { background: #0369A1; box-shadow: 0 0 8rpx #0369A1; animation: pulse 2s ease-in-out infinite; }
  }

  &.status-DISPUTE {
    background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%);
    color: #B91C1C;
    .status-dot { background: #DC2626; box-shadow: 0 0 8rpx #DC2626; }
  }

  &.status-COMPLETED {
    background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
    color: #047857;
    .status-dot { background: #10B981; box-shadow: 0 0 8rpx #10B981; }
  }

  &.status-CANCELLED {
    background: linear-gradient(135deg, #F3F4F6 0%, #E5E7EB 100%);
    color: #4B5563;
    .status-dot { background: #6B7280; }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.3); }
}

.order-body {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8rpx 0;
}

.order-image-wrap {
  width: 180rpx;
  height: 130rpx;
  border-radius: 20rpx;
  margin-right: 24rpx;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #E2E8F0 0%, #CBD5E1 100%);
}

.order-image {
  width: 100%;
  height: 100%;
  border-radius: 20rpx;
}

.order-image-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 60%;
  height: 100%;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.4) 50%, transparent 100%);
  animation: shine 3s ease-in-out infinite;
}

@keyframes shine {
  0% { left: -100%; }
  50%, 100% { left: 150%; }
}

.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.order-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-meta {
  margin-top: 10rpx;
}

.order-car-id {
  font-size: 22rpx;
  color: #94A3B8;
  font-family: 'SF Mono', monospace;
}

.order-price-row {
  display: flex;
  align-items: baseline;
  margin-top: 12rpx;
}

.order-price-symbol {
  font-size: 24rpx;
  color: #EF4444;
  font-weight: 600;
}

.order-price {
  font-size: 36rpx;
  color: #EF4444;
  font-weight: 800;
  letter-spacing: -0.5rpx;
  margin-left: 4rpx;
}

.order-arrow {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  text {
    font-size: 40rpx;
    color: #CBD5E1;
    font-weight: 300;
  }
}

.order-card:active .order-arrow text {
  color: #0369A1;
  transform: translateX(4rpx);
  transition: all 200ms ease;
}

/* ============ 操作按钮 ============ */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
  margin-top: 28rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #F1F5F9;
}

.action-btn {
  padding: 18rpx 36rpx;
  border-radius: 40rpx;
  font-size: 26rpx;
  font-weight: 600;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;

  &.secondary {
    background: #F8FAFC;
    color: #475569;
    border: 1rpx solid #E2E8F0;

    &:active {
      background: #E2E8F0;
      transform: scale(0.95);
    }
  }

  &.primary {
    background: linear-gradient(135deg, #0369A1 0%, #0284C7 50%, #0EA5E9 100%);
    color: #ffffff;
    box-shadow: 0 8rpx 24rpx rgba(3, 105, 161, 0.3);

    &:active {
      transform: scale(0.95);
      box-shadow: 0 4rpx 12rpx rgba(3, 105, 161, 0.35);
    }

    .btn-glow {
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
      animation: btnShine 2.5s ease-in-out infinite;
    }
  }
}

@keyframes btnShine {
  0% { left: -100%; }
  50%, 100% { left: 100%; }
}

/* ============ 空状态 ============ */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
  animation: fadeInUp 600ms cubic-bezier(0.4, 0, 0.2, 1) 200ms both;
}

.empty-icon-wrap {
  width: 160rpx;
  height: 160rpx;
  background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(3, 105, 161, 0.1);
}

.empty-icon {
  font-size: 80rpx;
}

.empty-title {
  font-size: 32rpx;
  color: #0F172A;
  font-weight: 700;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #64748B;
}

/* ============ 入场动画 ============ */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ============ 响应式适配 ============ */
@media (max-width: 320px) {
  .entry-row {
    padding: 24rpx;
  }
  .entry-icon-wrap {
    width: 64rpx;
    height: 64rpx;
    border-radius: 16rpx;
  }
  .entry-icon {
    font-size: 32rpx;
  }
  .entry-num {
    font-size: 36rpx;
  }
  .entry-label {
    font-size: 22rpx;
  }
  .search-input {
    height: 80rpx;
  }
  .search-field {
    font-size: 26rpx;
    height: 80rpx;
  }
  .order-card {
    padding: 20rpx;
    border-radius: 24rpx;
  }
  .order-image-wrap {
    width: 140rpx;
    height: 110rpx;
    margin-right: 16rpx;
  }
  .order-title {
    font-size: 26rpx;
  }
  .order-price {
    font-size: 30rpx;
  }
  .action-btn {
    padding: 14rpx 24rpx;
    font-size: 24rpx;
  }
}

@media (min-width: 376px) {
  .entry-num {
    font-size: 48rpx;
  }
  .order-title {
    font-size: 32rpx;
  }
  .order-price {
    font-size: 40rpx;
  }
  .status-tab-text {
    font-size: 30rpx;
  }
  .status-tab.active .status-tab-text {
    font-size: 32rpx;
  }
}

/* ============ 减少动画偏好 ============ */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>
