import request from './request'

export const shopApi = {
  list: (params) => request.get('/shops', { params }),
  detail: (id) => request.get(`/shops/${id}`),
  updateStatus: (id, data) => request.put(`/shops/${id}/status`, data),
}
