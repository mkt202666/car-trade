/** Notifications API */
import { get, post } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface Notification {
  id: number
  type: string
  targetUserId: number
  targetUserName: string
  title: string
  content: string
  targetType: string
  targetId: string
  status: string  // SENT / FAILED
  createdAt: string
}

export interface NotificationStats {
  totalSent: number
  totalFailed: number
}

export interface AnnouncementDTO {
  title: string
  content: string
}

export interface NotificationQuery extends PaginationQuery {
  type?: string
  status?: string
  keyword?: string
}

export function getNotifications(params?: NotificationQuery) {
  return get<ApiResponse<PageResult<Notification>>>('/notifications', params)
}

export function getNotificationStats() {
  return get<ApiResponse<NotificationStats>>('/notifications/stats')
}

export function sendAnnouncement(data: AnnouncementDTO) {
  return post<ApiResponse<{ sentCount: number }>>('/notifications/announcement', data)
}
