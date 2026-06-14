import { useState, useEffect, useCallback, useRef } from 'react';
import { Search, ChevronDown, ChevronLeft, ChevronRight, X, Wallet, Snowflake, TrendingUp, Users, PlusCircle, Download } from 'lucide-react';
import { getDepositAccounts, getDepositRecords, manualDepositAdjust, getDepositSummary } from '../../api/deposit';
import { usePagination } from '../../hooks/usePagination';
import { SkeletonTable } from '../../components/Skeleton';
import { formatDate, maskPhone } from '../../utils/format';
import { exportFile } from '../../utils/export';

const RECORD_TYPE_OPTIONS = ['全部', 'CHARGE', 'WITHDRAW', 'FREEZE', 'UNFREEZE', 'REFUND', 'MANUAL'];

const RECORD_TYPE_MAP = {
  CHARGE: { label: '充值', color: 'bg-green-100 text-green-700' },
  WITHDRAW: { label: '提现', color: 'bg-red-100 text-red-700' },
  FREEZE: { label: '冻结', color: 'bg-yellow-100 text-yellow-700' },
  UNFREEZE: { label: '解冻', color: 'bg-green-100 text-green-700' },
  REFUND: { label: '退款', color: 'bg-red-100 text-red-700' },
  MANUAL: { label: '手动调整', color: 'bg-blue-100 text-blue-700' },
};

function getTypeBadge(type) {
  const t = RECORD_TYPE_MAP[type];
  if (!t) return <span className="inline-block px-2 py-0.5 rounded-full text-[11px] font-medium bg-gray-100 text-gray-600">{type || '-'}</span>;
  return <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${t.color}`}>{t.label}</span>;
}

function formatAmount(amount, type) {
  if (amount == null) return '-';
  const num = Number(amount);
  const formatted = `¥${Math.abs(num).toLocaleString()}`;
  if (type) {
    if (['CHARGE', 'UNFREEZE'].includes(type)) {
      return <span className="text-green-600 font-mono font-medium">+{formatted}</span>;
    }
    if (['WITHDRAW', 'FREEZE', 'REFUND'].includes(type)) {
      return <span className="text-red-600 font-mono font-medium">-{formatted}</span>;
    }
  }
  if (num > 0) return <span className="text-green-600 font-mono font-medium">+{formatted}</span>;
  if (num < 0) return <span className="text-red-600 font-mono font-medium">-{formatted}</span>;
  return <span className="text-gray-600 font-mono font-medium">{formatted}</span>;
}

function formatBalance(val) {
  if (val == null) return '-';
  return `¥${Number(val).toLocaleString()}`;
}

export default function DepositManage() {
  // Tab state
  const [activeTab, setActiveTab] = useState('accounts');

  // Summary stats
  const [summary, setSummary] = useState({});
  const [summaryLoading, setSummaryLoading] = useState(false);

  // Accounts state
  const [accountSearch, setAccountSearch] = useState('');
  const [debouncedAccountSearch, setDebouncedAccountSearch] = useState('');
  const [accounts, setAccounts] = useState([]);
  const [accountsLoading, setAccountsLoading] = useState(false);
  const [accountsError, setAccountsError] = useState(null);
  const [showAccountPageSizeDropdown, setShowAccountPageSizeDropdown] = useState(false);

  // Records state
  const [recordSearch, setRecordSearch] = useState('');
  const [debouncedRecordSearch, setDebouncedRecordSearch] = useState('');
  const [recordTypeFilter, setRecordTypeFilter] = useState('全部');
  const [showRecordTypeDropdown, setShowRecordTypeDropdown] = useState(false);
  const [records, setRecords] = useState([]);
  const [recordsLoading, setRecordsLoading] = useState(false);
  const [recordsError, setRecordsError] = useState(null);
  const [showRecordPageSizeDropdown, setShowRecordPageSizeDropdown] = useState(false);

  // Manual adjust modal
  const [showManualModal, setShowManualModal] = useState(false);
  const [manualType, setManualType] = useState('');
  const [manualUserId, setManualUserId] = useState('');
  const [manualAmount, setManualAmount] = useState('');
  const [manualNote, setManualNote] = useState('');
  const [manualSubmitting, setManualSubmitting] = useState(false);

  const accountPagination = usePagination(10);
  const { page: accPage, size: accSize, total: accTotal, totalPages: accTotalPages, setPage: setAccPage, setSize: setAccSize, setTotal: setAccTotal, reset: resetAcc } = accountPagination;

  const recordPagination = usePagination(10);
  const { page: recPage, size: recSize, total: recTotal, totalPages: recTotalPages, setPage: setRecPage, setSize: setRecSize, setTotal: setRecTotal, reset: resetRec } = recordPagination;

  // Debounce search
  const accountDebounceRef = useRef(null);
  useEffect(() => {
    if (accountDebounceRef.current) clearTimeout(accountDebounceRef.current);
    accountDebounceRef.current = setTimeout(() => setDebouncedAccountSearch(accountSearch), 300);
    return () => clearTimeout(accountDebounceRef.current);
  }, [accountSearch]);

  const recordDebounceRef = useRef(null);
  useEffect(() => {
    if (recordDebounceRef.current) clearTimeout(recordDebounceRef.current);
    recordDebounceRef.current = setTimeout(() => setDebouncedRecordSearch(recordSearch), 300);
    return () => clearTimeout(recordDebounceRef.current);
  }, [recordSearch]);

  // Reset pagination on filter change
  useEffect(() => { resetAcc(); }, [debouncedAccountSearch, resetAcc]);
  useEffect(() => { resetRec(); }, [debouncedRecordSearch, recordTypeFilter, resetRec]);

  // Fetch summary
  const fetchSummary = useCallback(() => {
    setSummaryLoading(true);
    getDepositSummary()
      .then(res => {
        const data = res.data ?? res;
        setSummary(data ?? {});
      })
      .catch(() => {})
      .finally(() => setSummaryLoading(false));
  }, []);

  useEffect(() => { fetchSummary(); }, [fetchSummary]);

  // Fetch accounts
  const fetchAccounts = useCallback(() => {
    setAccountsLoading(true);
    setAccountsError(null);
    const params = { page: accPage, size: accSize };
    if (debouncedAccountSearch) params.keyword = debouncedAccountSearch;

    getDepositAccounts(params)
      .then(res => {
        const data = res.data ?? res;
        setAccounts(data?.list ?? data?.records ?? []);
        setAccTotal(data?.total ?? 0);
      })
      .catch(err => {
        setAccountsError(err.response?.data?.message || err.message || '加载失败');
        setAccounts([]);
      })
      .finally(() => setAccountsLoading(false));
  }, [accPage, accSize, debouncedAccountSearch, setAccTotal]);

  useEffect(() => { fetchAccounts(); }, [fetchAccounts]);

  // Fetch records
  const fetchRecords = useCallback(() => {
    setRecordsLoading(true);
    setRecordsError(null);
    const params = { page: recPage, size: recSize };
    if (debouncedRecordSearch) params.keyword = debouncedRecordSearch;
    if (recordTypeFilter !== '全部') params.type = recordTypeFilter;

    getDepositRecords(params)
      .then(res => {
        const data = res.data ?? res;
        setRecords(data?.list ?? data?.records ?? []);
        setRecTotal(data?.total ?? 0);
      })
      .catch(err => {
        setRecordsError(err.response?.data?.message || err.message || '加载失败');
        setRecords([]);
      })
      .finally(() => setRecordsLoading(false));
  }, [recPage, recSize, debouncedRecordSearch, recordTypeFilter, setRecTotal]);

  useEffect(() => { fetchRecords(); }, [fetchRecords]);

  // Manual adjust
  const submitManualAdjust = async () => {
    if (!manualType || !manualUserId.trim() || !manualAmount) return;
    setManualSubmitting(true);
    try {
      await manualDepositAdjust({
        type: manualType,
        userId: manualUserId.trim(),
        amount: Number(manualAmount),
        note: manualNote.trim(),
      });
      setShowManualModal(false);
      setManualType('');
      setManualUserId('');
      setManualAmount('');
      setManualNote('');
      fetchAccounts();
      fetchRecords();
      fetchSummary();
    } catch (err) {
      alert('操作失败: ' + (err.response?.data?.message || err.message));
    } finally {
      setManualSubmitting(false);
    }
  };

  const openManualModal = () => {
    setManualType('');
    setManualUserId('');
    setManualAmount('');
    setManualNote('');
    setShowManualModal(true);
  };

  // Export records
  const handleExportRecords = useCallback(async () => {
    try {
      const params = new URLSearchParams()
      if (debouncedRecordSearch) params.append('keyword', debouncedRecordSearch)
      if (recordTypeFilter !== '全部') params.append('type', recordTypeFilter)
      
      await exportFile(`/deposits/records/export?${params.toString()}`, `保证金流水_${new Date().toLocaleDateString()}.xlsx`)
    } catch (error) {
      console.error('导出失败:', error)
      alert('导出失败: ' + error.message)
    }
  }, [debouncedRecordSearch, recordTypeFilter]);

  // Pagination helpers
  const getPageNumbers = (currentPage, totalPagesCount) => {
    if (totalPagesCount <= 7) return Array.from({ length: totalPagesCount }, (_, i) => i + 1);
    const pages = [];
    if (currentPage <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i);
      pages.push('...');
      pages.push(totalPagesCount);
    } else if (currentPage >= totalPagesCount - 3) {
      pages.push(1);
      pages.push('...');
      for (let i = totalPagesCount - 4; i <= totalPagesCount; i++) pages.push(i);
    } else {
      pages.push(1);
      pages.push('...');
      for (let i = currentPage - 1; i <= currentPage + 1; i++) pages.push(i);
      pages.push('...');
      pages.push(totalPagesCount);
    }
    return pages;
  };

  const pageSizeOptions = [10, 20, 50];

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-[18px] font-bold text-gray-900 tracking-tight">保证金现金流</h2>
          <p className="text-[12px] text-gray-500 mt-0.5">查看和管理平台保证金账户信息、流水记录及手动调整。</p>
        </div>
        <button
          onClick={openManualModal}
          className="flex items-center gap-1.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold px-4 py-2 rounded-xl transition-colors"
        >
          <PlusCircle className="w-3.5 h-3.5" />
          手动调整
        </button>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-400 font-medium">总余额</span>
            <Wallet className="w-4 h-4 text-blue-500" />
          </div>
          <div className="mt-2">
            <span className="text-xl font-bold font-mono text-gray-900">
              {summaryLoading ? '...' : formatBalance(summary.totalBalance)}
            </span>
          </div>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-400 font-medium">冻结总额</span>
            <Snowflake className="w-4 h-4 text-yellow-500" />
          </div>
          <div className="mt-2">
            <span className="text-xl font-bold font-mono text-gray-900">
              {summaryLoading ? '...' : formatBalance(summary.frozenBalance)}
            </span>
          </div>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-400 font-medium">累计充值</span>
            <TrendingUp className="w-4 h-4 text-green-500" />
          </div>
          <div className="mt-2">
            <span className="text-xl font-bold font-mono text-gray-900">
              {summaryLoading ? '...' : formatBalance(summary.totalRecharge)}
            </span>
          </div>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-400 font-medium">活跃账户数</span>
            <Users className="w-4 h-4 text-purple-500" />
          </div>
          <div className="mt-2 flex items-baseline gap-1">
            <span className="text-xl font-bold font-mono text-gray-900">
              {summaryLoading ? '...' : (summary.activeAccounts ?? 0)}
            </span>
            <span className="text-sm text-gray-400">个</span>
          </div>
        </div>
      </div>

      {/* Tab Switch */}
      <div className="flex gap-1 bg-gray-100 rounded-xl p-1 w-fit">
        <button
          onClick={() => setActiveTab('accounts')}
          className={`px-4 py-1.5 text-xs font-medium rounded-lg transition-colors ${
            activeTab === 'accounts' ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'
          }`}
        >
          账户列表
        </button>
        <button
          onClick={() => setActiveTab('records')}
          className={`px-4 py-1.5 text-xs font-medium rounded-lg transition-colors ${
            activeTab === 'records' ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'
          }`}
        >
          流水记录
        </button>
      </div>

      {/* Tab1: 账户列表 */}
      {activeTab === 'accounts' && (
        <>
          {/* Search */}
          <div className="flex items-center gap-2">
            <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs">
              <Search className="w-3.5 h-3.5 text-gray-400" />
              <input
                type="text"
                placeholder="搜索用户名/手机号"
                value={accountSearch}
                onChange={(e) => setAccountSearch(e.target.value)}
                className="bg-transparent outline-none text-gray-700 placeholder:text-gray-400 w-52"
              />
            </div>
          </div>

          {/* Accounts Table */}
          {accountsLoading ? (
            <SkeletonTable rows={5} cols={5} />
          ) : accountsError ? (
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center text-red-500 text-sm">{accountsError}</div>
          ) : (
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-xs">
                  <thead>
                    <tr className="bg-gray-50 border-b border-gray-100">
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">用户</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">余额</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">冻结金额</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">累计充值</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">状态</th>
                    </tr>
                  </thead>
                  <tbody>
                    {accounts.length === 0 ? (
                      <tr>
                        <td colSpan={5} className="px-4 py-8 text-center text-gray-400 text-sm">暂无账户数据</td>
                      </tr>
                    ) : (
                      accounts.map((a) => (
                        <tr key={a.id || a.userId} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                          <td className="px-4 py-3">
                            <div className="font-medium text-gray-900 text-sm">{a.nickname || a.userName || '-'}</div>
                            <div className="text-[11px] text-gray-400 mt-0.5 font-mono">{maskPhone(a.phone)}</div>
                          </td>
                          <td className="px-4 py-3 text-sm text-gray-800 font-mono font-medium">{formatBalance(a.balance)}</td>
                          <td className="px-4 py-3 text-sm text-gray-800 font-mono">{formatBalance(a.frozenAmount)}</td>
                          <td className="px-4 py-3 text-sm text-gray-800 font-mono">{formatBalance(a.totalRecharge)}</td>
                          <td className="px-4 py-3">
                            <span className={`inline-block px-2 py-0.5 rounded-full text-[11px] font-medium ${
                              a.status === 'ACTIVE' || a.status === 'NORMAL' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
                            }`}>
                              {a.status === 'ACTIVE' || a.status === 'NORMAL' ? '正常' : a.status || '-'}
                            </span>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              {accTotal > 0 && (
                <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
                  <div className="flex items-center gap-3 text-xs text-gray-500">
                    <span>每页行数:</span>
                    <div className="relative">
                      <button
                        onClick={() => setShowAccountPageSizeDropdown(!showAccountPageSizeDropdown)}
                        className="flex items-center gap-1 px-2 py-1 border border-gray-200 rounded-lg bg-white hover:bg-gray-50 transition-colors"
                      >
                        {accSize}
                        <ChevronDown className="w-3 h-3" />
                      </button>
                      {showAccountPageSizeDropdown && (
                        <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg py-1 z-10">
                          {pageSizeOptions.map((opt) => (
                            <button
                              key={opt}
                              onClick={() => { setAccSize(opt); setShowAccountPageSizeDropdown(false); }}
                              className={`w-full text-left px-3 py-1 text-xs hover:bg-gray-50 transition-colors ${accSize === opt ? 'text-indigo-600 font-medium' : 'text-gray-600'}`}
                            >
                              {opt}
                            </button>
                          ))}
                        </div>
                      )}
                    </div>
                    <span className="text-gray-400">显示 {accTotal === 0 ? 0 : (accPage - 1) * accSize + 1}-{Math.min(accPage * accSize, accTotal)} 项，共 {accTotal} 项</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <button onClick={() => setAccPage(accPage - 1)} disabled={accPage <= 1} className={`p-1.5 rounded-lg transition-colors ${accPage <= 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'}`}>
                      <ChevronLeft className="w-3.5 h-3.5" />
                    </button>
                    {getPageNumbers(accPage, accTotalPages).map((p, i) =>
                      p === '...' ? (
                        <span key={`ae-${i}`} className="px-1.5 py-1 text-gray-400 text-xs">...</span>
                      ) : (
                        <button
                          key={p}
                          onClick={() => setAccPage(p)}
                          className={`px-2.5 py-1 rounded-lg text-xs font-medium transition-colors ${accPage === p ? 'bg-indigo-600 text-white' : 'text-gray-600 hover:bg-gray-100'}`}
                        >
                          {p}
                        </button>
                      )
                    )}
                    <button onClick={() => setAccPage(accPage + 1)} disabled={accPage >= accTotalPages} className={`p-1.5 rounded-lg transition-colors ${accPage >= accTotalPages ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'}`}>
                      <ChevronRight className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}
        </>
      )}

      {/* Tab2: 流水记录 */}
      {activeTab === 'records' && (
        <>
          {/* Search & Filter */}
          <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3">
            <div className="flex items-center gap-2">
              <div className="flex items-center gap-2 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs">
                <Search className="w-3.5 h-3.5 text-gray-400" />
                <input
                  type="text"
                  placeholder="搜索用户名/手机号"
                  value={recordSearch}
                  onChange={(e) => setRecordSearch(e.target.value)}
                  className="bg-transparent outline-none text-gray-700 placeholder:text-gray-400 w-52"
                />
              </div>
              <div className="relative">
                <button
                  onClick={() => setShowRecordTypeDropdown(!showRecordTypeDropdown)}
                  className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors"
                >
                  {recordTypeFilter === '全部' ? '全部类型' : RECORD_TYPE_MAP[recordTypeFilter]?.label || recordTypeFilter}
                  <ChevronDown className="w-3 h-3" />
                </button>
                {showRecordTypeDropdown && (
                  <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg py-1 z-10 min-w-[120px]">
                    {RECORD_TYPE_OPTIONS.map((opt) => (
                      <button
                        key={opt}
                        onClick={() => { setRecordTypeFilter(opt); setShowRecordTypeDropdown(false); }}
                        className={`w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50 transition-colors ${recordTypeFilter === opt ? 'text-indigo-600 font-medium bg-indigo-50' : 'text-gray-600'}`}
                      >
                        {opt === '全部' ? '全部' : RECORD_TYPE_MAP[opt]?.label || opt}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </div>
            <button
              onClick={handleExportRecords}
              className="flex items-center gap-1.5 bg-white border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-600 hover:bg-gray-50 transition-colors"
            >
              <Download size={14} />
              导出
            </button>
          </div>

          {/* Records Table */}
          {recordsLoading ? (
            <SkeletonTable rows={5} cols={6} />
          ) : recordsError ? (
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 text-center text-red-500 text-sm">{recordsError}</div>
          ) : (
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-xs">
                  <thead>
                    <tr className="bg-gray-50 border-b border-gray-100">
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">用户</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">类型</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">金额</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">变动后余额</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">备注</th>
                      <th className="text-left px-4 py-3 font-medium text-gray-500 uppercase tracking-wider text-xs">时间</th>
                    </tr>
                  </thead>
                  <tbody>
                    {records.length === 0 ? (
                      <tr>
                        <td colSpan={6} className="px-4 py-8 text-center text-gray-400 text-sm">暂无流水记录</td>
                      </tr>
                    ) : (
                      records.map((r, idx) => (
                        <tr key={r.id || idx} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                          <td className="px-4 py-3">
                            <div className="font-medium text-gray-900 text-sm">{r.nickname || r.userName || '-'}</div>
                            <div className="text-[11px] text-gray-400 mt-0.5 font-mono">{maskPhone(r.phone)}</div>
                          </td>
                          <td className="px-4 py-3">{getTypeBadge(r.type)}</td>
                          <td className="px-4 py-3 text-sm">{formatAmount(r.amount, r.type)}</td>
                          <td className="px-4 py-3 text-sm text-gray-800 font-mono">{formatBalance(r.balanceAfter)}</td>
                          <td className="px-4 py-3 text-sm text-gray-600 max-w-[200px] truncate">{r.note || r.remark || '-'}</td>
                          <td className="px-4 py-3 text-sm text-gray-400 font-mono">{formatDate(r.createTime || r.createdAt)}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              {recTotal > 0 && (
                <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
                  <div className="flex items-center gap-3 text-xs text-gray-500">
                    <span>每页行数:</span>
                    <div className="relative">
                      <button
                        onClick={() => setShowRecordPageSizeDropdown(!showRecordPageSizeDropdown)}
                        className="flex items-center gap-1 px-2 py-1 border border-gray-200 rounded-lg bg-white hover:bg-gray-50 transition-colors"
                      >
                        {recSize}
                        <ChevronDown className="w-3 h-3" />
                      </button>
                      {showRecordPageSizeDropdown && (
                        <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg py-1 z-10">
                          {pageSizeOptions.map((opt) => (
                            <button
                              key={opt}
                              onClick={() => { setRecSize(opt); setShowRecordPageSizeDropdown(false); }}
                              className={`w-full text-left px-3 py-1 text-xs hover:bg-gray-50 transition-colors ${recSize === opt ? 'text-indigo-600 font-medium' : 'text-gray-600'}`}
                            >
                              {opt}
                            </button>
                          ))}
                        </div>
                      )}
                    </div>
                    <span className="text-gray-400">显示 {recTotal === 0 ? 0 : (recPage - 1) * recSize + 1}-{Math.min(recPage * recSize, recTotal)} 项，共 {recTotal} 项</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <button onClick={() => setRecPage(recPage - 1)} disabled={recPage <= 1} className={`p-1.5 rounded-lg transition-colors ${recPage <= 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'}`}>
                      <ChevronLeft className="w-3.5 h-3.5" />
                    </button>
                    {getPageNumbers(recPage, recTotalPages).map((p, i) =>
                      p === '...' ? (
                        <span key={`re-${i}`} className="px-1.5 py-1 text-gray-400 text-xs">...</span>
                      ) : (
                        <button
                          key={p}
                          onClick={() => setRecPage(p)}
                          className={`px-2.5 py-1 rounded-lg text-xs font-medium transition-colors ${recPage === p ? 'bg-indigo-600 text-white' : 'text-gray-600 hover:bg-gray-100'}`}
                        >
                          {p}
                        </button>
                      )
                    )}
                    <button onClick={() => setRecPage(recPage + 1)} disabled={recPage >= recTotalPages} className={`p-1.5 rounded-lg transition-colors ${recPage >= recTotalPages ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'}`}>
                      <ChevronRight className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}
        </>
      )}

      {/* Manual Adjust Modal */}
      {showManualModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setShowManualModal(false)}>
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">手动调整保证金</h3>
              <button onClick={() => setShowManualModal(false)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                <X className="w-4 h-4 text-gray-400" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              {/* Type select */}
              <div>
                <label className="text-xs font-medium text-gray-500 block mb-2">调整类型</label>
                <div className="grid grid-cols-2 gap-2">
                  {[
                    { value: 'CHARGE', label: '充值' },
                    { value: 'WITHDRAW', label: '扣减' },
                    { value: 'FREEZE', label: '冻结' },
                    { value: 'UNFREEZE', label: '解冻' },
                  ].map((opt) => (
                    <button
                      key={opt.value}
                      onClick={() => setManualType(opt.value)}
                      className={`py-2 rounded-lg text-xs font-medium border transition-colors ${
                        manualType === opt.value
                          ? 'bg-indigo-50 border-indigo-300 text-indigo-700'
                          : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-gray-100'
                      }`}
                    >
                      {opt.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* User ID */}
              <div>
                <label className="text-xs font-medium text-gray-500 block mb-2">用户 ID</label>
                <input
                  type="text"
                  value={manualUserId}
                  onChange={(e) => setManualUserId(e.target.value)}
                  placeholder="请输入用户 ID"
                  className="w-full bg-gray-50 border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-700 placeholder:text-gray-400 outline-none focus:border-indigo-500"
                />
              </div>

              {/* Amount */}
              <div>
                <label className="text-xs font-medium text-gray-500 block mb-2">金额</label>
                <input
                  type="number"
                  value={manualAmount}
                  onChange={(e) => setManualAmount(e.target.value)}
                  placeholder="请输入金额"
                  className="w-full bg-gray-50 border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-700 placeholder:text-gray-400 outline-none focus:border-indigo-500"
                />
              </div>

              {/* Note */}
              <div>
                <label className="text-xs font-medium text-gray-500 block mb-2">备注</label>
                <textarea
                  value={manualNote}
                  onChange={(e) => setManualNote(e.target.value)}
                  placeholder="请输入备注信息..."
                  rows={3}
                  className="w-full bg-gray-50 border border-gray-200 rounded-xl px-3 py-2 text-xs text-gray-700 placeholder:text-gray-400 outline-none focus:border-indigo-500 resize-none"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2 p-5 border-t border-gray-100">
              <button onClick={() => setShowManualModal(false)} className="px-4 py-2 text-xs text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
                取消
              </button>
              <button
                onClick={submitManualAdjust}
                disabled={!manualType || !manualUserId.trim() || !manualAmount || manualSubmitting}
                className={`px-4 py-2 text-xs text-white rounded-lg transition-colors ${
                  !manualType || !manualUserId.trim() || !manualAmount || manualSubmitting
                    ? 'bg-indigo-400 cursor-not-allowed'
                    : 'bg-indigo-600 hover:bg-indigo-700'
                }`}
              >
                {manualSubmitting ? '提交中...' : '确认'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
