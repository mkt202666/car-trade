import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: null,
    token: null,
    unreadCount: 0
  },
  mutations: {
    setUser(state, user) {
      state.user = user
    },
    setToken(state, token) {
      state.token = token
      uni.setStorageSync('token', token)
    },
    setUnreadCount(state, count) {
      state.unreadCount = count
    },
    logout(state) {
      state.user = null
      state.token = null
      uni.clearStorageSync()
    }
  },
  actions: {
    async login({ commit }, credentials) {
      const res = await uni.$u.api.post('/users/login', credentials)
      commit('setToken', res.data.accessToken)
      commit('setUser', res.data.user)
      return res
    },
    async getUser({ commit }) {
      const res = await uni.$u.api.get('/users/me')
      commit('setUser', res.data)
      return res
    }
  },
  getters: {
    isLoggedIn: state => !!state.token,
    currentUser: state => state.user
  }
})
