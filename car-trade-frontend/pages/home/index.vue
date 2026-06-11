<template>
  <view class="page-container">
    <view class="search-bar">
      <view class="search-input-wrap">
        <u-icon name="search" size="28" color="#999" class="search-icon"></u-icon>
        <input type="text" placeholder="请输入搜索内容" v-model="searchText" class="search-input" @confirm="handleSearch"/>
      </view>
      <view class="search-btn" @click="handleSearch">
        <text class="search-btn-text">搜索</text>
      </view>
    </view>

    <view class="filter-tabs">
      <scroll-view scroll-x class="filter-scroll">
        <view class="filter-list">
          <view class="filter-item" :class="{ active: currentFilter === index }" v-for="(item, index) in filterList" :key="index" @click="switchFilter(index)">
            <text class="filter-text">{{ item }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="action-buttons">
      <view class="action-btn" @click="toPublish">
        <view class="action-icon-wrap publish-icon">
          <text class="action-icon">📤</text>
        </view>
        <text class="action-text">发布车源</text>
      </view>
      <view class="action-btn" @click="toPurchase">
        <view class="action-icon-wrap purchase-icon">
          <text class="action-icon">📝</text>
        </view>
        <text class="action-text">发布求购</text>
      </view>
      <view class="action-btn" @click="toService">
        <view class="action-icon-wrap service-icon">
          <text class="action-icon">💬</text>
        </view>
        <text class="action-text">客服帮助</text>
      </view>
      <view class="action-btn" @click="toAI">
        <view class="action-icon-wrap ai-icon">
          <text class="action-icon">✨</text>
        </view>
        <text class="action-text">AI助理</text>
      </view>
    </view>

    <view class="banner-section">
      <swiper class="banner-swiper" indicator-dots indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#fff" autoplay circular>
        <swiper-item v-for="(banner, index) in banners" :key="index">
          <image :src="banner" mode="aspectFill" class="banner-image"></image>
        </swiper-item>
      </swiper>
    </view>

    <view class="car-section">
      <view class="section-header">
        <text class="section-title">推荐车源</text>
        <view class="section-more" @click="toMore">
          <text class="more-text">更多</text>
          <u-icon name="arrow-right" size="24" color="#999"></u-icon>
        </view>
      </view>
      <view v-if="loading" class="loading-wrap">
        <u-loading mode="flower" size="50"></u-loading>
      </view>
      <block v-else>
        <view class="car-list">
          <view class="car-card" v-for="item in carList" :key="item.id" @click="toDetail(item.id)">
            <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="car-image"></image>
            <view class="car-info">
              <view class="car-title u-line-1">{{ item.title }}</view>
              <view class="car-tags">
                <u-tag :text="item.year + '年'" size="mini" type="info" mode="light" :plain="true"></u-tag>
                <u-tag :text="item.mileage + '万公里'" size="mini" type="info" mode="light" :plain="true"></u-tag>
                <u-tag :text="item.color" size="mini" type="info" mode="light" :plain="true" v-if="item.color"></u-tag>
              </view>
              <view class="car-exporter" v-if="item.exporter">
                <image :src="getCountryFlag(item.exporter)" mode="aspectFit" class="flag-icon"></image>
                <text class="exporter-text">{{ item.exporter }}</text>
              </view>
              <view class="car-bottom">
                <text class="car-price">{{ formatPrice(item.price) }}</text>
                <text class="car-location"><u-icon name="map-pin" size="20" color="#999"></u-icon>{{ item.city }}</text>
              </view>
            </view>
          </view>
        </view>
        <u-empty v-if="carList.length === 0 && !loading" mode="car" text="暂无车源"></u-empty>
        <u-loadmore v-if="carList.length > 0" :status="loadStatus" @loadmore="loadMore"></u-loadmore>
      </block>
    </view>
  </view>
</template>

<script>
import { getCarList } from '@/api/car'
import { formatPrice } from '@/utils/format'

export default {
  data() {
    return {
      searchText: '',
      filterList: ['最新', '新能源', '保证金', '出口'],
      currentFilter: 0,
      banners: [
        '/static/default-car.png',
        '/static/default-car.png',
        '/static/default-car.png'
      ],
      carList: [],
      page: 1,
      pageSize: 10,
      loading: false,
      loadStatus: 'loadmore',
      hasMore: true,
      countryFlags: {
        '俄罗斯': '🇷🇺',
        '哈萨克斯坦': '🇰🇿',
        '南非': '🇿🇦',
        '阿联酋': '🇦🇪',
        '东南亚': '🌏',
        '澳大利亚': '🇦🇺',
        '其他': '🌍'
      }
    }
  },
  onLoad() {
    this.loadCars()
  },
  onPullDownRefresh() {
    this.page = 1
    this.hasMore = true
    this.carList = []
    this.loadCars().then(() => { uni.stopPullDownRefresh() })
  },
  methods: {
    formatPrice,
    getCountryFlag(country) {
      return this.countryFlags[country] || '🌍'
    },
    async loadCars() {
      this.loading = true
      this.loadStatus = 'loading'
      try {
        const params = { page: this.page, pageSize: this.pageSize }
        if (this.currentFilter > 0) params.filter = this.filterList[this.currentFilter]
        const res = await getCarList(params)
        const list = res.data.records || res.data.list || res.data || []
        const mockData = [
          { id: 1, title: '大众 Polo 2023款 1.5L 自动全景乐享版', year: '2023', mileage: '2.3', color: '白色', price: 85000, city: '上海', coverImage: '/static/default-car.png', exporter: '俄罗斯' },
          { id: 2, title: '斯柯达 晶锐 2022款 1.5L 自动车顶', year: '2022', mileage: '3.1', color: '银色', price: 72000, city: '北京', coverImage: '/static/default-car.png', exporter: '哈萨克斯坦' },
          { id: 3, title: '奔驰 CLE (进口) 2024款 CLE 260 2.0T', year: '2024', mileage: '0.5', color: '黑色', price: 680000, city: '深圳', coverImage: '/static/default-car.png', exporter: '南非' },
          { id: 4, title: '宝马 X5 2023款 xDrive30Li 2.0T M运动套装', year: '2023', mileage: '1.2', color: '深蓝色', price: 780000, city: '广州', coverImage: '/static/default-car.png', exporter: '阿联酋' },
          { id: 5, title: '奔驰 EQE 2022款 350 先锋版', year: '2022', mileage: '2.0', color: '白色', price: 450000, city: '杭州', coverImage: '/static/default-car.png', exporter: '东南亚' },
          { id: 6, title: '奥迪 A6L 2024款 45 TFSI 臻选动感型', year: '2024', mileage: '0.8', color: '黑色', price: 520000, city: '成都', coverImage: '/static/default-car.png', exporter: '澳大利亚' },
          { id: 7, title: '小米 SU7 2024款 后驱长续航智驾版', year: '2024', mileage: '0.3', color: '绿色', price: 280000, city: '上海', coverImage: '/static/default-car.png', exporter: '阿联酋' },
          { id: 8, title: '蔚来 ET5T 2023款 75kWh', year: '2023', mileage: '1.5', color: '蓝色', price: 320000, city: '北京', coverImage: '/static/default-car.png', exporter: '东南亚' },
          { id: 9, title: '比亚迪 汉EV 2024款 荣耀版 715km旗舰型', year: '2024', mileage: '0.6', color: '红色', price: 260000, city: '深圳', coverImage: '/static/default-car.png', exporter: '南非' },
          { id: 10, title: '沃尔沃 S90 2024款 T8 E驱混动 智雅豪华版', year: '2024', mileage: '0.4', color: '黑色', price: 620000, city: '广州', coverImage: '/static/default-car.png', exporter: '澳大利亚' }
        ]
        this.carList = this.page === 1 ? mockData : this.carList.concat(mockData)
        this.hasMore = false
        this.loadStatus = 'nomore'
      } catch (e) {
        this.loadStatus = 'loadmore'
      }
      this.loading = false
    },
    switchFilter(index) {
      this.currentFilter = index
      this.page = 1
      this.hasMore = true
      this.carList = []
      this.loadCars()
    },
    loadMore() {
      if (!this.hasMore) return
      this.page++
      this.loadCars()
    },
    handleSearch() {
      if (this.searchText.trim()) {
        uni.navigateTo({ url: '/pages/ai/index?mode=search&keyword=' + encodeURIComponent(this.searchText) })
      }
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/car-detail/index?id=' + id })
    },
    toPublish() {
      uni.navigateTo({ url: '/pages/publish/index' })
    },
    toPurchase() {
      uni.navigateTo({ url: '/pages/purchase-demand/index' })
    },
    toService() {
      uni.navigateTo({ url: '/pages/customer-service/index' })
    },
    toAI() {
      uni.switchTab({ url: '/pages/ai/index' })
    },
    toMore() {
      uni.navigateTo({ url: '/pages/car-list/index' })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 140rpx;
}

.search-bar {
  display: flex;
  padding: 20rpx 30rpx;
  background: #fff;
  gap: 16rpx;
}

.search-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 40rpx;
  padding: 0 24rpx;
  height: 72rpx;
}

.search-icon {
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  background: transparent;
}

.search-btn {
  background: #0369A1;
  color: #fff;
  border-radius: 40rpx;
  padding: 0 32rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-btn-text {
  font-size: 28rpx;
  font-weight: 600;
}

.filter-tabs {
  background: #fff;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.filter-scroll {
  white-space: nowrap;
}

.filter-list {
  display: inline-flex;
  padding: 0 30rpx;
  gap: 24rpx;
}

.filter-item {
  padding: 12rpx 28rpx;
  background: #f8f9fa;
  border-radius: 30rpx;
  transition: all 0.2s;

  &.active {
    background: #0369A1;

    .filter-text {
      color: #fff;
    }
  }
}

.filter-text {
  font-size: 26rpx;
  color: #666;
}

.action-buttons {
  display: flex;
  background: #fff;
  padding: 24rpx 30rpx;
  gap: 20rpx;
  border-bottom: 12rpx solid #f5f5f5;
}

.action-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.action-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  &.publish-icon {
    background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
  }

  &.purchase-icon {
    background: linear-gradient(135deg, #4ECDC4, #6DD5ED);
  }

  &.service-icon {
    background: linear-gradient(135deg, #95E1D3, #A8E6CF);
  }

  &.ai-icon {
    background: linear-gradient(135deg, #F59E0B, #FBBF24);
  }
}

.action-icon {
  font-size: 36rpx;
}

.action-text {
  font-size: 22rpx;
  color: #666;
}

.banner-section {
  padding: 20rpx 30rpx;
}

.banner-swiper {
  height: 200rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.car-section {
  padding: 0 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.more-text {
  font-size: 26rpx;
  color: #999;
}

.car-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.car-card {
  width: calc(50% - 10rpx);
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.car-image {
  width: 100%;
  height: 280rpx;
}

.car-info {
  padding: 16rpx;
}

.car-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
}

.car-tags {
  margin-top: 12rpx;
  display: flex;
  gap: 8rpx;
  flex-wrap: wrap;
}

.car-exporter {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-top: 12rpx;
}

.flag-icon {
  width: 32rpx;
  height: 32rpx;
}

.exporter-text {
  font-size: 22rpx;
  color: #666;
}

.car-bottom {
  margin-top: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.car-price {
  font-size: 30rpx;
  font-weight: 700;
  color: #f56c6c;
}

.car-location {
  font-size: 22rpx;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 60rpx 0;
}
</style>