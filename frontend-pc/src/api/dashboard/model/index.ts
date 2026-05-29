/**
 * 最新动态条目（来源：sys_oper_log）
 */
export interface ActivityItem {
  /** 日志ID */
  id: number;
  /** 操作人员 */
  operName: string;
  /** 操作模块 */
  title: string;
  /** 业务类型：0其它 1新增 2修改 3删除 4授权 5导出 6导入 7强退 8生成代码 9清空 */
  businessType: number;
  /** 操作状态：0正常 1异常 */
  status: number;
  /** 操作时间 yyyy-MM-dd HH:mm:ss */
  operTime: string;
  /** 请求URL */
  operUrl: string;
}

/**
 * 小组成员条目（来源：sys_user + Redis 在线检测）
 */
export interface MemberItem {
  /** 用户ID */
  id: number;
  /** 登录账号 */
  userName: string;
  /** 显示名称（昵称优先） */
  name: string;
  /** 头像URL */
  avatar: string | null;
  /** 邮箱 */
  email: string;
  /** 在线状态：0=在线 1=离线 */
  status: number;
}
