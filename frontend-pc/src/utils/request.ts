/**
 * axios实例
 */
import axios, { AxiosError, type AxiosResponse } from 'axios';
import type { InternalAxiosRequestConfig } from 'axios';
import { unref } from 'vue';
import { API_BASE_URL, LAYOUT_PATH } from '@/config/setting';
import type { AjaxResult } from '@/api';
import router from '@/router';
import { isWhiteList } from '@/router/routes';
import { getToken } from './token-util';
import { logout, showLogoutConfirm, toURLSearch } from './common';

/**
 * 请求拦截处理
 */
export function requestInterceptor(config: InternalAxiosRequestConfig<any>) {
  // 添加token到header
  const token = getToken();
  if (token && config.headers) {
    config.headers['Authorization'] = token;
  }
  // get请求处理数组和对象类型参数
  if (config.method === 'get' && config.params) {
    config.url = toURLSearch(config.params, config.url);
    config.params = {};
  }
}

/**
 * 响应拦截处理
 */
export function responseInterceptor(res: AxiosResponse<AjaxResult<unknown>>) {
  // 登录过期处理
  if (res.data?.code === 401 || (res.data?.code === 403 && !getToken())) {
    const { path, fullPath } = unref(router.currentRoute);
    if (!(isWhiteList(path) || isWhiteList(location.pathname))) {
      if (path == LAYOUT_PATH) {
        logout(true, void 0, router.push);
      } else if (path !== '/login') {
        showLogoutConfirm(fullPath);
      }
    }
    return res.data.msg;
  }
}

/** 从 axios 错误中提取友好信息 */
function getFriendlyErrorMessage(error: AxiosError): string {
  // 网络层面的错误（后端完全不可达）
  if (error.code === 'ECONNREFUSED') {
    return '服务暂不可用，请检查网络或稍后重试';
  }
  if (error.code === 'ETIMEDOUT' || error.code === 'ESOCKETTIMEDOUT' || error.code === 'ETIMEDOUT') {
    return '请求超时，请稍后重试';
  }
  if (error.code === 'ENOTFOUND' || error.code === 'ECONNRESET' || error.code === 'ERR_NETWORK') {
    return '网络连接异常，请检查网络后重试';
  }
  if (error.code === 'ERR_CANCELED') {
    return '请求已取消';
  }

  // HTTP 状态码层面
  const status = error.response?.status;
  if (status) {
    if (status >= 500 && status < 600) {
      return '服务器开小差了，请稍后重试';
    }
    if (status === 403) {
      return '无权访问该资源';
    }
    if (status === 404) {
      return '请求的资源不存在';
    }
    if (status === 429) {
      return '操作过于频繁，请稍后再试';
    }
    if (status === 502 || status === 503 || status === 504) {
      return '服务暂时不可用，请稍后重试';
    }
  }

  // 通用兜底
  return '网络异常，请检查网络连接';
}

/** 创建axios实例 */
const service = axios.create({
  baseURL: API_BASE_URL
});

/**
 * 添加响应拦截器
 */
service.interceptors.response.use(
  (res: AxiosResponse<AjaxResult<unknown>>) => {
    const errorMessage = responseInterceptor(res);
    if (errorMessage) {
      return Promise.reject(new Error(errorMessage));
    }
    return res;
  },
  (error: AxiosError) => {
    const msg = getFriendlyErrorMessage(error);
    return Promise.reject(new Error(msg));
  }
);

/**
 * 添加请求拦截器
 */
service.interceptors.request.use(
  (config) => {
    requestInterceptor(config);
    return config;
  },
  (error: AxiosError) => {
    const msg = getFriendlyErrorMessage(error);
    return Promise.reject(new Error(msg));
  }
);

export default service;
