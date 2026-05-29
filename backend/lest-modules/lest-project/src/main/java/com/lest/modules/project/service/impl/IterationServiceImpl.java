package com.lest.modules.project.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.modules.project.domain.Iteration;
import com.lest.modules.project.mapper.IterationMapper;
import com.lest.modules.project.mapper.ProjectMapper;
import com.lest.modules.project.service.IIterationService;

/**
 * 迭代 服务层实现
 * 
 * @author yshan2028
 */
@Service
public class IterationServiceImpl implements IIterationService
{
    @Autowired
    private IterationMapper iterationMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public List<Iteration> selectIterationList(Long projectId, Integer status)
    {
        return iterationMapper.selectIterationList(projectId, status);
    }

    @Override
    public Iteration selectIterationById(Long id)
    {
        return iterationMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertIteration(Iteration iteration)
    {
        if (projectMapper.selectById(iteration.getProjectId()) == null)
        {
            throw new ServiceException("项目不存在");
        }
        if (iteration.getStartDate() != null && iteration.getEndDate() != null)
        {
            int conflictCount = iterationMapper.countDateConflicts(
                    iteration.getProjectId(), iteration.getStartDate(), iteration.getEndDate(), 0L);
            if (conflictCount > 0)
            {
                throw new ServiceException("迭代日期与现有迭代存在冲突");
            }
        }
        iteration.setStatus(1);
        return iterationMapper.insert(iteration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateIteration(Iteration iteration)
    {
        Iteration existing = iterationMapper.selectById(iteration.getId());
        if (existing == null)
        {
            throw new ServiceException("迭代不存在");
        }
        if (iteration.getStartDate() != null && iteration.getEndDate() != null)
        {
            int conflictCount = iterationMapper.countDateConflicts(
                    existing.getProjectId(), iteration.getStartDate(), iteration.getEndDate(), iteration.getId());
            if (conflictCount > 0)
            {
                throw new ServiceException("迭代日期与现有迭代存在冲突");
            }
        }
        return iterationMapper.updateById(iteration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteIterationById(Long id)
    {
        Iteration iteration = iterationMapper.selectById(id);
        if (iteration == null)
        {
            throw new ServiceException("迭代不存在");
        }
        return iterationMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int startIteration(Long id)
    {
        Iteration iteration = iterationMapper.selectById(id);
        if (iteration == null)
        {
            throw new ServiceException("迭代不存在");
        }
        if (iteration.getStatus() != 1)
        {
            throw new ServiceException("只能启动计划中的迭代");
        }
        iteration.setStatus(2);
        return iterationMapper.updateById(iteration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeIteration(Long id)
    {
        Iteration iteration = iterationMapper.selectById(id);
        if (iteration == null)
        {
            throw new ServiceException("迭代不存在");
        }
        if (iteration.getStatus() != 2)
        {
            throw new ServiceException("只能完成进行中的迭代");
        }
        iteration.setStatus(3);
        iteration.setCompletedAt(new Date());
        return iterationMapper.updateById(iteration);
    }
}
