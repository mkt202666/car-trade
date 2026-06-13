/**
 * ============================================
 *  前端存储 Key 集中常量
 * ============================================
 *
 * 严禁在多个文件中重复硬编码 key。所有 storage / 跨进程 key 必须从这里导出。
 * 与后端 JwtUtil、Redis 黑名单 key 保持一致。
 * ============================================
 */

export const STORAGE_KEYS = {
  /** JWT access token（短效） */
  TOKEN: 'token',
  /** JWT refresh token（长效） */
  REFRESH_TOKEN: 'refresh_token',
  /** refresh token 过期时间戳（毫秒） */
  REFRESH_EXPIRES_AT: 'refresh_expires_at',
  /** 当前用户信息（缓存，避免每次进 app 都要 /users/me） */
  USER_INFO: 'user_info',
  /** 历史搜索关键词 */
  SEARCH_HISTORY: 'search_history',
  /** 多语言/多主题 */
  LOCALE: 'app_locale'
}

/** token 最小合法长度（与后端签发一致，HS256 至少 80+ 字符） */
export const TOKEN_MIN_LENGTH = 20

/**
 * 统一 token 读取：先读 uni 同步存储，再读 localStorage 兜底（H5/小程序兼容）
 */
export function readToken() {
  try {
    const t = uni.getStorageSync(STORAGE_KEYS.TOKEN)
    if (t && t !== 'null' && t !== 'undefined' && String(t).trim().length > TOKEN_MIN_LENGTH) {
      return String(t).trim()
    }
  } catch (_) { /* ignore */ }
  try {
    const t = localStorage.getItem(STORAGE_KEYS.TOKEN)
    if (t && t !== 'null' && t !== 'undefined' && String(t).trim().length > TOKEN_MIN_LENGTH) {
      return String(t).trim()
    }
  } catch (_) { /* ignore */ }
  return null
}

export function readRefreshToken() {
  try {
    const t = uni.getStorageSync(STORAGE_KEYS.REFRESH_TOKEN)
    return t && t !== 'null' && t !== 'undefined' ? String(t).trim() : null
  } catch (_) { return null }
}

export function readRefreshExpiresAt() {
  try {
    const t = uni.getStorageSync(STORAGE_KEYS.REFRESH_EXPIRES_AT)
    return t ? Number(t) : 0
  } catch (_) { return 0 }
}
