import { useState, useEffect, useCallback, useRef } from 'react';
import { Search, ChevronDown, ChevronLeft, ChevronRight, X, AlertCircle, Download } from 'lucide-react';
import { getDisputeList, handleDispute, getPendingDisputeCount } from '../../api/dispute';
import { usePagination } from '../../hooks/usePagination';
import { SkeletonTable } from '../../components/Skeleton';
import { formatDate } from '../../utils/format';

const STATUS_OPTIONS = ['全部', 'OPEN', 'IN_PROGRESS', 'RESOLVED', 'REJECTED'];

const STATUS_MAP = {
  OPEN: { label: '待处理', color: 'bg-red-500/20 text-red-400 border-red-500/30' },
  IN_PROGRESS: { label: '处理中', color: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30' },
  RESOLVED: { label: '已解决', color: 'bg-green-500/20 text-green-400 border-green-500/30' },
  REJECTED: { label: '已驳回', color: 'bg-gray-500/20 text-gray-400 border-gray-500/30' },
};

const HANDLE_OPTIONS = [
  { value: 'APPROVE', label: '通过', color: 'bg-green-600 hover:bg-green-700' },
  { value: 'REJECT', label: '驳回', color: 'bg-red-600 hover:bg-red-700' },
  { value: 'NEGOTIATE', label: '协商', color: 'bg-blue-600 hover:bg-blue-700' },
];

function getStatusBadge(status) {
  const s = STATUS_MAP[status];
  if (!s) return <span className="inline-block px-2 py-0.5 rounded-full text-[11px] font-medium bg-gray-700 text-gray-400 border border-gray-600">{status || '-'}</span>;
  return <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium border ${s.color}`}>{s.label}</span>;
}

function truncateId(id) {
  if (!id) return '-';
  return id.length > 10 ? id.slice(0, 10) + '...' : id;
}

function truncateText(text, maxLen = 50) {
  if (!text) return '-';
  return text.length > maxLen ? text.slice(0, maxLen) + '...' : text;
}

export default function DisputeManage() {
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('全部');
  const [showStatusDropdown, setShowStatusDropdown] = useState(false);
  const [showPageSizeDropdown, setShowPageSizeDropdown] = useState(false);
  const [disputes, setDisputes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pendingCount, setPendingCount] = useState(0);
  const [countLoading, setCountLoading] = useState(false);

  // Handle modal state
  const [handleTarget, setHandleTarget] = useState(null);
  const [handleType, setHandleType] = useState('');
  const [handleDesc, setHandleDesc] = useState('');
  const [handleSubmitting, setHandleSubmitting] = useState(false);

  const pagination = usePagination(10);
  const { page, size, total, totalPages, setPage, setSize, setTotal, reset } = pagination;

  const debounceRef = useRef(null);
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // Debounce search
  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => setDebouncedSearch(search), 300);
    return () => clearTimeout(debounceRef.current);
  }, [search]);

  // Reset page on filter change
  useEffect(() => { reset(); }, [debouncedSearch, statusFilter, reset]);

  // Load pending count every 30 seconds
  const fetchPendingCount = useCallback(() => {
    setCountLoading(true);
    getPendingDisputeCount()
      .then(res => {
        setPendingCount(res?.count ?? 0);
      })
      .catch(() => {})
      .finally(() => setCountLoading(false));
  }, []);

  useEffect(() => {
    fetchPendingCount();
    const timer = setInterval(fetchPendingCount, 30000);
    return () => clearInterval(timer);
  }, [fetchPendingCount]);

  // Load disputes
  const fetchDisputes = useCallback(() => {
    setLoading(true);
    setError(null);
    const params = { page, size };
    if (debouncedSearch) params.keyword = debouncedSearch;
    if (statusFilter !== '全部') params.status = statusFilter;

    getDisputeList(params)
      .then(res => {
        const data = res.data ?? res;
        setDisputes(data?.list ?? data?.records ?? []);
        setTotal(data?.total ?? 0);
      })
      .catch(err => {
        setError(err.response?.data?.message || err.message || '加载失败');
        setDisputes([]);
      })
      .finally(() => setLoading(false));
  }, [page, size, debouncedSearch, statusFilter, setTotal]);

  useEffect(() => { fetchDisputes(); }, [fetchDisputes]);

  // Handle dispute
  const openHandleModal = (dispute) => {
    setHandleTarget(dispute);
    setHandleType('');
    setHandleDesc('');
  };

  const submitHandle = async () => {
    if (!handleType) return;
    if (!handleDesc.trim()) return;
    setHandleSubmitting(true);
    try {
      await handleDispute(handleTarget.id, {
        action: handleType,
        description: handleDesc.trim(),
      });
      setHandleTarget(null);
      fetchDisputes();
      fetchPendingCount();
    } catch (err) {
      alert('处理失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setHandleSubmitting(false);
    }
  };

  // Export
  const handleExport = useCallback(() => {
    const params = new URLSearchParams()
    if (debouncedSearch) params.append('keyword', debouncedSearch)
    if (statusFilter !== '全部') params.append('status', statusFilter)
    window.open(`/api/v1/admin/disputes/export?${params.toString()}`, '_blank')
  }, [debouncedSearch, statusFilter]);

  // Pagination helpers
  const paginationStart = total === 0 ? 0 : (page - 1) * size + 1;
  const paginationEnd = Math.min(page * size, total);
  const pageSizeOptions = [10, 20, 50];

  const getPageNumbers = () => {
    if (totalPages <= 7) return Array.from({ length: totalPages }, (_, i) => i + 1);
    const pages = [];
    if (page <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i);
      pages.push('...');
      pages.push(totalPages);
    } else if (page >= totalPages - 3) {
      pages.push(1);
      pages.push('...');
      for (let i = totalPages - 4; i <= totalPages; i++) pages.push(i);
    } else {
      pages.push(1);
      pages.push('...');
      for (let i = page - 1; i <= page + 1; i++) pages.push(i);
      pages.push('...');
      pages.push(totalPages);
    }
    return pages;
  };

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h2 className="text-[18px] font-bold text-white tracking-tight">争议处理</h2>
        <p className="text-[12px] text-gray-400 mt-0.5">处理用户提交的交易争议，审核并做出处理决定。</p>
      </div>

      {/* Stats card */}
      <div className="bg-gradient-to-r from-yellow-500/20 to-amber-500/20 border border-yellow-500/30 rounded-2xl p-5">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-yellow-500/30 flex items-center justify-center">
            <AlertCircle className="w-5 h-5 text-yellow-400" />
          </div>
          <div>
            <div className="text-xs text-yellow-400/80 font-medium">待处理争议</div>
            <div className="text-2xl font-bold text-yellow-400 font-mono">
              {countLoading ? '...' : pendingCount}
            </div>
          </div>
        </div>
        <p className="text-[11px] text-yellow-400/50 mt-2">数据每 30 秒自动刷新</p>
      </div>

      {/* Search & Filter */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3">
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-2 bg-gray-800 border border-gray-700 rounded-xl px-3 py-2 text-xs">
            <Search className="w-3.5 h-3.5 text-gray-500" />
            <input
              type="text"
              placeholder="搜索订单号/申请人"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="bg-transparent outline-none text-gray-200 placeholder:text-gray-500 w-52"
            />
          </div>
          <div className="relative">
            <button
              onClick={() => setShowStatusDropdown(!showStatusDropdown)}
              className="flex items-center gap-1.5 bg-gray-800 border border-gray-700 rounded-xl px-3 py-2 text-xs text-gray-300 hover:bg-gray-700 transition-colors"
            >
              {statusFilter === '全部' ? '全部状态' : STATUS_MAP[statusFilter]?.label || statusFilter}
              <ChevronDown className="w-3 h-3" />
            </button>
            {showStatusDropdown && (
              <div className="absolute top-full left-0 mt-1 bg-gray-800 border border-gray-700 rounded-xl shadow-lg py-1 z-10 min-w-[120px]">
                {STATUS_OPTIONS.map((opt) => (
                  <button
                    key={opt}
                    onClick={() => { setStatusFilter(opt); setShowStatusDropdown(false); }}
                    className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-700 transition-colors ${statusFilter === opt ? 'text-indigo-400 font-medium bg-indigo-500/10' : 'text-gray-400'}`}
                  >
                    {opt === '全部' ? '全部' : STATUS_MAP[opt]?.label || opt}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
        <button
          onClick={handleExport}
          className="flex items-center gap-1.5 px-3 py-1.5 text-sm border border-gray-600 rounded-lg hover:bg-gray-700/50 text-gray-300"
        >
          <Download size={14} />
          导出
        </button>
      </div>

      {/* Table */}
      {loading ? (
        <SkeletonTable rows={5} cols={6} />
      ) : error ? (
        <div className="bg-gray-800 rounded-2xl border border-gray-700 p-8 text-center text-red-400 text-sm">{error}</div>
      ) : (
        <div className="bg-gray-800 rounded-2xl border border-gray-700 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-xs">
              <thead>
                <tr className="bg-gray-900 border-b border-gray-700">
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">订单号</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">申请人</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">原因</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">状态</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">创建时间</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-400 uppercase tracking-wider text-xs">操作</th>
                </tr>
              </thead>
              <tbody>
                {disputes.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="px-4 py-8 text-center text-gray-500 text-sm">暂无争议数据</td>
                  </tr>
                ) : (
                  disputes.map((d) => (
                    <tr key={d.id} className="border-b border-gray-700/50 hover:bg-gray-700/30 transition-colors">
                      <td className="px-4 py-3">
                        <span className="font-mono text-gray-300 text-sm" title={d.orderId || d.id}>{truncateId(d.orderId || d.id)}</span>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-300">{d.applicant || d.applicantName || '-'}</td>
                      <td className="px-4 py-3">
                        <span className="text-sm text-gray-400 max-w-[200px] inline-block truncate" title={d.reason || d.description}>
                          {truncateText(d.reason || d.description)}
                        </span>
                      </td>
                      <td className="px-4 py-3">{getStatusBadge(d.status)}</td>
                      <td className="px-4 py-3 text-sm text-gray-400 font-mono">{formatDate(d.createTime || d.createdAt)}</td>
                      <td className="px-4 py-3">
                        <button
                          onClick={() => openHandleModal(d)}
                          className="flex items-center gap-1 px-2 py-1 rounded-lg text-[11px] text-indigo-400 hover:bg-indigo-500/10 transition-colors"
                        >
                          处理
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {total > 0 && (
            <div className="flex items-center justify-between px-4 py-3 border-t border-gray-700">
              <div className="flex items-center gap-3 text-xs text-gray-400">
                <span>每页行数:</span>
                <div className="relative">
                  <button
                    onClick={() => setShowPageSizeDropdown(!showPageSizeDropdown)}
                    className="flex items-center gap-1 px-2 py-1 border border-gray-700 rounded-lg bg-gray-800 hover:bg-gray-700 transition-colors"
                  >
                    {size}
                    <ChevronDown className="w-3 h-3" />
                  </button>
                  {showPageSizeDropdown && (
                    <div className="absolute top-full left-0 mt-1 bg-gray-800 border border-gray-700 rounded-lg shadow-lg py-1 z-10">
                      {pageSizeOptions.map((opt) => (
                        <button
                          key={opt}
                          onClick={() => { setSize(opt); setShowPageSizeDropdown(false); }}
                          className={`w-full text-left px-3 py-1 text-xs hover:bg-gray-700 transition-colors ${size === opt ? 'text-indigo-400 font-medium' : 'text-gray-400'}`}
                        >
                          {opt}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
                <span className="text-gray-500">显示 {paginationStart}-{paginationEnd} 项，共 {total} 项</span>
              </div>
              <div className="flex items-center gap-1">
                <button
                  onClick={() => setPage(page - 1)}
                  disabled={page <= 1}
                  className={`p-1.5 rounded-lg transition-colors ${page <= 1 ? 'text-gray-600 cursor-not-allowed' : 'text-gray-400 hover:bg-gray-700'}`}
                >
                  <ChevronLeft className="w-3.5 h-3.5" />
                </button>
                {getPageNumbers().map((p, i) =>
                  p === '...' ? (
                    <span key={`ellipsis-${i}`} className="px-1.5 py-1 text-gray-500 text-xs">...</span>
                  ) : (
                    <button
                      key={p}
                      onClick={() => setPage(p)}
                      className={`px-2.5 py-1 rounded-lg text-xs font-medium transition-colors ${page === p ? 'bg-indigo-600 text-white' : 'text-gray-400 hover:bg-gray-700'}`}
                    >
                      {p}
                    </button>
                  )
                )}
                <button
                  onClick={() => setPage(page + 1)}
                  disabled={page >= totalPages}
                  className={`p-1.5 rounded-lg transition-colors ${page >= totalPages ? 'text-gray-600 cursor-not-allowed' : 'text-gray-400 hover:bg-gray-700'}`}
                >
                  <ChevronRight className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Handle Modal */}
      {handleTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60" onClick={() => setHandleTarget(null)}>
          <div className="bg-gray-800 rounded-2xl shadow-xl w-full max-w-md border border-gray-700" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-700">
              <h3 className="text-base font-bold text-white">处理争议</h3>
              <button onClick={() => setHandleTarget(null)} className="p-1.5 rounded-lg hover:bg-gray-700 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              {/* Info */}
              <div className="text-xs text-gray-400 space-y-1">
                <div>订单号: <span className="text-gray-300 font-mono">{handleTarget.orderId || handleTarget.id}</span></div>
                <div>申请人: <span className="text-gray-300">{handleTarget.applicant || handleTarget.applicantName || '-'}</span></div>
                <div>原因: <span className="text-gray-300">{handleTarget.reason || handleTarget.description || '-'}</span></div>
              </div>

              {/* Handle type buttons */}
              <div>
                <label className="text-xs font-medium text-gray-400 block mb-2">处理方式</label>
                <div className="flex gap-2">
                  {HANDLE_OPTIONS.map((opt) => (
                    <button
                      key={opt.value}
                      onClick={() => setHandleType(opt.value)}
                      className={`flex-1 py-2 rounded-lg text-xs font-medium text-white transition-colors ${
                        handleType === opt.value ? opt.color + ' ring-2 ring-white/20' : 'bg-gray-700 hover:bg-gray-600 text-gray-400'
                      }`}
                    >
                      {opt.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* Description */}
              {handleType && (
                <div>
                  <label className="text-xs font-medium text-gray-400 block mb-2">结果描述</label>
                  <textarea
                    value={handleDesc}
                    onChange={(e) => setHandleDesc(e.target.value)}
                    placeholder="请输入处理结果的详细描述..."
                    rows={3}
                    className="w-full bg-gray-900 border border-gray-700 rounded-xl px-3 py-2 text-xs text-gray-200 placeholder:text-gray-500 outline-none focus:border-indigo-500 resize-none"
                  />
                </div>
              )}
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-700">
              <button
                onClick={() => setHandleTarget(null)}
                className="px-4 py-2 text-xs text-gray-300 bg-gray-700 hover:bg-gray-600 rounded-lg transition-colors"
              >
                取消
              </button>
              <button
                onClick={submitHandle}
                disabled={!handleType || !handleDesc.trim() || handleSubmitting}
                className={`px-4 py-2 text-xs text-white rounded-lg transition-colors ${
                  !handleType || !handleDesc.trim() || handleSubmitting
                    ? 'bg-indigo-600/50 cursor-not-allowed'
                    : 'bg-indigo-600 hover:bg-indigo-700'
                }`}
              >
                {handleSubmitting ? '提交中...' : '确认处理'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
