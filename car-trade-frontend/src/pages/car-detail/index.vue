<template>
  <view class="page">
    <u-navbar :border-bottom="false" :placeholder="true" :title="carInfo.title || '车源详情'">
      <view slot="right" class="navbar-right" @click="shareCar">
        <u-icon name="share" size="36"></u-icon>
      </view>
    </u-navbar>

    <view class="page-content">
      <!-- 图片轮播 -->
      <view class="image-section">
        <u-swiper :list="imageList" :height="500" :border-radius="0" indicator indicator-mode="number" @click="previewImage"></u-swiper>
      </view>

      <!-- 14天金融优惠 -->
      <view class="promo-banner">
        <text>可享：14天金融优惠</text>
      </view>

      <!-- 基本信息 -->
      <view class="detail-section">
        <view class="car-name-row">
          <text class="car-name">{{ carInfo.title }}</text>
          <text class="car-id">车源ID {{ carInfo.carId }}</text>
        </view>
        <view class="car-tags-row">
          <view class="tag deposit" v-if="carInfo.hasDeposit">保证金</view>
          <view class="tag export" v-for="code in carInfo.exportCountries" :key="code">{{ exportFlag(code) }}</view>
        </view>
        <view class="price-row">
          <text class="price">{{ carInfo.price / 10000 }}万</text>
          <view class="share-btn" @click="shareCar">
            <u-icon name="share" size="32" color="#3c9cff"></u-icon>
            <text>分享</text>
          </view>
        </view>
      </view>

      <!-- 卖家信息 -->
      <view class="detail-section seller-section">
        <view class="seller-header">
          <image :src="carInfo.seller.avatar || '/static/default-avatar.png'" mode="aspectFill" class="seller-avatar"></image>
          <view class="seller-info">
            <text class="seller-name">{{ carInfo.seller.name }}({{ carInfo.seller.shopName }})</text>
            <text class="seller-time">上传于 {{ carInfo.createTime }} 最后更新 {{ carInfo.updateTime }}</text>
          </view>
        </view>
      </view>

      <!-- AI 分析 -->
      <view class="detail-section ai-section">
        <view class="section-title">
          <u-icon name="grid" size="32" color="#a855f7"></u-icon>
          <text>AI 分析</text>
        </view>
        <text class="ai-text">{{ carInfo.aiAnalysis }}</text>
      </view>

      <!-- 车况描述 -->
      <view class="detail-section">
        <view class="section-title">车况描述及说明</view>
        <text class="desc-text">{{ carInfo.description || '暂无描述' }}</text>
      </view>

      <!-- 车辆参数 -->
      <view class="detail-section">
        <view class="section-title">车辆参数</view>
        <view class="params-grid">
          <view class="param-item">
            <text class="param-value">{{ carInfo.year }}-{{ carInfo.month || '01' }}</text>
            <text class="param-label">上牌日期</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.mileage }}万公里</text>
            <text class="param-label">表显里程</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.emissionStandard || '国六' }}</text>
            <text class="param-label">排放标准</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.displacement }}</text>
            <text class="param-label">排量</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.color }}|{{ carInfo.interiorColor }}</text>
            <text class="param-label">车身|内饰</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.gearbox }}</text>
            <text class="param-label">变速箱</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.manufacturer }}</text>
            <text class="param-label">主机厂</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.vehicleType }}</text>
            <text class="param-label">车辆类型</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.guidePrice / 10000 }}万</text>
            <text class="param-label">新车指导价</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.usageType }}</text>
            <text class="param-label">使用性质</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.insuranceExpire }}</text>
            <text class="param-label">交强险到期</text>
          </view>
          <view class="param-item">
            <text class="param-value">{{ carInfo.location }}</text>
            <text class="param-label">车辆位置</text>
          </view>
        </view>
      </view>

      <!-- 车辆报告 -->
      <view class="detail-section">
        <view class="section-title">车辆报告</view>
        <view class="report-list">
          <view class="report-item" @click="viewReport('chaboshi')">
            <text class="report-name">查博士</text>
            <text class="report-action">查看</text>
          </view>
          <view class="report-item" @click="viewReport('ningmengcha')">
            <text class="report-name">柠檬查</text>
            <text class="report-action">报告 联系获取</text>
          </view>
          <view class="report-item" @click="viewReport('weibao')">
            <text class="report-name">4S维保</text>
            <text class="report-action">记录 联系获取</text>
          </view>
        </view>
      </view>

      <!-- 卖方信息 -->
      <view class="detail-section seller-full">
        <view class="section-title">卖方信息</view>
        <view class="seller-card" @click="toSellerHome">
          <image :src="carInfo.seller.avatar || '/static/default-avatar.png'" mode="aspectFill" class="seller-avatar-lg"></image>
          <view class="seller-info-right">
            <view class="seller-top">
              <view>
                <text class="seller-name-lg">{{ carInfo.seller.name }}</text>
                <text class="seller-credit">信用{{ carInfo.seller.creditGrade }}级</text>
              </view>
              <text class="seller-shop">{{ carInfo.seller.shopName }}</text>
            </view>
            <view class="seller-last">上次在线: {{ carInfo.seller.lastOnline }}</view>
          </view>
        </view>
        <view class="seller-actions">
          <u-button size="mini" type="primary" :plain="true" @click="followSeller">+ 关注</u-button>
          <u-button size="mini" type="primary" @click="toSellerHome">他的主页</u-button>
        </view>
        <view class="seller-stats">
          <view class="seller-stat">
            <text class="ss-num">{{ carInfo.seller.carCount }}</text>
            <text class="ss-label">车源</text>
          </view>
          <view class="seller-stat">
            <text class="ss-num">{{ carInfo.seller.dealCount }}</text>
            <text class="ss-label">成交</text>
          </view>
          <view class="seller-stat">
            <text class="ss-num">{{ carInfo.seller.followerCount }}</text>
            <text class="ss-label">粉丝</text>
          </view>
        </view>
      </view>

      <!-- 车辆实拍 -->
      <view class="detail-section">
        <view class="section-title">车辆实拍</view>
        <view class="photo-grid">
          <image :src="carInfo.coverImage || '/static/default-car.png'" mode="aspectFill" class="photo-item" @click="previewImage"></image>
        </view>
        <view class="download-row" @click="downloadImages">
          <u-icon name="download" size="32" color="#3c9cff"></u-icon>
          <text>下载图片</text>
        </view>
      </view>

      <!-- 底部操作栏 -->
      <view class="bottom-bar">
        <view class="action-btn" @click="toggleFavorite">
          <u-icon :name="carInfo.favorited ? 'heart-fill' : 'heart'" :color="carInfo.favorited ? '#f56c6c' : '#666'" size="40"></u-icon>
          <text>收藏</text>
        </view>
        <view class="action-btn" @click="contact">
          <u-icon name="chat" size="40" color="#666"></u-icon>
          <text>聊天</text>
        </view>
        <view class="action-btn" @click="payDeposit">
          <u-icon name="rmb-circle" size="40" color="#666"></u-icon>
          <text>付定</text>
        </view>
        <view class="action-btn" @click="shareCar">
          <u-icon name="share" size="40" color="#666"></u-icon>
          <text>分享</text>
        </view>
        <view class="action-btn" @click="report">
          <u-icon name="warning" size="40" color="#666"></u-icon>
          <text>举报</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getCarDetail, favoriteCar, unfavoriteCar, contactSeller, downloadImage } from '@/api/car'
import { formatPrice } from '@/utils/format'
import { requireAuth } from '@/utils/auth'

export default {
  data() {
    return {
      carInfo: {
        id: '',
        carId: '',
        title: '',
        images: [],
        price: 0,
        deposit: 0,
        hasDeposit: false,
        exportCountries: [],
        year: '',
        month: '',
        mileage: 0,
        color: '',
        interiorColor: '',
        emissionStandard: '',
        displacement: '',
        gearbox: '',
        manufacturer: '',
        vehicleType: '',
        guidePrice: 0,
        usageType: '',
        insuranceExpire: '',
        location: '',
        createTime: '',
        updateTime: '',
        description: '',
        aiAnalysis: '',
        seller: { id: '', name: '', shopName: '', avatar: '', creditGrade: '', lastOnline: '', carCount: 0, dealCount: 0, followerCount: 0 },
        inspectionReports: { chaboshi: { available: false }, ningmengcha: { available: false }, weibao: { available: false } },
        favorited: false
      },
      imageList: [],
      flagMap: {
        RU: '🇷🇺', KZ: '🇰🇿', AE: '🇦🇪', AU: '🇦🇺'
      }
    }
  },
  onLoad(options) {
    if (options.id) {
      this.fetchCarDetail(options.id)
    }
  },
  methods: {
    formatPrice,
    async fetchCarDetail(id) {
      try {
        const res = await getCarDetail(id)
        const data = res.data
        this.carInfo = data
        this.imageList = data.images ? data.images.map(img => ({ image: img.url || img })) : [{ image: '' }]
      } catch (e) {
        uni.$u.toast('加载车源详情失败')
      }
    },
    exportFlag(code) {
      return this.flagMap[code] || code
    },
    previewImage() {
      const urls = this.imageList.map(img => img.image)
      uni.previewImage({ urls, current: 0 })
    },
    shareCar() {
      uni.$u.toast('分享功能')
    },
    async toggleFavorite() {
      if (!requireAuth()) return
      try {
        if (this.carInfo.favorited) {
          await unfavoriteCar(this.carInfo.id)
          this.carInfo.favorited = false
          uni.$u.toast('已取消收藏')
        } else {
          await favoriteCar(this.carInfo.id)
          this.carInfo.favorited = true
          uni.$u.toast('已收藏')
        }
      } catch (e) {
        uni.$u.toast('操作失败')
      }
    },
    async contact() {
      if (!requireAuth()) return
      try {
        await contactSeller(this.carInfo.id)
        uni.navigateTo({ url: '/pages/customer-service/index?target=' + this.carInfo.seller.id })
      } catch (e) {
        uni.$u.toast('联系卖家失败')
      }
    },
    payDeposit() {
      if (!requireAuth()) return
      uni.navigateTo({ url: '/pages/order-detail/index?carId=' + this.carInfo.id })
    },
    report() {
      uni.$u.toast('举报功能')
    },
    followSeller() {
      uni.$u.toast('已关注')
    },
    toSellerHome() {
      uni.navigateTo({ url: '/pages/seller-home/index?id=' + this.carInfo.seller.id })
    },
    viewReport(type) {
      uni.$u.toast('查看报告: ' + type)
    },
    async downloadImages() {
      try {
        if (this.carInfo.images && this.carInfo.images.length > 0) {
          await downloadImage(this.carInfo.id, this.carInfo.images[0].id || 0)
        }
        uni.$u.toast('下载图片')
      } catch (e) {
        uni.$u.toast('下载失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}
.page-content {
  padding-bottom: 140rpx;
}
.navbar-right {
  padding-right: 20rpx;
}

/* 图片轮播 */
.image-section {
  position: relative;
}

/* 金融优惠 */
.promo-banner {
  background: linear-gradient(135deg, #fff7e6, #fff3d6);
  padding: 20rpx 30rpx;
  text-align: center;
  font-size: 26rpx;
  color: #f9ae3d;
  font-weight: 600;
}

/* 通用区块 */
.detail-section {
  background: #fff;
  margin: 16rpx 0;
  padding: 30rpx;
}

/* 车名+标签 */
.car-name-row {
  margin-bottom: 12rpx;
}
.car-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}
.car-id {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-top: 6rpx;
}
.car-tags-row {
  display: flex;
  gap: 12rpx;
  margin-bottom: 20rpx;
}
.tag {
  font-size: 20rpx;
  padding: 4rpx 14rpx;
  border-radius: 6rpx;
}
.tag.deposit {
  color: #3c9cff;
  border: 1rpx solid #3c9cff;
}
.tag.export {
  font-size: 24rpx;
  padding: 2rpx 6rpx;
  border: none;
}
.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  font-size: 44rpx;
  font-weight: 700;
  color: #f56c6c;
}
.share-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: 24rpx;
  color: #3c9cff;
}

/* 卖家头部 */
.seller-header {
  display: flex;
  align-items: center;
}
.seller-avatar {
  width: 70rpx;
  height: 70rpx;
  border-radius: 50%;
  margin-right: 16rpx;
}
.seller-info {
  flex: 1;
}
.seller-name {
  font-size: 26rpx;
  color: #333;
  font-weight: 600;
  display: block;
}
.seller-time {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-top: 4rpx;
}

/* AI 分析 */
.ai-section {
  background: linear-gradient(135deg, #faf5ff, #f3e8ff);
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
}
.ai-text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.8;
}
.desc-text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.8;
}

/* 车辆参数 */
.params-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30rpx 0;
}
.param-item {
  text-align: center;
}
.param-value {
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
  display: block;
}
.param-label {
  font-size: 20rpx;
  color: #999;
  display: block;
  margin-top: 4rpx;
}

/* 车辆报告 */
.report-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}
.report-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.report-name {
  font-size: 28rpx;
  color: #333;
}
.report-action {
  font-size: 24rpx;
  color: #3c9cff;
}

/* 卖方信息 */
.seller-full {
  background: #fff;
}
.seller-card {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}
.seller-avatar-lg {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}
.seller-info-right {
  flex: 1;
}
.seller-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.seller-name-lg {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}
.seller-credit {
  font-size: 22rpx;
  color: #5ac725;
  margin-left: 12rpx;
}
.seller-shop {
  font-size: 22rpx;
  color: #999;
}
.seller-last {
  font-size: 22rpx;
  color: #999;
  margin-top: 6rpx;
}
.seller-actions {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}
.seller-stats {
  display: flex;
  text-align: center;
  padding-top: 16rpx;
  border-top: 1rpx solid #f5f5f5;
}
.seller-stat {
  flex: 1;
}
.ss-num {
  font-size: 32rpx;
  font-weight: 700;
  color: #3c9cff;
  display: block;
}
.ss-label {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-top: 4rpx;
}

/* 车辆实拍 */
.photo-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10rpx;
  margin-bottom: 20rpx;
}
.photo-item {
  width: 100%;
  height: 200rpx;
  border-radius: 8rpx;
}
.download-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #3c9cff;
  padding: 16rpx 0;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 12rpx 30rpx;
  padding-bottom: calc(12rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #eee;
  z-index: 10;
}
.action-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 20rpx;
  color: #666;
  gap: 4rpx;
}
</style>