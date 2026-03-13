import axios from 'axios';

const BASE_URL = '/api';

const request = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

request.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

export default {
  get(url, params) {
    return request.get(url, { params });
  },
  post(url, data) {
    return request.post(url, data);
  },
  put(url, data) {
    return request.put(url, data);
  },
  delete(url, params) {
    return request.delete(url, { params });
  }
};