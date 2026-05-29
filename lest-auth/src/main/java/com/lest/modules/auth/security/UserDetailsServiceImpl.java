package com.lest.modules.auth.security;

import com.lest.modules.auth.common.ErrorCode;
import com.lest.modules.auth.entity.domain.SysUser;
import com.lest.modules.auth.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security UserDetailsService 实现
 * <p>
 * 根据用户名加载用户信息
 * </p>
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;

    public UserDetailsServiceImpl(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getDeleted, 0)
        );

        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,
                true,
                true,
                true,
                authorities
        );
    }
}
