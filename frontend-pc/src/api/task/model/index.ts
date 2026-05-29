import type { PageParam } from '@/api';

/**
 * 任务
 */
export interface Task {
  /** 任务ID */
  id?: number;
  /** 任务标题 */
  title?: string;
  /** 任务描述 */
  description?: string;
  /** 所属项目ID */
  projectId?: number;
  /** 所属迭代ID */
  iterationId?: number;
  /** 父任务ID */
  parentId?: number;
  /** 类型：story / task / bug / improvement */
  taskType?: string;
  /** 优先级：p0 / p1 / p2 / p3 */
  priority?: string;
  /** 状态：todo / in_progress / completed */
  status?: string;
  /** 负责人ID */
  assigneeId?: number;
  /** 负责人名称（非数据库字段） */
  assigneeName?: string;
  /** 截止日期 yyyy-MM-dd */
  dueDate?: string;
  /** 开始时间 yyyy-MM-dd HH:mm:ss */
  startTime?: string;
  /** 预估工时 */
  estimatedHours?: number;
  /** 实际工时 */
  actualHours?: number;
  /** 排序权重 */
  sort?: number;
}

/**
 * 任务查询参数
 */
export interface TaskParam extends PageParam {
  title?: string;
  projectId?: number;
  iterationId?: number;
  assigneeId?: number;
  status?: string;
  priority?: string;
  taskType?: string;
}
