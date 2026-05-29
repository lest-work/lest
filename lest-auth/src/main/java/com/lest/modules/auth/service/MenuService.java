package com.lest.modules.auth.service;

import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.dto.MenuDTO;
import com.lest.modules.auth.entity.vo.MenuVO;
import com.lest.modules.auth.entity.vo.RouteVO;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface MenuService {

    PageResult<MenuVO> page(Integer page, Integer size, String menuName, String path, String permission);
    List<MenuVO> getTree();
    List<RouteVO> getRoutes(Long userId);
    MenuVO getById(Long id);
    List<MenuVO> listAll();
    Long create(MenuDTO dto);
    void update(MenuDTO dto);

    /**
     * 删除菜单（单个或批量）
     *
     * @param id  单个菜单ID
     * @param ids 批量删除时传入的ID列表，ids不为空时执行批量删除
     */
    void delete(Long id, List<Long> ids);

    List<String> getUserPermissions(Long userId);

    /**
     * 判断用户是否拥有超管角色
     *
     * @param userId 用户ID
     * @return true=是超管
     */
    boolean isSuperAdmin(Long userId);
}
