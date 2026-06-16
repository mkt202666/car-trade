/** 纠纷管理 composable — 调用后端 API */
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDisputes, handleDispute, type Dispute } from '../../../api/disputes'
import { downloadFile } from '../../../utils/request'
import { STATUS_LABELS } from './constants'
import type { DisputeItem, DisputeStatus } from './types'

/** Map backend status string to frontend DisputeStatus */
function mapStatus(raw: string): DisputeStatus {
  if (raw === 'OPEN') return 'OPEN'
  if (raw === 'IN_PROGRESS') return 'IN_PROGRESS'
  if (raw === 'RESOLVED') return 'RESOLVED'
  if (raw === 'REJECTED') return 'REJECTED'
  return 'OPEN'
}

/** Convert backend Dispute to frontend DisputeItem */
function toDisputeItem(d: Dispute): DisputeItem {
  return {
    id: d.id,
    orderId: d.orderId,
    orderTitle: d.orderTitle,
    initiatorName: d.initiatorName,
    initiatorPhone: d.initiatorPhone,
    reason: d.reason,
    status: mapStatus(d.status),
    createdAt: d.createdAt,
  }
}

/** Format ISO datetime string to local readable format */
function formatDateTime(value: string): string {
  if (!value) return ''
  const date = new Date(value)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}

export function useDisputes() {
  const keyword = ref('')
  const statusFilter = ref('')
  const currentPage = ref(1)
  const pageSize = ref(20)
  const loading = ref(false)
  const actionLoading = ref(false)

  const disputes = ref<DisputeItem[]>([])
  const total = ref(0)

  const filteredDisputes = computed(() => disputes.value)

  const pageRangeStart = computed(() => (currentPage.value - 1) * pageSize.value + 1)
  const pageRangeEnd = computed(() => Math.min(currentPage.value * pageSize.value, total.value))

  async function fetchDisputes() {
    loading.value = true
    try {
      const res = (await getDisputes({
        page: currentPage.value,
        size: pageSize.value,
        status: statusFilter.value || undefined,
        keyword: keyword.value.trim() || undefined,
      })) as unknown as { list?: Dispute[]; total?: number }

      disputes.value = (res.list || []).map(toDisputeItem)
      total.value = res.total ?? 0
    } catch {
      ElMessage.error('加载纠纷列表失败')
    } finally {
      loading.value = false
    }
  }

  function handlePageChange(page: number) {
    currentPage.value = page
    fetchDisputes()
  }

  function handleSizeChange(size: number) {
    pageSize.value = size
    currentPage.value = 1
    fetchDisputes()
  }

  function handleSearch() {
    currentPage.value = 1
    fetchDisputes()
  }

  async function handleApprove(id: number) {
    try {
      const { value } = await ElMessageBox.prompt('请输入支持处理的结果说明', '支持纠纷', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入处理结果...',
        inputValidator: (val) => {
          if (!val || !val.trim()) return '请输入处理结果'
          return true
        },
      })

      actionLoading.value = true
      await handleDispute(id, { action: 'APPROVE', result: value.trim() })
      ElMessage.success('已支持该纠纷')
      fetchDisputes()
    } catch (err) {
      if (err !== 'cancel' && (err as Error)?.message !== 'cancel') {
        ElMessage.error('操作失败，请重试')
      }
    } finally {
      actionLoading.value = false
    }
  }

  async function handleReject(id: number) {
    try {
      const { value } = await ElMessageBox.prompt('请输入驳回的理由说明', '驳回纠纷', {
        confirmButtonText: '确认驳回',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入驳回理由...',
        inputValidator: (val) => {
          if (!val || !val.trim()) return '请输入驳回理由'
          return true
        },
        confirmButtonClass: 'el-button--danger',
      })

      actionLoading.value = true
      await handleDispute(id, { action: 'REJECT', result: value.trim() })
      ElMessage.success('已驳回该纠纷')
      fetchDisputes()
    } catch (err) {
      if (err !== 'cancel' && (err as Error)?.message !== 'cancel') {
        ElMessage.error('操作失败，请重试')
      }
    } finally {
      actionLoading.value = false
    }
  }

  async function handleNegotiate(id: number) {
    try {
      const { value } = await ElMessageBox.prompt('请输入协商建议内容', '协商解决', {
        confirmButtonText: '发送',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入协商建议...',
        inputValidator: (val) => {
          if (!val || !val.trim()) return '请输入协商建议'
          return true
        },
      })

      actionLoading.value = true
      await handleDispute(id, { action: 'NEGOTIATE', result: value.trim() })
      ElMessage.success('已发起协商')
      fetchDisputes()
    } catch (err) {
      if (err !== 'cancel' && (err as Error)?.message !== 'cancel') {
        ElMessage.error('操作失败，请重试')
      }
    } finally {
      actionLoading.value = false
    }
  }

  function handleExport() {
    const params = new URLSearchParams()
    if (statusFilter.value) params.set('status', statusFilter.value)
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    const query = params.toString()
    const url = `/api/v1/disputes/export${query ? `?${query}` : ''}`
    downloadFile(url, `纠纷列表_${new Date().toISOString().slice(0, 10)}.xlsx`)
      .catch(() => ElMessage.error('导出失败'))
  }

  function statusLabel(status: DisputeStatus): string {
    return STATUS_LABELS[status] || status
  }

  watch([statusFilter], () => {
    handleSearch()
  })

  onMounted(fetchDisputes)

  return {
    keyword,
    statusFilter,
    currentPage,
    pageSize,
    loading,
    actionLoading,
    filteredDisputes,
    total,
    pageRangeStart,
    pageRangeEnd,
    handlePageChange,
    handleSizeChange,
    handleSearch,
    handleApprove,
    handleReject,
    handleNegotiate,
    handleExport,
    statusLabel,
    formatDateTime,
    refresh: fetchDisputes,
  }
}
