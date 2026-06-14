import type { AxiosRequestConfig } from 'axios'

/** 后端统一响应结构，可按实际接口调整字段名 */
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页列表常见结构 */
export interface PageResult<T> {
  list: T[]
  total: number
  page?: number
  pageSize?: number
}

export interface RequestConfig extends AxiosRequestConfig {
  /** 为 true 时不弹出错误提示 */
  silent?: boolean
  /** 为 true 时不携带 token */
  skipAuth?: boolean
}
