import { lazy, Suspense } from 'react'
import { Routes, Route, Navigate, useLocation } from 'react-router-dom'
import { useContext, useEffect } from 'react'
import AdminLayout from './layouts/AdminLayout'
import { AuthProvider, AuthContext } from './store/AuthContext'
import LoadingBar from './components/LoadingBar'
import ErrorBoundary from './components/ErrorBoundary'

// 路由懒加载 — 每个页面独立chunk
const Login = lazy(() => import('./pages/login'))
const Dashboard = lazy(() => import('./pages/dashboard'))
const UserManage = lazy(() => import('./pages/user-manage'))
const ShopManage = lazy(() => import('./pages/shop-manage'))
const ShopAudit = lazy(() => import('./pages/shop-audit'))
const CarManage = lazy(() => import('./pages/car-manage'))
const PurchaseManage = lazy(() => import('./pages/purchase-manage'))
const TradeManage = lazy(() => import('./pages/trade-manage'))
const DisputeManage = lazy(() => import('./pages/dispute-manage'))
const DepositManage = lazy(() => import('./pages/deposit-manage'))
const ExportConfig = lazy(() => import('./pages/export-config'))
const CarLibrary = lazy(() => import('./pages/car-library'))
const ResourceManage = lazy(() => import('./pages/resource-manage'))

// 页面级加载指示器
function PageLoader() {
  return (
    <div className="flex items-center justify-center min-h-[400px]">
      <div className="flex flex-col items-center gap-3">
        <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
        <span className="text-sm text-gray-400">加载中...</span>
      </div>
    </div>
  )
}

// 带Suspense的懒加载包裹
function LazyPage({ children }) {
  return (
    <ErrorBoundary>
      <Suspense fallback={<PageLoader />}>
        {children}
      </Suspense>
    </ErrorBoundary>
  )
}

function ProtectedRoute({ children }) {
  const { adminToken } = useContext(AuthContext)
  if (!adminToken) {
    return <Navigate to="/login" replace />
  }
  return children
}

function GuestRoute({ children }) {
  const { adminToken } = useContext(AuthContext)
  if (adminToken) {
    return <Navigate to="/" replace />
  }
  return children
}

// 路由切换时滚动到顶部
function ScrollToTop() {
  const { pathname } = useLocation()
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [pathname])
  return null
}

function App() {
  return (
    <ErrorBoundary>
      <LoadingBar />
      <AuthProvider>
        <ScrollToTop />
        <Routes>
          <Route path="/login" element={<GuestRoute><LazyPage><Login /></LazyPage></GuestRoute>} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <AdminLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<LazyPage><Dashboard /></LazyPage>} />
            <Route path="user-manage" element={<LazyPage><UserManage /></LazyPage>} />
            <Route path="shop-manage" element={<LazyPage><ShopManage /></LazyPage>} />
            <Route path="shop-audit" element={<LazyPage><ShopAudit /></LazyPage>} />
            <Route path="car-manage" element={<LazyPage><CarManage /></LazyPage>} />
            <Route path="purchase-manage" element={<LazyPage><PurchaseManage /></LazyPage>} />
            <Route path="trade-manage" element={<LazyPage><TradeManage /></LazyPage>} />
            <Route path="dispute-manage" element={<LazyPage><DisputeManage /></LazyPage>} />
            <Route path="deposit-manage" element={<LazyPage><DepositManage /></LazyPage>} />
            <Route path="export-config" element={<LazyPage><ExportConfig /></LazyPage>} />
            <Route path="car-library" element={<LazyPage><CarLibrary /></LazyPage>} />
            <Route path="resource-manage" element={<LazyPage><ResourceManage /></LazyPage>} />
          </Route>
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </ErrorBoundary>
  )
}

export default App
