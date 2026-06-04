package com.lest.system.controller;

import java.util.List;
import com.lest.common.core.utils.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.domain.R;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.security.annotation.InnerAuth;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.api.system.domain.SysUser;
import com.lest.api.system.model.LoginUser;
import com.lest.system.service.ISysUserService;

/**
 * 用户管理（精简版，适合敏捷平台）
 * 仅保留基础用户 CRUD，权限/角色/部门关联已按 PRD V2.0 废弃
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController
{
    private final ISysUserService userService;

    public SysUserController(ISysUserService userService)
    {
        this.userService = userService;
    }

    /**
     * 获取用户列表
     */
        @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 获取用户信息（Feign：按用户名）
     */
    @InnerAuth
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable String username)
    {
        SysUser user = userService.selectUserByUserName(username);
        if (user == null)
        {
            return R.fail("用户不存在");
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setSysUser(user);
        return R.ok(loginUser);
    }

    /**
     * 获取原始用户信息（Feign：仅返回 SysUser，用于认证服务校验状态/密码）
     */
    @InnerAuth
    @GetMapping("/raw/{username}")
    public R<SysUser> raw(@PathVariable String username)
    {
        SysUser user = userService.selectUserByUserName(username);
        if (user == null)
        {
            return R.fail("用户不存在");
        }
        return R.ok(user);
    }

    /**
     * 获取用户信息（Feign：按邮箱）
     */
    @InnerAuth
    @GetMapping("/email/{email}")
    public R<SysUser> infoByEmail(@PathVariable String email)
    {
        SysUser query = new SysUser();
        query.setEmail(email);
        List<SysUser> list = userService.selectUserList(query);
        if (list == null || list.isEmpty())
        {
            return R.fail("用户不存在");
        }
        return R.ok(list.get(0));
    }

    /**
     * 注册用户（Feign：lest-auth 调用）
     */
    @InnerAuth
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody SysUser sysUser)
    {
        String username = sysUser.getUserName();
        if (!userService.checkUserNameUnique(sysUser))
        {
            return R.fail("账号已存在");
        }
        if (sysUser.getPhonenumber() != null && !userService.checkPhoneUnique(sysUser))
        {
            return R.fail("手机号码已存在");
        }
        if (sysUser.getEmail() != null && !userService.checkEmailUnique(sysUser))
        {
            return R.fail("邮箱已存在");
        }
        sysUser.setPassword(SecurityUtils.encryptPassword(sysUser.getPassword()));
        return R.ok(userService.insertUser(sysUser) > 0);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getSysUser();
        AjaxResult ajax = success();
        ajax.put("user", user);
        return ajax;
    }

    /**
     * 根据用户ID获取详情
     */
        @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable Long userId)
    {
        SysUser user = userService.selectUserById(userId);
        return success(user);
    }

    /**
     * 修改用户基本信息（Feign：profile/avatar）
     */
    @InnerAuth
    @PutMapping("/profile")
    public R<Boolean> updateUserProfile(@RequestBody SysUser user)
    {
        if (StringUtils.isNotEmpty(user.getUpdateBy()))
        {
            return R.ok(userService.updateUserProfile(user));
        }
        return R.fail("更新者信息缺失");
    }

    /**
     * 校验手机号码唯一性（Feign）
     */
    @InnerAuth
    @GetMapping("/checkPhoneUnique")
    public R<Boolean> checkPhoneUnique(@RequestParam Long userId, @RequestParam String phonenumber)
    {
        SysUser query = new SysUser();
        query.setUserId(userId);
        query.setPhonenumber(phonenumber);
        return R.ok(userService.checkPhoneUnique(query));
    }

    /**
     * 校验邮箱唯一性（Feign）
     */
    @InnerAuth
    @GetMapping("/checkEmailUnique")
    public R<Boolean> checkEmailUnique(@RequestParam Long userId, @RequestParam String email)
    {
        SysUser query = new SysUser();
        query.setUserId(userId);
        query.setEmail(email);
        return R.ok(userService.checkEmailUnique(query));
    }

    /**
     * 重置密码（Feign）
     */
    @InnerAuth
    @PutMapping("/resetPwd/inner")
    public R<Boolean> resetPwd(@RequestParam Long userId, @RequestParam String password)
    {
        return R.ok(userService.resetUserPwd(userId, password) > 0);
    }

    /**
     * 新增用户
     */
        @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        if (!userService.checkUserNameUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，账号已存在");
        }
        if (user.getPhonenumber() != null && !userService.checkPhoneUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (user.getEmail() != null && !userService.checkEmailUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
        @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        if (user.getPhonenumber() != null && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (user.getEmail() != null && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
        @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
        @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 修改状态
     */
        @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * Feign: 记录用户登录信息（登录IP和登录时间）
     */
    @InnerAuth
    @PutMapping("/recordlogin")
    public R<Boolean> recordLogin(@RequestBody SysUser sysUser)
    {
        userService.updateLoginInfo(sysUser);
        return R.ok(true);
    }
}
