<template>

  <div class="page deposit-page">
    <PageHeader
      title="保证金现金流"
      subtitle="全景核验及调整关联主体在信贷和保证金科目的资金变动与流转账目明细。"
    />

    <section class="stats-grid">
      <div v-for="stat in summaryStats" :key="stat.label" class="summary-card">
        <div class="summary-card__head">
          <span class="summary-card__label">{{ stat.label }}</span>
          <div class="summary-card__icon" :class="`summary-card__icon--${stat.iconBg}`">
            <component :is="stat.icon" />
          </div>
        </div>
        <h3 class="summary-card__value">{{ stat.value }}</h3>
      </div>
    </section>

    <div class="filter-bar">
      <div class="filter-bar__controls">
        <el-input
          v-model="keyword"
          class="filter-bar__search"
          placeholder="搜索流水ID、客户姓名、或客户ID"
          clearable
          :prefix-icon="Search"
        />
        <div class="filter-select">
          <el-icon class="filter-select__icon"><Filter /></el-icon>
          <el-select v-model="typeFilter" class="filter-select__input" placeholder="所有的往来流水类型">
            <el-option label="所有的往来流水类型" value="all" />
            <el-option label="保证金网银充值" value="recharge" />
            <el-option label="冻结用于竞拍" value="freeze" />
            <el-option label="解冻回归原账户" value="release" />
          </el-select>
        </div>
      </div>
      <el-button type="primary" class="filter-bar__action" @click="openManualDialog">
        <el-icon><Plus /></el-icon>
        人工记账/划扣保证金
      </el-button>
    </div>

    <div class="table-panel">
      <el-table :data="paginatedFlows" style="width: 100%" class="deposit-table">
        <el-table-column label="交易序列号/时间" min-width="160">
          <template #default="{ row }">
            <div class="cell-tx">
              <span class="cell-tx__id">{{ row.id }}</span>
              <span class="cell-tx__time">{{ row.time }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="往来账户客户" min-width="140">
          <template #default="{ row }">
            <div class="cell-customer">
              <span class="cell-customer__name">{{ row.customerName }}</span>
              <span class="cell-customer__id">{{ row.customerId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="交易科目类型" min-width="150">
          <template #default="{ row }">
            <el-tag
              :type="flowTypeTag(row.typeKey).type"
              :effect="flowTypeTag(row.typeKey).effect"
              size="small"
              round
              class="type-tag"
            >
              {{ row.typeLabel }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="交易金额" min-width="120">
          <template #default="{ row }">
            <span
              class="cell-amount"
              :class="row.amountSign === '+' ? 'cell-amount--plus' : 'cell-amount--minus'"
            >
              {{ row.amountSign }} ¥ {{ row.amountValue }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="余额变动情况" min-width="120">
          <template #default="{ row }">
            <span class="cell-balance">¥ {{ row.balance }}</span>
          </template>
        </el-table-column>

        <el-table-column label="往来摘要备注" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-note">{{ row.note }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <div class="pagination__info">
          <span>每页行数:</span>
          <el-select v-model="pageSize" class="page-size-select" size="small">
            <el-option :value="10" label="10" />
            <el-option :value="20" label="20" />
            <el-option :value="50" label="50" />
          </el-select>
          <span>
            显示 {{ pageRangeStart }}-{{ pageRangeEnd }} 项，共
            <strong>{{ filteredFlows.length }}</strong> 项
          </span>
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredFlows.length"
          layout="prev, pager, next"
          background
        />
      </div>
    </div>

    <el-dialog
      v-model="manualDialogVisible"
      width="384px"
      class="manual-dialog"
      :show-close="true"
      align-center
      destroy-on-close
      @closed="resetManualForm"
    >
      <template #header>
        <div class="manual-dialog__header">
          <h4 class="manual-dialog__title">人工手动调配划销保证金</h4>
          <p class="manual-dialog__hint">
            执行强制补扣或资金解冻时，请提供真实的流转事由说明
          </p>
        </div>
      </template>

      <el-form
        ref="manualFormRef"
        :model="manualForm"
        :rules="manualRules"
        label-position="top"
        class="manual-form"
        @submit.prevent="submitManualForm"
      >
        <el-form-item label="目标核算商户/买家 *" prop="customerId" class="manual-form__item">
          <el-select
            v-model="manualForm.customerId"
            class="manual-form__select"
            placeholder="选择核算主体"
          >
            <el-option
              v-for="account in depositAccounts"
              :key="account.id"
              :label="formatAccountLabel(account)"
              :value="account.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="划转科目 *" prop="subjectKey" class="manual-form__item">
          <el-select v-model="manualForm.subjectKey" class="manual-form__select">
            <el-option
              v-for="subject in subjectOptions"
              :key="subject.value"
              :label="subject.label"
              :value="subject.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="记账金额 (CNY) *" prop="amount" class="manual-form__item">
          <el-input
            v-model="manualForm.amount"
            type="number"
            min="0"
            class="manual-form__amount"
          />
        </el-form-item>

        <el-form-item label="往来事由与单据摘要 *" prop="note" class="manual-form__item">
          <el-input
            v-model="manualForm.note"
            type="textarea"
            :rows="2"
            class="manual-form__textarea"
            placeholder="如: 手工扣划锁定用于奥迪A6L网拍临时担保协议"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="manual-dialog__footer">
          <el-button class="manual-dialog__cancel" @click="manualDialogVisible = false">
            取消
          </el-button>
          <el-button
            type="primary"
            class="manual-dialog__submit"
            :loading="manualSubmitting"
            @click="submitManualForm"
          >
            确认扣过记账
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>

</template>

<script setup lang="ts">
/**
 * 保证金现金流页面
 * 展示冻结定金、流水规模等汇总指标，支持按关键词与流水类型筛选分页查询；
 * 提供人工记账弹窗，可对指定核算主体执行充值、冻结、解冻或退还操作。
 */

import { Filter, Plus, Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import { useDeposit } from './hooks/useDeposit'

const {
  ShieldCheckIcon,
  CreditCardIcon,
  BuildingIcon,
  summaryStats,
  keyword,
  typeFilter,
  currentPage,
  pageSize,
  flows,
  depositAccounts,
  subjectOptions,
  manualDialogVisible,
  manualSubmitting,
  manualFormRef,
  manualForm,
  manualRules,
  filteredFlows,
  paginatedFlows,
  pageRangeStart,
  pageRangeEnd,
  flowTypeTag,
  formatAccountLabel,
  formatAmountValue,
  findAccount,
  openManualDialog,
  resetManualForm,
  submitManualForm,
} = useDeposit()

</script>

<style lang="scss" scoped>

@import '../../styles/page.css';

.deposit-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.summary-card {
  padding: 20px;
  border-radius: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  &__head {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 12px;
  }

  &__label {
    font-size: 12px;
    font-weight: 500;
    color: var(--text-muted);
    line-height: 1.4;
  }

  &__icon {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 8px;
    border-radius: 12px;

    &--amber {
      color: #d97706;
      background: #fffbeb;
    }

    &--emerald {
      color: #059669;
      background: #ecfdf5;
    }

    &--indigo {
      color: var(--color-primary-dark);
      background: var(--color-primary-soft);
    }
  }

  &__value {
    margin: 16px 0 0;
    font-size: 30px;
    font-weight: 700;
    font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
    letter-spacing: -0.025em;
    color: var(--text-primary);
    line-height: 1.2;
  }
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  &__controls {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 12px;
    flex: 1;
    min-width: 0;
  }

  &__search {
    flex: 1;
    min-width: 200px;
    max-width: 320px;

    :deep(.el-input__wrapper) {
      background: var(--bg-elevated);
      box-shadow: 0 0 0 1px var(--border-color) inset;
      border-radius: 8px;
      padding: 4px 12px 4px 8px;
    }

    :deep(.el-input__inner) {
      font-size: 12px;
      height: 28px;
    }
  }

  &__action {
    flex-shrink: 0;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 700;
    padding: 8px 16px;
    height: auto;
  }
}

.filter-select {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 8px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-color);
  border-radius: 8px;

  &__icon {
    font-size: 14px;
    color: var(--text-muted);
    flex-shrink: 0;
  }

  &__input {
    width: 160px;

    :deep(.el-select__wrapper) {
      background: transparent;
      box-shadow: none !important;
      padding-left: 0;
      min-height: 30px;
    }

    :deep(.el-select__placeholder),
    :deep(.el-select__selected-item) {
      font-size: 11px;
      font-weight: 500;
      color: var(--text-secondary);
    }
  }
}

.table-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 4px 0 16px;
  overflow: hidden;
}

.deposit-table {
  :deep(.el-table__header th) {
    font-size: 11px;
    font-weight: 600;
    color: var(--text-muted);
    background: var(--bg-elevated);
  }

  :deep(.el-table__row td) {
    font-size: 13px;
  }
}

.cell-tx {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__id {
    font-weight: 600;
    color: var(--text-primary);
    font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
    font-size: 12px;
  }

  &__time {
    font-size: 11px;
    color: var(--text-muted);
  }
}

.cell-customer {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__name {
    font-weight: 600;
    color: var(--text-primary);
  }

  &__id {
    font-size: 11px;
    color: var(--text-muted);
    font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
  }
}

.type-tag {
  font-weight: 600;
  border: none;

  &.el-tag--info.el-tag--plain {
    color: #7c3aed;
    background: #f5f3ff;
    border: 1px solid #ddd6fe;
  }
}

.cell-amount {
  font-weight: 700;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
  font-size: 13px;

  &--plus {
    color: #16a34a;
  }

  &--minus {
    color: #dc2626;
  }
}

.cell-balance {
  font-weight: 600;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
  color: var(--text-primary);
}

.cell-note {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.pagination {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 20px 0;

  &__info {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 12px;
    font-size: 12px;
    color: var(--text-muted);
  }
}

.page-size-select {
  width: 72px;
}

.manual-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
    border: 1px solid #f3f4f6;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  }

  :deep(.el-dialog__header) {
    padding: 24px 24px 0;
    margin-right: 0;
  }

  :deep(.el-dialog__body) {
    padding: 16px 24px 8px;
  }

  :deep(.el-dialog__footer) {
    padding: 8px 24px 24px;
  }

  &__header {
    padding-right: 24px;
  }

  &__title {
    margin: 0;
    font-size: 14px;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.4;
  }

  &__hint {
    margin: 4px 0 0;
    font-size: 12px;
    color: var(--text-muted);
    line-height: 1.5;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding-top: 8px;
  }

  &__cancel {
    font-size: 12px;
    font-weight: 700;
    color: #4b5563;
    background: #f3f4f6;
    border: none;
    border-radius: 8px;
    padding: 8px 12px;
    height: auto;

    &:hover {
      background: #e5e7eb;
      color: #4b5563;
    }
  }

  &__submit {
    font-size: 12px;
    font-weight: 700;
    border-radius: 8px;
    padding: 8px 16px;
    height: auto;
    background: #4f46e5;
    border-color: #4f46e5;

    &:hover {
      background: #4338ca;
      border-color: #4338ca;
    }
  }
}

.manual-form {
  font-size: 12px;

  &__item {
    margin-bottom: 16px;

    :deep(.el-form-item__label) {
      font-size: 12px;
      font-weight: 500;
      color: var(--text-muted);
      line-height: 1.4;
      margin-bottom: 4px;
      padding: 0;
    }
  }

  &__select {
    width: 100%;

    :deep(.el-select__wrapper) {
      padding: 8px 12px;
      border-radius: 8px;
      box-shadow: 0 0 0 1px #e5e7eb inset;
      min-height: 36px;
    }

    :deep(.el-select__selected-item) {
      font-size: 12px;
      font-weight: 600;
      color: var(--text-secondary);
    }
  }

  &__amount {
    :deep(.el-input__wrapper) {
      padding: 8px 12px;
      border-radius: 8px;
      box-shadow: 0 0 0 1px #e5e7eb inset;
    }

    :deep(.el-input__inner) {
      font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
      font-size: 12px;
      font-weight: 700;
      color: var(--text-primary);
    }
  }

  &__textarea {
    :deep(.el-textarea__inner) {
      padding: 8px 12px;
      border-radius: 8px;
      box-shadow: 0 0 0 1px #e5e7eb inset;
      font-size: 12px;
      color: var(--text-secondary);
      line-height: 1.5;
    }
  }
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 749px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;

    &__search {
      max-width: none;
    }

    &__action {
      width: 100%;
    }
  }

  .summary-card__value {
    font-size: 24px;
  }
}

</style>
