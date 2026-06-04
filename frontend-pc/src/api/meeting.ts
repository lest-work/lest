import request from './axios';

export interface Meeting {
  meetingId?: number;
  title: string;
  meetingType?: string;
  description?: string;
  projectId?: number;
  startTime?: string;
  endTime?: string;
  creatorId?: number;
  status?: string;
  createTime?: string;
  updateTime?: string;
}

export interface MeetingParticipant {
  participantId?: number;
  meetingId?: number;
  userId?: number;
  userName?: string;
  nickname?: string;
  status?: string; // pending | accepted | declined
  responseTime?: string;
}

export interface MeetingMinutes {
  minutesId?: number;
  meetingId?: number;
  content?: string;
  createdBy?: number;
  createTime?: string;
}

export const meetingApi = {
  list(params?: { projectId?: number; status?: string }) {
    return request.get<any, { code: number; data: { records: Meeting[]; total: number } }>('/meeting/list', { params });
  },
  getById(meetingId: number) {
    return request.get<any, { code: number; data: Meeting }>(`/meeting/${meetingId}`);
  },
  create(data: Partial<Meeting>) {
    return request.post<any, { code: number }>('/meeting', data);
  },
  update(data: Partial<Meeting>) {
    return request.put<any, { code: number }>('/meeting', data);
  },
  delete(meetingId: number) {
    return request.delete<any, { code: number }>(`/meeting/${meetingId}`);
  },
  participants(meetingId: number) {
    return request.get<any, { code: number; data: { records: MeetingParticipant[]; total: number } }>(
      `/meeting/${meetingId}/participant`
    );
  },
  addParticipant(meetingId: number, participant: Partial<MeetingParticipant>) {
    return request.post<any, { code: number }>(`/meeting/${meetingId}/participant`, participant);
  },
  getMinutes(meetingId: number) {
    return request.get<any, { code: number; data: MeetingMinutes }>(`/meeting/${meetingId}/minutes`);
  },
  saveMinutes(meetingId: number, content: string) {
    return request.post<any, { code: number }>(`/meeting/${meetingId}/minutes`, { content });
  },
};
