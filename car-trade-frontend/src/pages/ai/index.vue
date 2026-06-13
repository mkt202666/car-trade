<template>
  <view class="page">
    <u-navbar title="AI 智能助手" :border-bottom="false" :placeholder="true"></u-navbar>

    <view class="page-content" v-if="!currentChat">
      <!-- 顶部 Banner -->
      <view class="hero-section">
        <view class="hero-bg"></view>
        <view class="hero-inner">
          <view class="hero-icon">
            <text class="hero-icon-text">✦</text>
          </view>
          <view class="hero-text-wrap">
            <text class="hero-title">5D 好车 AI 助手</text>
            <text class="hero-desc">智能找车 · 行情分析 · 一键文案 · 精准估价</text>
          </view>
        </view>
      </view>

      <!-- 快捷功能网格 -->
      <view class="ai-grid">
        <view class="grid-title-row">
          <text class="grid-title">智能工具</text>
          <text class="grid-subtitle">选择您需要的功能</text>
        </view>
        <view class="grid-cards">
          <view
            class="ai-card"
            v-for="(item, index) in aiFunctions"
            :key="item.id"
            :class="'ai-card-' + item.id"
            @click="openFunction(item)"
            :style="{ animationDelay: (index * 60) + 'ms' }"
          >
            <view class="ai-card-icon" :style="{ background: item.bgGrad }">
              <text class="ai-card-emoji">{{ item.emoji }}</text>
              <view class="ai-card-shine"></view>
            </view>
            <view class="ai-card-body">
              <text class="ai-card-name">{{ item.name }}</text>
              <text class="ai-card-desc">{{ item.desc }}</text>
            </view>
            <view class="ai-card-arrow">
              <text>›</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 最近对话 -->
      <view class="section" v-if="historyList.length > 0">
        <view class="section-header">
          <view class="section-title-wrap">
            <text class="section-title">最近对话</text>
            <view class="section-badge">{{ historyList.length }}</view>
          </view>
          <view class="clear-btn" @click="clearHistory">
            <text>清空</text>
          </view>
        </view>
        <view class="history-list">
          <view class="history-item" v-for="(item, idx) in historyList" :key="item.id" @click="loadHistory(item)" :style="{ animationDelay: (idx * 40) + 'ms' }">
            <view class="history-icon">
              <text>💬</text>
            </view>
            <view class="history-info">
              <text class="history-title">{{ item.title }}</text>
              <text class="history-time">{{ formatTime(item.lastTime) }}</text>
            </view>
            <view class="history-arrow">
              <text>›</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 快速提问 -->
      <view class="quick-section">
        <view class="section-title-row">
          <text class="section-title">试试问我</text>
        </view>
        <view class="quick-tags">
          <view class="quick-tag" v-for="(q, i) in quickQuestions" :key="i" @click="startChat(q)" :style="{ animationDelay: (i * 50) + 'ms' }">
            <text class="quick-tag-icon">💡</text>
            <text class="quick-tag-text">{{ q }}</text>
          </view>
        </view>
      </view>

      <view class="footer-space"></view>
    </view>

    <!-- AI 聊天界面 -->
    <view class="chat-view" v-if="currentChat">
      <!-- 返回栏 -->
      <view class="chat-back-bar">
        <view class="chat-back" @click="closeChat">
          <text class="back-icon">‹</text>
          <text class="back-text">返回</text>
        </view>
        <view class="chat-status">
          <view class="status-dot"></view>
          <text class="status-text">{{ currentFunction ? getFunctionName(currentFunction) : 'AI 助手' }}</text>
        </view>
        <view class="chat-placeholder"></view>
      </view>

      <!-- 消息列表 -->
      <scroll-view scroll-y class="chat-messages" :scroll-top="scrollTop" :scroll-with-animation="true">
        <view class="chat-item" v-for="(msg, index) in messages" :key="index" :class="[msg.role, { streaming: msg.streaming }]">
          <view class="chat-avatar" :class="msg.role">
            <text class="avatar-emoji">{{ msg.role === 'user' ? '👤' : '🤖' }}</text>
          </view>
          <view class="chat-bubble-wrap">
            <view class="chat-bubble" :class="{ 'bubble-streaming': msg.streaming }">
              <text class="chat-text" v-if="msg.content">{{ msg.content }}</text>
              <text class="chat-text chat-cursor" v-if="msg.streaming && msg.content">&#8203;</text>
              <text class="chat-text placeholder" v-if="!msg.content && msg.streaming">{{ msg.loadingText || '正在思考中...' }}</text>
            </view>
            <text class="chat-time" v-if="msg.createTime">{{ formatChatTime(msg.createTime) }}</text>
          </view>
        </view>
        <view class="chat-bottom-space"></view>
      </scroll-view>

      <!-- 输入栏 -->
      <view class="chat-input-bar">
        <view class="input-wrap">
          <input
            class="chat-input"
            v-model="inputText"
            :disabled="isSending"
            placeholder="请输入您的问题..."
            placeholder-class="input-placeholder"
            @confirm="sendMessage"
            confirm-type="send"
          />
        </view>
        <view
          class="send-btn"
          :class="{ disabled: (!inputText.trim() && !isSending), sending: isSending }"
          @click="sendMessage"
        >
          <view v-if="isSending" class="spinner"></view>
          <text v-else class="send-icon">→</text>
        </view>
      </view>
    </view>

    <!-- 自定义底部导航栏 -->
    <custom-tab-bar />
  </view>
</template>

<script>
import { aiChat, aiChatStream, marketAnalysis, aiSearch, generateCopywriting, generateCustomerCopywriting, autoOutreach, carAnalysis, priceEstimate } from '@/api/ai'
import { requireAuth } from '@/utils/auth'
import CustomTabBar from '@/custom-tab-bar/index.vue'

export default {
  name: 'AiAssistant',
  components: {
    CustomTabBar
  },
  data() {
    return {
      aiFunctions: [
        { id: 'search', name: '智能找车', desc: '描述需求，快速匹配', emoji: '🔍', bgGrad: 'linear-gradient(135deg, #0369A1 0%, #0EA5E9 100%)' },
        { id: 'market', name: '行情分析', desc: '掌握市场最新动态', emoji: '📈', bgGrad: 'linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%)' },
        { id: 'customer', name: '获客文案', desc: 'AI生成推广文案', emoji: '✍️', bgGrad: 'linear-gradient(135deg, #8B5CF6 0%, #A78BFA 100%)' },
        { id: 'outreach', name: '自动外联', desc: '智能触达潜在客户', emoji: '🤝', bgGrad: 'linear-gradient(135deg, #10B981 0%, #34D399 100%)' },
        { id: 'analyze', name: '车源分析', desc: '车源价值评估', emoji: '🔬', bgGrad: 'linear-gradient(135deg, #06B6D4 0%, #22D3EE 100%)' },
        { id: 'price', name: '估价助手', desc: '快速估算车价', emoji: '💰', bgGrad: 'linear-gradient(135deg, #EF4444 0%, #F87171 100%)' }
      ],
      quickQuestions: [
        '最近宝马X5行情怎么样？',
        '帮我找10万以内的SUV',
        '生成一段获客文案',
        '分析一下我这个车源'
      ],
      historyList: [],
      currentChat: null,
      currentFunction: null,
      messages: [],
      inputText: '',
      scrollTop: 0,
      isSending: false,
      mode: ''
    }
      },
      onLoad(options) {
        // 支持mode参数，用于从profile页面导航
        if (options && options.mode) {
          this.mode = options.mode
        }
        this.loadModeData()
      },
      onShow() {
        // tabbar 页面：每次显示时检查登录态，确保登录后能正确加载数据
        if (!requireAuth()) return
        this.loadHistoryList()
      },
      methods: {
    getFunctionName(id) {
      const fn = this.aiFunctions.find(f => f.id === id)
      return fn ? fn.name : 'AI 助手'
    },
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
    loadModeData() {
      // 根据mode参数加载不同的数据和UI
      if (this.mode === 'distribute') {
        // AI分发车源模式
        this.aiFunctions = this.aiFunctions.filter(f => ['search', 'outreach'].includes(f.id))
        this.aiFunctions.forEach(f => {
          if (f.id === 'search') {
            f.name = 'AI分发车源'
            f.desc = '智能分发您的车源到多个平台'
            f.emoji = '🚀'
            f.bgGrad = 'linear-gradient(135deg, #059669 0%, #10B981 100%)'
          }
        })
        this.quickQuestions = [
          '帮我为这辆车源生成推广文案',
          '自动推广我的车源到瓜子人人车',
          '如何增加车源曝光率'
        ]
      }
    },
    openFunction(item) {
      if (!requireAuth()) return
      this.currentFunction = item.id
      const promptMap = {
        search: '帮我推荐一款适合家用的二手车',
        market: '请分析一下最近的二手车市场行情',
        copywriting: '帮我写一段吸引人的二手车推广文案',
        customer: '帮我为这款车生成获客文案',
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
        this.scrollToBottom()
        this.inputText = prompt
        setTimeout(() => this.sendMessage(), 300)
      }
    },
    getWelcomeMessage(functionId) {
      const welcomeMap = {
        search: '您好！我是智能找车助手。请告诉我您的需求，比如：预算、品牌、车型、用途等，我会为您推荐最合适的车型。',
        market: '您好！我是行情分析助手。请问您想了解哪个品牌或车型市场行情？比如"BBA豪华品牌"或"日系紧凑型SUV"。',
        copywriting: '您好！我是获客文案助手。请告诉我您的车源信息，如：品牌、车型、配置、亮点等，我来帮您生成吸引人的推广文案。',
        customer: '您好！我是获客文案助手。请提供车源详细信息，我将为您生成专业的营销文案，帮助快速吸引潜在买家。',
        outreach: '您好！我是自动外联助手。请提供客户信息和您的需求，我来帮您制定智能触达方案和话术。',
        analyze: '您好！我是车源分析助手。请提供车源信息，如：品牌、车型、年份、里程、车况描述等，我来帮您全面评估车辆价值。',
        price: '您好！我是估价助手。请告诉我车辆信息，如：品牌、车型、年份、里程、配置等，我来帮您估算合理的价格区间。'
      }
      return welcomeMap[functionId] || '您好！我是5D好车的AI助理，有什么可以帮您？'
    },
    getLoadingText(functionId) {
      const loadingMap = {
        search: '正在匹配车源数据...',
        market: '正在分析市场行情...',
        copywriting: '正在为您构思文案...',
        customer: '正在生成获客文案...',
        outreach: '正在生成触达方案...',
        analyze: '正在评估车源价值...',
        price: '正在估算车辆价格...'
      }
      return loadingMap[functionId] || '正在处理您的请求...'
    },
    // 打字机效果：逐字把文本写入消息
    typeText(msgObj, text) {
      return new Promise((resolve) => {
        const length = text.length
        let i = 0
        const interval = Math.max(15, Math.min(40, Math.floor(1200 / Math.max(length, 10))))
        msgObj.content = ''
        const timer = setInterval(() => {
          if (i >= length) {
            clearInterval(timer)
            msgObj.content = text
            this.$nextTick(() => this.scrollToBottom())
            resolve()
            return
          }
          msgObj.content = text.substring(0, i + 1)
          i++
          if (i % 5 === 0 || i === length) {
            this.$nextTick(() => this.scrollToBottom())
          }
        }, interval)
      })
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
      const now = new Date()
      const diff = now - d
      if (diff < 60 * 1000) return '刚刚'
      if (diff < 60 * 60 * 1000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 24 * 60 * 60 * 1000) return Math.floor(diff / 3600000) + '小时前'
      return d.getMonth() + 1 + '月' + d.getDate() + '日'
    },
    formatChatTime(t) {
      if (!t) return ''
      const d = new Date(t)
      return d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0')
    },
    // 从消息列表中构建多轮对话 history（排除当前轮未完成的 loading 气泡和空内容）
    // 返回 [{ role: 'user' | 'assistant', content: string }, ...]
    buildHistory(excludeMsgRefs) {
      const result = []
      for (let i = 0; i < this.messages.length; i++) {
        const m = this.messages[i]
        // 排除指定的引用（当前轮的 userMsg / 正在打字的 aiMsg）
        if (excludeMsgRefs && excludeMsgRefs.includes(m)) continue
        // 排除空内容消息（如尚未回复完的 loading 占位气泡）
        if (!m.content || !m.content.trim()) continue
        result.push({ role: m.role === 'user' ? 'user' : 'assistant', content: m.content })
      }
      // 取最近 10 轮，控制 token
      return result.slice(-10)
    },

    async sendMessage() {
      const text = this.inputText.trim()
      if (!text || this.isSending) return
      if (!requireAuth()) return

      // 1. 推送用户消息
      const userMsg = { role: 'user', content: text, createTime: new Date() }
      this.messages.push(userMsg)
      this.inputText = ''
      this.isSending = true

      // 2. 立即滚动到底部（用户发送）
      this.$nextTick(() => this.scrollToBottom())

      // 功能模式 → API 映射
      const funcMap = {
        search: aiSearch,
        market: marketAnalysis,
        copywriting: generateCopywriting,
        customer: generateCustomerCopywriting,
        outreach: autoOutreach,
        analyze: carAnalysis,
        price: priceEstimate
      }

      // 3. 功能模式（智能找车/行情分析等）：loading 气泡 + history 上下文
      if (funcMap[this.currentFunction]) {
        // 立即 push 一个 streaming 气泡（让用户看到系统正在处理）
        const aiMsg = { role: 'ai', content: '', createTime: new Date(), streaming: true, loadingText: this.getLoadingText(this.currentFunction) }
        this.messages.push(aiMsg)
        this.$nextTick(() => this.scrollToBottom())

        // 构建历史上下文（排除当前轮的 userMsg 和 aiMsg）
        const history = this.buildHistory([userMsg, aiMsg])

        try {
          const res = await funcMap[this.currentFunction]({ message: text, history: history })
          const rawReply = res.data && (res.data.content || res.data.reply || res.data.message || res.data)
          const replyText = typeof rawReply === 'string' ? rawReply : (rawReply && rawReply.content ? rawReply.content : '') || '感谢您的提问'
          // 打字机效果逐字显示
          await this.typeText(aiMsg, replyText)
          aiMsg.streaming = false
          this.saveCurrentChat()
        } catch (e) {
          console.error('AI请求失败', e)
          const errMsg = (e && e.data && e.data.message) || (e && e.message) || (typeof e === 'string' ? e : '') || 'AI服务暂时不可用，请稍后再试'
          aiMsg.content = '⚠️ ' + errMsg
          aiMsg.streaming = false
        } finally {
          this.isSending = false
          this.$nextTick(() => this.scrollToBottom())
        }
        return
      }

      // 4. 默认聊天模式 - 流式输出
      // 立刻 push 一个空的 AI 气泡（带 streaming 标记）
      const aiMsg = { role: 'ai', content: '', createTime: new Date(), streaming: true }
      this.messages.push(aiMsg)
      this.$nextTick(() => this.scrollToBottom())

      // 构建历史上下文（排除当前轮的 userMsg 和 aiMsg）
      const history = this.buildHistory([userMsg, aiMsg])

      const controller = aiChatStream({ message: text, history: history }, {
        onMessage: (piece, fullText) => {
          // 流式期间只写文本 — 利用 Vue 响应式自然更新；不 nextTick / 不 scrollToBottom
          aiMsg.content = fullText
        },
        onDone: (fullText) => {
          aiMsg.content = fullText
          aiMsg.streaming = false
          this.isSending = false
          this.saveCurrentChat()
          // done 时才滚到底，避免流式过程中高频 re-render 与滚动抖动
          this.$nextTick(() => this.scrollToBottom())
        },
        onError: (err) => {
          console.error('AI流式请求失败', err)
          const errMsg = (err && err.message) || 'AI服务暂时不可用，请稍后再试'
          if (!aiMsg.content) {
            aiMsg.content = '⚠️ ' + errMsg
          }
          aiMsg.streaming = false
          this.isSending = false
          this.$nextTick(() => this.scrollToBottom())
        }
      })

      this._streamController = controller
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
/* =========================================================
   5D好车 - AI智能助手 - 高级UI设计
   设计语言: Motion-Driven | Marketplace | AI Assistant
   颜色系统: 深蓝主色 #0369A1 | 琥珀金 #F59E0B | 多色渐变
   ========================================================= */

/* ============ 主容器 ============ */
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8FAFC 0%, #EEF2F7 100%);
}

.page-content {
  padding-bottom: 200rpx;
}

/* ============ Hero 区 ============ */
.hero-section {
  margin: 24rpx;
  border-radius: 32rpx;
  overflow: hidden;
  position: relative;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 50ms both;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #0F172A 0%, #1E3A5F 40%, #0369A1 100%);
  z-index: 0;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -30%;
    width: 300rpx;
    height: 300rpx;
    background: radial-gradient(circle, rgba(245, 158, 11, 0.3) 0%, transparent 70%);
    border-radius: 50%;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -30%;
    left: -20%;
    width: 250rpx;
    height: 250rpx;
    background: radial-gradient(circle, rgba(14, 165, 233, 0.3) 0%, transparent 70%);
    border-radius: 50%;
  }
}

.hero-inner {
  position: relative;
  z-index: 1;
  padding: 40rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.hero-icon {
  width: 100rpx;
  height: 100rpx;
  background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%);
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12rpx 32rpx rgba(245, 158, 11, 0.4);
  flex-shrink: 0;
  animation: float 3s ease-in-out infinite;
}

.hero-icon-text {
  font-size: 56rpx;
  color: #ffffff;
  font-weight: 900;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8rpx); }
}

.hero-text-wrap {
  flex: 1;
}

.hero-title {
  font-size: 36rpx;
  font-weight: 800;
  color: #ffffff;
  display: block;
  letter-spacing: 1rpx;
}

.hero-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.75);
  margin-top: 8rpx;
  display: block;
}

/* ============ AI 功能网格 ============ */
.ai-grid {
  padding: 12rpx 24rpx 0;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 100ms both;
}

.grid-title-row {
  margin-bottom: 20rpx;
  padding-left: 8rpx;
}

.grid-title {
  font-size: 32rpx;
  font-weight: 800;
  color: #0F172A;
  display: block;
}

.grid-subtitle {
  font-size: 24rpx;
  color: #64748B;
  margin-top: 6rpx;
  display: block;
}

.grid-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.ai-card {
  width: calc(50% - 10rpx);
  background: #ffffff;
  border-radius: 28rpx;
  padding: 28rpx 24rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(226, 232, 240, 0.6);
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) both;
  position: relative;
  overflow: hidden;

  &:active {
    transform: scale(0.96);
    box-shadow: 0 2rpx 12rpx rgba(15, 23, 42, 0.08);
  }
}

.ai-card-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

.ai-card-emoji {
  font-size: 40rpx;
  position: relative;
  z-index: 1;
}

.ai-card-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
  animation: cardShine 4s ease-in-out infinite;
}

@keyframes cardShine {
  0%, 100% { left: -100%; }
  50% { left: 150%; }
}

.ai-card-body {
  flex: 1;
  min-width: 0;
}

.ai-card-name {
  font-size: 28rpx;
  font-weight: 700;
  color: #0F172A;
  display: block;
}

.ai-card-desc {
  font-size: 22rpx;
  color: #64748B;
  margin-top: 4rpx;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ai-card-arrow {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  text {
    font-size: 36rpx;
    color: #CBD5E1;
    font-weight: 300;
  }
}

.ai-card:active .ai-card-arrow text {
  color: #0369A1;
  transform: translateX(4rpx);
  transition: all 200ms ease;
}

/* ============ Section 通用 ============ */
.section {
  margin: 32rpx 24rpx 0;
  background: #ffffff;
  border-radius: 28rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(226, 232, 240, 0.6);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 150ms both;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title-wrap {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
}

.section-badge {
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  color: #ffffff;
  font-size: 20rpx;
  font-weight: 600;
  padding: 4rpx 14rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(3, 105, 161, 0.25);
}

.clear-btn {
  padding: 10rpx 20rpx;
  border-radius: 20rpx;
  background: #F1F5F9;
  cursor: pointer;
  transition: all 200ms ease;

  text {
    font-size: 24rpx;
    color: #64748B;
    font-weight: 500;
  }

  &:active {
    background: #E2E8F0;
    transform: scale(0.96);
  }
}

/* ============ 历史记录 ============ */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  border-radius: 20rpx;
  background: #F8FAFC;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  animation: fadeInUp 400ms cubic-bezier(0.4, 0, 0.2, 1) both;

  &:active {
    background: #E2E8F0;
    transform: scale(0.98);
  }
}

.history-icon {
  width: 56rpx;
  height: 56rpx;
  background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-right: 16rpx;

  text {
    font-size: 28rpx;
  }
}

.history-info {
  flex: 1;
  min-width: 0;
}

.history-title {
  font-size: 26rpx;
  color: #0F172A;
  font-weight: 500;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-time {
  font-size: 22rpx;
  color: #94A3B8;
  margin-top: 4rpx;
  display: block;
}

.history-arrow {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  text {
    font-size: 36rpx;
    color: #CBD5E1;
    font-weight: 300;
  }
}

/* ============ 快速提问 ============ */
.quick-section {
  margin: 32rpx 24rpx 0;
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) 200ms both;
}

.section-title-row {
  margin-bottom: 20rpx;
  padding-left: 8rpx;
}

.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.quick-tag {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 20rpx 24rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  animation: fadeInUp 500ms cubic-bezier(0.4, 0, 0.2, 1) both;

  &:active {
    transform: scale(0.96);
    background: #F0F9FF;
    border-color: #0369A1;
    box-shadow: 0 4rpx 20rpx rgba(3, 105, 161, 0.15);
  }
}

.quick-tag-icon {
  font-size: 28rpx;
  flex-shrink: 0;
}

.quick-tag-text {
  font-size: 26rpx;
  color: #0F172A;
  font-weight: 500;
}

.footer-space {
  height: 80rpx;
}

/* ============ 聊天视图 ============ */
.chat-view {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  z-index: 1000;
  box-sizing: border-box;
}

.chat-back-bar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 20rpx 24rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  background: #ffffff;
  border-bottom: 1rpx solid rgba(226, 232, 240, 0.6);
  box-shadow: 0 2rpx 12rpx rgba(15, 23, 42, 0.04);
}

.chat-back {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  cursor: pointer;
  transition: all 200ms ease;

  &:active {
    background: #F1F5F9;
  }
}

.back-icon {
  font-size: 40rpx;
  color: #0369A1;
  font-weight: 300;
  line-height: 1;
}

.back-text {
  font-size: 28rpx;
  color: #0369A1;
  font-weight: 500;
}

.chat-status {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.status-dot {
  width: 12rpx;
  height: 12rpx;
  background: #10B981;
  border-radius: 50%;
  box-shadow: 0 0 8rpx #10B981;
  animation: pulse 2s ease-in-out infinite;
}

.status-text {
  font-size: 28rpx;
  color: #0F172A;
  font-weight: 600;
}

.chat-placeholder {
  width: 80rpx;
}

/* ============ 消息列表 ============ */
.chat-messages {
  flex: 1;
  min-height: 0;
  padding: 24rpx;
  padding-bottom: 40rpx;
  background: linear-gradient(180deg, #F8FAFC 0%, #EEF2F7 100%);
  box-sizing: border-box;
}

.chat-item {
  display: flex;
  gap: 16rpx;
  margin-bottom: 28rpx;
  animation: messageIn 400ms cubic-bezier(0.4, 0, 0.2, 1);

  &.user {
    flex-direction: row-reverse;
  }
}

@keyframes messageIn {
  from {
    opacity: 0;
    transform: translateY(10rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chat-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(15, 23, 42, 0.1);

  &.ai {
    background: linear-gradient(135deg, #0369A1 0%, #0EA5E9 100%);
  }

  &.user {
    background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%);
  }
}

.avatar-emoji {
  font-size: 36rpx;
}

.chat-bubble-wrap {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.chat-item.user .chat-bubble-wrap {
  align-items: flex-end;
}

.chat-bubble {
  padding: 24rpx 28rpx;
  border-radius: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
  transition: all 200ms ease;

  .chat-item.ai & {
    background: #ffffff;
    border: 1rpx solid rgba(226, 232, 240, 0.8);
    border-top-left-radius: 8rpx;
  }

  .chat-item.user & {
    background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
    border-top-right-radius: 8rpx;
  }
}

/* 流式中的气泡 - 轻微发光提示 */
.bubble-streaming {
  box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.15) !important;
  animation: bubblePulse 2s ease-in-out infinite;
}

@keyframes bubblePulse {
  0%, 100% { box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.12); }
  50% { box-shadow: 0 4rpx 20rpx rgba(3, 105, 161, 0.25); }
}

@keyframes placeholderPulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

.chat-text {
  font-size: 28rpx;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;

  .chat-item.ai & {
    color: #0F172A;
  }

  .chat-item.user & {
    color: #ffffff;
  }

  &.placeholder {
    color: #64748B;
    font-style: italic;
    opacity: 0.85;
    animation: placeholderPulse 1.4s ease-in-out infinite;
  }
}

/* 打字机光标 */
.chat-cursor {
  display: inline-block;
  width: 4rpx;
  height: 28rpx;
  margin-left: 4rpx;
  background: #0369A1;
  border-radius: 2rpx;
  vertical-align: middle;
  animation: cursorBlink 1s step-end infinite;
  opacity: 0.8;
}

@keyframes cursorBlink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.chat-time {
  font-size: 20rpx;
  color: #94A3B8;
  margin-top: 8rpx;
  padding: 0 8rpx;
}

.chat-bottom-space {
  height: 24rpx;
}

/* ============ 输入栏 ============ */
.chat-input-bar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #ffffff;
  border-top: 1rpx solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 -4rpx 16rpx rgba(15, 23, 42, 0.04);
}

.input-wrap {
  flex: 1;
  background: #F8FAFC;
  border-radius: 40rpx;
  padding: 0 28rpx;
  border: 2rpx solid transparent;
  transition: all 250ms ease;

  &:focus-within {
    border-color: #0369A1;
    background: #ffffff;
    box-shadow: 0 0 0 6rpx rgba(3, 105, 161, 0.08);
  }
}

.chat-input {
  height: 80rpx;
  font-size: 28rpx;
  color: #0F172A;
}

.input-placeholder {
  color: #94A3B8;
}

.send-btn {
  width: 88rpx;
  height: 88rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(3, 105, 161, 0.35);
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
  flex-shrink: 0;

  &:active:not(.disabled) {
    transform: scale(0.9);
    box-shadow: 0 4rpx 12rpx rgba(3, 105, 161, 0.4);
  }

  &.disabled {
    background: #CBD5E1;
    box-shadow: none;
    cursor: not-allowed;
  }

  &.sending {
    background: linear-gradient(135deg, #0284C7 0%, #0369A1 100%);
    box-shadow: 0 8rpx 24rpx rgba(3, 105, 161, 0.45);
  }
}

.send-icon {
  font-size: 40rpx;
  color: #ffffff;
  font-weight: 300;
}

.spinner {
  width: 36rpx;
  height: 36rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* ============ 入场动画 ============ */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ============ 响应式适配 ============ */
@media (max-width: 320px) {
  .hero-title {
    font-size: 32rpx;
  }
  .hero-desc {
    font-size: 22rpx;
  }
  .hero-icon {
    width: 80rpx;
    height: 80rpx;
  }
  .hero-icon-text {
    font-size: 48rpx;
  }
  .ai-card {
    width: 100%;
  }
  .chat-bubble-wrap {
    max-width: 75%;
  }
}

@media (min-width: 376px) {
  .hero-title {
    font-size: 40rpx;
  }
  .ai-card-name {
    font-size: 30rpx;
  }
  .ai-card-desc {
    font-size: 24rpx;
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
