<template>
  <view>
    <u-navbar title="电子合同" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="downloadContract">
        <u-icon name="download" size="36"></u-icon>
      </view>
    </u-navbar>
    <view class="page-content">
      <view v-if="loading" class="loading-wrap">
        <u-loading mode="flower" size="50"></u-loading>
      </view>
      <block v-else>
        <view class="contract-header">
          <text class="contract-title">二手车交易合同</text>
          <text class="contract-number">合同编号: {{ contract.contractNo }}</text>
        </view>
        <view class="section">
          <view class="section-title">合同信息</view>
          <u-cell-group :border="false">
            <u-cell-item title="卖家" :value="contract.sellerName" :border-bottom="true"></u-cell-item>
            <u-cell-item title="买家" :value="contract.buyerName" :border-bottom="true"></u-cell-item>
            <u-cell-item title="车辆信息" :value="contract.carTitle" :border-bottom="true"></u-cell-item>
            <u-cell-item title="成交价格" :value="formatPrice(contract.price)" :border-bottom="true"></u-cell-item>
            <u-cell-item title="签订日期" :value="formatTime(contract.signTime)" :border-bottom="true"></u-cell-item>
            <u-cell-item title="合同状态" :border-bottom="true">
              <u-tag slot="right" :text="statusText" :type="statusType" size="mini"></u-tag>
            </u-cell-item>
          </u-cell-group>
        </view>
        <view class="section">
          <view class="section-title">合同条款摘要</view>
          <view class="clause-list">
            <view class="clause-item" v-for="(clause, index) in clauses" :key="index">
              <text class="clause-title">第{{ index + 1 }}条 {{ clause.title }}</text>
              <text class="clause-content">{{ clause.content }}</text>
            </view>
          </view>
        </view>
        <view class="section sign-section" v-if="contract.status === 'PENDING_SIGN'">
          <view class="sign-hint">请仔细阅读以上合同条款</view>
          <u-button type="primary" size="default" shape="circle" :loading="signing" @click="handleSign">{{ signing ? '签署中...' : '点击签署合同' }}</u-button>
        </view>
        <view class="section sign-status" v-else-if="contract.status === 'SIGNED'">
          <u-icon name="checkmark-circle" size="64" color="#5ac725"></u-icon>
          <text class="signed-text">合同已签署</text>
          <text class="signed-time">签署时间: {{ formatTime(contract.signTime) }}</text>
        </view>
      </block>
    </view>
  </view>
</template>

<script>
import { getContractDetail, signContract, downloadContract as downloadContractApi } from '@/api/contract'
import { formatPrice, formatTime } from '@/utils/format'

export default {
  data() {
    return {
      contract: {},
      clauses: [
        { title: '车辆信息确认', content: '买卖双方确认车辆信息真实有效，包括但不限于车辆型号、行驶里程、车况等。' },
        { title: '交易价格', content: '双方确认交易价格为合同约定金额，定金已支付。' },
        { title: '过户手续', content: '卖方应在收到全款后配合买方完成车辆过户手续。' },
        { title: '违约责任', content: '任何一方违约需承担相应法律责任。' }
      ],
      loading: false,
      signing: false,
      id: ''
    }
  },
  computed: {
    statusText() {
      const map = { 'PENDING': '待签署', 'PENDING_SIGN': '待签署', 'SIGNED': '已签署', 'COMPLETED': '已完成' }
      return map[this.contract.status] || this.contract.status
    },
    statusType() {
      const map = { 'PENDING': 'warning', 'PENDING_SIGN': 'warning', 'SIGNED': 'success', 'COMPLETED': 'success' }
      return map[this.contract.status] || 'default'
    }
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id
      this.loadContract()
    }
  },
  methods: {
    formatPrice,
    formatTime,
    async loadContract() {
      this.loading = true
      try {
        const res = await getContractDetail(this.id)
        this.contract = res.data
      } catch (e) {}
      this.loading = false
    },
    async handleSign() {
      this.signing = true
      try {
        await signContract(this.id)
        uni.$u.toast('签署成功')
        this.loadContract()
      } catch (e) {}
      this.signing = false
    },
    async downloadContract() {
      try {
        const res = await downloadContractApi(this.id)
        const url = res.data.url || res.data
        uni.downloadFile({
          url: url,
          success: (res) => {
            uni.openDocument({ filePath: res.tempFilePath, showMenu: true })
          }
        })
      } catch (e) {
        uni.$u.toast('下载失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar-right { padding-right: 20rpx; }
.page-content { min-height: 100vh; background: #f5f5f5; }
.loading-wrap { display: flex; justify-content: center; padding: 100rpx 0; }
.contract-header { background: #fff; padding: 40rpx 30rpx; text-align: center; }
.contract-title { font-size: 36rpx; font-weight: 700; color: #333; }
.contract-number { font-size: 22rpx; color: #999; display: block; margin-top: 10rpx; }
.section { background: #fff; margin: 16rpx 0; padding: 30rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }
.clause-list { }
.clause-item { margin-bottom: 24rpx; }
.clause-title { font-size: 26rpx; font-weight: 600; color: #333; display: block; }
.clause-content { font-size: 24rpx; color: #666; line-height: 1.8; display: block; margin-top: 8rpx; }
.sign-section { }
.sign-hint { font-size: 24rpx; color: #999; text-align: center; margin-bottom: 20rpx; }
.sign-status { display: flex; flex-direction: column; align-items: center; padding: 60rpx 30rpx; }
.signed-text { font-size: 32rpx; font-weight: 600; color: #5ac725; margin-top: 16rpx; }
.signed-time { font-size: 24rpx; color: #999; margin-top: 8rpx; }
</style>
