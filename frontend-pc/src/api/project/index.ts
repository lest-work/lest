import request from '@/utils/request';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { Project, ProjectMember, ProjectParam } from './model';

export type { Project, ProjectMember, ProjectParam } from './model';

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
