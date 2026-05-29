package com.lest.modules.system.service;

import com.lest.modules.system.entity.dto.RoleDTO;
import com.lest.modules.system.entity.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author yshan2028
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     */
    List<RoleVO> selectRoleList(String roleCode, String roleName, Integer status);

    /**
     * 获取所有角色
     */
    List<RoleVO> selectAllRoles();

    /**
     * 根据ID获取角色
     */
    RoleVO getById(Long roleId);

    /**
     * 新增角色
     */
    Long createRole(RoleDTO dto);

    /**
     * 修改角色
     */
    void updateRole(RoleDTO dto);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 修改角色状态
     */
    void updateRoleStatus(Long roleId, Integer status);

    /**
     * 获取角色菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 更新角色菜单权限
     */
    void updateAuthRole(Long roleId, Long[] menuIds);

    /**
     * 导出角色列表
     */
    void exportRole(List<RoleVO> list);

    /**
     * 修改数据权限
     */
    void updateDataScope(RoleDTO dto);
}
