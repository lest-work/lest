package com.lest.system.api.factory;

import com.lest.common.core.web.domain.R;
import com.lest.system.api.RemoteDeptService;
import com.lest.system.api.domain.SysDept;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 部门服务降级工厂
 *
 * @author yshan2028
 */
@Slf4j
@Component
public class RemoteDeptFallbackFactory implements FallbackFactory<RemoteDeptService> {

    @Override
    public RemoteDeptService create(Throwable cause) {
        log.error("RemoteDeptService 调用失败: {}", cause.getMessage());
        return new RemoteDeptService() {
            @Override
            public R<List<SysDept>> getTree(String source) {
                return R.fail("获取部门树失败: " + cause.getMessage());
            }

            @Override
            public R<List<SysDept>> listAll(String source) {
                return R.fail("获取部门列表失败: " + cause.getMessage());
            }

            @Override
            public R<SysDept> getById(Long id, String source) {
                return R.fail("获取部门详情失败: " + cause.getMessage());
            }

            @Override
            public R<Long> create(SysDept dept, String source) {
                return R.fail("新增部门失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> update(SysDept dept, String source) {
                return R.fail("修改部门失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> delete(Long id, String source) {
                return R.fail("删除部门失败: " + cause.getMessage());
            }
        };
    }
}
