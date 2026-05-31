package com.lest.modules.project.mapper;

import com.lest.modules.project.domain.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目Mapper
 */
@Mapper
public interface ProjectMapper
{
    Project selectById(@Param("projectId") Long projectId);

    List<Project> selectProjectList(Project project);

    Project selectByName(@Param("name") String name);

    int insert(Project project);

    int updateById(Project project);

    int deleteById(@Param("projectId") Long projectId);
}
