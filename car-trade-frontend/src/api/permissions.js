/**
 * ============================================
 *  前端权限控制 — 真实实现
 * ============================================
 *
 * 与后端 WebMvcConfig 白名单 + @RequiresAuth 注解语义保持一致。
 *
 * 重要：客户端权限仅用于"友好提示/守卫"，真正的权限判定必须由后端完成。
 *
 * 公开接口白名单（PUBLIC）允许未登录访问
 * 商家接口白名单（CERTIFIED）要求用户已认证为商家
 * 其余默认 PROTECTED（需要登录）
 * ============================================
 */

import { STORAGE_KEYS, TOKEN_MIN_LENGTH, readToken } from '../constants/storage.js'

export const AUTH_LEVEL = {
  PUBLIC: 'public',
  PROTECTED: 'protected',
  CERTIFIED: 'certified'
}

// 公开接口白名单：{ 路径正则, 允许的 HTTP 方法（null 表示任意方法） }
const PUBLIC_ENDPOINTS = [
  { path: /^\/users\/login$/, method: 'POST' },
  { path: /^\/users\/register$/, method: 'POST' },
  { path: /^\/users\/sms\/send$/, method: 'POST' },
  { path: /^\/cars$/, method: 'GET' },
  { path: /^\/cars\/[\w-]+$/, method: 'GET' },
  { path: /^\/cars\/recommend$/, method: 'GET' },
  { path: /^\/cars\/export$/, method: 'GET' },
  { path: /^\/cars\/images\/proxy$/, method: 'GET' },
  { path: /^\/cars\/[\w-]+\/images\/[\w-]+\/download$/, method: 'GET' },
  { path: /^\/purchases$/, method: 'GET' },
  { path: /^\/purchases\/[\w-]+$/, method: 'GET' },
  { path: /^\/auctions$/, method: 'GET' },
  { path: /^\/auctions\/[\w-]+$/, method: 'GET' },
  { path: /^\/coupons$/, method: 'GET' },
  { path: /^\/membership\/plans$/, method: 'GET' },
  { path: /^\/cities/, method: 'GET' },
  { path: /^\/brands/, method: 'GET' },
  { path: /^\/banners/, method: 'GET' },
  { path: /^\/configs\/[\w-]+$/, method: 'GET' },
  { path: /^\/follows\/[\w-]+\/check$/, method: 'GET' },
  { path: /^\/ai\/chat$/, method: 'POST' },
  { path: /^\/ai\/search$/, method: 'POST' },
  { path: /^\/ai\/market-analysis$/, method: 'POST' },
  // 用户公开信息（仅数字 ID，排除 /users/login、/users/register、/users/me 等）
  { path: /^\/users\/\d+$/, method: 'GET' }
]

// 商家认证接口白名单
const CERTIFIED_ENDPOINTS = [
  { path: /^\/cars$/, method: 'POST' },
  { path: /^\/cars\/[\w-]+$/, method: 'PUT' },
  { path: /^\/cars\/[\w-]+$/, method: 'DELETE' },
  { path: /^\/shop\/members/, method: null },
  { path: /^\/ai\/distribute$/, method: 'POST' },
  { path: /^\/ai\/auto-outreach$/, method: 'POST' },
  { path: /^\/ai\/customer-generation$/, method: 'POST' },
  { path: /^\/ai\/copywriting$/, method: 'POST' },
  { path: /^\/purchases$/, method: 'POST' },
  { path: /^\/auctions$/, method: 'POST' }
]

/**
 * 剥离 URL 中的 baseURL（支持完整 URL 与相对路径）
 * 防止路径穿越（../）
 */
function normalizePath(url) {
  if (url == null || url === '') return ''
  let path = String(url).trim()
  if (path.startsWith('http://') || path.startsWith('https://')) {
    try {
      path = new URL(path).pathname
    } catch (e) { /* fall through */ }
  }
  // 去掉 /api/v1 前缀
  path = path.replace(/^\/api\/v1/, '')
  // 防止路径穿越：去掉所有 ..
  path = path.replace(/\.\.+/g, '')
  return path
}

function matchRule(url, method, rules) {
  if (!url) return false
  const path = normalizePath(url)
  if (!path) return false
  const m = (method || 'GET').toUpperCase()
  for (const rule of rules) {
    if (!rule.path.test(path)) continue
    if (rule.method && rule.method.toUpperCase() !== m) continue
    return true
  }
  return false
}

export function isPublicEndpoint(url, method) {
  return matchRule(url, method, PUBLIC_ENDPOINTS)
}

export function requiresCertification(url, method) {
  return matchRule(url, method, CERTIFIED_ENDPOINTS)
}

export function getEndpointAuthLevel(url, method) {
  if (isPublicEndpoint(url, method)) return AUTH_LEVEL.PUBLIC
  if (requiresCertification(url, method)) return AUTH_LEVEL.CERTIFIED
  return AUTH_LEVEL.PROTECTED
}

export function isAuthed() {
  return !!readToken()
}
