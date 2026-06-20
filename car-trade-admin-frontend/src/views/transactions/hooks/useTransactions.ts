/** 交易管理页面 composable */
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { usePagination } from '../../../composables/usePagination'
import { getOrders, confirmOrder, cancelOrder } from '../../../api/orders'
import { downloadFile } from '../../../utils/request'
import type { Order } from '../../../api/orders'
import { CITY_MAP, PROVINCES } from './constants'
import { orderStatus } from './transactionUtils'
import type { TransactionOrder, OrderStatusKey } from './types'

export type { OrderStatusKey, TransactionOrder } from './types'
export { orderStatus } from './transactionUtils'
export { PROVINCES } from './constants'

/**
 * 管理交易列表的筛选、分页与详情抽屉状态。
 */
export function useTransactions() {
  const keyword = ref('')
  const statusFilter = ref('all')
  const provinceFilter = ref('all')
  const cityFilter = ref('all')
  const dateRange = ref<[string, string] | null>(null)
  const drawerVisible = ref(false)
  const selectedOrderId = ref<string | null>(null)
  const loading = ref(false)
  const orders = ref<TransactionOrder[]>([])

  async function fetchOrders() {
    loading.value = true
    try {
      const res = await getOrders({ page: 1, size: 100 })
      if (res?.data?.list) {
        orders.value = res.data.list.map((order: Order) => ({
          id: order.id || '—',
          brand: order.carTitle?.split(' ')[0] || '—',
          model: order.carTitle || '—',
          location: '—',
          province: '—',
          city: '—',
          buyer: order.buyerName || '—',
          seller: order.sellerName || '—',
          statusKey: (order.status?.toLowerCase() || 'submitted') as OrderStatusKey,
          statusLabel: order.status === 'COMPLETED' ? '交易完成' : order.status === 'PENDING_CONFIRM' ? '待确认' : order.status === 'IN_TRANSIT' ? '运输中' : order.status === 'DISPUTE' ? '争议中' : order.status === 'CANCELLED' ? '已取消' : order.status || '—',
          amount: order.totalPrice ? `¥${order.totalPrice.toLocaleString()}` : '¥0',
          evidenceDate: order.createdAt?.split('T')[0] || '',
        }))
      }
    } catch (e) {
      console.error('Failed to fetch orders:', e)
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    fetchOrders()
  })

  const availableCities = computed(() => {
    if (provinceFilter.value === 'all') return []
    return CITY_MAP[provinceFilter.value] ?? []
  })

  watch(provinceFilter, () => {
    cityFilter.value = 'all'
  })

  const filteredOrders = computed(() => {
    const q = keyword.value.trim().toLowerCase()

    return orders.value.filter((order) => {
      if (statusFilter.value !== 'all' && order.statusKey !== statusFilter.value) return false
      if (provinceFilter.value !== 'all' && order.province !== provinceFilter.value) return false
      if (cityFilter.value !== 'all' && order.city !== cityFilter.value) return false
      if (dateRange.value) {
        const [start, end] = dateRange.value
        if (start && order.evidenceDate < start) return false
        if (end && order.evidenceDate > end) return false
      }

      if (!q) return true

      return (
        order.id.toLowerCase().includes(q)
        || order.brand.toLowerCase().includes(q)
        || order.model.toLowerCase().includes(q)
        || order.buyer.toLowerCase().includes(q)
        || order.seller.toLowerCase().includes(q)
      )
    })
  })

  const { currentPage, pageSize, paginatedItems: paginatedOrders, pageRangeStart, pageRangeEnd } =
    usePagination({
      source: filteredOrders,
      defaultPageSize: 20,
      resetOn: [keyword, statusFilter, provinceFilter, cityFilter, dateRange],
    })

  function openOrderDetail(order: TransactionOrder) {
    selectedOrderId.value = order.id
    drawerVisible.value = true
  }

  /**
   * 确认订单 — 调用 PUT /orders/{id}/confirm
   * 成功后刷新列表并弹出成功提示。
   */
  async function handleConfirmOrder(orderId: string) {
    try {
      await confirmOrder(orderId)
      ElMessage.success(`订单 ${orderId} 已确认`)
      await fetchOrders()
    } catch (e) {
      console.error('Failed to confirm order:', e)
    }
  }

  /**
   * 管理员强制取消订单 — 调用 PUT /orders/{id}/cancel
   * @param orderId 订单 ID
   * @param reason 取消原因（运营干预备注）
   */
  async function handleCancelOrder(orderId: string, reason: string) {
    if (!reason.trim()) {
      ElMessage.warning('请输入取消原因')
      return
    }
    try {
      await cancelOrder(orderId, reason)
      ElMessage.success(`订单 ${orderId} 已取消`)
      await fetchOrders()
    } catch (e) {
      console.error('Failed to cancel order:', e)
    }
  }

  /** 导出当前筛选条件下的交易列表为 Excel */
  function handleExport() {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1/admin'
    const params = new URLSearchParams()
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    if (statusFilter.value !== 'all') params.set('status', statusFilter.value)
    if (provinceFilter.value !== 'all') params.set('province', provinceFilter.value)
    if (cityFilter.value !== 'all') params.set('city', cityFilter.value)
    if (dateRange.value) {
      const [start, end] = dateRange.value
      if (start) params.set('startDate', start)
      if (end) params.set('endDate', end)
    }
    const qs = params.toString()
    const date = new Date().toISOString().slice(0, 10)
    downloadFile(`${baseUrl}/orders/export${qs ? `?${qs}` : ''}`, `交易列表_${date}.xlsx`)
  }

  return {
    keyword,
    statusFilter,
    provinceFilter,
    cityFilter,
    dateRange,
    currentPage,
    pageSize,
    drawerVisible,
    selectedOrderId,
    provinces: PROVINCES,
    availableCities,
    filteredOrders,
    paginatedOrders,
    pageRangeStart,
    pageRangeEnd,
    orderStatus,
    openOrderDetail,
    handleConfirmOrder,
    handleCancelOrder,
    handleExport,
    loading,
    fetchOrders,
  }
}
