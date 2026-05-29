package com.lest.common.log.service;

import com.lest.common.log.domain.SysOperLog;

/**
 * 异步日志服务接口
 *
 * @author yshan2028
 */
public interface AsyncLogService {

    /**
     * 保存操作日志到数据库
     *
     * @param operLog 操作日志对象
     */
    void saveLog(SysOperLog operLog);
}
