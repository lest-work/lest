/**
 * 登录参数
 */
export interface LoginParam {
  /** 账号 */
  username?: string;
  /** 密码 */
  password?: string;
  /** 验证码 */
  captcha?: string;
  /** 验证码UUID */
  uuid?: string;
  /** 租户id */
  tenantId?: number;
  /** 是否记住密码 */
  remember?: boolean;
}

/**
 * 登录返回结果
 */
export interface LoginResult {
  /** token */
  accessToken?: string;
  /** 刷新token */
  refreshToken?: string;
  /** 过期时间 */
  expiresIn?: number;
}

/**
 * 图形验证码返回结果
 */
export interface CaptchaResult {
  /** 验证码UUID */
  uuid: string;
  /** 图形验证码base64数据 */
  captcha: string;
}
