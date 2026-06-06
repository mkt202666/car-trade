<template>
  <view>
    <u-navbar title="消息中心" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="markAll">
        <text class="mark-all">全部已读</text>
      </view>
    </u-navbar>
    <view class="page-content">
      <view class="conversation-list">
        <view class="conversation-item" v-for="item in conversations" :key="item.id" @click="toChat(item)">
          <image :src="item.avatar || '/static/default-avatar.png'" mode="aspectFill" class="conv-avatar"></image>
          <view class="conv-info">
            <view class="conv-top">
              <text class="conv-name">{{ item.name }}</text>
              <text class="conv-time">{{ formatTime(item.lastMessageTime) }}</text>
            </view>
            <view class="conv-bottom">
              <text class="conv-preview u-line-1">{{ item.lastMessage || '暂无消息' }}</text>
              <view class="unread-badge" v-if="item.unreadCount > 0">{{ item.unreadCount > 99 ? '99+' : item.unreadCount }}</view>
            </view>
          </view>
        </view>
      </view>
      <view class="system-msgs">
        <view class="section-title">系统消息</view>
        <view class="msg-item" v-for="msg in systemMessages" :key="msg.id" @click="toDetail(msg)">
          <u-icon name="notification" size="40" color="#3c9cff"></u-icon>
          <view class="msg-info">
            <text class="msg-title">{{ msg.title }}</text>
            <text class="msg-preview u-line-1">{{ msg.content }}</text>
          </view>
          <text class="msg-time">{{ formatTime(msg.createTime) }}</text>
        </view>
      </view>
      <u-empty v-if="conversations.length === 0 && systemMessages.length === 0" mode="message" text="暂无消息"></u-empty>
    </view>
  </view>
</template>

<script>
import { getConversations } from '@/api/chat'
import { getMessageList, markAllRead } from '@/api/message'
import { formatTime } from '@/utils/format'

export default {
  data() {
    return {
      conversations: [],
      systemMessages: []
    }
  },
  onShow() {
    this.loadData()
  },
  methods: {
    formatTime,
    async loadData() {
      try {
        const [convRes, msgRes] = await Promise.all([
          getConversations(),
          getMessageList({ page: 1, pageSize: 10 })
        ])
        this.conversations = convRes.data || []
        const list = msgRes.data.records || msgRes.data.list || msgRes.data || []
        this.systemMessages = list.filter(m => m.type === 'SYSTEM')
      } catch (e) {}
    },
    toChat(item) {
      uni.navigateTo({ url: '/pages/customer-service/index?conversationId=' + item.id + '&name=' + item.name })
    },
    toDetail(msg) {
      if (msg.relatedId) {
        uni.navigateTo({ url: '/pages/order-detail/index?id=' + msg.relatedId })
      }
    },
    async markAll() {
      try {
        await markAllRead()
        uni.$u.toast('已全部标记已读')
      } catch (e) {}
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar-right { padding-right: 20rpx; }
.mark-all { font-size: 26rpx; color: #3c9cff; }
.page-content { min-height: 100vh; background: #f5f5f5; }
.conversation-list { background: #fff; margin-bottom: 16rpx; }
.conversation-item { display: flex; padding: 24rpx 30rpx; border-bottom: 1rpx solid #f5f5f5; }
.conv-avatar { width: 80rpx; height: 80rpx; border-radius: 50%; margin-right: 20rpx; }
.conv-info { flex: 1; }
.conv-top { display: flex; justify-content: space-between; align-items: center; }
.conv-name { font-size: 28rpx; font-weight: 600; color: #333; }
.conv-time { font-size: 22rpx; color: #999; }
.conv-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 10rpx; }
.conv-preview { font-size: 24rpx; color: #999; flex: 1; margin-right: 12rpx; }
.unread-badge {
  background: #f56c6c; color: #fff; font-size: 20rpx; padding: 2rpx 12rpx;
  border-radius: 20rpx; min-width: 32rpx; text-align: center;
}
.system-msgs { background: #fff; }
.section-title { font-size: 28rpx; font-weight: 600; color: #333; padding: 24rpx 30rpx 0; }
.msg-item { display: flex; align-items: center; padding: 20rpx 30rpx; }
.msg-info { flex: 1; margin: 0 16rpx; }
.msg-title { font-size: 26rpx; color: #333; display: block; }
.msg-preview { font-size: 22rpx; color: #999; display: block; }
.msg-time { font-size: 20rpx; color: #ccc; }
</style>
