<template>

  <div class="page transactions-page space-y-6">
    <PageHeader
      title="交易管理"
      subtitle="理算和管理平台车辆订立、预付款（定金）缴纳、物流交付与完结全链路数据。"
    />

    <div class="filter-bar">
      <div class="filter-bar__controls">
        <el-input
          v-model="keyword"
          class="filter-bar__search"
          placeholder="搜索订单号、买卖双方或车型..."
          clearable
          :prefix-icon="Search"
        />
        <div class="filter-select">
          <el-icon class="filter-select__icon"><Filter /></el-icon>
          <el-select v-model="statusFilter" class="filter-select__input status-select" placeholder="所有的订单流向">
            <el-option label="所有的订单流向" value="all" />
            <el-option label="创建交易" value="submitted" />
            <el-option label="创建合同" value="deposit_paid" />
            <el-option label="完成签约" value="document_prep" />
            <el-option label="交车过户" value="signed" />
            <el-option label="争议处理" value="delivering" />
            <el-option label="交易完成" value="completed" />
            <el-option label="交易已终止 (手动/违约)" value="cancelled" />
          </el-select>
        </div>
        <div class="filter-select">
          <el-icon class="filter-select__icon"><Location /></el-icon>
          <el-select v-model="provinceFilter" class="filter-select__input" placeholder="买家所有省份">
            <el-option label="买家所有省份" value="all" />
            <el-option v-for="p in provinces" :key="p" :label="p" :value="p" />
          </el-select>
        </div>
        <div class="filter-select">
          <el-icon class="filter-select__icon"><Location /></el-icon>
          <el-select
            v-model="cityFilter"
            class="filter-select__input"
            placeholder="买家所有城市"
            :disabled="provinceFilter === 'all'"
          >
            <el-option label="买家所有城市" value="all" />
            <el-option v-for="c in availableCities" :key="c" :label="c" :value="c" />
          </el-select>
        </div>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="起始时间"
          end-placeholder="截止时间"
          value-format="YYYY-MM-DD"
          class="filter-date-picker"
          size="small"
        />
      </div>
      <span class="filter-bar__hint">精确对接5D Auto移动端。点击任一订单可打开抽屉式全景核验详情</span>
    </div>

    <div class="orders-panel">
      <div class="orders-panel__header">
        <div class="orders-panel__title-group">
          <h3 class="orders-panel__title">订单列表</h3>
          <p class="orders-panel__subtitle">管理端履约全谱系仪表盘，可执行违约挂起及订单手动终止功能</p>
        </div>
        <el-tag type="primary" effect="light" round class="orders-panel__badge">
          当前共显示 {{ paginatedOrders.length }} / {{ filteredOrders.length }} 笔订单
        </el-tag>
      </div>

      <div class="orders-list">
        <div class="orders-list__header hidden md:grid">
          <div class="col-span-4">车型信息与订单</div>
          <div class="col-span-3">买卖双方</div>
          <div class="col-span-2">流转状态</div>
          <div class="col-span-2 text-right">成交价 (元)</div>
          <div class="col-span-1 text-right">交付核验</div>
        </div>

        <div
          v-for="order in paginatedOrders"
          :key="order.id"
          :id="`order-card-${order.id}`"
          class="order-card"
          @click="openOrderDetail(order)"
        >
          <div class="order-card__vehicle md:col-span-4">
            <div>
              <span class="order-card__brand">{{ order.brand }}</span>
              <span class="order-card__model" :title="order.model">{{ order.model }}</span>
            </div>
            <el-tag class="order-card__location" effect="plain" size="small">
              <el-icon><Location /></el-icon>
              车源地: {{ order.location }}
            </el-tag>
            <div class="order-card__meta">
              <span class="order-card__id">ID: {{ order.id }}</span>
              <span class="order-card__evidence">存证: {{ order.evidenceDate }}</span>
            </div>
          </div>

          <div class="order-card__parties md:col-span-3">
            <div class="order-card__party">
              <span class="order-card__party-label">买家:</span>
              <strong>{{ order.buyer }}</strong>
            </div>
            <div class="order-card__party">
              <span class="order-card__party-label">卖家:</span>
              <strong>{{ order.seller }}</strong>
            </div>
          </div>

          <div class="order-card__status md:col-span-2">
            <span class="order-card__mobile-label">履约状态:</span>
            <StatusBadge :status="orderStatus(order.statusKey)" :label="order.statusLabel" />
          </div>

          <div class="order-card__price md:col-span-2">
            <span class="order-card__mobile-label">商品总成交价:</span>
            <span class="order-card__amount">{{ order.amount }}</span>
          </div>

          <div class="order-card__action md:col-span-1">
            <el-button
              type="primary"
              plain
              size="small"
              class="verify-btn"
              @click.stop="openOrderDetail(order)"
            >
              核验履约单 →
            </el-button>
          </div>
        </div>

        <el-empty
          v-if="paginatedOrders.length === 0"
          description="暂无符合条件的订单"
          :image-size="80"
        />
      </div>

      <div class="pagination">
        <div class="pagination__info">
          <div class="pagination__page-size">
            <span>每页行数:</span>
            <el-select v-model="pageSize" class="page-size-select" size="small">
              <el-option :value="20" label="20" />
              <el-option :value="50" label="50" />
              <el-option :value="100" label="100" />
            </el-select>
          </div>
          <span>
            显示 {{ pageRangeStart }}-{{ pageRangeEnd }} 项，共
            <strong>{{ filteredOrders.length }}</strong> 项
          </span>
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredOrders.length"
          layout="prev, pager, next"
          background
        />
      </div>
    </div>

    <TransactionOrderDrawer
      v-model="drawerVisible"
      :order-id="selectedOrderId"
      @closed="selectedOrderId = null"
    />
  </div>

</template>

<script setup lang="ts">
/**
 * 交易管理列表页
 * 提供订单多维筛选（关键词、履约状态、买家省市、存证日期）、分页列表与全景详情抽屉入口。
 * 业务数据与状态逻辑由 useTransactions 管理，点击订单行或「核验履约单」打开 TransactionOrderDrawer。
 */
import { Filter, Location, Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import StatusBadge from '../../components/StatusBadge.vue'
import TransactionOrderDrawer from './TransactionOrderDrawer.vue'
import { useTransactions } from './hooks/useTransactions'

const {
  keyword,
  statusFilter,
  provinceFilter,
  cityFilter,
  dateRange,
  currentPage,
  pageSize,
  drawerVisible,
  selectedOrderId,
  provinces,
  availableCities,
  filteredOrders,
  paginatedOrders,
  pageRangeStart,
  pageRangeEnd,
  orderStatus,
  openOrderDetail,
} = useTransactions()

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
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  &__controls {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    flex: 1;
    min-width: 0;
  }

  &__search {
    width: 100%;
    max-width: 240px;

    :deep(.el-input__wrapper) {
      background: var(--bg-elevated);
      box-shadow: 0 0 0 1px #e5e7eb inset;
      border-radius: 8px;
      padding: 4px 12px 4px 8px;
    }

    :deep(.el-input__inner) {
      font-size: 12px;
      height: 28px;
    }
  }

  &__hint {
    flex-shrink: 0;
    font-size: 12px;
    font-weight: 500;
    color: var(--text-muted);
    white-space: nowrap;
  }
}

.filter-select {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 8px;
  background: var(--bg-elevated);
  border: 1px solid #e5e7eb;
  border-radius: 8px;

  &__icon {
    font-size: 14px;
    color: var(--text-muted);
    flex-shrink: 0;
  }

  &__input {
    width: 120px;

    &.status-select {
      width: 140px;
    }

    :deep(.el-select__wrapper) {
      background: transparent;
      box-shadow: none !important;
      padding-left: 0;
      min-height: 30px;
    }

    :deep(.el-select__placeholder),
    :deep(.el-select__selected-item) {
      font-size: 12px;
      font-weight: 500;
      color: var(--text-secondary);
    }
  }
}

.filter-date-picker {
  width: 220px;

  :deep(.el-input__wrapper) {
    background: var(--bg-elevated);
    box-shadow: 0 0 0 1px #e5e7eb inset;
    border-radius: 8px;
    padding: 2px 8px;
  }

  :deep(.el-range-input) {
    font-size: 10.5px;
    color: var(--text-secondary);
  }

  :deep(.el-range-separator) {
    font-size: 12px;
    color: var(--text-muted);
  }
}

.orders-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 24px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  padding: 24px;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding-bottom: 20px;
    margin-bottom: 24px;
    border-bottom: 1px solid var(--border-color);
  }

  &__title {
    font-size: 16px;
    font-weight: 800;
    color: var(--text-primary);
    margin: 0;
  }

  &__subtitle {
    font-size: 11px;
    color: var(--text-muted);
    margin: 2px 0 0;
  }

  &__badge {
    flex-shrink: 0;
    font-size: 12px;
    font-weight: 700;
    border: none;
  }
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 12px;

  &__header {
    grid-template-columns: repeat(12, minmax(0, 1fr));
    gap: 16px;
    padding: 12px 20px;
    background: var(--bg-elevated);
    border: 1px solid var(--border-color);
    border-radius: 12px;
    font-size: 10.5px;
    font-weight: 800;
    color: var(--text-secondary);
    letter-spacing: 0.05em;
  }
}

.order-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: var(--bg-card);
  border: 1px solid rgba(229, 231, 235, 0.45);
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;

  &:hover {
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.08);
    border-color: #c7d2fe;
  }

  @media (min-width: 768px) {
    display: grid;
    grid-template-columns: repeat(12, minmax(0, 1fr));
    gap: 16px;
    align-items: start;
  }

  &__brand {
    display: block;
    font-size: 13px;
    font-weight: 800;
    color: #030712;
  }

  &__model {
    display: block;
    font-size: 11px;
    font-weight: 500;
    color: var(--text-secondary);
    max-width: 210px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__location {
    margin-top: 4px;

    .el-icon {
      margin-right: 2px;
      font-size: 12px;
      vertical-align: -2px;
    }
  }

  &__meta {
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px dashed var(--border-color);
  }

  &__id {
    display: block;
    font-family: ui-monospace, monospace;
    font-size: 11px;
    font-weight: 700;
    color: var(--text-muted);
    letter-spacing: 0.05em;
  }

  &__evidence {
    display: block;
    font-size: 10px;
    color: var(--text-muted);
    margin-top: 2px;
  }

  &__parties {
    padding: 4px 0;
    font-size: 11px;
    color: var(--text-secondary);
    border-top: 1px dashed var(--border-color);
    border-bottom: 1px dashed var(--border-color);

    @media (min-width: 768px) {
      border: none;
      padding: 0;
    }
  }

  &__party {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;

    @media (min-width: 768px) {
      justify-content: flex-start;
    }

    + .order-card__party {
      margin-top: 4px;
    }

    strong {
      color: #1f2937;
      font-weight: 700;
    }
  }

  &__party-label {
    flex-shrink: 0;
    font-weight: 500;
    color: var(--text-muted);

    @media (min-width: 768px) {
      width: 64px;
    }
  }

  &__status,
  &__price {
    display: flex;
    align-items: center;

    @media (min-width: 768px) {
      display: block;
    }
  }

  &__price {
    justify-content: space-between;

    @media (min-width: 768px) {
      text-align: right;
    }
  }

  &__mobile-label {
    font-size: 10px;
    font-weight: 600;
    color: var(--text-muted);
    margin-right: 8px;

    @media (min-width: 768px) {
      display: none;
    }
  }

  &__amount {
    font-family: ui-monospace, monospace;
    font-size: 14px;
    font-weight: 800;
    color: var(--text-primary);
  }

  &__action {
    text-align: right;
  }
}

.verify-btn {
  width: 100%;
  font-size: 10px;
  font-weight: 700;
  padding: 4px 10px;
  height: auto;
  border-radius: 4px;

  @media (min-width: 768px) {
    width: auto;
  }
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  min-height: 50px;
  margin-top: 12px;
  padding: 12px 20px;
  border-top: 1px solid var(--border-color);

  &__info {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
    font-size: 12px;
    color: var(--text-secondary);

    strong {
      color: var(--text-primary);
      font-weight: 700;
    }
  }

  &__page-size {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.page-size-select {
  width: 70px;
}

@media (max-width: 1023px) {
  .filter-bar__hint {
    width: 100%;
    white-space: normal;
  }
}

@media (max-width: 749px) {
  .orders-panel {
    padding: 16px;
  }

  .orders-panel__header {
    flex-direction: column;
    align-items: flex-start;
  }
}

</style>
