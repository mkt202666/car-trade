/** 用户管理页状态与交互逻辑 */
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import {
  createEmptyRegisterForm,
  imageFields,
  registerRules,
  SEED_USERS,
} from './constants'
import {
  categoryStatus,
  formatCurrency,
  formatDeposit,
  generateUserId as computeUserId,
  parseMoneyStr,
} from './userUtils'
import type { User, UserProfile, UserRegisterForm } from './types'

export type { User, UserProfile, UserRegisterForm } from './types'
export { createEmptyRegisterForm, imageFields, registerRules, SEED_USERS } from './constants'
export {
  categoryStatus,
  formatCurrency,
  formatDeposit,
  generateUserId,
  parseMoneyStr,
} from './userUtils'

/**
 * 用户列表、档案编辑、手动建档与保证金调配的 composable。
 * 在 index.vue 中解构后绑定工具栏筛选、可展开表格、建档弹窗与调保弹窗。
 * @returns 响应式状态、计算属性与页面交互方法
 */
export function useUsers() {
  /** 搜索关键词，匹配用户 ID、姓名或手机号 */
  const keyword = ref('')
  /** 业务角色筛选，'all' 表示全部角色 */
  const roleFilter = ref('all')
  /** 表格分页当前页码 */
  const currentPage = ref(1)
  /** 表格展开行 key 列表，手风琴模式仅保留一行 */
  const expandedKeys = ref<string[]>([])
  /** 当前正在编辑档案的用户 ID，null 表示非编辑态 */
  const editingUserId = ref<string | null>(null)
  /** 进入编辑前 profile 快照，取消时用于回滚 */
  const editBackup = ref<UserProfile | null>(null)

  /** 手动建档弹窗可见性 */
  const registerDialogVisible = ref(false)
  /** 建档表单提交中 loading 状态 */
  const registerSubmitting = ref(false)
  /** 建档表单 Element Plus 实例引用 */
  const registerFormRef = ref<FormInstance>()

  /** 保证金调配弹窗可见性 */
  const adjustDialogVisible = ref(false)
  /** 调保表单提交中 loading 状态 */
  const adjustSubmitting = ref(false)
  /** 当前调保目标用户 */
  const adjustTarget = ref<User | null>(null)
  /** 调配额度输入值，正数增补、负数扣减 */
  const adjustAmount = ref('')

  /** 建档弹窗表单数据 */
  const registerForm = reactive<UserRegisterForm>(createEmptyRegisterForm())

  /** 用户列表数据源，由 SEED_USERS 初始化 */
  const users = ref<User[]>([...SEED_USERS])

  /** 经关键词与角色筛选后的用户列表，绑定 el-table :data */
  const filteredUsers = computed(() => {
    const q = keyword.value.trim().toLowerCase()
    return users.value.filter((user) => {
      const matchRole = roleFilter.value === 'all' || user.category === roleFilter.value
      if (!matchRole) return false
      if (!q) return true
      return (
        user.id.toLowerCase().includes(q) ||
        user.name.toLowerCase().includes(q) ||
        user.phone.includes(q)
      )
    })
  })

  /**
   * 判断指定用户是否处于档案编辑态。
   * @param userId 用户 ID
   * @returns 正在编辑该用户档案时为 true
   */
  function isEditing(userId: string) {
    return editingUserId.value === userId
  }

  /**
   * 表格行点击：切换展开/收起，手风琴模式仅展开一行。
   * 收起或切换行时自动取消未保存的编辑。
   * @param row 被点击的用户行数据
   */
  function handleRowClick(row: User) {
    const idx = expandedKeys.value.indexOf(row.id)
    if (idx >= 0) {
      if (isEditing(row.id)) cancelEdit()
      expandedKeys.value = []
    } else {
      if (editingUserId.value) cancelEdit()
      expandedKeys.value = [row.id]
    }
  }

  /**
   * 表格行 className 回调，区分可点击与已展开行样式。
   * @param row 当前行用户数据
   * @returns 'row-expanded' 或 'row-clickable'
   */
  function rowClassName({ row }: { row: User }) {
    return expandedKeys.value.includes(row.id) ? 'row-expanded' : 'row-clickable'
  }

  /**
   * 进入档案编辑模式，保存 profile 快照供取消时回滚。
   * 若目标行未展开则先展开；切换编辑对象时取消上一编辑。
   * @param row 目标用户行数据
   */
  function startEdit(row: User) {
    if (!expandedKeys.value.includes(row.id)) {
      expandedKeys.value = [row.id]
    }
    if (editingUserId.value && editingUserId.value !== row.id) {
      cancelEdit()
    }
    editBackup.value = { ...row.profile }
    editingUserId.value = row.id
  }

  /** 取消档案编辑，从 editBackup 恢复 profile 并清空编辑态 */
  function cancelEdit() {
    if (editingUserId.value && editBackup.value) {
      const user = users.value.find((u) => u.id === editingUserId.value)
      if (user) user.profile = { ...editBackup.value }
    }
    editingUserId.value = null
    editBackup.value = null
  }

  /**
   * 保存档案编辑（本地 mock，直接提交 row.profile 变更）。
   * @param row 已编辑的用户行数据
   */
  function saveEdit(row: User) {
    editingUserId.value = null
    editBackup.value = null
    ElMessage.success(`已保存 ${row.name} 的档案资料`)
  }

  /**
   * 禁用用户：将角色标记为「已注销」。
   * @param row 目标用户行数据
   */
  function confirmDisable(row: User) {
    row.category = '已注销'
    ElMessage.warning(`已禁用用户 ${row.name}`)
  }

  /** 调保弹窗：当前目标用户可用保证金余额（元） */
  const adjustCurrentBalance = computed(() => {
    if (!adjustTarget.value) return 0
    return parseMoneyStr(adjustTarget.value.deposit.available)
  })

  /** 调保弹窗：输入调配额度后的预计余额，不低于 0 */
  const adjustAfterBalance = computed(() => {
    const delta = Number.parseFloat(adjustAmount.value)
    if (Number.isNaN(delta)) return adjustCurrentBalance.value
    return Math.max(0, adjustCurrentBalance.value + delta)
  })

  /**
   * 打开保证金调配弹窗并绑定目标用户。
   * @param row 待调保的用户行数据
   */
  function openAdjustDialog(row: User) {
    adjustTarget.value = row
    adjustAmount.value = ''
    adjustDialogVisible.value = true
  }

  /** 调保弹窗关闭后重置目标用户与额度输入 */
  function resetAdjustForm() {
    adjustTarget.value = null
    adjustAmount.value = ''
  }

  /** 提交保证金调配：校验额度后更新可用/总保证金并关闭弹窗 */
  async function submitAdjustForm() {
    const delta = Number.parseFloat(adjustAmount.value)
    if (!adjustTarget.value || Number.isNaN(delta) || delta === 0) {
      ElMessage.warning('请输入有效的调配额度')
      return
    }

    adjustSubmitting.value = true
    await new Promise((resolve) => setTimeout(resolve, 400))

    const newBalance = adjustAfterBalance.value
    const depositStr = formatDeposit(newBalance)
    adjustTarget.value.deposit.available = depositStr
    adjustTarget.value.deposit.total = depositStr

    adjustSubmitting.value = false
    adjustDialogVisible.value = false
    ElMessage.success(`已为 ${adjustTarget.value.name} 完成调配过账`)
  }

  /** 打开手动建档弹窗 */
  function openRegisterDialog() {
    registerDialogVisible.value = true
  }

  /** 建档弹窗关闭后重置表单并清除校验状态 */
  function resetRegisterForm() {
    Object.assign(registerForm, createEmptyRegisterForm())
    registerFormRef.value?.clearValidate()
  }

  /** 提交建档表单：校验通过后生成新用户并插入列表头部 */
  async function submitRegisterForm() {
    const valid = await registerFormRef.value?.validate().catch(() => false)
    if (!valid) return

    registerSubmitting.value = true
    await new Promise((resolve) => setTimeout(resolve, 400))

    const depositStr = formatDeposit(registerForm.deposit)
    const today = new Date()
    const registerAt = `${today.getFullYear()}/${String(today.getMonth() + 1).padStart(2, '0')}/${String(today.getDate()).padStart(2, '0')}`
    const isDealer = registerForm.category === '车行用户'

    users.value.unshift({
      id: computeUserId(users.value),
      name: registerForm.name,
      registerAt,
      category: registerForm.category,
      phone: registerForm.phone,
      wechat: registerForm.wechat || '—',
      listing: { onSale: 0, offShelf: 0, wanted: 0 },
      dealership: { name: isDealer ? registerForm.name : '—', vehicles: '—' },
      deposit: { available: depositStr, total: depositStr },
      transaction: { count: 0, total: '¥0' },
      credit: { used: '0', total: isDealer ? '50.0W' : '10.0W' },
      profile: {
        dealershipName: isDealer ? registerForm.name : '',
        creditCode: '',
        provinceCity: '',
        idNumber: '',
        businessLicenseUrl: '',
        idFrontUrl: '',
        idBackUrl: '',
      },
    })

    registerSubmitting.value = false
    registerDialogVisible.value = false
    currentPage.value = 1
    ElMessage.success(`已成功建档：${registerForm.name}`)
  }

  return {
    imageFields,
    keyword,
    roleFilter,
    currentPage,
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
    generateUserId: () => computeUserId(users.value),
    openRegisterDialog,
    resetRegisterForm,
    submitAdjustForm,
    submitRegisterForm,
  }
}
