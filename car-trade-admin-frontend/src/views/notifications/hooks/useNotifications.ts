/** 通知管理 composable */

import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotifications, getNotificationStats, sendAnnouncement } from '../../../api/notifications'
import type { Notification } from '../../../api/notifications'
import { usePagination } from '../../../composables/usePagination'
import { TYPE_LABELS, TYPE_TAG_MAP, STATUS_TAG_MAP, STATUS_LABELS } from './constants'
import type { NotificationItem } from './types'

export type { NotificationItem } from './types'

/** 管理通知列表查询、筛选、分页及发送系统公告 */
export function useNotifications() {
  const keyword = ref('')
  const typeFilter = ref('')
  const statusFilter = ref('')
  const loading = ref(false)
  const statsLoading = ref(false)

  const notifications = ref<NotificationItem[]>([])
  const totalSent = ref(0)
  const totalFailed = ref(0)
  const totalRecords = ref(0)

  /** 将 API 返回的 Notification 映射为前端 NotificationItem */
  function mapToItem(n: Notification): NotificationItem {
    return {
      id: n.id,
      type: n.type,
      targetUserId: n.targetUserId,
      targetUserName: n.targetUserName || '—',
      title: n.title,
      content: n.content,
      targetType: n.targetType,
      targetId: n.targetId,
      status: n.status,
      createdAt: n.createdAt || '—',
    }
  }

  /** 拉取通知列表 */
  async function fetchNotifications() {
    loading.value = true
    try {
      const res = await getNotifications({
        page: currentPage.value,
        size: pageSize.value,
        type: typeFilter.value || undefined,
        status: statusFilter.value || undefined,
        keyword: keyword.value.trim() || undefined,
      })
      if (res?.data?.list) {
        notifications.value = res.data.list.map(mapToItem)
        totalRecords.value = res.data.total ?? res.data.list.length
      }
    } catch (e) {
      console.error('Failed to fetch notifications:', e)
    } finally {
      loading.value = false
    }
  }

  /** 拉取统计卡片数据 */
  async function fetchStats() {
    statsLoading.value = true
    try {
      const res = await getNotificationStats()
      if (res?.data) {
        totalSent.value = res.data.totalSent ?? 0
        totalFailed.value = res.data.totalFailed ?? 0
      }
    } catch (e) {
      console.error('Failed to fetch notification stats:', e)
    } finally {
      statsLoading.value = false
    }
  }

  onMounted(() => {
    fetchStats()
    fetchNotifications()
  })

  /** 统计卡片展示数据 */
  const summaryStats = computed(() => [
    { label: '已发送数', value: totalSent.value.toLocaleString() },
    { label: '失败数', value: totalFailed.value.toLocaleString() },
  ])

  /** 前端二次过滤（当后端未处理 keyword 时兜底） */
  const filteredNotifications = computed(() => {
    return notifications.value
  })

  const { currentPage, pageSize, paginatedItems, pageRangeStart, pageRangeEnd } = usePagination({
    source: filteredNotifications,
    defaultPageSize: 20,
    resetOn: [keyword, typeFilter, statusFilter],
  })

  // 当筛选条件或分页变化时重新拉取
  watch([typeFilter, statusFilter], () => {
    fetchNotifications()
  })

  watch([currentPage, pageSize], () => {
    fetchNotifications()
  })

  /** 搜索处理 */
  function handleSearch() {
    currentPage.value = 1
    fetchNotifications()
  }

  // ── 发送系统公告 ──

  const announcementDialogVisible = ref(false)
  const announcementSubmitting = ref(false)
  const announcementTitle = ref('')
  const announcementContent = ref('')

  function openAnnouncementDialog() {
    announcementDialogVisible.value = true
  }

  function resetAnnouncementForm() {
    announcementTitle.value = ''
    announcementContent.value = ''
  }

  /** 提交系统公告 */
  async function submitAnnouncement() {
    if (!announcementTitle.value.trim()) {
      ElMessage.warning('请输入公告标题')
      return
    }
    if (!announcementContent.value.trim()) {
      ElMessage.warning('请输入公告内容')
      return
    }

    announcementSubmitting.value = true
    try {
      const res = await sendAnnouncement({
        title: announcementTitle.value.trim(),
        content: announcementContent.value.trim(),
      })
      const sentCount = res?.data?.sentCount ?? 0
      ElMessage.success(`公告已发送，成功推送 ${sentCount} 人`)
      announcementDialogVisible.value = false
      resetAnnouncementForm()
      await Promise.all([fetchNotifications(), fetchStats()])
    } catch (e) {
      console.error('Failed to send announcement:', e)
    } finally {
      announcementSubmitting.value = false
    }
  }

  /** 获取通知类型的中文标签 */
  function getTypeLabel(type: string): string {
    return TYPE_LABELS[type] || type
  }

  /** 获取通知类型对应的 el-tag type */
  function getTypeTagType(type: string): string {
    return TYPE_TAG_MAP[type] || 'info'
  }

  /** 获取通知状态的中文标签 */
  function getStatusLabel(status: string): string {
    return STATUS_LABELS[status] || status
  }

  /** 获取通知状态对应的 el-tag type */
  function getStatusTagType(status: string): string {
    return STATUS_TAG_MAP[status] || 'info'
  }

  return {
    // 筛选
    keyword,
    typeFilter,
    statusFilter,
    loading,
    statsLoading,

    // 数据
    summaryStats,
    filteredNotifications,
    totalRecords,

    // 分页
    currentPage,
    pageSize,
    paginatedItems,
    pageRangeStart,
    pageRangeEnd,

    // 公告弹窗
    announcementDialogVisible,
    announcementSubmitting,
    announcementTitle,
    announcementContent,
    openAnnouncementDialog,
    resetAnnouncementForm,
    submitAnnouncement,

    // 操作
    handleSearch,
    fetchNotifications,

    // 格式化
    getTypeLabel,
    getTypeTagType,
    getStatusLabel,
    getStatusTagType,
  }
}
