import request from './request'

export const dashboardApi = {
  kpi: (params) => request.get('/dashboard/kpi', { params }),
  trend: (params) => request.get('/dashboard/trend', { params }),
  carDistribution: () => request.get('/dashboard/car-distribution'),
  couponStats: (params) => request.get('/dashboard/coupon-stats', { params }),
  approvalQueue: () => request.get('/dashboard/approval-queue'),
  warnings: (params) => request.get('/dashboard/warnings', { params }),
}
