import { useState, useEffect, useCallback, useRef } from 'react'
import { Search, ChevronDown, ChevronLeft, ChevronRight, Eye, Power, X, Download } from 'lucide-react'
import { shopApi } from '../../api/shop'
import { usePagination } from '../../hooks/usePagination'
import { SkeletonTable } from '../../components/Skeleton'
import { formatDate, maskPhone, getStatusColor, getStatusText } from '../../utils/format'
import { exportFile } from '../../utils/export'

const STATUS_OPTIONS = ['全部', 'ACTIVE', 'FROZEN']

export default function ShopManage() {
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('全部')
  const [showStatusDropdown, setShowStatusDropdown] = useState(false)
  const [showPageSizeDropdown, setShowPageSizeDropdown] = useState(false)
  const [shops, setShops] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [detailShop, setDetailShop] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [toggleTarget, setToggleTarget] = useState(null)

  const pagination = usePagination(10)
  const { page, size, total, totalPages, setPage, setSize, setTotal, reset } = pagination

  const debounceRef = useRef(null)
  const [debouncedSearch, setDebouncedSearch] = useState('')

  // Debounce search
  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current)
    debounceRef.current = setTimeout(() => setDebouncedSearch(search), 300)
    return () => clearTimeout(debounceRef.current)
  }, [search])

  // Reset page on filter change
  useEffect(() => { reset() }, [debouncedSearch, statusFilter, reset])

  // Load shops
  const fetchShops = useCallback(() => {
    setLoading(true)
    setError(null)
    const params = { page, size }
    if (debouncedSearch) params.keyword = debouncedSearch
    if (statusFilter !== '全部') params.status = statusFilter

    shopApi.list(params)
      .then(res => {
        const data = res.data || res
        setShops(data.list || data.records || [])
        setTotal(data.total || 0)
      })
      .catch(err => {
        setError(err.response?.data?.message || err.message || '加载失败')
        setShops([])
      })
      .finally(() => setLoading(false))
  }, [page, size, debouncedSearch, statusFilter])

  useEffect(() => { fetchShops() }, [fetchShops])

  // Detail modal
  const handleViewDetail = useCallback(async (shop) => {
    setDetailShop(null)
    setDetailLoading(true)
    try {
      const res = await shopApi.detail(shop.id)
      setDetailShop(res.data || res)
    } catch (err) {
      setDetailShop({ ...shop, members: [], stats: { dealCount: shop.dealCount || 0, onSaleCount: shop.onSaleCount || 0, viewCount: shop.viewCount || 0 } })
    } finally {
      setDetailLoading(false)
    }
  }, [])

  // Toggle status
  const handleToggleStatus = useCallback((shop) => {
    setToggleTarget(shop)
  }, [])

  const confirmToggle = useCallback(async () => {
    if (!toggleTarget) return
    const shop = toggleTarget
    const newStatus = shop.status === 'ACTIVE' ? 'FROZEN' : 'ACTIVE'
    const actionText = newStatus === 'FROZEN' ? '冻结' : '启用'
    try {
      await shopApi.updateStatus(shop.id, { status: newStatus })
      setToggleTarget(null)
      fetchShops()
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message))
    }
  }, [toggleTarget, fetchShops])

  // Export
  const handleExport = useCallback(async () => {
    try {
      const params = new URLSearchParams()
      if (debouncedSearch) params.append('keyword', debouncedSearch)
      if (statusFilter !== '全部') params.append('status', statusFilter)
      
      await exportFile(`/shops/export?${params.toString()}`, `车行列表_${new Date().toLocaleDateString()}.xlsx`)
    } catch (error) {
      console.error('导出失败:', error)
      alert('导出失败: ' + error.message)
    }
  }, [debouncedSearch, statusFilter])

  // Pagination
  const paginationStart = total === 0 ? 0 : (page - 1) * size + 1
  const paginationEnd = Math.min(page * size, total)
  const pageSizeOptions = [10, 20, 50]

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

  // Credit level badge color
  const creditColorMap = {
    AAA: 'bg-purple-100 text-purple-700',
    AA: 'bg-blue-100 text-blue-700',
    A: 'bg-green-100 text-green-700',
    B: 'bg-yellow-100 text-yellow-700',
    C: 'bg-red-100 text-red-700',
  }

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">车行管理</h2>
        <p className="text-[12px] text-gray-500 mt-0.5">监督全部入驻车行运营状态，进行准入冻结调整与资质合规监察。</p>
      </div>

      {/* Search & Filter */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3">
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs">
            <Search className="w-3.5 h-3.5 text-gray-400" />
            <input
              type="text"
              placeholder="搜索车行名称"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="bg-transparent outline-none text-gray-700 placeholder:text-gray-400 w-52"
            />
          </div>
          <div className="relative">
            <button
              onClick={() => setShowStatusDropdown(!showStatusDropdown)}
              className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors"
            >
              {statusFilter === '全部' ? '全部' : statusFilter === 'ACTIVE' ? 'ACTIVE' : 'FROZEN'}
              <ChevronDown className="w-3 h-3" />
            </button>
            {showStatusDropdown && (
              <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg py-1 z-10 min-w-[120px]">
                {STATUS_OPTIONS.map((opt) => (
                  <button
                    key={opt}
                    onClick={() => { setStatusFilter(opt); setShowStatusDropdown(false) }}
                    className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50 transition-colors ${statusFilter === opt ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
                  >
                    {opt}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
        <button
          onClick={handleExport}
          className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors"
        >
          <Download size={14} />
          导出
        </button>
      </div>

      {/* Table */}
      {loading ? (
        <SkeletonTable rows={5} cols={7} />
      ) : error ? (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center text-red-500 text-sm">{error}</div>
      ) : (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-xs">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">店名</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">车主</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">手机号</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">在售车辆</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">成交数</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">信用等级</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">状态</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">操作</th>
                </tr>
              </thead>
              <tbody>
                {shops.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="px-4 py-8 text-center text-gray-400 text-sm">暂无车行数据</td>
                  </tr>
                ) : (
                  shops.map((s) => (
                    <tr key={s.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                      <td className="px-4 py-3">
                        <div className="font-medium text-gray-900 text-sm">{s.shopName || s.name || '-'}</div>
                        <div className="text-[11px] text-gray-400 mt-0.5 font-mono">ID: {s.id}</div>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-800">{s.realName || s.ownerName || '-'}</td>
                      <td className="px-4 py-3 text-sm text-gray-800 font-mono">{maskPhone(s.phone)}</td>
                      <td className="px-4 py-3 text-sm text-gray-800 font-mono font-medium">{s.onSaleCount ?? '-'}</td>
                      <td className="px-4 py-3 text-sm text-gray-800 font-mono font-medium">{s.dealCount ?? '-'}</td>
                      <td className="px-4 py-3">
                        <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium font-mono ${creditColorMap[s.creditLevel] || 'bg-gray-100 text-gray-600'}`}>
                          {s.creditLevel || '-'}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${getStatusColor(s.status)}`}>
                          {getStatusText(s.status)}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => handleViewDetail(s)}
                            className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors"
                          >
                            <Eye className="w-3 h-3" />
                            查看
                          </button>
                          <button
                            onClick={() => handleToggleStatus(s)}
                            className={`flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] transition-colors ${
                              s.status === 'ACTIVE' ? 'text-red-500 hover:bg-red-50' : 'text-emerald-500 hover:bg-emerald-50'
                            }`}
                          >
                            <Power className="w-3 h-3" />
                            {s.status === 'ACTIVE' ? '冻结' : '启用'}
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
          {total > 0 && (
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
                <span className="text-gray-400">显示 {paginationStart}-{paginationEnd} 项，共 {total} 项</span>
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
          )}
        </div>
      )}

      {/* Detail Modal */}
      {detailShop && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setDetailShop(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[85vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">车行详情</h3>
              <button onClick={() => setDetailShop(null)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            {detailLoading ? (
              <div className="p-6 space-y-4">
                {[1, 2, 3].map(i => (
                  <div key={i} className="h-12 bg-gray-100 rounded-lg animate-pulse" />
                ))}
              </div>
            ) : (
              <div className="p-5 space-y-5">
                {/* Basic Info */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">基本信息</h4>
                  <div className="grid grid-cols-2 gap-3 text-xs">
                    <div>
                      <span className="text-gray-400">店名：</span>
                      <span className="text-gray-800 font-medium">{detailShop.shopName || detailShop.name || '-'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">Logo：</span>
                      {detailShop.logo ? (
                        <img src={detailShop.logo} alt="logo" className="inline-block w-8 h-8 rounded object-cover" />
                      ) : (
                        <span className="text-gray-400">-</span>
                      )}
                    </div>
                    <div>
                      <span className="text-gray-400">信用等级：</span>
                      <span className={`inline-block px-1.5 py-0.5 rounded text-[11px] font-medium font-mono ${creditColorMap[detailShop.creditLevel] || 'bg-gray-100 text-gray-600'}`}>
                        {detailShop.creditLevel || '-'}
                      </span>
                    </div>
                    <div>
                      <span className="text-gray-400">描述：</span>
                      <span className="text-gray-800">{detailShop.description || detailShop.desc || '-'}</span>
                    </div>
                  </div>
                </div>

                {/* Members */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">成员列表</h4>
                  {detailShop.members && detailShop.members.length > 0 ? (
                    <table className="w-full text-xs border border-gray-100 rounded-lg overflow-hidden">
                      <thead>
                        <tr className="bg-gray-50">
                          <th className="text-left px-3 py-2 font-medium text-gray-500">昵称</th>
                          <th className="text-left px-3 py-2 font-medium text-gray-500">角色</th>
                          <th className="text-left px-3 py-2 font-medium text-gray-500">加入时间</th>
                        </tr>
                      </thead>
                      <tbody>
                        {detailShop.members.map((m, i) => (
                          <tr key={m.id || i} className="border-t border-gray-50">
                            <td className="px-3 py-2 text-gray-800">{m.nickname || m.name || '-'}</td>
                            <td className="px-3 py-2 text-gray-600">{m.role || '-'}</td>
                            <td className="px-3 py-2 text-gray-600">{m.joinTime ? formatDate(m.joinTime) : '-'}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  ) : (
                    <p className="text-xs text-gray-400">暂无成员数据</p>
                  )}
                </div>

                {/* Statistics */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">统计数据</h4>
                  <div className="grid grid-cols-3 gap-3 text-xs">
                    <div className="bg-gray-50 rounded-xl p-3 text-center">
                      <div className="text-gray-400">成交数</div>
                      <div className="text-lg font-bold text-gray-900 font-mono">{detailShop.dealCount ?? detailShop.stats?.dealCount ?? 0}</div>
                    </div>
                    <div className="bg-gray-50 rounded-xl p-3 text-center">
                      <div className="text-gray-400">在售车辆数</div>
                      <div className="text-lg font-bold text-gray-900 font-mono">{detailShop.onSaleCount ?? detailShop.stats?.onSaleCount ?? 0}</div>
                    </div>
                    <div className="bg-gray-50 rounded-xl p-3 text-center">
                      <div className="text-gray-400">浏览量</div>
                      <div className="text-lg font-bold text-gray-900 font-mono">{detailShop.viewCount ?? detailShop.stats?.viewCount ?? 0}</div>
                    </div>
                  </div>
                </div>
              </div>
            )}
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setDetailShop(null)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                关闭
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Toggle Confirm Modal */}
      {toggleTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setToggleTarget(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="p-5">
              <h3 className="text-base font-bold text-gray-900 mb-2">确认操作</h3>
              <p className="text-sm text-gray-600">
                确定要{toggleTarget.status === 'ACTIVE' ? '冻结' : '启用'}车行「{toggleTarget.shopName || toggleTarget.name}」吗？
              </p>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setToggleTarget(null)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                取消
              </button>
              <button
                onClick={confirmToggle}
                className={`px-4 py-2 text-xs text-white rounded-lg transition-colors ${
                  toggleTarget.status === 'ACTIVE' ? 'bg-red-500 hover:bg-red-600' : 'bg-emerald-500 hover:bg-emerald-600'
                }`}
              >
                确定
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
