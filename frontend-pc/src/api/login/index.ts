import request from '@/utils/request';
import { setToken } from '@/utils/token-util';
import type { AjaxResult } from '@/api';
import type { LoginParam, LoginTokenResult, CaptchaResult } from './model';

/**
 * 登录
 * POST /auth/login → gateway → auth service POST /login
 * 返回: R<{ access_token: string, expires_in: number }>
 */
export async function login(data: LoginParam): Promise<string> {
  const res = await request.post<AjaxResult<LoginTokenResult>>('/auth/login', data);
  if (res.data.code === 200 && res.data.data) {
    setToken('Bearer ' + res.data.data.access_token, data.remember);
    return res.data.msg || '登录成功';
  }
  return Promise.reject(new Error(res.data.msg || '登录失败'));
}

/**
 * 获取验证码
 * GET /captcha → 直接由网关处理，返回 AjaxResult 顶层字段 { captchaEnabled, uuid, img }
 */
export async function getCaptcha(): Promise<CaptchaResult> {
  const res = await request.get<AjaxResult<unknown>>('/captcha');
  if (res.data.code === 200) {
    return res.data as unknown as CaptchaResult;
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
