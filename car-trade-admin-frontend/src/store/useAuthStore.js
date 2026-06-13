import { create } from 'zustand'
import { authApi } from '../api/auth'

const useAuthStore = create((set, get) => ({
  user: JSON.parse(localStorage.getItem('admin_user') || 'null'),
  token: localStorage.getItem('admin_token'),

  login: async (username, password) => {
    const data = await authApi.login({ username, password })
    localStorage.setItem('admin_token', data.accessToken)
    localStorage.setItem('admin_user', JSON.stringify(data.admin))
    localStorage.setItem('admin_refresh_token', data.refreshToken)
    set({ token: data.accessToken, user: data.admin })
    return data
  },

  logout: () => {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
    localStorage.removeItem('admin_refresh_token')
    set({ token: null, user: null })
  },

  refreshToken: async () => {
    const refreshToken = localStorage.getItem('admin_refresh_token')
    if (!refreshToken) return null
    try {
      const data = await authApi.refresh(refreshToken)
      localStorage.setItem('admin_token', data.accessToken)
      set({ token: data.accessToken })
      return data.accessToken
    } catch {
      get().logout()
      return null
    }
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('admin_token')
  },
}))

export default useAuthStore
