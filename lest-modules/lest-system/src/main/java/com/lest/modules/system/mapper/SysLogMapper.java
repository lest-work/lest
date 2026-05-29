package com.lest.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.system.entity.domain.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper接口，继承MyBatis-Plus BaseMapper
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {}
