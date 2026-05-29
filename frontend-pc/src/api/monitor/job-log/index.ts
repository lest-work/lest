import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo, PageParam } from '@/api';

export interface JobLog {
  jobLogId?: number;
  jobName?: string;
  jobGroup?: string;
  invokeTarget?: string;
  jobMessage?: string;
  status?: string;
  exceptionInfo?: string;
  createTime?: string;
}

export interface JobLogParam extends PageParam {
  jobName?: string;
  jobGroup?: string;
  status?: string;
  beginTime?: string;
  endTime?: string;
}

/**
 * 分页查询调度日志
 * GET /job/log/list → lest-job SysJobLogController GET /job/log/list
 */
export async function pageJobLogs(params?: JobLogParam): Promise<TableDataInfo<JobLog>> {
  const res = await request.get<TableDataInfo<JobLog>>('/job/log/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeJobLogs(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/job/log/' + ids.join());
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function exportJobLogs(params?: JobLogParam) {
  const res = await request({ url: '/job/log/export', method: 'POST', data: toFormData(params), responseType: 'blob' });
  await checkDownloadRes(res);
  download(res.data, `job_log_${Date.now()}.xlsx`);
}

export async function clearJoblogs(): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/job/log/clean');
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
