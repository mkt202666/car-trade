<template>
	<view class="page">
		<u-navbar title="我的优惠券" :border-bottom="false" :placeholder="true"></u-navbar>
		<view class="tabs">
			<view class="tab" :class="{ active: tab === 'available' }" @click="tab = 'available'; fetchAvailable()">可领取</view>
			<view class="tab" :class="{ active: tab === 'mine' }" @click="tab = 'mine'; fetchMine()">我的券</view>
		</view>

		<view class="coupon-list" v-if="couponList.length > 0">
			<view class="coupon-card" :class="{ used: item.status === 'USED', expired: item.status === 'EXPIRED' }" v-for="item in couponList" :key="item.id">
				<view class="coupon-left">
					<text class="coupon-value">{{ item.type === 'DISCOUNT' ? item.value + '折' : '¥' + item.value }}</text>
					<text class="coupon-condition" v-if="item.minAmount">满{{ item.minAmount }}可用</text>
				</view>
				<view class="coupon-right">
					<text class="coupon-name">{{ item.name }}</text>
					<text class="coupon-time" v-if="item.endAt">有效期至 {{ formatTime(item.endAt) }}</text>
					<text class="coupon-status" v-if="item.status === 'USED'">已使用</text>
					<text class="coupon-status" v-else-if="item.status === 'EXPIRED'">已过期</text>
					<view class="claim-btn" v-else-if="tab === 'available'" @click="claimCoupon(item.id)">领取</view>
				</view>
			</view>
		</view>
		<view class="empty" v-if="couponList.length === 0 && !loading">
			<u-empty :text="tab === 'available' ? '暂无可领取优惠券' : '暂无优惠券'" mode="coupon"></u-empty>
		</view>
	</view>
</template>

<script>
import { getAvailableCoupons, getMyCoupons, claimCoupon } from '@/api/coupon'
import { formatTime } from '@/utils/format'
export default {
	data() { return { tab: 'available', couponList: [], loading: false } },
	onLoad() { this.fetchAvailable() },
	methods: {
		formatTime,
		async fetchAvailable() {
			this.loading = true
			try {
				const res = await getAvailableCoupons()
				this.couponList = res.data || []
			} catch (e) { console.error(e) } finally { this.loading = false }
		},
		async fetchMine() {
			this.loading = true
			try {
				const res = await getMyCoupons()
				this.couponList = res.data || []
			} catch (e) { console.error(e) } finally { this.loading = false }
		},
		async claimCoupon(id) {
			try {
				await claimCoupon(id)
				uni.$u.toast('领取成功')
				this.couponList = this.couponList.filter(c => c.id !== id)
			} catch (e) { console.error(e) }
		}
	}
}
</script>

<style lang="scss" scoped>
.page { background: #F8FAFC; min-height: 100vh; }
.tabs { display: flex; background: #fff; padding: 20rpx 30rpx; gap: 30rpx; }
.tab { font-size: 28rpx; color: #64748B; padding-bottom: 12rpx; }
.tab.active { color: #0369A1; border-bottom: 4rpx solid #0369A1; font-weight: 600; }
.coupon-list { padding: 20rpx 30rpx; }
.coupon-card { display: flex; background: #fff; border-radius: 16rpx; margin-bottom: 16rpx; overflow: hidden; }
.coupon-card.used, .coupon-card.expired { opacity: 0.6; }
.coupon-left { width: 200rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #FEF3C7; padding: 24rpx; }
.coupon-value { font-size: 36rpx; font-weight: 700; color: #DC2626; }
.coupon-condition { font-size: 20rpx; color: #92400E; margin-top: 8rpx; }
.coupon-right { flex: 1; padding: 20rpx; display: flex; flex-direction: column; justify-content: center; }
.coupon-name { font-size: 28rpx; color: #0F172A; font-weight: 500; }
.coupon-time { font-size: 22rpx; color: #94A3B8; margin-top: 8rpx; }
.coupon-status { font-size: 24rpx; color: #94A3B8; margin-top: 8rpx; }
.claim-btn { background: #0369A1; color: #fff; font-size: 24rpx; padding: 10rpx 28rpx; border-radius: 8rpx; text-align: center; margin-top: 12rpx; align-self: flex-start; }
.empty { padding-top: 200rpx; }
</style>
