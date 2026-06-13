import { readToken } from '../constants/storage'
const API_URL = '/api/v1'

export const login = (data) => uni.http.post('/users/login', data)
export const register = (data) => uni.http.post('/users/register', data)
export const getUserInfo = () => uni.http.get('/users/me')
export const updateUserInfo = (data) => uni.http.put('/users/me', data)
export const getUserStats = () => uni.http.get('/users/me/stats')
export const certify = (data) => uni.http.post('/users/certification', data)
export const changePassword = (data) => uni.http.put('/users/me/password', data)
export const updatePhone = (data) => uni.http.put('/users/me/phone', data)
export const sendSms = (data) => uni.http.post('/users/sms/send', data)
export const getUser = (id) => uni.http.get('/users/' + id)
export const uploadAvatar = (filePath) => {
  const token = readToken()
  const authHeader = token ? { 'Authorization': 'Bearer ' + token } : {}
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: API_URL + '/users/me/avatar',
      filePath,
      name: 'file',
      header: authHeader,
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (res.statusCode === 200 && data.code === 200) {
            resolve(data)
          } else {
            uni.$u.toast(data.message || '上传失败')
            reject(new Error(data.message || '上传失败'))
          }
        } catch (e) {
          reject(new Error('服务器返回数据解析失败'))
        }
      },
      fail: (err) => reject(err)
    })
  })
}
export const getBrowsingHistory = (params) => uni.http.get('/users/me/browsing', { params })
export const clearBrowsingHistory = () => uni.http.delete('/users/me/browsing')
export const getUnreadCount = () => uni.http.get('/messages/unread-count')
export const getMessageList = (params) => uni.http.get('/messages', { params })
