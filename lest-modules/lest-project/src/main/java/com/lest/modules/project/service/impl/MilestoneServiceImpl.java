package com.lest.modules.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.Assert;
import com.lest.modules.project.common.ErrorCode;
import com.lest.common.core.PageResult;
import com.lest.modules.project.entity.domain.Iteration;
import com.lest.modules.project.entity.domain.Milestone;
import com.lest.modules.project.entity.domain.MilestoneIteration;
import com.lest.modules.project.entity.domain.Project;
import com.lest.modules.project.entity.dto.MilestoneDTO;
import com.lest.modules.project.entity.dto.MilestoneIterationDTO;
import com.lest.modules.project.entity.vo.IterationVO;
import com.lest.modules.project.entity.vo.MilestoneVO;
import com.lest.modules.project.mapper.IterationMapper;
import com.lest.modules.project.mapper.MilestoneIterationMapper;
import com.lest.modules.project.mapper.MilestoneMapper;
import com.lest.modules.project.mapper.ProjectMapper;
import com.lest.modules.project.service.MilestoneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 里程碑服务实现
 */
@Slf4j
@Service
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneMapper milestoneMapper;
    private final MilestoneIterationMapper milestoneIterationMapper;
    private final IterationMapper iterationMapper;
    private final ProjectMapper projectMapper;

    public MilestoneServiceImpl(MilestoneMapper milestoneMapper,
                                MilestoneIterationMapper milestoneIterationMapper,
                                IterationMapper iterationMapper,
                                ProjectMapper projectMapper) {
        this.milestoneMapper = milestoneMapper;
        this.milestoneIterationMapper = milestoneIterationMapper;
        this.iterationMapper = iterationMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MilestoneDTO dto) {
        // 检查项目存在
        Project project = projectMapper.selectById(dto.getProjectId());
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);

        Milestone milestone = new Milestone();
        milestone.setProjectId(dto.getProjectId());
        milestone.setName(dto.getName());
        milestone.setDescription(dto.getDescription());
        milestone.setTargetDate(dto.getTargetDate());

        milestoneMapper.insert(milestone);
        log.info("创建里程碑: id={}, name={}", milestone.getId(), dto.getName());
        return milestone.getId();
    }

    @Override
    public PageResult<MilestoneVO> page(Long projectId, Integer page, Integer size) {
        Project project = projectMapper.selectById(projectId);
        Assert.notNull(project, ErrorCode.PROJECT_NOT_FOUND);

        LambdaQueryWrapper<Milestone> query = Wrappers.lambdaQuery();
        query.eq(Milestone::getProjectId, projectId);
        query.orderByAsc(Milestone::getTargetDate);

        IPage<Milestone> iPage = milestoneMapper.selectPage(new Page<>(page, size), query);
        List<MilestoneVO> voList = iPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, iPage.getTotal(), page, size);
    }

    @Override
    public MilestoneVO getById(Long id) {
        Milestone milestone = milestoneMapper.selectById(id);
        Assert.notNull(milestone, ErrorCode.MILESTONE_NOT_FOUND);
        return convertToVO(milestone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MilestoneDTO dto) {
        Assert.notNull(dto.getId(), ErrorCode.MILESTONE_NOT_FOUND);

        Milestone milestone = milestoneMapper.selectById(dto.getId());
        Assert.notNull(milestone, ErrorCode.MILESTONE_NOT_FOUND);

        if (dto.getName() != null) {
            milestone.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            milestone.setDescription(dto.getDescription());
        }
        if (dto.getTargetDate() != null) {
            milestone.setTargetDate(dto.getTargetDate());
        }

        milestoneMapper.updateById(milestone);
        log.info("更新里程碑: id={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Milestone milestone = milestoneMapper.selectById(id);
        Assert.notNull(milestone, ErrorCode.MILESTONE_NOT_FOUND);

        // 删除关联关系
        milestoneIterationMapper.deleteByMilestoneId(id);
        // 删除里程碑
        milestoneMapper.deleteById(id);
        log.info("删除里程碑: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addIteration(Long milestoneId, MilestoneIterationDTO dto) {
        Milestone milestone = milestoneMapper.selectById(milestoneId);
        Assert.notNull(milestone, ErrorCode.MILESTONE_NOT_FOUND);

        Iteration iteration = iterationMapper.selectById(dto.getIterationId());
        Assert.notNull(iteration, ErrorCode.ITERATION_NOT_FOUND);

        // 检查关联是否已存在
        LambdaQueryWrapper<MilestoneIteration> query = Wrappers.lambdaQuery();
        query.eq(MilestoneIteration::getMilestoneId, milestoneId)
                .eq(MilestoneIteration::getIterationId, dto.getIterationId());
        MilestoneIteration existing = milestoneIterationMapper.selectOne(query);
        Assert.isNull(existing, ErrorCode.PROJECT_MEMBER_EXISTS);

        MilestoneIteration relation = new MilestoneIteration();
        relation.setMilestoneId(milestoneId);
        relation.setIterationId(dto.getIterationId());
        milestoneIterationMapper.insert(relation);

        log.info("里程碑关联迭代: milestoneId={}, iterationId={}", milestoneId, dto.getIterationId());
    }

    @Override
    public List<Long> getIterationIds(Long milestoneId) {
        Milestone milestone = milestoneMapper.selectById(milestoneId);
        Assert.notNull(milestone, ErrorCode.MILESTONE_NOT_FOUND);

        return milestoneIterationMapper.selectIterationIdsByMilestoneId(milestoneId);
    }

    private MilestoneVO convertToVO(Milestone milestone) {
        List<Long> iterationIds = milestoneIterationMapper.selectIterationIdsByMilestoneId(milestone.getId());
        List<IterationVO> iterations = Collections.emptyList();
        if (!iterationIds.isEmpty()) {
            iterations = iterationIds.stream()
                    .map(id -> {
                        Iteration iter = iterationMapper.selectById(id);
                        if (iter != null) {
                            return IterationVO.builder()
                                    .id(iter.getId())
                                    .projectId(iter.getProjectId())
                                    .name(iter.getName())
                                    .goal(iter.getGoal())
                                    .status(iter.getStatus())
                                    .startDate(iter.getStartDate())
                                    .endDate(iter.getEndDate())
                                    .completedAt(iter.getCompletedAt())
                                    .createdAt(iter.getCreatedAt())
                                    .updatedAt(iter.getUpdatedAt())
                                    .build();
                        }
                        return null;
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return MilestoneVO.builder()
                .id(milestone.getId())
                .projectId(milestone.getProjectId())
                .name(milestone.getName())
                .description(milestone.getDescription())
                .targetDate(milestone.getTargetDate())
                .iterations(iterations)
                .createdAt(milestone.getCreatedAt())
                .updatedAt(milestone.getUpdatedAt())
                .build();
    }
}
