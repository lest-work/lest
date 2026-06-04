package com.lest.system.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.constant.CacheConstants;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.redis.service.RedisService;
import com.lest.api.system.domain.SysUser;
import com.lest.api.system.model.LoginUser;
import com.lest.system.service.ISysUserService;

/**
 * 工作台仪表盘（精简版，适合敏捷平台）
 * 操作日志相关功能已移至 Kafka，后续通过事件驱动实现
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/dashboard")
public class SysDashboardController extends BaseController
{
    private final ISysUserService userService;
    private final RedisService redisService;

    @Autowired
    public SysDashboardController(ISysUserService userService, RedisService redisService)
    {
        this.userService = userService;
        this.redisService = redisService;
    }

    /**
     * 小组成员 - 用户列表及在线状态
     * GET /dashboard/members
     */
    @GetMapping("/members")
    public AjaxResult members()
    {
        SysUser query = new SysUser();
        query.setStatus("0");
        List<SysUser> users = userService.selectUserList(query);

        Set<String> onlineUserNames = getOnlineUserNames();

        List<Map<String, Object>> result = users.stream().map(u -> {
            Map<String, Object> item = new java.util.HashMap<>();
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
