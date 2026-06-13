import { useState, useEffect, useCallback, useRef } from 'react'
import { Search, ChevronDown, ChevronLeft, ChevronRight, CheckCircle, XCircle, Eye, X } from 'lucide-react'
import { shopAuditApi } from '../../api/shopAudit'
import { usePagination } from '../../hooks/usePagination'
import { SkeletonTable } from '../../components/Skeleton'
import { formatDate, maskPhone, getStatusColor, getStatusText } from '../../utils/format'

const STATUS_OPTIONS = ['全部', 'PENDING', 'CERTIFIED', 'REJECTED']

export default function ShopAudit() {
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('全部')
  const [showStatusDropdown, setShowStatusDropdown] = useState(false)
  const [showPageSizeDropdown, setShowPageSizeDropdown] = useState(false)
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [pendingCount, setPendingCount] = useState(0)
  const [selectedIds, setSelectedIds] = useState([])
  const [rejectTarget, setRejectTarget] = useState(null)
  const [rejectReason, setRejectReason] = useState('')
  const [approveTarget, setApproveTarget] = useState(null)
  const [batchApproveVisible, setBatchApproveVisible] = useState(false)
  const [detailReview, setDetailReview] = useState(null)
  const [actionLoading, setActionLoading] = useState(false)

  const pagination = usePagination(10)
  const { page, size, total, totalPages, setPage, setSize, setTotal, reset } = pagination

  const debounceRef = useRef(null)
  const [debouncedSearch, setDebouncedSearch] = useState('')
  const pendingCountRef = useRef(null)

  // Debounce search
  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current)
    debounceRef.current = setTimeout(() => setDebouncedSearch(search), 300)
    return () => clearTimeout(debounceRef.current)
  }, [search])

  // Reset page on filter change
  useEffect(() => { reset(); setSelectedIds([]) }, [debouncedSearch, statusFilter, reset])

  // Load reviews
  const fetchReviews = useCallback(() => {
    setLoading(true)
    setError(null)
    const params = { page, size }
    if (debouncedSearch) params.keyword = debouncedSearch
    if (statusFilter !== '全部') params.status = statusFilter

    shopAuditApi.list(params)
      .then(res => {
        const data = res.data || res
        setReviews(data.list || data.records || [])
        setTotal(data.total || 0)
      })
      .catch(err => {
        setError(err.response?.data?.message || err.message || '加载失败')
        setReviews([])
      })
      .finally(() => setLoading(false))
  }, [page, size, debouncedSearch, statusFilter])

  useEffect(() => { fetchReviews() }, [fetchReviews])

  // Pending count polling every 30s
  useEffect(() => {
    const fetchCount = () => {
      shopAuditApi.pendingCount()
        .then(res => setPendingCount(res?.count ?? 0))
        .catch(() => {})
    }
    fetchCount()
    pendingCountRef.current = setInterval(fetchCount, 30000)
    return () => { if (pendingCountRef.current) clearInterval(pendingCountRef.current) }
  }, [])

  // Selection
  const handleSelectAll = (e) => {
    if (e.target.checked) {
      setSelectedIds(reviews.filter(r => r.status === 'PENDING').map(r => r.id))
    } else {
      setSelectedIds([])
    }
  }

  const handleSelectOne = (id) => {
    setSelectedIds(prev =>
      prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]
    )
  }

  // Approve single
  const handleApprove = (review) => setApproveTarget(review)

  const confirmApprove = useCallback(async () => {
    if (!approveTarget) return
    setActionLoading(true)
    try {
      await shopAuditApi.approve(approveTarget.id)
      setApproveTarget(null)
      fetchReviews()
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message))
    } finally {
      setActionLoading(false)
    }
  }, [approveTarget, fetchReviews])

  // Batch approve
  const handleBatchApprove = () => setBatchApproveVisible(true)

  const confirmBatchApprove = useCallback(async () => {
    setActionLoading(true)
    try {
      await shopAuditApi.batchApprove({ ids: selectedIds })
      setBatchApproveVisible(false)
      setSelectedIds([])
      fetchReviews()
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message))
    } finally {
      setActionLoading(false)
    }
  }, [selectedIds, fetchReviews])

  // Reject
  const handleReject = (review) => {
    setRejectTarget(review)
    setRejectReason('')
  }

  const confirmReject = useCallback(async () => {
    if (!rejectTarget || !rejectReason.trim()) return
    setActionLoading(true)
    try {
      await shopAuditApi.reject(rejectTarget.id, { reason: rejectReason.trim() })
      setRejectTarget(null)
      setRejectReason('')
      fetchReviews()
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message))
    } finally {
      setActionLoading(false)
    }
  }, [rejectTarget, rejectReason, fetchReviews])

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

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">车行注册审核</h2>
        <p className="text-[12px] text-gray-500 mt-0.5">审查和校验待入驻车行的申报注册材料，进行营业资质与经营合规评估。</p>
      </div>

      {/* Pending Count Card */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-gradient-to-br from-amber-500 to-orange-600 text-white p-5 rounded-2xl shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs text-amber-100 font-medium">待审核数量</span>
            <div className="w-8 h-8 bg-white/20 rounded-lg flex items-center justify-center text-lg font-bold">{pendingCount}</div>
          </div>
          <div className="mt-2 text-2xl font-bold font-mono">{pendingCount}</div>
          <p className="text-[11px] text-amber-100 mt-1">每 30 秒自动刷新</p>
        </div>
      </div>

      {/* Search & Filter & Batch Actions */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 flex-wrap">
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
              {statusFilter === '全部' ? '全部' : statusFilter}
              <ChevronDown className="w-3 h-3" />
            </button>
            {showStatusDropdown && (
              <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg py-1 z-10 min-w-[140px]">
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
        {selectedIds.length > 0 && (
          <button
            onClick={handleBatchApprove}
            className="flex items-center gap-1.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold px-4 py-2 rounded-xl transition-colors"
          >
            <CheckCircle className="w-3.5 h-3.5" />
            批量通过 ({selectedIds.length})
          </button>
        )}
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
                  <th className="text-left px-4 py-3 w-10">
                    <input
                      type="checkbox"
                      onChange={handleSelectAll}
                      checked={selectedIds.length > 0 && reviews.filter(r => r.status === 'PENDING').every(r => selectedIds.includes(r.id))}
                      className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                    />
                  </th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">申请人</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">车主</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">手机号</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">申请时间</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">状态</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">操作</th>
                </tr>
              </thead>
              <tbody>
                {reviews.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="px-4 py-8 text-center text-gray-400 text-sm">暂无审核数据</td>
                  </tr>
                ) : (
                  reviews.map((r) => {
                    const isPending = r.status === 'PENDING'
                    const isSelected = selectedIds.includes(r.id)
                    return (
                      <tr key={r.id} className={`border-b border-gray-50 hover:bg-gray-50/50 transition-colors ${isSelected ? 'bg-indigo-50/30' : ''}`}>
                        <td className="px-4 py-3">
                          {isPending && (
                            <input
                              type="checkbox"
                              checked={isSelected}
                              onChange={() => handleSelectOne(r.id)}
                              className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                            />
                          )}
                        </td>
                        <td className="px-4 py-3">
                          <div className="font-medium text-gray-900 text-sm">{r.shopName || r.name || '-'}</div>
                          <div className="text-[11px] text-gray-400 font-mono mt-0.5">ID: {r.id}</div>
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-800">{r.realName || r.ownerName || '-'}</td>
                        <td className="px-4 py-3 text-sm text-gray-800 font-mono">{maskPhone(r.phone)}</td>
                        <td className="px-4 py-3 text-sm text-gray-600">{r.createdAt || r.applyTime ? formatDate(r.createdAt || r.applyTime) : '-'}</td>
                        <td className="px-4 py-3">
                          <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${getStatusColor(r.status)}`}>
                            {getStatusText(r.status)}
                          </span>
                        </td>
                        <td className="px-4 py-3">
                          <div className="flex items-center gap-1">
                            <button
                              onClick={() => setDetailReview(r)}
                              className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors"
                            >
                              <Eye className="w-3 h-3" />
                              查看
                            </button>
                            {isPending && (
                              <>
                                <button
                                  onClick={() => handleApprove(r)}
                                  className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-emerald-600 hover:bg-emerald-50 transition-colors"
                                >
                                  <CheckCircle className="w-3 h-3" />
                                  通过
                                </button>
                                <button
                                  onClick={() => handleReject(r)}
                                  className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-red-500 hover:bg-red-50 transition-colors"
                                >
                                  <XCircle className="w-3 h-3" />
                                  拒绝
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
      {detailReview && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setDetailReview(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg max-h-[85vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">申请详情</h3>
              <button onClick={() => setDetailReview(null)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4 text-sm">
              <div className="grid grid-cols-2 gap-3 text-xs">
                <div>
                  <span className="text-gray-400">车行名：</span>
                  <span className="text-gray-800 font-medium">{detailReview.shopName || detailReview.name || '-'}</span>
                </div>
                <div>
                  <span className="text-gray-400">申请人：</span>
                  <span className="text-gray-800 font-medium">{detailReview.realName || detailReview.applicantName || '-'}</span>
                </div>
                <div>
                  <span className="text-gray-400">手机号：</span>
                  <span className="text-gray-800 font-mono">{maskPhone(detailReview.phone)}</span>
                </div>
                <div>
                  <span className="text-gray-400">申请时间：</span>
                  <span className="text-gray-800">{detailReview.createdAt || detailReview.applyTime ? formatDate(detailReview.createdAt || detailReview.applyTime) : '-'}</span>
                </div>
                <div>
                  <span className="text-gray-400">状态：</span>
                  <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${getStatusColor(detailReview.status)}`}>
                    {getStatusText(detailReview.status)}
                  </span>
                </div>
                <div>
                  <span className="text-gray-400">审核ID：</span>
                  <span className="text-gray-800 font-mono">{detailReview.id}</span>
                </div>
                {detailReview.description && (
                  <div className="col-span-2">
                    <span className="text-gray-400">描述：</span>
                    <span className="text-gray-800">{detailReview.description}</span>
                  </div>
                )}
                {detailReview.rejectReason && (
                  <div className="col-span-2">
                    <span className="text-gray-400">拒绝原因：</span>
                    <span className="text-red-600">{detailReview.rejectReason}</span>
                  </div>
                )}
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setDetailReview(null)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                关闭
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Approve Confirm Modal */}
      {approveTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setApproveTarget(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="p-5">
              <h3 className="text-base font-bold text-gray-900 mb-2">确认通过</h3>
              <p className="text-sm text-gray-600">确认通过此车行审核？</p>
              <p className="text-xs text-gray-400 mt-1">{approveTarget.shopName || approveTarget.name}</p>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={() => setApproveTarget(null)}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={confirmApprove}
                disabled={actionLoading}
                className="px-4 py-2 text-xs text-white bg-emerald-500 hover:bg-emerald-600 rounded-lg transition-colors disabled:opacity-50"
              >
                {actionLoading ? '处理中...' : '确认通过'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Batch Approve Confirm Modal */}
      {batchApproveVisible && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setBatchApproveVisible(false)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="p-5">
              <h3 className="text-base font-bold text-gray-900 mb-2">批量通过确认</h3>
              <p className="text-sm text-gray-600">确认批量通过 {selectedIds.length} 条审核？</p>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={() => setBatchApproveVisible(false)}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={confirmBatchApprove}
                disabled={actionLoading}
                className="px-4 py-2 text-xs text-white bg-emerald-500 hover:bg-emerald-600 rounded-lg transition-colors disabled:opacity-50"
              >
                {actionLoading ? '处理中...' : '确认通过'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Reject Modal */}
      {rejectTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setRejectTarget(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">拒绝审核</h3>
              <button onClick={() => setRejectTarget(null)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-3">
              <p className="text-sm text-gray-600">拒绝车行「{rejectTarget.shopName || rejectTarget.name}」的审核申请</p>
              <div>
                <label className="block text-xs text-gray-500 mb-1">拒绝原因</label>
                <textarea
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
              placeholder="请输入拒绝原因..."
              rows={4}
              className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-red-300 focus:ring-1 focus:ring-red-300 resize-none"
            />
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={() => setRejectTarget(null)}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={confirmReject}
                disabled={!rejectReason.trim() || actionLoading}
                className="px-4 py-2 text-xs text-white bg-red-500 hover:bg-red-600 rounded-lg transition-colors disabled:opacity-50"
              >
                {actionLoading ? '处理中...' : '确认拒绝'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
