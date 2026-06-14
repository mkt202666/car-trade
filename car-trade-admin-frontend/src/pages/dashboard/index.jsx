import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  DollarSign,
  Car,
  ShieldAlert,
  UserCheck,
  ArrowUpRight,
  Activity,
  Clock,
  AlertTriangle,
  ChevronRight,
} from 'lucide-react'
import { LineChart, Line, Area, XAxis, YAxis, CartesianGrid, ResponsiveContainer, Tooltip as RechartsTooltip, PieChart as RePieChart, Pie, Cell, Legend } from 'recharts'
import { dashboardApi } from '../../api/dashboard'
import { SkeletonCard, SkeletonChart, SkeletonTable } from '../../components/Skeleton'
import { formatMoney, formatDate } from '../../utils/format'

// 饼图颜色
const PIE_COLORS = ['#4f46e5', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6', '#06b6d4']

// 默认 KPI 降级数据
const DEFAULT_KPI = {
  gmv: 92850000,
  gmvTrend: '+18.2%',
  carCount: 3,
  carNew: 1,
  deposit: 700000,
  depositActive: 4,
  pendingReview: 3,
  pendingProcessed: 2,
}

// 默认车源分布降级数据
const DEFAULT_DISTRIBUTION = [
  { name: '司法拍卖/竞标', percent: 17 },
  { name: '车商寄售', percent: 33 },
  { name: '旧车置换', percent: 17 },
  { name: '个人卖家', percent: 33 },
]

// 默认优惠券降级数据
const DEFAULT_COUPONS = [
  { code: 'DEALER666', name: '车商端首充补贴券', used: 48, active: true },
  { code: 'AUTO80', name: '物流长途费8折通用券', used: 112, active: true },
  { code: 'STARFREE', name: '5D星级拍卖免除服务费券', used: 15, active: true },
  { code: 'EXPIRED5D', name: '过时备用券5D', used: 220, active: false },
]

// 默认审批队列降级数据
const DEFAULT_APPROVALS = [
  { name: '南京腾达二手名车汇', desc: '新车行创建申请', date: '2026/6/4' },
  { name: '成都捷诚二手车交易网', desc: '区域代理商合伙人', date: '2026/6/3' },
  { name: '杭州星驰高端车行', desc: '自营直销商户', date: '2026/5/28' },
]

export default function Dashboard() {
  const navigate = useNavigate()
  // KPI
  const [kpi, setKpi] = useState(null)
  const [kpiLoading, setKpiLoading] = useState(true)
  // Trend
  const [trend, setTrend] = useState(null)
  const [trendLoading, setTrendLoading] = useState(true)
  // Car distribution
  const [distribution, setDistribution] = useState(null)
  const [distLoading, setDistLoading] = useState(true)
  // Coupons
  const [coupons, setCoupons] = useState(null)
  const [couponLoading, setCouponLoading] = useState(true)
  // Approvals
  const [approvals, setApprovals] = useState(null)
  const [approvalLoading, setApprovalLoading] = useState(true)
  // Warnings
  const [warnings, setWarnings] = useState(null)
  const [warningLoading, setWarningLoading] = useState(true)

  // 获取 KPI
  useEffect(() => {
    setKpiLoading(true)
    dashboardApi.kpi()
      .then(res => {
        // 后端返回完整 KPI，映射为前端需要的字段
        const data = res || {}
        setKpi({
          gmv: data.gmv ?? data.tradeAmount ?? 0,
          gmvTrend: data.gmvTrend ?? '+0%',
          carCount: data.carCount ?? 0,
          carNew: data.todayNewCars ?? data.carNew ?? 0,
          deposit: data.deposit ?? 0,
          depositActive: data.depositActive ?? 0,
          pendingReview: data.pendingReviewCount ?? data.pendingReview ?? 0,
          pendingProcessed: data.pendingProcessed ?? 0,
        })
      })
      .catch(() => setKpi(null))
      .finally(() => setKpiLoading(false))
  }, [])

  // 获取趋势
  useEffect(() => {
    setTrendLoading(true)
    dashboardApi.trend({ period: 'MONTH' })
      .then(res => setTrend(res.data || res))
      .catch(() => setTrend(null))
      .finally(() => setTrendLoading(false))
  }, [])

  // 获取车源分布
  useEffect(() => {
    setDistLoading(true)
    dashboardApi.carDistribution()
      .then(res => setDistribution(res.data || res))
      .catch(() => setDistribution(null))
      .finally(() => setDistLoading(false))
  }, [])

  // 获取优惠券统计
  useEffect(() => {
    setCouponLoading(true)
    dashboardApi.couponStats()
      .then(res => {
        // 后端返回聚合数据 {totalCount, usedCount, remainCount, usageRate}
        const data = res || {}
        if (data.totalCount != null) {
          setCoupons([
            { code: '-', name: '优惠券总量', used: data.totalCount, active: true },
            { code: '-', name: '已核销数量', used: data.usedCount ?? 0, active: true },
            { code: '-', name: '剩余可用', used: data.remainCount ?? 0, active: true },
            { code: '-', name: '使用率', used: data.usageRate != null ? data.usageRate + '%' : '0%', active: true, isPercent: true },
          ])
        } else {
          setCoupons(res)
        }
      })
      .catch(() => setCoupons(null))
      .finally(() => setCouponLoading(false))
  }, [])

  // 获取审批队列
  useEffect(() => {
    setApprovalLoading(true)
    dashboardApi.approvalQueue()
      .then(res => {
        // 后端返回 [{type, id, title, createdAt}]
        const data = Array.isArray(res) ? res : []
        setApprovals(data.map(a => ({
          name: a.title || '-',
          desc: a.type || '',
          date: a.createdAt ? new Date(a.createdAt).toLocaleDateString('zh-CN') : '-',
        })))
      })
      .catch(() => setApprovals(null))
      .finally(() => setApprovalLoading(false))
  }, [])

  // 获取预警
  useEffect(() => {
    setWarningLoading(true)
    dashboardApi.warnings()
      .then(res => {
        // 后端返回 [{id, type, level, message, createdAt}] 数组
        const data = Array.isArray(res) ? res : []
        setWarnings({
          list: data,
          message: data.length > 0
            ? `平台当前共有 ${data.length} 条预警信息。${data.map(w => w.message).join('；')}`
            : '平台当前无异常预警，资金链路总体健康。',
        })
      })
      .catch(() => setWarnings(null))
      .finally(() => setWarningLoading(false))
  }, [])

  // 生成趋势 SVG 路径
  // 生成折线图(使用Recharts)
  const renderTrendChart = () => {
    const data = trend || []
    if (!Array.isArray(data) || data.length === 0) return null

    // 转换数据格式为Recharts所需
    const chartData = data.map(d => ({
      date: d.date || d.label || '',
      amount: d.tradeAmount ?? d.amount ?? 0,
    }))

    return (
      <ResponsiveContainer width="100%" height={240}>
        <LineChart data={chartData} margin={{ top: 20, right: 20, left: 0, bottom: 0 }}>
          <defs>
            <linearGradient id="colorAmount" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#4f46e5" stopOpacity={0.3}/>
              <stop offset="95%" stopColor="#4f46e5" stopOpacity={0}/>
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
          <XAxis 
            dataKey="date" 
            tick={{ fontSize: 11, fill: '#94a3b8' }}
            axisLine={false}
            tickLine={false}
          />
          <YAxis 
            tickFormatter={(value) => formatMoney(value).replace('¥', '¥').slice(0, 10)}
            tick={{ fontSize: 11, fill: '#94a3b8' }}
            axisLine={false}
            tickLine={false}
            width={60}
          />
          <RechartsTooltip 
            formatter={(value) => [formatMoney(value), '交易额']}
            contentStyle={{ backgroundColor: '#fff', border: '1px solid #e2e8f0', borderRadius: '8px' }}
          />
          <Area 
            type="monotone" 
            dataKey="amount" 
            stroke="#4f46e5" 
            strokeWidth={2}
            fillOpacity={1} 
            fill="url(#colorAmount)" 
          />
          <Line 
            type="monotone" 
            dataKey="amount" 
            stroke="#4f46e5" 
            strokeWidth={3}
            dot={{ r: 4, fill: '#4f46e5', stroke: '#fff', strokeWidth: 2 }}
            activeDot={{ r: 6, fill: '#4f46e5', stroke: '#fff', strokeWidth: 2 }}
          />
        </LineChart>
      </ResponsiveContainer>
    )
  }

  // 生成饼图
  const renderPieChart = () => {
    const rawData = distribution
    // 后端返回 [{channel, count, percentage}]，映射为前端需要的 [{name, percent, count}]
    const data = Array.isArray(rawData) ? rawData.map(d => ({
      name: d.channel || d.name || '',
      percent: d.percentage != null ? Number(d.percentage) : (d.percent || 0),
      count: d.count || 0,
    })) : DEFAULT_DISTRIBUTION
    const items = data.length > 0 ? data : DEFAULT_DISTRIBUTION
    const total = items.reduce((s, d) => s + (d.percent || 0), 0) || 1

    let offset = 0
    const slices = items.map((d, i) => {
      const pct = (d.percent || 0) / total * 100
      const dash = `${pct.toFixed(1)} ${(100 - pct).toFixed(1)}`
      const off = (-offset).toFixed(1)
      offset += pct
      return { ...d, dash, off, color: PIE_COLORS[i % PIE_COLORS.length] }
    })

    const totalCount = items.reduce((s, d) => s + (d.count || 0), 0)

    return (
      <div className="flex flex-col items-center py-4">
        <div className="relative w-36 h-36">
          <svg className="w-full h-full transform -rotate-90" viewBox="0 0 36 36">
            <circle cx="18" cy="18" r="15.915" fill="none" stroke="#f1f5f9" strokeWidth="3" />
            {slices.map((s, i) => (
              <circle key={i} cx="18" cy="18" r="15.915" fill="none" stroke={s.color} strokeWidth="3" strokeDasharray={s.dash} strokeDashoffset={s.off} />
            ))}
          </svg>
          <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
            <span className="text-xs text-gray-400">货源总计</span>
            <span className="text-lg font-bold text-gray-800 font-mono">{totalCount || items.length * 6} 台</span>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-3 text-xs mt-4 w-full">
          {items.map((d, i) => (
            <div key={i} className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: PIE_COLORS[i % PIE_COLORS.length] }} />
              <div className="flex flex-col">
                <span className="text-gray-400">{d.name || d.channel || `来源${i + 1}`}</span>
                <span className="font-semibold text-gray-800 font-mono">{(d.percent || 0)}%</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    )
  }

  const displayKpi = kpi || DEFAULT_KPI
  const displayCoupons = coupons || DEFAULT_COUPONS
  const displayApprovals = approvals || DEFAULT_APPROVALS
  const displayWarnings = warnings || { overdueCount: 0, message: '平台当前共有 0 笔处于超期未流转的锁定保证金。资金链路总体健康。' }

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 bg-white/40 backdrop-blur-md p-4 rounded-2xl border border-gray-100">
        <div>
          <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">5D Auto 数据全景透视</h2>
          <p className="text-[12px] text-gray-500">业务指标实时对齐与合规操作舱</p>
        </div>
        <div className="flex items-center gap-2 bg-gray-50 px-3 py-1.5 rounded-lg border border-gray-100 self-start sm:self-auto text-xs text-gray-500 font-mono">
          <Clock className="w-3.5 h-3.5 text-gray-400" />
          <span>最新同步: {formatDate(new Date())}</span>
        </div>
      </div>

      {/* KPI Cards */}
      {kpiLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {[1, 2, 3, 4].map(i => <SkeletonCard key={i} />)}
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* GMV */}
          <div className="bg-gradient-to-br from-indigo-900 via-indigo-950 to-slate-900 text-white p-5 rounded-2xl shadow-sm relative overflow-hidden group">
            <div className="absolute right-[-20%] bottom-[-20%] w-36 h-36 bg-indigo-500/10 rounded-full blur-2xl group-hover:bg-indigo-500/20 transition-all duration-500" />
            <div className="flex justify-between items-start">
              <span className="text-xs text-indigo-200/80 font-medium tracking-wider">实时履约总 GMV (CNY)</span>
              <div className="p-2 bg-white/10 backdrop-blur-md rounded-xl text-indigo-300">
                <DollarSign className="w-5 h-5" />
              </div>
            </div>
            <div className="mt-4">
              <h3 className="text-3xl font-bold font-mono tracking-tight">{formatMoney(displayKpi.gmv)}</h3>
              <div className="flex items-center gap-1.5 mt-2 text-xs text-emerald-400">
                <ArrowUpRight className="w-3.5 h-3.5" />
                <span>较上周复合提升 {displayKpi.gmvTrend || '0%'}</span>
              </div>
            </div>
          </div>
          {/* Car count */}
          <div className="bg-white p-5 rounded-2xl border border-gray-100 relative overflow-hidden group">
            <div className="flex justify-between items-start">
              <span className="text-xs text-gray-400 font-medium tracking-wider">在售/上架车源总数</span>
              <div className="p-2 bg-indigo-50 rounded-xl text-indigo-600">
                <Car className="w-5 h-5" />
              </div>
            </div>
            <div className="mt-4">
              <h3 className="text-3xl font-bold font-mono text-gray-900 tracking-tight">{displayKpi.carCount ?? '-'} 台</h3>
              <div className="flex items-center gap-1.5 mt-2 text-xs text-gray-500">
                <span className="font-medium text-emerald-600 font-mono">+{displayKpi.carNew ?? 0}</span>
                <span>本周新增提报</span>
              </div>
            </div>
          </div>
          {/* Deposit */}
          <div className="bg-white p-5 rounded-2xl border border-gray-100 relative overflow-hidden group">
            <div className="flex justify-between items-start">
              <span className="text-xs text-gray-400 font-medium tracking-wider">冻结中投标保证金余额</span>
              <div className="p-2 bg-amber-50 rounded-xl text-amber-600">
                <ShieldAlert className="w-5 h-5" />
              </div>
            </div>
            <div className="mt-4">
              <h3 className="text-3xl font-bold font-mono text-gray-900 tracking-tight">{formatMoney(displayKpi.deposit)}</h3>
              <div className="flex items-center gap-1.5 mt-2 text-xs text-amber-600">
                <Activity className="w-3.5 h-3.5 animate-pulse" />
                <span>当前 {displayKpi.depositActive ?? 0} 个竞拍协议活跃担保</span>
              </div>
            </div>
          </div>
          {/* Pending */}
          <div className="bg-white p-5 rounded-2xl border border-gray-100 relative overflow-hidden group">
            <div className="flex justify-between items-start">
              <span className="text-xs text-gray-400 font-medium tracking-wider">待审车商/合伙人资质</span>
              <div className="p-2 bg-rose-50 rounded-xl text-rose-600">
                <UserCheck className="w-5 h-5" />
              </div>
            </div>
            <div className="mt-4">
              <h3 className="text-3xl font-bold font-mono text-gray-900 tracking-tight">{displayKpi.pendingReview ?? 0} 件</h3>
              <div className="flex items-center gap-1.5 mt-2 text-xs text-rose-600 font-medium">
                <span>{displayKpi.pendingProcessed ?? 0} 个三要素AI校正完毕</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Line chart */}
        {trendLoading ? (
          <div className="lg:col-span-2"><SkeletonChart /></div>
        ) : (
          <div className="bg-white p-5 rounded-2xl border border-gray-100 lg:col-span-2 shadow-sm">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h3 className="text-base font-bold text-gray-900">平台每日交易及成交趋势 (CNY)</h3>
                <p className="text-xs text-gray-400">每日实际锁定定金及最终合同签发金额</p>
              </div>
              <div className="flex items-center gap-3 text-xs">
                <span className="flex items-center gap-1.5 text-gray-500 font-medium font-mono">
                  <span className="w-2.5 h-2.5 bg-indigo-600 rounded-full" />
                  交易金额 (CNY)
                </span>
              </div>
            </div>
            <div className="relative h-64 w-full">
              {renderTrendChart() || (
                <div className="flex items-center justify-center h-full text-gray-400 text-sm">暂无趋势数据</div>
              )}
            </div>
          </div>
        )}

        {/* Pie chart */}
        {distLoading ? (
          <SkeletonChart />
        ) : (
          <div className="bg-white p-5 rounded-2xl border border-gray-100 flex flex-col justify-between shadow-sm">
            <div>
              <h3 className="text-base font-bold text-gray-900">上架车源获取渠道构成</h3>
              <p className="text-xs text-gray-400">多维度货源占比与平台转化效能分析</p>
            </div>
            {renderPieChart()}
          </div>
        )}
      </div>

      {/* Bottom row */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Coupons */}
        {couponLoading ? (
          <SkeletonTable rows={4} cols={2} />
        ) : (
          <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex flex-col justify-between">
            <div>
              <h3 className="text-sm font-bold text-gray-900 mb-1">优惠券效能与发放统计</h3>
              <p className="text-xs text-gray-400 mb-4">平台补贴池及车商消耗核销概览</p>
            </div>
            <div className="space-y-3">
              {displayCoupons.map((c, idx) => (
                <div key={idx} className="flex justify-between items-center bg-gray-50/50 p-2.5 rounded-xl border border-gray-100 text-xs">
                  <div>
                    <span className="font-bold text-gray-800 block">{c.name}</span>
                  </div>
                  <div className="text-right">
                    <span className="font-bold text-indigo-600 block font-mono">
                      {c.isPercent ? c.used : `${c.used ?? 0} 张`}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Approvals */}
        {approvalLoading ? (
          <SkeletonTable rows={3} cols={2} />
        ) : (
          <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm flex flex-col justify-between">
            <div>
              <h3 className="text-sm font-bold text-gray-900 mb-1">资质注册处理队列 (最新)</h3>
              <p className="text-xs text-gray-400 mb-4">车商与代理合伙人准入审批流</p>
            </div>
            <div className="space-y-3">
              {displayApprovals.map((a, idx) => (
                <div key={idx} className="flex justify-between items-center text-xs">
                  <div className="flex items-center gap-2">
                    <span className="w-2 h-2 rounded-full bg-yellow-500 animate-pulse" />
                    <div>
                      <span className="font-semibold text-gray-800 block">{a.name}</span>
                      <span className="text-gray-400 text-[10px]">{a.desc} | {a.date}</span>
                    </div>
                  </div>
                  <button onClick={() => navigate('/shop-audit')} className="text-indigo-500 hover:text-indigo-600 font-medium text-[11px]">前往处理</button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Warnings */}
        {warningLoading ? (
          <SkeletonCard />
        ) : (
          <div className="bg-white p-5 rounded-2xl border border-rose-100/50 shadow-sm flex flex-col justify-between bg-gradient-to-tr from-rose-50/10 to-transparent">
            <div>
              <div className="flex items-center gap-2 text-rose-600 mb-1">
                <AlertTriangle className="w-4 h-4" />
                <h3 className="text-sm font-bold text-gray-900">异常交易与资金预警系统</h3>
              </div>
              <p className="text-xs text-gray-400 mb-4">自动拦截高风险、超时未成交订单定金</p>
            </div>
            <div className="bg-rose-50/50 p-3 rounded-xl border border-rose-100 text-xs flex flex-col gap-2">
              <div className="text-gray-700">
                {displayWarnings.message}
              </div>
              <div className="text-[11px] text-gray-400">
                当任何保证金锁定超过45个法定期限，系统将自动发起人工接听核实流转。
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
