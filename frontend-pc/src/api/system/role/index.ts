import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/api';
import type { Role, RoleParam } from './model';

/**
 * 分页查询角色
 */
export async function pageRoles(params: RoleParam) {
  const res = await request.get<ApiResult<PageResult<Role>>>(
    '/auth/role/page',
    { params }
  );
  if (res.data.code === 200) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询角色列表
 */
export async function listRoles(params?: RoleParam) {
  const res = await request.get<ApiResult<Role[]>>('/auth/role', {
    params
  });
  if (res.data.code === 200 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加角色
 */
export async function addRole(data: Partial<Role> & { roleCode: string; roleName: string }) {
  const res = await request.post<ApiResult<unknown>>('/auth/role', data);
  if (res.data.code === 200) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改角色
 */
export async function updateRole(data: Partial<Role> & { roleId: number }) {
  const res = await request.put<ApiResult<unknown>>('/auth/role', data);
  if (res.data.code === 200) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除角色（单个或批量）
 * @param id 单个ID
 * @param ids 批量ID列表（传此参数时表示批量删除）
 */
export async function removeRole(id: number, ids?: number[]) {
  const params: Record<string, any> = {};
  if (ids && ids.length > 0) {
    params.ids = ids.join(',');
  }
  const res = await request.delete<ApiResult<void>>(
    '/auth/role/' + id,
    { params }
  );
  if (res.data.code === 200) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
