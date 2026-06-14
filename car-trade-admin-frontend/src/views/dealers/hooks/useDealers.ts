/** 车行管理页状态与交互逻辑 */
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { createEmptyDealerForm, createRules, provinces, SEED_DEALERS } from './constants'
import {
  formatCredit,
  formatDeposit,
  generateDealerId as computeDealerId,
  memberAvatar,
  statusLabel,
} from './dealerUtils'
import type { Dealer, DealerCreateForm, DealerMember } from './types'

export type { Dealer, DealerCreateForm, DealerCredit, DealerMember } from './types'
export { createRules, createEmptyDealerForm, provinces, SEED_DEALERS } from './constants'
export {
  formatCredit,
  formatDeposit,
  generateDealerId,
  memberAvatar,
  statusLabel,
} from './dealerUtils'

/**
 * 车行列表、详情抽屉、新增车行与成员管理的 composable。
 * 在 index.vue 中解构后绑定筛选栏、分页表格、创建弹窗、调保弹窗与右侧详情抽屉。
 * @returns 响应式状态、计算属性与页面交互方法
 */
export function useDealers() {
  /** 搜索关键词，匹配车行名称、管理员、手机号或 ID */
  const keyword = ref('')
  /** 准入状态筛选，'all' 表示全部状态 */
  const statusFilter = ref('all')
  /** 驻点省份筛选，'all' 表示全部省份 */
  const provinceFilter = ref('all')
  /** 表格分页当前页码 */
  const currentPage = ref(1)
  /** 每页显示行数，支持 20/50/100 */
  const pageSize = ref(20)

  /** 新增车行弹窗可见性 */
  const createDialogVisible = ref(false)
  /** 创建表单提交中 loading 状态 */
  const createSubmitting = ref(false)
  /** 创建表单 Element Plus 实例引用 */
  const createFormRef = ref<FormInstance>()

  /** 保证金/授信调配弹窗可见性 */
  const adjustDialogVisible = ref(false)
  /** 调保表单提交中 loading 状态 */
  const adjustSubmitting = ref(false)
  /** 当前调保目标车行 */
  const adjustTarget = ref<Dealer | null>(null)
  /** 调配额度输入值，正数增补、负数扣减 */
  const adjustAmount = ref('')

  /** 详情抽屉可见性 */
  const drawerVisible = ref(false)
  /** 详情抽屉当前选中的车行 */
  const selectedDealer = ref<Dealer | null>(null)

  /** 详情抽屉成员列表，取自 selectedDealer.membersList */
  const drawerMembers = computed(() => selectedDealer.value?.membersList ?? [])

  /** 新增车行弹窗表单数据 */
  const createForm = reactive<DealerCreateForm>(createEmptyDealerForm())

  /** 车行列表数据源，由 SEED_DEALERS 初始化 */
  const dealers = ref<Dealer[]>([...SEED_DEALERS])

  /** 经关键词、状态与省份筛选后的车行列表 */
  const filteredDealers = computed(() => {
    const kw = keyword.value.trim().toLowerCase()
    return dealers.value.filter((d) => {
      if (statusFilter.value !== 'all' && d.status !== statusFilter.value) return false
      if (provinceFilter.value !== 'all' && d.province !== provinceFilter.value) return false
      if (!kw) return true
      return (
        d.name.toLowerCase().includes(kw) ||
        d.adminName.toLowerCase().includes(kw) ||
        d.phone.includes(kw) ||
        d.id.toLowerCase().includes(kw)
      )
    })
  })

  /** 当前页切片后的车行列表，绑定 el-table :data */
  const paginatedDealers = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return filteredDealers.value.slice(start, start + pageSize.value)
  })

  /** 分页信息：当前页起始序号（从 1 计，无数据时为 0） */
  const pageRangeStart = computed(() =>
    filteredDealers.value.length === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1,
  )

  /** 分页信息：当前页结束序号 */
  const pageRangeEnd = computed(() =>
    Math.min(currentPage.value * pageSize.value, filteredDealers.value.length),
  )

  // 筛选条件或每页行数变化时重置到第一页
  watch([keyword, statusFilter, provinceFilter, pageSize], () => {
    currentPage.value = 1
  })

  /**
   * 表格行点击：选中车行并打开详情抽屉。
   * @param row 被点击的车行行数据
   */
  function handleRowClick(row: Dealer) {
    selectedDealer.value = row
    drawerVisible.value = true
  }

  /** 关闭详情抽屉 */
  function closeDrawer() {
    drawerVisible.value = false
  }

  /** 添加成员（功能占位，待后续实现） */
  function handleAddMember() {
    ElMessage.info('添加成员功能开发中')
  }

  /**
   * 将指定成员设为车行管理员，同步更新 adminName 与 phone。
   * @param member 目标成员数据
   */
  function handleSetAdmin(member: DealerMember) {
    if (!selectedDealer.value) return
    selectedDealer.value.adminName = member.name
    selectedDealer.value.phone = member.phone
    ElMessage.success(`已将 ${member.name} 设为车行管理员`)
  }

  /**
   * 从当前车行移除成员，确认后更新 membersList 与 members 计数。
   * @param member 待移除的成员数据
   */
  async function handleRemoveMember(member: DealerMember) {
    if (!selectedDealer.value) return
    try {
      await ElMessageBox.confirm(`确定将「${member.name}」从该车行移除？`, '确认移除', {
        confirmButtonText: '确认移除',
        cancelButtonText: '取消',
        type: 'warning',
      })
      const list = selectedDealer.value.membersList
      const idx = list.findIndex((m) => m.userId === member.userId)
      if (idx !== -1) {
        list.splice(idx, 1)
        selectedDealer.value.members = list.length
      }
      ElMessage.success(`已移除成员：${member.name}`)
    } catch {
      // 用户取消确认框
    }
  }

  /** 打开新增车行弹窗 */
  function openCreateDialog() {
    createDialogVisible.value = true
  }

  /** 创建弹窗关闭后重置表单并清除校验状态 */
  function resetCreateForm() {
    Object.assign(createForm, createEmptyDealerForm())
    createFormRef.value?.resetFields()
  }

  /** 提交创建表单：校验通过后生成新车行并插入列表头部 */
  async function submitCreateForm() {
    const valid = await createFormRef.value?.validate().catch(() => false)
    if (!valid) return

    createSubmitting.value = true
    await new Promise((r) => setTimeout(r, 400))

    const today = new Date()
    const joinedAt = `${today.getFullYear()}/${today.getMonth() + 1}/${today.getDate()}`
    const creditTotal = createForm.initialCredit

    dealers.value.unshift({
      id: computeDealerId(dealers.value),
      name: createForm.name,
      joinedAt,
      adminName: createForm.adminName,
      phone: createForm.phone,
      province: '—',
      city: '—',
      address: createForm.address || '—',
      creditCode: createForm.creditCode || '未录入',
      depositBalance: creditTotal,
      members: 1,
      membersList: [
        {
          userId: `USR-${Date.now().toString().slice(-7)}`,
          name: createForm.adminName,
          phone: createForm.phone,
        },
      ],
      monthlyOrders: 0,
      totalOrders: 0,
      vehicles: { onSale: 0, offShelf: 0 },
      licenseUrl: createForm.licenseUrl,
      credit: creditTotal > 0 ? { used: '0', total: formatCredit(creditTotal) } : null,
      status: 'active',
    })

    createSubmitting.value = false
    createDialogVisible.value = false
    currentPage.value = 1
    ElMessage.success(`已创建并提报审核：${createForm.name}`)
  }

  /**
   * 打开保证金/授信调配弹窗并绑定目标车行。
   * @param dealer 待调保的车行数据
   */
  function openAdjustDialog(dealer: Dealer) {
    adjustTarget.value = dealer
    adjustAmount.value = ''
    adjustDialogVisible.value = true
  }

  /** 调保弹窗关闭后重置目标车行与额度输入 */
  function resetAdjustForm() {
    adjustTarget.value = null
    adjustAmount.value = ''
  }

  /** 提交调保表单：校验额度后提示成功（本地 mock，不修改余额） */
  async function submitAdjustForm() {
    if (!adjustTarget.value) return
    const amount = parseFloat(adjustAmount.value)
    if (Number.isNaN(amount) || amount === 0) {
      ElMessage.warning('请输入有效的调配额度')
      return
    }

    adjustSubmitting.value = true
    await new Promise((r) => setTimeout(r, 400))

    ElMessage.success(`已对 ${adjustTarget.value.name} 完成调配过账`)
    adjustSubmitting.value = false
    adjustDialogVisible.value = false
  }

  /**
   * 停发车行：确认后将 status 设为 suspended（封禁挂起）。
   * @param dealer 目标车行数据
   */
  async function confirmSuspend(dealer: Dealer) {
    try {
      await ElMessageBox.confirm(
        `确定对「${dealer.name}」执行停发操作？停发后车行将被封禁挂起。`,
        '确认停发',
        { confirmButtonText: '确认停发', cancelButtonText: '取消', type: 'warning' },
      )
      dealer.status = 'suspended'
      ElMessage.success(`已停发：${dealer.name}`)
    } catch {
      // 用户取消确认框
    }
  }

  return {
    keyword,
    statusFilter,
    provinceFilter,
    currentPage,
    pageSize,
    createDialogVisible,
    createSubmitting,
    createFormRef,
    adjustDialogVisible,
    adjustSubmitting,
    adjustTarget,
    adjustAmount,
    drawerVisible,
    selectedDealer,
    drawerMembers,
    createForm,
    provinces,
    dealers,
    filteredDealers,
    paginatedDealers,
    pageRangeStart,
    pageRangeEnd,
    statusLabel,
    handleRowClick,
    closeDrawer,
    formatDeposit,
    memberAvatar,
    handleAddMember,
    handleSetAdmin,
    openCreateDialog,
    resetCreateForm,
    formatCredit,
    generateDealerId: () => computeDealerId(dealers.value),
    openAdjustDialog,
    resetAdjustForm,
    handleRemoveMember,
    submitCreateForm,
    submitAdjustForm,
    confirmSuspend,
    createRules,
  }
}
