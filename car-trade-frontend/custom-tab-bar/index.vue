<template>
  <view class="custom-tabbar">
    <view class="tabbar-inner">
      <!-- 找车 -->
      <view class="tab-item" @click="switchTab(0)">
        <view class="tab-icon-box">
          <view class="tab-icon icon-search" :class="{ active: activeIndex === 0 }">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="11" cy="11" r="7" stroke-width="2.2"/>
              <line x1="16.5" y1="16.5" x2="21" y2="21" stroke-width="2.2" stroke-linecap="round"/>
            </svg>
          </view>
          <view class="tab-dot" v-if="activeIndex === 0"></view>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === 0 }">找车</text>
      </view>

      <!-- 求购 -->
      <view class="tab-item" @click="switchTab(1)">
        <view class="tab-icon-box">
          <view class="tab-icon icon-list" :class="{ active: activeIndex === 1 }">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="4" y="4" width="16" height="16" rx="3" stroke-width="2"/>
              <line x1="8" y1="9" x2="16" y2="9" stroke-width="2" stroke-linecap="round"/>
              <line x1="8" y1="12.5" x2="16" y2="12.5" stroke-width="2" stroke-linecap="round"/>
              <line x1="8" y1="16" x2="13" y2="16" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </view>
          <view class="tab-dot" v-if="activeIndex === 1"></view>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === 1 }">求购</text>
      </view>

      <!-- 中间凸起按钮：AI 助理 -->
      <view class="tab-center" @click="gotoAI">
        <view class="center-ring">
          <view class="center-btn">
            <view class="center-icon">
              <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M24 6 L27 18 L39 18 L29 26 L32 40 L24 32 L16 40 L19 26 L9 18 L21 18 Z" stroke-linejoin="round" stroke-width="2.4"/>
              </svg>
            </view>
          </view>
        </view>
        <text class="center-text" :class="{ active: activeIndex === 2 }">AI助理</text>
        <view class="tab-dot center" v-if="activeIndex === 2"></view>
      </view>

      <!-- 消息 -->
      <view class="tab-item" @click="switchTab(3)">
        <view class="tab-icon-box">
          <view class="tab-icon icon-chat" :class="{ active: activeIndex === 3 }">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 6 Q4 4 6 4 L18 4 Q20 4 20 6 L20 16 Q20 18 18 18 L10 18 L5 22 L5 18 L5 6 Z" stroke-width="2" stroke-linejoin="round" stroke-linecap="round"/>
            </svg>
          </view>
          <view class="tab-dot" v-if="activeIndex === 3"></view>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === 3 }">消息</text>
      </view>

      <!-- 我的 -->
      <view class="tab-item" @click="switchTab(4)">
        <view class="tab-icon-box">
          <view class="tab-icon icon-user" :class="{ active: activeIndex === 4 }">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="8.5" r="4" stroke-width="2"/>
              <path d="M4.5 21 Q4.5 14.5 12 14.5 Q19.5 14.5 19.5 21" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </view>
          <view class="tab-dot" v-if="activeIndex === 4"></view>
        </view>
        <text class="tab-text" :class="{ active: activeIndex === 4 }">我的</text>
      </view>
    </view>

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
        { pagePath: '/pages/ai/index', text: 'AI助手' },
        { pagePath: '/pages/message/index', text: '消息' },
        { pagePath: '/pages/profile/index', text: '我的' }
      ]
    }
  },
  mounted() {
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
   5D好车 Custom TabBar - 简洁线描风格（参考图2）
   白色背景 + 黑色线描图标 + 黄色中间按钮 + 选中点
   ========================================================= */

.custom-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background: #ffffff;
  border-top: 1rpx solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 -4rpx 24rpx rgba(15, 23, 42, 0.06);
}

.tabbar-inner {
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: space-around;
  height: 120rpx;
  padding-bottom: 8rpx;
}

/* ============ 普通 Tab ============ */
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-top: 16rpx;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.25, 0.1, 0.25, 1);
  position: relative;

  &:active {
    opacity: 0.85;
  }
}

.tab-icon-box {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 56rpx;
}

.tab-icon {
  width: 44rpx;
  height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 250ms cubic-bezier(0.25, 0.1, 0.25, 1);

  svg {
    width: 100%;
    height: 100%;
    stroke: #64748B;
    transition: all 250ms cubic-bezier(0.25, 0.1, 0.25, 1);
  }

  &.active {
    svg {
      stroke: #1E293B;
      stroke-width: 2.4;
    }
  }
}

/* 选中态的黄色小圆点 */
.tab-dot {
  position: absolute;
  bottom: -6rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background: #F59E0B;
  box-shadow: 0 2rpx 6rpx rgba(245, 158, 11, 0.5);

  &.center {
    bottom: -10rpx;
    position: static;
    transform: none;
    margin-top: 6rpx;
  }
}

.tab-text {
  font-size: 24rpx;
  color: #64748B;
  margin-top: 12rpx;
  font-weight: 500;
  letter-spacing: 0.5rpx;
  transition: color 250ms ease;
  line-height: 1.2;

  &.active {
    color: #1E293B;
    font-weight: 700;
  }
}

/* ============ 中间 AI 按钮 - 浅黄色圆形 ============ */
.tab-center {
  position: absolute;
  left: 50%;
  top: -28rpx;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  z-index: 1000;
  transition: all 250ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:active {
    transform: translateX(-50%) scale(0.94);
  }
}

/* 外浅黄光晕 */
.center-ring {
  width: 112rpx;
  height: 112rpx;
  border-radius: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  box-shadow:
    0 -4rpx 16rpx rgba(15, 23, 42, 0.08),
    0 4rpx 24rpx rgba(15, 23, 42, 0.12);
  border: 2rpx solid rgba(226, 232, 240, 0.9);
}

/* 内层浅黄色圆按钮 */
.center-btn {
  width: 92rpx;
  height: 92rpx;
  border-radius: 46rpx;
  background: linear-gradient(135deg, #FFFBEB 0%, #FEF3C7 50%, #FDE68A 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    inset 0 2rpx 6rpx rgba(255, 255, 255, 0.9),
    inset 0 -2rpx 4rpx rgba(245, 158, 11, 0.15);
}

/* 黑色星星图标 */
.center-icon {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 100%;
    height: 100%;
    stroke: #1E293B;
    fill: #1E293B;
  }
}

.center-text {
  font-size: 24rpx;
  color: #1E293B;
  margin-top: 6rpx;
  font-weight: 700;
  letter-spacing: 0.5rpx;
  line-height: 1.2;
}

.safe-area {
  height: constant(safe-area-inset-bottom);
  height: env(safe-area-inset-bottom);
}
</style>
