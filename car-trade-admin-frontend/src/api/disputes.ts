/** Disputes API */
import { get, put } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface Dispute {
  id: number
  orderId: string
  orderTitle: string
  initiatorName: string
  initiatorPhone: string
  reason: string
  status: string  // OPEN / IN_PROGRESS / RESOLVED / REJECTED
  createdAt: string
}

export interface DisputeQuery extends PaginationQuery {
  status?: string
  keyword?: string
}

export interface DisputeHandleDTO {
  action: 'APPROVE' | 'REJECT' | 'NEGOTIATE'
  result: string
}

export function getDisputes(params?: DisputeQuery) {
  return get<ApiResponse<PageResult<Dispute>>>('/disputes', params)
}

export function handleDispute(id: number, data: DisputeHandleDTO) {
  return put<ApiResponse<void>>(`/disputes/${id}/handle`, data)
}

export function getPendingDisputeCount() {
  return get<ApiResponse<{ count: number }>>('/disputes/pending-count')
}
