import request from '@/utils/request';
import type { ApiResult } from '@/api';
import type { Menu } from '../menu/model';

/**
 * 获取角色分配的菜单（返回完整菜单树，已标记选中状态）
 */
export async function listRoleMenus(roleId: number) {
  const res = await request.get<ApiResult<Menu[]>>(
    '/auth/role-menu/' + roleId + '/tree'
  );
  if (res.data.code === 200) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改角色菜单
 */
export async function updateRoleMenus(roleId: number, data?: number[]) {
  const res = await request.put<ApiResult<void>>(
    '/auth/role-menu/' + roleId,
    data
  );
  if (res.data.code === 200) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
