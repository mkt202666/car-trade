export const getOrderList = (params) => uni.$u.http.get('/orders', { params })
export const getOrderStats = () => uni.$u.http.get('/orders/stats')
export const getOrderDetail = (id) => uni.$u.http.get(`/orders/${id}`)
export const createOrder = (data) => uni.$u.http.post('/orders', data)
export const confirmOrder = (id) => uni.$u.http.put(`/orders/${id}/confirm`)
export const cancelOrder = (id, reason) => uni.$u.http.put(`/orders/${id}/cancel`, { reason })
export const payDeposit = (id) => uni.$u.http.put(`/orders/${id}/pay-deposit`)
export const completeOrder = (id) => uni.$u.http.put(`/orders/${id}/complete`)
export const createDispute = (id, data) => uni.$u.http.post(`/orders/${id}/dispute`, data)

// 合同相关接口
export const submitContract = (id, data) => uni.$u.http.post(`/orders/${id}/contract`, data)
export const updateContract = (id, data) => uni.$u.http.put(`/orders/${id}/contract`, data)
export const confirmContract = (id) => uni.$u.http.put(`/orders/${id}/contract/confirm`)
export const getContract = (id) => uni.$u.http.get(`/orders/${id}/contract`)

// 终止交易接口
export const terminateOrder = (id, data) => uni.$u.http.post(`/orders/${id}/terminate`, data)
export const getTerminateCount = (id) => uni.$u.http.get(`/orders/${id}/terminate/count`)

// 订单日志接口
export const getOrderLogs = (id) => uni.$u.http.get(`/orders/${id}/logs`)
