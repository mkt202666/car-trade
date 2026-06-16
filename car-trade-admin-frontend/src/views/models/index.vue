<template>

  <div class="page models-page">
    <PageHeader title="车型库维护" subtitle="维护3级车型字典与挂架关联数据">
      <template #actions>
        <el-button @click="handleDownloadTemplate">
          <el-icon class="btn-icon"><Download /></el-icon>
          下载模板
        </el-button>
        <el-upload
          :show-file-list="false"
          accept=".xlsx,.xls"
          :auto-upload="false"
          @change="handleUpload"
        >
          <el-button type="success" :loading="importing">
            <el-icon v-if="!importing" class="btn-icon"><Upload /></el-icon>
            {{ importing ? '导入中...' : 'Excel 批量导入' }}
          </el-button>
        </el-upload>
        <el-button type="primary" @click="handleCreate">
          <el-icon class="btn-icon"><Plus /></el-icon>
          新增基础车型
        </el-button>
      </template>
    </PageHeader>

    <div class="filter-bar">
      <div class="filter-bar__controls">
        <el-select
          v-model="brandFilter"
          class="filter-select filter-select--brand"
          placeholder="全部品牌分类"
          clearable
        >
          <el-option label="全部品牌分类" value="all" />
          <el-option v-for="brand in BRANDS" :key="brand" :label="brand" :value="brand" />
        </el-select>

        <el-select
          v-model="seriesFilter"
          class="filter-select filter-select--series"
          placeholder="选择车系"
          :disabled="seriesOptions.length === 0"
          clearable
        >
          <el-option label="全部车系" value="all" />
          <el-option v-for="series in seriesOptions" :key="series" :label="series" :value="series" />
        </el-select>

        <el-select
          v-model="variantFilter"
          class="filter-select filter-select--variant"
          placeholder="选择年款及车型"
          :disabled="variantOptions.length === 0"
          clearable
        >
          <el-option label="全部年款及车型" value="all" />
          <el-option v-for="item in variantOptions" :key="item" :label="item" :value="item" />
        </el-select>

        <el-select
          v-model="exportFilter"
          class="filter-select filter-select--export"
          placeholder="满足出口条件"
          clearable
        >
          <el-option label="满足出口条件" value="all" />
          <el-option v-for="country in EXPORT_COUNTRIES" :key="country" :label="country" :value="country" />
        </el-select>

        <el-select
          v-model="energyFilter"
          class="filter-select filter-select--energy"
          placeholder="能源类型"
          clearable
        >
          <el-option label="能源类型" value="all" />
          <el-option v-for="type in ENERGY_TYPES" :key="type" :label="type" :value="type" />
        </el-select>
      </div>
    </div>

    <div class="table-panel">
      <el-table
        ref="tableRef"
        :data="paginatedModels"
        style="width: 100%"
        class="models-table"
        row-key="id"
        :row-class-name="rowClassName"
      >
        <el-table-column label="3级车型谱系" min-width="280">
          <template #default="{ row }">
            <div v-if="isEditing(row as VehicleModel)" class="cell-edit-stack">
              <el-input v-model="editForm.brand" size="small" placeholder="品牌" />
              <el-input v-model="editForm.series" size="small" placeholder="车系" />
              <el-input v-model="editForm.variant" size="small" placeholder="具体车型" />
              <div class="cell-lineage__id">ID: {{ row.id }}</div>
            </div>
            <div v-else class="cell-lineage">
              <div class="cell-lineage__path">
                <span class="cell-lineage__brand">{{ row.brand }}</span>
                <span class="cell-lineage__sep">/</span>
                <span class="cell-lineage__series">{{ row.series }}</span>
              </div>
              <div class="cell-lineage__name">{{ row.variant }}</div>
              <div class="cell-lineage__id">ID: {{ row.id }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="核心参数" min-width="200">
          <template #default="{ row }">
            <div v-if="isEditing(row as VehicleModel)" class="cell-edit-stack">
              <el-date-picker
                v-model="editForm.launchMonth"
                type="month"
                size="small"
                placeholder="上市时间"
                value-format="YYYY-MM"
                format="YYYY年MM月"
                class="cell-edit-date"
              />
              <el-input v-model="editForm.powerType" size="small" placeholder="动力类型" />
              <el-input v-model="editForm.bodyType" size="small" placeholder="车型" />
            </div>
            <dl v-else class="cell-params">
              <div class="cell-params__row">
                <dt>上市时间:</dt>
                <dd>{{ row.launchDate }}</dd>
              </div>
              <div class="cell-params__row">
                <dt>动力类型:</dt>
                <dd>{{ row.powerType }}</dd>
              </div>
              <div class="cell-params__row">
                <dt>车型:</dt>
                <dd>{{ row.bodyType || '-' }}</dd>
              </div>
            </dl>
          </template>
        </el-table-column>

        <el-table-column label="指导价" width="120">
          <template #default="{ row }">
            <div v-if="isEditing(row as VehicleModel)" class="cell-price-edit">
              <el-input v-model="editForm.guidePriceWan" size="small" placeholder="10.99" />
              <span class="cell-price-edit__unit">万</span>
            </div>
            <span v-else class="cell-price">{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column label="出口" width="140">
          <template #default="{ row }">
            <el-select
              v-if="isEditing(row as VehicleModel)"
              v-model="editForm.exportCountries"
              multiple
              collapse-tags
              collapse-tags-tooltip
              size="small"
              placeholder="选择出口地区"
              class="cell-export-select"
            >
              <el-option
                v-for="country in EXPORT_COUNTRIES"
                :key="country"
                :label="country"
                :value="country"
              />
            </el-select>
            <div v-else class="cell-export">
              <el-tag
                v-for="country in row.exportCountries"
                :key="country"
                effect="plain"
                size="small"
                class="export-tag"
              >
                {{ country }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <div class="cell-status">
              <el-tag
                :type="row.status === 'ACTIVE' ? 'success' : 'danger'"
                effect="light"
                size="small"
                class="status-tag"
              >
                {{ row.status }}
              </el-tag>
              <span class="cell-status__date">{{ row.updatedAt }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="130" fixed="right" align="center">
          <template #default="{ row }">
            <div class="table-actions">
              <div v-if="!isEditing(row as VehicleModel)" class="table-actions__icons">
                <el-tooltip :content="row.status === 'ACTIVE' ? '停用此车型' : '恢复启用此车型'" placement="top">
                  <el-button
                    link
                    :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
                    @click="toggleStatus(row as VehicleModel)"
                  >
                    <el-icon><CircleClose v-if="row.status === 'ACTIVE'" /><CircleCheck v-else /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="编辑" placement="top">
                  <el-button type="primary" link @click="startEdit(row as VehicleModel)">
                    <el-icon><EditPen /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button type="danger" link @click="handleDelete(row as VehicleModel)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
              <el-button
                size="small"
                :class="['detail-btn', { 'detail-btn--active': isEditing(row as VehicleModel) || expandedIds.has(row.id) }]"
                @click="isEditing(row as VehicleModel) ? cancelEdit(row as VehicleModel) : toggleDetail(row as VehicleModel)"
              >
                <el-icon :class="{ 'detail-btn__icon--open': isEditing(row as VehicleModel) || expandedIds.has(row.id) }">
                  <ArrowUp v-if="isEditing(row as VehicleModel) || expandedIds.has(row.id)" />
                  <ArrowDown v-else />
                </el-icon>
                {{ isEditing(row as VehicleModel) ? '收起配置' : '详细配置' }}
              </el-button>
            </div>
          </template>
        </el-table-column>

        <el-table-column type="expand" width="1" class-name="expand-column">
          <template #default="{ row }">
            <div
              class="detail-panel"
              :class="{ 'detail-panel--editing': isEditing(row as VehicleModel) }"
            >
              <template v-if="isEditing(row as VehicleModel)">
                <div class="factory-form">
                  <h4 class="factory-form__title">
                    <el-icon><EditPen /></el-icon>
                    车辆出厂详细参数
                  </h4>
                  <el-form label-position="top" class="factory-form__grid">
                    <el-form-item label="厂商">
                      <el-input v-model="editForm.manufacturer" disabled placeholder="厂商 (只读)" />
                    </el-form-item>
                    <el-form-item label="能源类型">
                      <el-input v-model="editForm.factoryParams['能源类型']" />
                    </el-form-item>
                    <el-form-item label="环保标准">
                      <el-input v-model="editForm.factoryParams['环保标准']" />
                    </el-form-item>
                    <el-form-item label="最大功率(kW)">
                      <el-input v-model="editForm.factoryParams['最大功率(kW)']" />
                    </el-form-item>
                    <el-form-item label="最大扭矩(N·m)">
                      <el-input v-model="editForm.factoryParams['最大扭矩(N·m)']" />
                    </el-form-item>
                    <el-form-item label="车门数">
                      <el-input v-model="editForm.factoryParams['车门数']" />
                    </el-form-item>
                    <el-form-item label="座位数">
                      <el-input v-model="editForm.factoryParams['座位数']" />
                    </el-form-item>
                    <el-form-item label="左/右舵">
                      <el-select v-model="editForm.factoryParams['左/右舵']" placeholder="选择">
                        <el-option label="左舵" value="左舵" />
                        <el-option label="右舵" value="右舵" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="油箱容积(L)">
                      <el-input v-model="editForm.factoryParams['油箱容积(L)']" />
                    </el-form-item>
                    <el-form-item label="整车质量(kg)">
                      <el-input v-model="editForm.factoryParams['整车质量(kg)']" />
                    </el-form-item>
                    <el-form-item label="排量(mL)">
                      <el-input v-model="editForm.factoryParams['排量(mL)']" />
                    </el-form-item>
                    <el-form-item label="进气形式">
                      <el-input v-model="editForm.factoryParams['进气形式']" />
                    </el-form-item>
                    <el-form-item label="气缸排列形式">
                      <el-input v-model="editForm.factoryParams['气缸排列形式']" />
                    </el-form-item>
                    <el-form-item label="气缸数">
                      <el-input v-model="editForm.factoryParams['气缸数']" />
                    </el-form-item>
                    <el-form-item label="燃油标号">
                      <el-input v-model="editForm.factoryParams['燃油标号']" />
                    </el-form-item>
                    <el-form-item label="三维尺寸">
                      <el-input v-model="editForm.dimensions" placeholder="4053*1740*1449mm" />
                    </el-form-item>
                  </el-form>
                  <div class="factory-form__footer">
                    <el-button @click="cancelEdit(row as VehicleModel)">取消编辑</el-button>
                    <el-button type="primary" :loading="savingEdit" @click="saveEdit(row as VehicleModel)">
                      <el-icon class="btn-icon"><DocumentChecked /></el-icon>
                      保存修改
                    </el-button>
                  </div>
                </div>
              </template>
              <template v-else>
                <h4 class="detail-panel__title">详细配置 — {{ row.variant }}</h4>
                <el-descriptions :column="3" border size="small">
                  <el-descriptions-item label="品牌">{{ row.brand }}</el-descriptions-item>
                  <el-descriptions-item label="车系">{{ row.series }}</el-descriptions-item>
                  <el-descriptions-item label="能源类型">{{ row.energyType }}</el-descriptions-item>
                  <el-descriptions-item label="上市时间">{{ row.launchDate }}</el-descriptions-item>
                  <el-descriptions-item label="动力类型">{{ row.powerType }}</el-descriptions-item>
                  <el-descriptions-item label="车身级别">{{ row.bodyType || '-' }}</el-descriptions-item>
                  <el-descriptions-item v-if="row.dimensions" label="三维尺寸">{{ row.dimensions }}</el-descriptions-item>
                  <el-descriptions-item label="指导价">{{ row.price }}</el-descriptions-item>
                  <el-descriptions-item label="出口市场" :span="2">
                    {{ row.exportCountries.join('、') || '-' }}
                  </el-descriptions-item>
                </el-descriptions>
                <el-descriptions
                  v-if="row.detailConfig && Object.keys(row.detailConfig).length"
                  :column="3"
                  border
                  size="small"
                  class="detail-panel__extra"
                  title="扩展参数"
                >
                  <el-descriptions-item
                    v-for="(value, key) in row.detailConfig"
                    :key="key"
                    :label="String(key)"
                  >
                    {{ value }}
                  </el-descriptions-item>
                </el-descriptions>
              </template>
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
            <strong>{{ filteredModels.length }}</strong> 项
          </span>
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredModels.length"
          layout="prev, pager, next"
          background
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      width="860px"
      class="model-dialog"
      :show-close="true"
      align-center
      destroy-on-close
      @closed="resetForm"
    >
      <template #header>
        <h3 class="model-dialog__title">新增车型数据</h3>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-position="top"
        class="model-form"
      >
        <div class="model-form__grid">
          <el-form-item label="1级: 品牌品牌" prop="brand">
            <el-input v-model="form.brand" placeholder="如：大众" />
          </el-form-item>
          <el-form-item label="2级: 品牌车系" prop="series">
            <el-input v-model="form.series" placeholder="如：Polo" />
          </el-form-item>

          <el-form-item label="3级: 具体车型型号名称" prop="variant" class="model-form__full">
            <el-input v-model="form.variant" placeholder="如：2024款 1.5L 自动全景乐享版" />
          </el-form-item>

          <el-form-item label="上市时间" prop="launchDate">
            <el-date-picker
              v-model="form.launchDate"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
              format="YYYY年MM月"
              class="model-form__date"
            />
          </el-form-item>
          <el-form-item label="动力类型" prop="powerType">
            <el-input v-model="form.powerType" placeholder="如：1.5T 自然吸气" />
          </el-form-item>
          <el-form-item label="三维尺寸 (长宽高)">
            <el-input v-model="form.dimensions" placeholder="4000*1800*1500mm" />
          </el-form-item>
          <el-form-item label="车型">
            <el-input v-model="form.bodyType" placeholder="如：C级车" />
          </el-form-item>

          <el-form-item label="车型指导价 (元)" prop="guidePrice" class="model-form__half">
            <el-input v-model="form.guidePrice" placeholder="109900" />
          </el-form-item>

          <el-form-item class="model-form__full model-form__checkbox">
            <el-checkbox v-model="form.exportEligible">
              符合出口要求 (系统过滤推荐依据)
            </el-checkbox>
          </el-form-item>

          <el-form-item
            label="其它车型详细参数配置表 (JSON键值对)"
            prop="detailConfigJson"
            class="model-form__full"
          >
            <el-input
              v-model="form.detailConfigJson"
              type="textarea"
              :rows="8"
              class="json-textarea"
              placeholder='{"厂商": "上汽大众", "能源类型": "汽油"}'
            />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="model-dialog__footer">
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
 * 车型库维护页面
 *
 * 维护 3 级车型字典（品牌 / 车系 / 具体型号）及挂架关联数据：支持多级筛选、
 * 分页列表、行内编辑出厂参数、展开查看详细配置、启用/停用切换；
 * 可通过弹窗新增车型或上传附件批量维护（上传待接入）。逻辑由 useModels 提供。
 */
import { ArrowDown, ArrowUp, CircleCheck, CircleClose, Delete, DocumentChecked, Download, EditPen, Plus, Upload } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import { useModels, type VehicleModel } from './hooks/useModels'

const {
  DEFAULT_DETAIL_CONFIG,
  BRANDS,
  EXPORT_COUNTRIES,
  ENERGY_TYPES,
  tableRef,
  formRef,
  models,
  expandedIds,
  editingId,
  savingEdit,
  dialogVisible,
  submitting,
  form,
  editForm,
  formRules,
  brandFilter,
  seriesFilter,
  variantFilter,
  exportFilter,
  energyFilter,
  currentPage,
  pageSize,
  seriesOptions,
  variantOptions,
  filteredModels,
  paginatedModels,
  pageRangeStart,
  pageRangeEnd,
  buildMockModels,
  isEditing,
  rowClassName,
  toggleDetail,
  collapseRow,
  toggleStatus,
  createEmptyForm,
  createEmptyEditForm,
  parsePriceToWan,
  formatPriceFromWan,
  extractFactoryParams,
  buildDetailConfigFromEdit,
  fillEditForm,
  startEdit,
  cancelEdit,
  formatLaunchDate,
  parseLaunchDate,
  formatGuidePrice,
  inferEnergyType,
  parseDetailConfig,
  resetForm,
  openCreate,
  handleCreate,
  buildModelFromForm,
  nextModelId,
  handleUpload,
  importing,
  handleDownloadTemplate,
  saveEdit,
  submitForm,
  handleDelete,
} = useModels()

</script>

<style lang="scss" scoped>

@import '../../styles/page.css';

.btn-icon {
  margin-right: 4px;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
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
}

.filter-select {
  width: 140px;

  &--variant {
    flex: 1;
    min-width: 200px;
    max-width: 360px;
  }

  :deep(.el-select__wrapper) {
    background: var(--bg-elevated);
    box-shadow: 0 0 0 1px var(--border-color) inset;
    border-radius: 8px;
    font-size: 12px;
    min-height: 32px;
  }
}

.table-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  overflow: hidden;
}

.models-table {
  :deep(.el-table__header-wrapper th.el-table__cell) {
    background: #f8fafc !important;
    color: #6b7280;
    font-size: 11px;
    font-weight: 600;
    letter-spacing: 0.04em;
    text-transform: uppercase;
    border-bottom: 1px solid #e5e7eb;
    padding: 16px;
  }

  :deep(.el-table__body td.el-table__cell) {
    padding: 16px;
    vertical-align: top;
  }

  :deep(.el-table__body tr:hover > td.el-table__cell) {
    background: rgba(99, 102, 241, 0.04) !important;
  }

  :deep(.el-table__inner-wrapper::before) {
    display: none;
  }

  :deep(.expand-column) {
    padding: 0 !important;
    border: none !important;

    .cell {
      display: none;
    }
  }

  :deep(.el-table__expand-icon) {
    display: none;
  }

  :deep(.models-table__row--editing > td.el-table__cell) {
    background: rgba(99, 102, 241, 0.06) !important;
  }

  :deep(.el-table__expanded-cell) {
    padding: 0 !important;
  }
}

.cell-edit-stack {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.cell-edit-date {
  width: 100%;
}

.cell-price-edit {
  display: flex;
  align-items: center;
  gap: 4px;

  .el-input {
    flex: 1;
  }

  &__unit {
    flex-shrink: 0;
    font-size: 13px;
    font-weight: 700;
    color: #e11d48;
  }
}

.cell-export-select {
  width: 100%;
}

.cell-lineage {
  &__path {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 4px;
    font-size: 12px;
    font-weight: 600;
    color: var(--color-primary);
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }

  &__sep {
    color: #d1d5db;
  }

  &__name {
    font-size: 14px;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.35;
  }

  &__id {
    margin-top: 6px;
    font-family: ui-monospace, monospace;
    font-size: 10px;
    color: var(--text-muted);
  }
}

.cell-params {
  margin: 0;
  font-size: 12px;

  &__row {
    display: flex;
    gap: 8px;
    margin-bottom: 4px;

    dt {
      flex-shrink: 0;
      width: 64px;
      color: var(--text-muted);
    }

    dd {
      margin: 0;
      font-weight: 500;
      color: var(--text-primary);
    }
  }
}

.cell-price {
  font-size: 14px;
  font-weight: 700;
  color: #e11d48;
}

.cell-export {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}

.export-tag {
  font-size: 10px;
  font-weight: 500;
  color: #334155;
  background: #f1f5f9;
  border-color: #e2e8f0;
}

.cell-status {
  display: flex;
  flex-direction: column;
  gap: 4px;

  &__date {
    font-family: ui-monospace, monospace;
    font-size: 10px;
    color: var(--text-muted);
  }
}

.status-tag {
  width: fit-content;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.table-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;

  &__icons {
    display: flex;
    gap: 4px;
  }
}

.detail-btn {
  height: auto;
  padding: 4px 8px;
  font-size: 10px;
  font-weight: 700;
  color: var(--text-secondary);
  background: #f9fafb;
  border: 1px solid #e5e7eb;

  .el-icon {
    margin-right: 2px;
    font-size: 12px;
    transition: transform 0.2s;
  }

  &__icon--open {
    transform: rotate(180deg);
  }

  &:hover {
    color: var(--color-primary);
    background: rgba(99, 102, 241, 0.06);
    border-color: rgba(99, 102, 241, 0.2);
  }
}

.detail-panel {
  padding: 8px 16px 16px 48px;

  &--editing {
    padding: 0 16px 16px 24px;
    margin-left: 4px;
    border-left: 3px solid #6366f1;
  }

  &__title {
    margin: 0 0 12px;
    font-size: 13px;
    font-weight: 600;
    color: var(--text-primary);
  }

  &__extra {
    margin-top: 12px;
  }
}

.factory-form {
  padding: 16px;
  background: rgba(99, 102, 241, 0.04);
  border: 1px solid rgba(99, 102, 241, 0.15);
  border-radius: 12px;

  &__title {
    display: flex;
    align-items: center;
    gap: 6px;
    margin: 0 0 16px;
    font-size: 13px;
    font-weight: 700;
    color: #4338ca;

    .el-icon {
      font-size: 14px;
    }
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 0 16px;

    :deep(.el-form-item__label) {
      font-size: 11px;
      font-weight: 600;
      color: #6b7280;
      padding-bottom: 2px;
    }

    :deep(.el-input),
    :deep(.el-select) {
      width: 100%;
    }

    :deep(.el-input.is-disabled .el-input__wrapper) {
      background: #f3f4f6;
    }
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid rgba(99, 102, 241, 0.12);
  }
}

.detail-btn {
  &--active {
    color: var(--color-primary);
    background: rgba(99, 102, 241, 0.06);
    border-color: rgba(99, 102, 241, 0.2);
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

@media (max-width: 749px) {
  .filter-select {
    width: 100%;

    &--variant {
      max-width: none;
    }
  }

  .factory-form__grid {
    grid-template-columns: 1fr;
  }
}

.model-dialog {
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

.model-form {
  :deep(.el-form-item__label) {
    font-size: 12px;
    font-weight: 600;
    color: #6b7280;
    padding-bottom: 4px;
  }

  :deep(.el-input),
  :deep(.el-select),
  :deep(.el-textarea),
  :deep(.el-date-editor) {
    width: 100%;
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 0 16px;
  }

  &__full {
    grid-column: 1 / -1;
  }

  &__half {
    grid-column: span 2;
  }

  &__date {
    width: 100%;
  }

  &__checkbox {
    margin-bottom: 8px;

    :deep(.el-form-item__content) {
      line-height: 1;
    }

    :deep(.el-checkbox__label) {
      font-size: 13px;
      font-weight: 500;
      color: var(--text-primary);
    }
  }
}

.json-textarea :deep(.el-textarea__inner) {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  line-height: 1.6;
}

@media (max-width: 749px) {
  .model-form__grid {
    grid-template-columns: 1fr;
  }

  .model-form__half {
    grid-column: 1 / -1;
  }
}

</style>
