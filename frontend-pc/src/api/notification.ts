import request from './axios';

export interface Notification {
  id?: number;
  notificationId?: number;
  userId?: number;
  type: string;
  title: string;
  content: string;
  isRead?: boolean;
  read?: boolean;
  createdAt?: string;
  createTime?: string;
  taskNo?: string;
  projectName?: string;
  taskId?: number;
  projectId?: number;
}

export interface NotificationFilter {
  type?: string;
  isRead?: boolean;
  pageNum?: number;
  pageSize?: number;
}

export const notificationApi = {
  list(params?: NotificationFilter) {
    return request.get<any, { code: number; data: { records: Notification[]; total: number } }>('/notification/list', { params });
  },
  unreadCount() {
    return request.get<any, { code: number; data: number }>('/notification/unread/count');
  },
  markAsRead(notificationId: number) {
    return request.put<any, { code: number }>(`/notification/${notificationId}/read`);
  },
  markAllAsRead() {
    return request.put<any, { code: number }>('/notification/read/all');
  },
  delete(notificationId: number) {
    return request.delete<any, { code: number }>(`/notification/${notificationId}`);
  },
};
