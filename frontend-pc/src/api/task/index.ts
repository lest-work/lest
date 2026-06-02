import request from '@/utils/request';
import type { AjaxResult, TableDataInfo } from '@/api';
import type {
  Task, TaskParam, TaskWorklog, TaskComment, Label,
  IssueLink, IssueLinkType, Attachment, TaskVote,
  AutomationRule, AutomationExecutionLog
} from './model';

export type {
  Task, TaskParam, TaskWorklog, TaskComment, Label,
  IssueLink, IssueLinkType, Attachment, TaskVote,
  AutomationRule, AutomationExecutionLog
} from './model';
export type { BoardColumn } from './model';

const PRIORITY_ORDER: Record<string, number> = { p0: 1, p1: 2, p2: 3, p3: 4 };

/**
 * 分页查询任务列表
 * GET /task/list → gateway 去掉 task → lest-task /list
 */
export async function pageTasks(params?: TaskParam): Promise<TableDataInfo<Task>> {
  const res = await request.get<TableDataInfo<Task>>('/task/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取任务详情
 */
export async function getTask(id: number): Promise<Task> {
  const res = await request.get<AjaxResult<Task>>(`/task/${id}`);
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 新增任务
 */
export async function addTask(data: Task): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/task', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 修改任务
 */
export async function updateTask(data: Task): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/task', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 删除任务
 */
export async function removeTask(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/${id}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 更新任务状态
 */
export async function updateTaskStatus(id: number, status: string): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/task/${id}/status`, { status });
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 认领任务
 */
export async function claimTask(id: number): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/${id}/claim`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取看板视图（按状态分组）
 */
export async function getBoard(projectId: number, iterationId?: number): Promise<BoardColumn[]> {
  const res = await request.get<AjaxResult<BoardColumn[]>>('/task/board', {
    params: { projectId, iterationId }
  });
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取甘特图数据
 */
export async function getGantt(projectId: number) {
  const res = await request.get<AjaxResult<unknown>>('/task/gantt', { params: { projectId } });
  if (res.data.code === 200) return res.data.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 查询子任务列表
 */
export async function listSubtasks(id: number): Promise<Task[]> {
  const res = await request.get<AjaxResult<Task[]>>(`/task/${id}/subtask`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 查询工时记录
 */
export async function listWorklogs(taskId: number): Promise<TaskWorklog[]> {
  const res = await request.get<AjaxResult<TaskWorklog[]>>(`/task/${taskId}/worklog/list`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 新增工时记录
 */
export async function addWorklog(taskId: number, data: TaskWorklog): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/${taskId}/worklog`, data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 查询任务评论列表
 */
export async function listComments(taskId: number): Promise<TaskComment[]> {
  const res = await request.get<AjaxResult<TaskComment[]>>(`/task/${taskId}/comment/list`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 新增任务评论
 */
export async function addComment(taskId: number, data: TaskComment): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/${taskId}/comment`, data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 删除任务评论
 */
export async function removeComment(taskId: number, commentId: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/${taskId}/comment/${commentId}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 查询标签列表
 */
export async function listLabels(projectId?: number): Promise<Label[]> {
  const res = await request.get<AjaxResult<Label[]>>(`/project/${projectId}/label/list`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 新增标签
 */
export async function addLabel(projectId: number, data: Label): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/project/${projectId}/label`, data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 删除标签
 */
export async function removeLabel(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/label/${id}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export { PRIORITY_ORDER };

// ===== V3.0 任务增强 =====

/**
 * 获取任务链接类型列表
 */
export async function getIssueLinkTypes(): Promise<IssueLinkType[]> {
  const res = await request.get<AjaxResult<IssueLinkType[]>>('/task/link/types');
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取任务链接列表
 */
export async function getIssueLinks(taskId: number): Promise<IssueLink[]> {
  const res = await request.get<AjaxResult<IssueLink[]>>(`/task/link/list/${taskId}`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 创建任务链接
 */
export async function addIssueLink(taskId: number, data: IssueLink): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/link/${taskId}`, data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 删除任务链接
 */
export async function removeIssueLink(linkId: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/link/${linkId}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取任务附件列表
 */
export async function getAttachments(taskId: number): Promise<Attachment[]> {
  const res = await request.get<AjaxResult<Attachment[]>>(`/task/attachment/list/${taskId}`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 上传附件
 */
export async function uploadAttachment(taskId: number, data: Attachment): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/attachment/${taskId}`, data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 删除附件（软删除）
 */
export async function deleteAttachment(attachmentId: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/attachment/${attachmentId}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 恢复附件
 */
export async function restoreAttachment(attachmentId: number): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/task/attachment/${attachmentId}/restore`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 投票任务
 */
export async function voteTask(taskId: number): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/vote/${taskId}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 取消投票
 */
export async function unvoteTask(taskId: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/vote/${taskId}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取投票数量
 */
export async function getVoteCount(taskId: number): Promise<number> {
  const res = await request.get<AjaxResult<number>>(`/task/vote/count/${taskId}`);
  if (res.data.code === 200) return res.data.data ?? 0;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取投票人列表
 */
export async function getVoters(taskId: number): Promise<TaskVote[]> {
  const res = await request.get<AjaxResult<TaskVote[]>>(`/task/vote/list/${taskId}`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取自动化规则列表
 */
export async function getAutomationRules(projectId?: number): Promise<TableDataInfo<AutomationRule>> {
  const res = await request.get<TableDataInfo<AutomationRule>>('/task/automation/list', {
    params: { projectId }
  });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取自动化规则详情
 */
export async function getAutomationRule(ruleId: number): Promise<AutomationRule> {
  const res = await request.get<AjaxResult<AutomationRule>>(`/task/automation/${ruleId}`);
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 创建自动化规则
 */
export async function createAutomationRule(data: AutomationRule): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/task/automation', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 更新自动化规则
 */
export async function updateAutomationRule(data: AutomationRule): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/task/automation', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 删除自动化规则
 */
export async function deleteAutomationRule(ruleId: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/automation/${ruleId}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 启用/禁用自动化规则
 */
export async function toggleAutomationRule(ruleId: number, isEnabled: boolean): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/task/automation/${ruleId}/toggle`, { isEnabled: isEnabled ? 1 : 0 });
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取自动化执行日志
 */
export async function getAutomationLogs(ruleId?: number, taskId?: number): Promise<TableDataInfo<AutomationExecutionLog>> {
  const res = await request.get<TableDataInfo<AutomationExecutionLog>>('/task/automation/log/list', {
    params: { ruleId, taskId }
  });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 获取回收站任务列表
 */
export async function getRecycleBin(projectId?: number): Promise<TableDataInfo<Task>> {
  const res = await request.get<TableDataInfo<Task>>('/task/recycle/list', {
    params: { projectId }
  });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 恢复任务
 */
export async function restoreTask(taskId: number): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/task/recycle/${taskId}/restore`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 彻底删除任务
 */
export async function permanentDeleteTask(taskId: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/recycle/${taskId}`);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 设置预估工时
 */
export async function setTaskEstimate(taskId: number, estimatedHours: number): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/task/${taskId}/estimate`, { estimatedHours });
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 设置故事点
 */
export async function setTaskStoryPoints(taskId: number, storyPoints: number): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/task/${taskId}/storypoints`, { storyPoints });
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

/**
 * 批量移动任务到迭代
 */
export async function batchMoveToIteration(taskIds: number[], iterationId: number): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/task/batch/move-to-iteration', {
    taskIds,
    iterationId
  });
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
