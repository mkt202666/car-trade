<template>
  <view>
    <u-navbar :title="convName || '在线客服'" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="createTicket">
        <u-icon name="list" size="36"></u-icon>
      </view>
    </u-navbar>

    <!-- 端到端加密提示 -->
    <view class="security-tip" v-if="showSecurityTip">
      <u-icon name="lock" size="24" color="#10B981"></u-icon>
      <text class="tip-text">车商专属客服通道已开通，数据进行端到端加密保护</text>
      <u-icon name="close" size="24" color="#999" @click="showSecurityTip = false"></u-icon>
    </view>

    <!-- 常见问题快捷入口 -->
    <view class="quick-questions" v-if="messages.length === 0 && !loading">
      <view class="quick-title">常见问题</view>
      <view class="quick-list">
        <view class="quick-item" v-for="(item, index) in quickQuestions" :key="index" @click="askQuestion(item)">
          <text class="quick-text">{{ item.question }}</text>
          <u-icon name="arrow-right" size="20" color="#999"></u-icon>
        </view>
      </view>
    </view>

    <!-- 贵宾热线 -->
    <view class="vip-hotline" v-if="messages.length === 0 && !loading">
      <view class="hotline-header">
        <u-icon name="phone" size="28" color="#F59E0B"></u-icon>
        <text class="hotline-title">贵宾热线</text>
      </view>
      <view class="hotline-content">
        <text class="hotline-number">400-123-4567</text>
        <text class="hotline-desc">工作时间：9:00-21:00</text>
      </view>
      <view class="hotline-btn" @click="callHotline">
        <text>立即拨打</text>
      </view>
    </view>

    <view class="page-content" ref="pageContent">
      <view class="msg-list" id="msgList">
        <view class="loading-tip" v-if="loading">
          <view class="custom-spinner"></view>
          <text>加载中...</text>
        </view>
        <view class="msg-item" v-for="(msg, index) in messages" :key="index" :class="msg.role">
          <image :src="msg.role === 'user' ? userAvatar : (msg.avatar || '/static/default-avatar.png')" mode="aspectFill" class="msg-avatar"></image>
          <view class="msg-bubble">
            <text class="msg-text">{{ msg.content }}</text>
            <text class="msg-time">{{ formatTime(msg.createTime) }}</text>
          </view>
        </view>
        <view class="empty-tip" v-if="!loading && messages.length === 0">
          <text>暂无消息记录，开始和客服对话吧</text>
        </view>
      </view>
      <view class="input-bar">
        <u-input v-model="inputText" placeholder="输入消息..." :border="false" :height="72" @confirm="sendMessage"></u-input>
        <view class="input-actions">
          <u-icon name="plus-circle" size="48" color="#666" @click="showActions"></u-icon>
          <u-button size="mini" type="primary" @click="sendMessage" :disabled="!inputText || sending">
            <text v-if="sending">发送中</text>
            <text v-else>发送</text>
          </u-button>
        </view>
      </view>
    </view>
    <!-- 连接状态提示 -->
    <u-toast ref="uToast"></u-toast>
  </view>
</template>

<script>
import { getChatMessages, createConversation, getConversations, sendChatMessage, markConversationRead } from '@/api/chat'
import { formatTime } from '@/utils/format'
import { requireAuth } from '@/utils/auth'
import { readToken } from '@/constants/storage'
import { createStompClient, disconnectStompClient } from '@/utils/stomp-client'

export default {
  data() {
    return {
      convName: '在线客服',
      conversationId: '',
      sellerId: '',
      messages: [],
      inputText: '',
      userAvatar: '/static/default-avatar.png',
      loading: false,
      sending: false,
      wsConnected: false,
      stompClient: null,
      stompSubscription: null,
      reconnectAttempts: 0,
      maxReconnectAttempts: 5,
      pollingTimer: null,
      showSecurityTip: true,
      quickQuestions: [
        { question: '如何退还保证金？', answer: '平台支持自助申请退还保证金。进入【我的】->【我的车行/保证金账户】，在无未决交易纠纷的前提下，点击"申请退款"即可，通常系统会在 1-3 个工作日内原路退还到您的支付账户。' },
        { question: '车源推广如何收费？', answer: '目前平台普通车源发布完全免费。如果您使用【AI 推广】或增值分发服务，系统会根据实际展示和点击扣除推广积分或会员点数，具体详情可在【会员特权】页面查阅。' },
        { question: '信用评级如何提升？', answer: '信用评级是系统根据您的「历史成交率」、「平均回复时效」、「零投诉记录」以及「保证金余额」等多维度综合评估的。保持优质的车况描述和及时的买家沟通是升级 S 级的最佳途径！' },
        { question: '争议如何处理？', answer: '当订单进入争议阶段时，您的这笔交易资金或定金将被平台临时托管。请在订单详情页内上传相关的车况检测报告或微信聊天记录，平台专属人工客服会在 24 小时内介入并客观仲裁。' }
      ]
    }
  },
  onLoad(options) {
    // 登录验证
    if (!requireAuth()) {
      return
    }
    // 获取用户头像
    const userInfo = uni.getStorageSync('userInfo')
    if (userInfo && userInfo.avatar) {
      this.userAvatar = userInfo.avatar
    }
    // 初始化聊天
    if (options.conversationId) {
      this.conversationId = options.conversationId
      this.convName = options.name || '在线客服'
      this.loadMessages()
      this.markAsRead()
    } else if (options.sellerId) {
      this.sellerId = options.sellerId
      this.createOrLoadConversation()
    } else {
      // 没有参数，加载对话列表，找第一个或创建新对话
      this.loadConversationsAndInit()
    }
  },
  onUnload() {
    this.closeWebSocket()
    this.stopPolling()
  },
  onHide() {
    this.closeWebSocket()
    this.stopPolling()
  },
  methods: {
    formatTime,
    // 加载对话列表并初始化
    async loadConversationsAndInit() {
      try {
        const res = await getConversations()
        const data = res.data
        const conversations = data.list || data.records || data || []
        if (conversations.length > 0) {
          const chat = conversations[0]
          this.conversationId = chat.id
          this.convName = chat.name || '在线客服'
          this.loadMessages()
          this.markAsRead()
        } else {
          // 没有对话，创建一个新对话
          await this.createDefaultConversation()
        }
      } catch (e) {
        // 没有对话，创建一个新对话
        await this.createDefaultConversation()
      }
    },
    // 创建默认对话
    async createDefaultConversation() {
      try {
        const res = await createConversation({})
        if (res.data && res.data.id) {
          this.conversationId = res.data.id
          this.convName = res.data.name || '在线客服'
        }
      } catch (e) {
        uni.$u.toast('创建对话失败')
      }
    },
    // 创建或加载对话
    async createOrLoadConversation() {
      try {
        uni.showLoading({ title: '加载中...' })
        const res = await createConversation({ sellerId: this.sellerId })
        if (res.data && res.data.id) {
          this.conversationId = res.data.id
          this.convName = res.data.name || '在线客服'
          this.loadMessages()
          this.markAsRead()
        }
      } catch (e) {
        uni.$u.toast('加载对话失败')
      } finally {
        uni.hideLoading()
      }
    },
    // 加载消息历史
    async loadMessages() {
      if (!this.conversationId) return
      this.loading = true
      try {
        const res = await getChatMessages(this.conversationId, { page: 1, pageSize: 50 })
        const data = res.data
        // 兼容多种返回格式
        let records = []
        if (Array.isArray(data)) {
          records = data
        } else if (data.records) {
          records = data.records
        } else if (data.list) {
          records = data.list
        }
        // 转换消息格式，确保有 role 字段
        this.messages = records.map(msg => ({
          id: msg.id,
          role: msg.role || (msg.senderType === 'USER' ? 'user' : 'ai'),
          content: msg.content || msg.message || '',
          createTime: msg.createTime || msg.createAt || new Date(),
          avatar: msg.avatar || ''
        }))
        this.scrollToBottom()
        // 启动轮询获取新消息
        this.startPolling()
        // 尝试连接 WebSocket
        this.connectWebSocket()
      } catch (e) {
        uni.$u.toast('加载消息失败')
      } finally {
        this.loading = false
      }
    },
    // 标记对话为已读
    async markAsRead() {
      if (!this.conversationId) return
      try {
        await markConversationRead(this.conversationId)
      } catch (e) {
        // 静默失败
      }
    },
    // 发送消息
    async sendMessage() {
      if (!this.inputText || this.sending) return
      if (!this.conversationId) {
        uni.$u.toast('请先创建对话')
        return
      }

      const text = this.inputText.trim()
      if (!text) return
      
      this.sending = true
      const tempId = Date.now()
      // 先添加到本地显示
      this.messages.push({ 
        id: tempId, 
        role: 'user', 
        content: text, 
        createTime: new Date(),
        avatar: this.userAvatar
      })
      this.inputText = ''
      this.scrollToBottom()
      
      try {
        const res = await sendChatMessage(this.conversationId, { content: text })
        // 移除临时消息，用服务器返回的消息替换
        const serverMsg = res.data
        if (serverMsg) {
          const msgIndex = this.messages.findIndex(m => m.id === tempId)
          if (msgIndex !== -1) {
            this.messages.splice(msgIndex, 1, {
              id: serverMsg.id || tempId,
              role: serverMsg.role || 'user',
              content: serverMsg.content || text,
              createTime: serverMsg.createTime || new Date(),
              avatar: this.userAvatar
            })
          }
          // 如果服务器返回了 AI 回复
          if (res.data.reply || (res.data.content && serverMsg.role === 'ai')) {
            // 检查是否已经添加了回复（某些后端会返回两条消息）
          }
        }
        this.scrollToBottom()
      } catch (e) {
        // 移除临时消息
        const msgIndex = this.messages.findIndex(m => m.id === tempId)
        if (msgIndex !== -1) {
          this.messages.splice(msgIndex, 1)
        }
        // 添加错误提示
        this.messages.push({ 
          role: 'ai', 
          content: '发送失败，请检查网络后重试', 
          createTime: new Date() 
        })
        this.scrollToBottom()
        uni.$u.toast('消息发送失败')
      } finally {
        this.sending = false
      }
    },
    // WebSocket 连接（STOMP / SockJS）
    connectWebSocket() {
      // 统一读取 token — 委托 constants/storage.js
      const token = readToken()
      if (!token || token === 'null' || token === 'undefined') return

      // ★ 安全：使用 STOMP / SockJS 客户端
      //   - token 通过 STOMP CONNECT 帧的 Authorization 头传递
      //   - URL 中绝不出现 token，避免 Nginx/网关/Referer 泄露
      //   - 后端 WebSocketAuthInterceptor 会从 CONNECT 帧读取 Authorization 头
      //   - 失败时降级为轮询（startPolling）
      const stompUrl = 'http://localhost:8080/ws'
      try {
        this.stompClient = createStompClient({
          url: stompUrl,
          onConnect: (client) => {
            console.log('STOMP WebSocket 已连接')
            this.wsConnected = true
            this.reconnectAttempts = 0
            // 订阅当前会话的私聊消息
            this.stompSubscription = client.subscribe('/user/queue/messages', (frame) => {
              try {
                const data = JSON.parse(frame.body)
                if (data && data.conversationId && String(data.conversationId) === String(this.conversationId)) {
                  const newMsg = {
                    id: data.id || Date.now(),
                    role: data.role || (data.senderType === 'USER' ? 'user' : 'ai'),
                    content: data.content || data.message || '',
                    createTime: data.createTime || new Date(),
                    avatar: data.avatar || ''
                  }
                  if (!this.messages.find(m => m.id === newMsg.id)) {
                    this.messages.push(newMsg)
                    this.scrollToBottom()
                  }
                }
              } catch (e) {
                console.log('解析 STOMP 消息失败')
              }
            })
          },
          onError: (err) => {
            console.log('STOMP WebSocket 连接失败:', err && err.message)
            this.wsConnected = false
            // 失败时使用轮询
            this.startPolling()
          },
          onClose: () => {
            console.log('STOMP WebSocket 已关闭')
            this.wsConnected = false
            // 自动重连（stompjs 内部已有 reconnectDelay 3000ms）
          }
        })

        if (!this.stompClient) {
          // createStompClient 内部已调用 onError；这里补一次轮询降级
          this.startPolling()
        }
      } catch (e) {
        console.log('STOMP WebSocket 连接异常:', e)
        this.startPolling()
      }
    },
    // 关闭 WebSocket
    closeWebSocket() {
      if (this.stompSubscription && typeof this.stompSubscription.unsubscribe === 'function') {
        try { this.stompSubscription.unsubscribe() } catch (_) { /* ignore */ }
        this.stompSubscription = null
      }
      if (this.stompClient) {
        disconnectStompClient(this.stompClient)
        this.stompClient = null
      }
    },
    // 启动轮询获取新消息
    startPolling() {
      if (this.pollingTimer) return
      this.pollingTimer = setInterval(async () => {
        if (!this.conversationId) return
        try {
          const res = await getChatMessages(this.conversationId, { page: 1, pageSize: 20 })
          const data = res.data
          const records = Array.isArray(data) ? data : (data.records || data.list || [])
          
          // 检查是否有新消息
          if (records.length > this.messages.length) {
            const newMessages = records.slice(this.messages.length)
            newMessages.forEach(msg => {
              if (!this.messages.find(m => m.id === msg.id)) {
                this.messages.push({
                  id: msg.id,
                  role: msg.role || (msg.senderType === 'USER' ? 'user' : 'ai'),
                  content: msg.content || msg.message || '',
                  createTime: msg.createTime || new Date(),
                  avatar: msg.avatar || ''
                })
              }
            })
            this.scrollToBottom()
          }
        } catch (e) {
          // 轮询失败静默处理
        }
      }, 5000) // 5秒轮询一次
    },
    // 停止轮询
    stopPolling() {
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer)
        this.pollingTimer = null
      }
    },
    // 滚动到底部
    scrollToBottom() {
      this.$nextTick(() => {
        uni.pageScrollTo({ scrollTop: 99999, duration: 200 })
      })
    },
    // 显示操作菜单
    showActions() {
      uni.showActionSheet({
        itemList: ['图片', '车源', '位置'],
        success: (res) => {
          uni.$u.toast(['图片', '车源', '位置'][res.tapIndex] + '功能开发中')
        }
      })
    },
    // 快速提问
    askQuestion(item) {
      this.inputText = item.question
      this.sendMessage()
    },
    // 拨打贵宾热线
    callHotline() {
      uni.makePhoneCall({
        phoneNumber: '400-123-4567',
        fail: (err) => {
          console.error('拨打电话失败', err)
          uni.$u.toast('拨打电话失败')
        }
      })
    },
    // 创建工单
    createTicket() {
      uni.showModal({
        title: '提交工单',
        content: '是否提交客服工单？我们会尽快处理您的问题。',
        success: (res) => {
          if (res.confirm) {
            uni.$u.toast('工单已提交')
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
/* 设计系统变量 */
$primary: #0F172A;
$cta: #0369A1;
$bg: #F8FAFC;
$text: #020617;
$text-secondary: #64748B;
$border: #E2E8F0;
$radius: 16rpx;
$radius-lg: 24rpx;
$shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
$transition: all 0.2s ease;

.navbar-right {
  padding-right: 20rpx;
  cursor: pointer;
  transition: $transition;
  border-radius: $radius;
  padding: 8rpx;
  &:hover {
    background: rgba(3, 105, 161, 0.08);
  }
  &:active {
    background: rgba(3, 105, 161, 0.16);
    transform: scale(0.95);
  }
}
.page-content {
  min-height: 100vh;
  background: $bg;
  padding-bottom: 140rpx;
}
.msg-list {
  padding: 30rpx;
}
.msg-item {
  display: flex;
  margin-bottom: 30rpx;
  transition: $transition;
}
.msg-item.user {
  flex-direction: row-reverse;
}
.msg-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}
.msg-bubble {
  max-width: 70%;
  margin: 0 16rpx;
  padding: 20rpx 24rpx;
  border-radius: $radius-lg;
  position: relative;
  transition: $transition;
}
.msg-item.ai .msg-bubble {
  background: #fff;
  box-shadow: $shadow;
}
.msg-item.user .msg-bubble {
  background: $cta;
  box-shadow: 0 4rpx 12rpx rgba(3, 105, 161, 0.3);
}
.msg-item.user .msg-text {
  color: #fff;
}
.msg-text {
  font-size: 28rpx;
  line-height: 1.6;
  color: $text;
}
.msg-time {
  font-size: 20rpx;
  color: $text-secondary;
  display: block;
  margin-top: 8rpx;
}
.msg-item.user .msg-time {
  color: rgba(255, 255, 255, 0.7);
  text-align: right;
}
.loading-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx;
  color: $text-secondary;
  font-size: 24rpx;
  gap: 16rpx;
}
.custom-spinner {
  width: 48rpx;
  height: 48rpx;
  border: 4rpx solid #e5e7eb;
  border-top-color: #0369A1;
  border-radius: 50%;
  animation: cs-spin 0.8s linear infinite;
}
@keyframes cs-spin {
  to { transform: rotate(360deg); }
}
.empty-tip {
  text-align: center;
  padding: 100rpx 30rpx;
  color: $text-secondary;
  font-size: 26rpx;
}
.input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid $border;
  box-shadow: 0 -4rpx 16rpx rgba(15, 23, 42, 0.06);
}
.input-bar u-input {
  flex: 1;
  border: 1rpx solid $border;
  border-radius: 40rpx;
  padding: 0 24rpx;
  background: $bg;
  transition: $transition;
  &:focus {
    border-color: $cta;
    background: #fff;
  }
}
.input-actions {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-left: 16rpx;
}
.input-actions .u-icon {
  cursor: pointer;
  transition: $transition;
  &:hover {
    transform: scale(1.1);
    opacity: 0.8;
  }
  &:active {
    transform: scale(0.95);
  }
}
.input-actions .u-button {
  background: $cta;
  border-color: $cta;
  transition: $transition;
  &:hover {
    background: #0284c7;
    border-color: #0284c7;
  }
  &:active {
    background: #0369A1;
    border-color: #0369A1;
    transform: scale(0.98);
  }
  &[disabled] {
    background: #94a3b8;
    border-color: #94a3b8;
  }
}

/* 安全提示 */
.security-tip {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 20rpx;
  background: #ECFDF5;
  border-bottom: 1rpx solid #D1FAE5;
}

.tip-text {
  flex: 1;
  font-size: 24rpx;
  color: #065F46;
}

/* 快速问题 */
.quick-questions {
  background: #fff;
  padding: 30rpx 20rpx;
  margin-bottom: 16rpx;
}

.quick-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}

.quick-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.quick-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background: #F8FAFC;
  border-radius: 12rpx;
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    background: #E2E8F0;
  }
}

.quick-text {
  font-size: 28rpx;
  color: #333;
  flex: 1;
}

/* 贵宾热线 */
.vip-hotline {
  background: #fff;
  padding: 30rpx 20rpx;
  margin-bottom: 16rpx;
}

.hotline-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.hotline-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.hotline-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.hotline-number {
  font-size: 36rpx;
  font-weight: 700;
  color: #F59E0B;
}

.hotline-desc {
  font-size: 22rpx;
  color: #999;
}

.hotline-btn {
  display: flex;
  justify-content: center;
  padding: 16rpx;
  background: linear-gradient(135deg, #F59E0B, #D97706);
  border-radius: 12rpx;
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.98);
  }

  text {
    font-size: 28rpx;
    color: #fff;
    font-weight: 600;
  }
}
</style>
