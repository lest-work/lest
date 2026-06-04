package com.lest.project.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lest.common.core.exception.ServiceException;
import com.lest.project.domain.Iteration;
import com.lest.api.task.RemoteTaskService;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.project.mapper.IterationMapper;
import com.lest.project.mapper.ProjectMapper;
import com.lest.project.service.IIterationService;

/**
 * 迭代 服务层实现
 *
 * @author yshan2028
 */
@Service
public class IterationServiceImpl implements IIterationService
{
    public static final int STATUS_PLANNING = 1;
    public static final int STATUS_IN_PROGRESS = 2;
    public static final int STATUS_COMPLETED = 3;

    @Autowired
    private IterationMapper iterationMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RemoteTaskService remoteTaskService;

    @Override
    public List<Iteration> selectIterationList(Long projectId, Integer status)
    {
        return iterationMapper.selectIterationList(projectId, status);
    }

    @Override
    public Iteration selectIterationById(Long iterationId)
    {
        return iterationMapper.selectById(iterationId);
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
        iteration.setStatus(STATUS_PLANNING);
        return iterationMapper.insert(iteration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateIteration(Iteration iteration)
    {
        Iteration existing = iterationMapper.selectById(iteration.getIterationId());
        if (existing == null)
        {
            throw new ServiceException("迭代不存在");
        }
        if (iteration.getStartDate() != null && iteration.getEndDate() != null)
        {
            int conflictCount = iterationMapper.countDateConflicts(
                    existing.getProjectId(), iteration.getStartDate(), iteration.getEndDate(), iteration.getIterationId());
            if (conflictCount > 0)
            {
                throw new ServiceException("迭代日期与现有迭代存在冲突");
            }
        }
        return iterationMapper.updateById(iteration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteIterationById(Long iterationId)
    {
        Iteration iteration = iterationMapper.selectById(iterationId);
        if (iteration == null)
        {
            throw new ServiceException("迭代不存在");
        }
        return iterationMapper.deleteById(iterationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int startIteration(Long iterationId)
    {
        Iteration iteration = iterationMapper.selectById(iterationId);
        if (iteration == null)
        {
            throw new ServiceException("迭代不存在");
        }
        if (iteration.getStatus() != STATUS_PLANNING)
        {
            throw new ServiceException("只能启动计划中的迭代");
        }
        iteration.setStatus(STATUS_IN_PROGRESS);
        iteration.setStartDate(new Date());
        return iterationMapper.updateById(iteration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeIteration(Long iterationId, Map<String, Object> params)
    {
        Iteration iteration = iterationMapper.selectById(iterationId);
        if (iteration == null)
        {
            throw new ServiceException("迭代不存在");
        }
        if (iteration.getStatus() != STATUS_IN_PROGRESS)
        {
            throw new ServiceException("只能完成进行中的迭代");
        }
        // PRD: includeIncomplete=true 时将未完成任务移到下一个迭代
        Boolean includeIncomplete = params != null ? (Boolean) params.get("includeIncomplete") : false;
        Long nextIterationId = params != null ? ((Number) params.getOrDefault("nextIterationId", 0)).longValue() : 0L;
        if (includeIncomplete && nextIterationId != null && nextIterationId > 0)
        {
            // 通过 Feign 调用 lest-task 将当前迭代未完成任务移到下一个迭代
            remoteTaskService.moveUnfinishedTasks(iterationId, nextIterationId, SecurityConstants.INNER);
        }
        iteration.setStatus(STATUS_COMPLETED);
        iteration.setCompletedAt(new Date());
        return iterationMapper.updateById(iteration);
    }

    @Override
    public Object getIterationTasks(Long iterationId)
    {
        // 通过 Feign 调用 lest-task 获取迭代任务，返回 Object 透传 JSON 给前端
        return remoteTaskService.getTasksByIteration(iterationId, SecurityConstants.INNER).getData();
    }
}
