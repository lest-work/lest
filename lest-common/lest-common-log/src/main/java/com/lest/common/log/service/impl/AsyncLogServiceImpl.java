package com.lest.common.log.service.impl;

import com.lest.common.log.domain.SysOperLog;
import com.lest.common.log.service.AsyncLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步日志服务实现类
 *
 * @author yshan2028
 */
@Service
public class AsyncLogServiceImpl implements AsyncLogService {

    private static final Logger log = LoggerFactory.getLogger(AsyncLogServiceImpl.class);

    /**
     * 异步保存操作日志
     * 注意：实际项目中需要注入 Mapper 来保存到数据库
     * 此处暂时使用日志输出，后续需要注入 SysOperLogMapper
     *
     * @param operLog 操作日志对象
     */
    @Override
    @Async
    public void saveLog(SysOperLog operLog) {
        // TODO: 注入 SysOperLogMapper 保存到数据库
        // 示例: sysOperLogMapper.insert(operLog);
        log.info("异步保存操作日志: {}", operLog);
    }
}
