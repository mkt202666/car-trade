import request from './request'

export const exportConfigApi = {
  templates: () => request.get('/export/templates'),
  create: (data) => request.post('/export/templates', data),
  update: (id, data) => request.put(`/export/templates/${id}`, data),
  delete: (id) => request.delete(`/export/templates/${id}`),
  execute: (id, data) => request.post(`/export/templates/${id}/execute`, data),
}
