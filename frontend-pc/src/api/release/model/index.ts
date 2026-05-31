import type { PageParam } from '@/api';

/**
 * 发布计划
 */
export interface ReleasePlan {
  releasePlanId?: number;
  projectId?: number;
  name?: string;
  description?: string;
  releaseDate?: string;
  /** 状态：0=草稿, 1=已发布, 2=构建中, 3=已发布, 4=已归档 */
  status?: number;
  buildNumber?: number;
  gitTag?: string;
  gitBranch?: string;
  changelog?: string;
  /** 描述（同 description）*/
  desc?: string;
  /** 发布类型 */
  releaseType?: number;
  isDraft?: number;
  isStable?: number;
  downloadUrl?: string;
  releaseNotes?: string;
  artifactCount?: number;
  issueCount?: number;
  statusName?: string;
  releaseTypeName?: string;
  createdBy?: number;
  updatedBy?: number;
  createTime?: string;
  updateTime?: string;
}

/**
 * 发布计划查询参数
 */
export interface ReleasePlanParam extends PageParam {
  projectId?: number;
  name?: string;
  status?: number;
}

/**
 * 发布产物
 */
export interface ReleaseArtifact {
  releaseArtifactId?: number;
  releasePlanId?: number;
  artifactName?: string;
  artifactType?: string;
  fileName?: string;
  filePath?: string;
  fileSize?: number;
  fileHash?: string;
  downloadUrl?: string;
  downloadCount?: number;
  uploadedBy?: number;
  uploadedAt?: string;
  releaseName?: string;
  uploadedByName?: string;
  metadata?: string;
  createdBy?: number;
  createTime?: string;
}

/**
 * 发布关联问题
 */
export interface ReleaseIssue {
  releaseIssueId?: number;
  releasePlanId?: number;
  issueId?: number;
  taskId?: number;
  category?: number;
  notes?: string;
  addedBy?: number;
  addedAt?: string;
  releaseName?: string;
  addedByName?: string;
  createdBy?: number;
  createTime?: string;
}
