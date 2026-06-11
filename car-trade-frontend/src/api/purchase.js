// 求购相关接口
export const createPurchaseDemand = (data) => uni.$u.http.post('/purchases', data)
export const getPurchaseList = (params) => uni.$u.http.get('/purchases', { params })
export const getMyDemands = (params) => uni.$u.http.get('/purchases/my', { params })
export const cancelDemand = (id) => uni.$u.http.post(`/purchases/${id}/cancel`)
export const deletePurchase = (id) => uni.$u.http.delete(`/purchases/${id}`)
