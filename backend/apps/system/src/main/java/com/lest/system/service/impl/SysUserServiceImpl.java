package com.lest.system.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.lest.common.core.exception.ServiceException;
import com.lest.common.core.utils.StringUtils;
import com.lest.api.system.domain.SysUser;
import com.lest.system.mapper.SysUserMapper;
import com.lest.system.service.ISysUserService;

/**
 * 用户管理（精简版，适合敏捷平台）
 * 仅保留基础用户 CRUD，岗位/角色/部门关联已废弃
 *
 * @author yshan2028
 */
@Service
public class SysUserServiceImpl implements ISysUserService
{
    private final SysUserMapper userMapper;

    public SysUserServiceImpl(SysUserMapper userMapper)
    {
        this.userMapper = userMapper;
    }

    @Override
    public List<SysUser> selectUserList(SysUser user)
    {
        return userMapper.selectUserList(user);
    }

    @Override
    public SysUser selectUserByUserName(String userName)
    {
        return userMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser selectUserById(Long userId)
    {
        return userMapper.selectUserById(userId);
    }

    @Override
    public boolean checkUserNameUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkUserNameUnique(user.getUserName());
        return info == null || info.getUserId().equals(userId);
    }

    @Override
    public boolean checkPhoneUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        return info == null || info.getUserId().equals(userId);
    }

    @Override
    public boolean checkEmailUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        return info == null || info.getUserId().equals(userId);
    }

    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && 1L == user.getUserId())
        {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    @Override
    public int insertUser(SysUser user)
    {
        return userMapper.insertUser(user);
    }

    @Override
    public boolean registerUser(SysUser user)
    {
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public int updateUser(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    @Override
    public int updateUserStatus(SysUser user)
    {
        return userMapper.updateUserStatus(user.getUserId(), user.getStatus());
    }

    @Override
    public boolean updateUserProfile(SysUser user)
    {
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean updateUserAvatar(Long userId, String avatar)
    {
        return userMapper.updateUserAvatar(userId, avatar) > 0;
    }

    public boolean updateLoginInfo(SysUser user)
    {
        return userMapper.updateLoginInfo(user) > 0;
    }

    @Override
    public int resetPwd(SysUser user)
    {
        return userMapper.resetUserPwd(user.getUserId(), user.getPassword());
    }

    @Override
    public int resetUserPwd(Long userId, String password)
    {
        return userMapper.resetUserPwd(userId, password);
    }

    @Override
    public int deleteUserById(Long userId)
    {
        SysUser user = userMapper.selectUserById(userId);
        if (user != null)
        {
            checkUserAllowed(user);
        }
        return userMapper.deleteUserById(userId);
    }

    @Override
    public int deleteUserByIds(Long[] userIds)
    {
        if (userIds != null)
        {
            for (Long userId : userIds)
            {
                SysUser user = userMapper.selectUserById(userId);
                if (user != null)
                {
                    checkUserAllowed(user);
                }
            }
        }
        return userMapper.deleteUserByIds(userIds);
    }
}
