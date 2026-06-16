/** 纠纷管理相关类型定义 */

/** 纠纷状态枚举 */
export type DisputeStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'REJECTED'

/** 单条纠纷记录 */
export interface DisputeItem {
  /** 纠纷唯一编号 */
  id: number
  /** 关联订单号 */
  orderId: string
  /** 订单标题 */
  orderTitle: string
  /** 发起人姓名 */
  initiatorName: string
  /** 发起人手机号 */
  initiatorPhone: string
  /** 纠纷原因 */
  reason: string
  /** 当前处理状态 */
  status: DisputeStatus
  /** 创建时间 */
  createdAt: string
}
