import { useState, useEffect, useCallback } from 'react'
import {
  Plus, Pencil, Trash2, Save, X, ImageIcon, Link2, ArrowUpDown,
} from 'lucide-react'
import {
  getBannerList, createBanner, updateBanner, deleteBanner,
  updateBannerStatus, sortBanners, getConfig, updateConfig,
} from '../../api/resource'
import { formatDate, getStatusColor, getStatusText } from '../../utils/format'

const TABS = [
  { key: 'banner', label: 'Banner 管理' },
  { key: 'config', label: '配置文本' },
]

const TYPE_MAP = {
  PAGE: { label: '页面', color: 'bg-blue-100 text-blue-700' },
  POPUP: { label: '弹窗', color: 'bg-purple-100 text-purple-700' },
}

const CONFIG_ITEMS = [
  { key: 'trade-rules', title: '交易规则' },
  { key: 'user-agreement', title: '用户协议' },
  { key: 'privacy-policy', title: '隐私条款' },
  { key: 'contract-template', title: '合同模板' },
]

const emptyBannerForm = {
  title: '',
  imageUrl: '',
  type: 'PAGE',
  linkUrl: '',
  sortOrder: 0,
}

export default function ResourceManage() {
  const [activeTab, setActiveTab] = useState('banner')

  // Banner states
  const [banners, setBanners] = useState([])
  const [bannerLoading, setBannerLoading] = useState(false)
  const [bannerError, setBannerError] = useState(null)
  const [bannerModalOpen, setBannerModalOpen] = useState(false)
  const [editingBanner, setEditingBanner] = useState(null)
  const [bannerForm, setBannerForm] = useState({ ...emptyBannerForm })
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [sortMap, setSortMap] = useState({})

  // Config states
  const [configMap, setConfigMap] = useState({})
  const [configLoadingMap, setConfigLoadingMap] = useState({})
  const [configModalOpen, setConfigModalOpen] = useState(false)
  const [editingConfigKey, setEditingConfigKey] = useState(null)
  const [configForm, setConfigForm] = useState({ content: '' })

  // Load banners
  const fetchBanners = useCallback(() => {
    setBannerLoading(true)
    setBannerError(null)
    getBannerList({ page: 1, size: 100 })
      .then((data) => {
        const list = Array.isArray(data) ? data : (data.list || data.records || [])
        setBanners(list)
        setSortMap({})
      })
      .catch((err) => {
        setBannerError(err.message || '加载失败')
        setBanners([])
      })
      .finally(() => setBannerLoading(false))
  }, [])

  useEffect(() => {
    if (activeTab === 'banner') fetchBanners()
  }, [activeTab, fetchBanners])

  // Load configs
  const fetchConfigs = useCallback(() => {
    CONFIG_ITEMS.forEach((item) => {
      setConfigLoadingMap((prev) => ({ ...prev, [item.key]: true }))
      getConfig(item.key)
        .then((data) => {
          setConfigMap((prev) => ({ ...prev, [item.key]: data || {} }))
        })
        .catch(() => {
          setConfigMap((prev) => ({ ...prev, [item.key]: { content: '', updatedAt: null } }))
        })
        .finally(() => {
          setConfigLoadingMap((prev) => ({ ...prev, [item.key]: false }))
        })
    })
  }, [])

  useEffect(() => {
    if (activeTab === 'config') fetchConfigs()
  }, [activeTab, fetchConfigs])

  // Banner actions
  const openBannerModal = (banner = null) => {
    if (banner) {
      setEditingBanner(banner)
      setBannerForm({
        title: banner.title || '',
        imageUrl: banner.imageUrl || banner.thumbnail || '',
        type: banner.type || 'PAGE',
        linkUrl: banner.linkUrl || '',
        sortOrder: banner.sortOrder ?? 0,
      })
    } else {
      setEditingBanner(null)
      setBannerForm({ ...emptyBannerForm })
    }
    setBannerModalOpen(true)
  }

  const closeBannerModal = () => {
    setBannerModalOpen(false)
    setEditingBanner(null)
    setBannerForm({ ...emptyBannerForm })
  }

  const handleSaveBanner = async () => {
    try {
      if (editingBanner) {
        await updateBanner(editingBanner.id, bannerForm)
      } else {
        await createBanner(bannerForm)
      }
      closeBannerModal()
      fetchBanners()
    } catch (err) {
      alert('保存失败: ' + (err.message || '未知错误'))
    }
  }

  const handleDeleteBanner = async () => {
    if (!deleteTarget) return
    try {
      await deleteBanner(deleteTarget.id)
      setDeleteTarget(null)
      fetchBanners()
    } catch (err) {
      alert('删除失败: ' + (err.message || '未知错误'))
    }
  }

  const handleToggleBannerStatus = async (banner) => {
    const newStatus = banner.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    try {
      await updateBannerStatus(banner.id, { status: newStatus })
      fetchBanners()
    } catch (err) {
      alert('操作失败: ' + (err.message || '未知错误'))
    }
  }

  const handleSortChange = (id, value) => {
    setSortMap((prev) => ({ ...prev, [id]: Number(value) }))
  }

  const handleSaveSort = async () => {
    const items = Object.entries(sortMap).map(([id, sortOrder]) => ({ id: Number(id), sortOrder }))
    if (items.length === 0) return
    try {
      await sortBanners({ items })
      setSortMap({})
      fetchBanners()
    } catch (err) {
      alert('保存排序失败: ' + (err.message || '未知错误'))
    }
  }

  // Config actions
  const openConfigModal = (key) => {
    const data = configMap[key] || { content: '' }
    setEditingConfigKey(key)
    setConfigForm({ content: data.content || '' })
    setConfigModalOpen(true)
  }

  const closeConfigModal = () => {
    setConfigModalOpen(false)
    setEditingConfigKey(null)
    setConfigForm({ content: '' })
  }

  const handleSaveConfig = async () => {
    if (!editingConfigKey) return
    try {
      await updateConfig(editingConfigKey, { content: configForm.content })
      setConfigMap((prev) => ({
        ...prev,
        [editingConfigKey]: { ...(prev[editingConfigKey] || {}), content: configForm.content, updatedAt: new Date().toISOString() },
      }))
      closeConfigModal()
    } catch (err) {
      alert('保存失败: ' + (err.message || '未知错误'))
    }
  }

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">资源管理</h2>
        <p className="text-[12px] text-gray-500 mt-0.5">管理首页 Banner 与平台配置文本内容。</p>
      </div>

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-gray-200">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`px-4 py-2 text-xs font-semibold transition-colors border-b-2 -mb-[1px] ${
              activeTab === tab.key
                ? 'text-indigo-600 border-indigo-600'
                : 'text-gray-500 border-transparent hover:text-gray-700'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Banner Tab */}
      {activeTab === 'banner' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div className="text-xs text-gray-500">共 {banners.length} 条 Banner</div>
            <button
              onClick={() => openBannerModal()}
              className="flex items-center gap-1.5 px-3 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-medium rounded-xl transition-colors"
            >
              <Plus className="w-3.5 h-3.5" />
              添加 Banner
            </button>
          </div>

          {Object.keys(sortMap).length > 0 && (
            <div className="flex items-center gap-2">
              <button
                onClick={handleSaveSort}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-medium rounded-lg transition-colors"
              >
                <Save className="w-3.5 h-3.5" />
                保存排序
              </button>
              <button
                onClick={() => { setSortMap({}); fetchBanners(); }}
                className="px-3 py-1.5 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
            </div>
          )}

          {bannerLoading ? (
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 space-y-3">
              {[1, 2, 3].map((i) => (
                <div key={i} className="h-10 bg-gray-100 rounded-lg animate-pulse" />
              ))}
            </div>
          ) : bannerError ? (
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center text-red-500 text-sm">{bannerError}</div>
          ) : (
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-xs">
                  <thead>
                    <tr className="bg-gray-50 border-b border-gray-100">
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">封面图</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">标题</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">类型</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">链接</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">排序</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">状态</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    {banners.length === 0 ? (
                      <tr>
                        <td colSpan={7} className="px-4 py-8 text-center text-gray-400 text-sm">暂无 Banner 数据</td>
                      </tr>
                    ) : (
                      banners.map((b) => (
                        <tr key={b.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                          <td className="px-4 py-3">
                            {b.thumbnail || b.imageUrl ? (
                              <img
                                src={b.thumbnail || b.imageUrl}
                                alt=""
                                className="w-16 h-9 object-cover rounded-lg bg-gray-100"
                              />
                            ) : (
                              <div className="w-16 h-9 rounded-lg bg-gray-100 flex items-center justify-center text-[10px] text-gray-400">
                                <ImageIcon className="w-3.5 h-3.5" />
                              </div>
                            )}
                          </td>
                          <td className="px-4 py-3">
                            <div className="font-medium text-gray-900 text-sm">{b.title || '-'}</div>
                            <div className="text-[11px] text-gray-400 mt-0.5 font-mono">ID: {b.id}</div>
                          </td>
                          <td className="px-4 py-3">
                            <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${TYPE_MAP[b.type]?.color || 'bg-gray-100 text-gray-600'}`}>
                              {TYPE_MAP[b.type]?.label || b.type || '-'}
                            </span>
                          </td>
                          <td className="px-4 py-3">
                            {b.linkUrl ? (
                              <a
                                href={b.linkUrl}
                                target="_blank"
                                rel="noreferrer"
                                className="flex items-center gap-1 text-indigo-600 hover:underline max-w-[200px] truncate"
                              >
                                <Link2 className="w-3 h-3 shrink-0" />
                                <span className="truncate">{b.linkUrl}</span>
                              </a>
                            ) : (
                              <span className="text-gray-400">-</span>
                            )}
                          </td>
                          <td className="px-4 py-3">
                            <div className="flex items-center gap-1">
                              <ArrowUpDown className="w-3 h-3 text-gray-400" />
                              <input
                                type="number"
                                value={sortMap[b.id] !== undefined ? sortMap[b.id] : (b.sortOrder ?? 0)}
                                onChange={(e) => handleSortChange(b.id, e.target.value)}
                                className="w-14 border border-gray-200 rounded-lg px-2 py-1 text-xs text-gray-700 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                              />
                            </div>
                          </td>
                          <td className="px-4 py-3">
                            <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${getStatusColor(b.status)}`}>
                              {getStatusText(b.status)}
                            </span>
                          </td>
                          <td className="px-4 py-3">
                            <div className="flex items-center gap-1">
                              <button
                                onClick={() => openBannerModal(b)}
                                className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-indigo-600 hover:bg-indigo-50 transition-colors"
                              >
                                <Pencil className="w-3 h-3" />
                                编辑
                              </button>
                              <button
                                onClick={() => setDeleteTarget(b)}
                                className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-red-500 hover:bg-red-50 transition-colors"
                              >
                                <Trash2 className="w-3 h-3" />
                                删除
                              </button>
                              <button
                                onClick={() => handleToggleBannerStatus(b)}
                                className={`flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] transition-colors ${
                                  b.status === 'ACTIVE'
                                    ? 'text-gray-500 hover:bg-gray-100'
                                    : 'text-emerald-500 hover:bg-emerald-50'
                                }`}
                              >
                                {b.status === 'ACTIVE' ? '停用' : '启用'}
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Config Tab */}
      {activeTab === 'config' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {CONFIG_ITEMS.map((item) => {
            const data = configMap[item.key] || {}
            const loading = configLoadingMap[item.key]
            return (
              <div
                key={item.key}
                className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 space-y-3"
              >
                <div className="flex items-center justify-between">
                  <h3 className="text-sm font-bold text-gray-900">{item.title}</h3>
                  <span className="text-[10px] text-gray-400 font-mono">{item.key}</span>
                </div>
                {loading ? (
                  <div className="h-8 bg-gray-100 rounded-lg animate-pulse" />
                ) : (
                  <div className="text-xs text-gray-500">
                    最后更新：{data.updatedAt ? formatDate(data.updatedAt) : '未设置'}
                  </div>
                )}
                <div className="pt-1">
                  <button
                    onClick={() => openConfigModal(item.key)}
                    className="flex items-center gap-1.5 px-3 py-2 text-xs font-medium text-indigo-600 bg-indigo-50 hover:bg-indigo-100 rounded-lg transition-colors"
                  >
                    <Pencil className="w-3.5 h-3.5" />
                    编辑
                  </button>
                </div>
              </div>
            )
          })}
        </div>
      )}

      {/* Banner Modal */}
      {bannerModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={closeBannerModal}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">
                {editingBanner ? '编辑 Banner' : '添加 Banner'}
              </h3>
              <button onClick={closeBannerModal} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              <div>
                <label className="block text-xs text-gray-500 mb-1">标题</label>
                <input
                  type="text"
                  value={bannerForm.title}
                  onChange={(e) => setBannerForm((prev) => ({ ...prev, title: e.target.value }))}
                  placeholder="请输入标题"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">图片 URL</label>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    value={bannerForm.imageUrl}
                    onChange={(e) => setBannerForm((prev) => ({ ...prev, imageUrl: e.target.value }))}
                    placeholder="请输入图片地址"
                    className="flex-1 border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                  />
                  <div className="w-16 h-9 rounded-lg bg-gray-100 flex items-center justify-center text-[10px] text-gray-400 border border-dashed border-gray-300">
                    <ImageIcon className="w-3.5 h-3.5" />
                  </div>
                </div>
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">类型</label>
                <select
                  value={bannerForm.type}
                  onChange={(e) => setBannerForm((prev) => ({ ...prev, type: e.target.value }))}
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300 bg-white"
                >
                  <option value="PAGE">页面</option>
                  <option value="POPUP">弹窗</option>
                </select>
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">链接</label>
                <input
                  type="text"
                  value={bannerForm.linkUrl}
                  onChange={(e) => setBannerForm((prev) => ({ ...prev, linkUrl: e.target.value }))}
                  placeholder="请输入跳转链接"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">排序</label>
                <input
                  type="number"
                  value={bannerForm.sortOrder}
                  onChange={(e) => setBannerForm((prev) => ({ ...prev, sortOrder: Number(e.target.value) }))}
                  placeholder="0"
                  className="w-full border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={closeBannerModal}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={handleSaveBanner}
                className="px-4 py-2 text-xs text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg transition-colors"
              >
                保存
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirm Modal */}
      {deleteTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setDeleteTarget(null)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm" onClick={(e) => e.stopPropagation()}>
            <div className="p-5">
              <h3 className="text-base font-bold text-gray-900 mb-2">确认删除</h3>
              <p className="text-sm text-gray-600">
                确定要删除 Banner「{deleteTarget.title}」吗？此操作不可撤销。
              </p>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={() => setDeleteTarget(null)}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={handleDeleteBanner}
                className="px-4 py-2 text-xs text-white bg-red-500 hover:bg-red-600 rounded-lg transition-colors"
              >
                确认删除
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Config Modal */}
      {configModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={closeConfigModal}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-2xl" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">
                编辑{CONFIG_ITEMS.find((c) => c.key === editingConfigKey)?.title || ''}
              </h3>
              <button onClick={closeConfigModal} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5">
              <textarea
                value={configForm.content}
                onChange={(e) => setConfigForm((prev) => ({ ...prev, content: e.target.value }))}
                placeholder="请输入配置内容..."
                className="w-full h-64 border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-indigo-300 focus:ring-1 focus:ring-indigo-300 resize-none"
              />
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button
                onClick={closeConfigModal}
                className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={handleSaveConfig}
                className="px-4 py-2 text-xs text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg transition-colors"
              >
                保存
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
