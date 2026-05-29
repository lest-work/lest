package com.lest.system.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageHelper;
import com.lest.common.core.constant.CacheConstants;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.redis.service.RedisService;
import com.lest.system.api.domain.SysOperLog;
import com.lest.system.api.domain.SysUser;
import com.lest.system.api.model.LoginUser;
import com.lest.system.service.ISysOperLogService;
import com.lest.system.service.ISysUserService;

/**
 * 工作台仪表盘
 * 
 * @author yshan2028
 */
@RestController
@RequestMapping("/dashboard")
public class SysDashboardController extends BaseController
{
    @Autowired
    private ISysOperLogService operLogService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private RedisService redisService;

    /**
     * 最新动态 - 近期操作日志
     * GET /system/dashboard/activities?limit=15
     * 网关剥离 system → /dashboard/activities
     */
    @GetMapping("/activities")
    public AjaxResult activities(@RequestParam(defaultValue = "15") Integer limit)
    {
        PageHelper.startPage(1, limit);
        SysOperLog query = new SysOperLog();
        List<SysOperLog> logs = operLogService.selectOperLogList(query);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysOperLog log : logs)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", log.getOperId());
            item.put("operName", log.getOperName());
            item.put("title", log.getTitle());
            item.put("businessType", log.getBusinessType());
            item.put("status", log.getStatus());
            item.put("operTime", log.getOperTime());
            item.put("operUrl", log.getOperUrl());
            result.add(item);
        }
        return success(result);
    }

    /**
     * 小组成员 - 用户列表及在线状态
     * GET /system/dashboard/members
     * 网关剥离 system → /dashboard/members
     */
    @GetMapping("/members")
    public AjaxResult members()
    {
        SysUser query = new SysUser();
        query.setStatus("0");
        List<SysUser> users = userService.selectUserList(query);

        Set<String> onlineUserNames = getOnlineUserNames();

        List<Map<String, Object>> result = users.stream().map(u -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getUserId());
            item.put("userName", u.getUserName());
            item.put("name", u.getNickName() != null ? u.getNickName() : u.getUserName());
            item.put("avatar", u.getAvatar());
            item.put("email", u.getEmail());
            item.put("status", onlineUserNames.contains(u.getUserName()) ? 0 : 1);
            return item;
        }).collect(Collectors.toList());

        return success(result);
    }

    private Set<String> getOnlineUserNames()
    {
        try
        {
            Collection<String> keys = redisService.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
            return keys.stream().map(key -> {
                LoginUser user = redisService.getCacheObject(key);
                return user != null ? user.getUsername() : null;
            }).filter(name -> name != null).collect(Collectors.toSet());
        }
        catch (Exception e)
        {
            return java.util.Collections.emptySet();
        }
    }
}
