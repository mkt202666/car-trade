<template>

  <div class="page purchase-page">
    <PageHeader
      title="求购管理"
      subtitle="管理前端展示的车商及个人求购意向，及AI匹配推荐情况。"
    />

    <div class="filter-bar">
      <div class="filter-bar__controls">
        <el-input
          v-model="keyword"
          class="filter-bar__search"
          placeholder="搜索品牌车型、发布人或车行..."
          clearable
          :prefix-icon="Search"
        />
      </div>
      <span class="filter-bar__count">
        共收录 <strong>{{ filteredPurchases.length }}</strong> 条求购信息
      </span>
    </div>

    <div class="table-panel">
      <el-table
        :data="paginatedPurchases"
        style="width: 100%"
        class="purchase-table"
        row-key="id"
      >
        <el-table-column label="求购品牌车型" min-width="200">
          <template #default="{ row }">
            <div class="cell-model">
              <span class="cell-model__brand">{{ row.brand }}</span>
              <span class="cell-model__trim">{{ row.trim }}</span>
              <span class="cell-model__id">ID: {{ row.id }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="发布人 (姓名/车行)" min-width="180">
          <template #default="{ row }">
            <div class="cell-publisher">
              <span class="cell-publisher__name">
                {{ row.publisher.name }}
                <span class="cell-publisher__dealer">({{ row.publisher.dealer }})</span>
              </span>
              <el-tag
                :type="row.publisher.type === 'dealer' ? 'primary' : 'info'"
                effect="light"
                size="small"
                round
                class="cell-publisher__tag"
              >
                {{ publisherTypeLabel(row.publisher.type) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="年限要求" min-width="110">
          <template #default="{ row }">
            <span class="cell-text">{{ row.yearRequirement }}</span>
          </template>
        </el-table-column>

        <el-table-column label="里程要求" min-width="110">
          <template #default="{ row }">
            <span class="cell-text">{{ row.mileageRequirement }}</span>
          </template>
        </el-table-column>

        <el-table-column label="颜色要求" min-width="120">
          <template #default="{ row }">
            <span class="cell-text">{{ row.colorRequirement }}</span>
          </template>
        </el-table-column>

        <el-table-column label="求购价格" min-width="110">
          <template #default="{ row }">
            <span class="cell-price">{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作区" width="120" fixed="right" align="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button size="small" class="action-btn action-btn--blue">
                <el-icon><MagicStick /></el-icon>
                AI参谋
              </el-button>
              <StatusBadge :status="purchaseStatus(row.status)" :label="row.status" />
            </div>
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
            <strong>{{ filteredPurchases.length }}</strong> 项
          </span>
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredPurchases.length"
          layout="prev, pager, next"
          background
        />
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
/**
 * 求购管理列表页
 * 展示车商及个人发布的求购意向，支持关键词搜索、分页与 AI 参谋入口（待对接）。
 * 列表数据与分页逻辑由 usePurchase 管理，状态标签通过 purchaseStatus 映射。
 */
import { MagicStick, Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import StatusBadge from '../../components/StatusBadge.vue'
import { usePurchase } from './hooks/usePurchase'

const {
  keyword,
  currentPage,
  pageSize,
  filteredPurchases,
  paginatedPurchases,
  pageRangeStart,
  pageRangeEnd,
  publisherTypeLabel,
  purchaseStatus,
} = usePurchase()

</script>

<style lang="scss" scoped>

@import '../../styles/page.css';

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px;
  margin-bottom: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
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
    min-width: 240px;

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

  &__count {
    flex-shrink: 0;
    font-size: 12px;
    color: var(--text-secondary);
    white-space: nowrap;

    strong {
      color: var(--text-primary);
      font-weight: 700;
    }
  }
}

.table-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  overflow: hidden;
}

.purchase-table {
  :deep(.el-table__header-wrapper th.el-table__cell) {
    background: #f8fafc !important;
    color: #6b7280;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 0.02em;
    border-bottom: 1px solid #e5e7eb;
    padding: 16px;
  }

  :deep(.el-table__body td.el-table__cell) {
    padding: 16px;
    vertical-align: top;
  }

  :deep(.el-table__body tr:hover > td.el-table__cell) {
    background: #f8fafc !important;
  }

  :deep(.el-table__inner-wrapper::before) {
    display: none;
  }
}

.cell-model {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__brand {
    font-size: 13px;
    font-weight: 700;
    color: var(--color-primary);
  }

  &__trim {
    font-size: 12px;
    font-weight: 500;
    color: var(--text-secondary);
  }

  &__id {
    margin-top: 4px;
    font-family: ui-monospace, monospace;
    font-size: 10px;
    color: var(--text-muted);
  }
}

.cell-publisher {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;

  &__name {
    font-size: 12px;
    font-weight: 700;
    color: var(--text-primary);
  }

  &__dealer {
    font-weight: 500;
    color: var(--text-secondary);
  }

  &__tag {
    height: auto;
    padding: 2px 8px;
    font-size: 10px;
    font-weight: 600;
  }
}

.cell-text {
  font-size: 12px;
  font-weight: 500;
  color: var(--text-secondary);
}

.cell-price {
  font-size: 13px;
  font-weight: 700;
  color: #e11d48;
  font-family: ui-monospace, monospace;
}

.table-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.action-btn {
  height: auto;
  padding: 4px 8px;
  font-size: 11px;
  font-weight: 500;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  color: #4b5563;
  border-radius: 4px;

  .el-icon {
    margin-right: 2px;
    font-size: 12px;
  }

  &--blue {
    color: #2563eb;
    border-color: #bfdbfe;
    background: #eff6ff;
  }

  &:hover {
    color: var(--color-primary);
    border-color: var(--color-primary-border);
    background: var(--color-primary-soft-bg);
  }

  &--blue:hover {
    color: #1d4ed8;
    border-color: #93c5fd;
    background: #dbeafe;
  }
}

@media (max-width: 749px) {
  .filter-bar__count {
    width: 100%;
    text-align: right;
  }
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  min-height: 50px;
  padding: 12px 20px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-card);

  &__info {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 12px;
    color: var(--text-secondary);

    strong {
      color: var(--text-primary);
    }
  }
}

.page-size-select {
  width: 70px;
}

</style>
