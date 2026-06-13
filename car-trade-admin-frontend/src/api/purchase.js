import request from './request'

export const purchaseApi = {
  list: (params) => request.get('/purchase-requests', { params }),
  detail: (id) => request.get(`/purchase-requests/${id}`),
  close: (id, data) => request.put(`/purchase-requests/${id}/close`, data),
  match: (id) => request.post(`/purchase-requests/${id}/match`),
  matchedCars: (id, params) => request.get(`/purchase-requests/${id}/matched-cars`, { params }),
}
