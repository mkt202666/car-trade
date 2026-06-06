export const getPlans = () => uni.$u.http.get('/membership/plans')
export const getMyMembership = () => uni.$u.http.get('/membership/my')
export const renewMembership = (planId) => uni.$u.http.post('/membership/renew', { planId })
