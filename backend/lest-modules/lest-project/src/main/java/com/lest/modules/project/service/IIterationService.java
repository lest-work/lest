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
    /**
     * 查询迭代列表
     */
    public List<Iteration> selectIterationList(Long projectId, Integer status);

    /**
     * 查询迭代详情
     */
    public Iteration selectIterationById(Long id);

    /**
     * 新增迭代
     */
    public int insertIteration(Iteration iteration);

    /**
     * 修改迭代
     */
    public int updateIteration(Iteration iteration);

    /**
     * 删除迭代
     */
    public int deleteIterationById(Long id);

    /**
     * 启动迭代
     */
    public int startIteration(Long id);

    /**
     * 完成迭代
     */
    public int completeIteration(Long id);
}
