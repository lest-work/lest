import request from './axios';

export interface BoardTask {
  id: number;
  taskNo: string;
  title: string;
  type: 'epic' | 'story' | 'task' | 'bug';
  status: string;
  priority: 'highest' | 'high' | 'medium' | 'low' | 'lowest';
  assigneeId?: number;
  assigneeName?: string;
  assigneeAvatar?: string;
  iterationName?: string;
  dueDate?: string;
  storyPoints?: number;
  labelNames?: string[];
  position: number;
}

export interface BoardColumn {
  id: number;
  name: string;
  status: string;
  taskCount: number;
  tasks: BoardTask[];
}

export interface MoveCardPayload {
  targetColumn?: string;
  targetPosition?: number;
}

export const boardApi = {
  getBoard(projectId: number, iterationId?: number) {
    return request.get<any, { code: number; data: BoardColumn[] }>(
      `/task/board/${projectId}`,
      { params: { iterationId } }
    );
  },
  moveCard(taskId: number, data: MoveCardPayload) {
    return request.put(`/task/${taskId}/move`, data);
  },
};
