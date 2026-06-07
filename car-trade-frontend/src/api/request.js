const request = {
  get(url, options) {
    return uni.$u.http.get(url, options)
  },
  post(url, data, options) {
    return uni.$u.http.post(url, data, options)
  },
  put(url, data, options) {
    return uni.$u.http.put(url, data, options)
  },
  delete(url, data, options) {
    return uni.$u.http.delete(url, data, options)
  }
}

export function setupHttp() {
  const http = uni.$u.http

  // setConfig 要求传入回调函数（luch-request API）
  http.setConfig((config) => {
    config.baseURL = 'http://localhost:8080/api/v1'
    return config
  })

  // 请求拦截器
  http.interceptors.request.use((config) => {
    const token = uni.getStorageSync('token')
    if (token) {
      config.header = config.header || {}
      config.header.Authorization = 'Bearer ' + token
    }
    const platform = uni.getSystemInfoSync().platform
    let platformFlag = 'h5'
    if (platform === 'ios' || platform === 'android') {
      platformFlag = 'app'
    } else if (uni.getProvider) {
      platformFlag = 'mp'
    }
    config.header = config.header || {}
    config.header['X-Platform'] = platformFlag
    return config
  }, (error) => {
    return Promise.reject(error)
  })

  // 响应拦截器
  http.interceptors.response.use((response) => {
    return response.data
  }, (error) => {
    if (error && error.statusCode === 401) {
      uni.clearStorageSync()
      uni.navigateTo({ url: '/pages/login/index' })
    }
    const msg =
      (error && error.data && error.data.message) ||
      '请求失败，请重试'
    uni.$u.toast(msg)
    return Promise.reject(error)
  })
}

export default request
