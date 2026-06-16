/** Dashboard API */
import { get } from '../utils/request'
import type { ApiResponse } from './types'

/** 仪表盘 KPI 卡片数据，对应后端 DashboardKpiVO */
export interface DashboardKPI {
  userCount: number
  shopCount: number
  carCount: number
  orderCount: number
  tradeAmount: number
  pendingReviewCount: number
  pendingDisputeCount: number
  todayNewUsers: number
  todayNewCars: number
  todayOrders: number
  gmv: number
  gmvTrend: string
  deposit: number
  depositActive: number
  pendingProcessed: number
}

/** 趋势数据单条记录，对应后端 DashboardTrendVO */
export interface DashboardTrend {
  date: string
  orderCount: number
  tradeAmount: number
  newUsers: number
}

/** 预警信息，对应后端 DashboardWarningVO */
export interface DashboardWarning {
  id: unknown
  type: string
  level: string
  message: string
  createdAt: string
}

/** 渠道分布数据，对应后端 DashboardCarDistVO */
export interface DashboardCarDist {
  channel: string
  count: number
  percentage: number
}

/** 优惠券统计，对应后端 DashboardCouponVO */
export interface DashboardCouponStats {
  totalCount: number
  usedCount: number
  remainCount: number
  usageRate: number
}

/** 审批队列项，对应后端 DashboardApprovalVO */
export interface DashboardApproval {
  type: string
  id: unknown
  title: string
  createdAt: string
}

export function getDashboardKPI() {
  return get<ApiResponse<DashboardKPI>>('/dashboard/kpi')
}

export function getDashboardTrend(period: string = 'MONTH') {
  return get<ApiResponse<DashboardTrend[]>>('/dashboard/trend', { period })
}

export function getDashboardWarnings() {
  return get<ApiResponse<DashboardWarning[]>>('/dashboard/warnings')
}

export function getDashboardCarDistribution() {
  return get<ApiResponse<DashboardCarDist[]>>('/dashboard/car-distribution')
}

export function getDashboardCouponStats() {
  return get<ApiResponse<DashboardCouponStats>>('/dashboard/coupon-stats')
}

export function getDashboardApprovalQueue() {
  return get<ApiResponse<DashboardApproval[]>>('/dashboard/approval-queue')
}
