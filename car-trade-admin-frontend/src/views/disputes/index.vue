<template>
  <div class="disputes-page space-y-6">
    <PageHeader title="纠纷管理" subtitle="处理和管理平台交易纠纷，支持、驳回或协商解决用户提交的争议申请。">
      <template #actions>
        <el-button type="primary" plain :icon="Download" @click="handleExport">导出</el-button>
      </template>
    </PageHeader>

    <!-- 筛选栏 -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <div class="filter-bar__left">
          <el-input
            v-model="keyword"
            placeholder="搜索订单号、发起人、手机号"
            :prefix-icon="Search"
            clearable
            class="filter-input"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-select v-model="statusFilter" placeholder="纠纷状态" class="filter-select">
            <el-option
              v-for="opt in STATUS_OPTIONS"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </div>
    </el-card>

    <!-- 主内容区 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-header">
          <div>
            <h3 class="table-header__title">纠纷列表</h3>
            <p class="table-header__subtitle">管理交易纠纷记录，执行支持、驳回或协商操作</p>
          </div>
          <el-tag type="primary" effect="light" round>
            共 {{ total }} 条记录
          </el-tag>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="filteredDisputes"
        stripe
        class="disputes-table"
      >
        <el-table-column label="ID" prop="id" width="70" align="center" />
        <el-table-column label="订单号" prop="orderId" min-width="140">
          <template #default="{ row }">
            <span class="cell-mono">{{ row.orderId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单标题" prop="orderTitle" min-width="180" show-overflow-tooltip />
        <el-table-column label="发起人" prop="initiatorName" min-width="100" />
        <el-table-column label="手机号" prop="initiatorPhone" min-width="130">
          <template #default="{ row }">
            <span class="cell-mono">{{ row.initiatorPhone }}</span>
          </template>
        </el-table-column>
        <el-table-column label="纠纷原因" prop="reason" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="STATUS_COLORS[row.status as DisputeStatus]"
              size="small"
              effect="light"
              round
            >
              {{ statusLabel(row.status as DisputeStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">
            <span class="cell-mono">{{ formatDateTime(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div v-if="row.status === 'OPEN' || row.status === 'IN_PROGRESS'" class="action-btns">
              <el-button type="success" size="small" plain @click="handleApprove(row.id)">支持</el-button>
              <el-button type="danger" size="small" plain @click="handleReject(row.id)">驳回</el-button>
              <el-button type="warning" size="small" plain @click="handleNegotiate(row.id)">协商</el-button>
            </div>
            <span v-else class="text-xs text-gray-400">已处理</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <div class="pagination__info">
          <div class="pagination__page-size">
            <span>每页行数:</span>
            <el-select :model-value="pageSize" class="page-size-select" size="small" @update:model-value="handleSizeChange">
              <el-option :value="10" label="10" />
              <el-option :value="20" label="20" />
              <el-option :value="50" label="50" />
            </el-select>
          </div>
          <span>
            显示 {{ pageRangeStart }}-{{ pageRangeEnd }} 项，共
            <strong>{{ total }}</strong> 项
          </span>
        </div>
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @update:current-page="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
/**
 * 纠纷管理列表页
 * 提供纠纷多维筛选（关键词、状态）、分页列表与操作（支持/驳回/协商）。
 * 业务数据与状态逻辑由 useDisputes 管理。
 */
import { Download, Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import { STATUS_OPTIONS, STATUS_COLORS } from './hooks/constants'
import { useDisputes } from './hooks/useDisputes'
import type { DisputeStatus } from './hooks/types'

const {
  keyword,
  statusFilter,
  currentPage,
  pageSize,
  loading,
  filteredDisputes,
  total,
  pageRangeStart,
  pageRangeEnd,
  handlePageChange,
  handleSizeChange,
  handleSearch,
  handleApprove,
  handleReject,
  handleNegotiate,
  handleExport,
  statusLabel,
  formatDateTime,
} = useDisputes()
</script>

<style lang="scss" scoped>
@import '../../styles/page.css';

.disputes-page {
  padding: 16px 24px 40px;
  max-width: 1600px;
  color: var(--text-primary);
}

.filter-card {
  :deep(.el-card__body) {
    padding: 16px;
  }
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;

  &__left {
    display: flex;
    gap: 12px;
    flex: 1;
    min-width: 0;
    align-items: center;
  }
}

.filter-input {
  max-width: 300px;
}

.filter-select {
  width: 160px;
}

.table-card {
  :deep(.el-card__header) {
    padding: 16px 20px;
    border-bottom: 1px solid var(--border-color);
  }
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;

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
}

.cell-mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 12px;
  color: #6b7280;
}

.action-btns {
  display: flex;
  gap: 4px;
  justify-content: center;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  min-height: 50px;
  margin-top: 12px;
  padding: 12px 0;
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

@media (max-width: 749px) {
  .disputes-page {
    padding: 16px 14px 32px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;

    &__left {
      flex-direction: column;
    }
  }

  .filter-input {
    max-width: 100%;
  }

  .filter-select {
    width: 100%;
  }

  .table-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
