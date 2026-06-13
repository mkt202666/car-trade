export const createTicket = (data) => uni.http.post('/cs/tickets', data)
export const listTickets = (params) => uni.http.get('/cs/tickets', { params })
export const getTicketDetail = (id) => uni.http.get(`/cs/tickets/${id}`)
