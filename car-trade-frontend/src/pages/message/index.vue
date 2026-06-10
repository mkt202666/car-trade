<template>
  <view class="page">
    <u-navbar title="消息中心" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="readAll">
        <text class="read-all-btn">全部已读</text>
      </view>
    </u-navbar>

    <!-- Tab 切换 -->
    <u-tabs :list="tabs" :current="currentTab" @change="switchTab" active-color="#0369A1" :bold="false" bg-color="#fff" bar-width="40"></u-tabs>

    <!-- 消息 Tab -->
    <view v-if="currentTab === 0" class="tab-content">
      <!-- 系统消息分组 -->
      <view class="message-group" v-if="systemMessages.length > 0">
        <view class="group-header">
          <text class="group-date">{{ currentDate }}</text>
        </view>
        <view class="msg-list">
          <view class="msg-item" v-for="msg in systemMessages" :key="msg.id" @click="handleSystemMsg(msg)">
            <view class="msg-icon" :class="'icon-' + msg.subtype">
              <u-icon :name="systemIcon(msg.subtype)" size="36" color="#fff"></u-icon>
            </view>
            <view class="msg-info">
              <text class="msg-title">{{ msg.title }}</text>
              <text class="msg-content u-line-1">{{ msg.content }}</text>
            </view>
            <text class="msg-time">{{ msg.createTime }}</text>
          </view>
        </view>
      </view>

      <!-- 聊天对话 -->
      <view class="chat-group">
        <view class="chat-item" v-for="chat in chatConversations" :key="chat.id" @click="toChat(chat)">
          <image :src="chat.avatar || '/static/default-avatar.png'" mode="aspectFill" class="chat-avatar"></image>
          <view class="chat-info">
            <view class="chat-top">
              <text class="chat-name">{{ chat.name }}</text>
              <text class="chat-time">{{ chat.lastMessageTime }}</text>
            </view>
            <view class="chat-bottom">
              <text class="chat-msg u-line-1">{{ chat.lastMessage }}</text>
              <view class="chat-badge" v-if="chat.unreadCount > 0">
                <text>{{ chat.unreadCount > 99 ? '99+' : chat.unreadCount }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 订阅 Tab -->
    <view v-if="currentTab === 1" class="tab-content">
      <view class="subscribe-list">
        <view class="subscribe-item" v-for="item in subscribeList" :key="item.id">
          <view class="sub-info">
            <text class="sub-title">{{ item.title }}</text>
            <text class="sub-desc">{{ item.desc }}</text>
          </view>
          <u-switch :value="item.enabled" @change="toggleSubscribe(item)" active-color="#3c9cff"></u-switch>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getMessageList, markAllRead, markRead, getUnreadCount } from '@/api/message'
import { getConversations } from '@/api/chat'
import { requireAuth } from '@/utils/auth'

export default {
  data() {
    return {
      currentTab: 0,
      tabs: [{ name: '消息' }, { name: '订阅' }],
      systemMessages: [],
      chatConversations: [],
      unreadCount: 0,
      subscribeList: [
        { id: 1, title: '系统通知', desc: '平台活动、功能更新等', enabled: true },
        { id: 2, title: '自动推广', desc: '车源自动推广提醒', enabled: true },
        { id: 3, title: '订单通知', desc: '订单状态变更、交易提醒', enabled: true },
        { id: 4, title: '合同通知', desc: '合同签署、到期提醒', enabled: true },
        { id: 5, title: '保证金通知', desc: '保证金变动提醒', enabled: true },
        { id: 6, title: '车行通知', desc: '员工申请、权限变更', enabled: true }
      ],
      currentDate: '今天',
      loading: false
    }
  },
  onLoad() {
    // 登录验证
    if (!requireAuth()) {
      return
    }
  },
  onShow() {
    this.fetchUnreadCount()
    this.fetchMessages()
    this.fetchConversations()
  },
  onPullDownRefresh() {
    Promise.all([
      this.fetchUnreadCount(),
      this.fetchMessages(),
      this.fetchConversations()
    ]).finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    async fetchUnreadCount() {
      try {
        const res = await getUnreadCount()
        if (res.data !== undefined) {
          this.unreadCount = res.data
        }
      } catch (e) {
        // 静默失败
      }
    },
    async fetchMessages() {
      this.loading = true
      try {
        const res = await getMessageList({ type: 'SYSTEM' })
        const data = res.data
        // 兼容多种返回格式
        let records = []
        if (Array.isArray(data)) {
          records = data
        } else if (data.list) {
          records = data.list
        } else if (data.records) {
          records = data.records
        }
        this.systemMessages = records.map(msg => ({
          ...msg,
          subtype: msg.subtype || msg.relatedType || 'system',
          isRead: msg.isRead || msg.read
        }))
      } catch (e) {
        uni.$u.toast('加载消息失败')
      } finally {
        this.loading = false
      }
    },
    async fetchConversations() {
      try {
        const res = await getConversations()
        const data = res.data
        // 兼容多种返回格式
        let records = []
        if (Array.isArray(data)) {
          records = data
        } else if (data.list) {
          records = data.list
        } else if (data.records) {
          records = data.records
        }
        this.chatConversations = records.map(chat => ({
          id: chat.id,
          name: chat.name || chat.conversationName || '在线客服',
          lastMessage: chat.lastMessage || chat.lastMessageContent || '',
          lastMessageTime: chat.lastMessageTime || chat.updateTime || '',
          unreadCount: chat.unreadCount || 0,
          avatar: chat.avatar || chat.userAvatar || ''
        }))
      } catch (e) {
        // 静默失败
      }
    },
    switchTab(e) {
      this.currentTab = e.index
    },
    async readAll() {
      try {
        await markAllRead()
        this.systemMessages = this.systemMessages.map(m => ({ ...m, isRead: true }))
        this.unreadCount = 0
        uni.$u.toast('全部已读')
      } catch (e) {
        uni.$u.toast('操作失败')
      }
    },
    async handleSystemMsg(msg) {
      // 标记已读
      if (!msg.isRead) {
        try {
          await markRead(msg.id)
          msg.isRead = true
          this.unreadCount = Math.max(0, this.unreadCount - 1)
        } catch (e) {
          // 静默失败
        }
      }
      // 根据消息类型跳转
      if (msg.relatedId) {
        switch (msg.relatedType) {
          case 'order':
            uni.navigateTo({ url: '/pages/order-detail/index?id=' + msg.relatedId })
            break
          case 'car':
            uni.navigateTo({ url: '/pages/car-detail/index?id=' + msg.relatedId })
            break
          case 'contract':
            uni.navigateTo({ url: '/pages/contract-detail/index?id=' + msg.relatedId })
            break
          default:
            uni.$u.toast('查看详情: ' + msg.title)
        }
      } else {
        uni.$u.toast('查看详情: ' + msg.title)
      }
    },
    systemIcon(subtype) {
      const map = {
        auto_promotion: 'share',
        order_update: 'order',
        contract: 'file-text',
        deposit_warning: 'rmb-circle',
        shop_apply: 'account'
      }
      return map[subtype] || 'bell'
    },
    toChat(chat) {
      // 传递 conversationId 给客服页面
      uni.navigateTo({ 
        url: '/pages/customer-service/index?conversationId=' + chat.id + '&name=' + encodeURIComponent(chat.name)
      })
    },
    toggleSubscribe(item) {
      item.enabled = !item.enabled
      uni.$u.toast(item.enabled ? '已开启' : '已关闭')
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

.page {
  min-height: 100vh;
  background: $bg;
}
.navbar-right {
  padding-right: 20rpx;
}
.read-all-btn {
  font-size: 26rpx;
  color: $cta;
  cursor: pointer;
  transition: $transition;
  padding: 8rpx 16rpx;
  border-radius: $radius;
  &:hover {
    background: rgba(3, 105, 161, 0.08);
  }
  &:active {
    background: rgba(3, 105, 161, 0.16);
    transform: scale(0.98);
  }
}

.tab-content {
  padding-bottom: 20rpx;
}

/* 消息分组 */
.message-group {
  background: #fff;
  margin: 24rpx;
  border-radius: $radius-lg;
  box-shadow: $shadow;
  overflow: hidden;
}
.group-header {
  padding: 24rpx 30rpx 0;
}
.group-date {
  font-size: 24rpx;
  color: $text-secondary;
}
.msg-list {
  padding: 0 30rpx;
}
.msg-item {
  display: flex;
  align-items: center;
  padding: 28rpx 0;
  border-bottom: 1rpx solid $border;
  cursor: pointer;
  transition: $transition;
  border-radius: $radius;
  margin: 0 -16rpx;
  padding-left: 16rpx;
  padding-right: 16rpx;
  &:hover {
    background: rgba(3, 105, 161, 0.04);
  }
  &:active {
    background: rgba(3, 105, 161, 0.08);
    transform: scale(0.99);
  }
  &:last-child {
    border-bottom: none;
  }
}
.msg-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}
.icon-auto_promotion { background: linear-gradient(135deg, $cta, #0284c7); }
.icon-order_update { background: linear-gradient(135deg, #f59e0b, #d97706); }
.icon-contract { background: linear-gradient(135deg, #22c55e, #16a34a); }
.icon-deposit_warning { background: linear-gradient(135deg, #ef4444, #dc2626); }
.icon-shop_apply { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }
.msg-info {
  flex: 1;
  overflow: hidden;
}
.msg-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text;
  display: block;
}
.msg-content {
  font-size: 24rpx;
  color: $text-secondary;
  display: block;
  margin-top: 6rpx;
}
.msg-time {
  font-size: 22rpx;
  color: $text-secondary;
  margin-left: 16rpx;
  flex-shrink: 0;
}

/* 聊天对话 */
.chat-group {
  background: #fff;
  margin: 0 24rpx 24rpx;
  border-radius: $radius-lg;
  box-shadow: $shadow;
  overflow: hidden;
}
.chat-item {
  display: flex;
  align-items: center;
  padding: 28rpx 30rpx;
  border-bottom: 1rpx solid $border;
  cursor: pointer;
  transition: $transition;
  &:hover {
    background: rgba(3, 105, 161, 0.04);
  }
  &:active {
    background: rgba(3, 105, 161, 0.08);
    transform: scale(0.99);
  }
  &:last-child {
    border-bottom: none;
  }
}
.chat-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}
.chat-info {
  flex: 1;
  overflow: hidden;
}
.chat-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chat-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text;
}
.chat-time {
  font-size: 22rpx;
  color: $text-secondary;
}
.chat-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8rpx;
}
.chat-msg {
  font-size: 24rpx;
  color: $text-secondary;
  flex: 1;
}
.chat-badge {
  min-width: 36rpx;
  height: 36rpx;
  background: $cta;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 10rpx;
  margin-left: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(3, 105, 161, 0.3);
  text {
    font-size: 20rpx;
    color: #fff;
    font-weight: 500;
  }
}

/* 订阅 */
.subscribe-list {
  background: #fff;
  margin: 24rpx;
  border-radius: $radius-lg;
  box-shadow: $shadow;
  overflow: hidden;
  padding: 0 30rpx;
}
.subscribe-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 0;
  border-bottom: 1rpx solid $border;
  transition: $transition;
  &:last-child {
    border-bottom: none;
  }
}
.sub-info {
  flex: 1;
}
.sub-title {
  font-size: 28rpx;
  color: $text;
  display: block;
  font-weight: 500;
}
.sub-desc {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
  margin-top: 4rpx;
}
</style>