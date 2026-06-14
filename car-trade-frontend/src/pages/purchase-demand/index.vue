<template>
	<view class="page">
		<u-navbar title="发布求购" :border-bottom="false" :placeholder="true"></u-navbar>
		<view class="form">
			<view class="form-section">
				<view class="section-title">车辆需求</view>
				<view class="form-item">
					<text class="label">品牌</text>
					<u-input v-model="form.brandName" placeholder="如：宝马、奔驰" :border="'surround'"></u-input>
				</view>
				<view class="form-item">
					<text class="label">车系</text>
					<u-input v-model="form.seriesName" placeholder="如：5系、E级" :border="'surround'"></u-input>
				</view>
				<view class="form-item">
					<text class="label">车型</text>
					<u-input v-model="form.modelName" placeholder="如：530Li 尊享版" :border="'surround'"></u-input>
				</view>
				<view class="form-row">
					<view class="form-item half">
						<text class="label">年份起</text>
						<u-input v-model="form.yearFrom" type="number" placeholder="2020" :border="'surround'"></u-input>
					</view>
					<view class="form-item half">
						<text class="label">年份止</text>
						<u-input v-model="form.yearTo" type="number" placeholder="2024" :border="'surround'"></u-input>
					</view>
				</view>
				<view class="form-row">
					<view class="form-item half">
						<text class="label">预算最低(万)</text>
						<u-input v-model="form.priceMin" type="number" placeholder="10" :border="'surround'"></u-input>
					</view>
					<view class="form-item half">
						<text class="label">预算最高(万)</text>
						<u-input v-model="form.priceMax" type="number" placeholder="30" :border="'surround'"></u-input>
					</view>
				</view>
				<view class="form-item">
					<text class="label">最大里程(万公里)</text>
					<u-input v-model="form.mileageMax" type="number" placeholder="5" :border="'surround'"></u-input>
				</view>
				<view class="form-item">
					<text class="label">意向颜色</text>
					<u-input v-model="form.color" placeholder="如：白色、黑色" :border="'surround'"></u-input>
				</view>
				<view class="form-item">
					<text class="label">所在城市</text>
					<u-input v-model="form.cityName" placeholder="如：北京" :border="'surround'"></u-input>
				</view>
				<view class="form-item">
					<text class="label">补充描述</text>
					<u-input v-model="form.description" type="textarea" placeholder="描述您的具体需求，如车况要求、配置偏好等" :border="'surround'" maxlength="500"></u-input>
				</view>
			</view>
		</view>
		<view class="submit-btn" @click="submit">发布求购</view>
	</view>
</template>

<script>
import { createPurchaseDemand } from '@/api/purchase'
export default {
	data() {
		return {
			form: { brandName: '', seriesName: '', modelName: '', yearFrom: '', yearTo: '', priceMin: '', priceMax: '', mileageMax: '', color: '', cityName: '', description: '' }
		}
	},
	methods: {
		async submit() {
			if (!this.form.brandName && !this.form.seriesName) {
				uni.$u.toast('请至少填写品牌或车系'); return
			}
			const data = { ...this.form }
			if (data.priceMin) data.priceMin = Number(data.priceMin) * 10000
			if (data.priceMax) data.priceMax = Number(data.priceMax) * 10000
			if (data.mileageMax) data.mileageMax = Number(data.mileageMax) * 10000
			if (data.yearFrom) data.yearFrom = Number(data.yearFrom)
			if (data.yearTo) data.yearTo = Number(data.yearTo)
			try {
				await createPurchaseDemand(data)
				uni.$u.toast('发布成功')
				setTimeout(() => uni.navigateBack(), 1500)
			} catch (e) {
				console.error(e)
				uni.$u.toast('发布失败,请重试')
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.page { background: #F8FAFC; min-height: 100vh; padding-bottom: 120rpx; }
.form { padding: 20rpx 30rpx; }
.form-section { background: #fff; border-radius: 16rpx; padding: 24rpx; }
.section-title { font-size: 30rpx; font-weight: 600; color: #0F172A; margin-bottom: 24rpx; }
.form-item { margin-bottom: 20rpx; }
.form-item .label { font-size: 26rpx; color: #64748B; margin-bottom: 8rpx; display: block; }
.form-row { display: flex; gap: 20rpx; }
.form-item.half { flex: 1; }
.submit-btn { position: fixed; bottom: 0; left: 0; right: 0; background: #0369A1; color: #fff; text-align: center; padding: 28rpx 0; font-size: 32rpx; font-weight: 600; }
</style>
