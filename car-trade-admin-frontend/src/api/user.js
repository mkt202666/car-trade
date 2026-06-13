import request from './request'

export const userApi = {
  list: (params) => request.get('/users', { params }),
  detail: (id) => request.get(`/users/${id}`),
  updateStatus: (id, data) => request.put(`/users/${id}/status`, data),
  statistics: (id) => request.get(`/users/${id}/statistics`),
}
