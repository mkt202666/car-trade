/** 求购管理页面 composable */
import { computed, ref, watch } from 'vue'
import { SEED_PURCHASES } from './constants'
import { publisherTypeLabel, purchaseStatus } from './purchaseUtils'
import type { Purchase } from './types'

export type { Purchase, PurchasePublisherType, PurchaseStatus } from './types'
export { publisherTypeLabel, purchaseStatus } from './purchaseUtils'
export { SEED_PURCHASES } from './constants'

/**
 * 管理求购列表的搜索与分页。
 * 在 index.vue 中解构后绑定筛选栏、表格与底部分页器。
 * @returns 搜索 ref、分页计算属性及状态展示工具函数
 */
export function usePurchase() {
  /** 关键词搜索：匹配 ID、品牌车型、发布人、车行及类型标签 */
  const keyword = ref('')
  /** 当前页码，从 1 开始 */
  const currentPage = ref(1)
  /** 每页条数 */
  const pageSize = ref(10)

  /** 求购列表 mock 数据（从种子数据浅拷贝，不参与接口更新） */
  const purchases: Purchase[] = [...SEED_PURCHASES]

  /** 经关键词筛选后的求购列表 */
  const filteredPurchases = computed(() => {
    const q = keyword.value.trim().toLowerCase()
    if (!q) return purchases
    return purchases.filter((item) => {
      const haystack = [
        item.id,
        item.brand,
        item.trim,
        item.publisher.name,
        item.publisher.dealer,
        publisherTypeLabel(item.publisher.type),
      ]
        .join(' ')
        .toLowerCase()
      return haystack.includes(q)
    })
  })

  /** 当前页展示的求购记录切片 */
  const paginatedPurchases = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return filteredPurchases.value.slice(start, start + pageSize.value)
  })

  /** 分页展示区间起始序号（无数据时为 0） */
  const pageRangeStart = computed(() => {
    if (filteredPurchases.value.length === 0) return 0
    return (currentPage.value - 1) * pageSize.value + 1
  })

  /** 分页展示区间结束序号 */
  const pageRangeEnd = computed(() => {
    return Math.min(currentPage.value * pageSize.value, filteredPurchases.value.length)
  })

  /** 搜索关键词变化时重置到第一页 */
  watch(keyword, () => {
    currentPage.value = 1
  })

  /** 每页条数变化时重置到第一页 */
  watch(pageSize, () => {
    currentPage.value = 1
  })

  return {
    keyword,
    currentPage,
    pageSize,
    filteredPurchases,
    paginatedPurchases,
    pageRangeStart,
    pageRangeEnd,
    publisherTypeLabel,
    purchaseStatus,
  }
}
