/** 车行管理页格式化与 ID 工具 */
import type { Dealer } from './types'

/**
 * 车行准入状态对应的中文展示文案。
 * @param status 车行准入状态枚举
 * @returns 「正常开业」、「已停业」或「封禁挂起」
 */
export function statusLabel(status: Dealer['status']) {
  if (status === 'active') return '正常开业'
  if (status === 'inactive') return '已停业'
  return '封禁挂起'
}

/**
 * 格式化保证金余额为人民币展示字符串。
 * @param amount 保证金金额（元）
 * @returns 带人民币符号与千分位的字符串，如「￥80,000」
 */
export function formatDeposit(amount: number) {
  return `￥${amount.toLocaleString('zh-CN')}`
}

/**
 * 取成员姓名首字作为圆形头像占位符。
 * @param name 成员姓名
 * @returns 姓名字符串的第一个字符
 */
export function memberAvatar(name: string) {
  return name.charAt(0)
}

/**
 * 格式化授信额度为万单位展示。
 * @param amount 授信金额（元）
 * @returns 大于等于 1 万时返回「x.xW」，否则返回整数字符串
 */
export function formatCredit(amount: number) {
  if (amount >= 10000) return `${(amount / 10000).toFixed(1)}W`
  return String(amount)
}

/**
 * 基于现有车行列表生成递增 ID（S 前缀，与 API 返回的格式化一致）。
 * 扫描 S 前缀编号取最大值后 +1，默认起始基数 0。
 * @param dealers 当前车行列表
 * @returns 新车行 ID，格式 S000001
 */
export function generateDealerId(dealers: Dealer[]) {
  const maxNum = dealers.reduce((max, d) => {
    const num = parseInt(d.id.replace(/^S/, ''), 10)
    return num > max ? num : max
  }, 0)
  return `S${String(maxNum + 1).padStart(6, '0')}`
}
