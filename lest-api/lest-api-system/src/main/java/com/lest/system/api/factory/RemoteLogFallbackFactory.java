package com.lest.system.api.factory;

import com.lest.common.core.web.domain.R;
import com.lest.system.api.RemoteLogService;
import com.lest.system.api.domain.SysLogininfor;
import com.lest.system.api.domain.SysOperLog;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 日志服务降级工厂
 *
 * @author yshan2028
 */
@Slf4j
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService> {

    @Override
    public RemoteLogService create(Throwable cause) {
        log.error("RemoteLogService 调用失败: {}", cause.getMessage());
        return new RemoteLogService() {
            @Override
            public R<Boolean> saveLoginLog(String username, Integer status, String msg, String ip, String source) {
                return R.fail("记录登录日志失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> saveLoginLogBody(SysLogininfor logininfor, String source) {
                return R.fail("记录登录日志失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> saveOperateLog(String username, String operation, String method, String params, String result, String ip, String source) {
                return R.fail("记录操作日志失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> saveOperateLogBody(SysOperLog operLog, String source) {
                return R.fail("记录操作日志失败: " + cause.getMessage());
            }
        };
    }
}
