/** 车源管理 composable */
import { computed, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { provinces, SEED_VEHICLES } from './constants'
import {
  computeConditionStats,
  sellerInitial,
  statusDetailLabel,
  statusLabel,
  vehicleStatus,
} from './vehicleUtils'
import type { Vehicle, VehicleChannel, VehicleStatus } from './types'

export type { Vehicle, VehicleChannel, VehicleDetail, VehicleSpecRow, VehicleStatus } from './types'
export { provinces, SEED_VEHICLES } from './constants'
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
  /** 当前分页页码，从 1 开始 */
  const currentPage = ref(1)
  /** 每页展示条数 */
  const pageSize = ref(20)
  /** 车源详情弹窗是否可见 */
  const detailVisible = ref(false)
  /** 当前选中查看详情的车源 */
  const selectedVehicle = ref<Vehicle | null>(null)

  /** 全量车源列表（可本地增删改） */
  const vehicles = ref<Vehicle[]>([...SEED_VEHICLES])

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

  /** 当前页应展示的车源切片 */
  const paginatedVehicles = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return filteredVehicles.value.slice(start, start + pageSize.value)
  })

  /** 分页信息起始序号（无数据时为 0） */
  const pageRangeStart = computed(() =>
    filteredVehicles.value.length === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1,
  )

  /** 分页信息结束序号 */
  const pageRangeEnd = computed(() =>
    Math.min(currentPage.value * pageSize.value, filteredVehicles.value.length),
  )

  /** 筛选条件或每页条数变化时重置到第一页 */
  watch([keyword, channelFilter, statusFilter, provinceFilter, pageSize], () => {
    currentPage.value = 1
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
   * 下架在售车源，弹出确认框后将状态改为 disapproved
   * @param vehicle - 待下架的车源
   */
  function handleDelist(vehicle: Vehicle) {
    ElMessageBox.confirm(`确定下架车源 ${vehicle.id}？`, '下架确认', {
      confirmButtonText: '确认下架',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        vehicle.status = 'disapproved'
        ElMessage.success('车源已下架')
      })
      .catch(() => {})
  }

  /**
   * 从列表中软删除车源（本地移除，无二次确认）
   * @param vehicle - 待删除的车源
   */
  function handleDelete(vehicle: Vehicle) {
    vehicles.value = vehicles.value.filter((v) => v.id !== vehicle.id)
    ElMessage.success('车源已删除')
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
  }
}
