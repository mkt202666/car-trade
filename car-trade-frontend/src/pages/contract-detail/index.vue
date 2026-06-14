<template>
  <view>
    <u-navbar title="电子合同" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="downloadContract">
        <u-icon name="download" size="36"></u-icon>
      </view>
    </u-navbar>
    <view class="page-content">
      <view v-if="loading" class="loading-wrap">
        <view class="custom-spinner"></view>
      </view>
      <block v-else>
        <view class="contract-header">
          <text class="contract-title">{{ contract.title || '二手车交易合同' }}</text>
          <text class="contract-number">合同编号: {{ contract.contractNo }}</text>
        </view>
        <view class="section">
          <view class="section-title">合同信息</view>
          <u-cell-group :border="false">
            <u-cell-item title="卖家" :value="contract.sellerName || '未指定'" :border-bottom="true"></u-cell-item>
            <u-cell-item title="买家" :value="contract.buyerName || '未指定'" :border-bottom="true"></u-cell-item>
            <u-cell-item title="车辆信息" :value="contract.carTitle || '暂无'" :border-bottom="true"></u-cell-item>
            <u-cell-item title="成交价格" :value="formatPrice(contract.price)" :border-bottom="true"></u-cell-item>
            <u-cell-item title="签订日期" :value="formatTime(contract.signTime || contract.createdAt)" :border-bottom="true"></u-cell-item>
            <u-cell-item title="合同状态" :border-bottom="true">
              <u-tag slot="right" :text="statusText" :type="statusType" size="mini"></u-tag>
            </u-cell-item>
          </u-cell-group>
        </view>
        
        <!-- 签署状态 -->
        <view class="section">
          <view class="section-title">签署状态</view>
          <view class="sign-status-grid">
            <view class="sign-party">
              <view class="party-label">买方签署</view>
              <u-icon :name="contract.buyerSigned ? 'checkmark-circle' : 'minus-circle'" :color="contract.buyerSigned ? '#5ac725' : '#999'" size="40"></u-icon>
              <text class="party-status-text">{{ contract.buyerSigned ? '已签署' : '未签署' }}</text>
              <text class="party-time" v-if="contract.buyerSignedAt">{{ formatTime(contract.buyerSignedAt) }}</text>
            </view>
            <view class="sign-party">
              <view class="party-label">卖方签署</view>
              <u-icon :name="contract.sellerSigned ? 'checkmark-circle' : 'minus-circle'" :color="contract.sellerSigned ? '#5ac725' : '#999'" size="40"></u-icon>
              <text class="party-status-text">{{ contract.sellerSigned ? '已签署' : '未签署' }}</text>
              <text class="party-time" v-if="contract.sellerSignedAt">{{ formatTime(contract.sellerSignedAt) }}</text>
            </view>
          </view>
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
        
        <!-- 合同内容 -->
        <view class="section" v-if="contract.content">
          <view class="section-title">完整合同内容</view>
          <view class="contract-content">{{ contract.content }}</view>
        </view>
        
        <!-- 买方签署按钮 -->
        <view class="section sign-section" v-if="showBuyerSignButton">
          <view class="sign-hint">作为买方，请仔细阅读以上合同条款</view>
          <u-button type="primary" size="default" shape="circle" :loading="signing" @click="handleBuyerSign">{{ signing ? '签署中...' : '买方签署合同' }}</u-button>
        </view>
        
        <!-- 卖方签署按钮 -->
        <view class="section sign-section" v-if="showSellerSignButton">
          <view class="sign-hint">作为卖方，请仔细阅读以上合同条款</view>
          <u-button type="success" size="default" shape="circle" :loading="signing" @click="handleSellerSign">{{ signing ? '签署中...' : '卖方签署合同' }}</u-button>
        </view>
        
        <!-- 已完成签署 -->
        <view class="section sign-status" v-if="contract.status === 'SIGNED'">
          <u-icon name="checkmark-circle" size="64" color="#5ac725"></u-icon>
          <text class="signed-text">合同已签署完成</text>
          <text class="signed-time">签署完成时间: {{ formatTime(contract.signedAt || contract.updatedAt) }}</text>
        </view>
      </block>
    </view>
    
    <!-- 手写签名弹窗 -->
    <u-popup v-model="showSignatureModal" mode="bottom" height="60%" border-radius="16">
      <view class="signature-modal">
        <view class="modal-header">
          <text class="modal-title">{{ signRole === 'BUYER' ? '买方' : '卖方' }}手写签名</text>
          <u-icon name="close" size="24" @click="onSignatureCancel"></u-icon>
        </view>
        
        <SignaturePad 
          ref="signaturePad"
          @confirm="onSignatureConfirm"
          title="请在下方区域签名"
          hint="签名完成后点击确认按钮"
        />
        
        <view class="modal-footer">
          <u-button type="info" size="default" shape="circle" @click="$refs.signaturePad.clear()">重新签名</u-button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getContractDetail, signContract, downloadContract as downloadContractApi, uploadSignature } from '@/api/contract'
import { formatPrice, formatTime } from '@/utils/format'
import { requireAuth } from '@/utils/auth'
import SignaturePad from '@/components/signature-pad.vue'
import store from '@/store'

export default {
  components: {
    SignaturePad
  },
  data() {
    return {
      contract: {
        buyerSigned: false,
        sellerSigned: false
      },
      clauses: [
        { title: '车辆信息确认', content: '买卖双方确认车辆信息真实有效，包括但不限于车辆型号、行驶里程、车况等。' },
        { title: '交易价格', content: '双方确认交易价格为合同约定金额，定金已支付。' },
        { title: '过户手续', content: '卖方应在收到全款后配合买方完成车辆过户手续。' },
        { title: '违约责任', content: '任何一方违约需承担相应法律责任。' }
      ],
      loading: false,
      signing: false,
      id: '',
      // 签名相关
      showSignatureModal: false,
      signatureImage: null,
      signRole: '' // 'BUYER' or 'SELLER'
    }
  },
  computed: {
    statusText() {
      const map = { 'PENDING_SIGN': '待签署', 'SIGNED': '已签署', 'COMPLETED': '已完成', 'CANCELLED': '已取消' }
      return map[this.contract.status] || this.contract.status || '待签署'
    },
    statusType() {
      const map = { 'PENDING_SIGN': 'warning', 'SIGNED': 'success', 'COMPLETED': 'success', 'CANCELLED': 'info' }
      return map[this.contract.status] || 'default'
    },
    currentUserId() {
      return store.state.user?.id || null
    },
    currentUserRole() {
      const user = store.state.user
      if (!user || !this.contract.buyerId || !this.contract.sellerId) return null
      if (this.contract.buyerId === user.id) return 'BUYER'
      if (this.contract.sellerId === user.id) return 'SELLER'
      return null
    },
    showBuyerSignButton() {
      return this.contract.status === 'PENDING_SIGN' && !this.contract.buyerSigned && this.currentUserRole === 'BUYER'
    },
    showSellerSignButton() {
      return this.contract.status === 'PENDING_SIGN' && !this.contract.sellerSigned && this.currentUserRole === 'SELLER'
    }
  },
  onLoad(options) {
    if (options.id) {
      this.id = options.id
      this.loadContract()
    } else {
      uni.$u.toast('参数错误')
      setTimeout(() => uni.navigateBack(), 1500)
    }
  },
  methods: {
    formatPrice,
    formatTime,
    async loadContract() {
      if (!requireAuth()) return
      
      this.loading = true
      try {
        const res = await getContractDetail(this.id)
        const data = res.data || res
        this.contract = {
          ...this.contract,
          ...data,
          buyerSigned: data.buyerSigned || false,
          sellerSigned: data.sellerSigned || false
        }
      } catch (e) {
        console.error('加载合同详情失败', e)
        uni.$u.toast('加载合同详情失败，请重试')
      } finally {
        this.loading = false
      }
    },
    async handleBuyerSign() {
      if (!requireAuth()) return
      
      const currentRole = this.currentUserRole
      if (currentRole !== 'BUYER') {
        uni.$u.toast('您不是买方，无权签署')
        return
      }
      
      // 显示签名弹窗
      this.signRole = 'BUYER'
      this.showSignatureModal = true
      this.signatureImage = null
    },
    
    // 确认手写签名
    async onSignatureConfirm(tempFilePath) {
      this.signatureImage = tempFilePath
      this.showSignatureModal = false
      
      // 二次确认
      const res = await new Promise((resolve) => {
        uni.showModal({
          title: '确认签署',
          content: `作为${this.signRole === 'BUYER' ? '买方' : '卖方'}确认签署此合同？签署后不可撤回。`,
          success: (modalRes) => {
            if (modalRes.confirm) resolve(true)
            else resolve(false)
          }
        })
      })
      
      if (!res) {
        this.signatureImage = null
        return
      }
      
      this.signing = true
      try {
        // 1. 先上传签名图片
        uni.showLoading({ title: '上传签名中...' })
        const uploadResult = await uploadSignature(this.id, this.signatureImage)
        const signatureUrl = uploadResult.data.signatureUrl
        
        // 2. 调用签署接口(传入签名URL)
        uni.showLoading({ title: '签署合同中...' })
        await signContract(this.id, this.signRole, this.currentUserId, signatureUrl)
        
        uni.hideLoading()
        uni.$u.toast('签署成功')
        this.loadContract()
      } catch (e) {
        uni.hideLoading()
        console.error('签署失败', e)
        uni.$u.toast(e.message || '签署失败，请重试')
      } finally {
        this.signing = false
        this.signatureImage = null
      }
    },
    
    // 取消签名
    onSignatureCancel() {
      this.showSignatureModal = false
      this.signatureImage = null
    },
    async handleSellerSign() {
      if (!requireAuth()) return
      
      const currentRole = this.currentUserRole
      if (currentRole !== 'SELLER') {
        uni.$u.toast('您不是卖方，无权签署')
        return
      }
      
      // 显示签名弹窗
      this.signRole = 'SELLER'
      this.showSignatureModal = true
      this.signatureImage = null
    },
    async downloadContract() {
      try {
        const res = await downloadContractApi(this.id)
        const url = res.data || res
        if (!url) {
          uni.$u.toast('下载链接获取失败')
          return
        }
        uni.showLoading({ title: '正在下载...' })
        uni.downloadFile({
          url: url,
          success: (downloadRes) => {
            uni.hideLoading()
            if (downloadRes.statusCode === 200) {
              uni.openDocument({
                filePath: downloadRes.tempFilePath,
                showMenu: true,
                fail: () => {
                  uni.$u.toast('文件打开失败，请检查是否安装了PDF阅读器')
                }
              })
            } else {
              uni.$u.toast('下载失败')
            }
          },
          fail: (err) => {
            uni.hideLoading()
            console.error('下载失败', err)
            uni.$u.toast('下载失败，请重试')
          }
        })
      } catch (e) {
        uni.hideLoading()
        console.error('下载失败', e)
        uni.$u.toast('下载失败，请重试')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
/* 设计系统变量 */
$primary-color: #0F172A;
$cta-color: #0369A1;
$bg-color: #F8FAFC;
$text-color: #020617;
$text-secondary: #64748B;
$border-color: #E2E8F0;
$border-radius: 16rpx;
$shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
$transition: all 0.2s ease;

.navbar-right {
  padding-right: 20rpx;
  cursor: pointer;
  transition: $transition;

  &:active {
    opacity: 0.7;
  }
}
.page-content {
  min-height: 100vh;
  background: $bg-color;
}
.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 100rpx 0;
}
.custom-spinner {
  width: 64rpx;
  height: 64rpx;
  border: 6rpx solid #e5e7eb;
  border-top-color: #0369A1;
  border-radius: 50%;
  animation: cd-spin 0.8s linear infinite;
}
@keyframes cd-spin {
  to { transform: rotate(360deg); }
}
.contract-header {
  background: #fff;
  padding: 40rpx 30rpx;
  text-align: center;
  border-radius: $border-radius;
  margin: 20rpx;
  box-shadow: $shadow;
}
.contract-title {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-color;
}
.contract-number {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
  margin-top: 10rpx;
}
.section {
  background: #fff;
  margin: 20rpx;
  padding: 30rpx;
  border-radius: $border-radius;
  box-shadow: $shadow;
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 20rpx;
}
.clause-list {
}
.clause-item {
  margin-bottom: 24rpx;
  padding: 20rpx;
  background: $bg-color;
  border-radius: $border-radius;
  transition: $transition;

  &:active {
    background: darken($bg-color, 2%);
  }
}
.clause-title {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
}
.clause-content {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.8;
  display: block;
  margin-top: 8rpx;
}
.contract-content {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.8;
  white-space: pre-wrap;
  padding: 20rpx;
  background: $bg-color;
  border-radius: $border-radius;
}
.sign-section {
  cursor: pointer;
}
.sign-hint {
  font-size: 24rpx;
  color: $text-secondary;
  text-align: center;
  margin-bottom: 20rpx;
}
.sign-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 30rpx;
}
.signed-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #5ac725;
  margin-top: 16rpx;
}
.signed-time {
  font-size: 24rpx;
  color: $text-secondary;
  margin-top: 8rpx;
}
.sign-status-grid {
  display: flex;
  justify-content: space-around;
}
.sign-party {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  border-radius: $border-radius;
  transition: $transition;

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}
.party-label {
  font-size: 24rpx;
  color: $text-secondary;
  margin-bottom: 12rpx;
}
.party-status-text {
  font-size: 24rpx;
  color: $text-color;
  margin-top: 8rpx;
}
.party-time {
  font-size: 20rpx;
  color: $text-secondary;
  margin-top: 4rpx;
}
</style>
  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}
.party-label {
  font-size: 24rpx;
  color: $text-secondary;
  margin-bottom: 12rpx;
}
.party-status-text {
  font-size: 24rpx;
  color: $text-color;
  margin-top: 8rpx;
}
.party-time {
  font-size: 20rpx;
  color: $text-secondary;
  margin-top: 4rpx;
}
</style>
