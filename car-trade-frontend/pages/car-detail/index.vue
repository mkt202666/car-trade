<template>
  <view>
    <u-navbar :border-bottom="false" :placeholder="true" :title="carInfo.title || '车源详情'">
      <view slot="right" class="navbar-right" @click="shareCar">
        <u-icon name="share" size="36"></u-icon>
      </view>
    </u-navbar>
    <view class="page-content">
      <u-swiper :list="imageList" :height="500" :border-radius="0" indicator indicator-mode="dot" @click="previewImage"></u-swiper>
      <view class="detail-section">
        <view class="price-row">
          <text class="price">{{ formatPrice(carInfo.price) }}</text>
          <text class="original-price" v-if="carInfo.originalPrice">{{ formatPrice(carInfo.originalPrice) }}</text>
          <u-tag :text="carInfo.status === 'ACTIVE' ? '在售' : carInfo.status" size="mini" :type="carInfo.status === 'ACTIVE' ? 'success' : 'warning'" mode="light"></u-tag>
        </view>
        <view class="title">{{ carInfo.title }}</view>
        <view class="params-row">
          <view class="param-item">
            <text class="param-value">{{ carInfo.year }}年</text>
            <text class="param-label">上牌时间</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.mileage }}万公里</text>
            <text class="param-label">行驶里程</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.emission }}L</text>
            <text class="param-label">排量</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.gearbox }}</text>
            <text class="param-label">变速箱</text>
          </view>
        </view>
      </view>
      <view class="detail-section">
        <view class="section-title">车辆配置</view>
        <view class="tag-grid">
          <u-tag v-for="conf in configTags" :key="conf" :text="conf" type="primary" mode="light" :plain="true" size="mini"></u-tag>
        </view>
      </view>
      <view class="detail-section">
        <view class="section-title">车辆描述</view>
        <text class="desc-text">{{ carInfo.description || '暂无描述' }}</text>
      </view>
      <view class="detail-section seller-section" @click="toSellerHome">
        <image :src="seller.avatar || '/static/default-avatar.png'" mode="aspectFill" class="seller-avatar"></image>
        <view class="seller-info">
          <text class="seller-name">{{ seller.name }}</text>
          <text class="seller-company" v-if="seller.company">{{ seller.company }}</text>
        </view>
        <u-icon name="arrow-right" size="28" color="#999"></u-icon>
      </view>
      <view class="bottom-bar">
        <view class="action-btn" @click="toggleFavorite">
          <u-icon :name="isFav ? 'heart-fill' : 'heart'" :color="isFav ? '#f56c6c' : '#666'" size="40"></u-icon>
          <text>{{ isFav ? '已收藏' : '收藏' }}</text>
        </view>
        <view class="action-btn" @click="contact">
          <u-icon name="chat" size="40" color="#666"></u-icon>
          <text>咨询</text>
        </view>
        <view class="buy-btn" @click="buyNow">
          <text>立即购买</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getCarDetail, favoriteCar, unfavoriteCar } from '@/api/car'
import { formatPrice, formatMileage } from '@/utils/format'
import { requireAuth } from '@/utils/auth'

export default {
  data() {
    return {
      carInfo: {},
      seller: {},
      imageList: [],
      configTags: [],
      isFav: false,
      id: ''
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
    formatMileage,
    async loadDetail() {
      try {
        const res = await getCarDetail(this.id)
        const data = res.data
        this.carInfo = data
        this.seller = data.seller || {}
        this.imageList = data.images ? data.images.map(img => ({ image: img.url })) : []
        this.configTags = data.configs || []
        this.isFav = data.favorited || false
      } catch (e) {}
    },
    async toggleFavorite() {
      if (!requireAuth()) return
      try {
        if (this.isFav) {
          await unfavoriteCar(this.id)
        } else {
          await favoriteCar(this.id)
        }
        this.isFav = !this.isFav
        uni.$u.toast(this.isFav ? '已收藏' : '取消收藏')
      } catch (e) {}
    },
    previewImage() {
      const urls = this.imageList.map(img => img.image)
      uni.previewImage({ urls, current: 0 })
    },
    shareCar() {
      uni.showShareMenu({ withShareTicket: true })
    },
    contact() {
      if (!requireAuth()) return
      uni.navigateTo({ url: '/pages/message/index?target=' + this.seller.id + '&car=' + this.id })
    },
    toSellerHome() {
      uni.navigateTo({ url: '/pages/seller-home/index?id=' + this.seller.id })
    },
    buyNow() {
      if (!requireAuth()) return
      uni.navigateTo({ url: '/pages/order-detail/index?carId=' + this.id })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar-right { padding-right: 20rpx; }
.page-content { padding-bottom: 120rpx; background: #f5f5f5; min-height: 100vh; }
.detail-section { background: #fff; margin: 16rpx 0; padding: 30rpx; }
.price-row { display: flex; align-items: center; gap: 16rpx; }
.price { font-size: 44rpx; font-weight: 700; color: #f56c6c; }
.original-price { font-size: 26rpx; color: #999; text-decoration: line-through; }
.title { font-size: 32rpx; font-weight: 600; color: #333; margin-top: 16rpx; }
.params-row { display: flex; margin-top: 30rpx; }
.param-item { flex: 1; text-align: center; border-right: 1rpx solid #eee; }
.param-item:last-child { border-right: none; }
.param-value { display: block; font-size: 30rpx; font-weight: 600; color: #333; }
.param-label { display: block; font-size: 22rpx; color: #999; margin-top: 8rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }
.tag-grid { display: flex; gap: 12rpx; flex-wrap: wrap; }
.desc-text { font-size: 26rpx; color: #666; line-height: 1.8; }
.seller-section { display: flex; align-items: center; }
.seller-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; margin-right: 20rpx; }
.seller-info { flex: 1; }
.seller-name { font-size: 28rpx; font-weight: 600; color: #333; }
.seller-company { font-size: 22rpx; color: #999; display: block; }
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  display: flex; align-items: center; padding: 12rpx 30rpx; padding-bottom: calc(12rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #eee;
}
.action-btn { display: flex; flex-direction: column; align-items: center; margin-right: 30rpx; font-size: 20rpx; color: #666; }
.buy-btn { flex: 1; background: linear-gradient(135deg, #3c9cff, #2979ff); border-radius: 44rpx; height: 80rpx; display: flex; align-items: center; justify-content: center; }
.buy-btn text { color: #fff; font-size: 30rpx; font-weight: 600; }
</style>
