import request from './request'

export const bannerApi = {
  list: (params) => request.get('/resources/banners', { params }),
  create: (data) => request.post('/resources/banners', data),
  update: (id, data) => request.put(`/resources/banners/${id}`, data),
  delete: (id) => request.delete(`/resources/banners/${id}`),
  updateStatus: (id, data) => request.put(`/resources/banners/${id}/status`, data),
  sort: (ids) => request.put('/resources/banners/sort', { ids }),
}
