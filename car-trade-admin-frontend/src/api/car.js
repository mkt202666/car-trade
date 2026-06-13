import request from './request';

export const carApi = {
  list: (params) => request.get('/cars', { params }),
  detail: (id) => request.get(`/cars/${id}`),
  updateStatus: (id, data) => request.put(`/cars/${id}/status`, data),
  violate: (id, data) => request.post(`/cars/${id}/violate`, data),
};

// 兼容旧版独立函数导出
export function getCarList(params) { return carApi.list(params); }
export function getCarDetail(id) { return carApi.detail(id); }
export function updateCarStatus(id, data) { return carApi.updateStatus(id, data); }
export function violateCar(id, data) { return carApi.violate(id, data); }
