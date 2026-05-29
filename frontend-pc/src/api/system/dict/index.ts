import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo, PageParam } from '@/api';
import type { Dict } from './model';

/**
 * 分页查询字典类型列表（管理页）
 * GET /system/dict/type/list
 */
export async function pageDicts(params?: PageParam & { dictName?: string; dictType?: string; status?: string }): Promise<TableDataInfo<Dict>> {
  const res = await request.get<TableDataInfo<Dict>>('/system/dict/type/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 根据字典id查询字典类型详情
 * GET /system/dict/type/{dictId}
 */
export async function getDict(id: number): Promise<Dict> {
  const res = await request.get<AjaxResult<Dict>>('/system/dict/type/' + id);
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 获取字典类型下拉选择列表（不分页，用于筛选选项）
 * GET /system/dict/type/optionselect
 */
export async function listDicts(): Promise<Dict[]> {
  const res = await request.get<AjaxResult<Dict[]>>('/system/dict/type/optionselect');
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 导出字典类型列表
 * POST /system/dict/type/export
 */
export async function exportDicts(params?: Record<string, any>) {
  const res = await request({ url: '/system/dict/type/export', method: 'POST', data: toFormData(params), responseType: 'blob' });
  await checkDownloadRes(res);
  download(res.data, `dict_${Date.now()}.xlsx`);
}

export async function addDict(data: Dict): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/dict/type', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function updateDict(data: Dict): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/dict/type', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeDict(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/dict/type/' + id);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function refreshDicts(): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/dict/type/refreshCache');
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
