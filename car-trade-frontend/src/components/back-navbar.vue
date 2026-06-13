<template>
  <view class="back-navbar">
    <view class="bn-back" @click="onBack" @touchstart="touchStart" @touchend="touchEnd" :class="{ pressed: isPressed }">
      <view class="bn-icon-wrap">
        <u-icon name="arrow-left" :size="22" color="#0F172A"></u-icon>
      </view>
    </view>
    <view class="bn-title">
      <text class="bn-title-text">{{ title }}</text>
    </view>
    <view class="bn-right" @click="onRight">
      <slot name="right">
        <template v-if="showShare">
          <view class="bn-icon-wrap bn-icon-right">
            <u-icon name="redo" :size="22" color="#0F172A"></u-icon>
          </view>
        </template>
      </slot>
    </view>
  </view>
</template>

<script>
export default {
  name: 'BackNavBar',
  props: {
    title: { type: String, default: '返回' },
    showShare: { type: Boolean, default: false }
  },
  data() {
    return { isPressed: false, touchTimer: null }
  },
  methods: {
    onBack() {
      const pages = getCurrentPages()
      if (pages && pages.length > 1) {
        uni.navigateBack({ fail: () => uni.switchTab({ url: '/pages/home/index' }) })
      } else {
        uni.switchTab({ url: '/pages/home/index' })
      }
    },
    onRight() { this.$emit('right') },
    touchStart() {
      this.isPressed = true
    },
    touchEnd() {
      this.touchTimer = setTimeout(() => { this.isPressed = false }, 80)
    }
  }
}
</script>

<style lang="scss" scoped>
.back-navbar {
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: calc(env(safe-area-inset-top, 0) + 12rpx);
  padding-bottom: 12rpx;
  padding-left: 20rpx;
  padding-right: 20rpx;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24rpx) saturate(1.2);
  -webkit-backdrop-filter: blur(24rpx) saturate(1.2);
  z-index: 100;
}

.bn-back {
  width: 76rpx;
  height: 76rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.18s cubic-bezier(0.25, 0.1, 0.25, 1), background 0.18s ease;
}
.bn-back.pressed { transform: scale(0.92); background: rgba(15, 23, 42, 0.08); }
.bn-back:active { transform: scale(0.94); background: rgba(15, 23, 42, 0.06); }

.bn-icon-wrap {
  width: 76rpx;
  height: 76rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}
.bn-icon-right { background: rgba(15, 23, 42, 0.04); }

.bn-title {
  flex: 1;
  text-align: center;
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-12rpx);
  pointer-events: none;
}
.bn-title-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #0F172A;
  letter-spacing: -0.2rpx;
}

.bn-right {
  width: 76rpx;
  height: 76rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.18s cubic-bezier(0.25, 0.1, 0.25, 1);
}
.bn-right:active { transform: scale(0.94); }
</style>
