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
          <view class="setting-item" @click="showPasswordPopup = true">
            <view class="setting-left">
              <u-icon name="lock" size="32" color="#10B981"></u-icon>
              <text class="setting-name">修改密码</text>
            </view>
            <u-icon name="arrow-right" size="24" color="#999"></u-icon>
          </view>
          <view class="setting-item" @click="showPhonePopup = true">
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

    <!-- 修改密码弹窗 -->
    <u-popup :show="showPasswordPopup" mode="bottom" round="24" @close="showPasswordPopup = false">
      <view class="popup-content">
        <view class="popup-title">修改密码</view>
        <view class="popup-form">
          <u-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" :border="'surround'" v-if="hasPassword"></u-input>
          <u-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" :border="'surround'"></u-input>
          <u-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" :border="'surround'"></u-input>
        </view>
        <view class="popup-btn" @click="submitPassword">确认修改</view>
      </view>
    </u-popup>

    <!-- 更换手机号弹窗 -->
    <u-popup :show="showPhonePopup" mode="bottom" round="24" @close="showPhonePopup = false">
      <view class="popup-content">
        <view class="popup-title">更换手机号</view>
        <view class="popup-form">
          <u-input v-model="phoneForm.newPhone" type="number" placeholder="请输入新手机号" :border="'surround'" maxlength="11"></u-input>
        </view>
        <view class="popup-btn" @click="submitPhone">确认更换</view>
      </view>
    </u-popup>

    <!-- 关于我们弹窗 -->
    <u-popup :show="showAbout" mode="center" round="24" @close="showAbout = false">
      <view class="about-content">
        <view class="about-title">5D好车</view>
        <view class="about-version">v1.0.0</view>
        <view class="about-desc">AI赋能的B2B二手车交易平台</view>
        <view class="about-info">
          <text>专注二手车交易，提供找车、发布、交易、合同签署一站式服务</text>
        </view>
        <view class="about-contact">
          <text>客服热线：400-XXX-XXXX</text>
          <text>邮箱：support@5dcar.com</text>
        </view>
        <view class="popup-btn" @click="showAbout = false">知道了</view>
      </view>
    </u-popup>

    <!-- 用户协议/隐私政策弹窗 -->
    <u-popup :show="showAgreement" mode="center" round="24" @close="showAgreement = false">
      <view class="agreement-content">
        <view class="popup-title">{{ agreementTitle }}</view>
        <scroll-view scroll-y class="agreement-scroll">
          <view class="agreement-text" v-html="agreementContent"></view>
        </scroll-view>
        <view class="popup-btn" @click="showAgreement = false">我已阅读</view>
      </view>
    </u-popup>
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
      cacheSize: '12.5MB',
      showPasswordPopup: false,
      showPhonePopup: false,
      showAbout: false,
      showAgreement: false,
      hasPassword: false,
      agreementTitle: '',
      agreementContent: '',
      passwordForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      phoneForm: { newPhone: '' }
    }
  },
  onLoad() {
    this.loadSettings()
  },
  methods: {
    loadSettings() {
      try {
        const saved = uni.getStorageSync('app_settings')
        if (saved) this.settings = { ...this.settings, ...saved }
      } catch (e) {}
    },
    saveSettings() {
      try { uni.setStorageSync('app_settings', this.settings) } catch (e) {}
    },
    editProfile() {
      uni.navigateTo({ url: '/pages/profile/index' })
    },
    async submitPassword() {
      const { oldPassword, newPassword, confirmPassword } = this.passwordForm
      if (!newPassword || newPassword.length < 6) { uni.$u.toast('新密码长度不能少于6位'); return }
      if (newPassword !== confirmPassword) { uni.$u.toast('两次密码不一致'); return }
      try {
        await uni.http.put('/users/me/password', { oldPassword, newPassword })
        uni.$u.toast('密码修改成功')
        this.showPasswordPopup = false
        this.passwordForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
      } catch (e) { console.error(e) }
    },
    async submitPhone() {
      const { newPhone } = this.phoneForm
      if (!newPhone || newPhone.length < 11) { uni.$u.toast('请输入正确手机号'); return }
      try {
        await uni.http.put('/users/me/phone', { phone: newPhone })
        uni.$u.toast('手机号更换成功')
        this.showPhonePopup = false
        this.phoneForm = { newPhone: '' }
      } catch (e) { console.error(e) }
    },
    clearCache() {
      uni.showModal({
        title: '确认清除', content: '确定要清除缓存吗？',
        success: (res) => {
          if (res.confirm) {
            try { uni.clearStorageSync(); this.cacheSize = '0MB'; uni.$u.toast('缓存已清除') } catch (e) { uni.$u.toast('清除失败') }
          }
        }
      })
    },
    aboutUs() { this.showAbout = true },
    userAgreement() {
      this.agreementTitle = '用户协议'
      this.agreementContent = '<p><strong>《5D找车使用协议》</strong></p><p>欢迎您使用5D好车平台。在使用本平台服务前，请仔细阅读以下协议条款。</p><p>1. 本平台为二手车B2B交易信息服务平台，提供车源发布、交易撮合、电子合同签署等服务。</p><p>2. 用户注册时需提供真实有效的身份信息和企业资质。</p><p>3. 发布车源需符合平台《车源发布规范》，确保车辆信息真实准确。</p><p>4. 交易过程中使用平台定金保障服务，享受全程争议追溯。</p><p>5. 严禁发布虚假车源、恶意刷单等违规行为。</p><p>6. 本平台保留对违规账号进行处罚的权利。</p>'
      this.showAgreement = true
    },
    privacyPolicy() {
      this.agreementTitle = '隐私政策'
      this.agreementContent = '<p><strong>《5D找车隐私政策》</strong></p><p>我们非常重视您的隐私保护。</p><p>1. 我们收集的信息：手机号、昵称、车行信息、车辆信息等必要数据。</p><p>2. 信息使用目的：提供平台服务、交易撮合、信用评估。</p><p>3. 信息安全：采用加密存储和传输，严格控制数据访问权限。</p><p>4. 信息共享：未经您同意，不会向第三方共享您的个人信息。</p><p>5. 您有权随时查看、修改或删除您的个人信息。</p><p>6. 如有疑问，请联系客服：400-XXX-XXXX。</p>'
      this.showAgreement = true
    },
    handleLogout() {
      uni.showModal({
        title: '确认退出', content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            this.$store.commit('logout')
            uni.$u.toast('已退出登录')
            setTimeout(() => uni.reLaunch({ url: '/pages/login/index' }), 500)
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

.popup-content, .about-content, .agreement-content {
  padding: 40rpx 30rpx;
}
.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  text-align: center;
  margin-bottom: 30rpx;
}
.popup-form {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-bottom: 30rpx;
}
.popup-btn {
  background: $cta-color;
  color: #fff;
  text-align: center;
  padding: 24rpx 0;
  border-radius: 12rpx;
  font-size: 30rpx;
  font-weight: 600;
}
.about-title {
  font-size: 40rpx;
  font-weight: 700;
  text-align: center;
  color: $text-color;
}
.about-version {
  font-size: 26rpx;
  color: $text-secondary;
  text-align: center;
  margin-top: 8rpx;
}
.about-desc {
  font-size: 28rpx;
  color: $cta-color;
  text-align: center;
  margin: 20rpx 0;
}
.about-info {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: 20rpx;
}
.about-contact {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  font-size: 24rpx;
  color: $text-secondary;
  margin-bottom: 30rpx;
}
.agreement-scroll {
  max-height: 600rpx;
  margin-bottom: 30rpx;
}
.agreement-text {
  font-size: 26rpx;
  color: $text-color;
  line-height: 1.8;
}
</style>
