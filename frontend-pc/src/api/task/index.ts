import request from '@/utils/request';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { Task, TaskParam } from './model';

export type { Task, TaskParam } from './model';

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
 * 获取看板视图
 */
export async function getBoard(projectId: number, iterationId?: number) {
  const res = await request.get<AjaxResult<unknown>>('/task/board', {
    params: { projectId, iterationId }
  });
  if (res.data.code === 200) return res.data.data;
  return Promise.reject(new Error(res.data.msg));
}

export { PRIORITY_ORDER };
