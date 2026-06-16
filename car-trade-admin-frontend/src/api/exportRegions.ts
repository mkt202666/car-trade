import { get, post, put, del } from '../utils/request'
import type { ApiResponse } from './types'

export interface ExportRegionDTO {
  code: string
  name: string
  flag?: string
  group: string
  groupKey: string
  icon?: string
  constraints?: string
  requirements?: string
  status?: string
}

export interface ExportRegionVO {
  id: number
  code: string
  name: string
  flag: string
  group: string
  groupKey: string
  icon: string
  constraints: string
  requirements: string
  status: string
  createdAt: string
  updatedAt: string
}

export function getExportRegions() {
  return get<ApiResponse<ExportRegionVO[]>>('/export-regions')
}

export function createExportRegion(data: ExportRegionDTO) {
  return post<ApiResponse<{ id: number }>>('/export-regions', data)
}

export function updateExportRegion(id: number, data: ExportRegionDTO) {
  return put<ApiResponse<void>>(`/export-regions/${id}`, data)
}

export function deleteExportRegion(id: number) {
  return del<ApiResponse<void>>(`/export-regions/${id}`)
}
