import store from '@/store'

export function requireAuth() {
  if (!store.getters.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/index' })
    return false
  }
  return true
}

export function checkRole(requiredRole) {
  const user = store.state.user
  if (!user) return false
  return user.certificationStatus === 'CERTIFIED'
}
