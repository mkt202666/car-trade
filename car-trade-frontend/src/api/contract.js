export const createContract = (orderId) => uni.$u.http.post('/contracts', { orderId })
export const getContractDetail = (id) => uni.$u.http.get(`/contracts/${id}`)
export const signContract = (id, role, userId) => uni.$u.http.put(`/contracts/${id}/sign?role=${role}&userId=${userId}`)
export const downloadContract = (id) => uni.$u.http.get(`/contracts/${id}/download`)
