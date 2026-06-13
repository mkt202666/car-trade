import request from './request'

// 对象风格 API（推荐）
export const tradeApi = {
  list: (params) => request.get('/orders', { params }),
  detail: (id) => request.get(`/orders/${id}`),
  logs: (id) => request.get(`/orders/${id}/logs`),
  confirm: (id) => request.put(`/orders/${id}/confirm`),
  forceCancel: (id, data) => request.put(`/orders/${id}/force-cancel`, data),
  resolveDispute: (id, data) => request.put(`/orders/${id}/resolve-dispute`, data),
  exportUrl: (params) => {
    const qs = new URLSearchParams(params || {}).toString()
    return `/api/v1/admin/orders/export${qs ? '?' + qs : ''}`
  },
}

// 兼容旧版独立函数导出
export function getOrderList(params) { return tradeApi.list(params) }
export function getOrderDetail(id) { return tradeApi.detail(id) }
export function getOrderLogs(id) { return tradeApi.logs(id) }
export function confirmOrder(id) { return tradeApi.confirm(id) }
export function forceCancelOrder(id, data) { return tradeApi.forceCancel(id, data) }
export function resolveDispute(id, data) { return tradeApi.resolveDispute(id, data) }
