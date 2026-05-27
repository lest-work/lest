package com.lest.modules.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lest.common.base.PageResult;
import com.lest.modules.system.entity.domain.OnlineUser;
import com.lest.modules.system.entity.vo.OnlineUserVO;
import com.lest.modules.system.service.OnlineUserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 在线用户服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements OnlineUserService {

    /** Redis Key前缀 */
    private static final String ONLINE_KEY_PREFIX = "online:session:";
    /** 会话有效期（分钟），与AccessToken过期时间保持一致 */
    private static final long SESSION_TTL_MINUTES = 15;

    private final StringRedisTemplate redisTemplate;
    private ObjectMapper objectMapper;

    /** 初始化Jackson，注册Java8时间模块 */
    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /** 分页查询在线用户 */
    @Override
    public PageResult<OnlineUserVO> page(Integer page, Integer size) {
        int pageNum = page != null ? page : 1;
        int pageSize = size != null ? size : 20;
        List<OnlineUserVO> all = list();
        int total = all.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<OnlineUserVO> records = fromIndex < total ? all.subList(fromIndex, toIndex) : Collections.emptyList();
        return PageResult.of(records, (long) total, pageNum, pageSize);
    }

    /** 查询所有在线用户，按登录时间倒序 */
    @Override
    public List<OnlineUserVO> list() {
        Set<String> keys = redisTemplate.keys(ONLINE_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<OnlineUserVO> result = new ArrayList<>();
        for (String key : keys) {
            String sessionId = key.substring(ONLINE_KEY_PREFIX.length());
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) continue;
            try {
                OnlineUser user = fromJson(json);
                result.add(toVO(user, sessionId));
            } catch (Exception e) {
                log.warn("解析在线用户失败: {}", key, e);
            }
        }
        result.sort((a, b) -> {
            if (a.loginTime() == null || b.loginTime() == null) return 0;
            return b.loginTime().compareTo(a.loginTime());
        });
        return result;
    }

    /** 获取在线用户数量 */
    @Override
    public long count() {
        Set<String> keys = redisTemplate.keys(ONLINE_KEY_PREFIX + "*");
        return keys != null ? keys.size() : 0;
    }

    /**
     * 踢出在线用户（单个或批量）
     * - sessionIds不为空时踢出指定用户
     * - sessionIds为空时踢出全部用户
     */
    @Override
    public void kickout(List<String> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            // 踢出全部
            Set<String> keys = redisTemplate.keys(ONLINE_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("踢出全部在线用户: count={}", keys.size());
            }
            return;
        }
        // 踢出指定用户
        for (String sessionId : sessionIds) {
            String key = ONLINE_KEY_PREFIX + sessionId;
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                try {
                    OnlineUser user = fromJson(json);
                    redisTemplate.delete(key);
                    log.info("踢出在线用户: userId={}, username={}, sessionId={}", user.getUserId(), user.getUsername(), sessionId);
                } catch (Exception e) {
                    // 解析失败也删除
                    redisTemplate.delete(key);
                }
            }
        }
    }

    /**
     * 保存用户会话信息（供认证服务调用）
     */
    public void saveSession(String sessionId, Long userId, String username, String nickname,
                            String ipAddress, String os, String browser, String device) {
        try {
            OnlineUser user = new OnlineUser();
            user.setSessionId(sessionId);
            user.setUserId(userId);
            user.setUsername(username);
            user.setNickname(nickname);
            user.setIpAddress(ipAddress);
            user.setOs(os);
            user.setBrowser(browser);
            user.setDevice(device);
            user.setLoginTime(LocalDateTime.now());
            user.setLastAccessTime(LocalDateTime.now());
            redisTemplate.opsForValue().set(ONLINE_KEY_PREFIX + sessionId, toJson(user));
            log.debug("保存在线用户会话: sessionId={}, userId={}", sessionId, userId);
        } catch (Exception e) {
            log.error("保存在线用户会话失败: sessionId={}", sessionId, e);
        }
    }

    /**
     * 刷新会话活跃时间
     */
    public void refreshSession(String sessionId) {
        String key = ONLINE_KEY_PREFIX + sessionId;
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            try {
                String json = redisTemplate.opsForValue().get(key);
                if (json != null) {
                    OnlineUser user = fromJson(json);
                    user.setLastAccessTime(LocalDateTime.now());
                    redisTemplate.opsForValue().set(key, toJson(user));
                }
            } catch (Exception e) {
                log.warn("刷新在线用户会话失败: sessionId={}", sessionId, e);
            }
        }
    }

    /**
     * 移除会话（退出登录时调用）
     */
    public void removeSession(String sessionId) {
        redisTemplate.delete(ONLINE_KEY_PREFIX + sessionId);
        log.debug("移除在线用户会话: sessionId={}", sessionId);
    }

    /** 实体转VO */
    private OnlineUserVO toVO(OnlineUser user, String sessionId) {
        return new OnlineUserVO(
            sessionId, user.getUserId(), user.getUsername(), user.getNickname(),
            user.getIpAddress(), user.getOs(), user.getBrowser(), user.getDevice(),
            user.getLoginTime(), user.getLastAccessTime()
        );
    }

    /** 序列化为JSON字符串 */
    private String toJson(OnlineUser user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            throw new RuntimeException("序列化在线用户失败", e);
        }
    }

    /** 从JSON字符串反序列化 */
    private OnlineUser fromJson(String json) {
        try {
            return objectMapper.readValue(json, OnlineUser.class);
        } catch (Exception e) {
            throw new RuntimeException("反序列化在线用户失败", e);
        }
    }
}
