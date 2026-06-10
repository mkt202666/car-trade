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
          <u-button
            size="mini"
            :type="carInfo.seller.followedByCurrentUser ? 'warning' : 'primary'"
            :plain="!carInfo.seller.followedByCurrentUser"
            @click="followSeller"
          >
            {{ carInfo.seller.followedByCurrentUser ? '已关注' : '+ 关注' }}
          </u-button>
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
          <text>联系</text>
        </view>
        <view class="action-btn" @click="payDeposit">
          <u-icon name="rmb-circle" size="40" color="#666"></u-icon>
          <text>付定</text>
        </view>
        <view class="action-btn" @click="shareCar">
          <u-icon name="share" size="40" color="#666"></u-icon>
          <text>分享</text>
        </view>
        <view class="action-btn" @click="showReportDialog">
          <u-icon name="warning" size="40" color="#666"></u-icon>
          <text>举报</text>
        </view>
      </view>
    </view>

    <!-- 举报弹窗 -->
    <u-popup :show="reportDialogVisible" mode="center" :round="16" @close="closeReportDialog">
      <view class="report-popup">
        <view class="report-header">
          <text class="report-title">举报车源</text>
          <u-icon name="close" size="36" @click="closeReportDialog"></u-icon>
        </view>
        <view class="report-body">
          <text class="report-label">举报原因</text>
          <view class="report-reasons">
            <view
              v-for="reason in reportReasons"
              :key="reason"
              class="reason-item"
              :class="{ active: reportForm.reason === reason }"
              @click="selectReportReason(reason)"
            >
              <text>{{ reason }}</text>
            </view>
          </view>
          <text class="report-label">补充说明（选填）</text>
          <u-input
            v-model="reportForm.description"
            type="textarea"
            :height="120"
            :maxlength="200"
            placeholder="请详细描述问题..."
            border="surround"
          />
        </view>
        <view class="report-footer">
          <u-button type="primary" @click="submitReport">提交举报</u-button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getCarDetail, favoriteCar, unfavoriteCar, contactSeller, downloadImage } from '@/api/car'
import { followUser, unfollowUser } from '@/api/follow'
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
        seller: { id: '', name: '', shopName: '', avatar: '', creditGrade: '', lastOnline: '', carCount: 0, dealCount: 0, followerCount: 0, followedByCurrentUser: false },
        inspectionReports: { chaboshi: { available: false }, ningmengcha: { available: false }, weibao: { available: false } },
        favorited: false,
        // 扩展字段
        brandName: '',
        seriesName: '',
        modelName: '',
        city: '',
        energyType: '',
        ownerType: '',
        isMortgaged: false,
        isInherited: false,
        registrationDate: '',
        insuranceExpiry: '',
        inspectionExpiry: '',
        transferCount: 0,
        mileageType: '',
        abnormalPhotos: []
      },
      imageList: [],
      flagMap: {
        RU: '🇷🇺', KZ: '🇰🇿', AE: '🇦🇪', AU: '🇦🇺'
      },
      loading: false,
      reportDialogVisible: false,
      reportForm: {
        reason: '',
        description: ''
      },
      reportReasons: [
        '价格虚假',
        '车辆信息不实',
        '图片与实际不符',
        '重复发布',
        '涉嫌欺诈',
        '其他'
      ]
    }
  },
  onLoad(options) {
    if (options.id) {
      this.fetchCarDetail(options.id)
    } else if (options.carId) {
      this.fetchCarDetail(options.carId)
    }
  },
  // 页面分享
  onShareAppMessage() {
    return {
      title: this.carInfo.title,
      path: `/pages/car-detail/index?id=${this.carInfo.id}`,
      imageUrl: this.carInfo.coverImage || (this.imageList[0] && this.imageList[0].image)
    }
  },
  onShareTimeline() {
    return {
      title: this.carInfo.title,
      query: `id=${this.carInfo.id}`,
      imageUrl: this.carInfo.coverImage || (this.imageList[0] && this.imageList[0].image)
    }
  },
  methods: {
    formatPrice,
    async fetchCarDetail(id) {
      this.loading = true
      try {
        const res = await getCarDetail(id)
        const data = res.data || res
        // 映射后端 VO 到前端数据结构
        this.carInfo = {
          id: data.id || '',
          carId: data.id || '',
          title: data.title || '',
          images: data.images || [],
          price: data.price || 0,
          deposit: data.deposit || 0,
          hasDeposit: (data.deposit && data.deposit > 0) || false,
          exportCountries: data.tags || [],
          year: data.year || '',
          month: data.registrationDate ? String(data.registrationDate).split('-')[1] || '' : '',
          mileage: data.mileage || 0,
          color: data.color || '',
          interiorColor: '',
          emissionStandard: '',
          displacement: data.energyType || '',
          gearbox: data.transmission || '',
          manufacturer: data.brandName || '',
          vehicleType: data.vehicleType || '',
          guidePrice: data.guidePrice || 0,
          usageType: data.usageType || '',
          insuranceExpire: data.insuranceExpiry || '',
          location: data.city || '',
          createTime: data.createdAt ? this.formatDateTime(data.createdAt) : '',
          updateTime: data.createdAt ? this.formatDateTime(data.createdAt) : '',
          description: data.description || '',
          aiAnalysis: data.overallCondition || '暂无AI分析',
          seller: {
            id: data.sellerId || '',
            name: data.sellerName || '匿名卖家',
            shopName: data.sellerShopName || '',
            avatar: data.sellerAvatar || '',
            creditGrade: data.sellerCreditGrade || '',
            lastOnline: '',
            carCount: 0,
            dealCount: data.sellerDealCount || 0,
            followerCount: data.sellerFollowerCount || 0,
            followedByCurrentUser: data.followedByCurrentUser || false
          },
          favorited: data.favoritedByCurrentUser || false,
          // 扩展字段
          brandName: data.brandName || '',
          seriesName: data.seriesName || '',
          modelName: data.modelName || '',
          city: data.city || '',
          energyType: data.energyType || '',
          ownerType: data.ownerType || '',
          isMortgaged: data.isMortgaged || false,
          isInherited: data.isInherited || false,
          registrationDate: data.registrationDate || '',
          insuranceExpiry: data.insuranceExpiry || '',
          inspectionExpiry: data.inspectionExpiry || '',
          transferCount: data.transferCount || 0,
          mileageType: data.mileageType || '',
          abnormalPhotos: data.abnormalPhotos || [],
          coverImage: data.coverImage || ''
        }
        // 构建图片列表
        if (this.carInfo.images && this.carInfo.images.length > 0) {
          this.imageList = this.carInfo.images.map((img, index) => {
            if (typeof img === 'string') {
              return { image: img }
            }
            return { image: img.url || img }
          })
        } else if (this.carInfo.coverImage) {
          this.imageList = [{ image: this.carInfo.coverImage }]
        } else {
          this.imageList = [{ image: '/static/default-car.png' }]
        }
      } catch (e) {
        console.error('获取车源详情失败:', e)
        uni.$u.toast('加载车源详情失败，请重试')
      } finally {
        this.loading = false
      }
    },
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    },
    exportFlag(code) {
      return this.flagMap[code] || code
    },
    previewImage(index) {
      const urls = this.imageList.map(img => img.image)
      uni.previewImage({ urls, current: index || 0 })
    },
    // 分享功能
    shareCar() {
      if (!requireAuth()) return
      // 触发分享面板
      uni.showShareMenu({
        withShareTicket: true,
        menus: ['shareAppMessage', 'shareTimeline']
      })
    },
    // 收藏功能
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
        console.error('收藏操作失败:', e)
        uni.$u.toast('操作失败，请重试')
      }
    },
    // 联系卖家
    async contact() {
      if (!requireAuth()) return
      try {
        await contactSeller(this.carInfo.id)
        // 跳转到聊天页面
        uni.navigateTo({
          url: `/pages/chat/index?targetId=${this.carInfo.seller.id}&targetName=${this.carInfo.seller.name}`
        })
      } catch (e) {
        console.error('联系卖家失败:', e)
        uni.$u.toast('联系卖家失败，请重试')
      }
    },
    payDeposit() {
      if (!requireAuth()) return
      uni.navigateTo({ url: `/pages/order-detail/index?carId=${this.carInfo.id}` })
    },
    // 关注卖家
    async followSeller() {
      if (!requireAuth()) return
      try {
        if (this.carInfo.seller.followedByCurrentUser) {
          await unfollowUser(this.carInfo.seller.id)
          this.carInfo.seller.followedByCurrentUser = false
          this.carInfo.seller.followerCount = Math.max(0, (this.carInfo.seller.followerCount || 0) - 1)
          uni.$u.toast('已取消关注')
        } else {
          await followUser(this.carInfo.seller.id)
          this.carInfo.seller.followedByCurrentUser = true
          this.carInfo.seller.followerCount = (this.carInfo.seller.followerCount || 0) + 1
          uni.$u.toast('关注成功')
        }
      } catch (e) {
        console.error('关注卖家失败:', e)
        uni.$u.toast('操作失败，请重试')
      }
    },
    toSellerHome() {
      if (!this.carInfo.seller.id) {
        uni.$u.toast('卖家信息加载中')
        return
      }
      uni.navigateTo({ url: `/pages/seller-home/index?id=${this.carInfo.seller.id}` })
    },
    viewReport(type) {
      if (!requireAuth()) return
      // 暂时显示提示，后续可对接具体报告查看页面
      uni.$u.toast('报告功能开发中，请联系卖家获取')
    },
    // 下载图片
    async downloadImages() {
      if (!requireAuth()) return
      try {
        uni.showLoading({ title: '准备下载...' })
        const images = this.carInfo.images || []
        if (images.length === 0) {
          uni.$u.toast('暂无图片可下载')
          uni.hideLoading()
          return
        }
        for (let i = 0; i < images.length; i++) {
          const img = images[i]
          const url = typeof img === 'string' ? img : img.url
          if (url) {
            await this.downloadSingleImage(url, i + 1)
          }
        }
        uni.hideLoading()
        uni.$u.toast('图片保存成功')
      } catch (e) {
        console.error('下载图片失败:', e)
        uni.hideLoading()
        uni.$u.toast('下载失败，请重试')
      }
    },
    downloadSingleImage(url, index) {
      return new Promise((resolve, reject) => {
        uni.downloadFile({
          url: url,
          success: (res) => {
            if (res.statusCode === 200) {
              uni.saveImageToPhotosAlbum({
                filePath: res.tempFilePath,
                success: () => {
                  resolve()
                },
                fail: (err) => {
                  if (err.errMsg && err.errMsg.includes('auth deny')) {
                    uni.$u.toast('请授权保存图片到相册')
                    // 打开设置页面
                    uni.openSetting()
                  }
                  reject(err)
                }
              })
            } else {
              reject(new Error('下载失败'))
            }
          },
          fail: reject
        })
      })
    },
    // 举报功能
    showReportDialog() {
      if (!requireAuth()) return
      this.reportDialogVisible = true
    },
    closeReportDialog() {
      this.reportDialogVisible = false
      this.reportForm = { reason: '', description: '' }
    },
    selectReportReason(reason) {
      this.reportForm.reason = reason
    },
    async submitReport() {
      if (!this.reportForm.reason) {
        uni.$u.toast('请选择举报原因')
        return
      }
      try {
        uni.showLoading({ title: '提交中...' })
        // TODO: 对接举报 API
        // await reportCar(this.carInfo.id, this.reportForm)
        await new Promise(resolve => setTimeout(resolve, 1000))
        uni.hideLoading()
        uni.$u.toast('举报已提交，我们会尽快处理')
        this.closeReportDialog()
      } catch (e) {
        console.error('举报失败:', e)
        uni.hideLoading()
        uni.$u.toast('提交失败，请重试')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
/* 设计系统变量 */
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
  padding-bottom: 140rpx;
}
.navbar-right {
  padding-right: 20rpx;
  cursor: pointer;
  transition: $transition;

  &:active {
    opacity: 0.7;
  }
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
  border-radius: $border-radius;
  box-shadow: $shadow;
  margin: 20rpx;
}

/* 车名+标签 */
.car-name-row {
  margin-bottom: 12rpx;
}
.car-name {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-color;
}
.car-id {
  font-size: 22rpx;
  color: $text-secondary;
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
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  border: 1rpx solid $border-color;
  color: $text-secondary;
  background: $bg-color;
}
.tag.deposit {
  color: $cta-color;
  border-color: $cta-color;
  background: rgba(3, 105, 161, 0.08);
}
.tag.export {
  font-size: 24rpx;
  padding: 2rpx 6rpx;
  border: none;
  background: transparent;
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
  color: $cta-color;
  cursor: pointer;
  transition: $transition;

  &:active {
    opacity: 0.7;
  }
}

/* 卖家头部 */
.seller-header {
  display: flex;
  align-items: center;
  padding: 10rpx 0;
}
.seller-avatar {
  width: 70rpx;
  height: 70rpx;
  border-radius: 50%;
  margin-right: 16rpx;
  border: 2rpx solid $border-color;
}
.seller-info {
  flex: 1;
}
.seller-name {
  font-size: 26rpx;
  color: $text-color;
  font-weight: 600;
  display: block;
}
.seller-time {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
  margin-top: 4rpx;
}

/* AI 分析 */
.ai-section {
  background: linear-gradient(135deg, #faf5ff, #f3e8ff);
  border-radius: $border-radius;
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
}
.ai-text {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.8;
}
.desc-text {
  font-size: 26rpx;
  color: $text-secondary;
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
  padding: 16rpx 0;
  border-radius: $border-radius;
  transition: $transition;

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}
.param-value {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
}
.param-label {
  font-size: 20rpx;
  color: $text-secondary;
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
  padding: 20rpx 0;
  border-bottom: 1rpx solid $border-color;
  cursor: pointer;
  transition: $transition;
  border-radius: $border-radius;

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}
.report-name {
  font-size: 28rpx;
  color: $text-color;
}
.report-action {
  font-size: 24rpx;
  color: $cta-color;
}

/* 卖方信息 */
.seller-full {
  background: #fff;
  border-radius: $border-radius;
  box-shadow: $shadow;
  margin: 20rpx;
}
.seller-card {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  cursor: pointer;
  transition: $transition;
  padding: 16rpx;
  border-radius: $border-radius;

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}
.seller-avatar-lg {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  border: 2rpx solid $border-color;
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
  color: $text-color;
}
.seller-credit {
  font-size: 22rpx;
  color: #5ac725;
  margin-left: 12rpx;
}
.seller-shop {
  font-size: 22rpx;
  color: $text-secondary;
}
.seller-last {
  font-size: 22rpx;
  color: $text-secondary;
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
  border-top: 1rpx solid $border-color;
}
.seller-stat {
  flex: 1;
}
.ss-num {
  font-size: 32rpx;
  font-weight: 700;
  color: $cta-color;
  display: block;
}
.ss-label {
  font-size: 22rpx;
  color: $text-secondary;
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
  border-radius: $border-radius;
  cursor: pointer;
  transition: $transition;

  &:active {
    opacity: 0.8;
  }
}
.download-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: $cta-color;
  padding: 16rpx 0;
  cursor: pointer;
  transition: $transition;
  border-radius: $border-radius;

  &:active {
    background: rgba(3, 105, 161, 0.05);
  }
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
  border-top: 1rpx solid $border-color;
  box-shadow: 0 -4rpx 16rpx rgba(15, 23, 42, 0.08);
  z-index: 10;
}
.action-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 20rpx;
  color: $text-secondary;
  gap: 4rpx;
  cursor: pointer;
  transition: $transition;
  padding: 8rpx 0;
  border-radius: $border-radius;

  &:active {
    background: rgba(0, 0, 0, 0.05);
    transform: scale(0.95);
  }
}

/* 举报弹窗 */
.report-popup {
  width: 600rpx;
  padding: 30rpx;
}
.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}
.report-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-color;
}
.report-body {
  margin-bottom: 30rpx;
}
.report-label {
  font-size: 26rpx;
  color: $text-secondary;
  display: block;
  margin-bottom: 16rpx;
}
.report-reasons {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.reason-item {
  padding: 12rpx 24rpx;
  border: 1rpx solid $border-color;
  border-radius: $border-radius;
  font-size: 24rpx;
  color: $text-secondary;
  cursor: pointer;
  transition: $transition;
  background: $bg-color;

  &:hover {
    border-color: $cta-color;
    color: $cta-color;
  }

  &:active {
    transform: scale(0.98);
  }
}
.reason-item.active {
  border-color: $cta-color;
  background: rgba(3, 105, 161, 0.1);
  color: $cta-color;
}
.report-footer {
  margin-top: 20rpx;
}
</style>