import request from '@/utils/request';
import type { AjaxResult, TableDataInfo, PageParam } from '@/api';

export interface Notice {
  noticeId?: number;
  noticeTitle?: string;
  noticeType?: string;
  noticeContent?: string;
  status?: string;
  remark?: string;
  createBy?: string;
  createTime?: string;
  /** 当前用户是否已读 */
  isRead?: boolean;
}

export interface NoticeTopResult {
  code: number;
  msg: string;
  data?: Notice[];
  /** 未读数量 */
  unreadCount?: number;
}

export interface NoticeParam extends PageParam {
  noticeTitle?: string;
  noticeType?: string;
  createBy?: string;
}

/**
 * 获取单个公告详情
 * GET /system/notice/{noticeId}
 */
export async function getNotice(id: number): Promise<Notice> {
  const res = await request.get<AjaxResult<Notice>>('/system/notice/' + id);
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 获取首页顶部公告列表（带当前用户已读标记）
 * GET /system/notice/listTop
 * 返回格式: { code, msg, data: Notice[], unreadCount: number }
 */
export async function listTop(): Promise<NoticeTopResult> {
  const res = await request.get<NoticeTopResult>('/system/notice/listTop');
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 标记公告已读
 * POST /system/notice/markRead?noticeId=
 */
export async function markRead(noticeId: number): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/notice/markRead', null, { params: { noticeId } });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 批量标记已读
 * POST /system/notice/markReadAll?ids=逗号分隔id
 */
export async function markReadAll(ids: string): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/notice/markReadAll', null, { params: { ids } });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function pageNotices(params?: NoticeParam): Promise<TableDataInfo<Notice>> {
  const res = await request.get<TableDataInfo<Notice>>('/system/notice/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function addNotice(data: Notice): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/notice', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function updateNotice(data: Notice): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/notice', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeNotice(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/notice/' + id);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export async function removeNotices(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/notice/' + ids.join());
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
