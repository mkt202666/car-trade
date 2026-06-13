/**
 * ============================================
 *  前端存储 - 兼容层（已废弃，请使用 constants/storage.js）
 * ============================================
 *
 * 保留此文件仅为兼容旧引用，所有新代码应直接使用
 *   import { readToken, saveToken, clearToken, STORAGE_KEYS, TOKEN_MIN_LENGTH }
 *     from '@/constants/storage'
 * ============================================
 */

import { STORAGE_KEYS, TOKEN_MIN_LENGTH, readToken } from '@/constants/storage'

// 旧 key 兼容：早期版本使用 '5d_user_token'，现统一为 STORAGE_KEYS.TOKEN
const LEGACY_TOKEN_KEY = '5d_user_token'
const LEGACY_USER_KEY = '5d_user_info'

/**
 * 读取 token（优先 STORAGE_KEYS.TOKEN，再读 localStorage 兜底，再读旧 key）
 * 委托给 constants/storage.js 的 readToken 实现。
 */
export function getToken() {
  return readToken()
}

/** 保存 token（写入 uni storage + localStorage 双写，保证刷新可用） */
export function setToken(token) {
  if (!token) {
    clearToken()
    return
  }
  try {
    uni.setStorageSync(STORAGE_KEYS.TOKEN, token)
  } catch (_) { /* ignore */ }
  try { localStorage.setItem(STORAGE_KEYS.TOKEN, token) } catch (_) { /* ignore */ }
  // 清理旧 key，避免遗留
  try { uni.removeStorageSync(LEGACY_TOKEN_KEY) } catch (_) {}
  try { localStorage.removeItem(LEGACY_TOKEN_KEY) } catch (_) {}
}

/** 清除 token（同时清掉旧 key 残留） */
export function removeToken() {
  try { uni.removeStorageSync(STORAGE_KEYS.TOKEN) } catch (_) {}
  try { localStorage.removeItem(STORAGE_KEYS.TOKEN) } catch (_) {}
  try { uni.removeStorageSync(LEGACY_TOKEN_KEY) } catch (_) {}
  try { localStorage.removeItem(LEGACY_TOKEN_KEY) } catch (_) {}
}

export function getUser() {
  try {
    const raw = uni.getStorageSync(STORAGE_KEYS.USER_INFO)
    if (raw) {
      return typeof raw === 'string' ? safeJsonParse(raw) : raw
    }
  } catch (_) { /* ignore */ }
  // 兼容旧 key
  try {
    const raw = uni.getStorageSync(LEGACY_USER_KEY)
    if (raw) {
      const parsed = typeof raw === 'string' ? safeJsonParse(raw) : raw
      if (parsed) {
        // 迁移到新 key
        try { uni.setStorageSync(STORAGE_KEYS.USER_INFO, typeof raw === 'string' ? raw : JSON.stringify(parsed)) } catch (_) {}
        try { localStorage.setItem(STORAGE_KEYS.USER_INFO, typeof raw === 'string' ? raw : JSON.stringify(parsed)) } catch (_) {}
        return parsed
      }
    }
  } catch (_) { /* ignore */ }
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.USER_INFO) || localStorage.getItem(LEGACY_USER_KEY)
    if (raw) return safeJsonParse(raw)
  } catch (_) { /* ignore */ }
  return null
}

export function setUser(user) {
  const payload = user ? JSON.stringify(user) : ''
  try { uni.setStorageSync(STORAGE_KEYS.USER_INFO, payload) } catch (_) {}
  try { localStorage.setItem(STORAGE_KEYS.USER_INFO, payload) } catch (_) {}
  // 清理旧 key
  try { uni.removeStorageSync(LEGACY_USER_KEY) } catch (_) {}
  try { localStorage.removeItem(LEGACY_USER_KEY) } catch (_) {}
}

export function clearStorage() {
  try { uni.clearStorageSync() } catch (_) {}
  try { localStorage.clear() } catch (_) {}
}

function safeJsonParse(raw) {
  try { return JSON.parse(raw) } catch (_) { return null }
}

export { TOKEN_MIN_LENGTH, STORAGE_KEYS }
