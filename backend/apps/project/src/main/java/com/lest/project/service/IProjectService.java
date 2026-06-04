package com.lest.project.service;

import java.util.List;
import com.lest.project.domain.Project;
import com.lest.project.domain.ProjectMember;

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

    /**
     * 通过邮箱邀请成员加入项目（仅查找已存在用户，不负责发送邮件）。
     */
    public int inviteMemberByEmail(Long projectId, String email, String role);

    /**
     * 创建邮箱邀请链接，返回邀请 token（由 Controller 封装为 URL）。
     */
    public String createEmailInvite(Long projectId, String email, String role, long ttlMinutes);

    /**
     * 接受邮箱邀请链接（一次性使用），返回加入后的项目成员数量增量（0/1）。
     */
    public int acceptInvite(String token);
}
