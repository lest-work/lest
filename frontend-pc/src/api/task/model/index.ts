import type { PageParam } from '@/api';

/**
 * 任务
 */
export interface Task {
  /** 任务ID */
  taskId?: number;
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
 * 任务工时记录
 */
export interface TaskWorklog {
  /** 工时记录ID */
  taskWorklogId?: number;
  /** 任务ID */
  taskId?: number;
  /** 记录人用户ID */
  userId?: number;
  /** 工时（小时） */
  hours?: number;
  /** 工作日期 yyyy-MM-dd */
  workDate?: string;
  /** 工作内容描述 */
  description?: string;
  /** 创建时间 */
  createTime?: string;
}

/**
 * 任务评论
 */
export interface TaskComment {
  /** 评论ID */
  taskCommentId?: number;
  /** 任务ID */
  taskId?: number;
  /** 评论人用户ID */
  userId?: number;
  /** 评论内容 */
  content?: string;
  /** 父评论ID（回复） */
  parentId?: number;
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
}

/**
 * 标签
 */
export interface Label {
  /** 标签ID */
  labelId?: number;
  /** 所属项目ID */
  projectId?: number;
  /** 标签名称 */
  name?: string;
  /** 标签颜色 */
  color?: string;
}

/**
 * 看板列
 */
export interface BoardColumn {
  status: string;
  title: string;
  tasks: Task[];
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
