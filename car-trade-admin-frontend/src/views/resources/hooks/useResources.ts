/** 资源管理 composable */

import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadFile } from 'element-plus'
import {
  bannerFormRules,
  createEmptyAdForm,
  INITIAL_TEXT_CONTENTS,
  popupFormRules,
  SEED_BANNERS,
  TEXT_TABS,
} from './constants'
import { moveListItem } from './resourceUtils'
import type { AdItem, TextTabKey } from './types'

export type { AdItem, TextTabKey } from './types'
export {
  bannerFormRules,
  createEmptyAdForm,
  INITIAL_TEXT_CONTENTS,
  popupFormRules,
  SEED_BANNERS,
  TEXT_TABS,
} from './constants'
export { moveListItem } from './resourceUtils'

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
  const banners = ref<AdItem[]>([...SEED_BANNERS])
  /** 弹窗广告列表数据 */
  const popups = ref<AdItem[]>([])

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

  /** 校验并提交 Banner 表单，新建追加或更新已有条目 */
  async function submitBannerForm() {
    const valid = await bannerFormRef.value?.validate().catch(() => false)
    if (!valid) return

    bannerSubmitting.value = true
    try {
      if (bannerDialogMode.value === 'create') {
        banners.value.push({
          id: `b${Date.now()}`,
          title: bannerForm.title,
          link: bannerForm.link,
          image: bannerForm.image,
          active: bannerForm.active,
        })
        ElMessage.success('Banner 已添加')
      } else {
        const target = banners.value.find((b) => b.id === bannerEditingId.value)
        if (target) {
          target.title = bannerForm.title
          target.link = bannerForm.link
          target.image = bannerForm.image
          target.active = bannerForm.active
        }
        ElMessage.success('Banner 已更新')
      }
      bannerDialogVisible.value = false
    } finally {
      bannerSubmitting.value = false
    }
  }

  /**
   * 调整 Banner 列表排序
   * @param index - 当前 Banner 索引
   * @param direction - 移动方向：-1 向前，1 向后
   */
  function moveBanner(index: number, direction: -1 | 1) {
    const next = moveListItem(banners.value, index, direction)
    if (next) banners.value = next
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
      banners.value = banners.value.filter((b) => b.id !== banner.id)
      ElMessage.success('已删除')
    } catch {
      /* cancelled */
    }
  }

  /**
   * 处理 Banner 图片本地上传，生成本地预览 URL
   * @param file - Element Plus 上传组件返回的文件对象
   */
  function handleBannerImageUpload(file: UploadFile) {
    const raw = file.raw
    if (!raw) return
    bannerForm.image = URL.createObjectURL(raw)
  }

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

  /** 校验并提交弹窗表单，新建追加或更新已有条目 */
  async function submitPopupForm() {
    const valid = await popupFormRef.value?.validate().catch(() => false)
    if (!valid) return

    popupSubmitting.value = true
    try {
      if (popupDialogMode.value === 'create') {
        popups.value.push({
          id: `p${Date.now()}`,
          title: popupForm.title,
          link: popupForm.link,
          image: popupForm.image,
          active: popupForm.active,
        })
        ElMessage.success('弹窗广告已添加')
      } else {
        const target = popups.value.find((p) => p.id === popupEditingId.value)
        if (target) {
          target.title = popupForm.title
          target.link = popupForm.link
          target.image = popupForm.image
          target.active = popupForm.active
        }
        ElMessage.success('弹窗广告已更新')
      }
      popupDialogVisible.value = false
    } finally {
      popupSubmitting.value = false
    }
  }

  /**
   * 调整弹窗广告列表排序
   * @param index - 当前弹窗索引
   * @param direction - 移动方向：-1 向前，1 向后
   */
  function movePopup(index: number, direction: -1 | 1) {
    const next = moveListItem(popups.value, index, direction)
    if (next) popups.value = next
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
      popups.value = popups.value.filter((p) => p.id !== popup.id)
      ElMessage.success('已删除')
    } catch {
      /* cancelled */
    }
  }

  /**
   * 处理弹窗图片本地上传，生成本地预览 URL
   * @param file - Element Plus 上传组件返回的文件对象
   */
  function handlePopupImageUpload(file: UploadFile) {
    const raw = file.raw
    if (!raw) return
    popupForm.image = URL.createObjectURL(raw)
  }

  /** 保存当前协议 Tab 的文本内容（mock 延迟后提示成功） */
  async function saveTextContent() {
    const key = activeTab.value as TextTabKey
    if (!TEXT_TABS.some((t) => t.name === key)) return

    textSaving.value = true
    try {
      await new Promise((resolve) => setTimeout(resolve, 400))
      ElMessage.success(`${currentTextTab.value?.label} 已保存`)
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
    textTabs: TEXT_TABS,
  }
}
