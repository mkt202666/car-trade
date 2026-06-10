<template>
	<view class="page">
		<u-navbar title="拍卖详情" :border-bottom="false" :placeholder="true"></u-navbar>

		<view v-if="auction">
			<!-- 车辆图片 -->
			<swiper class="car-images" v-if="auction.carImages && auction.carImages.length" indicator-dots autoplay circular>
				<swiper-item v-for="(img, i) in auction.carImages" :key="i">
					<image :src="img" mode="aspectFill" class="car-img"></image>
				</swiper-item>
			</swiper>
			<view class="car-images placeholder" v-else>
				<text class="placeholder-text">暂无图片</text>
			</view>

			<!-- 状态栏 -->
			<view class="status-bar" :class="auction.status">
				<text class="status-text">{{ statusText(auction.status) }}</text>
				<text class="countdown" v-if="auction.status === 'BIDDING'">距结束: {{ countdown }}</text>
			</view>

			<!-- 价格信息 -->
			<view class="price-section">
				<view class="price-item">
					<text class="price-label">起拍价</text>
					<text class="price-value">{{ formatPrice(auction.startPrice) }}</text>
				</view>
				<view class="price-item main">
					<text class="price-label">当前最高价</text>
					<text class="price-value highlight">{{ formatPrice(auction.currentPrice) }}</text>
				</view>
				<view class="price-item">
					<text class="price-label">加价幅度</text>
					<text class="price-value">{{ formatPrice(auction.bidIncrement) }}</text>
				</view>
			</view>

			<!-- 车辆信息 -->
			<view class="section">
				<view class="section-title">车辆信息</view>
				<view class="info-grid">
					<view class="info-cell"><text class="cell-label">车辆</text><text class="cell-value">{{ auction.carName || '--' }}</text></view>
					<view class="info-cell"><text class="cell-label">年份</text><text class="cell-value">{{ auction.carYear || '--' }}</text></view>
					<view class="info-cell"><text class="cell-label">里程</text><text class="cell-value">{{ auction.carMileage ? auction.carMileage + '公里' : '--' }}</text></view>
					<view class="info-cell"><text class="cell-label">城市</text><text class="cell-value">{{ auction.city || '--' }}</text></view>
				</view>
			</view>

			<!-- 拍卖信息 -->
			<view class="section">
				<view class="section-title">拍卖信息</view>
				<view class="info-grid">
					<view class="info-cell"><text class="cell-label">出价次数</text><text class="cell-value">{{ auction.totalBids || 0 }}次</text></view>
					<view class="info-cell"><text class="cell-label">围观人数</text><text class="cell-value">{{ auction.viewCount || 0 }}</text></view>
					<view class="info-cell"><text class="cell-label">开始时间</text><text class="cell-value">{{ formatTime(auction.startTime) }}</text></view>
					<view class="info-cell"><text class="cell-label">结束时间</text><text class="cell-value">{{ formatTime(auction.endTime) }}</text></view>
				</view>
			</view>

			<!-- 卖家信息 -->
			<view class="section">
				<view class="section-title">卖家信息</view>
				<view class="seller-row">
					<image :src="auction.sellerAvatar || '/static/default-avatar.png'" class="seller-avatar"></image>
					<view class="seller-detail">
						<text class="seller-name">{{ auction.sellerName || '卖家' }}</text>
						<text class="seller-shop">{{ auction.sellerShopName || '' }}</text>
					</view>
				</view>
			</view>

			<!-- 最近出价记录 -->
			<view class="section" v-if="auction.recentBids && auction.recentBids.length">
				<view class="section-title">最近出价</view>
				<view class="bid-list">
					<view class="bid-item" v-for="bid in auction.recentBids" :key="bid.id">
						<view class="bid-user">
							<image :src="bid.bidderAvatar || '/static/default-avatar.png'" class="bid-avatar"></image>
							<text class="bid-name">{{ bid.bidderName || '匿名' }}</text>
							<text class="bid-winning" v-if="bid.isWinning">最高</text>
						</view>
						<text class="bid-price">{{ formatPrice(bid.bidPrice) }}</text>
					</view>
				</view>
			</view>

			<!-- 中标信息 -->
			<view class="section" v-if="auction.status === 'SETTLED'">
				<view class="winner-section">
					<text class="winner-title">🎉 拍卖已成交</text>
					<text class="winner-price">成交价: {{ formatPrice(auction.winningPrice) }}</text>
				</view>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar" v-if="auction">
			<view class="bar-left">
				<view class="watch-btn" @click="toggleWatch">
					<u-icon :name="auction.isWatching ? 'heart-fill' : 'heart'" :color="auction.isWatching ? '#DC2626' : '#64748B'" size="40"></u-icon>
					<text>{{ auction.isWatching ? '已关注' : '关注' }}</text>
				</view>
			</view>
			<view class="bar-right">
				<view class="bid-action" v-if="auction.canBid" @click="showBidPopup = true">
					<text>出价</text>
				</view>
				<view class="bid-action disabled" v-else-if="auction.status === 'BIDDING'">
					<text>不可出价</text>
				</view>
				<view class="bid-action ended" v-else>
					<text>{{ statusText(auction.status) }}</text>
				</view>
			</view>
		</view>

		<!-- 出价弹窗 -->
		<u-popup :show="showBidPopup" mode="bottom" round="24" @close="showBidPopup = false">
			<view class="bid-popup">
				<view class="popup-title">出价竞拍</view>
				<view class="popup-info">
					<text>当前最高价: {{ formatPrice(auction.currentPrice) }}</text>
					<text>最低出价: {{ formatPrice(minBidPrice) }}</text>
				</view>
				<view class="popup-input">
					<u-input v-model="bidPrice" type="number" placeholder="请输入出价金额" :border="'surround'"></u-input>
				</view>
				<view class="quick-bids">
					<view class="quick-btn" v-for="q in quickBids" :key="q" @click="bidPrice = String(q)">
						{{ formatPrice(q) }}
					</view>
				</view>
				<view class="popup-btn" @click="submitBid">确认出价</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
import { getAuctionDetail, placeBid, watchAuction, unwatchAuction } from '@/api/auction'
import { formatPrice, formatTime } from '@/utils/format'

export default {
	data() {
		return {
			auctionId: null,
			auction: null,
			showBidPopup: false,
			bidPrice: '',
			countdown: '',
			countdownTimer: null
		}
	},
	computed: {
		minBidPrice() {
			if (!this.auction) return 0
			return Number(this.auction.currentPrice) + Number(this.auction.bidIncrement)
		},
		quickBids() {
			if (!this.auction) return []
			const min = this.minBidPrice
			const inc = Number(this.auction.bidIncrement)
			return [min, min + inc, min + inc * 2]
		}
	},
	onLoad(options) {
		this.auctionId = options.id
		this.fetchDetail()
	},
	onUnload() {
		if (this.countdownTimer) clearInterval(this.countdownTimer)
	},
	methods: {
		formatPrice,
		formatTime,
		async fetchDetail() {
			try {
				const res = await getAuctionDetail(this.auctionId)
				this.auction = res.data
				if (this.auction.status === 'BIDDING') this.startCountdown()
			} catch (e) {
				console.error(e)
			}
		},
		startCountdown() {
			this.updateCountdown()
			this.countdownTimer = setInterval(() => this.updateCountdown(), 1000)
		},
		updateCountdown() {
			if (!this.auction || !this.auction.endTime) return
			const diff = new Date(this.auction.endTime).getTime() - Date.now()
			if (diff <= 0) { this.countdown = '已结束'; clearInterval(this.countdownTimer); return }
			const d = Math.floor(diff / 86400000)
			const h = Math.floor((diff % 86400000) / 3600000)
			const m = Math.floor((diff % 3600000) / 60000)
			const s = Math.floor((diff % 60000) / 1000)
			this.countdown = (d > 0 ? d + '天' : '') + h + '时' + m + '分' + s + '秒'
		},
		async toggleWatch() {
			try {
				if (this.auction.isWatching) {
					await unwatchAuction(this.auctionId)
					this.auction.isWatching = false
					uni.$u.toast('已取消关注')
				} else {
					await watchAuction(this.auctionId)
					this.auction.isWatching = true
					uni.$u.toast('已关注')
				}
			} catch (e) { console.error(e) }
		},
		async submitBid() {
			const price = Number(this.bidPrice)
			if (!price || price < this.minBidPrice) {
				uni.$u.toast('出价不能低于 ' + this.formatPrice(this.minBidPrice))
				return
			}
			try {
				await placeBid({ auctionId: Number(this.auctionId), bidPrice: price })
				uni.$u.toast('出价成功！')
				this.showBidPopup = false
				this.bidPrice = ''
				this.fetchDetail()
			} catch (e) { console.error(e) }
		},
		statusText(status) {
			const map = { PENDING: '待开始', BIDDING: '竞拍中', ENDED: '已结束', SETTLED: '已结算', CANCELLED: '已取消', FAILED: '流拍' }
			return map[status] || status
		}
	}
}
</script>

<style lang="scss" scoped>
.page { background: #F8FAFC; min-height: 100vh; padding-bottom: 140rpx; }
.car-images { width: 100%; height: 500rpx; }
.car-img { width: 100%; height: 100%; }
.car-images.placeholder { display: flex; align-items: center; justify-content: center; background: #E2E8F0; }
.placeholder-text { color: #94A3B8; font-size: 28rpx; }
.status-bar { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 30rpx; }
.status-bar.BIDDING { background: #DCFCE7; }
.status-bar.PENDING { background: #FEF9C3; }
.status-bar.ENDED, .status-bar.SETTLED { background: #F1F5F9; }
.status-text { font-size: 28rpx; font-weight: 600; color: #0F172A; }
.countdown { font-size: 26rpx; color: #DC2626; font-weight: 500; }
.price-section { display: flex; background: #fff; padding: 30rpx; margin: 20rpx 30rpx; border-radius: 16rpx; }
.price-item { flex: 1; display: flex; flex-direction: column; align-items: center; }
.price-item.main { border-left: 1rpx solid #F1F5F9; border-right: 1rpx solid #F1F5F9; }
.price-label { font-size: 22rpx; color: #94A3B8; margin-bottom: 8rpx; }
.price-value { font-size: 30rpx; font-weight: 600; color: #0F172A; }
.price-value.highlight { color: #DC2626; font-size: 36rpx; }
.section { background: #fff; margin: 20rpx 30rpx; border-radius: 16rpx; padding: 24rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: #0F172A; margin-bottom: 20rpx; }
.info-grid { display: flex; flex-wrap: wrap; }
.info-cell { width: 50%; display: flex; flex-direction: column; margin-bottom: 16rpx; }
.cell-label { font-size: 22rpx; color: #94A3B8; }
.cell-value { font-size: 26rpx; color: #0F172A; margin-top: 4rpx; }
.seller-row { display: flex; align-items: center; gap: 16rpx; }
.seller-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; }
.seller-detail { display: flex; flex-direction: column; }
.seller-name { font-size: 28rpx; font-weight: 500; color: #0F172A; }
.seller-shop { font-size: 22rpx; color: #94A3B8; }
.bid-list { display: flex; flex-direction: column; gap: 16rpx; }
.bid-item { display: flex; justify-content: space-between; align-items: center; padding: 12rpx 0; border-bottom: 1rpx solid #F1F5F9; }
.bid-user { display: flex; align-items: center; gap: 12rpx; }
.bid-avatar { width: 48rpx; height: 48rpx; border-radius: 50%; }
.bid-name { font-size: 24rpx; color: #0F172A; }
.bid-winning { font-size: 20rpx; color: #16A34A; background: #DCFCE7; padding: 2rpx 10rpx; border-radius: 6rpx; }
.bid-price { font-size: 28rpx; font-weight: 600; color: #DC2626; }
.winner-section { text-align: center; padding: 30rpx 0; }
.winner-title { font-size: 32rpx; font-weight: 600; display: block; margin-bottom: 12rpx; }
.winner-price { font-size: 28rpx; color: #DC2626; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; align-items: center; background: #fff; padding: 20rpx 30rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.05); }
.bar-left { margin-right: 30rpx; }
.watch-btn { display: flex; flex-direction: column; align-items: center; font-size: 20rpx; color: #64748B; gap: 4rpx; }
.bar-right { flex: 1; }
.bid-action { background: #0369A1; color: #fff; text-align: center; padding: 24rpx 0; border-radius: 12rpx; font-size: 30rpx; font-weight: 600; }
.bid-action.disabled { background: #CBD5E1; }
.bid-action.ended { background: #F1F5F9; color: #64748B; }
.bid-popup { padding: 30rpx; }
.popup-title { font-size: 32rpx; font-weight: 600; text-align: center; margin-bottom: 24rpx; }
.popup-info { display: flex; justify-content: space-between; margin-bottom: 24rpx; font-size: 24rpx; color: #64748B; }
.popup-input { margin-bottom: 24rpx; }
.quick-bids { display: flex; gap: 16rpx; margin-bottom: 30rpx; }
.quick-btn { flex: 1; text-align: center; padding: 16rpx 0; background: #F1F5F9; border-radius: 12rpx; font-size: 24rpx; color: #0369A1; }
.popup-btn { background: #0369A1; color: #fff; text-align: center; padding: 24rpx 0; border-radius: 12rpx; font-size: 30rpx; font-weight: 600; }
</style>
