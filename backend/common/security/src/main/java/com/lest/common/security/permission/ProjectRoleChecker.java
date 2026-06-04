package com.lest.common.security.permission;

import java.util.List;
import java.util.Set;

/**
 * 项目角色检查器接口
 */
public interface ProjectRoleChecker
{
    /**
     * 获取用户在指定项目中的角色列表
     */
    Set<String> getUserRoles(Long projectId, Long userId);

    /**
     * 判断用户是否为指定项目的成员
     */
    boolean isMember(Long projectId, Long userId);

    /**
     * 判断用户是否为指定项目的管理员
     */
    boolean isAdmin(Long projectId, Long userId);
}
