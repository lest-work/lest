package com.lest.modules.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.base.Assert;
import com.lest.modules.project.common.ErrorCode;
import com.lest.common.base.PageResult;
import com.lest.modules.project.entity.domain.Iteration;
import com.lest.modules.project.entity.domain.Project;
import com.lest.modules.project.entity.dto.IterationDTO;
import com.lest.modules.project.entity.vo.IterationVO;
import com.lest.modules.project.mapper.IterationMapper;
import com.lest.modules.project.mapper.ProjectMapper;
import com.lest.modules.project.service.IterationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 迭代服务实现
 */
@Slf4j
@Service
public class IterationServiceImpl implements IterationService {

    private final IterationMapper iterationMapper;
    private final ProjectMapper projectMapper;

    public IterationServiceImpl(IterationMapper iterationMapper,
                                ProjectMapper projectMapper) {
        this.iterationMapper = iterationMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(IterationDTO dto) {
        // 检查项目存在
        Project project = projectMapper.selectById(dto.getProjectId());
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);

        // 检查时间冲突
        checkDateConflict(dto.getProjectId(), dto.getStartDate(), dto.getEndDate(), null);

        Iteration iteration = new Iteration();
        iteration.setProjectId(dto.getProjectId());
        iteration.setName(dto.getName());
        iteration.setGoal(dto.getGoal());
        iteration.setStatus(1); // 默认计划中
        iteration.setStartDate(dto.getStartDate());
        iteration.setEndDate(dto.getEndDate());

        iterationMapper.insert(iteration);
        log.info("创建迭代: id={}, name={}", iteration.getId(), dto.getName());
        return iteration.getId();
    }

    @Override
    public PageResult<IterationVO> page(Long projectId, Integer status, Integer page, Integer size) {
        Project project = projectMapper.selectById(projectId);
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);

        LambdaQueryWrapper<Iteration> query = Wrappers.lambdaQuery();
        query.eq(Iteration::getProjectId, projectId);
        if (status != null) {
            query.eq(Iteration::getStatus, status);
        }
        query.orderByDesc(Iteration::getCreatedAt);

        IPage<Iteration> iPage = iterationMapper.selectPage(new Page<>(page, size), query);
        List<IterationVO> voList = iPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        return PageResult.of(voList, iPage.getTotal(), page, size);
    }

    @Override
    public IterationVO getById(Long id) {
        Iteration iteration = iterationMapper.selectById(id);
        Assert.notNull(iteration, ErrorCode.ITERATION_NOT_FOUND);
        return convertToVO(iteration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(IterationDTO dto) {
        Assert.notNull(dto.getId(), ErrorCode.ITERATION_NOT_FOUND);

        Iteration iteration = iterationMapper.selectById(dto.getId());
        Assert.notNull(iteration, ErrorCode.ITERATION_NOT_FOUND);

        // 检查时间冲突
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            checkDateConflict(iteration.getProjectId(), dto.getStartDate(), dto.getEndDate(), dto.getId());
        }

        if (dto.getName() != null) {
            iteration.setName(dto.getName());
        }
        if (dto.getGoal() != null) {
            iteration.setGoal(dto.getGoal());
        }
        if (dto.getStartDate() != null) {
            iteration.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            iteration.setEndDate(dto.getEndDate());
        }

        iterationMapper.updateById(iteration);
        log.info("更新迭代: id={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Iteration iteration = iterationMapper.selectById(id);
        Assert.notNull(iteration, ErrorCode.ITERATION_NOT_FOUND);

        iterationMapper.deleteById(id);
        log.info("删除迭代: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id) {
        Iteration iteration = iterationMapper.selectById(id);
        Assert.notNull(iteration, ErrorCode.ITERATION_NOT_FOUND);
        Assert.isTrue(iteration.getStatus() == 1, ErrorCode.ITERATION_ALREADY_ACTIVE);

        iteration.setStatus(2); // 进行中
        iterationMapper.updateById(iteration);
        log.info("启动迭代: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        Iteration iteration = iterationMapper.selectById(id);
        Assert.notNull(iteration, ErrorCode.ITERATION_NOT_FOUND);
        Assert.isTrue(iteration.getStatus() == 2, ErrorCode.ITERATION_ALREADY_COMPLETED);

        iteration.setStatus(3); // 已完成
        iteration.setCompletedAt(LocalDateTime.now());
        iterationMapper.updateById(iteration);
        log.info("完成迭代: id={}", id);
    }

    @Override
    public List<Long> getTaskIds(Long iterationId) {
        Iteration iteration = iterationMapper.selectById(iterationId);
        Assert.notNull(iteration, ErrorCode.ITERATION_NOT_FOUND);
        // TODO: 关联任务表获取任务ID列表
        return Collections.emptyList();
    }

    private void checkDateConflict(Long projectId, java.time.LocalDate startDate,
                                   java.time.LocalDate endDate, Long excludeId) {
        int conflictCount = iterationMapper.countDateConflicts(projectId, startDate, endDate, excludeId != null ? excludeId : 0L);
        Assert.isTrue(conflictCount == 0, ErrorCode.ITERATION_DATE_CONFLICT);
    }

    private IterationVO convertToVO(Iteration iteration) {
        return IterationVO.builder()
                .id(iteration.getId())
                .projectId(iteration.getProjectId())
                .name(iteration.getName())
                .goal(iteration.getGoal())
                .status(iteration.getStatus())
                .startDate(iteration.getStartDate())
                .endDate(iteration.getEndDate())
                .completedAt(iteration.getCompletedAt())
                .createdAt(iteration.getCreatedAt())
                .updatedAt(iteration.getUpdatedAt())
                .build();
    }
}
