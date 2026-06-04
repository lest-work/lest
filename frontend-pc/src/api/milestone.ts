import request from './axios';

export interface Milestone {
  id?: number;
  milestoneId?: number;
  projectId?: number;
  name: string;
  description?: string;
  targetDate?: string;
  dueDate?: string;
  completedAt?: string;
  taskCount?: number;
  createTime?: string;
  updateTime?: string;
}

function normalizeMilestone(raw: any): Milestone {
  return {
    id: raw.milestoneId ?? raw.id,
    milestoneId: raw.milestoneId,
    projectId: raw.projectId,
    name: raw.name,
    description: raw.description,
    targetDate: raw.targetDate,
    dueDate: raw.targetDate ?? raw.dueDate,
    completedAt: raw.completedAt,
    taskCount: raw.taskCount,
    createTime: raw.createTime,
    updateTime: raw.updateTime,
  };
}

export const milestoneApi = {
  list(projectId: number, params?: { page?: number; size?: number }) {
    return request.get<any, { code: number; data: { records: any[]; total: number } }>(
      `/project/milestone/${projectId}/list`,
      { params }
    ).then(res => ({
      ...res,
      data: {
        ...res.data,
        records: (res.data.records || []).map(normalizeMilestone),
      },
    })) as Promise<{ code: number; data: { records: Milestone[]; total: number } }>;
  },
  getById(id: number) {
    return request.get<any, { code: number; data: any }>(`/project/milestone/${id}`)
      .then(res => ({
        ...res,
        data: normalizeMilestone(res.data?.data ?? res.data),
      })) as Promise<{ code: number; data: Milestone }>;
  },
  create(projectId: number, data: Partial<Milestone>) {
    return request.post<any, { code: number; data: any }>(
      `/project/milestone/${projectId}`,
      { ...data, projectId }
    ).then(res => ({
      ...res,
      data: normalizeMilestone(res.data?.data ?? res.data),
    })) as Promise<{ code: number; data: Milestone }>;
  },
  update(id: number, data: Partial<Milestone>) {
    return request.put<any, { code: number; data: any }>(`/project/milestone/${id}`, data)
      .then(res => ({
        ...res,
        data: normalizeMilestone(res.data?.data ?? res.data),
      })) as Promise<{ code: number; data: Milestone }>;
  },
  delete(id: number) {
    return request.delete(`/project/milestone/${id}`);
  },
  iterationList(milestoneId: number) {
    return request.get<any, { code: number; data: any[] }>(`/project/milestone/${milestoneId}/iteration/list`);
  },
};
