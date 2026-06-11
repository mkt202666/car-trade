<template>
  <view class="custom-tabbar">
    <view class="tabbar-inner">
      <!-- 左侧：找车 -->
      <view class="tab-item" @click="switchTab(0)">
        <view class="tab-icon-wrapper" :class="{ active: activeIndex === 0 }">
          <text class="tab-icon-text" v-if="activeIndex === 0">🔍</text>
          <text class="tab-icon-text" v-else>🔎</text>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === 0 }">找车</text>
      </view>

      <!-- 求购 -->
      <view class="tab-item" @click="switchTab(1)">
        <view class="tab-icon-wrapper" :class="{ active: activeIndex === 1 }">
          <text class="tab-icon-text">📋</text>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === 1 }">求购</text>
      </view>

      <!-- 中间位置：占位，实际凸起按钮在下方绝对定位 -->
      <view class="tab-center-placeholder"></view>

      <!-- 消息 -->
      <view class="tab-item" @click="switchTab(3)">
        <view class="tab-icon-wrapper" :class="{ active: activeIndex === 3 }">
          <text class="tab-icon-text">💬</text>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === 3 }">消息</text>
      </view>

      <!-- 我的 -->
      <view class="tab-item" @click="switchTab(4)">
        <view class="tab-icon-wrapper" :class="{ active: activeIndex === 4 }">
          <text class="tab-icon-text">👤</text>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === 4 }">我的</text>
      </view>
    </view>

    <!-- 中间凸起圆形按钮：AI助理（突出显示） -->
    <view class="tab-center" @click="gotoAI">
      <view class="center-circle">
        <view class="center-btn-inner">
          <text class="center-btn-icon">✦</text>
        </view>
      </view>
      <text class="center-text" :class="{ active: activeIndex === 2 }">AI助理</text>
    </view>

    <!-- 底部安全区 -->
    <view class="safe-area"></view>
  </view>
</template>

<script>
export default {
  name: 'CustomTabbar',
  data() {
    return {
      activeIndex: 0,
      tabList: [
        { pagePath: '/pages/home/index', text: '找车' },
        { pagePath: '/pages/trade/index', text: '求购' },
        { pagePath: '/pages/ai/index', text: 'AI助理' },
        { pagePath: '/pages/message/index', text: '消息' },
        { pagePath: '/pages/profile/index', text: '我的' }
      ]
    }
  },
  mounted() {
    // 根据当前页面设置选中状态
    const pages = getCurrentPages()
    const current = pages[pages.length - 1]
    if (current && current.route) {
      const route = '/' + current.route
      const idx = this.tabList.findIndex(t => t.pagePath === route)
      if (idx >= 0) this.activeIndex = idx
    }
  },
  methods: {
    switchTab(index) {
      this.activeIndex = index
      uni.switchTab({ url: this.tabList[index].pagePath })
    },
    gotoAI() {
      this.activeIndex = 2
      uni.switchTab({ url: '/pages/ai/index' })
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================================
   5D好车自定义TabBar - 高级设计系统样式
   设计语言: Motion-Driven | Marketplace
   颜色系统: 深蓝主色 + 琥珀金突出按钮
   ========================================================= */

/* ============ 主容器 ============ */
.custom-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background: #ffffff;
  box-shadow: 
    0 -8rpx 32rpx rgba(15, 23, 42, 0.08),
    0 -2rpx 8rpx rgba(15, 23, 42, 0.04);
  border-top: 1rpx solid #F1F5F9;
  animation: slideUp 500ms cubic-bezier(0.4, 0, 0.2, 1) both;
}

/* ============ 内部布局 ============ */
.tabbar-inner {
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: space-around;
  height: 128rpx;
  padding-bottom: 8rpx;
  overflow: visible; /* 关键：允许中间按钮凸起超出容器 */
}

/* ============ 普通Tab项 ============ */
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 16rpx;
  height: 128rpx;
  cursor: pointer;
  transition: all 200ms cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;

  &:hover {
    transform: translateY(-4rpx);

    .tab-icon-text {
      transform: scale(1.1);
    }
  }

  &:active {
    transform: translateY(-2rpx) scale(0.95);
  }
}

.tab-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);

  &.active {
    background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%);
    box-shadow: 
      0 4rpx 16rpx rgba(3, 105, 161, 0.15),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9);
  }
}

.tab-icon-text {
  font-size: 44rpx;
  line-height: 1;
  opacity: 0.75;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  filter: grayscale(20%);

  .active & {
    opacity: 1;
    filter: grayscale(0%);
    transform: scale(1.1);
  }
}

.tab-text {
  font-size: 22rpx;
  color: #64748B;
  margin-top: 8rpx;
  font-weight: 500;
  letter-spacing: 0.5rpx;
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);

  &.active {
    color: #0369A1;
    font-weight: 700;
    font-size: 24rpx;
  }
}

/* ============ 活动指示条 ============ */
.tab-item .active-indicator {
  position: absolute;
  top: 8rpx;
  width: 40rpx;
  height: 6rpx;
  background: linear-gradient(90deg, #0369A1 0%, #0284C7 100%);
  border-radius: 4rpx;
  opacity: 0;
  transition: opacity 200ms ease;
}

.tab-item .active-indicator.active {
  opacity: 1;
}

/* ============ 中间位置占位 ============ */
.tab-center-placeholder {
  flex: 1;
  height: 128rpx;
}

/* ============ 中间凸起圆形按钮（突出显示） ============ */
.tab-center {
  position: absolute;
  left: 50%;
  top: -64rpx; /* 向上突出，形成凸起效果 */
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  z-index: 1000;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  animation: pulseGlow 3s ease-in-out infinite;

  &:hover {
    transform: translateX(-50%) translateY(-6rpx);

    .center-circle {
      box-shadow: 
        0 20rpx 60rpx rgba(245, 158, 11, 0.45),
        0 8rpx 24rpx rgba(245, 158, 11, 0.25),
        inset 0 2rpx 8rpx rgba(255, 255, 255, 0.9);
    }

    .center-btn-inner {
      transform: scale(1.05);
    }
  }

  &:active {
    transform: translateX(-50%) scale(0.94);
  }
}

/* 外圈 - 大圆形背景（白色边框 + 多层阴影） */
.center-circle {
  width: 148rpx;
  height: 148rpx;
  border-radius: 74rpx;
  background: #ffffff;
  box-shadow: 
    0 16rpx 48rpx rgba(245, 158, 11, 0.35),
    0 4rpx 16rpx rgba(245, 158, 11, 0.2),
    inset 0 2rpx 8rpx rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid rgba(245, 158, 11, 0.3);
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 内圈 - 实际按钮主体（琥珀金渐变） */
.center-btn-inner {
  width: 124rpx;
  height: 124rpx;
  border-radius: 62rpx;
  background: linear-gradient(
    180deg,
    #FDE68A 0%,
    #FCD34D 25%,
    #F59E0B 60%,
    #D97706 100%
  );
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 
    inset 0 4rpx 12rpx rgba(255, 255, 255, 0.5),
    inset 0 -4rpx 12rpx rgba(180, 83, 9, 0.25),
    0 2rpx 8rpx rgba(245, 158, 11, 0.15);
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 10%;
    left: 15%;
    width: 35%;
    height: 25%;
    background: linear-gradient(
      180deg,
      rgba(255, 255, 255, 0.6) 0%,
      rgba(255, 255, 255, 0) 100%
    );
    border-radius: 50%;
    pointer-events: none;
  }
}

/* 图标 */
.center-btn-icon {
  font-size: 56rpx;
  color: #FFFFFF;
  font-weight: 900;
  text-shadow: 
    0 2rpx 8rpx rgba(180, 83, 9, 0.4),
    0 4rpx 12rpx rgba(245, 158, 11, 0.3);
  line-height: 1;
  position: relative;
  z-index: 1;
  animation: iconFloat 2s ease-in-out infinite;
}

/* 文字 */
.center-text {
  font-size: 24rpx;
  color: #F59E0B; /* 琥珀金色，突出显示 */
  margin-top: 10rpx;
  font-weight: 800;
  letter-spacing: 2rpx;
  text-shadow: 0 2rpx 8rpx rgba(245, 158, 11, 0.2);
  transition: all 200ms cubic-bezier(0.4, 0, 0.2, 1);

  &.active {
    color: #D97706;
    font-size: 26rpx;
  }
}

/* ============ 安全区 ============ */
.safe-area {
  height: constant(safe-area-inset-bottom);
  height: env(safe-area-inset-bottom);
}

/* ============ 动画定义 ============ */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulseGlow {
  0%, 100% {
    filter: drop-shadow(0 0 0 rgba(245, 158, 11, 0));
  }
  50% {
    filter: drop-shadow(0 0 20rpx rgba(245, 158, 11, 0.3));
  }
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-4rpx) rotate(10deg);
  }
}

/* ============ 响应式适配 ============ */
@media (min-width: 376px) {
  .tabbar-inner {
    height: 140rpx;
  }

  .tab-item {
    height: 140rpx;
  }

  .tab-center-placeholder {
    height: 140rpx;
  }

  .center-circle {
    width: 160rpx;
    height: 160rpx;
    border-radius: 80rpx;
  }

  .center-btn-inner {
    width: 136rpx;
    height: 136rpx;
    border-radius: 68rpx;
  }

  .center-btn-icon {
    font-size: 60rpx;
  }

  .tab-icon-wrapper {
    width: 60rpx;
    height: 60rpx;
  }

  .tab-icon-text {
    font-size: 48rpx;
  }

  .tab-text {
    font-size: 24rpx;

    &.active {
      font-size: 26rpx;
    }
  }

  .center-text {
    font-size: 26rpx;

    &.active {
      font-size: 28rpx;
    }
  }
}

@media (max-width: 320px) {
  .tabbar-inner {
    height: 112rpx;
  }

  .tab-item {
    height: 112rpx;
  }

  .tab-center-placeholder {
    height: 112rpx;
  }

  .center-circle {
    width: 128rpx;
    height: 128rpx;
    border-radius: 64rpx;
  }

  .center-btn-inner {
    width: 108rpx;
    height: 108rpx;
    border-radius: 54rpx;
  }

  .center-btn-icon {
    font-size: 48rpx;
  }

  .tab-icon-wrapper {
    width: 48rpx;
    height: 48rpx;
  }

  .tab-icon-text {
    font-size: 36rpx;
  }

  .tab-text {
    font-size: 20rpx;
    margin-top: 6rpx;

    &.active {
      font-size: 22rpx;
    }
  }

  .center-text {
    font-size: 22rpx;
    margin-top: 6rpx;

    &.active {
      font-size: 24rpx;
    }
  }
}

/* ============ 减少动画偏好 ============ */
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
