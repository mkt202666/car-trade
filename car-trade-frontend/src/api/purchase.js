// 求购相关接口
export const createPurchaseDemand = (data) => uni.http.post('/purchases', data)
export const getPurchaseList = (params) => uni.http.get('/purchases', { params })
export const getMyDemands = (params) => uni.http.get('/purchases/my', { params })
export const cancelDemand = (id) => uni.http.post(`/purchases/${id}/cancel`)
export const deletePurchase = (id) => uni.http.delete(`/purchases/${id}`)
export const getPurchaseDetail = (id) => uni.http.get(`/purchases/${id}`)
export const createPurchase = (data) => uni.http.post('/purchases', data)
export const updatePurchase = (id, data) => uni.http.put(`/purchases/${id}`, data)