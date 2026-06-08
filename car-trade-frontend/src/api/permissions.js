/**
 * ============================================
 *  接口权限配置中心
 * ============================================
 * 
 * 功能：
 *  - 定义接口权限级别（公开 / 需登录 / 需商家认证）
 *  - 提供 isPublicEndpoint / requiresAuth 等判断函数
 *  - 请求拦截器和响应拦截器从此处读取配置
 * 
 * 使用方式：
 *   import { isPublicEndpoint, AUTH_LEVEL, getEndpointAuthLevel } from '@/api/permissions'
 *   if (isPublicEndpoint(url, method)) { ... }
 * 
 * ============================================
 */

/** 权限级别 */
export const AUTH_LEVEL = {
  PUBLIC: 'public',       // 🔓 公开，未登录也可访问
  PROTECTED: 'protected', // 🔒 需要登录才能访问
  CERTIFIED: 'certified'  // 🔐 需要商家认证身份
}

/**
 * 📋 公开接口白名单
 * 匹配规则：path（正则） + method（大写，可选）
 * 匹配到任意一条即为公开接口
 */
export const PUBLIC_ENDPOINTS = [
  // -------- 认证相关 --------
  { path: /^\/users\/login$/, method: 'POST' },
  { path: /^\/users\/register$/, method: 'POST' },

  // -------- 车源浏览 --------
  { path: /^\/cars$/, method: 'GET' },                                 // GET /cars 车源列表
  { path: /^\/cars\/[\w-]+$/, method: 'GET' },                         // GET /cars/:id 车源详情
  { path: /^\/cars\/[\w-]+\/images\/[\w-]+\/download$/, method: 'GET' }, // 图片下载

  // -------- 公开查询数据 --------
  { path: /^\/coupons$/, method: 'GET' },                              // GET /coupons 可用优惠券
  { path: /^\/membership\/plans$/, method: 'GET' },                    // GET /membership/plans 会员套餐

  // -------- AI 开放接口（试用）--------
  { path: /^\/ai\/market-analysis$/, method: 'POST' },                 // 行情简报
  { path: /^\/ai\/search$/, method: 'POST' },                          // 智能搜索
  { path: /^\/ai\/chat$/, method: 'POST' },                            // AI 对话

  // -------- 关注状态查询 --------
  { path: /^\/follows\/[\w-]+\/check$/, method: 'GET' }                // 查询是否已关注某用户
]

/**
 * 🛡️ 需要商家认证的接口（除登录外还需认证通过）
 */
export const CERTIFIED_ENDPOINTS = [
  { path: /^\/cars$/, method: 'POST' },                                // POST /cars 发布车源
  { path: /^\/shop\/members/, method: null },                          // /shop/* 车行管理
  { path: /^\/ai\/distribute$/, method: 'POST' },                      // AI 分发车源
  { path: /^\/ai\/auto-outreach$/, method: 'POST' },                   // AI 自动触达
  { path: /^\/ai\/customer-generation$/, method: 'POST' }              // AI 文案生成
]

/**
 * 判断某请求是否为公开接口
 * @param {string} url       请求 URL（可以是完整 URL 或相对路径）
 * @param {string} method    HTTP 方法（GET/POST/PUT/DELETE 等，不区分大小写）
 * @returns {boolean}
 */
export function isPublicEndpoint(url, method) {
  if (!url) return false
  // 从 URL 中抽取 path（去除 query 和 baseURL）
  let path = url
  if (path.startsWith('http')) {
    try {
      const u = new URL(path)
      path = u.pathname.replace(/^\/api\/v1/, '')
    } catch (e) { /* ignore */ }
  }
  path = path.replace(/^\/api\/v1/, '') // 去除 baseURL 前缀
  const m = (method || 'GET').toUpperCase()

  for (const rule of PUBLIC_ENDPOINTS) {
    if (!rule.path.test(path)) continue
    if (rule.method && rule.method.toUpperCase() !== m) continue
    return true
  }
  return false
}

/**
 * 判断某接口是否需要商家认证
 */
export function requiresCertification(url, method) {
  if (!url) return false
  let path = url
  if (path.startsWith('http')) {
    try {
      const u = new URL(path)
      path = u.pathname.replace(/^\/api\/v1/, '')
    } catch (e) { /* ignore */ }
  }
  path = path.replace(/^\/api\/v1/, '')
  const m = (method || 'GET').toUpperCase()

  for (const rule of CERTIFIED_ENDPOINTS) {
    if (!rule.path.test(path)) continue
    if (rule.method && rule.method.toUpperCase() !== m) continue
    return true
  }
  return false
}

/**
 * 获取某接口的权限级别
 * @returns {string} AUTH_LEVEL.PUBLIC / PROTECTED / CERTIFIED
 */
export function getEndpointAuthLevel(url, method) {
  if (isPublicEndpoint(url, method)) return AUTH_LEVEL.PUBLIC
  if (requiresCertification(url, method)) return AUTH_LEVEL.CERTIFIED
  return AUTH_LEVEL.PROTECTED
}

/**
 * 当前是否已登录（基于 Storage + Store）
 * 优先读 Storage，避免依赖 Store 初始化时机
 */
export function isAuthed() {
  try {
    const token = uni.getStorageSync('token')
    return !!token
  } catch (e) {
    return false
  }
}
