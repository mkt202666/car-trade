<template>
  <view class="page">
    <!-- 顶部标题 -->
    <view class="header">
      <text class="header-title">消息订阅</text>
    </view>

    <!-- Tab 切换 -->
    <view class="tabs-wrap">
      <view
        class="tab-item"
        :class="{ active: currentTab === 0 }"
        @click="switchTab(0)"
      >
        <u-icon name="chat" size="32" :color="currentTab === 0 ? '#F59E0B' : '#94A3B8'"></u-icon>
        <text class="tab-text">消息</text>
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 1 }"
        @click="switchTab(1)"
      >
        <u-icon name="bell" size="32" :color="currentTab === 1 ? '#F59E0B' : '#94A3B8'"></u-icon>
        <text class="tab-text">订阅</text>
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

    <!-- 自定义底部导航栏 -->
    <custom-tab-bar />
  </view>
</template>

<script>
import { getMessageList, markAllRead, markRead, getUnreadCount, getNotificationSettings, updateNotificationSettings } from '@/api/message'
import { getConversations } from '@/api/chat'
import { requireAuth } from '@/utils/auth'
import CustomTabBar from '@/custom-tab-bar/index.vue'

export default {
  name: 'MessageCenter',
  components: {
    CustomTabBar
  },
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
    // Message page is accessible without login
  },
  onShow() {
    // Load demo data for display
    this.loadDemoData()
  },
  onPullDownRefresh() {
    this.loadDemoData()
    uni.stopPullDownRefresh()
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
    loadDemoData() {
      // Demo data matching prototype
      this.systemMessages = [
        { id: 1, title: '自动推广', content: '您有 20 个车源正在自动拓客', createTime: '13:00:00', isRead: false, subtype: 'auto_promotion' },
        { id: 2, title: '订单状态更新', content: '您的订单状态已更新，请点击查看详情', createTime: '12:00:00', isRead: false, subtype: 'order_update' },
        { id: 3, title: '新的订单合同', content: '您有一份新的订单合同待签署或确认', createTime: '11:00:00', isRead: false, subtype: 'contract' },
        { id: 4, title: '可用保证金不足', content: '您的保证金不足3000，建议及时充值', createTime: '11:00:00', isRead: false, subtype: 'deposit_warning' },
        { id: 5, title: '新成员申请加入您的车行', content: '有新的员工申请加入，请点此审核', createTime: '11:00:00', isRead: false, subtype: 'shop_apply' }
      ]
      this.chatConversations = [
        { id: 1, name: '张学友', lastMessage: '查博士过了吗?', lastMessageTime: '10:00:00', unreadCount: 0, avatar: '' },
        { id: 2, name: '华仔', lastMessage: '老板，奔驰那台客户约了下午看车', lastMessageTime: '4-15', unreadCount: 0, avatar: '' }
      ]
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
   5D好车 - 消息中心 - Liquid Glass Premium Design
   设计语言: Glassmorphism | Dimensional Layering | Motion
   颜色系统: 深海军蓝 #0F172A | CTA蓝 #0369A1 | 琥珀金 #F59E0B
   ========================================================= */

/* ============ 主容器 ============ */
.page {
  min-height: 100vh;
  background:
    radial-gradient(ellipse at top left, rgba(3, 105, 161, 0.06) 0%, transparent 50%),
    radial-gradient(ellipse at bottom right, rgba(245, 158, 11, 0.04) 0%, transparent 50%),
    linear-gradient(180deg, #F8FAFC 0%, #EEF2F7 100%);
  padding-bottom: 220rpx;
}

/* ============ 顶部标题 Hero ============ */
.header {
  position: relative;
  padding: 56rpx 32rpx 40rpx;
  text-align: center;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 200rpx;
    background: linear-gradient(180deg, rgba(15, 23, 42, 0.04) 0%, transparent 100%);
    pointer-events: none;
  }
}

.header-title {
  font-size: 42rpx;
  font-weight: 800;
  color: #0F172A;
  letter-spacing: 1rpx;
  position: relative;
  z-index: 1;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ============ Tab 切换 - Glass Pill Style ============ */
.tabs-wrap {
  display: flex;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
  padding: 12rpx;
  margin: 0 24rpx 24rpx;
  gap: 12rpx;
  border-radius: 24rpx;
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.06), 0 1rpx 4rpx rgba(15, 23, 42, 0.03);
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 24rpx 16rpx;
  border-radius: 18rpx;
  background: transparent;
  cursor: pointer;
  transition: all 400ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:active {
    transform: scale(0.97);
  }
}

.tab-text {
  font-size: 28rpx;
  color: #64748B;
  font-weight: 500;
  transition: all 400ms cubic-bezier(0.25, 0.1, 0.25, 1);
}

.tab-item.active {
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
  box-shadow:
    0 4rpx 20rpx rgba(15, 23, 42, 0.25),
    0 1rpx 4rpx rgba(15, 23, 42, 0.1),
    inset 0 1rpx 0 rgba(255, 255, 255, 0.1);

  .tab-text {
    color: #ffffff;
    font-weight: 700;
    letter-spacing: 0.5rpx;
  }
}

/* ============ Tab 内容 ============ */
.tab-content {
  padding: 0 24rpx;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
}

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

/* ============ 消息分组 - Liquid Glass Card ============ */
.message-group {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16rpx);
  border-radius: 28rpx;
  margin-bottom: 24rpx;
  box-shadow:
    0 4rpx 24rpx rgba(15, 23, 42, 0.08),
    0 1rpx 4rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  overflow: hidden;
  position: relative;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) 100ms both;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 28rpx 28rpx;
  background: linear-gradient(135deg, rgba(248, 250, 252, 0.8) 0%, rgba(241, 245, 249, 0.5) 100%);
  border-bottom: 1rpx solid rgba(226, 232, 240, 0.5);
}

.group-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 18rpx;
  background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(3, 105, 161, 0.15);

  text {
    font-size: 26rpx;
  }

  &.chat {
    background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
    box-shadow: 0 2rpx 8rpx rgba(245, 158, 11, 0.2);
  }
}

.group-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
  flex: 1;
  letter-spacing: 0.3rpx;
}

.group-date {
  font-size: 22rpx;
  color: #94A3B8;
  font-weight: 500;
}

/* ============ 消息列表 ============ */
.msg-list {
  padding: 4rpx 0;
}

.msg-item {
  display: flex;
  align-items: center;
  padding: 24rpx 28rpx;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  position: relative;

  &::after {
    content: '';
    position: absolute;
    left: 120rpx;
    right: 28rpx;
    bottom: 0;
    height: 1rpx;
    background: linear-gradient(90deg, transparent 0%, rgba(226, 232, 240, 0.7) 10%, rgba(226, 232, 240, 0.7) 90%, transparent 100%);
  }

  &:last-child::after {
    display: none;
  }

  &:active {
    background: linear-gradient(135deg, rgba(248, 250, 252, 0.9) 0%, rgba(241, 245, 249, 0.7) 100%);
    transform: scale(0.995);
  }
}

.msg-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1), inset 0 1rpx 0 rgba(255, 255, 255, 0.25);

  .msg-icon-text {
    font-size: 34rpx;
    filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.2));
  }

  &.icon-auto_promotion {
    background: linear-gradient(135deg, #0369A1 0%, #0EA5E9 100%);
    box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.35), inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  }
  &.icon-order_update {
    background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%);
    box-shadow: 0 4rpx 16rpx rgba(245, 158, 11, 0.35), inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  }
  &.icon-contract {
    background: linear-gradient(135deg, #10B981 0%, #34D399 100%);
    box-shadow: 0 4rpx 16rpx rgba(16, 185, 129, 0.3), inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  }
  &.icon-deposit_warning {
    background: linear-gradient(135deg, #EF4444 0%, #F87171 100%);
    box-shadow: 0 4rpx 16rpx rgba(239, 68, 68, 0.3), inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  }
  &.icon-shop_apply {
    background: linear-gradient(135deg, #8B5CF6 0%, #A78BFA 100%);
    box-shadow: 0 4rpx 16rpx rgba(139, 92, 246, 0.3), inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  }
  &.icon-system {
    background: linear-gradient(135deg, #475569 0%, #64748B 100%);
    box-shadow: 0 4rpx 16rpx rgba(71, 85, 105, 0.25), inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  }
}

.msg-info {
  flex: 1;
  min-width: 0;
}

.msg-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
  display: block;
  margin-bottom: 8rpx;
  letter-spacing: 0.3rpx;
}

.msg-content {
  font-size: 26rpx;
  color: #64748B;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
}

.msg-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12rpx;
  margin-left: 16rpx;
  flex-shrink: 0;
}

.msg-time {
  font-size: 22rpx;
  color: #94A3B8;
  font-weight: 500;
}

.msg-unread {
  width: 18rpx;
  height: 18rpx;
  background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
  border-radius: 50%;
  box-shadow: 0 2rpx 10rpx rgba(239, 68, 68, 0.5), 0 0 0 4rpx rgba(239, 68, 68, 0.12);
}

/* ============ 聊天对话列表 ============ */
.chat-group {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16rpx);
  border-radius: 28rpx;
  box-shadow:
    0 4rpx 24rpx rgba(15, 23, 42, 0.08),
    0 1rpx 4rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  overflow: hidden;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) 200ms both;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 24rpx 28rpx;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  position: relative;

  &::after {
    content: '';
    position: absolute;
    left: 136rpx;
    right: 28rpx;
    bottom: 0;
    height: 1rpx;
    background: linear-gradient(90deg, transparent 0%, rgba(226, 232, 240, 0.7) 10%, rgba(226, 232, 240, 0.7) 90%, transparent 100%);
  }

  &:last-child::after {
    display: none;
  }

  &:active {
    background: linear-gradient(135deg, rgba(248, 250, 252, 0.9) 0%, rgba(241, 245, 249, 0.7) 100%);
    transform: scale(0.995);
  }
}

.chat-avatar-wrap {
  position: relative;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.chat-avatar {
  width: 92rpx;
  height: 92rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #E2E8F0 0%, #CBD5E1 100%);
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.1), inset 0 1rpx 0 rgba(255, 255, 255, 0.5);
}

.chat-online {
  position: absolute;
  right: 6rpx;
  bottom: 6rpx;
  width: 22rpx;
  height: 22rpx;
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  border-radius: 50%;
  border: 4rpx solid #ffffff;
  box-shadow: 0 2rpx 8rpx rgba(16, 185, 129, 0.5);
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
}

.chat-name {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
  letter-spacing: 0.3rpx;
}

.chat-time {
  font-size: 22rpx;
  color: #94A3B8;
  flex-shrink: 0;
  margin-left: 16rpx;
  font-weight: 500;
}

.chat-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-msg {
  font-size: 26rpx;
  color: #64748B;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 16rpx;
  line-height: 1.5;
}

.chat-badge {
  min-width: 44rpx;
  height: 44rpx;
  background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 14rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 12rpx rgba(239, 68, 68, 0.35), inset 0 1rpx 0 rgba(255, 255, 255, 0.25);
  animation: badgePulse 2.4s cubic-bezier(0.25, 0.1, 0.25, 1) infinite;

  text {
    font-size: 22rpx;
    color: #ffffff;
    font-weight: 700;
    letter-spacing: 0.5rpx;
  }
}

@keyframes badgePulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 4rpx 12rpx rgba(239, 68, 68, 0.35), inset 0 1rpx 0 rgba(255, 255, 255, 0.25);
  }
  50% {
    transform: scale(1.06);
    box-shadow: 0 6rpx 20rpx rgba(239, 68, 68, 0.45), inset 0 1rpx 0 rgba(255, 255, 255, 0.25);
  }
}

/* ============ 空状态 ============ */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 140rpx 40rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16rpx);
  border-radius: 28rpx;
  box-shadow:
    0 4rpx 24rpx rgba(15, 23, 42, 0.08),
    0 1rpx 4rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.7);
}

.empty-icon-wrap {
  width: 180rpx;
  height: 180rpx;
  background: linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%);
  border-radius: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 36rpx;
  box-shadow: inset 0 2rpx 8rpx rgba(15, 23, 42, 0.06), 0 4rpx 16rpx rgba(15, 23, 42, 0.06);
}

.empty-icon {
  font-size: 88rpx;
  opacity: 0.7;
}

.empty-title {
  font-size: 34rpx;
  color: #0F172A;
  font-weight: 700;
  margin-bottom: 14rpx;
  letter-spacing: 0.5rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #64748B;
  line-height: 1.6;
  text-align: center;
}

/* ============ 订阅管理页面 ============ */
.subscribe-header {
  padding: 8rpx 8rpx 32rpx;
  position: relative;
}

.subscribe-header-title {
  font-size: 40rpx;
  font-weight: 800;
  color: #0F172A;
  display: block;
  letter-spacing: 1rpx;
  background: linear-gradient(135deg, #0F172A 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subscribe-header-desc {
  font-size: 26rpx;
  color: #64748B;
  margin-top: 12rpx;
  display: block;
  line-height: 1.6;
}

.subscribe-list {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16rpx);
  border-radius: 28rpx;
  box-shadow:
    0 4rpx 24rpx rgba(15, 23, 42, 0.08),
    0 1rpx 4rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  overflow: hidden;
}

.subscribe-item {
  display: flex;
  align-items: center;
  padding: 28rpx;
  border-bottom: 1rpx solid rgba(226, 232, 240, 0.5);
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: linear-gradient(135deg, rgba(248, 250, 252, 0.9) 0%, rgba(241, 245, 249, 0.7) 100%);
  }
}

.sub-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08), inset 0 1rpx 0 rgba(255, 255, 255, 0.6);

  text {
    font-size: 34rpx;
  }

  &.sub-1 { background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%); box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.2), inset 0 1rpx 0 rgba(255, 255, 255, 0.6); }
  &.sub-2 { background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%); box-shadow: 0 4rpx 16rpx rgba(245, 158, 11, 0.2), inset 0 1rpx 0 rgba(255, 255, 255, 0.6); }
  &.sub-3 { background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%); box-shadow: 0 4rpx 16rpx rgba(59, 130, 246, 0.2), inset 0 1rpx 0 rgba(255, 255, 255, 0.6); }
  &.sub-4 { background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%); box-shadow: 0 4rpx 16rpx rgba(16, 185, 129, 0.2), inset 0 1rpx 0 rgba(255, 255, 255, 0.6); }
  &.sub-5 { background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%); box-shadow: 0 4rpx 16rpx rgba(239, 68, 68, 0.15), inset 0 1rpx 0 rgba(255, 255, 255, 0.6); }
  &.sub-6 { background: linear-gradient(135deg, #EDE9FE 0%, #DDD6FE 100%); box-shadow: 0 4rpx 16rpx rgba(139, 92, 246, 0.2), inset 0 1rpx 0 rgba(255, 255, 255, 0.6); }
}

.sub-info {
  flex: 1;
  min-width: 0;
}

.sub-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #0F172A;
  display: block;
  margin-bottom: 8rpx;
  letter-spacing: 0.3rpx;
}

.sub-desc {
  font-size: 26rpx;
  color: #64748B;
  display: block;
  line-height: 1.5;
}

.sub-switch-wrap {
  padding: 8rpx;
  cursor: pointer;
  margin-left: 20rpx;
  flex-shrink: 0;
}

.sub-switch {
  width: 96rpx;
  height: 52rpx;
  border-radius: 26rpx;
  background: linear-gradient(135deg, #E2E8F0 0%, #CBD5E1 100%);
  position: relative;
  transition: all 400ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: inset 0 2rpx 6rpx rgba(15, 23, 42, 0.1);

  &.active {
    background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
    box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.35), inset 0 1rpx 0 rgba(255, 255, 255, 0.1);
  }
}

.sub-switch-knob {
  position: absolute;
  top: 6rpx;
  left: 6rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffffff 0%, #F8FAFC 100%);
  box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, 0.15), 0 1rpx 2rpx rgba(15, 23, 42, 0.1);
  transition: all 400ms cubic-bezier(0.25, 0.1, 0.25, 1);

  .sub-switch.active & {
    left: 50rpx;
  }
}

.subscribe-footer {
  padding: 48rpx 8rpx;
  text-align: center;
}

.footer-text {
  font-size: 24rpx;
  color: #94A3B8;
  line-height: 1.6;
}

/* ============ 响应式适配 ============ */
@media (max-width: 320px) {
  .msg-icon {
    width: 64rpx;
    height: 64rpx;
    border-radius: 20rpx;
    .msg-icon-text { font-size: 28rpx; }
  }
  .chat-avatar {
    width: 76rpx;
    height: 76rpx;
    border-radius: 22rpx;
  }
  .sub-icon {
    width: 64rpx;
    height: 64rpx;
    border-radius: 20rpx;
    text { font-size: 28rpx; }
  }
  .subscribe-header-title { font-size: 34rpx; }
  .header-title { font-size: 36rpx; }
}

@media (min-width: 376px) {
  .msg-title { font-size: 32rpx; }
  .chat-name { font-size: 32rpx; }
  .sub-title { font-size: 32rpx; }
  .msg-content, .chat-msg, .sub-desc { font-size: 28rpx; }
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
