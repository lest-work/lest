import request from '@/utils/request';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { Task, TaskParam, TaskWorklog, TaskComment, Label, BoardColumn } from './model';

export type { Task, TaskParam, TaskWorklog, TaskComment, Label, BoardColumn } from './model';

const PRIORITY_ORDER: Record<string, number> = { p0: 1, p1: 2, p2: 3, p3: 4 };

/**
 * 分页查询任务列表
 * GET /task/list → gateway 去掉 task → lest-task /list
 */
export async function pageTasks(params?: TaskParam): Promise<TableDataInfo<Task>> {
  const res = await request.get<TableDataInfo<Task>>('/task/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 获取任务详情
 */
export async function getTask(id: number): Promise<Task> {
  const res = await request.get<AjaxResult<Task>>(`/task/${id}`);
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增任务
 */
export async function addTask(data: Task): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/task', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 修改任务
 */
export async function updateTask(data: Task): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/task', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除任务
 */
export async function removeTask(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/${id}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 更新任务状态
 */
export async function updateTaskStatus(id: number, status: string): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/task/${id}/status`, { status });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 认领任务
 */
export async function claimTask(id: number): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/${id}/claim`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 获取看板视图（按状态分组）
 */
export async function getBoard(projectId: number, iterationId?: number): Promise<BoardColumn[]> {
  const res = await request.get<AjaxResult<BoardColumn[]>>('/task/board', {
    params: { projectId, iterationId }
  });
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 获取甘特图数据
 */
export async function getGantt(projectId: number) {
  const res = await request.get<AjaxResult<unknown>>('/task/gantt', { params: { projectId } });
  if (res.data.code === 200) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询子任务列表
 */
export async function listSubtasks(id: number): Promise<Task[]> {
  const res = await request.get<AjaxResult<Task[]>>(`/task/${id}/subtask`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询工时记录
 */
export async function listWorklogs(taskId: number): Promise<TaskWorklog[]> {
  const res = await request.get<AjaxResult<TaskWorklog[]>>(`/task/${taskId}/worklog/list`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增工时记录
 */
export async function addWorklog(taskId: number, data: TaskWorklog): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/${taskId}/worklog`, data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询任务评论列表
 */
export async function listComments(taskId: number): Promise<TaskComment[]> {
  const res = await request.get<AjaxResult<TaskComment[]>>(`/task/${taskId}/comment/list`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增任务评论
 */
export async function addComment(taskId: number, data: TaskComment): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/task/${taskId}/comment`, data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除任务评论
 */
export async function removeComment(taskId: number, commentId: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/task/${taskId}/comment/${commentId}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询标签列表
 */
export async function listLabels(projectId?: number): Promise<Label[]> {
  const res = await request.get<AjaxResult<Label[]>>(`/project/${projectId}/label/list`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增标签
 */
export async function addLabel(projectId: number, data: Label): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/project/${projectId}/label`, data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除标签
 */
export async function removeLabel(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/label/${id}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

export { PRIORITY_ORDER };
