/** 车行注册审核 composable — 调用后端 API */
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  auditCodeSuffix,
  auditDecisionLabel,
  auditStatusClass,
  auditStatusLabel,
  formatAuditDate,
} from './auditUtils'
import { NEW_DEALER_TYPE } from './constants'
import { getShopReviews, approveShopReview, rejectShopReview, type ShopReview } from '../../../api/shopReviews'
import type { AuditItem, AuditStatus } from './types'

export type { AiCheck, AuditApplicant, AuditItem, AuditStatus } from './types'
export {
  auditCodeSuffix,
  auditDecisionLabel,
  auditStatusClass,
  auditStatusLabel,
  formatAuditDate,
} from './auditUtils'
export { NEW_DEALER_TYPE } from './constants'

/** Map backend status string to frontend AuditStatus */
function mapStatus(raw: string): AuditStatus {
  if (raw === 'APPROVED' || raw === 'approved') return 'approved'
  if (raw === 'REJECTED' || raw === 'rejected') return 'rejected'
  return 'pending'
}

/** Convert backend ShopReview to frontend AuditItem */
function toAuditItem(r: ShopReview): AuditItem {
  const status = mapStatus(r.status)
  return {
    id: `SR-${r.id}`,
    name: r.shopName || '未知商户',
    type: NEW_DEALER_TYPE,
    contact: r.applicantPhone || '',
    certNumber: r.businessLicense || '',
    status,
    submittedAt: (r.createdAt || '').slice(0, 10),
    applicant: r.applicantName
      ? { name: r.applicantName, idNumber: '', idCardImage: r.idCardFront || '' }
      : undefined,
    creditCode: r.businessLicense || undefined,
    licenseImage: r.idCardFront || undefined,
    storefrontImage: r.idCardBack || undefined,
    auditNote: status === 'rejected' ? r.rejectReason || undefined : undefined,
  }
}

/** Extract numeric ID from prefixed string like "SR-123" */
function numericId(id: string): number {
  return Number(id.replace(/^SR-/, ''))
}

export function useDealerAudit() {
  const keyword = ref('')
  const statusFilter = ref<'all' | AuditStatus>('all')
  const selectedId = ref<string>('')
  const previewVisible = ref(false)
  const previewUrl = ref('')
  const rejectDialogVisible = ref(false)
  const rejectReason = ref('')
  const loading = ref(false)
  const actionLoading = ref(false)

  const audits = ref<AuditItem[]>([])

  const filteredAudits = computed(() => {
    const q = keyword.value.trim().toLowerCase()
    return audits.value.filter((item) => {
      const matchStatus = statusFilter.value === 'all' || item.status === statusFilter.value
      const matchKeyword =
        !q ||
        item.name.toLowerCase().includes(q) ||
        item.contact.includes(q) ||
        item.certNumber.toLowerCase().includes(q) ||
        item.id.toLowerCase().includes(q)
      return matchStatus && matchKeyword
    })
  })

  const selectedAudit = computed(() => audits.value.find((item) => item.id === selectedId.value) ?? null)

  const isNewDealerApplication = computed(
    () => selectedAudit.value?.type === NEW_DEALER_TYPE && !!selectedAudit.value.applicant,
  )

  async function fetchAudits() {
    loading.value = true
    try {
      // The API interceptor unwraps ApiResponse, so res IS PageResult<ShopReview> directly
      const res = (await getShopReviews({ page: 1, size: 200 })) as unknown as {
        list?: ShopReview[]
      }
      audits.value = (res.list || []).map(toAuditItem)
      if (!selectedId.value && audits.value.length) {
        selectedId.value = audits.value[0].id
      }
    } catch {
      ElMessage.error('加载审核列表失败')
    } finally {
      loading.value = false
    }
  }

  function selectAudit(id: string) {
    selectedId.value = id
  }

  function openRejectDialog() {
    rejectReason.value = ''
    rejectDialogVisible.value = true
  }

  function resetRejectForm() {
    rejectReason.value = ''
  }

  async function confirmReject() {
    const reason = rejectReason.value.trim()
    if (!reason || !selectedId.value) return

    const item = audits.value.find((a) => a.id === selectedId.value)
    if (!item) return

    actionLoading.value = true
    try {
      await rejectShopReview(numericId(item.id), reason)
      item.status = 'rejected'
      item.auditNote = reason
      rejectDialogVisible.value = false
      ElMessage.success('已驳回该注册申请')
    } catch {
      ElMessage.error('驳回操作失败，请重试')
    } finally {
      actionLoading.value = false
    }
  }

  async function approveAudit() {
    if (!selectedId.value) return

    const item = audits.value.find((a) => a.id === selectedId.value)
    if (!item) return

    actionLoading.value = true
    try {
      await approveShopReview(numericId(item.id))
      item.status = 'approved'
      item.auditNote = '人工审核通过，资质合规。'
      ElMessage.success('已准予通过')
    } catch {
      ElMessage.error('通过操作失败，请重试')
    } finally {
      actionLoading.value = false
    }
  }

  function previewImage(url: string) {
    previewUrl.value = url
    previewVisible.value = true
  }

  onMounted(fetchAudits)

  return {
    keyword,
    statusFilter,
    selectedId,
    previewVisible,
    previewUrl,
    rejectDialogVisible,
    rejectReason,
    loading,
    actionLoading,
    filteredAudits,
    selectedAudit,
    isNewDealerApplication,
    selectAudit,
    formatDate: formatAuditDate,
    statusLabel: auditStatusLabel,
    statusClass: auditStatusClass,
    auditCodeSuffix,
    decisionLabel: auditDecisionLabel,
    openRejectDialog,
    resetRejectForm,
    confirmReject,
    approveAudit,
    previewImage,
    refresh: fetchAudits,
  }
}
