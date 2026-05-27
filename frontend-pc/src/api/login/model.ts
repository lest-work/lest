export interface LoginParam {
  username: string;
  password: string;
  captcha: string;
  uuid: string;
  remember?: boolean;
}

export interface CaptchaResult {
  uuid: string;
  captcha: string;
}

export interface LoginResult {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}
