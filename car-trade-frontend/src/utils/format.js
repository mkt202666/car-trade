export function formatPrice(price) {
  if (!price) return '0.00'
  return (price / 10000).toFixed(1) + '万'
}

export function formatMileage(mileage) {
  if (!mileage) return ''
  return mileage >= 10000 ? (mileage / 10000).toFixed(1) + '万公里' : mileage + '公里'
}

export function formatTime(time) {
  if (!time) return ''
  const diff = Date.now() - new Date(time).getTime()
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return minutes + '分钟前'
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return hours + '小时前'
  const days = Math.floor(hours / 24)
  if (days < 30) return days + '天前'
  return new Date(time).toLocaleDateString('zh-CN')
}

export function formatPhone(phone) {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

export function formatIdCard(id) {
  if (!id) return ''
  return id.replace(/^(.{6}).+(.{4})$/, '$1**********$2')
}
