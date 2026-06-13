/**
 * ============================================
 *  权限控制单元测试 & 集成测试
 * ============================================
 *
 * 直接 import 真实实现（src/api/permissions.js），
 * 防止"桩函数替换真实实现"类回归。
 *
 * 运行：node src/api/__tests__/permissions.test.js
 * ============================================
 */

/* eslint-disable no-console */

// 模拟 uni 对象（Node 环境中无 uni，需自行 mock）
const mockStorage = {}
global.uni = {
  getStorageSync(key) { return mockStorage[key] || '' },
  setStorageSync(key, value) { mockStorage[key] = value },
  removeStorageSync(key) { delete mockStorage[key] },
  clearStorageSync() { Object.keys(mockStorage).forEach((k) => delete mockStorage[k]) }
}
global.localStorage = {
  getItem(k) { return mockStorage['ls_' + k] || null },
  setItem(k, v) { mockStorage['ls_' + k] = String(v) },
  removeItem(k) { delete mockStorage['ls_' + k] }
}

// 通过 import() 动态加载 ESM 模块
const mod = await import('../../api/permissions.js')
const { AUTH_LEVEL, isPublicEndpoint, requiresCertification, getEndpointAuthLevel, isAuthed } = mod

// ---------- 测试框架（极简实现）----------
let passed = 0
let failed = 0
const failures = []

function test(name, fn) {
  try {
    fn()
    console.log('  ✅ ' + name)
    passed++
  } catch (err) {
    console.log('  ❌ ' + name)
    console.log('     ' + err.message)
    failed++
    failures.push({ name, error: err })
  }
}

function expect(actual) {
  return {
    toBe(expected) {
      if (actual !== expected) {
        throw new Error('Expected ' + JSON.stringify(expected) + ' but got ' + JSON.stringify(actual))
      }
    },
    toEqual(expected) {
      const a = JSON.stringify(actual)
      const e = JSON.stringify(expected)
      if (a !== e) {
        throw new Error('Expected ' + e + ' but got ' + a)
      }
    },
    toBeTruthy() {
      if (!actual) throw new Error('Expected truthy but got ' + JSON.stringify(actual))
    },
    toBeFalsy() {
      if (actual) throw new Error('Expected falsy but got ' + JSON.stringify(actual))
    }
  }
}

function section(title) {
  console.log('\n📦 ' + title)
}

// ============================================================
// 开始测试
// ============================================================
console.log('========================================')
console.log('  权限控制测试套件')
console.log('========================================')

// ---------- 测试用例 1：公开接口白名单 ----------
section('isPublicEndpoint() - 公开接口白名单匹配')

test('POST /users/login 应该是公开接口', () => {
  expect(isPublicEndpoint('/users/login', 'POST')).toBe(true)
})
test('POST /users/register 应该是公开接口', () => {
  expect(isPublicEndpoint('/users/register', 'POST')).toBe(true)
})
test('GET /users/login 不应该是公开接口（方法不匹配）', () => {
  expect(isPublicEndpoint('/users/login', 'GET')).toBe(false)
})

test('GET /cars 车源列表应该是公开接口', () => {
  expect(isPublicEndpoint('/cars', 'GET')).toBe(true)
})
test('GET /cars/abc123 车源详情应该是公开接口', () => {
  expect(isPublicEndpoint('/cars/abc123', 'GET')).toBe(true)
})
test('POST /cars 发布车源不应该是公开接口（需登录）', () => {
  expect(isPublicEndpoint('/cars', 'POST')).toBe(false)
})
test('DELETE /cars/abc123 删除车源不应该是公开接口', () => {
  expect(isPublicEndpoint('/cars/abc123', 'DELETE')).toBe(false)
})

test('GET /cars/abc/images/xyz/download 图片下载应该是公开接口', () => {
  expect(isPublicEndpoint('/cars/abc/images/xyz/download', 'GET')).toBe(true)
})

test('GET /coupons 可用优惠券应该是公开接口', () => {
  expect(isPublicEndpoint('/coupons', 'GET')).toBe(true)
})

test('GET /membership/plans 会员套餐应该是公开接口', () => {
  expect(isPublicEndpoint('/membership/plans', 'GET')).toBe(true)
})

test('POST /ai/chat AI 对话应该是公开接口', () => {
  expect(isPublicEndpoint('/ai/chat', 'POST')).toBe(true)
})
test('POST /ai/search AI 搜索应该是公开接口', () => {
  expect(isPublicEndpoint('/ai/search', 'POST')).toBe(true)
})
test('POST /ai/distribute AI 分发车源不应该是公开接口（需商家）', () => {
  expect(isPublicEndpoint('/ai/distribute', 'POST')).toBe(false)
})

test('GET /follows/abc/check 查询关注状态应该是公开接口', () => {
  expect(isPublicEndpoint('/follows/abc/check', 'GET')).toBe(true)
})

test('GET /orders 订单列表不应该是公开接口', () => {
  expect(isPublicEndpoint('/orders', 'GET')).toBe(false)
})
test('GET /users/me 用户信息不应该是公开接口', () => {
  expect(isPublicEndpoint('/users/me', 'GET')).toBe(false)
})
test('GET /chat/conversations 聊天会话不应该是公开接口', () => {
  expect(isPublicEndpoint('/chat/conversations', 'GET')).toBe(false)
})

test('完整 URL 也应该正确识别（带 baseURL）', () => {
  expect(isPublicEndpoint('http://localhost:8080/api/v1/cars', 'GET')).toBe(true)
})
test('完整 URL 带 /api/v1 前缀应该能剥离', () => {
  expect(isPublicEndpoint('http://localhost:8080/api/v1/orders', 'GET')).toBe(false)
})

test('HTTP 方法应该不区分大小写', () => {
  expect(isPublicEndpoint('/cars', 'get')).toBe(true)
  expect(isPublicEndpoint('/cars', 'post')).toBe(false)
})

// ---------- 测试用例 2：商家认证接口匹配 ----------
section('requiresCertification() - 商家认证接口匹配')

test('POST /cars 发布车源需要商家认证', () => {
  expect(requiresCertification('/cars', 'POST')).toBe(true)
})
test('GET /shop/members 车行管理需要商家认证', () => {
  expect(requiresCertification('/shop/members', 'GET')).toBe(true)
})
test('POST /shop/members/invite 邀请成员需要商家认证', () => {
  expect(requiresCertification('/shop/members/invite', 'POST')).toBe(true)
})
test('POST /ai/distribute 分发车源需要商家认证', () => {
  expect(requiresCertification('/ai/distribute', 'POST')).toBe(true)
})
test('POST /ai/auto-outreach 自动触达需要商家认证', () => {
  expect(requiresCertification('/ai/auto-outreach', 'POST')).toBe(true)
})
test('GET /cars 车源浏览不需要商家认证', () => {
  expect(requiresCertification('/cars', 'GET')).toBe(false)
})
test('POST /users/login 登录不需要商家认证', () => {
  expect(requiresCertification('/users/login', 'POST')).toBe(false)
})

// ---------- 测试用例 3：权限级别判定 ----------
section('getEndpointAuthLevel() - 权限级别判定')

test('登录接口应为 PUBLIC', () => {
  expect(getEndpointAuthLevel('/users/login', 'POST')).toBe(AUTH_LEVEL.PUBLIC)
})
test('车源列表应为 PUBLIC', () => {
  expect(getEndpointAuthLevel('/cars', 'GET')).toBe(AUTH_LEVEL.PUBLIC)
})
test('车源详情应为 PUBLIC', () => {
  expect(getEndpointAuthLevel('/cars/abc', 'GET')).toBe(AUTH_LEVEL.PUBLIC)
})
test('发布车源应为 CERTIFIED', () => {
  expect(getEndpointAuthLevel('/cars', 'POST')).toBe(AUTH_LEVEL.CERTIFIED)
})
test('订单列表应为 PROTECTED', () => {
  expect(getEndpointAuthLevel('/orders', 'GET')).toBe(AUTH_LEVEL.PROTECTED)
})
test('我的车源应为 PROTECTED', () => {
  expect(getEndpointAuthLevel('/users/me', 'GET')).toBe(AUTH_LEVEL.PROTECTED)
})
test('车行管理应为 CERTIFIED', () => {
  expect(getEndpointAuthLevel('/shop/members', 'POST')).toBe(AUTH_LEVEL.CERTIFIED)
})

// ---------- 测试用例 4：登录状态判定 ----------
section('isAuthed() - 登录状态判定')

test('未设置 token 时应该返回 false', () => {
  global.uni.clearStorageSync()
  expect(isAuthed()).toBe(false)
})
test('设置 token 后应该返回 true', () => {
  // token 长度需 >= 20（与 readToken 中的 TOKEN_MIN_LENGTH 一致，模拟真实 JWT）
  global.uni.setStorageSync('token', 'fake-jwt-token-string-with-enough-length-1234567890')
  expect(isAuthed()).toBe(true)
})
test('清除 token 后应该返回 false', () => {
  global.uni.clearStorageSync()
  expect(isAuthed()).toBe(false)
})

// ---------- 测试用例 5：集成测试 - 模拟请求拦截器 ----------
section('集成测试 - 模拟请求拦截器行为')

function simulateRequestInterceptor(url, method, hasToken) {
  const isPublic = isPublicEndpoint(url, method)
  const authLevel = getEndpointAuthLevel(url, method)
  return {
    shouldBlock: !isPublic && !hasToken,
    reason: !isPublic && !hasToken ? '未登录，需要先登录' : (authLevel === AUTH_LEVEL.CERTIFIED && hasToken ? '需商家认证（由后端进一步校验）' : '通过'),
    shouldInjectToken: hasToken,
    authLevel
  }
}

test('【未登录】访问 GET /cars 车源列表应该放行', () => {
  const result = simulateRequestInterceptor('/cars', 'GET', false)
  expect(result.shouldBlock).toBe(false)
  expect(result.authLevel).toBe(AUTH_LEVEL.PUBLIC)
})
test('【未登录】访问 POST /users/login 登录应该放行', () => {
  const result = simulateRequestInterceptor('/users/login', 'POST', false)
  expect(result.shouldBlock).toBe(false)
})
test('【未登录】访问 GET /cars/abc 车源详情应该放行', () => {
  const result = simulateRequestInterceptor('/cars/abc', 'GET', false)
  expect(result.shouldBlock).toBe(false)
})

test('【未登录】访问 GET /orders 订单列表应该被拦截', () => {
  const result = simulateRequestInterceptor('/orders', 'GET', false)
  expect(result.shouldBlock).toBe(true)
})
test('【未登录】访问 GET /users/me 应该被拦截', () => {
  const result = simulateRequestInterceptor('/users/me', 'GET', false)
  expect(result.shouldBlock).toBe(true)
})
test('【未登录】访问 GET /chat/conversations 应该被拦截', () => {
  const result = simulateRequestInterceptor('/chat/conversations', 'GET', false)
  expect(result.shouldBlock).toBe(true)
})
test('【未登录】访问 POST /cars 发布车源应该被拦截', () => {
  const result = simulateRequestInterceptor('/cars', 'POST', false)
  expect(result.shouldBlock).toBe(true)
})

test('【已登录】访问 GET /orders 订单列表应该放行', () => {
  const result = simulateRequestInterceptor('/orders', 'GET', true)
  expect(result.shouldBlock).toBe(false)
})
test('【已登录】访问 GET /cars 车源列表应该放行', () => {
  const result = simulateRequestInterceptor('/cars', 'GET', true)
  expect(result.shouldBlock).toBe(false)
})
test('【已登录】访问 POST /cars 发布车源应该放行（后端再校验认证）', () => {
  const result = simulateRequestInterceptor('/cars', 'POST', true)
  expect(result.shouldBlock).toBe(false)
  expect(result.authLevel).toBe(AUTH_LEVEL.CERTIFIED)
})

// ---------- 测试用例 6：边界与异常情况 ----------
section('边界测试 - 异常 URL / 方法')

test('空 URL 不应匹配任何接口', () => {
  expect(isPublicEndpoint('', 'GET')).toBe(false)
})
test('null URL 不应匹配', () => {
  expect(isPublicEndpoint(null, 'GET')).toBe(false)
})
test('undefined method 应默认当作 GET', () => {
  expect(isPublicEndpoint('/cars')).toBe(true)
})
test('奇怪的路径 /../cars 不应绕过白名单', () => {
  expect(isPublicEndpoint('/../orders', 'GET')).toBe(false)
})

// ============================================================
// 汇总报告
// ============================================================
console.log('\n========================================')
console.log('  测试报告')
console.log('========================================')
console.log('  ✅ 通过: ' + passed)
console.log('  ❌ 失败: ' + failed)
console.log('  📊 总计: ' + (passed + failed))
console.log('========================================')

if (failed > 0) {
  console.log('\n  失败详情:')
  failures.forEach((f, i) => {
    console.log('  ' + (i + 1) + '. ' + f.name)
    console.log('     ' + f.error.message)
  })
  process.exit(1)
} else {
  console.log('\n  🎉 所有测试通过！')
  process.exit(0)
}
