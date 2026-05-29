package com.lest.system.api.factory;

import com.lest.common.core.web.domain.R;
import com.lest.system.api.RemoteMenuService;
import com.lest.system.api.domain.SysMenu;
import com.lest.system.api.domain.vo.SysMenuVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 菜单服务降级工厂
 *
 * @author yshan2028
 */
@Slf4j
@Component
public class RemoteMenuFallbackFactory implements FallbackFactory<RemoteMenuService> {

    @Override
    public RemoteMenuService create(Throwable cause) {
        log.error("RemoteMenuService 调用失败: {}", cause.getMessage());
        return new RemoteMenuService() {
            @Override
            public R<List<SysMenuVO>> getTree(String source) {
                return R.fail("获取菜单树失败: " + cause.getMessage());
            }

            @Override
            public R<List<SysMenuVO>> listAll(String source) {
                return R.fail("获取菜单列表失败: " + cause.getMessage());
            }

            @Override
            public R<SysMenuVO> getById(Long id, String source) {
                return R.fail("获取菜单详情失败: " + cause.getMessage());
            }

            @Override
            public R<List<SysMenu>> getRoutes(String source) {
                return R.fail("获取路由列表失败: " + cause.getMessage());
            }
        };
    }
}
