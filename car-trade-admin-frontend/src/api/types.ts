/** API 通用类型定义 */

export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
  timestamp: string
  traceId: string | null
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  size: number
}

export interface PageParams {
  page?: number
  size?: number
}

export interface PaginationQuery extends PageParams {
  [key: string]: unknown
}
