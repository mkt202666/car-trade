import { useState, useEffect } from 'react'
import { Plus, Edit2, Trash2, Globe, X } from 'lucide-react'
import { getExportRegions, createExportRegion, updateExportRegion, deleteExportRegion, toggleExportRegionStatus } from '../../api/export'
import { SkeletonCard } from '../../components/Skeleton'

const regionGroups = ['独联体', '非洲', '中东', '东南亚', '南美', '欧洲', '其他']

export default function ExportConfig() {
  const [regions, setRegions] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [formData, setFormData] = useState({
    name: '',
    code: '',
    flag: '',
    regionGroup: '',
    constraints: [],
    requirements: '',
    status: 'ACTIVE',
    sortOrder: 999
  })

  // 加载数据
  useEffect(() => {
    loadRegions()
  }, [])

  const loadRegions = async () => {
    setLoading(true)
    try {
      const response = await getExportRegions()
      if (response.data && response.code === 200) {
        setRegions(response.data)
      }
    } catch (error) {
      console.error('加载出口地区配置失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 打开新增弹窗
  const handleAdd = () => {
    setEditingId(null)
    setFormData({
      name: '',
      code: '',
      flag: '',
      regionGroup: '',
      constraints: [],
      requirements: '',
      status: 'ACTIVE',
      sortOrder: 999
    })
    setModalOpen(true)
  }

  // 打开编辑弹窗
  const handleEdit = (region) => {
    setEditingId(region.id)
    setFormData({
      name: region.name,
      code: region.code,
      flag: region.flag || '',
      regionGroup: region.regionGroup || '',
      constraints: region.constraints ? JSON.parse(region.constraints) : [],
      requirements: region.requirements || '',
      status: region.status,
      sortOrder: region.sortOrder || 999
    })
    setModalOpen(true)
  }

  // 删除
  const handleDelete = async (id) => {
    if (!confirm('确定要删除这个出口地区配置吗?')) return
    
    try {
      const response = await deleteExportRegion(id)
      if (response.code === 200) {
        loadRegions()
      } else {
        alert(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      alert('删除失败,请重试')
    }
  }

  // 切换状态
  const handleToggleStatus = async (id) => {
    try {
      const response = await toggleExportRegionStatus(id)
      if (response.code === 200) {
        loadRegions()
      }
    } catch (error) {
      console.error('切换状态失败:', error)
    }
  }

  // 提交表单
  const handleSubmit = async () => {
    // 验证必填字段
    if (!formData.name || !formData.code) {
      alert('请填写国家名称和代码')
      return
    }

    try {
      const submitData = {
        ...formData,
        constraints: JSON.stringify(formData.constraints)
      }

      let response
      if (editingId) {
        response = await updateExportRegion(editingId, submitData)
      } else {
        response = await createExportRegion(submitData)
      }

      if (response.code === 200) {
        setModalOpen(false)
        loadRegions()
      } else {
        alert(response.message || '保存失败')
      }
    } catch (error) {
      console.error('保存失败:', error)
      alert('保存失败,请重试')
    }
  }

  // 添加约束条件
  const addConstraint = () => {
    setFormData({
      ...formData,
      constraints: [...formData.constraints, { field: '', op: '==', value: '' }]
    })
  }

  // 删除约束条件
  const removeConstraint = (index) => {
    const newConstraints = formData.constraints.filter((_, i) => i !== index)
    setFormData({ ...formData, constraints: newConstraints })
  }

  // 更新约束条件
  const updateConstraint = (index, field, value) => {
    const newConstraints = [...formData.constraints]
    newConstraints[index] = { ...newConstraints[index], [field]: value }
    setFormData({ ...formData, constraints: newConstraints })
  }

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-gray-900">出口配置菜单</h2>
          <p className="text-sm text-gray-500 mt-1">管理前端展示的出口国家、要求及展示条件</p>
        </div>
        <button 
          onClick={handleAdd}
          className="flex items-center gap-1.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold px-4 py-2 rounded-xl transition-colors self-start"
        >
          <Plus className="w-3.5 h-3.5" />
          新增地区配置
        </button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-gray-50">
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">地区名称/图标</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">所属地区分组</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">参数约束条件</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">出口要求展示描述</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">状态</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {loading ? (
                <tr>
                  <td colSpan="6" className="px-4 py-8">
                    <SkeletonCard />
                  </td>
                </tr>
              ) : regions.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-4 py-8 text-center text-gray-400">
                    暂无出口地区配置
                  </td>
                </tr>
              ) : (
                regions.map((region) => (
                  <tr key={region.code} className="hover:bg-gray-50/50 transition-colors">
                    <td className="px-4 py-3 text-sm text-gray-800">
                      <div className="flex items-center gap-2">
                        <span className="text-xl">{region.flag}</span>
                        <div>
                          <div className="font-medium text-gray-900">{region.name}</div>
                          <div className="text-[11px] text-gray-400 font-mono">{region.code}</div>
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600">{region.regionGroup || '-'}</td>
                    <td className="px-4 py-3 text-sm text-gray-800">
                      <div className="flex flex-wrap gap-x-3 gap-y-1">
                        {region.constraints && JSON.parse(region.constraints).map((c, i) => (
                          <span key={i} className="inline-flex items-center gap-1 text-xs">
                            <span className="text-gray-500">{c.field}</span>
                            <span className="text-indigo-500 font-mono font-semibold">{c.op}</span>
                            <span className="text-gray-900 font-medium">{c.value}</span>
                          </span>
                        ))}
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600 max-w-[300px]">
                      <div className="whitespace-pre-line text-xs leading-relaxed">{region.requirements || '-'}</div>
                    </td>
                    <td className="px-4 py-3 text-sm">
                      <button
                        onClick={() => handleToggleStatus(region.id)}
                        className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-medium ${
                          region.status === 'ACTIVE' 
                            ? 'bg-emerald-50 text-emerald-600 hover:bg-emerald-100' 
                            : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
                        }`}
                      >
                        <span className={`w-1.5 h-1.5 rounded-full ${region.status === 'ACTIVE' ? 'bg-emerald-500' : 'bg-gray-400'}`} />
                        {region.status}
                      </button>
                    </td>
                    <td className="px-4 py-3 text-sm">
                      <div className="flex items-center gap-1">
                        <button 
                          onClick={() => handleEdit(region)}
                          className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-indigo-600 transition-colors" 
                          title="编辑"
                        >
                          <Edit2 className="w-3.5 h-3.5" />
                        </button>
                        <button 
                          onClick={() => handleDelete(region.id)}
                          className="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors" 
                          title="删除"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
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

      {/* Modal */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <div className="sticky top-0 bg-white border-b border-gray-100 px-6 py-4 flex items-center justify-between">
              <h3 className="text-lg font-bold text-gray-900">
                {editingId ? '编辑出口地区配置' : '新增出口地区配置'}
              </h3>
              <button 
                onClick={() => setModalOpen(false)}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
              >
                <X className="w-5 h-5 text-gray-400" />
              </button>
            </div>

            <div className="p-6 space-y-4">
              {/* 基本信息 */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">国家名称 *</label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    placeholder="如:俄罗斯"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">国家代码 *</label>
                  <input
                    type="text"
                    value={formData.code}
                    onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                    className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    placeholder="如:RU"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">国旗emoji</label>
                  <input
                    type="text"
                    value={formData.flag}
                    onChange={(e) => setFormData({ ...formData, flag: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    placeholder="如:🇷🇺"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">地区分组</label>
                  <select
                    value={formData.regionGroup}
                    onChange={(e) => setFormData({ ...formData, regionGroup: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  >
                    <option value="">请选择</option>
                    {regionGroups.map(group => (
                      <option key={group} value={group}>{group}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* 约束条件 */}
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-2">参数约束条件</label>
                {formData.constraints.map((constraint, index) => (
                  <div key={index} className="flex items-center gap-2 mb-2">
                    <input
                      type="text"
                      value={constraint.field}
                      onChange={(e) => updateConstraint(index, 'field', e.target.value)}
                      className="flex-1 px-3 py-2 border border-gray-200 rounded-lg text-sm"
                      placeholder="字段名(如:左舵/右舵)"
                    />
                    <select
                      value={constraint.op}
                      onChange={(e) => updateConstraint(index, 'op', e.target.value)}
                      className="px-3 py-2 border border-gray-200 rounded-lg text-sm"
                    >
                      <option value="==">==</option>
                      <option value="!=">!=</option>
                      <option value="包含">包含</option>
                      <option value=">">&gt;</option>
                      <option value="<">&lt;</option>
                    </select>
                    <input
                      type="text"
                      value={constraint.value}
                      onChange={(e) => updateConstraint(index, 'value', e.target.value)}
                      className="flex-1 px-3 py-2 border border-gray-200 rounded-lg text-sm"
                      placeholder="值"
                    />
                    <button
                      onClick={() => removeConstraint(index)}
                      className="p-2 hover:bg-red-50 text-red-500 rounded-lg"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  </div>
                ))}
                <button
                  onClick={addConstraint}
                  className="text-xs text-indigo-600 hover:text-indigo-700 font-medium"
                >
                  + 添加约束条件
                </button>
              </div>

              {/* 出口要求 */}
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">出口要求描述</label>
                <textarea
                  value={formData.requirements}
                  onChange={(e) => setFormData({ ...formData, requirements: e.target.value })}
                  rows={4}
                  className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  placeholder="1. 必须为左舵车辆&#10;2. 排放标准不低于国VI&#10;3. 需提供GLONASS认证"
                />
              </div>

              {/* 排序号 */}
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">排序号</label>
                <input
                  type="number"
                  value={formData.sortOrder}
                  onChange={(e) => setFormData({ ...formData, sortOrder: parseInt(e.target.value) || 999 })}
                  className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  placeholder="数字越小越靠前"
                />
              </div>
            </div>

            <div className="sticky bottom-0 bg-gray-50 border-t border-gray-100 px-6 py-4 flex justify-end gap-3">
              <button
                onClick={() => setModalOpen(false)}
                className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={handleSubmit}
                className="px-4 py-2 text-sm bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-lg transition-colors"
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
