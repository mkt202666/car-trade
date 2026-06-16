<template>

  <div class="page">
    <PageHeader title="用户管理" subtitle="管理系统用户、角色权限与账户余额" />

    <el-card class="panel" shadow="never">
      <div class="toolbar">
        <el-input
          v-model="keyword"
          class="toolbar-input"
          placeholder="搜索用户ID、客户姓名、或手机号"
          clearable
          :prefix-icon="Search"
        />
        <el-select v-model="roleFilter" class="toolbar-select role-select" placeholder="全部业务角色">
          <el-option label="全部业务角色" value="all" />
          <el-option label="个人用户" value="个人用户" />
          <el-option label="车行用户" value="车行用户" />
          <el-option label="系统管理员" value="系统管理员" />
          <el-option label="已注销" value="已注销" />
        </el-select>
        <el-button type="primary" class="toolbar-action" @click="openRegisterDialog">
          <el-icon><Plus /></el-icon>
          提报建档新客群
        </el-button>
      </div>

      <el-table
        ref="tableRef"
        :data="filteredUsers"
        row-key="id"
        :expand-row-keys="expandedKeys"
        style="width: 100%"
        class="users-table"
        :row-class-name="rowClassName"
        v-loading="loading"
        @row-click="handleRowClick"
      >
        <el-table-column type="expand" width="48">
          <template #default="{ row }">
            <div class="profile-panel">
              <div class="profile-panel__header">
                <h3 class="profile-panel__title">用户档案资料 (详细)</h3>
                <div v-if="isEditing(row.id)" class="profile-panel__actions">
                  <el-button size="small" @click.stop="cancelEdit">取消</el-button>
                  <el-button type="primary" size="small" @click.stop="saveEdit(row as User)">保存修改</el-button>
                </div>
                <el-button
                  v-else
                  type="primary"
                  link
                  size="small"
                  @click.stop="startEdit(row as User)"
                >
                  编辑资料
                </el-button>
              </div>

              <div class="profile-form">
                <div class="profile-form__fields">
                  <div class="profile-field">
                    <label>完整的车行名称</label>
                    <el-input
                      v-if="isEditing(row.id)"
                      v-model="row.profile.dealershipName"
                      placeholder="请输入完整车行名称"
                    />
                    <span v-else class="profile-field__value">{{ row.profile.dealershipName || '—' }}</span>
                  </div>
                  <div class="profile-field">
                    <label>统一社会信用代码</label>
                    <el-input
                      v-if="isEditing(row.id)"
                      v-model="row.profile.creditCode"
                      placeholder="请输入统一社会信用代码"
                    />
                    <span v-else class="profile-field__value">{{ row.profile.creditCode || '—' }}</span>
                  </div>
                  <div class="profile-field">
                    <label>车行所在省市</label>
                    <el-input
                      v-if="isEditing(row.id)"
                      v-model="row.profile.provinceCity"
                      placeholder="请输入省市"
                    />
                    <span v-else class="profile-field__value">{{ row.profile.provinceCity || '—' }}</span>
                  </div>
                  <div class="profile-field">
                    <label>身份证编号</label>
                    <el-input
                      v-if="isEditing(row.id)"
                      v-model="row.profile.idNumber"
                      placeholder="请输入身份证号"
                    />
                    <span v-else class="profile-field__value">{{ row.profile.idNumber || '—' }}</span>
                  </div>
                </div>

                <div class="profile-form__images">
                  <div v-for="item in imageFields" :key="item.key" class="image-upload">
                    <label>{{ item.label }}</label>
                    <div class="image-upload__box">
                      <img
                        v-if="row.profile[item.key]"
                        :src="row.profile[item.key]"
                        :alt="item.label"
                        class="image-upload__preview"
                      />
                      <el-input
                        v-if="isEditing(row.id)"
                        v-model="row.profile[item.key]"
                        placeholder="请输入图片外链URL..."
                        class="image-upload__input"
                      />
                      <span v-else-if="!row.profile[item.key]" class="image-upload__empty">暂无图片</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="姓名" min-width="150">
          <template #default="{ row }">
            <div class="cell-name">
              <span class="cell-name__title">{{ row.name }}</span>
              <span class="cell-name__meta">注册: {{ row.registerAt }}</span>
              <span class="cell-name__meta">ID: {{ row.id }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="类别" width="110">
          <template #default="{ row }">
            <StatusBadge :status="categoryStatus(row.category)" :label="row.category" />
          </template>
        </el-table-column>

        <el-table-column label="联系方式" min-width="150">
          <template #default="{ row }">
            <div class="cell-contact">
              <span>{{ row.phone }}</span>
              <span class="cell-contact__wechat">
                <el-icon><ChatDotRound /></el-icon>
                {{ row.wechat }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="挂牌/求购数据" width="130">
          <template #default="{ row }">
            <div class="cell-stats">
              <span>在售 <strong>{{ row.listing.onSale }}</strong></span>
              <span>下架 <strong>{{ row.listing.offShelf }}</strong></span>
              <span>求购 <strong>{{ row.listing.wanted }}</strong></span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="用户车行" min-width="140">
          <template #default="{ row }">
            <div class="cell-dealer">
              <span>{{ row.dealership.name }}</span>
              <span class="cell-dealer__sub">{{ row.dealership.vehicles }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="保证金" width="110">
          <template #default="{ row }">
            <div class="cell-money">
              <span class="cell-money__label">可用 / 总计</span>
              <span>{{ row.deposit.available }} / {{ row.deposit.total }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="交易" width="120">
          <template #default="{ row }">
            <div class="cell-transaction">
              <span>{{ row.transaction.count }} 笔交易</span>
              <span class="cell-transaction__total">{{ row.transaction.total }} 总额</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="金融授信" width="110">
          <template #default="{ row }">
            <div class="cell-money">
              <span class="cell-money__label">已用 / 授信</span>
              <span>{{ row.credit.used }} / {{ row.credit.total }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="table-actions" @click.stop>
              <el-button type="primary" link size="small" @click="startEdit(row as User)">编辑</el-button>
              <el-button
                type="primary"
                link
                size="small"
                :disabled="row.category === '已注销' || row.category === '已冻结'"
                @click="openAdjustDialog(row as User)"
              >
                调保
              </el-button>
              <el-popconfirm
                title="确定冻结该用户？冻结后账户将无法正常使用。"
                confirm-button-text="确认冻结"
                cancel-button-text="取消"
                width="220"
                @confirm="confirmDisable(row as User)"
              >
                <template #reference>
                  <el-button
                    type="danger"
                    link
                    size="small"
                    :disabled="row.category === '已注销' || row.category === '已冻结'"
                  >
                    冻结
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="10"
          :total="totalUsers"
          layout="total, prev, pager, next"
          background
        />
      </div>
    </el-card>

    <el-dialog
      v-model="registerDialogVisible"
      width="520px"
      class="register-dialog"
      :show-close="true"
      align-center
      destroy-on-close
      @closed="resetRegisterForm"
    >
      <template #header>
        <div class="register-dialog__header">
          <h2 class="register-dialog__title">手动提报开通新客户往来账户</h2>
          <p class="register-dialog__subtitle">填写主要营业名和联系电话，建立建档档案</p>
        </div>
      </template>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-position="top"
        class="register-form"
      >
        <el-form-item label="注册姓名/车商名称" prop="name" required>
          <el-input v-model="registerForm.name" placeholder="如：江苏苏美二手名车汇" />
        </el-form-item>
        <el-form-item label="联系电话号码（实名预留）" prop="phone" required>
          <el-input v-model="registerForm.phone" placeholder="13xxxxxxxxx" maxlength="11" />
        </el-form-item>
        <el-form-item label="微信 Union ID (可选)" prop="wechat">
          <el-input v-model="registerForm.wechat" placeholder="wxid_..." />
        </el-form-item>
        <div class="register-form__row">
          <el-form-item label="归置角色分类" prop="category">
            <el-select v-model="registerForm.category" placeholder="选择角色">
              <el-option label="个人用户" value="个人用户" />
              <el-option label="车行用户" value="车行用户" />
            </el-select>
          </el-form-item>
          <el-form-item label="首充存余保证金 (元)" prop="deposit">
            <el-input-number
              v-model="registerForm.deposit"
              :min="0"
              :step="1000"
              :precision="0"
              controls-position="right"
              class="register-form__deposit"
            />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="register-dialog__footer">
          <el-button @click="registerDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="registerSubmitting" @click="submitRegisterForm">
            确认档案注册
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
          <h2 class="adjust-dialog__title">调定客户授信/保证金余额</h2>
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
          <span>当前余额: {{ formatCurrency(adjustCurrentBalance) }}</span>
          <span>划转后余额: {{ formatCurrency(adjustAfterBalance) }}</span>
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
  </div>

</template>

<script setup lang="ts">
/**
 * 用户管理页面。
 * 提供用户列表检索与角色筛选、可展开行档案编辑、手动建档弹窗及保证金调配弹窗。
 * 业务逻辑由 useUsers composable 承载，本文件仅负责模板绑定与样式。
 */
import PageHeader from '../../components/PageHeader.vue'
import StatusBadge from '../../components/StatusBadge.vue'
import { Search } from '@element-plus/icons-vue'
import { useUsers, type User } from './hooks/useUsers'

// 从 composable 解构页面所需的响应式状态、计算属性与交互方法
const {
  imageFields,
  keyword,
  roleFilter,
  currentPage,
  totalUsers,
  loading,
  expandedKeys,
  editingUserId,
  editBackup,
  registerDialogVisible,
  registerSubmitting,
  registerFormRef,
  registerRules,
  adjustDialogVisible,
  adjustSubmitting,
  adjustTarget,
  adjustAmount,
  registerForm,
  users,
  filteredUsers,
  adjustCurrentBalance,
  adjustAfterBalance,
  isEditing,
  handleRowClick,
  rowClassName,
  startEdit,
  cancelEdit,
  saveEdit,
  confirmDisable,
  parseMoneyStr,
  formatCurrency,
  openAdjustDialog,
  resetAdjustForm,
  categoryStatus,
  formatDeposit,
  generateUserId,
  openRegisterDialog,
  resetRegisterForm,
  submitAdjustForm,
  submitRegisterForm,
} = useUsers()

</script>

<style lang="scss" scoped>

@import '../../styles/page.css';

.toolbar {
  .toolbar-action {
    margin-left: auto;
  }
}

.role-select {
  width: 160px;
}

.users-table {
  :deep(.el-table__row) {
    cursor: pointer;
  }

  :deep(.row-expanded) {
    background: var(--table-hover) !important;
  }

  :deep(.el-table__expanded-cell) {
    padding: 0 !important;
    background: var(--bg-elevated);
    overflow-x: hidden;
  }

  :deep(.el-table__expanded-cell .cell) {
    padding: 0;
    overflow-x: hidden;
  }
}

.cell-name {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__title {
    font-weight: 600;
    color: var(--text-primary);
  }

  &__meta {
    font-size: 12px;
    color: var(--text-muted);
  }
}

.cell-contact {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;

  &__wechat {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: var(--text-secondary);
  }
}

.cell-stats {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
  color: var(--text-secondary);

  strong {
    color: var(--text-primary);
    font-weight: 600;
  }
}

.cell-dealer {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 13px;

  &__sub {
    font-size: 12px;
    color: var(--text-muted);
  }
}

.cell-money,
.cell-transaction {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 13px;
}

.cell-money__label,
.cell-transaction__total {
  font-size: 11px;
  color: var(--text-muted);
}

.profile-panel {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  padding: 20px 24px 24px 56px;
  border-top: 1px solid var(--border-color);
  overflow-x: hidden;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
  }

  &__title {
    margin: 0;
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
  }

  &__actions {
    display: flex;
    gap: 8px;
  }
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-width: 0;

  &__fields {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px 24px;
  }

  &__images {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 16px;
  }
}

.profile-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;

  :deep(.el-input) {
    width: 100%;
  }

  label {
    font-size: 13px;
    font-weight: 500;
    color: var(--text-secondary);
  }

  &__value {
    font-size: 14px;
    color: var(--text-primary);
    min-height: 32px;
    line-height: 32px;
  }
}

.image-upload {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;

  label {
    font-size: 13px;
    font-weight: 500;
    color: var(--text-secondary);
  }

  &__box {
    display: flex;
    flex-direction: column;
    gap: 10px;
    min-width: 0;
    min-height: 140px;
    padding: 16px;
    border: 1px dashed var(--border-color);
    border-radius: 8px;
    background: var(--bg-card);
    overflow: hidden;
  }

  &__input {
    width: 100%;

    :deep(.el-input__wrapper) {
      width: 100%;
    }
  }

  &__preview {
    max-width: 100%;
    max-height: 100px;
    object-fit: contain;
    border-radius: 4px;
  }

  &__empty {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    color: var(--text-muted);
  }
}

@media (max-width: 1200px) {
  .profile-form__fields,
  .profile-form__images {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 749px) {
  .toolbar .toolbar-action {
    margin-left: 0;
    width: 100%;
  }

  .profile-panel {
    padding: 16px;
  }
}

.register-dialog {
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
    font-size: 18px;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.4;
  }

  &__subtitle {
    margin: 8px 0 0;
    font-size: 13px;
    color: var(--text-muted);
    line-height: 1.5;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

.register-form {
  :deep(.el-form-item__label) {
    font-size: 13px;
    font-weight: 500;
    color: var(--text-secondary);
    padding-bottom: 6px;
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

  &__deposit {
    width: 100%;

    :deep(.el-input__wrapper) {
      width: 100%;
    }
  }
}

@media (max-width: 520px) {
  .register-form__row {
    grid-template-columns: 1fr;
  }
}

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
    padding: 16px 24px 8px;
  }

  :deep(.el-dialog__footer) {
    padding: 12px 24px 24px;
  }

  &__header {
    padding-right: 24px;
  }

  &__title {
    margin: 0;
    font-size: 18px;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.4;
  }

  &__subject {
    margin: 8px 0 0;
    font-size: 13px;
    color: var(--text-muted);
    line-height: 1.5;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

.adjust-form {
  &__hint {
    margin: 0 0 12px;
    font-size: 13px;
    color: var(--text-muted);
    line-height: 1.5;
  }

  &__input {
    :deep(.el-input__wrapper) {
      padding: 12px 16px;
      border-radius: 10px;
    }

    :deep(.el-input__inner) {
      font-size: 15px;
    }
  }

  &__balance {
    display: flex;
    justify-content: space-between;
    margin-top: 16px;
    font-size: 13px;
    color: var(--text-secondary);
  }
}

</style>
