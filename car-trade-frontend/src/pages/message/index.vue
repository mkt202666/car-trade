<template>
  <view class="page">
    <u-navbar title="消息中心" :border-bottom="false" :placeholder="true">
      <view slot="right" class="navbar-right" @click="readAll">
        <text class="read-all-btn">全部已读</text>
      </view>
    </u-navbar>

    <!-- Tab 切换 -->
    <u-tabs :list="tabs" :current="currentTab" @change="switchTab" active-color="#3c9cff" :bold="false" bg-color="#fff" bar-width="40"></u-tabs>

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
import { getMessageList, markAllRead } from '@/api/message'

export default {
  data() {
    return {
      currentTab: 0,
      tabs: [{ name: '消息' }, { name: '订阅' }],
      systemMessages: [],
      chatConversations: [],
      subscribeList: [
        { id: 1, title: '系统通知', desc: '平台活动、功能更新等', enabled: true },
        { id: 2, title: '自动推广', desc: '车源自动推广提醒', enabled: true },
        { id: 3, title: '订单通知', desc: '订单状态变更、交易提醒', enabled: true },
        { id: 4, title: '合同通知', desc: '合同签署、到期提醒', enabled: true },
        { id: 5, title: '保证金通知', desc: '保证金变动提醒', enabled: true },
        { id: 6, title: '车行通知', desc: '员工申请、权限变更', enabled: true }
      ],
      currentDate: '今天'
    }
  },
  onShow() {
    this.fetchMessages()
  },
  methods: {
    async fetchMessages() {
      try {
        const res = await getMessageList({ type: 'SYSTEM' })
        const data = res.data
        this.systemMessages = (data.list || data).map(msg => ({
          ...msg,
          subtype: msg.subtype || msg.relatedType || 'system'
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
        uni.$u.toast('全部已读')
      } catch (e) {
        uni.$u.toast('操作失败')
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
    handleSystemMsg(msg) {
      if (msg.relatedId && msg.relatedType === 'order') {
        uni.navigateTo({ url: '/pages/order-detail/index?id=' + msg.relatedId })
      } else {
        uni.$u.toast('查看详情: ' + msg.title)
      }
    },
    toChat(chat) {
      uni.navigateTo({ url: '/pages/customer-service/index?chatId=' + chat.id + '&name=' + chat.name })
    },
    toggleSubscribe(item) {
      item.enabled = !item.enabled
      uni.$u.toast(item.enabled ? '已开启' : '已关闭')
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}
.navbar-right {
  padding-right: 20rpx;
}
.read-all-btn {
  font-size: 26rpx;
  color: #3c9cff;
}

.tab-content {
  padding-bottom: 20rpx;
}

/* 消息分组 */
.message-group {
  background: #fff;
  margin: 16rpx 0;
}
.group-header {
  padding: 24rpx 30rpx 0;
}
.group-date {
  font-size: 24rpx;
  color: #999;
}
.msg-list {
  padding: 0 30rpx;
}
.msg-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.msg-item:last-child {
  border-bottom: none;
}
.msg-icon {
  width: 68rpx;
  height: 68rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
}
.icon-auto_promotion { background: linear-gradient(135deg, #3c9cff, #2979ff); }
.icon-order_update { background: linear-gradient(135deg, #f9ae3d, #f7b731); }
.icon-contract { background: linear-gradient(135deg, #5ac725, #4ca81b); }
.icon-deposit_warning { background: linear-gradient(135deg, #f56c6c, #e74c3c); }
.icon-shop_apply { background: linear-gradient(135deg, #a855f7, #8b5cf6); }
.msg-info {
  flex: 1;
  overflow: hidden;
}
.msg-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  display: block;
}
.msg-content {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-top: 6rpx;
}
.msg-time {
  font-size: 22rpx;
  color: #ccc;
  margin-left: 16rpx;
  flex-shrink: 0;
}

/* 聊天对话 */
.chat-group {
  background: #fff;
  margin: 16rpx 0;
}
.chat-item {
  display: flex;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.chat-item:last-child {
  border-bottom: none;
}
.chat-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  margin-right: 20rpx;
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
  color: #333;
}
.chat-time {
  font-size: 22rpx;
  color: #ccc;
}
.chat-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8rpx;
}
.chat-msg {
  font-size: 24rpx;
  color: #999;
  flex: 1;
}
.chat-badge {
  min-width: 36rpx;
  height: 36rpx;
  background: #f56c6c;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
  margin-left: 12rpx;
  text {
    font-size: 20rpx;
    color: #fff;
  }
}

/* 订阅 */
.subscribe-list {
  background: #fff;
  margin: 16rpx 0;
  padding: 0 30rpx;
}
.subscribe-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.subscribe-item:last-child {
  border-bottom: none;
}
.sub-info {
  flex: 1;
}
.sub-title {
  font-size: 28rpx;
  color: #333;
  display: block;
}
.sub-desc {
  font-size: 22rpx;
  color: #999;
  display: block;
  margin-top: 4rpx;
}
</style>