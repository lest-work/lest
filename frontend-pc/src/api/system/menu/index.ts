import request from '@/utils/request';
import { ruoYiIcons } from '@/api/layout';
import type { AjaxResult } from '@/api';
import type { Menu, MenuParam } from './model';

export async function listMenus(params?: MenuParam): Promise<Menu[]> {
  const res = await request.get<AjaxResult<Menu[]>>('/system/menu/list', { params });
  if (res.data.code === 200 && res.data.data) {
    return res.data.data.map((d) => {
      const icon = d.icon === '#' ? void 0 : d.icon;
      return { ...d, icon: ruoYiIcons[icon as string] ?? icon };
    });
  }
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function addMenu(data: Menu): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/menu', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function updateMenu(data: Menu): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/menu', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function removeMenu(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/menu/' + id);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
