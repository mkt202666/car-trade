<template>

  <el-drawer
    v-model="visible"
    direction="rtl"
    :size="drawerSize"
    :show-close="false"
    :with-header="false"
    class="order-detail-drawer"
    destroy-on-close
    @closed="emit('closed')"
  >
    <div v-if="detail" class="order-drawer__inner">
      <div class="order-drawer__header">
        <div>
          <h2 class="order-drawer__title">全景核验详情</h2>
          <p class="order-drawer__subtitle">
            {{ detail.brand }} · {{ detail.model }}
            <span class="order-drawer__order-id">{{ detail.id }}</span>
          </p>
        </div>
        <el-button class="order-drawer__close" circle @click="visible = false">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>

      <div class="order-drawer__body">
        <!-- 履约流程节点 -->
        <section class="flow-section">
          <span class="flow-section__label">5D-AUTO 履约流程节点流转状态 (精确至秒级存证)</span>
          <div class="flow-grid">
            <div
              v-for="step in flowSteps"
              :key="step.key"
              :class="['flow-step', `flow-step--${step.state}`]"
            >
              <div class="flow-step__icon">
                <el-icon><Check /></el-icon>
              </div>
              <span class="flow-step__label">{{ step.label }}</span>
              <div class="flow-step__time">
                <template v-if="step.timestamp">
                  <span class="flow-step__clock">
                    <el-icon><Clock /></el-icon>
                    {{ step.timestamp.time }}
                  </span>
                  <span class="flow-step__date">{{ step.timestamp.date }}</span>
                </template>
                <span v-else class="flow-step__empty">—</span>
              </div>
            </div>
          </div>
        </section>

        <!-- 清算总揽 -->
        <section class="detail-card">
          <div class="detail-card__head">
            <span class="detail-card__title">数字化交单清算与托运规费总揽 (只读账本)</span>
            <span class="detail-card__meta">合同编号: {{ detail.contractId }}</span>
          </div>
          <div class="amount-grid">
            <div class="amount-cell">
              <span class="amount-cell__label">成交车辆价 (CNY)</span>
              <span class="amount-cell__value">{{ detail.vehiclePrice }}</span>
            </div>
            <div class="amount-cell">
              <span class="amount-cell__label">买方保证金</span>
              <div class="amount-cell__row">
                <span class="amount-cell__value">{{ detail.buyerDeposit }}</span>
                <el-tag type="primary" effect="light" size="small">已支付</el-tag>
              </div>
            </div>
            <div class="amount-cell">
              <span class="amount-cell__label">卖方保证金</span>
              <div class="amount-cell__row">
                <span class="amount-cell__value">{{ detail.sellerDeposit }}</span>
                <el-tag type="primary" effect="light" size="small">已支付</el-tag>
              </div>
            </div>
          </div>
        </section>

        <!-- 车源 / 实名 / 资金 / 验车 / 争议 -->
        <section class="detail-stack">
          <div class="sub-card">
            <div class="sub-card__head">
              <div class="sub-card__title-row">
                <el-icon><Van /></el-icon>
                <span>商品车源状态及挂牌描述</span>
              </div>
              <el-link type="primary" :underline="false" class="sub-card__link">
                查看车源详情
                <el-icon><ArrowRight /></el-icon>
              </el-link>
            </div>
            <div class="info-grid info-grid--4">
              <div>
                <span class="info-label">车架号 (VIN)</span>
                <div class="info-value info-value--mono" :title="detail.vin">{{ detail.vin }}</div>
              </div>
              <div>
                <span class="info-label">首牌日期</span>
                <div class="info-value info-value--mono">{{ detail.firstRegistration }}</div>
              </div>
              <div>
                <span class="info-label">表显里程</span>
                <div class="info-value info-value--mono">{{ detail.mileage }}</div>
              </div>
              <div>
                <span class="info-label">车况评级</span>
                <div class="info-value info-value--mono">{{ detail.conditionGrade }}</div>
              </div>
            </div>
            <div class="spec-grid">
              <div v-for="spec in detail.vehicleSpecs" :key="spec.label">
                <span class="spec-label">{{ spec.label }}</span>
                <span class="spec-value">{{ spec.value }}</span>
              </div>
            </div>
            <div>
              <span class="info-label">卖方补充车源描述参数</span>
              <p class="vehicle-desc">{{ detail.vehicleDescription }}</p>
            </div>
          </div>

          <div class="sub-card">
            <div class="sub-card__head sub-card__head--simple">
              <el-icon><Check /></el-icon>
              <span>买卖双方实名认证</span>
            </div>
            <div class="party-block">
              <div class="party-block__title">
                买方实名信息
                <span class="party-block__id">(ID: {{ detail.buyerInfo.id }})</span>
              </div>
              <div class="party-grid">
                <div class="party-grid__name">{{ detail.buyerInfo.name }}</div>
                <div class="party-grid__mono">{{ detail.buyerInfo.phone }}</div>
                <div class="party-grid__mono">证件: {{ detail.buyerInfo.idMasked }}</div>
                <div class="party-grid__verified">
                  <el-icon><Check /></el-icon>
                  {{ detail.buyerInfo.verifyText }}
                </div>
              </div>
            </div>
            <div class="party-block party-block--bordered">
              <div class="party-block__title">
                卖方实名信息
                <span class="party-block__id">(ID: {{ detail.sellerInfo.id }})</span>
              </div>
              <div class="party-grid">
                <div class="party-grid__name">{{ detail.sellerInfo.name }}</div>
                <div class="party-grid__mono">{{ detail.sellerInfo.phone }}</div>
                <div class="party-grid__mono">证件: {{ detail.sellerInfo.idMasked }}</div>
                <div class="party-grid__verified">
                  <el-icon><Check /></el-icon>
                  {{ detail.sellerInfo.verifyText }}
                </div>
              </div>
            </div>
          </div>

          <div class="sub-card">
            <div class="sub-card__head">
              <div class="sub-card__title-row">
                <el-icon><Medal /></el-icon>
                <span>资金监管托管</span>
              </div>
              <el-tag effect="plain" size="small">{{ detail.escrowBank }}</el-tag>
            </div>
            <div class="escrow-grid">
              <div>
                <span class="info-label">买方保证金</span>
                <div class="info-value info-value--mono escrow-amount">{{ detail.buyerDeposit }}</div>
                <el-tag effect="plain" size="small" class="escrow-tag">锁定中</el-tag>
                <div class="penalty-row">
                  <el-input
                    v-model="buyerPenalty"
                    placeholder="罚扣金额"
                    size="small"
                    class="penalty-input"
                  />
                  <el-button type="primary" plain size="small">更新</el-button>
                </div>
              </div>
              <div>
                <span class="info-label">卖方保证金</span>
                <div class="info-value info-value--mono escrow-amount">{{ detail.sellerDeposit }}</div>
                <el-tag effect="plain" size="small" class="escrow-tag">锁定中</el-tag>
                <div class="penalty-row">
                  <el-input
                    v-model="sellerPenalty"
                    placeholder="罚扣金额"
                    size="small"
                    class="penalty-input"
                  />
                  <el-button type="primary" plain size="small">更新</el-button>
                </div>
              </div>
              <div>
                <span class="info-label">流水状态</span>
                <p class="escrow-desc">{{ detail.escrowStatus }}</p>
              </div>
            </div>
          </div>

          <div class="sub-card">
            <div class="sub-card__head sub-card__head--simple">
              <el-icon><Van /></el-icon>
              <span>验车交付</span>
            </div>
            <div>
              <span class="info-label">发票及提单状态</span>
              <div class="info-value">{{ detail.deliveryStatus }}</div>
            </div>
            <div class="proof-grid">
              <div class="proof-placeholder">暂无买方交车凭证。</div>
              <div class="proof-placeholder">暂无卖方交车凭证。</div>
            </div>
          </div>

          <div class="sub-card">
            <div class="sub-card__head sub-card__head--simple">
              <el-icon><Warning /></el-icon>
              <span>争议处理</span>
            </div>
            <div>
              <span class="info-label">争议状态</span>
              <div class="info-value">{{ detail.disputeStatus }}</div>
            </div>
          </div>
        </section>

        <!-- 运营干预 -->
        <section class="admin-panel">
          <div class="admin-panel__head">
            <span class="admin-panel__tag">Administrative Controls Only</span>
            <p class="admin-panel__title">运营手动干预控制台</p>
          </div>
          <div class="admin-panel__body">
            <div class="admin-panel__warn">
              <strong>⚠️ 交易合同中止流程授权</strong>
              <p>
                根据5D合规章程，运营不能任意篡改流转状态。若买卖一方单方毁约、车辆发生破损爆仓或资料验证失败，您仅可在此输入争议理由并一键终止该订单。
              </p>
            </div>
            <div class="admin-panel__actions">
              <el-input
                v-model="terminateReason"
                placeholder="在此输入终止原因与核审备注..."
                size="small"
                class="admin-panel__input"
              />
              <el-button type="danger" size="small" class="admin-panel__btn">
                <el-icon><Warning /></el-icon>
                手动终止订单
              </el-button>
            </div>
          </div>
        </section>
      </div>
    </div>
  </el-drawer>

</template>

<script setup lang="ts">
/**
 * 交易订单全景详情抽屉组件
 * 右侧滑出展示履约流程节点、清算账本、车源/实名/托管/验车/争议信息及运营手动终止控制台。
 * 详情数据由 orderId 驱动，关闭时通过 closed 事件通知父组件清理选中订单。
 */
import { ArrowRight, Check, Clock, Close, Medal, Van, Warning } from '@element-plus/icons-vue'
import { useTransactionOrderDrawer } from './hooks/useTransactionOrderDrawer'

const props = defineProps<{
  /** 控制抽屉显示/隐藏，支持 v-model */
  modelValue: boolean
  /** 待查看的订单号；为 null 时不加载详情 */
  orderId: string | null
}>()

const emit = defineEmits<{
  /** 更新抽屉显隐状态，用于 v-model */
  'update:modelValue': [value: boolean]
  /** 抽屉完全关闭后触发，父组件可据此清空 selectedOrderId */
  closed: []
}>()

const {
  visible,
  buyerPenalty,
  sellerPenalty,
  terminateReason,
  drawerSize,
  detail,
  flowSteps,
} = useTransactionOrderDrawer(props, emit)

</script>

<style lang="scss" scoped>

.order-drawer__inner {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.order-drawer__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  flex-shrink: 0;
}

.order-drawer__title {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
}

.order-drawer__subtitle {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.order-drawer__order-id {
  margin-left: 8px;
  font-family: ui-monospace, monospace;
  font-size: 11px;
  color: var(--text-muted);
}

.order-drawer__close {
  flex-shrink: 0;
}

.order-drawer__body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  background: rgba(249, 250, 251, 0.5);
}

.flow-section {
  padding: 16px;
  background: rgba(249, 250, 251, 0.5);
  border: 1px solid var(--border-color);
  border-radius: 16px;

  &__label {
    display: block;
    font-size: 10px;
    font-weight: 700;
    color: var(--text-muted);
    letter-spacing: 0.05em;
    margin-bottom: 12px;
  }
}

.flow-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;

  @media (min-width: 640px) {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  @media (min-width: 1024px) {
    grid-template-columns: repeat(6, minmax(0, 1fr));
  }
}

.flow-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 6px;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid transparent;
  transition: all 0.3s;

  &--completed {
    background: rgba(16, 185, 129, 0.08);
    border-color: #a7f3d0;
    color: #065f46;

    .flow-step__icon {
      background: #10b981;
      color: #fff;
    }

    .flow-step__label {
      color: #022c22;
    }
  }

  &--current {
    background: linear-gradient(to top right, #eef2ff, #e0e7ff);
    border-color: #c7d2fe;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    box-shadow: 0 0 0 1px #a5b4fc;
    font-weight: 700;

    .flow-step__icon {
      background: #4f46e5;
      color: #fff;
    }

    .flow-step__label {
      color: #312e81;
    }
  }

  &--pending {
    background: rgba(249, 250, 251, 0.4);
    border-color: var(--border-color);
    color: #d1d5db;

    .flow-step__icon {
      background: #f3f4f6;
      color: #d1d5db;
    }

    .flow-step__label {
      color: #9ca3af;
    }
  }

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    border-radius: 9999px;
    font-size: 12px;
  }

  &__label {
    font-size: 10.5px;
    font-weight: 700;
  }

  &__time {
    width: 100%;
    padding-top: 6px;
    margin-top: 4px;
    border-top: 1px dashed #e5e7eb;
  }

  &__clock {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 2px;
    font-size: 7.5px;
    font-family: ui-monospace, monospace;
    font-weight: 700;
    color: var(--text-muted);
    transform: scale(0.9);
  }

  &__date {
    display: block;
    font-size: 7px;
    font-family: ui-monospace, monospace;
    color: var(--text-muted);
    transform: scale(0.9);
  }

  &__empty {
    font-size: 8px;
    font-family: ui-monospace, monospace;
    color: #d1d5db;
  }
}

.detail-card {
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  &__head {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
    gap: 8px;
    padding-bottom: 8px;
    margin-bottom: 16px;
    border-bottom: 1px solid var(--border-color);
  }

  &__title {
    font-size: 12px;
    font-weight: 800;
    color: #030712;
  }

  &__meta {
    font-size: 10px;
    font-family: ui-monospace, monospace;
    color: var(--text-muted);
  }
}

.amount-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;

  @media (min-width: 768px) {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

.amount-cell {
  padding: 12px;
  background: rgba(249, 250, 251, 0.5);
  border: 1px solid rgba(243, 244, 246, 0.6);
  border-radius: 12px;
  font-family: ui-monospace, monospace;

  &__label {
    display: block;
    font-size: 10px;
    color: var(--text-muted);
    font-family: inherit;
  }

  &__value {
    display: block;
    margin-top: 4px;
    font-size: 13px;
    font-weight: 700;
    color: #1f2937;
  }

  &__row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 4px;
  }
}

.detail-stack {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.sub-card {
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 12px;

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding-bottom: 12px;
    margin-bottom: 16px;
    border-bottom: 1px solid var(--border-color);

    &--simple {
      justify-content: flex-start;
      gap: 8px;
      color: #1f2937;
      font-weight: 700;
      font-size: 14px;
    }
  }

  &__title-row {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 700;
    font-size: 14px;
    color: #1f2937;
  }

  &__link {
    font-size: 11px;
    font-weight: 500;
  }
}

.info-grid {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;

  &--4 {
    grid-template-columns: repeat(2, minmax(0, 1fr));

    @media (min-width: 768px) {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
  }
}

.info-label {
  display: block;
  margin-bottom: 4px;
  font-size: 12px;
  color: var(--text-muted);
}

.info-value {
  font-size: 12px;
  color: #1f2937;

  &--mono {
    font-family: ui-monospace, monospace;
    font-weight: 500;
  }
}

.spec-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 8px;
  padding: 12px;
  margin-bottom: 16px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 12px;

  @media (min-width: 640px) {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  @media (min-width: 768px) {
    grid-template-columns: repeat(8, minmax(0, 1fr));
  }
}

.spec-label {
  display: block;
  font-size: 10px;
  color: var(--text-muted);
}

.spec-value {
  color: #1f2937;
}

.vehicle-desc {
  margin: 0;
  font-size: 11px;
  line-height: 1.6;
  color: #1f2937;
}

.party-block {
  & + & {
    margin-top: 16px;
  }

  &--bordered {
    padding-top: 16px;
    border-top: 1px solid var(--border-color);
  }

  &__title {
    margin-bottom: 8px;
    font-size: 14px;
    font-weight: 700;
    color: #1f2937;
  }

  &__id {
    font-size: 12px;
    font-weight: 400;
    color: var(--text-secondary);
    margin-left: 4px;
  }
}

.party-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  font-size: 11px;

  @media (min-width: 768px) {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  &__name {
    font-weight: 500;
    color: #1f2937;
  }

  &__mono {
    font-family: ui-monospace, monospace;
    color: var(--text-secondary);
  }

  &__verified {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #16a34a;
  }
}

.escrow-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;

  @media (min-width: 768px) {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

.escrow-amount {
  font-size: 14px;
  margin-bottom: 4px;
}

.escrow-tag {
  margin-bottom: 8px;
}

.escrow-desc {
  margin: 0;
  font-size: 11px;
  line-height: 1.6;
  color: #374151;
}

.penalty-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}

.penalty-input {
  width: 80px;
}

.proof-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  margin-top: 8px;

  @media (min-width: 640px) {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

.proof-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 120px;
  padding: 8px;
  text-align: center;
  color: var(--text-muted);
  background: var(--bg-elevated);
  border: 1px solid #e5e7eb;
  border-radius: 4px;
}

.admin-panel {
  padding: 16px;
  background: var(--bg-card);
  border: 1px solid rgba(229, 231, 235, 0.4);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  &__head {
    padding-bottom: 8px;
    margin-bottom: 12px;
    border-bottom: 1px solid var(--border-color);
  }

  &__tag {
    display: block;
    font-size: 10px;
    font-family: ui-monospace, monospace;
    font-weight: 700;
    color: #e11d48;
    letter-spacing: 0.05em;
    text-transform: uppercase;
  }

  &__title {
    margin: 4px 0 0;
    font-size: 12px;
    font-weight: 800;
    color: var(--text-primary);
  }

  &__body {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 18px;
    background: rgba(254, 242, 242, 0.4);
    border: 1px solid #fecaca;
    border-radius: 12px;

    @media (min-width: 768px) {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
    }
  }

  &__warn {
    flex: 1;
    font-size: 10.5px;
    color: #991b1b;
    line-height: 1.6;

    strong {
      display: block;
      font-size: 12px;
      color: #450a0a;
      margin-bottom: 4px;
    }

    p {
      margin: 0;
      color: #9f1239;
    }
  }

  &__actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 100%;

    @media (min-width: 640px) {
      flex-direction: row;
      align-items: center;
      width: auto;
    }
  }

  &__input {
    width: 100%;

    @media (min-width: 640px) {
      width: 240px;
    }
  }

  &__btn {
    flex-shrink: 0;
  }
}

</style>

<style lang="scss">
.order-detail-drawer {
  .el-drawer__body {
    padding: 0 !important;
    height: 100%;
    overflow: hidden;
  }
}
</style>
