/** 用户管理页状态与交互逻辑 */
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import {
  createEmptyRegisterForm,
  imageFields,
  registerRules,
} from './constants'
import {
  categoryStatus,
  formatCurrency,
  formatDeposit,
  generateUserId as computeUserId,
  parseMoneyStr,
} from './userUtils'
import {
  getUsers as fetchUsersApi,
  updateUserStatus,
  createUser,
  updateUserProfile,
  type User as ApiUser,
} from '../../../api/users'
import { createJournalEntry } from '../../../api/deposits'
import type { User, UserProfile, UserRegisterForm } from './types'

export type { User, UserProfile, UserRegisterForm } from './types'
export { createEmptyRegisterForm, imageFields, registerRules } from './constants'
export {
  categoryStatus,
  formatCurrency,
  formatDeposit,
  generateUserId,
  parseMoneyStr,
} from './userUtils'

/**
 * 将后端 ApiUser 映射为前端 User 类型。
 * 后端字段与前端展示模型差异较大，此处做统一适配：
 * - nickname → name, userRole → category (PERSONAL→个人用户, SHOP→车行用户, ADMIN→系统管理员)
 * - createdAt → registerAt, status=FROZEN→已冻结, status=DELETED→已注销
 * - 统计字段从 API 响应中读取：onSaleCount, dealCount, creditScore, creditGrade
 * - 档案字段从 API 响应中读取：creditCode, province, city, idCardNumber, 图片URL等
 * @param u 后端返回的用户对象
 * @returns 前端表格行所需的 User 结构
 */
function mapApiUserToFrontendUser(u: ApiUser): User {
  const isDeleted = u.status === 'DELETED'
  const isFrozen = u.status === 'FROZEN'

  // userRole 映射为中文分类名
  const roleMap: Record<string, string> = {
    PERSONAL: '个人用户',
    SHOP: '车行用户',
    ADMIN: '系统管理员',
    DEVELOPER: 'IT开发',
  }
  let categoryName: string
  if (isDeleted) {
    categoryName = '已注销'
  } else if (isFrozen) {
    categoryName = '已冻结'
  } else {
    categoryName = roleMap[u.userRole] || '个人用户'
  }

  const depositStr = formatDeposit(0)
  const provinceCity = [u.province, u.city].filter(Boolean).join(' ') || ''

  return {
    id: String(u.id),
    name: u.nickname || u.phone || `用户${u.id}`,
    registerAt: u.createdAt
      ? new Date(u.createdAt).toLocaleDateString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
        })
      : '',
    category: categoryName,
    phone: u.phone || '—',
    wechat: '—',
    listing: { onSale: u.onSaleCount || 0, offShelf: 0, wanted: 0 },
    dealership: {
      name: u.shopName || '—',
      vehicles: u.shopName ? '5D 合作车辆' : '—',
    },
    deposit: { available: depositStr, total: depositStr },
    transaction: {
      count: u.dealCount || 0,
      total: u.dealCount ? `¥${u.dealCount}笔` : '¥0',
    },
    credit: {
      used: u.creditScore ? String(u.creditScore) : '0',
      total: u.creditGrade || '0',
    },
    profile: {
      dealershipName: u.shopName || '',
      creditCode: u.creditCode || '',
      provinceCity: provinceCity,
      idNumber: u.idCardNumber || '',
      businessLicenseUrl: u.businessLicenseUrl || '',
      idFrontUrl: u.idCardFrontUrl || '',
      idBackUrl: u.idCardBackUrl || '',
    },
  }
}

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
  /** 服务端返回的总记录数，驱动分页组件 total */
  const totalUsers = ref(0)
  /** 列表加载中状态 */
  const loading = ref(false)
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

  /** 用户列表数据源，由后端 API 加载 */
  const users = ref<User[]>([])

  /**
   * 从后端加载用户分页列表。
   * 将 keyword 和 roleFilter 映射为后端查询参数，
   * 并将返回的 ApiUser 数组转换为前端 User 类型。
   */
  async function loadUsers() {
    loading.value = true
    try {
      const params: Record<string, unknown> = {
        page: currentPage.value,
        size: 10,
      }

      const q = keyword.value.trim()
      if (q) {
        params.keyword = q
      }

      // 角色筛选映射：前端中文角色名 → 后端 userType 枚举值
      // 'all' 不传 userType 参数，表示不筛选
      if (roleFilter.value !== 'all') {
        const roleMap: Record<string, string> = {
          '个人用户': 'PERSONAL_USER',
          '车行用户': 'SHOP_USER',
          '系统管理员': 'ADMIN_USER',
          '已注销': 'DELETED',
        }
        const apiUserType = roleMap[roleFilter.value]
        if (apiUserType) {
          params.userType = apiUserType
        }
      }

      const result = await fetchUsersApi(params)
      // 拦截器已解包 ApiResponse，此处 result 实际为 PageResult<ApiUser>
      const pageResult = result as unknown as {
        list: ApiUser[]
        total: number
        page: number
        size: number
      }
      users.value = (pageResult.list || []).map(mapApiUserToFrontendUser)
      totalUsers.value = pageResult.total || 0
    } catch {
      ElMessage.error('加载用户列表失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  /** 组件挂载时加载首页用户数据 */
  onMounted(() => {
    loadUsers()
  })

  /** 搜索关键词或角色筛选变化时，重置到第一页并重新加载 */
  let searchTimer: ReturnType<typeof setTimeout> | null = null
  watch([keyword, roleFilter], () => {
    currentPage.value = 1
    // 防抖 300ms，避免快速输入时频繁请求
    if (searchTimer) clearTimeout(searchTimer)
    searchTimer = setTimeout(() => {
      loadUsers()
    }, 300)
  })

  /** 页码变化时重新加载对应页数据 */
  watch(currentPage, () => {
    loadUsers()
  })

  /**
   * 经关键词与角色筛选后的用户列表，绑定 el-table :data。
   * 服务端已做分页与筛选，此处直接返回当前页数据。
   */
  const filteredUsers = computed(() => users.value)

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
   * 保存档案编辑，调用后端 PUT /users/{id}/profile 提交变更。
   * 将行内可编辑字段映射为 UserProfileUpdateDTO 后提交，
   * 成功后清除编辑态并刷新列表。
   * @param row 已编辑的用户行数据
   */
  async function saveEdit(row: User) {
    try {
      const [province, city] = (row.profile.provinceCity || '').split(/\s+/)
      await updateUserProfile(Number(row.id), {
        nickname: row.name,
        shopName: row.profile.dealershipName || undefined,
        creditCode: row.profile.creditCode || undefined,
        province: province || undefined,
        city: city || undefined,
        idCardNumber: row.profile.idNumber || undefined,
        businessLicenseUrl: row.profile.businessLicenseUrl || undefined,
        idCardFrontUrl: row.profile.idFrontUrl || undefined,
        idCardBackUrl: row.profile.idBackUrl || undefined,
      })
      editingUserId.value = null
      editBackup.value = null
      ElMessage.success(`已保存 ${row.name} 的档案资料`)
      await loadUsers()
    } catch {
      ElMessage.error('保存档案失败，请重试')
    }
  }

  /**
   * 冻结用户：调用后端 API 将状态设为 FROZEN，成功后更新本地列表。
   * @param row 目标用户行数据
   */
  async function confirmDisable(row: User) {
    try {
      await updateUserStatus(Number(row.id), 'FROZEN')
      row.category = '已冻结'
      ElMessage.warning(`已冻结用户 ${row.name}`)
      await loadUsers()
    } catch {
      ElMessage.error(`冻结用户 ${row.name} 失败`)
    }
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

  /**
   * 提交保证金调配，调用后端 deposits/journal 接口完成 CHARGE 或 WITHDRAW 过账。
   */
  async function submitAdjustForm() {
    const delta = Number.parseFloat(adjustAmount.value)
    if (!adjustTarget.value || Number.isNaN(delta) || delta === 0) {
      ElMessage.warning('请输入有效的调配额度')
      return
    }

    adjustSubmitting.value = true
    try {
      await createJournalEntry({
        userId: Number(adjustTarget.value.id),
        amount: Math.abs(delta),
        type: delta > 0 ? 'CHARGE' : 'WITHDRAW',
        description: '管理员手动调整',
      })
      ElMessage.success(`已为 ${adjustTarget.value.name} 完成调配过账`)
      adjustDialogVisible.value = false
      await loadUsers()
    } catch {
      ElMessage.error('保证金调配失败')
    } finally {
      adjustSubmitting.value = false
    }
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

  /**
   * 提交建档表单：校验通过后调用 createUser API 创建用户，
   * 若首充保证金 > 0 则自动调用保证金入账接口，
   * 成功后提示默认密码并刷新列表。
   */
  async function submitRegisterForm() {
    const valid = await registerFormRef.value?.validate().catch(() => false)
    if (!valid) return

    registerSubmitting.value = true
    try {
      const result = await createUser({
        phone: registerForm.phone,
        nickname: registerForm.name,
        userRole: registerForm.category === '车行用户' ? 'SHOP' : 'PERSONAL',
      })

      // 首充保证金 > 0 时，自动入账
      if (registerForm.deposit > 0) {
        const created = result as unknown as { id: number }
        await createJournalEntry({
          userId: created.id,
          amount: registerForm.deposit,
          type: 'CHARGE',
          description: '开户首充保证金',
        })
      }

      ElMessage.success(`用户创建成功，默认密码为 123456，请提醒用户及时修改。`)
      registerDialogVisible.value = false
      currentPage.value = 1
      await loadUsers()
    } catch (err) {
      ElMessage.error(err instanceof Error ? err.message : '创建失败')
    } finally {
      registerSubmitting.value = false
    }
  }

  return {
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
    generateUserId: () => computeUserId(users.value),
    openRegisterDialog,
    resetRegisterForm,
    submitAdjustForm,
    submitRegisterForm,
    loadUsers,
  }
}
