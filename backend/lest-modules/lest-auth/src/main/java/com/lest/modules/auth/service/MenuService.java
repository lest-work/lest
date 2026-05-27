package com.lest.modules.auth.service;

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

    List<MenuVO> getTree();
    List<RouteVO> getRoutes();
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
}
