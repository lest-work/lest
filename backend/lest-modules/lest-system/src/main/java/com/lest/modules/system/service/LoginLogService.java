package com.lest.modules.system.service;

import com.lest.common.base.PageResult;
import com.lest.modules.system.entity.dto.LoginLogDTO;
import com.lest.modules.system.entity.dto.LoginLogQueryDTO;
import com.lest.modules.system.entity.vo.LoginLogVO;

import java.util.List;

/**
 * 登录日志服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface LoginLogService {

    /**
     * 分页查询登录日志
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<LoginLogVO> page(LoginLogQueryDTO dto);

    /**
     * 根据ID查询登录日志
     *
     * @param id 日志ID
     * @return 登录日志
     */
    LoginLogVO getById(Long id);

    /**
     * 删除登录日志（单个或批量）
     *
     * @param id  单个日志ID
     * @param ids 批量删除时传入的ID列表，ids不为空时执行批量删除
     */
    void delete(Long id, List<Long> ids);

    /**
     * 统计登录日志数量
     *
     * @param username  用户名（模糊匹配）
     * @param loginType 登录类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 日志数量
     */
    long count(String username, Integer loginType, String startTime, String endTime);

    /**
     * 保存登录日志（供认证服务调用）
     *
     * @param dto 登录日志信息
     */
    void save(LoginLogDTO dto);
}
