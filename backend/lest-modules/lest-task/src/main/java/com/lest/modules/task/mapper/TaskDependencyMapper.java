package com.lest.modules.task.mapper;

import com.lest.modules.task.domain.TaskDependency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务依赖Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface TaskDependencyMapper
{
    int insert(TaskDependency dependency);

    int deleteByTaskIdAndDependencyTaskId(@Param("taskId") Long taskId, @Param("dependencyTaskId") Long dependencyTaskId);

    List<Long> selectDependencyTaskIds(@Param("taskId") Long taskId);

    List<Long> selectBlockedByTaskIds(@Param("taskId") Long taskId);

    int countByTaskId(@Param("taskId") Long taskId);

    int deleteByTaskId(@Param("taskId") Long taskId);
}
