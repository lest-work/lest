import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/api';
import type { Menu, MenuParam } from './model';

/**
 * 分页查询菜单
 */
export async function pageMenus(params: MenuParam) {
  const res = await request.get<ApiResult<PageResult<Menu>>>(
    '/auth/menu/page',
    { params }
  );
  if (res.data.code === 200) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询菜单列表
 */
export async function listMenus(params?: MenuParam) {
  const res = await request.get<ApiResult<Menu[]>>('/auth/menu', {
    params
  });
  if (res.data.code === 200 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取菜单树
 */
export async function getMenuTree() {
  const res = await request.get<ApiResult<Menu[]>>('/auth/menu/tree');
  if (res.data.code === 200) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 添加菜单
 */
export async function addMenu(data: Menu) {
  const res = await request.post<ApiResult<unknown>>('/auth/menu', data);
  if (res.data.code === 200) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 修改菜单
 */
export async function updateMenu(data: Menu) {
  const res = await request.put<ApiResult<unknown>>('/auth/menu', data);
  if (res.data.code === 200) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除菜单（单个或批量）
 * @param id 单个ID
 * @param ids 批量ID列表（传此参数时表示批量删除）
 */
export async function removeMenu(id: number, ids?: number[]) {
  const params: Record<string, any> = {};
  if (ids && ids.length > 0) {
    params.ids = ids.join(',');
  }
  const res = await request.delete<ApiResult<void>>(
    '/auth/menu/' + id,
    { params }
  );
  if (res.data.code === 200) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
