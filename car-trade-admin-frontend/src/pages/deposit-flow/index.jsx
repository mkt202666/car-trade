import { useState, useEffect } from 'react'
import { Wallet, Search, ChevronDown, PlusCircle, DollarSign, Users, TrendingUp } from 'lucide-react'
import { getDepositRecords, getDepositSummary } from '../../api/deposit'
import { SkeletonCard } from '../../components/Skeleton'

const flowTypeOptions = [
  '所有的往来流水类型',
  '充值保证金 (Pay)',
  '冻结履约保证金',
  '完成交易释放 (Release)',
  '退还回原件提现',
]

export default function DepositFlow() {
  const [flowType, setFlowType] = useState(flowTypeOptions[0])
  const [flowTypeOpen, setFlowTypeOpen] = useState(false)
  const [searchText, setSearchText] = useState('')
  
  // 数据状态
  const [transactions, setTransactions] = useState([])
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(true)
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const pageSize = 10

  // 加载流水记录
  useEffect(() => {
    loadRecords()
  }, [page, flowType, searchText])

  // 加载汇总数据
  useEffect(() => {
    loadSummary()
  }, [])

  const loadRecords = async () => {
    setLoading(true)
    try {
      const params = {
        page,
        size: pageSize,
      }
      
      // 添加筛选条件
      if (flowType !== flowTypeOptions[0]) {
        params.type = flowType
      }
      if (searchText) {
        params.keyword = searchText
      }
      
      const response = await getDepositRecords(params)
      if (response.data && response.code === 200) {
        setTransactions(response.data.list || [])
        setTotal(response.data.total || 0)
      }
    } catch (error) {
      console.error('加载保证金流水失败:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadSummary = async () => {
    try {
      const response = await getDepositSummary()
      if (response.data && response.code === 200) {
        setSummary(response.data)
      }
    } catch (error) {
      console.error('加载汇总数据失败:', error)
    }
  }

  // 格式化金额
  const formatMoney = (amount) => {
    if (amount == null) return '￥0'
    return `￥${Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
  }

  // 格式化时间
  const formatTime = (timeStr) => {
    if (!timeStr) return '-'
    return new Date(timeStr).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    }).replace(/\//g, '/')
  }

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h2 className="text-xl font-bold text-gray-900">保证金现金流</h2>
        <p className="text-sm text-gray-500 mt-1">全景核验及调整关联主体在信贷和保证金科目的资金变动与流转账目明细。</p>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {loading ? (
          <>
            <SkeletonCard />
            <SkeletonCard />
            <SkeletonCard />
          </>
        ) : (
          <>
            <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-xs text-gray-400 font-medium">当前所托管托管定金总和 (CNY)</span>
                <Wallet className="w-4 h-4 text-indigo-500" />
              </div>
              <div className="mt-2 flex items-baseline gap-1">
                <span className="text-lg text-gray-500">￥</span>
                <span className="text-2xl font-bold font-mono text-gray-900">{summary?.totalBalance || 0}</span>
              </div>
            </div>
            <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-xs text-gray-400 font-medium">保证金流水累计交易规模</span>
                <DollarSign className="w-4 h-4 text-emerald-500" />
              </div>
              <div className="mt-2 flex items-baseline gap-1">
                <span className="text-lg text-gray-500">￥</span>
                <span className="text-2xl font-bold font-mono text-gray-900">{summary?.totalTransactionVolume || 0}</span>
              </div>
            </div>
            <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-xs text-gray-400 font-medium">入账商户往来核算账户数</span>
                <Users className="w-4 h-4 text-amber-500" />
              </div>
              <div className="mt-2 flex items-baseline gap-1">
                <span className="text-2xl font-bold font-mono text-gray-900">{summary?.accountCount || 0}</span>
                <span className="text-sm text-gray-400 ml-1">个主体</span>
              </div>
            </div>
          </>
        )}
      </div>

      {/* Filters & action */}
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs flex-1 max-w-md">
          <Search className="w-3.5 h-3.5 text-gray-400" />
          <input
            type="text"
            placeholder="搜索流水ID、客户姓名、或客户ID"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="bg-transparent outline-none text-gray-700 placeholder:text-gray-400 w-full"
          />
        </div>
        <div className="relative">
          <button
            onClick={() => setFlowTypeOpen(!flowTypeOpen)}
            className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors"
          >
            {flowType}
            <ChevronDown className="w-3.5 h-3.5 text-gray-400" />
          </button>
          {flowTypeOpen && (
            <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-10 py-1 min-w-[220px]">
              {flowTypeOptions.map((opt) => (
                <button
                  key={opt}
                  onClick={() => { setFlowType(opt); setFlowTypeOpen(false) }}
                  className={`block w-full text-left px-3 py-2 text-xs hover:bg-gray-50 transition-colors ${opt === flowType ? 'text-indigo-600 font-medium' : 'text-gray-600'}`}
                >
                  {opt}
                </button>
              ))}
            </div>
          )}
        </div>
        <button className="flex items-center gap-1.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold px-4 py-2 rounded-xl transition-colors ml-auto">
          <PlusCircle className="w-3.5 h-3.5" />
          人工记账/划扣保证金
        </button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-gray-50">
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">交易序列号/时间</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">往来账户客户</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">交易科目类型</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">交易金额</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">余额变动情况</th>
                <th className="px-4 py-3 text-left text-xs text-gray-500 font-medium uppercase tracking-wider">往来摘要备注</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {loading ? (
                <tr>
                  <td colSpan="6" className="px-4 py-8 text-center text-gray-400">
                    <SkeletonCard />
                  </td>
                </tr>
              ) : transactions.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-4 py-8 text-center text-gray-400">
                    暂无保证金流水记录
                  </td>
                </tr>
              ) : (
                transactions.map((tx) => (
                  <tr key={tx.id} className="hover:bg-gray-50/50 transition-colors">
                    <td className="px-4 py-3 text-sm text-gray-800">
                      <div className="font-mono text-xs font-semibold text-gray-900">{tx.id}</div>
                      <div className="text-[11px] text-gray-400 mt-0.5">{formatTime(tx.createdAt)}</div>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-800">
                      <div className="font-medium text-gray-900">{tx.customerName}</div>
                      <div className="text-[11px] text-gray-400 mt-0.5">{tx.customerId}</div>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-800">{tx.type}</td>
                    <td className="px-4 py-3 text-sm">
                      <span className={tx.amount >= 0 ? 'text-emerald-600 font-semibold font-mono' : 'text-red-500 font-semibold font-mono'}>
                        {formatMoney(tx.amount)}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-800 font-mono">{formatMoney(tx.balanceAfter)}</td>
                    <td className="px-4 py-3 text-sm text-gray-600 max-w-[240px]">{tx.remark || '-'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        
        {/* Pagination */}
        {!loading && total > pageSize && (
          <div className="px-4 py-3 border-t border-gray-100 flex items-center justify-between">
            <div className="text-xs text-gray-500">
              共 {total} 条记录，第 {page} / {Math.ceil(total / pageSize)} 页
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => setPage(p => Math.max(1, p - 1))}
                disabled={page === 1}
                className="px-3 py-1 text-xs border border-gray-200 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                上一页
              </button>
              <button
                onClick={() => setPage(p => p + 1)}
                disabled={page * pageSize >= total}
                className="px-3 py-1 text-xs border border-gray-200 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                下一页
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
