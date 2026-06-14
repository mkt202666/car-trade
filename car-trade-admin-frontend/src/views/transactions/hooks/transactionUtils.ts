/** 交易状态展示工具 */
import type { OrderStatusKey } from './types'

/**
 * 将订单履约状态键映射为 StatusBadge 组件的 Tag 类型。
 * @param statusKey 订单当前履约状态键
 * @returns Element Plus Tag 语义类型：info / warning / success / primary / neutral / danger
 */
export function orderStatus(statusKey: OrderStatusKey) {
  const map: Record<OrderStatusKey, 'info' | 'warning' | 'success' | 'primary' | 'neutral' | 'danger'> = {
    submitted: 'info',
    deposit_paid: 'warning',
    document_prep: 'primary',
    signed: 'primary',
    delivering: 'danger',
    completed: 'success',
    cancelled: 'neutral',
  }
  return map[statusKey] ?? 'neutral'
}
