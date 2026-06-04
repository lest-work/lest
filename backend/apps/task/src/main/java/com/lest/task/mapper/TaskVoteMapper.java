package com.lest.task.mapper;

import com.lest.task.domain.TaskVote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskVoteMapper
{
    TaskVote selectById(@Param("voteId") Long voteId);
    List<TaskVote> selectByTaskId(@Param("taskId") Long taskId);
    int countByTaskId(@Param("taskId") Long taskId);
    TaskVote selectByTaskIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);
    int insert(TaskVote vote);
    int deleteById(@Param("voteId") Long voteId);
    int deleteByTaskIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);
    int deleteByTaskId(@Param("taskId") Long taskId);
}
