package com.lest.modules.project.mapper;

import com.lest.modules.project.domain.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目成员Mapper
 */
@Mapper
public interface ProjectMemberMapper
{
    ProjectMember selectById(@Param("id") Long id);

    List<ProjectMember> selectByProjectId(@Param("projectId") Long projectId);

    ProjectMember selectByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    int insert(ProjectMember member);

    int updateById(ProjectMember member);

    int deleteByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    int countAdminsByProjectId(@Param("projectId") Long projectId);

    int countMembersByProjectId(@Param("projectId") Long projectId);
}
