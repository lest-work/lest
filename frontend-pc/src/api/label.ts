import request from './axios';

export interface Label {
  labelId?: number;
  projectId?: number;
  name: string;
  color?: string;
  description?: string;
  createTime?: string;
  updateTime?: string;
}

export const labelApi = {
  list(projectId: number) {
    return request.get<any, { code: number; data: Label[] }>(
      `/project/${projectId}/label/labels`
    );
  },
  create(projectId: number, data: Partial<Label>) {
    return request.post<any, { code: number }>(
      `/project/${projectId}/label/labels`,
      { ...data, projectId }
    );
  },
  update(labelId: number, data: Partial<Label>) {
    return request.put<any, { code: number }>(
      `/project/label/${labelId}`,
      data
    );
  },
  delete(labelId: number) {
    return request.delete(`/project/label/${labelId}`);
  },
};
