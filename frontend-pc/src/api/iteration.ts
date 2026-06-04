import request from './axios';

export type IterationStatus = number;
export const IterationStatus = {
  PLANNING: 1 as IterationStatus,
  ACTIVE: 2 as IterationStatus,
  COMPLETED: 3 as IterationStatus,
} as const;

export const IterationStatusLabel: Record<IterationStatus, string> = {
  [IterationStatus.PLANNING]: '计划中',
  [IterationStatus.ACTIVE]: '进行中',
  [IterationStatus.COMPLETED]: '已完成',
};

export interface Iteration {
  id?: number;
  iterationId?: number;
  projectId?: number;
  name: string;
  status: IterationStatus;
  startDate?: string;
  endDate?: string;
  goal?: string;
  completedAt?: string;
  taskCount?: number;
  completedTaskCount?: number;
  createTime?: string;
  updateTime?: string;
}

function normalizeIteration(raw: any): Iteration {
  return {
    id: raw.iterationId ?? raw.id,
    iterationId: raw.iterationId,
    projectId: raw.projectId,
    name: raw.name,
    status: raw.status,
    startDate: raw.startDate ?? raw.startTime,
    endDate: raw.endDate ?? raw.endTime,
    goal: raw.goal,
    completedAt: raw.completedAt,
    taskCount: raw.taskCount,
    completedTaskCount: raw.completedTaskCount,
    createTime: raw.createTime,
    updateTime: raw.updateTime,
  };
}

export const iterationApi = {
  list(projectId: number, params?: { page?: number; size?: number }) {
    return request.get<any, { code: number; data: { records: any[]; total: number } }>(
      `/project/iteration/${projectId}/list`,
      { params }
    ).then(res => ({
      ...res,
      data: {
        ...res.data,
        records: (res.data.records || []).map(normalizeIteration),
      },
    })) as Promise<{ code: number; data: { records: Iteration[]; total: number } }>;
  },
  getById(id: number) {
    return request.get<any, { code: number; data: any }>(`/project/iteration/${id}`)
      .then(res => ({
        ...res,
        data: normalizeIteration(res.data?.data ?? res.data),
      })) as Promise<{ code: number; data: Iteration }>;
  },
  create(projectId: number, data: Partial<Iteration>) {
    return request.post<any, { code: number; data: any }>(
      `/project/iteration/${projectId}`,
      { ...data, projectId }
    ).then(res => ({
      ...res,
      data: normalizeIteration(res.data?.data ?? res.data),
    })) as Promise<{ code: number; data: Iteration }>;
  },
  update(id: number, data: Partial<Iteration>) {
    return request.put<any, { code: number; data: any }>(`/project/iteration/${id}`, data)
      .then(res => ({
        ...res,
        data: normalizeIteration(res.data?.data ?? res.data),
      })) as Promise<{ code: number; data: Iteration }>;
  },
  delete(id: number) {
    return request.delete(`/project/iteration/${id}`);
  },
  start(id: number) {
    return request.put(`/project/iteration/${id}/start`);
  },
  complete(id: number, params?: { includeIncomplete?: boolean; nextIterationId?: number }) {
    return request.put(`/project/iteration/${id}/complete`, params || {});
  },
  getTasks(iterationId: number) {
    return request.get<any, { code: number; data: any[] }>(`/project/iteration/${iterationId}/task`);
  },
};
