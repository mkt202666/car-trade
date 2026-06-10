export const getAvailableCoupons = () => uni.$u.http.get('/coupons')
export const getMyCoupons = (params) => uni.$u.http.get('/coupons/my', { params })
export const claimCoupon = (id) => uni.$u.http.post(`/coupons/${id}/claim`)
export const useCoupon = (id, orderId) => uni.$u.http.post(`/coupons/${id}/use`, { orderId })
