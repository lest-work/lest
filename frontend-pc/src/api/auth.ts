import request from './axios';

export interface LoginForm {
  username: string;
  password: string;
  captchaId?: string;
  captchaCode?: string;
}

export interface LoginResult {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
  user: UserInfo;
}

export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  avatar?: string;
  email?: string;
  roles?: string[];
  platformAdmin?: boolean;
}

export const authApi = {
  async login(data: LoginForm): Promise<LoginResult> {
    const res = await request.post<any, { code: number; msg: string; data: LoginResult }>('/auth/login', data);
    return res.data.data;
  },
  logout() {
    return request.delete('/auth/logout');
  },
  refreshToken(refresh_token: string) {
    return request.post('/auth/refresh', { refresh_token });
  },
  async getUserInfo(): Promise<UserInfo> {
    const res = await request.get<any, { code: number; msg: string; data: UserInfo }>('/auth/profile');
    return res.data.data;
  },
  async updateProfile(data: Partial<UserInfo>): Promise<void> {
    await request.put('/auth/profile', data);
  },
  async updatePassword(oldPassword: string, newPassword: string): Promise<void> {
    await request.put('/auth/password', { oldPassword, newPassword });
  },
};
