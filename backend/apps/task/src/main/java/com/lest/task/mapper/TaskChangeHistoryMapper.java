package com.lest.task.mapper;

import com.lest.task.domain.TaskChangeHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskChangeHistoryMapper
{
    List<TaskChangeHistory> selectByTaskId(Long taskId);

    int insert(TaskChangeHistory history);
}
