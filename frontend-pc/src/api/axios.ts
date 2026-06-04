import axios from 'axios';
import type { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';

interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  timestamp?: number;
}

const service: AxiosInstance = axios.create({
  baseURL: '',
  timeout: 30000,
});

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('access_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, message } = response.data;
    if (code === 200) {
      return response;
    }
    ElMessage.error(message || '请求失败');
    return Promise.reject(new Error(message));
  },
  (error: AxiosError<ApiResponse>) => {
    if (error.response) {
      const { status, data } = error.response;
      switch (status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录');
          localStorage.removeItem('access_token');
          router.push('/login');
          break;
        case 403:
          ElMessage.error('没有权限访问该资源');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error(data?.message || '服务器内部错误');
          break;
        default:
          ElMessage.error(data?.message || '网络请求失败');
      }
    } else {
      ElMessage.error('网络连接失败，请检查网络');
    }
    return Promise.reject(error);
  }
);

export default service;
