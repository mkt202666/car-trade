/** Shop Members API */
import { get, post, put, del } from '../utils/request'
import type { ApiResponse } from './types'

export interface ShopMember {
  id: number
  memberUserId: number
  nickname: string
  realName: string
  phone: string
  avatarUrl: string
  role: string
  status: string
  joinedAt: string
}

export interface ShopMemberAddDTO {
  phone: string
  nickname?: string
  role?: string
}

export interface ShopMemberRoleUpdateDTO {
  role: string
}

export function getShopMembers(shopId: number) {
  return get<ApiResponse<ShopMember[]>>(`/shops/${shopId}/members`)
}

export function addShopMember(shopId: number, data: ShopMemberAddDTO) {
  return post<ApiResponse<void>>(`/shops/${shopId}/members`, data)
}

export function updateShopMemberRole(shopId: number, memberId: number, data: ShopMemberRoleUpdateDTO) {
  return put<ApiResponse<void>>(`/shops/${shopId}/members/${memberId}/role`, data)
}

export function removeShopMember(shopId: number, memberId: number) {
  return del<ApiResponse<void>>(`/shops/${shopId}/members/${memberId}`)
}
