/**
 * 系统配置文本 API
 * 提供用户协议、隐私条款、交易规范、合同模板等配置文本的查询接口（公开，无需登录）
 * 支持的 key: trade-rules, user-agreement, privacy-policy, contract-template
 */
export const getConfig = (key) => uni.http.get(`/configs/${key}`)
