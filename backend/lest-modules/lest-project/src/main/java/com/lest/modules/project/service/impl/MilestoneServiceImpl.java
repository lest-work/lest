package com.lest.modules.project.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.modules.project.domain.Milestone;
import com.lest.modules.project.domain.MilestoneIteration;
import com.lest.modules.project.mapper.IterationMapper;
import com.lest.modules.project.mapper.MilestoneIterationMapper;
import com.lest.modules.project.mapper.MilestoneMapper;
import com.lest.modules.project.mapper.ProjectMapper;
import com.lest.modules.project.service.IMilestoneService;

/**
 * 里程碑 服务层实现
 * 
 * @author yshan2028
 */
@Service
public class MilestoneServiceImpl implements IMilestoneService
{
    @Autowired
    private MilestoneMapper milestoneMapper;

    @Autowired
    private MilestoneIterationMapper milestoneIterationMapper;

    @Autowired
    private IterationMapper iterationMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public List<Milestone> selectMilestoneList(Long projectId)
    {
        return milestoneMapper.selectByProjectId(projectId);
    }

    @Override
    public Milestone selectMilestoneById(Long id)
    {
        return milestoneMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertMilestone(Milestone milestone)
    {
        if (projectMapper.selectById(milestone.getProjectId()) == null)
        {
            throw new ServiceException("项目不存在");
        }
        return milestoneMapper.insert(milestone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMilestone(Milestone milestone)
    {
        if (milestoneMapper.selectById(milestone.getId()) == null)
        {
            throw new ServiceException("里程碑不存在");
        }
        return milestoneMapper.updateById(milestone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMilestoneById(Long id)
    {
        if (milestoneMapper.selectById(id) == null)
        {
            throw new ServiceException("里程碑不存在");
        }
        milestoneIterationMapper.deleteByMilestoneId(id);
        return milestoneMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addIteration(Long milestoneId, Long iterationId)
    {
        if (milestoneMapper.selectById(milestoneId) == null)
        {
            throw new ServiceException("里程碑不存在");
        }
        if (iterationMapper.selectById(iterationId) == null)
        {
            throw new ServiceException("迭代不存在");
        }
        MilestoneIteration existing = milestoneIterationMapper.selectByMilestoneIdAndIterationId(milestoneId, iterationId);
        if (existing != null)
        {
            throw new ServiceException("该迭代已关联到此里程碑");
        }
        MilestoneIteration relation = new MilestoneIteration();
        relation.setMilestoneId(milestoneId);
        relation.setIterationId(iterationId);
        return milestoneIterationMapper.insert(relation);
    }

    @Override
    public List<Long> selectIterationIds(Long milestoneId)
    {
        return milestoneIterationMapper.selectIterationIdsByMilestoneId(milestoneId);
    }
}
