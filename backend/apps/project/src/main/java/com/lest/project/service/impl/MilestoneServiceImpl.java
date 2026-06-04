package com.lest.project.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.project.domain.Milestone;
import com.lest.project.domain.MilestoneIteration;
import com.lest.project.mapper.IterationMapper;
import com.lest.project.mapper.MilestoneIterationMapper;
import com.lest.project.mapper.MilestoneMapper;
import com.lest.project.mapper.ProjectMapper;
import com.lest.project.service.IMilestoneService;

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
        List<Milestone> milestones = milestoneMapper.selectByProjectId(projectId);
        for (Milestone m : milestones)
        {
            calculateProgress(m);
        }
        return milestones;
    }

    private void calculateProgress(Milestone milestone)
    {
        List<Long> iterationIds = milestoneIterationMapper.selectIterationIdsByMilestoneId(milestone.getMilestoneId());
        if (iterationIds == null || iterationIds.isEmpty())
        {
            milestone.setProgress(0);
            milestone.setTotalTaskCount(0);
            milestone.setCompletedTaskCount(0);
            return;
        }
        int total = 0;
        int completed = 0;
        for (Long iterationId : iterationIds)
        {
            total += milestoneMapper.countTasksByIterationId(iterationId);
            completed += milestoneMapper.countCompletedTasksByIterationId(iterationId);
        }
        milestone.setTotalTaskCount(total);
        milestone.setCompletedTaskCount(completed);
        milestone.setProgress(total > 0 ? (int) (completed * 100.0 / total) : 0);
    }

    @Override
    public Milestone selectMilestoneById(Long milestoneId)
    {
        return milestoneMapper.selectById(milestoneId);
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
        if (milestoneMapper.selectById(milestone.getMilestoneId()) == null)
        {
            throw new ServiceException("里程碑不存在");
        }
        return milestoneMapper.updateById(milestone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMilestoneById(Long milestoneId)
    {
        Milestone milestone = milestoneMapper.selectById(milestoneId);
        if (milestone == null)
        {
            throw new ServiceException("里程碑不存在");
        }
        int iterationCount = milestoneIterationMapper.countIterationsByMilestoneId(milestoneId);
        if (iterationCount > 0)
        {
            throw new ServiceException("该里程碑已关联 " + iterationCount + " 个迭代，请先取消关联后再删除");
        }
        milestoneMapper.deleteById(milestoneId, SecurityUtils.getUsername());
        return 1;
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
