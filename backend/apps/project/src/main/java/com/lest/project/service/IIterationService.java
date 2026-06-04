package com.lest.project.service;

import java.util.List;
import java.util.Map;
import com.lest.project.domain.Iteration;

/**
 * 迭代 服务层
 *
 * @author yshan2028
 */
public interface IIterationService
{
    public List<Iteration> selectIterationList(Long projectId, Integer status);

    public Iteration selectIterationById(Long iterationId);

    public int insertIteration(Iteration iteration);

    public int updateIteration(Iteration iteration);

    public int deleteIterationById(Long iterationId);

    public int startIteration(Long iterationId);

    public int completeIteration(Long iterationId, Map<String, Object> params);

    /**
     * 获取迭代下的任务列表（通过 Feign 调用 lest-task，返回类型为 Object 避免引入 Task 领域类）
     */
    public Object getIterationTasks(Long iterationId);
}
