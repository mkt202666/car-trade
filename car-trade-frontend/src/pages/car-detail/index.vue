<template>
  <view class="page">
    <!-- 自定义返回导航栏 -->
    <view class="page-hero">
      <back-navbar :title="carInfo.title" :show-share="true" @right="shareCar" />

      <!-- 图片轮播 -->
      <view class="hero-visual" v-if="imageList.length">
        <view class="visual-indicator">
          <text class="indicator-num">{{ currentSwiper + 1 }}/{{ imageList.length }}</text>
        </view>
        <swiper
          class="visual-swiper"
          :current="currentSwiper"
          :circular="true"
          :duration="450"
          @change="onSwiperChange"
        >
          <swiper-item v-for="(img, idx) in imageList" :key="idx">
            <image :src="img.image" mode="aspectFill" class="visual-img" @tap="previewImage(idx)" />
          </swiper-item>
        </swiper>
        <view class="visual-gradient"></view>
      </view>
      <view v-else class="hero-visual placeholder">
        <text class="placeholder-text">加载中...</text>
      </view>
    </view>

    <view class="page-body">
      <!-- 标题 + 价格卡片 -->
      <view class="card-primary animate-in" style="animation-delay: 0.02s">
        <view class="tag-row">
          <view class="badge badge-navy" v-if="carInfo.hasDeposit">已保价</view>
          <view class="flag-item" v-for="code in carInfo.exportCountries" :key="code">
            <text>{{ exportFlag(code) }}</text>
          </view>
          <view class="badge badge-gold" v-if="carInfo.year">车源优选</view>
        </view>

        <view class="title-row">
          <text class="car-title">{{ carInfo.title || '车源详情' }}</text>
        </view>

        <view class="meta-row">
          <text class="meta-item">ID: {{ carInfo.carId || carInfo.id }}</text>
          <text class="meta-dot">·</text>
          <text class="meta-item">{{ carInfo.city || '—' }}</text>
          <text class="meta-dot" v-if="carInfo.updateTime">·</text>
          <text class="meta-item" v-if="carInfo.updateTime">更新: {{ formatDateTime(carInfo.updateTime) }}</text>
        </view>

        <view class="price-container">
          <view class="price-main">
            <text class="price-symbol">¥</text>
            <text class="price-value">{{ formatPrice(carInfo.price) }}</text>
          </view>
          <view class="price-detail">
            <text class="price-ref" v-if="carInfo.guidePrice">新车参考价 ¥{{ formatPrice(carInfo.guidePrice) }}</text>
            <text class="price-hint">价可面议 · 支持金融方案</text>
          </view>
        </view>

        <view class="finance-banner" v-if="carInfo.exportCountries && carInfo.exportCountries.length">
          <view class="finance-icon">
            <u-icon name="volume" size="28" color="#D97706"></u-icon>
          </view>
          <view class="finance-text">
            <text class="finance-title">支持跨境出口 · 多币种结算</text>
            <text class="finance-desc">覆盖 {{ carInfo.exportCountries.length }} 个国家/地区</text>
          </view>
        </view>
      </view>

      <!-- AI 分析卡片 -->
      <view class="card-ai animate-in" v-if="carInfo.aiAnalysis" style="animation-delay: 0.08s">
        <view class="ai-header">
          <view class="ai-icon">
            <text class="ai-logo">AI</text>
          </view>
          <view class="ai-meta">
            <text class="ai-title">智能车源分析</text>
            <text class="ai-subtitle">基于车况 · 历史数据 · 市场行情</text>
          </view>
          <view class="ai-badge">已分析</view>
        </view>
        <view class="ai-divider"></view>
        <text class="ai-body">{{ carInfo.aiAnalysis }}</text>
      </view>

      <!-- 核心参数卡片 -->
      <view class="card-secondary animate-in" style="animation-delay: 0.12s">
        <view class="card-header">
          <text class="card-title">核心参数</text>
        </view>
        <view class="param-grid">
          <view class="param-cell">
            <text class="param-v">{{ carInfo.year || '—' }}</text>
            <text class="param-l">上牌年份</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.mileage ? (carInfo.mileage / 10000).toFixed(1) + '万' : '—' }}</text>
            <text class="param-l">表显里程</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.gearbox || '—' }}</text>
            <text class="param-l">变速箱</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.color || '—' }}</text>
            <text class="param-l">车身颜色</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.emissionStandard || '—' }}</text>
            <text class="param-l">排放标准</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.displacement || '—' }}</text>
            <text class="param-l">排量</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.manufacturer || '—' }}</text>
            <text class="param-l">主机厂</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.vehicleType || '—' }}</text>
            <text class="param-l">车辆类型</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.usageType || '—' }}</text>
            <text class="param-l">使用性质</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.insuranceExpire || '—' }}</text>
            <text class="param-l">交强险到期</text>
          </view>
          <view class="param-cell">
            <text class="param-v">{{ carInfo.location || '—' }}</text>
            <text class="param-l">车辆所在地</text>
          </view>
        </view>
      </view>

      <!-- 车况描述 -->
      <view class="card-secondary animate-in" style="animation-delay: 0.16s">
        <view class="card-header">
          <text class="card-title">车况描述</text>
        </view>
        <text class="desc-text">{{ carInfo.description || '暂无详细描述' }}</text>
      </view>

      <!-- 检测报告卡片 -->
      <view class="card-secondary animate-in" style="animation-delay: 0.20s">
        <view class="card-header">
          <text class="card-title">检测报告</text>
          <text class="card-hint">权威第三方</text>
        </view>
        <view class="report-list">
          <view class="report-row" @click="viewReport('chaboshi')">
            <view class="report-left">
              <view class="report-icon report-chaboshi">查</view>
              <view class="report-meta">
                <text class="report-name">查博士</text>
                <text class="report-desc">车辆历史数据</text>
              </view>
            </view>
            <view class="report-arrow">
              <u-icon name="arrow-right" size="20" color="#94A3B8"></u-icon>
            </view>
          </view>
          <view class="report-row" @click="viewReport('ningmengcha')">
            <view class="report-left">
              <view class="report-icon report-lemon">柠</view>
              <view class="report-meta">
                <text class="report-name">柠檬查</text>
                <text class="report-desc">透明车况</text>
              </view>
            </view>
            <view class="report-arrow">
              <u-icon name="arrow-right" size="20" color="#94A3B8"></u-icon>
            </view>
          </view>
          <view class="report-row" @click="viewReport('weibao')">
            <view class="report-left">
              <view class="report-icon report-service">维</view>
              <view class="report-meta">
                <text class="report-name">4S维保记录</text>
                <text class="report-desc">保养维修历史</text>
              </view>
            </view>
            <view class="report-arrow">
              <u-icon name="arrow-right" size="20" color="#94A3B8"></u-icon>
            </view>
          </view>
        </view>
      </view>

      <!-- 卖家信息 -->
      <view class="card-secondary animate-in" style="animation-delay: 0.24s">
        <view class="card-header">
          <text class="card-title">卖家信息</text>
        </view>
        <view class="seller-card" @click="toSellerHome">
          <image :src="carInfo.seller.avatar || '/static/default-avatar.png'" mode="aspectFill" class="seller-avatar-lg"></image>
          <view class="seller-info-right">
            <view class="seller-line">
              <text class="seller-name-lg">{{ carInfo.seller.name }}</text>
              <text class="seller-credit">信用评级 A</text>
            </view>
            <text class="seller-shop">{{ carInfo.seller.shopName || '—' }}</text>
          </view>
          <view class="seller-arrow">
            <u-icon name="arrow-right" size="20" color="#CBD5E1"></u-icon>
          </view>
        </view>

        <view class="seller-stats">
          <view class="stat">
            <text class="stat-num">{{ carInfo.seller.carCount || 0 }}</text>
            <text class="stat-lab">在售车源</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat">
            <text class="stat-num">{{ carInfo.seller.dealCount || 0 }}</text>
            <text class="stat-lab">成交</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat">
            <text class="stat-num">{{ carInfo.seller.followerCount || 0 }}</text>
            <text class="stat-lab">粉丝</text>
          </view>
        </view>

        <view class="seller-actions">
          <view class="btn-outline" @click="followSeller">
            <text>{{ carInfo.seller.followedByCurrentUser ? '已关注' : '+ 关注' }}</text>
          </view>
          <view class="btn-primary-s" @click="toSellerHome">
            <text>进店查看</text>
          </view>
        </view>
      </view>

      <!-- 车辆实拍图片 -->
      <view class="card-secondary animate-in" style="animation-delay: 0.28s" v-if="imageList.length">
        <view class="card-header">
          <text class="card-title">车辆实拍</text>
          <text class="card-hint">{{ imageList.length }} 张</text>
        </view>
        <view class="photo-grid">
          <view class="photo-cell" v-for="(img, idx) in imageList" :key="idx" @click="previewImage(idx)">
            <image :src="img.image" mode="aspectFill" class="photo-img"></image>
          </view>
        </view>
        <view class="download-row" @click="downloadImages">
          <u-icon name="download" size="22" color="#0369A1"></u-icon>
          <text class="download-text">打包下载图片</text>
        </view>
      </view>

      <view class="page-bottom-space"></view>
    </view>

    <!-- 底部操作栏 -->
    <view class="action-bar">
      <view class="ab-col" @click="toggleFavorite">
        <view class="ab-icon">
          <u-icon :name="carInfo.favorited ? 'heart-fill' : 'heart'" :color="carInfo.favorited ? '#DC2626' : '#64748B'" size="40"></u-icon>
        </view>
        <text class="ab-label">{{ carInfo.favorited ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="ab-col" @click="contact">
        <view class="ab-icon">
          <u-icon name="chat" size="40" color="#64748B"></u-icon>
        </view>
        <text class="ab-label">咨询</text>
      </view>
      <view class="ab-btn-wrapped" @click="payDeposit">
        <text class="ab-btn-label">付保证金</text>
        <text class="ab-btn-sub">可议价 · 可担保</text>
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
          <u-input v-model="reportForm.description" type="textarea" :height="120" :maxlength="200" placeholder="请详细描述问题..." border="surround" />
        </view>
        <view class="report-footer">
          <view class="btn-primary" @click="submitReport">提交举报</view>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getCarDetail, favoriteCar, unfavoriteCar, contactSeller } from '@/api/car'
import { followUser, unfollowUser } from '@/api/follow'
import { requireAuth } from '@/utils/auth'

export default {
  components: {
    BackNavBar: () => import('@/components/back-navbar.vue')
  },
  data() {
    return {
      carInfo: {
        id: '', carId: '', title: '', images: [], price: 0, deposit: 0,
        hasDeposit: false, exportCountries: [], year: '', mileage: 0, color: '',
        interiorColor: '', emissionStandard: '', displacement: '', gearbox: '',
        manufacturer: '', vehicleType: '', guidePrice: 0, usageType: '',
        insuranceExpire: '', location: '', updateTime: '', description: '',
        aiAnalysis: '', coverImage: '', brandName: '', seriesName: '', modelName: '',
        city: '', energyType: '', ownerType: '', isMortgaged: false, registrationDate: '',
        inspectionExpiry: '', transferCount: 0, abnormalPhotos: [],
        seller: { id: '', name: '', shopName: '', avatar: '', creditGrade: '', lastOnline: '', carCount: 0, dealCount: 0, followerCount: 0, followedByCurrentUser: false }
      },
      imageList: [],
      currentSwiper: 0,
      flagMap: { RU: '🇷🇺', KZ: '🇰🇿', AE: '🇦🇪', AU: '🇦🇺' },
      reportDialogVisible: false,
      reportForm: { reason: '', description: '' },
      reportReasons: ['价格虚假', '车辆信息不实', '图片与实际不符', '重复发布', '涉嫌欺诈', '其他']
    }
  },
  onLoad(options) {
    if (options.id) this.fetchCarDetail(options.id)
    else if (options.carId) this.fetchCarDetail(options.carId)
  },
  onShareAppMessage() {
    return { title: this.carInfo.title, path: `/pages/car-detail/index?id=${this.carInfo.id}`, imageUrl: this.carInfo.coverImage }
  },
  onShareTimeline() {
    return { title: this.carInfo.title, query: `id=${this.carInfo.id}`, imageUrl: this.carInfo.coverImage }
  },
  methods: {
    formatPrice(v) {
      if (!v) return '0'
      const num = Number(v)
      if (num >= 10000) return (num / 10000).toFixed(2).replace(/\.0+$/, '') + '万'
      return String(num).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },
    formatDateTime(dt) {
      if (!dt) return ''
      const d = new Date(dt)
      const y = d.getFullYear(), m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    },
    exportFlag(code) { return this.flagMap[code] || code },
    async fetchCarDetail(id) {
      try {
        const res = await getCarDetail(id)
        const data = res.data || res
        this.carInfo = Object.assign({}, this.carInfo, {
          id: data.id || '', carId: data.id || '', title: data.title || '车源详情',
          images: data.images || [], price: data.price || 0,
          hasDeposit: (data.deposit && data.deposit > 0) || false,
          exportCountries: data.tags || [], year: data.year || '',
          mileage: data.mileage || 0, color: data.color || '',
          emissionStandard: data.emissionStandard || '', displacement: data.displacement || '',
          gearbox: data.transmission || '', manufacturer: data.brandName || '',
          vehicleType: data.vehicleType || '', guidePrice: data.guidePrice || 0,
          usageType: data.usageType || '', insuranceExpire: data.insuranceExpiry || '',
          location: data.city || '', createTime: data.createdAt || '',
          updateTime: data.updatedAt || '', description: data.description || '',
          aiAnalysis: data.overallCondition || '暂无AI分析', coverImage: data.coverImage || '',
          brandName: data.brandName || '', city: data.city || '',
          seller: {
            id: data.sellerId || '', name: data.sellerName || '匿名卖家',
            shopName: data.sellerShopName || '', avatar: data.sellerAvatar || '',
            creditGrade: data.sellerCreditGrade || 'A', lastOnline: '',
            carCount: data.sellerCarCount || 0, dealCount: data.sellerDealCount || 0,
            followerCount: data.sellerFollowerCount || 0, followedByCurrentUser: data.followedByCurrentUser || false
          }
        })
        this.imageList = this.carInfo.images && this.carInfo.images.length > 0
          ? this.carInfo.images.map(img => typeof img === 'string' ? { image: img } : { image: img.url || img })
          : (this.carInfo.coverImage ? [{ image: this.carInfo.coverImage }] : [])
      } catch (e) {
        console.error('获取车源详情失败', e)
        uni.$u.toast('加载失败，请重试')
      }
    },
    onSwiperChange(e) { this.currentSwiper = e.detail.current },
    previewImage(idx) {
      const urls = this.imageList.map(img => img.image)
      uni.previewImage({ urls, current: idx || 0 })
    },
    shareCar() {
      if (!requireAuth()) return
      uni.showShareMenu({ withShareTicket: true, menus: ['shareAppMessage', 'shareTimeline'] })
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
      } catch (e) { console.error(e) }
    },
    async contact() {
      if (!requireAuth()) return
      try {
        await contactSeller(this.carInfo.id)
        uni.navigateTo({ url: `/pages/message/index?targetId=${this.carInfo.seller.id}` })
      } catch (e) { console.error(e) }
    },
    payDeposit() {
      if (!requireAuth()) return
      uni.navigateTo({ url: `/pages/order-detail/index?carId=${this.carInfo.id}` })
    },
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
      } catch (e) { console.error(e) }
    },
    toSellerHome() {
      uni.navigateTo({ url: `/pages/seller-home/index?id=${this.carInfo.seller.id}` })
    },
    viewReport(type) {
      if (!requireAuth()) return
      uni.$u.toast('报告功能开发中')
    },
    async downloadImages() {
      if (!requireAuth()) return
      uni.$u.toast('图片下载中')
    },
    showReportDialog() { this.reportDialogVisible = true },
    closeReportDialog() {
      this.reportDialogVisible = false
      this.reportForm = { reason: '', description: '' }
    },
    selectReportReason(reason) { this.reportForm.reason = reason },
    async submitReport() {
      if (!this.reportForm.reason) { uni.$u.toast('请选择举报原因'); return }
      try {
        uni.showLoading({ title: '提交中...' })
        await new Promise(r => setTimeout(r, 800))
        uni.hideLoading()
        uni.$u.toast('举报已提交')
        this.closeReportDialog()
      } catch (e) { uni.hideLoading() }
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F8FAFC;
}

/* Hero 区域 - 玻璃态导航 + 图片轮播 */
.page-hero {
  position: relative;
  background: linear-gradient(180deg, #FFFFFF 0%, #F8FAFC 100%);
}

.hero-visual {
  position: relative;
  width: 100%;
  height: 560rpx;
  overflow: hidden;
}

.visual-swiper {
  width: 100%;
  height: 100%;
}

.visual-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.visual-indicator {
  position: absolute;
  top: 16rpx;
  right: 20rpx;
  z-index: 10;
  background: rgba(15, 23, 42, 0.65);
  backdrop-filter: blur(12rpx);
  color: #fff;
  font-size: 22rpx;
  font-weight: 500;
  padding: 8rpx 18rpx;
  border-radius: 100rpx;
  letter-spacing: 0;
}

.visual-gradient {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 120rpx;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0) 0%, #F8FAFC 100%);
  pointer-events: none;
}

.hero-visual.placeholder {
  background: #E2E8F0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-text {
  color: #94A3B8;
  font-size: 26rpx;
}

.page-body {
  padding: 0 24rpx;
  margin-top: -40rpx;
  position: relative;
  z-index: 2;
  padding-bottom: 220rpx;
}

/* 主卡片 */
.card-primary {
  background: #fff;
  border-radius: 24rpx;
  padding: 36rpx 32rpx 32rpx;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06), 0 2rpx 6rpx rgba(15, 23, 42, 0.03);
  border: 1rpx solid rgba(226, 232, 240, 0.6);
  margin-bottom: 20rpx;
}

.tag-row {
  display: flex;
  gap: 12rpx;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 20rpx;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  font-weight: 500;
  letter-spacing: 0;
}

.badge-navy {
  background: #0F172A;
  color: #fff;
}

.badge-gold {
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  color: #92400E;
}

.flag-item {
  font-size: 26rpx;
  padding: 4rpx 8rpx;
}

.title-row {
  margin-bottom: 12rpx;
}

.car-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #0F172A;
  line-height: 1.25;
  letter-spacing: -0.5rpx;
}

.meta-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 24rpx;
  padding-bottom: 28rpx;
  border-bottom: 1rpx solid #F1F5F9;
}

.meta-item {
  font-size: 24rpx;
  color: #64748B;
  font-weight: 400;
}

.meta-dot {
  color: #CBD5E1;
  margin: 0 10rpx;
  font-size: 22rpx;
}

.price-container {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: 20rpx;
}

.price-main {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 28rpx;
  color: #DC2626;
  font-weight: 600;
  margin-right: 4rpx;
}

.price-value {
  font-size: 56rpx;
  font-weight: 700;
  color: #DC2626;
  letter-spacing: -1rpx;
  line-height: 1;
}

.price-detail {
  text-align: right;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6rpx;
}

.price-ref {
  font-size: 22rpx;
  color: #94A3B8;
  text-decoration: line-through;
}

.price-hint {
  font-size: 22rpx;
  color: #64748B;
  background: #F8FAFC;
  padding: 6rpx 12rpx;
  border-radius: 6rpx;
}

/* 金融方案 */
.finance-banner {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 24rpx;
  padding: 20rpx;
  background: linear-gradient(135deg, #FFFBEB 0%, #FEF3C7 100%);
  border-radius: 16rpx;
  border: 1rpx solid rgba(245, 158, 11, 0.18);
}

.finance-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: rgba(245, 158, 11, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.finance-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.finance-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #92400E;
}

.finance-desc {
  font-size: 22rpx;
  color: #B45309;
}

/* AI 卡片 */
.card-ai {
  background: linear-gradient(135deg, #FAF5FF 0%, #F3E8FF 100%);
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(168, 85, 247, 0.08);
  border: 1rpx solid rgba(168, 85, 247, 0.12);
  margin-bottom: 20rpx;
}

.ai-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.ai-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 14rpx;
  background: linear-gradient(135deg, #8B5CF6 0%, #6366F1 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(139, 92, 246, 0.3);
}

.ai-logo {
  color: #fff;
  font-size: 22rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
}

.ai-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.ai-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #0F172A;
}

.ai-subtitle {
  font-size: 22rpx;
  color: #64748B;
}

.ai-badge {
  font-size: 20rpx;
  color: #7C3AED;
  background: rgba(139, 92, 246, 0.12);
  padding: 6rpx 14rpx;
  border-radius: 8rpx;
  font-weight: 500;
}

.ai-divider {
  height: 1rpx;
  background: rgba(139, 92, 246, 0.15);
  margin: 20rpx 0 16rpx;
}

.ai-body {
  font-size: 26rpx;
  color: #334155;
  line-height: 1.75;
}

/* 普通卡片 */
.card-secondary {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 2rpx 12rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.6);
  margin-bottom: 20rpx;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
  letter-spacing: -0.2rpx;
}

.card-hint {
  font-size: 22rpx;
  color: #94A3B8;
}

/* 参数网格 */
.param-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0;
}

.param-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 8rpx;
  text-align: center;
  background: #FAFBFC;
  border-radius: 12rpx;
  margin: 8rpx;
}

.param-v {
  font-size: 26rpx;
  font-weight: 600;
  color: #0F172A;
  margin-bottom: 8rpx;
}

.param-l {
  font-size: 20rpx;
  color: #64748B;
}

.desc-text {
  font-size: 26rpx;
  color: #334155;
  line-height: 1.8;
}

/* 检测报告 */
.report-list {
  display: flex;
  flex-direction: column;
}

.report-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F1F5F9;
  cursor: pointer;
  transition: background 0.2s ease;
  border-radius: 12rpx;
}
.report-row:last-child { border-bottom: none; }
.report-row:active { background: #F8FAFC; }

.report-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.report-icon {
  width: 68rpx;
  height: 68rpx;
  border-radius: 14rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 700;
  color: #fff;
}

.report-chaboshi { background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%); }
.report-lemon { background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%); }
.report-service { background: linear-gradient(135deg, #10B981 0%, #34D399 100%); }

.report-meta {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.report-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #0F172A;
}

.report-desc {
  font-size: 22rpx;
  color: #64748B;
}

.report-arrow { flex-shrink: 0; }

/* 卖家信息 */
.seller-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx;
  background: #FAFBFC;
  border-radius: 16rpx;
  cursor: pointer;
}

.seller-avatar-lg {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  border: 2rpx solid #E2E8F0;
  background: #E2E8F0;
  flex-shrink: 0;
}

.seller-info-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.seller-line {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.seller-name-lg {
  font-size: 30rpx;
  font-weight: 600;
  color: #0F172A;
}

.seller-credit {
  font-size: 20rpx;
  color: #16A34A;
  background: #DCFCE7;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  font-weight: 500;
}

.seller-shop {
  font-size: 24rpx;
  color: #64748B;
}

.seller-arrow { flex-shrink: 0; opacity: 0.7; }

.seller-stats {
  display: flex;
  align-items: center;
  margin: 20rpx 0;
  padding: 20rpx 0;
  background: #F8FAFC;
  border-radius: 12rpx;
}

.stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
}

.stat-num {
  font-size: 32rpx;
  font-weight: 700;
  color: #0F172A;
}

.stat-lab {
  font-size: 20rpx;
  color: #64748B;
}

.stat-divider {
  width: 1rpx;
  height: 44rpx;
  background: #E2E8F0;
}

.seller-actions {
  display: flex;
  gap: 16rpx;
}

.btn-outline {
  flex: 1;
  padding: 20rpx;
  border: 2rpx solid #CBD5E1;
  border-radius: 14rpx;
  text-align: center;
  font-size: 26rpx;
  color: #334155;
  font-weight: 500;
  transition: all 0.2s ease;
}
.btn-outline:active { background: #F1F5F9; border-color: #94A3B8; }

.btn-primary-s {
  flex: 1;
  padding: 20rpx;
  background: #0369A1;
  border-radius: 14rpx;
  text-align: center;
  font-size: 26rpx;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.25);
  transition: all 0.2s ease;
}
.btn-primary-s:active { transform: scale(0.98); box-shadow: 0 2rpx 8rpx rgba(3, 105, 161, 0.2); }

.btn-primary {
  padding: 24rpx;
  background: #0369A1;
  border-radius: 14rpx;
  text-align: center;
  font-size: 28rpx;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.25);
  cursor: pointer;
  transition: all 0.2s ease;
}
.btn-primary:active { transform: scale(0.98); }

/* 车辆实拍 */
.photo-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8rpx;
  margin-bottom: 20rpx;
}

.photo-cell {
  aspect-ratio: 1;
  border-radius: 12rpx;
  overflow: hidden;
  background: #E2E8F0;
  cursor: pointer;
  transition: opacity 0.2s ease;
}
.photo-cell:active { opacity: 0.85; }

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.download-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  padding: 20rpx;
  background: #F0F9FF;
  border-radius: 14rpx;
  cursor: pointer;
  transition: background 0.2s ease;
}
.download-row:active { background: #E0F2FE; }

.download-text {
  font-size: 26rpx;
  color: #0369A1;
  font-weight: 500;
}

.page-bottom-space {
  height: 40rpx;
}

/* 底部操作栏 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(24rpx) saturate(1.2);
  -webkit-backdrop-filter: blur(24rpx) saturate(1.2);
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  gap: 16rpx;
  box-shadow: 0 -4rpx 24rpx rgba(15, 23, 42, 0.08);
  z-index: 50;
}

.ab-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  width: 110rpx;
  flex-shrink: 0;
  cursor: pointer;
  transition: transform 0.2s ease;
}
.ab-col:active { transform: scale(0.94); }

.ab-icon {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ab-label {
  font-size: 22rpx;
  color: #334155;
  font-weight: 500;
}

.ab-btn-wrapped {
  flex: 1;
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  border-radius: 18rpx;
  padding: 20rpx 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  box-shadow: 0 6rpx 20rpx rgba(3, 105, 161, 0.3);
  cursor: pointer;
  transition: all 0.2s ease;
}
.ab-btn-wrapped:active { transform: scale(0.98); box-shadow: 0 2rpx 10rpx rgba(3, 105, 161, 0.25); }

.ab-btn-label {
  font-size: 30rpx;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0;
}

.ab-btn-sub {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: 0;
}

/* 入场动画 */
.animate-in {
  animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both;
  transform: translateY(20rpx);
  opacity: 0;
}

@keyframes fadeUp {
  to { transform: translateY(0); opacity: 1; }
}

/* 举报弹窗 */
.report-popup {
  width: 620rpx;
  padding: 32rpx;
  background: #fff;
  border-radius: 24rpx;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28rpx;
}

.report-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #0F172A;
}

.report-body {
  margin-bottom: 28rpx;
}

.report-label {
  font-size: 26rpx;
  color: #334155;
  display: block;
  margin-bottom: 16rpx;
  margin-top: 12rpx;
  font-weight: 500;
}

.report-reasons {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-bottom: 20rpx;
}

.reason-item {
  padding: 14rpx 22rpx;
  border: 2rpx solid #E2E8F0;
  border-radius: 12rpx;
  font-size: 24rpx;
  color: #334155;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #F8FAFC;
}

.reason-item.active {
  border-color: #0369A1;
  background: #F0F9FF;
  color: #0369A1;
  font-weight: 500;
}
</style>
