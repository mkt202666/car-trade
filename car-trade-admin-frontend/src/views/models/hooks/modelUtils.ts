/** 车型数据转换与表单工具函数 */
import { DEFAULT_DETAIL_CONFIG, DEFAULT_FACTORY_PARAMS, ENERGY_TYPES, EXPORT_COUNTRIES, SEED_MODELS } from './constants'
import type { EditForm, FactoryParams, ModelForm, VehicleModel } from './types'

/**
 * 基于种子数据生成车型 mock 列表，并扩展至 303 条以测试分页
 * @returns 含唯一 id 的完整 VehicleModel 数组
 */
export function buildMockModels(): VehicleModel[] {
  const models: VehicleModel[] = SEED_MODELS.map((item, index) => ({
    ...item,
    id: `MOD-${String(index + 1).padStart(3, '0')}`,
  }))

  const suffixes = ['标准版', '豪华版', '旗舰版', '运动版', '智享版']
  let counter = models.length + 1

  while (models.length < 303) {
    const seed = SEED_MODELS[(models.length - SEED_MODELS.length) % SEED_MODELS.length]
    const suffix = suffixes[models.length % suffixes.length]
    models.push({
      ...seed,
      id: `MOD-${String(counter).padStart(4, '0')}`,
      variant: `${seed.variant} ${suffix}`,
      updatedAt: '2026/6/14',
    })
    counter += 1
  }

  return models
}

/**
 * 从带货币符号的价格字符串中提取万元数值
 * @param price - 如「¥10.99万」
 * @returns 数值字符串，无法解析时返回空字符串
 */
export function parsePriceToWan(price: string) {
  const match = price.match(/[\d.]+/)
  if (!match) return ''
  return match[0]
}

/**
 * 将万元数值格式化为指导价展示字符串
 * @param wan - 万元数值字符串
 * @returns 如「¥10.99万」，无效输入返回「-」
 */
export function formatPriceFromWan(wan: string) {
  const num = parseFloat(wan)
  if (Number.isNaN(num) || num <= 0) return '-'
  return `¥${num.toFixed(2)}万`
}

/**
 * 从 detailConfig 提取出厂参数，缺失字段回退至 DEFAULT_FACTORY_PARAMS
 * @param config - 车型的扩展参数键值对，可为 undefined
 * @returns 完整的 FactoryParams 结构
 */
export function extractFactoryParams(config?: Record<string, string>): FactoryParams {
  return {
    '能源类型': config?.['能源类型'] || DEFAULT_FACTORY_PARAMS['能源类型'],
    '环保标准': config?.['环保标准'] || DEFAULT_FACTORY_PARAMS['环保标准'],
    '最大功率(kW)': config?.['最大功率(kW)'] || DEFAULT_FACTORY_PARAMS['最大功率(kW)'],
    '最大扭矩(N·m)': config?.['最大扭矩(N·m)'] || DEFAULT_FACTORY_PARAMS['最大扭矩(N·m)'],
    '车门数': config?.['车门数'] || DEFAULT_FACTORY_PARAMS['车门数'],
    '座位数': config?.['座位数'] || DEFAULT_FACTORY_PARAMS['座位数'],
    '左/右舵': config?.['左/右舵'] || DEFAULT_FACTORY_PARAMS['左/右舵'],
    '油箱容积(L)': config?.['油箱容积(L)'] || DEFAULT_FACTORY_PARAMS['油箱容积(L)'],
    '整车质量(kg)': config?.['整车质量(kg)'] || DEFAULT_FACTORY_PARAMS['整车质量(kg)'],
    '排量(mL)': config?.['排量(mL)'] || DEFAULT_FACTORY_PARAMS['排量(mL)'],
    '进气形式': config?.['进气形式'] || DEFAULT_FACTORY_PARAMS['进气形式'],
    '气缸排列形式': config?.['气缸排列形式'] || DEFAULT_FACTORY_PARAMS['气缸排列形式'],
    '气缸数': config?.['气缸数'] || DEFAULT_FACTORY_PARAMS['气缸数'],
    '燃油标号': config?.['燃油标号'] || DEFAULT_FACTORY_PARAMS['燃油标号'],
  }
}

/**
 * 将行内编辑表单中的厂商与出厂参数合并为 detailConfig
 * @param formData - 行内编辑表单数据
 * @returns 键值对对象，键含「厂商」及各出厂参数字段名
 */
export function buildDetailConfigFromEdit(formData: EditForm): Record<string, string> {
  return {
    厂商: formData.manufacturer,
    ...formData.factoryParams,
  }
}

/**
 * 将月份选择器值格式化为上市日期展示文案
 * @param value - YYYY-MM 格式字符串
 * @returns 如「2024年01月」，空值返回空字符串
 */
export function formatLaunchDate(value: string) {
  if (!value) return ''
  const [year, month] = value.split('-')
  return `${year}年${month}月`
}

/**
 * 将上市日期文案解析为月份选择器值
 * @param value - 如「2024年01月」
 * @returns YYYY-MM 格式，无法解析时返回空字符串
 */
export function parseLaunchDate(value: string) {
  const match = value.match(/(\d{4})年(\d{2})月/)
  if (match) return `${match[1]}-${match[2]}`
  return ''
}

/**
 * 将元单位指导价格式化为万元展示字符串（用于新增表单提交）
 * @param yuan - 元单位数值字符串
 * @returns 如「¥10.99万」，无效输入返回「-」
 */
export function formatGuidePrice(yuan: string) {
  const num = parseFloat(yuan)
  if (Number.isNaN(num) || num <= 0) return '-'
  return `¥${(num / 10000).toFixed(2)}万`
}

/**
 * 根据动力类型描述或 detailConfig 推断能源类型
 * @param powerType - 动力类型文案
 * @param detailConfig - 可选的扩展参数，优先读取其中的「能源类型」
 * @returns ENERGY_TYPES 中的某一枚举值，默认「汽油」
 */
export function inferEnergyType(powerType: string, detailConfig?: Record<string, string>) {
  const fromConfig = detailConfig?.['能源类型']
  if (fromConfig && ENERGY_TYPES.includes(fromConfig as typeof ENERGY_TYPES[number])) {
    return fromConfig
  }
  if (powerType.includes('增程')) return '增程式'
  if (powerType.includes('插混') || powerType.includes('插电')) return '插电式混合动力'
  if (powerType.includes('纯电动') || powerType.includes('EV')) return '纯电动'
  return '汽油'
}

/**
 * 解析 JSON 格式的 detailConfig 字符串
 * @param json - JSON 对象字符串
 * @returns 键值均为 string 的对象；空字符串返回 undefined
 * @throws JSON 格式错误时由 JSON.parse 抛出
 */
export function parseDetailConfig(json: string): Record<string, string> | undefined {
  const trimmed = json.trim()
  if (!trimmed) return undefined
  const parsed = JSON.parse(trimmed) as Record<string, unknown>
  return Object.fromEntries(
    Object.entries(parsed).map(([key, value]) => [key, String(value)]),
  )
}

/**
 * 创建新增车型弹窗的空表单初始值
 * @returns 带默认模板与占位字段的 ModelForm
 */
export function createEmptyForm(): ModelForm {
  return {
    brand: '',
    series: '',
    variant: '',
    launchDate: '2024-01',
    powerType: '1.5T 自然吸气',
    dimensions: '4000*1800*1500mm',
    bodyType: '',
    guidePrice: '',
    exportEligible: true,
    detailConfigJson: DEFAULT_DETAIL_CONFIG,
  }
}

/**
 * 创建行内编辑表单的空初始值
 * @returns 出厂参数已填充默认值的 EditForm
 */
export function createEmptyEditForm(): EditForm {
  return {
    brand: '',
    series: '',
    variant: '',
    launchMonth: '',
    powerType: '',
    bodyType: '',
    guidePriceWan: '',
    exportCountries: [],
    manufacturer: '',
    dimensions: '',
    factoryParams: { ...DEFAULT_FACTORY_PARAMS },
  }
}

/**
 * 根据现有列表生成下一个车型 ID
 * @param models - 当前全量车型列表
 * @returns 格式为 MOD-XXX 的新 ID（数字部分递增）
 */
export function nextModelId(models: VehicleModel[]) {
  const numericIds = models
    .map((item) => parseInt(item.id.replace(/\D/g, ''), 10))
    .filter((num) => !Number.isNaN(num))
  const next = numericIds.length ? Math.max(...numericIds) + 1 : 1
  return `MOD-${String(next).padStart(3, '0')}`
}

/**
 * 将新增表单数据转换为可写入列表的车型对象（不含 id）
 * @param form - 已通过校验的新增表单
 * @returns Omit<VehicleModel, 'id'>，含推断的 energyType 与解析后的 detailConfig
 */
export function buildModelFromForm(form: ModelForm): Omit<VehicleModel, 'id'> {
  const detailConfig = form.detailConfigJson.trim()
    ? parseDetailConfig(form.detailConfigJson)
    : undefined

  return {
    brand: form.brand.trim(),
    series: form.series.trim(),
    variant: form.variant.trim(),
    launchDate: formatLaunchDate(form.launchDate),
    powerType: form.powerType.trim(),
    bodyType: form.bodyType.trim(),
    dimensions: form.dimensions.trim() || undefined,
    price: form.guidePrice ? formatGuidePrice(form.guidePrice) : '-',
    exportCountries: form.exportEligible ? [...EXPORT_COUNTRIES] : [],
    status: 'ACTIVE',
    updatedAt: new Date().toLocaleDateString('zh-CN'),
    energyType: inferEnergyType(form.powerType, detailConfig),
    detailConfig,
  }
}
