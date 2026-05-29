import request from '@/utils/request';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { Project, ProjectMember, ProjectParam, Iteration, IterationParam, Milestone } from './model';

export type { Project, ProjectMember, ProjectParam, Iteration, IterationParam, Milestone } from './model';

/**
 * 分页查询项目列表
 * GET /project/list → gateway 去掉 project → lest-project /list
 */
export async function pageProjects(params?: ProjectParam): Promise<TableDataInfo<Project>> {
  const res = await request.get<TableDataInfo<Project>>('/project/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 获取项目详情
 */
export async function getProject(id: number): Promise<Project> {
  const res = await request.get<AjaxResult<Project>>(`/project/${id}`);
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增项目
 */
export async function addProject(data: Project): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/project', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 修改项目
 */
export async function updateProject(data: Project): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/project', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除项目
 */
export async function removeProject(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/project/${id}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 归档项目
 */
export async function archiveProject(id: number): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/project/${id}/archive`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询项目成员列表
 */
export async function getProjectMembers(id: number): Promise<ProjectMember[]> {
  const res = await request.get<AjaxResult<ProjectMember[]>>(`/project/${id}/member/list`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 添加项目成员
 */
export async function addProjectMember(projectId: number, userId: number, role: string): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/project/${projectId}/member`, { userId, role });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 移除项目成员
 */
export async function removeProjectMember(projectId: number, userId: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/project/${projectId}/member/${userId}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询迭代列表
 * GET /project/{projectId}/iteration/list → gateway strips 'project' → lest-project /{projectId}/iteration/list
 */
export async function listIterations(projectId: number, params?: IterationParam): Promise<TableDataInfo<Iteration>> {
  const res = await request.get<TableDataInfo<Iteration>>(`/project/${projectId}/iteration/list`, { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增迭代
 */
export async function addIteration(projectId: number, data: Iteration): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/project/${projectId}/iteration`, data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 修改迭代（含状态更新）
 */
export async function updateIteration(data: Iteration): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(`/project/iteration/${data.id}`, data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除迭代
 */
export async function removeIteration(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/project/iteration/${id}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询里程碑列表
 * GET /project/{projectId}/milestone/list → gateway strips 'project' → lest-project /{projectId}/milestone/list
 */
export async function listMilestones(projectId: number): Promise<Milestone[]> {
  const res = await request.get<AjaxResult<Milestone[]>>(`/project/${projectId}/milestone/list`);
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增里程碑
 */
export async function addMilestone(projectId: number, data: Milestone): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/project/${projectId}/milestone`, data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除里程碑
 */
export async function removeMilestone(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/project/milestone/${id}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
