export const listMembers = (params) => uni.http.get('/shop/members', { params })
export const getShopMembers = listMembers
export const inviteMember = (data) => uni.http.post('/shop/members/invite', data)
export const approveMember = (id) => uni.http.put(`/shop/members/${id}/approve`)
export const removeMember = (id) => uni.http.delete(`/shop/members/${id}`)
