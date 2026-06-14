/** 交易管理模块类型定义 */

/** 订单履约流程状态键，对应 UI 流转节点与筛选下拉选项 */
export type OrderStatusKey =
  | 'submitted'      /** 创建交易 */
  | 'deposit_paid'   /** 创建合同（定金已缴） */
  | 'document_prep'  /** 完成签约 */
  | 'signed'         /** 交车过户 */
  | 'delivering'     /** 争议处理 */
  | 'completed'      /** 交易完成 */
  | 'cancelled'      /** 交易已终止（手动/违约） */

/** 流程节点存证时间戳 */
export interface FlowTimestamp {
  /** 精确到秒的时间，如 09:30:00 */
  time: string
  /** 存证日期，如 2026-05-30 */
  date: string
}

/** 买卖双方实名认证信息 */
export interface PartyInfo {
  /** 平台用户 ID */
  id: string
  /** 实名姓名 */
  name: string
  /** 联系电话 */
  phone: string
  /** 脱敏证件号 */
  idMasked: string
  /** 资质/实名核验说明文案 */
  verifyText: string
}

/** 车辆规格参数项（标签 + 展示值） */
export interface VehicleSpec {
  /** 参数名称，如「外观内饰」 */
  label: string
  /** 参数展示值 */
  value: string
}

/** 订单全景详情，含车辆、资金托管、履约流程等完整字段 */
export interface OrderDetail {
  /** 订单号 */
  id: string
  /** 品牌名称（含英文名） */
  brand: string
  /** 车型/年款描述 */
  model: string
  /** 车源地展示文案，如「江苏省-常州市」 */
  location: string
  /** 买家姓名 */
  buyer: string
  /** 卖家姓名 */
  seller: string
  /** 当前履约状态键 */
  statusKey: OrderStatusKey
  /** 当前履约状态中文标签 */
  statusLabel: string
  /** 成交总价展示文案，如 ¥203,000 */
  amount: string
  /** 存证日期 */
  evidenceDate: string
  /** 关联合同编号 */
  contractId: string
  /** 车辆成交价展示文案 */
  vehiclePrice: string
  /** 买方保证金展示文案 */
  buyerDeposit: string
  /** 卖方保证金展示文案 */
  sellerDeposit: string
  /** 车架号 VIN */
  vin: string
  /** 首牌登记日期 */
  firstRegistration: string
  /** 表显里程 */
  mileage: string
  /** 车况评级，如 94A */
  conditionGrade: string
  /** 车辆规格参数列表 */
  vehicleSpecs: VehicleSpec[]
  /** 卖方补充车源描述 */
  vehicleDescription: string
  /** 买方实名信息 */
  buyerInfo: PartyInfo
  /** 卖方实名信息 */
  sellerInfo: PartyInfo
  /** 资金托管银行名称 */
  escrowBank: string
  /** 托管流水状态说明 */
  escrowStatus: string
  /** 验车交付/发票提单状态 */
  deliveryStatus: string
  /** 争议处理状态说明 */
  disputeStatus: string
  /** 各流程节点存证时间，未到达的节点为 null */
  flowTimestamps: Record<OrderStatusKey, FlowTimestamp | null>
}

/** 列表行展示用的交易订单，在基础订单上扩展省、市字段供地域筛选 */
export interface TransactionOrder {
  /** 订单号 */
  id: string
  /** 品牌名称 */
  brand: string
  /** 车型描述 */
  model: string
  /** 车源地展示文案 */
  location: string
  /** 买家所在省份 */
  province: string
  /** 买家所在城市 */
  city: string
  /** 买家姓名 */
  buyer: string
  /** 卖家姓名 */
  seller: string
  /** 当前履约状态键 */
  statusKey: OrderStatusKey
  /** 当前履约状态中文标签 */
  statusLabel: string
  /** 成交总价展示文案 */
  amount: string
  /** 存证日期 */
  evidenceDate: string
}

/** 订单列表项，为 OrderDetail 去掉详情抽屉才需要的扩展字段 */
export type OrderListItem = Omit<
  OrderDetail,
  'contractId' | 'vehiclePrice' | 'buyerDeposit' | 'sellerDeposit' | 'vin' | 'firstRegistration' |
  'mileage' | 'conditionGrade' | 'vehicleSpecs' | 'vehicleDescription' | 'buyerInfo' | 'sellerInfo' |
  'escrowBank' | 'escrowStatus' | 'deliveryStatus' | 'disputeStatus' | 'flowTimestamps'
>
