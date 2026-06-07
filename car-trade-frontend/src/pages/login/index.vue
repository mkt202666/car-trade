<template>
  <view class="page-content">
    <view class="login-header">
      <image src="/static/logo.png" mode="aspectFit" class="logo"></image>
      <text class="app-name">5D好车</text>
      <text class="app-desc">B2B二手车交易平台</text>
    </view>
    <view class="login-form">
      <u-field label="手机号" :required="true">
        <u-input v-model="form.phone" placeholder="请输入手机号" type="number" :maxlength="11" input-align="right"></u-input>
      </u-field>
      <u-field label="密码" :required="true">
        <u-input v-model="form.password" placeholder="请输入密码" type="password" input-align="right"></u-input>
      </u-field>
      <view class="form-options">
        <u-checkbox v-model="remember">记住密码</u-checkbox>
        <text class="forgot-pwd" @click="toRegister">没有账号？去注册</text>
      </view>
      <u-button type="primary" size="default" shape="circle" :loading="loading" @click="handleLogin">{{ loading ? '登录中...' : '登录' }}</u-button>
    </view>
    <view class="agreement">
      <text>登录即代表同意</text>
      <text class="link">《用户协议》</text>
      <text>和</text>
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
.page-content { min-height: 100vh; background: #fff; display: flex; flex-direction: column; padding: 0 60rpx; }
.login-header { display: flex; flex-direction: column; align-items: center; padding: 100rpx 0 80rpx; }
.logo { width: 160rpx; height: 160rpx; border-radius: 40rpx; }
.app-name { font-size: 48rpx; font-weight: 700; color: #333; margin-top: 20rpx; }
.app-desc { font-size: 26rpx; color: #999; margin-top: 10rpx; }
.login-form { }
.form-options { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 0 40rpx; }
.forgot-pwd { font-size: 26rpx; color: #3c9cff; }
.agreement { text-align: center; margin-top: 60rpx; font-size: 24rpx; color: #999; }
.link { color: #3c9cff; }
</style>
