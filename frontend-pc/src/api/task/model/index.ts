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

// ===== V3.0 任务增强 =====

/** 任务链接类型 */
export interface IssueLinkType {
  linkTypeId?: number;
  linkKey?: string;
  name?: string;
  inward?: string;
  outward?: string;
  isSystem?: number;
  isActive?: number;
}

/** 任务链接 */
export interface IssueLink {
  linkId?: number;
  sourceTaskId?: number;
  targetTaskId?: number;
  linkTypeId?: number;
  createdBy?: number;
  createdAt?: string;
  linkKey?: string;
  linkTypeName?: string;
  inward?: string;
  outward?: string;
  sourceTaskTitle?: string;
  sourceTaskStatus?: string;
  targetTaskTitle?: string;
  targetTaskStatus?: string;
  creatorName?: string;
}

/** 任务附件 */
export interface Attachment {
  attachmentId?: number;
  taskId?: number;
  fileName?: string;
  fileSize?: number;
  filePath?: string;
  fileType?: string;
  thumbnailPath?: string;
  uploadedBy?: number;
  uploadedAt?: string;
  version?: number;
  parentId?: number;
  isDeleted?: number;
  deletedAt?: string;
  deletedBy?: number;
  uploaderName?: string;
}

/** 任务投票 */
export interface TaskVote {
  voteId?: number;
  taskId?: number;
  userId?: number;
  createdAt?: string;
  userName?: string;
}

/** 自动化规则 */
export interface AutomationRule {
  ruleId?: number;
  projectId?: number;
  name?: string;
  description?: string;
  triggerType?: string;
  triggerConfig?: string;
  conditions?: string;
  actions?: string;
  isEnabled?: number;
  createdBy?: number;
  createdAt?: string;
  updatedAt?: string;
  projectName?: string;
  creatorName?: string;
}

/** 自动化执行日志 */
export interface AutomationExecutionLog {
  logId?: number;
  ruleId?: number;
  triggerEvent?: string;
  taskId?: number;
  executedBy?: string;
  actionsExecuted?: string;
  status?: 'success' | 'failed' | 'skipped';
  errorMessage?: string;
  executionTimeMs?: number;
  executedAt?: string;
  ruleName?: string;
  taskTitle?: string;
}

/** 看板配置 */
export interface Board {
  boardId?: number;
  projectId?: number;
  name?: string;
  boardType?: string;
  isDefault?: number;
  configuration?: string;
  createdBy?: number;
  createdAt?: string;
  updatedAt?: string;
}

/** 看板列配置 */
export interface BoardColumnConfig {
  columnId?: number;
  boardId?: number;
  name?: string;
  statusMapping?: string;
  wipLimit?: number;
  color?: string;
  sortOrder?: number;
}

/** 泳道配置 */
export interface BoardSwimlane {
  swimlaneId?: number;
  boardId?: number;
  groupBy?: string;
  customFieldId?: number;
  isCollapsed?: number;
  sortOrder?: number;
}

/** 燃尽图数据 */
export interface SprintBurndown {
  id?: number;
  iterationId?: number;
  recordDate?: string;
  totalPoints?: number;
  remainingPoints?: number;
  totalCount?: number;
  remainingCount?: number;
  totalHours?: number;
  remainingHours?: number;
}

/** 冲刺速度 */
export interface SprintVelocity {
  id?: number;
  iterationId?: number;
  committedPoints?: number;
  completedPoints?: number;
  committedCount?: number;
  completedCount?: number;
  velocity?: number;
}

/** 团队容量 */
export interface TeamCapacity {
  capacityId?: number;
  iterationId?: number;
  userId?: number;
  hoursPerDay?: number;
  workingDays?: number;
  totalHours?: number;
  userName?: string;
}

/** 周期时间记录 */
export interface CycleTimeRecord {
  id?: number;
  taskId?: number;
  projectId?: number;
  startedAt?: string;
  completedAt?: string;
  cycleTimeHours?: number;
  leadTimeHours?: number;
  recordDate?: string;
  taskTitle?: string;
}

/** 周期时间统计摘要 */
export interface CycleTimeSummary {
  averageCycleTime: number;
  medianCycleTime: number;
  stdDev: number;
  p85CycleTime: number;
  totalRecords: number;
}

/** 史诗进度 */
export interface EpicProgress {
  taskId: number;
  taskTitle: string;
  totalChildren: number;
  completedChildren: number;
  inProgressChildren: number;
  totalPoints: number;
  completedPoints: number;
  progressPercent: number;
}
