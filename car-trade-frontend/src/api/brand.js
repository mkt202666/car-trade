export const getBrands = () => uni.http.get('/brands')
export const getSeries = (brandId) => uni.http.get(`/brands/${brandId}/series`)
export const getModels = (seriesId) => uni.http.get(`/brands/series/${seriesId}/models`)
