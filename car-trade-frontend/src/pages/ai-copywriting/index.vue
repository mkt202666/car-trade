<template>
  <view class="page">
    <u-navbar title="获客文案生成" :border-bottom="false" :placeholder="true">
      <template v-slot:left>
        <view class="nav-back" @click="goBack">
          <u-icon name="arrow-left" size="32" color="#333"></u-icon>
        </view>
      </template>
    </u-navbar>

    <view class="page-content">
      <!-- 车源信息输入 -->
      <view class="section">
        <view class="section-title">车源信息</view>
        <view class="form-group">
          <view class="form-item">
            <text class="form-label">品牌车型</text>
            <input v-model="carInfo.brand" placeholder="如：宝马X5" class="form-input" />
          </view>
          <view class="form-item">
            <text class="form-label">年份</text>
            <input v-model="carInfo.year" placeholder="如：2022" class="form-input" type="number" />
          </view>
          <view class="form-item">
            <text class="form-label">里程(万公里)</text>
            <input v-model="carInfo.mileage" placeholder="如：3.5" class="form-input" type="digit" />
          </view>
          <view class="form-item">
            <text class="form-label">价格(万)</text>
            <input v-model="carInfo.price" placeholder="如：25.8" class="form-input" type="digit" />
          </view>
          <view class="form-item">
            <text class="form-label">亮点配置</text>
            <textarea v-model="carInfo.highlights" placeholder="如：全景天窗、柏林之声、360全景影像" class="form-textarea" />
          </view>
        </view>
      </view>

      <!-- 平台选择 -->
      <view class="section">
        <view class="section-title">选择平台</view>
        <view class="platform-list">
          <view
            class="platform-item"
            :class="{ active: selectedPlatform === 'wechat' }"
            @click="selectedPlatform = 'wechat'"
          >
            <u-icon name="weixin" size="40" color="#07c160"></u-icon>
            <text class="platform-name">朋友圈</text>
          </view>
          <view
            class="platform-item"
            :class="{ active: selectedPlatform === 'douyin' }"
            @click="selectedPlatform = 'douyin'"
          >
            <u-icon name="video" size="40" color="#000"></u-icon>
            <text class="platform-name">抖音</text>
          </view>
          <view
            class="platform-item"
            :class="{ active: selectedPlatform === 'xiaohongshu' }"
            @click="selectedPlatform = 'xiaohongshu'"
          >
            <u-icon name="bookmark" size="40" color="#ff2442"></u-icon>
            <text class="platform-name">小红书</text>
          </view>
          <view
            class="platform-item"
            :class="{ active: selectedPlatform === 'weibo' }"
            @click="selectedPlatform = 'weibo'"
          >
            <u-icon name="star" size="40" color="#e6162d"></u-icon>
            <text class="platform-name">微博</text>
          </view>
        </view>
      </view>

      <!-- 生成按钮 -->
      <view class="generate-section">
        <u-button type="primary" @click="generateCopywriting" :loading="generating" :disabled="generating">
          {{ generating ? '生成中...' : 'AI生成文案' }}
        </u-button>
      </view>

      <!-- 生成结果 -->
      <view class="section" v-if="generatedText">
        <view class="section-header">
          <text class="section-title">生成结果</text>
          <view class="copy-btn" @click="copyText">
            <u-icon name="copy" size="24" color="#0369A1"></u-icon>
            <text>复制</text>
          </view>
        </view>
        <view class="result-content">
          <text class="result-text">{{ generatedText }}</text>
        </view>
        <view class="result-actions">
          <u-button type="default" size="mini" @click="regenerate">重新生成</u-button>
          <u-button type="primary" size="mini" @click="useText">使用文案</u-button>
        </view>
      </view>

      <!-- 历史记录 -->
      <view class="section" v-if="historyList.length > 0">
        <view class="section-header">
          <text class="section-title">历史记录</text>
          <text class="clear-btn" @click="clearHistory">清空</text>
        </view>
        <view class="history-list">
          <view class="history-item" v-for="(item, index) in historyList" :key="index" @click="useHistory(item)">
            <view class="history-info">
              <text class="history-car">{{ item.carInfo.brand }} {{ item.carInfo.year }}款</text>
              <text class="history-platform">{{ getPlatformName(item.platform) }}</text>
            </view>
            <text class="history-time">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { generateCopywriting } from '@/api/ai'
import { formatTime } from '@/utils/format'

export default {
  data() {
    return {
      carInfo: {
        brand: '',
        year: '',
        mileage: '',
        price: '',
        highlights: ''
      },
      selectedPlatform: 'wechat',
      generatedText: '',
      generating: false,
      historyList: []
    }
  },
  onLoad() {
    this.loadHistory()
  },
  methods: {
    formatTime,
    async generateCopywriting() {
      if (!this.carInfo.brand) {
        uni.$u.toast('请输入品牌车型')
        return
      }

      this.generating = true
      try {
        const res = await generateCopywriting({
          carInfo: this.carInfo,
          platform: this.selectedPlatform
        })

        if (res.data) {
          this.generatedText = res.data.content || res.data.text || ''
          this.saveHistory()
        }
      } catch (e) {
        console.error('生成文案失败', e)
        uni.$u.toast('生成失败，请重试')
      } finally {
        this.generating = false
      }
    },
    copyText() {
      if (!this.generatedText) return

      uni.setClipboardData({
        data: this.generatedText,
        success: () => {
          uni.$u.toast('已复制到剪贴板')
        }
      })
    },
    regenerate() {
      this.generateCopywriting()
    },
    useText() {
      // 返回上一页并传递文案
      const pages = getCurrentPages()
      const prevPage = pages[pages.length - 2]
      if (prevPage) {
        prevPage.$vm.setCopywriting && prevPage.$vm.setCopywriting(this.generatedText)
      }
      uni.navigateBack()
    },
    saveHistory() {
      const item = {
        carInfo: { ...this.carInfo },
        platform: this.selectedPlatform,
        content: this.generatedText,
        createTime: new Date()
      }

      this.historyList.unshift(item)
      if (this.historyList.length > 10) {
        this.historyList = this.historyList.slice(0, 10)
      }

      try {
        uni.setStorageSync('copywriting_history', this.historyList)
      } catch (e) {}
    },
    loadHistory() {
      try {
        const saved = uni.getStorageSync('copywriting_history')
        if (saved) {
          this.historyList = saved
        }
      } catch (e) {}
    },
    clearHistory() {
      uni.showModal({
        title: '确认清空',
        content: '确定要清空所有历史记录吗？',
        success: (res) => {
          if (res.confirm) {
            this.historyList = []
            try {
              uni.removeStorageSync('copywriting_history')
            } catch (e) {}
            uni.$u.toast('已清空')
          }
        }
      })
    },
    useHistory(item) {
      this.carInfo = { ...item.carInfo }
      this.selectedPlatform = item.platform
      this.generatedText = item.content
    },
    getPlatformName(platform) {
      const map = {
        wechat: '朋友圈',
        douyin: '抖音',
        xiaohongshu: '小红书',
        weibo: '微博'
      }
      return map[platform] || platform
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #0F172A;
$cta-color: #0369A1;
$bg-color: #F8FAFC;
$text-color: #020617;
$text-secondary: #64748B;
$border-color: #E2E8F0;
$border-radius: 16rpx;
$shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
$transition: all 0.2s ease;

.page {
  min-height: 100vh;
  background: $bg-color;
}

.page-content {
  padding: 20rpx;
}

.section {
  background: #fff;
  border-radius: $border-radius;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: $shadow;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-color;
}

/* 表单 */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.form-item {
  display: flex;
  align-items: center;
}

.form-label {
  width: 160rpx;
  font-size: 28rpx;
  color: $text-color;
}

.form-input {
  flex: 1;
  height: 80rpx;
  background: $bg-color;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: $text-color;
}

.form-textarea {
  flex: 1;
  height: 120rpx;
  background: $bg-color;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
  font-size: 28rpx;
  color: $text-color;
}

/* 平台选择 */
.platform-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.platform-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 140rpx;
  height: 140rpx;
  background: $bg-color;
  border-radius: 16rpx;
  cursor: pointer;
  transition: $transition;
  border: 2rpx solid transparent;

  &.active {
    border-color: $cta-color;
    background: rgba(3, 105, 161, 0.05);
  }

  &:active {
    transform: scale(0.95);
  }
}

.platform-name {
  font-size: 24rpx;
  color: $text-secondary;
  margin-top: 8rpx;
}

/* 生成按钮 */
.generate-section {
  margin-bottom: 20rpx;
}

/* 生成结果 */
.result-content {
  background: $bg-color;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.result-text {
  font-size: 28rpx;
  color: $text-color;
  line-height: 1.6;
}

.result-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
}

/* 历史记录 */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid $border-color;
  cursor: pointer;

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}

.history-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.history-car {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
}

.history-platform {
  font-size: 22rpx;
  color: $cta-color;
  background: rgba(3, 105, 161, 0.1);
  padding: 4rpx 12rpx;
  border-radius: 4rpx;
}

.history-time {
  font-size: 22rpx;
  color: $text-secondary;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 24rpx;
  color: $cta-color;
  cursor: pointer;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  transition: $transition;

  &:active {
    background: rgba(3, 105, 161, 0.1);
  }
}

.clear-btn {
  font-size: 24rpx;
  color: #f56c6c;
  cursor: pointer;
}
</style>
