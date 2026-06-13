import axios from 'axios'

const ADMIN_TOKEN_KEY = 'admin_token'
const ADMIN_REFRESH_TOKEN_KEY = 'admin_refresh_token'
const ADMIN_USER_KEY = 'admin_user'

const request = axios.create({
  baseURL: '/api/v1/admin',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor - attach token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(ADMIN_TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor - handle errors + token refresh
let isRefreshing = false
let pendingRequests = []

request.interceptors.response.use(
  (response) => {
    const data = response.data
    // Backend returns ApiResponse<T>: { code, message, data }
    if (data.code === 0 || data.code === 200) {
      return data.data
    }
    // Business error
    return Promise.reject(new Error(data.message || '请求失败'))
  },
  async (error) => {
    const originalRequest = error.config

    if (error.response) {
      const status = error.response.status

      // 401 - try refresh token
      if (status === 401 && !originalRequest._retry) {
        if (isRefreshing) {
          // Queue pending requests while refreshing
          return new Promise((resolve, reject) => {
            pendingRequests.push({ resolve, reject, config: originalRequest })
          })
        }

        originalRequest._retry = true
        isRefreshing = true

        const refreshToken = localStorage.getItem(ADMIN_REFRESH_TOKEN_KEY)
        if (!refreshToken) {
          localStorage.removeItem(ADMIN_TOKEN_KEY)
          localStorage.removeItem(ADMIN_USER_KEY)
          localStorage.removeItem(ADMIN_REFRESH_TOKEN_KEY)
          window.location.href = '/login'
          return Promise.reject(new Error('登录已过期，请重新登录'))
        }

        try {
          const res = await axios.post('/api/v1/admin/auth/refresh', { refreshToken })
          const newToken = res.data.data.accessToken
          localStorage.setItem(ADMIN_TOKEN_KEY, newToken)

          // Retry pending requests
          pendingRequests.forEach(({ resolve, reject, config }) => {
            config.headers.Authorization = `Bearer ${newToken}`
            request(config).then(resolve).catch(reject)
          })
          pendingRequests = []

          // Retry original request
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          return request(originalRequest)
        } catch (refreshError) {
          // Refresh failed - logout
          pendingRequests.forEach(({ reject }) => reject(refreshError))
          pendingRequests = []
          localStorage.removeItem(ADMIN_TOKEN_KEY)
          localStorage.removeItem(ADMIN_USER_KEY)
          localStorage.removeItem(ADMIN_REFRESH_TOKEN_KEY)
          window.location.href = '/login'
          return Promise.reject(new Error('登录已过期，请重新登录'))
        } finally {
          isRefreshing = false
        }
      }

      if (status === 403) {
        return Promise.reject(new Error('没有权限访问'))
      }

      const msg = error.response.data?.message || `服务器错误 (${status})`
      return Promise.reject(new Error(msg))
    }

    if (error.code === 'ECONNABORTED') {
      return Promise.reject(new Error('请求超时，请重试'))
    }

    return Promise.reject(new Error(error.message || '网络异常'))
  }
)

export default request
