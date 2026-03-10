import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.response.use(
  response => {
    const res = response.data
    if (!res.success) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// Authorization Letter API
export const authLetterApi = {
  list: () => api.get('/auth-letters'),
  get: (id) => api.get(`/auth-letters/${id}`),
  getByCode: (code) => api.get(`/auth-letters/code/${code}`),
  create: (data) => api.post('/auth-letters', data),
  update: (id, data) => api.put(`/auth-letters/${id}`, data),
  delete: (id) => api.delete(`/auth-letters/${id}`),
  publish: (id, params) => api.post(`/auth-letters/${id}/publish`, null, { params }),
  match: (data) => api.post('/auth-letters/match', data)
}

// Scene API
export const sceneApi = {
  list: (authLetterId) => api.get(`/auth-letters/${authLetterId}/scenes`),
  get: (id) => api.get(`/scenes/${id}`),
  create: (authLetterId, data) => api.post(`/auth-letters/${authLetterId}/scenes`, data),
  update: (id, data) => api.put(`/scenes/${id}`, data),
  delete: (id) => api.delete(`/scenes/${id}`)
}

// Rule API
export const ruleApi = {
  list: (sceneId) => api.get(`/scenes/${sceneId}/rules`),
  get: (id) => api.get(`/rules/${id}`),
  create: (sceneId, data) => api.post(`/scenes/${sceneId}/rules`, data),
  update: (id, data) => api.put(`/rules/${id}`, data),
  delete: (id) => api.delete(`/rules/${id}`)
}

export default api