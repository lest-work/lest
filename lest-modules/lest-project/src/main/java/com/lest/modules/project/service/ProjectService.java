package com.lest.modules.project.service;

import com.lest.common.core.PageResult;
import com.lest.modules.project.entity.dto.ProjectDTO;
import com.lest.modules.project.entity.dto.ProjectMemberDTO;
import com.lest.modules.project.entity.vo.ProjectMemberVO;
import com.lest.modules.project.entity.vo.ProjectVO;

import java.util.List;

/**
 * 项目服务接口
 */
public interface ProjectService {

    /**
     * 创建项目
     */
    Long create(ProjectDTO dto);

    /**
     * 分页查询项目
     */
    PageResult<ProjectVO> page(Integer page, Integer size, String name, Integer status);

    /**
     * 获取项目详情
     */
    ProjectVO getById(Long id);

    /**
     * 更新项目
     */
    void update(ProjectDTO dto);

    /**
     * 删除项目
     */
    void delete(Long id);

    /**
     * 归档项目
     */
    void archive(Long id);

    /**
     * 取消归档项目
     */
    void unarchive(Long id);

    /**
     * 获取项目成员列表
     */
    List<ProjectMemberVO> listMembers(Long projectId);

    /**
     * 添加项目成员
     */
    void addMember(Long projectId, ProjectMemberDTO dto);

    /**
     * 移除项目成员
     */
    void removeMember(Long projectId, Long userId);

    /**
     * 修改项目成员角色
     */
    void updateMemberRole(Long projectId, Long userId, String role);
}
