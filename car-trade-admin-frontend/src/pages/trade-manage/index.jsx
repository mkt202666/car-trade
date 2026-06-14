import { useState, useEffect, useCallback, useRef } from 'react'
import { Search, ChevronDown, ChevronLeft, ChevronRight, Eye, X, Download, CheckCircle, XCircle, Scale, AlertTriangle } from 'lucide-react'
import { getOrderList, getOrderDetail, getOrderLogs, confirmOrder, forceCancelOrder, resolveDispute } from '../../api/trade'
import { usePagination } from '../../hooks/usePagination'
import { SkeletonTable } from '../../components/Skeleton'
import { formatDate, maskPhone } from '../../utils/format'
import { exportFile } from '../../utils/export'

const STATUS_OPTIONS = ['全部', 'PENDING_DEPOSIT', 'CONTRACT_DRAFT', 'CONTRACT_SIGNED', 'IN_TRANSIT', 'COMPLETED', 'CANCELLED', 'TERMINATED', 'DISPUTE']

const STATUS_MAP = {
  PENDING_DEPOSIT: { label: '待付保证金', color: 'bg-amber-100 text-amber-700' },
  CONTRACT_DRAFT: { label: '合同草拟', color: 'bg-yellow-100 text-yellow-700' },
  CONTRACT_SIGNED: { label: '合同已签', color: 'bg-emerald-100 text-emerald-700' },
  IN_TRANSIT: { label: '过户中', color: 'bg-blue-100 text-blue-700' },
  COMPLETED: { label: '已完成', color: 'bg-green-100 text-green-700' },
  CANCELLED: { label: '已取消', color: 'bg-gray-100 text-gray-500' },
  TERMINATED: { label: '已终止', color: 'bg-gray-200 text-gray-600' },
  DISPUTE: { label: '纠纷中', color: 'bg-red-100 text-red-700' },
}

function getStatusBadge(status) {
  const s = STATUS_MAP[status]
  if (!s) return <span className="inline-block px-2 py-0.5 rounded-full text-[11px] font-medium bg-gray-100 text-gray-600">{status || '-'}</span>
  return <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${s.color}`}>{s.label}</span>
}

export default function TradeManage() {
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('全部')
  const [showStatusDropdown, setShowStatusDropdown] = useState(false)
  const [showPageSizeDropdown, setShowPageSizeDropdown] = useState(false)
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [detailOrder, setDetailOrder] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [orderLogs, setOrderLogs] = useState([])

  // Admin operation states
  const [actionLoading, setActionLoading] = useState(false)
  const [showCancelModal, setShowCancelModal] = useState(null) // order id
  const [cancelReason, setCancelReason] = useState('')
  const [showDisputeModal, setShowDisputeModal] = useState(null) // order id
  const [disputeReason, setDisputeReason] = useState('')
  const [disputeResolution, setDisputeResolution] = useState('buyer')

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
  useEffect(() => { reset() }, [debouncedSearch, statusFilter, startDate, endDate, reset])

  // Load orders
  const fetchOrders = useCallback(() => {
    setLoading(true)
    setError(null)
    const params = { page, size }
    if (debouncedSearch) params.keyword = debouncedSearch
    if (statusFilter !== '全部') params.status = statusFilter
    if (startDate) params.startDate = startDate
    if (endDate) params.endDate = endDate

    getOrderList(params)
      .then(res => {
        const data = res.data || res
        setOrders(data.list || data.records || [])
        setTotal(data.total || 0)
      })
      .catch(err => {
        setError(err.response?.data?.message || err.message || '加载失败')
        setOrders([])
      })
      .finally(() => setLoading(false))
  }, [page, size, debouncedSearch, statusFilter, startDate, endDate])

  useEffect(() => { fetchOrders() }, [fetchOrders])

  // Detail modal
  const handleViewDetail = useCallback(async (order) => {
    setDetailOrder(null)
    setOrderLogs([])
    setDetailLoading(true)
    try {
      const [detailRes, logsRes] = await Promise.all([
        getOrderDetail(order.id),
        getOrderLogs(order.id),
      ])
      setDetailOrder(detailRes.data || detailRes)
      setOrderLogs(logsRes.data?.list || logsRes.data || logsRes || [])
    } catch (err) {
      setDetailOrder({ ...order })
      setOrderLogs([])
    } finally {
      setDetailLoading(false)
    }
  }, [])

  // Admin: Confirm order
  const handleConfirm = useCallback(async (orderId) => {
    if (!window.confirm('确认推进此订单到下一状态？')) return
    setActionLoading(true)
    try {
      await confirmOrder(orderId)
      fetchOrders()
      if (detailOrder?.id === orderId) setDetailOrder(null)
    } catch (err) {
      alert(err.message || '操作失败')
    } finally {
      setActionLoading(false)
    }
  }, [detailOrder, fetchOrders])

  // Admin: Force cancel
  const handleForceCancel = useCallback(async () => {
    if (!cancelReason.trim()) { alert('请填写取消原因'); return }
    setActionLoading(true)
    try {
      await forceCancelOrder(showCancelModal, { reason: cancelReason })
      setShowCancelModal(null)
      setCancelReason('')
      fetchOrders()
      if (detailOrder?.id === showCancelModal) setDetailOrder(null)
    } catch (err) {
      alert(err.message || '操作失败')
    } finally {
      setActionLoading(false)
    }
  }, [showCancelModal, cancelReason, detailOrder, fetchOrders])

  // Admin: Resolve dispute
  const handleResolveDispute = useCallback(async () => {
    if (!disputeReason.trim()) { alert('请填写裁决说明'); return }
    setActionLoading(true)
    try {
      await resolveDispute(showDisputeModal, { resolution: disputeResolution, reason: disputeReason })
      setShowDisputeModal(null)
      setDisputeReason('')
      fetchOrders()
      if (detailOrder?.id === showDisputeModal) setDetailOrder(null)
    } catch (err) {
      alert(err.message || '操作失败')
    } finally {
      setActionLoading(false)
    }
  }, [showDisputeModal, disputeResolution, disputeReason, detailOrder, fetchOrders])

  // Format price
  const formatPrice = (price) => {
    if (price == null) return '-'
    return `¥${Number(price).toLocaleString()}`
  }

  // Truncate order ID
  const truncateId = (id) => {
    if (!id) return '-'
    return id.length > 8 ? id.slice(0, 8) + '...' : id
  }

  // Export
  const handleExport = useCallback(async () => {
    try {
      const params = {}
      if (debouncedSearch) params.keyword = debouncedSearch
      if (statusFilter !== '全部') params.status = statusFilter
      if (startDate) params.startDate = startDate
      if (endDate) params.endDate = endDate
      const qs = new URLSearchParams(params).toString()
      
      await exportFile(`/orders/export${qs ? '?' + qs : ''}`, `订单列表_${new Date().toLocaleDateString()}.xlsx`)
    } catch (error) {
      console.error('导出失败:', error)
      alert('导出失败: ' + error.message)
    }
  }, [debouncedSearch, statusFilter, startDate, endDate])

  // Check what actions are available for an order
  const getAvailableActions = (order) => {
    const actions = []
    const s = order.status
    if (s === 'PENDING_DEPOSIT') actions.push('confirm')
    if (s !== 'COMPLETED' && s !== 'CANCELLED' && s !== 'TERMINATED') actions.push('forceCancel')
    if (s === 'DISPUTE') actions.push('resolveDispute')
    return actions
  }

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
        <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">交易管理</h2>
        <p className="text-[12px] text-gray-500 mt-0.5">管理平台所有交易订单，查看订单详情、执行管理操作与查看操作日志。</p>
      </div>

      {/* Search & Filter */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3">
        <div className="flex items-center gap-2 flex-wrap">
          <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs">
            <Search className="w-3.5 h-3.5 text-gray-400" />
            <input
              type="text"
              placeholder="搜索订单号"
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
              {statusFilter === '全部' ? '全部状态' : STATUS_MAP[statusFilter]?.label || statusFilter}
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
                    {opt === '全部' ? '全部' : STATUS_MAP[opt]?.label || opt}
                  </button>
                ))}
              </div>
            )}
          </div>
          <div className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs">
            <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="bg-transparent outline-none text-gray-700 text-xs" />
            <span className="text-gray-400">~</span>
            <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} className="bg-transparent outline-none text-gray-700 text-xs" />
          </div>
        </div>
        <button onClick={handleExport} className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors">
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
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">订单号</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">车源标题</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">买家</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">卖家</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">金额</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">状态</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">操作</th>
                </tr>
              </thead>
              <tbody>
                {orders.length === 0 ? (
                  <tr><td colSpan={7} className="px-4 py-8 text-center text-gray-400 text-sm">暂无订单数据</td></tr>
                ) : (
                  orders.map((o) => {
                    const actions = getAvailableActions(o)
                    return (
                      <tr key={o.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                        <td className="px-4 py-3">
                          <span className="font-mono text-gray-800 text-sm" title={o.id}>{truncateId(o.id)}</span>
                        </td>
                        <td className="px-4 py-3">
                          <div className="font-medium text-gray-900 text-sm max-w-[160px] truncate">{o.carTitle || '-'}</div>
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-800">{o.buyerName || '-'}</td>
                        <td className="px-4 py-3 text-sm text-gray-800">{o.sellerName || '-'}</td>
                        <td className="px-4 py-3 text-sm text-red-600 font-bold font-mono">{formatPrice(o.totalPrice)}</td>
                        <td className="px-4 py-3">{getStatusBadge(o.status)}</td>
                        <td className="px-4 py-3">
                          <div className="flex items-center gap-1">
                            <button onClick={() => handleViewDetail(o)} className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors">
                              <Eye className="w-3 h-3" />详情
                            </button>
                            {actions.includes('confirm') && (
                              <button onClick={() => handleConfirm(o.id)} disabled={actionLoading} className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-emerald-600 hover:bg-emerald-50 transition-colors disabled:opacity-50">
                                <CheckCircle className="w-3 h-3" />确认
                              </button>
                            )}
                            {actions.includes('forceCancel') && (
                              <button onClick={() => { setShowCancelModal(o.id); setCancelReason('') }} className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-red-600 hover:bg-red-50 transition-colors">
                                <XCircle className="w-3 h-3" />取消
                              </button>
                            )}
                            {actions.includes('resolveDispute') && (
                              <button onClick={() => { setShowDisputeModal(o.id); setDisputeReason(''); setDisputeResolution('buyer') }} className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-orange-600 hover:bg-orange-50 transition-colors">
                                <Scale className="w-3 h-3" />裁决
                              </button>
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
                  <button onClick={() => setShowPageSizeDropdown(!showPageSizeDropdown)} className="flex items-center gap-1 px-2 py-1 border border-gray-200 rounded-lg bg-white hover:bg-gray-50 transition-colors">
                    {size}<ChevronDown className="w-3 h-3" />
                  </button>
                  {showPageSizeDropdown && (
                    <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg py-1 z-10">
                      {pageSizeOptions.map((opt) => (
                        <button key={opt} onClick={() => { setSize(opt); setShowPageSizeDropdown(false) }} className={`w-full text-left px-3 py-1 text-xs hover:bg-gray-50 transition-colors ${size === opt ? 'text-indigo-600 font-medium' : 'text-gray-600'}`}>{opt}</button>
                      ))}
                    </div>
                  )}
                </div>
                <span className="text-gray-400">显示 {paginationStart}-{paginationEnd} 项，共 {total} 项</span>
              </div>
              <div className="flex items-center gap-1">
                <button onClick={() => setPage(page - 1)} disabled={page <= 1} className={`p-1.5 rounded-lg transition-colors ${page <= 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'}`}>
                  <ChevronLeft className="w-3.5 h-3.5" />
                </button>
                {getPageNumbers().map((p, i) =>
                  p === '...' ? (
                    <span key={`ellipsis-${i}`} className="px-1.5 py-1 text-gray-400 text-xs">...</span>
                  ) : (
                    <button key={p} onClick={() => setPage(p)} className={`px-2.5 py-1 rounded-lg text-xs font-medium transition-colors ${page === p ? 'bg-indigo-600 text-white' : 'text-gray-600 hover:bg-gray-100'}`}>{p}</button>
                  )
                )}
                <button onClick={() => setPage(page + 1)} disabled={page >= totalPages} className={`p-1.5 rounded-lg transition-colors ${page >= totalPages ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'}`}>
                  <ChevronRight className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Detail Modal */}
      {detailOrder && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setDetailOrder(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[85vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">订单详情</h3>
              <button onClick={() => setDetailOrder(null)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors"><X className="w-4 h-4 text-gray-400" /></button>
            </div>
            {detailLoading ? (
              <div className="p-6 space-y-4">{[1, 2, 3].map(i => (<div key={i} className="h-12 bg-gray-100 rounded-lg animate-pulse" />))}</div>
            ) : (
              <div className="p-5 space-y-5">
                {/* Car Info */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">车源信息</h4>
                  <div className="bg-gray-50 rounded-xl p-4">
                    <div className="font-medium text-gray-900 text-sm">{detailOrder.carTitle || '-'}</div>
                    <div className="text-sm text-red-600 font-bold font-mono mt-1">{formatPrice(detailOrder.totalPrice)}</div>
                  </div>
                </div>

                {/* Buyer & Seller */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">买卖双方</h4>
                  <div className="grid grid-cols-2 gap-3 text-xs">
                    <div className="bg-gray-50 rounded-xl p-3">
                      <div className="text-gray-400 mb-1">买家</div>
                      <div className="text-gray-800 font-medium">{detailOrder.buyerName || '-'}</div>
                      <div className="text-gray-600 font-mono mt-0.5">{detailOrder.buyerPhone ? maskPhone(detailOrder.buyerPhone) : '-'}</div>
                    </div>
                    <div className="bg-gray-50 rounded-xl p-3">
                      <div className="text-gray-400 mb-1">卖家</div>
                      <div className="text-gray-800 font-medium">{detailOrder.sellerName || '-'}</div>
                      <div className="text-gray-600 font-mono mt-0.5">{detailOrder.sellerPhone ? maskPhone(detailOrder.sellerPhone) : '-'}</div>
                    </div>
                  </div>
                </div>

                {/* Deposit & Amount */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">保证金与金额</h4>
                  <div className="grid grid-cols-2 gap-3 text-xs">
                    <div>
                      <span className="text-gray-400">总金额：</span>
                      <span className="text-red-600 font-bold font-mono">{formatPrice(detailOrder.totalPrice)}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">保证金额：</span>
                      <span className="text-gray-800 font-mono">{formatPrice(detailOrder.depositAmount)}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">买家保证金：</span>
                      <span className={detailOrder.buyerDepositPaid ? 'text-emerald-600' : 'text-amber-600'}>
                        {detailOrder.buyerDepositPaid ? '已缴纳' : '未缴纳'}
                      </span>
                    </div>
                    <div>
                      <span className="text-gray-400">卖家保证金：</span>
                      <span className={detailOrder.sellerDepositPaid ? 'text-emerald-600' : 'text-amber-600'}>
                        {detailOrder.sellerDepositPaid ? '已缴纳' : '未缴纳'}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Contract Info */}
                {detailOrder.contractNo && (
                  <div>
                    <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">合同信息</h4>
                    <div className="grid grid-cols-2 gap-3 text-xs">
                      <div>
                        <span className="text-gray-400">合同编号：</span>
                        <span className="text-gray-800 font-mono">{detailOrder.contractNo}</span>
                      </div>
                      <div>
                        <span className="text-gray-400">合同状态：</span>
                        <span className="text-gray-800">
                          {detailOrder.contractConfirmed ? '已签署' : detailOrder.contractSubmitted ? '待签署' : '未提交'}
                        </span>
                      </div>
                    </div>
                  </div>
                )}

                {/* Order Status */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">订单状态</h4>
                  <div className="flex items-center gap-2">
                    {getStatusBadge(detailOrder.status)}
                    {detailOrder.cancelReason && (
                      <span className="text-xs text-red-500 flex items-center gap-1">
                        <AlertTriangle className="w-3 h-3" />{detailOrder.cancelReason}
                      </span>
                    )}
                  </div>
                  {detailOrder.remark && (
                    <div className="mt-2 text-xs text-gray-500">备注：{detailOrder.remark}</div>
                  )}
                </div>

                {/* Time Info */}
                <div className="grid grid-cols-2 gap-3 text-xs text-gray-500">
                  <div>创建：{detailOrder.createdAt ? formatDate(detailOrder.createdAt) : '-'}</div>
                  <div>更新：{detailOrder.updatedAt ? formatDate(detailOrder.updatedAt) : '-'}</div>
                  {detailOrder.completedAt && <div>完成：{formatDate(detailOrder.completedAt)}</div>}
                  {detailOrder.cancelledAt && <div>取消：{formatDate(detailOrder.cancelledAt)}</div>}
                </div>

                {/* Operation Logs Timeline */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">操作日志</h4>
                  {orderLogs.length === 0 ? (
                    <p className="text-xs text-gray-400">暂无操作日志</p>
                  ) : (
                    <div className="relative">
                      <div className="absolute left-2 top-2 bottom-2 w-0.5 bg-gray-200" />
                      <div className="space-y-4">
                        {orderLogs.map((log, i) => (
                          <div key={log.id || i} className="flex items-start gap-3 relative">
                            <div className="w-4 h-4 rounded-full bg-indigo-500 border-2 border-white shadow-sm shrink-0 z-10" />
                            <div className="text-xs flex-1">
                              <div className="flex items-center gap-2 mb-0.5">
                                <span className="text-gray-400 font-mono">{log.createdAt ? formatDate(log.createdAt) : '-'}</span>
                                <span className="text-gray-600 font-medium">{log.operatorName || '-'}</span>
                                <span className="px-1.5 py-0.5 bg-gray-100 rounded text-[10px] text-gray-500 font-mono">{log.action || ''}</span>
                              </div>
                              <div className="text-gray-800">{log.detail || '-'}</div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setDetailOrder(null)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">关闭</button>
            </div>
          </div>
        </div>
      )}

      {/* Force Cancel Modal */}
      {showCancelModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setShowCancelModal(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-red-600">强制取消订单</h3>
              <button onClick={() => setShowCancelModal(null)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors"><X className="w-4 h-4 text-gray-400" /></button>
            </div>
            <div className="p-5 space-y-4">
              <div className="flex items-start gap-2 bg-red-50 rounded-xl p-3 text-xs text-red-700">
                <AlertTriangle className="w-4 h-4 shrink-0 mt-0.5" />
                <span>强制取消将终止订单交易流程，买卖双方将收到通知。请确认操作必要性。</span>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1.5">取消原因 <span className="text-red-500">*</span></label>
                <textarea
                  value={cancelReason}
                  onChange={(e) => setCancelReason(e.target.value)}
                  placeholder="请填写强制取消的原因..."
                  rows={3}
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-xs outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-100 resize-none"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setShowCancelModal(null)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">取消</button>
              <button onClick={handleForceCancel} disabled={actionLoading} className="px-4 py-2 text-xs text-white bg-red-600 hover:bg-red-700 rounded-lg transition-colors disabled:opacity-50">
                {actionLoading ? '处理中...' : '确认取消'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Dispute Resolution Modal */}
      {showDisputeModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setShowDisputeModal(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-orange-600">纠纷裁决</h3>
              <button onClick={() => setShowDisputeModal(null)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors"><X className="w-4 h-4 text-gray-400" /></button>
            </div>
            <div className="p-5 space-y-4">
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1.5">裁决结果 <span className="text-red-500">*</span></label>
                <div className="flex gap-2">
                  <button
                    onClick={() => setDisputeResolution('buyer')}
                    className={`flex-1 px-3 py-2 rounded-xl text-xs font-medium border transition-colors ${disputeResolution === 'buyer' ? 'bg-blue-50 border-blue-300 text-blue-700' : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'}`}
                  >
                    支持买家（退款取消）
                  </button>
                  <button
                    onClick={() => setDisputeResolution('seller')}
                    className={`flex-1 px-3 py-2 rounded-xl text-xs font-medium border transition-colors ${disputeResolution === 'seller' ? 'bg-emerald-50 border-emerald-300 text-emerald-700' : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'}`}
                  >
                    支持卖家（继续执行）
                  </button>
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1.5">裁决说明 <span className="text-red-500">*</span></label>
                <textarea
                  value={disputeReason}
                  onChange={(e) => setDisputeReason(e.target.value)}
                  placeholder="请填写裁决依据和说明..."
                  rows={3}
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-xs outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-100 resize-none"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setShowDisputeModal(null)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">取消</button>
              <button onClick={handleResolveDispute} disabled={actionLoading} className="px-4 py-2 text-xs text-white bg-orange-600 hover:bg-orange-700 rounded-lg transition-colors disabled:opacity-50">
                {actionLoading ? '处理中...' : '提交裁决'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
