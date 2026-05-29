import request from '@/utils/request';
import type { AjaxResult } from '@/api';

export interface ServerInfo {
  cpu?: Record<string, any>;
  mem?: Record<string, any>;
  jvm?: Record<string, any>;
  sys?: Record<string, any>;
  sysFiles?: any[];
}

/**
 * 查询服务器信息
 * NOTE: 网关无 /monitor/** 路由，此接口暂无后端实现
 */
export async function getServer(): Promise<ServerInfo> {
  const res = await request.get<AjaxResult<ServerInfo>>('/monitor/server');
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg));
}
