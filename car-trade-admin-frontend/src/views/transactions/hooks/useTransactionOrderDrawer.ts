/** 交易订单详情抽屉 composable */
import { computed, ref } from 'vue'
import { FLOW_STEP_DEFS } from './constants'
import { buildOrderDetail, getFlowStepState } from './orderDetailUtils'
import type { OrderDetail } from './types'

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

/**
 * 构建订单详情、履约流程节点与抽屉交互状态。
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

  /** 按 orderId 构建的订单全景详情，无 ID 或订单不存在时为 null */
  const detail = computed<OrderDetail | null>(() => {
    if (!props.orderId) return null
    return buildOrderDetail(props.orderId)
  })

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
  }
}
