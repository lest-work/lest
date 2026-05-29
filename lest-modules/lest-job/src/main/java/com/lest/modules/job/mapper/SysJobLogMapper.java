package com.lest.modules.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.job.entity.domain.SysJobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志 Mapper
 *
 * @author yshan2028
 */
@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {
}
