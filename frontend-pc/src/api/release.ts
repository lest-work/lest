import request from './axios';

export type ReleaseStatus = '0' | '1' | '2' | '3';

export interface ReleasePlan {
  releasePlanId?: number;
  projectId?: number;
  name: string;
  version?: string;
  description?: string;
  status?: ReleaseStatus;
  releaseDate?: string;
  plannedDate?: string;
  actualDate?: string;
  buildStatus?: string;
  downloadUrl?: string;
  createBy?: string;
  createTime?: string;
  updateTime?: string;
}

export interface ReleaseArtifact {
  releaseArtifactId?: number;
  releasePlanId?: number;
  name: string;
  version?: string;
  fileUrl?: string;
  fileSize?: number;
  artifactType?: string;
  createTime?: string;
}

function toArtifactPayload(data: Partial<ReleaseArtifact>) {
  return {
    artifactName: data.name,
    version: data.version,
    fileUrl: data.fileUrl,
    fileSize: data.fileSize,
    artifactType: data.artifactType,
    releasePlanId: data.releasePlanId,
  };
}

export interface ReleaseIssue {
  releaseIssueId?: number;
  releasePlanId?: number;
  taskId?: number;
  issueId?: number;
  category?: number;
  notes?: string;
}

export const releaseApi = {
  plan: {
    list(params?: Partial<ReleasePlan>) {
      return request.get<any, { code: number; data: { records: ReleasePlan[]; total: number } }>(
        '/release/plan/list', { params }
      ).then(res => ({
        ...res,
        data: {
          ...res.data,
          records: (res.data?.records || []).map((p: any) => ({
            ...p,
            status: String(p.status ?? '0') as ReleaseStatus,
          })),
        },
      }));
    },
    getById(releasePlanId: number) {
      return request.get<any, { code: number; data: ReleasePlan }>(
        `/release/plan/${releasePlanId}`
      );
    },
    create(data: Partial<ReleasePlan>) {
      return request.post<any, { code: number }>('/release/plan', data);
    },
    update(data: Partial<ReleasePlan>) {
      return request.put<any, { code: number }>('/release/plan', {
        ...data,
        releasePlanId: data.releasePlanId,
      });
    },
    delete(releasePlanId: number) {
      return request.delete(`/release/plan/${releasePlanId}`);
    },
    publish(releasePlanId: number) {
      return request.post(`/release/plan/${releasePlanId}/publish`);
    },
    archive(releasePlanId: number) {
      return request.post(`/release/plan/${releasePlanId}/archive`);
    },
    restore(releasePlanId: number) {
      return request.post(`/release/plan/${releasePlanId}/restore`);
    },
    startBuild(releasePlanId: number) {
      return request.post(`/release/plan/${releasePlanId}/build/start`);
    },
    completeBuild(releasePlanId: number, downloadUrl?: string) {
      return request.post(`/release/plan/${releasePlanId}/build/complete`, null, {
        params: { downloadUrl },
      });
    },
    upcoming() {
      return request.get<any, { code: number; data: ReleasePlan[] }>('/release/plan/upcoming');
    },
    recent(projectId: number, limit?: number) {
      return request.get<any, { code: number; data: ReleasePlan[] }>(
        `/release/${projectId}/plan/recent`,
        { params: { limit } }
      );
    },
  },
  artifact: {
    list(releasePlanId: number) {
      return request.get<any, { code: number; data: ReleaseArtifact[] }>(
        '/release/artifact/list',
        { params: { releasePlanId } }
      );
    },
    create(data: Partial<ReleaseArtifact>) {
      return request.post<any, { code: number }>('/release/artifact', toArtifactPayload(data));
    },
    delete(releaseArtifactId: number) {
      return request.delete(`/release/artifact/${releaseArtifactId}`);
    },
  },
  issue: {
    list(releasePlanId: number) {
      return request.get<any, { code: number; data: ReleaseIssue[] }>(
        '/release/issue/list',
        { params: { releasePlanId } }
      );
    },
    create(data: Partial<ReleaseIssue>) {
      return request.post<any, { code: number }>('/release/issue', data);
    },
    batchCreate(releasePlanId: number, taskIds?: number[], issueIds?: number[], category?: number, notes?: string) {
      return request.post<any, { code: number }>('/release/issue/batch', null, {
        params: { releasePlanId, taskIds, issueIds, category, notes },
      });
    },
    delete(releaseIssueId: number) {
      return request.delete(`/release/issue/${releaseIssueId}`);
    },
  },
};
