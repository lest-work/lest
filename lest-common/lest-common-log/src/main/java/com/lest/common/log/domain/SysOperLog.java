package com.lest.common.log.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志记录表 sys_oper_log
 *
 * @author yshan2028
 */
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    @TableId(type = IdType.AUTO)
    private Long operId;

    /**
     * 操作模块
     */
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除 4授权 5导出 6导入 7强退 8清空数据）
     */
    private Integer businessType;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 操作人员类型（0后台用户 1后台管理 2移动端）
     */
    private Integer operatorType;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 返回参数
     */
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 消耗时间（毫秒）
     */
    private Long costTime;

    public SysOperLog() {
    }

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    @Override
    public String toString() {
        return "SysOperLog{" +
                "operId=" + operId +
                ", title='" + title + '\'' +
                ", businessType=" + businessType +
                ", requestMethod='" + requestMethod + '\'' +
                ", operatorType=" + operatorType +
                ", operName='" + operName + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", requestIp='" + requestIp + '\'' +
                ", requestParams='" + requestParams + '\'' +
                ", jsonResult='" + jsonResult + '\'' +
                ", status=" + status +
                ", errorMsg='" + errorMsg + '\'' +
                ", operateTime=" + operateTime +
                ", costTime=" + costTime +
                '}';
    }
}
