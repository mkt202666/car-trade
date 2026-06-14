/** 车源管理相关类型定义 */

/** 车源来源渠道：司法拍卖、置换、合作车商、个人卖家 */
export type VehicleChannel = 'Auction' | 'TradeIn' | 'Dealer' | 'DirectUser'

/** 车源上架流转状态 */
export type VehicleStatus = 'listed' | 'reserved' | 'pending_review' | 'sold' | 'disapproved'

/** 详情页出厂参数表格中的单行数据 */
export interface VehicleSpecRow {
  /** 参数名称，如「能源类型」「车门数」 */
  label: string
  /** 参数值 */
  value: string
  /** 是否高亮显示（如车源所在地区行） */
  highlight?: boolean
}

/** 车源详情扩展信息，包含车况描述、卖家联系方式与出厂参数 */
export interface VehicleDetail {
  /** 车况文字描述 */
  description: string
  /** 表显里程（格式化展示，如「2.80万公里」） */
  mileageKm: string
  /** 排放标准，纯电车型通常为「-」 */
  emissionStandard: string
  /** 排量或动力描述，如「2.0T」「纯电」 */
  displacement: string
  /** 车身与内饰颜色，以「|」分隔 */
  colors: string
  /** 变速箱类型 */
  transmission: string
  /** 主机厂/制造商名称 */
  manufacturer: string
  /** 车辆类型分类，如「中型车」「猎装车」 */
  vehicleType: string
  /** 新车指导价（带单位，如「26.0万」） */
  newCarPrice: string
  /** 使用性质，如「非营运」 */
  usageNature: string
  /** 交强险到期日，格式 YYYY-MM-DD */
  insuranceExpiry: string
  /** 卖家平台用户 ID */
  sellerId: string
  /** 卖家联系电话 */
  sellerPhone: string
  /** 卖家角色标签，如「个人车主」「5D 合作车辆」 */
  sellerRole: string
  /** 出厂详细参数配置列表，供详情弹窗表格渲染 */
  specs: VehicleSpecRow[]
}

/** 车源列表与详情弹窗的核心数据结构 */
export interface Vehicle {
  /** 车源唯一标识，如 CAR-1002 */
  id: string
  /** 品牌名称（含英文名），如「特斯拉 (Tesla)」 */
  brand: string
  /** 车系与年款型号全称 */
  model: string
  /** 能源类型标签，如「纯电」「混动」，用于列表角标展示 */
  energyType?: string
  /** 上牌日期（格式化，如「2023年08月」） */
  registerDate: string
  /** 表显里程简写，如「2.8w 公里」 */
  mileage: string
  /** 所在地区全称，如「浙江省-杭州市」 */
  region: string
  /** 所在省份，用于省份筛选 */
  province: string
  /** 货源渠道 */
  channel: VehicleChannel
  /** 卖家展示信息 */
  seller: {
    /** 卖家真实姓名 */
    name: string
    /** 卖家昵称/平台称呼 */
    nickname: string
    /** 卖家类型标签，如「5D 合作车辆」「个人车商」 */
    type: string
  }
  /** 竞市价（万元，不含货币符号） */
  price: string
  /** 新车指导价（万元，不含货币符号） */
  guidePrice: string
  /** 当前上架流转状态 */
  status: VehicleStatus
  /** 车况详情与出厂参数 */
  detail: VehicleDetail
}
