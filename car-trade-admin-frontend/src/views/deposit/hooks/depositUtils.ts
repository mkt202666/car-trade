/** 保证金流水展示层工具函数 */

import type { DepositAccount, FlowTypeKey } from './types'

/**
 * 获取流水类型对应的 Tag 样式配置
 * @param typeKey - 流水业务类型键
 * @returns Element Plus Tag 的 type 与 effect 属性
 */
export function flowTypeTag(typeKey: FlowTypeKey) {
  const map: Record<FlowTypeKey, { type: 'success' | 'warning' | 'info' | 'danger'; effect: 'light' | 'plain' }> = {
    recharge: { type: 'success', effect: 'light' },
    freeze: { type: 'warning', effect: 'light' },
    release: { type: 'info', effect: 'plain' },
    refund: { type: 'danger', effect: 'light' },
  }
  return map[typeKey]
}

/**
 * 格式化核算主体下拉选项标签
 * @param account - 保证金账户对象
 * @returns 含姓名、角色与可用余额的展示文本
 */
export function formatAccountLabel(account: DepositAccount) {
  const roleSuffix = account.role ? ` (${account.role})` : ''
  const balance = account.available.toLocaleString('en-US')
  return `${account.name}${roleSuffix} (可用额: ￥${balance})`
}

/**
 * 格式化金额显示（千分位，取绝对值）
 * @param num - 原始数值，可为正负
 * @returns 千分位分隔的绝对值字符串
 */
export function formatAmountValue(num: number) {
  return Math.abs(num).toLocaleString('en-US')
}
