import type { PageParam } from '@/api';

/**
 * 项目
 */
export interface Project {
  /** 项目ID */
  projectId?: number;
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
  projectId?: number;
  userId?: number;
  role?: string;
  userName?: string;
  nickName?: string;
  joinedAt?: string;
}

/**
 * 迭代
 */
export interface Iteration {
  /** 迭代ID */
  iterationId?: number;
  /** 所属项目ID */
  projectId?: number;
  /** 迭代名称 */
  name?: string;
  /** 迭代目标 */
  goal?: string;
  /** 状态：1=计划中，2=进行中，3=已完成 */
  status?: number;
  /** 开始日期 yyyy-MM-dd */
  startDate?: string;
  /** 结束日期 yyyy-MM-dd */
  endDate?: string;
  /** 完成时间 yyyy-MM-dd HH:mm:ss */
  completedAt?: string;
}

/**
 * 里程碑
 */
export interface Milestone {
  /** 里程碑ID */
  milestoneId?: number;
  /** 所属项目ID */
  projectId?: number;
  /** 里程碑名称 */
  name?: string;
  /** 里程碑描述 */
  description?: string;
  /** 目标日期 yyyy-MM-dd */
  targetDate?: string;
  /** 关联迭代列表（非数据库字段） */
  iterations?: Iteration[];
}

/**
 * 项目查询参数
 */
export interface ProjectParam extends PageParam {
  name?: string;
  status?: number;
  ownerId?: number;
}

/**
 * 迭代查询参数
 */
export interface IterationParam extends PageParam {
  projectId?: number;
  status?: number;
}
