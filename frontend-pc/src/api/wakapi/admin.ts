import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/api';
import type {
  WakapiUser,
  WakapiUserParam,
  ScheduledTask,
  ScheduledTaskLog
} from './model';

/* ==================== WakAPI 用户管理 ==================== */

/** 分页查询 WakAPI 用户 */
export async function pageWakapiUsers(params: WakapiUserParam & { page?: number; limit?: number }) {
  const res = await request.get<ApiResult<PageResult<WakapiUser>>>(
    '/wakapi/admin/users/page',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 查询 WakAPI 用户列表 */
export async function listWakapiUsers(params?: WakapiUserParam) {
  const res = await request.get<ApiResult<WakapiUser[]>>('/wakapi/admin/users', { params });
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 根据id查询 WakAPI 用户 */
export async function getWakapiUser(userId: string) {
  const res = await request.get<ApiResult<WakapiUser>>('/wakapi/admin/users/' + userId);
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 修改 WakAPI 用户 */
export async function updateWakapiUser(data: WakapiUser) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/admin/users/' + data.userId, data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取用户的编码统计 */
export async function getWakapiUserStats(
  userId: string,
  params?: { range?: string; start?: string; end?: string }
) {
  const res = await request.get<ApiResult<unknown>>(
    '/wakapi/admin/users/' + userId + '/stats',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取当前用户的 WakAPI Key */
export async function getWakapiUserKey() {
  const res = await request.get<ApiResult<{ apiKey?: string }>>('/wakapi/my/key');
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 重新生成当前用户的 WakAPI Key */
export async function regenerateWakapiKey() {
  const res = await request.post<ApiResult<unknown>>('/wakapi/my/key/regenerate');
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/* ==================== 定时任务管理 ==================== */

/** 获取定时任务列表 */
export async function getScheduledTasks() {
  const res = await request.get<ApiResult<ScheduledTask[]>>('/wakapi/admin/scheduled');
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 手动触发定时任务 */
export async function triggerScheduledTask(taskName: string) {
  const res = await request.post<ApiResult<unknown>>(
    '/wakapi/admin/scheduled/' + taskName + '/trigger'
  );
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取定时任务执行日志 */
export async function getScheduledTaskLogs(params: {
  taskName?: string;
  page?: number;
  limit?: number;
}) {
  const res = await request.get<ApiResult<PageResult<ScheduledTaskLog>>>(
    '/wakapi/admin/scheduled/logs',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}
