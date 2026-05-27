package com.lest.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.task.entity.domain.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 任务Mapper
 *
 * @author Lest
 * @since 2026-05-26
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    @Select("SELECT COUNT(*) FROM task WHERE parent_id = #{parentId} AND deleted = 0")
    int countByParentId(@Param("parentId") Long parentId);

    @Select("SELECT MAX(sort) FROM task WHERE project_id = #{projectId} AND deleted = 0")
    Integer getMaxSortByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT MAX(sort) FROM task WHERE parent_id = #{parentId} AND deleted = 0")
    Integer getMaxSortByParentId(@Param("parentId") Long parentId);
}
