<template>
	<view class="page">
		<u-navbar title="我的关注" :border-bottom="false" :placeholder="true"></u-navbar>
		<view class="list" v-if="followList.length > 0">
			<view class="follow-item" v-for="item in followList" :key="item.id" @click="goSeller(item.id)">
				<image :src="item.avatar || '/static/default-avatar.png'" class="avatar"></image>
				<view class="info">
					<text class="name">{{ item.nickname || '用户' }}</text>
					<text class="shop" v-if="item.shopName">{{ item.shopName }}</text>
					<view class="stats">
						<text class="stat">成交 {{ item.dealCount || 0 }}</text>
						<text class="stat">在售 {{ item.onSaleCount || 0 }}</text>
						<text class="stat">粉丝 {{ item.followerCount || 0 }}</text>
					</view>
				</view>
				<view class="unfollow-btn" @click.stop="unfollow(item.id, item.nickname)">取消关注</view>
			</view>
		</view>
		<view class="empty" v-if="followList.length === 0 && !loading">
			<u-empty text="暂无关注的车商" mode="list"></u-empty>
		</view>
	</view>
</template>

<script>
import { getMyFollows, unfollowUser } from '@/api/follow'
export default {
	data() { return { followList: [], loading: false } },
	onLoad() { this.fetchList() },
	onPullDownRefresh() { this.fetchList(); uni.stopPullDownRefresh() },
	methods: {
		async fetchList() {
			this.loading = true
			try {
				const res = await getMyFollows()
				this.followList = res.data || []
			} catch (e) {
				console.error(e)
				uni.$u.toast('加载关注列表失败')
			} finally { this.loading = false }
		},
		async unfollow(id, name) {
			uni.showModal({
				title: '提示', content: `确定取消关注「${name}」吗？`,
				success: async (res) => {
					if (res.confirm) {
						try {
							await unfollowUser(id)
							this.followList = this.followList.filter(f => f.id !== id)
							uni.$u.toast('已取消关注')
						} catch (e) {
							console.error(e)
							uni.$u.toast('操作失败,请重试')
						}
					}
				}
			})
		},
		goSeller(id) { uni.navigateTo({ url: '/pages/seller-home/index?id=' + id }) }
	}
}
</script>

<style lang="scss" scoped>
.page { background: #F8FAFC; min-height: 100vh; }
.list { padding: 20rpx 30rpx; }
.follow-item { display: flex; align-items: center; background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.avatar { width: 96rpx; height: 96rpx; border-radius: 50%; margin-right: 20rpx; }
.info { flex: 1; }
.name { font-size: 30rpx; font-weight: 600; color: #0F172A; display: block; }
.shop { font-size: 24rpx; color: #64748B; display: block; margin-top: 4rpx; }
.stats { display: flex; gap: 20rpx; margin-top: 8rpx; }
.stat { font-size: 22rpx; color: #94A3B8; }
.unfollow-btn { font-size: 24rpx; color: #64748B; border: 1rpx solid #E2E8F0; padding: 10rpx 24rpx; border-radius: 8rpx; }
.empty { padding-top: 200rpx; }
</style>
