import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo, PageParam } from '@/api';

export interface Operlog {
  operId?: number;
  title?: string;
  businessType?: number;
  method?: string;
  requestMethod?: string;
  operatorType?: number;
  operName?: string;
  deptName?: string;
  operUrl?: string;
  operIp?: string;
  operLocation?: string;
  operParam?: string;
  jsonResult?: string;
  status?: number;
  errorMsg?: string;
  operTime?: string;
  costTime?: number;
}

export interface OperlogParam extends PageParam {
  title?: string;
  operName?: string;
  businessType?: number;
  status?: number;
  beginTime?: string;
  endTime?: string;
}

/**
 * 分页查询操作日志
 * GET /system/operlog/list → lest-system SysOperlogController
 */
export async function pageOperlogs(params?: OperlogParam): Promise<TableDataInfo<Operlog>> {
  const res = await request.get<TableDataInfo<Operlog>>('/system/operlog/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 导出操作日志
 */
export async function exportOperlogs(params?: OperlogParam) {
  const res = await request({ url: '/system/operlog/export', method: 'POST', data: toFormData(params), responseType: 'blob' });
  await checkDownloadRes(res);
  download(res.data, `operlog_${Date.now()}.xlsx`);
}

/**
 * 批量删除操作日志
 */
export async function removeOperlogs(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/operlog/' + ids.join());
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 清空操作日志
 */
export async function clearOperlogs(): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/operlog/clean');
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
