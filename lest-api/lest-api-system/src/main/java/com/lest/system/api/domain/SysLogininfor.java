package com.lest.system.api.domain;

/**
 * 登录日志（Feign 跨服务传递）
 */
public class SysLogininfor {

    private Long infoId;
    private String userName;
    private String ipaddr;
    private String loginLocation;
    private String loginType;
    private String status;
    private String msg;
    private String loginTime;

    public Long getInfoId() { return infoId; }
    public void setInfoId(Long infoId) { this.infoId = infoId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getIpaddr() { return ipaddr; }
    public void setIpaddr(String ipaddr) { this.ipaddr = ipaddr; }
    public String getLoginLocation() { return loginLocation; }
    public void setLoginLocation(String loginLocation) { this.loginLocation = loginLocation; }
    public String getLoginType() { return loginType; }
    public void setLoginType(String loginType) { this.loginType = loginType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }
}
