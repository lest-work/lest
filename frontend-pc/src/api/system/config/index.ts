import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { Config, ConfigParam } from './model';

export async function pageConfigs(params?: ConfigParam): Promise<TableDataInfo<Config>> {
  const res = await request.get<TableDataInfo<Config>>('/system/config/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function addConfig(data: Config): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/config', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function updateConfig(data: Config): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/config', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function removeConfig(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/config/' + id);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function removeConfigs(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/config/' + ids.join());
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function exportConfigs(params?: ConfigParam) {
  const res = await request({ url: '/system/config/export', method: 'POST', data: toFormData(params), responseType: 'blob' });
  await checkDownloadRes(res);
  download(res.data, `config_${Date.now()}.xlsx`);
}

export async function refreshConfigs(): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/config/refreshCache');
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
