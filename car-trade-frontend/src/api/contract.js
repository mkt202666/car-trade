export const createContract = (orderId) => uni.$u.http.post('/contracts', { orderId })
export const getContractDetail = (id) => uni.$u.http.get(`/contracts/${id}`)
export const signContract = (id) => uni.$u.http.put(`/contracts/${id}/sign`)
export const downloadContract = (id) => uni.$u.http.get(`/contracts/${id}/download`)
