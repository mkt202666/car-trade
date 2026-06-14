<template>

  <div class="page vehicles-page">
    <PageHeader
      title="5D 车源管理"
      subtitle="调优和管理当前处于不同流转生命周期的平台端车源、车况信息与估值明细。"
    />

    <div class="filter-bar">
      <div class="filter-bar__controls">
        <el-input
          v-model="keyword"
          class="filter-bar__search"
          placeholder="搜索品牌、车系或车源ID"
          clearable
          :prefix-icon="Search"
        />
        <div class="filter-select">
          <el-icon class="filter-select__icon"><List /></el-icon>
          <el-select v-model="channelFilter" class="filter-select__input channel-select" placeholder="全部货源渠道">
            <el-option label="全部货源渠道" value="all" />
            <el-option label="司法拍卖/竞标" value="Auction" />
            <el-option label="按期置换车" value="TradeIn" />
            <el-option label="合作二手车商" value="Dealer" />
            <el-option label="卖家" value="DirectUser" />
          </el-select>
        </div>
        <div class="filter-select">
          <el-icon class="filter-select__icon"><Filter /></el-icon>
          <el-select v-model="statusFilter" class="filter-select__input" placeholder="全部上架状态">
            <el-option label="全部上架状态" value="all" />
            <el-option label="在售中" value="listed" />
            <el-option label="锁车洽谈" value="reserved" />
            <el-option label="交易中" value="pending_review" />
            <el-option label="完成出售" value="sold" />
            <el-option label="下架" value="disapproved" />
          </el-select>
        </div>
        <div class="filter-select">
          <el-icon class="filter-select__icon"><Location /></el-icon>
          <el-select v-model="provinceFilter" class="filter-select__input" placeholder="全部省份">
            <el-option label="全部省份" value="all" />
            <el-option v-for="p in provinces" :key="p" :label="p" :value="p" />
          </el-select>
        </div>
      </div>
    </div>

    <div class="table-panel">
      <el-table
        :data="paginatedVehicles"
        style="width: 100%"
        class="vehicles-table"
        row-key="id"
        @row-click="handleRowClick"
      >
        <el-table-column label="基础车源信息" min-width="200">
          <template #default="{ row }">
            <div class="cell-vehicle">
              <span class="cell-vehicle__brand">{{ row.brand }}</span>
              <span class="cell-vehicle__model">{{ row.model }}</span>
              <div class="cell-vehicle__tags">
                <el-tag type="warning" effect="light" size="small" class="vehicle-tag">保证金</el-tag>
                <el-tag
                  v-if="row.energyType"
                  type="success"
                  effect="light"
                  size="small"
                  class="vehicle-tag"
                >
                  {{ row.energyType }}
                </el-tag>
                <el-tag type="info" effect="plain" size="small" class="vehicle-tag vehicle-tag--muted">
                  {{ row.registerDate }}
                </el-tag>
                <el-tag type="info" effect="plain" size="small" class="vehicle-tag vehicle-tag--muted">
                  {{ row.mileage }}
                </el-tag>
              </div>
              <span class="cell-vehicle__id">ID: {{ row.id }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="所在地区" min-width="130">
          <template #default="{ row }">
            <el-tag class="location-tag" effect="plain" round>
              <el-icon><Location /></el-icon>
              {{ row.region }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="卖家" min-width="130">
          <template #default="{ row }">
            <div class="cell-seller">
              <span class="cell-seller__name">
                {{ row.seller.name }}
                <span class="cell-seller__nickname">({{ row.seller.nickname }})</span>
              </span>
              <span class="cell-seller__type">{{ row.seller.type }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="竞市价(CNY)" min-width="120">
          <template #default="{ row }">
            <div class="cell-price">
              <span class="cell-price__value">￥{{ row.price }} 万</span>
              <span class="cell-price__guide">指导价: ￥{{ row.guidePrice }}万</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="上架状态" min-width="100" align="center">
          <template #default="{ row }">
            <StatusBadge :status="vehicleStatus(row.status)" :label="statusLabel(row.status)" />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" fixed="right" align="right">
          <template #default="{ row }">
            <div class="table-actions" @click.stop>
              <el-button
                v-if="row.status === 'listed'"
                size="small"
                class="action-btn action-btn--orange"
                @click="handleDelist(row as Vehicle)"
              >
                <el-icon><Bottom /></el-icon>
                下架
              </el-button>
              <el-button size="small" class="action-btn action-btn--blue">
                <el-icon><Share /></el-icon>
                AI推广
              </el-button>
              <el-button size="small" class="action-btn">
                <el-icon><MagicStick /></el-icon>
                AI参谋
              </el-button>
              <el-popconfirm
                title="确定软删除该车源？此操作可恢复。"
                confirm-button-text="确认删除"
                cancel-button-text="取消"
                width="220"
                @confirm="handleDelete(row as Vehicle)"
              >
                <template #reference>
                  <el-button size="small" class="action-btn action-btn--danger">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无匹配的车源记录" :image-size="80" />
        </template>
      </el-table>

      <div class="pagination">
        <div class="pagination__info">
          <span>每页行数:</span>
          <el-select v-model="pageSize" class="page-size-select" size="small">
            <el-option :value="20" label="20" />
            <el-option :value="50" label="50" />
            <el-option :value="100" label="100" />
          </el-select>
          <span>
            显示 {{ pageRangeStart }}-{{ pageRangeEnd }} 项，共
            <strong>{{ filteredVehicles.length }}</strong> 项
          </span>
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredVehicles.length"
          layout="prev, pager, next"
          background
        />
      </div>
    </div>

    <el-dialog
      v-model="detailVisible"
      width="672px"
      class="vehicle-detail-dialog"
      :show-close="false"
      align-center
      destroy-on-close
      @closed="selectedVehicle = null"
    >
      <template v-if="selectedVehicle" #header>
        <div class="detail-header">
          <h3 class="detail-header__title">
            <el-tag type="primary" effect="light" size="small" class="detail-header__badge">车源详情</el-tag>
            {{ selectedVehicle.brand }}
          </h3>
          <el-button circle class="detail-header__close" @click="closeDetail">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </template>

      <div v-if="selectedVehicle" class="detail-body">
          <div class="detail-hero">
            <el-tag type="primary" effect="dark" class="detail-hero__status">
              {{ statusDetailLabel(selectedVehicle.status) }}
            </el-tag>
            <h2 class="detail-hero__name">
              {{ selectedVehicle.brand }} {{ selectedVehicle.model }}
            </h2>
            <div class="detail-hero__price">
              <span class="detail-hero__amount">
                {{ selectedVehicle.price }}<span class="detail-hero__unit">万</span>
              </span>
              <span class="detail-hero__guide">新车指导价: {{ selectedVehicle.guidePrice }}万</span>
            </div>
          </div>

          <div class="detail-feature-tags">
            <el-tag type="warning" effect="light" class="feature-tag">🛡️ 平台定金担保 (保证金)</el-tag>
            <el-tag
              v-if="selectedVehicle.energyType"
              type="success"
              effect="light"
              class="feature-tag"
            >
              🌿 新能源
            </el-tag>
          </div>

          <section class="detail-section">
            <h4 class="detail-section__title">车况描述及说明</h4>
            <div class="detail-card">
              <p class="detail-desc">{{ selectedVehicle.detail.description }}</p>
              <div class="detail-stats-grid">
                <div v-for="item in conditionStats" :key="item.label" class="detail-stat">
                  <span class="detail-stat__value">{{ item.value }}</span>
                  <span class="detail-stat__label">{{ item.label }}</span>
                </div>
              </div>
            </div>
          </section>

          <section class="detail-section">
            <h4 class="detail-section__title">车辆报告</h4>
            <div class="report-grid">
              <el-button class="report-card report-card--active">
                <el-icon class="report-card__icon report-card__icon--amber"><CircleCheck /></el-icon>
                <span class="report-card__name">查博士</span>
                <span class="report-card__action report-card__action--amber">查看</span>
              </el-button>
              <el-button class="report-card">
                <el-icon class="report-card__icon"><Document /></el-icon>
                <span class="report-card__name">柠檬查报告</span>
                <span class="report-card__action">联系获取</span>
              </el-button>
              <el-button class="report-card">
                <el-icon class="report-card__icon"><SetUp /></el-icon>
                <span class="report-card__name">4S维保记录</span>
                <span class="report-card__action">联系获取</span>
              </el-button>
            </div>
          </section>

          <section class="detail-section">
            <h4 class="detail-section__title">车辆出厂详细参数配置</h4>
            <div class="spec-table">
              <div
                v-for="(row, index) in selectedVehicle.detail.specs"
                :key="row.label"
                class="spec-row"
                :class="{ 'spec-row--highlight': row.highlight, 'spec-row--alt': !row.highlight && index % 2 === 1 }"
              >
                <span class="spec-row__label" :class="{ 'spec-row__label--highlight': row.highlight }">
                  {{ row.label }}
                </span>
                <span class="spec-row__value" :class="{ 'spec-row__value--highlight': row.highlight }">
                  {{ row.value }}
                </span>
              </div>
            </div>
          </section>

          <section class="detail-section detail-section--last">
            <h4 class="detail-section__title detail-section__title--sm">发布者信息</h4>
            <div class="publisher-card">
              <el-avatar :size="56" class="publisher-card__avatar">
                {{ sellerInitial(selectedVehicle.seller.name) }}
              </el-avatar>
              <div class="publisher-card__info">
                <div class="publisher-card__name-row">
                  <span class="publisher-card__name">{{ selectedVehicle.seller.name }}</span>
                  <el-tag size="small" effect="plain" round class="publisher-card__nickname">
                    {{ selectedVehicle.seller.nickname }}
                  </el-tag>
                </div>
                <span class="publisher-card__role">{{ selectedVehicle.detail.sellerRole }}</span>
                <div class="publisher-card__meta">
                  <span>sellerId: {{ selectedVehicle.detail.sellerId }}</span>
                  <span>phone: {{ selectedVehicle.detail.sellerPhone }}</span>
                </div>
              </div>
            </div>
          </section>
      </div>
    </el-dialog>
  </div>

</template>

<script setup lang="ts">
/**
 * 5D 车源管理页面
 *
 * 提供平台端车源的全生命周期浏览与管理：支持按渠道、状态、省份筛选及关键词搜索，
 * 表格展示竞市价与卖家信息；点击行打开详情弹窗（车况、报告入口、出厂参数、发布者）；
 * 在售车源可下架，任意车源可软删除。数据与交互逻辑由 useVehicles 提供。
 */
import PageHeader from '../../components/PageHeader.vue'
import StatusBadge from '../../components/StatusBadge.vue'
import { Search } from '@element-plus/icons-vue'
import { useVehicles, type Vehicle } from './hooks/useVehicles'

const {
  keyword,
  channelFilter,
  statusFilter,
  provinceFilter,
  currentPage,
  pageSize,
  detailVisible,
  selectedVehicle,
  provinces,
  vehicles,
  filteredVehicles,
  paginatedVehicles,
  pageRangeStart,
  pageRangeEnd,
  conditionStats,
  handleRowClick,
  closeDetail,
  statusDetailLabel,
  sellerInitial,
  statusLabel,
  vehicleStatus,
  handleDelist,
  handleDelete,
} = useVehicles()

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
    min-width: 200px;
    max-width: 256px;

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
    width: 120px;

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

.channel-select {
  width: 132px;
}

.table-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  overflow: hidden;
}

.vehicles-table {
  :deep(.el-table__row) {
    cursor: pointer;
  }

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

.cell-vehicle {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__brand {
    font-weight: 700;
    color: var(--text-primary);
  }

  &__model {
    font-size: 12px;
    font-weight: 500;
    color: var(--text-secondary);
  }

  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-top: 4px;
  }

  &__id {
    margin-top: 6px;
    font-family: ui-monospace, monospace;
    font-size: 10px;
    color: var(--text-muted);
  }
}

.vehicle-tag {
  height: auto;
  padding: 1px 6px;
  font-size: 9px;
  font-weight: 700;
  border-radius: 6px;

  &--muted {
    font-weight: 500;
    border-radius: 4px;
  }
}

.location-tag {
  height: auto;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 500;
  color: var(--text-secondary);
  background: var(--bg-elevated);
  border-color: var(--border-color);

  .el-icon {
    margin-right: 4px;
    color: var(--color-primary);
    font-size: 14px;
    vertical-align: -2px;
  }
}

.cell-seller {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__name {
    font-size: 12px;
    font-weight: 700;
    color: var(--text-primary);
  }

  &__nickname {
    color: var(--color-primary);
  }

  &__type {
    font-size: 11px;
    color: var(--text-secondary);
  }
}

.cell-price {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-family: ui-monospace, monospace;

  &__value {
    font-size: 12px;
    font-weight: 600;
    color: #e11d48;
  }

  &__guide {
    font-size: 10px;
    font-weight: 400;
    color: var(--text-muted);
  }
}

.table-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 4px;
  max-width: 150px;
  margin-left: auto;
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

  &:hover {
    color: var(--color-primary);
    border-color: var(--color-primary-border);
    background: var(--color-primary-soft-bg);
  }

  &--orange:hover {
    color: #ea580c;
    border-color: #fed7aa;
    background: #fff7ed;
  }

  &--blue:hover {
    color: #2563eb;
    border-color: #bfdbfe;
    background: #eff6ff;
  }

  &--danger:hover {
    color: #e11d48;
    border-color: #fecdd3;
    background: #fff1f2;
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

[data-theme='dark'] .vehicles-table {
  :deep(.el-table__header-wrapper th.el-table__cell) {
    background: var(--surface-glass-bg) !important;
    color: var(--text-secondary);
    border-bottom-color: var(--border-color);
  }

  :deep(.el-table__body tr:hover > td.el-table__cell) {
    background: var(--table-hover) !important;
  }
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  background: rgba(249, 250, 251, 0.5);

  &__title {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 700;
    color: var(--text-primary);
  }

  &__badge {
    font-weight: 700;
  }

  &__close {
    border: 1px solid var(--border-color);
    color: var(--text-muted);

    &:hover {
      color: var(--text-primary);
      background: var(--bg-elevated);
    }
  }
}

.detail-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.detail-hero {
  position: relative;
  padding: 20px;
  border-radius: 16px;
  border: 1px solid var(--border-color);
  background: linear-gradient(to right, #f9fafb, #fff);

  &__status {
    position: absolute;
    top: 16px;
    right: 16px;
    border-radius: 12px;
    font-weight: 700;
  }

  &__name {
    margin: 0 0 8px;
    max-width: 80%;
    font-size: 20px;
    font-weight: 700;
    line-height: 1.3;
    color: var(--text-primary);
  }

  &__price {
    display: flex;
    align-items: flex-end;
    gap: 12px;
    margin-top: 16px;
  }

  &__amount {
    font-size: 30px;
    font-weight: 800;
    color: #e11d48;
    letter-spacing: -0.02em;
    font-family: ui-monospace, monospace;
  }

  &__unit {
    margin-left: 4px;
    font-size: 14px;
    font-weight: 700;
  }

  &__guide {
    margin-bottom: 4px;
    font-size: 14px;
    font-weight: 500;
    color: var(--text-muted);
    text-decoration: line-through;
  }
}

.detail-feature-tags {
  display: flex;
  align-items: center;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.feature-tag {
  height: auto;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
  border-radius: 8px;
}

.detail-section {
  &__title {
    margin: 0 0 16px;
    padding-left: 12px;
    border-left: 4px solid var(--color-primary);
    font-size: 18px;
    font-weight: 700;
    color: var(--text-primary);

    &--sm {
      font-size: 16px;
    }
  }

  &--last {
    padding-bottom: 24px;
  }
}

.detail-card {
  padding: 20px;
  border-radius: 16px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.detail-desc {
  margin: 0 0 24px;
  font-size: 15px;
  line-height: 1.7;
  color: #374151;
}

.detail-stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px 16px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.detail-stat {
  display: flex;
  flex-direction: column;

  &__value {
    font-size: 15px;
    font-weight: 700;
    color: var(--text-primary);
  }

  &__label {
    margin-top: 4px;
    font-size: 12px;
    color: var(--text-secondary);
  }
}

.report-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 20px;
  border-radius: 16px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.report-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: auto;
  padding: 16px;
  margin: 0;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  background: rgba(249, 250, 251, 0.5);

  &:hover {
    background: #f9fafb;
  }

  &--active {
    border-color: #fde68a;
    background: rgba(255, 251, 235, 0.5);

    &:hover {
      background: #fffbeb;
    }
  }

  &__icon {
    margin-bottom: 8px;
    font-size: 24px;
    color: var(--text-muted);

    &--amber {
      color: #f59e0b;
    }
  }

  &__name {
    font-size: 14px;
    font-weight: 700;
    color: var(--text-primary);
  }

  &__action {
    margin-top: 4px;
    font-size: 12px;
    font-weight: 500;
    color: var(--text-muted);

    &--amber {
      color: #d97706;
    }
  }
}

.spec-table {
  overflow: hidden;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  font-size: 14px;
}

.spec-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  padding: 16px;

  &--highlight {
    background: rgba(238, 242, 255, 0.1);
  }

  &--alt {
    background: rgba(249, 250, 251, 0.3);
  }

  &__label {
    color: var(--text-secondary);

    &--highlight {
      font-weight: 600;
      color: var(--color-primary);
    }
  }

  &__value {
    font-weight: 500;
    color: var(--text-primary);
    text-align: right;

    &--highlight {
      font-weight: 800;
      color: var(--color-primary);
    }
  }
}

.publisher-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);

  &__avatar {
    flex-shrink: 0;
    background: var(--color-primary-soft);
    color: var(--color-primary);
    font-weight: 700;
    font-size: 20px;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 4px;
    min-width: 0;
  }

  &__name-row {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
  }

  &__name {
    font-size: 16px;
    font-weight: 700;
    color: var(--text-primary);
  }

  &__nickname {
    font-size: 10px;
  }

  &__role {
    font-size: 14px;
    color: var(--text-secondary);
  }

  &__meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 4px;
    font-family: ui-monospace, monospace;
    font-size: 11px;
    color: var(--text-muted);
  }
}

@media (max-width: 749px) {
  .detail-stats-grid,
  .report-grid {
    grid-template-columns: 1fr;
  }

  .detail-hero__name {
    max-width: 100%;
    padding-right: 80px;
  }
}

</style>

<style lang="scss">
.vehicle-detail-dialog {
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  margin: 5vh auto;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(15, 23, 42, 0.25);

  .el-dialog__header {
    flex-shrink: 0;
    padding: 0;
    margin: 0;
  }

  .el-dialog__body {
    flex: 1;
    min-height: 0;
    padding: 0;
    overflow-x: hidden;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
  }
}
</style>
