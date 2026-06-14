/** 车型库 mock 数据、选项与表单校验 */
import type { FormRules } from 'element-plus'
import type { FactoryParams, ModelForm, VehicleModel } from './types'

/** 出厂参数默认值，用于无 detailConfig 时的回填与新增编辑表单初始化 */
export const DEFAULT_FACTORY_PARAMS: FactoryParams = {
  '能源类型': '汽油',
  '环保标准': '国VI',
  '最大功率(kW)': '83',
  '最大扭矩(N·m)': '145',
  '车门数': '5',
  '座位数': '5',
  '左/右舵': '左舵',
  '油箱容积(L)': '42.5',
  '整车质量(kg)': '1190',
  '排量(mL)': '1498',
  '进气形式': '自然吸气',
  '气缸排列形式': 'L',
  '气缸数': '4',
  '燃油标号': '92号',
}

/** Polo 车型的完整出厂参数示例，作为 SEED_MODELS 首条的 detailConfig */
export const POLO_DETAIL_CONFIG: Record<string, string> = {
  厂商: '上汽大众',
  ...DEFAULT_FACTORY_PARAMS,
}

/** 新增车型弹窗中 detailConfigJson 的默认 JSON 模板文本 */
export const DEFAULT_DETAIL_CONFIG = `{
  "厂商": "上汽大众",
  "能源类型": "汽油",
  "环保标准": "国VI",
  "最大功率(kW)": "83",
  "最大扭矩(N·m)": "145",
  "车门数": "5"
}`

/** 品牌筛选下拉选项，覆盖主流乘用车品牌 */
export const BRANDS = [
  '丰田', '五菱', '吉利', '哈弗', '大众', '奔驰', '奥迪', '宝马', '小米', '斯柯达',
  '日产', '本田', '极氪', '比亚迪', '沃尔沃', '特斯拉', '理想', '蔚来', '长安',
] as const

/** 出口目标国家/地区选项，用于筛选与出口多选 */
export const EXPORT_COUNTRIES = ['俄罗斯', '哈萨克斯坦', '尼日利亚'] as const

/** 能源类型筛选选项，与 inferEnergyType 推断结果对齐 */
export const ENERGY_TYPES = ['增程式', '插电式混合动力', '汽油', '纯电动'] as const

/** 车型库初始种子数据（不含 id，由 buildMockModels 分配） */
export const SEED_MODELS: Omit<VehicleModel, 'id'>[] = [
  { brand: '大众', series: 'Polo', variant: '2023款 1.5L 自动全景乐享版', launchDate: '2023年05月', powerType: '1.5L 自然吸气', bodyType: 'A0级车', dimensions: '4053*1740*1449mm', price: '¥10.99万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/5/15', energyType: '汽油', detailConfig: { ...POLO_DETAIL_CONFIG } },
  { brand: '比亚迪', series: '汉', variant: '2024款 荣耀版 EV 610KM 四驱智驾型', launchDate: '2024年02月', powerType: '纯电动 517马力', bodyType: 'C级车', price: '¥24.98万', exportCountries: ['哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/1', energyType: '纯电动' },
  { brand: '特斯拉', series: 'Model Y', variant: '2024款 后轮驱动版', launchDate: '2024年02月', powerType: '纯电动 299马力', bodyType: '中型SUV', price: '¥25.89万', exportCountries: ['哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/5/20', energyType: '纯电动' },
  { brand: '理想', series: 'L9', variant: '2024款 Pro版', launchDate: '2024年03月', powerType: '增程式 449马力', bodyType: '大型SUV', price: '¥42.98万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/5/22', energyType: '增程式' },
  { brand: '丰田', series: '凯美瑞', variant: '2024款 2.0E 精英版', launchDate: '2024年03月', powerType: '2.0L 173马力 L4', bodyType: 'B级车', price: '¥17.18万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/5/25', energyType: '汽油' },
  { brand: '哈弗', series: 'H6', variant: '2023款 第三代 1.5T 自动两驱Max', launchDate: '2023年08月', powerType: '1.5T 184马力 L4', bodyType: '紧凑型SUV', price: '¥12.89万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/5/28', energyType: '汽油' },
  { brand: '极氪', series: '001', variant: '2024款 WE版 100kWh 后驱', launchDate: '2024年02月', powerType: '纯电动 422马力', bodyType: 'C级车', price: '¥26.90万', exportCountries: ['哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/4', energyType: '纯电动' },
  { brand: '宝马', series: '3系', variant: '2024款 325Li M运动套装', launchDate: '2023年09月', powerType: '2.0T 184马力 L4', bodyType: 'B级车', price: '¥31.69万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/5/30', energyType: '汽油' },
  { brand: '五菱', series: '宏光MINIEV', variant: '2024款 马卡龙 215km 磷酸铁锂', launchDate: '2023年11月', powerType: '纯电动 41马力', bodyType: 'A00级车', price: '¥4.68万', exportCountries: ['哈萨克斯坦', '尼日利亚'], status: 'INACTIVE', updatedAt: '2026/6/2', energyType: '纯电动' },
  { brand: '奥迪', series: 'A6L', variant: '2024款 45 TFSI 臻选动感型', launchDate: '2023年10月', powerType: '2.0T 245马力 L4', bodyType: '', price: '¥45.49万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/1', energyType: '汽油' },
  { brand: '大众', series: 'Polo', variant: '2024款 1.4T 尊贵版', launchDate: '2024年02月', powerType: '1.4T 150马力 L4', bodyType: 'C级车', price: '¥13.90万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
  { brand: '大众', series: 'Polo', variant: '2022款 2.0T Pro版', launchDate: '2022年10月', powerType: '2.0T 252马力 L4', bodyType: '大型SUV', price: '¥17.48万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
  { brand: '大众', series: 'Polo', variant: '2024款 1.5T 风尚版', launchDate: '2024年07月', powerType: '1.5T 169马力 L4', bodyType: 'B级车', price: '¥25.45万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
  { brand: '大众', series: '高尔夫', variant: '2024款 2.0T 四驱版', launchDate: '2024年12月', powerType: '2.0T 190马力 L4 插混', bodyType: '大型SUV', price: '¥32.50万', exportCountries: ['哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '插电式混合动力' },
  { brand: '大众', series: '高尔夫', variant: '2023款 2.0T 旗舰版', launchDate: '2023年10月', powerType: '2.0T 190马力 L4', bodyType: 'A级车', price: '¥17.36万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
  { brand: '大众', series: '高尔夫', variant: '2023款 1.4T 尊贵版', launchDate: '2023年01月', powerType: '1.4T 150马力 L4', bodyType: '大型SUV', price: '¥22.61万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
  { brand: '大众', series: '迈腾', variant: '2024款 2.0T 尊贵版', launchDate: '2024年11月', powerType: '2.0T 252马力 L4', bodyType: 'A级车', price: '¥24.40万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
  { brand: '大众', series: '迈腾', variant: '2023款 2.0T 长续航版', launchDate: '2023年04月', powerType: '2.0T 190马力 L4', bodyType: 'MPV', price: '¥25.21万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
  { brand: '大众', series: '迈腾', variant: '2022款 1.5L Ultra版', launchDate: '2022年08月', powerType: '1.5L 113马力 L4', bodyType: 'A级车', price: '¥27.16万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
  { brand: '大众', series: '帕萨特', variant: '2023款 1.4T 四驱版', launchDate: '2023年11月', powerType: '1.4T 150马力 L4', bodyType: '大型SUV', price: '¥36.21万', exportCountries: ['俄罗斯', '哈萨克斯坦', '尼日利亚'], status: 'ACTIVE', updatedAt: '2026/6/14', energyType: '汽油' },
]

/** 新增车型弹窗的 Element Plus 表单校验规则 */
export const formRules: FormRules<ModelForm> = {
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  series: [{ required: true, message: '请输入车系', trigger: 'blur' }],
  variant: [{ required: true, message: '请输入具体车型型号', trigger: 'blur' }],
  launchDate: [{ required: true, message: '请选择上市时间', trigger: 'change' }],
  powerType: [{ required: true, message: '请输入动力类型', trigger: 'blur' }],
  guidePrice: [
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!/^\d+(\.\d+)?$/.test(value)) {
          callback(new Error('指导价须为数字（单位：元）'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
  detailConfigJson: [
    {
      validator: (_rule, value, callback) => {
        if (!value.trim()) {
          callback()
          return
        }
        try {
          const parsed = JSON.parse(value)
          if (typeof parsed !== 'object' || parsed === null || Array.isArray(parsed)) {
            callback(new Error('JSON 须为键值对对象'))
            return
          }
          callback()
        } catch {
          callback(new Error('JSON 格式不正确'))
        }
      },
      trigger: 'blur',
    },
  ],
}
