import request from './axios';

export interface AiProvider {
  providerId?: number;
  name: string;
  providerType: string;   // openai | claude | gemini | local
  apiBase?: string;
  apiKey?: string;
  model?: string;
  enabled?: boolean;
  status?: string;
  createTime?: string;
  updateTime?: string;
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

export const aiApi = {
  provider: {
    list(params?: Partial<AiProvider>) {
      return request.get<any, { code: number; data: { records: AiProvider[]; total: number } }>('/ai/provider/list', { params });
    },
    getById(providerId: number) {
      return request.get<any, { code: number; data: AiProvider }>(`/ai/provider/${providerId}`);
    },
    create(data: Partial<AiProvider>) {
      return request.post<any, { code: number }>('/ai/provider', data);
    },
    update(data: Partial<AiProvider>) {
      return request.put<any, { code: number }>('/ai/provider', data);
    },
    delete(providerId: number) {
      return request.delete<any, { code: number }>(`/ai/provider/${providerId}`);
    },
  },

  chat(prompt: string, providerId?: number) {
    return request.post<any, { code: number; data: string }>('/ai/chat', { prompt, providerId });
  },

  chatWithContext(messages: ChatMessage[], providerId?: number) {
    return request.post<any, { code: number; data: string }>('/ai/chat/context', { messages, providerId });
  },
};
