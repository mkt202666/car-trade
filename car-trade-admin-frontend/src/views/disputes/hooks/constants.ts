/** 纠纷管理配置常量 */
import type { DisputeStatus } from './types'

/** 状态筛选下拉选项 */
export const STATUS_OPTIONS = [
  { label: '全部', value: '' },
  { label: '待处理', value: 'OPEN' },
  { label: '处理中', value: 'IN_PROGRESS' },
  { label: '已解决', value: 'RESOLVED' },
  { label: '已驳回', value: 'REJECTED' },
]

/** 状态中文标签映射 */
export const STATUS_LABELS: Record<DisputeStatus, string> = {
  OPEN: '待处理',
  IN_PROGRESS: '处理中',
  RESOLVED: '已解决',
  REJECTED: '已驳回',
}

/** 状态对应的 el-tag type 颜色 */
export const STATUS_COLORS: Record<DisputeStatus, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
  OPEN: 'warning',
  IN_PROGRESS: 'primary',
  RESOLVED: 'success',
  REJECTED: 'danger',
}
