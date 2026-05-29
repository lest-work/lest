import request from '@/utils/request';
import type { AjaxResult, TableDataInfo } from '@/api';

export interface OnlineUser {
  tokenId?: string;
  userId?: number;
  userName?: string;
  deptName?: string;
  ipaddr?: string;
  loginLocation?: string;
  browser?: string;
  os?: string;
  loginTime?: string;
}

export interface OnlineParam {
  ipaddr?: string;
  userName?: string;
}

/**
 * 分页查询在线用户
 * GET /system/online/list → lest-system SysUserOnlineController GET /online/list
 */
export async function pageOnlines(params?: OnlineParam): Promise<TableDataInfo<OnlineUser>> {
  const res = await request.get<TableDataInfo<OnlineUser>>('/system/online/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 强退在线用户
 * DELETE /system/online/{tokenId} → lest-system SysUserOnlineController DELETE /online/{tokenId}
 */
export async function kickoutOnline(tokenId: string): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/online/' + tokenId);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
