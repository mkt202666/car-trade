import { useState } from 'react'
import { Wallet, Search, ChevronDown, PlusCircle, DollarSign, Users, TrendingUp } from 'lucide-react'

const flowTypeOptions = [
  '所有的往来流水类型',
  '充值保证金 (Pay)',
  '冻结履约保证金',
  '完成交易释放 (Release)',
  '退还回原件提现',
]

const transactions = [
  {
    id: 'TX-5001',
    time: '2026/6/1 16:30:00',
    customer: '张建国',
    customerId: 'USR-3001',
    type: '保证金网银充值',
    amount: '+￥10,000',
    balance: '￥50,000',
    note: '支付宝线上扫画充值保证金',
  },
  {
    id: 'TX-5002',
    time: '2026/6/1 18:00:00',
    customer: '张建国',
    customerId: 'USR-3001',
    type: '应标暂扣冻结',
    amount: '-￥10,000',
    balance: '￥40,000',
    note: '用于锁定订单 ORD-2026053001 担保定金',
  },
  {
    id: 'TX-5003',
    time: '2026/5/31 00:45:00',
    customer: '李思平',
    customerId: 'USR-3002',
    type: '释放退还原账',
    amount: '+￥20,000',
    balance: '￥20,000',
    note: '订单成交交付完成，系统释放其全额挂单担保金',
  },
  {
    id: 'TX-5004',
    time: '2026/6/3 17:12:00',
    customer: '王凯文',
    customerId: 'USR-3003',
    type: '应标暂扣冻结',
    amount: '-￥5,000',
    balance: '￥30,000',
    note: '履约竞价暂扣锁定',
  },
]

export default function DepositFlow() {
  const [flowType, setFlowType] = useState(flowTypeOptions[0])
  const [flowTypeOpen, setFlowTypeOpen] = useState(false)
  const [searchText, setSearchText] = useState('')

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h2 className="text-xl font-bold text-gray-900">保证金现金流</h2>
        <p className="text-sm text-gray-500 mt-1">全景核验及调整关联主体在信贷和保证金科目的资金变动与流转账目明细。</p>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-400 font-medium">当前冻结托管定金总和 (CNY)</span>
            <Wallet className="w-4 h-4 text-indigo-500" />
          </div>
          <div className="mt-2 flex items-baseline gap-1">
            <span className="text-lg text-gray-500">￥</span>
            <span className="text-2xl font-bold font-mono text-gray-900">5,000</span>
          </div>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-400 font-medium">保证金流水累计交易规模</span>
            <DollarSign className="w-4 h-4 text-emerald-500" />
          </div>
          <div className="mt-2 flex items-baseline gap-1">
            <span className="text-lg text-gray-500">￥</span>
            <span className="text-2xl font-bold font-mono text-gray-900">45,000</span>
          </div>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-400 font-medium">入账商户往来核算账户数</span>
            <Users className="w-4 h-4 text-amber-500" />
          </div>
          <div className="mt-2 flex items-baseline gap-1">
            <span className="text-2xl font-bold font-mono text-gray-900">12</span>
            <span className="text-sm text-gray-400 ml-1">个主体</span>
          </div>
        </div>
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
              {transactions.map((tx) => (
                <tr key={tx.id} className="hover:bg-gray-50/50 transition-colors">
                  <td className="px-4 py-3 text-sm text-gray-800">
                    <div className="font-mono text-xs font-semibold text-gray-900">{tx.id}</div>
                    <div className="text-[11px] text-gray-400 mt-0.5">{tx.time}</div>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-800">
                    <div className="font-medium text-gray-900">{tx.customer}</div>
                    <div className="text-[11px] text-gray-400 mt-0.5">{tx.customerId}</div>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-800">{tx.type}</td>
                  <td className="px-4 py-3 text-sm">
                    <span className={tx.amount.startsWith('+') ? 'text-emerald-600 font-semibold font-mono' : 'text-red-500 font-semibold font-mono'}>
                      {tx.amount}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-800 font-mono">{tx.balance}</td>
                  <td className="px-4 py-3 text-sm text-gray-600 max-w-[240px]">{tx.note}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
