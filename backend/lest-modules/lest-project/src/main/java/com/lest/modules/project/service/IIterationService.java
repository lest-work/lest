package com.lest.modules.project.service;

import java.util.List;
import com.lest.modules.project.domain.Iteration;

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

    public int completeIteration(Long iterationId);
}
