export interface NavItem {
  path: string
  label: string
  icon: string
  badge?: number
}

export const navItems: NavItem[] = [
  { path: '/dashboard', label: '分析仪表盘', icon: 'chart' },
  { path: '/users', label: '用户管理', icon: 'users' },
  { path: '/dealers', label: '车行管理', icon: 'store' },
  { path: '/dealer-audit', label: '车行注册审核', icon: 'audit', badge: 3 },
  { path: '/vehicles', label: '5D 车源管理', icon: 'car' },
  { path: '/purchase', label: '求购管理', icon: 'purchase' },
  { path: '/transactions', label: '交易管理', icon: 'transaction', badge: 1 },
  { path: '/deposit', label: '保证金现金流', icon: 'deposit' },
  { path: '/export-config', label: '出口配置', icon: 'export' },
  { path: '/models', label: '车型库', icon: 'model' },
  { path: '/resources', label: '资源管理', icon: 'resource' },
  { path: '/disputes', label: '纠纷管理', icon: 'dispute' },
  { path: '/notifications', label: '通知管理', icon: 'notification' },
]
