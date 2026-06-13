export const createContract = (orderId) => uni.http.post('/contracts', { orderId })
export const getContractDetail = (id) => uni.http.get(`/contracts/${id}`)
export const signContract = (id, role, userId) => uni.http.put(`/contracts/${id}/sign?role=${role}&userId=${userId}`)
export const downloadContract = (id) => uni.http.get(`/contracts/${id}/download`)
