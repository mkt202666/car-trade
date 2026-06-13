<template>
  <view class="page">
    <u-navbar title="全网找车" :border-bottom="false" :placeholder="true">
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
            placeholder="输入品牌、车型、价格等条件"
            class="search-input"
            @confirm="doSearch"
          />
          <view class="search-btn" @click="doSearch">
            <text>搜索</text>
          </view>
        </view>

        <!-- 筛选条件 -->
        <view class="filter-tags">
          <scroll-view scroll-x class="filter-scroll">
            <view class="filter-list">
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
          </scroll-view>
        </view>
      </view>

      <!-- 搜索结果 -->
      <view class="result-section">
        <view class="result-header">
          <text class="result-count">共找到 {{ totalCount }} 辆符合条件的车源</text>
          <view class="sort-btn" @click="showSortPicker = true">
            <text>{{ currentSort }}</text>
            <u-icon name="arrow-down" size="20" color="#666"></u-icon>
          </view>
        </view>

        <!-- 车源列表 -->
        <view class="car-list" v-if="carList.length > 0">
          <view class="car-item" v-for="car in carList" :key="car.id" @click="viewCarDetail(car)">
            <image :src="car.image" mode="aspectFill" class="car-image"></image>
            <view class="car-info">
              <text class="car-name">{{ car.title }}</text>
              <view class="car-tags">
                <text class="tag">{{ car.year }}年</text>
                <text class="tag">{{ car.mileage }}万公里</text>
                <text class="tag">{{ car.location }}</text>
              </view>
              <view class="car-price-row">
                <text class="car-price">¥{{ formatPrice(car.price) }}</text>
                <view class="car-source">
                  <text class="source-tag">{{ car.source }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 加载更多 -->
        <view class="load-more" v-if="hasMore" @click="loadMore">
          <text>加载更多</text>
        </view>

        <!-- 空状态 -->
        <u-empty v-if="!loading && carList.length === 0" mode="list" text="暂无搜索结果"></u-empty>

        <!-- 加载中 -->
        <view class="loading" v-if="loading">
          <view class="custom-spinner"></view>
          <text class="loading-text">正在搜索全网车源...</text>
        </view>
      </view>
    </view>

    <!-- 排序选择器 -->
    <u-picker
      mode="selector"
      v-model="showSortPicker"
      :range="sortOptions"
      @confirm="onSortConfirm"
    ></u-picker>
  </view>
</template>

<script>
import { searchAllCars } from '@/api/ai'
import { formatPrice } from '@/utils/format'

export default {
  data() {
    return {
      searchKeyword: '',
      activeFilter: 'all',
      carList: [],
      totalCount: 0,
      loading: false,
      hasMore: true,
      page: 1,
      pageSize: 20,
      currentSort: '综合排序',
      showSortPicker: false,
      sortOptions: ['综合排序', '价格从低到高', '价格从高到低', '车龄从新到旧', '里程从少到多']
    }
  },
  onLoad(options) {
    if (options.keyword) {
      this.searchKeyword = options.keyword
      this.doSearch()
    }
  },
  methods: {
    formatPrice,
    setFilter(filter) {
      this.activeFilter = filter
      this.page = 1
      this.carList = []
      this.doSearch()
    },
    async doSearch() {
      if (!this.searchKeyword.trim()) {
        uni.$u.toast('请输入搜索条件')
        return
      }

      this.loading = true
      this.page = 1
      this.carList = []

      try {
        const res = await searchAllCars({
          keyword: this.searchKeyword,
          source: this.activeFilter,
          page: this.page,
          pageSize: this.pageSize,
          sort: this.currentSort
        })

        if (res.data) {
          this.carList = res.data.list || []
          this.totalCount = res.data.total || 0
          this.hasMore = this.carList.length < this.totalCount
        }
      } catch (e) {
        console.error('搜索失败', e)
        uni.$u.toast('搜索失败，请重试')
      } finally {
        this.loading = false
      }
    },
    async loadMore() {
      if (this.loading || !this.hasMore) return

      this.page++
      this.loading = true

      try {
        const res = await searchAllCars({
          keyword: this.searchKeyword,
          source: this.activeFilter,
          page: this.page,
          pageSize: this.pageSize,
          sort: this.currentSort
        })

        if (res.data && res.data.list) {
          this.carList = [...this.carList, ...res.data.list]
          this.hasMore = this.carList.length < this.totalCount
        }
      } catch (e) {
        console.error('加载更多失败', e)
        this.page--
      } finally {
        this.loading = false
      }
    },
    onSortConfirm(e) {
      const index = e[0]
      this.currentSort = this.sortOptions[index]
      this.page = 1
      this.carList = []
      this.doSearch()
    },
    viewCarDetail(car) {
      // 如果是外部链接，使用web-view打开
      if (car.url) {
        uni.navigateTo({
          url: `/pages/webview/index?url=${encodeURIComponent(car.url)}`
        })
      } else {
        uni.navigateTo({
          url: `/pages/car-detail/index?id=${car.id}`
        })
      }
    },
    goBack() {
      uni.navigateBack()
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

.page {
  min-height: 100vh;
  background: $bg-color;
}

.page-content {
  padding-bottom: 40rpx;
}

/* 搜索区域 */
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
  margin-bottom: 20rpx;
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
  margin-top: 16rpx;
}

.filter-scroll {
  white-space: nowrap;
}

.filter-list {
  display: inline-flex;
  gap: 16rpx;
}

.filter-tag {
  display: inline-flex;
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

/* 搜索结果 */
.result-section {
  background: #fff;
  padding: 20rpx;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.result-count {
  font-size: 26rpx;
  color: $text-secondary;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: $text-secondary;
  cursor: pointer;
}

/* 车源列表 */
.car-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.car-item {
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
  border-radius: 8rpx;
  margin-right: 20rpx;
}

.car-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.car-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
  margin-bottom: 12rpx;
}

.car-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 12rpx;
}

.tag {
  font-size: 22rpx;
  color: $text-secondary;
  background: #fff;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.car-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.car-price {
  font-size: 32rpx;
  font-weight: 700;
  color: #f56c6c;
}

.car-source {
  display: flex;
  align-items: center;
}

.source-tag {
  font-size: 20rpx;
  color: $cta-color;
  background: rgba(3, 105, 161, 0.1);
  padding: 6rpx 12rpx;
  border-radius: 4rpx;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 30rpx;
  font-size: 28rpx;
  color: $cta-color;
  cursor: pointer;

  &:active {
    opacity: 0.7;
  }
}

/* 加载中 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
}
.custom-spinner {
  width: 64rpx;
  height: 64rpx;
  border: 6rpx solid #e5e7eb;
  border-top-color: #0369A1;
  border-radius: 50%;
  animation: aisc-spin 0.8s linear infinite;
}
@keyframes aisc-spin {
  to { transform: rotate(360deg); }
}
.loading-text {
  font-size: 28rpx;
  color: $text-secondary;
  margin-top: 20rpx;
}
</style>
