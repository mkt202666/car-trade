<template>
  <view class="page-container">
    <view class="trade-header">
      <text class="header-title">求购大厅</text>
      <view class="header-action" @click="toPublishPurchase">
        <text class="action-icon">✏️</text>
        <text class="action-text">发布求购</text>
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

    <view class="purchase-list">
      <view class="purchase-card" v-for="item in purchaseList" :key="item.id" @click="toDetail(item.id)">
        <view class="card-header">
          <view class="buyer-info">
            <image :src="item.buyerAvatar || '/static/default-avatar.png'" mode="aspectFill" class="buyer-avatar"></image>
            <view class="buyer-detail">
              <text class="buyer-name">{{ item.buyerName }}</text>
              <text class="buyer-location"><u-icon name="map-pin" size="18" color="#999"></u-icon>{{ item.city }}</text>
            </view>
          </view>
          <view class="urgent-tag" v-if="item.isUrgent">
            <text class="urgent-text">加急</text>
          </view>
        </view>
        
        <view class="car-info">
          <view class="car-title">{{ item.carBrand }} {{ item.carModel }} {{ item.carYear }}款</view>
          <view class="car-specs">
            <view class="spec-item">
              <text class="spec-label">排量</text>
              <text class="spec-value">{{ item.displacement }}</text>
            </view>
            <view class="spec-item">
              <text class="spec-label">里程</text>
              <text class="spec-value">{{ item.mileage }}万公里</text>
            </view>
            <view class="spec-item">
              <text class="spec-label">颜色</text>
              <text class="spec-value">{{ item.color }}</text>
            </view>
          </view>
        </view>

        <view class="price-section">
          <view class="price-range">
            <text class="price-label">意向价</text>
            <text class="price-value">{{ formatPrice(item.minPrice) }} - {{ formatPrice(item.maxPrice) }}</text>
          </view>
          <view class="price-tips">
            <text class="tips-text">{{ item.priceTips }}</text>
          </view>
        </view>

        <view class="card-footer">
          <text class="publish-time">{{ formatTime(item.publishTime) }}发布</text>
          <view class="contact-btn" @click.stop="contactBuyer(item)">
            <text class="contact-text">联系买家</text>
          </view>
        </view>
      </view>
    </view>

    <u-empty v-if="purchaseList.length === 0 && !loading" mode="search" text="暂无求购信息"></u-empty>
    <u-loadmore v-if="purchaseList.length > 0" :status="loadStatus" @loadmore="loadMore"></u-loadmore>
  </view>
</template>

<script>
import { formatPrice, formatTime } from '@/utils/format'

export default {
  data() {
    return {
      filterList: ['全部', '最新发布', '高价收车', '同城求购', '新能源'],
      currentFilter: 0,
      purchaseList: [],
      page: 1,
      pageSize: 10,
      loading: false,
      loadStatus: 'loadmore',
      hasMore: true
    }
  },
  onLoad() {
    this.loadData()
  },
  methods: {
    formatPrice,
    formatTime,
    async loadData() {
      this.loading = true
      this.loadStatus = 'loading'
      try {
        const mockData = [
          { id: 1, buyerName: '张老板', buyerAvatar: '', city: '上海', isUrgent: true, carBrand: '宝马', carModel: '3系', carYear: '2023', displacement: '2.0T', mileage: '3', color: '白色', minPrice: 280000, maxPrice: 320000, priceTips: '车况好可加价', publishTime: '2024-01-15 10:30' },
          { id: 2, buyerName: '李经理', buyerAvatar: '', city: '北京', isUrgent: false, carBrand: '奔驰', carModel: 'C级', carYear: '2022', displacement: '1.5T', mileage: '4.5', color: '黑色', minPrice: 250000, maxPrice: 280000, priceTips: '接受分期', publishTime: '2024-01-14 16:20' },
          { id: 3, buyerName: '王总', buyerAvatar: '', city: '广州', isUrgent: true, carBrand: '特斯拉', carModel: 'Model Y', carYear: '2023', displacement: '电动', mileage: '2', color: '蓝色', minPrice: 260000, maxPrice: 290000, priceTips: '现金秒提', publishTime: '2024-01-14 09:15' },
          { id: 4, buyerName: '陈老板', buyerAvatar: '', city: '深圳', isUrgent: false, carBrand: '奥迪', carModel: 'A4L', carYear: '2022', displacement: '2.0T', mileage: '5', color: '银色', minPrice: 220000, maxPrice: 250000, priceTips: '诚心收购', publishTime: '2024-01-13 14:45' },
          { id: 5, buyerName: '刘总', buyerAvatar: '', city: '成都', isUrgent: true, carBrand: '比亚迪', carModel: '汉EV', carYear: '2023', displacement: '电动', mileage: '1.5', color: '红色', minPrice: 200000, maxPrice: 230000, priceTips: '高价收', publishTime: '2024-01-13 11:00' }
        ]
        this.purchaseList = this.page === 1 ? mockData : this.purchaseList.concat(mockData)
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
      this.purchaseList = []
      this.loadData()
    },
    loadMore() {
      if (!this.hasMore) return
      this.page++
      this.loadData()
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/purchase-detail/index?id=' + id })
    },
    toPublishPurchase() {
      uni.navigateTo({ url: '/pages/purchase-demand/index' })
    },
    contactBuyer(item) {
      uni.navigateTo({ url: '/pages/customer-service/index?buyerId=' + item.id + '&buyerName=' + item.buyerName })
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

.trade-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 60rpx 30rpx 24rpx;
  background: linear-gradient(135deg, #10B981, #059669);
}

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
}

.header-action {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(255,255,255,0.2);
  padding: 12rpx 24rpx;
  border-radius: 30rpx;
}

.action-icon {
  font-size: 28rpx;
}

.action-text {
  font-size: 26rpx;
  color: #fff;
}

.filter-tabs {
  background: #fff;
  padding: 16rpx 0;
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
    background: #10B981;

    .filter-text {
      color: #fff;
    }
  }
}

.filter-text {
  font-size: 26rpx;
  color: #666;
}

.purchase-list {
  padding: 20rpx 30rpx;
}

.purchase-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.buyer-info {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.buyer-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: #f0f0f0;
}

.buyer-detail {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.buyer-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.buyer-location {
  font-size: 22rpx;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.urgent-tag {
  background: linear-gradient(135deg, #F59E0B, #D97706);
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

.urgent-text {
  font-size: 22rpx;
  color: #fff;
  font-weight: 600;
}

.car-info {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f5f5f5;
}

.car-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}

.car-specs {
  display: flex;
  gap: 30rpx;
  margin-top: 16rpx;
}

.spec-item {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.spec-label {
  font-size: 22rpx;
  color: #999;
}

.spec-value {
  font-size: 24rpx;
  color: #333;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20rpx;
  padding: 16rpx;
  background: #f8fafc;
  border-radius: 12rpx;
}

.price-range {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.price-label {
  font-size: 22rpx;
  color: #999;
}

.price-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #f56c6c;
}

.price-tips {
  background: #FEF3C7;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
}

.tips-text {
  font-size: 22rpx;
  color: #D97706;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f5f5f5;
}

.publish-time {
  font-size: 22rpx;
  color: #999;
}

.contact-btn {
  background: #10B981;
  padding: 12rpx 28rpx;
  border-radius: 30rpx;
}

.contact-text {
  font-size: 26rpx;
  color: #fff;
  font-weight: 600;
}
</style>