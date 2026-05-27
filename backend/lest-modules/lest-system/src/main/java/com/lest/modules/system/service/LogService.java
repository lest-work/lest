package com.lest.modules.system.service;

import com.lest.common.base.PageResult;
import com.lest.modules.system.entity.dto.LogQueryDTO;
import com.lest.modules.system.entity.vo.LogVO;

import java.util.List;

/**
 * 操作日志服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface LogService {

    /**
     * 分页查询操作日志
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<LogVO> page(LogQueryDTO dto);

    /**
     * 根据ID查询操作日志
     *
     * @param id 日志ID
     * @return 操作日志
     */
    LogVO getById(Long id);

    /**
     * 删除操作日志（单个或批量）
     *
     * @param id  单个日志ID
     * @param ids 批量删除时传入的ID列表，ids不为空时执行批量删除
     */
    void delete(Long id, List<Long> ids);

    /**
     * 统计操作日志数量
     *
     * @param operation 操作类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 日志数量
     */
    long count(String operation, String startTime, String endTime);
}
