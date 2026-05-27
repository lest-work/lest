import request from '@/utils/request';
import type { ApiResult } from '@/api';
import type {
  WakapiStatsResponse,
  WakapiSummariesResponse,
  WakapiAllTimeResponse,
  WakapiProjectResponse,
  WakapiHeartbeatsResponse,
  WakapiBadgeResponse,
  WakapiLeadersResponse,
  WakapiSummariesData
} from './model';

/** 获取编码统计数据 */
export async function getWakapiStats(params?: {
  range?: string;
  project?: string;
  language?: string;
  editor?: string;
  operating_system?: string;
  machine?: string;
  label?: string;
}) {
  const res = await request.get<ApiResult<WakapiStatsResponse>>(
    '/compat/wakatime/v1/users/current/stats',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取每日汇总列表 */
export async function getWakapiSummaries(params?: {
  range?: string;
  start?: string;
  end?: string;
  timezone?: string;
  project?: string;
  language?: string;
  editor?: string;
  operating_system?: string;
  machine?: string;
  label?: string;
}): Promise<WakapiSummariesResponse> {
  const res = await request.get<ApiResult<WakapiSummariesResponse>>(
    '/compat/wakatime/v1/users/current/summaries',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取全部编码时长 */
export async function getWakapiAllTime() {
  const res = await request.get<ApiResult<WakapiAllTimeResponse>>(
    '/compat/wakatime/v1/users/current/all_time_since_today'
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取项目列表 */
export async function getWakapiProjects(params?: { search?: string }) {
  const res = await request.get<ApiResult<WakapiProjectResponse>>(
    '/compat/wakatime/v1/users/current/projects',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data.projects;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取心跳记录 */
export async function getWakapiHeartbeats(params: { date: string }) {
  const res = await request.get<ApiResult<WakapiHeartbeatsResponse>>(
    '/compat/wakatime/v1/users/current/heartbeats',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取 Shield 徽章 */
export async function getWakapiBadge(params: {
  interval?: string;
  filter_type?: string;
  filter_value?: string;
}) {
  const res = await request.get<ApiResult<WakapiBadgeResponse>>(
    '/compat/wakatime/v1_shield',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取排行榜 */
export async function getWakapiLeaders(params?: {
  range?: string;
  limit?: number;
}) {
  const res = await request.get<ApiResult<WakapiLeadersResponse>>(
    '/compat/wakatime/v1/leaders',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 辅助函数：将秒数格式化为可读时长 */
export function formatDuration(seconds: number): string {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  if (hours > 0) {
    return `${hours} hrs ${minutes} mins`;
  }
  return `${minutes} mins`;
}

/** 辅助函数：将秒数格式化为 HH:MM:SS */
export function formatDigital(seconds: number): string {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = Math.floor(seconds % 60);
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}

/** 辅助函数：将秒数转换为天数（按每天8小时计算） */
export function secondsToDays(seconds: number, hoursPerDay = 8): number {
  return Math.round(seconds / (hoursPerDay * 3600) * 10) / 10;
}

/** 辅助函数：获取指定范围的开始/结束日期 */
export function getRangeDates(range: string): { start: string; end: string } {
  const now = new Date();
  const fmt = (d: Date) => d.toISOString().slice(0, 10);

  switch (range) {
    case 'today':
      return { start: fmt(now), end: fmt(now) };
    case 'yesterday': {
      const y = new Date(now);
      y.setDate(y.getDate() - 1);
      return { start: fmt(y), end: fmt(y) };
    }
    case 'last_7_days': {
      const s = new Date(now);
      s.setDate(s.getDate() - 6);
      return { start: fmt(s), end: fmt(now) };
    }
    case 'last_14_days': {
      const s = new Date(now);
      s.setDate(s.getDate() - 13);
      return { start: fmt(s), end: fmt(now) };
    }
    case 'last_30_days': {
      const s = new Date(now);
      s.setDate(s.getDate() - 29);
      return { start: fmt(s), end: fmt(now) };
    }
    case 'this_week': {
      const s = new Date(now);
      s.setDate(s.getDate() - s.getDay() + 1);
      return { start: fmt(s), end: fmt(now) };
    }
    case 'this_month': {
      const s = new Date(now.getFullYear(), now.getMonth(), 1);
      return { start: fmt(s), end: fmt(now) };
    }
    case 'this_year': {
      const s = new Date(now.getFullYear(), 0, 1);
      return { start: fmt(s), end: fmt(now) };
    }
    default:
      return { start: fmt(now), end: fmt(now) };
  }
}
