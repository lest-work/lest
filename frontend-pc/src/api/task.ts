import request from './axios';

export type TaskType = 'epic' | 'story' | 'task' | 'bug';
export type TaskStatus = 'todo' | 'in_progress' | 'completed';
export type TaskPriority = 'highest' | 'high' | 'medium' | 'low' | 'lowest';
export type TaskPriorityCode = 'p0' | 'p1' | 'p2' | 'p3';

export interface Task {
  id: number;
  taskId?: number;
  taskNo?: string;
  projectId: number;
  title: string;
  description?: string;
  type: TaskType;
  taskType?: TaskType;
  status: TaskStatus;
  priority: TaskPriority;
  assigneeId?: number;
  assigneeName?: string;
  reporterId?: number;
  reporterName?: string;
  iterationId?: number;
  iterationName?: string;
  parentId?: number;
  storyPoints?: number;
  estimateHours?: number;
  actualHours?: number;
  remainingHours?: number;
  startDate?: string;
  startTime?: string;
  dueDate?: string;
  endDate?: string;
  completedAt?: string;
  labels?: string[];
  labelNames?: string[];
  labelIds?: number[];
  watchers?: number[];
  childCount?: number;
  hasBlockers?: boolean;
  depth?: number;
  sort?: number;
  createdAt?: string;
  createTime?: string;
  updatedAt?: string;
  updateTime?: string;
}

export interface TaskFilter {
  projectId?: number;
  type?: TaskType[];
  taskType?: TaskType[];
  status?: TaskStatus[];
  assigneeId?: number;
  assigneeIds?: number[];
  iterationId?: number;
  priority?: TaskPriority[];
  keyword?: string;
}

export interface TaskComment {
  id?: number;
  taskCommentId?: number;
  taskId: number;
  userId?: number;
  userName?: string;
  content: string;
  parentId?: number;
  createTime?: string;
  updateTime?: string;
}

export interface TaskWorklog {
  id?: number;
  taskWorklogId?: number;
  taskId: number;
  userId?: number;
  userName?: string;
  hours: number;
  workDate: string;
  description?: string;
  createTime?: string;
}

export interface TaskDependency {
  taskId: number;
  dependencyTaskId: number;
  dependencyTaskTitle?: string;
  dependencyTaskStatus?: string;
  type?: string;
}

export interface TaskCommit {
  id?: number;
  taskCommitId?: number;
  taskId: number;
  type?: 'commit' | 'mr';
  source?: string;
  commitHash?: string;
  commitMessage?: string;
  repoId?: string;
  url?: string;
  author?: string;
  date?: string;
  createTime?: string;
}

export const taskApi = {
  list(params: { page?: number; size?: number } & Partial<TaskFilter>) {
    return request.get<any, { code: number; data: { records: Task[]; total: number } }>('/task/list', { params });
  },
  getById(id: number) {
    return request.get<any, { code: number; data: Task }>(`/task/${id}`);
  },
  create(data: Partial<Task>) {
    return request.post<any, { code: number; data: Task }>('/task', data);
  },
  update(id: number, data: Partial<Task>) {
    return request.put<any, { code: number; data: Task }>(`/task`, { ...data, taskId: id });
  },
  delete(id: number) {
    return request.delete(`/task/${id}`);
  },

  updateStatus(id: number, status: string) {
    return request.put(`/task/${id}/status`, { status });
  },
  assign(id: number, assigneeId: number) {
    return request.put(`/task/${id}/assign`, { assigneeId });
  },
  claim(id: number) {
    return request.post(`/task/${id}/claim`);
  },

  move(id: number, data: { targetColumn?: string; targetPosition?: number }) {
    return request.put(`/task/${id}/move`, data);
  },
  batchMove(params: { taskIds: number[]; iterationId?: number; status?: string }) {
    return request.put('/task/batch/move-to-iteration', params);
  },

  gantt(params: { projectId?: number; iterationId?: number }) {
    return request.get<any, { code: number; data: any[] }>('/task/gantt', { params });
  },

  subtaskList(parentId: number) {
    return request.get<any, { code: number; data: Task[] }>(`/task/${parentId}/subtask/list`);
  },
  createSubtask(parentId: number, data: Partial<Task>) {
    return request.post<any, { code: number }>(`/task/${parentId}/subtask`, data);
  },

  dependencyList(taskId: number) {
    return request.get<any, { code: number; data: TaskDependency[] }>(`/task/${taskId}/dependency/list`);
  },
  addDependency(taskId: number, dependencyTaskId: number, type?: string) {
    return request.post(`/task/${taskId}/dependency`, { dependencyTaskId, type: type || 'blocker' });
  },
  removeDependency(taskId: number, depTaskId: number) {
    return request.delete(`/task/${taskId}/dependency/${depTaskId}`);
  },

  worklogList(taskId: number) {
    return request.get<any, { code: number; data: { records: TaskWorklog[]; total: number } }>(`/task/${taskId}/worklog/list`);
  },
  addWorklog(taskId: number, data: { hours: number; workDate: string; description?: string }) {
    return request.post(`/task/${taskId}/worklog`, data);
  },
  setEstimate(taskId: number, estimatedHours: number) {
    return request.put(`/task/${taskId}/estimate`, { estimatedHours });
  },
  setRemaining(taskId: number, remainingHours: number) {
    return request.put(`/task/${taskId}/remaining`, { remainingHours });
  },
  setStoryPoints(taskId: number, storyPoints: number) {
    return request.put(`/task/${taskId}/storypoints`, { storyPoints });
  },

  commentList(taskId: number) {
    return request.get<any, { code: number; data: TaskComment[] }>(`/task/${taskId}/comment/list`);
  },
  addComment(taskId: number, content: string) {
    return request.post(`/task/${taskId}/comment`, { content });
  },
  updateComment(taskId: number, commentId: number, content: string) {
    return request.put(`/task/${taskId}/comment/${commentId}`, { content });
  },
  removeComment(taskId: number, commentId: number) {
    return request.delete(`/task/${taskId}/comment/${commentId}`);
  },

  commitList(taskId: number) {
    return request.get<any, { code: number; data: TaskCommit[] }>(`/task/${taskId}/commit/list`);
  },
  mergeRequestList(taskId: number) {
    return request.get<any, { code: number; data: TaskCommit[] }>(`/task/${taskId}/merge-request/list`);
  },
  addCommit(taskId: number, data: Partial<TaskCommit>) {
    return request.post(`/task/${taskId}/commit`, data);
  },
  addMergeRequest(taskId: number, data: Partial<TaskCommit>) {
    return request.post(`/task/${taskId}/merge-request`, data);
  },

  watch(taskId: number) {
    return request.post(`/task/${taskId}/watch`);
  },
  unwatch(taskId: number) {
    return request.delete(`/task/${taskId}/watch`);
  },
  getWatched() {
    return request.get<any, { code: number; data: number[] }>('/task/watched');
  },

  changeHistory(taskId: number) {
    return request.get<any, { code: number; data: any[] }>(`/task/${taskId}/change-history`);
  },
  watchers(taskId: number) {
    return request.get<any, { code: number; data: any[] }>(`/task/${taskId}/watchers`);
  },
  clone(taskId: number, data: { title?: string; projectId?: number; iterationId?: number; includeSubtasks?: boolean; includeAttachments?: boolean; includeComments?: boolean }) {
    return request.post<any, { code: number; data: { id: number; title: string; taskKey: string } }>(`/task/${taskId}/clone`, data);
  },
};
