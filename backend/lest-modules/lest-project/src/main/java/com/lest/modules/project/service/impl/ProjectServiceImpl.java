package com.lest.modules.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.base.Assert;
import com.lest.modules.project.common.ErrorCode;
import com.lest.common.base.PageResult;
import com.lest.modules.project.entity.domain.Project;
import com.lest.modules.project.entity.domain.ProjectMember;
import com.lest.modules.project.entity.dto.ProjectDTO;
import com.lest.modules.project.entity.dto.ProjectMemberDTO;
import com.lest.modules.project.entity.vo.ProjectMemberVO;
import com.lest.modules.project.entity.vo.ProjectVO;
import com.lest.modules.project.mapper.IterationMapper;
import com.lest.modules.project.mapper.ProjectMapper;
import com.lest.modules.project.mapper.ProjectMemberMapper;
import com.lest.modules.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目服务实现
 */
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final IterationMapper iterationMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper,
                             ProjectMemberMapper projectMemberMapper,
                             IterationMapper iterationMapper) {
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.iterationMapper = iterationMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProjectDTO dto) {
        // 检查项目名唯一性
        LambdaQueryWrapper<Project> nameCheck = Wrappers.lambdaQuery();
        nameCheck.eq(Project::getName, dto.getName());
        Assert.isNull(projectMapper.selectOne(nameCheck), ErrorCode.PROJECT_NAME_EXISTS);

        // 创建项目
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(1); // 默认活跃
        project.setTemplate(dto.getTemplate());
        project.setOwnerId(dto.getOwnerId());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());

        projectMapper.insert(project);

        // 创建者自动成为管理员
        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId());
        member.setUserId(dto.getOwnerId());
        member.setRole("admin");
        projectMemberMapper.insert(member);

        log.info("创建项目: id={}, name={}", project.getId(), dto.getName());
        return project.getId();
    }

    @Override
    public PageResult<ProjectVO> page(Integer page, Integer size, String name, Integer status) {
        LambdaQueryWrapper<Project> query = Wrappers.lambdaQuery();
        if (name != null && !name.isBlank()) {
            query.like(Project::getName, name);
        }
        if (status != null) {
            query.eq(Project::getStatus, status);
        }
        query.orderByDesc(Project::getCreatedAt);

        IPage<Project> iPage = projectMapper.selectPage(new Page<>(page, size), query);
        List<ProjectVO> voList = iPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, iPage.getTotal(), page, size);
    }

    @Override
    public ProjectVO getById(Long id) {
        Project project = projectMapper.selectById(id);
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);
        return convertToVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProjectDTO dto) {
        Assert.notNull(dto.getId(), ErrorCode.PROJECT_NOT_FOUND);

        Project project = projectMapper.selectById(dto.getId());
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);

        // 检查名称唯一性
        if (dto.getName() != null && !dto.getName().equals(project.getName())) {
            LambdaQueryWrapper<Project> nameCheck = Wrappers.lambdaQuery();
            nameCheck.eq(Project::getName, dto.getName());
            Project existing = projectMapper.selectOne(nameCheck);
            Assert.isNull(existing, ErrorCode.PROJECT_NAME_EXISTS);
            project.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            project.setStatus(dto.getStatus());
        }
        if (dto.getTemplate() != null) {
            project.setTemplate(dto.getTemplate());
        }
        if (dto.getOwnerId() != null) {
            project.setOwnerId(dto.getOwnerId());
        }
        if (dto.getStartDate() != null) {
            project.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            project.setEndDate(dto.getEndDate());
        }

        projectMapper.updateById(project);
        log.info("更新项目: id={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Project project = projectMapper.selectById(id);
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);

        // 检查是否有迭代
        int iterationCount = iterationMapper.countByProjectId(id);
        Assert.isTrue(iterationCount == 0, ErrorCode.PROJECT_HAS_ITERATIONS);

        // 检查是否有成员
        int memberCount = projectMemberMapper.countMembersByProjectId(id);
        Assert.isTrue(memberCount == 0, ErrorCode.PROJECT_HAS_MEMBERS);

        projectMapper.deleteById(id);
        log.info("删除项目: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archive(Long id) {
        Project project = projectMapper.selectById(id);
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);
        Assert.isTrue(project.getStatus() != 2, ErrorCode.PROJECT_ARCHIVED);

        project.setStatus(2);
        projectMapper.updateById(project);
        log.info("归档项目: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unarchive(Long id) {
        Project project = projectMapper.selectById(id);
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);
        Assert.isTrue(project.getStatus() == 2, ErrorCode.PROJECT_ARCHIVED);

        project.setStatus(1);
        projectMapper.updateById(project);
        log.info("取消归档项目: id={}", id);
    }

    @Override
    public List<ProjectMemberVO> listMembers(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);

        LambdaQueryWrapper<ProjectMember> query = Wrappers.lambdaQuery();
        query.eq(ProjectMember::getProjectId, projectId);
        List<ProjectMember> members = projectMemberMapper.selectList(query);

        return members.stream().map(this::convertMemberToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long projectId, ProjectMemberDTO dto) {
        Project project = projectMapper.selectById(projectId);
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);

        LambdaQueryWrapper<ProjectMember> query = Wrappers.lambdaQuery();
        query.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, dto.getUserId());
        ProjectMember existing = projectMemberMapper.selectOne(query);
        Assert.isNull(existing, ErrorCode.PROJECT_MEMBER_EXISTS);

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(dto.getUserId());
        member.setRole(dto.getRole());
        projectMemberMapper.insert(member);

        log.info("添加项目成员: projectId={}, userId={}, role={}", projectId, dto.getUserId(), dto.getRole());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long projectId, Long userId) {
        LambdaQueryWrapper<ProjectMember> query = Wrappers.lambdaQuery();
        query.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId);
        ProjectMember member = projectMemberMapper.selectOne(query);
        Assert.notNull(member, ErrorCode.PROJECT_MEMBER_NOT_FOUND);

        // 如果是管理员，检查是否最后一个
        if ("admin".equals(member.getRole())) {
            int adminCount = projectMemberMapper.countAdminsByProjectId(projectId);
            Assert.isTrue(adminCount > 1, ErrorCode.PROJECT_LAST_ADMIN);
        }

        projectMemberMapper.delete(query);
        log.info("移除项目成员: projectId={}, userId={}", projectId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberRole(Long projectId, Long userId, String role) {
        LambdaQueryWrapper<ProjectMember> query = Wrappers.lambdaQuery();
        query.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId);
        ProjectMember member = projectMemberMapper.selectOne(query);
        Assert.notNull(member, ErrorCode.PROJECT_MEMBER_NOT_FOUND);

        // 如果当前是管理员且要改成非管理员，检查是否最后一个
        if ("admin".equals(member.getRole()) && !"admin".equals(role)) {
            int adminCount = projectMemberMapper.countAdminsByProjectId(projectId);
            Assert.isTrue(adminCount > 1, ErrorCode.PROJECT_LAST_ADMIN_ROLE);
        }

        member.setRole(role);
        projectMemberMapper.updateById(member);
        log.info("更新项目成员角色: projectId={}, userId={}, role={}", projectId, userId, role);
    }

    private ProjectVO convertToVO(Project project) {
        return ProjectVO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .template(project.getTemplate())
                .ownerId(project.getOwnerId())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private ProjectMemberVO convertMemberToVO(ProjectMember member) {
        return ProjectMemberVO.builder()
                .projectId(member.getProjectId())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
