package com.lest.system.api.factory;

import com.lest.common.core.web.domain.R;
import com.lest.system.api.RemoteOrganizationService;
import com.lest.system.api.domain.SysDept;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 机构服务降级工厂
 *
 * @author yshan2028
 */
@Slf4j
@Component
public class RemoteOrganizationFallbackFactory implements FallbackFactory<RemoteOrganizationService> {

    @Override
    public RemoteOrganizationService create(Throwable cause) {
        log.error("RemoteOrganizationService 调用失败: {}", cause.getMessage());
        return new RemoteOrganizationService() {
            @Override
            public R<List<SysDept>> getTree(String source) {
                return R.fail("获取机构树失败: " + cause.getMessage());
            }

            @Override
            public R<List<SysDept>> listAll(String source) {
                return R.fail("获取机构列表失败: " + cause.getMessage());
            }

            @Override
            public R<SysDept> getById(Long id, String source) {
                return R.fail("获取机构详情失败: " + cause.getMessage());
            }
        };
    }
}
