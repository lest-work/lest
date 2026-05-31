package com.lest.auth.form;

/**
 * 用户登录对象
 *
 * @author yshan2028
 */
public class LoginBody
{
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 验证码ID
     */
    private String captchaId;

    /**
     * 验证码
     */
    private String captchaCode;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getCaptchaId()
    {
        return captchaId;
    }

    public void setCaptchaId(String captchaId)
    {
        this.captchaId = captchaId;
    }

    public String getCaptchaCode()
    {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode)
    {
        this.captchaCode = captchaCode;
    }
}
