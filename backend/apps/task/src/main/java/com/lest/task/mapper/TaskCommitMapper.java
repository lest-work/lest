package com.lest.task.mapper;

import com.lest.task.domain.TaskCommit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务代码关联Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface TaskCommitMapper
{
    int insert(TaskCommit commit);

    List<TaskCommit> selectCommitsByTaskId(@Param("taskId") Long taskId);

    List<TaskCommit> selectMRsByTaskId(@Param("taskId") Long taskId);

    Long selectTaskIdByCommitHash(@Param("commitHash") String commitHash);

    int deleteByTaskId(@Param("taskId") Long taskId);
}
