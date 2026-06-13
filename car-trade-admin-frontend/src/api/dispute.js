import request from './request';

export const disputeApi = {
  list: (params) => request.get('/disputes', { params }),
  detail: (id) => request.get(`/disputes/${id}`),
  handle: (id, data) => request.put(`/disputes/${id}/handle`, data),
  pendingCount: () => request.get('/disputes/pending-count'),
};

// 兼容旧版独立函数导出
export function getDisputeList(params) { return disputeApi.list(params); }
export function handleDispute(id, data) { return disputeApi.handle(id, data); }
export function getPendingDisputeCount() { return disputeApi.pendingCount(); }
