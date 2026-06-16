/** Purchase Requests API */
import { get, put } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface PurchaseRequest {
  id: number
  userId: number
  userName: string
  userPhone: string
  brandName: string
  seriesName: string
  modelName: string
  minPrice: number
  maxPrice: number
  yearMin: number
  yearMax: number
  cityName: string
  energyType: string
  status: string
  createdAt: string
}

export interface PurchaseRequestQuery extends PaginationQuery {
  keyword?: string
  status?: string
  brand?: string
}

export function getPurchaseRequests(params?: PurchaseRequestQuery) {
  return get<ApiResponse<PageResult<PurchaseRequest>>>('/purchase-requests', params)
}

export function getPurchaseRequest(id: number) {
  return get<ApiResponse<PurchaseRequest>>(`/purchase-requests/${id}`)
}

export function matchPurchaseRequest(id: number) {
  return put<ApiResponse<void>>(`/purchase-requests/${id}/match`)
}

/** 关闭求购需求 */
export function closePurchaseRequest(id: number, reason: string) {
  return put<ApiResponse<void>>(`/purchase-requests/${id}/close`, { reason })
}
