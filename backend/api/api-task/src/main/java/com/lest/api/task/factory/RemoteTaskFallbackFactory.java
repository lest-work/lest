package com.lest.api.task.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.lest.common.core.domain.R;
import com.lest.api.task.RemoteTaskService;

/**
 * 任务服务降级处理
 *
 * @author yshan2028
 */
@Component
public class RemoteTaskFallbackFactory implements FallbackFactory<RemoteTaskService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteTaskFallbackFactory.class);

    @Override
    public RemoteTaskService create(Throwable throwable)
    {
        log.error("任务服务调用失败:{}", throwable.getMessage());
        return new RemoteTaskService()
        {
            @Override
            public R<Object> getTasksByIteration(Long iterationId, String source)
            {
                return R.fail("获取迭代任务失败:" + throwable.getMessage());
            }

            @Override
            public R<Integer> moveUnfinishedTasks(Long fromIterationId, Long toIterationId, String source)
            {
                return R.fail("移动未完成任务失败:" + throwable.getMessage());
            }
        };
    }
}
