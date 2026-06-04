package com.lest.api.task;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.constant.ServiceNameConstants;
import com.lest.common.core.domain.R;
import com.lest.api.task.factory.RemoteTaskFallbackFactory;

/**
 * 任务服务 Feign 接口
 * <p>
 * lest-project 通过此接口与 lest-task 跨服务通信，
 * 避免直接注入 TaskMapper 导致的服务边界违规。
 * </p>
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteTaskService",
        value = ServiceNameConstants.TASK_SERVICE,
        url = "${feign.task.url:}",
        fallbackFactory = RemoteTaskFallbackFactory.class)
public interface RemoteTaskService
{
    /**
     * 查询某迭代下的所有任务
     *
     * @param iterationId 迭代ID
     * @param source      请求来源标识（内部调用时传 SecurityConstants.INNER）
     * @return 任务列表（JSON 透传，不依赖 Task 领域类）
     */
    @GetMapping("/task/iteration/{iterationId}")
    R<Object> getTasksByIteration(@PathVariable("iterationId") Long iterationId,
                                  @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 将某迭代中未完成的任务移入下一个迭代（完成迭代时调用）
     *
     * @param fromIterationId 当前迭代ID
     * @param toIterationId   目标迭代ID
     * @param source          请求来源标识
     * @return 影响行数
     */
    @PutMapping("/task/iteration/{fromId}/move-to/{toId}")
    R<Integer> moveUnfinishedTasks(@PathVariable("fromId") Long fromIterationId,
                                   @PathVariable("toId") Long toIterationId,
                                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
