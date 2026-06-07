<template>
  <view class="page">
    <u-navbar title="AI助理" :border-bottom="false" :placeholder="true"></u-navbar>

    <view class="page-content">
      <!-- AI 快捷入口 -->
      <view class="ai-grid">
        <view class="ai-item" v-for="item in aiFunctions" :key="item.id" @click="toAI(item)">
          <view class="ai-icon" :style="{ background: item.color }">
            <u-icon :name="item.icon" size="44" color="#fff"></u-icon>
          </view>
          <text class="ai-name">{{ item.name }}</text>
          <text class="ai-desc">{{ item.desc }}</text>
        </view>
      </view>

      <!-- 最近对话 -->
      <view class="section">
        <view class="section-title">最近对话</view>
        <view class="history-list" v-if="aiHistory.length > 0">
          <view class="history-item" v-for="item in aiHistory" :key="item.id" @click="toHistory(item)">
            <view class="history-left">
              <u-icon name="chat" size="32" color="#3c9cff"></u-icon>
              <text class="history-title">{{ item.title }}</text>
            </view>
            <text class="history-time">{{ item.time }}</text>
          </view>
        </view>
        <u-empty v-else mode="list" text="暂无对话记录"></u-empty>
      </view>

      <!-- 快速提问 -->
      <view class="quick-ask">
        <view class="section-title">快速提问</view>
        <view class="quick-tags">
          <text class="quick-tag" @click="quickAsk('最近宝马X5行情怎么样')">最近宝马X5行情怎么样</text>
          <text class="quick-tag" @click="quickAsk('帮我找10万以内的SUV')">帮我找10万以内的SUV</text>
          <text class="quick-tag" @click="quickAsk('生成一段获客文案')">生成一段获客文案</text>
          <text class="quick-tag" @click="quickAsk('分析一下我这个车源')">分析一下我这个车源</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { aiFunctions, aiHistory } from '@/mock/data'

export default {
  data() {
    return {
      aiFunctions,
      aiHistory: [...aiHistory]
    }
  },
  methods: {
    toAI(item) {
      uni.$u.toast('进入 ' + item.name)
    },
    toHistory(item) {
      uni.$u.toast('打开对话: ' + item.title)
    },
    quickAsk(text) {
      uni.$u.toast('提问: ' + text)
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}
.page-content {
  padding-bottom: 40rpx;
}

/* AI 功能入口 */
.ai-grid {
  background: #fff;
  padding: 30rpx 20rpx;
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 16rpx;
}
.ai-item {
  width: 33.33%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;
}
.ai-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}
.ai-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
}
.ai-desc {
  font-size: 20rpx;
  color: #999;
  margin-top: 6rpx;
  text-align: center;
}

/* 通用区块 */
.section {
  background: #fff;
  margin: 16rpx 0;
  padding: 30rpx;
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}

/* 最近对话 */
.history-list {
  display: flex;
  flex-direction: column;
}
.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.history-item:last-child {
  border-bottom: none;
}
.history-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.history-title {
  font-size: 26rpx;
  color: #333;
}
.history-time {
  font-size: 22rpx;
  color: #ccc;
}

/* 快速提问 */
.quick-ask {
  background: #fff;
  margin: 16rpx 0;
  padding: 30rpx;
}
.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}
.quick-tag {
  font-size: 24rpx;
  color: #3c9cff;
  background: #e8f4ff;
  padding: 12rpx 24rpx;
  border-radius: 32rpx;
}
</style>