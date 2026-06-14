/** 求购信息展示工具 */
import type { PurchasePublisherType, PurchaseStatus } from './types'

/**
 * 将发布人类型键转为中文标签。
 * @param type 发布人类型：dealer 合作车商 / individual 个人用户
 * @returns 用于 Tag 与搜索匹配的中文文案
 */
export function publisherTypeLabel(type: PurchasePublisherType) {
  return type === 'dealer' ? '5D合作车商' : '个人用户'
}

/**
 * 将求购业务状态映射为 StatusBadge 组件的 Tag 类型。
 * @param status 求购状态：推广中 / 待处理 / 已成交 / 已关闭
 * @returns Element Plus Tag 语义类型：primary / success / warning / neutral
 */
export function purchaseStatus(status: PurchaseStatus) {
  const map: Record<PurchaseStatus, 'primary' | 'success' | 'warning' | 'neutral'> = {
    推广中: 'primary',
    待处理: 'warning',
    已成交: 'success',
    已关闭: 'neutral',
  }
  return map[status] || 'neutral'
}
