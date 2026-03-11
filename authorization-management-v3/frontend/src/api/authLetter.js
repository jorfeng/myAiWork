/**
 * Authorization Letter API
 */
import apiClient from './index'

export const authLetterApi = {
  /**
   * Query authorization letters with pagination
   */
  queryList(params) {
    return apiClient.get('/auth-letters', { params })
  },

  /**
   * Batch publish authorization letters
   */
  batchPublish(ids) {
    return apiClient.put('/auth-letters/batch/publish', { ids })
  },

  /**
   * Batch expire authorization letters
   */
  batchExpire(ids) {
    return apiClient.put('/auth-letters/batch/expire', { ids })
  },

  /**
   * Batch delete authorization letters
   */
  batchDelete(ids) {
    return apiClient.delete('/auth-letters/batch', { data: { ids } })
  }
}

/**
 * Lookup API
 * TODO: Implement real lookup service integration
 */
export const lookupApi = {
  /**
   * Get lookup values by code
   */
  getValues(code) {
    return apiClient.get(`/lookup/${code}`)
  },

  /**
   * Get organization tree
   */
  getOrgTree() {
    return apiClient.get('/lookup/org/tree')
  }
}