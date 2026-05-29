package com.lest.modules.system.service;

import com.lest.modules.system.entity.dto.MenuDTO;
import com.lest.modules.system.entity.vo.MenuVO;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author yshan2028
 */
public interface MenuService {

    /**
     * 分页查询菜单列表
     */
    List<MenuVO> selectMenuList(String menuName, String path, String permission);

    /**
     * 获取菜单树
     */
    List<MenuVO> selectMenuTree();

    /**
     * 根据ID获取菜单
     */
    MenuVO getById(Long menuId);

    /**
     * 新增菜单
     */
    Long createMenu(MenuDTO dto);

    /**
     * 修改菜单
     */
    void updateMenu(MenuDTO dto);

    /**
     * 删除菜单
     */
    void deleteMenu(Long menuId);

    /**
     * 获取角色的菜单树（带选中状态）
     */
    List<MenuVO> buildRoleMenuTree(List<MenuVO> allMenus, List<Long> checkedMenuIds);

    /**
     * 获取用户菜单ID列表
     */
    List<Long> getMenuIdsByUserId(Long userId);

    /**
     * 获取角色的菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
}
