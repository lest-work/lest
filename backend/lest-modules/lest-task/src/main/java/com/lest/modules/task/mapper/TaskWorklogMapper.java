package com.lest.modules.task.mapper;

import com.lest.modules.task.domain.TaskWorklog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工时记录Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface TaskWorklogMapper
{
    int insert(TaskWorklog worklog);

    List<TaskWorklog> selectByTaskId(@Param("taskId") Long taskId);
}
