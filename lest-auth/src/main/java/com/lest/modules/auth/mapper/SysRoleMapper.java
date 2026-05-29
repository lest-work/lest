package com.lest.modules.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.auth.entity.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统角色Mapper接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
