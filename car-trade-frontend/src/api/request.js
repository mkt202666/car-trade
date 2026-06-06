const http = uni.$u.http

http.setConfig({
  baseURL: 'http://localhost:8080/api/v1'
})

http.interceptors.request.use(config => {
  const token = uni.getStorageSync('token')
  if (token) {
    config.header.Authorization = 'Bearer ' + token
  }
  const platform = uni.getSystemInfoSync().platform
  let platformFlag = 'h5'
  if (platform === 'ios' || platform === 'android') {
    platformFlag = 'app'
  } else if (uni.getProvider) {
    platformFlag = 'mp'
  }
  config.header['X-Platform'] = platformFlag
  return config
})

http.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.statusCode === 401) {
      uni.clearStorageSync()
      uni.navigateTo({ url: '/pages/login/index' })
    }
    const msg = error.data && error.data.message ? error.data.message : '请求失败，请重试'
    uni.$u.toast(msg)
    return Promise.reject(error)
  }
)

export default {
  get: http.get,
  post: http.post,
  put: http.put,
  delete: http.delete
}
