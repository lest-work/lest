import request from './axios';

export interface SysJob {
  jobId?: number;
  jobName: string;
  jobGroup: string;
  invokeTarget: string;
  cronExpression: string;
  misfirePolicy: string;
  status: string;  // '0'=正常, '1'=暂停
  createTime?: string;
  updateTime?: string;
  description?: string;
}

export const jobApi = {
  list(params?: { jobName?: string; jobGroup?: string; status?: string }) {
    return request.get<any, { code: number; data: { records: SysJob[]; total: number } }>('/job/list', { params });
  },
  getById(jobId: number) {
    return request.get<any, { code: number; data: SysJob }>(`/job/${jobId}`);
  },
  create(data: Partial<SysJob>) {
    return request.post<any, { code: number }>('/job', data);
  },
  update(data: Partial<SysJob>) {
    return request.put<any, { code: number }>('/job', data);
  },
  changeStatus(jobId: number, status: string) {
    return request.put<any, { code: number }>('/job/changeStatus', { jobId, status });
  },
  run(jobId: number) {
    return request.put<any, { code: number }>('/job/run', { jobId });
  },
  delete(jobIds: number[]) {
    return request.delete<any, { code: number }>(`/job/${jobIds.join(',')}`);
  },
};
