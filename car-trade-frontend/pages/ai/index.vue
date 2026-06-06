<template>
  <view>
    <u-navbar title="AI助理" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="showHistory">
        <u-icon name="clock" size="36"></u-icon>
      </view>
    </u-navbar>
    <view class="page-content">
      <view class="ai-banner">
        <image src="/static/ai-banner.png" mode="aspectFill" class="banner-img"></image>
        <text class="banner-title">5D AI 智能车商助理</text>
        <text class="banner-desc">数据分析 · 精准获客 · 智能营销</text>
      </view>
      <view class="func-grid">
        <view class="func-item" @click="openFunction('market')">
          <view class="func-icon" style="background: linear-gradient(135deg, #3c9cff, #2979ff);">
            <u-icon name="trending-up" size="40" color="#fff"></u-icon>
          </view>
          <text class="func-name">行情分析</text>
        </view>
        <view class="func-item" @click="openFunction('search')">
          <view class="func-icon" style="background: linear-gradient(135deg, #5ac725, #4ca81b);">
            <u-icon name="search" size="40" color="#fff"></u-icon>
          </view>
          <text class="func-name">智能找车</text>
        </view>
        <view class="func-item" @click="openFunction('copywrite')">
          <view class="func-icon" style="background: linear-gradient(135deg, #f9ae3d, #f7b731);">
            <u-icon name="edit-pen" size="40" color="#fff"></u-icon>
          </view>
          <text class="func-name">文案生成</text>
        </view>
        <view class="func-item" @click="openFunction('outreach')">
          <view class="func-icon" style="background: linear-gradient(135deg, #f56c6c, #e74c3c);">
            <u-icon name="share" size="40" color="#fff"></u-icon>
          </view>
          <text class="func-name">主动拓客</text>
        </view>
        <view class="func-item" @click="openFunction('distribute')">
          <view class="func-icon" style="background: linear-gradient(135deg, #a855f7, #8b5cf6);">
            <u-icon name="red-packet" size="40" color="#fff"></u-icon>
          </view>
          <text class="func-name">车源分发</text>
        </view>
        <view class="func-item" @click="openFunction('chat')">
          <view class="func-icon" style="background: linear-gradient(135deg, #06b6d4, #0891b2);">
            <u-icon name="chat" size="40" color="#fff"></u-icon>
          </view>
          <text class="func-name">AI对话</text>
        </view>
      </view>
      <view class="chat-section">
        <view class="section-title">快捷咨询</view>
        <view class="chat-box">
          <view class="msg-list" ref="msgList">
            <view class="msg-item" v-for="(msg, index) in messages" :key="index" :class="msg.role">
              <image :src="msg.role === 'user' ? '/static/default-avatar.png' : '/static/ai-avatar.png'" mode="aspectFill" class="msg-avatar"></image>
              <view class="msg-content">{{ msg.content }}</view>
            </view>
          </view>
          <view class="chat-input">
            <u-input v-model="inputText" placeholder="输入您的问题..." :border="false" :height="72"></u-input>
            <u-button size="mini" type="primary" @click="sendMessage" :disabled="!inputText">发送</u-button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { aiSearch, marketAnalysis, generateCopywriting, autoOutreach, distributeCar } from '@/api/ai'

export default {
  data() {
    return {
      inputText: '',
      messages: [
        { role: 'ai', content: '您好！我是5D好车AI助理，可以帮您分析行情、智能找车、生成营销文案等。请问有什么可以帮您？' }
      ]
    }
  },
  methods: {
    async sendMessage() {
      if (!this.inputText) return
      const text = this.inputText
      this.messages.push({ role: 'user', content: text })
      this.inputText = ''
      this.messages.push({ role: 'ai', content: '正在思考...' })
      try {
        const res = await aiSearch({ query: text })
        this.messages.pop()
        this.messages.push({ role: 'ai', content: res.data.reply || res.data.result || '已收到您的问题，稍后会有专人联系您。' })
      } catch (e) {
        this.messages.pop()
        this.messages.push({ role: 'ai', content: '抱歉，暂时无法处理您的问题，请稍后重试。' })
      }
    },
    openFunction(type) {
      const pages = {
        market: { url: '' },
        search: { url: '/pages/home/index?aiSearch=1' },
        copywrite: { url: '' },
        outreach: { url: '' },
        distribute: { url: '' },
        chat: { url: '' }
      }
      const page = pages[type]
      if (page && page.url) {
        uni.switchTab({ url: page.url })
      } else {
        uni.$u.toast('功能开发中，敬请期待')
      }
    },
    showHistory() {}
  }
}
</script>

<style lang="scss" scoped>
.navbar-right { padding-right: 20rpx; }
.page-content { min-height: 100vh; background: #f5f5f5; }
.ai-banner { position: relative; height: 280rpx; margin: 20rpx 30rpx; border-radius: 20rpx; overflow: hidden; }
.banner-img { width: 100%; height: 100%; }
.banner-title { position: absolute; top: 50rpx; left: 40rpx; font-size: 36rpx; font-weight: 700; color: #fff; }
.banner-desc { position: absolute; top: 110rpx; left: 40rpx; font-size: 24rpx; color: rgba(255,255,255,0.8); }
.func-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 20rpx;
  padding: 0 30rpx; margin-bottom: 30rpx;
}
.func-item { display: flex; flex-direction: column; align-items: center; }
.func-icon {
  width: 100rpx; height: 100rpx; border-radius: 24rpx;
  display: flex; align-items: center; justify-content: center;
}
.func-name { font-size: 24rpx; color: #333; margin-top: 12rpx; }
.chat-section { background: #fff; border-radius: 20rpx; margin: 0 30rpx 30rpx; padding: 30rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; }
.chat-box { }
.msg-list { max-height: 500rpx; overflow-y: auto; margin-bottom: 20rpx; }
.msg-item { display: flex; margin-bottom: 20rpx; }
.msg-item.user { flex-direction: row-reverse; }
.msg-avatar { width: 60rpx; height: 60rpx; border-radius: 50%; flex-shrink: 0; }
.msg-content {
  max-width: 70%; padding: 16rpx 20rpx; border-radius: 12rpx; font-size: 26rpx;
  margin: 0 16rpx; line-height: 1.6;
}
.msg-item.ai .msg-content { background: #f0f4ff; color: #333; }
.msg-item.user .msg-content { background: #3c9cff; color: #fff; }
.chat-input { display: flex; align-items: center; border: 1rpx solid #eee; border-radius: 40rpx; padding: 0 16rpx; }
.chat-input u-input { flex: 1; }
</style>
