<template>
  <view class="page-content">
    <view class="login-header">
      <view class="logo-container">
        <view class="logo-circle">
          <text class="logo-text">5D</text>
        </view>
      </view>
      <text class="app-name">5D好车</text>
      <text class="app-desc">AI赋能二手车拓展商机</text>
    </view>

    <view class="login-form">
      <view class="form-card">
        <view class="input-group">
          <view class="input-label">
            <text class="label-text">手机号</text>
            <text class="required">*</text>
          </view>
          <input
            class="ct-input"
            v-model="form.phone"
            placeholder="请输入手机号"
            type="number"
            maxlength="11"
            placeholder-class="placeholder"
          />
        </view>

        <view class="input-group">
          <view class="input-label">
            <text class="label-text">密码</text>
            <text class="required">*</text>
          </view>
          <input
            class="ct-input"
            v-model="form.password"
            placeholder="请输入密码"
            type="password"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-options">
          <view class="remember-me" @click="remember = !remember">
            <view class="checkbox" :class="{ active: remember }">
              <u-icon name="checkmark" size="12" color="#fff" v-if="remember"></u-icon>
            </view>
            <text class="remember-text">记住密码</text>
          </view>
          <text class="forgot-pwd" @click="toRegister">没有账号？去注册</text>
        </view>

        <button class="ct-btn-primary btn-login" :class="{ loading: loading }" @click="handleLogin" :disabled="loading">
          <text v-if="loading" class="loading-text">登录中...</text>
          <text v-else>登录</text>
        </button>
      </view>
    </view>

    <view class="agreement">
      <text class="agreement-text">登录即代表同意</text>
      <text class="link">《用户协议》</text>
      <text class="agreement-text">和</text>
      <text class="link">《隐私政策》</text>
    </view>
  </view>
</template>

<script>
import { isValidPhone } from '@/utils/validate'

export default {
  data() {
    return {
      form: { phone: '', password: '' },
      remember: false,
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      if (!isValidPhone(this.form.phone)) { uni.$u.toast('请输入正确手机号'); return }
      if (!this.form.password) { uni.$u.toast('请输入密码'); return }
      this.loading = true
      try {
        await this.$store.dispatch('login', this.form)
        uni.$u.toast('登录成功')
        setTimeout(() => { uni.switchTab({ url: '/pages/home/index' }) }, 500)
      } catch (e) {
        uni.$u.toast('登录失败，请检查账号密码')
      }
      this.loading = false
    },
    toRegister() {
      uni.navigateTo({ url: '/pages/register/index' })
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================
   5D Car Trade 企业级设计系统 - 登录页
   ========================================= */

.page-content {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8FAFC 0%, #E2E8F0 100%);
  display: flex;
  flex-direction: column;
  padding: 0 48rpx;
}

/* ============= 头部区域 ============= */
.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0 80rpx;
}

.logo-container {
  margin-bottom: 32rpx;
}

.logo-circle {
  width: 160rpx;
  height: 160rpx;
  border-radius: 40rpx;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 16rpx 48rpx rgba(15, 23, 42, 0.25);
}

.logo-text {
  font-size: 64rpx;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 4rpx;
}

.app-name {
  font-size: 48rpx;
  font-weight: 800;
  color: #0F172A;
  margin-top: 24rpx;
  letter-spacing: 2rpx;
}

.app-desc {
  font-size: 26rpx;
  color: #64748B;
  margin-top: 12rpx;
}

/* ============= 表单区域 ============= */
.login-form {
  flex: 1;
}

.form-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.08);
  border: 1px solid #E2E8F0;
}

.input-group {
  margin-bottom: 32rpx;
}

.input-label {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.label-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #0F172A;
}

.required {
  color: #EF4444;
  margin-left: 8rpx;
}

.ct-input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  border: 2rpx solid #E2E8F0;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #0F172A;
  background: #F8FAFC;
  transition: all 0.2s ease;
}

.ct-input:focus {
  border-color: #0369A1;
  background: #ffffff;
  box-shadow: 0 0 0 4rpx rgba(3, 105, 161, 0.1);
}

.placeholder {
  color: #94A3B8;
}

/* ============= 选项行 ============= */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0 40rpx;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 12rpx;
  cursor: pointer;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #CBD5E1;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &.active {
    background: #0369A1;
    border-color: #0369A1;
  }
}

.remember-text {
  font-size: 26rpx;
  color: #64748B;
}

.forgot-pwd {
  font-size: 26rpx;
  color: #0369A1;
  font-weight: 500;
}

/* ============= 登录按钮 ============= */
.btn-login {
  width: 100%;
  height: 96rpx;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 700;
  letter-spacing: 2rpx;
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  color: #ffffff;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(3, 105, 161, 0.3);
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  &:hover {
    box-shadow: 0 12rpx 32rpx rgba(3, 105, 161, 0.4);
  }

  &.loading {
    opacity: 0.7;
  }
}

.loading-text {
  opacity: 0.8;
}

/* ============= 协议区域 ============= */
.agreement {
  text-align: center;
  margin-top: 48rpx;
  padding-bottom: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8rpx;
}

.agreement-text {
  font-size: 24rpx;
  color: #64748B;
}

.link {
  font-size: 24rpx;
  color: #0369A1;
  font-weight: 500;
}

/* ============= 可访问性 ============= */
*:focus-visible {
  outline: 2px solid #0369A1;
  outline-offset: 2px;
}
</style>