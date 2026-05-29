package com.lest.system.api.factory;

import com.lest.common.core.web.domain.R;
import com.lest.system.api.RemoteRoleService;
import com.lest.system.api.domain.SysRole;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色服务降级工厂
 *
 * @author yshan2028
 */
@Slf4j
@Component
public class RemoteRoleFallbackFactory implements FallbackFactory<RemoteRoleService> {

    @Override
    public RemoteRoleService create(Throwable cause) {
        log.error("RemoteRoleService 调用失败: {}", cause.getMessage());
        return new RemoteRoleService() {
            @Override
            public R<SysRole> getById(Long id, String source) {
                return R.fail("获取角色信息失败: " + cause.getMessage());
            }

            @Override
            public R<List<SysRole>> listAll(String source) {
                return R.fail("获取角色列表失败: " + cause.getMessage());
            }

            @Override
            public R<List<Long>> getMenuIds(Long roleId, String source) {
                return R.fail("获取角色菜单ID列表失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> updateMenus(Long roleId, Long[] menuIds, String source) {
                return R.fail("更新角色菜单权限失败: " + cause.getMessage());
            }
        };
    }
}
