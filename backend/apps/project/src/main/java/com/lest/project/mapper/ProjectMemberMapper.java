package com.lest.project.mapper;

import com.lest.project.domain.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目成员Mapper
 */
@Mapper
public interface ProjectMemberMapper
{
    List<ProjectMember> selectByProjectId(@Param("projectId") Long projectId);

    ProjectMember selectByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    int insert(ProjectMember member);

    int updateByProjectIdAndUserId(ProjectMember member);

    int deleteByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    int countAdminsByProjectId(@Param("projectId") Long projectId);

    int countMembersByProjectId(@Param("projectId") Long projectId);
}
