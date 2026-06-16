/** Shop Reviews (Dealer Audits) API */
import { get, put } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface ShopReview {
  id: number
  shopId: number
  shopName: string
  applicantName: string
  applicantPhone: string
  businessLicense: string
  idCardFront: string
  idCardBack: string
  status: string
  rejectReason: string
  reviewerId: number
  reviewerName: string
  reviewedAt: string
  createdAt: string
}

export interface ShopReviewQuery extends PaginationQuery {
  status?: string
  keyword?: string
}

export function getShopReviews(params?: ShopReviewQuery) {
  return get<ApiResponse<PageResult<ShopReview>>>('/shop-reviews', params)
}

export function getShopReview(id: number) {
  return get<ApiResponse<ShopReview>>(`/shop-reviews/${id}`)
}

export function approveShopReview(id: number) {
  return put<ApiResponse<void>>(`/shop-reviews/${id}/approve`)
}

export function rejectShopReview(id: number, reason: string) {
  return put<ApiResponse<void>>(`/shop-reviews/${id}/reject`, { reason })
}
