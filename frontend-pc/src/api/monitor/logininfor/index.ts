import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo, PageParam } from '@/api';

export interface Logininfor {
  infoId?: number;
  userName?: string;
  ipaddr?: string;
  loginLocation?: string;
  browser?: string;
  os?: string;
  status?: string;
  msg?: string;
  loginTime?: string;
}

export interface LogininforParam extends PageParam {
  ipaddr?: string;
  userName?: string;
  status?: string;
  beginTime?: string;
  endTime?: string;
}

/**
 * 分页查询登录日志
 * GET /system/logininfor/list → lest-system SysLogininforController
 */
export async function pageLogininfors(params?: LogininforParam): Promise<TableDataInfo<Logininfor>> {
  const res = await request.get<TableDataInfo<Logininfor>>('/system/logininfor/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 导出登录日志
 */
export async function exportLogininfors(params?: LogininforParam) {
  const res = await request({ url: '/system/logininfor/export', method: 'POST', data: toFormData(params), responseType: 'blob' });
  await checkDownloadRes(res);
  download(res.data, `logininfor_${Date.now()}.xlsx`);
}

/**
 * 批量删除登录日志
 */
export async function removeLogininfors(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/logininfor/' + ids.join());
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 清空登录日志
 */
export async function clearLogininfors(): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/logininfor/clean');
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 解锁用户
 */
export async function unlockLogininfors(userName: string): Promise<string> {
  const res = await request.get<AjaxResult<unknown>>('/system/logininfor/unlock/' + userName);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
