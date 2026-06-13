<template>
	<view class="page">
		<u-navbar title="我的收藏" :border-bottom="false" :placeholder="true"></u-navbar>
		<view class="list" v-if="carList.length > 0">
			<view class="car-card" v-for="car in carList" :key="car.id" @click="goDetail(car.carId || car.id)">
				<view class="card-left">
					<image :src="car.coverImage || car.carImage || '/static/default-car.png'" mode="aspectFill" class="car-img"></image>
				</view>
				<view class="card-right">
					<text class="car-title">{{ car.title || car.carName || '车源' }}</text>
					<text class="car-price">{{ formatPrice(car.price) }}</text>
					<view class="car-tags">
						<text class="tag" v-if="car.year">{{ car.year }}年</text>
						<text class="tag" v-if="car.mileage">{{ formatMileage(car.mileage) }}</text>
						<text class="tag" v-if="car.cityName">{{ car.cityName }}</text>
					</view>
				</view>
			</view>
		</view>
		<u-loadmore v-if="carList.length > 0" :status="loadStatus" @loadmore="loadMore"></u-loadmore>
		<view class="empty" v-if="carList.length === 0 && !loading">
			<u-empty text="暂无收藏车源" mode="favor"></u-empty>
		</view>
	</view>
</template>

<script>
import { formatPrice, formatMileage } from '@/utils/format'
export default {
	data() {
		return { carList: [], page: 1, pageSize: 10, loading: false, loadStatus: 'loadmore', hasMore: true }
	},
	onLoad() { this.fetchList(true) },
	onPullDownRefresh() { this.fetchList(true); uni.stopPullDownRefresh() },
	onReachBottom() { if (this.hasMore) this.loadMore() },
	methods: {
		formatPrice, formatMileage,
		async fetchList(reset) {
        if (this.loading) return
        if (reset) { this.page = 1; this.hasMore = true }
        this.loading = true; this.loadStatus = 'loading'
        try {
            const res = await uni.http.get('/cars/favorites', { params: { page: this.page, size: this.pageSize } })
				const list = res.data.list || res.data.records || res.data || []
				this.carList = reset || this.page === 1 ? list : this.carList.concat(list)
				this.hasMore = list.length >= this.pageSize
				this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
			} catch (e) { console.error(e) } finally { this.loading = false }
		},
		loadMore() { if (this.hasMore) { this.page++; this.fetchList(false) } },
		goDetail(id) { uni.navigateTo({ url: '/pages/car-detail/index?id=' + id }) }
	}
}
</script>

<style lang="scss" scoped>
.page { background: #F8FAFC; min-height: 100vh; }
.list { padding: 20rpx 30rpx; }
.car-card { display: flex; background: #fff; border-radius: 16rpx; margin-bottom: 20rpx; overflow: hidden; }
.card-left { width: 240rpx; height: 180rpx; flex-shrink: 0; }
.car-img { width: 100%; height: 100%; }
.card-right { flex: 1; padding: 16rpx 20rpx; display: flex; flex-direction: column; justify-content: space-between; }
.car-title { font-size: 28rpx; color: #0F172A; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.car-price { font-size: 32rpx; color: #DC2626; font-weight: 700; }
.car-tags { display: flex; gap: 8rpx; flex-wrap: wrap; }
.tag { font-size: 20rpx; color: #64748B; background: #F1F5F9; padding: 4rpx 12rpx; border-radius: 6rpx; }
.empty { padding-top: 200rpx; }
</style>
