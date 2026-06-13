import { useState, useEffect, useCallback, useRef } from 'react'
import { Search, ChevronDown, ChevronLeft, ChevronRight, Eye, Power, Flag, X, Download, Image as ImageIcon, FileText } from 'lucide-react'
import { getCarList, getCarDetail, updateCarStatus, violateCar } from '../../api/car'
import { usePagination } from '../../hooks/usePagination'
import { SkeletonTable } from '../../components/Skeleton'
import { formatDate, maskPhone } from '../../utils/format'

const STATUS_OPTIONS = ['全部', 'ON_SALE', 'OFFLINE', 'SOLD', 'DRAFT']
const ENERGY_OPTIONS = ['全部', '汽油', '柴油', '纯电', '插电混动']

const STATUS_MAP = {
  ON_SALE: { label: '在售', color: 'bg-emerald-100 text-emerald-700' },
  OFFLINE: { label: '已下架', color: 'bg-gray-100 text-gray-500' },
  SOLD: { label: '已售', color: 'bg-blue-100 text-blue-700' },
  DRAFT: { label: '草稿', color: 'bg-amber-100 text-amber-700' },
}

function getStatusBadge(status) {
  const s = STATUS_MAP[status]
  if (!s) return <span className="inline-block px-2 py-0.5 rounded-full text-[11px] font-medium bg-gray-100 text-gray-600">{status || '-'}</span>
  return <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${s.color}`}>{s.label}</span>
}

export default function CarManage() {
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('全部')
  const [energyFilter, setEnergyFilter] = useState('全部')
  const [showStatusDropdown, setShowStatusDropdown] = useState(false)
  const [showEnergyDropdown, setShowEnergyDropdown] = useState(false)
  const [showPageSizeDropdown, setShowPageSizeDropdown] = useState(false)
  const [cars, setCars] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [detailCar, setDetailCar] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [offlineTarget, setOfflineTarget] = useState(null)
  const [violateTarget, setViolateTarget] = useState(null)
  const [violateReason, setViolateReason] = useState('')

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
  useEffect(() => { reset() }, [debouncedSearch, statusFilter, energyFilter, reset])

  // Load cars
  const fetchCars = useCallback(() => {
    setLoading(true)
    setError(null)
    const params = { page, size }
    if (debouncedSearch) params.keyword = debouncedSearch
    if (statusFilter !== '全部') params.status = statusFilter
    if (energyFilter !== '全部') params.energyType = energyFilter

    getCarList(params)
      .then(res => {
        const data = res.data || res
        setCars(data.list || data.records || [])
        setTotal(data.total || 0)
      })
      .catch(err => {
        setError(err.response?.data?.message || err.message || '加载失败')
        setCars([])
      })
      .finally(() => setLoading(false))
  }, [page, size, debouncedSearch, statusFilter, energyFilter])

  useEffect(() => { fetchCars() }, [fetchCars])

  // Detail modal
  const handleViewDetail = useCallback(async (car) => {
    setDetailCar(null)
    setDetailLoading(true)
    try {
      const res = await getCarDetail(car.id)
      setDetailCar(res.data || res)
    } catch (err) {
      setDetailCar({ ...car })
    } finally {
      setDetailLoading(false)
    }
  }, [])

  // Offline
  const confirmOffline = useCallback(async () => {
    if (!offlineTarget) return
    try {
      await updateCarStatus(offlineTarget.id, { status: 'OFFLINE' })
      setOfflineTarget(null)
      fetchCars()
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message))
    }
  }, [offlineTarget, fetchCars])

  // Violate
  const confirmViolate = useCallback(async () => {
    if (!violateTarget || !violateReason.trim()) return
    try {
      await violateCar(violateTarget.id, { reason: violateReason.trim(), status: 'OFFLINE' })
      setViolateTarget(null)
      setViolateReason('')
      fetchCars()
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message))
    }
  }, [violateTarget, violateReason, fetchCars])

  // Format price: price is in 元, display as 万
  const formatPrice = (price) => {
    if (price == null) return '-'
    const wan = Number(price) / 10000
    return `¥${wan.toFixed(1)}万`
  }

  // Export
  const handleExport = useCallback(() => {
    const params = new URLSearchParams()
    if (debouncedSearch) params.append('keyword', debouncedSearch)
    if (statusFilter !== '全部') params.append('status', statusFilter)
    if (energyFilter !== '全部') params.append('energyType', energyFilter)
    window.open(`/api/v1/admin/cars/export?${params.toString()}`, '_blank')
  }, [debouncedSearch, statusFilter, energyFilter])

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
        <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">5D车源管理</h2>
        <p className="text-[12px] text-gray-500 mt-0.5">管理平台所有在售及已下架车源，执行下架与违规标记操作。</p>
      </div>

      {/* Search & Filter */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3">
        <div className="flex items-center gap-2 flex-wrap">
          <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs">
            <Search className="w-3.5 h-3.5 text-gray-400" />
            <input
              type="text"
              placeholder="搜索车源标题"
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
              <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg py-1 z-10 min-w-[120px]">
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
          <div className="relative">
            <button
              onClick={() => setShowEnergyDropdown(!showEnergyDropdown)}
              className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors"
            >
              {energyFilter === '全部' ? '能源类型' : energyFilter}
              <ChevronDown className="w-3 h-3" />
            </button>
            {showEnergyDropdown && (
              <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg py-1 z-10 min-w-[120px]">
                {ENERGY_OPTIONS.map((opt) => (
                  <button
                    key={opt}
                    onClick={() => { setEnergyFilter(opt); setShowEnergyDropdown(false) }}
                    className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50 transition-colors ${energyFilter === opt ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
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
        <SkeletonTable rows={5} cols={9} />
      ) : error ? (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center text-red-500 text-sm">{error}</div>
      ) : (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-xs">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">封面</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">标题</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">价格</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">里程</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">年份</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">城市</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">卖家</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">状态</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">操作</th>
                </tr>
              </thead>
              <tbody>
                {cars.length === 0 ? (
                  <tr>
                    <td colSpan={9} className="px-4 py-8 text-center text-gray-400 text-sm">暂无车源数据</td>
                  </tr>
                ) : (
                  cars.map((c) => (
                    <tr key={c.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                      <td className="px-4 py-3">
                        {c.coverUrl ? (
                          <img src={c.coverUrl} alt="cover" className="w-16 h-12 object-cover rounded-lg bg-gray-100" />
                        ) : (
                          <div className="w-16 h-12 rounded-lg bg-gray-100 flex items-center justify-center text-[10px] text-gray-400">暂无图</div>
                        )}
                      </td>
                      <td className="px-4 py-3">
                        <div className="font-medium text-gray-900 text-sm max-w-[160px] truncate">{c.title || '-'}</div>
                        <div className="text-[11px] text-gray-400 mt-0.5 font-mono">ID: {c.id}</div>
                      </td>
                      <td className="px-4 py-3 text-sm text-red-600 font-bold font-mono">{formatPrice(c.price)}</td>
                      <td className="px-4 py-3 text-sm text-gray-800">{c.mileage != null ? `${c.mileage}万公里` : '-'}</td>
                      <td className="px-4 py-3 text-sm text-gray-800">{c.year || '-'}</td>
                      <td className="px-4 py-3 text-sm text-gray-800">{c.city || '-'}</td>
                      <td className="px-4 py-3 text-sm text-gray-800">{c.sellerName || c.shopName || '-'}</td>
                      <td className="px-4 py-3">{getStatusBadge(c.status)}</td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => handleViewDetail(c)}
                            className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors"
                          >
                            <Eye className="w-3 h-3" />
                            查看
                          </button>
                          {c.status === 'ON_SALE' && (
                            <button
                              onClick={() => setOfflineTarget(c)}
                              className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-gray-500 hover:bg-gray-100 transition-colors"
                            >
                              <Power className="w-3 h-3" />
                              下架
                            </button>
                          )}
                          <button
                            onClick={() => setViolateTarget(c)}
                            className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-red-500 hover:bg-red-50 transition-colors"
                          >
                            <Flag className="w-3 h-3" />
                            违规
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
      {detailCar && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setDetailCar(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-3xl max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">车源详情</h3>
              <button onClick={() => setDetailCar(null)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
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
              <div className="p-5 space-y-6">
                {/* Image Gallery */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3 flex items-center gap-1.5">
                    <ImageIcon className="w-3.5 h-3.5" />图片 ({detailCar.images?.length || 0})
                  </h4>
                  {detailCar.images && detailCar.images.length > 0 ? (
                    <div className="grid grid-cols-4 gap-2">
                      {detailCar.images.map((img, idx) => (
                        <div key={img.id || idx} className="aspect-square rounded-xl overflow-hidden bg-gray-100 border border-gray-200">
                          <img src={img.imageUrl} alt={`img-${idx}`} className="w-full h-full object-cover" />
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="bg-gray-50 rounded-xl p-8 text-center text-gray-400 text-xs">暂无图片</div>
                  )}
                </div>

                {/* Basic Info */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">基本信息</h4>
                  <div className="grid grid-cols-2 gap-3 text-xs">
                    <div>
                      <span className="text-gray-400">标题：</span>
                      <span className="text-gray-800 font-medium">{detailCar.title || '-'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">品牌/车系：</span>
                      <span className="text-gray-800">{detailCar.brand || '-'} {detailCar.series || ''}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">价格：</span>
                      <span className="text-red-600 font-bold font-mono">{formatPrice(detailCar.price)}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">里程：</span>
                      <span className="text-gray-800">{detailCar.mileage != null ? `${detailCar.mileage}万公里` : '-'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">年份：</span>
                      <span className="text-gray-800">{detailCar.year || '-'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">城市：</span>
                      <span className="text-gray-800">{detailCar.city || '-'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">能源类型：</span>
                      <span className="text-gray-800">{detailCar.energyType || '-'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">状态：</span>
                      {getStatusBadge(detailCar.status)}
                    </div>
                  </div>
                </div>

                {/* Inspection Report */}
                {detailCar.inspection && (
                  <div>
                    <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3 flex items-center gap-1.5">
                      <FileText className="w-3.5 h-3.5" />检测报告
                    </h4>
                    <div className="bg-gray-50 rounded-xl p-4 space-y-3 text-xs">
                      <div className="grid grid-cols-2 gap-3">
                        <div>
                          <span className="text-gray-400">检测结论：</span>
                          <span className={`font-medium ${detailCar.inspection.result === 'PASS' ? 'text-emerald-600' : 'text-red-600'}`}>
                            {detailCar.inspection.result === 'PASS' ? '通过' : '不通过'}
                          </span>
                        </div>
                        <div>
                          <span className="text-gray-400">检测时间：</span>
                          <span className="text-gray-800">{detailCar.inspection.inspectedAt ? formatDate(detailCar.inspection.inspectedAt) : '-'}</span>
                        </div>
                      </div>
                      {detailCar.inspection.description && (
                        <div>
                          <span className="text-gray-400">检测说明：</span>
                          <div className="mt-1 text-gray-700 leading-relaxed">{detailCar.inspection.description}</div>
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {/* Seller Info */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">卖家信息</h4>
                  <div className="grid grid-cols-2 gap-3 text-xs">
                    <div>
                      <span className="text-gray-400">昵称：</span>
                      <span className="text-gray-800">{detailCar.sellerName || detailCar.shopName || '-'}</span>
                    </div>
                    <div>
                      <span className="text-gray-400">手机号：</span>
                      <span className="text-gray-800 font-mono">{detailCar.sellerPhone ? maskPhone(detailCar.sellerPhone) : '-'}</span>
                    </div>
                  </div>
                </div>

                {/* Stats */}
                <div>
                  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">统计数据</h4>
                  <div className="grid grid-cols-2 gap-3 text-xs">
                    <div className="bg-gray-50 rounded-xl p-3 text-center">
                      <div className="text-gray-400">浏览量</div>
                      <div className="text-lg font-bold text-gray-900 font-mono">{detailCar.viewCount ?? 0}</div>
                    </div>
                    <div className="bg-gray-50 rounded-xl p-3 text-center">
                      <div className="text-gray-400">收藏量</div>
                      <div className="text-lg font-bold text-gray-900 font-mono">{detailCar.favoriteCount ?? 0}</div>
                    </div>
                  </div>
                </div>
              </div>
            )}
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setDetailCar(null)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                关闭
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Offline Confirm Modal */}
      {offlineTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setOfflineTarget(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="p-5">
              <h3 className="text-base font-bold text-gray-900 mb-2">确认下架</h3>
              <p className="text-sm text-gray-600">
                确定要下架车源「{offlineTarget.title}」吗？
              </p>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setOfflineTarget(null)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                取消
              </button>
              <button
                onClick={confirmOffline}
                className="px-4 py-2 text-xs text-white bg-gray-600 hover:bg-gray-700 rounded-lg transition-colors"
              >
                确认下架
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Violate Modal */}
      {violateTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setViolateTarget(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">标记违规</h3>
              <button onClick={() => setViolateTarget(null)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-3">
              <p className="text-sm text-gray-600">标记车源「{violateTarget.title}」为违规并下架</p>
              <div>
                <label className="block text-xs text-gray-500 mb-1">违规原因</label>
                <textarea
                  value={violateReason}
                  onChange={(e) => setViolateReason(e.target.value)}
                  placeholder="请输入违规原因..."
                  rows={4}
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-red-300 focus:ring-1 focus:ring-red-300 resize-none"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={() => setViolateTarget(null)}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={confirmViolate}
                disabled={!violateReason.trim()}
                className="px-4 py-2 text-xs text-white bg-red-500 hover:bg-red-600 rounded-lg transition-colors disabled:opacity-50"
              >
                确认标记
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
