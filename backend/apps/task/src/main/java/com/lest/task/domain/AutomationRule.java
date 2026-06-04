package com.lest.task.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import java.util.Date;

/**
 * 自动化规则对象 automation_rule
 *
 * @author yshan2028
 */
public class AutomationRule extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 项目ID */
    private Long projectId;

    /** 规则名称 */
    private String name;

    /** 触发条件 */
    private String trigger;

    /** 执行条件 */
    private String condition;

    /** 执行动作 */
    private String action;

    /** 是否启用 */
    private Integer isEnabled;

    /** 上次执行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastRunAt;

    /** 执行次数 */
    private Long runCount;

    public Long getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTrigger()
    {
        return trigger;
    }

    public void setTrigger(String trigger)
    {
        this.trigger = trigger;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public Integer getIsEnabled()
    {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled)
    {
        this.isEnabled = isEnabled;
    }

    public Date getLastRunAt()
    {
        return lastRunAt;
    }

    public void setLastRunAt(Date lastRunAt)
    {
        this.lastRunAt = lastRunAt;
    }

    public Long getRunCount()
    {
        return runCount;
    }

    public void setRunCount(Long runCount)
    {
        this.runCount = runCount;
    }
}
