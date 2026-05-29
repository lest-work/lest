package com.lest.modules.project.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.common.core.utils.StringUtils;
import com.lest.modules.project.domain.Project;
import com.lest.modules.project.domain.ProjectMember;
import com.lest.modules.project.mapper.IterationMapper;
import com.lest.modules.project.mapper.ProjectMapper;
import com.lest.modules.project.mapper.ProjectMemberMapper;
import com.lest.modules.project.service.IProjectService;

/**
 * 项目 服务层实现
 * 
 * @author yshan2028
 */
@Service
public class ProjectServiceImpl implements IProjectService
{
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private IterationMapper iterationMapper;

    @Override
    public List<Project> selectProjectList(Project project)
    {
        return projectMapper.selectProjectList(project);
    }

    @Override
    public Project selectProjectById(Long id)
    {
        return projectMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertProject(Project project)
    {
        Project existing = projectMapper.selectByName(project.getName());
        if (existing != null)
        {
            throw new ServiceException("项目名称'" + project.getName() + "'已存在");
        }
        project.setStatus(1);
        int rows = projectMapper.insert(project);
        // 创建者自动成为管理员
        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId());
        member.setUserId(project.getOwnerId());
        member.setRole("admin");
        projectMemberMapper.insert(member);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProject(Project project)
    {
        Project existing = projectMapper.selectById(project.getId());
        if (existing == null)
        {
            throw new ServiceException("项目不存在");
        }
        if (project.getName() != null && !project.getName().equals(existing.getName()))
        {
            Project nameCheck = projectMapper.selectByName(project.getName());
            if (nameCheck != null)
            {
                throw new ServiceException("项目名称'" + project.getName() + "'已存在");
            }
        }
        return projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProjectById(Long id)
    {
        Project project = projectMapper.selectById(id);
        if (project == null)
        {
            throw new ServiceException("项目不存在");
        }
        int iterationCount = iterationMapper.countByProjectId(id);
        if (iterationCount > 0)
        {
            throw new ServiceException("项目下存在迭代，无法删除");
        }
        int memberCount = projectMemberMapper.countMembersByProjectId(id);
        if (memberCount > 0)
        {
            throw new ServiceException("项目下存在成员，无法删除");
        }
        return projectMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int archiveProject(Long id)
    {
        Project project = projectMapper.selectById(id);
        if (project == null)
        {
            throw new ServiceException("项目不存在");
        }
        if (project.getStatus() == 2)
        {
            throw new ServiceException("项目已处于归档状态");
        }
        project.setStatus(2);
        return projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unarchiveProject(Long id)
    {
        Project project = projectMapper.selectById(id);
        if (project == null)
        {
            throw new ServiceException("项目不存在");
        }
        if (project.getStatus() != 2)
        {
            throw new ServiceException("项目未处于归档状态");
        }
        project.setStatus(1);
        return projectMapper.updateById(project);
    }

    @Override
    public List<ProjectMember> selectMembersByProjectId(Long projectId)
    {
        return projectMemberMapper.selectByProjectId(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertMember(ProjectMember member)
    {
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
        if ("admin".equals(member.getRole()))
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
        ProjectMember member = projectMemberMapper.selectByProjectIdAndUserId(projectId, userId);
        if (member == null)
        {
            throw new ServiceException("项目成员不存在");
        }
        if ("admin".equals(member.getRole()) && !"admin".equals(role))
        {
            int adminCount = projectMemberMapper.countAdminsByProjectId(projectId);
            if (adminCount <= 1)
            {
                throw new ServiceException("不能修改最后一个管理员的角色");
            }
        }
        member.setRole(role);
        return projectMemberMapper.updateById(member);
    }
}
