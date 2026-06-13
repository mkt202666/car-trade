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

    <!-- 自定义底部导航栏 -->
    <custom-tab-bar />
  </view>
</template>

<script>
import { getUserInfo, getUserStats } from '@/api/user'
import { readToken } from '@/constants/storage'
import CustomTabBar from '@/custom-tab-bar/index.vue'

export default {
  components: {
    CustomTabBar
  },
  data() {
    return {
      user: null,
      loading: false,
      certifiedMenus: [
        { label: '我的车源', icon: 'car', bg: 'linear-gradient(135deg, #0369A1, #0284C7)', page: 'my-car-source', params: {} },
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
      // 统一读取 token — 委托 constants/storage.js（兼容旧 key）
      const token = readToken()
      if (!token || token === 'null' || token === 'undefined') {
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
          uni.removeStorageSync(STORAGE_KEYS.TOKEN)
          localStorage.removeItem(STORAGE_KEYS.TOKEN)
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
   5D好车个人中心 - Liquid Glass Premium Design
   设计语言: Glassmorphism | Dimensional Layering | Motion
   颜色系统: 深蓝主色 #0F172A | 琥珀金 #F59E0B | CTA蓝 #0369A1
   ========================================================= */

.page {
  min-height: 100vh;
  background:
    radial-gradient(ellipse at top left, rgba(3, 105, 161, 0.06) 0%, transparent 50%),
    radial-gradient(ellipse at bottom right, rgba(245, 158, 11, 0.04) 0%, transparent 50%),
    linear-gradient(180deg, #F8FAFC 0%, #EEF2F7 100%);
  padding-bottom: 220rpx;
  animation: fadeIn 400ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
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
  padding: 180rpx 40rpx 80rpx;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
}

.empty-icon {
  width: 220rpx;
  height: 220rpx;
  border-radius: 68rpx;
  background: linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    inset 0 2rpx 8rpx rgba(15, 23, 42, 0.06),
    0 8rpx 32rpx rgba(15, 23, 42, 0.08);
  transition: all 400ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:active {
    transform: scale(0.96);
    box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.1);
  }
}

.empty-title {
  font-size: 38rpx;
  font-weight: 800;
  color: #0F172A;
  margin-top: 40rpx;
  letter-spacing: 1rpx;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.empty-desc {
  font-size: 26rpx;
  color: #64748B;
  margin-top: 16rpx;
  text-align: center;
  line-height: 1.7;
  max-width: 520rpx;
}

.empty-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 48rpx;
}

.btn-primary {
  padding: 28rpx 56rpx;
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  color: #ffffff;
  border: none;
  border-radius: 28rpx;
  font-size: 28rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 8rpx 24rpx rgba(3, 105, 161, 0.35), inset 0 1rpx 0 rgba(255, 255, 255, 0.2);

  &:active {
    transform: translateY(0) scale(0.97);
    box-shadow: 0 4rpx 12rpx rgba(3, 105, 161, 0.3), inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  }
}

.btn-secondary {
  padding: 28rpx 48rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10rpx);
  color: #0F172A;
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  border-radius: 28rpx;
  font-size: 28rpx;
  font-weight: 600;
  letter-spacing: 0.5rpx;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.06);

  &:active {
    transform: translateY(0) scale(0.97);
    background: #F8FAFC;
  }
}

/* ============ 加载中 ============ */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 0;
}

.loading-spinner {
  width: 72rpx;
  height: 72rpx;
  border: 4rpx solid #E2E8F0;
  border-top-color: #0369A1;
  border-radius: 50%;
  animation: spin 0.8s cubic-bezier(0.4, 0, 0.2, 1) infinite;
  box-shadow: 0 0 24rpx rgba(3, 105, 161, 0.2);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 28rpx;
  font-size: 26rpx;
  color: #64748B;
  font-weight: 500;
  letter-spacing: 1rpx;
}

/* ============ 用户卡片 Hero - Premium Dark ============ */
.user-card {
  position: relative;
  margin: 0;
  padding: 48rpx 32rpx 40rpx;
  background:
    radial-gradient(ellipse at top right, rgba(245, 158, 11, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at bottom left, rgba(3, 105, 161, 0.15) 0%, transparent 50%),
    linear-gradient(135deg, #0F172A 0%, #1E293B 50%, #334155 100%);
  color: #fff;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.25);

  &::before {
    content: '';
    position: absolute;
    top: -100rpx;
    right: -100rpx;
    width: 300rpx;
    height: 300rpx;
    background: radial-gradient(circle, rgba(245, 158, 11, 0.2) 0%, transparent 70%);
    border-radius: 50%;
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -80rpx;
    left: -80rpx;
    width: 260rpx;
    height: 260rpx;
    background: radial-gradient(circle, rgba(3, 105, 161, 0.15) 0%, transparent 70%);
    border-radius: 50%;
    pointer-events: none;
  }
}

.user-top {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 1;
}

.avatar-container {
  position: relative;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.user-avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 44rpx;
  border: 4rpx solid rgba(245, 158, 11, 0.5);
  background: linear-gradient(135deg, #334155 0%, #475569 100%);
  box-shadow:
    0 12rpx 32rpx rgba(0, 0, 0, 0.3),
    inset 0 2rpx 8rpx rgba(255, 255, 255, 0.1);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.user-name {
  font-size: 40rpx;
  font-weight: 800;
  color: #fff;
  letter-spacing: 0.5rpx;
}

.user-phone {
  font-size: 26rpx;
  opacity: 0.7;
  display: block;
  margin-bottom: 8rpx;
  font-weight: 400;
}

.user-actions {
  display: flex;
  gap: 12rpx;
  margin-top: 16rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: 600;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &.edit {
    background: rgba(255, 255, 255, 0.12);
    color: #fff;
    border: 1rpx solid rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10rpx);
  }

  &.home {
    background: linear-gradient(135deg, rgba(245, 158, 11, 0.25) 0%, rgba(245, 158, 11, 0.15) 100%);
    color: #FCD34D;
    border: 1rpx solid rgba(245, 158, 11, 0.3);
  }

  &:active {
    transform: scale(0.96);
  }
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx 24rpx;
  margin-top: 28rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.12);
  position: relative;
  z-index: 1;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.meta-label {
  font-size: 24rpx;
  opacity: 0.65;
  font-weight: 400;
}

.meta-value {
  font-size: 26rpx;
  font-weight: 700;
  color: #FCD34D;
  letter-spacing: 0.3rpx;
}

.credit-badge {
  display: inline-flex;
  align-items: center;
  font-size: 22rpx;
  padding: 8rpx 18rpx;
  border-radius: 14rpx;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.25) 0%, rgba(16, 185, 129, 0.15) 100%);
  color: #6EE7B7;
  font-weight: 700;
  border: 1rpx solid rgba(16, 185, 129, 0.3);
}

.deposit-inline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 28rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15) 0%, rgba(245, 158, 11, 0.08) 100%);
  border: 1rpx solid rgba(245, 158, 11, 0.25);
  position: relative;
  z-index: 1;
  box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.1);
}

.deposit-left {
  display: flex;
  flex-direction: column;
}

.deposit-label {
  font-size: 24rpx;
  opacity: 0.75;
  font-weight: 500;
  letter-spacing: 0.5rpx;
}

.deposit-amount-row {
  display: flex;
  align-items: baseline;
  gap: 10rpx;
  margin-top: 8rpx;
}

.deposit-amount {
  font-size: 52rpx;
  font-weight: 800;
  color: #FCD34D;
  letter-spacing: 1rpx;
  background: linear-gradient(135deg, #FCD34D 0%, #F59E0B 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.deposit-total {
  font-size: 26rpx;
  opacity: 0.7;
  color: #FDE68A;
}

.deposit-arrow {
  opacity: 0.7;
  color: #FCD34D;
}

/* ============ 数据统计卡片 - Premium ============ */
.stats-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16rpx);
  margin: 24rpx 24rpx;
  border-radius: 28rpx;
  padding: 32rpx 24rpx;
  box-shadow:
    0 4rpx 24rpx rgba(15, 23, 42, 0.08),
    0 1rpx 4rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) 200ms both;
}

.stats-row {
  display: flex;
  gap: 8rpx;
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 12rpx 8rpx;
  border-right: 1rpx solid rgba(226, 232, 240, 0.7);
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:last-child {
    border-right: none;
  }

  &.main {
    background: linear-gradient(135deg, rgba(15, 23, 42, 0.04) 0%, transparent 100%);
    border-radius: 16rpx;
    margin: -4rpx 0;
    padding: 16rpx 8rpx;
  }

  &:active {
    transform: scale(0.97);
    background: linear-gradient(135deg, rgba(248, 250, 252, 0.9) 0%, rgba(241, 245, 249, 0.7) 100%);
    border-radius: 16rpx;
  }
}

.stat-num {
  font-size: 48rpx;
  font-weight: 800;
  display: block;
  letter-spacing: 1rpx;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;

  &.big {
    font-size: 56rpx;
    background: linear-gradient(135deg, #0369A1 0%, #0EA5E9 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.stat-label {
  font-size: 22rpx;
  color: #64748B;
  display: block;
  margin-top: 10rpx;
  font-weight: 500;
  letter-spacing: 0.3rpx;
}

.stat-today {
  font-size: 20rpx;
  color: #059669;
  display: block;
  margin-top: 8rpx;
  font-weight: 600;
  padding: 2rpx 12rpx;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.12) 0%, rgba(16, 185, 129, 0.06) 100%);
  border-radius: 12rpx;
  display: inline-block;
}

/* ============ 认证车商功能区 ============ */
.section-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16rpx);
  margin: 0 24rpx 24rpx;
  border-radius: 28rpx;
  padding: 28rpx 28rpx 24rpx;
  box-shadow:
    0 4rpx 24rpx rgba(15, 23, 42, 0.08),
    0 1rpx 4rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) 300ms both;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid rgba(226, 232, 240, 0.5);
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
  letter-spacing: 0.5rpx;
}

.certified-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
  background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%);
  border-radius: 14rpx;
  border: 1rpx solid #A7F3D0;

  & text,
  & view {
    font-size: 22rpx;
    color: #059669;
    font-weight: 600;
  }
}

.certified-grid {
  display: flex;
  gap: 16rpx;
}

.certified-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 16rpx;
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  border-radius: 24rpx;
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, 0.04);

  &:active {
    transform: scale(0.96);
    background: linear-gradient(135deg, #EEF2F7 0%, #E2E8F0 100%);
  }
}

.certified-icon {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ffffff 0%, #F8FAFC 100%);
  border-radius: 28rpx;
  margin-bottom: 12rpx;
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4rpx 12rpx rgba(15, 23, 42, 0.06), inset 0 1rpx 0 rgba(255, 255, 255, 0.8);
}

.certified-label {
  font-size: 24rpx;
  color: #0F172A;
  font-weight: 600;
  letter-spacing: 0.3rpx;
  margin-top: 4rpx;
}

/* ============ 功能网格 - Premium ============ */
.func-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx 12rpx;
}

.func-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  padding: 12rpx 8rpx;
  border-radius: 20rpx;

  &:active {
    transform: scale(0.94);
    background: linear-gradient(135deg, rgba(248, 250, 252, 0.9) 0%, rgba(241, 245, 249, 0.7) 100%);
  }
}

.func-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  margin-bottom: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, 0.04), inset 0 1rpx 0 rgba(255, 255, 255, 0.8);
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
}

.func-label {
  font-size: 24rpx;
  color: #334155;
  margin-top: 4rpx;
  text-align: center;
  font-weight: 500;
  letter-spacing: 0.3rpx;
  transition: color 300ms ease;
}

/* ============ 退出登录 ============ */
.logout-section {
  padding: 16rpx 24rpx;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) 500ms both;
}

.btn-logout {
  width: 100%;
  padding: 32rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx);
  color: #DC2626;
  border: 1rpx solid rgba(254, 202, 202, 0.8);
  border-radius: 24rpx;
  font-size: 30rpx;
  font-weight: 700;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  letter-spacing: 1rpx;
  box-shadow: 0 4rpx 16rpx rgba(220, 38, 38, 0.08);

  &:active {
    transform: scale(0.98);
    background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%);
    border-color: #FCA5A5;
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
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ============ 响应式适配 ============ */
@media (min-width: 376px) {
  .user-name { font-size: 44rpx; }
  .func-icon { width: 100rpx; height: 100rpx; border-radius: 32rpx; }
  .user-avatar { width: 160rpx; height: 160rpx; border-radius: 52rpx; }
  .deposit-amount { font-size: 58rpx; }
  .stat-num { font-size: 52rpx; }
  .stat-num.big { font-size: 60rpx; }
}

@media (max-width: 320px) {
  .user-name { font-size: 32rpx; }
  .func-icon { width: 76rpx; height: 76rpx; border-radius: 24rpx; }
  .user-avatar { width: 112rpx; height: 112rpx; border-radius: 36rpx; }
  .deposit-amount { font-size: 40rpx; }
  .stat-num { font-size: 34rpx; }
  .stat-num.big { font-size: 40rpx; }
  .func-label { font-size: 22rpx; }
  .action-btn { font-size: 20rpx; padding: 10rpx 16rpx; }
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