export const getAvailableCoupons = () => uni.http.get('/coupons')
export const getMyCoupons = (params) => uni.http.get('/coupons/my', { params })
export const claimCoupon = (id) => uni.http.post(`/coupons/${id}/claim`)
export const useCoupon = (id, orderId) => uni.http.post(`/coupons/${id}/use`, { orderId })
