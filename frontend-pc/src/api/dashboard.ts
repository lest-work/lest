import request from './axios';

export interface DashboardMember {
  id: number;
  userName: string;
  name: string;
  avatar?: string;
  email?: string;
  status: 0 | 1; // 0=在线, 1=离线
}

export const dashboardApi = {
  members() {
    return request.get<any, { code: number; data: DashboardMember[] }>('/dashboard/members');
  },
};
