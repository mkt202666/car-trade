/** 出口地区约束字段、表单规则与 mock 数据 */

import type { FormRules } from 'element-plus'
import type { ExportRegion } from './types'

/** 机读约束可选参数字段列表，供约束编辑器 field 下拉选择 */
export const constraintFields = [
  '厂商', '能源类型', '环保标准', '最大功率(kW)', '最大扭矩(N·m)', '车门数', '座位数',
  '左舵/右舵', '油箱容积(L)', '整车质量(kg)', '排量(mL)', '进气形式', '气缸排列形式',
  '气缸数', '燃油标号', '车龄(年)', '动力类型', '三维尺寸', '过户次数', '车型',
]

/** 约束比较运算符列表，供约束编辑器 operator 下拉选择 */
export const constraintOperators = ['==', '!=', '>', '<', '>=', '<=', '包含', '不包含']

/** 地区编辑弹窗表单校验规则，绑定 formRef */
export const formRules: FormRules = {
  name: [{ required: true, message: '请填写地区名称', trigger: 'blur' }],
  code: [{ required: true, message: '请填写地区代码', trigger: 'blur' }],
  group: [{ required: true, message: '请填写所属地区分组', trigger: 'blur' }],
  requirementsText: [{ required: true, message: '请填写条件描述', trigger: 'blur' }],
}

/** 出口地区初始 mock 数据，页面加载时填充 regions 列表 */
export const SEED_REGIONS: ExportRegion[] = [
  {
    id: 'ru',
    code: 'RU',
    name: '俄罗斯',
    group: '独联体',
    groupKey: 'cis',
    icon: '🇷🇺',
    constraints: [
      { field: '左舵/右舵', operator: '==', value: '左舵' },
      { field: '环保标准', operator: '包含', value: '国VI' },
    ],
    requirements: [
      '必须为左舵车辆',
      '排放标准不低于国VI',
      '需具备 GLONASS 认证',
    ],
    status: 'ACTIVE',
  },
  {
    id: 'kz',
    code: 'KZ',
    name: '哈萨克斯坦',
    group: '独联体',
    groupKey: 'cis',
    icon: '🇰🇿',
    constraints: [{ field: '左舵/右舵', operator: '==', value: '左舵' }],
    requirements: [
      '车龄小于 5 年',
      '必须为左舵车辆',
      '排放标准符合当地要求',
      '需提供权威第三方中英文检测报告',
    ],
    status: 'ACTIVE',
  },
  {
    id: 'ng',
    code: 'NG',
    name: '尼日利亚',
    group: '非洲',
    groupKey: 'africa',
    icon: '🇳🇬',
    constraints: [{ field: '左舵/右舵', operator: '==', value: '左舵' }],
    requirements: [
      '车龄小于 15 年',
      '必须为左舵车辆',
      '需提供适航证',
    ],
    status: 'ACTIVE',
  },
]
