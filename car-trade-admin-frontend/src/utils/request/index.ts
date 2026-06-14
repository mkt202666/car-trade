import axios, {
  type AxiosError,
  type AxiosInstance,
  type InternalAxiosRequestConfig,
} from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse, RequestConfig } from './types'

const TOKEN_KEY = 'token'
const SUCCESS_CODE = 0
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

export { service as axiosInstance }
export type { ApiResponse, PageResult, RequestConfig } from './types'
