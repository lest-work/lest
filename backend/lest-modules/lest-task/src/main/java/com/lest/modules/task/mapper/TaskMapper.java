package com.lest.modules.task.mapper;

import com.lest.modules.task.domain.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 任务Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface TaskMapper
{
    Task selectById(@Param("taskId") Long taskId);

    List<Task> selectTaskList(@Param("projectId") Long projectId, @Param("iterationId") Long iterationId,
                              @Param("assigneeId") Long assigneeId, @Param("status") String status,
                              @Param("priority") String priority, @Param("title") String title);

    List<Task> selectBoardTasks(@Param("projectId") Long projectId, @Param("iterationId") Long iterationId);

    List<Task> selectGanttTasks(@Param("projectId") Long projectId, @Param("iterationId") Long iterationId,
                                @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<Task> selectByParentId(@Param("parentId") Long parentId);

    List<Task> selectByIds(@Param("ids") List<Long> ids);

    int insert(Task task);

    int updateById(Task task);

    int deleteById(@Param("taskId") Long taskId);

    int countByParentId(@Param("parentId") Long parentId);

    Integer getMaxSortByProjectId(@Param("projectId") Long projectId);

    Integer getMaxSortByParentId(@Param("parentId") Long parentId);
}
