export const followUser = (userId) => uni.$u.http.post(`/follows/${userId}`)
export const unfollowUser = (userId) => uni.$u.http.delete(`/follows/${userId}`)
export const getFollowList = (params) => uni.$u.http.get('/follows', { params })
export const checkFollowed = (userId) => uni.$u.http.get(`/follows/${userId}/check`)
