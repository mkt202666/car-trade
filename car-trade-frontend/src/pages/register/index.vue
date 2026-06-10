<template>
  <view class="page-content">
    <view class="register-header">
      <text class="register-title">注册账号</text>
      <text class="register-desc">填写信息创建您的5D好车账号</text>
    </view>

    <view class="register-form">
      <view class="form-card">
        <!-- 手机号 -->
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

        <!-- 验证码 -->
        <view class="input-group">
          <view class="input-label">
            <text class="label-text">验证码</text>
            <text class="required">*</text>
          </view>
          <view class="code-input-row">
            <input
              class="ct-input code-input"
              v-model="form.code"
              placeholder="请输入验证码"
              type="number"
              maxlength="6"
              placeholder-class="placeholder"
            />
            <view class="code-btn" :class="{ disabled: codeSending }" @click="sendCode">
              <text v-if="codeSending">{{ remain }}s</text>
              <text v-else>获取验证码</text>
            </view>
          </view>
        </view>

        <!-- 密码 -->
        <view class="input-group">
          <view class="input-label">
            <text class="label-text">密码</text>
            <text class="required">*</text>
          </view>
          <input
            class="ct-input"
            v-model="form.password"
            placeholder="请设置密码(6-20位)"
            type="password"
            placeholder-class="placeholder"
          />
        </view>

        <!-- 确认密码 -->
        <view class="input-group">
          <view class="input-label">
            <text class="label-text">确认密码</text>
            <text class="required">*</text>
          </view>
          <input
            class="ct-input"
            v-model="form.confirmPassword"
            placeholder="请再次输入密码"
            type="password"
            placeholder-class="placeholder"
          />
        </view>

        <!-- 公司名称 -->
        <view class="input-group">
          <view class="input-label">
            <text class="label-text">公司名称</text>
          </view>
          <input
            class="ct-input"
            v-model="form.company"
            placeholder="选填"
            placeholder-class="placeholder"
          />
        </view>

        <!-- 联系人 -->
        <view class="input-group">
          <view class="input-label">
            <text class="label-text">联系人</text>
          </view>
          <input
            class="ct-input"
            v-model="form.contactName"
            placeholder="选填"
            placeholder-class="placeholder"
          />
        </view>

        <!-- 协议 -->
        <view class="agreement-row" @click="agree = !agree">
          <view class="checkbox" :class="{ active: agree }">
            <u-icon name="checkmark" size="12" color="#fff" v-if="agree"></u-icon>
          </view>
          <text class="agreement-text">我已阅读并同意</text>
          <text class="link">《用户协议》</text>
        </view>

        <!-- 注册按钮 -->
        <button
          class="ct-btn-primary btn-register"
          :class="{ loading: loading, disabled: !agree }"
          @click="handleRegister"
          :disabled="loading || !agree"
        >
          <text v-if="loading">注册中...</text>
          <text v-else>注册</text>
        </button>
      </view>
    </view>

    <view class="login-link">
      <text class="login-text">已有账号？</text>
      <text class="link" @click="toLogin">去登录</text>
    </view>
  </view>
</template>

<script>
import { register } from '@/api/user'
import { isValidPhone } from '@/utils/validate'

export default {
  data() {
    return {
      form: { phone: '', code: '', password: '', confirmPassword: '', company: '', contactName: '' },
      agree: false,
      loading: false,
      codeSending: false,
      remain: 60
    }
  },
  methods: {
    sendCode() {
      if (this.codeSending) return
      if (!isValidPhone(this.form.phone)) { uni.$u.toast('请输入正确手机号'); return }
      this.codeSending = true
      this.remain = 60
      const timer = setInterval(() => {
        this.remain--
        if (this.remain <= 0) {
          clearInterval(timer)
          this.codeSending = false
        }
      }, 1000)
      uni.$u.toast('验证码已发送')
    },
    async handleRegister() {
      if (!isValidPhone(this.form.phone)) { uni.$u.toast('请输入正确手机号'); return }
      if (!this.form.code) { uni.$u.toast('请输入验证码'); return }
      if (!this.form.password || this.form.password.length < 6) { uni.$u.toast('密码至少6位'); return }
      if (this.form.password !== this.form.confirmPassword) { uni.$u.toast('两次密码不一致'); return }
      this.loading = true
      try {
        await register(this.form)
        uni.$u.toast('注册成功')
        setTimeout(() => { uni.navigateBack() }, 500)
      } catch (e) {
        uni.$u.toast('注册失败')
      }
      this.loading = false
    },
    toLogin() {
      uni.navigateBack()
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================
   5D Car Trade 企业级设计系统 - 注册页
   ========================================= */

.page-content {
  min-height: 100vh;
  background: #F8FAFC;
  padding: 0 48rpx;
}

/* ============= 头部区域 ============= */
.register-header {
  padding: 80rpx 0 60rpx;
}

.register-title {
  font-size: 48rpx;
  font-weight: 800;
  color: #0F172A;
  display: block;
}

.register-desc {
  font-size: 26rpx;
  color: #64748B;
  display: block;
  margin-top: 12rpx;
}

/* ============= 表单区域 ============= */
.register-form {
  margin-bottom: 40rpx;
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

/* ============= 验证码输入行 ============= */
.code-input-row {
  display: flex;
  gap: 16rpx;
}

.code-input {
  flex: 1;
}

.code-btn {
  flex-shrink: 0;
  width: 200rpx;
  height: 88rpx;
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  color: #ffffff;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  &.disabled {
    background: #CBD5E1;
    cursor: not-allowed;
  }
}

/* ============= 协议行 ============= */
.agreement-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 0 40rpx;
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

.agreement-text {
  font-size: 24rpx;
  color: #64748B;
}

.link {
  font-size: 24rpx;
  color: #0369A1;
  font-weight: 500;
}

/* ============= 注册按钮 ============= */
.btn-register {
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

  &:active:not(.disabled) {
    transform: scale(0.98);
  }

  &:hover:not(.disabled) {
    box-shadow: 0 12rpx 32rpx rgba(3, 105, 161, 0.4);
  }

  &.loading {
    opacity: 0.7;
  }

  &.disabled {
    background: #CBD5E1;
    box-shadow: none;
    cursor: not-allowed;
  }
}

/* ============= 登录链接 ============= */
.login-link {
  text-align: center;
  margin-top: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding-bottom: 60rpx;
}

.login-text {
  font-size: 26rpx;
  color: #64748B;
}

.link {
  font-size: 26rpx;
  color: #0369A1;
  font-weight: 600;
}

/* ============= 可访问性 ============= */
*:focus-visible {
  outline: 2px solid #0369A1;
  outline-offset: 2px;
}
</style>