package com.lest.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.project.entity.domain.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 项目成员Mapper
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {

    /**
     * 统计项目管理员数量
     */
    @Select("SELECT COUNT(*) FROM project_member WHERE project_id = #{projectId} AND role = 'admin' AND deleted = 0")
    int countAdminsByProjectId(@Param("projectId") Long projectId);

    /**
     * 统计项目成员数量
     */
    @Select("SELECT COUNT(*) FROM project_member WHERE project_id = #{projectId} AND deleted = 0")
    int countMembersByProjectId(@Param("projectId") Long projectId);
}
