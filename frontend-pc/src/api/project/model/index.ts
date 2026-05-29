import type { PageParam } from '@/api';

/**
 * 项目
 */
export interface Project {
  /** 主键ID */
  id?: number;
  /** 项目名称 */
  name?: string;
  /** 项目描述 */
  description?: string;
  /** 状态：1=活跃，2=已归档 */
  status?: number;
  /** 模板：agile / waterfall / kanban */
  template?: string;
  /** 负责人ID */
  ownerId?: number;
  /** 负责人名称（非数据库字段） */
  ownerName?: string;
  /** 开始日期 yyyy-MM-dd */
  startDate?: string;
  /** 结束日期 yyyy-MM-dd */
  endDate?: string;
  /** 进度百分比（前端计算，非数据库字段） */
  progress?: number;
}

/**
 * 项目成员
 */
export interface ProjectMember {
  userId?: number;
  projectId?: number;
  role?: string;
  userName?: string;
  nickName?: string;
}

/**
 * 项目查询参数
 */
export interface ProjectParam extends PageParam {
  name?: string;
  status?: number;
  ownerId?: number;
}
