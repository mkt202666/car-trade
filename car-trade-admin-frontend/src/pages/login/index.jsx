import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Lock, User } from 'lucide-react'
import useAuthStore from '../../store/useAuthStore'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { login } = useAuthStore()

  // If already logged in, redirect to dashboard
  useEffect(() => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      navigate('/', { replace: true })
    }
  }, [navigate])

  const handleLogin = async (e) => {
    e.preventDefault()
    if (!username.trim() || !password.trim()) {
      setError('请输入用户名和密码')
      return
    }
    setError('')
    setLoading(true)
    try {
      await login(username, password)
      navigate('/', { replace: true })
    } catch (err) {
      setError(err.message || '登录失败，请重试')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 flex items-center justify-center p-4">
      <div className="w-full max-w-sm">
        {/* Logo */}
        <div className="text-center mb-8">
          <div className="w-14 h-14 bg-indigo-600 rounded-2xl flex items-center justify-center font-black text-white text-2xl tracking-wider border border-indigo-400/40 shadow-lg mx-auto mb-4">
            5D
          </div>
          <h1 className="text-xl font-bold text-white">5D Auto Operation Desk</h1>
          <p className="text-xs text-indigo-300 mt-1 font-mono">车商互操作系统运作管理后台 v3.5</p>
        </div>

        {/* Form */}
        <form onSubmit={handleLogin} className="space-y-4">
          <div className="flex items-center gap-3 bg-white/10 backdrop-blur-md border border-white/10 rounded-xl px-4 py-3">
            <User className="w-4 h-4 text-indigo-300" />
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="用户名"
              autoComplete="username"
              className="bg-transparent outline-none text-white placeholder:text-indigo-300/50 text-sm w-full"
            />
          </div>
          <div className="flex items-center gap-3 bg-white/10 backdrop-blur-md border border-white/10 rounded-xl px-4 py-3">
            <Lock className="w-4 h-4 text-indigo-300" />
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="密码"
              autoComplete="current-password"
              className="bg-transparent outline-none text-white placeholder:text-indigo-300/50 text-sm w-full"
            />
          </div>

          {error && (
            <p className="text-red-400 text-xs text-center">{error}</p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-semibold py-3 rounded-xl transition-colors text-sm"
          >
            {loading ? '登录中...' : '登录'}
          </button>
        </form>

        <p className="text-center text-[10px] text-indigo-300/40 mt-8 font-mono">
          5D好车 &copy; 2026 — 合规风控审计系统
        </p>
      </div>
    </div>
  )
}
