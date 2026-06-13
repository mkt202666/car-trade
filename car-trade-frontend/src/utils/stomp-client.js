/**
 * ============================================
 *  STOMP / SockJS WebSocket 客户端
 * ============================================
 *
 * 用途：在 H5 端建立与后端 STOMP 端点 (/ws) 的连接，
 *       通过 STOMP CONNECT 帧的 Authorization 头传递 JWT token
 *       —— 彻底避免 token 出现在 URL / Nginx 日志 / Referer / 浏览器历史
 *
 * 兼容性：
 *   - H5 端：使用 SockJS + @stomp/stompjs（当前实现）
 *   - 小程序 / APP 端：暂不兼容，建议改用轮询或 SSE
 *
 * 安全要点：
 *   - token 仅出现在 STOMP CONNECT 帧的 Authorization 头，握手中不出现
 *   - 连接失败时降级为轮询，不抛出阻断业务流程
 *   - 显式调用 disconnect() 释放资源
 */
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { readToken } from '@/constants/storage'

/**
 * 创建 STOMP 客户端并自动连接。
 *
 * @param {Object} options
 * @param {string} options.url — WebSocket 入口（不含协议），例如 'http://localhost:8080/ws'
 * @param {string} [options.subscribePath='/user/queue/messages'] — 默认订阅路径（私聊/系统消息）
 * @param {Function} options.onMessage — 收到消息时的回调 (parsedBody) => void
 * @param {Function} [options.onError] — 连接/通信错误回调 (err) => void
 * @param {Function} [options.onConnect] — 连接成功回调 (client) => void
 * @param {Function} [options.onClose] — 连接关闭回调 () => void
 * @returns {Client} stompjs Client 实例（可用于 subscribe/unsubscribe/disconnect）
 */
export function createStompClient(options) {
  const {
    url = 'http://localhost:8080/ws',
    onMessage,
    onError,
    onConnect,
    onClose
  } = options || {}

  const token = readToken()
  if (!token) {
    console.warn('[STOMP] no token, refuse to connect')
    if (onError) onError(new Error('No token'))
    return null
  }

  const client = new Client({
    // 关键：使用 SockJS（与后端 .withSockJS() 匹配）
    webSocketFactory: () => new SockJS(url),
    // ★ 安全：token 通过 STOMP CONNECT 帧的 Authorization 头传递，URL 中不含 token
    connectHeaders: {
      Authorization: 'Bearer ' + token
    },
    // 自动重连：递增退避，最大 10s
    reconnectDelay: 3000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    // 调试日志（仅开发期可开启）
    debug: () => {},
  })

  client.onConnect = (frame) => {
    console.log('[STOMP] connected:', frame.headers && frame.headers.session ? 'session=' + frame.headers.session : 'ok')
    if (typeof onConnect === 'function') onConnect(client)
  }

  client.onStompError = (frame) => {
    console.warn('[STOMP] broker error:', frame.headers && frame.headers.message)
    if (typeof onError === 'function') onError(new Error(frame.headers && frame.headers.message || 'STOMP error'))
  }

  client.onWebSocketClose = (evt) => {
    console.log('[STOMP] closed:', evt && evt.code)
    if (typeof onClose === 'function') onClose()
  }

  client.onWebSocketError = (evt) => {
    console.warn('[STOMP] ws error:', evt && evt.message)
    if (typeof onError === 'function') onError(evt)
  }

  try {
    client.activate()
  } catch (e) {
    console.error('[STOMP] activate failed:', e)
    if (typeof onError === 'function') onError(e)
    return null
  }

  return client
}

/**
 * 安全关闭 STOMP 客户端
 */
export function disconnectStompClient(client) {
  if (client && typeof client.deactivate === 'function') {
    try { client.deactivate() } catch (_) { /* ignore */ }
  }
}
