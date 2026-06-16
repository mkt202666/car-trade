/** Resources API (Banners, Popups, Configs) */
import { get, post, put, del } from '../utils/request'
import type { ApiResponse } from './types'

export interface Banner {
  id: number
  title: string
  imageUrl: string
  type: string
  linkUrl: string
  sortOrder: number
  status: string
  clickCount: number
  startAt: string
  endAt: string
  createdAt: string
}

export interface Config {
  key: string
  content: string
  updatedAt: string
}

export interface BannerCreateDTO {
  title: string
  imageUrl: string
  linkUrl?: string
  sortOrder?: number
}

export interface BannerUpdateDTO {
  title?: string
  imageUrl?: string
  linkUrl?: string
  sortOrder?: number
  status?: string
}

export interface ConfigUpdateDTO {
  content: string
}

export function getBanners(status?: string) {
  return get<ApiResponse<Banner[]>>('/resources/banners', { status })
}

export function createBanner(data: BannerCreateDTO) {
  return post<ApiResponse<Banner>>('/resources/banners', data)
}

export function updateBanner(id: number, data: BannerUpdateDTO) {
  return put<ApiResponse<Banner>>(`/resources/banners/${id}`, data)
}

export function deleteBanner(id: number) {
  return del<ApiResponse<void>>(`/resources/banners/${id}`)
}

export function updateBannerStatus(id: number, status: string) {
  return put<ApiResponse<void>>(`/resources/banners/${id}/status`, { status })
}

export function getPopups(status?: string) {
  return get<ApiResponse<Banner[]>>('/resources/popups', { status })
}

export function createPopup(data: BannerCreateDTO) {
  return post<ApiResponse<Banner>>('/resources/popups', data)
}

export function updatePopup(id: number, data: BannerUpdateDTO) {
  return put<ApiResponse<Banner>>(`/resources/popups/${id}`, data)
}

export function deletePopup(id: number) {
  return del<ApiResponse<void>>(`/resources/popups/${id}`)
}

export function updatePopupStatus(id: number, status: string) {
  return put<ApiResponse<void>>(`/resources/popups/${id}/status`, { status })
}

export function getConfig(key: string) {
  return get<ApiResponse<Config>>(`/resources/configs/${key}`, undefined, { silent: true })
}

export function updateConfig(key: string, data: ConfigUpdateDTO) {
  return put<ApiResponse<Config>>(`/resources/configs/${key}`, data)
}

export interface BannerSortItem {
  id: number
  sortOrder: number
}

export function sortBanners(items: BannerSortItem[]) {
  return put<void>('/resources/banners/sort', items)
}

export function sortPopups(items: BannerSortItem[]) {
  return put<void>('/resources/popups/sort', items)
}
