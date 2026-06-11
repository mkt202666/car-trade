// AI 相关 API - 统一 60 秒超时（大模型响应较慢）
const AI_TIMEOUT = 60 * 1000

const post = (url, data) => uni.$u.http.post(url, data, { timeout: AI_TIMEOUT })

export const marketAnalysis = (data) => post('/ai/market-analysis', data)
export const aiSearch = (data) => post('/ai/search', data)
export const generateCopywriting = (data) => post('/ai/customer-generation', data)
export const autoOutreach = (data) => post('/ai/auto-outreach', data)
export const distributeCar = (data) => post('/ai/distribute', data)
export const aiChat = (data) => post('/ai/chat', data)
export const carAnalysis = (data) => post('/ai/car-analysis', data)
export const priceEstimate = (data) => post('/ai/price-estimate', data)
export const getMyCars = (params) => uni.$u.http.get('/cars', { params })
export const getMarketAnalysis = (data) => post('/ai/market-analysis', data)
export const getCompetitors = (params) => uni.$u.http.get('/ai/competitors', { params })
export const getSuggestions = (params) => uni.$u.http.get('/ai/suggestions', { params })
export const searchAllCars = (data) => post('/ai/search', data)

/**
 * AI 流式对话 - 使用 fetch + ReadableStream 读取 SSE
 *
 * 协议说明：
 *   - 请求: POST /api/v1/ai/chat/stream
 *   - 请求体: { message, history }
 *   - 响应: text/event-stream (SSE)
 *   - 事件:
 *       event: message + data: { content: "增量文本" }
 *       event: done    + data: { content: "完整文本" }
 *       event: error   + data: { message: "错误信息" }
 *
 * @param {Object} data
 * @param {{ onMessage?: (piece:string, fullText:string) => void, onDone?: (fullText:string) => void, onError?: (err:Error) => void }} callbacks
 * @returns {{ cancel: () => void }} 取消控制器
 */
export function aiChatStream(data, callbacks) {
  const token = uni.getStorageSync('token') || ''

  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'text/event-stream',
    'Cache-Control': 'no-cache'
  }
  if (token) {
    headers['Authorization'] = 'Bearer ' + token
  }

  // baseURL - 与 request.js 保持一致
  const baseURL = 'http://localhost:8080/api/v1'

  // ---------- 状态 ----------
  let fullContent = ''
  let cancelled = false
  let abortController = null
  let timeoutTimer = null

  // H5 环境使用 AbortController 支持真正中断
  if (typeof AbortController !== 'undefined') {
    abortController = new AbortController()
  }

  const { onMessage, onDone, onError } = callbacks

  // ---------- 超时控制（60 秒） ----------
  const STREAM_TIMEOUT_MS = 60 * 1000
  function clearTimeoutTimer() {
    if (timeoutTimer != null) {
      clearTimeout(timeoutTimer)
      timeoutTimer = null
    }
  }
  function startTimeoutTimer() {
    clearTimeoutTimer()
    timeoutTimer = setTimeout(() => {
      if (cancelled) return
      cancelled = true
      if (abortController) {
        try { abortController.abort(new Error('请求超时，请稍后重试')) } catch (_) { /* ignore */ }
      }
      if (onError && typeof onError === 'function') {
        onError(new Error('请求超时，请稍后重试'))
      }
    }, STREAM_TIMEOUT_MS)
  }

  // ---------- 节流: 每 40ms 最多触发一次 onMessage, 避免高频刷新导致掉帧 ----------
  let pendingPiece = ''
  let throttleTimer = null
  function flushMessage() {
    if (cancelled) return
    if (pendingPiece.length === 0) return
    const piece = pendingPiece
    pendingPiece = ''
    if (onMessage && typeof onMessage === 'function') {
      onMessage(piece, fullContent)
    }
  }
  function enqueueMessage(piece) {
    pendingPiece += piece
    if (throttleTimer == null) {
      throttleTimer = setTimeout(() => {
        throttleTimer = null
        flushMessage()
      }, 40)
    }
  }

  // ---------- 启动请求 ----------
  startTimeoutTimer()

  const fetchOptions = {
    method: 'POST',
    headers: headers,
    body: JSON.stringify(data)
  }
  if (abortController) {
    fetchOptions.signal = abortController.signal
  }

  fetch(baseURL + '/ai/chat/stream', fetchOptions)
    .then(async (response) => {
      if (!response.ok) {
        let detail = ''
        try { detail = await response.text() } catch (_) { /* ignore */ }
        throw new Error('HTTP ' + response.status + (detail ? ': ' + detail.slice(0, 200) : ''))
      }

      // H5: 走 ReadableStream
      if (response.body && typeof response.body.getReader === 'function') {
        const reader = response.body.getReader()
        const decoder = new TextDecoder('utf-8')
        let buffer = ''

        while (true) {
          const { value, done } = await reader.read()
          if (done) break
          if (cancelled) {
            try { reader.cancel() } catch (_) { /* ignore */ }
            break
          }

          buffer += decoder.decode(value, { stream: true })
          // SSE 以空行分隔事件(可能是 \n\n 或 \r\n\r\n)
          buffer = dispatchEvents(buffer, (eventName, payloadStr) => handleEvent(eventName, payloadStr, enqueueMessage, (text) => {
            flushMessage()
            clearTimeoutTimer()
            if (onDone && typeof onDone === 'function') onDone(text || fullContent)
          }, (msg) => {
            flushMessage()
            clearTimeoutTimer()
            if (onError && typeof onError === 'function') onError(new Error(msg))
          }))
        }

        // 读取结束，确保把 buffer 中残留的也处理掉
        if (!cancelled && buffer.trim().length > 0) {
          dispatchEvents(buffer + '\n\n', (eventName, payloadStr) => handleEvent(eventName, payloadStr, enqueueMessage, (text) => {
            clearTimeoutTimer()
            if (onDone && typeof onDone === 'function') onDone(text || fullContent)
          }, (msg) => {
            clearTimeoutTimer()
            if (onError && typeof onError === 'function') onError(new Error(msg))
          }))
        }

        // 读取结束时立即 flush 一次残余内容
        flushMessage()
        // 如果后端没有显式发送 done 事件，且已有内容，则补一次 onDone
        if (!cancelled && fullContent.length > 0) {
          clearTimeoutTimer()
          if (onDone && typeof onDone === 'function') onDone(fullContent)
        }
        return
      }

      // 小程序/其他平台: 回退到普通一次性响应
      const text = await response.text()
      clearTimeoutTimer()
      try {
        const json = JSON.parse(text)
        const content = (json.data && (json.data.content || json.data.result)) || json.content || json.message || text
        if (onMessage) onMessage(content, content)
        if (onDone) onDone(content)
      } catch (_) {
        if (onMessage) onMessage(text, text)
        if (onDone) onDone(text)
      }
    })
    .catch((err) => {
      if (cancelled) return
      clearTimeoutTimer()
      if (onError && typeof onError === 'function') {
        onError(err instanceof Error ? err : new Error(String(err)))
      }
    })

  /** 返回取消句柄 */
  return {
    cancel: () => {
      cancelled = true
      clearTimeoutTimer()
      if (abortController) {
        try { abortController.abort() } catch (_) { /* ignore */ }
      }
      if (throttleTimer != null) {
        clearTimeout(throttleTimer)
        throttleTimer = null
      }
    }
  }

  // ---------- 解析辅助函数 ----------

  /**
   * 从 buffer 中按 SSE 规则切分事件，返回未处理的尾部 buffer
   */
  function dispatchEvents(buffer, onEvent) {
    // 标准化换行: \r\n -> \n
    buffer = buffer.replace(/\r\n/g, '\n')
    // 查找空行分隔
    let idx
    while ((idx = buffer.indexOf('\n\n')) >= 0) {
      const chunk = buffer.substring(0, idx)
      buffer = buffer.substring(idx + 2)
      if (!chunk.trim()) continue

      // 解析一个事件块: 多行 field: value
      let eventName = 'message'
      let dataStr = ''
      const lines = chunk.split('\n')
      for (let i = 0; i < lines.length; i++) {
        const line = lines[i]
        if (!line) continue
        // 注释行以 ':' 开头
        if (line.charCodeAt(0) === 58) continue
        const colonIdx = line.indexOf(':')
        if (colonIdx < 0) continue
        const field = line.substring(0, colonIdx).trim()
        let value = line.substring(colonIdx + 1)
        // 值前面的单个空格应被去除 (SSE 规范)
        if (value.charCodeAt(0) === 32) value = value.substring(1)
        if (field === 'event') eventName = value.trim() || 'message'
        else if (field === 'data') dataStr += (dataStr ? '\n' : '') + value
      }
      if (!dataStr) continue
      onEvent(eventName, dataStr)
    }
    return buffer
  }

  /**
   * 处理单条 SSE 事件
   */
  function handleEvent(eventName, payloadStr, _enqueueMessage, _onDone, _onError) {
    try {
      const json = JSON.parse(payloadStr)
      if (eventName === 'message') {
        const piece = (json.content != null) ? String(json.content) : ''
        if (piece) {
          fullContent += piece
          _enqueueMessage(piece)
        }
      } else if (eventName === 'done') {
        const finalText = (json.content != null) ? String(json.content) : fullContent
        if (finalText && finalText.length > fullContent.length) {
          fullContent = finalText
        }
        _onDone(fullContent)
      } else if (eventName === 'error') {
        const msg = json.message || json.error || 'AI 服务异常'
        _onError(String(msg))
      }
    } catch (_e) {
      // JSON 解析失败，把整段作为纯文本增量下发
      fullContent += payloadStr
      _enqueueMessage(payloadStr)
    }
  }
}
