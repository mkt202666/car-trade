<template>
  <u-tag :text="statusText" :type="badgeType" size="mini" :plain="true"></u-tag>
</template>

<script>
const STATUS_MAP = {
  PENDING: { text: '待确认', type: 'warning' },
  TRADING: { text: '交易中', type: 'primary' },
  DISPUTE: { text: '争议中', type: 'error' },
  COMPLETED: { text: '已完成', type: 'success' },
  CANCELLED: { text: '已终止', type: 'info' },
  AUCTIONING: { text: '拍卖中', type: 'error' },
  SOLD: { text: '已售', type: 'success' },
  DRAFT: { text: '草稿', type: 'info' },
  PUBLISHED: { text: '已发布', type: 'success' },
  DEPOSIT_PAID: { text: '已付保证金', type: 'warning' },
  SIGNED: { text: '已签署', type: 'primary' }
}
export default {
  props: {
    status: { type: String, required: true },
    type: { type: String, default: '' }
  },
  computed: {
    statusText() {
      return STATUS_MAP[this.status]?.text || this.status
    },
    badgeType() {
      if (this.type) return this.type
      return STATUS_MAP[this.status]?.type || 'info'
    }
  }
}
</script>
