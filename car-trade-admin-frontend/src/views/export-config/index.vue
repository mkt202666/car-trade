<template>

  <div class="page">
    <PageHeader title="出口配置菜单" subtitle="管理前端展示的出口国家、要求及展示条件">
      <template #actions>
        <el-button type="primary" @click="openCreate">
          <el-icon class="btn-icon"><Plus /></el-icon>
          新增地区配置
        </el-button>
      </template>
    </PageHeader>

    <div class="table-panel">
      <el-table :data="regions" style="width: 100%" class="export-table">
        <el-table-column label="地区名称/图标" min-width="160">
          <template #default="{ row }">
            <div class="cell-region">
              <span class="cell-region__flag">
                <img v-if="isImageIcon(row.icon)" :src="row.icon" alt="" class="cell-region__img" />
                <span v-else>{{ displayIcon(row as ExportRegion) }}</span>
              </span>
              <div class="cell-region__info">
                <span class="cell-region__name">{{ row.name }}</span>
                <span class="cell-region__code">{{ row.code }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="所属地区分组" min-width="110">
          <template #default="{ row }">
            <el-tag :type="groupTagType(row.groupKey)" effect="light" size="small" round class="group-tag">
              {{ row.group }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="参数约束条件" min-width="220">
          <template #default="{ row }">
            <div class="constraint-list">
              <el-tag
                v-for="(item, idx) in row.constraints"
                :key="idx"
                effect="plain"
                size="small"
                class="constraint-tag"
              >
                <span class="constraint-tag__field">{{ item.field }}</span>
                <span class="constraint-tag__op">{{ item.operator }}</span>
                <span class="constraint-tag__value">{{ item.value }}</span>
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="出口要求展示描述" min-width="280">
          <template #default="{ row }">
            <ol class="requirement-list">
              <li v-for="(req, idx) in row.requirements" :key="idx">{{ req }}</li>
            </ol>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <StatusBadge :status="row.status === 'ACTIVE' ? 'success' : 'neutral'" :label="row.status" />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" link size="small" @click="openEdit(row as ExportRegion)">
                <el-icon><EditPen /></el-icon>
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row as ExportRegion)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      width="720px"
      class="region-dialog"
      :show-close="true"
      align-center
      destroy-on-close
      @closed="resetForm"
    >
      <template #header>
        <h3 class="region-dialog__title">{{ dialogMode === 'create' ? '新增配置' : '编辑配置' }}</h3>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-position="top"
        class="region-form"
      >
        <div class="region-form__grid">
          <el-form-item label="地区名称 (如: 俄罗斯)" prop="name">
            <el-input v-model="form.name" placeholder="俄罗斯" />
          </el-form-item>
          <el-form-item label="地区代码 (如: RU)" prop="code">
            <el-input v-model="form.code" placeholder="RU" maxlength="8" class="mono-input" />
          </el-form-item>
          <el-form-item label="所属地区分组 (如: 独联体, 其他)" prop="group">
            <el-input v-model="form.group" placeholder="独联体" />
          </el-form-item>
          <el-form-item label="地区图标 (Emoji国旗、URL或上传图片)">
            <div class="icon-input-row">
              <el-input v-model="form.icon" placeholder="URL或Emoji" />
              <el-upload
                :show-file-list="false"
                accept="image/*"
                :auto-upload="false"
                @change="handleIconUpload"
              >
                <el-button class="upload-btn" title="上传图标">
                  <el-icon><UploadFilled /></el-icon>
                </el-button>
              </el-upload>
            </div>
          </el-form-item>

          <el-form-item label="匹配机读参数约束条件 (组合规则)" class="region-form__full">
            <div class="constraint-editor">
              <div v-for="(rule, idx) in form.constraints" :key="idx" class="constraint-row">
                <el-select style="flex: 1;" v-model="rule.field" class="constraint-field-select">
                  <el-option v-for="opt in constraintFields" :key="opt" :label="opt" :value="opt" />
                </el-select>
                <el-select style="flex: 1;" v-model="rule.operator" class="constraint-op-select">
                  <el-option v-for="opt in constraintOperators" :key="opt" :label="opt" :value="opt" />
                </el-select>
                <el-input
                  style="flex: 1;"
                  v-model="rule.value"
                  class="constraint-value"
                  placeholder="比较值 (如: 左舵, 国VI, 5)"
                />
                <el-button
                  type="danger"
                  link
                  :disabled="form.constraints.length <= 1"
                  title="移除条件"
                  @click="removeConstraint(idx)"
                >
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
              <el-button type="primary" link class="add-rule-btn" @click="addConstraint">
                <el-icon><Plus /></el-icon>
                添加规则
              </el-button>
            </div>
          </el-form-item>

          <el-form-item label="条件描述 (用户展示出口要求，可换行)" prop="requirementsText" class="region-form__full">
            <el-input
              v-model="form.requirementsText"
              type="textarea"
              :rows="4"
              placeholder="1. 车龄需低于5年&#10;2. 必须为左舵"
            />
          </el-form-item>

          <el-form-item label="状态">
            <el-select v-model="form.status" placeholder="选择状态">
              <el-option label="激活 (前端展示)" value="active" />
              <el-option label="停用 (隐藏处理)" value="inactive" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="region-dialog__footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submitForm">
            <el-icon class="btn-icon"><DocumentChecked /></el-icon>
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>

</template>

<script setup lang="ts">
/**
 * 出口配置页面
 * 以表格展示各出口国家/地区的分组、机读约束条件与用户可见的出口要求；
 * 支持新增/编辑/删除地区配置，编辑弹窗内可维护参数约束规则与条件描述文本。
 */

import { Close, Delete, DocumentChecked, EditPen, Plus, UploadFilled } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import StatusBadge from '../../components/StatusBadge.vue'
import { useExportConfig, type ExportRegion } from './hooks/useExportConfig'

const {
  constraintFields,
  constraintOperators,
  formRef,
  dialogVisible,
  dialogMode,
  editingId,
  submitting,
  form,
  formRules,
  regions,
  createEmptyForm,
  countryFlag,
  isImageIcon,
  displayIcon,
  groupTagType,
  resolveGroupKey,
  formatRequirements,
  parseRequirements,
  fillForm,
  resetForm,
  openCreate,
  openEdit,
  addConstraint,
  removeConstraint,
  handleIconUpload,
  validateConstraints,
  buildRegionPayload,
  submitForm,
  handleDelete,
} = useExportConfig()

</script>

<style lang="scss" scoped>

@import '../../styles/page.css';

.btn-icon {
  margin-right: 4px;
}

.table-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  overflow: hidden;
}

.export-table {
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

.cell-region {
  display: flex;
  align-items: center;
  gap: 10px;

  &__flag {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #f3f4f6;
    font-size: 20px;
    line-height: 1;
    flex-shrink: 0;
    overflow: hidden;
  }

  &__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  &__name {
    font-size: 13px;
    font-weight: 700;
    color: var(--text-primary);
  }

  &__code {
    font-size: 10px;
    font-weight: 600;
    font-family: ui-monospace, monospace;
    color: var(--text-muted);
    letter-spacing: 0.05em;
  }
}

.group-tag {
  font-weight: 600;
  border: none;
}

.constraint-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-start;
}

.constraint-tag {
  height: auto;
  padding: 4px 8px;
  white-space: normal;
  line-height: 1.4;

  &__field {
    color: #6b7280;
    font-weight: 500;
  }

  &__op {
    margin: 0 4px;
    color: #7c3aed;
    font-weight: 600;
  }

  &__value {
    color: #374151;
    font-weight: 600;
  }
}

.requirement-list {
  margin: 0;
  padding-left: 18px;
  font-size: 12px;
  line-height: 1.6;
  color: var(--text-secondary);

  li + li {
    margin-top: 2px;
  }
}

.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.region-dialog {
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

  &__title {
    margin: 0;
    font-size: 16px;
    font-weight: 800;
    color: #1f2937;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}

.region-form {
  :deep(.el-form-item__label) {
    font-size: 12px;
    font-weight: 600;
    color: #6b7280;
    padding-bottom: 4px;
  }

  :deep(.el-input),
  :deep(.el-select),
  :deep(.el-textarea) {
    width: 100%;
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 0 16px;
  }

  &__full {
    grid-column: 1 / -1;
  }
}

.mono-input :deep(.el-input__inner) {
  font-family: ui-monospace, monospace;
  text-transform: uppercase;
}

.icon-input-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;

  .el-input {
    flex: 1;
  }
}

.upload-btn {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  padding: 0;
  color: #4f46e5;
  background: #eef2ff;
  border-color: #e0e7ff;

  &:hover {
    color: #4338ca;
    background: #e0e7ff;
    border-color: #c7d2fe;
  }
}

.constraint-editor {
  width: 100%;
  padding: 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.constraint-row {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 8px;

  & + & {
    margin-top: 12px;
  }
}

.constraint-field-select {
  width: 160px;
  flex-shrink: 0;
}

.constraint-op-select {
  width: 88px;
  flex-shrink: 0;
}

.constraint-value {
  flex: 1;
  min-width: 0;
}

.add-rule-btn {
  margin-top: 12px;
  padding-left: 0;
  font-weight: 700;
}

@media (max-width: 749px) {
  .region-form__grid {
    grid-template-columns: 1fr;
  }

  .constraint-row {
    flex-wrap: wrap;
  }

  .constraint-field-select,
  .constraint-op-select,
  .constraint-value {
    width: 100%;
  }
}

</style>
