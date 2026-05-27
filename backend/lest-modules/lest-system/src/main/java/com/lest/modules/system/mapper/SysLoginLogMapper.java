package com.lest.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.system.entity.domain.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper接口，继承MyBatis-Plus BaseMapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {}
