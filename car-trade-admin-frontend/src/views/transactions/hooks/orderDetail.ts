/** 订单详情模块聚合导出：类型、常量与详情构建工具的统一入口 */
export type {
  FlowTimestamp,
  OrderDetail,
  OrderListItem,
  OrderStatusKey,
  PartyInfo,
  TransactionOrder,
  VehicleSpec,
} from './types'

export {
  CITY_MAP,
  DEFAULT_SPECS,
  DETAIL_OVERRIDES,
  FLOW_STEP_DEFS,
  ORDER_LIST,
  PROVINCES,
  STEP_ORDER,
  TRANSACTION_ORDERS,
  buildTransactionOrders,
} from './constants'

export {
  buildFlowTimestamps,
  buildOrderDetail,
  getFlowStepState,
} from './orderDetailUtils'
