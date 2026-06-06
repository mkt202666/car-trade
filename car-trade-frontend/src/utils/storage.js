export function getToken() { return uni.getStorageSync('token') }
export function setToken(token) { uni.setStorageSync('token', token) }
export function removeToken() { uni.removeStorageSync('token') }
export function getUser() { return uni.getStorageSync('user') }
export function setUser(user) { uni.setStorageSync('user', JSON.stringify(user)) }
export function clearStorage() { uni.clearStorageSync() }
