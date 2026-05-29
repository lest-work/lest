package com.lest.modules.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.auth.entity.domain.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM sys_user_role WHERE role_id = #{roleId}")
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色关联
     *
     * @param userId 用户ID
     */
    @Select("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除角色的所有用户关联
     *
     * @param roleId 角色ID
     */
    @Select("DELETE FROM sys_user_role WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Select("SELECT r.role_code FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
