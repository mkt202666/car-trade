<template>
  <view class="page">
    <u-navbar :title="currentChat ? 'AI对话' : 'AI助理'" :border-bottom="false" :placeholder="true"></u-navbar>

    <view class="page-content">
      <!-- AI 快捷功能入口 -->
      <view class="ai-grid" v-if="!currentChat">
        <view class="ai-item" v-for="item in aiFunctions" :key="item.id" @click="openFunction(item)">
          <view class="ai-icon" :style="{ background: item.color }">
            <u-icon :name="item.icon" size="44" color="#fff"></u-icon>
          </view>
          <text class="ai-name">{{ item.name }}</text>
          <text class="ai-desc">{{ item.desc }}</text>
        </view>
      </view>

      <!-- 最近对话 -->
      <view class="section" v-if="!currentChat">
        <view class="section-title">最近对话</view>
        <view class="history-list" v-if="historyList.length > 0">
          <view class="history-item" v-for="item in historyList" :key="item.id" @click="loadHistory(item)">
            <view class="history-left">
              <u-icon name="chat" size="32" color="#3c9cff"></u-icon>
              <text class="history-title">{{ item.title }}</text>
            </view>
            <text class="history-time">{{ formatTime(item.lastTime) }}</text>
          </view>
        </view>
        <u-empty v-else mode="list" text="暂无对话记录"></u-empty>
      </view>

      <!-- 快速提问 -->
      <view class="quick-ask" v-if="!currentChat">
        <view class="section-title">快速提问</view>
        <view class="quick-tags">
          <text class="quick-tag" @click="startChat('最近宝马X5行情怎么样')">最近宝马X5行情怎么样</text>
          <text class="quick-tag" @click="startChat('帮我找10万以内的SUV')">帮我找10万以内的SUV</text>
          <text class="quick-tag" @click="startChat('生成一段获客文案')">生成一段获客文案</text>
          <text class="quick-tag" @click="startChat('分析一下我这个车源')">分析一下我这个车源</text>
        </view>
      </view>

      <!-- AI 聊天界面 -->
      <view class="chat-view" v-if="currentChat">
        <view class="chat-back" @click="closeChat">
          <u-icon name="arrow-left" size="32" color="#333"></u-icon>
          <text class="chat-back-text">返回</text>
        </view>
        <scroll-view scroll-y class="chat-messages" :scroll-top="scrollTop" :scroll-with-animation="true">
          <view class="chat-item" v-for="(msg, index) in messages" :key="index" :class="msg.role">
            <image :src="msg.role === 'user' ? userAvatar : aiAvatar" mode="aspectFill" class="chat-avatar"></image>
            <view class="chat-bubble">
              <text class="chat-text">{{ msg.content }}</text>
              <text class="chat-time" v-if="msg.createTime">{{ formatTime(msg.createTime) }}</text>
            </view>
          </view>
          <view class="loading-item" v-if="isSending">
            <text class="loading-dot">正在思考...</text>
          </view>
        </scroll-view>

        <view class="chat-input-bar">
          <u-input v-model="inputText" placeholder="输入您的问题..." :border="false" :height="72" @confirm="sendMessage"></u-input>
          <view class="chat-send-btn" @click="sendMessage" :class="{ disabled: !inputText.trim() }">
            <u-icon name="arrow-rightward" size="28" color="#fff"></u-icon>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { aiChat, marketAnalysis, aiSearch, generateCopywriting, autoOutreach } from '@/api/ai'

export default {
  data() {
    return {
      aiFunctions: [
        { id: 'search', name: '智能找车', desc: '描述需求，快速匹配', icon: 'search', color: 'linear-gradient(135deg, #3c9cff, #2979ff)' },
        { id: 'market', name: '行情分析', desc: '掌握市场最新动态', icon: 'chart', color: 'linear-gradient(135deg, #f9ae3d, #f7b731)' },
        { id: 'copywriting', name: '获客文案', desc: 'AI生成推广文案', icon: 'edit-pen', color: 'linear-gradient(135deg, #a855f7, #8b5cf6)' },
        { id: 'outreach', name: '自动外联', desc: '智能触达潜在客户', icon: 'man', color: 'linear-gradient(135deg, #5ac725, #4ca81b)' },
        { id: 'analyze', name: '车源分析', desc: '车源价值评估', icon: 'grid', color: 'linear-gradient(135deg, #06b6d4, #0891b2)' },
        { id: 'price', name: '估价助手', desc: '快速估算车价', icon: 'rmb-circle', color: 'linear-gradient(135deg, #f56c6c, #e74c3c)' }
      ],
      historyList: [],
      currentChat: null,
      messages: [],
      inputText: '',
      scrollTop: 0,
      isSending: false,
      userAvatar: '/static/default-avatar.png',
      aiAvatar: '/static/default-car.png'
    }
  },
  mounted() {
    this.loadHistoryList()
  },
  methods: {
    loadHistoryList() {
      try {
        const saved = uni.getStorageSync('ai_chat_history')
        if (saved) {
          this.historyList = saved
        }
      } catch (e) {}
    },
    saveHistoryList() {
      try {
        uni.setStorageSync('ai_chat_history', this.historyList)
      } catch (e) {}
    },
    openFunction(item) {
      const promptMap = {
        search: '帮我推荐一款适合家用的二手车',
        market: '请分析一下最近的二手车市场行情',
        copywriting: '帮我写一段吸引人的二手车推广文案',
        outreach: '如何用AI自动触达潜在客户？',
        analyze: '请帮我分析一下这个车源的价值',
        price: '请帮我估算一下2018年宝马X5的价格'
      }
      this.startChat(promptMap[item.id] || item.name)
    },
    startChat(prompt) {
      this.currentChat = { id: Date.now(), title: prompt || '新对话' }
      this.messages = []
      if (prompt) {
        this.messages.push({ role: 'ai', content: '您好！我是5D好车的AI助理，有什么可以帮您？' })
      }
      this.scrollToBottom()
      if (prompt) {
        this.inputText = prompt
        setTimeout(() => this.sendMessage(), 300)
      }
    },
    loadHistory(item) {
      this.currentChat = { id: item.id, title: item.title }
      try {
        const saved = uni.getStorageSync('ai_chat_' + item.id)
        this.messages = saved || []
      } catch (e) {
        this.messages = []
      }
      this.scrollToBottom()
    },
    closeChat() {
      this.saveCurrentChat()
      this.currentChat = null
      this.messages = []
      this.loadHistoryList()
    },
    formatTime(t) {
      if (!t) return ''
      const d = new Date(t)
      return d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0')
    },
    async sendMessage() {
      const text = this.inputText.trim()
      if (!text || this.isSending) return
      const userMsg = { role: 'user', content: text, createTime: new Date() }
      this.messages.push(userMsg)
      this.inputText = ''
      this.isSending = true
      this.scrollToBottom()
      try {
        const res = await aiChat({ message: text, history: this.messages.slice(-6).map(m => ({ role: m.role, content: m.content })) })
        const reply = res.data && (res.data.content || res.data.reply || res.data.message || res.data)
        const aiMsg = { role: 'ai', content: typeof reply === 'string' ? reply : (reply.content || '感谢您的提问'), createTime: new Date() }
        this.messages.push(aiMsg)
        this.saveCurrentChat()
      } catch (e) {
        this.messages.push({ role: 'ai', content: '抱歉，AI服务暂时不可用，请稍后再试。', createTime: new Date() })
      } finally {
        this.isSending = false
        this.scrollToBottom()
      }
    },
    saveCurrentChat() {
      if (!this.currentChat || this.messages.length === 0) return
      try {
        const firstMsg = this.messages.find(m => m.role === 'user')
        const title = firstMsg ? firstMsg.content.slice(0, 20) : '新对话'
        uni.setStorageSync('ai_chat_' + this.currentChat.id, this.messages)
        const existing = this.historyList.find(h => h.id === this.currentChat.id)
        if (existing) {
          existing.title = title
          existing.lastTime = new Date()
        } else {
          this.historyList.unshift({ id: this.currentChat.id, title, lastTime: new Date() })
        }
        this.saveHistoryList()
      } catch (e) {}
    },
    scrollToBottom() {
      this.$nextTick(() => {
        this.scrollTop = 99999
        setTimeout(() => { this.scrollTop = this.scrollTop + 1 }, 50)
      })
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

/* AI 聊天界面 */
.chat-view {
  padding-top: 20rpx;
}
.chat-back {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 30rpx;
  background: #fff;
  margin-bottom: 16rpx;
}
.chat-back-text {
  font-size: 26rpx;
  color: #333;
}
.chat-messages {
  height: calc(100vh - 320rpx);
  padding: 0 20rpx;
}
.chat-item {
  display: flex;
  margin-bottom: 30rpx;
  gap: 16rpx;
}
.chat-item.user {
  flex-direction: row-reverse;
}
.chat-avatar {
  width: 68rpx;
  height: 68rpx;
  border-radius: 50%;
  flex-shrink: 0;
  background: #eee;
}
.chat-bubble {
  max-width: 70%;
  padding: 22rpx;
  background: #fff;
  border-radius: 16rpx;
  line-height: 1.6;
}
.chat-item.user .chat-bubble {
  background: #3c9cff;
  color: #fff;
}
.chat-text {
  font-size: 28rpx;
  color: inherit;
  line-height: 1.6;
  white-space: pre-wrap;
}
.chat-item.user .chat-text {
  color: #fff;
}
.chat-time {
  font-size: 20rpx;
  color: #999;
  display: block;
  margin-top: 8rpx;
}
.chat-item.user .chat-time {
  color: rgba(255,255,255,0.7);
}
.loading-item {
  display: flex;
  justify-content: flex-start;
  padding-left: 84rpx;
  margin-bottom: 20rpx;
}
.loading-dot {
  font-size: 24rpx;
  color: #999;
  padding: 16rpx 24rpx;
  background: #fff;
  border-radius: 12rpx;
}
.chat-input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 16rpx 20rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  gap: 12rpx;
  border-top: 1rpx solid #eee;
}
.chat-input-bar u-input {
  flex: 1;
  background: #f5f5f5;
  border-radius: 40rpx;
  padding: 0 24rpx;
}
.chat-send-btn {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  background: #3c9cff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.disabled {
    background: #ccc;
  }
}
</style>