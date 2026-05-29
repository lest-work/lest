package com.lest.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.task.entity.domain.TaskCommit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务代码关联Mapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface TaskCommitMapper extends BaseMapper<TaskCommit> {

    @Select("SELECT * FROM task_commit WHERE task_id = #{taskId} AND type = 'commit' ORDER BY created_at DESC")
    List<TaskCommit> selectCommitsByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT * FROM task_commit WHERE task_id = #{taskId} AND type = 'mr' ORDER BY created_at DESC")
    List<TaskCommit> selectMRsByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT task_id FROM task_commit WHERE commit_hash = #{commitHash} AND type = 'commit'")
    Long selectTaskIdByCommitHash(@Param("commitHash") String commitHash);
}
