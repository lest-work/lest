package com.lest.modules.system.service;

import com.lest.common.core.PageResult;
import com.lest.modules.system.entity.vo.OnlineUserVO;

import java.util.List;

/**
 * 在线用户服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface OnlineUserService {

    /**
     * 分页查询在线用户
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<OnlineUserVO> page(Integer page, Integer size);

    /**
     * 查询所有在线用户
     *
     * @return 在线用户列表
     */
    List<OnlineUserVO> list();

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数
     */
    long count();

    /**
     * 踢出在线用户（单个或批量）
     *
     * @param sessionIds 会话ID列表，传入空列表时踢出全部用户
     */
    void kickout(List<String> sessionIds);
}
