<template>
  <view class="page">
    <u-navbar title="我的" :border-bottom="false" :placeholder="true"></u-navbar>

    <!-- 未登录空状态 -->
    <view class="empty-state" v-if="!user && !loading">
      <view class="empty-icon">
        <u-icon name="account" size="120" color="#CBD5E1"></u-icon>
      </view>
      <view class="empty-title">登录后查看个人信息</view>
      <view class="empty-desc">登录二手车交易平台，查看订单、管理车源、获取专属服务</view>
      <view class="empty-actions">
        <button class="btn-primary" @click="toLogin">立即登录</button>
        <button class="btn-secondary" @click="toRegister">注册新账号</button>
      </view>
    </view>

    <!-- 加载中 -->
    <view class="loading-state" v-if="loading">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <view class="page-content" v-if="user">
      <!-- 用户信息卡片 -->
      <view class="user-card">
        <view class="user-top">
          <view class="avatar-container">
            <image :src="user.avatar || '/static/default-avatar.png'" mode="aspectFill" class="user-avatar"></image>
            <view class="certified-badge" v-if="user.certificationStatus === 'CERTIFIED'">
              <u-icon name="checkmark-circle" size="24" color="#10B981"></u-icon>
            </view>
          </view>
          <view class="user-info">
            <view class="user-name-row">
              <text class="user-name">{{ user.nickname }}</text>
              <view class="role-badge" v-if="user.shopName">车商</view>
              <view class="role-badge personal" v-else>个人</view>
            </view>
            <text class="user-phone">{{ user.phone }}</text>
            <view class="shop-name" v-if="user.shopName">{{ user.shopName }}</view>
            <view class="user-meta">
              <view class="credit-badge" :class="'credit-' + user.creditGrade">
                <text>信用 {{ user.creditGrade }}{{ user.creditLabel }}</text>
              </view>
              <text class="deal-count">成交 {{ user.dealCount }}次</text>
            </view>
          </view>
        </view>
        <view class="user-actions">
          <view class="action-btn" @click="editProfile">
            <u-icon name="edit-pen" size="28" color="#0F172A"></u-icon>
            <text>修改资料</text>
          </view>
          <view class="action-btn" @click="toMyHome">
            <u-icon name="home" size="28" color="#0F172A"></u-icon>
            <text>我的主页</text>
          </view>
        </view>
      </view>

      <!-- 保证金卡片 -->
      <view class="deposit-card">
        <view class="deposit-header">
          <view class="deposit-info">
            <text class="deposit-label">可用保证金</text>
            <text class="deposit-amount">¥{{ formatAmount(user.deposit.balance) }}</text>
            <text class="deposit-total">总额 ¥{{ formatAmount(user.deposit.total) }}</text>
          </view>
          <view class="deposit-action">
            <u-button size="mini" type="primary" :plain="true" shape="circle" @click="toDeposit">立即充值</u-button>
          </view>
        </view>
      </view>

      <!-- 会员信息 -->
      <view class="member-card">
        <view class="member-info">
          <view class="member-left">
            <u-icon name="star" size="36" color="#F59E0B"></u-icon>
            <view class="member-text">
              <text class="member-label">{{ user.memberLevel }}会员</text>
              <text class="member-expire">到期: {{ user.memberExpireAt }}</text>
            </view>
          </view>
          <u-button size="mini" type="warning" :plain="true" shape="circle" @click="renewMember">立即续费</u-button>
        </view>
      </view>

      <!-- 数据统计 -->
      <view class="stats-card">
        <view class="stats-header">
          <text class="stats-title">数据统计</text>
        </view>
        <view class="stats-row">
          <view class="stat-item">
            <text class="stat-num">{{ user.stats.carCount }}</text>
            <text class="stat-label">车源</text>
          </view>
          <view class="stat-item">
            <text class="stat-num">{{ formatAmount(user.stats.viewCount) }}</text>
            <text class="stat-label">被查看</text>
            <text class="stat-today">+{{ user.stats.viewCountToday }} 今日</text>
          </view>
          <view class="stat-item">
            <text class="stat-num">{{ formatAmount(user.stats.contactCount) }}</text>
            <text class="stat-label">沟通</text>
            <text class="stat-today">+{{ user.stats.contactCountToday }} 今日</text>
          </view>
          <view class="stat-item">
            <text class="stat-num">{{ user.stats.followerCount }}</text>
            <text class="stat-label">粉丝</text>
            <text class="stat-today">+{{ user.stats.followerCountToday }} 今日</text>
          </view>
        </view>
      </view>

      <!-- 已认证标识 -->
      <view class="certified-banner" v-if="user.certificationStatus === 'CERTIFIED'">
        <u-icon name="checkmark-circle-fill" size="40" color="#10B981"></u-icon>
        <text>已认证商家 · 品质保障</text>
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
        <button class="btn-logout" @click="handleLogout">退出登录</button>
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
        { label: '我的车源', icon: 'car', bg: 'linear-gradient(135deg, #0369A1, #0284C7)', page: 'publish', params: { tab: 'mine' } },
        { label: '我的求购', icon: 'search', bg: 'linear-gradient(135deg, #F59E0B, #D97706)', page: 'my-purchase-demand', params: {} },
        { label: 'AI分发车源', icon: 'share', bg: 'linear-gradient(135deg, #7C3AED, #6D28D9)', page: 'ai', params: { mode: 'distribute' } },
        { label: 'AI行情简报', icon: 'file-text', bg: 'linear-gradient(135deg, #F59E0B, #D97706)', page: 'ai-market-report', params: {} },
        { label: '收藏车源', icon: 'heart', bg: 'linear-gradient(135deg, #EF4444, #DC2626)', page: 'home', params: { tab: 'favorite' } },
        { label: '我的车行', icon: 'shop', bg: 'linear-gradient(135deg, #10B981, #059669)', page: 'seller-home', params: { id: this.user && this.user.id } },
        { label: '浏览记录', icon: 'clock', bg: 'linear-gradient(135deg, #06B6D4, #0891B2)', page: 'home', params: { tab: 'history' } },
        { label: '我的订单', icon: 'order', bg: 'linear-gradient(135deg, #0369A1, #0284C7)', page: 'trade', params: {} },
        { label: '金融服务', icon: 'rmb-circle', bg: 'linear-gradient(135deg, #F59E0B, #D97706)', page: 'finance', params: {} },
        { label: '电子合同', icon: 'file-text', bg: 'linear-gradient(135deg, #10B981, #059669)', page: 'contract-detail', params: {} },
        { label: '我的关注', icon: 'star', bg: 'linear-gradient(135deg, #EF4444, #DC2626)', page: 'home', params: { tab: 'follow' } },
        { label: '我的优惠券', icon: 'coupon', bg: 'linear-gradient(135deg, #7C3AED, #6D28D9)', page: 'home', params: { tab: 'coupon' } },
        { label: '交易规范', icon: 'info-circle', bg: 'linear-gradient(135deg, #06B6D4, #0891B2)', page: 'trade-rules', params: {} },
        { label: '使用教程', icon: 'play-circle', bg: 'linear-gradient(135deg, #0369A1, #0284C7)', page: 'tutorial', params: {} },
        { label: '客服支持', icon: 'kefu-ermai', bg: 'linear-gradient(135deg, #10B981, #059669)', page: 'customer-service', params: {} },
        { label: '系统设置', icon: 'setting', bg: 'linear-gradient(135deg, #64748B, #475569)', page: 'settings', params: {} }
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
      uni.navigateTo({ url: '/pages/profile/index' })
    },
    toMyHome() {
      uni.$u.toast('我的主页开发中')
    },
    toDeposit() {
      uni.navigateTo({ url: '/pages/deposit/index' })
    },
    renewMember() {
      uni.navigateTo({ url: '/pages/membership/index' })
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
            this.user = null
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================================
   5D好车个人中心 - 高级设计系统样式
   设计语言: Motion-Driven | Marketplace
   颜色系统: 深蓝主色 + 深色层次 + 琥珀金/绿色点缀
   ========================================================= */

.page {
  min-height: 100vh;
  background: #F8FAFC;
  padding-bottom: 200rpx;
  animation: fadeIn 400ms cubic-bezier(0.4, 0, 0.2, 1) both;
}

.page-content {
  padding-bottom: 40rpx;
}

/* ============ 空状态 ============ */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 60rpx 80rpx;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) both;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  border-radius: 100rpx;
  background: linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 4rpx 12rpx rgba(15, 23, 42, 0.06);
  transition: all 300ms cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: scale(1.05);
    box-shadow: 
      0 8rpx 24rpx rgba(15, 23, 42, 0.08),
      inset 0 4rpx 12rpx rgba(15, 23, 42, 0.06);
  }
}

.empty-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #0F172A;
  margin-top: 40rpx;
  letter-spacing: 1rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #64748B;
  margin-top: 16rpx;
  text-align: center;
  line-height: 1.7;
  max-width: 500rpx;
}

.empty-actions {
  display: flex;
  gap: 24rpx;
  margin-top: 48rpx;
}

.btn-primary {
  padding: 26rpx 52rpx;
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  color: #ffffff;
  border: none;
  border-radius: 52rpx;
  font-size: 28rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 8rpx 24rpx rgba(3, 105, 161, 0.35);

  &:hover {
    transform: translateY(-4rpx);
    box-shadow: 0 12rpx 36rpx rgba(3, 105, 161, 0.45);
  }

  &:active {
    transform: translateY(0) scale(0.97);
    box-shadow: 0 4rpx 12rpx rgba(3, 105, 161, 0.3);
  }
}

.btn-secondary {
  padding: 26rpx 52rpx;
  background: #ffffff;
  color: #0369A1;
  border: 2rpx solid #0369A1;
  border-radius: 52rpx;
  font-size: 28rpx;
  font-weight: 600;
  letter-spacing: 1rpx;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);

  &:hover {
    background: #F0F9FF;
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 20rpx rgba(3, 105, 161, 0.2);
  }

  &:active {
    transform: translateY(0) scale(0.97);
    box-shadow: 0 4rpx 12rpx rgba(3, 105, 161, 0.15);
  }
}

/* ============ 加载中 ============ */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.loading-spinner {
  width: 64rpx;
  height: 64rpx;
  border: 4rpx solid #E2E8F0;
  border-top-color: #0369A1;
  border-radius: 50%;
  animation: spin 0.8s cubic-bezier(0.4, 0, 0.2, 1) infinite;
  box-shadow: 0 0 20rpx rgba(3, 105, 161, 0.2);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 24rpx;
  font-size: 26rpx;
  color: #64748B;
  letter-spacing: 1rpx;
}

/* ============ 用户卡片 Hero ============ */
.user-card {
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 50%, #334155 100%);
  padding: 48rpx 32rpx;
  color: #fff;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.2);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 100ms both;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 300rpx;
    height: 300rpx;
    background: radial-gradient(circle, rgba(3, 105, 161, 0.15) 0%, transparent 70%);
    border-radius: 50%;
    transform: translate(50%, -50%);
  }
}

.user-top {
  display: flex;
  align-items: flex-start;
  position: relative;
  z-index: 1;
}

.avatar-container {
  position: relative;
  margin-right: 24rpx;
}

.user-avatar {
  width: 128rpx;
  height: 128rpx;
  border-radius: 64rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.25);
  box-shadow: 
    0 8rpx 24rpx rgba(0, 0, 0, 0.2),
    inset 0 2rpx 8rpx rgba(255, 255, 255, 0.1);
  transition: transform 300ms cubic-bezier(0.34, 1.56, 0.64, 1);

  &:hover {
    transform: scale(1.05) rotate(-2deg);
  }
}

.certified-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 44rpx;
  height: 44rpx;
  border-radius: 22rpx;
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3rpx solid #0F172A;
  box-shadow: 0 4rpx 12rpx rgba(16, 185, 129, 0.4);
  animation: pulseGlow 3s ease-in-out infinite;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-wrap: wrap;
}

.user-name {
  font-size: 38rpx;
  font-weight: 800;
  letter-spacing: 1rpx;
  background: linear-gradient(135deg, #ffffff 0%, #CBD5E1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.role-badge {
  background: rgba(3, 105, 161, 0.3);
  font-size: 20rpx;
  padding: 6rpx 18rpx;
  border-radius: 24rpx;
  font-weight: 600;
  letter-spacing: 1rpx;
  border: 1rpx solid rgba(3, 105, 161, 0.4);

  &.personal {
    background: rgba(16, 185, 129, 0.25);
    border-color: rgba(16, 185, 129, 0.4);
  }
}

.user-phone {
  font-size: 24rpx;
  opacity: 0.85;
  display: block;
  margin-top: 8rpx;
  letter-spacing: 1rpx;
}

.shop-name {
  font-size: 24rpx;
  opacity: 0.9;
  margin-top: 4rpx;
  color: #FCD34D;
  font-weight: 500;
}

.user-meta {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-top: 16rpx;
  flex-wrap: wrap;
}

.credit-badge {
  font-size: 22rpx;
  padding: 6rpx 18rpx;
  border-radius: 20rpx;
  background: rgba(16, 185, 129, 0.25);
  font-weight: 600;
  letter-spacing: 1rpx;
  border: 1rpx solid rgba(16, 185, 129, 0.3);

  &.credit-S { 
    background: rgba(245, 158, 11, 0.25); 
    border-color: rgba(245, 158, 11, 0.4);
  }
  &.credit-A { 
    background: rgba(16, 185, 129, 0.25); 
    border-color: rgba(16, 185, 129, 0.4);
  }
  &.credit-B { 
    background: rgba(3, 105, 161, 0.25); 
    border-color: rgba(3, 105, 161, 0.4);
  }
  &.credit-C { 
    background: rgba(100, 116, 139, 0.25); 
    border-color: rgba(100, 116, 139, 0.4);
  }
}

.deal-count {
  font-size: 22rpx;
  opacity: 0.85;
}

.user-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 36rpx;
  position: relative;
  z-index: 1;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 10rpx;
  background: rgba(255, 255, 255, 0.15);
  padding: 18rpx 32rpx;
  border-radius: 44rpx;
  font-size: 26rpx;
  color: #fff;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  font-weight: 500;

  &:hover {
    background: rgba(255, 255, 255, 0.25);
    transform: translateY(-2rpx);
    box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.15);
  }

  &:active {
    transform: translateY(0) scale(0.97);
    background: rgba(255, 255, 255, 0.3);
  }
}

/* ============ 保证金卡片 ============ */
.deposit-card {
  background: #ffffff;
  margin: 28rpx 32rpx;
  border-radius: 24rpx;
  padding: 36rpx;
  box-shadow: 
    0 8rpx 24rpx rgba(15, 23, 42, 0.06),
    0 2rpx 8rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid #F1F5F9;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 200ms both;
  transition: all 300ms cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    box-shadow: 
      0 12rpx 36rpx rgba(15, 23, 42, 0.1),
      0 4rpx 12rpx rgba(15, 23, 42, 0.06);
    transform: translateY(-4rpx);
  }
}

.deposit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.deposit-info {
  display: flex;
  flex-direction: column;
}

.deposit-label {
  font-size: 24rpx;
  color: #64748B;
  font-weight: 500;
  letter-spacing: 1rpx;
}

.deposit-amount {
  font-size: 52rpx;
  font-weight: 800;
  color: #0F172A;
  margin-top: 8rpx;
  letter-spacing: 1rpx;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.deposit-total {
  font-size: 22rpx;
  color: #94A3B8;
  margin-top: 6rpx;
}

/* ============ 会员卡片 ============ */
.member-card {
  background: linear-gradient(135deg, #FFFBEB 0%, #FEF3C7 50%, #FDE68A 100%);
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 32rpx 36rpx;
  box-shadow: 
    0 8rpx 24rpx rgba(245, 158, 11, 0.15),
    0 2rpx 8rpx rgba(245, 158, 11, 0.08);
  border: 1rpx solid rgba(245, 158, 11, 0.2);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 300ms both;
  transition: all 300ms cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    box-shadow: 
      0 12rpx 36rpx rgba(245, 158, 11, 0.2),
      0 4rpx 12rpx rgba(245, 158, 11, 0.1);
    transform: translateY(-4rpx);
  }
}

.member-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.member-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.member-text {
  display: flex;
  flex-direction: column;
}

.member-label {
  font-size: 28rpx;
  font-weight: 700;
  color: #78350F;
  letter-spacing: 1rpx;
}

.member-expire {
  font-size: 22rpx;
  color: #A16207;
  margin-top: 6rpx;
}

/* ============ 数据统计卡片 ============ */
.stats-card {
  background: #ffffff;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 32rpx 36rpx;
  box-shadow: 
    0 8rpx 24rpx rgba(15, 23, 42, 0.06),
    0 2rpx 8rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid #F1F5F9;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 400ms both;
}

.stats-header {
  margin-bottom: 28rpx;
  display: flex;
  align-items: center;
}

.stats-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
  letter-spacing: 1rpx;
}

.stats-row {
  display: flex;
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 8rpx;
  border-right: 1rpx solid #F1F5F9;
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);

  &:last-child {
    border-right: none;
  }

  &:hover {
    transform: translateY(-4rpx);
  }
}

.stat-num {
  font-size: 44rpx;
  font-weight: 800;
  color: #0F172A;
  display: block;
  letter-spacing: 1rpx;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 22rpx;
  color: #64748B;
  display: block;
  margin-top: 8rpx;
  font-weight: 500;
}

.stat-today {
  font-size: 20rpx;
  color: #10B981;
  display: block;
  margin-top: 6rpx;
  font-weight: 600;
}

/* ============ 已认证标识 ============ */
.certified-banner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%);
  margin: 0 32rpx 24rpx;
  border-radius: 20rpx;
  padding: 28rpx;
  font-size: 28rpx;
  color: #059669;
  font-weight: 600;
  border: 1rpx solid #A7F3D0;
  box-shadow: 0 4rpx 16rpx rgba(16, 185, 129, 0.1);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 450ms both;
}

/* ============ 功能网格 ============ */
.func-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28rpx 20rpx;
  padding: 0 32rpx;
  margin-bottom: 32rpx;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 500ms both;
}

.func-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  padding: 8rpx 0;

  &:hover {
    transform: translateY(-6rpx);

    .func-icon {
      box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.15);
    }

    .func-label {
      color: #0F172A;
    }
  }

  &:active {
    transform: translateY(-2rpx) scale(0.97);

    .func-icon {
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
    }
  }
}

.func-icon {
  width: 108rpx;
  height: 108rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.12);
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
}

.func-label {
  font-size: 24rpx;
  color: #334155;
  margin-top: 14rpx;
  text-align: center;
  font-weight: 500;
  transition: color 200ms ease;
  letter-spacing: 0.5rpx;
}

/* ============ 退出登录 ============ */
.logout-section {
  padding: 0 32rpx;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 600ms both;
}

.btn-logout {
  width: 100%;
  padding: 32rpx;
  background: #ffffff;
  color: #DC2626;
  border: 2rpx solid #FECACA;
  border-radius: 20rpx;
  font-size: 30rpx;
  font-weight: 600;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: 2rpx;
  box-shadow: 0 4rpx 12rpx rgba(220, 38, 38, 0.08);

  &:hover {
    background: #FEF2F2;
    border-color: #FCA5A5;
    transform: translateY(-2rpx);
    box-shadow: 0 8rpx 20rpx rgba(220, 38, 38, 0.15);
  }

  &:active {
    background: #FEE2E2;
    border-color: #F87171;
    transform: translateY(0) scale(0.98);
    box-shadow: 0 2rpx 8rpx rgba(220, 38, 38, 0.1);
  }
}

/* ============ 动画定义 ============ */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulseGlow {
  0%, 100% {
    box-shadow: 0 4rpx 12rpx rgba(16, 185, 129, 0.4);
  }
  50% {
    box-shadow: 
      0 4rpx 12rpx rgba(16, 185, 129, 0.6),
      0 0 20rpx rgba(16, 185, 129, 0.3);
  }
}

/* ============ 响应式适配 ============ */
@media (min-width: 376px) {
  .user-name {
    font-size: 42rpx;
  }
  .func-icon {
    width: 120rpx;
    height: 120rpx;
  }
  .user-avatar {
    width: 144rpx;
    height: 144rpx;
    border-radius: 72rpx;
  }
  .deposit-amount {
    font-size: 56rpx;
  }
}

@media (max-width: 320px) {
  .user-name {
    font-size: 32rpx;
  }
  .func-icon {
    width: 84rpx;
    height: 84rpx;
    border-radius: 20rpx;
  }
  .user-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50rpx;
  }
  .deposit-amount {
    font-size: 36rpx;
  }
  .func-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 20rpx 12rpx;
  }
  .stat-num {
    font-size: 32rpx;
  }
  .func-label {
    font-size: 22rpx;
  }
}

/* ============ 减少动画偏好 ============ */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>