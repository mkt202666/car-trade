<template>
  <view>
    <u-navbar title="我的" :border-bottom="false" :placeholder="true"></u-navbar>
    <view class="page-content">
      <view class="user-header" @click="toLogin">
        <image :src="user.avatar || '/static/default-avatar.png'" mode="aspectFill" class="user-avatar"></image>
        <view class="user-info" v-if="user">
          <text class="user-name">{{ user.nickname || user.phone }}</text>
          <text class="user-company" v-if="user.company">{{ user.company }}</text>
          <u-tag v-if="user.certificationStatus === 'CERTIFIED'" text="已认证" type="success" size="mini" mode="light"></u-tag>
          <u-tag v-else text="未认证" type="warning" size="mini" mode="light" @click="goCertify"></u-tag>
        </view>
        <view class="user-info" v-else>
          <text class="user-name">登录/注册</text>
          <text class="user-company">登录后体验更多功能</text>
        </view>
        <u-icon name="arrow-right" size="28" color="#999"></u-icon>
      </view>
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">{{ stats.favorites || 0 }}</text>
          <text class="stat-label">收藏</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ stats.browsing || 0 }}</text>
          <text class="stat-label">浏览</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ stats.orders || 0 }}</text>
          <text class="stat-label">订单</text>
        </view>
        <view class="stat-item">
          <text class="stat-num">{{ stats.followers || 0 }}</text>
          <text class="stat-label">粉丝</text>
        </view>
      </view>
      <view class="menu-section">
        <u-cell-group :border="false">
          <u-cell-item title="我的收藏" icon="heart" icon-color="#f56c6c" @click="toPage('')"></u-cell-item>
          <u-cell-item title="浏览历史" icon="clock" icon-color="#3c9cff" @click="toPage('')"></u-cell-item>
          <u-cell-item title="我的车源" icon="car" icon-color="#5ac725" @click="toPage('publish')"></u-cell-item>
          <u-cell-item title="交易管理" icon="order" icon-color="#f9ae3d" @click="switchTab(1)"></u-cell-item>
          <u-cell-item title="保证金" icon="rmb-circle" icon-color="#3c9cff" @click="toPage('')"></u-cell-item>
          <u-cell-item title="会员中心" icon="vip-card" icon-color="#f9ae3d" @click="toPage('')"></u-cell-item>
          <u-cell-item title="店铺管理" icon="shop" icon-color="#5ac725" @click="toPage('')"></u-cell-item>
          <u-cell-item title="在线客服" icon="kefu-ermai" icon-color="#3c9cff" @click="toPage('customer-service')"></u-cell-item>
        </u-cell-group>
      </view>
      <view class="logout-section" v-if="user">
        <u-button type="error" plain @click="handleLogout" shape="circle">退出登录</u-button>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserStats } from '@/api/user'

export default {
  data() {
    return {
      stats: {}
    }
  },
  computed: {
    user() {
      return this.$store.state.user
    }
  },
  onShow() {
    if (this.$store.getters.isLoggedIn) {
      this.loadStats()
    }
  },
  methods: {
    async loadStats() {
      try {
        const res = await getUserStats()
        this.stats = res.data || {}
      } catch (e) {}
    },
    toLogin() {
      if (!this.$store.getters.isLoggedIn) {
        uni.navigateTo({ url: '/pages/login/index' })
      }
    },
    goCertify() {
      uni.$u.toast('认证功能开发中')
    },
    switchTab(index) {
      uni.switchTab({ url: '/pages/trade/index' })
    },
    toPage(name) {
      if (name) {
        uni.navigateTo({ url: '/pages/' + name + '/index' })
      } else {
        uni.$u.toast('功能开发中')
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
.page-content { min-height: 100vh; background: #f5f5f5; }
.user-header {
  display: flex; align-items: center; padding: 60rpx 30rpx 40rpx;
  background: linear-gradient(135deg, #3c9cff, #2979ff); color: #fff;
}
.user-avatar { width: 120rpx; height: 120rpx; border-radius: 50%; border: 4rpx solid rgba(255,255,255,0.5); margin-right: 24rpx; }
.user-info { flex: 1; }
.user-name { font-size: 36rpx; font-weight: 700; display: block; }
.user-company { font-size: 24rpx; opacity: 0.8; display: block; margin-top: 6rpx; }
.stats-row {
  display: flex; background: #fff; margin: 0 30rpx; border-radius: 16rpx;
  padding: 30rpx 0; margin-top: -20rpx; position: relative; z-index: 2;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}
.stat-item { flex: 1; text-align: center; }
.stat-num { font-size: 36rpx; font-weight: 700; color: #333; display: block; }
.stat-label { font-size: 22rpx; color: #999; display: block; margin-top: 6rpx; }
.menu-section { margin: 30rpx; border-radius: 16rpx; overflow: hidden; }
.logout-section { padding: 30rpx; }
</style>
