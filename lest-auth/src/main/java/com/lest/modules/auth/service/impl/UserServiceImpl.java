package com.lest.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.Assert;
import com.lest.modules.auth.common.ErrorCode;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.domain.SysUser;
import com.lest.modules.auth.entity.domain.SysUserRole;
import com.lest.modules.auth.entity.dto.UserDTO;
import com.lest.modules.auth.entity.vo.UserVO;
import com.lest.modules.auth.mapper.SysUserMapper;
import com.lest.modules.auth.mapper.SysUserRoleMapper;
import com.lest.modules.auth.service.UserService;
import com.lest.common.security.util.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_PASSWORD = "lest123456";
    private static final Long SUPER_ADMIN_ID = 1L;

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> page(Integer page, Integer size, String username, String nickname, String phone,
                                   String email, Integer status, Long orgId) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) wrapper.like(SysUser::getUsername, username);
        if (nickname != null && !nickname.isEmpty()) wrapper.like(SysUser::getNickname, nickname);
        if (phone != null && !phone.isEmpty()) wrapper.like(SysUser::getPhone, phone);
        if (email != null && !email.isEmpty()) wrapper.like(SysUser::getEmail, email);
        if (status != null) wrapper.eq(SysUser::getStatus, status);
        if (orgId != null) wrapper.eq(SysUser::getOrgId, orgId);
        wrapper.orderByDesc(SysUser::getCreatedAt);

        Page<SysUser> result = userMapper.selectPage(pageParam, wrapper);
        List<UserVO> records = result.getRecords().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public UserVO getById(Long id) {
        SysUser user = userMapper.selectById(id);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);
        return convertToVO(user);
    }

    @Override
    public List<UserVO> listAll() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreatedAt);
        return userMapper.selectList(wrapper).stream()
                .map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserDTO dto) {
        Assert.isNull(userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
                        .eq(SysUser::getDeleted, 0)),
                ErrorCode.USER_USERNAME_EXISTS);

        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            Assert.isNull(userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getPhone, dto.getPhone())
                            .eq(SysUser::getDeleted, 0)),
                    ErrorCode.USER_PHONE_EXISTS);
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            Assert.isNull(userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getEmail, dto.getEmail())
                            .eq(SysUser::getDeleted, 0)),
                    ErrorCode.USER_EMAIL_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setSex(dto.getSex() != null ? dto.getSex() : 0);
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setOrgId(dto.getOrgId());

        userMapper.insert(user);

        if (dto.getRoleIds() != null && dto.getRoleIds().length > 0) {
            assignRoles(user.getId(), dto.getRoleIds());
        }

        log.info("创建用户成功: userId={}, username={}", user.getId(), dto.getUsername());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserDTO dto) {
        Assert.notNull(dto.getId(), ErrorCode.USER_NOT_FOUND);
        SysUser user = userMapper.selectById(dto.getId());
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            SysUser existPhone = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getPhone, dto.getPhone())
                            .eq(SysUser::getDeleted, 0)
                            .ne(SysUser::getId, dto.getId()));
            Assert.isNull(existPhone, ErrorCode.USER_PHONE_EXISTS);
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            SysUser existEmail = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getEmail, dto.getEmail())
                            .eq(SysUser::getDeleted, 0)
                            .ne(SysUser::getId, dto.getId()));
            Assert.isNull(existEmail, ErrorCode.USER_EMAIL_EXISTS);
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(dto.getId());
        if (dto.getNickname() != null) updateUser.setNickname(dto.getNickname());
        if (dto.getEmail() != null) updateUser.setEmail(dto.getEmail());
        if (dto.getPhone() != null) updateUser.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) updateUser.setAvatar(dto.getAvatar());
        if (dto.getSex() != null) updateUser.setSex(dto.getSex());
        if (dto.getStatus() != null) updateUser.setStatus(dto.getStatus());
        if (dto.getOrgId() != null) updateUser.setOrgId(dto.getOrgId());

        userMapper.updateById(updateUser);

        if (dto.getRoleIds() != null) {
            assignRoles(dto.getId(), dto.getRoleIds());
        }

        log.info("更新用户成功: userId={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysUser user = userMapper.selectById(id);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);
        Assert.isFalse(id.equals(SUPER_ADMIN_ID), ErrorCode.USER_DELETE_SUPER_ADMIN);

        userMapper.deleteById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));

        log.info("删除用户成功: userId={}", id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        LoginUser loginUser = getLoginUser();
        Assert.isFalse(id.equals(loginUser.getUserId()) && status == 0, ErrorCode.USER_DISABLE_SELF);
        Assert.isFalse(id.equals(SUPER_ADMIN_ID) && status == 0, ErrorCode.USER_DISABLE_SELF);

        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(status);
        userMapper.updateById(updateUser);

        log.info("更新用户状态成功: userId={}, status={}", id, status);
    }

    @Override
    public void resetPassword(Long id, String password) {
        SysUser user = userMapper.selectById(id);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);
        String newPassword = (password != null && !password.isBlank()) ? password : DEFAULT_PASSWORD;
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(updateUser);
        log.info("重置用户密码成功: userId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(Long[] ids) {
        if (ids == null || ids.length == 0) return;
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, Long[] roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds != null && roleIds.length > 0) {
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
        log.info("分配用户角色成功: userId={}, roleIds={}", userId, Arrays.toString(roleIds));
    }

    /** 实体转VO */
    private UserVO convertToVO(SysUser user) {
        List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(user.getId());
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .sex(user.getSex())
                .status(user.getStatus())
                .orgId(user.getOrgId())
                .roles(roleCodes.toArray(new String[0]))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /** 获取当前登录用户 */
    private LoginUser getLoginUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        throw new RuntimeException("用户未登录");
    }
}
