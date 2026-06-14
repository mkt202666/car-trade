import { useState, useEffect, useCallback, useRef } from 'react'
import { Search, ChevronDown, ChevronLeft, ChevronRight, X, Eye } from 'lucide-react'
import { purchaseApi } from '../../api/purchase'
import { usePagination } from '../../hooks/usePagination'
import { SkeletonTable } from '../../components/Skeleton'

const BRAND_OPTIONS = ['全部', '宝马', '奔驰', '奥迪', '大众', '丰田', '本田']

const STATUS_OPTIONS = [
  { label: '全部', value: '' },
  { label: '求购中', value: 'OPEN' },
  { label: '已匹配', value: 'MATCHED' },
  { label: '已关闭', value: 'CLOSED' },
]

const STATUS_CONFIG = {
  OPEN:    { label: '求购中', className: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20' },
  MATCHED: { label: '已匹配', className: 'bg-blue-500/10 text-blue-400 border-blue-500/20' },
  CLOSED:  { label: '已关闭', className: 'bg-gray-500/10 text-gray-400 border-gray-500/20' },
}

function formatBudget(minPrice, maxPrice) {
  if (minPrice != null && maxPrice != null) {
    return `¥${Number(minPrice).toLocaleString()}-${Number(maxPrice).toLocaleString()}`
  }
  if (minPrice != null) {
    return `¥${Number(minPrice).toLocaleString()}起`
  }
  if (maxPrice != null) {
    return `≤¥${Number(maxPrice).toLocaleString()}`
  }
  return '-'
}

function formatYearRange(yearMin, yearMax) {
  if (yearMin && yearMax) return `${yearMin}-${yearMax}`
  if (yearMin) return `${yearMin}年起`
  if (yearMax) return `${yearMax}年及以下`
  return '-'
}

export default function PurchaseManage() {
  const [search, setSearch] = useState('')
  const [brand, setBrand] = useState('全部')
  const [statusFilter, setStatusFilter] = useState('')
  const [showBrandDropdown, setShowBrandDropdown] = useState(false)
  const [showStatusDropdown, setShowStatusDropdown] = useState(false)
  const [showPageSizeDropdown, setShowPageSizeDropdown] = useState(false)
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // Detail modal
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailData, setDetailData] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)

  const pagination = usePagination(20)
  const { page, size, total, totalPages, setPage, setSize, setTotal, reset } = pagination

  // Debounce
  const debounceRef = useRef(null)
  const [debouncedSearch, setDebouncedSearch] = useState('')

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current)
    debounceRef.current = setTimeout(() => setDebouncedSearch(search), 300)
    return () => clearTimeout(debounceRef.current)
  }, [search])

  // Reset page when filters change
  useEffect(() => {
    reset()
  }, [debouncedSearch, brand, statusFilter, reset])

  // Load list
  useEffect(() => {
    setLoading(true)
    setError(null)
    const params = { page, size }
    if (debouncedSearch) params.keyword = debouncedSearch
    if (brand !== '全部') params.brand = brand
    if (statusFilter) params.status = statusFilter

    purchaseApi.list(params)
      .then(data => {
        setRecords(data.list || data.records || [])
        setTotal(data.total || data.totalItems || 0)
      })
      .catch(err => {
        setError(err.response?.data?.message || err.message || '加载失败')
        setRecords([])
      })
      .finally(() => setLoading(false))
  }, [page, size, debouncedSearch, brand, statusFilter])

  // Open detail modal
  const handleViewDetail = useCallback(async (record) => {
    setDetailOpen(true)
    setDetailData(null)
    setDetailLoading(true)
    try {
      const data = await purchaseApi.detail(record.id)
      setDetailData(data)
    } catch (err) {
      setDetailData({ ...record, remark: record.remark || '-' })
    } finally {
      setDetailLoading(false)
    }
  }, [])

  // Close detail modal
  const closeDetail = useCallback(() => {
    setDetailOpen(false)
    setDetailData(null)
  }, [])

  // Close purchase request
  const handleClose = useCallback(async (id) => {
    if (!confirm('确定要关闭这条求购需求吗？')) return
    try {
      await purchaseApi.close(id, { reason: '管理员关闭' })
      alert('已关闭')
      // Reload list
      const params = { page, size }
      if (debouncedSearch) params.keyword = debouncedSearch
      if (brand !== '全部') params.brand = brand
      if (statusFilter) params.status = statusFilter
      const data = await purchaseApi.list(params)
      setRecords(data.list || data.records || [])
      setTotal(data.total || data.totalItems || 0)
    } catch (err) {
      alert(err.response?.data?.message || err.message || '关闭失败')
    }
  }, [page, size, debouncedSearch, brand, statusFilter])

  // Match cars for purchase request
  const handleMatch = useCallback(async (id) => {
    if (!confirm('确定要为这条求购需求匹配车源吗？')) return
    try {
      await purchaseApi.match(id)
      alert('匹配成功')
      // Reload list
      const params = { page, size }
      if (debouncedSearch) params.keyword = debouncedSearch
      if (brand !== '全部') params.brand = brand
      if (statusFilter) params.status = statusFilter
      const data = await purchaseApi.list(params)
      setRecords(data.list || data.records || [])
      setTotal(data.total || data.totalItems || 0)
    } catch (err) {
      alert(err.response?.data?.message || err.message || '匹配失败')
    }
  }, [page, size, debouncedSearch, brand, statusFilter])

  // Pagination calcs
  const start = total === 0 ? 0 : (page - 1) * size + 1
  const end = Math.min(page * size, total)
  const pageSizeOptions = [20, 50, 100]

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
        <h2 className="text-[18px] font-bold text-gray-100 tracking-tight">求购管理</h2>
        <p className="text-[12px] text-gray-400 mt-0.5">管理用户求购意向，跟踪匹配状态与处理进度。</p>
      </div>

      {/* Search & Filters */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 flex-wrap">
          {/* Search */}
          <div className="flex items-center gap-2 bg-gray-800 border border-gray-700 rounded-xl px-3 py-2 text-xs">
            <Search className="w-3.5 h-3.5 text-gray-500" />
            <input
              type="text"
              placeholder="搜索车型名称"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="bg-transparent outline-none text-gray-200 placeholder:text-gray-500 w-56"
            />
          </div>

          {/* Brand filter */}
          <div className="relative">
            <button
              onClick={() => { setShowBrandDropdown(!showBrandDropdown); setShowStatusDropdown(false) }}
              className="flex items-center gap-1.5 bg-gray-800 border border-gray-700 rounded-xl px-3 py-2 text-xs text-gray-300 hover:bg-gray-700 transition-colors"
            >
              {brand}
              <ChevronDown className="w-3 h-3" />
            </button>
            {showBrandDropdown && (
              <div className="absolute top-full left-0 mt-1 bg-gray-800 border border-gray-700 rounded-xl shadow-lg py-1 z-20 min-w-[120px]">
                {BRAND_OPTIONS.map((opt) => (
                  <button
                    key={opt}
                    onClick={() => { setBrand(opt); setShowBrandDropdown(false) }}
                    className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-700 transition-colors ${brand === opt ? 'text-indigo-400 font-medium bg-gray-700/50' : 'text-gray-300'}`}
                  >
                    {opt}
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Status filter */}
          <div className="relative">
            <button
              onClick={() => { setShowStatusDropdown(!showStatusDropdown); setShowBrandDropdown(false) }}
              className="flex items-center gap-1.5 bg-gray-800 border border-gray-700 rounded-xl px-3 py-2 text-xs text-gray-300 hover:bg-gray-700 transition-colors"
            >
              {STATUS_OPTIONS.find(o => o.value === statusFilter)?.label || '全部'}
              <ChevronDown className="w-3 h-3" />
            </button>
            {showStatusDropdown && (
              <div className="absolute top-full left-0 mt-1 bg-gray-800 border border-gray-700 rounded-xl shadow-lg py-1 z-20 min-w-[110px]">
                {STATUS_OPTIONS.map((opt) => (
                  <button
                    key={opt.value}
                    onClick={() => { setStatusFilter(opt.value); setShowStatusDropdown(false) }}
                    className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-700 transition-colors ${statusFilter === opt.value ? 'text-indigo-400 font-medium bg-gray-700/50' : 'text-gray-300'}`}
                  >
                    {opt.label}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Table */}
      {loading ? (
        <SkeletonTable rows={5} cols={8} />
      ) : error ? (
        <div className="bg-gray-800 rounded-2xl border border-gray-700 p-8 text-center text-red-400 text-sm">
          {error}
        </div>
      ) : (
        <div className="bg-gray-800 rounded-2xl border border-gray-700 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-xs">
              <thead>
                <tr className="bg-gray-700/50 border-b border-gray-700">
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">发布人</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">车型</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">预算范围</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">年份范围</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">城市</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">匹配数</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">状态</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">操作</th>
                </tr>
              </thead>
              <tbody>
                {records.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="px-4 py-12 text-center text-gray-500 text-sm">
                      <div className="space-y-2">
                        <Search className="w-8 h-8 mx-auto text-gray-600" />
                        <p>暂无求购数据</p>
                      </div>
                    </td>
                  </tr>
                ) : (
                  records.map((record) => {
                    const statusCfg = STATUS_CONFIG[record.status] || { label: record.status || '-', className: 'bg-gray-500/10 text-gray-400 border-gray-500/20' }
                    return (
                      <tr key={record.id} className="border-b border-gray-700/60 hover:bg-gray-750 hover:bg-gray-700/40 transition-colors">
                        <td className="px-4 py-3 text-sm text-gray-300">
                          <div className="font-medium text-gray-200">{record.userName || '-'}</div>
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-300">
                          <div className="text-gray-200">{record.modelName || '-'}</div>
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-300">
                          <span className="font-mono text-gray-200 font-medium">{formatBudget(record.minPrice, record.maxPrice)}</span>
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-300">
                          {formatYearRange(record.yearMin, record.yearMax)}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-300">
                          {record.cityName || '-'}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-300">
                          <span className="font-mono font-medium text-gray-200">{record.matchCount ?? 0}</span>
                        </td>
                        <td className="px-4 py-3 text-sm">
                          <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium border ${statusCfg.className}`}>
                            {statusCfg.label}
                          </span>
                        </td>
                        <td className="px-4 py-3">
                          <div className="flex items-center gap-1">
                            <button
                              onClick={() => handleViewDetail(record)}
                              className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-indigo-400 hover:bg-indigo-500/10 transition-colors"
                            >
                              <Eye className="w-3 h-3" />
                              查看
                            </button>
                            {record.status === 'OPEN' && (
                              <>
                                <button
                                  onClick={() => handleMatch(record.id)}
                                  className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-emerald-400 hover:bg-emerald-500/10 transition-colors"
                                >
                                  匹配
                                </button>
                                <button
                                  onClick={() => handleClose(record.id)}
                                  className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-red-400 hover:bg-red-500/10 transition-colors"
                                >
                                  关闭
                                </button>
                              </>
                            )}
                          </div>
                        </td>
                      </tr>
                    )
                  })
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="flex items-center justify-between px-4 py-3 border-t border-gray-700">
            <div className="flex items-center gap-3 text-xs text-gray-400">
              <span>每页行数:</span>
              <div className="relative">
                <button
                  onClick={() => setShowPageSizeDropdown(!showPageSizeDropdown)}
                  className="flex items-center gap-1 px-2 py-1 border border-gray-700 rounded-lg bg-gray-800 hover:bg-gray-700 transition-colors text-gray-300"
                >
                  {size}
                  <ChevronDown className="w-3 h-3" />
                </button>
                {showPageSizeDropdown && (
                  <div className="absolute bottom-full left-0 mb-1 bg-gray-800 border border-gray-700 rounded-lg shadow-lg py-1 z-10">
                    {pageSizeOptions.map((opt) => (
                      <button
                        key={opt}
                        onClick={() => { setSize(opt); setShowPageSizeDropdown(false) }}
                        className={`w-full text-left px-3 py-1 text-xs hover:bg-gray-700 transition-colors ${size === opt ? 'text-indigo-400 font-medium' : 'text-gray-400'}`}
                      >
                        {opt}
                      </button>
                    ))}
                  </div>
                )}
              </div>
              <span className="text-gray-500">显示 {start}-{end} 项，共 {total} 项</span>
            </div>
            <div className="flex items-center gap-1">
              <button
                onClick={() => setPage(page - 1)}
                disabled={page <= 1}
                className={`p-1.5 rounded-lg transition-colors ${page <= 1 ? 'text-gray-700 cursor-not-allowed' : 'text-gray-400 hover:bg-gray-700'}`}
              >
                <ChevronLeft className="w-3.5 h-3.5" />
              </button>
              {getPageNumbers().map((p, i) =>
                p === '...' ? (
                  <span key={`ellipsis-${i}`} className="px-1.5 py-1 text-gray-600 text-xs">...</span>
                ) : (
                  <button
                    key={p}
                    onClick={() => setPage(p)}
                    className={`px-2.5 py-1 rounded-lg text-xs font-medium transition-colors ${page === p ? 'bg-indigo-600 text-white' : 'text-gray-400 hover:bg-gray-700'}`}
                  >
                    {p}
                  </button>
                )
              )}
              <button
                onClick={() => setPage(page + 1)}
                disabled={page >= totalPages}
                className={`p-1.5 rounded-lg transition-colors ${page >= totalPages ? 'text-gray-700 cursor-not-allowed' : 'text-gray-400 hover:bg-gray-700'}`}
              >
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Detail Modal */}
      {detailOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm" onClick={closeDetail}>
          <div
            className="bg-gray-800 border border-gray-700 rounded-2xl shadow-2xl w-full max-w-lg mx-4 max-h-[85vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}
          >
            {/* Header */}
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-700">
              <h3 className="text-base font-bold text-gray-100">求购详情</h3>
              <button
                onClick={closeDetail}
                className="p-1.5 rounded-lg text-gray-500 hover:text-gray-300 hover:bg-gray-700 transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Body */}
            {detailLoading ? (
              <div className="px-6 py-8 space-y-4 animate-pulse">
                {Array.from({ length: 6 }).map((_, i) => (
                  <div key={i} className="space-y-2">
                    <div className="h-3 w-20 bg-gray-700 rounded" />
                    <div className="h-5 w-48 bg-gray-700 rounded" />
                  </div>
                ))}
              </div>
            ) : detailData ? (
              <div className="px-6 py-5 space-y-5">
                {/* 基本信息 */}
                <div>
                  <h4 className="text-[11px] font-bold text-gray-500 uppercase tracking-wider mb-3">基本信息</h4>
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <span className="text-gray-500 text-xs">发布人</span>
                      <p className="text-gray-200 mt-0.5">{detailData.userName || '-'}</p>
                    </div>
                    <div>
                      <span className="text-gray-500 text-xs">车型名称</span>
                      <p className="text-gray-200 mt-0.5">{detailData.modelName || '-'}</p>
                    </div>
                    <div>
                      <span className="text-gray-500 text-xs">品牌</span>
                      <p className="text-gray-200 mt-0.5">{detailData.brand || '-'}</p>
                    </div>
                    <div>
                      <span className="text-gray-500 text-xs">车系</span>
                      <p className="text-gray-200 mt-0.5">{detailData.series || '-'}</p>
                    </div>
                    <div>
                      <span className="text-gray-500 text-xs">预算范围</span>
                      <p className="text-gray-200 mt-0.5 font-mono font-medium">{formatBudget(detailData.minPrice, detailData.maxPrice)}</p>
                    </div>
                    <div>
                      <span className="text-gray-500 text-xs">年份范围</span>
                      <p className="text-gray-200 mt-0.5">{formatYearRange(detailData.yearMin, detailData.yearMax)}</p>
                    </div>
                    <div className="col-span-2">
                      <span className="text-gray-500 text-xs">城市</span>
                      <p className="text-gray-200 mt-0.5">{detailData.cityName || '-'}</p>
                    </div>
                  </div>
                </div>

                {/* 匹配信息 */}
                <div className="border-t border-gray-700 pt-4">
                  <h4 className="text-[11px] font-bold text-gray-500 uppercase tracking-wider mb-3">匹配信息</h4>
                  <div className="text-sm">
                    <span className="text-gray-500 text-xs">匹配数量</span>
                    <p className="text-gray-200 mt-0.5 font-mono text-lg font-semibold">{detailData.matchCount ?? 0}</p>
                  </div>
                </div>

                {/* 备注 */}
                <div className="border-t border-gray-700 pt-4">
                  <h4 className="text-[11px] font-bold text-gray-500 uppercase tracking-wider mb-3">备注</h4>
                  <p className="text-gray-300 text-sm leading-relaxed whitespace-pre-wrap">{detailData.remark || '无'}</p>
                </div>

                {/* 状态 */}
                <div className="border-t border-gray-700 pt-4">
                  <h4 className="text-[11px] font-bold text-gray-500 uppercase tracking-wider mb-3">状态</h4>
                  <div className="text-sm">
                    {(() => {
                      const cfg = STATUS_CONFIG[detailData.status]
                      if (cfg) {
                        return (
                          <span className={`inline-block px-3 py-1 rounded-full text-xs font-medium border ${cfg.className}`}>
                            {cfg.label}
                          </span>
                        )
                      }
                      return <span className="text-gray-400">{detailData.status || '-'}</span>
                    })()}
                  </div>
                </div>
              </div>
            ) : (
              <div className="px-6 py-8 text-center text-gray-500 text-sm">加载详情失败</div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
