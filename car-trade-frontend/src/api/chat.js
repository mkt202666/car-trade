export const getConversations = () => uni.$u.http.get('/chat/conversations')
export const createConversation = (data) => uni.$u.http.post('/chat/conversations', data)
export const getChatMessages = (id, params) => uni.$u.http.get(`/chat/conversations/${id}/messages`, { params })
export const markConversationRead = (id) => uni.$u.http.post(`/chat/conversations/${id}/read`)
