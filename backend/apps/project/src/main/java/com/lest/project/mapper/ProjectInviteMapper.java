package com.lest.project.mapper;

import com.lest.project.domain.ProjectInvite;

/**
 * 项目邀请表 数据层
 */
public interface ProjectInviteMapper {

    int insert(ProjectInvite invite);

    ProjectInvite selectByToken(String token);

    int updateById(ProjectInvite invite);
}
