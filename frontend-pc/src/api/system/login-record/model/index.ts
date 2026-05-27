import { PageParam } from '@/api';

/**
 * 登录日志
 */
export interface LoginRecord {
  /** 登录日志id */
  id: number;
  /** 用户id */
  userId?: number;
  /** 用户账号 */
  username: string;
  /** 用户昵称 */
  nickname: string;
  /** 登录类型, 0-登录成功, 1-登录失败, 2-退出登录, 3-刷新Token */
  loginType: number;
  /** IP地址 */
  ipAddress: string;
  /** 操作系统 */
  os: string;
  /** 设备名称 */
  device: string;
  /** 浏览器类型 */
  browser: string;
  /** 用户代理 */
  userAgent?: string;
  /** 状态, 0-失败, 1-成功 */
  status: number;
  /** 消息 */
  msg?: string;
  /** 操作时间 */
  createdAt: string;
}

/**
 * 登录日志搜索条件
 */
export interface LoginRecordParam extends PageParam {
  /** 用户账号 */
  username?: string;
  /** 用户昵称 */
  nickname?: string;
  /** 登录类型, 0-登录成功, 1-登录失败, 2-退出登录, 3-刷新Token */
  loginType?: number;
  /** 开始时间 */
  createTimeStart?: string;
  /** 截止时间 */
  createTimeEnd?: string;
}
