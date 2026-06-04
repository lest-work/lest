package com.lest.common.security.utils;

import com.lest.common.security.permission.ProjectRoleChecker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 项目权限校验辅助类
 */
@Component
public class ProjectAuthHelper
{
    private ProjectRoleChecker projectRoleChecker;

    public void setProjectRoleChecker(ProjectRoleChecker projectRoleChecker)
    {
        this.projectRoleChecker = projectRoleChecker;
    }

    public boolean hasRole(Long projectId, Long userId, String role)
    {
        if (projectRoleChecker == null || userId == null || projectId == null)
        {
            return false;
        }
        Set<String> roles = projectRoleChecker.getUserRoles(projectId, userId);
        return roles != null && roles.contains(role);
    }

    public boolean isMember(Long projectId, Long userId)
    {
        if (projectRoleChecker == null || userId == null || projectId == null)
        {
            return false;
        }
        return projectRoleChecker.isMember(projectId, userId);
    }

    public static void checkRole(ProjectRoleChecker checker, Long projectId, String role)
    {
        if (checker == null || projectId == null) return;
        Long userId = SecurityUtils.getUserId();
        if (SecurityUtils.isPlatformAdmin()) return;
        Set<String> roles = checker.getUserRoles(projectId, userId);
        if (roles == null || !roles.contains(role))
        {
            throw new com.lest.common.core.exception.ServiceException("您没有足够的项目权限");
        }
    }

    public static void checkMembership(ProjectRoleChecker checker, Long projectId)
    {
        if (checker == null || projectId == null) return;
        Long userId = SecurityUtils.getUserId();
        if (SecurityUtils.isPlatformAdmin()) return;
        if (!checker.isMember(projectId, userId))
        {
            throw new com.lest.common.core.exception.ServiceException("您不是该项目成员，无权访问");
        }
    }
}
