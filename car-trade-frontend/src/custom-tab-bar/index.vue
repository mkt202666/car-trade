<template>
  <view class="custom-tabbar">
    <view class="tabbar-inner">
      <view
        class="tab-item"
        v-for="(item, index) in tabList"
        :key="item.pagePath"
        @click="switchTab(item, index)"
      >
        <view class="tab-icon">
          <!-- 纯图标：中间为特殊凸起按钮 -->
          <block v-if="index === 0">
            <u-icon name="search" :size="22" :color="activeIndex === 0 ? '#1f2937' : '#9ca3af'"></u-icon>
          </block>
          <block v-else-if="index === 1">
            <u-icon name="list" :size="22" :color="activeIndex === 1 ? '#1f2937' : '#9ca3af'"></u-icon>
          </block>
          <block v-else-if="index === 2">
            <!-- 中间AI按钮不在这里显示，在center-btn中显示 -->
          </block>
          <block v-else-if="index === 3">
            <u-icon name="chat" :size="22" :color="activeIndex === 3 ? '#1f2937' : '#9ca3af'"></u-icon>
          </block>
          <block v-else-if="index === 4">
            <u-icon name="account" :size="22" :color="activeIndex === 4 ? '#1f2937' : '#9ca3af'"></u-icon>
          </block>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === index }">{{ item.text }}</text>
      </view>

      <!-- 中间凸起按钮占位 -->
      <view class="tab-center-placeholder"></view>
    </view>

    <!-- 中间凸起圆形按钮：AI助理 -->
    <view class="tab-center" @click="gotoAI">
      <view class="center-btn ai">
        <u-icon name="chat" size="32" color="#ffffff"></u-icon>
      </view>
      <text class="center-text">AI</text>
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
        { pagePath: '/pages/trade/index', text: '交易' },
        { pagePath: '/pages/ai/index', text: 'AI助理' },
        { pagePath: '/pages/message/index', text: '消息' },
        { pagePath: '/pages/profile/index', text: '我的' }
      ]
    }
  },
  mounted() {
    // 根据当前页面路径设置选中状态
    const pages = getCurrentPages()
    const current = pages[pages.length - 1]
    if (current && current.route) {
      const route = '/' + current.route
      const idx = this.tabList.findIndex(t => t.pagePath === route)
      if (idx >= 0) this.activeIndex = idx
    }
  },
  methods: {
    switchTab(item, index) {
      if (index === 2) {
        // 中间按钮特殊处理（通过 center-btn 点击跳转，这里不响应）
        return
      }
      this.activeIndex = index
      uni.switchTab({ url: item.pagePath })
    },
    gotoAI() {
      uni.switchTab({ url: '/pages/ai/index' })
    }
  }
}
</script>

<style lang="scss" scoped>
.custom-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background: #ffffff;
  box-shadow: 0 -2rpx 20rpx rgba(0, 0, 0, 0.04);
}

.tabbar-inner {
  position: relative;
  display: flex;
  align-items: flex-start;
  height: 100rpx;
  padding-bottom: 8rpx;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 14rpx;
  height: 100rpx;
  transition: transform 0.15s ease;

  &:active {
    transform: scale(0.9);
  }
}

.tab-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44rpx;
  height: 44rpx;
}

.tab-text {
  font-size: 22rpx;
  color: #9ca3af;
  margin-top: 4rpx;
  font-weight: 500;
  letter-spacing: 1rpx;

  &.active {
    color: #1f2937;
    font-weight: 700;
  }
}

/* 中间位置占位：确保两侧 tab 对称布局 */
.tab-center-placeholder {
  flex: 1;
  height: 100rpx;
}

/* 中间凸起圆形按钮 */
.tab-center {
  position: absolute;
  left: 50%;
  top: -30rpx;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.center-btn {
  width: 110rpx;
  height: 110rpx;
  border-radius: 55rpx;
  background: linear-gradient(180deg, #ffd43b 0%, #fab005 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(250, 176, 5, 0.35);
  transition: transform 0.15s ease;

  &.ai {
    background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
    box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.35);
  }

  &:active {
    transform: scale(0.92);
  }
}

.center-text {
  font-size: 22rpx;
  color: #1f2937;
  margin-top: 6rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
}

.safe-area {
  height: constant(safe-area-inset-bottom);
  height: env(safe-area-inset-bottom);
}
</style>
