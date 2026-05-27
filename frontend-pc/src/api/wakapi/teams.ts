import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/api';
import type {
  Team,
  TeamMember,
  TeamLeaderboardItem,
  TeamRankItem
} from './model';

/* ==================== Team Management ==================== */

/** 获取团队列表 */
export async function getTeams(params?: { page?: number; limit?: number }) {
  const res = await request.get<ApiResult<PageResult<Team>>>('/wakapi/teams', { params });
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取团队详情 */
export async function getTeam(id: number) {
  const res = await request.get<ApiResult<Team>>('/wakapi/teams/' + id);
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 创建团队 */
export async function createTeam(data: Partial<Team>) {
  const res = await request.post<ApiResult<unknown>>('/wakapi/teams', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 更新团队 */
export async function updateTeam(data: Partial<Team>) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/teams/' + data.id, data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 删除团队 */
export async function deleteTeam(id: number) {
  const res = await request.delete<ApiResult<unknown>>('/wakapi/teams/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/* ==================== Team Members ==================== */

/** 获取团队成员列表 */
export async function getTeamMembers(teamId: number) {
  const res = await request.get<ApiResult<TeamMember[]>>('/wakapi/teams/' + teamId + '/members');
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 添加团队成员 */
export async function addTeamMember(teamId: number, userId: string, role: string = 'member') {
  const res = await request.post<ApiResult<unknown>>(
    '/wakapi/teams/' + teamId + '/members',
    { userId, role }
  );
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 移除团队成员 */
export async function removeTeamMember(teamId: number, userId: string) {
  const res = await request.delete<ApiResult<unknown>>(
    '/wakapi/teams/' + teamId + '/members/' + userId
  );
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 更新成员角色 */
export async function updateTeamMemberRole(teamId: number, userId: string, role: string) {
  const res = await request.put<ApiResult<unknown>>(
    '/wakapi/teams/' + teamId + '/members/' + userId,
    { role }
  );
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/* ==================== Join / Leave ==================== */

/** 通过邀请码加入团队 */
export async function joinTeam(inviteCode: string) {
  const res = await request.post<ApiResult<unknown>>('/wakapi/teams/join', { inviteCode });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 离开团队 */
export async function leaveTeam(teamId: number) {
  const res = await request.post<ApiResult<unknown>>('/wakapi/teams/' + teamId + '/leave');
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取团队排行榜 */
export async function getTeamLeaderboard(
  teamId: number,
  params?: { range?: string; limit?: number }
) {
  const res = await request.get<ApiResult<TeamLeaderboardItem[]>>(
    '/wakapi/teams/' + teamId + '/leaderboard',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取全平台团队排行榜 */
export async function getAllTeamLeaderboard(params?: {
  range?: string;
  limit?: number;
  page?: number;
  order?: string;
}) {
  const res = await request.get<ApiResult<PageResult<TeamRankItem>>>(
    '/wakapi/teams/leaderboard',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}
