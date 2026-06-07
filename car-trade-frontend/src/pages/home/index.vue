<template>
  <view class="page">
    <!-- 顶部滚动广告位（带悬浮 Logo） -->
    <view class="ad-section">
      <swiper class="ad-swiper" indicator-dots indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#ffffff" circular autoplay :interval="3500" duration="600">
        <swiper-item v-for="(ad, idx) in adList" :key="idx">
          <view class="ad-slide" :style="{ background: ad.bg }">
            <view class="ad-text">
              <text class="ad-title">{{ ad.title }}</text>
              <text class="ad-sub">{{ ad.subtitle }}</text>
            </view>
            <text class="ad-tag" v-if="ad.tag">{{ ad.tag }}</text>
          </view>
        </swiper-item>
      </swiper>

      <!-- 状态栏占位 -->
      <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>

      <!-- 悬浮顶部：Logo + 语言切换 -->
      <view class="ad-header">
        <view class="ad-logo-area">
          <text class="ad-logo">5D好车</text>
          <text class="ad-subtitle">AI赋能二手车拓展商机</text>
        </view>
        <view class="ad-lang">
          <text class="lang-text">中文</text>
          <u-icon name="arrow-down" size="20" color="#ffffff"></u-icon>
        </view>
      </view>

      <!-- 渐变遮罩（平滑过渡到下方白色） -->
      <view class="ad-bottom-mask"></view>
    </view>

    <!-- 搜索栏（白底 + 搜索按钮 + 筛选） -->
    <view class="search-wrap">
      <view class="search-field">
        <u-icon name="search" size="28" color="#9ca3af"></u-icon>
        <input class="search-input" placeholder="请输入搜索内容" placeholder-style="color:#9ca3af" v-model="searchKeyword" confirm-type="search" @confirm="doSearch" />
      </view>
      <view class="search-btn" @click="doSearch">
        <text>搜索</text>
      </view>
      <view class="filter-icon" @click="openFilter">
        <u-icon name="filter" size="24" color="#1f2937"></u-icon>
      </view>
    </view>

    <!-- 一级筛选 Tab（原型：胶囊按钮风格） -->
    <view class="tabs-wrap">
      <view class="tabs-inner">
        <view
          class="tab-pill"
          v-for="(tab, index) in filterTabs"
          :key="tab.key"
          :class="{ active: currentTab === index }"
          @click="switchTab(index)"
        >
          <text class="tab-label">{{ tab.label }}</text>
        </view>
      </view>
    </view>

    <!-- 出口二级区域筛选（点击"出口"Tab 时显示）：中东/俄罗斯/非洲/远东/东南亚/南美 -->
    <view class="region-wrap" v-if="currentTab === 3">
      <view class="region-inner">
        <view
          class="region-pill"
          v-for="(r, i) in exportRegions"
          :key="r.code"
          :class="{ active: currentRegion === r.code }"
          @click="filterByRegion(r.code)"
        >
          <text class="region-label">{{ r.name }}</text>
        </view>
      </view>
    </view>

    <!-- 车源列表：单列大图卡片（左图右信息） -->
    <view class="car-list">
      <view class="car-card" v-for="item in carList" :key="item.id" @click="toDetail(item.id)">
        <!-- 左侧大图 -->
        <view class="card-image-wrap">
          <image :src="item.coverImage || defaultImage" mode="aspectFill" class="car-image"></image>
          <view class="image-tags">
            <view class="tag-energy pure" v-if="item.energyType === '纯电'">纯电</view>
            <view class="tag-energy hybrid" v-else-if="item.energyType === '混动'">混动</view>
            <view class="tag-energy fuel" v-else>燃油</view>
          </view>
        </view>

        <!-- 右侧信息 -->
        <view class="card-info">
          <view class="car-title">{{ item.brandName }} {{ item.seriesName }} {{ item.modelName || '' }}</view>
          <view class="car-sub-row">
            <text class="car-sub">{{ item.year }}年</text>
            <text class="dot">·</text>
            <text class="car-sub">{{ formatMileage(item.mileage) }}公里</text>
          </view>
          <view class="deposit-row" v-if="item.hasDeposit">
            <view class="deposit-badge">
              <u-icon name="lock" size="16" color="#ffffff"></u-icon>
              <text>保证金</text>
            </view>
          </view>
          <view class="location-row">
            <u-icon name="map-pin" size="18" color="#6b7280"></u-icon>
            <text class="location">{{ item.city || '全国' }}</text>
            <text class="dot">·</text>
            <text class="time">{{ formatTime(item.createdAt) }}</text>
          </view>

          <view class="price-row">
            <text class="price-value">¥{{ formatPrice(item.price) }}</text>
            <view class="export-flags" v-if="item.exportCountries && item.exportCountries.length > 0">
              <text v-for="(code, i) in item.exportCountries.slice(0, 3)" :key="i" class="mini-flag">{{ flagMap[code] || code }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="carList.length > 0">
      <text>{{ hasMore ? '上拉加载更多' : '已加载全部车源' }}</text>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-if="carList.length === 0 && !loading">
      <u-icon name="shopping-cart" size="80" color="#e5e7eb"></u-icon>
      <text class="empty-text">暂无车源</text>
    </view>

    <!-- 右侧悬浮按钮组（发布/AI/客服/+开始） -->
    <view class="fab-group">
      <view class="fab-item" @click="gotoPublish">
        <view class="fab-circle">
          <u-icon name="plus" size="28" color="#1f2937"></u-icon>
        </view>
        <text class="fab-label">发布车源</text>
      </view>
      <view class="fab-item" @click="toAI">
        <view class="fab-circle ai">
          <u-icon name="chat" size="26" color="#1f2937"></u-icon>
        </view>
        <text class="fab-label">AI助理</text>
      </view>
      <view class="fab-item" @click="toCustomerService">
        <view class="fab-circle cs">
          <u-icon name="chat" size="26" color="#1f2937"></u-icon>
        </view>
        <text class="fab-label">在线客服</text>
      </view>
      <view class="fab-item primary" @click="gotoPublish">
        <view class="fab-circle primary">
          <u-icon name="plus" size="32" color="#1f2937"></u-icon>
        </view>
        <text class="fab-label primary">+ 开始</text>
      </view>
    </view>

    <!-- 底部 TabBar 占位 -->
    <view class="tabbar-placeholder"></view>
  </view>
</template>

<script>
import { getCarList } from '@/api/car'

export default {
  data() {
    return {
      statusBarHeight: 20,
      searchKeyword: '',
      currentTab: 0,
      carList: [],
      page: 1,
      size: 10,
      total: 0,
      loading: false,
      hasMore: true,
      defaultImage: '/static/tab/home.png',
      // 顶部滚动广告位数据
      adList: [
        { title: '全球二手车出口', subtitle: '一站式出口服务 · 覆盖30+国家', tag: 'HOT', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
        { title: 'AI智能车况评估', subtitle: '精准评估 · 快速成交', tag: 'AI', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
        { title: '厂家认证车源', subtitle: '品质保证 · 放心购买', tag: '精选', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
        { title: '限时降价专场', subtitle: '今日特卖 · 最高立减3万', tag: '限时', bg: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
      ],
      filterTabs: [
        { key: 'latest', label: '最新' },
        { key: 'ne', label: '新能源' },
        { key: 'deposit', label: '保证金' },
        { key: 'export', label: '出口' }
      ],
      // 出口二级区域筛选（原型显示：中东/俄罗斯/非洲/远东/东南亚/南美）
      exportRegions: [
        { code: 'ME', name: '中东' },
        { code: 'RU', name: '俄罗斯' },
        { code: 'AF', name: '非洲' },
        { code: 'FE', name: '远东' },
        { code: 'SEA', name: '东南亚' },
        { code: 'SA', name: '南美' }
      ],
      currentRegion: 'ME',
      exportCountries: [
        { code: 'RU', name: '俄罗斯', flag: '🇷🇺', count: 128 },
        { code: 'KZ', name: '哈萨克斯坦', flag: '🇰🇿', count: 86 },
        { code: 'AE', name: '阿联酋', flag: '🇦🇪', count: 72 },
        { code: 'AU', name: '澳大利亚', flag: '🇦🇺', count: 54 },
        { code: 'SA', name: '南非', flag: '🇿🇦', count: 41 },
        { code: 'SE', name: '东南亚', flag: '🌏', count: 98 }
      ],
      flagMap: {
        RU: '🇷🇺', KZ: '🇰🇿', AE: '🇦🇪', AU: '🇦🇺', SA: '🇿🇦', SE: '🌏', CN: '🇨🇳', JP: '🇯🇵', US: '🇺🇸', EU: '🇪🇺'
      }
    }
  },
  onLoad() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight || 20
    this.fetchCarList()
  },
  onPullDownRefresh() {
    this.page = 1
    this.hasMore = true
    this.fetchCarList(true).finally(() => uni.stopPullDownRefresh())
  },
  onReachBottom() {
    if (this.hasMore) {
      this.page++
      this.fetchCarList()
    }
  },
  onShow() {
    // 进入页面时，根据当前页更新自定义 tabBar 的选中状态
    if (typeof this.$mp === 'undefined') {
      // 触发子组件内部 activeIndex 同步
    }
  },
  methods: {
    async fetchCarList(reset) {
      if (this.loading) return
      this.loading = true
      try {
        const params = { page: this.page, size: this.size }
        if (this.searchKeyword) params.keyword = this.searchKeyword
        if (this.currentTab === 1) params.energyType = '纯电'
        if (this.currentTab === 2) params.deposit = true
        if (this.currentTab === 3 && this.currentRegion) params.region = this.currentRegion
        const res = await getCarList(params)
        const data = res.data
        if (reset || this.page === 1) {
          this.carList = data.list || []
        } else {
          this.carList = this.carList.concat(data.list || [])
        }
        this.total = data.total || 0
        this.hasMore = this.carList.length < this.total
      } catch (e) {
        console.error('load cars fail', e)
      } finally {
        this.loading = false
      }
    },
    switchTab(index) {
      if (this.currentTab === index) return
      this.currentTab = index
      this.page = 1
      this.hasMore = true
      if (index === 3) {
        // 出口Tab，默认使用当前区域
        this.currentRegion = 'ME'
      }
      this.fetchCarList(true)
    },
    filterByRegion(code) {
      this.currentRegion = code
      this.page = 1
      this.hasMore = true
      this.fetchCarList(true)
    },
    doSearch() {
      this.page = 1
      this.hasMore = true
      this.fetchCarList(true)
    },
    filterByCountry(code) {
      this.page = 1
      this.hasMore = true
      this.fetchCarList(true)
    },
    openFilter() {
      uni.showToast({ title: '筛选功能开发中', icon: 'none' })
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/car-detail/index?id=' + id })
    },
    gotoPublish() {
      uni.navigateTo({ url: '/pages/publish/index' })
    },
    toAI() {
      uni.showToast({ title: 'AI助理开发中', icon: 'none' })
    },
    toCustomerService() {
      uni.showToast({ title: '在线客服开发中', icon: 'none' })
    },
    formatPrice(price) {
      if (!price) return '面议'
      const wan = price / 10000
      return wan >= 10 ? wan.toFixed(0) + '万' : wan.toFixed(1) + '万'
    },
    formatMileage(mileage) {
      if (!mileage) return '0'
      if (mileage >= 10000) return (mileage / 10000).toFixed(1) + '万'
      return mileage.toString()
    },
    formatTime(ts) {
      if (!ts) return ''
      const now = Date.now()
      const d = new Date(ts).getTime()
      const diff = (now - d) / 1000
      if (diff < 60) return '刚刚'
      if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
      if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
      if (diff < 86400 * 7) return Math.floor(diff / 86400) + '天前'
      const dt = new Date(ts)
      return (dt.getMonth() + 1) + '-' + dt.getDate()
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================
   全局设计系统
   主色: #3c9cff (蓝)
   辅色: #ffd43b (金黄 FAB)
   文本: #1f2937 (主) / #6b7280 (次) / #9ca3af (辅)
   背景: #f3f4f6 (浅灰) / #ffffff (白)
   ========================================= */

.page {
  min-height: 100vh;
  background: #f3f4f6;
}

/* ============= 顶部滚动广告位 ============= */
.ad-section {
  position: relative;
  overflow: hidden;
  background: #1a202c;
}

.ad-swiper {
  width: 100%;
  height: 340rpx;
}

.ad-slide {
  width: 100%;
  height: 340rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 32rpx;
  position: relative;
  box-sizing: border-box;
}

.ad-text {
  display: flex;
  flex-direction: column;
}

.ad-title {
  font-size: 40rpx;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 2rpx;
  line-height: 1.2;
}

.ad-sub {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 10rpx;
  letter-spacing: 1rpx;
}

.ad-tag {
  position: absolute;
  top: 30rpx;
  right: 32rpx;
  font-size: 22rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.2);
  padding: 8rpx 22rpx;
  border-radius: 24rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
}

.status-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 5;
  width: 100%;
}

.ad-header {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 4;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16rpx 32rpx 24rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 16rpx);
}

.ad-logo-area {
  display: flex;
  flex-direction: column;
}

.ad-logo {
  font-size: 38rpx;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 2rpx;
  line-height: 1.2;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.ad-subtitle {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 6rpx;
  letter-spacing: 1rpx;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.ad-lang {
  display: flex;
  align-items: center;
  gap: 4rpx;
  background: rgba(255, 255, 255, 0.2);
  padding: 10rpx 20rpx;
  border-radius: 24rpx;
}

.ad-bottom-mask {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 60rpx;
  background: linear-gradient(180deg, rgba(243, 244, 246, 0) 0%, rgba(243, 244, 246, 1) 100%);
  z-index: 2;
  pointer-events: none;
}

/* 搜索栏 */
.search-wrap {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 32rpx 20rpx;
  margin-top: -40rpx;
  position: relative;
  z-index: 10;
}

.location-pill {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: #ffffff;
  padding: 14rpx 18rpx;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
}

.location-text {
  font-size: 24rpx;
  color: #1f2937;
  font-weight: 600;
  white-space: nowrap;
}

.search-field {
  flex: 1;
  display: flex;
  align-items: center;
  background: #ffffff;
  padding: 14rpx 20rpx;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
}

.search-input {
  flex: 1;
  font-size: 26rpx;
  color: #1f2937;
  margin-left: 12rpx;
}

.search-btn {
  flex-shrink: 0;
  background: #3c9cff;
  padding: 16rpx 32rpx;
  border-radius: 12rpx;
  box-shadow: 0 4rpx 14rpx rgba(60, 156, 255, 0.35);

  text {
    color: #ffffff;
    font-size: 26rpx;
    font-weight: 700;
    letter-spacing: 2rpx;
  }

  &:active {
    transform: scale(0.96);
    opacity: 0.92;
  }
}

.filter-icon {
  flex-shrink: 0;
  width: 68rpx;
  height: 68rpx;
  background: #ffffff;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);

  &:active {
    transform: scale(0.94);
  }
}

/* ============= 筛选 Tab（原型：胶囊按钮风格） ============= */
.tabs-wrap {
  background: #ffffff;
  padding: 20rpx 32rpx;
  margin-top: 16rpx;
}

.tabs-inner {
  display: flex;
  gap: 14rpx;
  align-items: center;
}

.tab-pill {
  flex-shrink: 0;
  padding: 14rpx 28rpx;
  background: #f3f4f6;
  border-radius: 32rpx;
  transition: all 0.2s ease;

  &.active {
    background: #1f2937;

    .tab-label {
      color: #ffffff;
      font-weight: 700;
    }
  }

  &:active {
    transform: scale(0.96);
  }
}

.tab-label {
  font-size: 26rpx;
  color: #6b7280;
  font-weight: 500;
}

/* ============= 出口二级区域筛选 ============= */
.region-wrap {
  background: #ffffff;
  padding: 10rpx 32rpx 24rpx;
  border-bottom: 1rpx solid #e5e7eb;
}

.region-inner {
  display: flex;
  gap: 12rpx;
  align-items: center;
  flex-wrap: wrap;
}

.region-pill {
  flex-shrink: 0;
  padding: 10rpx 22rpx;
  background: #f8fafc;
  border: 1rpx solid #e5e7eb;
  border-radius: 28rpx;
  transition: all 0.2s ease;

  &.active {
    background: #eff6ff;
    border-color: #3c9cff;

    .region-label {
      color: #3c9cff;
      font-weight: 600;
    }
  }

  &:active {
    transform: scale(0.96);
  }
}

.region-label {
  font-size: 24rpx;
  color: #6b7280;
  font-weight: 500;
}

/* ============= 出口国家区 ============= */
.export-section {
  background: #ffffff;
  margin: 16rpx 0;
  padding: 24rpx 32rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #1f2937;
}

.section-more {
  font-size: 22rpx;
  color: #6b7280;
}

.export-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.export-item {
  width: calc(33.33% - 12rpx);
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: #f8fafc;
  border-radius: 14rpx;
  padding: 16rpx 12rpx;
  box-sizing: border-box;
  border: 1rpx solid #e5e7eb;
  transition: all 0.2s;

  &:active {
    background: #eff6ff;
    transform: scale(0.97);
  }
}

.export-flag {
  font-size: 36rpx;
  flex-shrink: 0;
}

.export-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.export-name {
  font-size: 24rpx;
  color: #1f2937;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.export-count {
  font-size: 20rpx;
  color: #6b7280;
  margin-top: 2rpx;
}

/* ============= 车源列表（单列大图卡片） ============= */
.car-list {
  padding: 16rpx 32rpx 0;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.car-card {
  display: flex;
  flex-direction: row;
  background: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.06);
  padding: 20rpx;
  gap: 20rpx;
  transition: transform 0.15s ease;

  &:active {
    transform: scale(0.99);
    background: #fafafa;
  }
}

.card-image-wrap {
  position: relative;
  width: 240rpx;
  height: 240rpx;
  flex-shrink: 0;
  border-radius: 14rpx;
  overflow: hidden;
  background: #e5e7eb;
}

.car-image {
  width: 100%;
  height: 100%;
}

.image-tags {
  position: absolute;
  top: 12rpx;
  left: 12rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.tag-energy {
  font-size: 18rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  font-weight: 600;
  color: #ffffff;

  &.pure {
    background: #10b981;
  }
  &.hybrid {
    background: #f59e0b;
  }
  &.fuel {
    background: #6b7280;
  }
}

/* 右侧信息 */
.card-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.car-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.car-sub-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-top: 10rpx;
}

.car-sub {
  font-size: 22rpx;
  color: #6b7280;
}

.dot {
  color: #d1d5db;
  font-size: 18rpx;
}

.deposit-row {
  margin-top: 12rpx;
}

.deposit-badge {
  display: inline-flex;
  align-items: center;
  gap: 6rpx;
  background: #dbeafe;
  color: #1e40af;
  font-size: 20rpx;
  font-weight: 600;
  padding: 6rpx 14rpx;
  border-radius: 8rpx;
  letter-spacing: 1rpx;
}

.location-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-top: 12rpx;
  flex-wrap: wrap;
}

.location,
.time {
  font-size: 22rpx;
  color: #6b7280;
}

/* 价格行 */
.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14rpx;
  padding-top: 14rpx;
  border-top: 1rpx dashed #e5e7eb;
}

.price-value {
  font-size: 32rpx;
  font-weight: 800;
  color: #ef4444;
  letter-spacing: 1rpx;
  line-height: 1.2;
}

.export-flags {
  display: flex;
  gap: 8rpx;
}

.mini-flag {
  font-size: 30rpx;
  line-height: 1;
}

/* ============= 加载更多 & 空状态 ============= */
.load-more {
  text-align: center;
  padding: 30rpx;
  font-size: 22rpx;
  color: #9ca3af;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-text {
  margin-top: 20rpx;
  font-size: 26rpx;
  color: #9ca3af;
}

/* ============= 右侧悬浮按钮组（发布/AI/客服/+开始） ============= */
.fab-group {
  position: fixed;
  right: 32rpx;
  bottom: 220rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
  z-index: 100;
}

.fab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;

  &:active .fab-circle {
    transform: scale(0.92);
  }

  &.primary .fab-circle {
    width: 110rpx;
    height: 110rpx;
    background: linear-gradient(180deg, #ffd43b 0%, #fab005 100%);
    box-shadow: 0 8rpx 28rpx rgba(250, 176, 5, 0.5);
  }

  &.primary .fab-label {
    color: #1f2937;
    font-weight: 700;
    font-size: 22rpx;
  }
}

.fab-circle {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 18rpx rgba(0, 0, 0, 0.08);
  transition: transform 0.15s ease;

  &.ai {
    background: #fef3c7;
  }

  &.cs {
    background: #dbeafe;
  }
}

.fab-label {
  font-size: 20rpx;
  color: #6b7280;
  font-weight: 500;
}

/* ============= TabBar 占位 ============= */
.tabbar-placeholder {
  height: 200rpx;
}

/* ============= 响应式适配 ============= */
/* 大屏：375px 以上 */
@media (min-width: 376px) {
  .car-title {
    font-size: 32rpx;
  }
  .price-value {
    font-size: 40rpx;
  }
  .card-image-wrap {
    width: 260rpx;
    height: 260rpx;
  }
}

/* 小屏优化 */
@media (max-width: 320px) {
  .card-image-wrap {
    width: 200rpx;
    height: 200rpx;
  }
  .car-title {
    font-size: 28rpx;
  }
  .price-value {
    font-size: 32rpx;
  }
  .hero-logo {
    font-size: 40rpx;
  }
}
</style>
