/** 车型库维护 composable */
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, TableInstance, UploadFile } from 'element-plus'
import { usePagination } from '../../../composables/usePagination'
import {
  getBrands, getSeries, getModels,
  createBrand, createSeries, createModel,
  updateBrand, updateSeries, updateModel,
  deleteBrand, deleteSeries, deleteModel,
  importCarModels, downloadImportTemplate,
} from '../../../api/carLibrary'
import type { CarBrand, CarSeries, CarModel, CarModelDTO } from '../../../api/carLibrary'
import {
  BRANDS,
  DEFAULT_DETAIL_CONFIG,
  ENERGY_TYPES,
  EXPORT_COUNTRIES,
  formRules,
} from './constants'
import {
  buildDetailConfigFromEdit,
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
  /** 全量车型列表 */
  const models = ref<VehicleModel[]>([])
  /** 品牌原始数据（API 返回），用于名称 → ID 查找 */
  const brandsList = ref<CarBrand[]>([])
  /** 车系原始数据（API 返回），用于名称 → ID 查找 */
  const seriesList = ref<CarSeries[]>([])
  const loading = ref(false)
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

  /** 从 API 获取车型数据 */
  async function fetchModels() {
    loading.value = true
    try {
      const [brandsRes, seriesRes, modelsRes] = await Promise.all([
        getBrands(),
        getSeries(),
        getModels(),
      ])

      const brandsMap = new Map<number, CarBrand>()
      if (brandsRes?.data) {
        brandsList.value = brandsRes.data
        brandsRes.data.forEach((b: CarBrand) => brandsMap.set(b.id, b))
      }

      const seriesMap = new Map<number, CarSeries>()
      if (seriesRes?.data) {
        seriesList.value = seriesRes.data
        seriesRes.data.forEach((s: CarSeries) => seriesMap.set(s.id, s))
      }

      if (modelsRes?.data) {
        models.value = modelsRes.data.map((model: CarModel) => {
          const series = seriesMap.get(model.seriesId)
          const brand = series ? brandsMap.get(series.brandId) : null
          return {
            id: `MOD-${model.id}`,
            brand: brand?.name || '—',
            series: series?.name || '—',
            variant: model.name,
            launchDate: model.year ? `${model.year}年` : '—',
            powerType: '燃油',
            bodyType: '轿车',
            price: model.price ? `¥${(model.price / 10000).toFixed(1)}万` : '—',
            exportCountries: [],
            status: (model.status || 'ACTIVE') as 'ACTIVE' | 'INACTIVE',
            updatedAt: model.createdAt?.split('T')[0]?.replace(/-/g, '/') || '—',
            energyType: '燃油',
            detailConfig: {},
          }
        })
      }
    } catch (e: unknown) {
      console.error('Failed to fetch models:', e)
      ElMessage.error('加载车型数据失败，请刷新页面重试')
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    fetchModels()
  })

  /**
   * 从 "MOD-123" 格式的字符串中提取数字 ID
   * @param id - 形如 "MOD-001" 的前端展示 ID
   * @returns 数字 ID，无法解析时返回 NaN
   */
  function extractNumericId(id: string): number {
    return parseInt(id.replace(/\D/g, ''), 10)
  }

  /**
   * 根据品牌名称查找对应的品牌 ID
   * @param name - 品牌名称
   * @returns 品牌数字 ID，未找到时返回 undefined
   */
  function findBrandIdByName(name: string): number | undefined {
    return brandsList.value.find((b) => b.name === name)?.id
  }

  /**
   * 根据车系名称和品牌 ID 查找对应的车系 ID
   * @param name - 车系名称
   * @param brandId - 品牌数字 ID
   * @returns 车系数字 ID，未找到时返回 undefined
   */
  function findSeriesIdByName(name: string, brandId: number): number | undefined {
    return seriesList.value.find((s) => s.name === name && s.brandId === brandId)?.id
  }

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

  const { currentPage, pageSize, paginatedItems: paginatedModels, pageRangeStart, pageRangeEnd } =
    usePagination({
      source: filteredModels,
      defaultPageSize: 20,
      resetOn: [brandFilter, seriesFilter, exportFilter, energyFilter, variantFilter],
    })

  /** 品牌变更时重置下级筛选 */
  watch(brandFilter, () => {
    seriesFilter.value = 'all'
    variantFilter.value = 'all'
  })

  /** 车系变更时重置车型筛选 */
  watch(seriesFilter, () => {
    variantFilter.value = 'all'
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
   * 保存行内编辑结果，调用 API 更新车型数据并刷新列表
   * @param row - 正在编辑的车型行（用于定位 id）
   */
  async function saveEdit(row: VehicleModel) {
    if (!editForm.brand.trim() || !editForm.series.trim() || !editForm.variant.trim()) {
      ElMessage.warning('请填写完整的车型谱系信息')
      return
    }

    savingEdit.value = true
    try {
      const numericId = extractNumericId(row.id)
      if (Number.isNaN(numericId)) {
        ElMessage.error('车型 ID 无效，无法保存')
        return
      }

      // 解析品牌 → 车系 → 构建 API 请求
      const brandId = findBrandIdByName(editForm.brand.trim())
      let seriesId: number | undefined
      if (brandId !== undefined) {
        seriesId = findSeriesIdByName(editForm.series.trim(), brandId)
      }

      if (seriesId === undefined) {
        ElMessage.error(`未找到品牌「${editForm.brand}」下的车系「${editForm.series}」，请先在品牌/车系管理中创建`)
        return
      }

      // 解析年份：从 "2024年05月" 或 "2024年" 提取年份数字
      const yearMatch = editForm.launchMonth.match(/(\d{4})/)
      const year = yearMatch ? parseInt(yearMatch[1], 10) : undefined

      // 解析价格：万元 → 元
      const priceNum = parseFloat(editForm.guidePriceWan)
      const price = !Number.isNaN(priceNum) && priceNum > 0 ? Math.round(priceNum * 10000) : undefined

      const updateData: CarModelDTO = {
        seriesId,
        name: editForm.variant.trim(),
        year,
        price,
      }

      await updateModel(numericId, updateData)

      // 同步更新本地列表，避免等待刷新时的闪烁
      const detailConfig = buildDetailConfigFromEdit(editForm)
      const index = models.value.findIndex((item) => item.id === row.id)
      if (index !== -1) {
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
      }

      editingId.value = null
      Object.assign(editForm, createEmptyEditForm())
      ElMessage.success('车型修改已保存')

      // 后台刷新列表以同步服务端最新状态
      fetchModels()
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '保存失败，请稍后重试'
      ElMessage.error(msg)
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

  /** 校验并提交新增表单，调用 API 创建车型并刷新列表 */
  async function submitForm() {
    if (!formRef.value) return
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return

    submitting.value = true
    try {
      const brandName = form.brand.trim()
      const seriesName = form.series.trim()

      // 1. 解析品牌 ID：已有则使用，否则调用 API 创建
      let brandId = findBrandIdByName(brandName)
      if (brandId === undefined) {
        const brandRes = await createBrand({ name: brandName })
        if (brandRes?.data) {
          brandId = brandRes.data.id
        } else {
          ElMessage.error('品牌创建失败，请重试')
          return
        }
      }

      // 2. 解析车系 ID：已有则使用，否则调用 API 创建
      let seriesId = findSeriesIdByName(seriesName, brandId)
      if (seriesId === undefined) {
        const seriesRes = await createSeries({ brandId, name: seriesName })
        if (seriesRes?.data) {
          seriesId = seriesRes.data.id
        } else {
          ElMessage.error('车系创建失败，请重试')
          return
        }
      }

      // 3. 构建车型 DTO 并调用 API 创建
      const yearMatch = form.launchDate.match(/(\d{4})/)
      const year = yearMatch ? parseInt(yearMatch[1], 10) : undefined

      const priceNum = parseFloat(form.guidePrice)
      const price = !Number.isNaN(priceNum) && priceNum > 0 ? Math.round(priceNum) : undefined

      const modelData: CarModelDTO = {
        seriesId,
        name: form.variant.trim(),
        year,
        price,
      }

      await createModel(modelData)

      ElMessage.success('车型已新增')
      dialogVisible.value = false

      // 刷新全部列表（品牌/车系/车型）
      await fetchModels()
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '新增失败，请稍后重试'
      ElMessage.error(msg)
    } finally {
      submitting.value = false
    }
  }

  /**
   * 删除车型，弹出二次确认后调用 API 并刷新列表
   * @param row - 待删除的车型行
   */
  async function handleDelete(row: VehicleModel) {
    try {
      await ElMessageBox.confirm(`确定删除车型「${row.variant}」吗？`, '删除确认', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消',
      })
    } catch {
      // 用户取消，直接返回
      return
    }

    try {
      const numericId = extractNumericId(row.id)
      if (Number.isNaN(numericId)) {
        ElMessage.error('车型 ID 无效，无法删除')
        return
      }

      await deleteModel(numericId)

      // 清除编辑/展开状态
      if (editingId.value === row.id) {
        editingId.value = null
        expandedIds.value.delete(row.id)
      }

      ElMessage.success('删除成功')

      // 刷新列表以同步服务端状态
      fetchModels()
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '删除失败，请稍后重试'
      ElMessage.error(msg)
    }
  }

  /** 批量导入 loading 状态 */
  const importing = ref(false)

  /**
   * Excel 批量导入车型
   * @param uploadFile - Element Plus 上传组件返回的文件对象
   */
  async function handleUpload(uploadFile: UploadFile) {
    const rawFile = uploadFile.raw
    if (!rawFile) {
      ElMessage.warning('未获取到文件，请重新选择')
      return
    }

    importing.value = true
    try {
      const result = await importCarModels(rawFile)
      const parts: string[] = []
      parts.push(`总计 ${result.total} 条`)
      parts.push(`成功 ${result.created} 条`)
      if (result.skipped > 0) parts.push(`跳过 ${result.skipped} 条`)
      if (result.errors && result.errors.length > 0) {
        parts.push(`错误 ${result.errors.length} 条`)
      }
      const summary = parts.join('，')

      if (result.errors && result.errors.length > 0) {
        ElMessage.warning(`导入完成：${summary}。部分行存在错误，请检查数据后重试。`)
      } else if (result.skipped > 0) {
        ElMessage.info(`导入完成：${summary}（部分记录已存在，已跳过）`)
      } else {
        ElMessage.success(`导入完成：${summary}`)
      }

      await fetchModels()
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '导入失败，请稍后重试'
      ElMessage.error(msg)
    } finally {
      importing.value = false
    }
  }

  /** 下载车型导入模板 */
  function handleDownloadTemplate() {
    downloadImportTemplate()
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
    importing,
    handleDownloadTemplate,
    saveEdit,
    submitForm,
    handleDelete,
    buildMockModels: () => [],
  }
}
