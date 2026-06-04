package com.lest.task.mapper;

import com.lest.task.domain.TaskLabel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务标签Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface TaskLabelMapper
{
    int insert(TaskLabel taskLabel);

    List<Long> selectLabelIdsByTaskId(@Param("taskId") Long taskId);

    void deleteByTaskId(@Param("taskId") Long taskId);

    void insertBatch(@Param("taskId") Long taskId, @Param("labelIds") List<Long> labelIds);
}
