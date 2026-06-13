import request from './request'

export const configApi = {
  get: (key) => request.get(`/resources/configs/${key}`),
  update: (key, data) => request.put(`/resources/configs/${key}`, data),
}
