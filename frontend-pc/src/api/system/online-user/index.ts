import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/api';
import type { OnlineUser, OnlineUserParam } from './model';

/**
 * 分页查询在线用户
 */
export async function pageOnlineUsers(params: OnlineUserParam) {
  const res = await request.get<ApiResult<PageResult<OnlineUser>>>(
    '/system/online-user/page',
    { params }
  );
  if (res.data.code === 200) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询在线用户列表
 */
export async function listOnlineUsers() {
  const res = await request.get<ApiResult<OnlineUser[]>>(
    '/system/online-user/list'
  );
  if (res.data.code === 200) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取在线用户数量
 */
export async function getOnlineUserCount() {
  const res = await request.get<ApiResult<number>>(
    '/system/online-user/count'
  );
  if (res.data.code === 200) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 踢出在线用户（单个或批量）
 * @param sessionIds 会话ID列表，为空数组时踢出所有
 */
export async function kickoutOnlineUsers(sessionIds?: string[]) {
  const params: Record<string, any> = {};
  if (sessionIds && sessionIds.length > 0) {
    params.sessionIds = sessionIds.join(',');
  }
  const res = await request.post<ApiResult<void>>(
    '/system/online-user/kickout',
    { sessionIds: sessionIds || [] }
  );
  if (res.data.code === 200) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
