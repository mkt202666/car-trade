/**
 * Banner 广告位 API
 * 提供首页轮播 Banner 和弹窗广告的查询接口（公开，无需登录）
 */
export const getBanners = () => uni.http.get('/banners')
export const getPopup = () => uni.http.get('/banners/popup')
