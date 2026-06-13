<template>
  <view class="page">
    <u-navbar title="我的车源" :border-bottom="false" :placeholder="true">
      <template v-slot:left>
        <view class="nav-back" @click="goBack">
          <u-icon name="arrow-left" size="32" color="#333"></u-icon>
        </view>
      </template>
    </u-navbar>

    <view class="page-content">
      <!-- 搜索区域 -->
      <view class="search-section">
        <view class="search-bar">
          <u-icon name="search" size="28" color="#999"></u-icon>
          <input
            v-model="searchKeyword"
            placeholder="搜索车源"
            class="search-input"
            @confirm="doSearch"
          />
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
            :class="{ active: activeFilter === 'gazi' }"
            @click="setFilter('gazi')"
          >
            <text>瓜子二手车</text>
          </view>
          <view
            class="filter-tag"
            :class="{ active: activeFilter === 'renren' }"
            @click="setFilter('renren')"
          >
            <text>人人车</text>
          </view>
          <view
            class="filter-tag"
            :class="{ active: activeFilter === 'che168' }"
            @click="setFilter('che168')"
          >
            <text>二手车之家</text>
          </view>
        </view>
      </view>

      <!-- 车源列表 -->
      <view class="car-list-section">
        <view class="list-header">
          <view class="status-tabs">
            <view
              class="status-tab"
              :class="{ active: activeTab === 'onSale' }"
              @click="setTab('onSale')"
            >
              <text>在售 ({{ onSaleCount }})</text>
            </view>
            <view
              class="status-tab"
              :class="{ active: activeTab === 'offSale' }"
              @click="setTab('offSale')"
            >
              <text>下架 ({{ offSaleCount }})</text>
            </view>
          </view>

          <view class="list-actions">
            <view class="refresh-btn" @click="refreshCarSource">
              <u-icon name="refresh" size="28" color="#2979ff"></u-icon>
              <text>刷新我的车源</text>
            </view>
            <view class="share-btn">
              <u-icon name="share" size="28" color="#2979ff"></u-icon>
              <text>分享</text>
            </view>
          </view>
        </view>

        <view class="car-list" v-if="carList.length > 0">
          <view class="car-item" v-for="car in carList" :key="car.id" @click="viewCarDetail(car)">
            <view class="car-image">
              <image :src="car.image" mode="aspectFill" class="image"></image>
              <view class="ai-badge" v-if="car.isAi">AI</view>
            </view>
            <view class="car-info">
              <text class="car-name">{{ car.title }}</text>
              <view class="car-meta">
                <text class="car-year">{{ car.year }}年</text>
                <text class="car-mileage">{{ car.mileage }}万公里</text>
                <text class="car-location">{{ car.location }}</text>
              </view>
              <text class="car-price">¥{{ formatPrice(car.price) }}</text>
              <view class="car-status">
                <text class="status-tag" :class="getStatusClass(car.status)">
                  {{ car.statusText }}
                </text>
                <text class="days-tag">{{ car.days }}天</text>
              </view>
            </view>
            <view class="car-actions">
              <view class="action-item" @click.stop="viewCarDetail(car)">
                <u-icon name="eye" size="24" color="#666"></u-icon>
                <text>{{ car.viewCount }}</text>
              </view>
              <view class="action-item" @click.stop="collectCar(car)">
                <u-icon name="heart" size="24" :color="car.isCollected ? '#ff4d4f' : '#666'"></u-icon>
                <text>{{ car.collectCount }}</text>
              </view>
              <view class="action-item" @click.stop="contactCar(car)">
                <u-icon name="chat" size="24" color="#666"></u-icon>
                <text>{{ car.contactCount }}</text>
              </view>
            </view>
            <view class="full-sync-btn" @click.stop="fullSync(car)">
              <u-icon name="sync" size="24" color="#2979ff"></u-icon>
              <text>全网同步</text>
            </view>
            <view class="edit-btn" @click.stop="editCar(car)">
              <u-icon name="edit" size="24" color="#2979ff"></u-icon>
              <text>编辑</text>
            </view>
          </view>
        </view>

        <u-empty v-else mode="car" text="暂无车源"></u-empty>

        <!-- 加载更多 -->
        <view class="load-more" v-if="hasMore" @click="loadMore">
          <text>加载更多</text>
        </view>
      </view>

      <!-- 排序栏 -->
      <view class="sort-bar">
        <view class="sort-item" :class="{ active: currentSort === 'date' }" @click="setSort('date')">
          <text>发车日期</text>
        </view>
        <view class="sort-item" :class="{ active: currentSort === 'price' }" @click="setSort('price')">
          <text>价格</text>
        </view>
        <view class="sort-item" :class="{ active: currentSort === 'year' }" @click="setSort('year')">
          <text>年份</text>
        </view>
      </view>

      <!-- 全选 -->
      <view class="select-all">
        <view class="checkbox" :class="{ checked: allSelected }" @click="toggleSelectAll">
          <u-icon v-if="allSelected" name="checked" size="24" color="#2979ff"></u-icon>
          <u-icon v-else name="circle" size="24" color="#999"></u-icon>
        </view>
        <text>全选</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyCarSource } from '@/api/car'

export default {
  data() {
    return {
      searchKeyword: '',
      activeFilter: 'all',
      activeTab: 'onSale',
      onSaleCount: 2,
      offSaleCount: 2,
      currentSort: 'date',
      allSelected: false,
      carList: [],
      hasMore: false,
      loading: false,
      page: 1,
      size: 10
    }
  },
  async onLoad() {
    await this.loadCarSource()
  },
  onShow() {
    // 刷新数据
    this.loadCarSource(true)
  },
  methods: {
    async loadCarSource(refresh = false) {
      if (refresh) {
        this.page = 1
        this.carList = []
      }

      if (this.loading) return

      this.loading = true
      try {
        const res = await getMyCarSource({
          keyword: this.searchKeyword,
          filter: this.activeFilter,
          status: this.activeTab === 'onSale' ? 'on_sale' : 'off_sale',
          sort: this.currentSort,
          page: this.page,
          size: this.size
        })

        if (this.page === 1) {
          this.carList = res.data.list || []
        } else {
          this.carList = [...this.carList, ...(res.data.list || [])]
        }

        this.hasMore = this.carList.length < res.data.total
      } catch (e) {
        console.error('加载车源失败', e)
      } finally {
        this.loading = false
      }
    },
    doSearch() {
      this.loadCarSource(true)
    },
    setFilter(filter) {
      this.activeFilter = filter
      this.loadCarSource(true)
    },
    setTab(tab) {
      this.activeTab = tab
      this.loadCarSource(true)
    },
    setSort(sort) {
      this.currentSort = sort
      this.loadCarSource(true)
    },
    refreshCarSource() {
      this.loadCarSource(true)
    },
    viewCarDetail(car) {
      uni.navigateTo({
        url: `/pages/car-detail/index?id=${car.id}`
      })
    },
    collectCar(car) {
      car.isCollected = !car.isCollected
      car.collectCount = car.isCollected ? car.collectCount + 1 : car.collectCount - 1
    },
    contactCar(car) {
      uni.navigateTo({
        url: `/pages/chat/index?toUser=${car.sellerId}`
      })
    },
    fullSync(car) {
      uni.showToast({
        title: '全网同步中...',
        icon: 'loading'
      })
      setTimeout(() => {
        uni.showToast({
          title: '同步完成',
          icon: 'success'
        })
      }, 1000)
    },
    editCar(car) {
      uni.navigateTo({
        url: `/pages/publish/index?mode=edit&id=${car.id}`
      })
    },
    loadMore() {
      if (!this.loading && this.hasMore) {
        this.page++
        this.loadCarSource()
      }
    },
    formatPrice(price) {
      return (price / 10000).toFixed(2)
    },
    getStatusClass(status) {
      return status === 'on_sale' ? 'status-on-sale' : 'status-off-sale'
    },
    goBack() {
      uni.navigateBack()
    },
    toggleSelectAll() {
      this.allSelected = !this.allSelected
    }
  }
}
</script>

<style>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.nav-back {
  padding: 10rpx;
}

.page-content {
  padding: 20rpx;
}

.search-section {
  margin-bottom: 20rpx;
}

.search-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.search-input {
  flex: 1;
  margin-left: 20rpx;
  font-size: 28rpx;
}

.filter-tags {
  display: flex;
  gap: 20rpx;
  padding: 0 30rpx;
  overflow-x: auto;
}

.filter-tag {
  padding: 12rpx 24rpx;
  background: #f0f0f0;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

.filter-tag.active {
  background: #e6f2ff;
  color: #2979ff;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.status-tabs {
  display: flex;
  gap: 20rpx;
}

.status-tab {
  padding: 12rpx 24rpx;
  background: #fff;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-tab.active {
  background: #e6f2ff;
  color: #2979ff;
}

.list-actions {
  display: flex;
  gap: 20rpx;
}

.refresh-btn,
.share-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  background: #fff;
  border-radius: 8rpx;
}

.car-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.car-item {
  padding: 20rpx;
  background: #fff;
  border-radius: 12rpx;
  position: relative;
}

.car-image {
  position: relative;
  width: 200rpx;
  height: 150rpx;
  margin-bottom: 20rpx;
}

.car-image .image {
  width: 100%;
  height: 100%;
  border-radius: 8rpx;
}

.ai-badge {
  position: absolute;
  top: 10rpx;
  right: 10rpx;
  padding: 4rpx 12rpx;
  background: #ff9900;
  color: #fff;
  border-radius: 4rpx;
  font-size: 20rpx;
}

.car-info {
  flex: 1;
}

.car-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 12rpx;
}

.car-meta {
  display: flex;
  gap: 20rpx;
  margin-bottom: 12rpx;
}

.car-year,
.car-mileage,
.car-location {
  font-size: 24rpx;
  color: #999;
}

.car-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #ff4d4f;
  margin-bottom: 12rpx;
}

.car-status {
  display: flex;
  gap: 10rpx;
  align-items: center;
}

.status-tag {
  padding: 4rpx 12rpx;
  border-radius: 4rpx;
  font-size: 22rpx;
}

.status-on-sale {
  background: #e6f7e6;
  color: #5ac725;
}

.status-off-sale {
  background: #f0f0f0;
  color: #999;
}

.days-tag {
  font-size: 22rpx;
  color: #999;
}

.car-actions {
  display: flex;
  gap: 30rpx;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

.full-sync-btn,
.edit-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
  background: #e6f2ff;
  border-radius: 8rpx;
  color: #2979ff;
  margin-left: auto;
}

.load-more {
  text-align: center;
  padding: 20rpx;
  color: #2979ff;
}

.sort-bar {
  display: flex;
  background: #fff;
  border-radius: 8rpx;
  overflow: hidden;
  margin: 20rpx 0;
}

.sort-item {
  flex: 1;
  text-align: center;
  padding: 16rpx;
  font-size: 24rpx;
  color: #666;
  border-right: 1rpx solid #f0f0f0;
}

.sort-item:last-child {
  border-right: none;
}

.sort-item.active {
  color: #2979ff;
  background: #e6f2ff;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 20rpx;
  background: #fff;
  border-radius: 8rpx;
  margin-top: 20rpx;
}
.checkbox {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.checkbox.checked {
  background: #e6f2ff;
  border-radius: 8rpx;
}
</style>
