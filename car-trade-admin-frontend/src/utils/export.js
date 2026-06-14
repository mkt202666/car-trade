/**
 * 通用导出工具函数
 * 用于替代window.open直接访问导出URL的方式,确保携带JWT Token
 */

const ADMIN_TOKEN_KEY = 'admin_token'

/**
 * 导出文件(携带JWT Token)
 * @param {string} url - 导出API URL(完整路径或相对路径)
 * @param {string} filename - 下载文件名(可选,默认从Content-Disposition或URL提取)
 */
export const exportFile = async (url, filename) => {
  try {
    // 获取token
    const token = localStorage.getItem(ADMIN_TOKEN_KEY)
    if (!token) {
      throw new Error('未登录,请先登录')
    }

    // 如果url是相对路径,添加baseURL
    const fullUrl = url.startsWith('http') ? url : `/api/v1/admin${url}`

    // 发起请求
    const response = await fetch(fullUrl, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    })

    if (!response.ok) {
      throw new Error(`导出失败: HTTP ${response.status}`)
    }

    // 获取blob
    const blob = await response.blob()

    // 从Content-Disposition提取文件名
    let downloadFilename = filename
    if (!downloadFilename) {
      const contentDisposition = response.headers.get('Content-Disposition')
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="?([^"]+)"?/)
        if (filenameMatch && filenameMatch[1]) {
          downloadFilename = decodeURIComponent(filenameMatch[1])
        }
      }
    }

    // 如果还是没有文件名,从URL提取
    if (!downloadFilename) {
      const urlParts = fullUrl.split('/')
      downloadFilename = urlParts[urlParts.length - 1] || 'export.xlsx'
    }

    // 创建下载链接
    const downloadUrl = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = downloadUrl
    a.download = downloadFilename
    document.body.appendChild(a)
    a.click()

    // 清理
    window.URL.revokeObjectURL(downloadUrl)
    document.body.removeChild(a)

    return true
  } catch (error) {
    console.error('导出失败:', error)
    throw error
  }
}

/**
 * 导出Excel文件(快捷方法)
 * @param {string} apiUrl - API路径
 * @param {object} params - 查询参数
 */
export const exportExcel = (apiUrl, params = {}) => {
  const qs = new URLSearchParams(params).toString()
  const url = qs ? `${apiUrl}?${qs}` : apiUrl
  return exportFile(url)
}
