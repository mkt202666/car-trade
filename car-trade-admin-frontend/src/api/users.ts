/** Users API */
import { get, post, put, del } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface User {
  id: number
  phone: string
  nickname: string
  realName: string
  avatarUrl: string
  shopName: string
  shopLogo: string
  shopDescription: string
  creditGrade: string
  creditScore: number
  dealCount: number
  onSaleCount: number
  userRole: string
  certificationStatus: string
  status: string
  createdAt: string
  updatedAt: string
  memberExpireAt: string
  // Profile fields
  creditCode: string
  province: string
  city: string
  idCardNumber: string
  businessLicenseUrl: string
  idCardFrontUrl: string
  idCardBackUrl: string
}

export interface UserQuery extends PaginationQuery {
  keyword?: string
  userType?: string
  status?: string
  certificationStatus?: string
}

export interface UserCreateDTO {
  phone: string
  nickname?: string
  realName?: string
  userRole?: string
  password?: string
  shopName?: string
}

export function getUsers(params?: UserQuery) {
  return get<ApiResponse<PageResult<User>>>('/users', params)
}

export function getUser(id: number) {
  return get<ApiResponse<User>>(`/users/${id}`)
}

export function updateUserStatus(id: number, status: string) {
  return put<ApiResponse<void>>(`/users/${id}/status`, { status })
}

export function deleteUser(id: number) {
  return del<ApiResponse<void>>(`/users/${id}`)
}

export interface UserProfileUpdateDTO {
  nickname?: string
  realName?: string
  avatarUrl?: string
  shopName?: string
  shopLogo?: string
  shopDescription?: string
  creditCode?: string
  province?: string
  city?: string
  idCardNumber?: string
  businessLicenseUrl?: string
  idCardFrontUrl?: string
  idCardBackUrl?: string
}

/** 编辑用户资料 */
export function updateUserProfile(id: number, data: UserProfileUpdateDTO) {
  return put<ApiResponse<void>>(`/users/${id}/profile`, data)
}

/** 创建用户 */
export function createUser(data: UserCreateDTO) {
  return post<ApiResponse<{ id: number }>>('/users', data)
}
