package com.lest.modules.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.job.entity.domain.SysJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务 Mapper
 *
 * @author yshan2028
 */
@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {
}
