import request from '@/utils/request';
import type { AjaxResult } from '@/api';
import type { ActivityItem, MemberItem } from './model';

export type { ActivityItem, MemberItem } from './model';

/**
 * 最新动态 - 近期操作日志
 * GET /system/dashboard/activities
 */
export async function getDashboardActivities(limit = 15): Promise<ActivityItem[]> {
  const res = await request.get<AjaxResult<ActivityItem[]>>('/system/dashboard/activities', {
    params: { limit }
  });
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 小组成员 - 用户列表及在线状态
 * GET /system/dashboard/members
 */
export async function getDashboardMembers(): Promise<MemberItem[]> {
  const res = await request.get<AjaxResult<MemberItem[]>>('/system/dashboard/members');
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
