/** 车型库相关类型定义 */

/** 车型库列表中的单条车型记录 */
export interface VehicleModel {
  /** 车型唯一标识，如 MOD-001 */
  id: string
  /** 1 级：品牌名称 */
  brand: string
  /** 2 级：车系名称 */
  series: string
  /** 3 级：具体年款及型号全称 */
  variant: string
  /** 上市时间（格式化，如「2023年05月」） */
  launchDate: string
  /** 动力类型描述，如「1.5L 自然吸气」「纯电动 517马力」 */
  powerType: string
  /** 车身级别/车型分类，如「A0级车」「中型SUV」 */
  bodyType: string
  /** 三维尺寸，格式如「4053*1740*1449mm」 */
  dimensions?: string
  /** 指导价展示字符串，如「¥10.99万」 */
  price: string
  /** 满足出口条件的国家/地区列表 */
  exportCountries: string[]
  /** 启用状态：ACTIVE 可用，INACTIVE 已停用 */
  status: 'ACTIVE' | 'INACTIVE'
  /** 最近更新时间，格式如「2026/5/15」 */
  updatedAt: string
  /** 能源类型枚举值，用于筛选与列表展示 */
  energyType: string
  /** 扩展出厂参数键值对（厂商、排量、气缸数等），存入 detailConfig */
  detailConfig?: Record<string, string>
}

/** 新增车型弹窗表单数据结构 */
export interface ModelForm {
  /** 1 级：品牌 */
  brand: string
  /** 2 级：车系 */
  series: string
  /** 3 级：具体车型型号 */
  variant: string
  /** 上市月份，月份选择器值格式 YYYY-MM */
  launchDate: string
  /** 动力类型 */
  powerType: string
  /** 三维尺寸 */
  dimensions: string
  /** 车身级别/车型分类 */
  bodyType: string
  /** 指导价（元，纯数字字符串） */
  guidePrice: string
  /** 是否勾选「符合出口要求」，决定 exportCountries 默认值 */
  exportEligible: boolean
  /** 其它详细参数的 JSON 字符串，提交时解析为 detailConfig */
  detailConfigJson: string
}

/** 车辆出厂详细参数，键名与主机厂配置表字段一致 */
export interface FactoryParams {
  /** 能源类型，如汽油、纯电动 */
  '能源类型': string
  /** 环保排放标准 */
  '环保标准': string
  /** 最大功率 */
  '最大功率(kW)': string
  /** 最大扭矩 */
  '最大扭矩(N·m)': string
  /** 车门数量 */
  '车门数': string
  /** 座位数量 */
  '座位数': string
  /** 方向盘位置 */
  '左/右舵': string
  /** 油箱容积 */
  '油箱容积(L)': string
  /** 整车质量 */
  '整车质量(kg)': string
  /** 发动机排量 */
  '排量(mL)': string
  /** 进气形式，如自然吸气、涡轮增压 */
  '进气形式': string
  /** 气缸排列形式 */
  '气缸排列形式': string
  /** 气缸数量 */
  '气缸数': string
  /** 推荐燃油标号 */
  '燃油标号': string
}

/** 表格行内编辑表单数据结构 */
export interface EditForm {
  /** 1 级：品牌 */
  brand: string
  /** 2 级：车系 */
  series: string
  /** 3 级：具体车型 */
  variant: string
  /** 上市月份，月份选择器值格式 YYYY-MM */
  launchMonth: string
  /** 动力类型 */
  powerType: string
  /** 车身级别 */
  bodyType: string
  /** 指导价（万元数值字符串） */
  guidePriceWan: string
  /** 出口目标国家/地区多选 */
  exportCountries: string[]
  /** 厂商名称，只读展示，写入 detailConfig.厂商 */
  manufacturer: string
  /** 三维尺寸 */
  dimensions: string
  /** 出厂详细参数子表单 */
  factoryParams: FactoryParams
}
