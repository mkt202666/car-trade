
<template>
  <view class="page">
    <u-navbar title="保证金" :border-bottom="false" :placeholder="true"></u-navbar>

    <view class="page-content">
      <!-- 余额卡片 -->
      <view class="balance-card">
        <text class="balance-label">可用保证金</text>
        <text class="balance-amount">¥{{ formatAmount(deposit.balance) }}</text>
        <view class="balance-meta">
          <text class="meta-item">冻结: ¥{{ formatAmount(deposit.frozenAmount) }}</text>
          <text class="meta-item">累计: ¥{{ formatAmount(deposit.totalDeposit) }}</text>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-row">
        <view class="action-btn recharge-btn" @click="showRecharge = true">
          <u-icon name="plus-circle" size="40" color="#0369A1"></u-icon>
          <text>充值</text>
        </view>
        <view class="action-btn withdraw-btn" @click="showWithdraw = true">
          <u-icon name="minus-circle" size="40" color="#f56c6c"></u-icon>
          <text>提现</text>
        </view>
      </view>

      <!-- 交易记录 -->
      <view class="records-section">
        <view class="section-header">
          <text class="section-title">交易记录</text>
        </view>
        <view class="records-list">
          <view class="record-item" v-for="record in records" :key="record.id">
            <view class="record-left">
              <view class="record-icon" :class="record.type === 'RECHARGE' ? 'recharge' : 'withdraw'">
                <u-icon :name="record.type === 'RECHARGE' ? 'plus' : 'minus'" size="24" color="#fff"></u-icon>
              </view>
              <view class="record-info">
                <text class="record-title">{{ record.remark }}</text>
                <text class="record-time">{{ formatTime(record.createdAt) }}</text>
              </view>
            </view>
            <view class="record-right">
              <text class="record-amount" :class="record.type === 'RECHARGE' ? 'plus' : 'minus'">
                {{ record.type === 'RECHARGE' ? '+' : '-' }}¥{{ formatAmount(record.amount) }}
              </text>
              <text class="record-balance">余额: ¥{{ formatAmount(record.balanceAfter) }}</text>
            </view>
          </view>
          <view class="empty-state" v-if="records.length === 0 && !loading">
            <text>暂无记录</text>
          </view>
          <view class="loading-more" v-if="loading">
            <text>加载中...</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 充值弹窗 -->
    <u-popup v-model="showRecharge" mode="bottom" :closeable="true" border-radius="24">
      <view class="popup-content">
        <text class="popup-title">保证金充值</text>
        <u-field 
          v-model="rechargeAmount" 
          type="number" 
          label="充值金额" 
          placeholder="请输入充值金额"
          :border="true"
        >
          <template slot="suffix">
            <text class="field-suffix">元</text>
          </template>
        </u-field>
        <view class="quick-amounts">
          <text 
            v-for="amt in quickAmounts" 
            :key="amt" 
            class="quick-amt" 
            :class="{ active: rechargeAmount == amt }"
            @click="rechargeAmount = amt"
          >{{ amt }}元</text>
        </view>
        <u-button type="primary" shape="circle" :disabled="!canRecharge" @click="handleRecharge">
          确认充值
        </u-button>
      </view>
    </u-popup>

    <!-- 提现弹窗 -->
    <u-popup v-model="showWithdraw" mode="bottom" :closeable="true" border-radius="24">
      <view class="popup-content">
        <text class="popup-title">保证金提现</text>
        <view class="withdraw-tip">
          <text>当前可提现余额: ¥{{ formatAmount(deposit.balance) }}</text>
        </view>
        <u-field 
          v-model="withdrawAmount" 
          type="number" 
          label="提现金额" 
          placeholder="请输入提现金额"
          :border="true"
        >
          <template slot="suffix">
            <text class="field-suffix">元</text>
          </template>
        </u-field>
        <view class="quick-amounts">
          <text 
            class="quick-amt"
            :class="{ active: withdrawAmount == deposit.balance }"
            @click="withdrawAmount = deposit.balance"
          >全部提现</text>
        </view>
        <u-button type="primary" shape="circle" :disabled="!canWithdraw" @click="handleWithdraw">
          确认提现
        </u-button>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getDeposit, rechargeDeposit, withdrawDeposit, getDepositRecords } from '@/api/finance'

export default {
  data() {
    return {
      deposit: {
        balance: 0,
        frozenAmount: 0,
        totalDeposit: 0
      },
      records: [],
      loading: false,
      page: 1,
      hasMore: true,
      showRecharge: false,
      showWithdraw: false,
      rechargeAmount: '',
      withdrawAmount: '',
      quickAmounts: [1000, 2000, 5000, 10000]
    }
  },
  computed: {
    canRecharge() {
      const amt = Number(this.rechargeAmount)
      return amt > 0
    },
    canWithdraw() {
      const amt = Number(this.withdrawAmount)
      return amt > 0 && amt <= this.deposit.balance
    }
  },
  onLoad() {
    this.fetchData()
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.fetchRecords()
    }
  },
  onPullDownRefresh() {
    this.page = 1
    this.hasMore = true
    this.records = []
    this.fetchData()
    setTimeout(() => uni.stopPullDownRefresh(), 500)
  },
  methods: {
    async fetchData() {
      await Promise.all([this.fetchDeposit(), this.fetchRecords()])
    },
    async fetchDeposit() {
      try {
        const res = await getDeposit()
        this.deposit = (res && res.data) || { balance: 0, frozenAmount: 0, totalDeposit: 0 }
      } catch (e) {
        console.error(e)
      }
    },
    async fetchRecords() {
      if (this.loading) return
      this.loading = true
      try {
        // 后端 PageResult 返回字段名为 "list"（非 "records"）
        // 使用与项目其他页面一致的防御性取值链：list → records → data → 空数组
        const res = await getDepositRecords({ page: this.page, size: 20 })
        const data = (res && res.data) || {}
        const list = data.list || data.records || []
        const total = data.total || 0
        if (this.page === 1) {
          this.records = list
        } else {
          this.records = [...this.records, ...list]
        }
        // 使用 total 判断是否还有更多数据（比固定 size 更准确）
        this.hasMore = this.records.length < total
        this.page++
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    async handleRecharge() {
      if (!this.canRecharge) return
      try {
        await rechargeDeposit({ amount: Number(this.rechargeAmount) })
        uni.$u.toast('充值成功')
        this.showRecharge = false
        this.rechargeAmount = ''
        this.page = 1
        this.records = []
        this.fetchData()
      } catch (e) {
        console.error(e)
      }
    },
    async handleWithdraw() {
      if (!this.canWithdraw) return
      try {
        await withdrawDeposit({ amount: Number(this.withdrawAmount) })
        uni.$u.toast('提现成功')
        this.showWithdraw = false
        this.withdrawAmount = ''
        this.page = 1
        this.records = []
        this.fetchData()
      } catch (e) {
        console.error(e)
      }
    },
    formatAmount(val) {
      const n = Number(val) || 0
      return n.toLocaleString()
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
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
  padding: 20rpx 30rpx 40rpx;
}

/* 余额卡片 */
.balance-card {
  background: linear-gradient(135deg, $cta-color, darken($cta-color, 10%));
  border-radius: $border-radius;
  padding: 40rpx 30rpx;
  color: #fff;
  margin-bottom: 20rpx;
  box-shadow: $shadow;
}
.balance-label {
  font-size: 26rpx;
  opacity: 0.9;
}
.balance-amount {
  font-size: 60rpx;
  font-weight: 700;
  margin: 10rpx 0 20rpx;
  display: block;
}
.balance-meta {
  display: flex;
  gap: 30rpx;
}
.meta-item {
  font-size: 24rpx;
  opacity: 0.85;
}

/* 操作按钮 */
.action-row {
  display: flex;
  gap: 20rpx;
  margin-bottom: 30rpx;
}
.action-btn {
  flex: 1;
  background: #fff;
  border-radius: $border-radius;
  padding: 30rpx 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  font-size: 28rpx;
  color: $text-color;
  box-shadow: $shadow;
  cursor: pointer;
  transition: $transition;

  &:active {
    transform: scale(0.98);
    box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, 0.08);
  }
}

/* 记录区域 */
.records-section {
  background: #fff;
  border-radius: $border-radius;
  padding: 30rpx;
  box-shadow: $shadow;
}
.section-header {
  margin-bottom: 20rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-color;
}
.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid $border-color;
  transition: $transition;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}
.record-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.record-icon {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.record-icon.recharge {
  background: $cta-color;
}
.record-icon.withdraw {
  background: #f56c6c;
}
.record-info {
  display: flex;
  flex-direction: column;
}
.record-title {
  font-size: 28rpx;
  color: $text-color;
}
.record-time {
  font-size: 22rpx;
  color: $text-secondary;
  margin-top: 4rpx;
}
.record-right {
  text-align: right;
}
.record-amount {
  font-size: 30rpx;
  font-weight: 600;
  display: block;
}
.record-amount.plus {
  color: $cta-color;
}
.record-amount.minus {
  color: #f56c6c;
}
.record-balance {
  font-size: 22rpx;
  color: $text-secondary;
  margin-top: 4rpx;
  display: block;
}
.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: $text-secondary;
  font-size: 26rpx;
}
.loading-more {
  text-align: center;
  padding: 30rpx 0;
  color: $text-secondary;
  font-size: 24rpx;
}

/* 弹窗 */
.popup-content {
  padding: 30rpx;
}
.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  display: block;
  text-align: center;
  margin-bottom: 30rpx;
  color: $text-color;
}
.field-suffix {
  font-size: 28rpx;
  color: $text-secondary;
}
.quick-amounts {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin: 20rpx 0 40rpx;
}
.quick-amt {
  padding: 10rpx 24rpx;
  background: $bg-color;
  border-radius: $border-radius;
  font-size: 26rpx;
  color: $text-color;
  cursor: pointer;
  transition: $transition;
  border: 1rpx solid transparent;

  &:active {
    transform: scale(0.98);
  }
}
.quick-amt.active {
  background: rgba(3, 105, 161, 0.1);
  color: $cta-color;
  border-color: $cta-color;
}
.withdraw-tip {
  background: #fff7e6;
  padding: 16rpx;
  border-radius: $border-radius;
  margin-bottom: 20rpx;
  font-size: 24rpx;
  color: #f9ae3d;
  text-align: center;
}
</style>