import request from './request'

/**
 * 获取所有城市列表
 */
export function getAllCities() {
  return request({
    url: '/cities',
    method: 'get'
  })
}

/**
 * 获取热门城市列表
 */
export function getHotCities() {
  return request({
    url: '/cities/hot',
    method: 'get'
  })
}

/**
 * 根据省份获取城市列表
 */
export function getCitiesByProvince(province) {
  return request({
    url: `/cities/province/${encodeURIComponent(province)}`,
    method: 'get'
  })
}