/** 保证金现金流 composable */

import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { flowTypeTag, formatAccountLabel, formatAmountValue } from './depositUtils'
import {
  BuildingIcon,
  CreditCardIcon,
  DEFAULT_MANUAL_FORM,
  manualRules,
  SEED_DEPOSIT_ACCOUNTS,
  SEED_FLOWS,
  ShieldCheckIcon,
  subjectLabelMap,
  subjectOptions,
  subjectSignMap,
  subjectToFlowType,
  summaryStats,
} from './constants'
import type { SubjectKey } from './types'

export type { DepositAccount, DepositFlow, FlowTypeKey, SubjectKey } from './types'
export { flowTypeTag, formatAccountLabel, formatAmountValue } from './depositUtils'
export {
  BuildingIcon,
  CreditCardIcon,
  DEFAULT_MANUAL_FORM,
  manualRules,
  SEED_DEPOSIT_ACCOUNTS,
  SEED_FLOWS,
  ShieldCheckIcon,
  subjectLabelMap,
  subjectOptions,
  subjectSignMap,
  subjectToFlowType,
  summaryStats,
} from './constants'

/** 管理流水筛选分页与人工调配记账 */
export function useDeposit() {
  /** 搜索关键词，匹配流水 ID、客户姓名或客户 ID */
  const keyword = ref('')
  /** 流水类型筛选，all 表示不过滤 */
  const typeFilter = ref('all')
  /** 当前页码，从 1 开始 */
  const currentPage = ref(1)
  /** 每页显示条数 */
  const pageSize = ref(10)

  /** 保证金流水列表数据 */
  const flows = ref([...SEED_FLOWS])
  /** 保证金核算主体账户列表，供人工记账选择 */
  const depositAccounts = ref([...SEED_DEPOSIT_ACCOUNTS])

  /** 人工记账弹窗可见性 */
  const manualDialogVisible = ref(false)
  /** 人工记账提交中的 loading 状态 */
  const manualSubmitting = ref(false)
  /** 人工记账表单实例引用，用于校验与重置 */
  const manualFormRef = ref<FormInstance>()
  /** 人工记账表单数据 */
  const manualForm = reactive({ ...DEFAULT_MANUAL_FORM })

  /** 经关键词与类型筛选后的流水列表 */
  const filteredFlows = computed(() => {
    const q = keyword.value.trim().toLowerCase()
    return flows.value.filter((flow) => {
      const matchType = typeFilter.value === 'all' || flow.typeKey === typeFilter.value
      const matchKeyword =
        !q ||
        flow.id.toLowerCase().includes(q) ||
        flow.customerName.toLowerCase().includes(q) ||
        flow.customerId.toLowerCase().includes(q)
      return matchType && matchKeyword
    })
  })

  /** 当前页展示的流水切片 */
  const paginatedFlows = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return filteredFlows.value.slice(start, start + pageSize.value)
  })

  /** 分页信息起始序号（无数据时为 0） */
  const pageRangeStart = computed(() =>
    filteredFlows.value.length === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1,
  )

  /** 分页信息结束序号 */
  const pageRangeEnd = computed(() =>
    Math.min(currentPage.value * pageSize.value, filteredFlows.value.length),
  )

  watch([keyword, typeFilter, pageSize], () => {
    currentPage.value = 1
  })

  /**
   * 根据 ID 查找核算主体账户
   * @param id - 用户/主体 ID
   * @returns 匹配的账户对象，未找到时 undefined
   */
  function findAccount(id: string) {
    return depositAccounts.value.find((a) => a.id === id)
  }

  /** 打开人工记账弹窗 */
  function openManualDialog() {
    manualDialogVisible.value = true
  }

  /** 重置人工记账表单至默认值并清除校验提示 */
  function resetManualForm() {
    manualForm.customerId = DEFAULT_MANUAL_FORM.customerId
    manualForm.subjectKey = DEFAULT_MANUAL_FORM.subjectKey
    manualForm.amount = DEFAULT_MANUAL_FORM.amount
    manualForm.note = DEFAULT_MANUAL_FORM.note
    manualFormRef.value?.clearValidate()
  }

  /** 校验并提交人工记账，更新账户余额并在流水列表头部插入新记录 */
  async function submitManualForm() {
    if (!manualFormRef.value) return
    const valid = await manualFormRef.value.validate().catch(() => false)
    if (!valid) return

    manualSubmitting.value = true
    await new Promise((r) => setTimeout(r, 400))

    const subjectKey = manualForm.subjectKey as SubjectKey
    const typeKey = subjectToFlowType[subjectKey]
    const sign = subjectSignMap[subjectKey]
    const amountAbs = Number(manualForm.amount)
    const signedAmount = sign === '+' ? amountAbs : -amountAbs
    const account = findAccount(manualForm.customerId)
    const customerName = account?.name ?? '未知主体'
    const prevBalance = account?.available ?? 0
    const newBalance = Math.max(0, prevBalance + signedAmount)

    if (account) {
      account.available = newBalance
    }

    flows.value.unshift({
      id: `TX-${Date.now().toString().slice(-4)}`,
      time: new Date().toLocaleString('zh-CN', { hour12: false }),
      customerName,
      customerId: manualForm.customerId,
      typeKey,
      typeLabel: subjectLabelMap[subjectKey],
      amountSign: sign,
      amountValue: formatAmountValue(amountAbs),
      balance: formatAmountValue(newBalance),
      note: manualForm.note,
    })

    manualSubmitting.value = false
    manualDialogVisible.value = false
    ElMessage.success('人工调配记账已提交')
  }

  return {
    ShieldCheckIcon,
    CreditCardIcon,
    BuildingIcon,
    summaryStats,
    keyword,
    typeFilter,
    currentPage,
    pageSize,
    flows,
    depositAccounts,
    subjectOptions,
    manualDialogVisible,
    manualSubmitting,
    manualFormRef,
    manualForm,
    manualRules,
    filteredFlows,
    paginatedFlows,
    pageRangeStart,
    pageRangeEnd,
    flowTypeTag,
    formatAccountLabel,
    formatAmountValue,
    findAccount,
    openManualDialog,
    resetManualForm,
    submitManualForm,
  }
}
