package com.lest.project.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.api.system.RemoteUserService;
import com.lest.api.system.domain.SysUser;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.domain.R;
import com.lest.common.core.exception.ServiceException;
import com.lest.common.security.permission.ProjectRoleChecker;
import com.lest.common.security.utils.ProjectAuthHelper;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.project.domain.Project;
import com.lest.project.domain.ProjectMember;
import com.lest.project.domain.ProjectInvite;
import com.lest.project.enums.ProjectRole;
import com.lest.project.mapper.IterationMapper;
import com.lest.project.mapper.ProjectInviteMapper;
import com.lest.project.mapper.ProjectMapper;
import com.lest.project.mapper.ProjectMemberMapper;
import com.lest.project.service.IProjectService;

/**
 * 项目 服务层实现
 *
 * @author yshan2028
 */
@Service
public class ProjectServiceImpl implements IProjectService
{
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_ARCHIVED = 2;

    private final ProjectMapper projectMapper;

    private final ProjectMemberMapper projectMemberMapper;

    private final IterationMapper iterationMapper;

    private final ProjectInviteMapper projectInviteMapper;

    private final ProjectRoleChecker projectRoleChecker;

    private final RemoteUserService remoteUserService;

    public ProjectServiceImpl(ProjectMapper projectMapper,
        ProjectMemberMapper projectMemberMapper,
        IterationMapper iterationMapper,
        ProjectInviteMapper projectInviteMapper,
        ProjectRoleChecker projectRoleChecker,
        RemoteUserService remoteUserService)
    {
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.iterationMapper = iterationMapper;
        this.projectInviteMapper = projectInviteMapper;
        this.projectRoleChecker = projectRoleChecker;
        this.remoteUserService = remoteUserService;
    }

    @Override
    public List<Project> selectProjectList(Project project)
    {
        return projectMapper.selectProjectList(project);
    }

    @Override
    public Project selectProjectById(Long projectId)
    {
        return projectMapper.selectById(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertProject(Project project)
    {
        assertProjectNameUnique(project.getName(), null);
        project.setStatus(STATUS_ACTIVE);
        int rows = projectMapper.insert(project);
        if (rows > 0)
        {
            // V1.0: 简化 Project Key 规则为 P{projectId}，满足唯一且可作为 Issue Key 前缀
            project.setProjectKey("P" + project.getProjectId());
            projectMapper.updateById(project);
        }
        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getProjectId());
        member.setUserId(SecurityUtils.getUserId());
        member.setRole("admin");
        projectMemberMapper.insert(member);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProject(Project project)
    {
        Project existing = getRequiredProject(project.getProjectId());
        if (projectRoleChecker != null)
        {
            ProjectAuthHelper.checkRole(projectRoleChecker, project.getProjectId(), ProjectRole.ADMIN.getValue());
        }
        if (project.getName() != null && !Objects.equals(project.getName(), existing.getName()))
        {
            assertProjectNameUnique(project.getName(), project.getProjectId());
        }
        return projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProjectById(Long projectId)
    {
        Project project = getRequiredProject(projectId);
        int iterationCount = iterationMapper.countByProjectId(projectId);
        if (iterationCount > 0)
        {
            throw new ServiceException("项目下存在迭代，无法删除");
        }
        int memberCount = projectMemberMapper.countMembersByProjectId(projectId);
        if (memberCount > 0)
        {
            throw new ServiceException("项目下存在成员，无法删除");
        }
        return projectMapper.deleteById(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int archiveProject(Long projectId)
    {
        Project project = getRequiredProject(projectId);
        if (projectRoleChecker != null)
        {
            ProjectAuthHelper.checkRole(projectRoleChecker, projectId, ProjectRole.ADMIN.getValue());
        }
        if (project.getStatus() == STATUS_ARCHIVED)
        {
            throw new ServiceException("项目已处于归档状态");
        }
        project.setStatus(STATUS_ARCHIVED);
        return projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unarchiveProject(Long projectId)
    {
        Project project = getRequiredProject(projectId);
        if (projectRoleChecker != null)
        {
            ProjectAuthHelper.checkRole(projectRoleChecker, projectId, ProjectRole.ADMIN.getValue());
        }
        if (project.getStatus() != STATUS_ARCHIVED)
        {
            throw new ServiceException("项目未处于归档状态");
        }
        project.setStatus(STATUS_ACTIVE);
        return projectMapper.updateById(project);
    }

    @Override
    public List<ProjectMember> selectMembersByProjectId(Long projectId)
    {
        List<ProjectMember> list = projectMemberMapper.selectByProjectId(projectId);
        // 对外返回产品口径角色：ADMIN / MEMBER / VIEWER
        for (ProjectMember member : list)
        {
            member.setRole(ProjectRole.toProductRole(member.getRole()));
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertMember(ProjectMember member)
    {
        String normalizedRole = normalizeProjectRole(member.getRole());
        member.setRole(normalizedRole);
        ProjectMember existing = projectMemberMapper.selectByProjectIdAndUserId(member.getProjectId(), member.getUserId());
        if (existing != null)
        {
            throw new ServiceException("该成员已在项目中");
        }
        return projectMemberMapper.insert(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMember(Long projectId, Long userId)
    {
        ProjectMember member = projectMemberMapper.selectByProjectIdAndUserId(projectId, userId);
        if (member == null)
        {
            throw new ServiceException("项目成员不存在");
        }
        if (ProjectRole.ADMIN.getValue().equals(member.getRole()))
        {
            int adminCount = projectMemberMapper.countAdminsByProjectId(projectId);
            if (adminCount <= 1)
            {
                throw new ServiceException("不能移除最后一个管理员");
            }
        }
        return projectMemberMapper.deleteByProjectIdAndUserId(projectId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMemberRole(Long projectId, Long userId, String role)
    {
        String normalizedRole = normalizeProjectRole(role);
        ProjectMember member = projectMemberMapper.selectByProjectIdAndUserId(projectId, userId);
        if (member == null)
        {
            throw new ServiceException("项目成员不存在");
        }
        if (ProjectRole.ADMIN.getValue().equals(member.getRole()) && !ProjectRole.ADMIN.getValue().equals(normalizedRole))
        {
            int adminCount = projectMemberMapper.countAdminsByProjectId(projectId);
            if (adminCount <= 1)
            {
                throw new ServiceException("不能修改最后一个管理员的角色");
            }
        }
        member.setRole(normalizedRole);
        return projectMemberMapper.updateByProjectIdAndUserId(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int inviteMemberByEmail(Long projectId, String email, String role)
    {
        String normalizedRole = normalizeProjectRole(role);
        // 确保项目存在
        getRequiredProject(projectId);

        // 通过邮箱的本地部分（@ 前）尝试解析为用户名
        String lookup = email;
        int atIndex = email != null ? email.indexOf('@') : -1;
        if (atIndex > 0)
        {
            lookup = email.substring(0, atIndex);
        }

        com.lest.api.system.model.LoginUser loginUser = null;
        R<com.lest.api.system.model.LoginUser> resp = remoteUserService.getUserInfo(lookup, SecurityConstants.INNER);
        if (resp == null || R.FAIL == resp.getCode() || resp.getData() == null)
        {
            String msg = resp != null && resp.getMsg() != null ? resp.getMsg() : "通过邮箱未找到用户";
            throw new ServiceException(msg);
        }
        loginUser = resp.getData();
        SysUser user = loginUser.getSysUser();
        if (user.getUserId() == null)
        {
            throw new ServiceException("通过邮箱未找到有效用户");
        }

        // 直接加入项目（V1.0 要求：已注册用户可立即加入）
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(user.getUserId());
        member.setRole(normalizedRole);
        int rows = insertMember(member);

        // 同时为该邮箱生成一个一次性邀请记录，便于后续从邮箱链接接受
        // 默认有效期 7 天
        createEmailInvite(projectId, email, normalizedRole, 7 * 24 * 60);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createEmailInvite(Long projectId, String email, String role, long ttlMinutes)
    {
        String normalizedRole = normalizeProjectRole(role);
        getRequiredProject(projectId);
        if (ttlMinutes <= 0)
        {
            throw new ServiceException("邀请有效期必须大于 0 分钟");
        }

        ProjectInvite invite = new ProjectInvite();
        invite.setProjectId(projectId);
        invite.setEmail(email);
        invite.setRole(normalizedRole);
        invite.setToken(UUID.randomUUID().toString().replace("-", ""));
        invite.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
        invite.setUsed(Boolean.FALSE);
        projectInviteMapper.insert(invite);
        return invite.getToken();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int acceptInvite(String token)
    {
        if (token == null || token.isEmpty())
        {
            throw new ServiceException("邀请链接无效");
        }
        ProjectInvite invite = projectInviteMapper.selectByToken(token);
        if (invite == null)
        {
            throw new ServiceException("邀请不存在或已失效");
        }
        if (Boolean.TRUE.equals(invite.getUsed()))
        {
            throw new ServiceException("邀请链接已被使用");
        }
        if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(LocalDateTime.now()))
        {
            throw new ServiceException("邀请链接已过期");
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null)
        {
            throw new ServiceException("当前用户未登录，无法接受邀请");
        }

        // 如果当前用户已经是成员，则不重复添加，只标记邀请为已使用
        ProjectMember existing = projectMemberMapper.selectByProjectIdAndUserId(invite.getProjectId(), currentUserId);
        int delta = 0;
        if (existing == null)
        {
            ProjectMember member = new ProjectMember();
            member.setProjectId(invite.getProjectId());
            member.setUserId(currentUserId);
            member.setRole(invite.getRole());
            delta = insertMember(member);
        }

        invite.setUsed(Boolean.TRUE);
        invite.setUsedBy(currentUserId);
        invite.setUsedAt(LocalDateTime.now());
        projectInviteMapper.updateById(invite);

        return delta;
    }

    private void assertProjectNameUnique(String name, Long excludeId)
    {
        Project existing = projectMapper.selectByName(name);
        if (existing != null && !Objects.equals(existing.getProjectId(), excludeId))
        {
            throw new ServiceException("项目名称'" + name + "'已存在");
        }
    }

    private Project getRequiredProject(Long projectId)
    {
        Project project = projectMapper.selectById(projectId);
        if (project == null)
        {
            throw new ServiceException("项目不存在");
        }
        return project;
    }

    private String normalizeProjectRole(String role)
    {
        String stored = ProjectRole.normalizeToStoredValue(role);
        if (stored == null)
        {
            throw new ServiceException("非法的项目角色: " + role);
        }
        return stored;
    }
}
