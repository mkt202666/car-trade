import { useState, useEffect, useCallback } from 'react'
import {
  Plus, Pencil, Trash2, RefreshCw, Upload, X, ImageIcon, ChevronDown,
} from 'lucide-react'
import {
  getBrands, createBrand, updateBrand, deleteBrand,
  getSeries, createSeries, updateSeries, deleteSeries,
  getModels, createModel, updateModel, deleteModel, importModels,
} from '../../api/carModel'
import { formatDate, getStatusColor, getStatusText } from '../../utils/format'

const emptyBrandForm = { name: '', logoUrl: '', status: 'ACTIVE' }
const emptySeriesForm = { name: '', brandId: null, status: 'ACTIVE' }
const emptyModelForm = { name: '', year: new Date().getFullYear(), price: 0, seriesId: null, status: 'ACTIVE' }

export default function CarLibrary() {
  // Data states
  const [brands, setBrands] = useState([])
  const [seriesList, setSeriesList] = useState([])
  const [modelsList, setModelsList] = useState([])
  const [loadingBrands, setLoadingBrands] = useState(false)
  const [loadingSeries, setLoadingSeries] = useState(false)
  const [loadingModels, setLoadingModels] = useState(false)

  // Selection states
  const [selectedBrandId, setSelectedBrandId] = useState(null)
  const [selectedSeriesId, setSelectedSeriesId] = useState(null)
  const [topBrandId, setTopBrandId] = useState('')
  const [showBrandDropdown, setShowBrandDropdown] = useState(false)

  // Modals
  const [brandModalOpen, setBrandModalOpen] = useState(false)
  const [editingBrand, setEditingBrand] = useState(null)
  const [brandForm, setBrandForm] = useState({ ...emptyBrandForm })

  const [seriesModalOpen, setSeriesModalOpen] = useState(false)
  const [editingSeries, setEditingSeries] = useState(null)
  const [seriesForm, setSeriesForm] = useState({ ...emptySeriesForm })

  const [modelModalOpen, setModelModalOpen] = useState(false)
  const [editingModel, setEditingModel] = useState(null)
  const [modelForm, setModelForm] = useState({ ...emptyModelForm })

  const [importModalOpen, setImportModalOpen] = useState(false)
  const [importFile, setImportFile] = useState(null)
  const [importLoading, setImportLoading] = useState(false)
  const [importResult, setImportResult] = useState(null)

  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleteType, setDeleteType] = useState(null)

  // Fetch brands
  const fetchBrands = useCallback(() => {
    setLoadingBrands(true)
    getBrands({ page: 1, size: 1000 })
      .then((data) => {
        const list = Array.isArray(data) ? data : (data.list || data.records || [])
        setBrands(list)
      })
      .catch(() => setBrands([]))
      .finally(() => setLoadingBrands(false))
  }, [])

  // Fetch series
  const fetchSeries = useCallback((brandId) => {
    if (!brandId) {
      setSeriesList([])
      return
    }
    setLoadingSeries(true)
    getSeries({ brandId, page: 1, size: 1000 })
      .then((data) => {
        const list = Array.isArray(data) ? data : (data.list || data.records || [])
        setSeriesList(list)
      })
      .catch(() => setSeriesList([]))
      .finally(() => setLoadingSeries(false))
  }, [])

  // Fetch models
  const fetchModels = useCallback((seriesId) => {
    if (!seriesId) {
      setModelsList([])
      return
    }
    setLoadingModels(true)
    getModels({ seriesId, page: 1, size: 1000 })
      .then((data) => {
        const list = Array.isArray(data) ? data : (data.list || data.records || [])
        setModelsList(list)
      })
      .catch(() => setModelsList([]))
      .finally(() => setLoadingModels(false))
  }, [])

  useEffect(() => {
    fetchBrands()
  }, [fetchBrands])

  const handleSelectBrand = (brandId) => {
    setSelectedBrandId(brandId)
    setSelectedSeriesId(null)
    setModelsList([])
    setTopBrandId(String(brandId))
    fetchSeries(brandId)
  }

  const handleSelectSeries = (seriesId) => {
    setSelectedSeriesId(seriesId)
    fetchModels(seriesId)
  }

  const handleTopBrandChange = (brandId) => {
    setTopBrandId(brandId)
    setShowBrandDropdown(false)
    if (brandId) {
      const id = Number(brandId)
      handleSelectBrand(id)
    } else {
      setSelectedBrandId(null)
      setSelectedSeriesId(null)
      setSeriesList([])
      setModelsList([])
    }
  }

  const handleRefresh = () => {
    fetchBrands()
    if (selectedBrandId) fetchSeries(selectedBrandId)
    if (selectedSeriesId) fetchModels(selectedSeriesId)
  }

  // Brand modal
  const openBrandModal = (brand = null) => {
    if (brand) {
      setEditingBrand(brand)
      setBrandForm({ name: brand.name || '', logoUrl: brand.logoUrl || '', status: brand.status || 'ACTIVE' })
    } else {
      setEditingBrand(null)
      setBrandForm({ ...emptyBrandForm })
    }
    setBrandModalOpen(true)
  }

  const closeBrandModal = () => {
    setBrandModalOpen(false)
    setEditingBrand(null)
    setBrandForm({ ...emptyBrandForm })
  }

  const handleSaveBrand = async () => {
    try {
      if (editingBrand) {
        await updateBrand(editingBrand.id, brandForm)
      } else {
        await createBrand(brandForm)
      }
      closeBrandModal()
      fetchBrands()
    } catch (err) {
      alert('保存失败: ' + (err.message || '未知错误'))
    }
  }

  // Series modal
  const openSeriesModal = (series = null) => {
    if (!selectedBrandId) {
      alert('请先选择一个品牌')
      return
    }
    if (series) {
      setEditingSeries(series)
      setSeriesForm({ name: series.name || '', brandId: series.brandId || selectedBrandId, status: series.status || 'ACTIVE' })
    } else {
      setEditingSeries(null)
      setSeriesForm({ ...emptySeriesForm, brandId: selectedBrandId })
    }
    setSeriesModalOpen(true)
  }

  const closeSeriesModal = () => {
    setSeriesModalOpen(false)
    setEditingSeries(null)
    setSeriesForm({ ...emptySeriesForm })
  }

  const handleSaveSeries = async () => {
    try {
      if (editingSeries) {
        await updateSeries(editingSeries.id, seriesForm)
      } else {
        await createSeries(seriesForm)
      }
      closeSeriesModal()
      fetchSeries(selectedBrandId)
    } catch (err) {
      alert('保存失败: ' + (err.message || '未知错误'))
    }
  }

  // Model modal
  const openModelModal = (model = null) => {
    if (!selectedSeriesId) {
      alert('请先选择一个车系')
      return
    }
    if (model) {
      setEditingModel(model)
      setModelForm({
        name: model.name || '',
        year: model.year || new Date().getFullYear(),
        price: model.price || 0,
        seriesId: model.seriesId || selectedSeriesId,
        status: model.status || 'ACTIVE',
      })
    } else {
      setEditingModel(null)
      setModelForm({ ...emptyModelForm, seriesId: selectedSeriesId })
    }
    setModelModalOpen(true)
  }

  const closeModelModal = () => {
    setModelModalOpen(false)
    setEditingModel(null)
    setModelForm({ ...emptyModelForm })
  }

  const handleSaveModel = async () => {
    try {
      if (editingModel) {
        await updateModel(editingModel.id, modelForm)
      } else {
        await createModel(modelForm)
      }
      closeModelModal()
      fetchModels(selectedSeriesId)
    } catch (err) {
      alert('保存失败: ' + (err.message || '未知错误'))
    }
  }

  // Delete
  const confirmDelete = async () => {
    if (!deleteTarget || !deleteType) return
    try {
      if (deleteType === 'brand') {
        await deleteBrand(deleteTarget.id)
        if (selectedBrandId === deleteTarget.id) {
          setSelectedBrandId(null)
          setSelectedSeriesId(null)
          setSeriesList([])
          setModelsList([])
          setTopBrandId('')
        }
        fetchBrands()
      } else if (deleteType === 'series') {
        await deleteSeries(deleteTarget.id)
        if (selectedSeriesId === deleteTarget.id) {
          setSelectedSeriesId(null)
          setModelsList([])
        }
        fetchSeries(selectedBrandId)
      } else if (deleteType === 'model') {
        await deleteModel(deleteTarget.id)
        fetchModels(selectedSeriesId)
      }
      setDeleteTarget(null)
      setDeleteType(null)
    } catch (err) {
      alert('删除失败: ' + (err.message || '未知错误'))
    }
  }

  // Import
  const handleImportFileChange = (e) => {
    const file = e.target.files?.[0]
    if (file) setImportFile(file)
  }

  const handleImport = async () => {
    if (!importFile) return
    const formData = new FormData()
    formData.append('file', importFile)
    setImportLoading(true)
    setImportResult(null)
    try {
      const res = await importModels(formData)
      setImportResult({ success: true, data: res })
    } catch (err) {
      setImportResult({ success: false, message: err.message || '导入失败' })
    } finally {
      setImportLoading(false)
    }
  }

  const closeImportModal = () => {
    setImportModalOpen(false)
    setImportFile(null)
    setImportResult(null)
    setImportLoading(false)
  }

  const selectedBrand = brands.find((b) => b.id === selectedBrandId)
  const selectedSeries = seriesList.find((s) => s.id === selectedSeriesId)

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">车型库</h2>
        <p className="text-[12px] text-gray-500 mt-0.5">管理品牌、车系与车型数据，维护平台标准车型库。</p>
      </div>

      {/* Top bar */}
      <div className="flex items-center gap-3">
        <div className="relative">
          <button
            onClick={() => setShowBrandDropdown(!showBrandDropdown)}
            className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors min-w-[160px]"
          >
            {selectedBrand ? selectedBrand.name : '全部品牌'}
            <ChevronDown className="w-3 h-3 ml-auto" />
          </button>
          {showBrandDropdown && (
            <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg py-1 z-20 min-w-[180px] max-h-64 overflow-y-auto">
              <button
                onClick={() => handleTopBrandChange('')}
                className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50 transition-colors ${!topBrandId ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
              >
                全部品牌
              </button>
              {brands.map((b) => (
                <button
                  key={b.id}
                  onClick={() => handleTopBrandChange(String(b.id))}
                  className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50 transition-colors ${topBrandId === String(b.id) ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
                >
                  {b.name}
                </button>
              ))}
            </div>
          )}
        </div>
        <button
          onClick={handleRefresh}
          className="flex items-center gap-1.5 px-3 py-2 bg-white border border-gray-200 rounded-xl text-xs text-gray-600 hover:bg-gray-50 transition-colors"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          刷新
        </button>
      </div>

      {/* Three panels */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Brand Panel */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden flex flex-col">
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
            <h3 className="text-sm font-bold text-gray-900">品牌</h3>
            <button
              onClick={() => openBrandModal()}
              className="flex items-center gap-1 px-2 py-1 bg-indigo-600 hover:bg-indigo-700 text-white text-[11px] font-medium rounded-lg transition-colors"
            >
              <Plus className="w-3 h-3" />
              添加
            </button>
          </div>
          <div className="flex-1 overflow-auto max-h-[600px]">
            {loadingBrands ? (
              <div className="p-4 space-y-2">
                {[1, 2, 3].map((i) => (
                  <div key={i} className="h-10 bg-gray-100 rounded-lg animate-pulse" />
                ))}
              </div>
            ) : brands.length === 0 ? (
              <div className="p-8 text-center text-gray-400 text-sm">暂无品牌数据</div>
            ) : (
              <table className="w-full text-xs">
                <thead>
                  <tr className="bg-gray-50 border-b border-gray-100">
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">品牌</th>
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">状态</th>
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {brands.map((b) => (
                    <tr
                      key={b.id}
                      onClick={() => handleSelectBrand(b.id)}
                      className={`border-b border-gray-50 cursor-pointer transition-colors ${
                        selectedBrandId === b.id ? 'bg-indigo-50/60' : 'hover:bg-gray-50/50'
                      }`}
                    >
                      <td className="px-4 py-2">
                        <div className="flex items-center gap-2">
                          {b.logoUrl ? (
                            <img src={b.logoUrl} alt="" className="w-6 h-6 rounded object-cover bg-gray-100" />
                          ) : (
                            <div className="w-6 h-6 rounded bg-gray-100 flex items-center justify-center">
                              <ImageIcon className="w-3 h-3 text-gray-400" />
                            </div>
                          )}
                          <span className="font-medium text-gray-900 text-sm">{b.name}</span>
                        </div>
                      </td>
                      <td className="px-4 py-2">
                        <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${getStatusColor(b.status)}`}>
                          {getStatusText(b.status)}
                        </span>
                      </td>
                      <td className="px-4 py-2">
                        <div className="flex items-center gap-1">
                          <button
                            onClick={(e) => { e.stopPropagation(); openBrandModal(b) }}
                            className="flex items-center gap-1 px-1.5 py-1 rounded-md text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors"
                          >
                            <Pencil className="w-3 h-3" />
                          </button>
                          <button
                            onClick={(e) => { e.stopPropagation(); setDeleteTarget(b); setDeleteType('brand') }}
                            className="flex items-center gap-1 px-1.5 py-1 rounded-md text-[11px] text-red-500 hover:bg-red-50 transition-colors"
                          >
                            <Trash2 className="w-3 h-3" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>

        {/* Series Panel */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden flex flex-col">
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
            <h3 className="text-sm font-bold text-gray-900">
              {selectedBrand ? `${selectedBrand.name} - 车系` : '车系'}
            </h3>
            <button
              onClick={() => openSeriesModal()}
              disabled={!selectedBrandId}
              className="flex items-center gap-1 px-2 py-1 bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-300 text-white text-[11px] font-medium rounded-lg transition-colors"
            >
              <Plus className="w-3 h-3" />
              添加
            </button>
          </div>
          <div className="flex-1 overflow-auto max-h-[600px]">
            {!selectedBrandId ? (
              <div className="p-8 text-center text-gray-400 text-sm">请先选择一个品牌</div>
            ) : loadingSeries ? (
              <div className="p-4 space-y-2">
                {[1, 2, 3].map((i) => (
                  <div key={i} className="h-10 bg-gray-100 rounded-lg animate-pulse" />
                ))}
              </div>
            ) : seriesList.length === 0 ? (
              <div className="p-8 text-center text-gray-400 text-sm">该车系下暂无车系数据</div>
            ) : (
              <table className="w-full text-xs">
                <thead>
                  <tr className="bg-gray-50 border-b border-gray-100">
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">车系名称</th>
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">状态</th>
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {seriesList.map((s) => (
                    <tr
                      key={s.id}
                      onClick={() => handleSelectSeries(s.id)}
                      className={`border-b border-gray-50 cursor-pointer transition-colors ${
                        selectedSeriesId === s.id ? 'bg-indigo-50/60' : 'hover:bg-gray-50/50'
                      }`}
                    >
                      <td className="px-4 py-2">
                        <span className="font-medium text-gray-900 text-sm">{s.name}</span>
                      </td>
                      <td className="px-4 py-2">
                        <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${getStatusColor(s.status)}`}>
                          {getStatusText(s.status)}
                        </span>
                      </td>
                      <td className="px-4 py-2">
                        <div className="flex items-center gap-1">
                          <button
                            onClick={(e) => { e.stopPropagation(); openSeriesModal(s) }}
                            className="flex items-center gap-1 px-1.5 py-1 rounded-md text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors"
                          >
                            <Pencil className="w-3 h-3" />
                          </button>
                          <button
                            onClick={(e) => { e.stopPropagation(); setDeleteTarget(s); setDeleteType('series') }}
                            className="flex items-center gap-1 px-1.5 py-1 rounded-md text-[11px] text-red-500 hover:bg-red-50 transition-colors"
                          >
                            <Trash2 className="w-3 h-3" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>

        {/* Model Panel */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden flex flex-col">
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
            <h3 className="text-sm font-bold text-gray-900">
              {selectedSeries ? `${selectedSeries.name} - 车型` : '车型'}
            </h3>
            <div className="flex items-center gap-1.5">
              <button
                onClick={() => setImportModalOpen(true)}
                disabled={!selectedSeriesId}
                className="flex items-center gap-1 px-2 py-1 bg-white border border-gray-200 hover:bg-gray-50 disabled:opacity-50 text-gray-700 text-[11px] font-medium rounded-lg transition-colors"
              >
                <Upload className="w-3 h-3" />
                导入
              </button>
              <button
                onClick={() => openModelModal()}
                disabled={!selectedSeriesId}
                className="flex items-center gap-1 px-2 py-1 bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-300 text-white text-[11px] font-medium rounded-lg transition-colors"
              >
                <Plus className="w-3 h-3" />
                添加
              </button>
            </div>
          </div>
          <div className="flex-1 overflow-auto max-h-[600px]">
            {!selectedSeriesId ? (
              <div className="p-8 text-center text-gray-400 text-sm">请先选择一个车系</div>
            ) : loadingModels ? (
              <div className="p-4 space-y-2">
                {[1, 2, 3].map((i) => (
                  <div key={i} className="h-10 bg-gray-100 rounded-lg animate-pulse" />
                ))}
              </div>
            ) : modelsList.length === 0 ? (
              <div className="p-8 text-center text-gray-400 text-sm">该车系下暂无车型数据</div>
            ) : (
              <table className="w-full text-xs">
                <thead>
                  <tr className="bg-gray-50 border-b border-gray-100">
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">车型名称</th>
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">年份</th>
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">价格</th>
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">状态</th>
                    <th className="text-left px-4 py-2 font-medium text-gray-500 text-xs">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {modelsList.map((m) => (
                    <tr key={m.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                      <td className="px-4 py-2">
                        <span className="font-medium text-gray-900 text-sm">{m.name}</span>
                      </td>
                      <td className="px-4 py-2 text-sm text-gray-800 font-mono">{m.year || '-'}</td>
                      <td className="px-4 py-2 text-sm text-red-600 font-bold font-mono">
                        {m.price != null ? `¥${Number(m.price).toFixed(2)}` : '-'}
                      </td>
                      <td className="px-4 py-2">
                        <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${getStatusColor(m.status)}`}>
                          {getStatusText(m.status)}
                        </span>
                      </td>
                      <td className="px-4 py-2">
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => openModelModal(m)}
                            className="flex items-center gap-1 px-1.5 py-1 rounded-md text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors"
                          >
                            <Pencil className="w-3 h-3" />
                          </button>
                          <button
                            onClick={() => { setDeleteTarget(m); setDeleteType('model') }}
                            className="flex items-center gap-1 px-1.5 py-1 rounded-md text-[11px] text-red-500 hover:bg-red-50 transition-colors"
                          >
                            <Trash2 className="w-3 h-3" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>

      {/* Brand Modal */}
      {brandModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={closeBrandModal}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">{editingBrand ? '编辑品牌' : '添加品牌'}</h3>
              <button onClick={closeBrandModal} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              <div>
                <label className="block text-xs text-gray-500 mb-1">品牌名称</label>
                <input
                  type="text"
                  value={brandForm.name}
                  onChange={(e) => setBrandForm((prev) => ({ ...prev, name: e.target.value }))}
                  placeholder="请输入品牌名称"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">Logo URL</label>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    value={brandForm.logoUrl}
                    onChange={(e) => setBrandForm((prev) => ({ ...prev, logoUrl: e.target.value }))}
                    placeholder="请输入 Logo 地址"
                    className="flex-1 border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                  />
                  <div className="w-10 h-10 rounded-lg bg-gray-100 flex items-center justify-center text-[10px] text-gray-400 border border-dashed border-gray-300">
                    <ImageIcon className="w-4 h-4" />
                  </div>
                </div>
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={closeBrandModal} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                取消
              </button>
              <button onClick={handleSaveBrand} className="px-4 py-2 text-xs text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg transition-colors">
                保存
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Series Modal */}
      {seriesModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={closeSeriesModal}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">{editingSeries ? '编辑车系' : '添加车系'}</h3>
              <button onClick={closeSeriesModal} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              <div>
                <label className="block text-xs text-gray-500 mb-1">车系名称</label>
                <input
                  type="text"
                  value={seriesForm.name}
                  onChange={(e) => setSeriesForm((prev) => ({ ...prev, name: e.target.value }))}
                  placeholder="请输入车系名称"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={closeSeriesModal} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                取消
              </button>
              <button onClick={handleSaveSeries} className="px-4 py-2 text-xs text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg transition-colors">
                保存
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Model Modal */}
      {modelModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={closeModelModal}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">{editingModel ? '编辑车型' : '添加车型'}</h3>
              <button onClick={closeModelModal} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              <div>
                <label className="block text-xs text-gray-500 mb-1">车型名称</label>
                <input
                  type="text"
                  value={modelForm.name}
                  onChange={(e) => setModelForm((prev) => ({ ...prev, name: e.target.value }))}
                  placeholder="请输入车型名称"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">年份</label>
                <input
                  type="number"
                  value={modelForm.year}
                  onChange={(e) => setModelForm((prev) => ({ ...prev, year: Number(e.target.value) }))}
                  placeholder="例如 2024"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">价格（元）</label>
                <input
                  type="number"
                  value={modelForm.price}
                  onChange={(e) => setModelForm((prev) => ({ ...prev, price: Number(e.target.value) }))}
                  placeholder="0"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={closeModelModal} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                取消
              </button>
              <button onClick={handleSaveModel} className="px-4 py-2 text-xs text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg transition-colors">
                保存
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Import Modal */}
      {importModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={closeImportModal}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">导入车型</h3>
              <button onClick={closeImportModal} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              {!importResult ? (
                <>
                  <div className="border-2 border-dashed border-gray-200 rounded-xl p-6 text-center hover:border-indigo-300 transition-colors">
                    <Upload className="w-8 h-8 text-gray-400 mx-auto mb-2" />
                    <p className="text-xs text-gray-500 mb-3">选择 Excel 文件上传，支持 .xlsx / .xls 格式</p>
                    <label className="inline-flex items-center gap-1.5 px-3 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-medium rounded-lg transition-colors cursor-pointer">
                      <Plus className="w-3.5 h-3.5" />
                      选择文件
                      <input
                        type="file"
                        accept=".xlsx,.xls"
                        onChange={handleImportFileChange}
                        className="hidden"
                      />
                    </label>
                    {importFile && (
                      <p className="mt-3 text-xs text-gray-700 font-medium">已选择：{importFile.name}</p>
                    )}
                  </div>
                </>
              ) : importResult.success ? (
                <div className="space-y-3">
                  <div className="bg-emerald-50 text-emerald-700 rounded-xl p-4 text-sm font-medium">
                    导入成功
                  </div>
                  {importResult.data && (
                    <div className="text-xs text-gray-600 space-y-1">
                      {typeof importResult.data === 'object' && !Array.isArray(importResult.data) ? (
                        Object.entries(importResult.data).map(([k, v]) => (
                          <div key={k} className="flex justify-between">
                            <span className="text-gray-500">{k}</span>
                            <span className="font-medium">{String(v)}</span>
                          </div>
                        ))
                      ) : (
                        <pre className="bg-gray-50 rounded-lg p-3 overflow-auto">{JSON.stringify(importResult.data, null, 2)}</pre>
                      )}
                    </div>
                  )}
                </div>
              ) : (
                <div className="bg-red-50 text-red-700 rounded-xl p-4 text-sm">
                  导入失败：{importResult.message}
                </div>
              )}
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={closeImportModal} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                {importResult ? '关闭' : '取消'}
              </button>
              {!importResult && (
                <button
                  onClick={handleImport}
                  disabled={!importFile || importLoading}
                  className="px-4 py-2 text-xs text-white bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-300 rounded-lg transition-colors"
                >
                  {importLoading ? '上传中...' : '开始导入'}
                </button>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirm Modal */}
      {deleteTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => { setDeleteTarget(null); setDeleteType(null) }}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="p-5">
              <h3 className="text-base font-bold text-gray-900 mb-2">确认删除</h3>
              <p className="text-sm text-gray-600">
                确定要删除{deleteType === 'brand' ? '品牌' : deleteType === 'series' ? '车系' : '车型'}「{deleteTarget.name}」吗？此操作不可撤销。
              </p>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={() => { setDeleteTarget(null); setDeleteType(null) }}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={confirmDelete}
                className="px-4 py-2 text-xs text-white bg-red-500 hover:bg-red-600 rounded-lg transition-colors"
              >
                确认删除
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
