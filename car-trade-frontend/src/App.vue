<script>
export default {
  onLaunch() {
    const token = uni.getStorageSync('token')
    if (token) {
      this.$store.commit('setToken', token)
      this.$store.dispatch('getUser').catch(() => {})
    }
  }
}
</script>

<style lang="scss">
@import 'uview-plus/theme.scss';

page {
  background: #f5f5f5;
  min-height: 100vh;
}
</style>
