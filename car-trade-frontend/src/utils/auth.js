/**
 * ============================================
 *  前端认证与权限工具
 * ============================================
 * 
 * 提供纯函数，便于在页面 / 组件 / 拦截器中直接调用：
 *   - isAuthed()           是否已登录（基于 Storage，不依赖 Store）
 *   - isPublicEndpoint()   某 URL 是否为公开接口
 *   - requireAuth()        登录守卫：未登录则跳转到登录页
 *   - requireAuthAsync()   异步版登录守卫（返回 Promise）
 *   - requireRole()        角色守卫（如商家认证）
 *   - saveToken()          保存token
 *   - clearToken()         清除token
 * 
 * 注意：所有跳转函数均返回 boolean，表示"是否已通过校验"
 * ============================================
 */

import store from '@/store'
import {
  isPublicEndpoint as _isPublicEndpoint,
  requiresCertification,
  AUTH_LEVEL
} from '@/api/permissions'
import { STORAGE_KEYS, TOKEN_MIN_LENGTH, readToken } from '@/constants/storage'

// 旧 user info key：保留只读兼容，新代码应统一使用 STORAGE_KEYS.USER_INFO
const LEGACY_USER_KEY = '5d_user_info'

export function saveToken(token, refreshToken, refreshExpiresIn) {
  try {
    uni.setStorageSync(STORAGE_KEYS.TOKEN, token)
    try { localStorage.setItem(STORAGE_KEYS.TOKEN, token) } catch (_) {}
    if (refreshToken) {
      uni.setStorageSync(STORAGE_KEYS.REFRESH_TOKEN, refreshToken)
      try { localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken) } catch (_) {}
    }
    if (refreshExpiresIn && Number(refreshExpiresIn) > 0) {
      // expiresIn 是秒，换算成毫秒时间戳
      const expiresAt = Date.now() + Number(refreshExpiresIn) * 1000
      uni.setStorageSync(STORAGE_KEYS.REFRESH_EXPIRES_AT, String(expiresAt))
    }
    console.log('[Auth] Token saved successfully, length=' + (token ? token.length : 0))
  } catch (e) {
    console.log('[Auth] saveToken error:', e)
    try { localStorage.setItem(STORAGE_KEYS.TOKEN, token) } catch (_) {}
  }
}

/** 统一读取 token — 先读 uni storage，失败再尝试原生 localStorage */
export function getToken() {
  return readToken()
}

export function clearToken() {
  try {
    uni.removeStorageSync(STORAGE_KEYS.TOKEN)
    uni.removeStorageSync(STORAGE_KEYS.REFRESH_TOKEN)
    uni.removeStorageSync(STORAGE_KEYS.REFRESH_EXPIRES_AT)
    try { localStorage.removeItem(STORAGE_KEYS.TOKEN) } catch (_) {}
    try { uni.removeStorageSync(STORAGE_KEYS.USER_INFO) } catch (_) {}
    try { localStorage.removeItem(STORAGE_KEYS.USER_INFO) } catch (_) {}
    // 兼容旧 key 清理
    try { uni.removeStorageSync(LEGACY_USER_KEY) } catch (_) {}
    try { localStorage.removeItem(LEGACY_USER_KEY) } catch (_) {}
    console.log('[Auth] Token cleared')
  } catch (e) {
    console.log('[Auth] clearToken error:', e)
  }
}

/** 是否已登录（基于 localStorage，确保刷新后能正确保持） */
export function isAuthed() {
  try {
    const token = getToken()
    return !!token && token !== 'null' && token !== 'undefined' && token.length > TOKEN_MIN_LENGTH
  } catch (e) {
    console.log('[Auth] isAuthed error:', e)
    return false
  }
}

/** 判断某 URL 是否为公开接口（method 不区分大小写） */
export function isPublicEndpoint(url, method) {
  return _isPublicEndpoint(url, method)
}

/** 判断某接口是否需要商家认证 */
export function isCertificationRequired(url, method) {
  return requiresCertification(url, method)
}

/** 获取某接口的权限级别 */
export function getAuthLevel(url, method) {
  if (_isPublicEndpoint(url, method)) return AUTH_LEVEL.PUBLIC
  if (requiresCertification(url, method)) return AUTH_LEVEL.CERTIFIED
  return AUTH_LEVEL.PROTECTED
}

/**
 * 登录守卫：未登录则跳转到登录页
 * @param {object} options 跳转配置
 * @param {string} options.redirect   登录后返回的页面（可选，默认返回当前页）
 * @param {string} options.tip        自定义提示文案（默认 '请先登录'）
 * @returns {boolean} true=已登录通过，false=未登录并已跳转
 */
export function requireAuth(options = {}) {
  const token = getToken()
  console.log('[Auth] requireAuth called, token=' + (token ? 'found(' + token.length + ')' : 'not found'))
  if (token && token.length > 10) {
    return true
  }
  // 未登录 — 友好提示并跳转
  const tip = options.tip || '请先登录'
  uni.$u.toast(tip)
  const redirect = options.redirect
    ? encodeURIComponent(options.redirect)
    : encodeURIComponent(getCurrentPagePath())
  setTimeout(() => {
    uni.navigateTo({ url: '/pages/login/index?redirect=' + redirect })
  }, 300)
  return false
}

/** 异步版登录守卫（可在 async onLoad / async 方法中使用） */
export function requireAuthAsync(options = {}) {
  return new Promise((resolve) => {
    if (isAuthed()) {
      resolve(true)
      return
    }
    const redirect = options.redirect
      ? encodeURIComponent(options.redirect)
      : encodeURIComponent(getCurrentPagePath())
    uni.navigateTo({ url: '/pages/login/index?redirect=' + redirect })
    resolve(false)
  })
}

/**
 * 角色守卫：检查当前用户是否为认证商家
 * 只做轻量检查（读 Store），真正的权限校验由后端完成
 */
export function requireRole(requiredRole) {
  const user = store.state.user
  if (!user) {
    uni.navigateTo({ url: '/pages/login/index' })
    return false
  }
  // 认证商家：certificationStatus === 'CERTIFIED'
  if (requiredRole === 'CERTIFIED' && user.certificationStatus !== 'CERTIFIED') {
    uni.$u.toast('请先完成商家认证')
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/profile/index' })
    }, 500)
    return false
  }
  return true
}

/** 获取当前页面路径（用于登录后的回跳） */
function getCurrentPagePath() {
  try {
    const pages = getCurrentPages()
    if (pages && pages.length > 0) {
      const p = pages[pages.length - 1]
      return p.route || p.$page && p.$page.fullPath || '/pages/home/index'
    }
  } catch (e) { /* ignore */ }
  return '/pages/home/index'
}

export default {
  isAuthed,
  isPublicEndpoint,
  isCertificationRequired,
  getAuthLevel,
  requireAuth,
  requireAuthAsync,
  requireRole
}
