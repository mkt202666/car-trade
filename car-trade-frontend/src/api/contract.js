export const createContract = (orderId) => uni.http.post('/contracts', { orderId })
export const getContractDetail = (id) => uni.http.get(`/contracts/${id}`)
export const signContract = (id, role, userId, signatureUrl) => {
  const params = `role=${role}&userId=${userId}${signatureUrl ? `&signatureUrl=${encodeURIComponent(signatureUrl)}` : ''}`
  return uni.http.put(`/contracts/${id}/sign?${params}`)
}
export const downloadContract = (id) => uni.http.get(`/contracts/${id}/download`)

// 上传合同签名图片
export const uploadSignature = (contractId, filePath) => {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `/api/v1/contracts/${contractId}/upload-signature`,
      filePath,
      name: 'file',
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        } catch (e) {
          reject(e)
        }
      },
      fail: (err) => reject(err)
    })
  })
}
