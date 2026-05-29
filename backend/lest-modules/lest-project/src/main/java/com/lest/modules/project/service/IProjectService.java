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
    /**
     * 查询项目列表
     */
    public List<Project> selectProjectList(Project project);

    /**
     * 查询项目详情
     */
    public Project selectProjectById(Long id);

    /**
     * 新增项目
     */
    public int insertProject(Project project);

    /**
     * 修改项目
     */
    public int updateProject(Project project);

    /**
     * 删除项目
     */
    public int deleteProjectById(Long id);

    /**
     * 归档项目
     */
    public int archiveProject(Long id);

    /**
     * 取消归档项目
     */
    public int unarchiveProject(Long id);

    /**
     * 查询项目成员列表
     */
    public List<ProjectMember> selectMembersByProjectId(Long projectId);

    /**
     * 新增项目成员
     */
    public int insertMember(ProjectMember member);

    /**
     * 删除项目成员
     */
    public int deleteMember(Long projectId, Long userId);

    /**
     * 修改成员角色
     */
    public int updateMemberRole(Long projectId, Long userId, String role);
}
