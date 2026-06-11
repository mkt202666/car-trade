<template>
  <view class="page">
    <u-navbar title="我的求购" :border-bottom="false" :placeholder="true">
      <template v-slot:right>
        <view class="nav-right" @click="goPublish">
          <u-icon name="plus-circle" size="36" color="#0369A1"></u-icon>
        </view>
      </template>
    </u-navbar>

    <!-- 状态筛选 -->
    <view class="tabs">
      <view class="tab-item" :class="{ active: currentTab === item.value }" v-for="item in tabs" :key="item.value" @click="switchTab(item.value)">
        <text>{{ item.label }}</text>
      </view>
    </view>

    <!-- 列表 -->
    <scroll-view scroll-y class="list-wrap" @scrolltolower="loadMore">
      <view class="demand-card" v-for="item in demandList" :key="item.id">
        <view class="card-header">
          <view class="car-info">
            <text class="car-name">{{ item.brandName }} {{ item.seriesName }}</text>
            <view class="status-tag" :class="item.status">{{ statusText(item.status) }}</view>
          </view>
          <text class="time">{{ formatTime(item.createdAt) }}</text>
        </view>
        <view class="card-body">
          <view class="info-row" v-if="item.modelName">
            <text class="label">车型</text>
            <text class="value">{{ item.modelName }}</text>
          </view>
          <view class="info-row" v-if="item.yearFrom || item.yearTo">
            <text class="label">年份</text>
            <text class="value">{{ item.yearFrom || '?' }}-{{ item.yearTo || '?' }}</text>
          </view>
          <view class="info-row" v-if="item.priceMin || item.priceMax">
            <text class="label">预算</text>
            <text class="value price">{{ formatPrice(item.priceMin) }}-{{ formatPrice(item.priceMax) }}</text>
          </view>
          <view class="info-row" v-if="item.mileageMax">
            <text class="label">里程</text>
            <text class="value">{{ (item.mileageMax / 10000).toFixed(1) }}万公里内</text>
          </view>
          <view class="info-row" v-if="item.color">
            <text class="label">颜色</text>
            <text class="value">{{ item.color }}</text>
          </view>
          <view class="info-row" v-if="item.cityName">
            <text class="label">城市</text>
            <text class="value">{{ item.cityName }}</text>
          </view>
          <view class="desc" v-if="item.description">
            <text>{{ item.description }}</text>
          </view>
        </view>
        <view class="card-footer" v-if="item.status === 'ACTIVE'">
          <view class="btn-cancel" @click="handleCancel(item)">取消求购</view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="demandList.length === 0 && !loading">
        <text class="empty-icon">🔍</text>
        <text class="empty-title">暂无求购意向</text>
        <text class="empty-desc">发布求购意向，让车商主动匹配</text>
        <view class="btn-publish" @click="goPublish">发布求购</view>
      </view>

      <!-- 加载状态 -->
      <view class="loading-more" v-if="loading">
        <text>加载中...</text>
      </view>
      <view class="no-more" v-if="!hasMore && demandList.length > 0">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getMyDemands, cancelDemand } from '@/api/purchase'

export default {
  data() {
    return {
      currentTab: 'ACTIVE',
      tabs: [
        { label: '进行中', value: 'ACTIVE' },
        { label: '已取消', value: 'CANCELLED' },
        { label: '已成交', value: 'FULFILLED' }
      ],
      demandList: [],
      page: 1,
      hasMore: true,
      loading: false
    }
  },
  onShow() {
    this.refresh()
  },
  methods: {
    refresh() {
      this.page = 1
      this.hasMore = true
      this.demandList = []
      this.fetchData()
    },
    async fetchData() {
      if (this.loading || !this.hasMore) return
      this.loading = true
      try {
        const res = await getMyDemands({ page: this.page, size: 10 })
        const data = res.data
        let records = []
        if (data && data.list) {
          records = data.list
        } else if (data && data.records) {
          records = data.records
        } else if (Array.isArray(data)) {
          records = data
        }
        this.demandList = this.page === 1 ? records : [...this.demandList, ...records]
        this.hasMore = records.length >= 10
        this.page++
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      this.fetchData()
    },
    switchTab(value) {
      this.currentTab = value
      this.refresh()
    },
    goPublish() {
      uni.navigateTo({ url: '/pages/purchase-demand/index' })
    },
    async handleCancel(item) {
      uni.showModal({
        title: '提示',
        content: '确定取消该求购意向吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await cancelDemand(item.id)
              item.status = 'CANCELLED'
              uni.$u.toast('已取消')
            } catch (e) {
              uni.$u.toast('操作失败')
            }
          }
        }
      })
    },
    statusText(status) {
      const map = { ACTIVE: '进行中', CANCELLED: '已取消', FULFILLED: '已成交' }
      return map[status] || status
    },
    formatPrice(val) {
      if (!val) return '?'
      return (Number(val) / 10000).toFixed(1) + '万'
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      return `${d.getMonth() + 1}-${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F8FAFC;
}
.nav-right {
  padding-right: 24rpx;
}
.tabs {
  display: flex;
  background: #fff;
  padding: 0 24rpx;
  border-bottom: 1rpx solid #F1F5F9;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #64748B;
  position: relative;
  &.active {
    color: #0369A1;
    font-weight: 600;
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 48rpx;
      height: 4rpx;
      background: #0369A1;
      border-radius: 2rpx;
    }
  }
}
.list-wrap {
  height: calc(100vh - 200rpx);
  padding: 20rpx 24rpx;
}
.demand-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #F1F5F9;
}
.car-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.car-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #0F172A;
}
.status-tag {
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  &.ACTIVE { background: #DBEAFE; color: #0369A1; }
  &.CANCELLED { background: #F1F5F9; color: #64748B; }
  &.FULFILLED { background: #D1FAE5; color: #059669; }
}
.time {
  font-size: 22rpx;
  color: #94A3B8;
}
.card-body {
  padding: 20rpx 24rpx;
}
.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}
.label {
  font-size: 24rpx;
  color: #94A3B8;
  width: 80rpx;
}
.value {
  font-size: 26rpx;
  color: #334155;
  &.price { color: #EF4444; font-weight: 600; }
}
.desc {
  margin-top: 12rpx;
  padding-top: 12rpx;
  border-top: 1rpx solid #F1F5F9;
  text {
    font-size: 24rpx;
    color: #64748B;
    line-height: 1.6;
  }
}
.card-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16rpx 24rpx;
  border-top: 1rpx solid #F1F5F9;
}
.btn-cancel {
  font-size: 24rpx;
  color: #EF4444;
  padding: 8rpx 24rpx;
  border: 1rpx solid #FCA5A5;
  border-radius: 8rpx;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 40rpx;
}
.empty-icon { font-size: 80rpx; margin-bottom: 24rpx; }
.empty-title { font-size: 32rpx; color: #0F172A; font-weight: 600; margin-bottom: 12rpx; }
.empty-desc { font-size: 26rpx; color: #64748B; margin-bottom: 32rpx; }
.btn-publish {
  background: #0369A1;
  color: #fff;
  padding: 16rpx 48rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}
.loading-more, .no-more {
  text-align: center;
  padding: 24rpx;
  font-size: 24rpx;
  color: #94A3B8;
}
</style>
