package com.lest.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.task.entity.domain.TaskLabel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务标签Mapper
 *
 * @author Lest
 * @since 2026-05-26
 */
@Mapper
public interface TaskLabelMapper extends BaseMapper<TaskLabel> {

    @Select("SELECT label_id FROM task_label WHERE task_id = #{taskId}")
    List<Long> selectLabelIdsByTaskId(@Param("taskId") Long taskId);

    void deleteByTaskId(@Param("taskId") Long taskId);

    void insertBatch(@Param("taskId") Long taskId, @Param("labelIds") List<Long> labelIds);
}
