import { useState, useEffect, useCallback, useRef } from 'react'
import { Search, Plus, ChevronDown, Edit2, Shield, Ban, ChevronLeft, ChevronRight, CheckCircle } from 'lucide-react'
import { userApi } from '../../api/user'
import { usePagination } from '../../hooks/usePagination'
import { SkeletonTable } from '../../components/Skeleton'
import { formatDate, maskPhone, getStatusColor, getStatusText } from '../../utils/format'

// Role 映射（匹配后端 UserRole 枚举）
const ROLE_MAP = {
  '全部': null,
  '个人用户': 'PERSONAL',
  '车行用户': 'SHOP',
  '系统管理员': 'ADMIN',
  '开发人员': 'DEVELOPER',
}
const ROLE_OPTIONS = Object.keys(ROLE_MAP)

// 角色标签颜色
const ROLE_COLOR_MAP = {
  PERSONAL: 'bg-emerald-50 text-emerald-600 border-emerald-200',
  SHOP: 'bg-indigo-50 text-indigo-600 border-indigo-200',
  ADMIN: 'bg-purple-50 text-purple-600 border-purple-200',
  DEVELOPER: 'bg-blue-50 text-blue-600 border-blue-200',
}

// 格式化会员到期时间
function formatMemberExpire(expireAt) {
  if (!expireAt) return '-'
  const expireDate = new Date(expireAt)
  const now = new Date()
  if (expireDate < now) {
    return <span className="text-red-500">已过期</span>
  }
  const daysLeft = Math.ceil((expireDate - now) / (1000 * 60 * 60 * 24))
  if (daysLeft <= 7) {
    return <span className="text-orange-500">{daysLeft}天后到期</span>
  }
  return expireAt.split('T')[0]
}

// 登录失败次数徽章
function LoginFailBadge({ count, lockedUntil }) {
  if (!count || count === 0) return null
  
  const isLocked = lockedUntil && new Date(lockedUntil) > new Date()
  
  if (isLocked) {
    return (
      <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[10px] font-medium bg-red-100 text-red-600 border border-red-200">
        🔒 已锁定
      </span>
    )
  }
  
  if (count >= 3) {
    return (
      <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[10px] font-medium bg-orange-100 text-orange-600 border border-orange-200">
        ⚠️ {count}次失败
      </span>
    )
  }
  
  return (
    <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[10px] font-medium bg-yellow-100 text-yellow-600 border border-yellow-200">
      {count}次失败
    </span>
  )
}

export default function UserManage() {
  const [search, setSearch] = useState('')
  const [roleFilter, setRoleFilter] = useState('全部')
  const [showRoleDropdown, setShowRoleDropdown] = useState(false)
  const [showPageSizeDropdown, setShowPageSizeDropdown] = useState(false)
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const pagination = usePagination(50)
  const { page, size, total, totalPages, setPage, setSize, setTotal, reset } = pagination

  // Debounce ref
  const debounceRef = useRef(null)
  const [debouncedSearch, setDebouncedSearch] = useState('')

  // Debounce search
  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current)
    debounceRef.current = setTimeout(() => {
      setDebouncedSearch(search)
    }, 300)
    return () => clearTimeout(debounceRef.current)
  }, [search])

  // Reset page when search or role changes
  useEffect(() => {
    reset()
  }, [debouncedSearch, roleFilter, reset])

  // Load users
  useEffect(() => {
    setLoading(true)
    setError(null)
    const params = { page, size }
    if (debouncedSearch) {
      params.keyword = debouncedSearch
    }
    const role = ROLE_MAP[roleFilter]
    if (role) params.role = role

    userApi.list(params)
      .then(res => {
        const data = res.data || res
        setUsers(data.list || data.records || [])
        setTotal(data.total || data.totalItems || 0)
      })
      .catch(err => {
        setError(err.response?.data?.message || err.message || '加载失败')
        setUsers([])
      })
      .finally(() => setLoading(false))
  }, [page, size, debouncedSearch, roleFilter])

  // 禁用/启用
  const handleToggleStatus = useCallback(async (user) => {
    const isActive = user.status === 'ACTIVE'
    const action = isActive ? '禁用' : '启用'
    if (!window.confirm(`确定要${action}用户「${user.nickname || user.realName || user.id}」吗？`)) return
    try {
      await userApi.updateStatus(user.id, { status: isActive ? 'DISABLED' : 'ACTIVE' })
      // 刷新列表
      setLoading(true)
      const params = { page, size }
      if (debouncedSearch) params.keyword = debouncedSearch
      const role = ROLE_MAP[roleFilter]
      if (role) params.role = role
      userApi.list(params)
        .then(res => {
          const data = res.data || res
          setUsers(data.list || data.records || [])
          setTotal(data.total || data.totalItems || 0)
        })
        .finally(() => setLoading(false))
    } catch (err) {
      window.alert('操作失败: ' + (err.response?.data?.message || err.message))
    }
  }, [page, size, debouncedSearch, roleFilter])

  // 分页计算
  const start = total === 0 ? 0 : (page - 1) * size + 1
  const end = Math.min(page * size, total)
  const pageSizeOptions = [20, 50, 100]

  // 生成页码按钮
  const getPageNumbers = () => {
    if (totalPages <= 7) return Array.from({ length: totalPages }, (_, i) => i + 1)
    const pages = []
    if (page <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i)
      pages.push('...')
      pages.push(totalPages)
    } else if (page >= totalPages - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = totalPages - 4; i <= totalPages; i++) pages.push(i)
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = page - 1; i <= page + 1; i++) pages.push(i)
      pages.push('...')
      pages.push(totalPages)
    }
    return pages
  }

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">用户管理</h2>
        <p className="text-[12px] text-gray-500 mt-0.5">维护平台各类型业务系统主体用户、角色权限与余额流水建档。</p>
      </div>

      {/* Search & Filters */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 flex-wrap">
          {/* Search */}
          <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs">
            <Search className="w-3.5 h-3.5 text-gray-400" />
            <input
              type="text"
              placeholder="搜索用户ID、客户姓名、或手机号"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="bg-transparent outline-none text-gray-700 placeholder:text-gray-400 w-56"
            />
          </div>
          {/* Role filter */}
          <div className="relative">
            <button
              onClick={() => setShowRoleDropdown(!showRoleDropdown)}
              className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors"
            >
              {roleFilter}
              <ChevronDown className="w-3 h-3" />
            </button>
            {showRoleDropdown && (
              <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg py-1 z-10 min-w-[140px]">
                {ROLE_OPTIONS.map((opt) => (
                  <button
                    key={opt}
                    onClick={() => { setRoleFilter(opt); setShowRoleDropdown(false) }}
                    className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50 transition-colors ${roleFilter === opt ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
                  >
                    {opt}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
        <button className="flex items-center gap-1.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold px-4 py-2 rounded-xl transition-colors">
          <Plus className="w-3.5 h-3.5" />
          提报建档新客群
        </button>
      </div>

      {/* Table */}
      {loading ? (
        <SkeletonTable rows={5} cols={9} />
      ) : error ? (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center text-red-500 text-sm">
          {error}
        </div>
      ) : (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-xs">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">姓名</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">角色</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">联系方式</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">会员到期</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">登录安全</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">通知设置</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">车源数据</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">交易</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">状态</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">操作</th>
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? (
                  <tr>
                    <td colSpan={10} className="px-4 py-8 text-center text-gray-400 text-sm">暂无数据</td>
                  </tr>
                ) : (
                  users.map((u) => (
                    <tr key={u.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors cursor-pointer">
                      {/* 姓名 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <div className="font-medium text-gray-900">{u.nickname || u.realName || '-'}</div>
                        <div className="text-[11px] text-gray-400 mt-0.5">
                          注册: <span className="text-gray-500">{u.createdAt ? formatDate(u.createdAt) : '-'}</span>
                        </div>
                        <div className="text-[11px] text-gray-400">
                          ID: <span className="text-gray-500 font-mono">{u.id}</span>
                        </div>
                      </td>
                      {/* 角色 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium border ${ROLE_COLOR_MAP[u.userRole] || 'bg-gray-50 text-gray-600 border-gray-200'}`}>
                          {u.userRole === 'PERSONAL' ? '个人用户' : 
                           u.userRole === 'SHOP' ? '车行用户' : 
                           u.userRole === 'ADMIN' ? '系统管理员' : 
                           u.userRole === 'DEVELOPER' ? '开发人员' : u.userRole || '-'}
                        </span>
                      </td>
                      {/* 联系方式 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <div className="font-mono text-gray-700">{maskPhone(u.phone)}</div>
                        <div className="text-[11px] text-gray-400 mt-0.5">
                          微信 <span className="text-gray-500">{u.wechatId || '-'}</span>
                        </div>
                      </td>
                      {/* 会员到期 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        {formatMemberExpire(u.memberExpireAt)}
                      </td>
                      {/* 登录安全 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <LoginFailBadge count={u.loginFailCount} lockedUntil={u.lockedUntil} />
                      </td>
                      {/* 通知设置 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        {u.notificationSettings ? (
                          <span className="inline-flex items-center gap-1 px-1.5 py-0.5 rounded text-[10px] font-medium bg-green-50 text-green-600 border border-green-200">
                            ✓ 已配置
                          </span>
                        ) : (
                          <span className="text-gray-400 text-[11px]">未配置</span>
                        )}
                      </td>
                      {/* 车源数据 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <div className="flex gap-3 text-[11px]">
                          <span><span className="text-gray-400">在售:</span> <span className="text-gray-700 font-medium">{u.onSaleCount ?? 0}</span></span>
                          <span><span className="text-gray-400">下架:</span> <span className="text-gray-700 font-medium">{u.offShelfCount ?? 0}</span></span>
                          <span><span className="text-gray-400">求购:</span> <span className="text-gray-700 font-medium">{u.purchaseCount ?? 0}</span></span>
                        </div>
                      </td>
                      {/* 交易 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <div>
                          <span className="font-mono font-medium">{u.dealCount ?? 0}</span>
                          <span className="text-gray-400"> 笔交易</span>
                        </div>
                        <div className="text-[11px] text-gray-400">
                          ￥<span className="text-gray-600 font-medium">{(u.tradeAmount ?? 0) / 10000}</span>w 总额
                        </div>
                      </td>
                      {/* 状态 */}
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${getStatusColor(u.status)}`}>
                          {getStatusText(u.status)}
                        </span>
                      </td>
                      {/* 操作 */}
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-1">
                          <button className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-gray-600 hover:bg-gray-100 transition-colors">
                            <Edit2 className="w-3 h-3" />
                            编辑
                          </button>
                          <button className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors">
                            <Shield className="w-3 h-3" />
                            调保
                          </button>
                          <button
                            onClick={() => handleToggleStatus(u)}
                            className={`flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] transition-colors ${
                              u.status === 'ACTIVE'
                                ? 'text-red-500 hover:bg-red-50'
                                : 'text-emerald-500 hover:bg-emerald-50'
                            }`}
                          >
                            {u.status === 'ACTIVE' ? <Ban className="w-3 h-3" /> : <CheckCircle className="w-3 h-3" />}
                            {u.status === 'ACTIVE' ? '禁用' : '启用'}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
          {/* Pagination */}
          <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
            <div className="flex items-center gap-3 text-xs text-gray-500">
              <span>每页行数:</span>
              <div className="relative">
                <button
                  onClick={() => setShowPageSizeDropdown(!showPageSizeDropdown)}
                  className="flex items-center gap-1 px-2 py-1 border border-gray-200 rounded-lg bg-white hover:bg-gray-50 transition-colors"
                >
                  {size}
                  <ChevronDown className="w-3 h-3" />
                </button>
                {showPageSizeDropdown && (
                  <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg py-1 z-10">
                    {pageSizeOptions.map((opt) => (
                      <button
                        key={opt}
                        onClick={() => { setSize(opt); setShowPageSizeDropdown(false) }}
                        className={`w-full text-left px-3 py-1 text-xs hover:bg-gray-50 transition-colors ${size === opt ? 'text-indigo-600 font-medium' : 'text-gray-600'}`}
                      >
                        {opt}
                      </button>
                    ))}
                  </div>
                )}
              </div>
              <span className="text-gray-400">显示 {start}-{end} 项，共 {total} 项</span>
            </div>
            <div className="flex items-center gap-1">
              <button
                onClick={() => setPage(page - 1)}
                disabled={page <= 1}
                className={`p-1.5 rounded-lg transition-colors ${page <= 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'}`}
              >
                <ChevronLeft className="w-3.5 h-3.5" />
              </button>
              {getPageNumbers().map((p, i) =>
                p === '...' ? (
                  <span key={`ellipsis-${i}`} className="px-1.5 py-1 text-gray-400 text-xs">...</span>
                ) : (
                  <button
                    key={p}
                    onClick={() => setPage(p)}
                    className={`px-2.5 py-1 rounded-lg text-xs font-medium transition-colors ${page === p ? 'bg-indigo-600 text-white' : 'text-gray-600 hover:bg-gray-100'}`}
                  >
                    {p}
                  </button>
                )
              )}
              <button
                onClick={() => setPage(page + 1)}
                disabled={page >= totalPages}
                className={`p-1.5 rounded-lg transition-colors ${page >= totalPages ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'}`}
              >
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
