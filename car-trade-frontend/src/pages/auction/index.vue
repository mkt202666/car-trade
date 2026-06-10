<template>
	<view class="page">
		<u-navbar title="在线拍卖" :border-bottom="false" :placeholder="true"></u-navbar>

		<!-- 状态筛选 -->
		<view class="tabs">
			<view class="tab-item" :class="{ active: currentTab === item.value }" v-for="item in tabs" :key="item.value" @click="switchTab(item.value)">
				<text>{{ item.label }}</text>
			</view>
		</view>

		<!-- 拍卖列表 -->
		<view class="auction-list">
			<view class="auction-card" v-for="item in auctionList" :key="item.id" @click="goDetail(item.id)">
				<view class="card-header">
					<text class="car-name">{{ item.carName || '车源 #' + item.carId }}</text>
					<view class="status-tag" :class="item.status">{{ statusText(item.status) }}</view>
				</view>
				<view class="card-body">
					<view class="info-row">
						<view class="info-item">
							<text class="label">起拍价</text>
							<text class="value">{{ formatPrice(item.startPrice) }}</text>
						</view>
						<view class="info-item">
							<text class="label">当前价</text>
							<text class="value price-highlight">{{ formatPrice(item.currentPrice) }}</text>
						</view>
						<view class="info-item">
							<text class="label">出价次数</text>
							<text class="value">{{ item.totalBids || 0 }}次</text>
						</view>
					</view>
					<view class="time-row">
						<text class="time-label">{{ item.status === 'BIDDING' ? '距结束' : '开始时间' }}</text>
						<text class="time-value">{{ item.status === 'BIDDING' ? formatEndTime(item.endTime) : formatTime(item.startTime) }}</text>
					</view>
				</view>
				<view class="card-footer">
					<view class="seller-info">
						<text class="seller-name">{{ item.sellerName || '卖家' }}</text>
						<text class="city" v-if="item.city">{{ item.city }}</text>
					</view>
					<view class="bid-btn" v-if="item.status === 'BIDDING' && item.canBid" @click.stop="goDetail(item.id)">
						立即出价
					</view>
				</view>
			</view>
		</view>

		<!-- 加载更多 -->
		<u-loadmore v-if="auctionList.length > 0" :status="loadStatus" @loadmore="loadMore"></u-loadmore>

		<!-- 空状态 -->
		<view class="empty" v-if="auctionList.length === 0 && !loading">
			<u-empty text="暂无拍卖" mode="list"></u-empty>
		</view>
	</view>
</template>

<script>
import { getAuctionList } from '@/api/auction'
import { formatPrice, formatTime } from '@/utils/format'

export default {
	data() {
		return {
			tabs: [
				{ label: '全部', value: '' },
				{ label: '竞拍中', value: 'BIDDING' },
				{ label: '即将开始', value: 'PENDING' },
				{ label: '已结束', value: 'ENDED' }
			],
			currentTab: '',
			auctionList: [],
			page: 1,
			pageSize: 10,
			loading: false,
			loadStatus: 'loadmore',
			hasMore: true
		}
	},
	onLoad() {
		this.fetchList(true)
	},
	onPullDownRefresh() {
		this.fetchList(true)
		uni.stopPullDownRefresh()
	},
	onReachBottom() {
		if (this.hasMore) this.loadMore()
	},
	methods: {
		formatPrice,
		formatTime,
		switchTab(value) {
			this.currentTab = value
			this.fetchList(true)
		},
		async fetchList(reset) {
			if (this.loading) return
			if (reset) { this.page = 1; this.hasMore = true }
			this.loading = true
			this.loadStatus = 'loading'
			try {
				const params = { page: this.page, size: this.pageSize }
				if (this.currentTab) params.status = this.currentTab
				const res = await getAuctionList(params)
				const list = res.data.list || res.data.records || res.data || []
				if (reset || this.page === 1) {
					this.auctionList = list
				} else {
					this.auctionList = this.auctionList.concat(list)
				}
				this.hasMore = list.length >= this.pageSize
				this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
			} catch (e) {
				console.error(e)
			} finally {
				this.loading = false
			}
		},
		loadMore() {
			if (!this.hasMore) return
			this.page++
			this.fetchList(false)
		},
		goDetail(id) {
			uni.navigateTo({ url: '/pages/auction-detail/index?id=' + id })
		},
		statusText(status) {
			const map = { PENDING: '待开始', BIDDING: '竞拍中', ENDED: '已结束', SETTLED: '已结算', CANCELLED: '已取消', FAILED: '流拍' }
			return map[status] || status
		},
		formatEndTime(endTime) {
			if (!endTime) return '--'
			const end = new Date(endTime).getTime()
			const now = Date.now()
			const diff = end - now
			if (diff <= 0) return '已结束'
			const hours = Math.floor(diff / 3600000)
			const mins = Math.floor((diff % 3600000) / 60000)
			if (hours > 24) return Math.floor(hours / 24) + '天' + (hours % 24) + '时'
			return hours + '时' + mins + '分'
		}
	}
}
</script>

<style lang="scss" scoped>
.page { background: #F8FAFC; min-height: 100vh; }
.tabs { display: flex; background: #fff; padding: 20rpx 30rpx; gap: 20rpx; }
.tab-item { padding: 12rpx 28rpx; border-radius: 30rpx; font-size: 26rpx; color: #64748B; background: #F1F5F9; }
.tab-item.active { background: #0369A1; color: #fff; }
.auction-list { padding: 20rpx 30rpx; }
.auction-card { background: #fff; border-radius: 16rpx; margin-bottom: 24rpx; overflow: hidden; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05); }
.card-header { display: flex; justify-content: space-between; align-items: center; padding: 24rpx 24rpx 0; }
.car-name { font-size: 30rpx; font-weight: 600; color: #0F172A; flex: 1; }
.status-tag { font-size: 22rpx; padding: 6rpx 16rpx; border-radius: 8rpx; }
.status-tag.BIDDING { background: #DCFCE7; color: #16A34A; }
.status-tag.PENDING { background: #FEF9C3; color: #CA8A04; }
.status-tag.ENDED, .status-tag.SETTLED { background: #F1F5F9; color: #64748B; }
.status-tag.FAILED { background: #FEE2E2; color: #DC2626; }
.card-body { padding: 20rpx 24rpx; }
.info-row { display: flex; justify-content: space-between; }
.info-item { display: flex; flex-direction: column; }
.label { font-size: 22rpx; color: #94A3B8; margin-bottom: 6rpx; }
.value { font-size: 28rpx; color: #0F172A; font-weight: 500; }
.price-highlight { color: #DC2626; font-size: 32rpx; font-weight: 700; }
.time-row { display: flex; justify-content: space-between; margin-top: 16rpx; padding-top: 16rpx; border-top: 1rpx solid #F1F5F9; }
.time-label { font-size: 22rpx; color: #94A3B8; }
.time-value { font-size: 24rpx; color: #0369A1; }
.card-footer { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 24rpx 24rpx; }
.seller-info { display: flex; align-items: center; gap: 12rpx; }
.seller-name { font-size: 24rpx; color: #64748B; }
.city { font-size: 22rpx; color: #94A3B8; }
.bid-btn { background: #0369A1; color: #fff; font-size: 24rpx; padding: 12rpx 32rpx; border-radius: 8rpx; }
.empty { padding-top: 200rpx; }
</style>
