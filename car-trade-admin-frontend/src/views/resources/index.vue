<template>

  <div class="page resources-page">
    <PageHeader
      title="资源管理"
      subtitle="管理应用前端所依赖的核心格式文本内容及运营资源位"
    />

    <el-tabs v-model="activeTab" class="resource-tabs">
      <el-tab-pane label="Banner 广告位" name="banner" />
      <el-tab-pane label="弹窗广告位" name="popup" />
      <el-tab-pane label="5D找车在线交易规范" name="trade-rules" />
      <el-tab-pane label="5D找车用户协议" name="user-agreement" />
      <el-tab-pane label="5D找车隐私条款" name="privacy" />
      <el-tab-pane label="在线交易标准合同" name="contract" />
    </el-tabs>

    <!-- Banner 广告位 -->
    <section v-if="activeTab === 'banner'" class="tab-content">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon class="section-title__icon"><Picture /></el-icon>
          轮播活动配置 (Banners)
        </h3>
        <el-button type="primary" @click="openBannerDialog('create')">
          <el-icon class="btn-icon"><Plus /></el-icon>
          添加Banner
        </el-button>
      </div>

      <el-row :gutter="16">
        <el-col
          v-for="(banner, index) in banners"
          :key="banner.id"
          :xs="24"
          :sm="12"
          :lg="8"
        >
          <el-card class="banner-card" shadow="never">
            <div
              class="banner-card__cover"
              title="点击图片直接进入编辑"
              @click="openBannerDialog('edit', banner)"
            >
              <el-image :src="banner.image" :alt="banner.title" fit="cover" class="banner-card__img" />
              <el-tag
                class="banner-card__status"
                :type="banner.active ? 'success' : 'info'"
                size="small"
                effect="dark"
                round
              >
                {{ banner.active ? '生效中' : '未生效' }}
              </el-tag>
              <div class="banner-card__overlay">
                <span class="banner-card__overlay-text">
                  <el-icon><EditPen /></el-icon>
                  点击图片编辑项目
                </span>
              </div>
            </div>
            <div class="banner-card__body">
              <h5 class="banner-card__title">{{ banner.title }}</h5>
              <p class="banner-card__link">跳转: {{ banner.link }}</p>
              <div class="banner-card__actions">
                <el-button
                  size="small"
                  :disabled="index === 0"
                  title="向前移动/排序"
                  @click="moveBanner(index, -1)"
                >
                  <el-icon><ArrowLeft /></el-icon>
                </el-button>
                <el-button
                  size="small"
                  :disabled="index === banners.length - 1"
                  title="向后移动/排序"
                  @click="moveBanner(index, 1)"
                >
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
                <el-button size="small" type="primary" plain title="编辑" @click="openBannerDialog('edit', banner)">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-button size="small" type="danger" plain title="删除" @click="handleDeleteBanner(banner)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <!-- 弹窗广告位 -->
    <section v-else-if="activeTab === 'popup'" class="tab-content">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon class="section-title__icon"><Bell /></el-icon>
          弹窗广告配置 (Popups)
        </h3>
        <el-button type="primary" @click="openPopupDialog('create')">
          <el-icon class="btn-icon"><Plus /></el-icon>
          添加弹窗
        </el-button>
      </div>

      <el-empty v-if="popups.length === 0" description="暂无弹窗广告，点击上方按钮添加" />

      <el-row v-else :gutter="16">
        <el-col
          v-for="(popup, index) in popups"
          :key="popup.id"
          :xs="24"
          :sm="12"
          :lg="8"
        >
          <el-card class="banner-card" shadow="never">
            <div
              class="banner-card__cover"
              title="点击图片直接进入编辑"
              @click="openPopupDialog('edit', popup)"
            >
              <el-image :src="popup.image" :alt="popup.title" fit="cover" class="banner-card__img" />
              <el-tag
                class="banner-card__status"
                :type="popup.active ? 'success' : 'info'"
                size="small"
                effect="dark"
                round
              >
                {{ popup.active ? '生效中' : '未生效' }}
              </el-tag>
              <div class="banner-card__overlay">
                <span class="banner-card__overlay-text">
                  <el-icon><EditPen /></el-icon>
                  点击图片编辑项目
                </span>
              </div>
            </div>
            <div class="banner-card__body">
              <h5 class="banner-card__title">{{ popup.title }}</h5>
              <p class="banner-card__link">跳转: {{ popup.link }}</p>
              <div class="banner-card__actions">
                <el-button
                  size="small"
                  :disabled="index === 0"
                  title="向前移动/排序"
                  @click="movePopup(index, -1)"
                >
                  <el-icon><ArrowLeft /></el-icon>
                </el-button>
                <el-button
                  size="small"
                  :disabled="index === popups.length - 1"
                  title="向后移动/排序"
                  @click="movePopup(index, 1)"
                >
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
                <el-button size="small" type="primary" plain title="编辑" @click="openPopupDialog('edit', popup)">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-button size="small" type="danger" plain title="删除" @click="handleDeletePopup(popup)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <!-- 文本内容类 Tab -->
    <section v-else class="tab-content">
      <el-card class="text-panel" shadow="never">
        <div class="section-header section-header--inner">
          <h3 class="section-title">
            <el-icon class="section-title__icon"><Document /></el-icon>
            {{ currentTextTab?.label }}
          </h3>
          <el-button type="primary" :loading="textSaving" @click="saveTextContent">
            <el-icon class="btn-icon"><DocumentChecked /></el-icon>
            保存内容
          </el-button>
        </div>
        <el-input
          v-model="currentTextContent"
          type="textarea"
          :rows="18"
          placeholder="在此编辑文档内容..."
          class="text-editor"
        />
      </el-card>
    </section>

    <!-- Banner 编辑弹窗 -->
    <el-dialog
      v-model="bannerDialogVisible"
      width="560px"
      class="resource-dialog"
      align-center
      destroy-on-close
      @closed="resetBannerForm"
    >
      <template #header>
        <h3 class="resource-dialog__title">
          {{ bannerDialogMode === 'create' ? '添加 Banner' : '编辑 Banner' }}
        </h3>
      </template>

      <el-form
        ref="bannerFormRef"
        :model="bannerForm"
        :rules="bannerFormRules"
        label-position="top"
        class="resource-form"
      >
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="bannerForm.title" placeholder="如：5D好车春季大促首发" />
        </el-form-item>
        <el-form-item label="跳转路径" prop="link">
          <el-input v-model="bannerForm.link" placeholder="如：/promotions/spring" class="mono-input" />
        </el-form-item>
        <el-form-item label="Banner 图片" prop="image">
          <div class="image-input-row">
            <el-input v-model="bannerForm.image" placeholder="图片 URL" />
            <el-upload
              :show-file-list="false"
              accept="image/*"
              :auto-upload="false"
              @change="handleBannerImageUpload"
            >
              <el-button title="上传图片">
                <el-icon><UploadFilled /></el-icon>
              </el-button>
            </el-upload>
          </div>
          <el-image
            v-if="bannerForm.image"
            :src="bannerForm.image"
            fit="cover"
            class="image-preview"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="bannerForm.active"
            active-text="生效中"
            inactive-text="未生效"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="resource-dialog__footer">
          <el-button @click="bannerDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="bannerSubmitting" @click="submitBannerForm">
            确认保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 弹窗广告编辑弹窗 -->
    <el-dialog
      v-model="popupDialogVisible"
      width="560px"
      class="resource-dialog"
      align-center
      destroy-on-close
      @closed="resetPopupForm"
    >
      <template #header>
        <h3 class="resource-dialog__title">
          {{ popupDialogMode === 'create' ? '添加弹窗广告' : '编辑弹窗广告' }}
        </h3>
      </template>

      <el-form
        ref="popupFormRef"
        :model="popupForm"
        :rules="popupFormRules"
        label-position="top"
        class="resource-form"
      >
        <el-form-item label="弹窗标题" prop="title">
          <el-input v-model="popupForm.title" placeholder="如：新用户专享优惠" />
        </el-form-item>
        <el-form-item label="跳转路径" prop="link">
          <el-input v-model="popupForm.link" placeholder="如：/promotions/new-user" class="mono-input" />
        </el-form-item>
        <el-form-item label="弹窗图片" prop="image">
          <div class="image-input-row">
            <el-input v-model="popupForm.image" placeholder="图片 URL" />
            <el-upload
              :show-file-list="false"
              accept="image/*"
              :auto-upload="false"
              @change="handlePopupImageUpload"
            >
              <el-button title="上传图片">
                <el-icon><UploadFilled /></el-icon>
              </el-button>
            </el-upload>
          </div>
          <el-image
            v-if="popupForm.image"
            :src="popupForm.image"
            fit="cover"
            class="image-preview"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="popupForm.active"
            active-text="生效中"
            inactive-text="未生效"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="resource-dialog__footer">
          <el-button @click="popupDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="popupSubmitting" @click="submitPopupForm">
            确认保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>

</template>

<script setup lang="ts">
/**
 * 资源管理页面
 * 通过 Tab 切换管理 Banner 轮播、弹窗广告及四类协议文本（交易规范、用户协议、隐私条款、标准合同）；
 * 广告位支持增删改、排序与图片上传，协议文本支持在线编辑与保存。
 */

import { ArrowLeft, ArrowRight, Bell, Delete, Document, DocumentChecked, EditPen, Picture, Plus, UploadFilled } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import { useResources } from './hooks/useResources'

const {
  activeTab,
  currentTextTab,
  currentTextContent,
  banners,
  popups,
  textContents,
  textSaving,
  bannerDialogVisible,
  bannerDialogMode,
  bannerEditingId,
  bannerSubmitting,
  bannerFormRef,
  bannerForm,
  bannerFormRules,
  popupDialogVisible,
  popupDialogMode,
  popupEditingId,
  popupSubmitting,
  popupFormRef,
  popupForm,
  popupFormRules,
  createEmptyAdForm,
  openBannerDialog,
  resetBannerForm,
  moveBanner,
  handleBannerImageUpload,
  openPopupDialog,
  resetPopupForm,
  movePopup,
  handlePopupImageUpload,
  submitBannerForm,
  handleDeleteBanner,
  submitPopupForm,
  handleDeletePopup,
  saveTextContent,
  textTabs,
} = useResources()

</script>

<style lang="scss" scoped>

@import '../../styles/page.css';

.resources-page {
  .btn-icon {
    margin-right: 4px;
  }
}

.resource-tabs {
  margin-bottom: 0;

  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }

  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background-color: var(--border-color);
  }

  :deep(.el-tabs__item) {
    font-size: 14px;
    font-weight: 600;
    padding: 10px 16px;
    height: auto;
  }
}

.tab-content {
  margin-top: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 40px;
  margin-bottom: 16px;
  gap: 12px;

  &--inner {
    margin-bottom: 20px;
  }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;

  &__icon {
    font-size: 20px;
    color: var(--el-color-primary);
  }
}

.banner-card {
  margin-bottom: 16px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  overflow: hidden;

  :deep(.el-card__body) {
    padding: 0;
  }

  &__cover {
    position: relative;
    height: 128px;
    background: #f3f4f6;
    overflow: hidden;
    cursor: pointer;

    &:hover .banner-card__overlay {
      opacity: 1;
    }

    &:hover .banner-card__img {
      transform: scale(1.05);
    }
  }

  &__img {
    width: 100%;
    height: 128px;
    display: block;
    transition: transform 0.5s ease;
  }

  &__status {
    position: absolute;
    top: 8px;
    right: 8px;
    z-index: 2;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.15);
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: 1;
  }

  &__overlay-text {
    display: flex;
    align-items: center;
    gap: 6px;
    background: rgba(255, 255, 255, 0.95);
    color: #1e1b4b;
    font-size: 12px;
    font-weight: 700;
    padding: 6px 12px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  &__body {
    padding: 16px;
    display: flex;
    flex-direction: column;
    flex: 1;
  }

  &__title {
    font-size: 14px;
    font-weight: 700;
    color: var(--text-primary);
    margin: 0 0 4px;
  }

  &__link {
    font-size: 12px;
    color: var(--text-secondary);
    font-family: ui-monospace, monospace;
    word-break: break-all;
    margin: 0 0 16px;
  }

  &__actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: auto;
  }
}

.text-panel {
  border: 1px solid var(--border-color);
  border-radius: 12px;

  :deep(.el-card__body) {
    padding: 20px 24px;
  }
}

.text-editor {
  :deep(.el-textarea__inner) {
    font-family: ui-monospace, monospace;
    font-size: 13px;
    line-height: 1.6;
  }
}

.image-input-row {
  display: flex;
  gap: 8px;
  width: 100%;

  .el-input {
    flex: 1;
  }
}

.image-preview {
  margin-top: 12px;
  width: 100%;
  height: 120px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.mono-input :deep(.el-input__inner) {
  font-family: ui-monospace, monospace;
}

.resource-dialog {
  :deep(.el-dialog) {
    border-radius: 12px;
  }

  :deep(.el-dialog__header) {
    padding: 20px 24px 0;
    margin-right: 0;
  }

  :deep(.el-dialog__body) {
    padding: 16px 24px;
  }

  :deep(.el-dialog__footer) {
    padding: 12px 24px 20px;
  }

  &__title {
    font-size: 16px;
    font-weight: 700;
    margin: 0;
    color: var(--text-primary);
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}

.resource-form {
  .el-form-item {
    margin-bottom: 18px;
  }
}

</style>
