/**
 * 登录相关接口定义
 */
import request from '@/utils/request';
import { setToken } from '@/utils/token-util';
import type { ApiResult } from '@/api';

export interface LoginParam {
  username: string;
  password: string;
  captcha: string;
  uuid: string;
}

export interface CaptchaResult {
  uuid: string;
  captcha: string;
}

export interface LoginResult {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

/**
 * 登录
 */
export async function login(data: LoginParam) {
  const res = await request.post<ApiResult<LoginResult>>('/auth/login', data);
  if (res.data.code === 200) {
    setToken('Bearer ' + res.data.data?.accessToken, data.remember ?? true);
    return res.data.message || '登录成功';
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取验证码
 */
export async function getCaptcha() {
  const res = await request.get<ApiResult<CaptchaResult>>('/auth/captcha');
  if (res.data.code === 200 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}
