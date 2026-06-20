/** Car Library API (Brands, Series, Models) */
import { ElMessage } from 'element-plus'
import { get, post, put, del } from '../utils/request'
import type { ApiResponse } from './types'

export interface CarBrand {
  id: number
  name: string
  logoUrl: string
  firstLetter: string
  status: string
  createdAt: string
}

export interface CarSeries {
  id: number
  brandId: number
  name: string
  status: string
  createdAt: string
}

export interface CarModel {
  id: number
  seriesId: number
  name: string
  year: number
  price: number
  status: string
  createdAt: string
}

export interface CarBrandDTO {
  name: string
  logoUrl?: string
}

export interface CarSeriesDTO {
  brandId: number
  name: string
}

export interface CarModelDTO {
  seriesId: number
  name: string
  year?: number
  price?: number
}

export function getBrands(status?: string) {
  return get<ApiResponse<CarBrand[]>>('/car-library/brands', { status })
}

export function createBrand(data: CarBrandDTO) {
  return post<ApiResponse<CarBrand>>('/car-library/brands', data)
}

export function updateBrand(id: number, data: CarBrandDTO) {
  return put<ApiResponse<CarBrand>>(`/car-library/brands/${id}`, data)
}

export function deleteBrand(id: number) {
  return del<ApiResponse<void>>(`/car-library/brands/${id}`)
}

export function getSeries(brandId?: number, status?: string) {
  return get<ApiResponse<CarSeries[]>>('/car-library/series', { brandId, status })
}

export function createSeries(data: CarSeriesDTO) {
  return post<ApiResponse<CarSeries>>('/car-library/series', data)
}

export function updateSeries(id: number, data: CarSeriesDTO) {
  return put<ApiResponse<CarSeries>>(`/car-library/series/${id}`, data)
}

export function deleteSeries(id: number) {
  return del<ApiResponse<void>>(`/car-library/series/${id}`)
}

export function getModels(seriesId?: number, status?: string) {
  return get<ApiResponse<CarModel[]>>('/car-library/models', { seriesId, status })
}

export function createModel(data: CarModelDTO) {
  return post<ApiResponse<CarModel>>('/car-library/models', data)
}

export function updateModel(id: number, data: CarModelDTO) {
  return put<ApiResponse<CarModel>>(`/car-library/models/${id}`, data)
}

export function deleteModel(id: number) {
  return del<ApiResponse<void>>(`/car-library/models/${id}`)
}

export interface CarModelImportResult {
  total: number
  created: number
  skipped: number
  errors: string[]
}

/** 批量导入车型（Excel） */
export async function importCarModels(file: File): Promise<CarModelImportResult> {
  const formData = new FormData()
  formData.append('file', file)
  const token = localStorage.getItem('token') || ''
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1/admin'
  const response = await fetch(`${baseUrl}/car-library/import`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: formData,
  })
  if (!response.ok) throw new Error('导入请求失败')
  const data = await response.json()
  if (data.code !== 200) throw new Error(data.message || '导入失败')
  return data.data
}

/** 下载车型导入模板 */
export function downloadImportTemplate() {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1/admin'
  const token = localStorage.getItem('token') || ''
  const url = `${baseUrl}/car-library/import-template`
  fetch(url, { headers: { Authorization: `Bearer ${token}` } })
    .then((res) => {
      if (!res.ok) throw new Error('下载失败')
      return res.blob()
    })
    .then((blob) => {
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = '车型导入模板.xlsx'
      link.click()
      URL.revokeObjectURL(link.href)
    })
    .catch(() => {
      ElMessage.error('模板下载失败，请稍后重试')
    })
}
