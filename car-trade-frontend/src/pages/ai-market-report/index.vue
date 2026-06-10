<template>
  <view class="page">
    <u-navbar title="AI行情简报" :border-bottom="false" :placeholder="true">
      <template v-slot:left>
        <view class="nav-back" @click="goBack">
          <u-icon name="arrow-left" size="32" color="#333"></u-icon>
        </view>
      </template>
    </u-navbar>

    <view class="page-content">
      <!-- 我的在售车型 -->
      <view class="section">
        <view class="section-title">我的在售车型</view>
        <view class="car-list" v-if="myCars.length > 0">
          <view class="car-item" v-for="car in myCars" :key="car.id" @click="viewCarDetail(car)">
            <image :src="car.coverImage" mode="aspectFill" class="car-image"></image>
            <view class="car-info">
              <text class="car-name">{{ car.title }}</text>
              <text class="car-price">¥{{ formatPrice(car.price) }}</text>
              <view class="car-tags">
                <text class="tag">{{ car.year }}年</text>
                <text class="tag">{{ formatMileage(car.mileage) }}公里</text>
              </view>
            </view>
            <view class="car-trend" :class="car.trend">
              <u-icon :name="car.trend === 'up' ? 'arrow-up' : 'arrow-down'" size="24" :color="car.trend === 'up' ? '#5ac725' : '#f56c6c'"></u-icon>
              <text>{{ car.trendValue }}%</text>
            </view>
          </view>
        </view>
        <u-empty v-else mode="list" text="暂无在售车型"></u-empty>
      </view>

      <!-- 行情分析 -->
      <view class="section">
        <view class="section-title">行情分析</view>
        <view class="analysis-cards">
          <view class="analysis-card">
            <text class="card-label">市场热度</text>
            <text class="card-value">{{ marketData.hotIndex }}</text>
            <text class="card-trend up">↑ 12%</text>
          </view>
          <view class="analysis-card">
            <text class="card-label">成交率</text>
            <text class="card-value">{{ marketData.dealRate }}%</text>
            <text class="card-trend up">↑ 5%</text>
          </view>
          <view class="analysis-card">
            <text class="card-label">平均价格</text>
            <text class="card-value">¥{{ formatPrice(marketData.avgPrice) }}</text>
            <text class="card-trend down">↓ 3%</text>
          </view>
        </view>
      </view>

      <!-- 竞品分析 -->
      <view class="section">
        <view class="section-title">竞品分析</view>
        <view class="competitor-list">
          <view class="competitor-item" v-for="item in competitors" :key="item.id">
            <view class="competitor-info">
              <text class="competitor-name">{{ item.name }}</text>
              <text class="competitor-count">{{ item.count }}个在售</text>
            </view>
            <view class="competitor-price">
              <text class="price-range">¥{{ formatPrice(item.minPrice) }} - ¥{{ formatPrice(item.maxPrice) }}</text>
              <text class="price-avg">均价 ¥{{ formatPrice(item.avgPrice) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 市场趋势 -->
      <view class="section">
        <view class="section-title">市场趋势</view>
        <view class="trend-chart">
          <view class="chart-header">
            <text class="chart-title">价格走势</text>
            <view class="chart-tabs">
              <text class="tab" :class="{ active: trendTab === 'week' }" @click="trendTab = 'week'">近7天</text>
              <text class="tab" :class="{ active: trendTab === 'month' }" @click="trendTab = 'month'">近30天</text>
            </view>
          </view>
          <view class="chart-content">
            <!-- 这里可以集成图表组件 -->
            <view class="chart-placeholder">
              <text class="chart-text">价格走势图表</text>
              <text class="chart-desc">显示近{{ trendTab === 'week' ? '7' : '30' }}天的价格变化趋势</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 建议 -->
      <view class="section">
        <view class="section-title">AI建议</view>
        <view class="suggestions">
          <view class="suggestion-item" v-for="(item, index) in suggestions" :key="index">
            <u-icon name="lightbulb" size="28" color="#f9ae3d"></u-icon>
            <text class="suggestion-text">{{ item }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyCars, getMarketAnalysis, getCompetitors, getSuggestions } from '@/api/ai'
import { formatPrice, formatMileage } from '@/utils/format'
import { requireAuth } from '@/utils/auth'

export default {
  data() {
    return {
      myCars: [],
      marketData: {
        hotIndex: 0,
        dealRate: 0,
        avgPrice: 0
      },
      competitors: [],
      suggestions: [],
      trendTab: 'week',
      loading: false
    }
  },
  onLoad() {
    if (!requireAuth()) return
    this.loadData()
  },
  methods: {
    formatPrice,
    formatMileage,
    async loadData() {
      this.loading = true
      try {
        await Promise.all([
          this.loadMyCars(),
          this.loadMarketData(),
          this.loadCompetitors(),
          this.loadSuggestions()
        ])
      } catch (e) {
        console.error('加载数据失败', e)
      } finally {
        this.loading = false
      }
    },
    async loadMyCars() {
      try {
        const res = await getMyCars()
        this.myCars = res.data || []
      } catch (e) {
        console.error('加载我的车源失败', e)
      }
    },
    async loadMarketData() {
      try {
        const res = await getMarketAnalysis()
        this.marketData = res.data || {}
      } catch (e) {
        console.error('加载行情数据失败', e)
      }
    },
    async loadCompetitors() {
      try {
        const res = await getCompetitors()
        this.competitors = res.data || []
      } catch (e) {
        console.error('加载竞品数据失败', e)
      }
    },
    async loadSuggestions() {
      try {
        const res = await getSuggestions()
        this.suggestions = res.data || []
      } catch (e) {
        console.error('加载建议失败', e)
      }
    },
    viewCarDetail(car) {
      uni.navigateTo({
        url: `/pages/car-detail/index?id=${car.id}`
      })
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #0F172A;
$cta-color: #0369A1;
$bg-color: #F8FAFC;
$text-color: #020617;
$text-secondary: #64748B;
$border-color: #E2E8F0;
$border-radius: 16rpx;
$shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
$transition: all 0.2s ease;

.page {
  min-height: 100vh;
  background: $bg-color;
}

.page-content {
  padding: 20rpx;
}

.section {
  background: #fff;
  border-radius: $border-radius;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: $shadow;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 24rpx;
}

/* 车源列表 */
.car-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.car-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: $bg-color;
  border-radius: 12rpx;
  cursor: pointer;
  transition: $transition;

  &:active {
    background: rgba(0, 0, 0, 0.05);
  }
}

.car-image {
  width: 120rpx;
  height: 90rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
}

.car-info {
  flex: 1;
}

.car-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
  margin-bottom: 8rpx;
}

.car-price {
  font-size: 28rpx;
  font-weight: 600;
  color: #f56c6c;
  display: block;
  margin-bottom: 8rpx;
}

.car-tags {
  display: flex;
  gap: 12rpx;
}

.tag {
  font-size: 22rpx;
  color: $text-secondary;
  background: #fff;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.car-trend {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 24rpx;
  font-weight: 600;

  &.up {
    color: #5ac725;
  }

  &.down {
    color: #f56c6c;
  }
}

/* 行情分析 */
.analysis-cards {
  display: flex;
  gap: 20rpx;
}

.analysis-card {
  flex: 1;
  background: $bg-color;
  border-radius: 12rpx;
  padding: 20rpx;
  text-align: center;
}

.card-label {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
  margin-bottom: 12rpx;
}

.card-value {
  font-size: 32rpx;
  font-weight: 700;
  color: $text-color;
  display: block;
  margin-bottom: 8rpx;
}

.card-trend {
  font-size: 22rpx;
  font-weight: 600;

  &.up {
    color: #5ac725;
  }

  &.down {
    color: #f56c6c;
  }
}

/* 竞品分析 */
.competitor-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.competitor-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid $border-color;
}

.competitor-info {
  flex: 1;
}

.competitor-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
  margin-bottom: 8rpx;
}

.competitor-count {
  font-size: 22rpx;
  color: $text-secondary;
}

.competitor-price {
  text-align: right;
}

.price-range {
  font-size: 26rpx;
  color: $text-color;
  display: block;
  margin-bottom: 8rpx;
}

.price-avg {
  font-size: 22rpx;
  color: $text-secondary;
}

/* 市场趋势 */
.trend-chart {
  background: $bg-color;
  border-radius: 12rpx;
  padding: 20rpx;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.chart-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
}

.chart-tabs {
  display: flex;
  gap: 16rpx;
}

.tab {
  font-size: 24rpx;
  color: $text-secondary;
  padding: 8rpx 16rpx;
  border-radius: 6rpx;
  cursor: pointer;

  &.active {
    background: $cta-color;
    color: #fff;
  }
}

.chart-content {
  height: 200rpx;
}

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.chart-text {
  font-size: 28rpx;
  color: $text-secondary;
  margin-bottom: 12rpx;
}

.chart-desc {
  font-size: 22rpx;
  color: $text-secondary;
}

/* AI建议 */
.suggestions {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  padding: 16rpx;
  background: #fff9e6;
  border-radius: 8rpx;
}

.suggestion-text {
  font-size: 26rpx;
  color: $text-color;
  line-height: 1.5;
  flex: 1;
}
</style>
