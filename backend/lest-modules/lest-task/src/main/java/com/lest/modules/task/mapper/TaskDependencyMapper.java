package com.lest.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.task.entity.domain.TaskDependency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务依赖Mapper
 *
 * @author Lest
 * @since 2026-05-26
 */
@Mapper
public interface TaskDependencyMapper extends BaseMapper<TaskDependency> {

    @Select("SELECT dependency_task_id FROM task_dependency WHERE task_id = #{taskId}")
    List<Long> selectDependencyTaskIds(@Param("taskId") Long taskId);

    @Select("SELECT task_id FROM task_dependency WHERE dependency_task_id = #{taskId}")
    List<Long> selectBlockedByTaskIds(@Param("taskId") Long taskId);

    @Select("SELECT COUNT(*) FROM task_dependency WHERE task_id = #{taskId} OR dependency_task_id = #{taskId}")
    int countByTaskId(@Param("taskId") Long taskId);
}
