export const login = (data) => uni.$u.http.post('/users/login', data)
export const register = (data) => uni.$u.http.post('/users/register', data)
export const getUserInfo = () => uni.$u.http.get('/users/me')
export const updateUser = (data) => uni.$u.http.put('/users/me', data)
export const getUserStats = () => uni.$u.http.get('/users/me/stats')
export const certify = (data) => uni.$u.http.post('/users/certification', data)
export const uploadAvatar = (filePath) => {
  return uni.$u.http.upload('/upload/avatar', {
    filePath,
    name: 'file'
  })
}
export const getBrowsingHistory = (params) => uni.$u.http.get('/users/me/browsing', { params })
export const clearBrowsingHistory = () => uni.$u.http.delete('/users/me/browsing')
