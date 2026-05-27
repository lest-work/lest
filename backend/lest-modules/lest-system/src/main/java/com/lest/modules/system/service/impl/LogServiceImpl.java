package com.lest.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.base.PageResult;
import com.lest.modules.system.entity.domain.SysLog;
import com.lest.modules.system.entity.dto.LogQueryDTO;
import com.lest.modules.system.entity.vo.LogVO;
import com.lest.modules.system.mapper.SysLogMapper;
import com.lest.modules.system.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 操作日志服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    /** 完整日期时间格式 */
    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /** 仅日期格式 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SysLogMapper logMapper;

    /**
     * 分页查询操作日志
     * 支持按用户名、昵称、模块、状态、时间范围进行多条件筛选
     */
    @Override
    public PageResult<LogVO> page(LogQueryDTO dto) {
        int pageNum = dto.page() != null ? dto.page() : 1;
        int pageSize = dto.size() != null ? dto.size() : 20;
        Page<SysLog> p = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLog> w = new LambdaQueryWrapper<>();
        if (dto.userId() != null) w.eq(SysLog::getUserId, dto.userId());
        if (StringUtils.hasText(dto.username())) w.like(SysLog::getUsername, dto.username());
        if (StringUtils.hasText(dto.nickname())) w.like(SysLog::getNickname, dto.nickname());
        if (StringUtils.hasText(dto.module())) w.like(SysLog::getModule, dto.module());
        if (StringUtils.hasText(dto.operation())) w.like(SysLog::getOperation, dto.operation());
        if (dto.status() != null) w.eq(SysLog::getResponseStatus, dto.status());
        // 支持yyyy-MM-dd和yyyy-MM-dd HH:mm:ss两种格式
        if (StringUtils.hasText(dto.startTime())) w.ge(SysLog::getCreatedAt, parseDateTime(dto.startTime(), true));
        if (StringUtils.hasText(dto.endTime())) w.le(SysLog::getCreatedAt, parseDateTime(dto.endTime(), false));
        w.orderByDesc(SysLog::getCreatedAt);
        IPage<SysLog> result = logMapper.selectPage(p, w);
        return PageResult.of(result.getRecords().stream().map(this::toVO).toList(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public LogVO getById(Long id) {
        return toVO(logMapper.selectById(id));
    }

    /**
     * 删除操作日志（单个或批量）
     * - ids不为空时批量删除
     * - ids为空时删除指定id的记录
     */
    @Override
    public void delete(Long id, List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            logMapper.deleteBatchIds(ids);
        } else {
            logMapper.deleteById(id);
        }
    }

    /**
     * 统计操作日志数量
     */
    @Override
    public long count(String operation, String startTime, String endTime) {
        LambdaQueryWrapper<SysLog> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(operation)) w.like(SysLog::getOperation, operation);
        if (StringUtils.hasText(startTime)) w.ge(SysLog::getCreatedAt, parseDateTime(startTime, true));
        if (StringUtils.hasText(endTime)) w.le(SysLog::getCreatedAt, parseDateTime(endTime, false));
        return logMapper.selectCount(w);
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

    /** 实体转VO，将数据库字段映射为前端期望的字段名 */
    private LogVO toVO(SysLog e) {
        if (e == null) return null;
        return new LogVO(
            e.getId(), e.getUserId(), e.getUsername(), e.getNickname(),
            e.getModule(), e.getDescription(), e.getRequestUrl(),
            e.getRequestMethod(), e.getOperation(), e.getRequestParams(),
            e.getResponseBody(), e.getErrorMessage(), e.getExecutionTime(),
            e.getOs(), e.getDevice(), e.getBrowser(),
            e.getIpAddress(), e.getResponseStatus(), e.getCreatedAt()
        );
    }
}
