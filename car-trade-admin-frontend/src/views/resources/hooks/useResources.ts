/** 资源管理 composable — 对接后端 Resources API（Banners / Popups / Configs） */

import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadFile } from 'element-plus'
import {
  bannerFormRules,
  CONFIG_KEY_MAP,
  createEmptyAdForm,
  INITIAL_TEXT_CONTENTS,
  popupFormRules,
  TEXT_TABS,
} from './constants'
import { moveListItem } from './resourceUtils'
import type { AdItem, TextTabKey } from './types'
import { uploadImage } from '../../../utils/request'
import {
  createBanner as apiCreateBanner,
  createPopup as apiCreatePopup,
  deleteBanner as apiDeleteBanner,
  deletePopup as apiDeletePopup,
  getBanners,
  getConfig,
  getPopups,
  sortBanners as apiSortBanners,
  sortPopups as apiSortPopups,
  updateBanner as apiUpdateBanner,
  updateConfig,
  updatePopup as apiUpdatePopup,
} from '../../../api/resources'

export type { AdItem, TextTabKey } from './types'
export {
  bannerFormRules,
  createEmptyAdForm,
  INITIAL_TEXT_CONTENTS,
  popupFormRules,
  TEXT_TABS,
} from './constants'
export { moveListItem } from './resourceUtils'

/**
 * 将后端 Banner/Popup 响应对象映射为前端 AdItem。
 * 后端 status 字段: 'ACTIVE' → active=true，其余 → active=false。
 */
function mapToAdItem(raw: any): AdItem {
  return {
    id: String(raw.id),
    title: raw.title ?? '',
    link: raw.linkUrl ?? '',
    image: raw.imageUrl ?? '',
    active: raw.status === 'ACTIVE',
  }
}

/** 管理 Banner/弹窗广告及协议文本内容的增删改与排序 */
export function useResources() {
  /** 当前激活的 Tab name，含 banner、popup 及协议类 TextTabKey */
  const activeTab = ref('banner')

  /** 当前协议类 Tab 的配置项，非协议 Tab 时为 undefined */
  const currentTextTab = computed(() => TEXT_TABS.find((t) => t.name === activeTab.value))

  /** 各协议文本 Tab 的内容存储，key 为 TextTabKey */
  const textContents = reactive<Record<TextTabKey, string>>({ ...INITIAL_TEXT_CONTENTS })

  /** 当前协议 Tab 的文本内容双向绑定，非协议 Tab 时读写为空字符串 */
  const currentTextContent = computed({
    get: () => {
      const key = activeTab.value as TextTabKey
      return TEXT_TABS.some((t) => t.name === key) ? textContents[key] : ''
    },
    set: (value: string) => {
      const key = activeTab.value as TextTabKey
      if (TEXT_TABS.some((t) => t.name === key)) {
        textContents[key] = value
      }
    },
  })

  /** Banner 广告列表数据 */
  const banners = ref<AdItem[]>([])
  /** 弹窗广告列表数据 */
  const popups = ref<AdItem[]>([])

  /** Banner 列表加载状态 */
  const bannersLoading = ref(false)
  /** 弹窗列表加载状态 */
  const popupsLoading = ref(false)
  /** 协议文本保存中的 loading 状态 */
  const textSaving = ref(false)

  /** Banner 编辑弹窗可见性 */
  const bannerDialogVisible = ref(false)
  /** Banner 弹窗模式：新建或编辑 */
  const bannerDialogMode = ref<'create' | 'edit'>('create')
  /** 当前编辑中的 Banner ID，新建时为 null */
  const bannerEditingId = ref<string | null>(null)
  /** Banner 表单提交中的 loading 状态 */
  const bannerSubmitting = ref(false)
  /** Banner 表单实例引用，用于校验与重置 */
  const bannerFormRef = ref<FormInstance>()

  /** Banner 编辑表单数据 */
  const bannerForm = reactive(createEmptyAdForm())

  /** 弹窗广告编辑弹窗可见性 */
  const popupDialogVisible = ref(false)
  /** 弹窗广告弹窗模式：新建或编辑 */
  const popupDialogMode = ref<'create' | 'edit'>('create')
  /** 当前编辑中的弹窗 ID，新建时为 null */
  const popupEditingId = ref<string | null>(null)
  /** 弹窗表单提交中的 loading 状态 */
  const popupSubmitting = ref(false)
  /** 弹窗表单实例引用，用于校验与重置 */
  const popupFormRef = ref<FormInstance>()

  /** 弹窗广告编辑表单数据 */
  const popupForm = reactive(createEmptyAdForm())

  // ───────────────────────────────────────────
  // 数据加载
  // ───────────────────────────────────────────

  /** 从后端拉取 Banner 列表并映射为 AdItem */
  async function fetchBanners() {
    bannersLoading.value = true
    try {
      // 拦截器已解包 ApiResponse，运行时直接拿到 Banner[]
      const data = (await getBanners()) as any as any[]
      banners.value = data.map(mapToAdItem)
    } catch {
      ElMessage.error('加载 Banner 列表失败')
    } finally {
      bannersLoading.value = false
    }
  }

  /** 从后端拉取弹窗广告列表并映射为 AdItem */
  async function fetchPopups() {
    popupsLoading.value = true
    try {
      const data = (await getPopups()) as any as any[]
      popups.value = data.map(mapToAdItem)
    } catch {
      ElMessage.error('加载弹窗列表失败')
    } finally {
      popupsLoading.value = false
    }
  }

  /** 从后端拉取指定协议文本内容 */
  async function fetchConfig(tabKey: TextTabKey) {
    const configKey = CONFIG_KEY_MAP[tabKey]
    try {
      const data = (await getConfig(configKey)) as any as { content?: string }
      textContents[tabKey] = data?.content ?? ''
    } catch {
      // 配置项可能尚未创建，静默处理，保留空字符串
    }
  }

  /** 页面挂载时加载 Banner、弹窗列表及全部协议文本 */
  onMounted(() => {
    fetchBanners()
    fetchPopups()
    for (const tab of TEXT_TABS) {
      fetchConfig(tab.name)
    }
  })

  // ───────────────────────────────────────────
  // Banner CRUD
  // ───────────────────────────────────────────

  /**
   * 打开 Banner 编辑弹窗
   * @param mode - 新建或编辑模式
   * @param banner - 编辑模式下传入的目标 Banner 数据
   */
  function openBannerDialog(mode: 'create' | 'edit', banner?: AdItem) {
    bannerDialogMode.value = mode
    if (mode === 'edit' && banner) {
      bannerEditingId.value = banner.id
      Object.assign(bannerForm, {
        title: banner.title,
        link: banner.link,
        image: banner.image,
        active: banner.active,
      })
    } else {
      bannerEditingId.value = null
      Object.assign(bannerForm, createEmptyAdForm())
    }
    bannerDialogVisible.value = true
  }

  /** 重置 Banner 表单至初始状态并清除校验提示 */
  function resetBannerForm() {
    Object.assign(bannerForm, createEmptyAdForm())
    bannerEditingId.value = null
    bannerFormRef.value?.clearValidate()
  }

  /** 校验并提交 Banner 表单，调用后端新建或更新接口 */
  async function submitBannerForm() {
    const valid = await bannerFormRef.value?.validate().catch(() => false)
    if (!valid) return

    bannerSubmitting.value = true
    try {
      if (bannerDialogMode.value === 'create') {
        const result = (await apiCreateBanner({
          title: bannerForm.title,
          imageUrl: bannerForm.image,
          linkUrl: bannerForm.link,
        })) as any as any
        banners.value.push(mapToAdItem(result))
        ElMessage.success('Banner 已添加')
      } else {
        const id = Number(bannerEditingId.value)
        const result = (await apiUpdateBanner(id, {
          title: bannerForm.title,
          imageUrl: bannerForm.image,
          linkUrl: bannerForm.link,
          status: bannerForm.active ? 'ACTIVE' : 'INACTIVE',
        })) as any as any
        const idx = banners.value.findIndex((b) => b.id === bannerEditingId.value)
        if (idx !== -1) {
          banners.value[idx] = mapToAdItem(result)
        }
        ElMessage.success('Banner 已更新')
      }
      bannerDialogVisible.value = false
    } catch {
      ElMessage.error(bannerDialogMode.value === 'create' ? '添加 Banner 失败' : '更新 Banner 失败')
    } finally {
      bannerSubmitting.value = false
    }
  }

  /**
   * 调整 Banner 列表排序，乐观更新本地列表后调用后端排序 API 持久化顺序
   * @param index - 当前 Banner 索引
   * @param direction - 移动方向：-1 向前，1 向后
   */
  async function moveBanner(index: number, direction: -1 | 1) {
    const next = moveListItem(banners.value, index, direction)
    if (!next) return
    banners.value = next
    try {
      await apiSortBanners(
        banners.value.map((item, idx) => ({ id: Number(item.id), sortOrder: idx + 1 })),
      )
    } catch {
      ElMessage.error('Banner 排序保存失败')
    }
  }

  /**
   * 删除指定 Banner，需用户确认
   * @param banner - 待删除的 Banner 数据
   */
  async function handleDeleteBanner(banner: AdItem) {
    try {
      await ElMessageBox.confirm(`确定删除 Banner「${banner.title}」吗？`, '删除确认', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消',
      })
      await apiDeleteBanner(Number(banner.id))
      banners.value = banners.value.filter((b) => b.id !== banner.id)
      ElMessage.success('已删除')
    } catch (err: any) {
      // ElMessageBox 取消时 action === 'cancel'，不显示错误提示
      if (err !== 'cancel' && err?.toString() !== 'cancel') {
        ElMessage.error('删除 Banner 失败')
      }
    }
  }

  /** Banner 图片上传中状态 */
  const bannerImageUploading = ref(false)

  /**
   * 处理 Banner 图片上传：调用后端上传接口获取远程 URL 写入表单，
   * 同时生成本地预览 URL 供即时预览。
   * @param file - Element Plus 上传组件返回的文件对象
   */
  async function handleBannerImageUpload(file: UploadFile) {
    const raw = file.raw
    if (!raw) return
    // 立即显示本地预览
    const localPreview = URL.createObjectURL(raw)
    bannerForm.image = localPreview
    bannerImageUploading.value = true
    try {
      const remoteUrl = await uploadImage(raw)
      bannerForm.image = remoteUrl
    } catch {
      ElMessage.error('图片上传失败，请重试')
      // 保留本地预览，用户可重新上传
    } finally {
      bannerImageUploading.value = false
      URL.revokeObjectURL(localPreview)
    }
  }

  // ───────────────────────────────────────────
  // Popup CRUD
  // ───────────────────────────────────────────

  /**
   * 打开弹窗广告编辑弹窗
   * @param mode - 新建或编辑模式
   * @param popup - 编辑模式下传入的目标弹窗数据
   */
  function openPopupDialog(mode: 'create' | 'edit', popup?: AdItem) {
    popupDialogMode.value = mode
    if (mode === 'edit' && popup) {
      popupEditingId.value = popup.id
      Object.assign(popupForm, {
        title: popup.title,
        link: popup.link,
        image: popup.image,
        active: popup.active,
      })
    } else {
      popupEditingId.value = null
      Object.assign(popupForm, createEmptyAdForm())
    }
    popupDialogVisible.value = true
  }

  /** 重置弹窗表单至初始状态并清除校验提示 */
  function resetPopupForm() {
    Object.assign(popupForm, createEmptyAdForm())
    popupEditingId.value = null
    popupFormRef.value?.clearValidate()
  }

  /** 校验并提交弹窗表单，调用后端新建或更新接口 */
  async function submitPopupForm() {
    const valid = await popupFormRef.value?.validate().catch(() => false)
    if (!valid) return

    popupSubmitting.value = true
    try {
      if (popupDialogMode.value === 'create') {
        const result = (await apiCreatePopup({
          title: popupForm.title,
          imageUrl: popupForm.image,
          linkUrl: popupForm.link,
        })) as any as any
        popups.value.push(mapToAdItem(result))
        ElMessage.success('弹窗广告已添加')
      } else {
        const id = Number(popupEditingId.value)
        const result = (await apiUpdatePopup(id, {
          title: popupForm.title,
          imageUrl: popupForm.image,
          linkUrl: popupForm.link,
          status: popupForm.active ? 'ACTIVE' : 'INACTIVE',
        })) as any as any
        const idx = popups.value.findIndex((p) => p.id === popupEditingId.value)
        if (idx !== -1) {
          popups.value[idx] = mapToAdItem(result)
        }
        ElMessage.success('弹窗广告已更新')
      }
      popupDialogVisible.value = false
    } catch {
      ElMessage.error(popupDialogMode.value === 'create' ? '添加弹窗失败' : '更新弹窗失败')
    } finally {
      popupSubmitting.value = false
    }
  }

  /**
   * 调整弹窗广告列表排序，乐观更新本地列表后调用后端排序 API 持久化顺序
   * @param index - 当前弹窗索引
   * @param direction - 移动方向：-1 向前，1 向后
   */
  async function movePopup(index: number, direction: -1 | 1) {
    const next = moveListItem(popups.value, index, direction)
    if (!next) return
    popups.value = next
    try {
      await apiSortPopups(
        popups.value.map((item, idx) => ({ id: Number(item.id), sortOrder: idx + 1 })),
      )
    } catch {
      ElMessage.error('弹窗排序保存失败')
    }
  }

  /**
   * 删除指定弹窗广告，需用户确认
   * @param popup - 待删除的弹窗数据
   */
  async function handleDeletePopup(popup: AdItem) {
    try {
      await ElMessageBox.confirm(`确定删除弹窗「${popup.title}」吗？`, '删除确认', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消',
      })
      await apiDeletePopup(Number(popup.id))
      popups.value = popups.value.filter((p) => p.id !== popup.id)
      ElMessage.success('已删除')
    } catch (err: any) {
      if (err !== 'cancel' && err?.toString() !== 'cancel') {
        ElMessage.error('删除弹窗失败')
      }
    }
  }

  /** 弹窗图片上传中状态 */
  const popupImageUploading = ref(false)

  /**
   * 处理弹窗图片上传：调用后端上传接口获取远程 URL 写入表单，
   * 同时生成本地预览 URL 供即时预览。
   * @param file - Element Plus 上传组件返回的文件对象
   */
  async function handlePopupImageUpload(file: UploadFile) {
    const raw = file.raw
    if (!raw) return
    // 立即显示本地预览
    const localPreview = URL.createObjectURL(raw)
    popupForm.image = localPreview
    popupImageUploading.value = true
    try {
      const remoteUrl = await uploadImage(raw)
      popupForm.image = remoteUrl
    } catch {
      ElMessage.error('图片上传失败，请重试')
      // 保留本地预览，用户可重新上传
    } finally {
      popupImageUploading.value = false
      URL.revokeObjectURL(localPreview)
    }
  }

  // ───────────────────────────────────────────
  // 协议文本 Config
  // ───────────────────────────────────────────

  /** 保存当前协议 Tab 的文本内容至后端 Config */
  async function saveTextContent() {
    const key = activeTab.value as TextTabKey
    if (!TEXT_TABS.some((t) => t.name === key)) return

    const configKey = CONFIG_KEY_MAP[key]
    textSaving.value = true
    try {
      await updateConfig(configKey, { content: textContents[key] })
      ElMessage.success(`${currentTextTab.value?.label} 已保存`)
    } catch {
      ElMessage.error('保存失败，请重试')
    } finally {
      textSaving.value = false
    }
  }

  return {
    activeTab,
    currentTextTab,
    currentTextContent,
    banners,
    popups,
    bannersLoading,
    popupsLoading,
    textContents,
    textSaving,
    bannerDialogVisible,
    bannerDialogMode,
    bannerEditingId,
    bannerSubmitting,
    bannerFormRef,
    bannerForm,
    bannerFormRules,
    bannerImageUploading,
    popupDialogVisible,
    popupDialogMode,
    popupEditingId,
    popupSubmitting,
    popupFormRef,
    popupForm,
    popupFormRules,
    popupImageUploading,
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
    textTabs: TEXT_TABS,
  }
}
