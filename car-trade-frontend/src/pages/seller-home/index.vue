<template>
  <view>
    <u-navbar :title="seller.name || '卖家主页'" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="toggleFollow">
        <u-button :type="isFollowed ? 'error' : 'primary'" size="mini" :plain="isFollowed">{{ isFollowed ? '已关注' : '+ 关注' }}</u-button>
      </view>
    </u-navbar>
    <view class="page-content">
      <view class="seller-header">
        <image :src="seller.avatar || '/static/default-avatar.png'" mode="aspectFill" class="seller-avatar"></image>
        <view class="seller-meta">
          <text class="seller-name">{{ seller.name }}</text>
          <text class="seller-company">{{ seller.company }}</text>
          <view class="seller-tags">
            <u-tag v-if="seller.certificationStatus === 'CERTIFIED'" text="认证车商" type="success" size="mini" mode="light"></u-tag>
            <u-tag :text="'信用' + (seller.creditGrade || 'C') + '级'" type="primary" size="mini" mode="light"></u-tag>
          </view>
        </view>
      </view>

      <!-- 统计数据 -->
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">{{ seller.carCount || 0 }}</text>
          <text class="stat-label">车源</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ seller.dealCount || 0 }}</text>
          <text class="stat-label">成交</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ seller.followerCount || 0 }}</text>
          <text class="stat-label">粉丝</text>
        </view>
        <view class="stat-item" @click="toMessage">
          <u-icon name="chat" size="36" color="#0369A1"></u-icon>
          <text class="stat-label">消息</text>
        </view>
        <view class="stat-item" @click="callSeller">
          <u-icon name="phone" size="36" color="#5ac725"></u-icon>
          <text class="stat-label">电话</text>
        </view>
      </view>

      <!-- 搜索和筛选 -->
      <view class="search-section">
        <view class="search-bar">
          <u-icon name="search" size="28" color="#999"></u-icon>
          <input
            v-model="searchKeyword"
            placeholder="搜索他的车源..."
            class="search-input"
            @confirm="doSearch"
          />
          <view class="search-btn" @click="doSearch">
            <text>搜索</text>
          </view>
        </view>

        <!-- 筛选标签 -->
        <view class="filter-tags">
          <view
            class="filter-tag"
            :class="{ active: activeFilter === 'all' }"
            @click="setFilter('all')"
          >
            <text>全部</text>
          </view>
          <view
            class="filter-tag"
            :class="{ active: activeFilter === 'fixed' }"
            @click="setFilter('fixed')"
          >
            <text>一口价</text>
          </view>
          <view
            class="filter-tag"
            :class="{ active: activeFilter === 'auction' }"
            @click="setFilter('auction')"
          >
            <text>拍卖</text>
          </view>
        </view>
      </view>

      <!-- 车源列表 -->
      <view class="section">
        <view class="section-title">{{ seller.name }}的车源</view>
        <view class="car-list" v-if="carList.length > 0">
          <view class="car-card" v-for="item in carList" :key="item.id" @click="toDetail(item.id)">
            <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="car-image"></image>
            <view class="car-info">
              <view class="car-title u-line-1">{{ item.title }}</view>
              <view class="car-tags">
                <u-tag :text="item.year + '年'" size="mini" type="info" mode="light" :plain="true"></u-tag>
                <u-tag :text="formatMileage(item.mileage) + '公里'" size="mini" type="info" mode="light" :plain="true"></u-tag>
                <u-tag v-if="item.pricingType === 'AUCTION'" text="拍卖" size="mini" type="warning" mode="light"></u-tag>
              </view>
              <view class="car-price-row">
                <text class="car-price">{{ formatPrice(item.price) }}</text>
                <view class="car-location">
                  <u-icon name="map-pin" size="20" color="#999"></u-icon>
                  <text>{{ item.cityName || '全国' }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
        <u-empty v-else mode="car" text="暂无在售车源"></u-empty>
      </view>

      <!-- 悬浮按钮组 -->
      <view class="fab-group">
        <view class="fab-item" @click="toPublish">
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
            <u-icon name="headphones" size="26" color="#1f2937"></u-icon>
          </view>
          <text class="fab-label">在线客服</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getCarList } from '@/api/car'
import { followUser, unfollowUser, checkFollowed } from '@/api/follow'
import { formatPrice, formatMileage } from '@/utils/format'

export default {
  data() {
    return {
      seller: { name: '卖家' },
      carList: [],
      isFollowed: false,
      id: '',
      searchKeyword: '',
      activeFilter: 'all'
    }
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id
      this.loadSeller()
    }
  },
  methods: {
    formatPrice,
    formatMileage,
    async loadSeller() {
      try {
        const res = await getCarList({ sellerId: this.id, page: 1, pageSize: 20 })
        this.seller = res.data.seller || {}
        this.carList = res.data.records || res.data.list || res.data || []
        const followRes = await checkFollowed(this.id)
        this.isFollowed = followRes.data.followed || false
      } catch (e) {}
    },
    async toggleFollow() {
      try {
        if (this.isFollowed) {
          await unfollowUser(this.id)
        } else {
          await followUser(this.id)
        }
        this.isFollowed = !this.isFollowed
        uni.$u.toast(this.isFollowed ? '已关注' : '取消关注')
      } catch (e) {}
    },
    doSearch() {
      this.loadSeller()
    },
    setFilter(filter) {
      this.activeFilter = filter
      this.loadSeller()
    },
    callSeller() {
      if (!this.seller.phone) {
        uni.$u.toast('暂无卖家电话')
        return
      }
      uni.makePhoneCall({
        phoneNumber: this.seller.phone,
        fail: (err) => {
          console.error('拨打电话失败', err)
          uni.$u.toast('拨打电话失败')
        }
      })
    },
    toMessage() {
      uni.navigateTo({
        url: `/pages/message/index?sellerId=${this.id}`
      })
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/car-detail/index?id=' + id })
    },
    toPublish() {
      uni.navigateTo({ url: '/pages/publish/index' })
    },
    toAI() {
      uni.navigateTo({ url: '/pages/ai/index' })
    },
    toCustomerService() {
      uni.navigateTo({ url: '/pages/customer-service/index' })
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

.navbar-right {
  padding-right: 20rpx;
}

.page-content {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 160rpx;
}

.seller-header {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  background: #fff;
  margin-bottom: 16rpx;
}

.seller-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  margin-right: 24rpx;
}

.seller-meta {
  flex: 1;
}

.seller-name {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-color;
  display: block;
}

.seller-company {
  font-size: 24rpx;
  color: $text-secondary;
  display: block;
  margin-top: 6rpx;
}

.seller-tags {
  display: flex;
  gap: 12rpx;
  margin-top: 12rpx;
}

/* 统计数据 */
.stats-row {
  display: flex;
  background: #fff;
  padding: 30rpx 0;
  margin-bottom: 16rpx;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: $transition;

  &:active {
    transform: scale(0.95);
  }
}

.stat-num {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-color;
  display: block;
}

.stat-label {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
  margin-top: 6rpx;
}

/* 搜索和筛选 */
.search-section {
  background: #fff;
  padding: 20rpx;
  margin-bottom: 16rpx;
}

.search-bar {
  display: flex;
  align-items: center;
  background: $bg-color;
  border-radius: 40rpx;
  padding: 16rpx 24rpx;
  margin-bottom: 16rpx;
}

.search-input {
  flex: 1;
  margin-left: 16rpx;
  font-size: 28rpx;
  color: $text-color;
}

.search-btn {
  background: $cta-color;
  color: #fff;
  padding: 12rpx 24rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  cursor: pointer;

  &:active {
    opacity: 0.8;
  }
}

.filter-tags {
  display: flex;
  gap: 16rpx;
}

.filter-tag {
  padding: 12rpx 24rpx;
  background: $bg-color;
  border-radius: 30rpx;
  font-size: 24rpx;
  color: $text-secondary;
  cursor: pointer;
  transition: $transition;

  &.active {
    background: $cta-color;
    color: #fff;
  }

  &:active {
    transform: scale(0.95);
  }
}

/* 车源列表 */
.section {
  background: #fff;
  padding: 30rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 20rpx;
}

.car-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.car-card {
  display: flex;
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
  width: 200rpx;
  height: 150rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
}

.car-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.car-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
}

.car-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 12rpx;
}

.car-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12rpx;
}

.car-price {
  font-size: 32rpx;
  font-weight: 700;
  color: #f56c6c;
}

.car-location {
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: 22rpx;
  color: $text-secondary;
}

/* 悬浮按钮组 */
.fab-group {
  position: fixed;
  right: 30rpx;
  bottom: 200rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  z-index: 10;
}

.fab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: $transition;

  &:active {
    transform: scale(0.9);
  }
}

.fab-circle {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);

  &.ai {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  &.cs {
    background: linear-gradient(135deg, #f9ae3d 0%, #f7b731 100%);
  }
}

.fab-label {
  font-size: 20rpx;
  color: $text-secondary;
  margin-top: 8rpx;
}
</style>
