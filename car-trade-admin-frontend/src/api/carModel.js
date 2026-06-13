import request from './request';

export function getBrands(params) {
  return request.get('/car-library/brands', { params });
}
export function createBrand(data) {
  return request.post('/car-library/brands', data);
}
export function updateBrand(id, data) {
  return request.put(`/car-library/brands/${id}`, data);
}
export function deleteBrand(id) {
  return request.delete(`/car-library/brands/${id}`);
}
export function getSeries(params) {
  return request.get('/car-library/series', { params });
}
export function createSeries(data) {
  return request.post('/car-library/series', data);
}
export function updateSeries(id, data) {
  return request.put(`/car-library/series/${id}`, data);
}
export function deleteSeries(id) {
  return request.delete(`/car-library/series/${id}`);
}
export function getModels(params) {
  return request.get('/car-library/models', { params });
}
export function createModel(data) {
  return request.post('/car-library/models', data);
}
export function updateModel(id, data) {
  return request.put(`/car-library/models/${id}`, data);
}
export function deleteModel(id) {
  return request.delete(`/car-library/models/${id}`);
}
export function importModels(data) {
  return request.post('/car-library/models/import', data);
}
