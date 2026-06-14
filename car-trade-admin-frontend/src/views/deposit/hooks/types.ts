/** 保证金流水相关类型定义 */

/** 流水业务类型键，用于筛选与 Tag 样式映射 */
export type FlowTypeKey = 'recharge' | 'freeze' | 'release' | 'refund'

/** 人工记账划转科目键，对应 subjectOptions 下拉选项 */
export type SubjectKey = 'pay_deposit' | 'lock_deposit' | 'release_deposit' | 'refund_deposit'

/** 保证金核算主体账户，供人工记账时选择目标商户/买家 */
export interface DepositAccount {
  /** 用户/主体唯一 ID，如 USR-3001 */
  id: string
  /** 主体显示名称 */
  name: string
  /** 角色说明，如「创建者」，可选 */
  role?: string
  /** 当前可用保证金余额（元） */
  available: number
}

/** 单条保证金流水记录，供表格展示与筛选 */
export interface DepositFlow {
  /** 交易序列号，如 TX-5001 */
  id: string
  /** 交易发生时间，本地化字符串 */
  time: string
  /** 往来账户客户姓名 */
  customerName: string
  /** 往来账户客户 ID */
  customerId: string
  /** 流水业务类型键，映射 Tag 样式 */
  typeKey: FlowTypeKey
  /** 交易科目中文标签，直接展示 */
  typeLabel: string
  /** 金额符号，+ 表示入账，- 表示出账 */
  amountSign: '+' | '-'
  /** 交易金额绝对值字符串，已千分位格式化 */
  amountValue: string
  /** 交易后账户余额字符串，已千分位格式化 */
  balance: string
  /** 往来摘要备注说明 */
  note: string
}
