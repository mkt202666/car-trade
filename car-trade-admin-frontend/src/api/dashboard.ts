/** Dashboard API */
import { get } from '../utils/request'
import type { ApiResponse } from './types'

export interface DashboardKPI {
  totalUsers: number
  userGrowth: number
  totalCars: number
  carGrowth: number
  totalOrders: number
  orderGrowth: number
  totalRevenue: number
  revenueGrowth: number
  pendingAudits: number
  pendingDisputes: number
  activeShops: number
  todayVisits: number
}

export interface DashboardTrend {
  dates: string[]
  users: number[]
  orders: number[]
  revenue: number[]
}

export interface DashboardWarning {
  id: string
  type: string
  title: string
  content: string
  level: 'info' | 'warning' | 'error'
  createdAt: string
}

export function getDashboardKPI() {
  return get<ApiResponse<DashboardKPI>>('/dashboard/kpi')
}

export function getDashboardTrend(days?: number) {
  return get<ApiResponse<DashboardTrend>>('/dashboard/trend', { days })
}

export function getDashboardWarnings() {
  return get<ApiResponse<DashboardWarning[]>>('/dashboard/warnings')
}
