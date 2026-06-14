/** 交易管理模块静态数据与配置 */
import type { OrderDetail, OrderListItem, OrderStatusKey, TransactionOrder, VehicleSpec } from './types'

/** 履约流程节点定义：状态键与 UI 展示标签的映射 */
export const FLOW_STEP_DEFS: { key: OrderStatusKey; label: string }[] = [
  { key: 'submitted', label: '创建交易' },
  { key: 'deposit_paid', label: '创建合同' },
  { key: 'document_prep', label: '完成签约' },
  { key: 'signed', label: '交车过户' },
  { key: 'delivering', label: '争议处理' },
  { key: 'completed', label: '交易完成' },
]

/** 正常履约流程中各状态键的顺序，用于计算节点完成/当前/待办状态 */
export const STEP_ORDER: OrderStatusKey[] = [
  'submitted',
  'deposit_paid',
  'document_prep',
  'signed',
  'delivering',
  'completed',
]

/** 车辆规格参数默认占位数据，详情无覆盖时使用 */
export const DEFAULT_SPECS: VehicleSpec[] = [
  { label: '外观内饰', value: '星空灰/黑内' },
  { label: '牌照归属', value: '浙江杭州' },
  { label: '交强险到期', value: '2026年08月11日' },
  { label: '年检到期', value: '2025年08月' },
  { label: '使用性质', value: '非营运' },
  { label: '过户次数', value: '0 次' },
  { label: '变速箱', value: '电动车单速变速箱' },
  { label: '排放标准', value: '新能源' },
  { label: '燃油类型', value: '—' },
  { label: '排量', value: '—' },
  { label: '座位数', value: '—' },
  { label: '驱动方式', value: '—' },
  { label: '新车指导价', value: '—' },
]

/** 订单列表 mock 数据（不含详情扩展字段） */
export const ORDER_LIST: OrderListItem[] = [
  {
    id: 'ORD-2026053001',
    brand: '特斯拉 (Tesla)',
    model: 'Model 3 2023 焕新版',
    location: '江苏省-常州市',
    buyer: '张建国',
    seller: '李铁强',
    statusKey: 'document_prep',
    statusLabel: '完成签约',
    amount: '¥203,000',
    evidenceDate: '2026-05-30',
  },
  {
    id: 'ORD-2026052802',
    brand: '极氪 (Zeekr)',
    model: '极氪001 ME版 100kWh',
    location: '江苏省-南通市',
    buyer: '李思平',
    seller: '陈伟东',
    statusKey: 'completed',
    statusLabel: '交易完成',
    amount: '¥238,000',
    evidenceDate: '2026-05-30',
  },
  {
    id: 'ORD-2026060103',
    brand: '丰田 (Toyota)',
    model: '凯美瑞 2.5G 豪华版',
    location: '江苏省-南通市',
    buyer: '王凯文',
    seller: '刘本山',
    statusKey: 'deposit_paid',
    statusLabel: '创建合同',
    amount: '¥114,500',
    evidenceDate: '2026-06-03',
  },
  {
    id: 'ORD-2026060304',
    brand: '比亚迪 (BYD)',
    model: '汉 EV 2024款 旗舰型',
    location: '江苏省-南通市',
    buyer: '陈伟',
    seller: '张店长',
    statusKey: 'delivering',
    statusLabel: '争议处理',
    amount: '¥198,000',
    evidenceDate: '2026-06-03',
  },
  {
    id: 'ORD-2026060405',
    brand: '蔚来 (NIO)',
    model: '蔚来 ET5 2022款 75kWh',
    location: '江苏省-南通市',
    buyer: '张建国',
    seller: '刘德华',
    statusKey: 'document_prep',
    statusLabel: '完成签约',
    amount: '¥175,000',
    evidenceDate: '2026-06-13',
  },
]

/** 按订单 ID 覆盖的详情字段，用于展示差异化全景数据 */
export const DETAIL_OVERRIDES: Partial<Record<string, Partial<OrderDetail>>> = {
  'ORD-2026053001': {
    contractId: 'CTR-500293818',
    vehiclePrice: '￥203,000',
    buyerDeposit: '￥10,000',
    sellerDeposit: '￥10,000',
    vin: 'LRW3F7AL8NC104XXX',
    firstRegistration: '2023年08月12日',
    mileage: '2.8 万公里',
    conditionGrade: '94A',
    vehicleDescription:
      '法拍竞价代拍车源，已完成5D核心128项整备认证。双电机性能好车。无任何结构性损伤。 (随车钥匙: 2把)',
    buyerInfo: {
      id: 'USR-3001',
      name: '张建国',
      phone: '13812345678',
      idMasked: '3204021980********',
      verifyText: '已通过 5D平台金牌商户资质核验',
    },
    sellerInfo: {
      id: 'USR-2041',
      name: '李铁强',
      phone: '13951928372',
      idMasked: '320402199002021234',
      verifyText: '已验证 国企法拍车产备档资产处置权',
    },
    escrowBank: '网商银行',
    escrowStatus:
      '买方提报定金 ¥10,000 已冻结 (由商网信托三方监管户存管) | 卖方违约保证金 ¥10,000 已锁定划拨。双向权益受托保护。',
    deliveryStatus: '待履约 (需待三方买卖协议签署、物流到店接车验验通过后解锁)',
    disputeStatus: '暂无争议记录',
  },
}

/** 买家省份筛选下拉可选省份列表 */
export const PROVINCES = ['江苏省']

/** 省份 → 城市列表映射，供省市联动筛选 */
export const CITY_MAP: Record<string, string[]> = {
  江苏省: ['南京市', '常州市', '南通市'],
}

/**
 * 将订单列表扩展为带省、市字段的交易订单数据。
 * 当前 mock 统一归属江苏省，城市从 location 字段解析。
 * @returns 含 province、city 的 TransactionOrder 数组
 */
export function buildTransactionOrders(): TransactionOrder[] {
  return ORDER_LIST.map((order) => ({
    ...order,
    province: '江苏省',
    city: order.location.split('-')[1] ?? '南通市',
  }))
}

/** 预构建的全量交易订单列表，供 useTransactions 直接引用 */
export const TRANSACTION_ORDERS = buildTransactionOrders()
