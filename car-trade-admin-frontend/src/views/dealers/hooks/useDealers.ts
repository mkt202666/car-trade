/** 车行管理页状态与交互逻辑 */
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { usePagination } from '../../../composables/usePagination'
import { getShops, getShop, createShop, updateShopStatus } from '../../../api/shops'
import {
  getShopMembers,
  addShopMember,
  updateShopMemberRole,
  removeShopMember,
  type ShopMember,
} from '../../../api/shopMembers'
import { downloadFile } from '../../../utils/request'
import type { Shop } from '../../../api/shops'
import { createJournalEntry } from '../../../api/deposits'
import { createEmptyDealerForm, createRules, provinces } from './constants'
import {
  formatCredit,
  formatDeposit,
  generateDealerId as computeDealerId,
  memberAvatar,
  statusLabel,
} from './dealerUtils'
import type { Dealer, DealerCreateForm } from './types'

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

  /** 添加成员弹窗可见性 */
  const addMemberDialogVisible = ref(false)
  /** 添加成员表单提交中 loading 状态 */
  const addMemberSubmitting = ref(false)
  /** 添加成员手机号 */
  const addMemberPhone = ref('')
  /** 添加成员昵称 */
  const addMemberNickname = ref('')
  /** 添加成员角色 */
  const addMemberRole = ref('MEMBER')

  /** 当前车行的成员列表（来自 API） */
  const shopMembers = ref<ShopMember[]>([])
  /** 成员列表加载中 */
  const membersLoading = ref(false)

  /** 详情抽屉可见性 */
  const drawerVisible = ref(false)
  /** 详情抽屉当前选中的车行 */
  const selectedDealer = ref<Dealer | null>(null)

  /** 详情抽屉成员列表，取自 selectedDealer.membersList */
  const drawerMembers = computed(() => selectedDealer.value?.membersList ?? [])

  /** 新增车行弹窗表单数据 */
  const createForm = reactive<DealerCreateForm>(createEmptyDealerForm())

  /** 车行列表数据源 */
  const dealers = ref<Dealer[]>([])
  const loading = ref(false)

  /** 从 API 获取车行数据 */
  async function fetchDealers() {
    loading.value = true
    try {
      // The API interceptor unwraps ApiResponse, so res IS PageResult<Shop> directly
      const res = (await getShops({ page: 1, size: 100 })) as unknown as {
        list?: Shop[]
      }
      if (res?.list) {
        dealers.value = res.list.map((shop: Shop) => ({
          id: `S${String(shop.id).padStart(6, '0')}`,
          name: shop.shopName || '—',
          joinedAt: shop.createdAt?.split('T')[0]?.replace(/-/g, '/') || '—',
          adminName: shop.realName || '—',
          phone: shop.phone || '—',
          province: '—',
          city: '—',
          address: '—',
          creditCode: '未录入',
          depositBalance: 0,
          members: shop.memberCount || 1,
          membersList: [],
          monthlyOrders: 0,
          totalOrders: shop.dealCount || 0,
          vehicles: { onSale: shop.onSaleCount || 0, offShelf: 0 },
          licenseUrl: '',
          credit: null,
          status: (shop.status?.toLowerCase() === 'active' ? 'active' : 'suspended') as 'active' | 'suspended',
        }))
      }
    } catch (e) {
      console.error('Failed to fetch dealers:', e)
      ElMessage.error('获取车行列表失败')
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    fetchDealers()
  })

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

  /** 分页：当前页码、每页行数、切片数据、起止序号 */
  const { currentPage, pageSize, paginatedItems: paginatedDealers, pageRangeStart, pageRangeEnd } =
    usePagination({
      source: filteredDealers,
      defaultPageSize: 20,
      resetOn: [keyword, statusFilter, provinceFilter],
    })

  /**
   * 从 API 获取车行成员列表。
   * @param dealer 当前选中的车行
   */
  async function fetchShopMembers(dealer: Dealer) {
    const shopId = parseInt(dealer.id.replace(/^S/, ''), 10)
    if (Number.isNaN(shopId)) return

    membersLoading.value = true
    try {
      const data = (await getShopMembers(shopId)) as unknown as ShopMember[]
      shopMembers.value = data ?? []
    } catch (e) {
      console.error('Failed to fetch shop members:', e)
      shopMembers.value = []
    } finally {
      membersLoading.value = false
    }
  }

  /**
   * 表格行点击：选中车行并打开详情抽屉，同时从 API 刷新详情数据。
   * @param row 被点击的车行行数据
   */
  function handleRowClick(row: Dealer) {
    selectedDealer.value = row
    drawerVisible.value = true
    fetchDealerDetail(row)
    fetchShopMembers(row)
  }

  /**
   * 从 API 获取车行详情并更新 selectedDealer。
   * 用 API 返回的最新数据覆盖本地列表行中的占位字段。
   * @param dealer 当前列表中的车行（用于提取 shopId）
   */
  async function fetchDealerDetail(dealer: Dealer) {
    const shopId = parseInt(dealer.id.replace(/^S/, ''), 10)
    if (Number.isNaN(shopId)) return

    try {
      const res = (await getShop(shopId)) as unknown as Shop
      if (!res || !selectedDealer.value || selectedDealer.value.id !== dealer.id) return

      // Merge API detail data into the selected dealer
      selectedDealer.value.name = res.shopName || selectedDealer.value.name
      selectedDealer.value.adminName = res.realName || selectedDealer.value.adminName
      selectedDealer.value.phone = res.phone || selectedDealer.value.phone
      selectedDealer.value.totalOrders = res.dealCount ?? selectedDealer.value.totalOrders
      selectedDealer.value.vehicles.onSale = res.onSaleCount ?? selectedDealer.value.vehicles.onSale
      selectedDealer.value.members = res.memberCount ?? selectedDealer.value.members
      selectedDealer.value.status = (res.status?.toLowerCase() === 'active' ? 'active' : 'suspended') as 'active' | 'suspended'
    } catch (e) {
      console.error('Failed to fetch dealer detail:', e)
      ElMessage.error('获取车行详情失败')
    }
  }

  /** 关闭详情抽屉 */
  function closeDrawer() {
    drawerVisible.value = false
  }

  /** 打开添加成员弹窗 */
  function handleAddMember() {
    addMemberPhone.value = ''
    addMemberNickname.value = ''
    addMemberRole.value = 'MEMBER'
    addMemberDialogVisible.value = true
  }

  /** 提交添加成员表单 */
  async function submitAddMember() {
    if (!selectedDealer.value) return
    if (!addMemberPhone.value.trim()) {
      ElMessage.warning('请输入成员手机号')
      return
    }

    const shopId = parseInt(selectedDealer.value.id.replace(/^S/, ''), 10)
    if (Number.isNaN(shopId)) return

    addMemberSubmitting.value = true
    try {
      await addShopMember(shopId, {
        phone: addMemberPhone.value.trim(),
        nickname: addMemberNickname.value.trim() || undefined,
        role: addMemberRole.value,
      })
      ElMessage.success('成员添加成功')
      addMemberDialogVisible.value = false
      await fetchShopMembers(selectedDealer.value)
    } catch (e) {
      console.error('Failed to add member:', e)
    } finally {
      addMemberSubmitting.value = false
    }
  }

  /**
   * 修改成员角色（设为管理员 MEMBER → ADMIN，或取消管理员 ADMIN → MEMBER）。
   * @param member 目标成员数据
   */
  async function handleChangeRole(member: ShopMember) {
    if (!selectedDealer.value) return
    const shopId = parseInt(selectedDealer.value.id.replace(/^S/, ''), 10)
    if (Number.isNaN(shopId)) return

    const newRole = member.role === 'ADMIN' ? 'MEMBER' : 'ADMIN'
    try {
      await updateShopMemberRole(shopId, member.id, { role: newRole })
      ElMessage.success(`已将 ${member.nickname || member.realName || '该成员'} 角色更新为 ${newRole === 'ADMIN' ? '管理员' : '普通成员'}`)
      await fetchShopMembers(selectedDealer.value)
    } catch (e) {
      console.error('Failed to update member role:', e)
    }
  }

  /**
   * 从当前车行移除成员，确认后调用 API 删除。
   * @param member 待移除的成员数据
   */
  async function handleRemoveMember(member: ShopMember) {
    if (!selectedDealer.value) return
    try {
      await ElMessageBox.confirm(`确定将「${member.nickname || member.realName || '该成员'}」从该车行移除？`, '确认移除', {
        confirmButtonText: '确认移除',
        cancelButtonText: '取消',
        type: 'warning',
      })
    } catch {
      return
    }

    const shopId = parseInt(selectedDealer.value.id.replace(/^S/, ''), 10)
    if (Number.isNaN(shopId)) return

    try {
      await removeShopMember(shopId, member.id)
      ElMessage.success('成员已移除')
      await fetchShopMembers(selectedDealer.value)
    } catch (e) {
      console.error('Failed to remove member:', e)
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

  /** 提交创建表单：校验通过后调用 API 创建车行 */
  async function submitCreateForm() {
    const valid = await createFormRef.value?.validate().catch(() => false)
    if (!valid) return

    createSubmitting.value = true
    try {
      await createShop({
        shopName: createForm.name,
        contactName: createForm.adminName,
        phone: createForm.phone,
      })
      ElMessage.success(`已创建并提报审核：${createForm.name}`)
      createDialogVisible.value = false
      currentPage.value = 1
      await fetchDealers()
    } catch (e) {
      console.error('Failed to create dealer:', e)
      ElMessage.error('创建车行失败')
    } finally {
      createSubmitting.value = false
    }
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

  /**
   * 提交调保表单：校验额度后调用 deposits/journal 接口完成 CHARGE 或 WITHDRAW 过账。
   */
  async function submitAdjustForm() {
    if (!adjustTarget.value) return
    const amount = parseFloat(adjustAmount.value)
    if (Number.isNaN(amount) || amount === 0) {
      ElMessage.warning('请输入有效的调配额度')
      return
    }

    adjustSubmitting.value = true
    try {
      await createJournalEntry({
        userId: Number(adjustTarget.value.id),
        amount: Math.abs(amount),
        type: amount > 0 ? 'CHARGE' : 'WITHDRAW',
        description: '车行保证金调整',
      })
      ElMessage.success(`已对 ${adjustTarget.value.name} 完成调配过账`)
      adjustDialogVisible.value = false
      await fetchDealers()
    } catch {
      ElMessage.error('保证金调配失败')
    } finally {
      adjustSubmitting.value = false
    }
  }

  /**
   * 停发车行：确认后调用 API 更新状态。
   * @param dealer 目标车行数据
   */
  async function confirmSuspend(dealer: Dealer) {
    try {
      await ElMessageBox.confirm(
        `确定对「${dealer.name}」执行停发操作？停发后车行将被封禁挂起。`,
        '确认停发',
        { confirmButtonText: '确认停发', cancelButtonText: '取消', type: 'warning' },
      )
    } catch {
      // 用户取消确认框，直接返回
      return
    }

    try {
      const shopId = parseInt(dealer.id.replace('S', ''), 10)
      await updateShopStatus(shopId, 'SUSPENDED')
      dealer.status = 'suspended'
      ElMessage.success(`已停发：${dealer.name}`)
    } catch (e) {
      console.error('Failed to suspend dealer:', e)
      ElMessage.error('停发车行操作失败')
    }
  }

  /** 导出当前筛选条件下的车行列表为 Excel */
  function handleExport() {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
    const params = new URLSearchParams()
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    if (statusFilter.value !== 'all') params.set('status', statusFilter.value)
    if (provinceFilter.value !== 'all') params.set('province', provinceFilter.value)
    const qs = params.toString()
    const date = new Date().toISOString().slice(0, 10)
    downloadFile(`${baseUrl}/shops/export${qs ? `?${qs}` : ''}`, `车行列表_${date}.xlsx`)
  }

  return {
    keyword,
    statusFilter,
    provinceFilter,
    currentPage,
    pageSize,
    loading,
    createDialogVisible,
    createSubmitting,
    createFormRef,
    adjustDialogVisible,
    adjustSubmitting,
    adjustTarget,
    adjustAmount,
    addMemberDialogVisible,
    addMemberSubmitting,
    addMemberPhone,
    addMemberNickname,
    addMemberRole,
    shopMembers,
    membersLoading,
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
    submitAddMember,
    handleChangeRole,
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
    handleExport,
    createRules,
    fetchDealers,
  }
}
