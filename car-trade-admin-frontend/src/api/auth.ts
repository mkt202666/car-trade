/** Auth API */
import { get, post } from '../utils/request'
import type { ApiResponse } from './types'

export interface AdminUser {
  id: number
  username: string
  nickname: string
  email: string
  role: string
  permissions: string[]
  status: string
}

export interface LoginDTO {
  username: string
  password: string
}

export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresIn: number
  admin: AdminUser
}

export function login(data: LoginDTO) {
  return post<ApiResponse<LoginResult>>('/auth/login', data, { skipAuth: true })
}

export function getCurrentUser() {
  return get<ApiResponse<AdminUser>>('/auth/me')
}

export function refreshToken(refreshToken: string) {
  return post<ApiResponse<LoginResult>>('/auth/refresh', { refreshToken }, { skipAuth: true })
}
