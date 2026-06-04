import request from './axios';

export interface Project {
  id?: number;
  projectId?: number;
  projectKey?: string;
  name: string;
  description?: string;
  status: number | string;
  template?: string;
  ownerId?: number;
  ownerName?: string;
  startDate?: string;
  endDate?: string;
  memberCount?: number;
  taskCount?: number;
  createdAt?: string;
  createTime?: string;
  updatedAt?: string;
  color?: string;
}

export interface ProjectMember {
  projectId: number;
  userId: number;
  userName?: string;
  nickname?: string;
  avatar?: string;
  role: string;
  joinedAt?: string;
}

function normalizeProject(raw: any): Project {
  return {
    id: raw.projectId ?? raw.id,
    projectId: raw.projectId,
    projectKey: raw.projectKey,
    name: raw.name,
    description: raw.description,
    status: raw.status,
    template: raw.template,
    ownerId: raw.ownerId,
    ownerName: raw.ownerName,
    startDate: raw.startDate,
    endDate: raw.endDate,
    memberCount: raw.memberCount,
    taskCount: raw.taskCount,
    createdAt: raw.createTime || raw.createdAt,
    createTime: raw.createTime,
    updatedAt: raw.updateTime || raw.updatedAt,
  };
}

export const projectApi = {
  list(params?: { page?: number; size?: number; status?: string }) {
    return request.get<any, { code: number; data: { records: any[]; total: number } }>(
      '/project/list',
      { params }
    ).then(res => ({
      ...res,
      data: {
        ...res.data,
        records: (res.data.records || []).map(normalizeProject),
      },
    })) as Promise<{ code: number; data: { records: Project[]; total: number } }>;
  },
  getById(id: number) {
    return request.get<any, { code: number; data: any }>(`/project/${id}`)
      .then(res => ({
        ...res,
        data: normalizeProject(res.data?.data ?? res.data),
      })) as Promise<{ code: number; data: Project }>;
  },
  create(data: Partial<Project>) {
    return request.post<any, { code: number; data: any }>('/project', data)
      .then(res => ({
        ...res,
        data: normalizeProject(res.data?.data ?? res.data),
      })) as Promise<{ code: number; data: Project }>;
  },
  update(id: number, data: Partial<Project>) {
    return request.put<any, { code: number; data: any }>(`/project/${id}`, { ...data })
      .then(res => ({
        ...res,
        data: normalizeProject(res.data?.data ?? res.data),
      })) as Promise<{ code: number; data: Project }>;
  },
  delete(id: number) {
    return request.delete(`/project/${id}`);
  },
  archive(id: number) {
    return request.put(`/project/${id}/archive`);
  },
  unarchive(id: number) {
    return request.put(`/project/${id}/unarchive`);
  },
  members(projectId: number) {
    return request.get<any, { code: number; data: ProjectMember[] }>(
      `/project/${projectId}/members`
    );
  },
  addMember(projectId: number, data: { userId: number; role: string }) {
    return request.post(`/project/${projectId}/members`, data);
  },
  inviteMemberByEmail(projectId: number, data: { email: string; role: string }) {
    return request.post(`/project/${projectId}/members/invite-by-email`, data);
  },
  createInviteLink(projectId: number, data: { email: string; role: string }) {
    return request.post<any, { code: number; data: { inviteUrl: string; token: string } }>(
      `/project/${projectId}/members/invite-link`,
      data,
    );
  },
  acceptInvite(token: string) {
    return request.post(`/project/invites/${token}/accept`);
  },
  removeMember(projectId: number, userId: number) {
    return request.delete(`/project/${projectId}/members/${userId}`);
  },
  updateMemberRole(projectId: number, userId: number, role: string) {
    return request.put(`/project/${projectId}/members/${userId}/role`, { role });
  },
};
