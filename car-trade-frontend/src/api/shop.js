export const listMembers = (params) => uni.$u.http.get('/shop/members', { params })
export const inviteMember = (userId) => uni.$u.http.post('/shop/members/invite', { userId })
export const approveMember = (id, approve) => uni.$u.http.put(`/shop/members/${id}/approve`, {}, { params: { approve } })
export const removeMember = (id) => uni.$u.http.delete(`/shop/members/${id}`)
