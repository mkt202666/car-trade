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
        <view class="section-header">
          <view class="section-title">最近对话</view>
          <text class="clear-btn" v-if="historyList.length > 0" @click="clearHistory">清空</text>
        </view>
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
import { aiChat, marketAnalysis, aiSearch, generateCopywriting, autoOutreach, carAnalysis, priceEstimate } from '@/api/ai'
import { requireAuth } from '@/utils/auth'

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
      aiAvatar: '/static/default-car.png',
      currentFunction: null
    }
  },
  mounted() {
    if (!requireAuth()) return
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
      if (!requireAuth()) return
      this.currentFunction = item.id
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
        const welcomeMsg = this.getWelcomeMessage(this.currentFunction)
        this.messages.push({ role: 'ai', content: welcomeMsg, createTime: new Date() })
      }
      this.scrollToBottom()
      if (prompt) {
        this.inputText = prompt
        setTimeout(() => this.sendMessage(), 300)
      }
    },
    getWelcomeMessage(functionId) {
      const welcomeMap = {
        search: '您好！我是智能找车助手。请告诉我您的需求，比如：预算、品牌、车型、用途等，我会为您推荐最合适的车型。',
        market: '您好！我是行情分析助手。请问您想了解哪个品牌或车型市场行情？比如"BBA豪华品牌"或"日系紧凑型SUV"。',
        copywriting: '您好！我是获客文案助手。请告诉我您的车源信息，如：品牌、车型、配置、亮点等，我来帮您生成吸引人的推广文案。',
        outreach: '您好！我是自动外联助手。请提供客户信息和您的需求，我来帮您制定智能触达方案和话术。',
        analyze: '您好！我是车源分析助手。请提供车源信息，如：品牌、车型、年份、里程、车况描述等，我来帮您全面评估车辆价值。',
        price: '您好！我是估价助手。请告诉我车辆信息，如：品牌、车型、年份、里程、配置等，我来帮您估算合理的价格区间。'
      }
      return welcomeMap[functionId] || '您好！我是5D好车的AI助理，有什么可以帮您？'
    },
    loadHistory(item) {
      if (!requireAuth()) return
      this.currentChat = { id: item.id, title: item.title }
      this.currentFunction = item.functionId || null
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
      this.currentFunction = null
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
      if (!requireAuth()) return
      
      const userMsg = { role: 'user', content: text, createTime: new Date() }
      this.messages.push(userMsg)
      this.inputText = ''
      this.isSending = true
      this.scrollToBottom()
      
      try {
        let res
        // 根据当前功能调用对应的API
        if (this.currentFunction === 'search') {
          res = await aiSearch({ message: text })
        } else if (this.currentFunction === 'market') {
          res = await marketAnalysis({ message: text })
        } else if (this.currentFunction === 'copywriting') {
          res = await generateCopywriting({ message: text })
        } else if (this.currentFunction === 'outreach') {
          res = await autoOutreach({ message: text })
        } else if (this.currentFunction === 'analyze') {
          res = await carAnalysis({ message: text })
        } else if (this.currentFunction === 'price') {
          res = await priceEstimate({ message: text })
        } else {
          // 普通对话
          res = await aiChat({ message: text, history: this.messages.slice(-6).map(m => ({ role: m.role, content: m.content })) })
        }
        
        const reply = res.data && (res.data.content || res.data.reply || res.data.message || res.data)
        const aiMsg = { role: 'ai', content: typeof reply === 'string' ? reply : (reply.content || '感谢您的提问'), createTime: new Date() }
        this.messages.push(aiMsg)
        this.saveCurrentChat()
      } catch (e) {
        console.error('AI请求失败', e)
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
          existing.functionId = this.currentFunction
        } else {
          this.historyList.unshift({ id: this.currentChat.id, title, lastTime: new Date(), functionId: this.currentFunction })
        }
        this.saveHistoryList()
      } catch (e) {}
    },
    scrollToBottom() {
      this.$nextTick(() => {
        this.scrollTop = 99999
        setTimeout(() => { this.scrollTop = this.scrollTop + 1 }, 50)
      })
    },
    clearHistory() {
      uni.showModal({
        title: '确认清空',
        content: '确定要清空所有对话记录吗？',
        success: (res) => {
          if (res.confirm) {
            try {
              const keys = uni.getStorageInfoSync().keys
              keys.forEach(key => {
                if (key.startsWith('ai_chat_')) {
                  uni.removeStorageSync(key)
                }
              })
              uni.removeStorageSync('ai_chat_history')
              this.historyList = []
              uni.$u.toast('已清空')
            } catch (e) {}
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
/* 设计系统变量 */
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
  padding-bottom: 40rpx;
}

/* AI 功能入口 */
.ai-grid {
  background: #fff;
  padding: 30rpx 20rpx;
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 16rpx;
  border-radius: $border-radius;
  margin: 20rpx;
  box-shadow: $shadow;
}
.ai-item {
  width: 33.33%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;
  cursor: pointer;
  transition: $transition;
  border-radius: $border-radius;

  &:active {
    background: rgba(0, 0, 0, 0.03);
    transform: scale(0.95);
  }
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
  color: $text-color;
}
.ai-desc {
  font-size: 20rpx;
  color: $text-secondary;
  margin-top: 6rpx;
  text-align: center;
}

/* 通用区块 */
.section {
  background: #fff;
  margin: 20rpx;
  padding: 30rpx;
  border-radius: $border-radius;
  box-shadow: $shadow;
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 20rpx;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}
.section-header .section-title {
  margin-bottom: 0;
}
.clear-btn {
  font-size: 24rpx;
  color: $text-secondary;
  cursor: pointer;
  transition: $transition;
  padding: 8rpx 16rpx;
  border-radius: $border-radius;

  &:active {
    background: rgba(0, 0, 0, 0.05);
  }
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
  border-bottom: 1rpx solid $border-color;
  cursor: pointer;
  transition: $transition;
  border-radius: $border-radius;
  margin: 0 -10rpx;
  padding-left: 10rpx;
  padding-right: 10rpx;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: rgba(0, 0, 0, 0.03);
  }
}
.history-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.history-title {
  font-size: 26rpx;
  color: $text-color;
}
.history-time {
  font-size: 22rpx;
  color: $text-secondary;
}

/* 快速提问 */
.quick-ask {
  background: #fff;
  margin: 20rpx;
  padding: 30rpx;
  border-radius: $border-radius;
  box-shadow: $shadow;
}
.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}
.quick-tag {
  font-size: 24rpx;
  color: $cta-color;
  background: rgba(3, 105, 161, 0.08);
  padding: 12rpx 24rpx;
  border-radius: 32rpx;
  cursor: pointer;
  transition: $transition;
  border: 1rpx solid transparent;

  &:active {
    background: rgba(3, 105, 161, 0.15);
    transform: scale(0.98);
  }
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
  cursor: pointer;
  transition: $transition;
  border-radius: $border-radius;
  margin: 20rpx;
  box-shadow: $shadow;

  &:active {
    opacity: 0.7;
  }
}
.chat-back-text {
  font-size: 26rpx;
  color: $text-color;
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
  border: 2rpx solid $border-color;
}
.chat-bubble {
  max-width: 70%;
  padding: 22rpx;
  background: #fff;
  border-radius: $border-radius;
  line-height: 1.6;
  box-shadow: $shadow;
}
.chat-item.user .chat-bubble {
  background: $cta-color;
  color: #fff;
}
.chat-text {
  font-size: 28rpx;
  color: $text-color;
  line-height: 1.6;
  white-space: pre-wrap;
}
.chat-item.user .chat-text {
  color: #fff;
}
.chat-time {
  font-size: 20rpx;
  color: $text-secondary;
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
  color: $text-secondary;
  padding: 16rpx 24rpx;
  background: #fff;
  border-radius: $border-radius;
  box-shadow: $shadow;
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
  border-top: 1rpx solid $border-color;
  box-shadow: 0 -4rpx 16rpx rgba(15, 23, 42, 0.08);
}
.chat-input-bar u-input {
  flex: 1;
  background: $bg-color;
  border-radius: 40rpx;
  padding: 0 24rpx;
}
.chat-send-btn {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  background: $cta-color;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  cursor: pointer;
  transition: $transition;

  &:active {
    transform: scale(0.95);
    background: darken($cta-color, 5%);
  }

  &.disabled {
    background: $border-color;
  }
}
</style>