/** 车行注册审核 composable */
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  auditCodeSuffix,
  auditDecisionLabel,
  auditStatusClass,
  auditStatusLabel,
  formatAuditDate,
} from './auditUtils'
import { DEFAULT_SELECTED_AUDIT_ID, NEW_DEALER_TYPE, SEED_AUDITS } from './constants'
import type { AuditItem, AuditStatus } from './types'

export type { AiCheck, AuditApplicant, AuditItem, AuditStatus } from './types'
export {
  auditCodeSuffix,
  auditDecisionLabel,
  auditStatusClass,
  auditStatusLabel,
  formatAuditDate,
} from './auditUtils'
export { DEFAULT_SELECTED_AUDIT_ID, NEW_DEALER_TYPE, SEED_AUDITS } from './constants'

/**
 * 管理车行注册审核页的列表筛选、选中项、通过/驳回操作及证照预览。
 * 在 index.vue 中解构后绑定搜索栏、表格、详情面板与弹窗。
 * @returns 响应式状态、计算属性与操作方法
 */
export function useDealerAudit() {
  /** 搜索关键词，双向绑定顶部搜索输入框，匹配名称/手机/证号/单号 */
  const keyword = ref('')
  /** 审核状态筛选项，双向绑定状态下拉框（all 表示不过滤） */
  const statusFilter = ref<'all' | AuditStatus>('all')
  /** 当前选中的审核单 ID，控制表格行高亮与右侧详情面板内容 */
  const selectedId = ref(DEFAULT_SELECTED_AUDIT_ID)
  /** 证照大图预览弹层是否可见，绑定 el-image-viewer v-if */
  const previewVisible = ref(false)
  /** 当前预览的图片 URL，传入 el-image-viewer 的 url-list */
  const previewUrl = ref('')
  /** 驳回原因弹窗是否可见，绑定 el-dialog v-model */
  const rejectDialogVisible = ref(false)
  /** 驳回原因输入内容，双向绑定弹窗内 textarea */
  const rejectReason = ref('')

  /** 全量审核列表（可变），通过/驳回操作会直接修改其中项的 status */
  const audits = ref<AuditItem[]>([...SEED_AUDITS])

  /** 经关键词与状态筛选后的列表，绑定左侧审核表格 tbody 的 v-for */
  const filteredAudits = computed(() => {
    const q = keyword.value.trim().toLowerCase() // 归一化搜索词，忽略大小写
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

  /** 当前选中审核单的完整对象，未选中时返回 null，驱动右侧详情面板 v-if */
  const selectedAudit = computed(() => audits.value.find((item) => item.id === selectedId.value) ?? null)

  /** 是否为含申请人信息的新车行创建申请，控制详情区申请人/证照扩展字段的展示 */
  const isNewDealerApplication = computed(
    () => selectedAudit.value?.type === NEW_DEALER_TYPE && !!selectedAudit.value.applicant,
  )

  /**
   * 选中指定审核单并在右侧展示详情。
   * @param id - 审核单 ID
   */
  function selectAudit(id: string) {
    selectedId.value = id
  }

  /**
   * 打开驳回弹窗并清空上次填写的驳回原因。
   * 在用户点击「驳回拒绝」按钮时调用。
   */
  function openRejectDialog() {
    rejectReason.value = ''
    rejectDialogVisible.value = true
  }

  /**
   * 重置驳回表单，在弹窗 closed 回调中调用以清理残留输入。
   */
  function resetRejectForm() {
    rejectReason.value = ''
  }

  /**
   * 确认驳回当前选中审核单，写入驳回原因并更新状态。
   * 原因为空或找不到选中项时静默返回。
   */
  function confirmReject() {
    const reason = rejectReason.value.trim()
    if (!reason || !selectedId.value) return

    const item = audits.value.find((audit) => audit.id === selectedId.value)
    if (!item) return

    item.status = 'rejected'
    item.auditNote = reason
    rejectDialogVisible.value = false
    ElMessage.success('已驳回该注册申请')
  }

  /**
   * 批准通过当前选中审核单，写入默认通过备注。
   * 无选中项时静默返回。
   */
  function approveAudit() {
    if (!selectedId.value) return

    const item = audits.value.find((audit) => audit.id === selectedId.value)
    if (!item) return

    item.status = 'approved'
    item.auditNote = '人工审核通过，资质合规。'
    ElMessage.success('已准予通过')
  }

  /**
   * 打开证照大图预览。
   * @param url - 图片资源地址，来自身份证/营业执照/门店照点击
   */
  function previewImage(url: string) {
    previewUrl.value = url
    previewVisible.value = true
  }

  return {
    keyword,
    statusFilter,
    selectedId,
    previewVisible,
    previewUrl,
    rejectDialogVisible,
    rejectReason,
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
  }
}
