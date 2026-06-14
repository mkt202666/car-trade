/** 订单详情构建与流程节点工具 */
import {
  DEFAULT_SPECS,
  DETAIL_OVERRIDES,
  ORDER_LIST,
  STEP_ORDER,
} from './constants'
import type { FlowTimestamp, OrderDetail, OrderStatusKey } from './types'

/**
 * 计算单个履约流程节点在当前订单状态下的展示状态。
 * @param currentKey 订单当前履约状态键
 * @param stepKey 待判断的流程节点状态键
 * @returns completed 已完成 / current 当前进行中 / pending 未到达
 */
export function getFlowStepState(
  currentKey: OrderStatusKey,
  stepKey: OrderStatusKey,
): 'completed' | 'current' | 'pending' {
  if (currentKey === 'cancelled') return 'pending'
  if (currentKey === 'completed') return 'completed'

  const currentIdx = STEP_ORDER.indexOf(currentKey)
  const stepIdx = STEP_ORDER.indexOf(stepKey)

  if (stepIdx < currentIdx) return 'completed'
  if (stepIdx === currentIdx) return 'current'
  return 'pending'
}

/**
 * 根据订单状态与存证基准日期，生成各流程节点的 mock 存证时间。
 * @param statusKey 订单当前履约状态键
 * @param baseDate 存证基准日期，格式 YYYY-MM-DD
 * @returns 各状态键对应的 FlowTimestamp 或 null（未到达节点为 null）
 */
export function buildFlowTimestamps(
  statusKey: OrderStatusKey,
  baseDate: string,
): Record<OrderStatusKey, FlowTimestamp | null> {
  const times: Record<string, { time: string; date: string }> = {
    submitted: { time: '09:30:00', date: baseDate },
    deposit_paid: { time: '09:45:32', date: baseDate },
    document_prep: { time: '12:12:19', date: baseDate },
    signed: { time: '14:20:08', date: baseDate },
    delivering: { time: '16:05:44', date: baseDate },
    completed: { time: '18:30:00', date: baseDate },
  }

  const result: Record<OrderStatusKey, FlowTimestamp | null> = {
    submitted: null,
    deposit_paid: null,
    document_prep: null,
    signed: null,
    delivering: null,
    completed: null,
    cancelled: null,
  }

  if (statusKey === 'cancelled') return result

  const currentIdx = statusKey === 'completed'
    ? STEP_ORDER.length
    : STEP_ORDER.indexOf(statusKey)

  STEP_ORDER.forEach((key, idx) => {
    if (idx < currentIdx) {
      result[key] = times[key]
    } else if (idx === currentIdx && statusKey !== 'completed') {
      result[key] = times[key]
    }
  })

  if (statusKey === 'completed') {
    STEP_ORDER.forEach((key) => {
      result[key] = times[key]
    })
  }

  return result
}

/**
 * 按订单 ID 组装全景详情（车辆、买卖双方、托管、流程存证等）。
 * 列表基础数据来自 ORDER_LIST，差异化字段由 DETAIL_OVERRIDES 覆盖，缺省值自动填充。
 * @param orderId 订单号
 * @returns 完整 OrderDetail，订单不存在时返回 null
 */
export function buildOrderDetail(orderId: string): OrderDetail | null {
  const base = ORDER_LIST.find((o) => o.id === orderId)
  if (!base) return null

  const override = DETAIL_OVERRIDES[orderId] ?? {}
  const depositAmount = base.amount.includes('114') ? '￥5,000' : '￥10,000'

  return {
    ...base,
    contractId: override.contractId ?? `CTR-${orderId.slice(-8)}`,
    vehiclePrice: override.vehiclePrice ?? base.amount.replace('¥', '￥'),
    buyerDeposit: override.buyerDeposit ?? depositAmount,
    sellerDeposit: override.sellerDeposit ?? depositAmount,
    vin: override.vin ?? 'LHGXXXXXXXXXXXXXX',
    firstRegistration: override.firstRegistration ?? '2022年05月15日',
    mileage: override.mileage ?? '3.5 万公里',
    conditionGrade: override.conditionGrade ?? '92B',
    vehicleSpecs: override.vehicleSpecs ?? DEFAULT_SPECS,
    vehicleDescription:
      override.vehicleDescription ??
      `${base.brand} ${base.model}，平台认证车源，车况良好，支持三方验车。`,
    buyerInfo: override.buyerInfo ?? {
      id: `USR-${base.buyer.length}${base.id.slice(-4)}`,
      name: base.buyer,
      phone: '13800000000',
      idMasked: '320*************',
      verifyText: '已通过 5D平台实名认证',
    },
    sellerInfo: override.sellerInfo ?? {
      id: `USR-${base.seller.length}${base.id.slice(-4)}`,
      name: base.seller,
      phone: '13900000000',
      idMasked: '320*************',
      verifyText: '已验证 平台入驻商户资质',
    },
    escrowBank: override.escrowBank ?? '网商银行',
    escrowStatus:
      override.escrowStatus ??
      `买方提报定金 ${depositAmount} 已冻结 (由商网信托三方监管户存管) | 卖方违约保证金 ${depositAmount} 已锁定划拨。`,
    deliveryStatus:
      override.deliveryStatus ??
      (base.statusKey === 'completed'
        ? '已完成交车过户及发票核验'
        : '待履约 (需待三方买卖协议签署、物流到店接车验验通过后解锁)'),
    disputeStatus:
      override.disputeStatus ??
      (base.statusKey === 'delivering' ? '争议处理中，待运营核审' : '暂无争议记录'),
    flowTimestamps: buildFlowTimestamps(base.statusKey, base.evidenceDate),
  }
}
