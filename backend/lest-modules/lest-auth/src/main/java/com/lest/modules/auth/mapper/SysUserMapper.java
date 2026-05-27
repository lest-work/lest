package com.lest.modules.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.auth.entity.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Mapper接口
 *
 * @author Lest
 * @since 2026-05-26
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
