import { createStore } from 'vuex'
import { saveToken, clearToken, getToken } from '@/utils/auth'
import { STORAGE_KEYS } from '@/constants/storage'

// 统一读取 token — 与 utils/auth.js 保持一致（避免 key 不匹配）
let savedToken = null
try {
  savedToken = getToken()
} catch (e) {
  savedToken = null
}

console.log('[Store init] token from storage:', savedToken ? 'found(' + savedToken.length + ')' : 'not found')

export default createStore({
  state: {
    user: null,
    token: savedToken,
    unreadCount: 0
  },
  mutations: {
    setUser(state, user) {
      state.user = user
      try {
        const payload = user ? JSON.stringify(user) : ''
        uni.setStorageSync(STORAGE_KEYS.USER_INFO, payload)
        try { localStorage.setItem(STORAGE_KEYS.USER_INFO, payload) } catch (_) {}
      } catch (_) {}
      console.log('[Store] setUser:', user)
    },
    setToken(state, token) {
      state.token = token
      console.log('[Store] setToken:', token ? token.substring(0, 20) + '... (' + token.length + ')' : 'null')
      // 统一使用 auth.saveToken — 保证 uni storage 和原生 storage 都能写入
      if (token && token !== 'null' && token !== 'undefined' && String(token).trim().length > 10) {
        saveToken(String(token).trim())
      } else {
        clearToken()
      }
    },
    /**
     * 一次性写入完整 token 套件（access + refresh + expiresIn）
     * 登录 / 续期成功后由 action 直接调用，避免在 mutation 内做副作用
     */
    saveAuthBundle(state, bundle) {
      if (!bundle || !bundle.accessToken) return
      state.token = bundle.accessToken
      saveToken(bundle.accessToken, bundle.refreshToken, bundle.expiresIn)
    },
    setUnreadCount(state, count) {
      state.unreadCount = count
    },
    logout(state) {
      state.user = null
      state.token = null
      clearToken()
    }
  },
  actions: {
    async login({ commit }, credentials) {
      console.log('[Store] login start:', credentials)
      const res = await uni.http.post('/users/login', credentials)
      console.log('[Store] login response, token len:', res.data?.accessToken ? res.data.accessToken.length : 'undefined')
      if (res.data && res.data.accessToken) {
        commit('saveAuthBundle', res.data)
      } else {
        console.warn('[Store] login got no accessToken!')
      }
      if (res.data && res.data.user) {
        commit('setUser', res.data.user)
      }
      return res
    },
    async getUser({ commit }) {
      const res = await uni.http.get('/users/me')
      commit('setUser', res.data)
      return res
    }
  },
  getters: {
    isLoggedIn: state => !!(state.token && state.token.length > 10),
    currentUser: state => state.user
  }
})
