export const getPlans = () => uni.http.get('/membership/plans')
export const getMyMembership = () => uni.http.get('/membership/my')
export const renewMembership = (planId) => uni.http.post('/membership/renew', { planId })
