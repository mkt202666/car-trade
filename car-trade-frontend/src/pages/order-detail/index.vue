<template>
  <view class="page">
    <u-navbar title="订单详情" :border-bottom="false" :placeholder="true"></u-navbar>

    <view class="page-content">
      <!-- 状态栏 -->
      <view class="status-bar" :class="'status-' + order.status">
        <text class="status-text">{{ order.statusText }}</text>
        <text class="status-desc" v-if="order.statusDesc">· {{ order.statusDesc }}</text>
      </view>

      <!-- 买卖双方信息 -->
      <view class="section">
        <view class="section-title">买卖双方信息</view>
        <view class="party-row">
          <view class="party-info">
            <text class="party-label">买方</text>
            <text class="party-name">{{ order.buyer.name }}({{ order.buyer.city }})</text>
            <text class="party-detail">{{ order.buyer.phone }} | {{ order.buyer.idCard }}</text>
          </view>
        </view>
        <view class="party-row">
          <view class="party-info">
            <text class="party-label">卖方</text>
            <text class="party-name">{{ order.seller.name }}({{ order.seller.city }})</text>
            <text class="party-detail">{{ order.seller.phone }} | {{ order.seller.idCard }}</text>
          </view>
          <view class="call-btn" @click="callSeller">
            <u-icon name="phone" size="32" color="#3c9cff"></u-icon>
            <text>打电话</text>
          </view>
        </view>
      </view>

      <!-- 车辆基本信息 -->
      <view class="section">
        <view class="section-title">车辆基本信息</view>
        <view class="car-basic">
          <text class="car-name">{{ order.carTitle }}</text>
          <text class="car-id">车源ID {{ order.carId }}</text>
        </view>
        <view class="price-row">
          <view class="price-item">
            <text class="price-label">交易总价</text>
            <text class="price-value">{{ order.price / 10000 }}万</text>
          </view>
          <view class="price-item">
            <text class="price-label">定金保障金</text>
            <text class="price-value">{{ order.depositAmount.toLocaleString() }} 元</text>
          </view>
        </view>
      </view>

      <!-- 车辆详情 -->
      <view class="section">
        <view class="section-title">车辆详情</view>
        <view class="detail-grid">
          <view class="detail-item">
            <text class="d-label">车架号</text>
            <text class="d-value">{{ order.vin }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">品牌型号</text>
            <text class="d-value">{{ order.brandModel }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">上牌日期</text>
            <text class="d-value">{{ order.registerDate }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">表显里程</text>
            <text class="d-value">{{ order.mileage }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">车身颜色</text>
            <text class="d-value">{{ order.color }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">年检到期</text>
            <text class="d-value">{{ order.annualInspection }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">强险到期</text>
            <text class="d-value">{{ order.insuranceExpire }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">出厂年月</text>
            <text class="d-value">{{ order.manufactureDate || '--' }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">钥匙数量</text>
            <text class="d-value">{{ order.keyCount }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">使用性质</text>
            <text class="d-value">{{ order.usageType }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">所有人性质</text>
            <text class="d-value">{{ order.ownerType }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">是否抵押车</text>
            <text class="d-value">{{ order.hasMortgage ? '是' : '否' }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">是否有继承</text>
            <text class="d-value">{{ order.hasInheritance ? '是' : '否' }}</text>
          </view>
        </view>
      </view>

      <!-- 价格确认 -->
      <view class="section">
        <view class="section-title">价格确认</view>
        <view class="price-confirm">
          <view class="pc-item">
            <text class="pc-label">交易总价 (元)</text>
            <text class="pc-value">{{ order.price.toLocaleString() }}</text>
          </view>
          <view class="pc-item">
            <text class="pc-label">保证金 (元)</text>
            <text class="pc-value">{{ order.depositAmount.toLocaleString() }}</text>
          </view>
        </view>
      </view>

      <!-- 车况信息 -->
      <view class="section">
        <view class="section-title">车况信息</view>
        <view class="condition-grid">
          <view class="condition-item">
            <text class="c-label">整体车况</text>
            <text class="c-value">{{ order.condition.overall }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">漆面</text>
            <text class="c-value">{{ order.condition.paint }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">结构件</text>
            <text class="c-value">{{ order.condition.structure }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">发动机</text>
            <text class="c-value">{{ order.condition.engine }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">变速箱</text>
            <text class="c-value">{{ order.condition.gearbox }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">过户次数</text>
            <text class="c-value">{{ order.condition.ownershipTransfer }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">公里数</text>
            <text class="c-value">{{ order.condition.mileageType }}</text>
          </view>
        </view>
      </view>

      <!-- 车况描述 -->
      <view class="section">
        <view class="section-title">车况描述及其他特别约定 (选填)</view>
        <textarea class="desc-textarea" :value="order.conditionDesc" placeholder="未填写" disabled></textarea>
      </view>

      <!-- 车况异常照片 -->
      <view class="section">
        <view class="section-title">车况异常照片 (选填)</view>
        <view class="upload-hint" v-if="order.conditionPhotos.length === 0">
          <text>可上传车况异常部位图片，支持多张上传</text>
        </view>
        <view class="photo-list" v-if="order.conditionPhotos.length > 0">
          <image v-for="(img, i) in order.conditionPhotos" :key="i" :src="img" mode="aspectFill" class="photo-thumb"></image>
        </view>
      </view>

      <!-- 车辆材料 -->
      <view class="section">
        <view class="section-title">车辆材料 (选填)</view>
        <view class="upload-hint">
          <text>可以上传行驶证、产权证、钥匙、车况图片等材料</text>
        </view>
      </view>

      <!-- 订单信息 -->
      <view class="section">
        <view class="section-title">订单信息</view>
        <view class="order-info-list">
          <view class="oi-item">
            <text class="oi-label">合同编号</text>
            <text class="oi-value">{{ order.contractId }}</text>
          </view>
          <view class="oi-item">
            <text class="oi-label">创建时间</text>
            <text class="oi-value">{{ order.createTime }}</text>
          </view>
          <view class="oi-item">
            <text class="oi-label">买方保障金</text>
            <text class="oi-value">{{ order.buyerDeposit.toLocaleString() }} 元（{{ order.buyerDepositStatus }}）</text>
          </view>
          <view class="oi-item">
            <text class="oi-label">卖方保障金</text>
            <text class="oi-value">{{ order.sellerDeposit.toLocaleString() }} 元（{{ order.sellerDepositStatus }}）</text>
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-bar" v-if="showActions">
        <u-button v-if="order.status === 'PENDING'" type="warning" @click="handleCancel" :plain="true">取消订单</u-button>
        <u-button v-if="order.status === 'PENDING'" type="primary" @click="handleConfirm">确认订单</u-button>
        <u-button v-if="order.status === 'IN_TRANSACTION'" type="warning" @click="handleDispute">发起纠纷</u-button>
        <u-button v-if="order.status === 'IN_TRANSACTION'" type="success" @click="handleComplete">完成交易</u-button>
        <u-button v-if="order.status === 'COMPLETED'" type="primary" @click="toContract" :plain="true">查看合同</u-button>
      </view>
    </view>
  </view>
</template>

<script>
import { getOrderDetail, confirmOrder, cancelOrder, completeOrder } from '@/api/order'
import { formatPrice } from '@/utils/format'

export default {
  data() {
    return {
      order: {
        id: '',
        orderNo: '',
        carId: '',
        carTitle: '',
        price: 0,
        depositAmount: 0,
        buyerDeposit: 0,
        buyerDepositStatus: '',
        sellerDeposit: 0,
        sellerDepositStatus: '',
        status: '',
        statusText: '',
        statusDesc: '',
        buyer: { name: '', city: '', phone: '', idCard: '' },
        seller: { name: '', city: '', phone: '', idCard: '' },
        contractId: '',
        createTime: '',
        vin: '',
        brandModel: '',
        registerDate: '',
        mileage: 0,
        color: '',
        annualInspection: '',
        insuranceExpire: '',
        manufactureDate: '',
        keyCount: 0,
        usageType: '',
        ownerType: '',
        hasMortgage: false,
        hasInheritance: false,
        condition: { overall: '', paint: '', structure: '', engine: '', gearbox: '', ownershipTransfer: '', mileageType: '' },
        conditionDesc: '',
        conditionPhotos: []
      },
      loading: false
    }
  },
  computed: {
    showActions() {
      return this.order.status && this.order.status !== 'CANCELLED'
    }
  },
  onLoad(options) {
    if (options.id) {
      this.fetchOrderDetail(options.id)
    }
  },
  methods: {
    formatPrice,
    async fetchOrderDetail(id) {
      try {
        const res = await getOrderDetail(id)
        this.order = res.data
      } catch (e) {
        uni.$u.toast('加载订单详情失败')
      }
    },
    callSeller() {
      uni.$u.toast('拨打电话: ' + this.order.seller.phone)
    },
    toContract() {
      uni.navigateTo({ url: '/pages/contract-detail/index?id=' + this.order.contractId })
    },
    async handleConfirm() {
      try {
        await confirmOrder(this.order.id)
        uni.$u.toast('已确认订单')
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        uni.$u.toast('操作失败')
      }
    },
    async handleCancel() {
      try {
        await cancelOrder(this.order.id)
        uni.$u.toast('已取消')
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        uni.$u.toast('取消失败')
      }
    },
    async handleComplete() {
      try {
        await completeOrder(this.order.id)
        uni.$u.toast('交易已完成')
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        uni.$u.toast('操作失败')
      }
    },
    handleDispute() {
      uni.navigateTo({ url: '/pages/customer-service/index?orderId=' + this.order.id })
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}
.page-content {
  padding-bottom: 140rpx;
}

/* 状态栏 */
.status-bar {
  padding: 30rpx;
  color: #fff;
  font-size: 26rpx;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
.status-text {
  font-size: 30rpx;
  font-weight: 600;
}
.status-desc {
  margin-left: 8rpx;
  opacity: 0.9;
}
.status-PENDING { background: linear-gradient(135deg, #f9ae3d, #f7b731); }
.status-IN_TRANSACTION { background: linear-gradient(135deg, #3c9cff, #2979ff); }
.status-DISPUTE { background: linear-gradient(135deg, #f56c6c, #e74c3c); }
.status-COMPLETED { background: linear-gradient(135deg, #5ac725, #4ca81b); }
.status-CANCELLED { background: linear-gradient(135deg, #999, #777); }

/* 通用区块 */
.section {
  background: #fff;
  margin: 16rpx 0;
  padding: 30rpx;
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}

/* 买卖双方 */
.party-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10rpx 0;
}
.party-info {
  flex: 1;
}
.party-label {
  font-size: 22rpx;
  color: #999;
}
.party-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-top: 4rpx;
}
.party-detail {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-top: 4rpx;
}
.call-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  font-size: 20rpx;
  color: #3c9cff;
}

/* 车辆基本信息 */
.car-basic {
  margin-bottom: 20rpx;
}
.car-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  display: block;
}
.car-id {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-top: 6rpx;
}
.price-row {
  display: flex;
  gap: 40rpx;
}
.price-item {
  flex: 1;
}
.price-label {
  font-size: 22rpx;
  color: #999;
  display: block;
}
.price-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #f56c6c;
  display: block;
  margin-top: 4rpx;
}

/* 车辆详情 */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}
.detail-item {
  padding: 12rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.d-label {
  font-size: 22rpx;
  color: #999;
  display: block;
}
.d-value {
  font-size: 26rpx;
  color: #333;
  display: block;
  margin-top: 4rpx;
}

/* 价格确认 */
.price-confirm {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.pc-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.pc-label {
  font-size: 26rpx;
  color: #333;
}
.pc-value {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

/* 车况信息 */
.condition-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}
.condition-item {
  padding: 12rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.c-label {
  font-size: 22rpx;
  color: #999;
  display: block;
}
.c-value {
  font-size: 26rpx;
  color: #333;
  font-weight: 600;
  display: block;
  margin-top: 4rpx;
}

/* 车况描述 */
.desc-textarea {
  width: 100%;
  min-height: 120rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 26rpx;
  color: #333;
}

/* 上传提示 */
.upload-hint {
  padding: 30rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  text-align: center;
  font-size: 24rpx;
  color: #999;
}
.photo-list {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
}
.photo-thumb {
  width: 140rpx;
  height: 140rpx;
  border-radius: 8rpx;
}

/* 订单信息 */
.order-info-list {
  display: flex;
  flex-direction: column;
}
.oi-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.oi-label {
  font-size: 26rpx;
  color: #333;
}
.oi-value {
  font-size: 24rpx;
  color: #666;
  max-width: 60%;
  text-align: right;
}

/* 操作按钮 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  display: flex;
  gap: 20rpx;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #eee;
  z-index: 10;
}
.action-bar u-button {
  flex: 1;
}
</style>