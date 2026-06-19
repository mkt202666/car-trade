import axios, {
  type AxiosError,
  type AxiosInstance,
  type InternalAxiosRequestConfig,
} from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse, RequestConfig } from './types'

const TOKEN_KEY = 'token'
const SUCCESS_CODE = 200
const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'
const TIMEOUT = Number(import.meta.env.VITE_API_TIMEOUT) || 30_000

interface RequestOptions {
  silent?: boolean
  skipAuth?: boolean
}

type InternalRequestConfig = InternalAxiosRequestConfig & RequestOptions

const service: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
})

function getToken(): string {
  return localStorage.getItem(TOKEN_KEY) || ''
}

function getErrorMessage(error: AxiosError<ApiResponse>): string {
  return (
    error.response?.data?.message ||
    error.message ||
    '网络异常，请稍后重试'
  )
}

service.interceptors.request.use(
  (config: InternalRequestConfig) => {
    if (!config.skipAuth) {
      const token = getToken()
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }
    return config
  },
  (error) => Promise.reject(error),
)

service.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse

    if (res.code === SUCCESS_CODE) {
      return res.data as never
    }

    const message = res.message || '请求失败'
    const config = response.config as InternalRequestConfig
    if (!config.silent) {
      ElMessage.error(message)
    }
    return Promise.reject(new Error(message))
  },
  (error: AxiosError<ApiResponse>) => {
    const config = error.config as InternalRequestConfig | undefined
    const status = error.response?.status

    if (status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('goodcar-admin-user')
      localStorage.removeItem('refreshToken')
      if (!window.location.pathname.startsWith('/login')) {
        import('../../router').then(({ default: router }) => {
          router.replace('/login')
        })
      }
    }

    if (!config?.silent) {
      ElMessage.error(getErrorMessage(error))
    }

    return Promise.reject(error)
  },
)

function mergeConfig(config?: RequestConfig): RequestConfig {
  return config ?? {}
}

export function request<T = unknown>(config: RequestConfig): Promise<T> {
  return service.request<unknown, T>(config)
}

export function get<T = unknown>(
  url: string,
  params?: Record<string, unknown>,
  config?: RequestConfig,
): Promise<T> {
  return request<T>({
    ...mergeConfig(config),
    method: 'GET',
    url,
    params,
  })
}

export function post<T = unknown>(
  url: string,
  data?: unknown,
  config?: RequestConfig,
): Promise<T> {
  return request<T>({
    ...mergeConfig(config),
    method: 'POST',
    url,
    data,
  })
}

export function put<T = unknown>(
  url: string,
  data?: unknown,
  config?: RequestConfig,
): Promise<T> {
  return request<T>({
    ...mergeConfig(config),
    method: 'PUT',
    url,
    data,
  })
}

export function del<T = unknown>(
  url: string,
  params?: Record<string, unknown>,
  config?: RequestConfig,
): Promise<T> {
  return request<T>({
    ...mergeConfig(config),
    method: 'DELETE',
    url,
    params,
  })
}

/** Trigger browser file download from API endpoint */
export async function downloadFile(url: string, filename: string) {
  const token = localStorage.getItem('token') || ''
  const response = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` },
  })
  if (!response.ok) throw new Error('导出失败')
  const blob = await response.blob()
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = filename
  link.click()
  URL.revokeObjectURL(link.href)
}

/** Upload a file and return the URL */
export async function uploadImage(file: File): Promise<string> {
  const formData = new FormData()
  formData.append('file', file)
  const token = localStorage.getItem('token') || ''
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  const response = await fetch(`${baseUrl}/uploads/image`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: formData,
  })
  if (!response.ok) throw new Error('上传失败')
  const data = await response.json()
  if (data.code !== 200) throw new Error(data.message || '上传失败')
  return data.data.url
}

export { service as axiosInstance }
export type { ApiResponse, PageResult, RequestConfig } from './types'
