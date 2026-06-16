/** Orders API */
import { get, put } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface Order {
  id: string
  carTitle: string
  carId: number
  buyerName: string
  sellerName: string
  totalPrice: number
  depositAmount: number
  status: string
  createdAt: string
}

export interface OrderQuery extends PaginationQuery {
  keyword?: string
  status?: string
  startDate?: string
  endDate?: string
}

/** 后端 OrderDetailVO 对应类型 — 订单详情全景数据 */
export interface OrderDetailVO {
  id: string
  carId: number
  carTitle: string
  carName: string
  carImage: string
  totalPrice: number
  depositAmount: number
  status: string
  buyerId: number
  buyerName: string
  buyerPhone: string
  sellerId: number
  sellerName: string
  sellerPhone: string
  createdAt: string
  updatedAt: string
  brandName: string
  seriesName: string
  modelName: string
  year: number
  mileage: number
  vin: string
  color: string
  city: string
  registrationDate: string
  insuranceExpiry: string
  inspectionExpiry: string
  energyType: string
  overallCondition: string
  paint: string
  structure: string
  engine: string
  transmission: string
  transferCount: number
  mileageType: string
  materials: string
  contractNo: string
  contractStatus: string
  buyerDepositPaid: boolean
  sellerDepositPaid: boolean
  depositStatus: string
  cancelReason: string
  completedAt: string | null
  cancelledAt: string | null
}

/** 订单操作日志条目 */
export interface OrderLog {
  id?: number
  action: string
  operator: string
  remark?: string
  createdAt: string
}

export function getOrders(params?: OrderQuery) {
  return get<ApiResponse<PageResult<Order>>>('/orders', params)
}

export function getOrder(id: number) {
  return get<ApiResponse<Order>>(`/orders/${id}`)
}

/** 获取订单全景详情 */
export function getOrderDetail(id: string) {
  return get<OrderDetailVO>(`/orders/${id}`)
}

/** 获取订单操作日志 */
export function getOrderLogs(id: string) {
  return get<OrderLog[]>(`/orders/${id}/logs`)
}

/** 确认订单 */
export function confirmOrder(id: string) {
  return put<void>(`/orders/${id}/confirm`)
}

/** 取消订单（管理员 / 运营强制终止） */
export function cancelOrder(id: string, reason: string) {
  return put<void>(`/orders/${id}/force-cancel`, { reason })
}

/** 管理员退款 */
export function refundOrder(id: string, reason: string, refundAmount?: number) {
  return put<void>(`/orders/${id}/refund`, { reason, refundAmount })
}
