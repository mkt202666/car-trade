/** 交易管理页面 composable */
import { computed, ref, watch } from 'vue'
import { CITY_MAP, PROVINCES, TRANSACTION_ORDERS } from './constants'
import { orderStatus } from './transactionUtils'
import type { TransactionOrder } from './types'

export type { OrderStatusKey, TransactionOrder } from './types'
export { orderStatus } from './transactionUtils'
export { PROVINCES, TRANSACTION_ORDERS } from './constants'

/**
 * 管理交易列表的筛选、分页与详情抽屉状态。
 * 在 index.vue 中解构后绑定筛选栏、订单卡片列表与 TransactionOrderDrawer。
 * @returns 筛选 ref、分页计算属性、抽屉状态及 openOrderDetail 方法
 */
export function useTransactions() {
  /** 关键词搜索：匹配订单号、品牌、车型、买卖双方 */
  const keyword = ref('')
  /** 履约状态筛选，'all' 表示不限 */
  const statusFilter = ref('all')
  /** 买家省份筛选，'all' 表示不限 */
  const provinceFilter = ref('all')
  /** 买家城市筛选，'all' 表示不限；省份为 all 时禁用 */
  const cityFilter = ref('all')
  /** 存证日期范围，格式 [起始, 截止] YYYY-MM-DD */
  const dateRange = ref<[string, string] | null>(null)
  /** 当前页码，从 1 开始 */
  const currentPage = ref(1)
  /** 每页条数 */
  const pageSize = ref(20)
  /** 详情抽屉是否可见 */
  const drawerVisible = ref(false)
  /** 当前打开详情的订单 ID，抽屉关闭后置 null */
  const selectedOrderId = ref<string | null>(null)

  /** 全量交易订单 mock 数据（静态引用，不参与响应式更新） */
  const orders: TransactionOrder[] = TRANSACTION_ORDERS

  /** 当前省份下可选城市列表；省份为 all 时返回空数组 */
  const availableCities = computed(() => {
    if (provinceFilter.value === 'all') return []
    return CITY_MAP[provinceFilter.value] ?? []
  })

  /** 省份变更时重置城市筛选，避免残留无效城市 */
  watch(provinceFilter, () => {
    cityFilter.value = 'all'
  })

  /** 任一筛选条件变化时重置到第一页 */
  watch([keyword, statusFilter, provinceFilter, cityFilter, dateRange], () => {
    currentPage.value = 1
  })

  /** 经关键词、状态、地域、日期筛选后的订单列表 */
  const filteredOrders = computed(() => {
    const q = keyword.value.trim().toLowerCase()

    return orders.filter((order) => {
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

  /** 当前页展示的订单切片 */
  const paginatedOrders = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return filteredOrders.value.slice(start, start + pageSize.value)
  })

  /** 分页展示区间起始序号（无数据时为 0） */
  const pageRangeStart = computed(() => {
    if (filteredOrders.value.length === 0) return 0
    return (currentPage.value - 1) * pageSize.value + 1
  })

  /** 分页展示区间结束序号 */
  const pageRangeEnd = computed(() => {
    return Math.min(currentPage.value * pageSize.value, filteredOrders.value.length)
  })

  /**
   * 打开指定订单的全景详情抽屉。
   * @param order 被点击的交易订单行数据
   */
  function openOrderDetail(order: TransactionOrder) {
    selectedOrderId.value = order.id
    drawerVisible.value = true
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
  }
}
