/**
 * 登录参数
 */
export interface LoginParam {
  /** 账号 */
  username?: string;
  /** 密码 */
  password?: string;
  /** 验证码 */
  code?: string;
  /** 验证码uuid */
  uuid?: string;
  /** 是否记住密码 */
  remember?: boolean;
}

/**
 * 登录 token 结果 (TokenService.createToken 返回格式)
 */
export interface LoginTokenResult {
  /** JWT token */
  access_token: string;
  /** 过期时间(分钟) */
  expires_in: number;
}

/**
 * 图形验证码返回结果 (网关 GET /code 返回顶层字段)
 */
export interface CaptchaResult {
  /** 验证码图片 base64 */
  img?: string;
  /** 验证码 uuid */
  uuid?: string;
  /** 是否开启验证码 */
  captchaEnabled?: boolean;
}
