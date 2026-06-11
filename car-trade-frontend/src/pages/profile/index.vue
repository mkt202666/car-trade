<template>
  <view class="page">
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
      <!-- 用户信息卡片（深色背景） -->
      <view class="user-card">
        <view class="user-top">
          <view class="avatar-container">
            <image :src="user.avatar || '/static/default-avatar.png'" mode="aspectFill" class="user-avatar"></image>
          </view>
          <view class="user-info">
            <view class="user-name-row">
              <text class="user-name">{{ user.nickname }}</text>
            </view>
            <text class="user-phone">{{ user.phone }}</text>
            <view class="user-actions">
              <view class="action-btn edit" @click="editProfile">
                <u-icon name="edit-pen" size="24" color="#fff"></u-icon>
                <text>修改资料</text>
              </view>
              <view class="action-btn home" @click="toMyHome">
                <text>我的主页</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 角色/车行/成交信息 -->
        <view class="user-meta">
          <view class="meta-item">
            <text class="meta-label">角色:</text>
            <text class="meta-value">{{ user.role }}</text>
          </view>
          <view class="meta-item">
            <text class="meta-label">车行:</text>
            <text class="meta-value">{{ user.shopName || '暂无' }}</text>
          </view>
          <view class="meta-item">
            <text class="meta-label">成交:</text>
            <text class="meta-value">{{ user.dealCount }}次</text>
          </view>
          <view class="credit-badge" v-if="user.creditGrade">
            <text>信用 {{ user.creditGrade }}{{ user.creditLabel }}</text>
          </view>
        </view>

        <!-- 保证金信息（在卡片内） -->
        <view class="deposit-inline">
          <view class="deposit-left">
            <text class="deposit-label">可用保证金</text>
            <view class="deposit-amount-row">
              <text class="deposit-amount">{{ formatAmount(user.deposit.balance) }}</text>
              <text class="deposit-total">/ {{ formatAmount(user.deposit.total) }}</text>
            </view>
          </view>
          <view class="deposit-arrow">
            <u-icon name="arrow-right" size="28" color="#94A3B8"></u-icon>
          </view>
        </view>
      </view>

      <!-- 数据统计（紧凑布局） -->
      <view class="stats-card">
        <view class="stats-row">
          <view class="stat-item main">
            <text class="stat-num big">{{ user.stats.carCount }}</text>
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
      </view>

      <!-- 认证车商功能 -->
      <view class="section-card certified-section" v-if="user.certificationStatus === 'CERTIFIED'">
        <view class="section-header">
          <text class="section-title">认证车商功能</text>
          <view class="certified-badge">
            <u-icon name="checkmark-circle-fill" size="28" color="#10B981"></u-icon>
            <text>已认证商家</text>
          </view>
        </view>
        <view class="certified-grid">
          <view class="certified-item" @click="menuClick(certifiedMenus[0])">
            <view class="certified-icon">
              <u-icon name="car" size="48" color="#64748B"></u-icon>
            </view>
            <text class="certified-label">我的车源</text>
          </view>
          <view class="certified-item" @click="menuClick(certifiedMenus[1])">
            <view class="certified-icon">
              <u-icon name="share" size="48" color="#64748B"></u-icon>
            </view>
            <text class="certified-label">AI分发车源</text>
          </view>
          <view class="certified-item" @click="menuClick(certifiedMenus[2])">
            <view class="certified-icon">
              <u-icon name="file-text" size="48" color="#64748B"></u-icon>
            </view>
            <text class="certified-label">AI行情简报</text>
          </view>
        </view>
      </view>

      <!-- 全部功能 -->
      <view class="section-card">
        <view class="section-header">
          <text class="section-title">全部功能</text>
        </view>
        <view class="func-grid">
          <view class="func-item" v-for="item in funcMenus" :key="item.label" @click="menuClick(item)">
            <view class="func-icon">
              <u-icon :name="item.icon" size="40" :color="item.color || '#64748B'"></u-icon>
            </view>
            <text class="func-label">{{ item.label }}</text>
          </view>
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
      certifiedMenus: [
        { label: '我的车源', icon: 'car', bg: 'linear-gradient(135deg, #0369A1, #0284C7)', page: 'publish', params: { tab: 'mine' } },
        { label: 'AI分发车源', icon: 'share', bg: 'linear-gradient(135deg, #7C3AED, #6D28D9)', page: 'ai', params: { mode: 'distribute' } },
        { label: 'AI行情简报', icon: 'file-text', bg: 'linear-gradient(135deg, #F59E0B, #D97706)', page: 'ai-market-report', params: {} }
      ],
      funcMenus: [
        { label: '收藏车源', icon: 'heart', color: '#EF4444', page: 'favorites', params: {} },
        { label: '我的车行', icon: 'shop', color: '#10B981', page: 'seller-home', params: { id: this.user && this.user.id } },
        { label: '浏览记录', icon: 'clock', color: '#06B6D4', page: 'browsing-history', params: {} },
        { label: '我的交易', icon: 'order', color: '#0369A1', page: 'trade', params: {} },
        { label: '金融服务', icon: 'rmb-circle', color: '#F59E0B', page: 'finance', params: {} },
        { label: '电子合同', icon: 'file-text', color: '#10B981', page: 'contract-detail', params: {} },
        { label: '我的关注', icon: 'star', color: '#EF4444', page: 'follows', params: {} },
        { label: '交易规范', icon: 'info-circle', color: '#06B6D4', page: 'trade-rules', params: {} },
        { label: '客服帮助', icon: 'kefu-ermai', color: '#10B981', page: 'customer-service', params: {} },
        { label: '系统设置', icon: 'setting', color: '#64748B', page: 'settings', params: {} }
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
        // Load demo data when not logged in
        this.loadDemoData()
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
    loadDemoData() {
      // Demo data matching prototype
      this.user = {
        nickname: '华仔 (刘德华)',
        phone: '13066668888',
        avatar: '',
        role: '车商',
        shopName: '天津5D好车',
        creditGrade: 'S',
        creditLabel: '优秀',
        dealCount: 10,
        certificationStatus: 'CERTIFIED',
        deposit: {
          balance: 3000,
          total: 4200
        },
        stats: {
          carCount: 4,
          viewCount: 34010,
          viewCountToday: 231,
          contactCount: 998,
          contactCountToday: 45,
          followerCount: 120,
          followerCountToday: 3
        }
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
  padding: 40rpx 32rpx;
  color: #fff;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.2);
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
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  border: 4rpx solid rgba(245, 158, 11, 0.6);
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.2);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.user-name {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
}

.user-phone {
  font-size: 24rpx;
  opacity: 0.8;
  display: block;
  margin-top: 6rpx;
}

.user-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 16rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 10rpx 20rpx;
  border-radius: 24rpx;
  font-size: 22rpx;
  font-weight: 500;
  
  &.edit {
    background: rgba(255, 255, 255, 0.15);
    color: #fff;
    border: 1rpx solid rgba(255, 255, 255, 0.2);
  }
  
  &.home {
    background: rgba(245, 158, 11, 0.2);
    color: #FCD34D;
    border: 1rpx solid rgba(245, 158, 11, 0.3);
  }
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.1);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.meta-label {
  font-size: 24rpx;
  opacity: 0.7;
}

.meta-value {
  font-size: 24rpx;
  font-weight: 600;
  color: #FCD34D;
}

.credit-badge {
  display: inline-flex;
  align-items: center;
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  background: rgba(245, 158, 11, 0.2);
  color: #FCD34D;
  font-weight: 600;
  margin-left: 12rpx;
}

.deposit-inline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.1);
}

.deposit-left {
  display: flex;
  flex-direction: column;
}

.deposit-label {
  font-size: 22rpx;
  opacity: 0.7;
}

.deposit-amount-row {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
  margin-top: 8rpx;
}

.deposit-amount {
  font-size: 44rpx;
  font-weight: 800;
  color: #FCD34D;
}

.deposit-total {
  font-size: 24rpx;
  opacity: 0.7;
}

.deposit-arrow {
  opacity: 0.5;
}

/* ============ 保证金卡片 ============ */
.deposit-card {
  background: #ffffff;
  margin: 28rpx 32rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 
    0 8rpx 24rpx rgba(15, 23, 42, 0.06),
    0 2rpx 8rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid #F1F5F9;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 200ms both;
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

.deposit-amount-row {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
  margin-top: 12rpx;
}

.deposit-amount {
  font-size: 52rpx;
  font-weight: 800;
  color: #0F172A;
  letter-spacing: 1rpx;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.deposit-total {
  font-size: 24rpx;
  color: #94A3B8;
}

.deposit-badge {
  background: #F0F9FF;
  padding: 12rpx 24rpx;
  border-radius: 12rpx;
  border: 1rpx solid rgba(3, 105, 161, 0.15);
}

.deposit-badge-text {
  font-size: 24rpx;
  color: #0369A1;
  font-weight: 600;
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
  justify-content: space-between;
  background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%);
  margin: 0 32rpx 24rpx;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  border: 1rpx solid #A7F3D0;
  box-shadow: 0 4rpx 16rpx rgba(16, 185, 129, 0.1);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 450ms both;
}

.certified-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.certified-label {
  font-size: 28rpx;
  color: #059669;
  font-weight: 600;
}

.certified-status {
  font-size: 24rpx;
  color: #059669;
  font-weight: 500;
}

/* ============ 认证车商功能网格 ============ */
.certified-grid {
  display: flex;
  gap: 24rpx;
}

.certified-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 16rpx;
  background: #F8FAFC;
  border-radius: 16rpx;
  border: 1rpx solid #E2E8F0;
}

.certified-icon {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 50%;
  margin-bottom: 12rpx;
  border: 1rpx solid #E2E8F0;
}

.certified-label {
  font-size: 24rpx;
  color: #334155;
  font-weight: 500;
}

/* ============ 功能网格 ============ */
.func-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 28rpx 16rpx;
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
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8FAFC;
  border: 1rpx solid #E2E8F0;
  margin-bottom: 12rpx;
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
    width: 108rpx;
    height: 108rpx;
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
    grid-template-columns: repeat(4, 1fr);
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