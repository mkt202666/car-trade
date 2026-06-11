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
        { pagePath: '/pages/purchase-demand-list/index', text: '求购' },
        { pagePath: '/pages/purchase-demand-list/index', text: 'AI助理' },
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
      uni.switchTab({ url: '/pages/purchase-demand-list/index' })
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================================
   5D好车自定义TabBar - 原型风格样式
   设计语言: Clean | Modern | Marketplace
   颜色系统: 深蓝灰 #0F172A (active) | 琥珀金 #F59E0B (AI按钮)
   ========================================================= */

/* ============ 主容器 ============ */
.custom-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background: #ffffff;
  box-shadow: 0 -2rpx 12rpx rgba(15, 23, 42, 0.06);
  border-top: 1rpx solid #F1F5F9;
}

/* ============ 内部布局 ============ */
.tabbar-inner {
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: space-around;
  height: 120rpx;
  padding-bottom: 8rpx;
  overflow: visible;
}

/* ============ 普通Tab项 ============ */
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 16rpx;
  height: 120rpx;
  cursor: pointer;
  transition: all 150ms cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;

  &:active {
    transform: scale(0.95);
  }
}

.tab-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48rpx;
  height: 48rpx;
  border-radius: 12rpx;
  transition: all 150ms cubic-bezier(0.4, 0, 0.2, 1);

  &.active {
    background: #F8FAFC;
  }
}

.tab-icon-text {
  font-size: 40rpx;
  line-height: 1;
  opacity: 0.6;
  transition: all 150ms ease;

  .active & {
    opacity: 1;
  }
}

.tab-text {
  font-size: 22rpx;
  color: #94A3B8;
  margin-top: 6rpx;
  font-weight: 500;
  transition: all 150ms ease;

  &.active {
    color: #0F172A;
    font-weight: 700;
  }
}

/* ============ 中间位置占位 ============ */
.tab-center-placeholder {
  flex: 1;
  height: 120rpx;
}

/* ============ 中间凸起圆形按钮（AI助理） ============ */
.tab-center {
  position: absolute;
  left: 50%;
  top: -40rpx;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  z-index: 1000;
  transition: all 150ms cubic-bezier(0.4, 0, 0.2, 1);

  &:active {
    transform: translateX(-50%) scale(0.94);
  }
}

/* 外圈 - 白色圆形背景 */
.center-circle {
  width: 132rpx;
  height: 132rpx;
  border-radius: 66rpx;
  background: #ffffff;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid #F59E0B;
}

/* 内圈 - 琥珀金渐变按钮主体 */
.center-btn-inner {
  width: 108rpx;
  height: 108rpx;
  border-radius: 54rpx;
  background: linear-gradient(180deg, #FCD34D 0%, #F59E0B 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 2rpx 4rpx rgba(255, 255, 255, 0.4);
}

/* 图标 */
.center-btn-icon {
  font-size: 48rpx;
  color: #ffffff;
  font-weight: 800;
  line-height: 1;
}

/* 文字 */
.center-text {
  font-size: 22rpx;
  color: #F59E0B;
  margin-top: 8rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
  transition: all 150ms ease;

  &.active {
    color: #D97706;
  }
}

/* ============ 安全区 ============ */
.safe-area {
  height: constant(safe-area-inset-bottom);
  height: env(safe-area-inset-bottom);
}
</style>
