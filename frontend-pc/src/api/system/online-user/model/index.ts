import { PageParam } from '@/api';

/**
 * 在线用户
 */
export interface OnlineUser {
  /** 会话ID */
  sessionId: string;
  /** 用户ID */
  userId?: number;
  /** 用户账号 */
  username: string;
  /** 用户昵称 */
  nickname: string;
  /** IP地址 */
  ipAddress: string;
  /** 操作系统 */
  os?: string;
  /** 浏览器 */
  browser?: string;
  /** 设备 */
  device?: string;
  /** 登录时间 */
  loginTime?: string;
  /** 最后访问时间 */
  lastAccessTime?: string;
}

/**
 * 在线用户搜索参数
 */
export interface OnlineUserParam extends PageParam {
  /** 用户账号 */
  username?: string;
  /** 用户昵称 */
  nickname?: string;
}
