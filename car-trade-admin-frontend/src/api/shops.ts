/** Shops (Dealers) API */
import { get, post, put } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface Shop {
  id: number
  shopName: string
  realName: string
  phone: string
  nickname: string
  avatarUrl: string
  creditGrade: string
  creditScore: number
  dealCount: number
  onSaleCount: number
  memberCount: number
  status: string
  certificationStatus: string
  createdAt: string
  province: string
  city: string
  address: string
  creditCode: string
  depositBalance: number
  licenseUrl: string
}

export interface ShopQuery extends PaginationQuery {
  keyword?: string
  status?: string
  province?: string
}

export interface ShopCreateDTO {
  shopName: string
  contactName?: string
  phone: string
  creditCode?: string
  province?: string
  city?: string
  address?: string
  licenseUrl?: string
  idCardNumber?: string
  idCardImageUrl?: string
  storeImageUrl?: string
}

export function getShops(params?: ShopQuery) {
  return get<ApiResponse<PageResult<Shop>>>('/shops', params)
}

export function getShop(id: number) {
  return get<ApiResponse<Shop>>(`/shops/${id}`)
}

export function createShop(data: ShopCreateDTO) {
  return post<ApiResponse<Shop>>('/shops', data)
}

export function updateShopStatus(id: number, status: string) {
  return put<ApiResponse<void>>(`/shops/${id}/status`, { status })
}
