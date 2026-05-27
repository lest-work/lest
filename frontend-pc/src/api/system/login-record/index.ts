import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/api';
import type { LoginRecord, LoginRecordParam } from './model';

/**
 * 分页查询登录日志
 */
export async function pageLoginRecords(params: LoginRecordParam) {
  const res = await request.get<ApiResult<PageResult<LoginRecord>>>(
    '/system/login-record/page',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 查询登录日志列表（导出用）
 */
export async function listLoginRecords(params?: LoginRecordParam) {
  const res = await request.get<ApiResult<LoginRecord[]>>(
    '/system/login-record',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 获取登录日志详情
 */
export async function getLoginRecord(id: number) {
  const res = await request.get<ApiResult<LoginRecord>>(
    `/system/login-record/${id}`
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/**
 * 删除登录日志（单个或批量）
 * @param id 单个ID
 * @param ids 批量ID列表（传此参数时表示批量删除）
 */
export async function deleteLoginRecord(id: number, ids?: number[]) {
  const params: Record<string, any> = {};
  if (ids && ids.length > 0) {
    params.ids = ids.join(',');
  }
  const res = await request.delete<ApiResult<void>>(
    `/system/login-record/${id}`,
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}
