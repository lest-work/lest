import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { User, UserParam, UserDetailResult, UserRoleResult } from './model';

/**
 * 分页查询用户
 */
export async function pageUsers(
  params?: UserParam
): Promise<TableDataInfo<User>> {
  const res = await request.get<TableDataInfo<User>>('/system/user/list', {
    params
  });
  if (res.data.code === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 根据id查询用户（含 roleIds/postIds/roles/posts 顶层字段）
 * 后端: SysUserController.getInfo(userId) → ajax.put("data", user) + ajax.put("roleIds",...)
 */
export async function getUser(id: number): Promise<UserDetailResult> {
  const res = await request.get<UserDetailResult>('/system/user/' + id);
  if (res.data.code === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 添加用户
 */
export async function addUser(data: User): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/user', data);
  if (res.data.code === 200) {
    return res.data.msg || '操作成功';
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 修改用户
 */
export async function updateUser(data: User): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/user', data);
  if (res.data.code === 200) {
    return res.data.msg || '操作成功';
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 删除用户
 */
export async function removeUser(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/user/' + id);
  if (res.data.code === 200) {
    return res.data.msg || '操作成功';
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 批量删除用户
 */
export async function removeUsers(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(
    '/system/user/' + ids.join()
  );
  if (res.data.code === 200) {
    return res.data.msg || '操作成功';
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 修改用户状态
 */
export async function updateUserStatus(
  userId: number,
  status: string
): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(
    '/system/user/changeStatus',
    { userId, status }
  );
  if (res.data.code === 200) {
    return res.data.msg || '操作成功';
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 重置用户密码
 */
export async function updateUserPassword(
  userId: number,
  password: string
): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/user/resetPwd', {
    userId,
    password
  });
  if (res.data.code === 200) {
    return res.data.msg || '操作成功';
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 导出用户列表
 */
export async function exportUsers(params?: UserParam) {
  const res = await request({
    url: '/system/user/export',
    method: 'POST',
    data: toFormData(params),
    responseType: 'blob'
  });
  await checkDownloadRes(res);
  download(res.data, `user_${Date.now()}.xlsx`);
}

/**
 * 下载用户导入模板
 */
export async function downloadTemplate() {
  const res = await request({
    url: '/system/user/importTemplate',
    method: 'POST',
    responseType: 'blob'
  });
  await checkDownloadRes(res);
  download(res.data, `user_template_${Date.now()}.xlsx`);
}

/**
 * 导入用户
 */
export async function importUsers(
  file: File,
  isUpdate: boolean
): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('updateSupport', String(isUpdate));
  const res = await request.post<AjaxResult<unknown>>(
    '/system/user/importData',
    formData
  );
  if (res.data.code === 200) {
    return res.data.msg || '操作成功';
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 查询用户角色（含 user + roles 顶层字段，roles.flag 标记已分配）
 * 后端: SysUserController.authRole(userId) → ajax.put("user",...) + ajax.put("roles",...)
 */
export async function getUserRole(id: number): Promise<UserRoleResult> {
  const res = await request.get<UserRoleResult>(
    '/system/user/authRole/' + id
  );
  if (res.data.code === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 修改用户角色
 */
export async function setUserRole(data: {
  userId?: number;
  roleIds?: string;
}): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(
    '/system/user/authRole',
    toFormData(data)
  );
  if (res.data.code === 200) {
    return res.data.msg || '操作成功';
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
