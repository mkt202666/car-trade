import request from './request';

export const depositApi = {
  accounts: (params) => request.get('/deposits/accounts', { params }),
  records: (params) => request.get('/deposits/records', { params }),
  manual: (data) => request.post('/deposits/records/manual', data),
  summary: () => request.get('/deposits/summary'),
  warnings: (params) => request.get('/deposits/warnings', { params }),
};

// 兼容旧版独立函数导出
export function getDepositAccounts(params) { return depositApi.accounts(params); }
export function getDepositRecords(params) { return depositApi.records(params); }
export function manualDepositAdjust(data) { return depositApi.manual(data); }
export function getDepositSummary() { return depositApi.summary(); }
