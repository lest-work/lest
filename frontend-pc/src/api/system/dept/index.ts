import request from '@/utils/request';
import type { AjaxResult } from '@/api';
import type { Dept, DeptParam } from './model';

/**
 * 根据部门id查询部门详情
 * GET /system/dept/{deptId}
 */
export async function getDept(id: number): Promise<Dept> {
  const res = await request.get<AjaxResult<Dept>>('/system/dept/' + id);
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 查询部门列表（排除指定节点及子节点）
 * GET /system/dept/list/exclude/{deptId}
 */
export async function listExcludeDepts(deptId: number): Promise<Dept[]> {
  const res = await request.get<AjaxResult<Dept[]>>('/system/dept/list/exclude/' + deptId);
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function listDepts(params?: DeptParam): Promise<Dept[]> {
  const res = await request.get<AjaxResult<Dept[]>>('/system/dept/list', { params });
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function addDept(data: Dept): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/dept', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function updateDept(data: Dept): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/dept', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function removeDept(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/dept/' + id);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
