/** 车源管理 composable */
import { computed, onMounted, ref } from 'vue'
import { usePagination } from '../../../composables/usePagination'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCars, updateCarStatus, recommendCar } from '../../../api/cars'
import { downloadFile } from '../../../utils/request'
import type { Car } from '../../../api/cars'
import { provinces } from './constants'
import {
  computeConditionStats,
  sellerInitial,
  statusDetailLabel,
  statusLabel,
  vehicleStatus,
} from './vehicleUtils'
import type { Vehicle, VehicleChannel, VehicleStatus } from './types'

export type { Vehicle, VehicleChannel, VehicleDetail, VehicleSpecRow, VehicleStatus } from './types'
export { provinces } from './constants'
export {
  computeConditionStats,
  sellerInitial,
  statusDetailLabel,
  statusLabel,
  vehicleStatus,
} from './vehicleUtils'

/** 管理车源列表筛选、分页、详情弹窗与下架/删除操作 */
export function useVehicles() {
  /** 搜索关键词，匹配品牌、车系或车源 ID */
  const keyword = ref('')
  /** 货源渠道筛选，「all」表示不限 */
  const channelFilter = ref<'all' | VehicleChannel>('all')
  /** 上架状态筛选，「all」表示不限 */
  const statusFilter = ref<'all' | VehicleStatus>('all')
  /** 省份筛选，「all」表示不限 */
  const provinceFilter = ref('all')
  /** 车源详情弹窗是否可见 */
  const detailVisible = ref(false)
  /** 当前选中查看详情的车源 */
  const selectedVehicle = ref<Vehicle | null>(null)
  const loading = ref(false)

  /** 全量车源列表 */
  const vehicles = ref<Vehicle[]>([])

  /** 从 API 获取车源数据 */
  async function fetchVehicles() {
    loading.value = true
    try {
      const res = await getCars({ page: 1, size: 100 })
      if (res?.data?.list) {
        vehicles.value = res.data.list.map((car: Car) => ({
          id: `CAR-${car.id}`,
          brand: `品牌${car.brandId || ''}`,
          model: car.title || '—',
          energyType: car.energyType === 'PURE_ELECTRIC' ? '纯电' : car.energyType === 'HYBRID' ? '混动' : '燃油',
          registerDate: car.createdAt?.split('T')[0] || '—',
          mileage: car.mileage ? `${(car.mileage / 10000).toFixed(1)}w 公里` : '—',
          region: car.cityName || '—',
          province: car.cityName || '—',
          channel: 'Dealer' as VehicleChannel,
          seller: { name: car.sellerName || '—', nickname: car.sellerName || '—', type: '个人车商' },
          price: car.price ? (car.price / 10000).toFixed(1) : '0',
          guidePrice: car.price ? (car.price / 10000).toFixed(1) : '0',
          status: (car.status?.toLowerCase() || 'listed') as VehicleStatus,
          detail: {
            description: car.title || '',
            mileageKm: car.mileage ? `${car.mileage}公里` : '—',
            emissionStandard: '—',
            displacement: car.energyType || '—',
            colors: '—',
            transmission: '—',
            manufacturer: '—',
            vehicleType: '—',
            newCarPrice: '—',
            usageNature: '非营运',
            insuranceExpiry: '—',
            sellerId: '',
            sellerPhone: car.sellerPhone || '—',
            sellerRole: '个人车商',
            specs: [],
          },
        }))
      }
    } catch (e) {
      console.error('Failed to fetch vehicles:', e)
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    fetchVehicles()
  })

  /** 经渠道、状态、省份与关键词过滤后的车源列表 */
  const filteredVehicles = computed(() => {
    const q = keyword.value.trim().toLowerCase()
    return vehicles.value.filter((v) => {
      if (channelFilter.value !== 'all' && v.channel !== channelFilter.value) return false
      if (statusFilter.value !== 'all' && v.status !== statusFilter.value) return false
      if (provinceFilter.value !== 'all' && v.province !== provinceFilter.value) return false
      if (!q) return true
      return (
        v.brand.toLowerCase().includes(q) ||
        v.model.toLowerCase().includes(q) ||
        v.id.toLowerCase().includes(q)
      )
    })
  })

  const { currentPage, pageSize, paginatedItems: paginatedVehicles, pageRangeStart, pageRangeEnd } =
    usePagination({
      source: filteredVehicles,
      defaultPageSize: 20,
      resetOn: [keyword, channelFilter, statusFilter, provinceFilter],
    })

  /** 选中车源的车况统计网格数据，供详情弹窗渲染 */
  const conditionStats = computed(() => computeConditionStats(selectedVehicle.value))

  /**
   * 点击表格行，打开该车源详情弹窗
   * @param row - 被点击的车源行数据
   */
  function handleRowClick(row: Vehicle) {
    selectedVehicle.value = row
    detailVisible.value = true
  }

  /** 关闭详情弹窗（不清空 selectedVehicle，由 dialog @closed 处理） */
  function closeDetail() {
    detailVisible.value = false
  }

  /**
   * 从本地车源 ID（如 CAR-1002）中提取后端数字 ID
   * @param vehicleId - 本地车源 ID
   * @returns 后端数字 ID，提取失败返回 null
   */
  function extractNumericId(vehicleId: string): number | null {
    const match = vehicleId.match(/^CAR-(\d+)$/)
    return match ? Number(match[1]) : null
  }

  /**
   * 下架在售车源，弹出确认框后调用 API 将状态改为 OFF_SHELF，成功后更新本地状态
   * @param vehicle - 待下架的车源
   */
  async function handleDelist(vehicle: Vehicle) {
    try {
      await ElMessageBox.confirm(`确定下架车源 ${vehicle.id}？`, '下架确认', {
        confirmButtonText: '确认下架',
        cancelButtonText: '取消',
        type: 'warning',
      })
    } catch {
      // 用户取消确认框
      return
    }

    const numericId = extractNumericId(vehicle.id)
    if (numericId === null) {
      ElMessage.error(`无效的车源 ID：${vehicle.id}`)
      return
    }

    try {
      await updateCarStatus(numericId, 'OFF_SHELF')
      vehicle.status = 'disapproved'
      ElMessage.success('车源已下架')
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '下架失败，请稍后重试'
      ElMessage.error(msg)
    }
  }

  /**
   * 软删除车源，调用 API 将状态改为 DELETED，成功后从本地列表移除
   * @param vehicle - 待删除的车源
   */
  async function handleDelete(vehicle: Vehicle) {
    const numericId = extractNumericId(vehicle.id)
    if (numericId === null) {
      ElMessage.error(`无效的车源 ID：${vehicle.id}`)
      return
    }

    try {
      await updateCarStatus(numericId, 'DELETED')
      vehicles.value = vehicles.value.filter((v) => v.id !== vehicle.id)
      ElMessage.success('车源已删除')
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '删除失败，请稍后重试'
      ElMessage.error(msg)
    }
  }

  /**
   * 切换车源推荐状态，调用 API 更新后刷新本地数据
   * @param vehicleId - 本地车源 ID（如 CAR-1002）
   * @param currentRecommended - 当前是否已推荐
   */
  async function handleRecommend(vehicleId: string, currentRecommended: boolean) {
    const numericId = extractNumericId(vehicleId)
    if (!numericId) {
      ElMessage.error('无效的车辆ID')
      return
    }
    try {
      await recommendCar(numericId, !currentRecommended)
      ElMessage.success(!currentRecommended ? '已设为推荐' : '已取消推荐')
      const vehicle = vehicles.value.find((v) => v.id === vehicleId)
      if (vehicle) {
        vehicle.recommended = !currentRecommended
      }
    } catch {
      ElMessage.error('操作失败')
    }
  }

  /** 导出当前筛选条件下的车辆列表为 Excel */
  function handleExport() {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
    const params = new URLSearchParams()
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    if (statusFilter.value !== 'all') params.set('status', statusFilter.value)
    if (channelFilter.value !== 'all') params.set('channel', channelFilter.value)
    if (provinceFilter.value !== 'all') params.set('province', provinceFilter.value)
    const qs = params.toString()
    const date = new Date().toISOString().slice(0, 10)
    downloadFile(`${baseUrl}/cars/export${qs ? `?${qs}` : ''}`, `车辆列表_${date}.xlsx`)
  }

  return {
    keyword,
    channelFilter,
    statusFilter,
    provinceFilter,
    currentPage,
    pageSize,
    detailVisible,
    selectedVehicle,
    provinces,
    vehicles,
    filteredVehicles,
    paginatedVehicles,
    pageRangeStart,
    pageRangeEnd,
    conditionStats,
    handleRowClick,
    closeDetail,
    statusDetailLabel,
    sellerInitial,
    statusLabel,
    vehicleStatus,
    handleDelist,
    handleDelete,
    handleRecommend,
    handleExport,
  }
}
