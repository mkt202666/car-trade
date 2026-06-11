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

        <!-- 错误提示 -->
        <view class="error-tip" v-if="errorTip">
          <text class="error-text">{{ errorTip }}</text>
        </view>

        <!-- 测试账号提示 -->
        <view class="demo-tip">
          <view class="demo-header">
            <text class="demo-title">🎯 测试账号（开发演示用）</text>
          </view>
          <view class="demo-list">
            <view class="demo-item">
              <text class="demo-phone">13800138001</text>
              <text class="demo-desc">诚信车行（车行主）</text>
            </view>
            <view class="demo-item">
              <text class="demo-phone">13800138002</text>
              <text class="demo-desc">车坊张老板</text>
            </view>
          </view>
          <text class="demo-password">默认密码：123456（或输入任意内容）</text>
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
  name: 'LoginPage',
  data() {
    return {
      form: { phone: '', password: '' },
      remember: false,
      loading: false,
      errorTip: ''
    }
  },
  methods: {
    async handleLogin() {
      this.errorTip = ''
      if (!isValidPhone(this.form.phone)) {
        this.errorTip = '请输入正确的手机号'
        uni.$u.toast(this.errorTip)
        return
      }
      if (!this.form.password) {
        this.errorTip = '请输入密码'
        uni.$u.toast(this.errorTip)
        return
      }
      this.loading = true
      try {
        await this.$store.dispatch('login', this.form)
        uni.$u.toast('登录成功，欢迎回来')
        setTimeout(() => {
          uni.switchTab({ url: '/pages/home/index' })
        }, 500)
      } catch (e) {
        // 兼容多种错误结构：前端拦截 error / 后端返回 ApiResponse 的 message
        let msg = '登录失败，请稍后重试'
        if (e && typeof e === 'string') msg = e
        else if (e && e.data && e.data.message) msg = e.data.message
        else if (e && e.message) msg = e.message
        else if (e && e.errMsg) msg = e.errMsg
        this.errorTip = msg
        uni.$u.toast(msg)
      } finally {
        this.loading = false
      }
    },
    toRegister() {
      uni.navigateTo({ url: '/pages/register/index' })
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================================
   5D好车登录页 - 高级设计系统样式
   设计语言: Motion-Driven | Marketplace
   颜色系统: 深蓝主色 + 深色层次 + 琥珀金点缀
   ========================================================= */

.page-content {
  min-height: 100vh;
  background: 
    radial-gradient(ellipse at top, #F8FAFC 0%, #F1F5F9 50%, #E2E8F0 100%);
  display: flex;
  flex-direction: column;
  padding: 0 48rpx;
  animation: fadeIn 600ms cubic-bezier(0.4, 0, 0.2, 1) both;
}

/* ============ 头部区域 Hero ============ */
.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0 80rpx;
  animation: fadeInUp 600ms cubic-bezier(0.4, 0, 0.2, 1) 100ms both;
}

.logo-container {
  margin-bottom: 32rpx;
  perspective: 1000rpx;
}

.logo-circle {
  width: 160rpx;
  height: 160rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 50%, #334155 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 
    0 20rpx 60rpx rgba(15, 23, 42, 0.35),
    0 4rpx 12rpx rgba(15, 23, 42, 0.15),
    inset 0 1rpx 0 rgba(255, 255, 255, 0.1);
  border: 2rpx solid rgba(255, 255, 255, 0.2);
  transition: all 400ms cubic-bezier(0.34, 1.56, 0.64, 1);
  animation: pulseGlow 3s ease-in-out infinite;

  &:hover {
    transform: scale(1.08) rotate(-2deg);
    box-shadow: 
      0 24rpx 80rpx rgba(15, 23, 42, 0.4),
      0 8rpx 20rpx rgba(15, 23, 42, 0.2),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.15);
  }

  &:active {
    transform: scale(1.02) rotate(0deg);
  }
}

.logo-text {
  font-size: 64rpx;
  font-weight: 900;
  color: #ffffff;
  letter-spacing: 4rpx;
  background: linear-gradient(180deg, #ffffff 0%, #E2E8F0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.3);
}

.app-name {
  font-size: 48rpx;
  font-weight: 800;
  color: #0F172A;
  margin-top: 24rpx;
  letter-spacing: 2rpx;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.app-desc {
  font-size: 26rpx;
  color: #64748B;
  margin-top: 12rpx;
  letter-spacing: 1rpx;
}

/* ============ 表单区域 ============ */
.login-form {
  flex: 1;
  animation: fadeInUp 600ms cubic-bezier(0.4, 0, 0.2, 1) 200ms both;
}

.form-card {
  background: #ffffff;
  border-radius: 32rpx;
  padding: 48rpx 40rpx;
  box-shadow: 
    0 8rpx 32rpx rgba(15, 23, 42, 0.08),
    0 2rpx 8rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid #F1F5F9;
  transition: all 400ms cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    box-shadow: 
      0 12rpx 48rpx rgba(15, 23, 42, 0.12),
      0 4rpx 12rpx rgba(15, 23, 42, 0.06);
  }
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
  letter-spacing: 0.5rpx;
}

.required {
  color: #DC2626;
  margin-left: 8rpx;
  font-weight: 700;
}

.ct-input {
  width: 100%;
  height: 96rpx;
  padding: 0 28rpx;
  border: 2rpx solid #E2E8F0;
  border-radius: 20rpx;
  font-size: 28rpx;
  color: #0F172A;
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;

  &:focus,
  &:active {
    border-color: #0369A1;
    background: #ffffff;
    box-shadow: 
      0 0 0 6rpx rgba(3, 105, 161, 0.1),
      0 4rpx 12rpx rgba(3, 105, 161, 0.08);
    transform: translateY(-2rpx);
  }

  &:hover:not(:focus) {
    border-color: #CBD5E1;
    background: #ffffff;
  }
}

.placeholder {
  color: #94A3B8;
  font-weight: 400;
}

/* ============ 选项行 ============ */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 0 44rpx;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 12rpx;
  cursor: pointer;
  transition: transform 200ms cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateX(-2rpx);
  }

  &:active {
    transform: translateX(0) scale(0.98);
  }
}

.checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #CBD5E1;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 200ms cubic-bezier(0.34, 1.56, 0.64, 1);
  background: #ffffff;

  &.active {
    background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
    border-color: #0369A1;
    box-shadow: 0 4rpx 12rpx rgba(3, 105, 161, 0.3);
    transform: scale(1.05);
  }

  &:hover:not(.active) {
    border-color: #0369A1;
    background: #F0F9FF;
  }
}

.remember-text {
  font-size: 26rpx;
  color: #64748B;
  font-weight: 500;
}

.forgot-pwd {
  font-size: 26rpx;
  color: #0369A1;
  font-weight: 600;
  cursor: pointer;
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    color: #075985;
    transform: translateX(2rpx);
  }

  &:active {
    transform: translateX(0) scale(0.98);
  }
}

/* ============ 登录按钮 ============ */
.btn-login {
  width: 100%;
  height: 104rpx;
  border-radius: 52rpx;
  font-size: 32rpx;
  font-weight: 700;
  letter-spacing: 3rpx;
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 50%, #0EA5E9 100%);
  color: #ffffff;
  border: none;
  box-shadow: 
    0 10rpx 32rpx rgba(3, 105, 161, 0.35),
    0 4rpx 12rpx rgba(3, 105, 161, 0.2);
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-4rpx);
    box-shadow: 
      0 16rpx 48rpx rgba(3, 105, 161, 0.4),
      0 6rpx 16rpx rgba(3, 105, 161, 0.25);
  }

  &:active {
    transform: translateY(-2rpx) scale(0.98);
    box-shadow: 
      0 6rpx 20rpx rgba(3, 105, 161, 0.3),
      0 2rpx 8rpx rgba(3, 105, 161, 0.2);
  }

  &.loading {
    opacity: 0.8;
    cursor: not-allowed;
  }
}

/* ============ 错误提示 ============ */
.error-tip {
  margin: 24rpx 0 12rpx;
  padding: 20rpx 24rpx;
  background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%);
  border-radius: 16rpx;
  border: 2rpx solid #FECACA;
  animation: fadeInUp 300ms ease;
}

.error-text {
  font-size: 26rpx;
  color: #DC2626;
  font-weight: 500;
  line-height: 1.5;
}

/* ============ 测试账号卡片 ============ */
.demo-tip {
  margin-top: 28rpx;
  padding: 28rpx;
  background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%);
  border-radius: 24rpx;
  border: 2rpx solid #BAE6FD;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 200ms both;
}

.demo-header {
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx dashed #7DD3FC;
}

.demo-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #0369A1;
}

.demo-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.demo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14rpx 20rpx;
  background: #ffffff;
  border-radius: 14rpx;
  transition: all 200ms ease;

  &:active {
    background: #F0F9FF;
    transform: scale(0.98);
  }
}

.demo-phone {
  font-size: 26rpx;
  color: #0F172A;
  font-weight: 600;
  font-family: 'SF Mono', 'Monaco', 'Menlo', monospace;
}

.demo-desc {
  font-size: 24rpx;
  color: #64748B;
}

.demo-password {
  display: block;
  font-size: 24rpx;
  color: #0369A1;
  font-weight: 500;
  text-align: center;
  padding: 12rpx;
  background: rgba(3, 105, 161, 0.08);
  border-radius: 12rpx;
}

.loading-text {
  opacity: 0.9;
}

/* ============ 协议区域 ============ */
.agreement {
  text-align: center;
  margin-top: 48rpx;
  padding-bottom: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8rpx;
  animation: fadeInUp 600ms cubic-bezier(0.4, 0, 0.2, 1) 400ms both;
}

.agreement-text {
  font-size: 24rpx;
  color: #64748B;
}

.link {
  font-size: 24rpx;
  color: #0369A1;
  font-weight: 600;
  cursor: pointer;
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    color: #075985;
    text-decoration: underline;
  }
}

/* ============ 响应式适配 (大屏 ≥376px) ============ */
@media (min-width: 376px) {
  .login-header {
    padding: 140rpx 0 100rpx;
  }

  .logo-circle {
    width: 180rpx;
    height: 180rpx;
    border-radius: 52rpx;
  }

  .logo-text {
    font-size: 72rpx;
  }

  .app-name {
    font-size: 56rpx;
  }

  .app-desc {
    font-size: 28rpx;
  }

  .form-card {
    padding: 56rpx 48rpx;
  }

  .input-label {
    font-size: 30rpx;
  }

  .ct-input {
    height: 104rpx;
    padding: 0 32rpx;
    font-size: 30rpx;
    border-radius: 24rpx;
  }

  .btn-login {
    height: 112rpx;
    font-size: 34rpx;
    border-radius: 56rpx;
  }

  .checkbox {
    width: 44rpx;
    height: 44rpx;
    border-radius: 12rpx;
  }

  .remember-text,
  .forgot-pwd,
  .agreement-text,
  .link {
    font-size: 26rpx;
  }
}

/* ============ 响应式适配 (超小屏 ≤320px) ============ */
@media (max-width: 320px) {
  .page-content {
    padding: 0 32rpx;
  }

  .login-header {
    padding: 100rpx 0 60rpx;
  }

  .logo-circle {
    width: 140rpx;
    height: 140rpx;
    border-radius: 36rpx;
  }

  .logo-text {
    font-size: 56rpx;
  }

  .app-name {
    font-size: 42rpx;
    margin-top: 20rpx;
  }

  .app-desc {
    font-size: 22rpx;
  }

  .form-card {
    padding: 36rpx 32rpx;
    border-radius: 24rpx;
  }

  .input-group {
    margin-bottom: 24rpx;
  }

  .input-label {
    font-size: 24rpx;
    margin-bottom: 12rpx;
  }

  .ct-input {
    height: 84rpx;
    padding: 0 24rpx;
    font-size: 26rpx;
    border-radius: 16rpx;
  }

  .form-options {
    padding: 20rpx 0 32rpx;
  }

  .btn-login {
    height: 92rpx;
    font-size: 28rpx;
    border-radius: 46rpx;
    letter-spacing: 2rpx;
  }

  .checkbox {
    width: 32rpx;
    height: 32rpx;
    border-radius: 8rpx;
  }

  .remember-text,
  .forgot-pwd {
    font-size: 22rpx;
  }

  .agreement {
    margin-top: 36rpx;
    padding-bottom: 48rpx;
  }

  .agreement-text,
  .link {
    font-size: 20rpx;
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
    transform: translateY(40rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulseGlow {
  0%, 100% {
    box-shadow: 
      0 20rpx 60rpx rgba(15, 23, 42, 0.35),
      0 4rpx 12rpx rgba(15, 23, 42, 0.15),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.1);
  }
  50% {
    box-shadow: 
      0 24rpx 80rpx rgba(15, 23, 42, 0.45),
      0 6rpx 16rpx rgba(15, 23, 42, 0.2),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.15),
      0 0 60rpx rgba(3, 105, 161, 0.15);
  }
}

/* ============ 减少动画偏好 - 尊重用户系统设置 ============ */
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