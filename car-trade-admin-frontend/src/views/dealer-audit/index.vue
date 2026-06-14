<template>

  <div class="audit-page space-y-6">
    <PageHeader
      title="车行注册审核"
      subtitle="审查和校验待入驻车行的申报注册材料，进行营业资质与经营合规评估。"
    />

    <div
      class="flex flex-col md:flex-row gap-4 items-center justify-between bg-white p-4 rounded-2xl border border-gray-100 shadow-sm"
    >
      <div class="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
        <div class="relative flex-1 sm:w-64">
          <el-icon class="search-icon"><Search /></el-icon>
          <input
            v-model="keyword"
            type="text"
            placeholder="搜索商户/个人姓名、手机、营业码"
            class="search-input"
          />
        </div>
        <div class="filter-select">
          <el-icon class="filter-icon"><Filter /></el-icon>
          <select v-model="statusFilter" class="status-select">
            <option value="all">所有的审核状态</option>
            <option value="pending">待处理审批</option>
            <option value="approved">审核已通过</option>
            <option value="rejected">驳回被拒绝</option>
          </select>
        </div>
      </div>
      <span class="hint-text">在提报资质过程中，AI助手会对证照长度、格式、三要素进行自动化预检。</span>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden lg:col-span-7">
        <div class="overflow-x-auto">
          <table class="w-full text-left text-xs border-collapse">
            <thead>
              <tr class="bg-gray-50/10 text-gray-400 font-semibold border-b border-gray-100">
                <th class="p-4">主体申报名称</th>
                <th class="p-4">类型与联系</th>
                <th class="p-4">申报证号</th>
                <th class="p-4">审核状态</th>
                <th class="p-4 text-right">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 text-gray-700">
              <tr
                v-for="item in filteredAudits"
                :key="item.id"
                class="cursor-pointer transition-colors"
                :class="selectedId === item.id ? 'bg-indigo-50/30' : 'hover:bg-gray-50/20'"
                @click="selectAudit(item.id)"
              >
                <td class="p-4">
                  <div class="flex flex-col">
                    <span class="text-[9px] text-gray-400 font-mono">{{ item.id }}</span>
                    <span class="font-bold text-gray-900 mt-0.5">{{ item.name }}</span>
                    <span class="text-gray-400 text-[10px] flex items-center gap-1 mt-1">
                      <el-icon class="clock-icon"><Clock /></el-icon>
                      {{ formatDate(item.submittedAt) }}
                    </span>
                  </div>
                </td>
                <td class="p-4 font-medium font-sans">
                  <div class="flex flex-col gap-0.5">
                    <span class="text-gray-800">{{ item.type }}</span>
                    <span class="text-gray-400 font-mono text-[11px] font-normal">{{ item.contact }}</span>
                  </div>
                </td>
                <td class="p-4 font-mono text-gray-500 font-medium">{{ item.certNumber }}</td>
                <td class="p-4">
                  <span class="status-badge" :class="statusClass(item.status)">
                    {{ statusLabel(item.status) }}
                  </span>
                </td>
                <td class="p-4 text-right">
                  <button
                    type="button"
                    class="view-btn"
                    @click.stop="selectAudit(item.id)"
                  >
                    检视
                  </button>
                </td>
              </tr>
              <tr v-if="filteredAudits.length === 0">
                <td colspan="5" class="p-8 text-center text-gray-400">暂无匹配的审核记录</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div
        v-if="selectedAudit"
        class="lg:col-span-5 bg-white rounded-2xl border border-gray-100 p-5 shadow-sm space-y-5"
      >
        <div>
          <span class="text-[10px] text-gray-400 font-bold tracking-wider font-mono uppercase">
            {{ selectedAudit.id }} {{ auditCodeSuffix(selectedAudit.status) }}
          </span>
          <h4 class="font-extrabold text-gray-900 text-[14px] mt-0.5">{{ selectedAudit.name }}</h4>
          <div class="flex flex-wrap gap-2 mt-2">
            <span class="meta-tag">申请资质: {{ selectedAudit.type }}</span>
            <span class="meta-tag font-mono">提交证照: {{ selectedAudit.certNumber }}</span>
            <span class="meta-tag font-mono">联系电话: {{ selectedAudit.contact }}</span>
          </div>

          <div v-if="isNewDealerApplication" class="detail-box mt-4">
            <div class="detail-row">
              <span class="detail-label">申请人姓名</span>
              <span class="detail-value font-bold">{{ selectedAudit.applicant!.name }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">申请人身份证号</span>
              <span class="detail-value font-mono">{{ selectedAudit.applicant!.idNumber }}</span>
            </div>
            <div class="detail-row detail-row--start">
              <span class="detail-label">申请人身份证图片</span>
              <button type="button" class="image-btn" @click="previewImage(selectedAudit.applicant!.idCardImage)">
                <img :src="selectedAudit.applicant!.idCardImage" alt="ID Card" class="w-16 h-12 object-cover" />
                <div class="image-btn__overlay"><span>查看</span></div>
              </button>
            </div>
            <div v-if="selectedAudit.creditCode" class="detail-row">
              <span class="detail-label">营业执照信用代码</span>
              <span class="detail-value font-mono">{{ selectedAudit.creditCode }}</span>
            </div>
            <div v-if="selectedAudit.address" class="detail-row detail-row--start">
              <span class="detail-label mt-0.5">驻点地址</span>
              <span class="detail-value text-right max-w-[180px]">{{ selectedAudit.address }}</span>
            </div>
            <div v-if="selectedAudit.licenseImage" class="detail-row detail-row--start">
              <span class="detail-label">营业执照附件</span>
              <button type="button" class="image-btn" @click="previewImage(selectedAudit.licenseImage)">
                <img :src="selectedAudit.licenseImage" alt="License" class="w-16 h-12 object-cover" />
                <div class="image-btn__overlay"><span>查看</span></div>
              </button>
            </div>
            <div v-if="selectedAudit.storefrontImage" class="detail-row detail-row--start">
              <span class="detail-label">车行实体门店图片</span>
              <button type="button" class="image-btn" @click="previewImage(selectedAudit.storefrontImage)">
                <img :src="selectedAudit.storefrontImage" alt="Storefront" class="w-16 h-16 object-cover" />
                <div class="image-btn__overlay"><span>查看</span></div>
              </button>
            </div>
          </div>
        </div>

        <div class="decision-section">
          <span class="text-[10px] text-gray-400 font-medium font-sans">当前人工审核决议:</span>
          <div class="decision-card">
            <div class="flex items-center gap-1.5 text-xs text-slate-700 font-semibold">
              <el-icon v-if="selectedAudit.status === 'approved'" class="decision-icon decision-icon--pass">
                <CircleCheck />
              </el-icon>
              <el-icon v-else-if="selectedAudit.status === 'rejected'" class="decision-icon decision-icon--reject">
                <CircleClose />
              </el-icon>
              <el-icon v-else class="decision-icon decision-icon--pending">
                <Clock />
              </el-icon>
              <span>{{ decisionLabel(selectedAudit.status) }}</span>
            </div>
            <div v-if="selectedAudit.status === 'pending'" class="decision-actions">
              <button type="button" class="decision-btn decision-btn--reject" @click="openRejectDialog">
                驳回拒绝
              </button>
              <button type="button" class="decision-btn decision-btn--approve" @click="approveAudit">
                准予通过
              </button>
            </div>
          </div>
          <div v-if="selectedAudit.auditNote" class="audit-note">
            <span class="font-bold text-gray-700 block mb-0.5">审核依据说明:</span>
            {{ selectedAudit.auditNote }}
          </div>
        </div>

        <div v-if="selectedAudit.aiCheck" class="space-y-3">
          <div class="ai-header">
            <div class="flex items-center gap-1.5">
              <el-icon class="ai-icon"><MagicStick /></el-icon>
              <span class="font-bold text-indigo-900 text-xs">Gemini 3.5 智能辅助校验</span>
            </div>
          </div>
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
        </div>
      </div>

      <div
        v-else
        class="lg:col-span-5 bg-white rounded-2xl border border-gray-100 p-5 shadow-sm flex items-center justify-center min-h-[320px]"
      >
        <p class="text-sm text-gray-400">请选择左侧审核记录查看详情</p>
      </div>
    </div>

    <el-image-viewer v-if="previewVisible" :url-list="[previewUrl]" @close="previewVisible = false" />

    <el-dialog
      v-model="rejectDialogVisible"
      width="520px"
      class="reject-dialog"
      :show-close="true"
      align-center
      destroy-on-close
      @closed="resetRejectForm"
    >
      <template #header>
        <div class="reject-dialog__header">
          <h2 class="reject-dialog__title">驳回注册申请资质</h2>
          <p class="reject-dialog__subtitle">请说明不予通过的主要合规原因</p>
        </div>
      </template>

      <div class="reject-form">
        <label class="reject-form__label">驳回依据与说明 (该文字将下发给商户通知栏):</label>
        <el-input
          v-model="rejectReason"
          type="textarea"
          :rows="5"
          resize="vertical"
          placeholder="说明具体的资质附件不足，或照片遮挡、营业执照超期等"
          class="reject-form__textarea"
        />
      </div>

      <template #footer>
        <div class="reject-dialog__footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" :disabled="!rejectReason.trim()" @click="confirmReject">确定驳回</el-button>
        </div>
      </template>
    </el-dialog>
  </div>

</template>

<script setup lang="ts">
/**
 * 车行注册审核页面。
 * 左侧为可搜索/筛选的审核列表，右侧展示选中单据的资质详情、
 * 人工决议操作（通过/驳回）及 AI 辅助校验结果；状态与交互由 useDealerAudit 管理。
 */
import PageHeader from '../../components/PageHeader.vue'
import { CircleCheck, CircleClose, Clock, Filter, MagicStick, Search } from '@element-plus/icons-vue'
import { useDealerAudit } from './hooks/useDealerAudit'

const {
  keyword,
  statusFilter,
  selectedId,
  previewVisible,
  previewUrl,
  rejectDialogVisible,
  rejectReason,
  filteredAudits,
  selectedAudit,
  isNewDealerApplication,
  selectAudit,
  formatDate,
  statusLabel,
  statusClass,
  auditCodeSuffix,
  decisionLabel,
  openRejectDialog,
  resetRejectForm,
  confirmReject,
  approveAudit,
  previewImage,
} = useDealerAudit()

</script>

<style lang="scss" scoped>

.audit-page {
  padding: 16px 24px 40px;
  max-width: 1400px;
  color: var(--text-primary);
}

.search-icon,
.filter-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  font-size: 14px;
}

.search-input {
  width: 100%;
  padding: 6px 16px 6px 36px;
  font-size: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #374151;
  outline: none;

  &:focus {
    border-color: #6366f1;
  }
}

.filter-select {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 8px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 12px;
}

.filter-icon {
  position: static;
  transform: none;
  font-size: 14px;
}

.status-select {
  padding: 6px 4px;
  background: transparent;
  border: none;
  color: #4b5563;
  font-weight: 500;
  outline: none;
  cursor: pointer;

  &:focus {
    color: #4f46e5;
  }
}

.hint-text {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

.clock-icon {
  font-size: 12px;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 9999px;
  font-size: 10px;
  font-weight: 700;
  border: 1px solid transparent;

  &--approved {
    background: #ecfdf5;
    color: #059669;
    border-color: #d1fae5;
  }

  &--pending {
    background: #fefce8;
    color: #ca8a04;
    border-color: #fef9c3;
  }

  &--rejected {
    background: #fff1f2;
    color: #f43f5e;
    border-color: #ffe4e6;
  }
}

.view-btn {
  padding: 4px 10px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  color: inherit;
  cursor: pointer;
  transition: all 0.15s ease;

  &:hover {
    background: #eef2ff;
    color: #4f46e5;
  }
}

.meta-tag {
  background: #f1f5f9;
  color: #334155;
  font-size: 9px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 4px;
}

.detail-box {
  padding: 12px;
  background: #f9fafb;
  border: 1px solid #f3f4f6;
  border-radius: 8px;
  font-size: 12px;
  color: #4b5563;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 8px;
  border: 1px solid #f3f4f6;
  border-radius: 4px;

  &--start {
    align-items: flex-start;
  }
}

.detail-label {
  font-weight: 500;
  color: #6b7280;
}

.detail-value {
  font-weight: 500;
  color: #1f2937;
}

.image-btn {
  position: relative;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  padding: 0;
  background: none;
  cursor: pointer;

  &__overlay {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.4);
    opacity: 0;
    transition: opacity 0.15s ease;

    span {
      font-size: 10px;
      color: #fff;
      font-weight: 700;
    }
  }

  &:hover .image-btn__overlay {
    opacity: 1;
  }
}

.decision-section {
  border-top: 1px solid #f9fafb;
  border-bottom: 1px solid #f9fafb;
  padding: 14px 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.decision-card {
  background: #fafafa;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #f4f4f5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.decision-icon {
  font-size: 20px;

  &--pass {
    color: #10b981;
  }

  &--reject {
    color: #f43f5e;
  }

  &--pending {
    color: #eab308;
  }
}

.decision-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.decision-btn {
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
  border: 1px solid transparent;

  &--reject {
    background: #fef0f0;
    border-color: #fbc4c4;
    color: #f56c6c;

    &:hover {
      background: #fde2e2;
    }
  }

  &--approve {
    background: #00965e;
    color: #fff;

    &:hover {
      background: #008052;
    }
  }
}

.reject-dialog {
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
    gap: 10px;
  }
}

.reject-form {
  &__label {
    display: block;
    margin-bottom: 10px;
    font-size: 13px;
    color: #6b7280;
    line-height: 1.5;
  }

  &__textarea :deep(.el-textarea__inner) {
    border-radius: 8px;
    font-size: 13px;
    line-height: 1.6;
    padding: 12px;
  }
}

.audit-note {
  font-size: 11px;
  color: #6b7280;
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  line-height: 1.6;
}

.ai-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(238, 242, 255, 0.5);
  padding: 10px;
  border-radius: 12px;
  border: 1px solid #e0e7ff;
}

.ai-icon {
  color: #6366f1;
  font-size: 16px;
}

.ai-panel {
  background: #f8fafc;
  font-size: 12px;
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
  justify-content: center;

  &__label {
    color: #9ca3af;
    font-family: inherit;
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
  font-size: 11px;
  color: #4b5563;
  line-height: 1.6;
}

@media (max-width: 749px) {
  .audit-page {
    padding: 16px 14px 32px;
  }

  .hint-text {
    text-align: center;
  }
}

</style>
