/** 保证金统计卡片、流水 mock 数据与人工记账表单配置 */

import type { FormRules } from 'element-plus'
import type { DepositAccount, DepositFlow, FlowTypeKey, SubjectKey } from './types'

/** 顶部统计卡片「冻结托管定金」图标组件 */
export const ShieldCheckIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
      <path d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z" />
      <path d="m9 12 2 2 4-4" />
    </svg>
  `,
}

/** 顶部统计卡片「流水累计规模」图标组件 */
export const CreditCardIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
      <rect width="20" height="14" x="2" y="5" rx="2" />
      <line x1="2" x2="22" y1="10" y2="10" />
    </svg>
  `,
}

/** 顶部统计卡片「核算账户数」图标组件 */
export const BuildingIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
      <rect width="16" height="20" x="4" y="2" rx="2" ry="2" />
      <path d="M9 22v-4h6v4" />
      <path d="M8 6h.01" />
      <path d="M16 6h.01" />
      <path d="M12 6h.01" />
      <path d="M12 10h.01" />
      <path d="M12 14h.01" />
      <path d="M16 10h.01" />
      <path d="M16 14h.01" />
      <path d="M8 10h.01" />
      <path d="M8 14h.01" />
    </svg>
  `,
}

/** 顶部三格汇总统计 mock 数据，绑定 stats-grid 区域 */
export const summaryStats = [
  {
    label: '当前冻结托管定金总和 (CNY)',
    value: '¥ 5,000',
    icon: ShieldCheckIcon,
    iconBg: 'amber',
  },
  {
    label: '保证金流水累计交易规模',
    value: '¥ 45,000',
    icon: CreditCardIcon,
    iconBg: 'emerald',
  },
  {
    label: '入账账户往来核算账户数',
    value: '19 个主体',
    icon: BuildingIcon,
    iconBg: 'indigo',
  },
]

/** 保证金流水初始 mock 数据，页面加载时填充 flows 列表 */
export const SEED_FLOWS: DepositFlow[] = [
  {
    id: 'TX-5001',
    time: '2026/6/1 16:30:00',
    customerName: '张建国',
    customerId: 'USR-3001',
    typeKey: 'recharge',
    typeLabel: '保证金网银充值',
    amountSign: '+',
    amountValue: '10,000',
    balance: '50,000',
    note: '支付宝网银扫码充值保证金',
  },
  {
    id: 'TX-5002',
    time: '2026/6/1 18:00:00',
    customerName: '张建国',
    customerId: 'USR-3001',
    typeKey: 'freeze',
    typeLabel: '冻结用于竞拍',
    amountSign: '-',
    amountValue: '10,000',
    balance: '40,000',
    note: '用于锁单 ORD-2026053001 保障金',
  },
  {
    id: 'TX-5003',
    time: '2026/5/31 00:45:00',
    customerName: '李四平',
    customerId: 'USR-3002',
    typeKey: 'release',
    typeLabel: '解冻回归原账户',
    amountSign: '+',
    amountValue: '20,000',
    balance: '20,000',
    note: '订单交付完成，系统释放全额待订单保障',
  },
  {
    id: 'TX-5004',
    time: '2026/6/3 17:12:00',
    customerName: '王凯文',
    customerId: 'USR-3003',
    typeKey: 'freeze',
    typeLabel: '冻结用于竞拍',
    amountSign: '-',
    amountValue: '5,000',
    balance: '30,000',
    note: '履约竞拍临时划扣锁定',
  },
]

/** 保证金核算主体账户 mock 数据，供人工记账下拉选择 */
export const SEED_DEPOSIT_ACCOUNTS: DepositAccount[] = [
  { id: 'USR-6283', name: '周腾达', role: '创建者', available: 0 },
  { id: 'USR-0690372', name: '新成员0690372', available: 0 },
  { id: 'USR-0141779', name: '新成员0141779', available: 0 },
  { id: 'USR-9291456', name: '新成员9291456', available: 0 },
  { id: 'USR-5407197', name: '新成员5407197', available: 0 },
  { id: 'USR-4924204', name: '新成员4924204', available: 0 },
  { id: 'USR-3773658', name: '新成员3773658', available: 0 },
  { id: 'USR-3001', name: '张建国', available: 50000 },
  { id: 'USR-3002', name: '李思平', available: 20000 },
  { id: 'USR-3003', name: '王凯文', available: 35000 },
  { id: 'USR-3004', name: '陈伟', available: 15000 },
  { id: 'USR-3005', name: '苏南宏大好车商行', available: 100000 },
  { id: 'USR-3006', name: '林建平', available: 20000 },
  { id: 'USR-3007', name: '赵子强', available: 0 },
  { id: 'USR-3008', name: '陈美华', available: 0 },
  { id: 'USR-3009', name: '李小龙', available: 10000 },
  { id: 'USR-3010', name: '周经理', available: 35000 },
  { id: 'USR-3011', name: '王小二', available: 5000 },
  { id: 'USR-3012', name: '赵德柱', available: 0 },
]

/** 人工记账划转科目下拉选项，value 为 SubjectKey */
export const subjectOptions: { value: SubjectKey; label: string }[] = [
  { value: 'pay_deposit', label: '充值补充保证金 (+余额)' },
  { value: 'lock_deposit', label: '扣划冻结竞价保证金 (-余额)' },
  { value: 'release_deposit', label: '释放解冻保证金 (+余额)' },
  { value: 'refund_deposit', label: '提现退还保证金 (-余额)' },
]

/** 划转科目到流水类型键的映射，用于生成新流水记录 */
export const subjectToFlowType: Record<SubjectKey, FlowTypeKey> = {
  pay_deposit: 'recharge',
  lock_deposit: 'freeze',
  release_deposit: 'release',
  refund_deposit: 'refund',
}

/** 划转科目到流水类型中文标签的映射 */
export const subjectLabelMap: Record<SubjectKey, string> = {
  pay_deposit: '充值补充保证金',
  lock_deposit: '扣划冻结竞价保证金',
  release_deposit: '释放解冻保证金',
  refund_deposit: '提现退还保证金',
}

/** 划转科目到金额符号的映射，+ 入账，- 出账 */
export const subjectSignMap: Record<SubjectKey, '+' | '-'> = {
  pay_deposit: '+',
  lock_deposit: '-',
  release_deposit: '+',
  refund_deposit: '-',
}

/** 前端 subjectKey → 后端 DepositManualDTO.type 枚举映射 */
export const subjectToBackendType: Record<SubjectKey, string> = {
  pay_deposit: 'CHARGE',
  lock_deposit: 'FREEZE',
  release_deposit: 'UNFREEZE',
  refund_deposit: 'REFUND',
}

/** 人工记账表单默认值，弹窗打开或重置时使用 */
export const DEFAULT_MANUAL_FORM = {
  customerId: '',
  subjectKey: 'pay_deposit' as SubjectKey,
  amount: '10000',
  note: '',
}

/** 人工记账表单校验规则，绑定 manualFormRef */
export const manualRules: FormRules = {
  customerId: [{ required: true, message: '请选择核算主体', trigger: 'change' }],
  subjectKey: [{ required: true, message: '请选择划转科目', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入记账金额', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        const num = Number(value)
        if (!value || Number.isNaN(num) || num <= 0) {
          callback(new Error('请输入大于 0 的金额'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
  note: [{ required: true, message: '请填写往来事由与单据摘要', trigger: 'blur' }],
}
