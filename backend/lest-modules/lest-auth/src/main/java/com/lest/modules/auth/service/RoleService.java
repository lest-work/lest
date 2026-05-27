package com.lest.modules.auth.service;

import com.lest.common.base.PageResult;
import com.lest.modules.auth.entity.dto.RoleDTO;
import com.lest.modules.auth.entity.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface RoleService {

    PageResult<RoleVO> page(Integer page, Integer size, String roleCode, String roleName, Integer status);
    RoleVO getById(Long id);
    List<RoleVO> listAll();
    Long create(RoleDTO dto);
    void update(RoleDTO dto);

    /**
     * 删除角色（单个或批量）
     *
     * @param id  单个角色ID
     * @param ids 批量删除时传入的ID列表，ids不为空时执行批量删除
     */
    void delete(Long id, List<Long> ids);

    List<Long> getMenuIds(Long roleId);
    void assignMenus(Long roleId, Long[] menuIds);
}
