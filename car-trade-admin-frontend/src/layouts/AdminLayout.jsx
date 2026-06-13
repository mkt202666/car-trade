import { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import {
  BarChart3,
  UsersRound,
  Store,
  ShieldCheck,
  Car,
  Search,
  ShoppingBag,
  Wallet,
  Download,
  BookOpen,
  FolderOpen,
  Shield,
  RotateCcw,
  AlertTriangle,
  CreditCard,
} from 'lucide-react'
import { useAuth } from '../store/AuthContext'

const navItems = [
  { path: '/dashboard', label: '分析仪表盘', icon: BarChart3 },
  { path: '/user-manage', label: '用户管理', icon: UsersRound },
  { path: '/shop-manage', label: '车行管理', icon: Store },
  { path: '/shop-audit', label: '车行注册审核', icon: ShieldCheck, badge: 3 },
  { path: '/car-manage', label: '5D 车源管理', icon: Car },
  { path: '/purchase-manage', label: '求购管理', icon: Search },
  { path: '/trade-manage', label: '交易管理', icon: ShoppingBag, badge: 1 },
  { path: '/dispute-manage', label: '争议处理', icon: AlertTriangle },
  { path: '/deposit-flow', label: '保证金现金流', icon: Wallet },
  { path: '/deposit-manage', label: '保证金管理', icon: CreditCard },
  { path: '/export-config', label: '出口配置', icon: Download },
  { path: '/car-library', label: '车型库', icon: BookOpen },
  { path: '/resource-manage', label: '资源管理', icon: FolderOpen },
]

export default function AdminLayout() {
  const navigate = useNavigate()
  const location = useLocation()
  const { adminUser } = useAuth()

  const handleRestorePreset = () => {
    window.location.reload()
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans text-gray-900 antialiased selection:bg-indigo-600 selection:text-white">
      {/* Header */}
      <header className="bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white h-16 border-b border-indigo-900/30 flex items-center justify-between px-6 shrink-0 shadow-md">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 bg-indigo-600 rounded-xl flex items-center justify-center font-black text-white text-lg tracking-wider border border-indigo-400/40 shadow-inner">
            5D
          </div>
          <div>
            <h1 className="text-base font-extrabold tracking-tight font-sans">5D Auto Operation Desk</h1>
            <p className="text-[10px] text-indigo-300 font-mono scale-95 origin-left font-semibold">车商互操作系统运作管理后台 v3.5</p>
          </div>
        </div>
        <div className="flex items-center gap-4">
          <div className="hidden md:flex items-center gap-1.5 bg-white/10 backdrop-blur-md border border-white/5 px-2.5 py-1 rounded-lg text-[10.5px] text-zinc-300 font-mono font-medium leading-none">
            <Shield className="w-3.5 h-3.5 text-indigo-300" />
            <span>运维登录: {adminUser?.email || 'yuan2026@5d.com'}</span>
          </div>
          <button
            onClick={handleRestorePreset}
            className="text-[10px] bg-amber-600 hover:bg-amber-700 text-white font-bold px-2.5 py-1 rounded-lg transition-colors cursor-pointer"
            title="点击恢复预置演示数据"
          >
            还原预设
          </button>
          <div className="flex items-center gap-2 border-l border-white/15 pl-4">
            <div className="w-8 h-8 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-950 font-black text-xs border border-indigo-400">
              AD
            </div>
            <div className="hidden lg:block text-left">
              <span className="block text-xs font-bold leading-tight">{adminUser?.realName || '管理员 A'}</span>
              <span className="block text-[9px] text-indigo-200/50 leading-tight">超级合规风控</span>
            </div>
          </div>
        </div>
      </header>

      {/* Main area: sidebar + content */}
      <div className="flex flex-1 overflow-hidden">
        {/* Sidebar */}
        <aside className="w-60 bg-white border-r border-slate-100 p-4 flex flex-col justify-between shrink-0 hidden md:flex">
          <div className="space-y-4">
            <span className="text-[10px] font-bold text-gray-400 tracking-wider block pl-2 font-mono">
              OPERATION MODULES
            </span>
            <nav className="space-y-1">
              {navItems.map((item) => {
                const isActive = location.pathname === item.path
                const Icon = item.icon
                return (
                  <button
                    key={item.path}
                    onClick={() => navigate(item.path)}
                    className={`w-full flex items-center justify-between p-2.5 rounded-xl text-xs font-semibold cursor-pointer transition-all duration-200 ${
                      isActive
                        ? 'bg-indigo-50 text-indigo-700 shadow-sm border-l-4 border-indigo-500'
                        : 'text-gray-500 hover:bg-gray-50/70 hover:text-gray-900'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <Icon className={`w-4 h-4 ${isActive ? 'text-indigo-600' : 'text-gray-400'}`} />
                      <span>{item.label}</span>
                    </div>
                    {item.badge && (
                      <span className="px-2 py-0.5 rounded-full text-[9px] font-mono font-bold bg-gray-100 text-gray-400">
                        {item.badge}
                      </span>
                    )}
                  </button>
                )
              })}
            </nav>
          </div>

          {/* Bottom section */}
          <div className="space-y-4">
            <div className="border-t border-slate-100 pt-4">
              <span className="text-[10px] font-bold text-gray-400 tracking-wider block pl-2 font-mono">
                5D 业务线管区规章
              </span>
              <p className="text-[10px] text-gray-400 leading-relaxed mt-2 pl-2">
                根据《反电信诈骗五要素》及《代拍法拍二手车监管守则》，本平台将所有核销让利与保证金变动实时日志归档案卷。
              </p>
            </div>
          </div>
        </aside>

        {/* Page content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
