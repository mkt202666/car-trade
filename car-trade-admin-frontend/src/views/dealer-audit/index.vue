<template>
  <div class="audit-page space-y-6">
    <PageHeader
      title="车行注册审核"
      subtitle="审查和校验待入驻车行的申报注册材料，进行营业资质与经营合规评估。"
    />

    <!-- 筛选栏 -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <div class="filter-bar__left">
          <el-input
            v-model="keyword"
            placeholder="搜索商户/个人姓名、手机、营业码"
            :prefix-icon="Search"
            clearable
            class="filter-input"
          />
          <el-select v-model="statusFilter" placeholder="审核状态" class="filter-select">
            <el-option label="所有状态" value="all" />
            <el-option label="待处理" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </div>
        <span class="filter-hint">AI助手会对证照长度、格式、三要素进行自动化预检</span>
      </div>
    </el-card>

    <!-- 主内容区 -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
      <!-- 左侧列表 -->
      <el-card shadow="never" class="lg:col-span-7">
        <el-table
          :data="filteredAudits"
          highlight-current-row
          :current-row-key="selectedId"
          row-key="id"
          class="audit-table"
          @current-change="handleCurrentChange"
        >
          <el-table-column label="主体申报名称" min-width="180">
            <template #default="{ row }">
              <div class="cell-name">
                <span class="cell-name__id">{{ row.id }}</span>
                <span class="cell-name__text">{{ row.name }}</span>
                <span class="cell-name__date">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(row.submittedAt) }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="类型与联系" min-width="140">
            <template #default="{ row }">
              <div class="cell-contact">
                <span class="cell-contact__type">{{ row.type }}</span>
                <span class="cell-contact__phone">{{ row.contact }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="申报证号" prop="certNumber" min-width="120">
            <template #default="{ row }">
              <span class="cell-mono">{{ row.certNumber }}</span>
            </template>
          </el-table-column>
          <el-table-column label="审核状态" width="100">
            <template #default="{ row }">
              <el-tag
                :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'"
                size="small"
                effect="light"
                round
              >
                {{ statusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click.stop="selectAudit(row.id)">
                检视
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 右侧详情 -->
      <div v-if="selectedAudit" class="lg:col-span-5 space-y-4">
        <el-card shadow="never">
          <template #header>
            <div class="detail-header">
              <span class="detail-header__id">{{ selectedAudit.id }} {{ auditCodeSuffix(selectedAudit.status) }}</span>
              <h4 class="detail-header__name">{{ selectedAudit.name }}</h4>
              <div class="detail-header__tags">
                <el-tag size="small" effect="plain">申请资质: {{ selectedAudit.type }}</el-tag>
                <el-tag size="small" effect="plain" class="font-mono">提交证照: {{ selectedAudit.certNumber }}</el-tag>
                <el-tag size="small" effect="plain" class="font-mono">联系电话: {{ selectedAudit.contact }}</el-tag>
              </div>
            </div>
          </template>

          <!-- 申请人信息 -->
          <div v-if="isNewDealerApplication" class="applicant-info">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="申请人姓名">{{ selectedAudit.applicant!.name }}</el-descriptions-item>
              <el-descriptions-item label="身份证号" class="font-mono">{{ selectedAudit.applicant!.idNumber }}</el-descriptions-item>
              <el-descriptions-item label="身份证图片" :span="2">
                <el-image
                  :src="selectedAudit.applicant!.idCardImage"
                  fit="cover"
                  class="doc-image"
                  :preview-src-list="[selectedAudit.applicant!.idCardImage]"
                />
              </el-descriptions-item>
              <el-descriptions-item v-if="selectedAudit.creditCode" label="营业执照信用代码" :span="2" class="font-mono">
                {{ selectedAudit.creditCode }}
              </el-descriptions-item>
              <el-descriptions-item v-if="selectedAudit.address" label="驻点地址" :span="2">
                {{ selectedAudit.address }}
              </el-descriptions-item>
              <el-descriptions-item v-if="selectedAudit.licenseImage" label="营业执照附件" :span="2">
                <el-image
                  :src="selectedAudit.licenseImage"
                  fit="cover"
                  class="doc-image"
                  :preview-src-list="[selectedAudit.licenseImage]"
                />
              </el-descriptions-item>
              <el-descriptions-item v-if="selectedAudit.storefrontImage" label="门店照片" :span="2">
                <el-image
                  :src="selectedAudit.storefrontImage"
                  fit="cover"
                  class="doc-image doc-image--large"
                  :preview-src-list="[selectedAudit.storefrontImage]"
                />
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <!-- 审核决议 -->
        <el-card shadow="never">
          <template #header>
            <span class="text-xs text-gray-400">当前人工审核决议</span>
          </template>
          <div class="decision-box">
            <div class="decision-box__status">
              <el-icon v-if="selectedAudit.status === 'approved'" class="text-green-500 text-xl"><CircleCheck /></el-icon>
              <el-icon v-else-if="selectedAudit.status === 'rejected'" class="text-red-500 text-xl"><CircleClose /></el-icon>
              <el-icon v-else class="text-yellow-500 text-xl"><Clock /></el-icon>
              <span class="font-semibold">{{ decisionLabel(selectedAudit.status) }}</span>
            </div>
            <div v-if="selectedAudit.status === 'pending'" class="decision-box__actions">
              <el-button type="danger" plain @click="openRejectDialog">驳回拒绝</el-button>
              <el-button type="success" @click="approveAudit">准予通过</el-button>
            </div>
          </div>
          <div v-if="selectedAudit.auditNote" class="audit-note">
            <span class="font-bold text-gray-700 block mb-0.5">审核依据说明:</span>
            {{ selectedAudit.auditNote }}
          </div>
        </el-card>

        <!-- AI 校验 -->
        <el-card v-if="selectedAudit.aiCheck" shadow="never">
          <template #header>
            <div class="ai-header">
              <el-icon class="text-indigo-500"><MagicStick /></el-icon>
              <span class="font-bold text-indigo-900 text-xs">Gemini 3.5 智能辅助校验</span>
            </div>
          </template>
          <div class="ai-panel">
            <div class="ai-grid">
              <div class="ai-stat">
                <span class="ai-stat__label">申报证格式</span>
                <span class="ai-stat__value">{{ selectedAudit.aiCheck.format }}</span>
              </div>
              <div class="ai-stat">
                <span class="ai-stat__label">风险归级</span>
                <span class="ai-stat__value">{{ selectedAudit.aiCheck.risk }}</span>
              </div>
              <div class="ai-stat">
                <span class="ai-stat__label">推荐决断</span>
                <span class="ai-stat__value">{{ selectedAudit.aiCheck.recommendation }}</span>
              </div>
            </div>
            <div class="ai-comment">
              <span class="font-bold text-gray-700 block mb-1">AI 资质评估意见:</span>
              {{ selectedAudit.aiCheck.comment }}
            </div>
          </div>
        </el-card>
      </div>

      <!-- 未选中提示 -->
      <div v-else class="lg:col-span-5">
        <el-card shadow="never" class="empty-card">
          <el-empty description="请选择左侧审核记录查看详情" />
        </el-card>
      </div>
    </div>

    <!-- 驳回弹窗 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="驳回注册申请资质"
      width="520px"
      align-center
      destroy-on-close
      @closed="resetRejectForm"
    >
      <p class="text-sm text-gray-500 mb-4">请说明不予通过的主要合规原因</p>
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="5"
        placeholder="说明具体的资质附件不足，或照片遮挡、营业执照超期等"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :disabled="!rejectReason.trim()" @click="confirmReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import PageHeader from '../../components/PageHeader.vue'
import { CircleCheck, CircleClose, Clock, MagicStick, Search } from '@element-plus/icons-vue'
import { useDealerAudit } from './hooks/useDealerAudit'

const {
  keyword,
  statusFilter,
  selectedId,
  rejectDialogVisible,
  rejectReason,
  filteredAudits,
  selectedAudit,
  isNewDealerApplication,
  selectAudit,
  formatDate,
  statusLabel,
  auditCodeSuffix,
  decisionLabel,
  openRejectDialog,
  resetRejectForm,
  confirmReject,
  approveAudit,
} = useDealerAudit()

function handleCurrentChange(row: { id: string } | null) {
  if (row) selectAudit(row.id)
}
</script>

<style lang="scss" scoped>
.audit-page {
  padding: 16px 24px 40px;
  max-width: 1400px;
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
  }
}

.filter-input {
  max-width: 280px;
}

.filter-select {
  width: 160px;
}

.filter-hint {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
}

.audit-table {
  :deep(.el-table__row) {
    cursor: pointer;
  }
}

.cell-name {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__id {
    font-size: 10px;
    color: #9ca3af;
    font-family: monospace;
  }

  &__text {
    font-weight: 700;
    color: #111827;
  }

  &__date {
    font-size: 11px;
    color: #9ca3af;
    display: flex;
    align-items: center;
    gap: 2px;
  }
}

.cell-contact {
  display: flex;
  flex-direction: column;
  gap: 2px;

  &__type {
    font-weight: 500;
    color: #374151;
  }

  &__phone {
    font-size: 11px;
    color: #9ca3af;
    font-family: monospace;
  }
}

.cell-mono {
  font-family: monospace;
  font-size: 12px;
  color: #6b7280;
}

.detail-header {
  &__id {
    font-size: 10px;
    color: #9ca3af;
    font-family: monospace;
    font-weight: 700;
    text-transform: uppercase;
  }

  &__name {
    margin: 4px 0 8px;
    font-size: 14px;
    font-weight: 800;
    color: #111827;
  }

  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }
}

.applicant-info {
  margin-top: 12px;
}

.doc-image {
  width: 80px;
  height: 60px;
  border-radius: 4px;

  &--large {
    width: 120px;
    height: 120px;
  }
}

.decision-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fafafa;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #f4f4f5;

  &__status {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
  }

  &__actions {
    display: flex;
    gap: 8px;
  }
}

.audit-note {
  margin-top: 12px;
  font-size: 12px;
  color: #6b7280;
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  line-height: 1.6;
}

.ai-header {
  display: flex;
  align-items: center;
  gap: 6px;
}

.ai-panel {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid rgba(226, 232, 240, 0.5);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ai-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  text-align: center;
  font-size: 10px;
  font-family: ui-monospace, monospace;
}

.ai-stat {
  background: #fff;
  padding: 8px;
  border: 1px solid #f1f5f9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;

  &__label {
    color: #9ca3af;
    display: block;
    margin-bottom: 2px;
  }

  &__value {
    font-weight: 700;
    color: #059669;
  }
}

.ai-comment {
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid rgba(226, 232, 240, 0.4);
  font-size: 12px;
  color: #4b5563;
  line-height: 1.6;
}

.empty-card {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 320px;
}

@media (max-width: 749px) {
  .audit-page {
    padding: 16px 14px 32px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-hint {
    text-align: center;
  }
}
</style>
