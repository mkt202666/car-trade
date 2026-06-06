export const getAvailableCoupons = () => uni.$u.http.get('/coupons')
export const getMyCoupons = (params) => uni.$u.http.get('/coupons/my', { params })
