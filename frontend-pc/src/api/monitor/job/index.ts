import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo, PageParam } from '@/api';

export interface Job {
  jobId?: number;
  jobName?: string;
  jobGroup?: string;
  invokeTarget?: string;
  cronExpression?: string;
  misfirePolicy?: string;
  concurrent?: string;
  status?: string;
  remark?: string;
  createTime?: string;
  nextValidTime?: string;
}

export interface JobParam extends PageParam {
  jobName?: string;
  jobGroup?: string;
  status?: string;
}

/**
 * 分页查询定时任务
 * GET /job/list → lest-job SysJobController GET /job/list
 */
export async function pageJobs(params?: JobParam): Promise<TableDataInfo<Job>> {
  const res = await request.get<TableDataInfo<Job>>('/job/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function addJob(data: Job): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/job', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function updateJob(data: Job): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/job', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeJob(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/job/' + id);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeJobs(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/job/' + ids.join());
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function updateJobStatus(jobId: number, status: string): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/job/changeStatus', { jobId, status });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function exportJobs(params?: JobParam) {
  const res = await request({ url: '/job/export', method: 'POST', data: toFormData(params), responseType: 'blob' });
  await checkDownloadRes(res);
  download(res.data, `job_${Date.now()}.xlsx`);
}

export async function runJob(jobId: number, jobGroup: string): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/job/run', { jobId, jobGroup });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
