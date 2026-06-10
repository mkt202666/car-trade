<template>
	<view class="page">
		<u-navbar title="浏览记录" :border-bottom="false" :placeholder="true">
			<view slot="right" class="clear-btn" @click="clearAll" v-if="recordList.length > 0">
				<text>清空</text>
			</view>
		</u-navbar>
		<view class="list" v-if="recordList.length > 0">
			<view class="record-item" v-for="item in recordList" :key="item.id" @click="goDetail(item.carId)">
				<view class="item-left">
					<image :src="item.carImage || '/static/default-car.png'" mode="aspectFill" class="car-img"></image>
				</view>
				<view class="item-right">
					<text class="car-title">{{ item.carName || '车源 #' + item.carId }}</text>
					<text class="car-price" v-if="item.price">{{ formatPrice(item.price) }}</text>
					<view class="item-tags">
						<text class="tag" v-if="item.mileage">{{ formatMileage(item.mileage) }}</text>
						<text class="tag" v-if="item.city">{{ item.city }}</text>
					</view>
					<text class="record-time">{{ formatTime(item.createdAt) }}</text>
				</view>
			</view>
		</view>
		<u-loadmore v-if="recordList.length > 0" :status="loadStatus" @loadmore="loadMore"></u-loadmore>
		<view class="empty" v-if="recordList.length === 0 && !loading">
			<u-empty text="暂无浏览记录" mode="history"></u-empty>
		</view>
	</view>
</template>

<script>
import { formatPrice, formatMileage, formatTime } from '@/utils/format'
export default {
	data() {
		return { recordList: [], page: 1, pageSize: 20, loading: false, loadStatus: 'loadmore', hasMore: true }
	},
	onLoad() { this.fetchList(true) },
	onPullDownRefresh() { this.fetchList(true); uni.stopPullDownRefresh() },
	onReachBottom() { if (this.hasMore) this.loadMore() },
	methods: {
		formatPrice, formatMileage, formatTime,
		async fetchList(reset) {
			if (this.loading) return
			if (reset) { this.page = 1; this.hasMore = true }
			this.loading = true; this.loadStatus = 'loading'
			try {
				const res = await uni.$u.http.get('/users/me/browsing', { params: { page: this.page, size: this.pageSize } })
				const list = res.data.list || res.data.records || res.data || []
				this.recordList = reset || this.page === 1 ? list : this.recordList.concat(list)
				this.hasMore = list.length >= this.pageSize
				this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
			} catch (e) { console.error(e) } finally { this.loading = false }
		},
		loadMore() { if (this.hasMore) { this.page++; this.fetchList(false) } },
		goDetail(id) { uni.navigateTo({ url: '/pages/car-detail/index?id=' + id }) },
		async clearAll() {
			uni.showModal({
				title: '提示', content: '确定清空所有浏览记录？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await uni.$u.http.delete('/users/me/browsing')
							this.recordList = []
							uni.$u.toast('已清空')
						} catch (e) { console.error(e) }
					}
				}
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.page { background: #F8FAFC; min-height: 100vh; }
.clear-btn { padding: 10rpx 20rpx; font-size: 26rpx; color: #64748B; }
.list { padding: 20rpx 30rpx; }
.record-item { display: flex; background: #fff; border-radius: 16rpx; margin-bottom: 16rpx; overflow: hidden; }
.item-left { width: 200rpx; height: 160rpx; flex-shrink: 0; }
.car-img { width: 100%; height: 100%; }
.item-right { flex: 1; padding: 16rpx 20rpx; display: flex; flex-direction: column; gap: 8rpx; }
.car-title { font-size: 28rpx; color: #0F172A; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.car-price { font-size: 30rpx; color: #DC2626; font-weight: 700; }
.item-tags { display: flex; gap: 8rpx; }
.tag { font-size: 20rpx; color: #64748B; background: #F1F5F9; padding: 4rpx 12rpx; border-radius: 6rpx; }
.record-time { font-size: 22rpx; color: #94A3B8; margin-top: auto; }
.empty { padding-top: 200rpx; }
</style>
