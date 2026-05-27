package com.lest.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.task.entity.domain.TaskComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 任务评论Mapper
 *
 * @author Lest
 * @since 2026-05-26
 */
@Mapper
public interface TaskCommentMapper extends BaseMapper<TaskComment> {

    @Select("SELECT COUNT(*) FROM task_comment WHERE parent_id = #{parentId} AND deleted = 0")
    int countByParentId(@Param("parentId") Long parentId);
}
