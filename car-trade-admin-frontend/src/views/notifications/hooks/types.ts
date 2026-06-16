/** 通知管理页面类型定义 */

/** 通知列表项，供表格展示 */
export interface NotificationItem {
  id: number
  type: string
  targetUserId: number
  targetUserName: string
  title: string
  content: string
  targetType: string
  targetId: string
  status: string
  createdAt: string
}
