package com.lest.project.service;

import com.lest.common.security.permission.ProjectRoleChecker;
import java.util.Set;

/**
 * 项目成员 服务层
 *
 * @author yshan2028
 */
public interface IProjectMemberService extends ProjectRoleChecker
{
    /**
     * 查询用户在指定项目中的所有角色
     *
     * @param projectId 项目ID
     * @param userId    用户ID
     * @return 角色集合，如果用户不是项目成员则返回 null 或空集合
     */
    Set<String> getUserRoles(Long projectId, Long userId);

    /**
     * 判断用户是否为项目成员
     *
     * @param projectId 项目ID
     * @param userId    用户ID
     * @return true=是成员
     */
    boolean isMember(Long projectId, Long userId);
}
