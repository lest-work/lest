import request from '@/utils/request';
import type { ApiResult } from '@/api';
import type {
  ActivityOverview,
  ActivityTrendPoint,
  ActivityRankItem
} from './model';

/* ==================== Activity Overview ==================== */

/** 获取活跃度概览 */
export async function getActivityOverview(params?: {
  range?: string;
  start?: string;
  end?: string;
}) {
  const res = await request.get<ApiResult<ActivityOverview>>('/wakapi/activity/overview', {
    params
  });
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取活跃度排名 */
export async function getActivityRanking(params?: {
  range?: string;
  limit?: number;
  start?: string;
  end?: string;
}) {
  const res = await request.get<ApiResult<ActivityRankItem[]>>('/wakapi/activity/ranking', {
    params
  });
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取活跃度趋势 */
export async function getActivityTrends(params?: {
  days?: number;
  start?: string;
  end?: string;
}) {
  const res = await request.get<ApiResult<ActivityTrendPoint[]>>('/wakapi/activity/trends', {
    params
  });
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}
