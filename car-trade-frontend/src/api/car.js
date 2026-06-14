import { readToken } from '../constants/storage'
const API_URL = '/api/v1'

// 车源CRUD
export const getCarList = (params) => uni.http.get('/cars', { params })
export const getCarDetail = (id) => uni.http.get(`/cars/${id}`)
export const createCar = (data) => uni.http.post('/cars', data)
export const updateCar = (id, data) => uni.http.put(`/cars/${id}`, data)
export const deleteCar = (id) => uni.http.delete(`/cars/${id}`)
export const favoriteCar = (id) => uni.http.post(`/cars/${id}/favorite`)
export const unfavoriteCar = (id) => uni.http.delete(`/cars/${id}/favorite`)
export const getFavorites = (params) => uni.http.get('/cars/favorites', { params })
export const getRecommend = () => uni.http.get('/cars/recommend')
export const contactSeller = (id) => uni.http.post(`/cars/${id}/contact`)
export const downloadImage = (carId, imageId) => uni.http.get(`/cars/${carId}/images/${imageId}/download`)
export const getMyCarSource = (params) => uni.http.get('/cars/mine', { params })

// 图片上传(用于车源发布)
export const uploadCarImage = (filePath) => {
  const token = readToken()
  const authHeader = token ? { 'Authorization': 'Bearer ' + token } : {}
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: API_URL + '/uploads/car-image',
      filePath,
      name: 'file',
      header: authHeader,
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (res.statusCode === 200 && data.code === 200) {
            resolve(data)
          } else {
            uni.$u.toast(data.message || '图片上传失败')
            reject(new Error(data.message || '图片上传失败'))
          }
        } catch (e) {
          reject(new Error('服务器返回数据解析失败'))
        }
      },
      fail: (err) => reject(err)
    })
  })
}

// 批量图片上传
export const uploadCarImages = async (filePaths) => {
  const results = []
  for (const filePath of filePaths) {
    try {
      const result = await uploadCarImage(filePath)
      results.push(result.data.url) // 假设后端返回 { code: 200, data: { url: '...' } }
    } catch (error) {
      console.error('图片上传失败:', filePath, error)
      throw error
    }
  }
  return results
}

// 文件上传(检测报告/证件材料)
export const uploadCarDocument = (filePath, type = 'report') => {
  const token = readToken()
  const authHeader = token ? { 'Authorization': 'Bearer ' + token } : {}
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: API_URL + `/uploads/car-document?type=${type}`,
      filePath,
      name: 'file',
      header: authHeader,
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (res.statusCode === 200 && data.code === 200) {
            resolve(data)
          } else {
            uni.$u.toast(data.message || '文件上传失败')
            reject(new Error(data.message || '文件上传失败'))
          }
        } catch (e) {
          reject(new Error('服务器返回数据解析失败'))
        }
      },
      fail: (err) => reject(err)
    })
  })
}

// OCR识别
export const recognizeVin = (filePath) => {
  const token = readToken()
  const authHeader = token ? { 'Authorization': 'Bearer ' + token } : {}
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: API_URL + '/ocr/vin',
      filePath,
      name: 'image',
      header: authHeader,
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data)
          } else {
            reject(new Error(data.message || 'VIN码识别失败'))
          }
        } catch (e) {
          reject(e)
        }
      },
      fail: (err) => reject(err)
    })
  })
}

export const recognizeDrivingLicense = (filePath) => {
  const token = readToken()
  const authHeader = token ? { 'Authorization': 'Bearer ' + token } : {}
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: API_URL + '/ocr/driving-license',
      filePath,
      name: 'image',
      header: authHeader,
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data)
          } else {
            reject(new Error(data.message || '行驶证识别失败'))
          }
        } catch (e) {
          reject(e)
        }
      },
      fail: (err) => reject(err)
    })
  })
}
