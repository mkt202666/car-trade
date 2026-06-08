<template>
  <view>
    <u-navbar :title="convName || '在线客服'" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="createTicket">
        <u-icon name="list" size="36"></u-icon>
      </view>
    </u-navbar>
    <view class="page-content" ref="pageContent">
      <view class="msg-list" id="msgList">
        <view class="msg-item" v-for="(msg, index) in messages" :key="index" :class="msg.role">
          <image :src="msg.role === 'user' ? userAvatar : '/static/default-avatar.png'" mode="aspectFill" class="msg-avatar"></image>
          <view class="msg-bubble">
            <text class="msg-text">{{ msg.content }}</text>
            <text class="msg-time">{{ formatTime(msg.createTime) }}</text>
          </view>
        </view>
      </view>
      <view class="input-bar">
        <u-input v-model="inputText" placeholder="输入消息..." :border="false" :height="72" @confirm="sendMessage"></u-input>
        <view class="input-actions">
          <u-icon name="plus-circle" size="48" color="#666" @click="showActions"></u-icon>
          <u-button size="mini" type="primary" @click="sendMessage" :disabled="!inputText">发送</u-button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getChatMessages, createConversation, getConversations } from '@/api/chat'
import { formatTime } from '@/utils/format'

export default {
  data() {
    return {
      convName: '在线客服',
      conversationId: '',
      sellerId: '',
      messages: [],
      inputText: '',
      userAvatar: '/static/default-avatar.png'
    }
  },
  onLoad(options) {
    if (options.conversationId) {
      this.conversationId = options.conversationId
      this.convName = options.name || '在线客服'
      this.loadMessages()
    } else if (options.sellerId) {
      this.sellerId = options.sellerId
      this.createOrLoadConversation()
    }
  },
  methods: {
    formatTime,
    async createOrLoadConversation() {
      try {
        const res = await createConversation({ sellerId: this.sellerId })
        this.conversationId = res.data.id
        this.loadMessages()
      } catch (e) {}
    },
    async loadMessages() {
      if (!this.conversationId) return
      try {
        const res = await getChatMessages(this.conversationId, { page: 1, pageSize: 50 })
        this.messages = res.data.records || res.data.list || res.data || []
        this.scrollToBottom()
      } catch (e) {}
    },
    async sendMessage() {
      if (!this.inputText) return
      const text = this.inputText
      this.messages.push({ role: 'user', content: text, createTime: new Date() })
      this.inputText = ''
      this.scrollToBottom()
      try {
        const res = await uni.$u.http.post('/chat/conversations/' + this.conversationId + '/messages', { content: text })
        if (res && res.data) {
          const reply = typeof res.data === 'string' ? res.data : (res.data.content || res.data.reply || '感谢您的咨询')
          this.messages.push({ role: 'ai', content: reply, createTime: new Date() })
          this.scrollToBottom()
        }
      } catch (e) {
        this.messages.push({ role: 'ai', content: '客服暂时忙碌，请稍后再试', createTime: new Date() })
        this.scrollToBottom()
      }
    },
    scrollToBottom() {
      this.$nextTick(() => {
        uni.pageScrollTo({ scrollTop: 99999, duration: 200 })
      })
    },
    showActions() {
      uni.showActionSheet({
        itemList: ['图片', '车源', '位置'],
        success: (res) => {
          uni.$u.toast(['图片', '车源', '位置'][res.tapIndex] + '功能开发中')
        }
      })
    },
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
.navbar-right { padding-right: 20rpx; }
.page-content { min-height: 100vh; background: #f5f5f5; padding-bottom: 120rpx; }
.msg-list { padding: 30rpx; }
.msg-item { display: flex; margin-bottom: 30rpx; }
.msg-item.user { flex-direction: row-reverse; }
.msg-avatar { width: 64rpx; height: 64rpx; border-radius: 50%; flex-shrink: 0; }
.msg-bubble {
  max-width: 70%; margin: 0 16rpx; padding: 20rpx; border-radius: 16rpx; position: relative;
}
.msg-item.ai .msg-bubble { background: #fff; }
.msg-item.user .msg-bubble { background: #3c9cff; }
.msg-item.user .msg-text { color: #fff; }
.msg-text { font-size: 28rpx; line-height: 1.6; color: #333; }
.msg-time { font-size: 20rpx; color: #999; display: block; margin-top: 8rpx; }
.msg-item.user .msg-time { color: rgba(255,255,255,0.7); }
.input-bar {
  position: fixed; bottom: 0; left: 0; right: 0; background: #fff;
  display: flex; align-items: center; padding: 12rpx 20rpx;
  padding-bottom: calc(12rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #eee;
}
.input-bar u-input { flex: 1; border: 1rpx solid #eee; border-radius: 40rpx; padding: 0 20rpx; }
.input-actions { display: flex; align-items: center; gap: 12rpx; margin-left: 16rpx; }
</style>
