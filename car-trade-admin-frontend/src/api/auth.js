import request from './request'

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  me: () => request.get('/auth/me'),
  refresh: (refreshToken) => request.post('/auth/refresh', { refreshToken }),
  password: (data) => request.put('/auth/password', data),
}
