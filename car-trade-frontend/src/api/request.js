import { isPublicEndpoint, requiresCertification, getEndpointAuthLevel } from './permissions'
import { getToken, clearToken, saveToken } from '../utils/auth'
import { readToken, readRefreshToken, readRefreshExpiresAt } from '../constants/storage'

const API_URL = '/api/v1'

function getAuthToken() {
  return readToken()
}

/**
 * 主动续期 access token。
 * 调用 POST /users/refresh，成功后保存新的 access/refresh token。
 *
 * 并发安全：使用 in-flight Promise 防止多个请求同时触发续期。
 *
 * @returns {Promise<boolean>} true 表示续期成功（或无需续期），false 表示续期失败
 */
let refreshInFlight = null
async function refreshAccessToken() {
  if (refreshInFlight) return refreshInFlight
  const refreshToken = readRefreshToken()
  if (!refreshToken) return false
  // 距过期 < 60s 才续期，避免无谓请求
  const expiresAt = readRefreshExpiresAt()
  if (expiresAt && Date.now() > expiresAt - 60 * 1000) {
    return false
  }
  refreshInFlight = (async () => {
    try {
      const res = await new Promise((resolve, reject) => {
        uni.request({
          url: API_URL + '/users/refresh',
          method: 'POST',
          header: { 'Content-Type': 'application/json' },
          data: { refreshToken },
          success: (r) => resolve(r),
          fail: (e) => reject(e)
        })
      })
      const data = res && res.data && res.data.data
      if (data && data.accessToken) {
        saveToken(data.accessToken, data.refreshToken, data.expiresIn)
        console.log('[Auth] token refreshed, new len=' + (data.accessToken ? data.accessToken.length : 0))
        return true
      }
      console.warn('[Auth] refresh failed: no accessToken in response')
      return false
    } catch (e) {
      console.warn('[Auth] refresh error:', e && e.message)
      return false
    } finally {
      refreshInFlight = null
    }
  })()
  return refreshInFlight
}

function buildConfig(options, tokenOverride) {
  const { url, method = 'GET', data = null, params = null, headers = {}, responseType = 'json', withCredentials = true } = options
  const token = tokenOverride !== undefined ? tokenOverride : getAuthToken()
  const authHeader = token ? { Authorization: `Bearer ${token}` } : {}
  return {
    url: API_URL + url,
    method,
    data,
    params,
    header: { ...authHeader, ...headers },
    dataType: responseType === 'json' ? 'json' : 'text',
    withCredentials,
  }
}

function request(options) {
  return new Promise((resolve, reject) => {
    const config = buildConfig(options)
    uni.request(config).then((response) => {
      const { data: resData, statusCode, errMsg } = response
      if (statusCode === 200) {
        // ⚠ 关键点：后端 GlobalExceptionHandler 使用 @ResponseStatus(HttpStatus.OK)
        // 即使业务逻辑失败（如登录错误）也会返回 HTTP 200，但 ApiResponse.code != 200
        if (resData && typeof resData.code !== 'undefined' && resData.code !== 200) {
          // 业务层面的失败 → 直接 reject，不触发 token 续期
          const message = (resData && resData.message) || errMsg || '请求失败'
          const err = new Error(message)
          err.status = resData.code
          err.data = resData
          reject(err)
        } else {
          resolve(resData)
        }
      } else if (statusCode === 401) {
        // HTTP 层 401（token 过期或无效）→ 尝试用 refresh token 续期并重试
        handleHttp401(options, resolve, reject)
      } else if (statusCode === 403) {
        const message = (resData && resData.message) || '权限不足'
        uni.$u.toast(message)
        const err = new Error(message)
        err.status = 403
        reject(err)
      } else {
        const message = (resData && resData.message) || errMsg || '请求失败'
        uni.$u.toast(message)
        const err = new Error(message)
        err.status = statusCode
        reject(err)
      }
    }).catch((error) => {
      const message = (error && error.errMsg) || '网络错误，请稍后重试'
      uni.$u.toast(message)
      const err = new Error(message)
      err.status = 500
      reject(err)
    })
  })
}

async function handleHttp401(originalOptions, resolve, reject) {
  const refreshed = await refreshAccessToken()
  if (refreshed) {
    try {
      const newToken = readToken()
      const retryConfig = buildConfig(originalOptions, newToken)
      const retryRes = await uni.request(retryConfig)
      if (retryRes.statusCode === 200) {
        const resData = retryRes.data
        if (resData && typeof resData.code !== 'undefined' && resData.code !== 200) {
          const err = new Error(resData.message || '请求失败')
          err.status = resData.code
          reject(err)
        } else {
          resolve(resData)
        }
      } else if (retryRes.statusCode === 401) {
        doLogout(reject, '登录已过期，请重新登录')
      } else {
        const err = new Error('请求失败，请稍后重试')
        err.status = retryRes.statusCode
        reject(err)
      }
    } catch (e) {
      const message = (e && e.errMsg) || '网络错误，请稍后重试'
      const err = new Error(message)
      err.status = 500
      reject(err)
    }
  } else {
    doLogout(reject, '登录已过期，请重新登录')
  }
}

function doLogout(reject, msg) {
  clearToken()
  uni.$u.toast(msg || '登录已过期')
  setTimeout(() => {
    uni.navigateTo({ url: '/pages/login/index' })
  }, 300)
  reject(new Error(msg || '登录已过期，请重新登录'))
}

function setupHttp() {
  uni.http = {
    get: (url, options = {}) => request({ ...options, url, method: 'GET' }),
    post: (url, data, options = {}) => request({ ...options, url, method: 'POST', data }),
    put: (url, data, options = {}) => request({ ...options, url, method: 'PUT', data }),
    delete: (url, options = {}) => request({ ...options, url, method: 'DELETE' }),
    upload: (url, options = {}) => request({ ...options, url, method: 'UPLOAD' }),
  }
}

export { request, isPublicEndpoint, requiresCertification, getEndpointAuthLevel, setupHttp, refreshAccessToken }
export default request
