import request from '@/utils/request';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { ReleasePlan, ReleasePlanParam, ReleaseArtifact, ReleaseIssue } from './model';

export type { ReleasePlan, ReleasePlanParam, ReleaseArtifact, ReleaseIssue } from './model';

/** 状态映射 */
export const STATUS_LABEL: Record<number, string> = {
  0: '草稿',
  1: '待发布',
  2: '构建中',
  3: '已发布',
  4: '已归档'
};

export const STATUS_TAG: Record<number, string> = {
  0: 'info',
  1: 'warning',
  2: 'primary',
  3: 'success',
  4: 'info'
};

/** 发布类型映射 */
export const RELEASE_TYPE_LABEL: Record<number, string> = {
  1: '正式发布',
  2: '热修复',
  3: '内测版',
  4: '公测版'
};

/** 关联类别映射 */
export const ISSUE_CATEGORY_LABEL: Record<number, string> = {
  1: '新功能',
  2: 'Bug修复',
  3: '改进',
  4: '文档'
};

/**
 * 分页查询发布计划列表
 */
export async function pageReleasePlans(params?: ReleasePlanParam): Promise<TableDataInfo<ReleasePlan>> {
  const res = await request.get<TableDataInfo<ReleasePlan>>('/release/plan/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 获取发布计划详情
 */
export async function getReleasePlan(id: number): Promise<ReleasePlan> {
  const res = await request.get<AjaxResult<ReleasePlan>>(`/release/plan/${id}`);
  if (res.data.code === 200) return res.data.data!;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增发布计划
 */
export async function addReleasePlan(data: ReleasePlan): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/release/plan', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 修改发布计划
 */
export async function updateReleasePlan(data: ReleasePlan): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/release/plan', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除发布计划
 */
export async function removeReleasePlan(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/release/plan/${id}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 发布
 */
export async function publishReleasePlan(id: number): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/release/plan/${id}/publish`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 归档
 */
export async function archiveReleasePlan(id: number): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/release/plan/${id}/archive`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 恢复
 */
export async function restoreReleasePlan(id: number): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/release/plan/${id}/restore`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 开始构建
 */
export async function startBuild(id: number): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/release/plan/${id}/build/start`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 完成构建
 */
export async function completeBuild(id: number, downloadUrl?: string): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(`/release/plan/${id}/build/complete`, null, {
    params: downloadUrl ? { downloadUrl } : {}
  });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 即将到来的发布
 */
export async function getUpcomingReleases(): Promise<ReleasePlan[]> {
  const res = await request.get<AjaxResult<ReleasePlan[]>>('/release/plan/upcoming');
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 最近的发布
 */
export async function getRecentReleases(projectId?: number, limit?: number): Promise<ReleasePlan[]> {
  const res = await request.get<AjaxResult<ReleasePlan[]>>('/release/plan/recent', {
    params: { projectId, limit }
  });
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询产物列表
 */
export async function listArtifacts(releasePlanId: number): Promise<ReleaseArtifact[]> {
  const res = await request.get<AjaxResult<ReleaseArtifact[]>>('/release/artifact/list', {
    params: { releasePlanId }
  });
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增产物
 */
export async function addArtifact(data: ReleaseArtifact): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/release/artifact', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除产物
 */
export async function removeArtifact(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/release/artifact/${id}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 查询关联问题列表
 */
export async function listIssues(releasePlanId: number): Promise<ReleaseIssue[]> {
  const res = await request.get<AjaxResult<ReleaseIssue[]>>('/release/issue/list', {
    params: { releasePlanId }
  });
  if (res.data.code === 200) return res.data.data ?? [];
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增关联问题
 */
export async function addIssue(data: ReleaseIssue): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/release/issue', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 批量新增关联问题
 */
export async function batchAddIssues(
  releasePlanId: number,
  category: number,
  opts?: { taskIds?: number[]; issueIds?: number[]; notes?: string }
): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/release/issue/batch', null, {
    params: {
      releasePlanId,
      category,
      taskIds: opts?.taskIds?.join(','),
      issueIds: opts?.issueIds?.join(','),
      notes: opts?.notes
    }
  });
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除关联问题
 */
export async function removeIssue(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(`/release/issue/${id}`);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
