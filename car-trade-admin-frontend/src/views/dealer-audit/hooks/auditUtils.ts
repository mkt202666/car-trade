/** 车行审核展示层工具函数 */
import type { AuditStatus } from './types'

/**
 * 将 ISO 日期字符串格式化为页面展示用的 Y/M/D 格式。
 * @param value - ISO 日期，如 "2026-06-04"
 * @returns 格式化后的日期字符串，如 "2026/6/4"
 */
export function formatAuditDate(value: string) {
  const [y, m, d] = value.split('-')
  return `${y}/${Number(m)}/${Number(d)}`
}

/**
 * 将审核状态枚举映射为中文标签，用于列表状态徽章文案。
 * @param status - 审核状态
 * @returns 对应的中文状态名
 */
export function auditStatusLabel(status: AuditStatus) {
  const map: Record<AuditStatus, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回',
  }
  return map[status]
}

/**
 * 将审核状态映射为 CSS 修饰类名，控制状态徽章配色。
 * @param status - 审核状态
 * @returns BEM 风格的状态类名，如 status-badge--pending
 */
export function auditStatusClass(status: AuditStatus) {
  const map: Record<AuditStatus, string> = {
    pending: 'status-badge--pending',
    approved: 'status-badge--approved',
    rejected: 'status-badge--rejected',
  }
  return map[status]
}

/**
 * 根据审核状态生成凭证编号后缀，拼接在详情标题的审核单 ID 后。
 * @param status - 审核状态
 * @returns 凭证后缀标识，如 CERTIF-PASS
 */
export function auditCodeSuffix(status: AuditStatus) {
  const map: Record<AuditStatus, string> = {
    approved: 'CERTIF-PASS',
    pending: 'CERTIF-PEND',
    rejected: 'CERTIF-REJECT',
  }
  return map[status]
}

/**
 * 将审核状态映射为人工决议区的主文案。
 * @param status - 审核状态
 * @returns 决议描述，如「批准入驻」「驳回申请」
 */
export function auditDecisionLabel(status: AuditStatus) {
  const map: Record<AuditStatus, string> = {
    approved: '批准入驻',
    pending: '未处理/合规中盘',
    rejected: '驳回申请',
  }
  return map[status]
}
