<template>

  <div class="page dealers-page">
    <PageHeader
      title="车行管理"
      subtitle="监管和认证平台入驻车商，管理日常经营、驻点以及资质履约状况。"
    />

    <div class="filter-bar">
      <div class="filter-bar__controls">
        <el-input
          v-model="keyword"
          class="filter-bar__search"
          placeholder="搜索车行名称、管理员或手机号"
          clearable
          :prefix-icon="Search"
        />
        <div class="filter-select">
          <el-icon class="filter-select__icon"><CircleCheck /></el-icon>
          <el-select v-model="statusFilter" class="filter-select__input" placeholder="全部准入状态">
            <el-option label="全部准入状态" value="all" />
            <el-option label="正常营业中" value="active" />
            <el-option label="封禁挂起中" value="suspended" />
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
      <el-button type="primary" class="filter-bar__action" @click="openCreateDialog">
        <el-icon><Plus /></el-icon>
        关联新增合作车行
      </el-button>
    </div>

    <div class="table-panel">
      <el-table
        :data="paginatedDealers"
        style="width: 100%"
        class="dealers-table"
        row-key="id"
        @row-click="handleRowClick"
      >
        <el-table-column label="车行名称/ID" min-width="150">
          <template #default="{ row }">
            <div class="cell-name">
              <span class="cell-name__title">{{ row.name }}</span>
              <span class="cell-name__meta">入驻: {{ row.joinedAt }}</span>
              <span class="cell-name__meta cell-name__id">ID: {{ row.id }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="管理员/联系方式" min-width="140">
          <template #default="{ row }">
            <div class="cell-admin">
              <span class="cell-admin__name">
                <el-icon><User /></el-icon>
                {{ row.adminName }}
              </span>
              <span class="cell-admin__phone">
                <el-icon><Phone /></el-icon>
                {{ row.phone }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="驻点地址" min-width="150">
          <template #default="{ row }">
            <div class="cell-location">
              <span class="cell-location__tag">
                <el-icon><Location /></el-icon>
                {{ row.province }} · {{ row.city }}
              </span>
              <span class="cell-location__addr" :title="row.address">{{ row.address }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="成员/订单数据" width="130" align="center">
          <template #default="{ row }">
            <div class="cell-stats">
              <div class="cell-stats__row">
                <span class="cell-stats__label">成员数:</span>
                <span class="cell-stats__value">{{ row.members }} 席</span>
              </div>
              <div class="cell-stats__row">
                <span class="cell-stats__label">本月:</span>
                <span class="cell-stats__value cell-stats__value--accent">{{ row.monthlyOrders }} 单</span>
              </div>
              <div class="cell-stats__row">
                <span class="cell-stats__label">累计:</span>
                <span class="cell-stats__value">{{ row.totalOrders }} 单</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="车源数据" width="100" align="center">
          <template #default="{ row }">
            <div class="cell-vehicles">
              <div class="cell-vehicles__row">
                <span class="cell-stats__label">在售:</span>
                <span class="cell-vehicles__on-sale">{{ row.vehicles.onSale }}</span>
              </div>
              <div class="cell-vehicles__row">
                <span class="cell-stats__label">下架:</span>
                <span class="cell-stats__value">{{ row.vehicles.offShelf }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="营业执照" width="90" align="center">
          <template #default="{ row }">
            <a
              v-if="row.licenseUrl"
              :href="row.licenseUrl"
              target="_blank"
              rel="noreferrer"
              class="cell-license"
              @click.stop
            >
              <el-icon><Document /></el-icon>
            </a>
            <span v-else class="cell-license__empty">-未传-</span>
          </template>
        </el-table-column>

        <el-table-column label="金融" width="110" align="center">
          <template #default="{ row }">
            <div v-if="row.credit" class="cell-credit">
              <span>{{ row.credit.used }} / {{ row.credit.total }}</span>
              <span class="cell-credit__label">已用 / 授信</span>
            </div>
            <span v-else class="cell-credit__none">无授信</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <span class="status-pill" :class="`status-pill--${row.status}`">
              {{ statusLabel(row.status) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="130" fixed="right" align="right">
          <template #default="{ row }">
            <div class="table-actions" @click.stop>
              <el-button size="small" class="action-btn" @click="openAdjustDialog(row as Dealer)">
                <el-icon><Coin /></el-icon>
                调保
              </el-button>
              <el-button
                size="small"
                class="action-btn action-btn--danger"
                :disabled="row.status === 'suspended'"
                @click="confirmSuspend(row as Dealer)"
              >
                <el-icon><CircleClose /></el-icon>
                停发
              </el-button>
            </div>
          </template>
        </el-table-column>
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
            <strong>{{ filteredDealers.length }}</strong> 项
          </span>
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredDealers.length"
          layout="prev, pager, next"
          background
        />
      </div>
    </div>

    <el-dialog
      v-model="createDialogVisible"
      width="480px"
      class="create-dialog"
      :show-close="true"
      align-center
      destroy-on-close
      @closed="resetCreateForm"
    >
      <template #header>
        <div class="create-dialog__header">
          <h2 class="create-dialog__title">关联准入新合并合作车行</h2>
          <p class="create-dialog__subtitle">建立全国5D互操作系统网络合作驻点</p>
        </div>
      </template>

      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-position="top"
        class="create-form"
      >
        <el-form-item label="车行名称" prop="name" required>
          <el-input v-model="createForm.name" placeholder="如: 江苏省中运通大好车">
            <template #prefix>
              <el-icon><OfficeBuilding /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="create-form__row">
          <el-form-item label="初始车行管理员" prop="adminName" required>
            <el-input v-model="createForm.adminName" placeholder="如: 王建华" />
          </el-form-item>
          <el-form-item label="联系手机电话" prop="phone" required>
            <el-input v-model="createForm.phone" placeholder="如: 18911223344" maxlength="11" />
          </el-form-item>
        </div>

        <div class="create-form__row">
          <el-form-item label="申请人身份证号" prop="idNumber">
            <el-input v-model="createForm.idNumber" placeholder="如: 11010519900101XXXX" class="mono-input" />
          </el-form-item>
          <el-form-item label="申请人身份证图片" prop="idImageUrl">
            <el-input v-model="createForm.idImageUrl" placeholder="图片URL" />
          </el-form-item>
        </div>

        <el-form-item label="车行实体门店图片" prop="storeImageUrl">
          <el-input v-model="createForm.storeImageUrl" placeholder="图片URL" />
        </el-form-item>

        <el-form-item label="统一社会信用代码" prop="creditCode" required>
          <el-input v-model="createForm.creditCode" placeholder="如: 91110108MA01XXXXXX" class="mono-input" />
        </el-form-item>

        <el-form-item label="营业执照附件" prop="licenseUrl" required>
          <el-input v-model="createForm.licenseUrl" placeholder="如: https://example.com/license.jpg" />
        </el-form-item>

        <el-form-item label="驻点地址" prop="address">
          <el-input v-model="createForm.address" placeholder="请输入车行实体店面的详细经营地址" />
        </el-form-item>

        <el-form-item label="首期授信保证金 (元)" prop="initialCredit">
          <el-input-number
            v-model="createForm.initialCredit"
            :min="0"
            :step="10000"
            :precision="0"
            controls-position="right"
            class="create-form__credit"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="create-dialog__footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="createSubmitting" @click="submitCreateForm">
            创建并提报审核
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="adjustDialogVisible"
      width="480px"
      class="adjust-dialog"
      :show-close="true"
      align-center
      destroy-on-close
      @closed="resetAdjustForm"
    >
      <template #header>
        <div class="adjust-dialog__header">
          <h2 class="adjust-dialog__title">调定车行授信/保证金余额</h2>
          <p v-if="adjustTarget" class="adjust-dialog__subject">
            对象主体: {{ adjustTarget.name }} ({{ adjustTarget.id }})
          </p>
        </div>
      </template>

      <div class="adjust-form">
        <p class="adjust-form__hint">请输入划定调配额度 (正数为调增补记，负数为调减抹画):</p>
        <el-input
          v-model="adjustAmount"
          placeholder="如: 10000 或 -5000"
          class="adjust-form__input"
        />
        <div class="adjust-form__balance">
          <span>当前已用: {{ adjustTarget?.credit?.used || '0' }}</span>
          <span>授信总额: {{ adjustTarget?.credit?.total || '0' }}</span>
        </div>
      </div>

      <template #footer>
        <div class="adjust-dialog__footer">
          <el-button @click="adjustDialogVisible = false">放弃</el-button>
          <el-button type="primary" :loading="adjustSubmitting" @click="submitAdjustForm">
            确认调配过账
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-drawer
      v-model="drawerVisible"
      direction="rtl"
      :size="480"
      :show-close="false"
      :with-header="false"
      class="dealer-drawer"
      destroy-on-close
      @closed="selectedDealer = null"
    >
      <div v-if="selectedDealer" class="dealer-drawer__inner">
        <div class="dealer-drawer__header">
          <h2 class="dealer-drawer__title">基本档案资料卡</h2>
          <button type="button" class="dealer-drawer__close" @click="closeDrawer">
            <el-icon><Close /></el-icon>
          </button>
        </div>

        <div class="dealer-drawer__body">
          <div class="dealer-card">
            <div class="dealer-card__head">
              <div class="dealer-card__icon">
                <el-icon><Shop /></el-icon>
              </div>
              <div>
                <h3 class="dealer-card__name">{{ selectedDealer.name }}</h3>
                <span class="dealer-card__id">dealerId: {{ selectedDealer.id }}</span>
              </div>
            </div>
            <div class="dealer-card__fields">
              <div class="dealer-field">
                <span class="dealer-field__label">统一社会信用代码</span>
                <span class="dealer-field__value dealer-field__value--mono">{{ selectedDealer.creditCode }}</span>
              </div>
              <div class="dealer-field">
                <span class="dealer-field__label">车行管理员</span>
                <span class="dealer-field__value">{{ selectedDealer.adminName }}</span>
              </div>
              <div class="dealer-field">
                <span class="dealer-field__label">联系电话</span>
                <span class="dealer-field__value dealer-field__value--mono">{{ selectedDealer.phone }}</span>
              </div>
              <div class="dealer-field">
                <span class="dealer-field__label">驻点地址</span>
                <span class="dealer-field__value dealer-field__value--addr">{{ selectedDealer.address }}</span>
              </div>
              <div class="dealer-field">
                <span class="dealer-field__label">入驻时期</span>
                <span class="dealer-field__value dealer-field__value--date">{{ selectedDealer.joinedAt }}</span>
              </div>
            </div>
          </div>

          <div class="dealer-stats">
            <div class="dealer-stats__item">
              <span class="dealer-stats__label">本月交易规模</span>
              <span class="dealer-stats__value dealer-stats__value--accent">{{ selectedDealer.monthlyOrders }} 单</span>
            </div>
            <div class="dealer-stats__item">
              <span class="dealer-stats__label">保证金现金流</span>
              <span class="dealer-stats__value">{{ formatDeposit(selectedDealer.depositBalance) }}</span>
            </div>
          </div>

          <div class="dealer-members">
            <div class="dealer-members__head">
              <div class="dealer-members__title-row">
                <h3 class="dealer-members__title">旗下成员 / 子账号</h3>
                <span class="dealer-members__badge">{{ drawerMembers.length }} 人</span>
              </div>
              <button type="button" class="dealer-members__add" @click="handleAddMember">＋ 添加成员</button>
            </div>
            <div class="dealer-members__list">
              <div v-for="member in drawerMembers" :key="member.userId" class="dealer-member">
                <div class="dealer-member__info">
                  <div class="dealer-member__avatar">{{ memberAvatar(member.name) }}</div>
                  <div>
                    <div class="dealer-member__name">{{ member.name }}</div>
                    <div class="dealer-member__meta">
                      {{ member.phone }}
                      <span class="dealer-member__uid">(userId: {{ member.userId }})</span>
                    </div>
                  </div>
                </div>
                <div class="dealer-member__actions">
                  <button
                    type="button"
                    class="dealer-member__btn dealer-member__btn--admin"
                    @click="handleSetAdmin(member)"
                  >
                    设为管理员
                  </button>
                  <button
                    type="button"
                    class="dealer-member__btn dealer-member__btn--remove"
                    @click="handleRemoveMember(member)"
                  >
                    移除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="dealer-drawer__footer">
          <button type="button" class="dealer-drawer__close-btn" @click="closeDrawer">关闭底册</button>
        </div>
      </div>
    </el-drawer>
  </div>

</template>

<script setup lang="ts">
/**
 * 车行管理页面。
 * 提供车行列表多条件筛选与分页、行点击详情抽屉、新增合作车行弹窗、
 * 保证金调配弹窗及成员管理（设管理员/移除）操作。
 * 业务逻辑由 useDealers composable 承载，本文件仅负责模板绑定与样式。
 */
import PageHeader from '../../components/PageHeader.vue'
import { Search } from '@element-plus/icons-vue'
import { useDealers, type Dealer } from './hooks/useDealers'

// 从 composable 解构页面所需的响应式状态、计算属性与交互方法
const {
  keyword,
  statusFilter,
  provinceFilter,
  currentPage,
  pageSize,
  createDialogVisible,
  createSubmitting,
  createFormRef,
  adjustDialogVisible,
  adjustSubmitting,
  adjustTarget,
  adjustAmount,
  drawerVisible,
  selectedDealer,
  drawerMembers,
  createForm,
  provinces,
  dealers,
  filteredDealers,
  paginatedDealers,
  pageRangeStart,
  pageRangeEnd,
  statusLabel,
  handleRowClick,
  closeDrawer,
  formatDeposit,
  memberAvatar,
  handleAddMember,
  handleSetAdmin,
  openCreateDialog,
  resetCreateForm,
  formatCredit,
  generateDealerId,
  openAdjustDialog,
  resetAdjustForm,
  handleRemoveMember,
  submitCreateForm,
  submitAdjustForm,
  confirmSuspend,
  createRules,
} = useDealers()

</script>

<style lang="scss" scoped>

@import '../../styles/page.css';

.dealers-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
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

.table-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  overflow: hidden;
}

.dealers-table {
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

[data-theme='dark'] .dealers-table {
  :deep(.el-table__header-wrapper th.el-table__cell) {
    background: var(--surface-glass-bg) !important;
    color: var(--text-secondary);
    border-bottom-color: var(--border-color);
  }

  :deep(.el-table__body tr:hover > td.el-table__cell) {
    background: var(--table-hover) !important;
  }
}

.cell-name {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__title {
    font-weight: 700;
    color: var(--text-primary);
  }

  &__meta {
    font-size: 11px;
    color: var(--text-muted);
  }

  &__id {
    font-family: ui-monospace, monospace;
    margin-top: 2px;
  }
}

.cell-admin {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 12px;

  &__name,
  &__phone {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }

  &__name {
    font-weight: 600;
    color: var(--text-primary);
  }

  &__phone {
    font-family: ui-monospace, monospace;
    font-weight: 600;
    color: var(--text-secondary);
  }

  .el-icon {
    font-size: 14px;
    color: var(--text-muted);
  }
}

.cell-location {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 145px;

  &__tag {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    width: fit-content;
    padding: 2px 6px;
    font-size: 10px;
    font-weight: 800;
    color: var(--color-primary);
    background: var(--color-primary-soft-bg);
    border: 1px solid var(--color-primary-border);
    border-radius: 6px;
    white-space: nowrap;

    .el-icon {
      font-size: 12px;
    }
  }

  &__addr {
    font-size: 11px;
    color: var(--text-secondary);
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.cell-stats,
.cell-vehicles {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  font-family: ui-monospace, monospace;
  min-width: 50px;
  margin: 0 auto;

  &__row {
    display: flex;
    justify-content: space-between;
    width: 100%;
    gap: 8px;
  }

  &__label {
    color: var(--text-muted);
  }

  &__value {
    font-weight: 700;
    color: var(--text-primary);

    &--accent {
      color: var(--color-primary);
    }
  }
}

.cell-vehicles__on-sale {
  font-weight: 700;
  color: #059669;
  background: #ecfdf5;
  padding: 0 4px;
  border-radius: 4px;
}

.cell-license {
  display: inline-flex;
  color: var(--text-muted);
  transition: color 0.15s;

  &:hover {
    color: var(--color-primary);
  }

  .el-icon {
    font-size: 20px;
  }

  &__empty {
    font-size: 10px;
    color: var(--text-muted);
  }
}

.cell-credit {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 11px;
  font-family: ui-monospace, monospace;
  font-weight: 700;
  color: var(--text-primary);

  &__label {
    font-size: 9px;
    font-weight: 400;
    color: var(--text-muted);
  }

  &__none {
    font-size: 10px;
    color: var(--text-muted);
  }
}

.status-pill {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 10px;
  font-weight: 600;
  line-height: 1;
  white-space: nowrap;

  &--active {
    color: #059669;
    background: #ecfdf5;
    border: 1px solid #d1fae5;
  }

  &--suspended {
    color: #e11d48;
    background: #fff1f2;
    border: 1px solid #fecdd3;
  }
}

.action-btn {
  font-size: 11px;
  font-weight: 500;
  padding: 4px 8px;
  height: auto;
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

  &--danger:hover {
    color: #e11d48;
    border-color: #fecdd3;
    background: #fff1f2;
  }
}

.table-actions {
  justify-content: flex-end;
  max-width: 100px;
  margin-left: auto;
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

.create-dialog,
.adjust-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
  }

  :deep(.el-dialog__header) {
    padding: 24px 24px 0;
    margin-right: 0;
  }

  :deep(.el-dialog__body) {
    padding: 20px 24px 8px;
  }

  :deep(.el-dialog__footer) {
    padding: 12px 24px 24px;
  }

  &__header {
    padding-right: 24px;
  }

  &__title {
    margin: 0;
    font-size: 16px;
    font-weight: 800;
    color: var(--text-primary);
    line-height: 1.4;
  }

  &__subtitle,
  &__subject {
    margin: 6px 0 0;
    font-size: 11px;
    color: var(--text-muted);
    line-height: 1.5;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}

.create-form {
  :deep(.el-form-item__label) {
    font-size: 12px;
    font-weight: 700;
    color: var(--text-secondary);
    padding-bottom: 4px;
  }

  :deep(.el-input),
  :deep(.el-select) {
    width: 100%;
  }

  &__row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }

  &__credit {
    width: 100%;

    :deep(.el-input__wrapper) {
      width: 100%;
    }
  }
}

.mono-input :deep(.el-input__inner) {
  font-family: ui-monospace, monospace;
}

.adjust-form {
  &__hint {
    margin: 0 0 12px;
    font-size: 13px;
    color: var(--text-secondary);
  }

  &__input {
    margin-bottom: 16px;
  }

  &__balance {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    color: var(--text-muted);
  }
}



.dealer-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f3f4f6;
  padding: 20px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  &__head {
    display: flex;
    align-items: center;
    gap: 12px;
    border-bottom: 1px solid #f9fafb;
    padding-bottom: 16px;
    margin-bottom: 16px;
  }

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    border-radius: 12px;
    background: #eef2ff;
    color: #4f46e5;

    .el-icon {
      font-size: 20px;
    }
  }

  &__name {
    margin: 0;
    font-size: 16px;
    font-weight: 700;
    color: #111827;
  }

  &__id {
    font-family: ui-monospace, monospace;
    font-size: 10px;
    color: #9ca3af;
  }

  &__fields {
    display: flex;
    flex-direction: column;
    gap: 12px;
    font-size: 14px;
  }
}

.dealer-field {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #4b5563;

  &__label {
    color: #9ca3af;
    flex-shrink: 0;
  }

  &__value {
    font-weight: 500;

    &--mono {
      font-family: ui-monospace, monospace;
    }

    &--addr {
      text-align: right;
      max-width: 66%;
      line-height: 1.4;
    }

    &--date {
      font-family: ui-monospace, monospace;
      font-size: 11px;
      color: #6b7280;
    }
  }
}

.dealer-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;

  &__item {
    background: #fff;
    border-radius: 12px;
    border: 1px solid #f3f4f6;
    padding: 16px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
  }

  &__label {
    font-size: 11px;
    color: #6b7280;
  }

  &__value {
    margin-top: 4px;
    font-family: ui-monospace, monospace;
    font-weight: 700;
    font-size: 14px;
    color: #1f2937;

    &--accent {
      font-weight: 900;
      font-size: 20px;
      color: #4f46e5;
    }
  }
}

.dealer-members {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f3f4f6;
  padding: 20px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #f9fafb;
    padding-bottom: 12px;
    margin-bottom: 12px;
    gap: 12px;
  }

  &__title-row {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__title {
    margin: 0;
    font-size: 14px;
    font-weight: 700;
    color: #111827;
  }

  &__badge {
    background: #eef2ff;
    color: #4f46e5;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 10px;
    font-weight: 700;
  }

  &__add {
    flex-shrink: 0;
    font-size: 12px;
    font-weight: 700;
    color: #4f46e5;
    background: #eef2ff;
    border: none;
    padding: 4px 8px;
    border-radius: 4px;
    cursor: pointer;
    transition: background 0.15s, color 0.15s;

    &:hover {
      color: #4338ca;
      background: #e0e7ff;
    }
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
}

.dealer-member {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #f9fafb;
  background: rgba(249, 250, 251, 0.3);

  @media (min-width: 480px) {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    gap: 0;
  }

  &__info {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #e2e8f0;
    color: #64748b;
    font-weight: 700;
    font-size: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  &__name {
    font-weight: 700;
    font-size: 12px;
    color: #111827;
  }

  &__meta {
    font-size: 10px;
    color: #6b7280;
    margin-top: 2px;
  }

  &__uid {
    font-family: ui-monospace, monospace;
    color: #9ca3af;
    margin-left: 4px;
  }

  &__actions {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-shrink: 0;
  }

  &__btn {
    font-size: 10px;
    font-weight: 700;
    padding: 4px 8px;
    border-radius: 4px;
    cursor: pointer;
    border: none;
    transition: background 0.15s, color 0.15s, border-color 0.15s;

    &--admin {
      color: #6b7280;
      background: #fff;
      border: 1px solid #e5e7eb;

      &:hover {
        color: #374151;
        border-color: #d1d5db;
      }
    }

    &--remove {
      color: #f43f5e;
      background: #fff1f2;

      &:hover {
        color: #be123c;
        background: #ffe4e6;
      }
    }
  }
}

[data-theme='dark'] .dealer-drawer {
  &__header {
    border-bottom-color: var(--border-color);
  }

  &__title {
    color: var(--text-primary);
  }

  &__body {
    background: var(--bg-elevated);
  }

  &__footer {
    border-top-color: var(--border-color);
    background: var(--bg-card);
  }

  &__close-btn {
    color: var(--text-secondary);
    background: var(--bg-elevated);

    &:hover {
      background: var(--table-hover);
    }
  }
}

[data-theme='dark'] .dealer-card,
[data-theme='dark'] .dealer-stats__item,
[data-theme='dark'] .dealer-members {
  background: var(--bg-card);
  border-color: var(--border-color);
}

@media (max-width: 749px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-bar__controls {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-bar__search {
    max-width: none;
  }

  .filter-select__input {
    width: 100%;
  }

  .filter-bar__action {
    width: 100%;
  }

  .create-form__row {
    grid-template-columns: 1fr;
  }

  .pagination {
    flex-direction: column;
    align-items: flex-start;
  }
}

</style>
<style lang="scss">
.dealer-drawer {
  .el-drawer__body {
    padding: 0 !important;
    height: 100%;
    overflow: hidden;
  }

  &__inner {
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px;
    border-bottom: 1px solid #f3f4f6;
    flex-shrink: 0;
  }

  &__title {
    margin: 0;
    font-size: 20px;
    font-weight: 700;
    color: #111827;
    line-height: 1.25;
  }

  &__close {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0;
    border: none;
    background: transparent;
    color: #9ca3af;
    cursor: pointer;
    transition: color 0.15s;

    &:hover {
      color: #4b5563;
    }

    .el-icon {
      font-size: 20px;
    }
  }

  &__body {
    flex: 1;
    overflow-y: auto;
    padding: 24px;
    background: rgba(249, 250, 251, 0.5);
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  &__footer {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 16px;
    border-top: 1px solid #f3f4f6;
    background: #fff;
    flex-shrink: 0;
  }

  &__close-btn {
    padding: 8px 16px;
    font-size: 14px;
    font-weight: 600;
    color: #4b5563;
    background: #f3f4f6;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.15s;

    &:hover {
      background: #e5e7eb;
    }
  }
}
</style>
