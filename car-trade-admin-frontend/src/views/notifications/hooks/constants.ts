/** 通知管理筛选项与类型映射常量 */

export const TYPE_OPTIONS = [
  { label: '全部类型', value: '' },
  { label: '系统公告', value: 'SYSTEM_ANNOUNCEMENT' },
  { label: '订单通知', value: 'ORDER_STATUS' },
  { label: '纠纷处理', value: 'DISPUTE_RESOLVED' },
  { label: '系统消息', value: 'SYSTEM' },
]

export const STATUS_OPTIONS = [
  { label: '全部状态', value: '' },
  { label: '已发送', value: 'SENT' },
  { label: '发送失败', value: 'FAILED' },
]

export const TYPE_LABELS: Record<string, string> = {
  SYSTEM_ANNOUNCEMENT: '系统公告',
  ORDER_STATUS: '订单通知',
  DISPUTE_RESOLVED: '纠纷处理',
  SYSTEM: '系统消息',
}

/** 通知类型到 Element Plus el-tag type 的映射 */
export const TYPE_TAG_MAP: Record<string, string> = {
  SYSTEM_ANNOUNCEMENT: 'primary',
  ORDER_STATUS: 'success',
  DISPUTE_RESOLVED: 'warning',
  SYSTEM: 'info',
}

/** 通知状态到 Element Plus el-tag type 的映射 */
export const STATUS_TAG_MAP: Record<string, string> = {
  SENT: 'success',
  FAILED: 'danger',
}

export const STATUS_LABELS: Record<string, string> = {
  SENT: '已发送',
  FAILED: '发送失败',
}
