/** 车型库维护 composable */
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, TableInstance, UploadFile } from 'element-plus'
import {
  BRANDS,
  DEFAULT_DETAIL_CONFIG,
  ENERGY_TYPES,
  EXPORT_COUNTRIES,
  formRules,
} from './constants'
import {
  buildDetailConfigFromEdit,
  buildMockModels,
  buildModelFromForm as buildModelFromFormData,
  createEmptyEditForm,
  createEmptyForm,
  extractFactoryParams,
  formatGuidePrice,
  formatLaunchDate,
  formatPriceFromWan,
  inferEnergyType,
  nextModelId as computeNextModelId,
  parseDetailConfig,
  parseLaunchDate,
  parsePriceToWan,
} from './modelUtils'
import type { EditForm, FactoryParams, ModelForm, VehicleModel } from './types'

export type { EditForm, FactoryParams, ModelForm, VehicleModel } from './types'
export {
  BRANDS,
  DEFAULT_DETAIL_CONFIG,
  ENERGY_TYPES,
  EXPORT_COUNTRIES,
  formRules,
} from './constants'
export {
  buildDetailConfigFromEdit,
  buildMockModels,
  buildModelFromForm,
  createEmptyEditForm,
  createEmptyForm,
  extractFactoryParams,
  formatGuidePrice,
  formatLaunchDate,
  formatPriceFromWan,
  inferEnergyType,
  nextModelId,
  parseDetailConfig,
  parseLaunchDate,
  parsePriceToWan,
} from './modelUtils'

/** 管理车型列表筛选、行内编辑、展开详情、新增弹窗与删除 */
export function useModels() {
  /** 车型表格实例，用于控制行展开/收起 */
  const tableRef = ref<TableInstance>()
  /** 新增车型弹窗表单实例，用于校验与清空 */
  const formRef = ref<FormInstance>()
  /** 全量车型列表（可本地增删改） */
  const models = ref<VehicleModel[]>(buildMockModels())
  /** 当前已展开详情行的 id 集合 */
  const expandedIds = ref(new Set<string>())
  /** 当前处于行内编辑模式的车型 id，null 表示无编辑 */
  const editingId = ref<string | null>(null)
  /** 行内编辑保存中的 loading 状态 */
  const savingEdit = ref(false)
  /** 新增车型弹窗是否可见 */
  const dialogVisible = ref(false)
  /** 新增表单提交中的 loading 状态 */
  const submitting = ref(false)

  /** 新增车型弹窗表单数据 */
  const form = reactive<ModelForm>(createEmptyForm())
  /** 行内编辑表单数据 */
  const editForm = reactive<EditForm>(createEmptyEditForm())

  /** 1 级品牌筛选，「all」表示不限 */
  const brandFilter = ref('all')
  /** 2 级车系筛选，依赖 brandFilter */
  const seriesFilter = ref('all')
  /** 3 级具体车型筛选，依赖 brandFilter 与 seriesFilter */
  const variantFilter = ref('all')
  /** 出口国家筛选，「all」表示不限 */
  const exportFilter = ref('all')
  /** 能源类型筛选，「all」表示不限 */
  const energyFilter = ref('all')
  /** 当前分页页码 */
  const currentPage = ref(1)
  /** 每页展示条数 */
  const pageSize = ref(20)

  /** 当前品牌下的车系选项（brandFilter 为 all 时为空） */
  const seriesOptions = computed(() => {
    if (brandFilter.value === 'all') return []
    const set = new Set<string>()
    models.value.forEach((item) => {
      if (item.brand === brandFilter.value) set.add(item.series)
    })
    return [...set].sort()
  })

  /** 当前品牌+车系下的具体车型选项 */
  const variantOptions = computed(() => {
    if (brandFilter.value === 'all' || seriesFilter.value === 'all') return []
    const set = new Set<string>()
    models.value.forEach((item) => {
      if (item.brand === brandFilter.value && item.series === seriesFilter.value) {
        set.add(item.variant)
      }
    })
    return [...set].sort()
  })

  /** 经品牌、车系、车型、出口与能源类型过滤后的列表 */
  const filteredModels = computed(() => {
    return models.value.filter((item) => {
      if (brandFilter.value !== 'all' && item.brand !== brandFilter.value) return false
      if (seriesFilter.value !== 'all' && item.series !== seriesFilter.value) return false
      if (variantFilter.value !== 'all' && item.variant !== variantFilter.value) return false
      if (exportFilter.value !== 'all' && !item.exportCountries.includes(exportFilter.value)) return false
      if (energyFilter.value !== 'all' && item.energyType !== energyFilter.value) return false
      return true
    })
  })

  /** 当前页应展示的车型切片 */
  const paginatedModels = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return filteredModels.value.slice(start, start + pageSize.value)
  })

  /** 分页信息起始序号 */
  const pageRangeStart = computed(() => {
    if (filteredModels.value.length === 0) return 0
    return (currentPage.value - 1) * pageSize.value + 1
  })

  /** 分页信息结束序号 */
  const pageRangeEnd = computed(() => {
    return Math.min(currentPage.value * pageSize.value, filteredModels.value.length)
  })

  /** 品牌变更时重置下级筛选与页码 */
  watch(brandFilter, () => {
    seriesFilter.value = 'all'
    variantFilter.value = 'all'
    currentPage.value = 1
  })

  /** 车系变更时重置车型筛选与页码 */
  watch(seriesFilter, () => {
    variantFilter.value = 'all'
    currentPage.value = 1
  })

  /** 其它筛选或每页条数变更时重置页码 */
  watch([exportFilter, energyFilter, variantFilter, pageSize], () => {
    currentPage.value = 1
  })

  /**
   * 判断指定行是否处于编辑模式
   * @param row - 表格行数据
   * @returns 该行 editingId 是否匹配
   */
  function isEditing(row: VehicleModel) {
    return editingId.value === row.id
  }

  /**
   * 为 el-table 提供行 class，编辑中行高亮
   * @param param.row - 当前行数据
   * @returns CSS 类名或空字符串
   */
  function rowClassName({ row }: { row: VehicleModel }) {
    return isEditing(row) ? 'models-table__row--editing' : ''
  }

  /**
   * 切换行的详情展开/收起（编辑其它行时禁止切换）
   * @param row - 目标车型行
   */
  function toggleDetail(row: VehicleModel) {
    if (editingId.value && editingId.value !== row.id) return
    tableRef.value?.toggleRowExpansion(row)
    if (expandedIds.value.has(row.id)) {
      expandedIds.value.delete(row.id)
    } else {
      expandedIds.value.add(row.id)
    }
  }

  /**
   * 收起指定行的展开面板
   * @param row - 目标车型行
   */
  function collapseRow(row: VehicleModel) {
    if (expandedIds.value.has(row.id)) {
      tableRef.value?.toggleRowExpansion(row)
      expandedIds.value.delete(row.id)
    }
  }

  /**
   * 切换车型启用/停用状态
   * @param row - 目标车型，直接修改 status 与 updatedAt
   */
  function toggleStatus(row: VehicleModel) {
    row.status = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    row.updatedAt = new Date().toLocaleDateString('zh-CN')
    ElMessage.success(row.status === 'ACTIVE' ? '已恢复启用' : '已停用此车型')
  }

  /**
   * 用行数据填充行内编辑表单
   * @param row - 待编辑的车型行
   */
  function fillEditForm(row: VehicleModel) {
    Object.assign(editForm, {
      brand: row.brand,
      series: row.series,
      variant: row.variant,
      launchMonth: parseLaunchDate(row.launchDate) || '',
      powerType: row.powerType,
      bodyType: row.bodyType,
      guidePriceWan: parsePriceToWan(row.price),
      exportCountries: [...row.exportCountries],
      manufacturer: row.detailConfig?.['厂商'] || `${row.brand}汽车`,
      dimensions: row.dimensions || '',
      factoryParams: extractFactoryParams(row.detailConfig),
    })
  }

  /**
   * 进入行内编辑模式，自动展开详情面板并填充表单
   * @param row - 待编辑的车型行
   */
  function startEdit(row: VehicleModel) {
    if (editingId.value && editingId.value !== row.id) {
      const prev = models.value.find((item) => item.id === editingId.value)
      if (prev) cancelEdit(prev, false)
    }

    editingId.value = row.id
    fillEditForm(row)

    if (!expandedIds.value.has(row.id)) {
      tableRef.value?.toggleRowExpansion(row)
      expandedIds.value.add(row.id)
    }
  }

  /**
   * 取消行内编辑，清空编辑表单并可选择收起展开行
   * @param row - 当前编辑的行
   * @param collapse - 是否同时收起展开面板，默认 true
   */
  function cancelEdit(row: VehicleModel, collapse = true) {
    editingId.value = null
    Object.assign(editForm, createEmptyEditForm())
    if (collapse) collapseRow(row)
  }

  /**
   * 保存行内编辑结果到 models 列表
   * @param row - 正在编辑的车型行（用于定位 id）
   */
  async function saveEdit(row: VehicleModel) {
    if (!editForm.brand.trim() || !editForm.series.trim() || !editForm.variant.trim()) {
      ElMessage.warning('请填写完整的车型谱系信息')
      return
    }

    savingEdit.value = true
    try {
      const detailConfig = buildDetailConfigFromEdit(editForm)
      const index = models.value.findIndex((item) => item.id === row.id)
      if (index === -1) return

      models.value[index] = {
        ...models.value[index],
        brand: editForm.brand.trim(),
        series: editForm.series.trim(),
        variant: editForm.variant.trim(),
        launchDate: formatLaunchDate(editForm.launchMonth),
        powerType: editForm.powerType.trim(),
        bodyType: editForm.bodyType.trim(),
        dimensions: editForm.dimensions.trim() || undefined,
        price: editForm.guidePriceWan ? formatPriceFromWan(editForm.guidePriceWan) : '-',
        exportCountries: [...editForm.exportCountries],
        energyType: inferEnergyType(editForm.powerType, detailConfig),
        detailConfig,
        updatedAt: new Date().toLocaleDateString('zh-CN'),
      }

      editingId.value = null
      Object.assign(editForm, createEmptyEditForm())
      ElMessage.success('车型修改已保存')
    } finally {
      savingEdit.value = false
    }
  }

  /** 重置新增表单为初始值并清除校验状态 */
  function resetForm() {
    Object.assign(form, createEmptyForm())
    formRef.value?.clearValidate()
  }

  /** 打开新增车型弹窗（先重置表单） */
  function openCreate() {
    resetForm()
    dialogVisible.value = true
  }

  /** PageHeader「新增基础车型」按钮回调 */
  function handleCreate() {
    openCreate()
  }

  /** 校验并提交新增表单，将新车型插入列表头部 */
  async function submitForm() {
    if (!formRef.value) return
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return

    submitting.value = true
    try {
      const payload: VehicleModel = {
        id: computeNextModelId(models.value),
        ...buildModelFromFormData(form),
      }
      models.value.unshift(payload)
      ElMessage.success('车型已新增')
      dialogVisible.value = false
    } finally {
      submitting.value = false
    }
  }

  /**
   * 删除车型，弹出二次确认
   * @param row - 待删除的车型行
   */
  async function handleDelete(row: VehicleModel) {
    try {
      await ElMessageBox.confirm(`确定删除车型「${row.variant}」吗？`, '删除确认', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消',
      })
      models.value = models.value.filter((item) => item.id !== row.id)
      if (editingId.value === row.id) {
        editingId.value = null
        expandedIds.value.delete(row.id)
      }
      ElMessage.success('删除成功')
    } catch {
      // cancelled
    }
  }

  /**
   * 附件上传占位处理（待接入实际上传接口）
   * @param file - Element Plus 上传组件返回的文件对象
   */
  function handleUpload(file: UploadFile) {
    ElMessage.success(`已选择文件：${file.name}，上传功能待接入`)
  }

  return {
    DEFAULT_DETAIL_CONFIG,
    BRANDS,
    EXPORT_COUNTRIES,
    ENERGY_TYPES,
    tableRef,
    formRef,
    models,
    expandedIds,
    editingId,
    savingEdit,
    dialogVisible,
    submitting,
    form,
    editForm,
    formRules,
    brandFilter,
    seriesFilter,
    variantFilter,
    exportFilter,
    energyFilter,
    currentPage,
    pageSize,
    seriesOptions,
    variantOptions,
    filteredModels,
    paginatedModels,
    pageRangeStart,
    pageRangeEnd,
    buildMockModels,
    isEditing,
    rowClassName,
    toggleDetail,
    collapseRow,
    toggleStatus,
    createEmptyForm,
    createEmptyEditForm,
    parsePriceToWan,
    formatPriceFromWan,
    extractFactoryParams,
    buildDetailConfigFromEdit,
    fillEditForm,
    startEdit,
    cancelEdit,
    formatLaunchDate,
    parseLaunchDate,
    formatGuidePrice,
    inferEnergyType,
    parseDetailConfig,
    resetForm,
    openCreate,
    handleCreate,
    buildModelFromForm: () => buildModelFromFormData(form),
    nextModelId: () => computeNextModelId(models.value),
    handleUpload,
    saveEdit,
    submitForm,
    handleDelete,
  }
}
