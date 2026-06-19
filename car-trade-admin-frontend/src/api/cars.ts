/** Cars (Vehicles) API */
import { get, put, del } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface Car {
  id: number
  title: string
  brandId: number
  brandName: string
  seriesId: number
  seriesName: string
  modelId: number
  modelName: string
  cityName: string
  energyType: string
  price: number
  mileage: number
  year: number
  sellerName: string
  sellerPhone: string
  status: string
  viewCount: number
  createdAt: string
}

export interface CarQuery extends PaginationQuery {
  keyword?: string
  brand?: string
  status?: string
  minPrice?: number
  maxPrice?: number
}

export function getCars(params?: CarQuery) {
  return get<ApiResponse<PageResult<Car>>>('/cars', params)
}

export function getCar(id: number) {
  return get<ApiResponse<Car>>(`/cars/${id}`)
}

export function updateCarStatus(id: number, status: string) {
  return put<ApiResponse<void>>(`/cars/${id}/status`, { status })
}

export function recommendCar(id: number, recommended: boolean) {
  return put<ApiResponse<void>>(`/cars/${id}/recommend`, { recommended })
}

export function deleteCar(id: number) {
  return del<ApiResponse<void>>(`/cars/${id}`)
}
