package com.lest.modules.task.mapper;

import com.lest.modules.task.domain.TaskComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务评论Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface TaskCommentMapper
{
    TaskComment selectById(@Param("id") Long id);

    List<TaskComment> selectByTaskId(@Param("taskId") Long taskId);

    int insert(TaskComment comment);

    int updateById(TaskComment comment);

    int deleteById(@Param("id") Long id);

    int countByParentId(@Param("parentId") Long parentId);
}
