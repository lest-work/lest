package com.lest.modules.project.service;

import java.util.List;
import com.lest.modules.project.domain.Project;
import com.lest.modules.project.domain.ProjectMember;

/**
 * 项目 服务层
 *
 * @author yshan2028
 */
public interface IProjectService
{
    public List<Project> selectProjectList(Project project);

    public Project selectProjectById(Long projectId);

    public int insertProject(Project project);

    public int updateProject(Project project);

    public int deleteProjectById(Long projectId);

    public int archiveProject(Long projectId);

    public int unarchiveProject(Long projectId);

    public List<ProjectMember> selectMembersByProjectId(Long projectId);

    public int insertMember(ProjectMember member);

    public int deleteMember(Long projectId, Long userId);

    public int updateMemberRole(Long projectId, Long userId, String role);
}
