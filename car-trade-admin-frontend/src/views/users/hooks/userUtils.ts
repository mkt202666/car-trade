/** 用户管理页格式化与 ID 工具 */
import type { User } from './types'

/**
 * 解析金额字符串为数字（元）。
 * 支持 W 后缀万单位、人民币符号与千分位逗号。
 * @param str 原始金额字符串，如「5.0W」「¥245w」「—」
 * @returns 换算后的数值（元），无效或空值返回 0
 */
export function parseMoneyStr(str: string) {
  const s = str.trim()
  if (!s || s === '0' || s === '—') return 0
  const upper = s.toUpperCase()
  // W 后缀表示万元，需乘以 10000
  if (upper.endsWith('W')) {
    return Number.parseFloat(upper.slice(0, -1)) * 10000
  }
  // 去除货币符号与逗号后解析
  return Number.parseFloat(s.replace(/[¥,]/g, '')) || 0
}

/**
 * 格式化为人民币展示字符串。
 * @param amount 金额（元）
 * @returns 带人民币符号与千分位的字符串，如「¥ 12,345」
 */
export function formatCurrency(amount: number) {
  return `¥ ${amount.toLocaleString('zh-CN')}`
}

/**
 * 格式化保证金展示（万为单位）。
 * @param amount 保证金金额（元）
 * @returns 大于等于 1 万时返回「x.xW」，否则返回整数字符串，零或负数返回「0」
 */
export function formatDeposit(amount: number) {
  if (amount <= 0) return '0'
  if (amount >= 10000) return `${(amount / 10000).toFixed(1)}W`
  return String(amount)
}

/**
 * 用户角色对应的 StatusBadge 标签样式。
 * @param category 业务角色分类文案
 * @returns Element Plus tag 类型或自定义 neutral 样式
 */
export function categoryStatus(category: string) {
  const map: Record<string, 'info' | 'primary' | 'success' | 'warning' | 'danger' | 'neutral'> = {
    个人用户: 'info',
    车行用户: 'primary',
    IT开发: 'success',
    系统管理员: 'warning',
    已冻结: 'danger',
    已注销: 'neutral',
  }
  return map[category] || 'neutral'
}

/**
 * 基于现有用户列表生成递增用户 ID。
 * 扫描 USR- 前缀编号取最大值后 +1，默认起始基数 3000。
 * @param users 当前用户列表
 * @returns 新用户 ID，格式 USR-xxxx
 */
export function generateUserId(users: User[]) {
  const maxNum = users.reduce((max, user) => {
    const num = Number.parseInt(user.id.replace('USR-', ''), 10)
    return Number.isNaN(num) ? max : Math.max(max, num)
  }, 3000)
  return `USR-${maxNum + 1}`
}
