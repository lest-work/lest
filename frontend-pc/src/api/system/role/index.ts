import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { Role, RoleParam } from './model';
import type { User } from '@/api/system/user/model';

export interface RoleMenuResult {
  menus?: any[];
  checkedKeys?: number[];
}

export interface DeptTreeResult {
  depts?: any[];
  checkedKeys?: number[];
}

export interface RoleAuthParam {
  roleId?: number;
  userIds?: string;
}

/**
 * 查询角色详情
 * GET /system/role/{roleId}
 */
export async function getRole(id: number): Promise<Role> {
  const res = await request.get<AjaxResult<Role>>('/system/role/' + id);
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function pageRoles(params?: RoleParam): Promise<TableDataInfo<Role>> {
  const res = await request.get<TableDataInfo<Role>>('/system/role/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function addRole(data: Role): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/role', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function updateRole(data: Role): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/role', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeRole(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/role/' + id);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeRoles(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/role/' + ids.join());
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function updateRoleStatus(roleId: number, status: string): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/role/changeStatus', { roleId, status });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function exportRoles(params?: RoleParam) {
  const res = await request({ url: '/system/role/export', method: 'POST', data: toFormData(params), responseType: 'blob' });
  await checkDownloadRes(res);
  download(res.data, `role_${Date.now()}.xlsx`);
}

export async function listRoles(): Promise<Role[]> {
  const res = await request.get<AjaxResult<Role[]>>('/system/role/optionselect');
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function listRoleMenus(id?: number): Promise<RoleMenuResult> {
  if (!id) {
    const res = await request.get<AjaxResult<any>>('/system/menu/treeselect');
    if (res.data.code === 200) return { menus: res.data.data };
    return Promise.reject(new Error(res.data.msg));
  }
  const res = await request.get<AjaxResult<any>>('/system/menu/roleMenuTreeselect/' + id);
  if (res.data.code === 200) return { menus: (res.data as any).menus, checkedKeys: (res.data as any).checkedKeys };
  return Promise.reject(new Error(res.data.msg));
}

export async function listDataScope(id: number): Promise<DeptTreeResult> {
  const res = await request.get<AjaxResult<any>>('/system/role/deptTree/' + id);
  if (res.data.code === 200) return { depts: (res.data as any).depts, checkedKeys: (res.data as any).checkedKeys };
  return Promise.reject(new Error(res.data.msg));
}

export async function setDataScope(data: { roleId?: number; dataScope?: string; deptIds?: number[] }): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/role/dataScope', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询已分配用户角色列表
 * GET /system/role/authUser/allocatedList
 */
export async function listRoleUsers(params?: { roleId?: number; userName?: string; phonenumber?: string; pageNum?: number; pageSize?: number }): Promise<TableDataInfo<User>> {
  const res = await request.get<TableDataInfo<User>>('/system/role/authUser/allocatedList', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeRoleUser(data: { roleId?: number; userId?: number }): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/role/authUser/cancel', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeRoleUsers(params: RoleAuthParam): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/role/authUser/cancelAll', toFormData(params));
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询未分配用户角色列表
 * GET /system/role/authUser/unallocatedList
 */
export async function listUnallocatedUsers(params?: { roleId?: number; userName?: string; phonenumber?: string; pageNum?: number; pageSize?: number }): Promise<TableDataInfo<User>> {
  const res = await request.get<TableDataInfo<User>>('/system/role/authUser/unallocatedList', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function addRoleUsers(params: RoleAuthParam): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/role/authUser/selectAll', toFormData(params));
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
