package com.lest.system.api.factory;

import com.lest.common.core.web.domain.R;
import com.lest.system.api.RemoteUserService;
import com.lest.system.api.domain.SysUser;
import com.lest.system.api.domain.SysUserVO;
import com.lest.system.api.model.LoginUser;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户服务降级工厂
 *
 * @author yshan2028
 */
@Slf4j
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {

    @Override
    public RemoteUserService create(Throwable cause) {
        log.error("RemoteUserService 调用失败: {}", cause.getMessage());
        return new RemoteUserService() {
            @Override
            public R<LoginUser> getUserInfo(String username, String source) {
                return R.fail("获取用户信息失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> registerUserInfo(SysUser sysUser, String source) {
                return R.fail("注册用户失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> recordUserLogin(SysUser sysUser, String source) {
                return R.fail("记录用户登录失败: " + cause.getMessage());
            }

            @Override
            public R<SysUserVO> getById(Long id, String source) {
                return R.fail("获取用户详情失败: " + cause.getMessage());
            }

            @Override
            public R<List<SysUserVO>> listAll(String source) {
                return R.fail("获取用户列表失败: " + cause.getMessage());
            }

            @Override
            public R<List<Long>> getRoleIds(Long userId, String source) {
                return R.fail("获取用户角色ID列表失败: " + cause.getMessage());
            }
        };
    }
}
