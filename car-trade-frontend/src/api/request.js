/**
 * ============================================
 *  HTTP 请求封装（集成权限控制）
 * ============================================
 * 
 * 改动点：
 *  1. 请求拦截器：区分公开接口 / 需登录接口
 *     - 公开接口：不强制要求 token，未登录也可正常请求
 *     - 需登录接口：未登录时直接 reject，不发请求到后端，同时提示"请先登录"
 *  2. 响应拦截器：区分 401 的处理方式
 *     - 公开接口返回 401：正常 reject，不跳登录页（可能后端配置有误）
 *     - 需登录接口返回 401：清除本地 token 并跳转到登录页
 *     - 403：商家认证相关，只提示，不清 token 不跳登录页
 * ============================================
 */

import { isPublicEndpoint, isAuthed, AUTH_LEVEL, getEndpointAuthLevel } from './permissions'

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

/**
 * 从 luch-request 的 config 对象中提取 URL 和 Method
 * luch-request 的 config.url 可能是 "/users/login" 或 "http://xxx/users/login"
 * config.method 为大写字符串（在 2.x 版本为小写）
 */
function extractUrlAndMethod(config) {
  const url = config.url || ''
  const method = (config.method || 'GET').toUpperCase()
  return { url, method }
}

export function setupHttp() {
  const http = uni.$u.http

  // setConfig 要求传入回调函数（luch-request API）
  http.setConfig((config) => {
    config.baseURL = 'http://localhost:8080/api/v1'
    // 全局超时：15 秒
    config.timeout = 15000
    return config
  })

  /**
   * 请求拦截器
   * 核心改动：
   *  - 公开接口：不强制 token，未登录也可请求
   *  - 需登录接口：未登录时直接 reject，不发请求到后端
   */
  http.interceptors.request.use((config) => {
    const { url, method } = extractUrlAndMethod(config)
    const isPublic = isPublicEndpoint(url, method)
    const authLevel = getEndpointAuthLevel(url, method)

    // 1) 注入通用平台标识（不区分权限）
    const platform = uni.getSystemInfoSync().platform
    let platformFlag = 'h5'
    if (platform === 'ios' || platform === 'android') {
      platformFlag = 'app'
    } else if (uni.getProvider) {
      platformFlag = 'mp'
    }
    config.header = config.header || {}
    config.header['X-Platform'] = platformFlag

    // 2) 读取 token
    const token = uni.getStorageSync('token')
    if (token) {
      config.header.Authorization = 'Bearer ' + token
    }

    // 3) 权限判定
    //   - 公开接口：直接放行
    //   - 需登录接口：未登录 → 直接 reject，toast 提示
    if (!isPublic && !token) {
      // 未登录访问受保护接口 → 前端直接拦截，不发请求
      // 这样可以避免后端"所有接口都要求 token"的配置导致公开接口也 401
      // 同时用户能看到明确提示，也能跳转到登录页
      uni.$u.toast('请先登录后再操作')
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/login/index' })
      }, 300)
      // 返回一个 Promise.reject 给 luch-request，中断本次请求
      return Promise.reject({
        statusCode: 401,
        data: { message: '未登录，已在前端拦截' },
        _interceptedByFrontend: true,
        _authLevel: authLevel
      })
    }

    // 4) 商家认证接口（轻量检查，有 token 就放行，具体由后端校验）
    //    这里不强校验商家状态，避免与 store 状态耦合过深；
    //    如后端返回 403，响应拦截器会提示"请先完成商家认证"
    return config
  }, (error) => {
    return Promise.reject(error)
  })

  /**
   * 响应拦截器
   * 核心改动：
   *  - 401：需登录接口才清 token + 跳登录页；公开接口 401 不跳登录页
   *  - 403：只提示"无权限/需商家认证"，不清 token 不跳登录页
   *  - 其他错误：统一 toast
   */
  http.interceptors.response.use((response) => {
    // 正常响应：返回业务数据
    return response.data
  }, (error) => {
    // 如果是前端请求拦截器主动 reject 的，直接透传给调用方
    if (error && error._interceptedByFrontend) {
      return Promise.reject(error)
    }

    const statusCode = error && error.statusCode
    const config = error && error.config
    const { url, method } = config
      ? extractUrlAndMethod(config)
      : { url: '', method: 'GET' }
    const isPublic = isPublicEndpoint(url, method)

    // --- 401 未认证 ---
    if (statusCode === 401) {
      if (isPublic) {
        // 公开接口 401：不应出现，视为后端异常，只报错，不跳登录页，不清 token
        const msg = (error && error.data && error.data.message) || '服务暂不可用，请稍后重试'
        uni.$u.toast(msg)
      } else {
        // 需登录接口 401：token 无效/过期 → 清除本地状态 + 跳登录页
        try {
          uni.clearStorageSync()
        } catch (e) { /* ignore */ }
        uni.$u.toast('登录已过期，请重新登录')
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/login/index' })
        }, 300)
      }
      return Promise.reject(error)
    }

    // --- 403 无权限（如未通过商家认证）---
    if (statusCode === 403) {
      const msg = (error && error.data && error.data.message) || '无权限进行此操作，请先完成商家认证'
      uni.$u.toast(msg)
      return Promise.reject(error)
    }

    // --- 其他错误：统一 toast ---
    const msg =
      (error && error.data && error.data.message) ||
      '请求失败，请重试'
    uni.$u.toast(msg)
    return Promise.reject(error)
  })
}

export default request
