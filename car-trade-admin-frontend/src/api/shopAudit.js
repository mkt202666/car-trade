import request from './request'

export const shopAuditApi = {
  list: (params) => request.get('/shop-reviews', { params }),
  approve: (id) => request.put(`/shop-reviews/${id}/approve`),
  reject: (id, data) => request.put(`/shop-reviews/${id}/reject`, data),
  batchApprove: (data) => request.put('/shop-reviews/batch-approve', data),
  pendingCount: () => request.get('/shop-reviews/pending-count'),
}
