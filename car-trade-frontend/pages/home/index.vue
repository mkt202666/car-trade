<template>
  <view>
    <u-navbar title="5D好车" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="toSearch">
        <u-icon name="search" size="36"></u-icon>
      </view>
    </u-navbar>
    <view class="page-content">
      <u-tabs :list="categories" :current="currentCategory" @change="switchCategory" :is-scroll="true" :duration="0.3" active-color="#3c9cff" :bold="false"></u-tabs>
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
import { formatPrice, formatMileage } from '@/utils/format'

export default {
  data() {
    return {
      categories: [
        { name: '全部' }, { name: '热门推荐' }, { name: '最新上架' }, { name: '价格最低' },
        { name: 'SUV' }, { name: '轿车' }, { name: 'MPV' }, { name: '跑车' }
      ],
      currentCategory: 0,
      carList: [],
      page: 1,
      pageSize: 10,
      loading: false,
      loadStatus: 'loadmore',
      hasMore: true
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
    formatMileage,
    async loadCars() {
      this.loading = true
      this.loadStatus = 'loading'
      try {
        const params = { page: this.page, pageSize: this.pageSize }
        if (this.currentCategory > 0) params.category = this.categories[this.currentCategory].name
        const res = await getCarList(params)
        const list = res.data.records || res.data.list || res.data || []
        this.carList = this.page === 1 ? list : this.carList.concat(list)
        this.hasMore = list.length >= this.pageSize
        this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
      } catch (e) {
        this.loadStatus = 'loadmore'
      }
      this.loading = false
    },
    switchCategory(index) {
      this.currentCategory = index
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
    toDetail(id) {
      uni.navigateTo({ url: '/pages/car-detail/index?id=' + id })
    },
    toSearch() {
      uni.navigateTo({ url: '/pages/ai/index?mode=search' })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar-right { padding-right: 20rpx; }
.page-content { min-height: 100vh; background: #f5f5f5; }
.loading-wrap { display: flex; justify-content: center; padding: 100rpx 0; }
.car-list { padding: 20rpx 30rpx; }
.car-card {
  background: #fff; border-radius: 16rpx; margin-bottom: 20rpx; overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.car-image { width: 100%; height: 360rpx; }
.car-info { padding: 20rpx; }
.car-title { font-size: 30rpx; font-weight: 600; color: #333; }
.car-tags { margin-top: 16rpx; display: flex; gap: 12rpx; flex-wrap: wrap; }
.car-bottom { margin-top: 20rpx; display: flex; justify-content: space-between; align-items: center; }
.car-price { font-size: 36rpx; font-weight: 700; color: #f56c6c; }
.car-location { font-size: 24rpx; color: #999; display: flex; align-items: center; gap: 6rpx; }
</style>
