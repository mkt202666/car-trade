/** 求购管理页面 composable */
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { usePagination } from '../../../composables/usePagination'
import {
  getPurchaseRequests,
  getPurchaseRequest,
  matchPurchaseRequest,
  closePurchaseRequest,
} from '../../../api/purchaseRequests'
import type { PurchaseRequest } from '../../../api/purchaseRequests'
import { publisherTypeLabel, purchaseStatus } from './purchaseUtils'
import type { Purchase, PurchaseStatus } from './types'

export type { Purchase, PurchasePublisherType, PurchaseStatus } from './types'
export { publisherTypeLabel, purchaseStatus } from './purchaseUtils'

/**
 * 将后端 PurchaseRequest 映射为前端 Purchase 展示结构。
 */
function mapPurchaseRequest(pr: PurchaseRequest): Purchase {
  return {
    id: `P${String(pr.id).padStart(6, '0')}`,
    brand: pr.brandName || '—',
    trim: pr.modelName || '',
    publisher: {
      name: pr.userName || '—',
      dealer: '独立用户',
      type: 'individual' as const,
    },
    yearRequirement: pr.yearMin ? `${pr.yearMin}年及以后` : '不限',
    mileageRequirement: '不限',
    colorRequirement: pr.energyType || '不限',
    price: pr.maxPrice ? `≤${(pr.maxPrice / 10000).toFixed(1)}万` : '面议',
    status: (pr.status === 'ACTIVE' ? '待处理' : pr.status === 'FULFILLED' ? '已成交' : '已关闭') as PurchaseStatus,
  }
}

/**
 * 管理求购列表的搜索与分页。
 */
export function usePurchase() {
  const keyword = ref('')
  const loading = ref(false)
  const purchases = ref<Purchase[]>([])

  /** 当前选中的求购详情（可供详情弹窗/抽屉使用） */
  const selectedPurchase = ref<Purchase | null>(null)
  /** 详情加载中状态 */
  const detailLoading = ref(false)
  /** 匹配操作中状态 */
  const matchLoading = ref(false)

  async function fetchPurchases() {
    loading.value = true
    try {
      // The API interceptor unwraps ApiResponse, so res IS PageResult<PurchaseRequest> directly
      const res = (await getPurchaseRequests({ page: 1, size: 100 })) as unknown as {
        list?: PurchaseRequest[]
      }
      if (res?.list) {
        purchases.value = res.list.map(mapPurchaseRequest)
      }
    } catch (e) {
      console.error('Failed to fetch purchases:', e)
      ElMessage.error('获取求购列表失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取单条求购详情，更新 selectedPurchase。
   * @param id 求购记录 ID（数字）
   */
  async function fetchPurchaseDetail(id: number) {
    detailLoading.value = true
    try {
      const res = (await getPurchaseRequest(id)) as unknown as PurchaseRequest
      selectedPurchase.value = mapPurchaseRequest(res)
    } catch (e) {
      console.error('Failed to fetch purchase detail:', e)
      ElMessage.error('获取求购详情失败')
    } finally {
      detailLoading.value = false
    }
  }

  /**
   * 对指定求购执行 AI 智能匹配，成功后刷新列表。
   * @param id 求购记录 ID（数字）
   */
  async function handleMatchPurchase(id: number) {
    matchLoading.value = true
    try {
      await matchPurchaseRequest(id)
      ElMessage.success('匹配请求已提交')
      await fetchPurchases()
    } catch (e) {
      console.error('Failed to match purchase:', e)
      ElMessage.error('求购匹配操作失败')
    } finally {
      matchLoading.value = false
    }
  }

  /**
   * 关闭指定求购需求，弹出确认框后调用 API，成功后刷新列表。
   * @param id 求购记录 ID（数字）
   */
  async function handleClosePurchase(id: number) {
    try {
      await ElMessageBox.confirm('确定要关闭这条求购需求吗？', '确认关闭', {
        confirmButtonText: '关闭',
        cancelButtonText: '取消',
        type: 'warning',
      })
    } catch {
      // 用户取消确认框
      return
    }

    try {
      await closePurchaseRequest(id, '管理员关闭')
      ElMessage.success('已关闭')
      await fetchPurchases()
    } catch (err) {
      if (err !== 'cancel') ElMessage.error('关闭失败')
    }
  }

  onMounted(() => {
    fetchPurchases()
  })

  const filteredPurchases = computed(() => {
    const q = keyword.value.trim().toLowerCase()
    if (!q) return purchases.value
    return purchases.value.filter((item) => {
      const haystack = [
        item.id,
        item.brand,
        item.trim,
        item.publisher.name,
      ]
        .join(' ')
        .toLowerCase()
      return haystack.includes(q)
    })
  })

  const { currentPage, pageSize, paginatedItems, pageRangeStart, pageRangeEnd } = usePagination({
    source: filteredPurchases,
    defaultPageSize: 20,
    resetOn: [keyword],
  })

  return {
    keyword,
    currentPage,
    pageSize,
    filteredPurchases,
    paginatedPurchases: paginatedItems,
    pageRangeStart,
    pageRangeEnd,
    publisherTypeLabel,
    purchaseStatus,
    loading,
    selectedPurchase,
    detailLoading,
    matchLoading,
    fetchPurchases,
    fetchPurchaseDetail,
    handleMatchPurchase,
    handleClosePurchase,
  }
}
