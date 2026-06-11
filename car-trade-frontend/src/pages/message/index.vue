<template>
  <view class="page">
    <u-navbar title="消息中心" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="readAll">
        <text class="read-all-btn">全部已读</text>
      </view>
    </u-navbar>

    <!-- Tab 切换 -->
    <view class="tabs-wrap">
      <view
        class="tab-item"
        :class="{ active: currentTab === 0 }"
        @click="switchTab(0)"
      >
        <text class="tab-text">消息</text>
        <view class="tab-indicator" v-if="currentTab === 0"></view>
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 1 }"
        @click="switchTab(1)"
      >
        <text class="tab-text">订阅</text>
        <view class="tab-indicator" v-if="currentTab === 1"></view>
      </view>
    </view>

    <!-- 消息 Tab -->
    <view v-if="currentTab === 0" class="tab-content">
      <!-- 系统消息分组 -->
      <view class="message-group" v-if="systemMessages.length > 0">
        <view class="group-header">
          <view class="group-icon">
            <text>📢</text>
          </view>
          <text class="group-title">系统消息</text>
          <text class="group-date">{{ currentDate }}</text>
        </view>
        <view class="msg-list">
          <view class="msg-item" v-for="msg in systemMessages" :key="msg.id" @click="handleSystemMsg(msg)">
            <view class="msg-icon" :class="'icon-' + msg.subtype">
              <text class="msg-icon-text">{{ getSubtypeIcon(msg.subtype) }}</text>
            </view>
            <view class="msg-info">
              <text class="msg-title">{{ msg.title }}</text>
              <text class="msg-content">{{ msg.content }}</text>
            </view>
            <view class="msg-meta">
              <text class="msg-time">{{ msg.createTime }}</text>
              <view class="msg-unread" v-if="!msg.isRead"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- 聊天对话 -->
      <view class="chat-group">
        <view class="group-header">
          <view class="group-icon chat">
            <text>💬</text>
          </view>
          <text class="group-title">聊天消息</text>
        </view>
        <view class="chat-item" v-for="chat in chatConversations" :key="chat.id" @click="toChat(chat)">
          <view class="chat-avatar-wrap">
            <image :src="chat.avatar || '/static/default-car.png'" mode="aspectFill" class="chat-avatar"></image>
            <view class="chat-online" v-if="chat.online"></view>
          </view>
          <view class="chat-info">
            <view class="chat-top">
              <text class="chat-name">{{ chat.name }}</text>
              <text class="chat-time">{{ chat.lastMessageTime }}</text>
            </view>
            <view class="chat-bottom">
              <text class="chat-msg">{{ chat.lastMessage }}</text>
              <view class="chat-badge" v-if="chat.unreadCount > 0">
                <text>{{ chat.unreadCount > 99 ? '99+' : chat.unreadCount }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="systemMessages.length === 0 && chatConversations.length === 0">
        <view class="empty-icon-wrap">
          <text class="empty-icon">📭</text>
        </view>
        <text class="empty-title">暂无消息</text>
        <text class="empty-desc">有新消息时会在这里显示</text>
      </view>
    </view>

    <!-- 订阅 Tab -->
    <view v-if="currentTab === 1" class="tab-content">
      <view class="subscribe-header">
        <text class="subscribe-header-title">订阅管理</text>
        <text class="subscribe-header-desc">选择您想接收的通知类型</text>
      </view>

      <view class="subscribe-list">
        <view class="subscribe-item" v-for="item in subscribeList" :key="item.id">
          <view class="sub-icon" :class="'sub-' + item.id">
            <text>{{ item.icon }}</text>
          </view>
          <view class="sub-info">
            <text class="sub-title">{{ item.title }}</text>
            <text class="sub-desc">{{ item.desc }}</text>
          </view>
          <view class="sub-switch-wrap" @click="toggleSubscribe(item)">
            <view class="sub-switch" :class="{ active: item.enabled }">
              <view class="sub-switch-knob"></view>
            </view>
          </view>
        </view>
      </view>

      <view class="subscribe-footer">
        <text class="footer-text">💡 开启订阅后，您将及时收到重要通知</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getMessageList, markAllRead, markRead, getUnreadCount, getNotificationSettings, updateNotificationSettings } from '@/api/message'
import { getConversations } from '@/api/chat'
import { requireAuth } from '@/utils/auth'

export default {
  name: 'MessageCenter',
  data() {
    return {
      currentTab: 0,
      systemMessages: [],
      chatConversations: [],
      unreadCount: 0,
      subscribeList: [
        { id: 1, key: 'system', title: '系统通知', desc: '平台活动、功能更新等', icon: '🔔', enabled: true },
        { id: 2, key: 'auto_promotion', title: '自动推广', desc: '车源自动推广提醒', icon: '📣', enabled: true },
        { id: 3, key: 'order', title: '订单通知', desc: '订单状态变更、交易提醒', icon: '📋', enabled: true },
        { id: 4, key: 'contract', title: '合同通知', desc: '合同签署、到期提醒', icon: '📄', enabled: true },
        { id: 5, key: 'deposit', title: '保证金通知', desc: '保证金变动提醒', icon: '💰', enabled: true },
        { id: 6, key: 'shop', title: '车行通知', desc: '员工申请、权限变更', icon: '🏢', enabled: true }
      ],
      currentDate: '今天',
      loading: false
    }
  },
  onLoad() {
    if (!requireAuth()) return
  },
  onShow() {
    if (!requireAuth()) return
    this.fetchUnreadCount()
    this.fetchMessages()
    this.fetchConversations()
    this.fetchNotificationSettings()
  },
  onPullDownRefresh() {
    if (!requireAuth()) {
      uni.stopPullDownRefresh()
      return
    }
    Promise.all([
      this.fetchUnreadCount(),
      this.fetchMessages(),
      this.fetchConversations()
    ]).finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    getSubtypeIcon(subtype) {
      const map = {
        auto_promotion: '📣',
        order_update: '📋',
        contract: '📄',
        deposit_warning: '💰',
        shop_apply: '🏢'
      }
      return map[subtype] || '🔔'
    },
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
          isRead: msg.isRead !== undefined ? msg.isRead : (msg.read !== undefined ? msg.read : true)
        }))
      } catch (e) {
        console.error('加载消息失败', e)
      } finally {
        this.loading = false
      }
    },
    async fetchConversations() {
      try {
        const res = await getConversations()
        const data = res.data
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
          lastMessage: chat.lastMessage || chat.lastMessageContent || '暂无消息',
          lastMessageTime: chat.lastMessageTime || chat.updateTime || '',
          unreadCount: chat.unreadCount || 0,
          avatar: chat.avatar || chat.userAvatar || '',
          online: chat.online || false
        }))
      } catch (e) {
        // 静默失败
      }
    },
    switchTab(index) {
      this.currentTab = index
    },
    async fetchNotificationSettings() {
      try {
        const res = await getNotificationSettings()
        if (res.data) {
          const settings = typeof res.data === 'string' ? JSON.parse(res.data) : res.data
          this.subscribeList = this.subscribeList.map(item => ({
            ...item,
            enabled: settings[item.key] !== false
          }))
        }
      } catch (e) {
        // 静默失败，使用默认值
      }
    },
    async readAll() {
      if (!requireAuth()) return
      try {
        await markAllRead()
        this.systemMessages = this.systemMessages.map(m => ({ ...m, isRead: true }))
        this.unreadCount = 0
        uni.$u.toast('已全部标记为已读')
      } catch (e) {
        uni.$u.toast('操作失败，请重试')
      }
    },
    async handleSystemMsg(msg) {
      if (!requireAuth()) return
      if (!msg.isRead) {
        try {
          await markRead(msg.id)
          msg.isRead = true
          this.unreadCount = Math.max(0, this.unreadCount - 1)
        } catch (e) {
          // 静默失败
        }
      }
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
    toChat(chat) {
      uni.navigateTo({
        url: '/pages/customer-service/index?conversationId=' + chat.id + '&name=' + encodeURIComponent(chat.name)
      })
    },
    async toggleSubscribe(item) {
      item.enabled = !item.enabled
      const settings = {}
      this.subscribeList.forEach(s => {
        settings[s.key] = s.enabled
      })
      try {
        await updateNotificationSettings(JSON.stringify(settings))
        uni.$u.toast(item.enabled ? '已开启 ' + item.title : '已关闭 ' + item.title)
      } catch (e) {
        // 回滚状态
        item.enabled = !item.enabled
        uni.$u.toast('保存失败，请重试')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================================
   5D好车 - 消息中心 - 高级UI设计
   颜色系统: 深蓝主色 #0369A1 | 灰色层次 | 状态色标识
   ========================================================= */

/* ============ 主容器 ============ */
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8FAFC 0%, #EEF2F7 100%);
  padding-bottom: 200rpx;
}

/* ============ 导航栏右侧 ============ */
.navbar-right {
  padding-right: 24rpx;
}

.read-all-btn {
  font-size: 26rpx;
  color: #0369A1;
  font-weight: 600;
  padding: 10rpx 20rpx;
  border-radius: 24rpx;
  background: rgba(3, 105, 161, 0.08);
  transition: all 200ms ease;

  &:active {
    background: rgba(3, 105, 161, 0.15);
    transform: scale(0.96);
  }
}

/* ============ Tab 切换 ============ */
.tabs-wrap {
  display: flex;
  background: #ffffff;
  padding: 0 24rpx;
  border-bottom: 1rpx solid rgba(226, 232, 240, 0.6);
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 28rpx 0 20rpx;
  position: relative;
  cursor: pointer;
  transition: all 250ms ease;
}

.tab-text {
  font-size: 30rpx;
  color: #64748B;
  font-weight: 500;
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-item.active {
  .tab-text {
    color: #0F172A;
    font-weight: 700;
    font-size: 32rpx;
  }
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 56rpx;
  height: 6rpx;
  background: linear-gradient(90deg, #0369A1 0%, #0284C7 100%);
  border-radius: 4rpx;
  box-shadow: 0 2rpx 8rpx rgba(3, 105, 161, 0.3);
  animation: slideIn 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes slideIn {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 56rpx;
    opacity: 1;
  }
}

/* ============ Tab 内容 ============ */
.tab-content {
  padding: 24rpx;
  animation: fadeInUp 400ms cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ============ 消息分组 ============ */
.message-group {
  background: #ffffff;
  border-radius: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(226, 232, 240, 0.6);
  overflow: hidden;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 28rpx;
  border-bottom: 1rpx solid #F1F5F9;
}

.group-icon {
  width: 48rpx;
  height: 48rpx;
  border-radius: 14rpx;
  background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%);
  display: flex;
  align-items: center;
  justify-content: center;

  text {
    font-size: 24rpx;
  }

  &.chat {
    background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  }
}

.group-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #0F172A;
  flex: 1;
}

.group-date {
  font-size: 22rpx;
  color: #94A3B8;
}

/* ============ 消息列表 ============ */
.msg-list {
  padding: 8rpx 0;
}

.msg-item {
  display: flex;
  align-items: center;
  padding: 24rpx 28rpx;
  cursor: pointer;
  transition: all 200ms ease;

  &:active {
    background: #F8FAFC;
  }
}

.msg-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);

  .msg-icon-text {
    font-size: 32rpx;
  }

  &.icon-auto_promotion {
    background: linear-gradient(135deg, #0369A1 0%, #0EA5E9 100%);
  }
  &.icon-order_update {
    background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%);
  }
  &.icon-contract {
    background: linear-gradient(135deg, #10B981 0%, #34D399 100%);
  }
  &.icon-deposit_warning {
    background: linear-gradient(135deg, #EF4444 0%, #F87171 100%);
  }
  &.icon-shop_apply {
    background: linear-gradient(135deg, #8B5CF6 0%, #A78BFA 100%);
  }
  &.icon-system {
    background: linear-gradient(135deg, #64748B 0%, #94A3B8 100%);
  }
}

.msg-info {
  flex: 1;
  min-width: 0;
}

.msg-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #0F172A;
  display: block;
  margin-bottom: 6rpx;
}

.msg-content {
  font-size: 24rpx;
  color: #64748B;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10rpx;
  margin-left: 16rpx;
  flex-shrink: 0;
}

.msg-time {
  font-size: 22rpx;
  color: #94A3B8;
}

.msg-unread {
  width: 16rpx;
  height: 16rpx;
  background: #EF4444;
  border-radius: 50%;
  box-shadow: 0 2rpx 8rpx rgba(239, 68, 68, 0.4);
}

/* ============ 聊天对话列表 ============ */
.chat-group {
  background: #ffffff;
  border-radius: 28rpx;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(226, 232, 240, 0.6);
  overflow: hidden;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 24rpx 28rpx;
  cursor: pointer;
  transition: all 200ms ease;
  border-bottom: 1rpx solid #F1F5F9;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #F8FAFC;
  }
}

.chat-avatar-wrap {
  position: relative;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.chat-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #E2E8F0 0%, #CBD5E1 100%);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.chat-online {
  position: absolute;
  right: 4rpx;
  bottom: 4rpx;
  width: 20rpx;
  height: 20rpx;
  background: #10B981;
  border-radius: 50%;
  border: 3rpx solid #ffffff;
  box-shadow: 0 2rpx 8rpx rgba(16, 185, 129, 0.4);
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.chat-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #0F172A;
}

.chat-time {
  font-size: 22rpx;
  color: #94A3B8;
  flex-shrink: 0;
  margin-left: 16rpx;
}

.chat-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-msg {
  font-size: 24rpx;
  color: #64748B;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 16rpx;
}

.chat-badge {
  min-width: 40rpx;
  height: 40rpx;
  background: linear-gradient(135deg, #EF4444 0%, #F87171 100%);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 12rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(239, 68, 68, 0.3);
  animation: badgePulse 2s ease-in-out infinite;

  text {
    font-size: 22rpx;
    color: #ffffff;
    font-weight: 700;
  }
}

@keyframes badgePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* ============ 空状态 ============ */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
  background: #ffffff;
  border-radius: 28rpx;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06);
}

.empty-icon-wrap {
  width: 160rpx;
  height: 160rpx;
  background: linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
}

.empty-icon {
  font-size: 80rpx;
}

.empty-title {
  font-size: 32rpx;
  color: #0F172A;
  font-weight: 700;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #64748B;
}

/* ============ 订阅页面 ============ */
.subscribe-header {
  padding: 32rpx 8rpx;
}

.subscribe-header-title {
  font-size: 36rpx;
  font-weight: 800;
  color: #0F172A;
  display: block;
}

.subscribe-header-desc {
  font-size: 26rpx;
  color: #64748B;
  margin-top: 8rpx;
  display: block;
}

.subscribe-list {
  background: #ffffff;
  border-radius: 28rpx;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06);
  border: 1rpx solid rgba(226, 232, 240, 0.6);
  overflow: hidden;
}

.subscribe-item {
  display: flex;
  align-items: center;
  padding: 28rpx;
  border-bottom: 1rpx solid #F1F5F9;
  transition: all 200ms ease;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #F8FAFC;
  }
}

.sub-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);

  text {
    font-size: 32rpx;
  }

  &.sub-1 { background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%); }
  &.sub-2 { background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%); }
  &.sub-3 { background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%); }
  &.sub-4 { background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%); }
  &.sub-5 { background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%); }
  &.sub-6 { background: linear-gradient(135deg, #EDE9FE 0%, #DDD6FE 100%); }
}

.sub-info {
  flex: 1;
  min-width: 0;
}

.sub-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #0F172A;
  display: block;
  margin-bottom: 6rpx;
}

.sub-desc {
  font-size: 24rpx;
  color: #64748B;
  display: block;
}

.sub-switch-wrap {
  padding: 8rpx;
  cursor: pointer;
  margin-left: 16rpx;
}

.sub-switch {
  width: 88rpx;
  height: 48rpx;
  border-radius: 24rpx;
  background: #E2E8F0;
  position: relative;
  transition: all 300ms cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: inset 0 2rpx 6rpx rgba(0, 0, 0, 0.1);

  &.active {
    background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
    box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.3);
  }
}

.sub-switch-knob {
  position: absolute;
  top: 6rpx;
  left: 6rpx;
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
  transition: all 300ms cubic-bezier(0.34, 1.56, 0.64, 1);

  .sub-switch.active & {
    left: 46rpx;
  }
}

.subscribe-footer {
  padding: 48rpx 8rpx;
  text-align: center;
}

.footer-text {
  font-size: 24rpx;
  color: #94A3B8;
}

/* ============ 响应式适配 ============ */
@media (max-width: 320px) {
  .msg-icon {
    width: 60rpx;
    height: 60rpx;
    border-radius: 16rpx;

    .msg-icon-text {
      font-size: 26rpx;
    }
  }
  .chat-avatar {
    width: 72rpx;
    height: 72rpx;
    border-radius: 20rpx;
  }
  .sub-icon {
    width: 60rpx;
    height: 60rpx;
    border-radius: 16rpx;

    text {
      font-size: 26rpx;
    }
  }
  .subscribe-header-title {
    font-size: 32rpx;
  }
}

@media (min-width: 376px) {
  .msg-title {
    font-size: 30rpx;
  }
  .chat-name {
    font-size: 30rpx;
  }
  .sub-title {
    font-size: 30rpx;
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
