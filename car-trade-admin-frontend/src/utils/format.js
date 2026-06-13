export function formatMoney(amountCents) {
  if (amountCents == null) return '¥0.00'
  const yuan = (amountCents / 100).toFixed(2)
  return `¥${Number(yuan).toLocaleString('zh-CN', { minimumFractionDigits: 2 })}`
}

export function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}

export function maskPhone(phone) {
  if (!phone || phone.length < 7) return phone || '-'
  return phone.slice(0, 3) + '****' + phone.slice(-4)
}

export function getStatusColor(status) {
  const colors = {
    ACTIVE: 'bg-green-100 text-green-700',
    INACTIVE: 'bg-gray-100 text-gray-600',
    SOLD: 'bg-blue-100 text-blue-700',
    PENDING: 'bg-yellow-100 text-yellow-700',
    CERTIFIED: 'bg-green-100 text-green-700',
    REJECTED: 'bg-red-100 text-red-700',
    FROZEN: 'bg-red-100 text-red-700',
    DISABLED: 'bg-red-100 text-red-700',
    TRADING: 'bg-blue-100 text-blue-700',
    COMPLETED: 'bg-green-100 text-green-700',
    CANCELLED: 'bg-gray-100 text-gray-600',
    DISPUTE: 'bg-orange-100 text-orange-700',
    RECHARGE: 'text-green-600',
    WITHDRAW: 'text-red-600',
    FREEZE: 'text-orange-600',
    UNFREEZE: 'text-blue-600',
    REFUND: 'text-red-600',
  }
  return colors[status] || 'bg-gray-100 text-gray-600'
}

export function getStatusText(status) {
  const texts = {
    ACTIVE: '在售',
    INACTIVE: '已下架',
    SOLD: '已售出',
    PENDING: '待处理',
    PENDING_CONFIRM: '待确认',
    CERTIFIED: '已认证',
    REJECTED: '已拒绝',
    FROZEN: '已冻结',
    DISABLED: '已禁用',
    TRADING: '交易中',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    DISPUTE: '争议中',
    RECHARGE: '充值',
    WITHDRAW: '提现',
    FREEZE: '冻结',
    UNFREEZE: '解冻',
    REFUND: '退款',
    PROCESSING: '处理中',
    RESOLVED: '已解决',
  }
  return texts[status] || status
}
