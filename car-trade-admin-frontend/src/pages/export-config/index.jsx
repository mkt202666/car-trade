import { useState } from 'react'
import { Plus, Edit2, Trash2, Globe } from 'lucide-react'

const regions = [
  {
    flag: '🇷🇺',
    name: '俄罗斯',
    code: 'RU',
    group: '独联体',
    constraints: [
      { field: '左舵/右舵', op: '==', value: '左舵' },
      { field: '环保标准', op: '包含', value: '国VI' },
    ],
    requirements: '1. 必须为左舵车辆\n2. 排放标准不低于国VI\n3. 需提供GLONASS认证',
    status: 'ACTIVE',
  },
  {
    flag: '🇰🇿',
    name: '哈萨克斯坦',
    code: 'KZ',
    group: '独联体',
    constraints: [
      { field: '左舵/右舵', op: '==', value: '左舵' },
    ],
    requirements: '1. 车龄需低于5年\n2. 必须为左舵车辆\n3. 排放标准符合当地要求\n4. 需提供权威第三方中英文检测报告',
    status: 'ACTIVE',
  },
  {
    flag: '🇳🇬',
    name: '尼日利亚',
    code: 'NG',
    group: '非洲',
    constraints: [
      { field: '左舵/右舵', op: '==', value: '左舵' },
    ],
    requirements: '1. 车龄需低于15年\n2. 必须为左舵车辆\n3. 需提供适航证',
    status: 'ACTIVE',
  },
]

export default function ExportConfig() {
  return (
    <div className="space-y-6">
      {/* Page header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-gray-900">出口配置菜单</h2>
          <p className="text-sm text-gray-500 mt-1">管理前端展示的出口国家、要求及展示条件</p>
        </div>
        <button className="flex items-center gap-1.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold px-4 py-2 rounded-xl transition-colors self-start">
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
              {regions.map((region) => (
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
                  <td className="px-4 py-3 text-sm text-gray-600">{region.group}</td>
                  <td className="px-4 py-3 text-sm text-gray-800">
                    <div className="flex flex-wrap gap-x-3 gap-y-1">
                      {region.constraints.map((c, i) => (
                        <span key={i} className="inline-flex items-center gap-1 text-xs">
                          <span className="text-gray-500">{c.field}</span>
                          <span className="text-indigo-500 font-mono font-semibold">{c.op}</span>
                          <span className="text-gray-900 font-medium">{c.value}</span>
                        </span>
                      ))}
                    </div>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-600 max-w-[300px]">
                    <div className="whitespace-pre-line text-xs leading-relaxed">{region.requirements}</div>
                  </td>
                  <td className="px-4 py-3 text-sm">
                    <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-medium bg-emerald-50 text-emerald-600">
                      <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full" />
                      {region.status}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm">
                    <div className="flex items-center gap-1">
                      <button className="p-1.5 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-indigo-600 transition-colors" title="编辑">
                        <Edit2 className="w-3.5 h-3.5" />
                      </button>
                      <button className="p-1.5 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors" title="删除">
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
