import request from './request';

export function getBannerList(params) {
  return request.get('/resources/banners', { params });
}
export function createBanner(data) {
  return request.post('/resources/banners', data);
}
export function updateBanner(id, data) {
  return request.put(`/resources/banners/${id}`, data);
}
export function deleteBanner(id) {
  return request.delete(`/resources/banners/${id}`);
}
export function updateBannerStatus(id, data) {
  return request.put(`/resources/banners/${id}/status`, data);
}
export function sortBanners(data) {
  return request.put('/resources/banners/sort', data);
}
export function getConfig(key) {
  return request.get(`/resources/configs/${key}`);
}
export function updateConfig(key, data) {
  return request.put(`/resources/configs/${key}`, data);
}
