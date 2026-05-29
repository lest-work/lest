package com.lest.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.PageResult;
import com.lest.modules.system.entity.domain.SysLoginLog;
import com.lest.modules.system.entity.dto.LoginLogDTO;
import com.lest.modules.system.entity.dto.LoginLogQueryDTO;
import com.lest.modules.system.entity.vo.LoginLogVO;
import com.lest.modules.system.mapper.SysLoginLogMapper;
import com.lest.modules.system.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 登录日志服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    /** 完整日期时间格式 */
    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /** 仅日期格式 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SysLoginLogMapper loginLogMapper;

    /**
     * 分页查询登录日志
     * 支持按用户名、昵称、登录类型、时间范围进行多条件筛选
     */
    @Override
    public PageResult<LoginLogVO> page(LoginLogQueryDTO dto) {
        int pageNum = dto.page() != null ? dto.page() : 1;
        int pageSize = dto.size() != null ? dto.size() : 20;
        Page<SysLoginLog> p = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLoginLog> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.username())) w.like(SysLoginLog::getUsername, dto.username());
        if (StringUtils.hasText(dto.nickname())) w.like(SysLoginLog::getNickname, dto.nickname());
        if (dto.loginType() != null) w.eq(SysLoginLog::getLoginType, dto.loginType());
        // 支持yyyy-MM-dd和yyyy-MM-dd HH:mm:ss两种格式
        if (StringUtils.hasText(dto.startTime())) w.ge(SysLoginLog::getCreatedAt, parseDateTime(dto.startTime(), true));
        if (StringUtils.hasText(dto.endTime())) w.le(SysLoginLog::getCreatedAt, parseDateTime(dto.endTime(), false));
        w.orderByDesc(SysLoginLog::getCreatedAt);
        IPage<SysLoginLog> result = loginLogMapper.selectPage(p, w);
        return PageResult.of(result.getRecords().stream().map(this::toVO).toList(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public LoginLogVO getById(Long id) {
        return toVO(loginLogMapper.selectById(id));
    }

    /**
     * 删除登录日志（单个或批量）
     * - ids不为空时批量删除
     * - ids为空时删除指定id的记录
     */
    @Override
    public void delete(Long id, List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            loginLogMapper.deleteBatchIds(ids);
        } else {
            loginLogMapper.deleteById(id);
        }
    }

    /**
     * 统计登录日志数量
     */
    @Override
    public long count(String username, Integer loginType, String startTime, String endTime) {
        LambdaQueryWrapper<SysLoginLog> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) w.like(SysLoginLog::getUsername, username);
        if (loginType != null) w.eq(SysLoginLog::getLoginType, loginType);
        if (StringUtils.hasText(startTime)) w.ge(SysLoginLog::getCreatedAt, parseDateTime(startTime, true));
        if (StringUtils.hasText(endTime)) w.le(SysLoginLog::getCreatedAt, parseDateTime(endTime, false));
        return loginLogMapper.selectCount(w);
    }

    /**
     * 保存登录日志（供认证服务调用）
     */
    @Override
    public void save(LoginLogDTO dto) {
        SysLoginLog entity = new SysLoginLog();
        entity.setUserId(dto.userId());
        entity.setUsername(dto.username());
        entity.setNickname(dto.nickname());
        entity.setLoginType(dto.loginType());
        entity.setIpAddress(dto.ipAddress());
        entity.setUserAgent(dto.userAgent());
        entity.setOs(dto.os());
        entity.setBrowser(dto.browser());
        entity.setDevice(dto.device());
        entity.setStatus(dto.status());
        entity.setMsg(dto.msg());
        loginLogMapper.insert(entity);
    }

    @Override
    public void save(SysLoginLog log) {
        loginLogMapper.insert(log);
    }

    /**
     * 解析日期时间字符串，支持两种格式
     * - yyyy-MM-dd HH:mm:ss -> 直接解析
     * - yyyy-MM-dd -> 解析为当天，isStart=true时为00:00:00，isStart=false时为次日00:00:00
     */
    private LocalDateTime parseDateTime(String value, boolean isStart) {
        try {
            return LocalDateTime.parse(value, FULL_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime date = LocalDateTime.parse(value, DATE_FORMATTER);
                // 结束时间默认到当天结束，开始时间默认当天开始
                return isStart ? date : date.plusDays(1).withHour(0).withMinute(0).withSecond(0);
            } catch (DateTimeParseException ex) {
                // 无法解析时，开始时间取最小值，结束时间取最大值
                return isStart ? LocalDateTime.MIN : LocalDateTime.MAX;
            }
        }
    }

    /** 实体转VO */
    private LoginLogVO toVO(SysLoginLog e) {
        if (e == null) return null;
        return new LoginLogVO(
            e.getId(), e.getUserId(), e.getUsername(), e.getNickname(),
            e.getLoginType(), e.getIpAddress(), e.getUserAgent(),
            e.getOs(), e.getBrowser(), e.getDevice(),
            e.getStatus(), e.getMsg(), e.getCreatedAt()
        );
    }

}
