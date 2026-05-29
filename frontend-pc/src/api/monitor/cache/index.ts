import request from '@/utils/request';
import type { AjaxResult } from '@/api';

export interface CacheInfo {
  info?: Record<string, any>;
  dbSize?: number;
  commandStats?: any[];
}

export interface CacheKey {
  cacheName?: string;
  cacheKey?: string;
  cacheValue?: string;
  remark?: string;
}

/**
 * 查询缓存信息
 * NOTE: 网关无 /monitor/** 路由，此接口暂无后端实现
 */
export async function getCache(): Promise<CacheInfo> {
  const res = await request.get<AjaxResult<CacheInfo>>('/monitor/cache');
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg));
}

export async function getCacheNames(): Promise<CacheKey[]> {
  const res = await request.get<AjaxResult<CacheKey[]>>('/monitor/cache/getNames');
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg));
}

export async function getCacheKeys(name: string): Promise<string[]> {
  const res = await request.get<AjaxResult<string[]>>('/monitor/cache/getKeys/' + name);
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg));
}

export async function getCacheValue(name: string, key: string): Promise<CacheKey> {
  const res = await request.get<AjaxResult<CacheKey>>(`/monitor/cache/getValue/${name}/${key}`);
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg));
}

export async function clearCacheName(name: string): Promise<any> {
  const res = await request.delete<AjaxResult<unknown>>('/monitor/cache/clearCacheName/' + name);
  if (res.data.code === 200) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function clearCacheKey(key: string): Promise<any> {
  const res = await request.delete<AjaxResult<unknown>>('/monitor/cache/clearCacheKey/' + key);
  if (res.data.code === 200) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

export async function clearCacheAll(): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/monitor/cache/clearCacheAll');
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
