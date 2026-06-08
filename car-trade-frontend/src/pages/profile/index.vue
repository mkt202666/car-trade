<template>
  <view class="page">
    <u-navbar title="我的" :border-bottom="false" :placeholder="true"></u-navbar>

    <!-- 未登录空状态 -->
    <view class="empty-state" v-if="!user && !loading">
      <u-icon name="account" size="120" color="#ccc"></u-icon>
      <view class="empty-title">登录后查看个人信息</view>
      <view class="empty-desc">登录二手车交易平台，查看订单、管理车源、获取专属服务</view>
      <u-button type="primary" shape="circle" class="empty-btn" @click="toLogin">立即登录</u-button>
      <u-button type="default" shape="circle" class="empty-btn" plain @click="toRegister">注册新账号</u-button>
    </view>

    <!-- 加载中 -->
    <view class="loading-state" v-if="loading">
      <u-icon name="reload" size="60" color="#3c9cff"></u-icon>
      <text class="loading-text">加载中...</text>
    </view>

    <view class="page-content" v-if="user">
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-top">
          <image :src="user.avatar || '/static/default-avatar.png'" mode="aspectFill" class="user-avatar"></image>
          <view class="user-info">
            <view class="user-name-row">
              <text class="user-name">{{ user.nickname }}({{ user.realName }})</text>
            </view>
            <text class="user-phone">{{ user.phone }}</text>
            <view class="user-badges">
              <view class="role-badge">{{ user.role }}</view>
              <text class="shop-name">{{ user.shopName }}</text>
            </view>
            <view class="user-meta">
              <text class="credit-badge" :class="'credit-' + user.creditGrade">信用: {{ user.creditGrade }}{{ user.creditLabel }}</text>
              <text class="deal-count">成交: {{ user.dealCount }}次</text>
            </view>
          </view>
        </view>
        <view class="user-actions">
          <view class="action-btn" @click="editProfile">
            <u-icon name="edit-pen" size="28" color="#fff"></u-icon>
            <text>修改资料</text>
          </view>
          <view class="action-btn" @click="toMyHome">
            <u-icon name="home" size="28" color="#fff"></u-icon>
            <text>我的主页</text>
          </view>
        </view>
      </view>

      <!-- 保证金卡片 -->
      <view class="deposit-card">
        <view class="deposit-header">
          <text class="deposit-label">可用保证金</text>
          <text class="deposit-amount">{{ formatAmount(user.deposit.balance) }} / {{ formatAmount(user.deposit.total) }}</text>
        </view>
        <view class="deposit-action">
          <u-button size="mini" type="primary" :plain="true" shape="circle" @click="toDeposit">立即充值</u-button>
        </view>
      </view>

      <!-- 会员信息 -->
      <view class="member-card">
        <view class="member-info">
          <text class="member-label">会员到期: {{ user.memberExpireAt }}</text>
          <text class="member-level">{{ user.memberLevel }}会员</text>
        </view>
        <u-button size="mini" type="warning" :plain="true" shape="circle" @click="renewMember">立即续费</u-button>
      </view>

      <!-- 数据统计 -->
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">{{ user.stats.carCount }}</text>
          <text class="stat-label">车源</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ formatAmount(user.stats.viewCount) }}</text>
          <text class="stat-label">被查看</text>
          <text class="stat-today">今日 +{{ user.stats.viewCountToday }}</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ formatAmount(user.stats.contactCount) }}</text>
          <text class="stat-label">沟通</text>
          <text class="stat-today">今日 +{{ user.stats.contactCountToday }}</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ user.stats.followerCount }}</text>
          <text class="stat-label">粉丝</text>
          <text class="stat-today">今日 +{{ user.stats.followerCountToday }}</text>
        </view>
      </view>

      <!-- 已认证标识 -->
      <view class="certified-badge" v-if="user.certificationStatus === 'CERTIFIED'">
        <u-icon name="checkmark-circle" size="36" color="#5ac725"></u-icon>
        <text>已认证商家</text>
      </view>

      <!-- 功能网格 -->
      <view class="func-grid">
        <view class="func-item" v-for="item in funcMenus" :key="item.label" @click="menuClick(item)">
          <view class="func-icon" :style="{ background: item.bg }">
            <u-icon :name="item.icon" size="40" color="#fff"></u-icon>
          </view>
          <text class="func-label">{{ item.label }}</text>
        </view>
      </view>

      <!-- 退出登录 -->
      <view class="logout-section">
        <u-button type="error" plain @click="handleLogout" shape="circle">退出登录</u-button>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserInfo, getUserStats } from '@/api/user'

export default {
  data() {
    return {
      user: null,
      loading: false,
      funcMenus: [
        { label: '我的车源', icon: 'car', bg: 'linear-gradient(135deg, #3c9cff, #2979ff)', page: 'publish', params: { tab: 'mine' } },
        { label: 'AI分发车源', icon: 'share', bg: 'linear-gradient(135deg, #a855f7, #8b5cf6)', page: 'ai', params: { mode: 'distribute' } },
        { label: 'AI行情简报', icon: 'file-text', bg: 'linear-gradient(135deg, #f9ae3d, #f7b731)', page: 'ai', params: { mode: 'market' } },
        { label: '收藏车源', icon: 'heart', bg: 'linear-gradient(135deg, #f56c6c, #e74c3c)', page: 'home', params: { tab: 'favorite' } },
        { label: '我的车行', icon: 'shop', bg: 'linear-gradient(135deg, #5ac725, #4ca81b)', page: 'seller-home', params: {} },
        { label: '浏览记录', icon: 'clock', bg: 'linear-gradient(135deg, #06b6d4, #0891b2)', page: 'home', params: { tab: 'history' } },
        { label: '我的订单', icon: 'order', bg: 'linear-gradient(135deg, #3c9cff, #2979ff)', page: 'trade', params: {} },
        { label: '金融服务', icon: 'rmb-circle', bg: 'linear-gradient(135deg, #f9ae3d, #f7b731)', page: 'home', params: { tab: 'finance' } },
        { label: '电子合同', icon: 'file-text', bg: 'linear-gradient(135deg, #5ac725, #4ca81b)', page: 'contract-detail', params: {} },
        { label: '我的关注', icon: 'star', bg: 'linear-gradient(135deg, #f56c6c, #e74c3c)', page: 'home', params: { tab: 'follow' } },
        { label: '我的优惠券', icon: 'coupon', bg: 'linear-gradient(135deg, #a855f7, #8b5cf6)', page: 'home', params: { tab: 'coupon' } },
        { label: '交易规范', icon: 'info-circle', bg: 'linear-gradient(135deg, #06b6d4, #0891b2)', page: '', params: {} },
        { label: '使用教程', icon: 'play-circle', bg: 'linear-gradient(135deg, #3c9cff, #2979ff)', page: '', params: {} },
        { label: '客服支持', icon: 'kefu-ermai', bg: 'linear-gradient(135deg, #5ac725, #4ca81b)', page: 'customer-service', params: {} },
        { label: '系统设置', icon: 'setting', bg: 'linear-gradient(135deg, #999, #777)', page: '', params: {} }
      ]
    }
  },
  computed: {
    storeUser() {
      return this.$store.state.user
    }
  },
  onShow() {
    this.fetchUserData()
  },
  methods: {
    async fetchUserData() {
      // 如果没有 token，直接显示空状态，不请求（避免 401）
      const token = uni.getStorageSync('token')
      if (!token) {
        this.user = null
        this.loading = false
        return
      }
      this.loading = true
      try {
        const [userRes, statsRes] = await Promise.all([getUserInfo(), getUserStats()])
        const userData = userRes.data
        const statsData = statsRes.data
        this.user = {
          ...userData,
          avatar: userData.avatar || '',
          role: userData.shopName ? '车商' : '个人',
          creditGrade: userData.creditGrade || 'A',
          creditLabel: userData.creditLabel || '优秀',
          dealCount: userData.dealCount || 0,
          certificationStatus: userData.certificationStatus || 'PENDING',
          memberExpireAt: userData.memberExpireAt || '--',
          memberLevel: userData.memberLevel || '--',
          deposit: {
            balance: userData.depositBalance || 0,
            total: userData.depositTotal || 0
          },
          stats: {
            carCount: userData.onSaleCount || 0,
            viewCount: statsData.viewCount || 0,
            viewCountToday: statsData.viewCountToday || 0,
            contactCount: statsData.messageCount || 0,
            contactCountToday: statsData.messageCountToday || 0,
            followerCount: statsData.followerCount || 0,
            followerCountToday: statsData.followerCountToday || 0
          }
        }
      } catch (e) {
        // 未登录或 token 失效，清除本地登录态并显示空状态
        this.user = null
        try {
          uni.removeStorageSync('token')
        } catch (_) {}
      } finally {
        this.loading = false
      }
    },
    formatAmount(val) {
      const n = Number(val) || 0
      return n.toLocaleString()
    },
    toLogin() {
      uni.navigateTo({ url: '/pages/login/index' })
    },
    toRegister() {
      uni.navigateTo({ url: '/pages/register/index' })
    },
    editProfile() {
      uni.$u.toast('修改资料开发中')
    },
    toMyHome() {
      uni.$u.toast('我的主页开发中')
    },
    toDeposit() {
      uni.$u.toast('保证金充值开发中')
    },
    renewMember() {
      uni.$u.toast('会员续费开发中')
    },
    menuClick(item) {
      if (item.page) {
        let url = '/pages/' + item.page + '/index'
        if (item.params && Object.keys(item.params).length > 0) {
          const params = Object.entries(item.params).map(([k, v]) => k + '=' + v).join('&')
          url += '?' + params
        }
        uni.navigateTo({ url })
      } else {
        uni.$u.toast('功能开发中，敬请期待')
      }
    },
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            this.$store.commit('logout')
            uni.$u.toast('已退出')
          }
        }
      })
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
  padding-bottom: 40rpx;
}

/* 空状态（未登录） */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 60rpx 80rpx;
}
.empty-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-top: 40rpx;
}
.empty-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 16rpx;
  text-align: center;
  line-height: 1.6;
}
.empty-btn {
  width: 400rpx;
  margin-top: 40rpx;
}

/* 加载中 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}
.loading-text {
  margin-top: 20rpx;
  font-size: 26rpx;
  color: #666;
}

/* 用户卡片 */
.user-card {
  background: linear-gradient(135deg, #3c9cff, #2979ff);
  padding: 30rpx 30rpx 40rpx;
  color: #fff;
}
.user-top {
  display: flex;
  align-items: flex-start;
}
.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255,255,255,0.5);
  margin-right: 20rpx;
}
.user-info {
  flex: 1;
}
.user-name-row {
  display: flex;
  align-items: center;
}
.user-name {
  font-size: 34rpx;
  font-weight: 700;
}
.user-phone {
  font-size: 24rpx;
  opacity: 0.85;
  display: block;
  margin-top: 4rpx;
}
.user-badges {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 8rpx;
}
.role-badge {
  background: rgba(255,255,255,0.25);
  font-size: 20rpx;
  padding: 2rpx 14rpx;
  border-radius: 20rpx;
}
.shop-name {
  font-size: 22rpx;
  opacity: 0.85;
}
.user-meta {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-top: 10rpx;
}
.credit-badge {
  font-size: 22rpx;
  padding: 2rpx 14rpx;
  border-radius: 20rpx;
  background: rgba(255,255,255,0.25);
}
.credit-badge.credit-S {
  background: #ff6b00;
}
.credit-badge.credit-A {
  background: #5ac725;
}
.deal-count {
  font-size: 22rpx;
  opacity: 0.85;
}
.user-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}
.action-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: rgba(255,255,255,0.2);
  padding: 10rpx 24rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
  color: #fff;
}

/* 保证金卡片 */
.deposit-card {
  background: #fff;
  margin: 24rpx 30rpx;
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.deposit-label {
  font-size: 24rpx;
  color: #999;
  display: block;
}
.deposit-amount {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
  display: block;
  margin-top: 4rpx;
}

/* 会员卡片 */
.member-card {
  background: #fff;
  margin: 0 30rpx 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.member-label {
  font-size: 24rpx;
  color: #999;
  display: block;
}
.member-level {
  font-size: 26rpx;
  color: #f9ae3d;
  font-weight: 600;
  display: block;
  margin-top: 4rpx;
}

/* 数据统计 */
.stats-row {
  display: flex;
  background: #fff;
  margin: 0 30rpx 24rpx;
  border-radius: 16rpx;
  padding: 24rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.stat-item {
  flex: 1;
  text-align: center;
  border-right: 1rpx solid #f0f0f0;
}
.stat-item:last-child {
  border-right: none;
}
.stat-num {
  font-size: 36rpx;
  font-weight: 700;
  color: #333;
  display: block;
}
.stat-label {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-top: 4rpx;
}
.stat-today {
  font-size: 20rpx;
  color: #5ac725;
  display: block;
  margin-top: 2rpx;
}

/* 已认证 */
.certified-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  background: #fff;
  margin: 0 30rpx 24rpx;
  border-radius: 16rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #5ac725;
  font-weight: 600;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

/* 功能网格 */
.func-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  padding: 0 30rpx;
  margin-bottom: 30rpx;
}
.func-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.func-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.func-label {
  font-size: 22rpx;
  color: #333;
  margin-top: 10rpx;
}

/* 退出 */
.logout-section {
  padding: 0 30rpx;
}
</style>