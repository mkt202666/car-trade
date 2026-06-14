import request from '../utils/request'

/**
 * 获取所有出口地区列表
 */
export const getExportRegions = () => {
  return request({
    url: '/export-regions',
    method: 'get'
  })
}

/**
 * 获取单个出口地区详情
 */
export const getExportRegionDetail = (id) => {
  return request({
    url: `/export-regions/${id}`,
    method: 'get'
  })
}

/**
 * 创建出口地区配置
 */
export const createExportRegion = (data) => {
  return request({
    url: '/export-regions',
    method: 'post',
    data
  })
}

/**
 * 更新出口地区配置
 */
export const updateExportRegion = (id, data) => {
  return request({
    url: `/export-regions/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除出口地区配置
 */
export const deleteExportRegion = (id) => {
  return request({
    url: `/export-regions/${id}`,
    method: 'delete'
  })
}

/**
 * 切换出口地区状态
 */
export const toggleExportRegionStatus = (id) => {
  return request({
    url: `/export-regions/${id}/toggle-status`,
    method: 'patch'
  })
}
