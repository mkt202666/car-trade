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
@import './styles/design-system.scss';

/* ============ 全局页面样式 ============ */
page {
  background: $bg-primary;
  min-height: 100vh;
  font-size: $font-base;
  color: $text-primary;
  line-height: $line-normal;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* ============ 全局容器样式 ============ */
.page-container {
  min-height: 100vh;
  background: $bg-primary;
}

/* ============ 全局卡片样式 ============ */
.section-card {
  background: $bg-secondary;
  border-radius: $radius-2xl;
  padding: $space-6;
  box-shadow: $shadow-md;
  margin-bottom: $space-6;
}

/* ============ 全局按钮悬停 ============ */
button {
  border: none;
  cursor: pointer;
  transition: all $duration-normal $timing-standard;
}

button::after {
  border: none;
}

/* ============ 全局链接样式 ============ */
uni-link,
.link {
  color: $primary;
  cursor: pointer;
  transition: color $duration-fast $timing-standard;
}

uni-link:hover,
.link:hover {
  color: $primary-dark;
}

/* ============ 全局滚动样式 ============ */
::-webkit-scrollbar {
  width: 8rpx;
  height: 8rpx;
}

::-webkit-scrollbar-track {
  background: $bg-hover;
  border-radius: $radius-full;
}

::-webkit-scrollbar-thumb {
  background: $border-medium;
  border-radius: $radius-full;
  transition: background $duration-normal $timing-standard;
}

::-webkit-scrollbar-thumb:hover {
  background: $text-light;
}

/* ============ 动画延迟工具类 ============ */
.delay-100 { animation-delay: 100ms; }
.delay-200 { animation-delay: 200ms; }
.delay-300 { animation-delay: 300ms; }
.delay-500 { animation-delay: 500ms; }

/* ============ 响应式字体大小 ============ */
@media (min-width: 376px) {
  page {
    font-size: 28rpx;
  }
}

@media (max-width: 320px) {
  page {
    font-size: 24rpx;
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
