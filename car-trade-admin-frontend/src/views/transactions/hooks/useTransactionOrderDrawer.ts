/** 交易订单详情抽屉 composable — 对接后端 getOrderDetail API */
import { computed, ref, watch } from 'vue'
import { getOrderDetail } from '../../../api/orders'
import type { OrderDetailVO } from '../../../api/orders'
import { FLOW_STEP_DEFS, STEP_ORDER } from './constants'
import { getFlowStepState } from './orderDetailUtils'
import type { FlowTimestamp, OrderDetail, OrderStatusKey, PartyInfo, VehicleSpec } from './types'

export type { OrderDetail, OrderStatusKey } from './types'

/** 抽屉组件 props 类型 */
interface DrawerProps {
  /** v-model 控制抽屉显隐 */
  modelValue: boolean
  /** 当前查看的订单 ID，为 null 时不渲染详情 */
  orderId: string | null
}

/** 抽屉组件 emit 类型 */
interface DrawerEmit {
  (e: 'update:modelValue', value: boolean): void
  (e: 'closed'): void
}

/* ------------------------------------------------------------------ */
/*  Backend status → frontend OrderStatusKey 映射                      */
/* ------------------------------------------------------------------ */

const STATUS_MAP: Record<string, OrderStatusKey> = {
  CREATED: 'submitted',
  PAID: 'deposit_paid',
  SHIPPED: 'signed',
  SIGNED: 'signed',
  COMPLETED: 'completed',
  CANCELLED: 'cancelled',
  DISPUTE: 'delivering',
  // 后端实际状态值
  PENDING_DEPOSIT: 'submitted',
  CONTRACT_DRAFT: 'deposit_paid',
  CONTRACT_SIGNED: 'signed',
  IN_TRANSIT: 'signed',
  TERMINATED: 'cancelled',
  // 兼容小写
  created: 'submitted',
  paid: 'deposit_paid',
  shipped: 'signed',
  signed: 'signed',
  completed: 'completed',
  cancelled: 'cancelled',
  dispute: 'delivering',
  pending_deposit: 'submitted',
  contract_draft: 'deposit_paid',
  contract_signed: 'signed',
  in_transit: 'signed',
  terminated: 'cancelled',
}

const STATUS_LABEL_MAP: Record<OrderStatusKey, string> = {
  submitted: '创建交易',
  deposit_paid: '创建合同',
  document_prep: '完成签约',
  signed: '交车过户',
  delivering: '争议处理',
  completed: '交易完成',
  cancelled: '已取消',
}

/* ------------------------------------------------------------------ */
/*  格式化工具                                                          */
/* ------------------------------------------------------------------ */

function formatCurrency(amount: number | null | undefined): string {
  if (amount == null || Number.isNaN(amount)) return '￥0'
  return `￥${amount.toLocaleString('zh-CN')}`
}

function formatMileage(km: number | null | undefined): string {
  if (km == null) return '—'
  if (km < 10000) return `${km} 公里`
  return `${(km / 10000).toFixed(1)} 万公里`
}

function formatDate(dateStr: string | null | undefined): string {
  if (!dateStr) return ''
  // Handle ISO datetime strings: "2026-05-30T09:30:00" → "2026-05-30"
  return dateStr.split('T')[0]
}

function formatTime(dateStr: string | null | undefined): string {
  if (!dateStr) return '09:00:00'
  // Handle ISO datetime strings: "2026-05-30T09:30:00" → "09:30:00"
  const parts = dateStr.split('T')
  return parts[1]?.slice(0, 8) || '09:00:00'
}

/* ------------------------------------------------------------------ */
/*  API → Template 数据映射                                             */
/* ------------------------------------------------------------------ */

/**
 * 将后端 OrderDetailVO 转换为模板所需的 OrderDetail 结构。
 * 缺失字段（如手机号、证件号等后端未返回的数据）使用占位值，
 * 标注 TODO 以便后续 API 扩展时补全。
 */
function mapApiOrderToDetail(api: OrderDetailVO): OrderDetail {
  const statusKey = STATUS_MAP[api.status] ?? 'submitted'
  const statusLabel = STATUS_LABEL_MAP[statusKey] ?? api.status ?? '未知'

  const depositAmount = api.depositAmount ?? 0
  const depositDisplay = formatCurrency(depositAmount)

  // ---------- 车辆规格参数 ----------
  const vehicleSpecs: VehicleSpec[] = [
    { label: '外观内饰', value: api.color || '—' },
    { label: '牌照归属', value: api.city || '—' },
    { label: '交强险到期', value: api.insuranceExpiry || '—' },
    { label: '年检到期', value: api.inspectionExpiry || '—' },
    { label: '使用性质', value: '非营运' },
    { label: '过户次数', value: api.transferCount != null ? `${api.transferCount} 次` : '—' },
    { label: '变速箱', value: api.transmission || '—' },
    { label: '排放标准', value: '—' },
    { label: '燃油类型', value: api.energyType || '—' },
    { label: '排量', value: '—' },
    { label: '座位数', value: '—' },
    { label: '驱动方式', value: '—' },
    { label: '新车指导价', value: '—' },
  ]

  // ---------- 车辆描述 ----------
  const descParts = [api.brandName, api.seriesName, api.modelName, api.year ? `${api.year}款` : ''].filter(Boolean)
  const vehicleDescription = [
    `${descParts.join(' ')}，平台认证车源，支持三方验车。`,
    api.paint ? `车漆: ${api.paint}` : '',
    api.structure ? `车身结构: ${api.structure}` : '',
    api.engine ? `发动机: ${api.engine}` : '',
    api.overallCondition ? `综合车况: ${api.overallCondition}` : '',
  ].filter(Boolean).join(' ')

  // ---------- 买卖双方实名信息 ----------
  const buyerInfo: PartyInfo = {
    id: api.buyerId ? `USR-${api.buyerId}` : '—',
    name: api.buyerName || '—',
    phone: api.buyerPhone || '—',
    idMasked: '—',
    verifyText: api.buyerDepositPaid
      ? '已通过 5D平台实名认证 (保证金已缴纳)'
      : '已通过 5D平台实名认证',
  }

  const sellerInfo: PartyInfo = {
    id: api.sellerId ? `USR-${api.sellerId}` : '—',
    name: api.sellerName || '—',
    phone: api.sellerPhone || '—',
    idMasked: '—',
    verifyText: api.sellerDepositPaid
      ? '已验证 平台入驻商户资质 (保证金已锁定)'
      : '已验证 平台入驻商户资质',
  }

  // ---------- 托管状态 ----------
  let escrowStatus: string
  switch (api.depositStatus) {
    case 'PAID':
      escrowStatus = `买方保证金 ${depositDisplay} 已冻结 (由三方监管户存管) | 卖方违约保证金 ${depositDisplay} 已锁定划拨。双向权益受托保护。`
      break
    case 'PARTIAL':
      escrowStatus = `保证金部分缴纳中，买方: ${api.buyerDepositPaid ? '已缴' : '未缴'}，卖方: ${api.sellerDepositPaid ? '已锁定' : '未锁定'}。`
      break
    case 'PARTIAL_REFUNDED':
      escrowStatus = '保证金部分退还，争议处理中。'
      break
    case 'REFUNDED':
      escrowStatus = '保证金已全额退还。'
      break
    default:
      escrowStatus = `买方保证金 ${depositDisplay} 待缴纳，卖方保证金待锁定。`
      break
  }

  // ---------- 交付状态 ----------
  let deliveryStatus: string
  if (api.status === 'COMPLETED') {
    deliveryStatus = '已完成交车过户及发票核验'
  } else if (api.cancelReason) {
    deliveryStatus = `订单已取消: ${api.cancelReason}`
  } else {
    deliveryStatus = '待履约 (需待三方买卖协议签署、物流到店接车验车通过后解锁)'
  }

  // ---------- 争议状态 ----------
  const disputeStatus = api.status === 'DISPUTE'
    ? '争议处理中，待运营审核'
    : '暂无争议记录'

  // ---------- 流程节点存证时间 ----------
  const currentIdx = statusKey === 'completed'
    ? STEP_ORDER.length
    : STEP_ORDER.indexOf(statusKey)

  const flowTimestamps = {} as Record<OrderStatusKey, FlowTimestamp | null>
  const createdAtDate = formatDate(api.createdAt) || new Date().toISOString().split('T')[0]
  const createdAtTime = formatTime(api.createdAt)

  STEP_ORDER.forEach((key, idx) => {
    if (statusKey === 'cancelled') {
      flowTimestamps[key] = null
    } else if (idx < currentIdx) {
      flowTimestamps[key] = { date: createdAtDate, time: createdAtTime }
    } else if (idx === currentIdx && statusKey !== 'completed') {
      flowTimestamps[key] = { date: createdAtDate, time: createdAtTime }
    } else {
      flowTimestamps[key] = null
    }
  })

  // completed 状态: 所有节点标记已完成
  if (statusKey === 'completed') {
    const completedDate = formatDate(api.completedAt) || createdAtDate
    const completedTime = formatTime(api.completedAt) || createdAtTime
    STEP_ORDER.forEach((key) => {
      flowTimestamps[key] = { date: completedDate, time: completedTime }
    })
  }

  // cancelled 也要有 null 作为键存在
  flowTimestamps.cancelled = null

  // ---------- 组装完整 OrderDetail ----------
  const brand = [api.brandName, api.seriesName].filter(Boolean).join(' ') || api.carName || '—'
  const model = api.modelName || api.carName || '—'

  return {
    id: api.id,
    brand,
    model,
    location: api.city || '—',
    buyer: api.buyerName || '—',
    seller: api.sellerName || '—',
    statusKey,
    statusLabel,
    amount: formatCurrency(api.totalPrice),
    evidenceDate: createdAtDate,
    contractId: api.contractNo || `CTR-${api.id?.slice(-8) || '00000000'}`,
    vehiclePrice: formatCurrency(api.totalPrice),
    buyerDeposit: depositDisplay,
    sellerDeposit: depositDisplay,
    vin: api.vin || '—',
    firstRegistration: api.registrationDate || '—',
    mileage: formatMileage(api.mileage),
    conditionGrade: api.overallCondition || '—',
    vehicleSpecs,
    vehicleDescription,
    buyerInfo,
    sellerInfo,
    escrowBank: '网商银行',
    escrowStatus,
    deliveryStatus,
    disputeStatus,
    flowTimestamps,
  }
}

/* ------------------------------------------------------------------ */
/*  Composable 主体                                                     */
/* ------------------------------------------------------------------ */

/**
 * 构建订单详情、履约流程节点与抽屉交互状态。
 * 当抽屉打开且 orderId 有效时，调用后端 getOrderDetail API 获取真实数据。
 * 在 TransactionOrderDrawer.vue 中配合 defineProps/emit 使用。
 * @param props 抽屉显隐与订单 ID
 * @param emit 更新 modelValue 与 closed 事件
 * @returns 抽屉 UI 绑定所需的 computed 与运营干预表单 ref
 */
export function useTransactionOrderDrawer(props: DrawerProps, emit: DrawerEmit) {
  /** 抽屉显隐，双向绑定 props.modelValue */
  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value),
  })

  /** 买方保证金罚扣金额输入（运营干预，待对接 API） */
  const buyerPenalty = ref('')
  /** 卖方保证金罚扣金额输入 */
  const sellerPenalty = ref('')
  /** 手动终止订单原因备注 */
  const terminateReason = ref('')

  /** 抽屉宽度：移动端全屏，桌面端最大 960px */
  const drawerSize = computed(() => {
    if (typeof window !== 'undefined' && window.innerWidth < 768) return '100%'
    return 'min(960px, 92vw)'
  })

  // ---------- 订单详情数据状态 ----------

  /** 是否正在加载订单详情 */
  const loading = ref(false)
  /** 加载错误信息，成功时为 null */
  const error = ref<string | null>(null)
  /** 订单全景详情，从后端 API 获取并映射 */
  const detail = ref<OrderDetail | null>(null)

  /**
   * 从后端获取订单详情并映射为模板数据结构。
   * 在 orderId 变更或抽屉打开时由 watch 自动触发。
   */
  async function fetchDetail(orderId: string) {
    loading.value = true
    error.value = null
    detail.value = null

    try {
      const apiOrder = await getOrderDetail(orderId)
      detail.value = mapApiOrderToDetail(apiOrder)
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取订单详情失败'
      console.error('Failed to fetch order detail:', e)
    } finally {
      loading.value = false
    }
  }

  /** 当 orderId 变更且抽屉可见时，自动拉取订单详情 */
  watch(
    () => [props.orderId, props.modelValue] as const,
    ([orderId, isOpen]) => {
      if (orderId && isOpen) {
        fetchDetail(orderId)
      } else if (!isOpen) {
        // 抽屉关闭时清理状态
        detail.value = null
        error.value = null
        loading.value = false
        buyerPenalty.value = ''
        sellerPenalty.value = ''
        terminateReason.value = ''
      }
    },
    { immediate: true },
  )

  /** 履约流程节点列表：含状态（completed/current/pending）与存证时间 */
  const flowSteps = computed(() => {
    if (!detail.value) return []

    const currentKey = detail.value.statusKey

    return FLOW_STEP_DEFS.map((step) => {
      const state = getFlowStepState(currentKey, step.key)
      const timestamp = detail.value!.flowTimestamps[step.key]

      return {
        key: step.key,
        label: step.label,
        state,
        timestamp: state === 'pending' ? null : timestamp,
      }
    })
  })

  return {
    visible,
    buyerPenalty,
    sellerPenalty,
    terminateReason,
    drawerSize,
    detail,
    flowSteps,
    loading,
    error,
  }
}
