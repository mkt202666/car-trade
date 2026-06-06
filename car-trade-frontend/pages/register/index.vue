<template>
  <view class="page-content">
    <view class="register-header">
      <text class="register-title">注册账号</text>
      <text class="register-desc">填写信息创建您的5D好车账号</text>
    </view>
    <view class="register-form">
      <u-field label="手机号" :required="true">
        <u-input v-model="form.phone" placeholder="请输入手机号" type="number" :maxlength="11" input-align="right"></u-input>
      </u-field>
      <u-field label="验证码" :required="true">
        <u-input v-model="form.code" placeholder="请输入验证码" type="number" :maxlength="6" input-align="right"></u-input>
        <u-button slot="right" size="mini" type="primary" :plain="true" :disabled="codeSending" @click="sendCode">{{ codeSending ? remain + 's' : '获取验证码' }}</u-button>
      </u-field>
      <u-field label="密码" :required="true">
        <u-input v-model="form.password" placeholder="请设置密码(6-20位)" type="password" input-align="right"></u-input>
      </u-field>
      <u-field label="确认密码" :required="true">
        <u-input v-model="form.confirmPassword" placeholder="请再次输入密码" type="password" input-align="right"></u-input>
      </u-field>
      <u-field label="公司名称">
        <u-input v-model="form.company" placeholder="选填" input-align="right"></u-input>
      </u-field>
      <u-field label="联系人">
        <u-input v-model="form.contactName" placeholder="选填" input-align="right"></u-input>
      </u-field>
      <view class="agreement-row">
        <u-checkbox v-model="agree"><text style="font-size:24rpx;color:#999;">我已阅读并同意</text><text style="font-size:24rpx;color:#3c9cff;">《用户协议》</text></u-checkbox>
      </view>
      <u-button type="primary" size="default" shape="circle" :loading="loading" :disabled="!agree" @click="handleRegister">{{ loading ? '注册中...' : '注册' }}</u-button>
    </view>
    <view class="login-link">
      <text>已有账号？</text>
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
.page-content { min-height: 100vh; background: #fff; padding: 0 60rpx; }
.register-header { padding: 80rpx 0 60rpx; }
.register-title { font-size: 48rpx; font-weight: 700; color: #333; }
.register-desc { font-size: 26rpx; color: #999; display: block; margin-top: 12rpx; }
.register-form { }
.agreement-row { padding: 20rpx 0 40rpx; }
.login-link { text-align: center; margin-top: 60rpx; font-size: 26rpx; color: #999; }
.link { color: #3c9cff; }
</style>
