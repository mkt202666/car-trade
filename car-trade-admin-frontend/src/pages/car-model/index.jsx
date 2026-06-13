import { useState, useEffect, useCallback } from 'react'
import { Plus, Search, Upload, Edit2, Trash2, Settings2, ChevronDown, ChevronLeft, ChevronRight, X } from 'lucide-react'
import { getModels, getBrands, getSeries, createModel, updateModel, deleteModel, importModels } from '../../api/carModel'
import { usePagination } from '../../hooks/usePagination'
import { SkeletonTable } from '../../components/Skeleton'
import { formatDate } from '../../utils/format'

export default function CarModel() {
  const [search, setSearch] = useState('')
  const [brandFilter, setBrandFilter] = useState('全部品牌')
  const [seriesFilter, setSeriesFilter] = useState('全部车系')
  const [statusFilter, setStatusFilter] = useState('全部状态')
  const [showBrandDropdown, setShowBrandDropdown] = useState(false)
  const [showSeriesDropdown, setShowSeriesDropdown] = useState(false)
  const [showStatusDropdown, setShowStatusDropdown] = useState(false)
  const [models, setModels] = useState([])
  const [brands, setBrands] = useState([])
  const [series, setSeries] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  
  // Modal states
  const [modalOpen, setModalOpen] = useState(false)
  const [editingModel, setEditingModel] = useState(null)
  const [form, setForm] = useState({
    name: '',
    year: new Date().getFullYear(),
    price: 0,
    seriesId: null,
    brandId: null,
    status: 'ACTIVE',
  })
  const [submitting, setSubmitting] = useState(false)
  
  const pagination = usePagination(20)
  const { page, size, total, totalPages, setPage, setSize, setTotal, reset } = pagination
  
  // Load brands on mount
  useEffect(() => {
    getBrands({ page: 1, size: 1000 })
      .then(data => {
        const list = Array.isArray(data) ? data : (data.list || data.records || [])
        setBrands(list)
      })
      .catch(() => setBrands([]))
  }, [])
  
  // Load series when brand changes
  useEffect(() => {
    if (brandFilter !== '全部品牌') {
      const brand = brands.find(b => b.name === brandFilter)
      if (brand) {
        getSeries({ brandId: brand.id, page: 1, size: 1000 })
          .then(data => {
            const list = Array.isArray(data) ? data : (data.list || data.records || [])
            setSeries(list)
          })
          .catch(() => setSeries([]))
      }
    } else {
      setSeries([])
    }
  }, [brandFilter, brands])
  
  // Reset page on filter change
  useEffect(() => { reset() }, [brandFilter, seriesFilter, statusFilter, search, reset])
  
  // Load models
  const fetchModels = useCallback(() => {
    setLoading(true)
    setError(null)
    const params = { page, size }
    
    if (brandFilter !== '全部品牌') {
      const brand = brands.find(b => b.name === brandFilter)
      if (brand) params.brandId = brand.id
    }
    
    if (seriesFilter !== '全部车系') {
      const serie = series.find(s => s.name === seriesFilter)
      if (serie) params.seriesId = serie.id
    }
    
    if (statusFilter !== '全部状态') {
      params.status = statusFilter
    }
    
    if (search) params.keyword = search
    
    getModels(params)
      .then(res => {
        const data = res.data ?? res
        setModels(data?.list ?? data?.records ?? [])
        setTotal(data?.total ?? 0)
      })
      .catch(err => {
        setError(err.response?.data?.message || err.message || '加载失败')
        setModels([])
      })
      .finally(() => setLoading(false))
  }, [page, size, brandFilter, seriesFilter, statusFilter, search, brands, series, setTotal])
  
  useEffect(() => { fetchModels() }, [fetchModels])
  
  // Open modal
  const openAddModal = () => {
    setEditingModel(null)
    setForm({
      name: '',
      year: new Date().getFullYear(),
      price: 0,
      seriesId: null,
      brandId: null,
      status: 'ACTIVE',
    })
    setModalOpen(true)
  }
  
  const openEditModal = (model) => {
    setEditingModel(model)
    setForm({
      name: model.name || '',
      year: model.year || new Date().getFullYear(),
      price: model.price || 0,
      seriesId: model.seriesId,
      brandId: model.brandId,
      status: model.status || 'ACTIVE',
    })
    setModalOpen(true)
  }
  
  // Submit form
  const handleSubmit = async () => {
    if (!form.name.trim()) return
    if (!form.seriesId) {
      alert('请选择车系')
      return
    }
    
    setSubmitting(true)
    try {
      if (editingModel) {
        await updateModel(editingModel.id, form)
      } else {
        await createModel(form)
      }
      setModalOpen(false)
      fetchModels()
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message))
    } finally {
      setSubmitting(false)
    }
  }
  
  // Delete model
  const handleDelete = async (model) => {
    if (!window.confirm(`确定要删除车型「${model.name}」吗？`)) return
    try {
      await deleteModel(model.id)
      fetchModels()
    } catch (err) {
      alert('删除失败: ' + (err.response?.data?.message || err.message))
    }
  }
  
  // Toggle status
  const handleToggleStatus = async (model) => {
    const newStatus = model.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    try {
      await updateModel(model.id, { ...model, status: newStatus })
      fetchModels()
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message))
    }
  }
  
  // Handle file upload
  const handleFileUpload = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    const formData = new FormData()
    formData.append('file', file)
    
    try {
      await importModels(formData)
      alert('导入成功')
      fetchModels()
    } catch (err) {
      alert('导入失败: ' + (err.response?.data?.message || err.message))
    }
  }
  
  // Pagination helpers
  const paginationStart = total === 0 ? 0 : (page - 1) * size + 1
  const paginationEnd = Math.min(page * size, total)
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
  
  // Status badge
  const getStatusBadge = (status) => {
    if (status === 'ACTIVE') {
      return <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-medium bg-emerald-50 text-emerald-600 border border-emerald-200">启用</span>
    }
    return <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-medium bg-gray-100 text-gray-500 border border-gray-200">停用</span>
  }
  
  return (
    <div className="space-y-6">
      {/* Page header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">车型库维护</h2>
          <p className="text-[12px] text-gray-500 mt-0.5">维护3级车型字典与挂架关联数据</p>
        </div>
        <div className="flex items-center gap-2 self-start">
          <label className="flex items-center gap-1.5 bg-white border border-gray-200 text-gray-600 text-xs font-semibold px-3 py-2 rounded-xl cursor-pointer hover:bg-gray-50 transition-colors">
            <Upload className="w-3.5 h-3.5" />
            上传附件维护
            <input type="file" accept=".xlsx,.xls" onChange={handleFileUpload} className="hidden" />
          </label>
          <button 
            onClick={openAddModal}
            className="flex items-center gap-1.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold px-4 py-2 rounded-xl transition-colors"
          >
            <Plus className="w-3.5 h-3.5" />
            新增基础车型
          </button>
        </div>
      </div>
      
      {/* Filters */}
      <div className="flex flex-wrap items-center gap-2">
        {/* Brand select */}
        <div className="relative">
          <button
            onClick={() => { setShowBrandDropdown(!showBrandDropdown); setShowSeriesDropdown(false) }}
            className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors min-w-[140px]"
          >
            {brandFilter}
            <ChevronDown className="w-3.5 h-3.5 text-gray-400" />
          </button>
          {showBrandDropdown && (
            <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-10 py-1 max-h-64 overflow-y-auto min-w-[160px]">
              <button
                onClick={() => { setBrandFilter('全部品牌'); setShowBrandDropdown(false) }}
                className={`block w-full text-left px-3 py-2 text-xs hover:bg-gray-50 transition-colors ${brandFilter === '全部品牌' ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
              >
                全部品牌
              </button>
              {brands.map((b) => (
                <button
                  key={b.id}
                  onClick={() => { setBrandFilter(b.name); setShowBrandDropdown(false) }}
                  className={`block w-full text-left px-3 py-2 text-xs hover:bg-gray-50 transition-colors ${brandFilter === b.name ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
                >
                  {b.name}
                </button>
              ))}
            </div>
          )}
        </div>
        
        {/* Series select */}
        <div className="relative">
          <button
            onClick={() => { setShowSeriesDropdown(!showSeriesDropdown); setShowBrandDropdown(false) }}
            disabled={!series.length}
            className={`flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs transition-colors min-w-[140px] ${series.length ? 'text-gray-600 hover:bg-gray-50 cursor-pointer' : 'text-gray-300 cursor-not-allowed'}`}
          >
            {seriesFilter}
            <ChevronDown className="w-3.5 h-3.5" />
          </button>
          {showSeriesDropdown && series.length > 0 && (
            <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-10 py-1 max-h-64 overflow-y-auto min-w-[160px]">
              <button
                onClick={() => { setSeriesFilter('全部车系'); setShowSeriesDropdown(false) }}
                className={`block w-full text-left px-3 py-2 text-xs hover:bg-gray-50 transition-colors ${seriesFilter === '全部车系' ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
              >
                全部车系
              </button>
              {series.map((s) => (
                <button
                  key={s.id}
                  onClick={() => { setSeriesFilter(s.name); setShowSeriesDropdown(false) }}
                  className={`block w-full text-left px-3 py-2 text-xs hover:bg-gray-50 transition-colors ${seriesFilter === s.name ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
                >
                  {s.name}
                </button>
              ))}
            </div>
          )}
        </div>
        
        {/* Status select */}
        <div className="relative">
          <button
            onClick={() => setShowStatusDropdown(!showStatusDropdown)}
            className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors"
          >
            {statusFilter}
            <ChevronDown className="w-3.5 h-3.5 text-gray-400" />
          </button>
          {showStatusDropdown && (
            <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-10 py-1 min-w-[120px]">
              {['全部状态', 'ACTIVE', 'INACTIVE'].map((opt) => (
                <button
                  key={opt}
                  onClick={() => { setStatusFilter(opt); setShowStatusDropdown(false) }}
                  className={`block w-full text-left px-3 py-2 text-xs hover:bg-gray-50 transition-colors ${statusFilter === opt ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
                >
                  {opt === '全部状态' ? '全部状态' : opt === 'ACTIVE' ? '启用' : '停用'}
                </button>
              ))}
            </div>
          )}
        </div>
        
        {/* Search */}
        <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs ml-auto">
          <Search className="w-3.5 h-3.5 text-gray-400" />
          <input
            type="text"
            placeholder="搜索车型名称"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="bg-transparent outline-none text-gray-700 placeholder:text-gray-400 w-40"
          />
        </div>
      </div>
      
      {/* Table */}
      {loading ? (
        <SkeletonTable rows={5} cols={6} />
      ) : error ? (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center text-red-500 text-sm">{error}</div>
      ) : (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50">
                  <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">3级车型谱系</th>
                  <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">核心参数</th>
                  <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">指导价</th>
                  <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">状态</th>
                  <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">创建时间</th>
                  <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {models.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="px-4 py-8 text-center text-gray-400 text-sm">暂无车型数据</td>
                  </tr>
                ) : (
                  models.map((m) => (
                    <tr key={m.id} className="hover:bg-gray-50/50 transition-colors">
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <div className="flex items-center gap-1 text-xs">
                          <span className="font-medium text-gray-900">{m.brandName || '-'}</span>
                          <span className="text-gray-300">/</span>
                          <span className="text-indigo-600 font-medium">{m.seriesName || '-'}</span>
                        </div>
                        <div className="text-xs text-gray-700 mt-0.5">{m.name}</div>
                        <div className="text-[11px] text-gray-400 mt-0.5">ID: <span className="font-mono">{m.id}</span></div>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-800">
                        <div className="space-y-0.5 text-xs">
                          <div><span className="text-gray-400">年份:</span> <span className="text-gray-700">{m.year || '-'}</span></div>
                          <div><span className="text-gray-400">动力类型:</span> <span className="text-gray-700">{m.powerType || '-'}</span></div>
                          <div><span className="text-gray-400">车型:</span> <span className="text-gray-700">{m.carType || '-'}</span></div>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-900 font-semibold font-mono">
                        {m.price ? `¥${(m.price / 10000).toFixed(2)}万` : '-'}
                      </td>
                      <td className="px-4 py-3 text-sm">
                        {getStatusBadge(m.status)}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-600 font-mono">
                        {m.createdAt ? formatDate(m.createdAt) : '-'}
                      </td>
                      <td className="px-4 py-3 text-sm">
                        <div className="flex flex-wrap items-center gap-1">
                          <button 
                            onClick={() => handleToggleStatus(m)}
                            className={`text-[11px] px-2 py-1 rounded-lg font-medium transition-colors ${m.status === 'ACTIVE' ? 'text-amber-600 hover:bg-amber-50' : 'text-emerald-600 hover:bg-emerald-50'}`}
                          >
                            {m.status === 'ACTIVE' ? '停用' : '启用'}
                          </button>
                          <button 
                            onClick={() => openEditModal(m)}
                            className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-indigo-600 transition-colors" 
                            title="编辑"
                          >
                            <Edit2 className="w-3.5 h-3.5" />
                          </button>
                          <button 
                            onClick={() => handleDelete(m)}
                            className="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors" 
                            title="删除"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                          <button className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-indigo-600 transition-colors" title="详细配置">
                            <Settings2 className="w-3.5 h-3.5" />
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
                    onClick={() => {}}
                    className="flex items-center gap-1 px-2 py-1 border border-gray-200 rounded-lg bg-white hover:bg-gray-50 transition-colors"
                  >
                    {size}
                    <ChevronDown className="w-3 h-3" />
                  </button>
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
      
      {/* Add/Edit Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setModalOpen(false)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">{editingModel ? '编辑车型' : '新增车型'}</h3>
              <button onClick={() => setModalOpen(false)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              {/* Brand select */}
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">品牌</label>
                <select
                  value={form.brandId || ''}
                  onChange={(e) => {
                    const brandId = e.target.value ? Number(e.target.value) : null
                    setForm({ ...form, brandId, seriesId: null })
                  }}
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                >
                  <option value="">请选择品牌</option>
                  {brands.map((b) => (
                    <option key={b.id} value={b.id}>{b.name}</option>
                  ))}
                </select>
              </div>
              
              {/* Series select */}
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">车系</label>
                <select
                  value={form.seriesId || ''}
                  onChange={(e) => setForm({ ...form, seriesId: e.target.value ? Number(e.target.value) : null })}
                  disabled={!form.brandId}
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300 disabled:bg-gray-50 disabled:text-gray-400"
                >
                  <option value="">请选择车系</option>
                  {series.map((s) => (
                    <option key={s.id} value={s.id}>{s.name}</option>
                  ))}
                </select>
              </div>
              
              {/* Model name */}
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">车型名称</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="例如：2024款 1.5L 自动全景乐享版"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
              
              {/* Year and Price */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">年份</label>
                  <input
                    type="number"
                    value={form.year}
                    onChange={(e) => setForm({ ...form, year: Number(e.target.value) })}
                    className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">指导价（元）</label>
                  <input
                    type="number"
                    value={form.price}
                    onChange={(e) => setForm({ ...form, price: Number(e.target.value) })}
                    className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                  />
                </div>
              </div>
              
              {/* Status */}
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">状态</label>
                <select
                  value={form.status}
                  onChange={(e) => setForm({ ...form, status: e.target.value })}
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                >
                  <option value="ACTIVE">启用</option>
                  <option value="INACTIVE">停用</option>
                </select>
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={() => setModalOpen(false)}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={handleSubmit}
                disabled={submitting || !form.name.trim() || !form.seriesId}
                className="px-4 py-2 text-xs text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg transition-colors disabled:opacity-50"
              >
                {submitting ? '提交中...' : '确认'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
