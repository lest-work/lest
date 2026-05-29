/**
 * axios实例
 */
import axios from 'axios';
import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios';
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
  (error) => {
    console.error(error);
    return Promise.reject(new Error('网络错误'));
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
  (error) => {
    console.error(error);
    return Promise.reject(new Error('网络错误'));
  }
);

export default service;
