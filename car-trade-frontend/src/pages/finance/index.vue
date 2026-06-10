<template>
  <view class="page">
    <u-navbar title="金融服务" :border-bottom="false" :placeholder="true">
      <template v-slot:left>
        <view class="nav-back" @click="goBack">
          <u-icon name="arrow-left" size="32" color="#333"></u-icon>
        </view>
      </template>
    </u-navbar>

    <view class="page-content">
      <!-- 金融服务概述 -->
      <view class="section">
        <view class="section-title">金融服务</view>
        <view class="section-content">
          <text class="content-text">平台提供多种金融服务，帮助您更好地进行二手车交易。</text>
        </view>
      </view>

      <!-- 保证金服务 -->
      <view class="section">
        <view class="section-title">保证金服务</view>
        <view class="service-list">
          <view class="service-item" @click="toDeposit">
            <view class="service-icon" style="background: linear-gradient(135deg, #0369A1, #0284C7)">
              <u-icon name="lock" size="36" color="#fff"></u-icon>
            </view>
            <view class="service-info">
              <text class="service-name">保证金账户</text>
              <text class="service-desc">管理您的保证金，查看余额和交易记录</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
          <view class="service-item" @click="toDepositRecharge">
            <view class="service-icon" style="background: linear-gradient(135deg, #10B981, #059669)">
              <u-icon name="plus-circle" size="36" color="#fff"></u-icon>
            </view>
            <view class="service-info">
              <text class="service-name">保证金充值</text>
              <text class="service-desc">快速充值保证金，保障交易安全</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
        </view>
      </view>

      <!-- 信用服务 -->
      <view class="section">
        <view class="section-title">信用服务</view>
        <view class="service-list">
          <view class="service-item" @click="toCredit">
            <view class="service-icon" style="background: linear-gradient(135deg, #F59E0B, #D97706)">
              <u-icon name="star" size="36" color="#fff"></u-icon>
            </view>
            <view class="service-info">
              <text class="service-name">信用额度</text>
              <text class="service-desc">查看和管理您的信用额度</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
          <view class="service-item" @click="toCreditApply">
            <view class="service-icon" style="background: linear-gradient(135deg, #7C3AED, #6D28D9)">
              <u-icon name="edit-pen" size="36" color="#fff"></u-icon>
            </view>
            <view class="service-info">
              <text class="service-name">额度申请</text>
              <text class="service-desc">申请提升信用额度</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
        </view>
      </view>

      <!-- 账单服务 -->
      <view class="section">
        <view class="section-title">账单服务</view>
        <view class="service-list">
          <view class="service-item" @click="toBill">
            <view class="service-icon" style="background: linear-gradient(135deg, #EF4444, #DC2626)">
              <u-icon name="file-text" size="36" color="#fff"></u-icon>
            </view>
            <view class="service-info">
              <text class="service-name">账单明细</text>
              <text class="service-desc">查看交易账单和收支明细</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
          <view class="service-item" @click="toBillExport">
            <view class="service-icon" style="background: linear-gradient(135deg, #06B6D4, #0891B2)">
              <u-icon name="download" size="36" color="#fff"></u-icon>
            </view>
            <view class="service-info">
              <text class="service-name">账单导出</text>
              <text class="service-desc">导出账单明细到本地</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
        </view>
      </view>

      <!-- 金融服务说明 -->
      <view class="section">
        <view class="section-title">服务说明</view>
        <view class="notice-list">
          <view class="notice-item" v-for="(item, index) in notices" :key="index">
            <view class="notice-index">{{ index + 1 }}</view>
            <text class="notice-text">{{ item }}</text>
          </view>
        </view>
      </view>

      <!-- 联系客服 -->
      <view class="section">
        <view class="contact-info">
          <text class="contact-text">如有金融相关问题，请联系平台客服：</text>
          <view class="contact-btn" @click="toCustomerService">
            <u-icon name="headphones" size="28" color="#0369A1"></u-icon>
            <text>联系客服</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      notices: [
        '保证金用于保障交易安全，交易完成后将自动退还。',
        '信用额度根据您的交易记录和信用评级动态调整。',
        '所有金融交易均受平台监管，确保资金安全。',
        '如有疑问，请联系平台客服获取帮助。'
      ]
    }
  },
  methods: {
    toDeposit() {
      uni.navigateTo({ url: '/pages/deposit/index' })
    },
    toDepositRecharge() {
      uni.navigateTo({ url: '/pages/deposit/index?tab=recharge' })
    },
    toCredit() {
      uni.$u.toast('信用额度功能开发中')
    },
    toCreditApply() {
      uni.$u.toast('额度申请功能开发中')
    },
    toBill() {
      uni.$u.toast('账单明细功能开发中')
    },
    toBillExport() {
      uni.$u.toast('账单导出功能开发中')
    },
    toCustomerService() {
      uni.navigateTo({ url: '/pages/customer-service/index' })
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #0F172A;
$cta-color: #0369A1;
$bg-color: #F8FAFC;
$text-color: #020617;
$text-secondary: #64748B;
$border-color: #E2E8F0;
$border-radius: 16rpx;
$shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
$transition: all 0.2s ease;

.page {
  min-height: 100vh;
  background: $bg-color;
}

.page-content {
  padding: 20rpx;
}

.section {
  background: #fff;
  border-radius: $border-radius;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: $shadow;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid $border-color;
}

.section-content {
  margin-bottom: 20rpx;
}

.content-text {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.6;
}

/* 服务列表 */
.service-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.service-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: $bg-color;
  border-radius: 12rpx;
  cursor: pointer;
  transition: $transition;

  &:active {
    background: rgba(0, 0, 0, 0.05);
  }
}

.service-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.service-info {
  flex: 1;
}

.service-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
  margin-bottom: 8rpx;
}

.service-desc {
  font-size: 24rpx;
  color: $text-secondary;
}

/* 通知列表 */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.notice-item {
  display: flex;
  gap: 12rpx;
  padding: 12rpx 0;
}

.notice-index {
  width: 36rpx;
  height: 36rpx;
  background: $cta-color;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.notice-text {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.5;
}

/* 联系客服 */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.contact-text {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.6;
}

.contact-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 20rpx;
  background: rgba(3, 105, 161, 0.1);
  border-radius: 12rpx;
  cursor: pointer;
  transition: $transition;

  &:active {
    background: rgba(3, 105, 161, 0.2);
  }

  text {
    font-size: 28rpx;
    color: $cta-color;
    font-weight: 600;
  }
}
</style>
