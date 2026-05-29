package com.lest.modules.job.service;

import com.lest.common.core.PageResult;
import com.lest.modules.job.entity.domain.SysJob;
import com.lest.modules.job.entity.domain.SysJobLog;

import java.util.List;

/**
 * 定时任务服务接口
 *
 * @author yshan2028
 */
public interface JobService {

    /**
     * 分页查询
     */
    PageResult<SysJob> page(Integer page, Integer size, String jobName, String jobGroup, Integer status);

    /**
     * 查询所有
     */
    List<SysJob> list(SysJob job);

    /**
     * 根据ID查询
     */
    SysJob getById(Long jobId);

    /**
     * 新增定时任务
     */
    Long add(SysJob job);

    /**
     * 修改定时任务
     */
    void update(SysJob job);

    /**
     * 删除定时任务
     */
    void delete(Long[] jobIds);

    /**
     * 立即执行一次
     */
    void run(Long jobId);

    /**
     * 暂停任务
     */
    void pause(Long jobId);

    /**
     * 恢复任务
     */
    void resume(Long jobId);

    /**
     * 分页查询执行日志
     */
    PageResult<SysJobLog> logPage(Integer page, Integer size, Long jobId);

    /**
     * 清空执行日志
     */
    void cleanLog();
}
