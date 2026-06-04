package com.lest.project.service.impl;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lest.project.domain.ProjectMember;
import com.lest.project.mapper.ProjectMemberMapper;
import com.lest.project.service.IProjectMemberService;
import com.lest.common.security.permission.ProjectRoleChecker;

/**
 * 项目成员 服务层实现<br>
 * 同时实现 ProjectRoleChecker 接口，供 @RequireProjectRole 切面使用。
 *
 * @author yshan2028
 */
@Service
public class ProjectMemberServiceImpl implements IProjectMemberService, ProjectRoleChecker
{
    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Override
    public Set<String> getUserRoles(Long projectId, Long userId)
    {
        if (projectId == null || userId == null)
        {
            return null;
        }
        ProjectMember member = projectMemberMapper.selectByProjectIdAndUserId(projectId, userId);
        if (member == null)
        {
            return null;
        }
        Set<String> roles = new HashSet<>();
        if (member.getRole() != null && !member.getRole().isEmpty())
        {
            for (String r : member.getRole().split(","))
            {
                roles.add(r.trim());
            }
        }
        return roles;
    }

    @Override
    public boolean isMember(Long projectId, Long userId)
    {
        return projectMemberMapper.selectByProjectIdAndUserId(projectId, userId) != null;
    }

    @Override
    public boolean isAdmin(Long projectId, Long userId)
    {
        ProjectMember member = projectMemberMapper.selectByProjectIdAndUserId(projectId, userId);
        if (member == null || member.getRole() == null)
        {
            return false;
        }
        for (String r : member.getRole().split(","))
        {
            if ("admin".equalsIgnoreCase(r.trim()))
            {
                return true;
            }
        }
        return false;
    }
}
