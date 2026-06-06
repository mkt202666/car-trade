<template>
  <u-splash-screen v-if="splash" @click="splash = false">
    <image src="/static/logo.png" mode="aspectFit"></image>
  </u-splash-screen>
  <view v-else>
    <u-toast ref="uToast"></u-toast>
    <router-view />
  </view>
</template>

<script>
export default {
  data() {
    return {
      splash: true
    }
  },
  computed: {
    hasTabBar() {
      const tabPages = ['pages/home/index', 'pages/trade/index', 'pages/ai/index', 'pages/message/index', 'pages/profile/index']
      const route = this.$mp && this.$mp.page ? this.$mp.page.route : ''
      return tabPages.includes(route)
    }
  },
  onLaunch() {
    setTimeout(() => { this.splash = false }, 1500)
    const token = uni.getStorageSync('token')
    if (token) {
      this.$store.commit('setToken', token)
      this.$store.dispatch('getUser').catch(() => {})
    }
  }
}
</script>

<style lang="scss">
@import 'uview-ui/index.scss';
</style>
