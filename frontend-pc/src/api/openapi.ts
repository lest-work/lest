import request from './axios';

export interface ApiKey {
  keyId?: number;
  userId?: number;
  keyName: string;
  apiKey?: string;      // 遮蔽显示
  keyPrefix?: string;
  permissions?: string[];
  expiresAt?: string;
  isEnabled?: boolean;
  createdAt?: string;
  createTime?: string;
}

export interface WebhookEndpoint {
  webhookId?: number;
  userId?: number;
  name: string;
  targetUrl: string;
  events?: string[];   // task.created | task.updated | ...
  secret?: string;
  isEnabled?: boolean;
  createdAt?: string;
  createTime?: string;
}

export const openApiApi = {
  key: {
    list() {
      return request.get<any, { code: number; data: { records: ApiKey[]; total: number } }>('/openapi/key/list');
    },
    create(data: { keyName: string; permissions?: string[]; expiresAt?: string }) {
      return request.post<any, { code: number; data: ApiKey }>('/openapi/key', data);
    },
    revoke(keyId: number) {
      return request.delete<any, { code: number }>(`/openapi/key/${keyId}`);
    },
  },
  webhook: {
    list() {
      return request.get<any, { code: number; data: { records: WebhookEndpoint[]; total: number } }>('/openapi/webhook/list');
    },
    create(data: Partial<WebhookEndpoint>) {
      return request.post<any, { code: number; data: WebhookEndpoint }>('/openapi/webhook', data);
    },
    update(data: Partial<WebhookEndpoint>) {
      return request.put<any, { code: number }>('/openapi/webhook', data);
    },
    delete(webhookId: number) {
      return request.delete<any, { code: number }>(`/openapi/webhook/${webhookId}`);
    },
  },
};
