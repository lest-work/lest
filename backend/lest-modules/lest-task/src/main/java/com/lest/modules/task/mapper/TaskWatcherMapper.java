package com.lest.modules.task.mapper;

import com.lest.modules.task.domain.TaskWatcher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务关注者Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface TaskWatcherMapper
{
    int insert(TaskWatcher watcher);

    List<Long> selectWatcherIdsByTaskId(@Param("taskId") Long taskId);

    void deleteByTaskId(@Param("taskId") Long taskId);

    void insertBatch(@Param("taskId") Long taskId, @Param("userIds") List<Long> userIds);
}
