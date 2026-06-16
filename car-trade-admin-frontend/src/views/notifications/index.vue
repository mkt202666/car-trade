<template>

  <div class="page notifications-page">
    <PageHeader
      title="通知管理"
      subtitle="查看系统消息推送记录，发送全站系统公告"
    >
      <template #actions>
        <el-button type="primary" @click="openAnnouncementDialog">
          <el-icon class="btn-icon"><Promotion /></el-icon>
          发送系统公告
        </el-button>
      </template>
    </PageHeader>

    <!-- 统计卡片 -->
    <section class="stats-grid">
      <div v-for="stat in summaryStats" :key="stat.label" class="summary-card">
        <span class="summary-card__label">{{ stat.label }}</span>
        <h3 class="summary-card__value">{{ stat.value }}</h3>
      </div>
    </section>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-bar__controls">
        <el-input
          v-model="keyword"
          class="filter-bar__search"
          placeholder="搜索标题、内容或接收用户"
          clearable
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-select
          v-model="typeFilter"
          class="filter-bar__select"
          placeholder="全部类型"
        >
          <el-option
            v-for="opt in TYPE_OPTIONS"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
        <el-select
          v-model="statusFilter"
          class="filter-bar__select"
          placeholder="全部状态"
        >
          <el-option
            v-for="opt in STATUS_OPTIONS"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-panel">
      <el-table
        :data="paginatedItems"
        v-loading="loading"
        style="width: 100%"
        class="notification-table"
      >
        <el-table-column label="ID" width="80" prop="id">
          <template #default="{ row }">
            <span class="cell-id">{{ row.id }}</span>
          </template>
        </el-table-column>

        <el-table-column label="类型" min-width="110">
          <template #default="{ row }">
            <el-tag
              :type="getTypeTagType(row.type) as any"
              effect="light"
              size="small"
              round
              class="type-tag"
            >
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="接收用户" min-width="120">
          <template #default="{ row }">
            <div class="cell-user">
              <span class="cell-user__name">{{ row.targetUserName }}</span>
              <span class="cell-user__id">ID: {{ row.targetUserId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="标题" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-title">{{ row.title }}</span>
          </template>
        </el-table-column>

        <el-table-column label="内容" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-content">{{ row.content }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusTagType(row.status) as any"
              effect="light"
              size="small"
              round
            >
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="发送时间" min-width="160">
          <template #default="{ row }">
            <span class="cell-time">{{ row.createdAt }}</span>
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
            <strong>{{ filteredNotifications.length }}</strong> 项
          </span>
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredNotifications.length"
          layout="prev, pager, next"
          background
        />
      </div>
    </div>

    <!-- 发送系统公告弹窗 -->
    <el-dialog
      v-model="announcementDialogVisible"
      width="560px"
      class="announcement-dialog"
      align-center
      destroy-on-close
      @closed="resetAnnouncementForm"
    >
      <template #header>
        <h3 class="announcement-dialog__title">发送系统公告</h3>
      </template>

      <el-form label-position="top" class="announcement-form">
        <el-form-item label="公告标题">
          <el-input
            v-model="announcementTitle"
            placeholder="如：平台维护通知"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="公告内容">
          <el-input
            v-model="announcementContent"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容..."
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="announcement-dialog__footer">
          <el-button @click="announcementDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="announcementSubmitting" @click="submitAnnouncement">
            <el-icon class="btn-icon"><Promotion /></el-icon>
            确认发送
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>

</template>

<script setup lang="ts">
/**
 * 通知管理页面
 * 展示系统通知推送记录，支持按关键词、类型和状态筛选分页查询；
 * 提供发送系统公告功能，向全站用户推送通知。
 */

import { Promotion, Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import { useNotifications } from './hooks/useNotifications'
import { TYPE_OPTIONS, STATUS_OPTIONS } from './hooks/constants'

const {
  keyword,
  typeFilter,
  statusFilter,
  loading,
  summaryStats,
  filteredNotifications,
  currentPage,
  pageSize,
  paginatedItems,
  pageRangeStart,
  pageRangeEnd,
  announcementDialogVisible,
  announcementSubmitting,
  announcementTitle,
  announcementContent,
  openAnnouncementDialog,
  resetAnnouncementForm,
  submitAnnouncement,
  handleSearch,
  getTypeLabel,
  getTypeTagType,
  getStatusLabel,
  getStatusTagType,
} = useNotifications()

</script>

<style lang="scss" scoped>

@import '../../styles/page.css';

.notifications-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.btn-icon {
  margin-right: 4px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.summary-card {
  padding: 20px;
  border-radius: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  &__label {
    font-size: 12px;
    font-weight: 500;
    color: var(--text-muted);
    line-height: 1.4;
  }

  &__value {
    margin: 12px 0 0;
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

  &__select {
    width: 140px;

    :deep(.el-select__wrapper) {
      background: var(--bg-elevated);
      box-shadow: 0 0 0 1px var(--border-color) inset;
      border-radius: 8px;
      min-height: 32px;
    }

    :deep(.el-select__placeholder),
    :deep(.el-select__selected-item) {
      font-size: 12px;
      font-weight: 500;
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

.notification-table {
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

.cell-id {
  font-weight: 600;
  color: var(--text-primary);
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
  font-size: 12px;
}

.cell-user {
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

.cell-title {
  font-weight: 600;
  color: var(--text-primary);
}

.cell-content {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.cell-time {
  font-size: 12px;
  color: var(--text-muted);
}

.type-tag {
  font-weight: 600;
  border: none;
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

.announcement-dialog {
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

  &__title {
    margin: 0;
    font-size: 16px;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.4;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}

.announcement-form {
  :deep(.el-form-item__label) {
    font-size: 12px;
    font-weight: 500;
    color: var(--text-muted);
    line-height: 1.4;
    margin-bottom: 4px;
    padding: 0;
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

    &__select {
      width: 100%;
    }
  }

  .summary-card__value {
    font-size: 24px;
  }
}

</style>
