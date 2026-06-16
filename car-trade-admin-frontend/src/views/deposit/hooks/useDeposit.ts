/** 保证金现金流 composable */

import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { usePagination } from '../../../composables/usePagination'
import { getDepositAccounts, getDepositRecords, getDepositSummary, createJournalEntry } from '../../../api/deposits'
import { downloadFile } from '../../../utils/request'
import type { DepositAccount as APIAccount, DepositRecord } from '../../../api/deposits'
import { flowTypeTag, formatAccountLabel, formatAmountValue } from './depositUtils'
import {
  BuildingIcon,
  CreditCardIcon,
  DEFAULT_MANUAL_FORM,
  manualRules,
  ShieldCheckIcon,
  subjectLabelMap,
  subjectOptions,
  subjectSignMap,
  subjectToFlowType,
} from './constants'
import type { DepositAccount, DepositFlow, FlowTypeKey, SubjectKey } from './types'

export type { DepositAccount, DepositFlow, FlowTypeKey, SubjectKey } from './types'
export { flowTypeTag, formatAccountLabel, formatAmountValue } from './depositUtils'
export {
  BuildingIcon,
  CreditCardIcon,
  DEFAULT_MANUAL_FORM,
  manualRules,
  ShieldCheckIcon,
  subjectLabelMap,
  subjectOptions,
  subjectSignMap,
  subjectToFlowType,
} from './constants'

/** 管理流水筛选分页与人工调配记账 */
export function useDeposit() {
  const keyword = ref('')
  const typeFilter = ref('all')
  const loading = ref(false)

  const flows = ref<DepositFlow[]>([])
  const depositAccounts = ref<DepositAccount[]>([])
  const summary = ref({ totalBalance: 0, totalFrozen: 0, totalAccounts: 0, todayIncome: 0, todayWithdraw: 0 })

  async function fetchData() {
    loading.value = true
    try {
      const [accountsRes, recordsRes, summaryRes] = await Promise.allSettled([
        getDepositAccounts({ page: 1, size: 100 }),
        getDepositRecords({ page: 1, size: 100 }),
        getDepositSummary(),
      ])

      if (accountsRes.status === 'fulfilled' && accountsRes.value?.data?.list) {
        depositAccounts.value = accountsRes.value.data.list.map((a: APIAccount) => ({
          id: `USR-${a.id}`,
          name: a.userName || '—',
          available: a.balance || 0,
        }))
      }

      if (recordsRes.status === 'fulfilled' && recordsRes.value?.data?.list) {
        flows.value = recordsRes.value.data.list.map((r: DepositRecord) => ({
          id: `TX-${r.id}`,
          time: r.createdAt || '—',
          customerName: r.userName || '—',
          customerId: `USR-${r.userId}`,
          typeKey: (r.type?.toLowerCase() || 'recharge') as FlowTypeKey,
          typeLabel: r.type === 'INCOME' ? '入账' : r.type === 'FREEZE' ? '冻结' : r.type === 'RELEASE' ? '解冻' : '退款',
          amountSign: r.amount >= 0 ? '+' as const : '-' as const,
          amountValue: Math.abs(r.amount).toLocaleString(),
          balance: r.balance?.toLocaleString() || '0',
          note: r.description || '',
        }))
      }

      if (summaryRes.status === 'fulfilled' && summaryRes.value?.data) {
        summary.value = summaryRes.value.data
      }
    } catch (e) {
      console.error('Failed to fetch deposit data:', e)
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    fetchData()
  })

  const summaryStats = computed(() => [
    { label: '总余额', value: summary.value.totalBalance, color: '#409eff', icon: ShieldCheckIcon, iconBg: 'amber' },
    { label: '总冻结', value: summary.value.totalFrozen, color: '#e6a23c', icon: CreditCardIcon, iconBg: 'emerald' },
    { label: '账户数', value: summary.value.totalAccounts, color: '#67c23a', icon: BuildingIcon, iconBg: 'indigo' },
    { label: '今日收入', value: summary.value.todayIncome, color: '#f56c6c', icon: CreditCardIcon, iconBg: 'blue' },
  ])

  const filteredFlows = computed(() => {
    const q = keyword.value.trim().toLowerCase()
    return flows.value.filter((f) => {
      if (typeFilter.value !== 'all' && f.typeKey !== typeFilter.value) return false
      if (!q) return true
      return (
        f.id.toLowerCase().includes(q)
        || f.customerName.toLowerCase().includes(q)
        || f.customerId.toLowerCase().includes(q)
      )
    })
  })

  const { currentPage, pageSize, paginatedItems, pageRangeStart, pageRangeEnd } = usePagination({
    source: filteredFlows,
    defaultPageSize: 20,
    resetOn: [keyword, typeFilter],
  })

  const manualDialogVisible = ref(false)
  const manualSubmitting = ref(false)
  const manualFormRef = ref<FormInstance>()
  const manualForm = reactive({ ...DEFAULT_MANUAL_FORM })

  function openManualDialog() {
    manualDialogVisible.value = true
  }

  function resetManualForm() {
    Object.assign(manualForm, DEFAULT_MANUAL_FORM)
    manualFormRef.value?.resetFields()
  }

  async function submitManualForm() {
    const valid = await manualFormRef.value?.validate().catch(() => false)
    if (!valid) return

    manualSubmitting.value = true
    try {
      const amount = subjectSignMap[manualForm.subjectKey as SubjectKey] === '-' ? -Number(manualForm.amount) : Number(manualForm.amount)
      await createJournalEntry({
        userId: parseInt(manualForm.customerId.replace('USR-', ''), 10) || 0,
        amount,
        type: manualForm.subjectKey,
        description: manualForm.note,
      })
      ElMessage.success('人工记账成功')
      manualDialogVisible.value = false
      await fetchData()
    } catch (e) {
      console.error('Failed to submit journal entry:', e)
    } finally {
      manualSubmitting.value = false
    }
  }

  function findAccount(id: string) {
    return depositAccounts.value.find((a) => a.id === id)
  }

  function formatAmountValue(value: number) {
    return value.toLocaleString()
  }

  /** 导出当前筛选条件下的保证金流水为 Excel */
  function handleExport() {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
    const params = new URLSearchParams()
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    if (typeFilter.value !== 'all') params.set('type', typeFilter.value)
    const qs = params.toString()
    const date = new Date().toISOString().slice(0, 10)
    downloadFile(`${baseUrl}/deposits/records/export${qs ? `?${qs}` : ''}`, `保证金流水_${date}.xlsx`)
  }

  return {
    keyword,
    typeFilter,
    loading,
    flows,
    depositAccounts,
    summaryStats,
    filteredFlows,
    currentPage,
    pageSize,
    paginatedFlows: paginatedItems,
    pageRangeStart,
    pageRangeEnd,
    manualDialogVisible,
    manualSubmitting,
    manualFormRef,
    manualForm,
    openManualDialog,
    resetManualForm,
    submitManualForm,
    fetchData,
    findAccount,
    formatAmountValue,
    handleExport,
    ShieldCheckIcon,
    CreditCardIcon,
    BuildingIcon,
    subjectOptions,
    manualRules,
    flowTypeTag,
    formatAccountLabel,
  }
}
