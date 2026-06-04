package com.lest.task.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import java.util.Date;

/**
 * 自动化执行日志对象 automation_execution_log
 *
 * @author yshan2028
 */
public class AutomationExecutionLog extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long logId;

    /** 规则ID */
    private Long ruleId;

    /** 任务ID */
    private Long taskId;

    /** 执行状态 */
    private String status;

    /** 执行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executedAt;

    /** 执行信息 */
    private String message;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getExecutedAt()
    {
        return executedAt;
    }

    public void setExecutedAt(Date executedAt)
    {
        this.executedAt = executedAt;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
