/** 出口配置 composable — 使用后端 API */

import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadFile } from 'element-plus'
import {
  getExportRegions,
  createExportRegion,
  updateExportRegion,
  deleteExportRegion,
  type ExportRegionVO,
  type ExportRegionDTO,
} from '../../../api/exportRegions'
import { constraintFields, constraintOperators, formRules } from './constants'
import {
  countryFlag,
  createEmptyForm,
  displayIcon,
  formatRequirements,
  groupTagType,
  isImageIcon,
  parseRequirements,
  resolveGroupKey,
} from './exportConfigUtils'
import type { ExportRegion } from './types'

export type { Constraint, ExportRegion, RegionForm } from './types'
export {
  constraintFields,
  constraintOperators,
  formRules,
} from './constants'
export {
  countryFlag,
  createEmptyForm,
  displayIcon,
  formatRequirements,
  groupTagType,
  isImageIcon,
  parseRequirements,
  resolveGroupKey,
} from './exportConfigUtils'

/** 将后端 VO 映射为前端 ExportRegion 类型 */
function mapVoToRegion(vo: ExportRegionVO): ExportRegion {
  let constraints: ExportRegion['constraints'] = []
  try {
    constraints = JSON.parse(vo.constraints || '[]')
  } catch {
    constraints = []
  }

  let requirements: string[] = []
  try {
    requirements = JSON.parse(vo.requirements || '[]')
  } catch {
    requirements = []
  }

  return {
    id: String(vo.id),
    code: vo.code,
    name: vo.name,
    flag: vo.flag,
    group: vo.group,
    groupKey: vo.groupKey as ExportRegion['groupKey'],
    icon: vo.icon,
    constraints,
    requirements,
    status: vo.status as 'ACTIVE' | 'INACTIVE',
  }
}

/** 将表单数据序列化为后端 DTO */
function buildDtoFromPayload(payload: ExportRegion): ExportRegionDTO {
  return {
    code: payload.code,
    name: payload.name,
    flag: payload.flag,
    group: payload.group,
    groupKey: payload.groupKey,
    icon: payload.icon,
    constraints: JSON.stringify(payload.constraints),
    requirements: JSON.stringify(payload.requirements),
    status: payload.status,
  }
}

/** 管理出口地区的增删改与约束条件配置 */
export function useExportConfig() {
  /** 地区编辑表单实例引用，用于校验与重置 */
  const formRef = ref<FormInstance>()
  /** 编辑弹窗可见性 */
  const dialogVisible = ref(false)
  /** 弹窗模式：新建或编辑 */
  const dialogMode = ref<'create' | 'edit'>('create')
  /** 当前编辑中的地区 ID，新建时为 null */
  const editingId = ref<string | null>(null)
  /** 表单提交中的 loading 状态 */
  const submitting = ref(false)
  /** 列表数据加载中状态 */
  const loading = ref(false)

  /** 地区编辑表单数据 */
  const form = reactive(createEmptyForm())

  /** 出口地区列表数据 */
  const regions = ref<ExportRegion[]>([])

  /** 从后端加载出口地区列表 */
  async function fetchRegions() {
    loading.value = true
    try {
      const data = (await getExportRegions()) as unknown as ExportRegionVO[]
      regions.value = data.map(mapVoToRegion)
    } catch {
      ElMessage.error('加载出口地区配置失败')
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    fetchRegions()
  })

  /**
   * 用已有地区数据填充编辑表单
   * @param region - 待编辑的出口地区对象
   */
  function fillForm(region: ExportRegion) {
    form.name = region.name
    form.code = region.code
    form.group = region.group
    form.icon = region.icon
    form.constraints = region.constraints.map((item) => ({ ...item }))
    form.requirementsText = formatRequirements(region.requirements)
    form.status = region.status === 'ACTIVE' ? 'active' : 'inactive'
  }

  /** 重置表单至初始状态并清除校验提示 */
  function resetForm() {
    Object.assign(form, createEmptyForm())
    editingId.value = null
    formRef.value?.clearValidate()
  }

  /** 打开新建地区配置弹窗 */
  function openCreate() {
    dialogMode.value = 'create'
    resetForm()
    dialogVisible.value = true
  }

  /**
   * 打开编辑地区配置弹窗
   * @param region - 待编辑的出口地区对象
   */
  function openEdit(region: ExportRegion) {
    dialogMode.value = 'edit'
    editingId.value = region.id
    fillForm(region)
    dialogVisible.value = true
  }

  /** 在约束编辑器中追加一条空白约束规则 */
  function addConstraint() {
    form.constraints.push({ field: '左舵/右舵', operator: '==', value: '' })
  }

  /**
   * 移除指定索引的约束规则，至少保留一条
   * @param index - 待移除的约束行索引
   */
  function removeConstraint(index: number) {
    if (form.constraints.length <= 1) return
    form.constraints.splice(index, 1)
  }

  /**
   * 处理地区图标本地上传，读取为 Base64 写入 form.icon
   * @param uploadFile - Element Plus 上传组件返回的文件对象
   */
  function handleIconUpload(uploadFile: UploadFile) {
    const file = uploadFile.raw
    if (!file) return

    const reader = new FileReader()
    reader.onload = () => {
      form.icon = String(reader.result)
    }
    reader.readAsDataURL(file)
  }

  /**
   * 校验所有约束规则的比较值是否已填写
   * @returns 全部有效返回 true，否则提示并返回 false
   */
  function validateConstraints() {
    const invalidConstraint = form.constraints.some((item) => !item.value.trim())
    if (invalidConstraint) {
      ElMessage.warning('请完善参数约束条件的比较值')
      return false
    }
    return true
  }

  /**
   * 将当前表单数据组装为 ExportRegion 提交载荷
   * @returns 可直接写入 regions 列表的地区对象
   */
  function buildRegionPayload(): ExportRegion {
    const code = form.code.trim().toUpperCase()
    const icon = form.icon.trim() || countryFlag(code)
    return {
      id: editingId.value ?? `region-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
      code,
      name: form.name.trim(),
      group: form.group.trim(),
      groupKey: resolveGroupKey(form.group),
      icon,
      constraints: form.constraints.map((item) => ({
        field: item.field,
        operator: item.operator,
        value: item.value.trim(),
      })),
      requirements: parseRequirements(form.requirementsText),
      status: form.status === 'active' ? 'ACTIVE' : 'INACTIVE',
    }
  }

  /** 校验表单与约束后提交，调用后端 API 新建或更新 */
  async function submitForm() {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid || !validateConstraints()) return

    const payload = buildRegionPayload()
    const duplicateCode = regions.value.some(
      (item) => item.code === payload.code && item.id !== payload.id,
    )
    if (duplicateCode) {
      ElMessage.warning('地区代码已存在')
      return
    }

    submitting.value = true
    try {
      const dto = buildDtoFromPayload(payload)

      if (dialogMode.value === 'create') {
        await createExportRegion(dto)
        ElMessage.success('地区配置已新增')
      } else {
        const numericId = Number(editingId.value)
        await updateExportRegion(numericId, dto)
        ElMessage.success('地区配置已更新')
      }

      dialogVisible.value = false
      await fetchRegions()
    } catch {
      ElMessage.error(dialogMode.value === 'create' ? '新增失败' : '更新失败')
    } finally {
      submitting.value = false
    }
  }

  /**
   * 删除指定出口地区配置，需用户确认
   * @param region - 待删除的地区对象
   */
  async function handleDelete(region: ExportRegion) {
    try {
      await ElMessageBox.confirm(`确定删除「${region.name}」的出口配置吗？`, '删除确认', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      })
      await deleteExportRegion(Number(region.id))
      ElMessage.success('已删除')
      await fetchRegions()
    } catch {
      // cancelled or failed
    }
  }

  return {
    constraintFields,
    constraintOperators,
    formRef,
    dialogVisible,
    dialogMode,
    editingId,
    submitting,
    loading,
    form,
    formRules,
    regions,
    createEmptyForm,
    countryFlag,
    isImageIcon,
    displayIcon,
    groupTagType,
    resolveGroupKey,
    formatRequirements,
    parseRequirements,
    fillForm,
    resetForm,
    openCreate,
    openEdit,
    addConstraint,
    removeConstraint,
    handleIconUpload,
    validateConstraints,
    buildRegionPayload,
    submitForm,
    handleDelete,
    fetchRegions,
  }
}
