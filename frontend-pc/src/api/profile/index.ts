import request from '@/utils/request';
import type { AjaxResult } from '@/api';
import type { User } from '@/api/system/user/model';

export interface ProfileResult {
  code: number;
  msg: string;
  data?: User;
  roleGroup?: string;
  postGroup?: string;
}

/**
 * 获取当前登录用户信息
 * GET /system/user/profile → lest-system SysProfileController GET /user/profile
 */
export async function getUserProfile(): Promise<ProfileResult> {
  const res = await request.get<ProfileResult>('/system/user/profile');
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 修改当前登录用户信息
 * PUT /system/user/profile
 */
export async function updateUserProfile(data: User): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/user/profile', data);
  if (res.data.code === 200) return res.data.msg || '修改成功';
  return Promise.reject(new Error(res.data.msg || '修改失败'));
}

/**
 * 修改当前登录用户密码
 * PUT /system/user/profile/updatePwd
 */
export async function updateUserPwd(oldPassword: string, newPassword: string): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/user/profile/updatePwd', { oldPassword, newPassword });
  if (res.data.code === 200) return res.data.msg || '密码修改成功';
  return Promise.reject(new Error(res.data.msg || '密码修改失败'));
}

/**
 * 修改当前登录用户头像
 * POST /system/user/profile/avatar
 */
export async function uploadAvatar(data: FormData): Promise<{ imgUrl: string }> {
  const res = await request.post<AjaxResult<unknown>>('/system/user/profile/avatar', data);
  if (res.data.code === 200) return res.data as unknown as { imgUrl: string };
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
