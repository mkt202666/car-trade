export const createTicket = (data) => uni.$u.http.post('/cs/tickets', data)
export const listTickets = (params) => uni.$u.http.get('/cs/tickets', { params })
export const getTicketDetail = (id) => uni.$u.http.get(`/cs/tickets/${id}`)
