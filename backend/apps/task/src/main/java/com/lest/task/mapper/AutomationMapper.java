package com.lest.task.mapper;

import com.lest.task.domain.AutomationRule;
import com.lest.task.domain.AutomationExecutionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AutomationMapper
{
    List<AutomationRule> selectRuleList(@Param("projectId") Long projectId);
    AutomationRule selectRuleById(@Param("ruleId") Long ruleId);
    int insertRule(AutomationRule rule);
    int updateRule(AutomationRule rule);
    int deleteRuleById(@Param("ruleId") Long ruleId);
    int toggleRuleEnabled(@Param("ruleId") Long ruleId, @Param("isEnabled") Integer isEnabled);

    List<AutomationExecutionLog> selectLogList(@Param("ruleId") Long ruleId);
    List<AutomationExecutionLog> selectLogListByTaskId(@Param("taskId") Long taskId);
    int insertLog(AutomationExecutionLog log);
}
