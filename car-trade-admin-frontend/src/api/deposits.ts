/** Deposits API */
import { get, post } from '../utils/request'
import type { ApiResponse, PageResult, PaginationQuery } from './types'

export interface DepositAccount {
  id: number
  userId: number
  userName: string
  userPhone: string
  userRole: string
  balance: number
  frozenAmount: number
  totalDeposit: number
  status: string
  createdAt: string
  updatedAt: string
}

export interface DepositRecord {
  id: number
  accountId: number
  userId: number
  userName: string
  type: string
  amount: number
  balanceAfter: number
  remark: string
  createdAt: string
}

export interface DepositSummary {
  totalBalance: number
  totalFrozen: number
  totalAccounts: number
  todayIncome: number
  todayWithdraw: number
}

export interface DepositQuery extends PaginationQuery {
  keyword?: string
  type?: string
}

export interface JournalEntryDTO {
  userId: number
  amount: number
  type: string
  remark: string
}

export function getDepositAccounts(params?: PaginationQuery) {
  return get<ApiResponse<PageResult<DepositAccount>>>('/deposits/accounts', params)
}

export function getDepositRecords(params?: DepositQuery) {
  return get<ApiResponse<PageResult<DepositRecord>>>('/deposits/records', params)
}

export function getDepositSummary() {
  return get<ApiResponse<DepositSummary>>('/deposits/summary')
}

export function createJournalEntry(data: JournalEntryDTO) {
  return post<ApiResponse<void>>('/deposits/records/manual', data)
}
