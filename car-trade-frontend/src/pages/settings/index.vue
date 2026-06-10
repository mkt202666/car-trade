<template>
  <view class="page">
    <u-navbar title="系统设置" :border-bottom="false" :placeholder="true">
      <template v-slot:left>
        <view class="nav-back" @click="goBack">
          <u-icon name="arrow-left" size="32" color="#333"></u-icon>
        </view>
      </template>
    </u-navbar>

    <view class="page-content">
      <!-- 账号设置 -->
      <view class="section">
        <view class="section-title">账号设置</view>
        <view class="setting-list">
          <view class="setting-item" @click="editProfile">
            <view class="setting-left">
              <u-icon name="account" size="32" color="#0369A1"></u-icon>
              <text class="setting-name">个人信息</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
          <view class="setting-item" @click="changePassword">
            <view class="setting-left">
              <u-icon name="lock" size="32" color="#10B981"></u-icon>
              <text class="setting-name">修改密码</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
          <view class="setting-item" @click="changePhone">
            <view class="setting-left">
              <u-icon name="phone" size="32" color="#F59E0B"></u-icon>
              <text class="setting-name">更换手机号</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
        </view>
      </view>

      <!-- 通知设置 -->
      <view class="section">
        <view class="section-title">通知设置</view>
        <view class="setting-list">
          <view class="setting-item">
            <view class="setting-left">
              <u-icon name="bell" size="32" color="#7C3AED"></u-icon>
              <text class="setting-name">消息通知</text>
            </view>
            <switch :checked="settings.messageNotify" @change="settings.messageNotify = $event.detail.value" color="#0369A1" />
          </view>
          <view class="setting-item">
            <view class="setting-left">
              <u-icon name="sound" size="32" color="#EF4444"></u-icon>
              <text class="setting-name">声音提醒</text>
            </view>
            <switch :checked="settings.soundNotify" @change="settings.soundNotify = $event.detail.value" color="#0369A1" />
          </view>
          <view class="setting-item">
            <view class="setting-left">
              <u-icon name="vibrate" size="32" color="#06B6D4"></u-icon>
              <text class="setting-name">震动提醒</text>
            </view>
            <switch :checked="settings.vibrateNotify" @change="settings.vibrateNotify = $event.detail.value" color="#0369A1" />
          </view>
        </view>
      </view>

      <!-- 隐私设置 -->
      <view class="section">
        <view class="section-title">隐私设置</view>
        <view class="setting-list">
          <view class="setting-item">
            <view class="setting-left">
              <u-icon name="eye" size="32" color="#F59E0B"></u-icon>
              <text class="setting-name">公开我的车源</text>
            </view>
            <switch :checked="settings.publicCars" @change="settings.publicCars = $event.detail.value" color="#0369A1" />
          </view>
          <view class="setting-item">
            <view class="setting-left">
              <u-icon name="people" size="32" color="#10B981"></u-icon>
              <text class="setting-name">允许他人关注</text>
            </view>
            <switch :checked="settings.allowFollow" @change="settings.allowFollow = $event.detail.value" color="#0369A1" />
          </view>
        </view>
      </view>

      <!-- 通用设置 -->
      <view class="section">
        <view class="section-title">通用设置</view>
        <view class="setting-list">
          <view class="setting-item" @click="clearCache">
            <view class="setting-left">
              <u-icon name="trash" size="32" color="#EF4444"></u-icon>
              <text class="setting-name">清除缓存</text>
            </view>
            <view class="setting-right">
              <text class="setting-value">{{ cacheSize }}</text>
              <u-icon name="arrow-right" size="24" color="#999"></u-icon>
            </view>
          </view>
          <view class="setting-item" @click="aboutUs">
            <view class="setting-left">
              <u-icon name="info-circle" size="32" color="#0369A1"></u-icon>
              <text class="setting-name">关于我们</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
          <view class="setting-item" @click="userAgreement">
            <view class="setting-left">
              <u-icon name="file-text" size="32" color="#7C3AED"></u-icon>
              <text class="setting-name">用户协议</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
          <view class="setting-item" @click="privacyPolicy">
            <view class="setting-left">
              <u-icon name="shield" size="32" color="#10B981"></u-icon>
              <text class="setting-name">隐私政策</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
        </view>
      </view>

      <!-- 退出登录 -->
      <view class="section">
        <view class="logout-btn" @click="handleLogout">
          <text>退出登录</text>
        </view>
      </view>

      <!-- 版本信息 -->
      <view class="version-info">
        <text class="version-text">5D好车 v1.0.0</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      settings: {
        messageNotify: true,
        soundNotify: true,
        vibrateNotify: false,
        publicCars: true,
        allowFollow: true
      },
      cacheSize: '12.5MB'
    }
  },
  onLoad() {
    this.loadSettings()
  },
  methods: {
    loadSettings() {
      try {
        const saved = uni.getStorageSync('app_settings')
        if (saved) {
          this.settings = { ...this.settings, ...saved }
        }
      } catch (e) {}
    },
    saveSettings() {
      try {
        uni.setStorageSync('app_settings', this.settings)
      } catch (e) {}
    },
    editProfile() {
      uni.navigateTo({ url: '/pages/profile/index' })
    },
    changePassword() {
      uni.$u.toast('修改密码功能开发中')
    },
    changePhone() {
      uni.$u.toast('更换手机号功能开发中')
    },
    clearCache() {
      uni.showModal({
        title: '确认清除',
        content: '确定要清除缓存吗？',
        success: (res) => {
          if (res.confirm) {
            try {
              uni.clearStorageSync()
              this.cacheSize = '0MB'
              uni.$u.toast('缓存已清除')
            } catch (e) {
              uni.$u.toast('清除失败')
            }
          }
        }
      })
    },
    aboutUs() {
      uni.$u.toast('关于我们功能开发中')
    },
    userAgreement() {
      uni.$u.toast('用户协议功能开发中')
    },
    privacyPolicy() {
      uni.$u.toast('隐私政策功能开发中')
    },
    handleLogout() {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            this.$store.commit('logout')
            uni.$u.toast('已退出登录')
            setTimeout(() => {
              uni.reLaunch({ url: '/pages/login/index' })
            }, 500)
          }
        }
      })
    },
    goBack() {
      this.saveSettings()
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

/* 设置列表 */
.setting-list {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid $border-color;
  cursor: pointer;
  transition: $transition;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}

.setting-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.setting-name {
  font-size: 28rpx;
  color: $text-color;
}

.setting-right {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.setting-value {
  font-size: 26rpx;
  color: $text-secondary;
}

/* 退出登录 */
.logout-btn {
  display: flex;
  justify-content: center;
  padding: 24rpx;
  background: #FEF2F2;
  border-radius: 12rpx;
  cursor: pointer;
  transition: $transition;

  &:active {
    background: #FEE2E2;
  }

  text {
    font-size: 28rpx;
    color: #EF4444;
    font-weight: 600;
  }
}

/* 版本信息 */
.version-info {
  text-align: center;
  padding: 40rpx 0;
}

.version-text {
  font-size: 24rpx;
  color: $text-secondary;
}
</style>
