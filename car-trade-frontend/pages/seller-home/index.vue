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
            <u-tag v-if="seller.certificationStatus === 'CERTIFIED'" text="已认证" type="success" size="mini" mode="light"></u-tag>
            <u-tag :text="seller.level || '普通会员'" type="primary" size="mini" mode="light"></u-tag>
          </view>
        </view>
      </view>
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">{{ seller.carCount || 0 }}</text>
          <text class="stat-label">在售车源</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ seller.soldCount || 0 }}</text>
          <text class="stat-label">已售</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ seller.followerCount || 0 }}</text>
          <text class="stat-label">粉丝</text>
        </view>
      </view>
      <view class="section">
        <view class="section-title">{{ seller.name }}的车源</view>
        <view class="car-list">
          <view class="car-card" v-for="item in carList" :key="item.id" @click="toDetail(item.id)">
            <image :src="item.coverImage || '/static/default-car.png'" mode="aspectFill" class="car-image"></image>
            <view class="car-info">
              <view class="car-title u-line-1">{{ item.title }}</view>
              <view class="car-tags">
                <u-tag :text="item.year + '年'" size="mini" type="info" mode="light" :plain="true"></u-tag>
                <u-tag :text="item.mileage + '万公里'" size="mini" type="info" mode="light" :plain="true"></u-tag>
              </view>
              <view class="car-price">{{ formatPrice(item.price) }}</view>
            </view>
          </view>
        </view>
        <u-empty v-if="carList.length === 0" mode="car" text="暂无车源"></u-empty>
      </view>
      <view class="contact-bar">
        <u-button type="primary" shape="circle" @click="contact">联系卖家</u-button>
      </view>
    </view>
  </view>
</template>

<script>
import { getCarList } from '@/api/car'
import { followUser, unfollowUser, checkFollowed } from '@/api/follow'
import { formatPrice } from '@/utils/format'

export default {
  data() {
    return {
      seller: { name: '卖家' },
      carList: [],
      isFollowed: false,
      id: ''
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
    toDetail(id) {
      uni.navigateTo({ url: '/pages/car-detail/index?id=' + id })
    },
    contact() {
      uni.navigateTo({ url: '/pages/customer-service/index?sellerId=' + this.id })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar-right { padding-right: 20rpx; }
.page-content { min-height: 100vh; background: #f5f5f5; padding-bottom: 120rpx; }
.seller-header {
  display: flex; align-items: center; padding: 40rpx 30rpx;
  background: #fff; margin-bottom: 16rpx;
}
.seller-avatar { width: 120rpx; height: 120rpx; border-radius: 50%; margin-right: 24rpx; }
.seller-meta { flex: 1; }
.seller-name { font-size: 36rpx; font-weight: 700; color: #333; display: block; }
.seller-company { font-size: 24rpx; color: #999; display: block; margin-top: 6rpx; }
.seller-tags { display: flex; gap: 12rpx; margin-top: 12rpx; }
.stats-row { display: flex; background: #fff; padding: 30rpx 0; margin-bottom: 16rpx; }
.stat-item { flex: 1; text-align: center; }
.stat-num { font-size: 36rpx; font-weight: 700; color: #333; display: block; }
.stat-label { font-size: 22rpx; color: #999; display: block; margin-top: 6rpx; }
.section { background: #fff; padding: 30rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }
.car-list { }
.car-card { display: flex; padding: 20rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.car-image { width: 200rpx; height: 150rpx; border-radius: 12rpx; margin-right: 20rpx; }
.car-info { flex: 1; }
.car-title { font-size: 28rpx; font-weight: 600; color: #333; }
.car-tags { display: flex; gap: 12rpx; margin-top: 12rpx; }
.car-price { font-size: 32rpx; font-weight: 700; color: #f56c6c; margin-top: 12rpx; }
.contact-bar {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  padding: 20rpx 30rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #eee;
}
</style>
