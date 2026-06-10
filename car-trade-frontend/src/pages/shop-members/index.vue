<template>
	<view class="page">
		<u-navbar title="车行管理" :border-bottom="false" :placeholder="true"></u-navbar>

		<!-- 邀请入口 -->
		<view class="invite-section">
			<view class="invite-btn" @click="showInvite = true">
				<u-icon name="plus" color="#0369A1" size="28"></u-icon>
				<text>邀请成员</text>
			</view>
			<text class="invite-tip">成员通过微信扫码即可申请绑定您的车行</text>
		</view>

		<!-- 成员列表 -->
		<view class="section-title">车行成员 ({{ memberList.length }})</view>
		<view class="member-list" v-if="memberList.length > 0">
			<view class="member-card" v-for="item in memberList" :key="item.id">
				<view class="member-info">
					<image :src="item.memberAvatar || '/static/default-avatar.png'" class="member-avatar"></image>
					<view class="member-detail">
						<text class="member-name">{{ item.memberName || '成员' }}</text>
						<text class="member-phone" v-if="item.memberPhone">{{ item.memberPhone }}</text>
					</view>
				</view>
				<view class="member-right">
					<view class="role-tag" :class="item.role">{{ roleText(item.role) }}</view>
					<view class="status-tag" :class="item.status">{{ statusText(item.status) }}</view>
				</view>
				<view class="member-actions" v-if="item.status === 'PENDING'">
					<view class="action-btn approve" @click="approve(item.id)">通过</view>
					<view class="action-btn reject" @click="remove(item.id)">拒绝</view>
				</view>
			</view>
		</view>
		<view class="empty" v-if="memberList.length === 0 && !loading">
			<u-empty text="暂无成员" mode="list"></u-empty>
		</view>

		<!-- 邀请弹窗 -->
		<u-popup :show="showInvite" mode="center" round="24" @close="showInvite = false">
			<view class="invite-popup">
				<view class="popup-title">邀请成员加入车行</view>
				<view class="popup-input">
					<u-input v-model="invitePhone" type="number" placeholder="输入成员手机号" :border="'surround'" maxlength="11"></u-input>
				</view>
				<view class="popup-btn" @click="doInvite">发送邀请</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
import { getShopMembers, inviteMember, approveMember, removeMember } from '@/api/shop'
export default {
	data() { return { memberList: [], loading: false, showInvite: false, invitePhone: '' } },
	onLoad() { this.fetchMembers() },
	onPullDownRefresh() { this.fetchMembers(); uni.stopPullDownRefresh() },
	methods: {
		async fetchMembers() {
			this.loading = true
			try {
				const res = await getShopMembers()
				this.memberList = res.data || []
			} catch (e) { console.error(e) } finally { this.loading = false }
		},
		async doInvite() {
			if (!this.invitePhone || this.invitePhone.length < 11) { uni.$u.toast('请输入正确手机号'); return }
			try {
				await inviteMember({ phone: this.invitePhone })
				uni.$u.toast('邀请已发送')
				this.showInvite = false; this.invitePhone = ''
			} catch (e) { console.error(e) }
		},
		async approve(id) {
			try {
				await approveMember(id)
				this.fetchMembers()
				uni.$u.toast('已通过')
			} catch (e) { console.error(e) }
		},
		async remove(id) {
			uni.showModal({
				title: '提示', content: '确定拒绝/移除该成员？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await removeMember(id)
							this.memberList = this.memberList.filter(m => m.id !== id)
							uni.$u.toast('已移除')
						} catch (e) { console.error(e) }
					}
				}
			})
		},
		roleText(role) { return { OWNER: '车行主', ADMIN: '管理员', MEMBER: '成员' }[role] || role },
		statusText(status) { return { PENDING: '待审批', ACTIVE: '已加入', REJECTED: '已拒绝' }[status] || status }
	}
}
</script>

<style lang="scss" scoped>
.page { background: #F8FAFC; min-height: 100vh; }
.invite-section { background: #fff; padding: 30rpx; margin: 20rpx 30rpx; border-radius: 16rpx; display: flex; align-items: center; gap: 16rpx; }
.invite-btn { display: flex; align-items: center; gap: 8rpx; color: #0369A1; font-size: 28rpx; font-weight: 500; }
.invite-tip { font-size: 22rpx; color: #94A3B8; margin-left: auto; }
.section-title { font-size: 28rpx; font-weight: 600; color: #0F172A; padding: 20rpx 30rpx 12rpx; }
.member-list { padding: 0 30rpx; }
.member-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.member-info { display: flex; align-items: center; gap: 16rpx; }
.member-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; }
.member-detail { flex: 1; }
.member-name { font-size: 28rpx; font-weight: 500; color: #0F172A; display: block; }
.member-phone { font-size: 24rpx; color: #94A3B8; display: block; margin-top: 4rpx; }
.member-right { display: flex; gap: 12rpx; margin-top: 12rpx; }
.role-tag { font-size: 22rpx; padding: 4rpx 14rpx; border-radius: 6rpx; background: #F1F5F9; color: #64748B; }
.role-tag.OWNER { background: #FEF3C7; color: #92400E; }
.role-tag.ADMIN { background: #DBEAFE; color: #1D4ED8; }
.status-tag { font-size: 22rpx; padding: 4rpx 14rpx; border-radius: 6rpx; }
.status-tag.ACTIVE { background: #DCFCE7; color: #16A34A; }
.status-tag.PENDING { background: #FEF9C3; color: #CA8A04; }
.member-actions { display: flex; gap: 16rpx; margin-top: 16rpx; }
.action-btn { flex: 1; text-align: center; padding: 14rpx 0; border-radius: 8rpx; font-size: 26rpx; }
.action-btn.approve { background: #0369A1; color: #fff; }
.action-btn.reject { background: #F1F5F9; color: #64748B; }
.invite-popup { padding: 40rpx 30rpx; width: 560rpx; }
.popup-title { font-size: 32rpx; font-weight: 600; text-align: center; margin-bottom: 30rpx; }
.popup-input { margin-bottom: 30rpx; }
.popup-btn { background: #0369A1; color: #fff; text-align: center; padding: 24rpx 0; border-radius: 12rpx; font-size: 30rpx; }
.empty { padding-top: 200rpx; }
</style>
