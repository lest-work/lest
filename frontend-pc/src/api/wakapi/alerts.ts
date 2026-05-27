import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/api';
import type {
  AlertRule,
  AlertHistory,
  AlertRuleType,
  AlertStatus
} from './model';

/* ==================== Alert Rules ==================== */

/** 分页查询告警规则 */
export async function pageAlertRules(params?: {
  name?: string;
  type?: AlertRuleType;
  enabled?: boolean;
  page?: number;
  limit?: number;
}) {
  const res = await request.get<ApiResult<PageResult<AlertRule>>>(
    '/wakapi/alerts/rules/page',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 查询告警规则列表 */
export async function listAlertRules(params?: {
  enabled?: boolean;
  type?: AlertRuleType;
}) {
  const res = await request.get<ApiResult<AlertRule[]>>('/wakapi/alerts/rules', { params });
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取规则详情 */
export async function getAlertRule(id: number) {
  const res = await request.get<ApiResult<AlertRule>>('/wakapi/alerts/rules/' + id);
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 创建告警规则 */
export async function createAlertRule(data: Partial<AlertRule>) {
  const res = await request.post<ApiResult<unknown>>('/wakapi/alerts/rules', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 更新告警规则 */
export async function updateAlertRule(data: Partial<AlertRule>) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/alerts/rules/' + data.id, data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 删除告警规则 */
export async function deleteAlertRule(id: number) {
  const res = await request.delete<ApiResult<unknown>>('/wakapi/alerts/rules/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 启用/禁用告警规则 */
export async function toggleAlertRule(id: number, enabled: boolean) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/alerts/rules/' + id + '/toggle', {
    enabled
  });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/* ==================== Alert History ==================== */

/** 分页查询告警历史 */
export async function pageAlertHistory(params?: {
  ruleId?: number;
  userId?: string;
  status?: AlertStatus;
  startTime?: string;
  endTime?: string;
  page?: number;
  limit?: number;
}) {
  const res = await request.get<ApiResult<PageResult<AlertHistory>>>(
    '/wakapi/alerts/history/page',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 获取告警历史详情 */
export async function getAlertHistory(id: number) {
  const res = await request.get<ApiResult<AlertHistory>>('/wakapi/alerts/history/' + id);
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 确认告警 */
export async function acknowledgeAlert(id: number) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/alerts/history/' + id + '/acknowledge');
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 解决告警 */
export async function resolveAlert(id: number, message?: string) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/alerts/history/' + id + '/resolve', {
    message
  });
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 忽略告警 */
export async function ignoreAlert(id: number) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/alerts/history/' + id + '/ignore');
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
